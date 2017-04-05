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
import com.hust.mining.dao.WebsiteDao;
import com.hust.mining.model.Issue;
import com.hust.mining.model.Result;
import com.hust.mining.model.ResultWithContent;
import com.hust.mining.model.Website;
import com.hust.mining.model.params.StatisticParams;
import com.hust.mining.service.IssueService;
import com.hust.mining.service.MiningService;
import com.hust.mining.service.RedisService;
import com.hust.mining.service.ResultService;
import com.hust.mining.service.UserService;
import com.hust.mining.util.CommonUtil;
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
    private WebsiteDao websiteDao;
    @Autowired
    private MiningService miningService;
    @Autowired
    private UserService userService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private RedisService redisService;

    @Override
    public String getCurrentResultId(HttpServletRequest request) {
        String result = redisService.getString(KEY.RESULT_ID, request);
        if (result == null) {
            return StringUtils.EMPTY;
        }
        return result;
    }

    @Override
    public List<String[]> getCountResultById(String resultId, String issueId, HttpServletRequest request) {
        // TODO Auto-generated method stub
        List<String[]> modiCount = resultDao.getResultConentById(resultId, issueId, DIRECTORY.MODIFY_COUNT);
        List<String[]> list = new ArrayList<String[]>();
        try {
            List<String[]> content = resultDao.getResultConentById(resultId, issueId, DIRECTORY.CONTENT);
            List<int[]> count = ConvertUtil.toIntList(modiCount);
            List<String[]> cluster = resultDao.getResultConentById(resultId, issueId, DIRECTORY.MODIFY_CLUSTER);
            redisService.setObject(KEY.REDIS_CLUSTER_RESULT, cluster, request);
            redisService.setObject(KEY.REDIS_CONTENT, content, request);
            redisService.setObject(KEY.REDIS_COUNT_RESULT, modiCount, request);
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
        String resultId = redisService.getString(KEY.RESULT_ID, request);
        String issueId = issueService.getCurrentIssueId(request);
        try {
            // 从redis获取数据
            List<String[]> count = (List<String[]>) redisService.getObject(KEY.REDIS_COUNT_RESULT, request);
            List<String[]> cluster = (List<String[]>) redisService.getObject(KEY.REDIS_CLUSTER_RESULT, request);
            // 删除集合
            Arrays.sort(sets);
            for (int i = sets.length - 1; i >= 0; i--) {
                cluster.remove(sets[i]);
                count.remove(sets[i]);
            }
            // 更新redis数据
            redisService.setObject(KEY.REDIS_CLUSTER_RESULT, cluster, request);
            redisService.setObject(KEY.REDIS_COUNT_RESULT, count, request);
            // 写回数据库
            Result result = new Result();
            result.setRid(resultId);
            result.setIssueId(issueId);
            ResultWithContent rc = new ResultWithContent();
            rc.setResult(result);
            rc.setModiCluster(cluster);
            rc.setModiCount(count);
            int update = resultDao.updateResult(rc);
            if (update <= 0) {
                return false;
            }
            String user = userService.getCurrentUser(request);
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
        String resultId = redisService.getString(KEY.RESULT_ID, request);
        String issueId = issueService.getCurrentIssueId(request);
        try {
            // 从redis获取数据
            List<String[]> content = (List<String[]>) redisService.getObject(KEY.REDIS_CONTENT, request);
            List<String[]> cluster = (List<String[]>) redisService.getObject(KEY.REDIS_CLUSTER_RESULT, request);
            // 合并集合
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
            // TODO:重新统计
            List<int[]> count = miningService.count(content, cluster);
            // 更新数据库
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
            String user = userService.getCurrentUser(request);
            Issue issue = new Issue();
            issue.setIssueId(issueId);
            issue.setLastOperator(user);
            issue.setLastUpdateTime(new Date());
            issueDao.updateIssueInfo(issue);
            // 更新redis数据
            redisService.setObject(KEY.REDIS_CLUSTER_RESULT, cluster, request);
            redisService.setObject(KEY.REDIS_COUNT_RESULT, count, request);
        } catch (Exception e) {
            logger.error("sth failed when combine sets:{}" + e.toString());
        }
        return true;
    }

    @Override
    public boolean reset(HttpServletRequest request) {
        // TODO Auto-generated method stub
        String resultId = redisService.getString(KEY.RESULT_ID, request);
        String issueId = issueService.getCurrentIssueId(request);
        // 从数据库获取数据
        List<String[]> origCluster = resultDao.getResultConentById(resultId, issueId, DIRECTORY.ORIG_CLUSTER);
        List<String[]> origCount = resultDao.getResultConentById(resultId, issueId, DIRECTORY.ORIG_COUNT);
        // 用原始数据覆盖修改后数据
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
        String user = userService.getCurrentUser(request);
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
        try {
            List<String[]> content = (List<String[]>) redisService.getObject(KEY.REDIS_CONTENT, request);
            List<String[]> cluster = (List<String[]>) redisService.getObject(KEY.REDIS_CLUSTER_RESULT, request);
            String[] set = cluster.get(params.getCurrentSet());
            Map<String, Map<String, Map<String, Integer>>> timeMap =
                    miningService.statistic(content, set, params.getInterval());
            Map<String, Object> reMap = miningService.getAmount(timeMap);
            Map<String, Integer> levelMap = (Map<String, Integer>) reMap.get(KEY.MINING_AMOUNT_MEDIA);
            Map<String, Integer> typeMap = (Map<String, Integer>) reMap.get(KEY.MINING_AMOUNT_TYPE);
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
    public Map<String, List<String[]>> exportService(String issueId, String resultId, HttpServletRequest request) {
        // TODO Auto-generated method stub
        try {
            List<String[]> content = (List<String[]>) redisService.getObject(KEY.REDIS_CONTENT, request);
            List<String[]> cluster = new ArrayList<String[]>();
            List<int[]> clusterIndex =
                    ConvertUtil.toIntList((List<String[]>) redisService.getObject(KEY.REDIS_CLUSTER_RESULT, request));
            for (int[] set : clusterIndex) {
                for (int index : set) {
                    String[] row = content.get(index);
                    cluster.add(row);
                }
                cluster.add(new String[1]);
            }
            List<String[]> count = new ArrayList<String[]>();
            List<int[]> countResult =
                    ConvertUtil.toIntList((List<String[]>) redisService.getObject(KEY.REDIS_COUNT_RESULT, request));
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

    @Override
    public String exportAbstract(List<String[]> count) {
        // TODO Auto-generated method stub
        if (null == count || count.size() == 0) {
            return null;
        }
        String[] firstData = count.get(0);
        Website firstSite = websiteDao.queryByUrl(CommonUtil.getPrefixUrl(firstData[1]));
        String line = "本次信息挖掘结果中，总共涉及 " + count.size() + " 个话题。";
        line += "其中，\"" + firstData[2] + "\" 话题的数量最多，总计 " + firstData[0] + "条,最早发布于 " + firstData[3] + ",来自 "
                + firstSite.getName() + " ,属于 " + firstSite.getType() + " 类型。\n";
        line += "其余排名前五的话题信息分别是：\n";
        for (int i = 1; i < count.size() && i < 5; i++) {
            String[] data = count.get(i);
            Website website = websiteDao.queryByUrl(CommonUtil.getPrefixUrl(data[1]));
            line += "话题名称：" + data[2] + "\n";
            line += "信息数量：" + data[0] + "\n";
            line += "最早发布时间：" + data[3] + "\n";
            line += "最早发布网站：" + website.getName() + "\n";
            line += "信息类型为:" + website.getLevel() + "\n\n\n";
        }
        return line;
    }

}
