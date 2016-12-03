package com.hust.mining.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hust.mining.model.Issue;
import com.hust.mining.model.IssueWithBLOBs;
import com.hust.mining.model.params.DeleteItemsParams;
import com.hust.mining.model.params.IssueQueryCondition;

public interface IssueService {

    int createIssue(IssueWithBLOBs issue);

    int combineFiles(String UUID, String user);

    String getCurrentIssueId(HttpServletRequest request);

    List<Issue> queryIssue(IssueQueryCondition con);

    IssueWithBLOBs queryIssueWithBLOBsById(String uuid);
    
    boolean deleteItemsFromClusterResult(DeleteItemsParams params, HttpServletRequest request);

    boolean combineCountResult(String type, int[] indexes, HttpServletRequest request);

    int updateIssueInfo(IssueWithBLOBs issue, HttpServletRequest request);

    List<List<String[]>> queryClusterResult(String issueId);

    List<List<String[]>> queryModifiedClusterResult(String issueId);

    boolean deleteSetsFromClusterResult(String type, int[] set, HttpServletRequest request);

    long countIssues(IssueQueryCondition con);

}
