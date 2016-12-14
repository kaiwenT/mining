package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.hust.mining.constant.Constant.Index;
import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.dao.IssueDao;
import com.hust.mining.dao.ResultDao;
import com.hust.mining.model.Issue;
import com.hust.mining.model.ResultWithBLOBs;
import com.hust.mining.model.params.StatisticParams;
import com.hust.mining.service.ResultService;
import com.hust.mining.service.StatisticService;
import com.hust.mining.util.ConvertUtil;

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

    @SuppressWarnings("unchecked")
    @Override
    public List<String[]> getCountResultById(String resultId) {
        // TODO Auto-generated method stub
        ResultWithBLOBs result = resultDao.getResultWithBLOBsById(resultId);
        if (result == null) {
            return null;
        }
        List<String[]> list = new ArrayList<String[]>();
        try {
            List<String[]> content = (List<String[]>) ConvertUtil.convertBytesToObject(result.getContent());
            List<int[]> count = (List<int[]>) ConvertUtil.convertBytesToObject(result.getModifiedCountResult());
            for (int[] item : count) {
                String[] old = content.get(item[Index.COUNT_ITEM_INDEX]);
                String[] ne = new String[old.length + 1];
                System.arraycopy(old, 0, ne, 0, old.length);
                ne[old.length] = item[Index.COUNT_ITEM_AMOUNT] + "";
                list.add(ne);
            }
        } catch (Exception e) {
            logger.error("convert result fail:{}" + e.toString());
            return null;
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean deleteSets(int[] sets, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String resultId = request.getSession().getAttribute(KEY.RESULT_ID).toString();
        ResultWithBLOBs result = resultDao.getResultWithBLOBsById(resultId);
        if (null == result) {
            return false;
        }
        try {
            List<List<Integer>> clusterResult =
                    (List<List<Integer>>) ConvertUtil.convertBytesToObject(result.getModifiedResult());
            List<int[]> countResult = (List<int[]>) ConvertUtil.convertBytesToObject(result.getModifiedCountResult());
            Arrays.sort(sets);
            for (int i = sets.length - 1; i >= 0; i--) {
                clusterResult.remove(sets[i]);
                countResult.remove(sets[i]);
            }
            result.setModifiedResult(ConvertUtil.convertToBytes(clusterResult));
            result.setModifiedCountResult(ConvertUtil.convertToBytes(countResult));
            int update = resultDao.updateResultWithBLOBs(result);
            if (update <= 0) {
                return false;
            }
            String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
            String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
            Issue issue = new Issue();
            issue.setIssueId(issueId);
            issue.setLastOperator(user);
            issue.setLastUpdateTime(new Date());
            issueDao.updateIssueInfo(issue);
        } catch (Exception e) {
            logger.error("sth failed when delete sets:{}" + e.toString());
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean combineSets(int[] sets, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String resultId = request.getSession().getAttribute(KEY.RESULT_ID).toString();
        ResultWithBLOBs result = resultDao.getResultWithBLOBsById(resultId);
        if (null == result) {
            return false;
        }
        try {
            List<String[]> content = (List<String[]>) ConvertUtil.convertBytesToObject(result.getContent());
            List<List<Integer>> clusterResult =
                    (List<List<Integer>>) ConvertUtil.convertBytesToObject(result.getModifiedResult());
            List<Integer> combineList = new ArrayList<Integer>();
            for (int i : sets) {
                combineList.addAll(clusterResult.get(i));
            }
            Arrays.sort(sets);
            for (int i = sets.length - 1; i >= 0; i--) {
                clusterResult.remove(sets[i]);
            }
            clusterResult.add(combineList);
            Collections.sort(clusterResult, new Comparator<List<Integer>>() {

                @Override
                public int compare(List<Integer> o1, List<Integer> o2) {
                    // TODO Auto-generated method stub
                    return o2.size() - o1.size();
                }
            });
            // TODO:重构统计
            List<int[]> countResult = statService.count(clusterResult, content);
            result.setModifiedResult(ConvertUtil.convertToBytes(clusterResult));
            result.setModifiedCountResult(ConvertUtil.convertToBytes(countResult));
            int update = resultDao.updateResultWithBLOBs(result);
            if (update <= 0) {
                return false;
            }
            String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
            String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
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
        ResultWithBLOBs result = resultDao.getResultWithBLOBsById(resultId);
        if (null == result) {
            return false;
        }
        result.setModifiedResult(result.getOrigResult());
        result.setModifiedCountResult(result.getCountResult());
        int update = resultDao.updateResultWithBLOBs(result);
        if (update <= 0) {
            return false;
        }
        String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
        String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
        Issue issue = new Issue();
        issue.setIssueId(issueId);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        issueDao.updateIssueInfo(issue);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String[]> getItemsInSets(int set, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String resultId = request.getSession().getAttribute(KEY.RESULT_ID).toString();
        ResultWithBLOBs result = resultDao.getResultWithBLOBsById(resultId);
        if (result == null) {
            return null;
        }
        List<String[]> list = new ArrayList<String[]>();
        try {
            List<String[]> content = (List<String[]>) ConvertUtil.convertBytesToObject(result.getContent());
            List<List<Integer>> clusterResult =
                    (List<List<Integer>>) ConvertUtil.convertBytesToObject(result.getModifiedResult());
            List<Integer> indexSet = clusterResult.get(set);
            for (int item : indexSet) {
                String[] row = content.get(item);
                list.add(row);
            }
        } catch (Exception e) {
            logger.error("sth error:{}" + e.toString());
            return null;
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> statistic(StatisticParams params, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String resultId = request.getSession().getAttribute(KEY.RESULT_ID).toString();
        ResultWithBLOBs result = resultDao.getResultWithBLOBsById(resultId);
        try {
            List<String[]> content = (List<String[]>) ConvertUtil.convertBytesToObject(result.getContent());
            List<List<Integer>> clusterResult =
                    (List<List<Integer>>) ConvertUtil.convertBytesToObject(result.getModifiedResult());
            List<Integer> set = clusterResult.get(params.getCurrentSet());
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
}
