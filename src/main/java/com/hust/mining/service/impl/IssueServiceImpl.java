package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.constant.Constant;
import com.hust.mining.constant.Constant.Index;
import com.hust.mining.dao.IssueDao;
import com.hust.mining.model.Issue;
import com.hust.mining.model.IssueWithBLOBs;
import com.hust.mining.model.params.DeleteItemsParams;
import com.hust.mining.model.params.IssueQueryCondition;
import com.hust.mining.service.FileService;
import com.hust.mining.service.IssueService;
import com.hust.mining.service.StatisticService;
import com.hust.mining.service.UserService;
import com.hust.mining.util.ConvertUtil;
import com.hust.mining.util.ResultUtil;

@Service
public class IssueServiceImpl implements IssueService {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    @Autowired
    private IssueDao issueDao;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private StatisticService statService;

    @Override
    public int createIssue(IssueWithBLOBs issue) {
        // TODO Auto-generated method stub
        return issueDao.insert(issue);
    }

    @Override
    public int combineFiles(String issueId, String user) {
        // TODO Auto-generated method stub
        try {
            List<String[]> oldlist = fileService.combineFilesContentOnSameIssueId(issueId);
            List<String[]> list = new ArrayList<String[]>();
            List<String> urllist = new ArrayList<String>();
            for (String[] row : oldlist) {
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
            IssueWithBLOBs issue = new IssueWithBLOBs();
            issue.setIssueId(issueId);
            issue.setFilteredContent(ConvertUtil.convertToBytes(list));
            issue.setLastOperator(user);
            return issueDao.updateIssueInfo(issue);
        } catch (Exception e) {
            logger.error("combining files related to this issue:{} failed", issueId);
            return 0;
        }
    }

    @Override
    public String getCurrentIssueId(HttpServletRequest request) {
        // TODO Auto-generated method stub
        Object obj = request.getSession().getAttribute(Constant.ISSUE_ID);
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

    @SuppressWarnings("unchecked")
    @Override
    public boolean combineCountResult(int[] indexes, HttpServletRequest request) {
        String issueId = request.getSession().getAttribute(Constant.ISSUE_ID).toString();
        List<List<String[]>> resultList = null;
        try {
            resultList = (List<List<String[]>>) ConvertUtil
                    .convertBytesToObject(issueDao.selectByUUID(issueId).getModifiedClusterResult());
        } catch (Exception e) {
            return false;
        }
        List<String[]> combineList = new ArrayList<String[]>();
        for (int i = 0; i < indexes.length; i++) {
            combineList.addAll(resultList.get(indexes[i]));
        }
        Arrays.sort(indexes);
        for (int i = indexes.length - 1; i >= 0; i--) {
            resultList.remove(indexes[i]);
        }
        resultList.add(combineList);
        Collections.sort(resultList, new Comparator<List<String[]>>() {

            @Override
            public int compare(List<String[]> o1, List<String[]> o2) {
                // TODO Auto-generated method stub
                return o2.size() - o1.size();
            }
        });
        List<String[]> calResult = statService.getOrigAndCount(resultList, Index.TIME_INDEX);
        IssueWithBLOBs issue = new IssueWithBLOBs();
        issue.setIssueId(issueId);
        try {
            issue.setModifiedClusterResult(ConvertUtil.convertToBytes(resultList));
            issue.setModifiedOrigCountResult(ConvertUtil.convertToBytes(calResult));
            issue.setLastOperator(userService.getCurrentUser(request));
            issue.setLastUpdateTime(new Date());
            if (issueDao.updateIssueInfo(issue) == 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public IssueWithBLOBs queryIssueWithBLOBsById(String uuid) {
        // TODO Auto-generated method stub
        return issueDao.selectByUUID(uuid);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean deleteItemsFromClusterResult(DeleteItemsParams params, HttpServletRequest request) {
        String issueId = getCurrentIssueId(request);
        try {
            List<List<String[]>> origlist = (List<List<String[]>>) ConvertUtil
                    .convertBytesToObject(queryIssueWithBLOBsById(issueId).getModifiedClusterResult());
            List<String[]> setList = origlist.get(params.getCurrentSet());
            Arrays.sort(params.getIndexSet());
            for (int i = params.getIndexSet().length - 1; i >= 0; i--) {
                setList.remove(i);
            }
            origlist.set(params.getCurrentSet(), setList);
            Collections.sort(origlist, new Comparator<List<String[]>>() {

                @Override
                public int compare(List<String[]> o1, List<String[]> o2) {
                    // TODO Auto-generated method stub
                    return o2.size() - o1.size();
                }
            });
            List<String[]> calList = statService.getOrigAndCount(origlist, Index.TIME_INDEX);
            IssueWithBLOBs issue = new IssueWithBLOBs();
            issue.setIssueId(issueId);
            issue.setModifiedClusterResult(ConvertUtil.convertToBytes(origlist));
            issue.setModifiedOrigCountResult(ConvertUtil.convertToBytes(calList));
            issue.setLastOperator(userService.getCurrentUser(request));
            issue.setLastUpdateTime(new Date());
            if (0 == issueDao.updateIssueInfo(issue)) {
                return false;
            }
        } catch (Exception e) {
            logger.error("exception occur during deleting items from cluster result\t" + e.toString());
            return false;
        }
        return true;
    }

    @Override
    public int updateIssueInfo(IssueWithBLOBs issue, HttpServletRequest request) {
        // TODO Auto-generated method stub
        String user = userService.getCurrentUser(request);
        issue.setLastOperator(user);
        issue.setLastUpdateTime(new Date());
        return issueDao.updateIssueInfo(issue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<List<String[]>> queryClusterResult(String issueId) {
        // TODO Auto-generated method stub
        try {
            List<List<String[]>> result = (List<List<String[]>>) ConvertUtil
                    .convertBytesToObject(issueDao.selectByUUID(issueId).getModifiedClusterResult());
            return result;
        } catch (Exception e) {
            logger.info("query cluster result from DB failed, issueId:{} \t" + e.toString(), issueId);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean deleteSetsFromClusterResult(int[] set, HttpServletRequest request) {
        String issueId = getCurrentIssueId(request);
        try {
            List<List<String[]>> origClusterList = null;
            List<String[]> origCalList = null;
                origClusterList = (List<List<String[]>>) ConvertUtil
                        .convertBytesToObject(queryIssueWithBLOBsById(issueId).getModifiedClusterResult());
                origCalList = (List<String[]>) ConvertUtil
                        .convertBytesToObject(queryIssueWithBLOBsById(issueId).getModifiedOrigCountResult());
            Arrays.sort(set);
            for (int i = set.length - 1; i >= 0; i--) {
                origClusterList.remove(set[i]);
                origCalList.remove(set[i]);
            }
            IssueWithBLOBs issue = new IssueWithBLOBs();
            issue.setIssueId(issueId);
            issue.setModifiedClusterResult(ConvertUtil.convertToBytes(origClusterList));
            issue.setModifiedOrigCountResult(ConvertUtil.convertToBytes(origCalList));
            issue.setLastOperator(userService.getCurrentUser(request));
            issue.setLastUpdateTime(new Date());
            if (0 == issueDao.updateIssueInfo(issue)) {
                return false;
            }
        }catch(

    Exception e)
    {
            logger.error("exception occur during deleting sets from cluster result\t" + e.toString());
            return false;
        }return true;
    }

    @Override
    public long countIssues(IssueQueryCondition con) {
        // TODO Auto-generated method stub
        return issueDao.countIssues(con);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean reset(HttpServletRequest request) {
        try {
            String issueId = request.getSession().getAttribute(Constant.ISSUE_ID).toString();
            IssueWithBLOBs issue = issueDao.selectByUUID(issueId);
            List<List<String[]>> content =
                    (List<List<String[]>>) ConvertUtil.convertBytesToObject(issue.getClusterResult());
            List<String[]> calResult = statService.getOrigAndCount(content, Index.TIME_INDEX);
            issue.setModifiedClusterResult(issue.getClusterResult());
            issue.setModifiedOrigCountResult(ConvertUtil.convertToBytes(calResult));
            issue.setLastOperator(userService.getCurrentUser(request));
            issue.setLastUpdateTime(new Date());
            if (0 == issueDao.updateIssueInfo(issue)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
