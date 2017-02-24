package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.dao.WeightDao;
import com.hust.mining.model.Weight;
import com.hust.mining.service.WeightService;

@Service
public class WeightServiceImple implements WeightService {

	private static final Logger logger = LoggerFactory.getLogger(WeightServiceImple.class);

	@Autowired
	private WeightDao weightDao;

	@Override
	public List<Weight> selectAllWeight() {
		List<Weight> weight = weightDao.selectAllWeight();
		if (weight.isEmpty()) {
			logger.info("weight is empty");
			return weight;
		}
		return weight;
	}

	@Override
	public boolean insertWeight(Weight weight) {
		int status = weightDao.insertWeight(weight);
		if (status == 0) {
			logger.info("insert is error");
			return false;
		}
		return true;
	}

	/**
	 * 不能更新成已有的新闻类型 出现返回错误情况
	 */
	@Override
	public boolean updateWeight(Weight weight) {
		List<Weight> weightInfo = weightDao.selectWeightById(weight.getId());
		List<Weight> weights = weightDao.selectWeigth(weightInfo.get(0).getName());
		List<String> weightName = new ArrayList<String>();
		for (Weight weightInfos : weights) {
			weightName.add(weightInfos.getName());
		}
		if (weightName.contains(weight.getName())) {
			logger.info("update weightName is exist");
			return false;
		}
		int status = weightDao.updateWeight(weight);
		if (status == 0) {
			logger.info("update weight is error");
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteWeightById(int id) {
		int status = weightDao.deleteWeightById(id);
		if (status == 0) {
			logger.info("delete is error");
			return false;
		}
		return true;
	}

	@Override
	public List<Weight> selectByCondition(Weight weight) {
		List<Weight> weights = weightDao.selectByCondition(weight);
		if (weights.isEmpty()) {
			logger.info("weights is empty");
			return weights;
		}
		return weights;
	}

}
