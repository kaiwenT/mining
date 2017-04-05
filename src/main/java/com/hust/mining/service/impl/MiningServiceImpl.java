package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.datamining.algorithm.cluster.Canopy;
import com.hust.datamining.convertor.Convertor;
import com.hust.datamining.convertor.DigitalConvertor;
import com.hust.datamining.simcal.AcrossSimilarity;
import com.hust.mining.constant.Config;
import com.hust.mining.constant.Constant;
import com.hust.mining.constant.Constant.Index;
import com.hust.mining.constant.Constant.KEY;
import com.hust.mining.dao.WebsiteDao;
import com.hust.mining.dao.WeightDao;
import com.hust.mining.model.Website;
import com.hust.mining.service.MiningService;
import com.hust.mining.service.SegmentService;
import com.hust.mining.util.CommonUtil;
import com.hust.mining.util.ConvertUtil;

@Service
public class MiningServiceImpl implements MiningService {

    @Autowired
    private SegmentService segmentService;
    @Autowired
    private WebsiteDao websiteDao;
    @Autowired
    private WeightDao weightDao;

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(MiningServiceImpl.class);

    @Override
    public List<List<Integer>> cluster(List<String[]> list) {
        // TODO 进行聚类
        List<String[]> segmentList = segmentService.getSegresult(list, Index.TITLE_INDEX, 0);
        Convertor convertor = new DigitalConvertor();
        convertor.setList(segmentList);
        List<double[]> vectors = convertor.getVector();
        Canopy canopy = new Canopy();
        canopy.setVectors(vectors);
        canopy.setSimi(new AcrossSimilarity(vectors));
        canopy.setThreshold(Config.SIMILARITYTHRESHOLD);
        try {
            canopy.clustering();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("error occur during clustering" + e.toString());
            return null;
        }
        List<List<Integer>> resultIndexSetList = canopy.getResultIndex();
        Collections.sort(resultIndexSetList, new Comparator<List<Integer>>() {

            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                // TODO Auto-generated method stub
                return o2.size() - o1.size();
            }
        });
        return resultIndexSetList;
    }

    @Override
    public List<int[]> count(List<String[]> content, List<String[]> cluster) {
        // TODO Auto-generated method stub
        List<int[]> clusterInt = ConvertUtil.toIntList(cluster);
        List<int[]> reList = new ArrayList<int[]>();
        for (int i = 0; i < clusterInt.size(); i++) {
            int[] tmpList = clusterInt.get(i);
            int origIndex = -1;
            String origTime = "9999-12-12 23:59:59";
            for (int j = 0; j < tmpList.length; j++) {
                String[] row = content.get(tmpList[j]);
                try{
                    if (origTime.compareTo(row[Index.TIME_INDEX]) > 0) {
                        origTime = row[Index.TIME_INDEX];
                        origIndex = tmpList[j];
                    }
                }catch(Exception e){
                    logger.error("sth error when count:{}",tmpList[j]);
                }
            }
            if (origIndex == -1) {
                origIndex = tmpList[0];
            }
            int[] item = new int[2];
            item[Index.COUNT_ITEM_INDEX] = origIndex;
            item[Index.COUNT_ITEM_AMOUNT] = tmpList.length;
            reList.add(item);
        }
        return reList;
    }

    @Override
    public Map<String, Integer> calAttention(Map<String, Integer> map) {
        // TODO Auto-generated method stub
        Map<String, Integer> attention = new HashMap<String, Integer>();
        if (null == map) {
            return attention;
        }
        for (Entry<String, Integer> entry : map.entrySet()) {
            int weight = weightDao.queryWeightByName(entry.getKey().toString());
            int atten = weight * entry.getValue();
            attention.put(entry.getKey(), atten);
        }
        return attention;
    }

    @Override
    public Map<String, Map<String, Map<String, Integer>>> statistic(List<String[]> content, String[] array,
            int interval) {
        // TODO Auto-generated method stub
        Map<String, Map<String, Map<String, Integer>>> map =
                new TreeMap<String, Map<String, Map<String, Integer>>>(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
        if (null == array) {
            array = new String[content.size()];
            for (int i = 0; i < content.size(); i++) {
                array[i] = i + "";
            }
        }
        int[] list = ConvertUtil.toIntArray(array);
        for (int item : list) {
            String[] row = content.get(item);
            if (CommonUtil.isEmptyArray(row)) {
                continue;
            }
            Website website = websiteDao.queryByUrl(CommonUtil.getPrefixUrl(row[Index.URL_INDEX]));
            String level = website.getLevel();
            String type = website.getType();
            String timeKey = CommonUtil.getTimeKey(row[Index.TIME_INDEX], interval);
            Map<String, Map<String, Integer>> timeMap = map.get(timeKey);
            if (timeMap == null) {
                timeMap = new HashMap<String, Map<String, Integer>>();
                Map<String, Integer> typeMap = new HashMap<String, Integer>();
                Map<String, Integer> levelMap = new HashMap<String, Integer>();
                typeMap.put(type, 1);
                levelMap.put(level, 1);
                timeMap.put(Constant.MEDIA_EN, levelMap);
                timeMap.put(Constant.INFOTYPE_EN, typeMap);
                map.put(timeKey, timeMap);
            } else {
                Map<String, Integer> typeMap = timeMap.get(Constant.INFOTYPE_EN);
                if (null == typeMap) {
                    typeMap = new HashMap<String, Integer>();
                    typeMap.put(type, 1);
                } else {
                    if (typeMap.get(type) == null) {
                        typeMap.put(type, 1);
                    } else {
                        typeMap.put(type, typeMap.get(type) + 1);
                    }
                }

                Map<String, Integer> levelMap = timeMap.get(Constant.MEDIA_EN);
                if (null == levelMap) {
                    levelMap = new HashMap<String, Integer>();
                    levelMap.put(level, 1);
                } else {
                    if (levelMap.get(level) == null) {
                        levelMap.put(level, 1);
                    } else {
                        levelMap.put(level, levelMap.get(level) + 1);
                    }
                }
                timeMap.put(Constant.MEDIA_EN, levelMap);
                timeMap.put(Constant.INFOTYPE_EN, typeMap);
                map.put(timeKey, timeMap);
            }
        }
        for (String time : map.keySet()) {
            Map<String, Map<String, Integer>> timeMap = map.get(time);
            Map<String, Integer> mediaAttention = calAttention(timeMap.get(Constant.MEDIA_EN));
            Map<String, Integer> netizenAttention = calAttention(timeMap.get(Constant.INFOTYPE_EN));
            timeMap.put(Constant.NETIZENATTENTION_EN, netizenAttention);
            timeMap.put(Constant.MEDIAATTENTION_EN, mediaAttention);
        }
        return map;
    }

    @Override
    public Map<String, Object> getAmount(Map<String, Map<String, Map<String, Integer>>> map) {
        // TODO Auto-generated method stub
        if (map == null) {
            return null;
        }
        Map<String, Integer> typeAmountMap = new HashMap<String, Integer>();
        for (Map<String, Map<String, Integer>> values : map.values()) {
            Map<String, Integer> typeMap = values.get(Constant.INFOTYPE_EN);
            for (Entry<String, Integer> entry : typeMap.entrySet()) {
                Integer oldValue = typeAmountMap.get(entry.getKey());
                if (null == oldValue) {
                    oldValue = 0;
                }
                typeAmountMap.put(entry.getKey(), entry.getValue() + oldValue);
            }
        }
        Map<String, Integer> mediaAmountMap = new HashMap<String, Integer>();
        for (Map<String, Map<String, Integer>> values : map.values()) {
            Map<String, Integer> mediaMap = values.get(Constant.MEDIA_EN);
            for (Entry<String, Integer> entry : mediaMap.entrySet()) {
                Integer oldValue = mediaAmountMap.get(entry.getKey());
                if (null == oldValue) {
                    oldValue = 0;
                }
                mediaAmountMap.put(entry.getKey(), entry.getValue() + oldValue);
            }
        }
        Map<String, Object> reMap = new HashMap<>();
        reMap.put(KEY.MINING_AMOUNT_MEDIA, mediaAmountMap);
        reMap.put(KEY.MINING_AMOUNT_TYPE, typeAmountMap);
        return reMap;
    }

}
