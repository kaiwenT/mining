package com.hust.mining.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hust.mining.model.params.StatisticParams;

public interface ResultService {

    public String getCurrentResultId(HttpServletRequest request);

    public List<String[]> getCountResultById(String resultId);

    public List<String[]> getItemsInSets(int set, HttpServletRequest request);

    public boolean deleteSets(int[] sets, HttpServletRequest request);

    public boolean combineSets(int[] sets, HttpServletRequest request);

    public boolean reset(HttpServletRequest request);

    Map<String, Object> statistic(StatisticParams params, HttpServletRequest request);

    public int delResultById(String resultId);
}
