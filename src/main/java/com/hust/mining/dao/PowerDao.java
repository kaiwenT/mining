package com.hust.mining.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.hust.mining.dao.mapper.PowerMapper;
import com.hust.mining.model.Power;
import com.hust.mining.model.PowerExample;
import com.hust.mining.model.PowerExample.Criteria;
import com.hust.mining.model.params.PowerQueryCondition;

public class PowerDao {

	@Autowired
	private PowerMapper powerMapper;

	public List<Power> selectPowerById(int powerId) {
		PowerExample example = new PowerExample();
		Criteria criteria = example.createCriteria();
		criteria.andPowerIdEqualTo(powerId);
		List<Power> powers = powerMapper.selectByExample(example);
		return powers;
	}

	public List<Power> selectPowerByPowerId(List<Integer> powerIds) {
		PowerExample example = new PowerExample();
		// 查询不出来用户的所有权限
		for (int powerId : powerIds) {
			Criteria criterias = example.createCriteria();
			criterias.andPowerIdEqualTo(powerId);
			example.or(criterias);
		}
		List<Power> powers = powerMapper.selectByExample(example);
		return powers;
	}

	public List<Power> selectPowerByPowerName(String powerName) {
		PowerExample example = new PowerExample();
		Criteria criteria = example.createCriteria();
		criteria.andPowerNameEqualTo(powerName);
		List<Power> powers = powerMapper.selectByExample(example);
		return powers;
	}

	public List<Power> selectByLikePowerName(PowerQueryCondition power) {
		PowerExample example = new PowerExample();
		Criteria criteria = example.createCriteria();
		if (!StringUtils.isBlank(power.getName())) {
			criteria.andPowerNameLike("%" + power.getName() + "%");
		}
		if (power.getStart() != 0) {
			example.setStart(power.getStart());
		}
		if (power.getLimit() != 0) {
			example.setLimit(power.getLimit());
		}
		List<Power> powers = powerMapper.selectByExample(example);
		return powers;

	}

	public List<Power> selectAllPowers() {
		PowerExample example = new PowerExample();
		Criteria criteria = example.createCriteria();
		criteria.andPowerIdIsNotNull();
		List<Power> powers = powerMapper.selectByExample(example);
		return powers;
	}

	public long countByExample(PowerExample example) {
		return powerMapper.countByExample(example);
	}

	public int deleteByExample(PowerExample example) {
		return powerMapper.deleteByExample(example);
	}

	public int deleteByPrimaryKey(Integer powerId) {
		return powerMapper.deleteByPrimaryKey(powerId);
	}

	public int insert(Power record) {
		return powerMapper.insert(record);
	}

	public int insertSelective(Power record) {
		return powerMapper.insertSelective(record);
	}

	public List<Power> selectByExample(PowerExample example) {
		return powerMapper.selectByExample(example);
	}

	public Power selectByPrimaryKey(Integer powerId) {
		return powerMapper.selectByPrimaryKey(powerId);
	}

	public int updateByExampleSelective(Power record, PowerExample example) {
		return powerMapper.updateByExampleSelective(record, example);
	}

	public int updateByExample(Power record, PowerExample example) {
		return powerMapper.updateByExample(record, example);
	}

	public int updateByPrimaryKeySelective(Power record) {
		return powerMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(Power record) {
		return powerMapper.updateByPrimaryKey(record);
	}

}
