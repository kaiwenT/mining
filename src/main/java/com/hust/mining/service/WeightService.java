package com.hust.mining.service;

import java.util.List;

import com.hust.mining.model.Weight;

public interface WeightService {

	List<Weight> selectAllWeight();

	List<Weight> selectByCondition(Weight weight);

	boolean insertWeight(Weight weight);

	boolean updateWeight(Weight weight);

	boolean deleteWeightById(int id);
}
