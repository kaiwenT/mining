package com.hust.mining.service;

import java.util.List;

import com.hust.mining.model.Power;


public interface PowerService {

	List<Power> selectAllPower();

	List<Power> selectOnePowerInfo(String powerName);

	boolean insertPowerInfo(Power power);

	boolean deletePowerById(int powerId);

	boolean updatePowerInfo(Power power);
}
