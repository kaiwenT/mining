package com.hust.mining.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hust.mining.dao.mapper.WeightMapper;
import com.hust.mining.model.Weight;
import com.hust.mining.model.WeightExample;
import com.hust.mining.model.WeightExample.Criteria;

public class WeightDao {

	@Autowired
	private WeightMapper weightMapper;

	public int queryWeightByName(String name) {
		WeightExample example = new WeightExample();
		example.createCriteria().andNameEqualTo(name);
		List<Weight> list = weightMapper.selectByExample(example);
		if (null == list || list.size() == 0) {
			return 0;
		}
		return list.get(0).getWeight();
	}

	public List<Weight> selectAllWeight() {
		WeightExample example = new WeightExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdIsNotNull();
		List<Weight> weight = weightMapper.selectByExample(example);
		return weight;
	}

	public int insertWeight(Weight weight) {
		return weightMapper.insert(weight);
	}

	public int deleteWeightById(int id) {
		WeightExample example = new WeightExample();
		Criteria criteria = example.createCriteria();
		criteria.andIdEqualTo(id);
		int status = weightMapper.deleteByExample(example);
		return status;
	}

	public int updateWeight(Weight weight) {
		return weightMapper.updateByPrimaryKeySelective(weight);
	}

}
