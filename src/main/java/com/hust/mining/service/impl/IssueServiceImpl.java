package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.dao.FileDao;
import com.hust.mining.dao.IssueDao;
import com.hust.mining.dao.ResultDao;
import com.hust.mining.model.Issue;
import com.hust.mining.model.IssueFileWithBLOBs;
import com.hust.mining.model.ResultWithBLOBs;
import com.hust.mining.model.params.IssueQueryCondition;
import com.hust.mining.model.params.QueryFileCondition;
import com.hust.mining.service.ClusterService;
import com.hust.mining.service.IssueService;
import com.hust.mining.service.StatisticService;
import com.hust.mining.service.UserService;
import com.hust.mining.util.ConvertUtil;

@Service
public class IssueServiceImpl implements IssueService {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

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
        issue.setLastUpdateTime(issue.getLastUpdateTime());
        request.getSession().setAttribute(KEY.ISSUE_ID, issue.getIssueId());
        return issueDao.insert(issue);
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
    public int deleteIssueById(String issueId) {
        // TODO Auto-generated method stub
        return issueDao.deleteIssueById(issueId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<int[]> miningByTime(Date start, Date end, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
        QueryFileCondition con = new QueryFileCondition();
        con.setIssueId(issueId);
        con.setStart(start);
        con.setEnd(end);
        List<IssueFileWithBLOBs> files = fileDao.queryFilesByCondition(con);
        ResultWithBLOBs result = mining(files);
        int update = resultDao.insert(result);
        if (update <= 0) {
            return null;
        }
        request.getSession().setAttribute(KEY.RESULT_ID, result.getRid());
        String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
        Issue issue = new Issue();
        issue.setIssueId(issueId);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        issueDao.updateIssueInfo(issue);
        try {
            List<int[]> count = (List<int[]>) ConvertUtil.convertBytesToObject(result.getModifiedCountResult());
            return count;
        } catch (Exception e) {
            logger.info("exception occur when convert:{}", e.toString());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<int[]> miningByFileIds(List<String> fileIds, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String issueId = request.getSession().getAttribute(KEY.ISSUE_ID).toString();
        QueryFileCondition con = new QueryFileCondition();
        con.setIssueId(issueId);
        con.setFileIds(fileIds);
        List<IssueFileWithBLOBs> files = fileDao.queryFilesByCondition(con);
        ResultWithBLOBs result = mining(files);
        int update = resultDao.insert(result);
        if (update <= 0) {
            return null;
        }
        request.getSession().setAttribute(KEY.RESULT_ID, result.getRid());
        String user = request.getSession().getAttribute(KEY.USER_NAME).toString();
        Issue issue = new Issue();
        issue.setIssueId(issueId);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        issueDao.updateIssueInfo(issue);
        try {
            List<int[]> count = (List<int[]>) ConvertUtil.convertBytesToObject(result.getModifiedCountResult());
            return count;
        } catch (Exception e) {
            logger.info("exception occur when convert:{}", e.toString());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private ResultWithBLOBs mining(List<IssueFileWithBLOBs> files) {
        if (files == null) {
            return null;
        }
        List<String[]> content = new ArrayList<>();
        for (IssueFileWithBLOBs file : files) {
            try {
                List<String[]> tmp = (List<String[]>) ConvertUtil.convertBytesToObject(file.getContent());
                content.addAll(tmp);
            } catch (Exception e) {
                logger.info("convert failed, fileName : {}", file.getFileName());
            }
        }
        if (content.size() == 0) {
            return null;
        }
        List<List<Integer>> clusterResult = clusterService.cluster(content);
        List<int[]> countResult = statService.count(clusterResult, content);
        ResultWithBLOBs result = new ResultWithBLOBs();
        result.setRid(UUID.randomUUID().toString());
        try {
            result.setContent(ConvertUtil.convertToBytes(content));
            result.setOrigResult(ConvertUtil.convertToBytes(clusterResult));
            result.setModifiedCountResult(ConvertUtil.convertToBytes(clusterResult));
            result.setCountResult(ConvertUtil.convertToBytes(countResult));
            result.setModifiedCountResult(ConvertUtil.convertToBytes(countResult));
        } catch (Exception e) {
            logger.error("exception occur when convert to byte : {}", e.toString());
        }
        return result;
    }

}
