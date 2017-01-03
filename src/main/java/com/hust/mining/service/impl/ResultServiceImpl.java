package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.hust.mining.constant.Constant.DIRECTORY;
import com.hust.mining.constant.Constant.Index;
import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.dao.IssueDao;
import com.hust.mining.dao.ResultDao;
import com.hust.mining.model.Issue;
import com.hust.mining.model.Result;
import com.hust.mining.model.ResultWithContent;
import com.hust.mining.model.params.StatisticParams;
import com.hust.mining.redis.RedisFacade;
import com.hust.mining.service.ResultService;
import com.hust.mining.service.StatisticService;
import com.hust.mining.util.ConvertUtil;

@Service
public class ResultServiceImpl implements ResultService {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ResultServiceImpl.class);

    @Autowired
    private ResultDao resultDao;
    @Autowired
    private IssueDao issueDao;
    @Autowired
    private StatisticService statService;

    @Override
    public String getCurrentResultId(HttpServletRequest request) {
        Object result = request.getSession().getAttribute(KEY.RESULT_ID);
        if (result == null) {
            return StringUtils.EMPTY;
        }
        return result.toString();
    }

    @Override
    public List<String[]> getCountResultById(String resultId, String issueId) {
        // TODO Auto-generated method stub
        List<String[]> modiCount = resultDao.getResultConentById(resultId, issueId, DIRECTORY.MODIFY_COUNT);
        List<String[]> list = new ArrayList<String[]>();
        try {
            List<String[]> content = resultDao.getResultConentById(resultId, issueId, DIRECTORY.CONTENT);
            List<int[]> count = ConvertUtil.toIntList(modiCount);
            List<String[]> cluster = resultDao.getResultConentById(resultId, issueId, DIRECTORY.MODIFY_CLUSTER);
            RedisFacade redis = RedisFacade.getInstance(true);
            redis.setObject(KEY.RESULT_CLUSTER, cluster);
            redis.setObject(KEY.RESULT_CONTENT, content);
            redis.setObject(KEY.RESULT_COUNT, count);
            for (int[] item : count) {
                String[] old = content.get(item[Index.COUNT_ITEM_INDEX]);
                String[] ne = new String[old.length + 1];
                System.arraycopy(old, 0, ne, 1, old.length);
                ne[0] = item[Index.COUNT_ITEM_AMOUNT] + "";
                list.add(ne);
            }
        } catch (Exception e) {
            logger.error("get count result failed:{}", e.toString());
            return null;
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean deleteSets(int[] sets, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String resultId = request.getSession().getAttribute(KEY.RESULT_ID).toString();
        String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
        RedisFacade redis = RedisFacade.getInstance(true);
        try {
            List<int[]> count = (List<int[]>) redis.getObject(KEY.RESULT_COUNT);
            List<String[]> cluster = (List<String[]>) redis.getObject(KEY.RESULT_CLUSTER);
            Arrays.sort(sets);
            for (int i = sets.length - 1; i >= 0; i--) {
                cluster.remove(sets[i]);
                count.remove(sets[i]);
            }
            redis.setObject(KEY.RESULT_CLUSTER, cluster);
            redis.setObject(KEY.RESULT_COUNT, count);
            Result result = new Result();
            result.setRid(resultId);
            result.setIssueId(issueId);
            ResultWithContent rc = new ResultWithContent();
            rc.setResult(result);
            rc.setModiCluster(cluster);
            rc.setModiCount(ConvertUtil.toStringList(count));
            int update = resultDao.updateResult(rc);
            if (update <= 0) {
                return false;
            }
            String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
            Issue issue = new Issue();
            issue.setIssueId(issueId);
            issue.setLastOperator(user);
            issue.setLastUpdateTime(new Date());
            issueDao.updateIssueInfo(issue);
        } catch (Exception e) {
            logger.error("sth failed when delete sets:{}" + e.toString());
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean combineSets(int[] sets, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String resultId = request.getSession().getAttribute(KEY.RESULT_ID).toString();
        String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
        RedisFacade redis = RedisFacade.getInstance(true);
        try {
            List<String[]> content = (List<String[]>) redis.getObject(KEY.RESULT_CONTENT);
            List<String[]> cluster = (List<String[]>) redis.getObject(KEY.RESULT_CLUSTER);
            String[] newrow = cluster.get(sets[0]);
            for (int i = 1; i < sets.length; i++) {
                newrow = (String[]) ArrayUtils.addAll(newrow, cluster.get(sets[i]));
            }
            Arrays.sort(sets);
            for (int i = sets.length - 1; i >= 0; i--) {
                cluster.remove(sets[i]);
            }
            cluster.add(newrow);
            Collections.sort(cluster, new Comparator<String[]>() {

                @Override
                public int compare(String[] o1, String[] o2) {
                    // TODO Auto-generated method stub
                    return o2.length - o1.length;
                }
            });
            // TODO:重构统计
            List<int[]> count = statService.countx(cluster, content);
            redis.setObject(KEY.RESULT_CLUSTER, cluster);
            redis.setObject(KEY.RESULT_COUNT, count);
            Result result = new Result();
            result.setRid(resultId);
            result.setIssueId(issueId);
            ResultWithContent rc = new ResultWithContent();
            rc.setResult(result);
            rc.setModiCluster(cluster);
            rc.setModiCount(ConvertUtil.toStringList(count));
            int update = resultDao.updateResult(rc);
            if (update <= 0) {
                return false;
            }
            String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
            Issue issue = new Issue();
            issue.setIssueId(issueId);
            issue.setLastOperator(user);
            issue.setLastUpdateTime(new Date());
            issueDao.updateIssueInfo(issue);
        } catch (Exception e) {
            logger.error("sth failed when combine sets:{}" + e.toString());
        }
        return true;
    }

    @Override
    public boolean reset(HttpServletRequest request) {
        // TODO Auto-generated method stub
        String resultId = request.getSession().getAttribute(KEY.RESULT_ID).toString();
        String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
        List<String[]> origCluster = resultDao.getResultConentById(resultId, issueId, DIRECTORY.ORIG_CLUSTER);
        List<String[]> origCount = resultDao.getResultConentById(resultId, issueId, DIRECTORY.ORIG_COUNT);

        Result result = new Result();
        result.setRid(resultId);
        result.setIssueId(issueId);
        ResultWithContent rc = new ResultWithContent();
        rc.setResult(result);
        rc.setModiCluster(origCluster);
        rc.setModiCount(origCount);
        int update = resultDao.updateResult(rc);
        if (update <= 0) {
            return false;
        }
        String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
        Issue issue = new Issue();
        issue.setIssueId(issueId);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        issueDao.updateIssueInfo(issue);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> statistic(StatisticParams params, HttpServletRequest request) {
        // TODO Auto-generated method stub
        RedisFacade redis = RedisFacade.getInstance(true);
        try {
            List<String[]> content = (List<String[]>) redis.getObject(KEY.RESULT_CONTENT);
            List<String[]> cluster = (List<String[]>) redis.getObject(KEY.RESULT_CLUSTER);
            int[] set = ConvertUtil.toIntArray(cluster.get(params.getCurrentSet()));
            Map<String, Map<String, Map<String, Integer>>> timeMap =
                    statService.statistic(content, set, params.getInterval());
            Map<String, Integer> typeMap = statService.getTypeCount(timeMap);
            Map<String, Integer> levelMap = statService.getLevelCount(timeMap);
            Map<String, Object> map = Maps.newHashMap();
            map.put("time", timeMap);
            Map<String, Object> countMap = Maps.newHashMap();
            countMap.put("type", typeMap);
            countMap.put("level", levelMap);
            map.put("count", countMap);
            return map;
        } catch (Exception e) {
            logger.error("exception occur when statistic:{}", e.toString());
        }
        return null;
    }

    @Override
    public int delResultById(String resultId) {
        // TODO Auto-generated method stub
        return resultDao.delResultById(resultId);
    }

    @Override
    public List<Result> queryResultsByIssueId(String issueId) {
        // TODO Auto-generated method stub
        return resultDao.queryResultsByIssueId(issueId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, List<String[]>> exportService(String issueId, String resultId) {
        // TODO Auto-generated method stub
        RedisFacade redis = RedisFacade.getInstance(true);
        try {
            List<String[]> content = (List<String[]>) redis.getObject(KEY.RESULT_CONTENT);
            List<String[]> cluster = new ArrayList<String[]>();
            List<int[]> clusterIndex = ConvertUtil.toIntList((List<String[]>) redis.getObject(KEY.RESULT_CLUSTER));
            for (int[] set : clusterIndex) {
                for (int index : set) {
                    String[] row = content.get(index);
                    cluster.add(row);
                }
                cluster.add(new String[1]);
            }
            List<String[]> count = new ArrayList<String[]>();
            List<int[]> countResult = (List<int[]>) redis.getObject(KEY.RESULT_COUNT);
            for (int[] row : countResult) {
                String[] oldRow = content.get(row[Index.COUNT_ITEM_INDEX]);
                String[] nRow = new String[oldRow.length + 1];
                System.arraycopy(oldRow, 0, nRow, 1, oldRow.length);
                nRow[0] = row[Index.COUNT_ITEM_AMOUNT] + "";
                count.add(nRow);
            }
            Map<String, List<String[]>> map = Maps.newHashMap();
            map.put("cluster", cluster);
            map.put("count", count);
            return map;
        } catch (Exception e) {
            logger.error("exception occur when get export result:{}", e.toString());
            return null;
        }
    }

}
