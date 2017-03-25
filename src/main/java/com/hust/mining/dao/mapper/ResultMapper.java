package com.hust.mining.dao.mapper;

import com.hust.mining.model.Result;
import com.hust.mining.model.ResultExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ResultMapper {
    long countByExample(ResultExample example);

    int deleteByExample(ResultExample example);

    int deleteByPrimaryKey(String rid);

    int insert(Result record);

    int insertSelective(Result record);

    List<Result> selectByExample(ResultExample example);

    Result selectByPrimaryKey(String rid);

    int updateByExampleSelective(@Param("record") Result record, @Param("example") ResultExample example);

    int updateByExample(@Param("record") Result record, @Param("example") ResultExample example);

    int updateByPrimaryKeySelective(Result record);

    int updateByPrimaryKey(Result record);
}