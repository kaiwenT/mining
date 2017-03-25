package com.hust.mining.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.mining.constant.Constant.DIRECTORY;
import com.hust.mining.dao.mapper.IssueFileMapper;
import com.hust.mining.model.IssueFile;
import com.hust.mining.model.IssueFileExample;
import com.hust.mining.model.IssueFileExample.Criteria;
import com.hust.mining.model.params.QueryFileCondition;
import com.hust.mining.util.FileUtil;

@Repository
public class FileDao {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(FileDao.class);

    @Autowired
    private IssueFileMapper issueFileMapper;

    public int insert(IssueFile file, List<String[]> content) {
        String filename = DIRECTORY.FILE + file.getFileId();
        try {
            boolean success = FileUtil.write(filename, content);
            if (success) {
                return issueFileMapper.insert(file);
            }
        } catch (Exception e) {
            logger.error("write file error:{}", e.toString());
        }
        return 0;
    }

    public int deleteById(String fileId) {
        if (FileUtil.delete(DIRECTORY.FILE + fileId)) {
            return issueFileMapper.deleteByPrimaryKey(fileId);
        }
        return 0;
    }

    public List<String[]> getFileContent(String...filenames) {
        List<String[]> content = FileUtil.read(filenames);
        return content;
    }

    public List<IssueFile> queryFilesByIssueId(String issueId) {
        IssueFileExample example = new IssueFileExample();
        example.createCriteria().andIssueIdEqualTo(issueId);
        return issueFileMapper.selectByExample(example);
    }

    public List<IssueFile> queryFilesByCondition(QueryFileCondition con) {
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
        return issueFileMapper.selectByExample(example);
    }
}
