package com.hust.mining.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hust.mining.dao.mapper.WeightMapper;
import com.hust.mining.model.Weight;
import com.hust.mining.model.WeightExample;

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
}
