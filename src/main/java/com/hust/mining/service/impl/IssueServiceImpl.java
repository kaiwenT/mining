package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.constant.Constant.DIRECTORY;
import com.hust.mining.constant.Constant.Index;
import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.dao.FileDao;
import com.hust.mining.dao.IssueDao;
import com.hust.mining.dao.ResultDao;
import com.hust.mining.model.Issue;
import com.hust.mining.model.IssueFile;
import com.hust.mining.model.Result;
import com.hust.mining.model.ResultWithContent;
import com.hust.mining.model.params.IssueQueryCondition;
import com.hust.mining.model.params.QueryFileCondition;
import com.hust.mining.redis.RedisFacade;
import com.hust.mining.service.ClusterService;
import com.hust.mining.service.IssueService;
import com.hust.mining.service.StatisticService;
import com.hust.mining.service.UserService;
import com.hust.mining.util.ConvertUtil;

@Service
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueDao issueDao;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private UserService userService;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private StatisticService statService;
    @Autowired
    private ResultDao resultDao;

    @Override
    public int createIssue(String issueName, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
        Issue issue = new Issue();
        issue.setIssueId(UUID.randomUUID().toString());
        issue.setIssueName(issueName);
        issue.setCreator(user);
        issue.setCreateTime(new Date());
        issue.setLastOperator(user);
        issue.setLastUpdateTime(issue.getCreateTime());
        int insert = issueDao.insert(issue);
        if (insert > 0) {
            request.getSession().setAttribute(KEY.ISSUE_ID, issue.getIssueId());
        }
        return insert;
    }

    @Override
    public String getCurrentIssueId(HttpServletRequest request) {
        // TODO Auto-generated method stub
        Object obj = request.getSession().getAttribute(KEY.ISSUE_ID);
        if (null == obj) {
            return StringUtils.EMPTY;
        }
        return obj.toString();
    }

    @Override
    public List<Issue> queryIssue(IssueQueryCondition con) {
        // TODO Auto-generated method stub
        return issueDao.queryIssue(con);
    }

    @Override
    public Issue queryIssueById(String uuid) {
        // TODO Auto-generated method stub
        return issueDao.selectById(uuid);
    }

    @Override
    public int updateIssueInfo(Issue issue, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String user = userService.getCurrentUser(request);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        return issueDao.updateIssueInfo(issue);
    }

    @Override
    public int deleteIssueById(String issueId, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
        return issueDao.deleteIssueById(issueId, user);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String[]> miningByTime(Date start, Date end, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
        QueryFileCondition con = new QueryFileCondition();
        con.setIssueId(issueId);
        con.setStart(start);
        con.setEnd(end);
        List<IssueFile> files = fileDao.queryFilesByCondition(con);
        String[] filenames = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            filenames[i] = DIRECTORY.FILE + files.get(i).getFileId();
        }
        List<String[]> content = fileDao.getFileContent(filenames);
        Map<String, Object> res = mining(content);
        if (null == res) {
            return null;
        }
        String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
        Result result = new Result();
        result.setRid(UUID.randomUUID().toString());
        result.setIssueId(issueId);
        result.setCreator(user);
        result.setCreateTime(new Date());
        ResultWithContent rc = new ResultWithContent();
        rc.setResult(result);
        rc.setContent(content);
        rc.setOrigCluster(toStringArrayA((List<List<Integer>>) res.get("clusterResult")));
        rc.setOrigCount(ConvertUtil.toStringList(((List<int[]>) res.get("countResult"))));
        int update = resultDao.insert(rc);
        if (update <= 0) {
            return null;
        }
        request.getSession().setAttribute(KEY.RESULT_ID, result.getRid());
        Issue issue = new Issue();
        issue.setIssueId(issueId);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        issueDao.updateIssueInfo(issue);
        List<int[]> count = (List<int[]>) res.get("countResult");
        List<String[]> cluster = (List<String[]>) res.get("clusterResult");
        List<String[]> list = new ArrayList<String[]>();
        for (int[] array : count) {
            String[] old = content.get(array[Index.COUNT_ITEM_INDEX]);
            String[] row = new String[old.length + 1];
            System.arraycopy(old, 0, row, 1, old.length);
            row[0] = array[Index.COUNT_ITEM_AMOUNT] + "";
            list.add(row);
        }
        RedisFacade redis = RedisFacade.getInstance(true);
        redis.setObject(KEY.RESULT_CONTENT, content);
        redis.setObject(KEY.RESULT_CLUSTER, cluster);
        redis.setObject(KEY.RESULT_COUNT, count);
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String[]> miningByFileIds(List<String> fileIds, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
        QueryFileCondition con = new QueryFileCondition();
        con.setIssueId(issueId);
        con.setFileIds(fileIds);
        List<IssueFile> files = fileDao.queryFilesByCondition(con);
        String[] filenames = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            filenames[i] = DIRECTORY.FILE + files.get(i).getFileId();
        }
        List<String[]> content = fileDao.getFileContent(filenames);
        Map<String, Object> res = mining(content);
        if (null == res) {
            return null;
        }
        String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
        Result result = new Result();
        result.setRid(UUID.randomUUID().toString());
        result.setIssueId(issueId);
        result.setCreator(user);
        result.setCreateTime(new Date());
        ResultWithContent rc = new ResultWithContent();
        rc.setResult(result);
        rc.setContent(content);
        rc.setOrigCluster(toStringArrayA((List<List<Integer>>) res.get("clusterResult")));
        rc.setOrigCount(ConvertUtil.toStringList(((List<int[]>) res.get("countResult"))));
        int update = resultDao.insert(rc);
        if (update <= 0) {
            return null;
        }
        request.getSession().setAttribute(KEY.RESULT_ID, result.getRid());
        Issue issue = new Issue();
        issue.setIssueId(issueId);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        issueDao.updateIssueInfo(issue);
        List<int[]> count = (List<int[]>) res.get("countResult");
        List<String[]> cluster = (List<String[]>) res.get("clusterResult");
        List<String[]> list = new ArrayList<String[]>();
        for (int[] array : count) {
            String[] old = content.get(array[Index.COUNT_ITEM_INDEX]);
            String[] row = new String[old.length + 1];
            System.arraycopy(old, 0, row, 0, old.length);
            row[old.length] = array[Index.COUNT_ITEM_AMOUNT] + "";
            list.add(row);
        }
        RedisFacade redis = RedisFacade.getInstance(true);
        redis.setObject(KEY.RESULT_CONTENT, content);
        redis.setObject(KEY.RESULT_CLUSTER, cluster);
        redis.setObject(KEY.RESULT_COUNT, count);
        return list;
    }

    private Map<String, Object> mining(List<String[]> content) {
        if (content == null || content.size() == 0) {
            return null;
        }
        // 去重开始
        List<String[]> list = new ArrayList<String[]>();
        List<String> urllist = new ArrayList<String>();
        for (String[] row : content) {
            int exitIndex = urllist.indexOf(row[Index.URL_INDEX]);
            if (exitIndex != -1) {
                if (row[Index.TIME_INDEX].compareTo(list.get(exitIndex)[Index.TIME_INDEX]) < 0) {
                    list.set(exitIndex, row);
                }
            } else {
                list.add(row);
                urllist.add(row[Index.URL_INDEX]);
            }
        }
        // 去重结束
        // 聚类
        List<List<Integer>> clusterResult = clusterService.cluster(list);
        // 统计
        List<int[]> countResult = statService.count(clusterResult, list);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("clusterResult", clusterResult);
        result.put("countResult", countResult);
        return result;
    }

    private List<String[]> toStringArrayA(List<List<Integer>> list) {
        List<String[]> result = new ArrayList<>();
        for (List<Integer> ele : list) {
            int[] array = new int[ele.size()];
            for (int i = 0; i < ele.size(); i++) {
                array[i] = ele.get(i);
            }
            String[] row = ConvertUtil.toStringArray(array);
            result.add(row);
        }
        return result;
    }

}
