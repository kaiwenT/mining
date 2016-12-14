package com.hust.mining.dao.mapper;

import com.hust.mining.model.Issue;
import com.hust.mining.model.IssueExample;
import com.hust.mining.model.IssueKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IssueMapper {
    long countByExample(IssueExample example);

    int deleteByExample(IssueExample example);

    int deleteByPrimaryKey(IssueKey key);

    int insert(Issue record);

    int insertSelective(Issue record);

    List<Issue> selectByExample(IssueExample example);

    Issue selectByPrimaryKey(IssueKey key);

    int updateByExampleSelective(@Param("record") Issue record, @Param("example") IssueExample example);

    int updateByExample(@Param("record") Issue record, @Param("example") IssueExample example);

    int updateByPrimaryKeySelective(Issue record);

    int updateByPrimaryKey(Issue record);
}