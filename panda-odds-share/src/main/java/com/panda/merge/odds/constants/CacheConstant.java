package com.panda.merge.odds.constants;


/**
 * CacheConstant
 *
 * @description: 缓存常量
 * @date: 1/20/2025
 **/
public class CacheConstant {

    // 赔率计算玩法分组缓存更新topic
    public static final String ODDS_CALCULATION_CATEGORY_GROUP_UPDATE = "ODDS_CALCULATION_CATEGORY_GROUP_UPDATE";

    public static final String ODDS_CALC_VERSION_CACHE = "odds_calc_version";

    public static final String ODDS_CALC_VERSION_SWITCH_TOPIC = "RCS_MERCHANT_TEMP_MAINSWITCH";

    // 本地缓存更新topic
    public static final String TOPIC_LOCAL_CACHE_UPDATE = "ODDS_INTERNAL_LOCAL_CACHE_UPDATE";

    public static final String TOPIC_NONREALTIME_CACHE_UPDATE = "NONREALTIME_LOCAL_CACHE_UPDATE";

    public static final int EXPIRE_ONE_DAY = 60 * 60 * 24;

    public static final int EXPIRE_THREE_DAY = 60 * 60 * 24 * 3;

}
