package com.hust.mining.service;

import java.util.List;

public interface ClusterService {
    List<List<String[]>> getClusterResult(List<String[]> list, int targetIndex);

    List<List<Integer>> cluster(List<String[]> list);
}
