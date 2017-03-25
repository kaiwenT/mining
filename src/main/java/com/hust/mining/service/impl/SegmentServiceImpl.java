package com.hust.mining.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Result;
import org.ansj.recognition.impl.FilterRecognition;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.springframework.stereotype.Service;

import com.hust.mining.service.SegmentService;

@Service
public class SegmentServiceImpl implements SegmentService {

    private static FilterRecognition filter = new FilterRecognition();
    static {
        filter.insertStopNatures("w", "u", "mq", "p", "e", "y", "o");
    }

    @Override
    public String[] getSegresult(String str) {
        Result res;
        try {
            res = NlpAnalysis.parse(str).recognition(filter);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString() + "\t" + str);
            return new String[] { "失败" };
        }
        String[] words = new String[res.size()];
        for (int i = 0; i < res.size(); i++) {
            words[i] = res.get(i).getName();
        }
        return words;
    }

    @Override
    public List<String[]> getSegresult(List<String> list, int start) {
        if (null == list) {
            throw new IllegalArgumentException();
        }
        if (start + 1 > list.size()) {
            throw new IllegalArgumentException();
        }
        List<String[]> result = new ArrayList<String[]>();
        for (int i = start; i < list.size(); i++) {
            String[] array = getSegresult(list.get(i));
            result.add(array);
        }
        return result;
    }

    @Override
    public List<List<Object>> getResult(List<String[]> list, int index, int start) {
        if (null == list) {
            throw new NullPointerException();
        }
        if (index < 0 || start + 1 > list.size()) {
            throw new IllegalArgumentException();
        }
        List<List<Object>> relist = new ArrayList<List<Object>>();
        for (int i = start; i < list.size(); i++) {
            String[] array = list.get(i);
            List<Object> templist = new ArrayList<Object>();
            for (int j = 0; j < array.length; j++) {
                if (j == index) {
                    String[] temparray = getSegresult(array[j]);
                    templist.add(temparray);
                } else {
                    templist.add(array[j]);
                }
            }
            relist.add(templist);
        }
        return relist;
    }

    @Override
    public List<String[]> getSegresult(List<String[]> list, int index, int start) {
        if (null == list) {
            throw new NullPointerException();
        }
        if (index < 0 || start + 1 > list.size()) {
            throw new IllegalArgumentException();
        }
        List<String[]> relist = new ArrayList<String[]>();
        for (int i = start; i < list.size(); i++) {
            String[] array = list.get(i);
            String[] temparray = getSegresult(array[index]);
            relist.add(temparray);
        }
        return relist;
    }

    @Override
    public List<String[]> getSegresult(String[] array) {
        if (null == array) {
            throw new NullPointerException();
        }
        List<String[]> list = new ArrayList<String[]>();
        if (array.length == 0) {
            return list;
        }
        for (String str : array) {
            String[] temparray = getSegresult(str);
            list.add(temparray);
        }
        return list;
    }

}
