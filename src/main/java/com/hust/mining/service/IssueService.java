package com.hust.mining.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hust.mining.model.Issue;
import com.hust.mining.model.params.IssueQueryCondition;

public interface IssueService {

    int createIssue(String issueName);

    int deleteIssueById(String issueId);

    String getCurrentIssueId();

    List<Issue> queryIssue(IssueQueryCondition con);

    Issue queryIssueById(String issueId);

    int updateIssueInfo(Issue issue);

    List<String[]> miningByTime(Date start, Date end);

    List<String[]> miningByFileIds(List<String> fileIds);
    
}
