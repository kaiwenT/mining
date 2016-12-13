package com.hust.mining.dao.mapper;

import com.hust.mining.model.Result;
import com.hust.mining.model.ResultExample;
import com.hust.mining.model.ResultKey;
import com.hust.mining.model.ResultWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ResultMapper {
    long countByExample(ResultExample example);

    int deleteByExample(ResultExample example);

    int deleteByPrimaryKey(ResultKey key);

    int insert(ResultWithBLOBs record);

    int insertSelective(ResultWithBLOBs record);

    List<ResultWithBLOBs> selectByExampleWithBLOBs(ResultExample example);

    List<Result> selectByExample(ResultExample example);

    ResultWithBLOBs selectByPrimaryKey(ResultKey key);

    int updateByExampleSelective(@Param("record") ResultWithBLOBs record, @Param("example") ResultExample example);

    int updateByExampleWithBLOBs(@Param("record") ResultWithBLOBs record, @Param("example") ResultExample example);

    int updateByExample(@Param("record") Result record, @Param("example") ResultExample example);

    int updateByPrimaryKeySelective(ResultWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ResultWithBLOBs record);

    int updateByPrimaryKey(Result record);
}