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
import com.hust.mining.service.IssueService;
import com.hust.mining.service.MiningService;
import com.hust.mining.service.RedisService;
import com.hust.mining.service.UserService;
import com.hust.mining.util.ConvertUtil;
import com.hust.mining.util.FileUtil;

@Service
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueDao issueDao;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private UserService userService;
    @Autowired
    private MiningService miningService;
    @Autowired
    private ResultDao resultDao;
    @Autowired
    private RedisService redisService;

    @Override
    public int createIssue(String issueName, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String user = userService.getCurrentUser(request);
        Issue issue = new Issue();
        issue.setIssueId(UUID.randomUUID().toString());
        issue.setIssueName(issueName);
        issue.setCreator(user);
        issue.setCreateTime(new Date());
        issue.setLastOperator(user);
        issue.setLastUpdateTime(issue.getCreateTime());
        int insert = issueDao.insert(issue);
        if (insert > 0) {
            redisService.setString(KEY.ISSUE_ID, issue.getIssueId(), request);
        }
        return insert;
    }

    @Override
    public String getCurrentIssueId(HttpServletRequest request) {
        // TODO Auto-generated method stub
        String obj = redisService.getString(KEY.ISSUE_ID, request);
        if (null == obj) {
            return StringUtils.EMPTY;
        }
        return obj;
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
        String user = userService.getCurrentUser(request);
        List<Result> results = resultDao.queryResultsByIssueId(issueId);
        for (Result result : results) {
            FileUtil.delete(DIRECTORY.CONTENT + result.getRid());
            FileUtil.delete(DIRECTORY.MODIFY_CLUSTER + result.getRid());
            FileUtil.delete(DIRECTORY.MODIFY_COUNT + result.getRid());
            FileUtil.delete(DIRECTORY.ORIG_CLUSTER + result.getRid());
            FileUtil.delete(DIRECTORY.ORIG_COUNT + result.getRid());
        }
        return issueDao.deleteIssueById(issueId, user);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String[]> miningByTime(Date start, Date end, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String issueId = redisService.getString(KEY.ISSUE_ID, request);
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
        if (null == content) {
            return null;
        }
        Map<String, Object> res = mining(content);
        if (null == res) {
            return null;
        }
        // 开始插入数据库
        String user = userService.getCurrentUser(request);
        content = (List<String[]>) res.get("content");
        List<int[]> count = (List<int[]>) res.get("countResult");
        List<List<Integer>> cluster = (List<List<Integer>>) res.get("clusterResult");
        Result result = new Result();
        result.setRid(UUID.randomUUID().toString());
        result.setIssueId(issueId);
        result.setCreator(user);
        result.setCreateTime(new Date());
        ResultWithContent rc = new ResultWithContent();
        rc.setResult(result);
        rc.setContent(content);
        rc.setOrigCluster(ConvertUtil.toStringListB(cluster));
        rc.setOrigCount(ConvertUtil.toStringList(count));
        int update = resultDao.insert(rc);
        if (update <= 0) {
            return null;
        }
        // 插入数据库完成

        // 开始更新issue状态
        redisService.setString(KEY.RESULT_ID, result.getRid(), request);
        Issue issue = new Issue();
        issue.setIssueId(issueId);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        issueDao.updateIssueInfo(issue);
        // 状态更新完成

        // 开始根据统计结果映射源数据
        List<String[]> list = new ArrayList<String[]>();
        for (int[] array : count) {
            String[] old = content.get(array[Index.COUNT_ITEM_INDEX]);
            String[] row = new String[old.length + 1];
            System.arraycopy(old, 0, row, 1, old.length);
            row[0] = array[Index.COUNT_ITEM_AMOUNT] + "";
            list.add(row);
        }
        // 映射完成
        redisService.setObject(KEY.REDIS_CONTENT, content, request);
        redisService.setObject(KEY.REDIS_CLUSTER_RESULT, rc.getOrigCluster(), request);
        redisService.setObject(KEY.REDIS_COUNT_RESULT, rc.getOrigCount(), request);
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String[]> miningByFileIds(List<String> fileIds, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String user = userService.getCurrentUser(request);
        String issueId = redisService.getString(KEY.ISSUE_ID, request);
        QueryFileCondition con = new QueryFileCondition();
        con.setIssueId(issueId);
        con.setFileIds(fileIds);
        List<IssueFile> files = fileDao.queryFilesByCondition(con);
        String[] filenames = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            filenames[i] = DIRECTORY.FILE + files.get(i).getFileId();
        }
        List<String[]> content = fileDao.getFileContent(filenames);
        if (null == content) {
            return null;
        }
        Map<String, Object> res = mining(content);
        if (null == res) {
            return null;
        }
        // 开始插入数据库
        content = (List<String[]>) res.get("content");
        List<int[]> count = (List<int[]>) res.get("countResult");
        List<List<Integer>> cluster = (List<List<Integer>>) res.get("clusterResult");
        String comment = "";
        for (int i = 0; i < files.size(); i++) {
            if (i == 0) {
                comment = files.get(i).getFileName();
            } else {
                comment += "\t" + files.get(i).getFileName();
            }
        }
        Result result = new Result();
        result.setRid(UUID.randomUUID().toString());
        result.setIssueId(issueId);
        result.setCreator(user);
        result.setCreateTime(new Date());
        result.setComment(comment);
        ResultWithContent rc = new ResultWithContent();
        rc.setResult(result);
        rc.setContent(content);
        rc.setOrigCluster(ConvertUtil.toStringListB(cluster));
        rc.setOrigCount(ConvertUtil.toStringList(count));
        int update = resultDao.insert(rc);
        if (update <= 0) {
            return null;
        }
        // 插入数据库完成

        // 开始更新issue状态
        redisService.setString(KEY.RESULT_ID, result.getRid(), request);
        Issue issue = new Issue();
        issue.setIssueId(issueId);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        issueDao.updateIssueInfo(issue);
        // 状态更新完成

        // 开始根据统计结果映射源数据
        List<String[]> list = new ArrayList<String[]>();
        for (int[] array : count) {
            String[] old = content.get(array[Index.COUNT_ITEM_INDEX]);
            String[] row = new String[old.length + 1];
            System.arraycopy(old, 0, row, 1, old.length);
            row[0] = array[Index.COUNT_ITEM_AMOUNT] + "";
            list.add(row);
        }
        // 映射完成
        redisService.setObject(KEY.REDIS_CONTENT, content, request);
        redisService.setObject(KEY.REDIS_CLUSTER_RESULT, rc.getOrigCluster(), request);
        redisService.setObject(KEY.REDIS_COUNT_RESULT, rc.getOrigCount(), request);
        return list;
    }

    private Map<String, Object> mining(List<String[]> content) {
        if (content == null || content.size() == 0) {
            return null;
        }
        // 聚类
        List<List<Integer>> clusterResult = miningService.cluster(content);
        // 统计
        List<String[]> cluster = ConvertUtil.toStringListB(clusterResult);
        List<int[]> countResult = miningService.count(content, cluster);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("clusterResult", clusterResult);
        result.put("countResult", countResult);
        result.put("content", content);
        return result;
    }

}
