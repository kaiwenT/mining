package com.hust.mining.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hust.mining.model.IssueFile;
import com.hust.mining.model.params.Condition;

public interface FileService {
    int insert(Condition con, HttpServletRequest request);

    int deleteById(String fileId);

    List<IssueFile> queryFilesByIssueId(String issueId);

    List<String[]> combineFilesContentOnSameIssueId(String issueId);
    
    
}
