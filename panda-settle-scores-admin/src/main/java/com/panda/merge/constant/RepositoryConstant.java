package com.panda.merge.constant;

import com.panda.merge.config.RedisConfig;

/**
 * 资源仓库常亮 ： redis key
 */
public class RepositoryConstant {


    /**
     * 联赛模板表redisKey
     */
    public final static String TEMPLATE_RELATION = "TEMPLATE_RELATION_";
    /**
     * 模板ID
     */
    public final  static  String TEMPLATE_ID="TEMPLATE_ID";

    /**
     * 缓存3小时
     */
    public static final Integer REDIS_THREE_TIME = 60 * 60 * 3;

    /**
     * 缓存12小时
     */
    public static final Integer REDIS_TWELVE_TIME = 60 * 60 * 12;

    public  static  String MATCH_SETTLE_GOAL_STATUS = "MATCH_SETTLE_GOAL_STATUS:";

    public  static  String STANDARD_MATCH_INFO = RedisConfig.REDIS_KEY_DATABASE + "::StandardMatchInfo:";

    public  static  String MATCH_SETTLE_INFO = "SETTLE_MATCH_SETTLE_INFO:";

    public  static  String MATCH_SETTLE_FACTOR_CHECK_INFO= "MATCH_SETTLE_FACTOR_CHECK_INFO:";

    public  static  String MATCH_SETTLE_ROLL_BACK_INFO= "MATCH_SETTLE_ROLL_BACK_INFO:";

    public  static  String MATCH_SETTLE_DATA_SOURCE_CONFIG= "MATCH_SETTLE_DATA_SOURCE_CONFIG:";


    public  static  String MATCH_EVENT_INFO= "MATCH_EVENT_INFO:";




}
