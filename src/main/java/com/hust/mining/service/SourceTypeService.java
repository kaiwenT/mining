package com.hust.mining.service;

import java.util.List;

import com.hust.mining.model.SourceType;

public interface SourceTypeService {

	List<SourceType> selectSourceType();

	List<SourceType> selectSourceTypeByName(String name);

	int deleteSourceTypeById(int id);

	int insertSourceType(String name);

	int updateSourceType(SourceType sourceType);

}
