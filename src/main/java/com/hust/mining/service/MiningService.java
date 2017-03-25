package com.hust.mining.service;

import java.util.List;
import java.util.Map;

public interface MiningService {
    public List<List<Integer>> cluster(List<String[]> content);

    public List<int[]> count(List<String[]> content, List<String[]> cluster);

    public Map<String, Map<String, Map<String, Integer>>> statistic(List<String[]> content, String[] array,
            int interval);
    
    public Map<String, Object> getAmount(Map<String, Map<String, Map<String, Integer>>> map);

    public Map<String, Integer> calAttention(Map<String, Integer> map);
}
