package com.hust.mining.service;

import java.util.List;
import java.util.Map;

public interface StatisticService {
    /**
     * 根据聚类结果对一个类中的信息进行统计 结果形式如下： "2016-09-18": { "emotion": { "positive": 32, "negative": 32, "neutral": 40 },
     * "intoType": { "weizhi": 90, "xinwen": 32, "baozhi": 90, "luntan": 89, "wenda": 30, "boke": 20, "weixin": 20,
     * "tieba": 10, "shouji": 30, "shiping": 39, "weibo": 30 }, "media": { "zhongyang": 80, "shengji": 20, "qita": 40,
     * "weizhi": 40 }, "netizenAttention": { "weizhi": 90, "xinwen": 32, "baozhi": 90, "luntan": 89, "wenda": 30,
     * "boke": 20, "weixin": 20, "tieba": 10, "shouji": 30, "shiping": 39, "weibo": 30 }, "mediaAttention": {
     * "zhongyang": 80, "shengji": 20, "qita": 40, "weizhi": 40 } }
     * 
     * @param list
     * @param interval
     * @return
     */
    public Map<String, Map<String, Map<String, Integer>>> processAll(List<String[]> list, int interval);

    public Map<String, Integer> getEmotionTendencyCount(List<String> list);

    public Map<String, Integer> getTypeCount(Map<String, Map<String, Map<String, Integer>>> map);

    public Map<String, Integer> getLevelCount(Map<String, Map<String, Map<String, Integer>>> map);

    public Map<String, Integer> calculateAttention(Map<String, Integer> map);

    public List<int[]> count(List<List<Integer>> clusterResult, List<String[]> content);

    public List<int[]> countx(List<String[]> cluster, List<String[]> content);

    public Map<String, Map<String, Map<String, Integer>>> statistic(List<String[]> content, int[] list,
            int interval);

}
