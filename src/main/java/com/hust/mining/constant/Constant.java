package com.hust.mining.constant;

import org.springframework.beans.factory.annotation.Value;

public class Constant {

    public final static String INVALID_TIME = "1970-01-01";
    public final static String EMOTION_EN = "emotion";
    public final static String EMOTION_CH = "情感";
    public final static String INFOTYPE_EN = "infoType";
    public final static String INFOTYPE_CH = "类型";
    public final static String MEDIA_EN = "media";
    public final static String MEDIA_CH = "媒体";
    public final static String NETIZENATTENTION_EN = "netizenAttention";
    public final static String NETIZENATTENTION_CH = "网民关注度";
    public final static String MEDIAATTENTION_EN = "mediaAttention";
    public final static String MEDIAATTENTION_CH = "媒体关注度";
    public final static String MEDIA_COUNT_EN = "mediaCount";
    public final static String INFOTYPE_COUNT_EN = "infoTyeCount";
    public final static String EMOTION_COUNT_EN = "emotionCount";
    public final static String COUNT_EN = "count";
    public final static String TIMELINE_EN = "timeline";
    public final static String TITLE_EN = "title";
    public final static String CATEGORIES_EN = "categories";
    public final static String SERIES_EN = "series";
    public final static String NAME_EN = "name";
    public final static String DATA_EN = "data";
    public final static String WEIZHI_CH = "未知";
    public final static String SHULIANG_CH = "数量";
    public final static String CLUSTER_RESULT_10ROW_EN = "clusterResult10row";
    public final static String ORIG_COUNT_10ROW_EN = "origAndCount10row";
    public final static String TYPE_ORIG = "orig";
    public final static String TYPE_MODIFIED = "modified";

    public static class KEY {
        public final static String ISSUE_ID = "issueId";
        public final static String RESULT_ID = "resultId";
        public static final String USER_NAME = "username";
    }

    public static class Index {
        public static final int URL_INDEX = 0;
        public static final int TITLE_INDEX = 1;
        public static final int TIME_INDEX = 2;
        public static final int CLICK_INDEX = 3;
        public static final int REPLY_INDEX = 4;
        public static final int COUNT_ITEM_INDEX = 0;
        public static final int COUNT_ITEM_AMOUNT = 1;
    }

    // 情感
    public static class Emotion {

        public static final String POSITIVE = "正面";
        public static final String NEGATIVE = "负面";
        public static final String NEUTRAL = "中性";
        public static final String WEIZHI = "未知";
    }

    // 统计时间间隔
    public static class Interval {
        public static final int HOUR = 1;
        public static final int DAY = 2;
        public static final int MONTH = 3;
    }

    // 媒体级别
    public static class MEDIALEVEL {
        public static final String ZHONGYANG = "中央";
        public static final String SHENGJI = "省级";
        public static final String QITA = "其他新闻网站";
        public static final String WEIZHI = "未知";
    }

    public static class INFOTYPE {
        public static final String XINWEN = "新闻";
        public static final String BAOZHI = "报纸";
        public static final String LUNTAN = "论坛";
        public static final String WENDA = "问答";
        public static final String BOKE = "博客";
        public static final String WEIXIN = "微信";
        public static final String TIEBA = "贴吧";
        public static final String SHOUJI = "手机";
        public static final String SHIPING = "视频";
        public static final String WEIBO = "微博";
        public static final String WEIZHI = "未知";
    }

    public static class DIRECTORY {
        @Value("upload_file")
        public static String UPLOAD;
        @Value("orig_cluster")
        public static String ORIG_CLUSTER;
        @Value("orig_count")
        public static String ORIG_COUNT;
        @Value("modify_count")
        public static String MODIFY_COUNT;
    }

}
