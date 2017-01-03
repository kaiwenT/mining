package com.hust.mining.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.mining.constant.Constant.DIRECTORY;
import com.hust.mining.dao.mapper.ResultMapper;
import com.hust.mining.model.Result;
import com.hust.mining.model.ResultExample;
import com.hust.mining.model.ResultExample.Criteria;
import com.hust.mining.model.ResultWithContent;
import com.hust.mining.util.FileUtil;

@Repository
public class ResultDao {

    @Autowired
    private ResultMapper resultMapper;

    public List<String[]> getResultConentById(String resultId, String issueId, String path) {
        ResultExample example = new ResultExample();
        Criteria cri = example.createCriteria();
        cri.andRidEqualTo(resultId);
        cri.andIssueIdEqualTo(issueId);
        List<Result> list = resultMapper.selectByExample(example);
        if (null == list || list.size() == 0) {
            return null;
        }
        List<String[]> clusterResult = FileUtil.read(path + resultId);
        return clusterResult;
    }

    public int updateResult(ResultWithContent rc) {
        String name = rc.getResult().getRid();
        if (rc.getModiCluster() != null) {
            FileUtil.write(DIRECTORY.MODIFY_CLUSTER + name, rc.getModiCluster());
        }
        if (rc.getModiCount() != null) {
            FileUtil.write(DIRECTORY.MODIFY_COUNT + name, rc.getModiCount());
        }
        Result result = rc.getResult();
        return resultMapper.updateByPrimaryKeySelective(result);
    }

    public int delResultById(String resultId) {
        FileUtil.delete(DIRECTORY.CONTENT + resultId);
        FileUtil.delete(DIRECTORY.MODIFY_CLUSTER + resultId);
        FileUtil.delete(DIRECTORY.MODIFY_COUNT + resultId);
        FileUtil.delete(DIRECTORY.ORIG_CLUSTER + resultId);
        FileUtil.delete(DIRECTORY.ORIG_COUNT + resultId);
        int del = resultMapper.deleteByPrimaryKey(resultId);
        return del;
    }

    public int insert(ResultWithContent rc) {
        String name = rc.getResult().getRid();
        String contentpath = DIRECTORY.CONTENT + name;
        FileUtil.write(contentpath, rc.getContent());
        String clusterpath = DIRECTORY.ORIG_CLUSTER + name;
        FileUtil.write(clusterpath, rc.getOrigCluster());
        String countpath = DIRECTORY.ORIG_COUNT + name;
        FileUtil.write(countpath, rc.getOrigCount());
        String modicluster = DIRECTORY.MODIFY_CLUSTER + name;
        FileUtil.write(modicluster, rc.getOrigCluster());
        String modicount = DIRECTORY.MODIFY_COUNT + name;
        FileUtil.write(modicount, rc.getOrigCount());
        int insert = resultMapper.insert(rc.getResult());
        return insert;
    }

    public List<Result> queryResultsByIssueId(String issueId) {
        ResultExample example = new ResultExample();
        example.createCriteria().andIssueIdEqualTo(issueId);
        return resultMapper.selectByExample(example);
    }
}
