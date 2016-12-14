package com.hust.mining.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.dao.SourceTypeDao;
import com.hust.mining.model.SourceType;
import com.hust.mining.service.SourceTypeService;

@Service
public class SourceTypeServiceImple implements SourceTypeService {
	private static final Logger logger = LoggerFactory.getLogger(SourceTypeServiceImple.class);
	@Autowired
	private SourceTypeDao sourceTypeDao;

	@Override
	public List<SourceType> selectSourceType() {
		List<SourceType> sourceType = sourceTypeDao.selectSourceType();
		if (sourceType.isEmpty()) {
			logger.info("sourceTYpe is empty");
			return null;
		}
		return sourceType;
	}

	@Override
	public List<SourceType> selectSourceTypeByName(String name) {
		List<SourceType> sourceType = sourceTypeDao.selectSourceTypeByName(name);
		if (sourceType.isEmpty()) {
			logger.info("The name is not exist");
			return null;
		}
		return sourceType;
	}

	@Override
	public int deleteSourceTypeById(int id) {
		int status = sourceTypeDao.deleteSourceTypeById(id);
		if (status == 0) {
			logger.info("delete sourceType is error");
			return status;
		}
		return status;
	}

	@Override
	public int insertSourceType(String name) {
		int status = sourceTypeDao.insertSourceType(name);
		if (status == 0) {
			logger.info("insert sourceType is error");
			return status;
		}
		return status;
	}
}
