package com.panda.merge.constant;

/**
 * redisKey的常量类
 */
public class RedisKeyConstant {

    private static final String PROJECT_NAME = "panda";

    private static final String SYSTEM_NAME = "websocket";

    private static final String SCORE = "score";

    private static final String SCORE_EVENT = "scoreEvent";

    private static final String SCORE_THIRD_MATCH_IDS = "scoreThirdMatchIds";

    private static final String SCORE_EVENT_THIRD_MATCH_IDS = "scoreEventThirdMatchIds";

    public static final String  WS_CHANNEL_HEART_INDEX="WS_CHANNEL_HEART_INDEX:";

    public static final String  CAO_PAN_ONLINE="CAO_PAN_ONLINE:";

    /**
     * 获取缓存到redis中的channelKey
     *
     * @param subscriptionType
     * @param channelId
     * @return
     */
    public static String getChannelKey(String subscriptionType, String channelId) {
        return PROJECT_NAME + ":" + SYSTEM_NAME + ":" + SCORE + ":" + subscriptionType + ":" + channelId;
    }


    /**
     * 获取存储比分的三方赛事id的key
     *
     * @return
     */
    public static String getSocreThirdMatchKey() {
        return PROJECT_NAME + ":" + SYSTEM_NAME + ":" + SCORE_THIRD_MATCH_IDS;
    }

    /**
     * 获取存储比分事件的三方赛事id的key
     *
     * @return
     */
    public static String getSocreEventThirdMatchKey() {
        return PROJECT_NAME + ":" + SYSTEM_NAME + ":" + SCORE_EVENT_THIRD_MATCH_IDS;
    }


    /**
     * 获取比分数据的redis下的模糊key值
     *
     * @return
     */
    public static String getLikeScorceKey() {
        return PROJECT_NAME + ":" + SYSTEM_NAME + ":" + SCORE + "*";
    }

    /**
     * 获取事件比分的redis下的模糊key值
     */
    public static String getLikeScorceEventKey() {
        return PROJECT_NAME + ":" + SYSTEM_NAME + ":" + SCORE_EVENT + "*";
    }
}
