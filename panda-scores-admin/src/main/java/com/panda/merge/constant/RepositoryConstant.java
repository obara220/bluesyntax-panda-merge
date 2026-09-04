package com.panda.merge.constant;

/**
 * 资源仓库常亮 ： redis key
 */
public class RepositoryConstant {

    /**
     * 缓存1分钟
     */
    public static final Integer REDIS_ONE_MINUS = 60;

    /**
     * 缓存3小时
     */
    public static final Integer REDIS_THREE_TIME = 60 * 60 * 3;
    /**
     * 三方比分
     */
    public static String MATCH_SCORES_INFO="REPOSITORY:MATCH_SCORES_INFO:";
    /**
     * 三方时间
     */
    public static String MATCH_TIME_INFO="REPOSITORY:MATCH_TIME_INFO:";

    /**
     * 三方赛事信息
     */
    public final static String THIRD_MATCH_INFO = "REPOSITORY:THIRD_MATCH_INFO:";
    /**
     *
     *
     * */
    public final static String BUSINESS_EVENT_MATCH_ID = "REPOSITORY:BUSINESS_EVENT_MATCH_ID:";
    /**
     * 三方赛事信息
     */
    public final static String STANDARD_MATCH_INFO = "REPOSITORY:STANDARD_MATCH_INFO:";

    public final static String AO_MATCH_ID ="REPOSITORY:AO_MATCH_ID:";

    public static String SCORES_SOURCE_TYPE="REPOSITORY:SCORES_SOURCE_TYPE:";

    /**
     * 盘口开售 用于开售盘口的滚球，赛前盘口
     */
    public final static String STANDARD_SPORT_MARKET_SELL = "REPOSITORY:STANDARD_SPORT_MARKET_SELL:";

    /**
     * 比分切换关联表
     */
    public final static String MATCH_SCORES_SOURCE_TYPE = "REPOSITORY:MATCH_SCORES_SOURCE_TYPE:";

    /**
     * 报球板热键（所有球种通用，key 后缀为 sportId）
     */
    public final static String FOOTBALL_KEYBOARD_SET = "PANDA-MERGE-REPOSITORY:FOOTBALL_KEYBOARD_SET:";

    /**
     * 报球板热键（语义化别名，与 FOOTBALL_KEYBOARD_SET 指向同一 Redis key，用于新增球种代码引用）
     */
    public final static String USER_KEYBOARD_SET = FOOTBALL_KEYBOARD_SET;


}
