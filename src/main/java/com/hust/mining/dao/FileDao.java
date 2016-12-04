package com.hust.mining.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hust.mining.dao.mapper.IssueFileMapper;
import com.hust.mining.model.IssueFile;
import com.hust.mining.model.IssueFileExample;
import com.hust.mining.model.IssueFileKey;
import com.hust.mining.model.IssueFileWithBLOBs;

public class FileDao {

    @Autowired
    private IssueFileMapper issueFileMapper;

    public int insert(IssueFileWithBLOBs file) {
        return issueFileMapper.insert(file);
    }

    public int deleteById(String fileId) {
        IssueFileKey key = new IssueFileKey();
        key.setFileId(fileId);
        return issueFileMapper.deleteByPrimaryKey(key);
    }

    public List<IssueFile> queryFilesByIssueId(String issueId) {
        IssueFileExample example = new IssueFileExample();
        example.createCriteria().andIssueIdEqualTo(issueId);
        return issueFileMapper.selectByExample(example);
    }

    public List<IssueFileWithBLOBs> queryFilesWithBOLOBsByIssueId(String issueId) {
        IssueFileExample example = new IssueFileExample();
        example.createCriteria().andIssueIdEqualTo(issueId);
        return issueFileMapper.selectByExampleWithBLOBs(example);
    }
}
