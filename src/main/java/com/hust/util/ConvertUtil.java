package com.hust.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConvertUtil {
    public static List<String[]> convertoStrList(List<List<String[]>> setList, String[] title) {
        if (null == setList)
            return null;
        List<String[]> listStr = new ArrayList<String[]>();
        listStr.add(title);
        for (List<String[]> set : setList) {
            if (null != set && set.size() != 0) {
                for (String[] row : set) {
                    listStr.add(row);
                }
                /**
                 * 新加一行空行，以区分类间的区别
                 */
                listStr.add(new String[set.get(0).length]);
            }
        }
        return listStr;
    }

    public static List<List<String[]>> convertToStringSet(List<String[]> list, List<List<Integer>> resultIndexSet,
            final int targetIndex) {
        if (null == resultIndexSet || resultIndexSet.size() == 0 || list.size() == 0 || null == list
                || targetIndex < 0) {
            return null;
        }
        Collections.sort(resultIndexSet, new Comparator<List<Integer>>() {

            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                // TODO Auto-generated method stub
                return o2.size() - o1.size();
            }
        });
        List<List<String[]>> listStrSet = new ArrayList<List<String[]>>();
        List<String[]> singleDataList = new ArrayList<String[]>();
        for (List<Integer> set : resultIndexSet) {
            if (set.size() == 1) {
                singleDataList.add(list.get(set.get(0) + 1));
                continue;
            }
            List<String[]> setDataList = new ArrayList<String[]>();
            for (int i : set) {
                setDataList.add(list.get(i + 1));
            }
            Collections.sort(setDataList, new Comparator<String[]>() {
                public int compare(String[] o1, String[] o2) {
                    return o1[targetIndex].compareTo(o2[targetIndex]);
                }
            });
            listStrSet.add(setDataList);
        }
        Collections.sort(singleDataList, new Comparator<String[]>() {
            public int compare(String[] o1, String[] o2) {
                return o1[targetIndex].compareTo(o2[targetIndex]);
            }
        });
        listStrSet.add(singleDataList);
        return listStrSet;
    }

    public static byte[] convertToBytes(Object obj) throws Exception {
        if (null == obj) {
            return null;
        }
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutput output = new ObjectOutputStream(byteStream);
        output.writeObject(obj);
        return byteStream.toByteArray();
    }

    public static Object convertBytesToObject(byte[] bytes) throws Exception {
        if (null == bytes || bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInput input = new ObjectInputStream(byteArrayInputStream);
        return input.readObject();
    }

}
