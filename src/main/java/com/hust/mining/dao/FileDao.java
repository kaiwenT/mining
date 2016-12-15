package com.hust.mining.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.hust.mining.dao.mapper.IssueFileMapper;
import com.hust.mining.model.IssueFile;
import com.hust.mining.model.IssueFileExample;
import com.hust.mining.model.IssueFileExample.Criteria;
import com.hust.mining.model.IssueFileKey;
import com.hust.mining.model.IssueFileWithBLOBs;
import com.hust.mining.model.params.QueryFileCondition;

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

    public List<IssueFileWithBLOBs> queryFilesByCondition(QueryFileCondition con) {
        IssueFileExample example = new IssueFileExample();
        Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(con.getIssueId())) {
            criteria.andIssueIdEqualTo(con.getIssueId());
        }
        if (null != con.getStart()) {
            criteria.andUploadTimeGreaterThanOrEqualTo(con.getStart());
        }
        if (null != con.getEnd()) {
            criteria.andUploadTimeLessThanOrEqualTo(con.getEnd());
        }
        if (null != con.getFileIds() && con.getFileIds().size() != 0) {
            criteria.andFileIdIn(con.getFileIds());
        }
        return issueFileMapper.selectByExampleWithBLOBs(example);
    }
}
