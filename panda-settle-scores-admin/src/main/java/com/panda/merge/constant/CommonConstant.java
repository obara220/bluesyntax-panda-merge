package com.panda.merge.constant;

/**
 * @description: common constant
 * @author: Henry Wang
 * @create: 2024-08-28 19:26
 **/
public class CommonConstant {

    public static final Integer COMMON_FALSE_FLAG = 0;
    public static final Integer COMMON_TRUE_FLAG = 1;
    public static final String SETTLE_MENTION_KEY = "settle-score:settle:mention:";
    public static final String BASKETBALL_GRAY_GAOL_6MIN = "goal6Min";
    public static final String BASKETBALL_SCORE_EVENT_CODE = "score_change";
    public static final String MATCH_PHASE_SCORE_SETTLE ="StandardMatchScoreConsumer:";
    public static final String MATCH_SEQUENCE_SCORE_SETTLE ="StandardMatchScoreConsumer:";
    public static final String GRAY_MIN = "gray_min";
    public static final String GRAY_TYPE = "grayType";
    public static final String GRAY_Score = "grayScore";
    public static final String SETTLE_SWITCH = "settle-switch:";
    public static final String SETTLE_SLAVE_DB_TOPIC = "settle_slave_db_storage";
    public static final String SETTLE_DATA_SOURCE_CONFIG_TABLE = "MatchSettleDataSourceConfigEntity";
    public static final String SETTLE_DATA_SOURCE_SWITCH_TABLE = "MatchSettleDataSourceSwitchEntity";
    public static final String SETTLE_INFO_TABLE = "MatchSettleInfoEntity";
    public static final String SETTLE_TEMPLATE_TABLE = "MatchSettleTemplateEntity";
    public static final String SETTLE_TEMPLATE_RELATION_TABLE = "MatchSettleTemplateRelationEntity";
    public static final String SETTLE_GOAL_STATUS_TABLE = "MatchSettleGoalStatusEntity";
    public static final String SETTLE_FACTOR_CHECK_INFO_TABLE = "MatchSettleFactorCheckInfoEntity";
    public static final String SETTLE_ROLL_BACK_INFO_TABLE = "MatchSettleRollBackInfoEntity";
    public static final String SETTLE_OPERATE_LOG_TABLE = "MatchSettleOperateLogEntity";
    public static final String IS_INSERT = "isInsert";
    public static final String TAG = "TAG";
    public static final String MATCH_SETTLE_SCORE_COUNT = "match.settle.score.send.count:";

    public static final String SETTLE_FLOW_CONTROL_MATCH_IDS = "settle.flow.control.match.ids";
    public static final String FIVE_MIN_SETTLE_TIMES = "settle:five:min:settle:times:";
    public static final String SETTLE_DATASOURCE_LOST_CONNECTION = "settle:datasource:lost:connection:";
    
    // 数据商心跳相关Redis key
    public static final String DATASOURCE_HEARTBEAT_TIMESTAMP = "datasource:heartbeat:timestamp:"; // 比赛+数据源时间戳（比赛维度）
    public static final String DATASOURCE_HEARTBEAT_CONNECTION_STATUS = "datasource:heartbeat:connection:status:"; // 比赛+数据源连接状态（比赛维度）
    public static final String DATASOURCE_MAINTENANCE_TIME = "datasource:maintenance:time:"; // 数据源维护时间
    
    // 结算相关Redis key
    public static final String SETTLE_DATA_MISMATCH_PHASE = "settle:data:mismatch:phase:"; // 数据不一致事件阶段
    public static final String SETTLE_DELETE_EVENT_DATA_SOURCE = "settle:delete:event:data:source:"; // 删除事件相应的次序,时段,阶段对应数据的数据源
    public static final String SETTLE_MANUAL_PROMPT = "settle:manual:prompt:"; // 人工结算提示

    /**
     * 标准赛事下关联的所有数据源列表
     * key 格式：settle:match:datasources:{standardMatchId}
     * value：List<String> 数据源编码列表
     */
    public static final String SETTLE_MATCH_DATASOURCES = "settle:match:datasources:";

}
