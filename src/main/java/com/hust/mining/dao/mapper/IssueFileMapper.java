package com.hust.mining.dao.mapper;

import com.hust.mining.model.IssueFile;
import com.hust.mining.model.IssueFileExample;
import com.hust.mining.model.IssueFileKey;
import com.hust.mining.model.IssueFileWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IssueFileMapper {
    long countByExample(IssueFileExample example);

    int deleteByExample(IssueFileExample example);

    int deleteByPrimaryKey(IssueFileKey key);

    int insert(IssueFileWithBLOBs record);

    int insertSelective(IssueFileWithBLOBs record);

    List<IssueFileWithBLOBs> selectByExampleWithBLOBs(IssueFileExample example);

    List<IssueFile> selectByExample(IssueFileExample example);

    IssueFileWithBLOBs selectByPrimaryKey(IssueFileKey key);

    int updateByExampleSelective(@Param("record") IssueFileWithBLOBs record, @Param("example") IssueFileExample example);

    int updateByExampleWithBLOBs(@Param("record") IssueFileWithBLOBs record, @Param("example") IssueFileExample example);

    int updateByExample(@Param("record") IssueFile record, @Param("example") IssueFileExample example);

    int updateByPrimaryKeySelective(IssueFileWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(IssueFileWithBLOBs record);

    int updateByPrimaryKey(IssueFile record);
}