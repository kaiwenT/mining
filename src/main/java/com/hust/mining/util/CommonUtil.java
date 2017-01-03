package com.hust.mining.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.StringUtils;

public class CommonUtil {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static boolean isEmptyArray(String[] array) {
        if (null == array || array.length == 0) {
            return true;
        }
        for (int i = 0; i < array.length; i++) {
            if (StringUtils.isBlank(array[i])) {
                return true;
            }
        }
        return false;
    }

    public static String getPrefixUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return StringUtils.EMPTY;
        }
        try {
            String prefix = url.substring(0, url.indexOf("/", url.indexOf("://") + 3));
            return prefix;
        } catch (Exception e) {
            logger.error("get prefix of url failed, url :{}, exception:{}", url, e.toString());
            return StringUtils.EMPTY;
        }
    }
}
