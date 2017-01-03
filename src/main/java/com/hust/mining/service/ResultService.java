package com.hust.mining.service;

import java.util.List;
import java.util.Map;

import com.hust.mining.model.Result;
import com.hust.mining.model.params.StatisticParams;

public interface ResultService {

    public String getCurrentResultId();

    public List<String[]> getCountResultById(String resultId, String issueId);

    public boolean deleteSets(int[] sets);

    public boolean combineSets(int[] sets);

    public boolean reset();

    Map<String, Object> statistic(StatisticParams params);

    public int delResultById(String resultId);

    public List<Result> queryResultsByIssueId(String issueId);

    public Map<String, List<String[]>> exportService(String issueId, String resultId);
}
