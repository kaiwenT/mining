package com.hust.mining.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hust.mining.dao.mapper.SourceTypeMapper;
import com.hust.mining.model.SourceType;
import com.hust.mining.model.SourceTypeExample;
import com.hust.mining.model.SourceTypeExample.Criteria;

public class SourceTypeDao {
	@Autowired
	private SourceTypeMapper sourceTypeMapper;

	public List<SourceType> selectSourceType() {
		SourceTypeExample example = new SourceTypeExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIsNotNull();
		List<SourceType> sourceType = sourceTypeMapper.selectByExample(example);
		return sourceType;
	}

	public List<SourceType> selectSourceTypeByName(String name) {
		SourceTypeExample example = new SourceTypeExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameLike(name);
		List<SourceType> sourceType = sourceTypeMapper.selectByExample(example);
		return sourceType;
	}

	public int deleteSourceTypeById(int id) {
		return sourceTypeMapper.deleteByPrimaryKey(id);
	}

	public int insertSourceType(String name) {
		SourceType sourceType = new SourceType();
		sourceType.setName(name);
		return sourceTypeMapper.insert(sourceType);
	}

	public int updateSourceType(SourceType sourceType) {
		return sourceTypeMapper.updateByPrimaryKey(sourceType);
	}
}
