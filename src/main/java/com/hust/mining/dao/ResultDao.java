package com.hust.mining.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hust.mining.dao.mapper.ResultMapper;
import com.hust.mining.model.Result;
import com.hust.mining.model.ResultExample;
import com.hust.mining.model.ResultExample.Criteria;
import com.hust.mining.model.ResultKey;
import com.hust.mining.model.ResultWithBLOBs;
import com.hust.mining.util.ConvertUtil;

public class ResultDao {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ResultDao.class);

    @Autowired
    private ResultMapper resultMapper;

    @SuppressWarnings("unchecked")
    public List<int[]> getCountResultById(String resultId) {
        ResultKey key = new ResultKey();
        key.setRid(resultId);
        List<int[]> list = null;
        try {
            list = (List<int[]>) ConvertUtil
                    .convertBytesToObject(resultMapper.selectByPrimaryKey(key).getModifiedCountResult());
        } catch (Exception e) {
            logger.warn("convert countresult error：{}", e.toString());
        }
        return list;
    }

    public ResultWithBLOBs getResultWithBLOBsById(String resultId, String issueId) {
        ResultExample example = new ResultExample();
        Criteria cri = example.createCriteria();
        cri.andRidEqualTo(resultId);
        cri.andIssueIdEqualTo(issueId);
        List<ResultWithBLOBs> list = resultMapper.selectByExampleWithBLOBs(example);
        if (null == list || list.size() > 0) {
            return null;
        }
        return list.get(0);
    }

    public int updateResultWithBLOBs(ResultWithBLOBs result) {
        return resultMapper.updateByPrimaryKeyWithBLOBs(result);
    }

    public int delResultById(String resultId) {
        ResultKey key = new ResultKey();
        key.setRid(resultId);
        int del = resultMapper.deleteByPrimaryKey(key);
        return del;
    }

    public int insert(ResultWithBLOBs result) {
        int insert = resultMapper.insert(result);
        return insert;
    }

    public List<Result> queryResultsByIssueId(String issueId) {
        ResultExample example = new ResultExample();
        example.createCriteria().andIssueIdEqualTo(issueId);
        return resultMapper.selectByExample(example);
    }
}
