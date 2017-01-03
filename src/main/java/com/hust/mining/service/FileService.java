package com.hust.mining.service;

import java.util.List;

import com.hust.mining.model.IssueFile;
import com.hust.mining.model.params.Condition;

public interface FileService {
    int insert(Condition con);

    int deleteById(String fileId);

    List<IssueFile> queryFilesByIssueId(String issueId);

}
