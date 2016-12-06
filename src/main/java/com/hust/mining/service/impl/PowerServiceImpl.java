package com.hust.mining.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.mining.dao.PowerDao;
import com.hust.mining.dao.RolePowerDao;
import com.hust.mining.model.Power;
import com.hust.mining.service.PowerService;

@Service
public class PowerServiceImpl implements PowerService {

	private static final Logger logger = LoggerFactory.getLogger(PowerServiceImpl.class);

	@Autowired
	private PowerDao powerDao;

	@Autowired
	private RolePowerDao rolePowerDao;

	@Override
	public List<Power> selectAllPower() {
		List<Power> powers = powerDao.selectAllPowers();
		if (powers.isEmpty()) {
			logger.info("select powers is empty");
		}
		return powers;
	}

	
	@Override
	public List<Power> selectOnePowerInfo(String powerName) {
		List<Power> powers = powerDao.selectByLikePowerName(powerName);
		if (powers.isEmpty()) {
			logger.info("powerName is not exist");
		}
		return powers;
	}

	@Override
	public boolean insertPowerInfo(Power power) {
		// 不能添加重复的判断
		List<Power> powers = powerDao.selectPowerByPowerName(power.getPowerName());
		if (!powers.isEmpty()) {
			logger.info("power table has this power");
			return false;
		}
		// 判断插入状态
		int status = powerDao.insertSelective(power);
		if (status == 0) {
			logger.info("insert table is error");
			return false;
		}
		return true;
	}

	@Override
	public boolean deletePowerById(int powerId) {
		// 判断删除是否成功
		int roleStatue = rolePowerDao.deleteRolePowerByPowerId(powerId);
		if (roleStatue == 0) {
			logger.info("role table has this power");
		}
		int powerStatue = powerDao.deleteByPrimaryKey(powerId);
		if (powerStatue == 0) {
			logger.info("power table has this power");
			return false;
		}
		return true;
	}

	@Override
	public boolean updatePowerInfo(Power power) {
		// 也不能更新成已经存在的权限名称
		List<Power> powers = powerDao.selectPowerByPowerName(power.getPowerName());
		if (null != powers) {
			logger.info("powername has been exist ");
			return false;
		}
		int statue = powerDao.updateByPrimaryKeySelective(power);
		if (statue == 0) {
			logger.info("updatepowerInfo have error ,unkonw error");
			return false;
		}
		return true;
	}
}