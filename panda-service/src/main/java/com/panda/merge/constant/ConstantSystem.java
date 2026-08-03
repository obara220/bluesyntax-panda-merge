package com.panda.merge.constant;

import com.google.common.collect.Lists;
import com.panda.merge.config.RedisConfig;

import java.util.List;

/**
 * 静态字段
 *
 * @author tell
 * @since 2020年9月3日13:56:09
 */
public final class ConstantSystem {

    /**
     * 非实时项目标识
     */
    public static final String PROJECT_ID_NOREALTIME = "panda-nonrealtime-admin";
    public static final String nonrealtime = "nonrealtime";
    /**
     * 非实时项目消费组前缀标识
     */
    public static final String CONSUME_NONREALTIME_GROUP = "nonrealtime-group-";

    public static final String PAND_ODDS_GROUP = "panda-odds-group-";

    public static final String CONSUMER_PANDA_A99_GROUP = "panda-a99-group-";
    /**
     * 比分中心&WS服务
     */
    public static final String CONSUME_PANDA_SCORES_ADMIN_GROUP = "panda-scores-admin-group-";
    public static final String CONSUME_PANDA_WEBSOCKET_ADMIN_GROUP = "panda-websocket-admin-group-";
    public static final String CONSUME_SETTLE_SCORE_GROUP = "settle-score-group-";
    /**
     * 非实时项目特有数据拼接符
     */
    public static final String NOREALTIME_FIX = "::NOREALTIME::";

    /**
     * 实时项目标识
     */
    public static final String PROJECT_ID_REALTIME = "panda-realtime-admin";
    public static final String realtime = "realtime";
    /**
     * 实时项目消费组前缀标识
     */
    public static final String CONSUME_REALTIME_GROUP = "realtime-group-";

    /**
     * 数字常量
     */
    public static final Integer NUM_f1 = -1;
    public static final Integer ZERO = 0;
    public static final Integer ONE = 1;
    public static final Integer TWO = 2;
    public static final Integer THREE = 3;
    public static final Integer NUM_4 = 4;
    public static final Integer FIVES = 5;
    public static final Integer NUM6 = 6;
    public static final Integer NUM7 = 7;
    public static final Integer TEN = 10;
    public static final Integer TWENTY_FOUR = 24;
    public static final Integer SIXTY = 60;
    public static final Integer HUNDRED = 100;


    /**
     * 数据拼接分隔符
     */
    public static final String FIX = ":";
    public static final String XIN = "*";
    /**
     * 主客队标识
     */
    public static final String HOME = "home";
    public static final String AWAY = "away";
    public static final String VS = " .vs ";
    public static final String OUT = "out";

    public static final String STR_F1 = "-1";
    public static final String STR_KH = "{}";

    public static final Long LONG_0 = 0L;
    /**
     * 1秒换算成毫秒
     */
    public static final Long SECOND_1 = 1000L;
    /**
     * 1分钟换算成毫秒
     */
    public static final Long MINS_1 = 60000L;
    /**
     * 1小时换算成毫秒
     */
    public static final Long HOUR_1 = 3600000L;

    public static final String CHAMPION_CACHE = "Champion_Rediskey:";

    /**
     * 维护足球赛事当前阶段处于第几阶段时需要增加的赛事已进行时间
     */
    public static final List<Integer> periodTimeIntegerValue = Lists.newArrayList(45 * 60, 45 * 60 * 2, 45 * 60 * 2 + 15 * 60, 45 * 60 * 2 + 15 * 60 * 2);

    /**
     * 区域下足蓝等电竞赛事不显示 中立场
     */
    public static final List<Long> regionIds = Lists.newArrayList(343L, 344L, 345L, 346L, 347L, 348L, 349L, 350L, 351L, 352L, 353L, 354L, 355L, 356L, 357L, 358L, 359L, 360L);

    //====================RocketMq常量开始=========================

    /**
     * PA数据服务日志topic
     */
    public static final String PA_DATA_SERVICE_LOG = "PA_DATA_SERVICE_LOG";

    /**
     * 80906 【优化】【数据支撑】使用MYSQL的服务加入Druid连接池监控
     * 报表服务广播发送MQ的TOPIC：
     */
    public static final String DATA_PUSH_DRUID_MESSAGE = "data_push_druid_message";
    /**
     * 各服务发送监控数据MQ的TOPIC：
     */
    public static final String DATA_DRUID_MESSAGE_HANDLER = "data_druid_message_handler";

    //======================================非实时服务MQ常量开始=============================================
    /**
     * 赛季信息topic
     */
    public static final String THIRD_SEASON_INFO_API = "THIRD_SEASON_INFO_API";
    /**
     * 联赛信息topic
     */
    public static final String THIRD_TOURNAMENT_API = "THIRD_TOURNAMENT_API";
    /**
     * 赛事信息topic
     */
    public static final String THIRD_MATCH_INFO_API = "THIRD_MATCH_INFO_API";
    /**
     * 重播赛事信息topic
     */
    public static final String THIRD_REPLAY_MATCH_INFO_API = "THIRD_REPLAY_MATCH_INFO_API";
    /**
     * 冠军赛事信息topic
     */
    public static final String THIRD_OUTRIGHT_MATCH_INFO_API = "THIRD_OUTRIGHT_MATCH_INFO_API";
    /**
     * 球队球员信息topic
     */
    public static final String THIRD_SPORT_TEAM_API = "THIRD_SPORT_TEAM_API";
    /**
     * 三方赛事预期分析topic
     */
    public static final String THIRD_MATCH_EXPECTATION_API = "THIRD_MATCH_EXPECTATION_API";

    /**
     * 赛程项目操作【修改标准赛事信息】通知刷新缓存
     */
    public static final String STANDARD_MATCH_REFRESH = "STANDARD_MATCH_REFRESH";
    /**
     * 赛程项目操作【修改标准赛事信息】通知批量刷新缓存
     */
    public static final String STANDARD_MATCH_BATCH_REFRESH = "STANDARD_MATCH_BATCH_REFRESH";
    /**
     * 赛程项目操作【三方赛事标记相反 或者 取消标记相反】通知刷新缓存
     */
    public static final String MATCH_ASSOCIATION_ROUTER = "MATCH_ASSOCIATION_ROUTER";
    /**
     * 赛程项目操作【三方赛事绑定标准赛事】通知刷新缓存
     */
    public static final String MATCH_OPERATE_MSG = "MATCH_OPERATE_MSG";
    /**
     * 赛程项目操作【手工开赛和完赛】通知刷新缓存
     */
    public static final String CHANGE_MATCH_OVER = "CHANGE_MATCH_OVER";

    /**
     * 定时任务清理过期标准赛事通知
     */
    public static final String STANDARD_MATCH_OVER_DAY_CLEAN = "STANDARD_MATCH_OVER_DAY_CLEAN";
    /**
     * 定时任务清理过期三方赛事通知
     */
    public static final String THIRD_MATCH_OVER_DAY_CLEAN = "THIRD_MATCH_OVER_DAY_CLEAN";

    /**
     * 重播赛事
     */
    public static final String REPLAY_MATCH_SEND_BEGIN = "replay_match_send_begin";


    /**
     * 通知赛程三方赛季信息变更
     */
    public static final String QUEUE_SEASON = "queue_season";
    /**
     * 通知赛程三方联赛信息变更
     */
    public static final String QUEUE_TOURNAMENT = "queue_tournament";
    /**
     * 球员、球队、联赛国际化信息变更
     */
    public static final String THIRD_TEAM_TOURNAMENT_UPDATE_REDIS = "THIRD_TEAM_TOURNAMENT_UPDATE_REDIS";
    /**
     * 通知赛程三方赛事信息变更
     */
    public static final String QUEUE_MATCH = "queue_match";
    /**
     * 通知赛程三方球队人员信息变更
     */
    public static final String QUEUE_PLAYER = "queue_player";
    /**
     * 通知下游标准赛事信息变更
     */
    public static final String PUSH_MODIFY_MATCH_INFO = "PUSH_MODIFY_MATCH_INFO";
    /**
     * 赛事手动结束topic 废弃
     */
    @Deprecated
    public static final String PROCESS_MATCH_TO_OVER = "Process_Match_To_Over";

    /**
     * 三方赛事切换内部数据源
     */
    public static final String THIRD_MATCH_WITCH_DATA_SOURCE = "THIRD_MATCH_WITCH_DATA_SOURCE";
    /**
     * 赔率联动配置
     */
    public static final String RCS_MARKET_ODDS_LINKAGE_CONFIG = "RCS_MARKET_ODDS_LINKAGE_CONFIG";
    /**
     * 足球盘口赔率自动水差
     */
    public static final String RCS_AUTO_DIFF_MARET_ODDS = "RCS_AUTO_DIFF_MARET_ODDS";

    /**
     * 电子联盟默认三方区域ID
     */
    public static final String THIRD_DZ_REGION_ID = "15";

    /**
     * 3803 赛季下发比分网后台
     */
    public static final String THIRD_SEASON_INFO_PLS = "THIRD_SEASON_INFO_PLS";
    /**
     * 3803 联赛下发比分网后台
     */
    public static final String THIRD_TOURNAMENT_INFO_PLS = "THIRD_TOURNAMENT_INFO_PLS";
    /**
     * 3803 赛事下发比分网后台
     */
    public static final String THIRD_MATCH_INFO_PLS = "THIRD_MATCH_INFO_PLS";

    /**
     * 4066 三方球员下发比分网后台
     */
    public static final String THIRD_SPORT_PLAYER_PLS = "THIRD_SPORT_PLAYER_PLS";
    //======================================去DB异步入库相关===============================================
    /**
     * 事件信息异步入库topic
     */
    public static final String DATA_MATCHS_EVENT_INFO_DB = "DATA_MATCHS_EVENT_INFO_DB";
    /**
     * 统计信息异步入库topic
     */
    public static final String DATA_MATCHS_TATISTICS_INFO_DB = "DATA_MATCHS_TATISTICS_INFO_DB";
    /**
     * 三方赛事信息异步入库topic
     */
    public static final String DATA_THIRD_MATCH_INFO_DB = "DATA_THIRD_MATCH_INFO_DB";
    /**
     * 标准赛事信息异步入库topic
     */
    public static final String DATA_STANDARD_MATCH_INFO_DB = "DATA_STANDARD_MATCH_INFO_DB";
    /**
     * 球员信息变动预警
     */
    public static final String PLAYER_MODIFY_ALERT = "PLAYER_MODIFY_ALERT";
    //======================================非实时服务MQ常量结束=============================================
    //======================================实时服务MQ常量开始===============================================

    /**
     * 接收拷贝赛事通知
     */
    public static final String COPY_MATCH = "copy_match";

    /**
     * 事件信息topic （单条增量事件）
     */
    public static final String THIRD_MATCH_EVENT_INFO_API = "THIRD_MATCH_EVENT_INFO_API";

    public static final String CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API = "CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API";
    /**
     * 切换阶段下发事件
     */
    public static final String CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO = "MATCH_EVENT_INFO";

    /**
     * 范特西赛事状态topic
     */
    public static final String STANDARD_MATCH_STATUS_FTS = "STANDARD_MATCH_STATUS_FTS";

    /**
     * 事件信息列表topic（list增量事件）
     */
    public static final String THIRD_MATCH_EVENT_LIST_INFO_API = "THIRD_MATCH_EVENT_LIST_INFO_API";

    public static final String THIRD_MATCH_EVENT_INFO_API_SCORES = "THIRD_MATCH_EVENT_INFO_API_SCORES";

    /**
     * 关联SK数据，需要重推全量事件
     */
    public static final String MATCH_ASSOCIATION_INFO_SK = "MATCH_ASSOCIATION_INFO_SK";

    /**
     * 三方事件(三方赛事事件) 赛程服务-事件审核消费
     */
    public static final String THIRD_MATCH_EVENT_INFO = "THIRD_MATCH_EVENT_INFO";
    /**
     * 风控事件（关联了标准赛事的事件）比分服务&风控消费
     */
    public static final String MATCH_EVENT_INFO_TO_RISK = "MATCH_EVENT_INFO_TO_RISK";
    /**
     * 标准事件（开售事件源对应的事件）业务，风控消费
     */
    public static final String MATCH_EVENT_INFO = "MATCH_EVENT_INFO";
    /**
     * 视频集锦事件（V02视频集锦事件）
     */
    public static final String MATCH_EVENT_INFO_VIDEO = "MATCH_EVENT_INFO_VIDEO";
    /**
     * SK相关事件
     */
    public static final String MATCH_EVENT_INFO_SK = "MATCH_EVENT_INFO_SK";
    /**
     * 3795需求，业务需要的标准赛事下的事件数据
     */
    public static final String MATCH_EVENT_INFO_TO_3795 = "MATCH_EVENT_INFO_TO_3795";
    /**
     * FTS相关事件
     */
    public static final String MATCH_EVENT_INFO_FTS = "MATCH_EVENT_INFO_FTS";

    /**
     * ws事件监听topic
     */
    public static final String THIRD_MATCH_EVENT_INFO_API_WEBSOCKET = "THIRD_MATCH_EVENT_INFO_API_WEBSOCKET";

    /**
     * 状态信息topic
     */
    public static final String THIRD_MATCH_STATUS_API = "THIRD_MATCH_STATUS_API";

    /**
     * 需求2659 PD报球板新增可删除数据商事件(可以覆盖完赛状态和阶段)
     */
    public static final String MATCH_CANCEL_END = "MATCH_CANCEL_END";

    /**
     * 统计信息topic
     */
    public static final String MATCH_STATISTICS_INFO_API = "MATCH_STATISTICS_INFO_API";

    public static final String MATCH_STATISTICS_INFO_API_SCORES = "MATCH_STATISTICS_INFO_API_SCORES";
    /**
     * 玩法信息topic
     */
    public static final String THIRD_MARKET_CATEGORY_API = "THIRD_MARKET_CATEGORY_API";
    /**
     * 玩法投注项信息topic
     */
    public static final String THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API = "THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API";

    /**
     * 数据源是否可用：当数据源不可用时调用此接口关闭该数据源 (盘口取消时调用)
     */
    public static final String THIRD_GLOBAL_STATUS_API = "THIRD_GLOBAL_STATUS_API";

    /**
     * 切换事件源通知topic
     */
    public static final String LIVE_BUSINESS_EVENT_UPDATE_MESSAGE = "LIVE_BUSINESS_EVENT_UPDATE_MESSAGE";

    /**
     * 切换赛事状态源通知topic
     */
    public static final String LIVE_BUSINESS_STATUS_UPDATE_MESSAGE = "LIVE_BUSINESS_STATUS_UPDATE_MESSAGE";

    /**
     * 预售消息
     **/
    public static final String MATCH_ADVANCE_SALE = "Match_Advance_Sale";

    /**
     * 联赛更新
     **/
    public static final String TOPIC_TOURNAMENT_MODIFICATION = "modify_tournament";

    /**
     * 开售处理后补发事件topic
     */
    public static final String SOLD_MESSAGE = "SOLD_MESSAGE";

    /**
     * 开售后查询数据源比分数据
     */
    public static final String SOLD_MESSAGE_STANDARD_SCORES = "SOLD_MESSAGE_STANDARD_SCORES";

    public static final String TOPIC_STANDARD_MATCH_SCORES = "STANDARD_MATCH_SCORES";

    /**
     * 接收操盘非常规结束赛事完赛
     */
    public static final String FROM_RCS_MATCH_IS_END = "FROM_RCS_MATCH_IS_END";

    /**
     * 接收操盘手工下发事件
     */
    public static final String SCORES_EVENT_OPERATE = "SCORES_EVENT_OPERATE";
    /**
     * 接收操盘手工下发VAR事件
     */
    public static final String VAR_EVENT_OPERATE = "VAR_EVENT_OPERATE";


    /**
     * 三方心跳验证（业务调用）
     */
    public static final String THIRD_HEARTBEAT = "THIRD_HEARTBEAT";
    /**
     * 三方心跳验证，收到后响应到响应的topic
     */
    public static final String STANDARD_HEARTBEAT = "STANDARD_HEARTBEAT";

    /**
     * 操盘后台新增人工录入补时事件
     */
    public static final String INJURY_TIME = "injury_time";

    /**
     * 点球大战开始球队
     */
    public static final String PENALTY_SHOOTOUT_STARTING_TEAM = "penalty_shootout_starting_team";

    /**
     * 3803 赛事下发比分网后台
     */
    public static final String THIRD_MATCH_INFO_STATUS_PLS = "THIRD_MATCH_INFO_STATUS_PLS";
    public static final String STANDARD_MATCH_INFO_STATUS_PLS = "STANDARD_MATCH_INFO_STATUS_PLS";
    public static final String THIRD_MATCH_INFO_PERIODID_PLS = "THIRD_MATCH_INFO_PERIODID_PLS";
    public static final String STANDARD_MATCH_INFO_PERIODID_PLS = "STANDARD_MATCH_INFO_PERIODID_PLS";
    public static final String THIRD_MATCH_OVER_PLS = "THIRD_MATCH_OVER_PLS";
    public static final String STANDARD_MATCH_OVER_PLS = "STANDARD_MATCH_OVER_PLS";


    /**
     * 3803 比分网切换赛程标准赛事通知，收到通知需要补发赛事赛事状态和赛事阶段
     */
    public static final String NOTIFY_SCORE_CENTER_SEND_SCORE_PLS = "NOTIFY_SCORE_CENTER_SEND_SCORE_PLS";

    /**
     * 3875 【比分网】比分网后台-榜單管理
     */
    public static final String THIRD_SPORT_TEAM_RANKING_PLS = "THIRD_SPORT_TEAM_RANKING_PLS";
    public static final String THIRD_SPORT_PLAYER_RANKING_PLS = "THIRD_SPORT_PLAYER_RANKING_PLS";

    /**
     * 接收风控通知标准赛事是否需要手工完赛
     * 103304 【生产】【产品】【操盘风控】足球-等待加时期间异常下发完赛临时兜底-手动完赛
     */
    public static final String RCS_TRADE_MANUAL_FINISH = "RCS_TRADE_MANUAL_FINISH";

    //======================================赛事分析相关（没有直接下发的就是下游同步非实时服务dubbo同步数据）===============================================
    /**
     * 播控中心投递视频信息topic
     */
    public static final String THIRD_VIDEO_INFO_API = "THIRD_VIDEO_INFO_API";
    /**
     * 播控中心投递视频截图图片信息topic
     */
    public static final String THIRD_VIDEO_IMG_INFO_API = "THIRD_VIDEO_IMG_INFO_API";
    /**
     * 联赛下球队积分排行榜单信息topic
     */
    public static final String THIRD_SPORT_TEAM_RANKING_API = "THIRD_SPORT_TEAM_RANKING_API";
    /**
     * 联赛下球员排行榜单信息topic
     */
    public static final String THIRD_SPORT_PAYER_RANKING_API = "THIRD_SPORT_PAYER_RANKING_API";
    /**
     * 赛事阵容信息topic
     */
    public static final String THIRD_MATCH_LINEUP_API = "THIRD_MATCH_LINEUP_API";
    /**
     * 赛事缺阵球员名单信息topic
     */
    public static final String THIRD_MATCH_SIDELINED_API = "THIRD_MATCH_SIDELINED_API";
    /**
     * 赛事历史对阵信息（含现在，未来赛事）topic
     */
    public static final String THIRD_MATCH_HISTORY_STATISTICS_API = "THIRD_MATCH_HISTORY_STATISTICS_API";
    /**
     * 赛事历史百家赔信息topic
     */
    public static final String THIRD_MATCH_HISTORY_ODDS_API = "THIRD_MATCH_HISTORY_ODDS_API";
    /**
     * 赛事文字直播topic
     */
    public static final String THIRD_MATCH_PHRASE_INFO_API = "THIRD_MATCH_PHRASE_INFO_API";
    /**
     * 赛事比赛情报综合资讯topic
     */
    public static final String THIRD_MATCH_EX_INFOMATION_API = "THIRD_MATCH_EX_INFOMATION_API";
    /**
     * 联赛球队历史表现topic
     */
    public static final String THIRD_MATCH_HISTORY_EXPRESSION_API = "THIRD_MATCH_HISTORY_EXPRESSION_API";
    /**
     * 当前赛季统计信息topic
     */
    public static final String THIRD_MATCH_SEASON_STATISTICS_API = "THIRD_MATCH_SEASON_STATISTICS_API";
    /**
     * 正面交手统计信息topic
     */
    public static final String THIRD_MATCH_FRONT_STATISTICS_API = "THIRD_MATCH_FRONT_STATISTICS_API";

    /**
     * 赛事球队技术统计 topic
     */
    public static final String THIRD_MATCH_TEAM_SKILL_STATISTICS_API = "THIRD_MATCH_TEAM_SKILL_STATISTICS_API";

    /**
     * 杯赛淘汰赛事 topic
     */
    public static final String THIRD_MATCH_PROMOTION_CHART_API = "THIRD_MATCH_PROMOTION_CHART_API";

    /**
     * 赛程自动开盘消息
     */
    public static final String RCS_MARKET_FOOTBALL_GOAL_STATUS = "RCS_MARKET_FOOTBALL_GOAL_STATUS";

    //3929 【融合】数据商异常下发告警&数据下发限频
    /**
     * 赛程限流通知
     */
    public static final String FLOW_CONTROL_NOTIFICATION = "FLOW_CONTROL_NOTIFICATION";
    /**
     * 赛程限流透传
     */
    public static final String FLOW_CONTROL_NOTIFICATION_FORWARD = "FLOW_CONTROL_NOTIFICATION_FORWARD";
    /**
     * 赛事分析更新modifyTIme
     */
    public static final String THIRD_MATCH_ANALYSIS_MODIFY_TIME = "THIRD_MATCH_ANALYSIS_MODIFY_TIME";
    //======================================赛事分析相关结束===============================================


    //======================================实时服务MQ常量结束===============================================

    //======================================赔率服务服务MQ常量开始===============================================

    /**
     * 更新盘口名称国际化消息
     */
    public static final String MARKET_NAME_I18N_LIST = "MARKET_NAME_I18N_LIST";
    /**
     * 标准冠军赛事消息
     */
    public static final String STANDARD_OUTRIGHT_MATCH = "STANDARD_OUTRIGHT_MATCH";
    /**
     * 冠军盘口开售处理
     */
    public static final String OUTRIGHT_MARKET_SOLD_MESSAGE = "OUTRIGHT_MARKET_SOLD_MESSAGE";
    /**
     * 冠军盘口排序处理
     */
    public static final String OUTRIGHT_MARKET_ORDER_MESSAGE = "OUTRIGHT_MARKET_ORDER_MESSAGE";
    /**
     * 自建盘口的处理
     **/
    public static final String BUILD_OUTRIGHT_MARKET = "BUILD_OUTRIGHT_MARKET";
    /**
     * 清除相关盘口数据
     **/
    public static final String CLEAR_OUTRIGHT_MARKET = "CLEAR_OUTRIGHT_MARKET";

    /**
     * 冠军赛事赛果同步业务的手动盘口赛果
     */
    public static final String OSMC_MARKET_RESULT = "OSMC_MARKET_RESULT";

    /**
     * 赛果信息topic
     */
    public static final String THIRD_MARKET_RESULT_API = "THIRD_MARKET_RESULT_API";
    /**
     * 盘口信息topic
     */
    public static final String THIRD_MATCH_MARKET_API = "THIRD_MATCH_MARKET_API";
    public static final String DATACENTER = "_DATACENTER";
    //赛程后缀是DC
    public static final String DC = "_DC";
    /**
     * 三方赛事关盘信息topic
     */
    public static final String THIRD_MATCH_TRADE_MARKET_CONFIG_API = "THIRD_MATCH_TRADE_MARKET_CONFIG_API";
    /**
     * 盘口信息topic
     */
    public static final String THIRD_TX_MATCH_MARKET_API = "THIRD_TX_MATCH_MARKET_API";
    /**
     * 盘口取消时调用
     */
    public static final String THIRD_BET_CANCEL_API = "THIRD_BET_CANCEL_API";
    public static final String THIRD_MARKET_BET_CANCEL = "THIRD_MARKET_BET_CANCEL";
    /**
     * 回滚盘口取消操作时调用
     */
    public static final String THIRD_BET_CANCEL_ROLLBACK_API = "THIRD_BET_CANCEL_ROLLBACK_API";
    /**
     * 回滚盘口结算操作
     */
    public static final String THIRD_BET_SETTLEMENT_ROLLBACK_API = "THIRD_BET_SETTLEMENT_ROLLBACK_API";
    /**
     * 盘口结算概率信息topic
     */
    public static final String THIRD_MARKET_PRE_RESULT_API = "THIRD_MARKET_PRE_RESULT_API";
    /**
     * 盘口结算概率信息新topic
     */
    public static final String THIRD_MARKET_PRE_RESULT_NEW_API = "THIRD_MARKET_PRE_RESULT_NEW_API";
    /**
     * 触发赛事赔率下发
     */
    public static final String STANDARD_MATCH_ODDS_ISSUED = "STANDARD_MATCH_ODDS_ISSUED";
    /**
     * 玩法赔率最新更新时间下发
     */
    public static final String MATCH_OPERATE_EX = "MATCH_OPERATE_EX";
    /**
     * AO apply清除水差
     */
    public static final String STANDARD_CATEGORYID_CLEAR_DIFF = "STANDARD_CATEGORYID_CLEAR_DIFF";
    /**
     * AO apply清除水差 通知风控
     */
    public static final String STANDARD_CATEGORYID_CLEAR_DIFF_RISK = "STANDARD_CATEGORYID_CLEAR_DIFF_RISK";
    /**
     * LS 切换数据源，通知风控清水差
     ***/
    public static final String LS_SEND_RSC_MATCH_STATUS = "LS_SEND_RSC_MATCH_STATUS";
    /**
     * 爬虫数据源赛事级关封topic通知
     */
    public static final String THIRD_MATCH_MARKET_STATUS = "THIRD_MATCH_MARKET_STATUS";

    /**
     * 操盘 MTS 开关配置
     */
    public static final String RCS_PENDING_TRADING_CONFIG = "rcs_pending_trading_config";

    public static final String CATEGORY_DATASOURCE_L_API = "CATEGORY_DATASOURCECODE_L_API";

    public static final String CATEGORY_DATASOURCE_T_API = "CATEGORY_DATASOURCECODE_T_API";

    public static final String THIRD_INTERNALCODE_API = "THIRD_INTERNALCODE_API";
    public static final String AUTO_OPEN_DATA_SOURCE_CODE_BEFORE = "AUTO_OPEN_DATA_SOURCE_CODE_BEFORE";

    public static final String STANDARD_MATCH_SCORE_CHANGE_API = "STANDARD_MATCH_SCORE_CHANGE_API";

    public static final String TOPIC_PA_WARN = "PA_COMMON_WARN_INFO";
    /**
     * 篮球独赢独赢原始赔率限制
     **/
    public static final String RCS_BASKETBALL_ORIGINALODDS_LIMIT = "rcs_basketball_originalOdds_limit";

    /**
     * 三方盘口新增
     */
    public static final String THIRD_SPORT_MARKET_INSERT = "THIRD_SPORT_MARKET_INSERT";
    /**
     * 三方盘口修改
     */
    public static final String THIRD_SPORT_MARKET_UPDATE = "THIRD_SPORT_MARKET_UPDATE";
    /**
     * 三方盘口赔率新增
     */
    public static final String THIRD_SPORT_MARKET_ODDS_INSERT = "THIRD_SPORT_MARKET_ODDS_INSERT";
    /**
     * 三方盘口赔率修改
     */
    public static final String THIRD_SPORT_MARKET_ODDS_UPDATE = "THIRD_SPORT_MARKET_ODDS_UPDATE";

    /**
     * 标准盘口新增
     */
    public static final String STANDARD_SPORT_MARKET_INSERT = "STANDARD_SPORT_MARKET_INSERT";
    /**
     * 标准盘口修改
     */
    public static final String STANDARD_SPORT_MARKET_UPDATE = "STANDARD_SPORT_MARKET_UPDATE";
    /**
     * 标准盘口赔率新增
     */
    public static final String STANDARD_SPORT_MARKET_ODDS_INSERT = "STANDARD_SPORT_MARKET_ODDS_INSERT";
    /**
     * 标准盘口赔率修改
     */
    public static final String STANDARD_SPORT_MARKET_ODDS_UPDATE = "STANDARD_SPORT_MARKET_ODDS_UPDATE";
    /**
     * 三方赛事解绑
     */
    public static final String UNBIND_AOMATCH_DATA = "Unbind_AOMatch_Data";
    /**
     * 延长开售
     */
    public static final String A01_EXTENDED_TIME_STATUS = "A01_EXTENDED_TIME_STATUS";
    public static final String A99_STANDARD_ODDS_API = "A99_STANDARD_ODDS_API";
    public static final String A99_STANDARD_ODDS_STATUS_API = "rcs_trade_a_ninetynine_switch";

    /**
     * 数据源维护
     */
    public static final String DATA_SOURCE_MAINTENANCE_NOTICE = "DATA_SOURCE_MAINTENANCE_NOTICE";
    /**
     * 标准玩法上下架
     */
    public static final String STANDARD_MATCH_CATEGORY_REMOVED = "STANDARD_MATCH_CATEGORY_REMOVED";
    //======================================赔率服务服务MQ常量结束===============================================

    //======================================A99服务MQ常量开始===============================================
    /** A99系统参数配置 */
    public static final String A99_SYSTEM_PARAM_CONFIG = "A99_SYSTEM_PARAM_CONFIG";

    /** A99赔率开关 */
    public static final String A99_MATCH_MARKET_SWITCH = "A99_MATCH_MARKET_SWITCH";

    /** A99下发给风控赔率 */
    public static final String A99_MARKET_ODDS_TO_RISK = "A99_MARKET_ODDS_TO_RISK";

    /** A99下发给A01 */
    public static final String A99_MARKET_ODDS_TO_A01 = "A01_NINETYNINE_ODDS_TRIGGER";

    /** A99下发给融合赔率 */
    public static final String A99_MARKET_ODDS_TO_RH = "A99_MARKET_ODDS_TO_RH";

    /** A99赛事开关*/
    public static final String A99_MATCH_SWITCH = "rcs_trade_a_ninetynine_swtich";

    /** A99数据源权重*/
    public static final String A99_DATA_SOURCE_WEIGHT = "RCS_TOUR_MATCH_TEMPLATE_A99_CONFIG_TOPIC";

    /** A99赔率变化差值*/
    public static final String A99_MATCH_ODDS_CHANGE_DIFFERENCE = "A99_MATCH_ODDS_CHANGE_DIFFERENCE";

    /** A99下发主盘口给A01*/
    public static final String A99_STANDARD_ODDS_TO_A01 = "A99_STANDARD_ODDS_TO_A01";
    //======================================A99服务MQ常量结束===============================================


    //====================RocketMq常量结束=========================

    //====================Dubbo常量开始=========================
    /**
     * 根据三方数据源赛事ID查询三方赛事信息
     */
    public static final String QUERY_THIRD_MATCH_INFO_BY_THIRD_SOURCE_ID = "queryThirdMatchInfoByThirdSourceId";
    /**
     * 获取三方联赛信息列表
     */
    public static final String QUERY_THIRD_SPORT_TOURNAMENT_LIST = "queryThirdSportTournamentList";
    /**
     * 根据修改时间分页查询标准球员信息
     */
    public static final String QUERY_STANDARD_SPORT_PLAYER_BY_UPDATE_TIME = "queryStandardSportPlayerByUpdateTime";
    /**
     * 获取全部字典信息（字典类型+字典值）
     */
    public static final String QUERY_SYSTEM_DATA = "querySystemData";
    /**
     * 分页查询标准赛事数据
     */
    public static final String QUERY_STANDARD_MATCH_INFO_PAGE = "queryStandardMatchInfoPage";
    /**
     * 据三方赛事ID查询标准赛事
     */
    public static final String QUERY_STANDARD_MATCH_INFO_BY_THIRD_SOURCE_ID = "queryStandardMatchInfoByThirdSourceId";
    /**
     * 根据标准赛事ID查询标准赛事
     */
    public static final String QUERY_STANDARD_MATCH_INFO_BY_ID = "queryStandardMatchInfoById";
    /**
     * 根据标准赛事ID查询标准赛事
     */
    public static final String QUERY_STANDARD_MATCH_INFO_OVER_TIME_BY_ID = "queryStandardMatchInfoOverTimeById";

    /**
     * 查询标准体育类型列表
     */
    public static final String QUERY_STANDARD_SPORT_TYPE_PAGE = "queryStandardSportTypePage";
    /**
     * 分页查询标准联赛列表
     */
    public static final String QUERY_SPORT_TOURNAMENT_PAGE = "querySportTournamentPage";
    /**
     * 分页查询标准联赛规则列表
     */
    public static final String QUERY_TOURNAMENT_RULE_PAGE = "queryTournamentRulePage";
    /**
     * 分页查询标准赛程（球队）列表
     */
    public static final String QUERY_SPORT_MATH_TEAM_PAGE = "querySportMathTeamPage";
    /**
     * 分页查询体育区域列表
     */
    public static final String QUERY_STANDARD_SPORT_REGION_PAGE = "queryStandardSportRegionPage";
    /**
     * 分页查询标准玩法玩，法投注项列表
     */
    public static final String QUERY_STANDARD_SPORT_MARKET_CATEGORY_PAGE = "queryStandardSportMarketCategoryPage";
    /**
     * 分页查询三方盘口列表
     */
    public static final String QUERY_THIRD_SPORT_MARKET_PAGE = "queryThirdSportMarketPage";
    /**
     * 分页查询三方盘口列表(统计使用)
     */
    public static final String QUERY_THIRD_SPORT_MARKET_PAGE_FOR_REPORT = "queryThirdSportMarketPageForReport";
    /**
     * 分页查询三方赛事列表
     */
    public static final String QUERY_THIRD_MATCH_INFO_PAGE = "queryThirdMatchInfoPage";
    /**
     * 查询数据来源列表
     */
    public static final String QUERY_DATA_SOURCE_PAGE = "queryDataSourcePage";

    /**
     * 查询三方球队排行榜单
     */
    public static final String QUERY_THIRD_SPORT_TEAM_RANKING = "queryThirdSportTeamRanking";
    /**
     * 查询三方球员排行榜单
     */
    public static final String QUERY_THIRD_SPORT_PLAYER_RANKING = "queryThirdSportPlayerRanking";
    /**
     * 冠军赛事下发
     */
    public static final String QUERY_OUTRIGHT_MATCH = "queryOutrihtMatch";

    //====================Dubbo常量结束=========================

    //====================Redis桶数量=========================
    public static final Integer BUCKET_QUANTITY_SIXTY_FOUR = 64;
    public static final Integer BUCKET_QUANTITY_EIGHT = 8;

    public static final String CATEGORY_MARKET_LEVEL = "CATEGORY_MARKET_LEVEL";


    //====================Redis缓存key=========================

    public static final String MATCH_OVER_TIME = "%s:MATCH_OVER_TIME";

    /**
     * 实时统计去DB
     * %s : 数据源编码
     * %s : 数据源赛事ID
     */
    public static String getMatchStatisticsInfoKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchStatisticsInfo:%s_%s";
    }

    /**
     * 实时事件去DB，记录告警事件前10条数据（单号：79713）
     * %s : 数据源编码
     * %s : 标准赛事ID
     */
    public static String getAlertsEventsKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:79713:alertsEvents:%s_%s";
    }

    /**
     * 实时事件去DB，缓存三方赛事阶段key
     * %s : 数据源编码
     * %s : 数据源赛事ID
     */
    public static String getThirdMatchPeriodKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:MatchPeriod:%s_%s";
    }

    /**
     * 实时事件去DB，缓存标准赛事阶段key
     * %s : 标准赛事ID
     */
    public static String getStandardMatchPeriodKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:MatchPeriod:%s";
    }

    /**
     * 实时事件去DB，缓存三方赛事999阶段
     * %s : 数据源编码
     * %s : 数据源赛事ID
     */
    public static String getMatchPeriod999Key() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:999:%s_%s";
    }


    /**
     * 记录同一标准赛事下发不同事件源5S内相同事件只需要下发一次
     * %s : 标准赛事ID
     * %s : 主客队标识
     * %s : 事件编码
     */
    public static String getStandardMatch3795Key() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:3795:%s_%s_%s";
    }


    /**
     * 获取同源赛事下源事件ID缓存（删除事件）
     * %s : 数据源编码
     * %s : 源赛事ID
     * %s : 源事件ID
     */
    public static String getDeleteEventKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:DELETE_EVENT:%s_%s_%s";
    }

    /**
     * 获取同源赛事下源事件ID缓存（比分事件）
     * %s : 数据源编码
     * %s : 源赛事ID
     * %s : 源事件ID
     */
    public static String getThirdMatchScoresEventKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:SCORES:85728:%s_%s_%s";
    }

    /**
     * 88998 【生产】【产品】【操盘风控】足球主玩法-客户端不展示关盘赛事尾声球头兜底优化
     * %s : 标准赛事ID
     */
    public static String getStandardSecondsMatchStartKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:SecondsMatchStart:%s";
    }


    /**
     * dubbo 需要兜底处理的事件
     * %s : 数据源编码
     * %s : 源赛事ID
     * %s : 源事件ID
     */
    public static String getDubboEventKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:DubboEvent:%s_%s_%s";
    }

    /**
     * 4248 【赛程】赛事中断场景优化
     * %s : 标准赛事ID
     */
    public static String getInterruptedKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:InterruptedKey:%s";
    }

    /**
     * 最近MatchStatus事件缓存
     * %s : 标准赛事ID
     */
    public static String getEventMatchStatusKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:MatchStatus:%s";
    }


    /**
     * 最近一条标准事件缓存
     * %s : 标准赛事ID
     */
    public static String getStandardEventLastKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:last:%s";
    }

    /**
     * 103304 【生产】【产品】【操盘风控】足球-等待加时期间异常下发完赛临时兜底-手动完赛
     *   兜底工具 PD事件源  PD状态源 可以正常触发完赛
     * %s : 标准赛事ID
     */
    public static String getStandardManuallyEndFlagKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:ManuallyEndFlag:%s";
    }

    /**
     *104504 【生产】【产品】【pc＆h5】自研动画var事件常驻展示 标记
     */
    public static String getZ01VarFlagKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:Z01VarFlag:%s";
    }

    /**
     *104504 【生产】【产品】【pc＆h5】自研动画var事件常驻展示 标记
     */
    public static String getZ01PossiblePenaltyFlagKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:Z01PossiblePenaltyFlag:%s";
    }

    /**
     * 获取同源赛事下源事件ID缓存（删除事件）,lockKey
     * %s : 数据源编码
     * %s : 源赛事ID
     * %s : 源事件ID
     */
    public static String getDeleteEventLockKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:DELETE_EVENT_LOCK:%s_%s_%s";
    }


    /**
     * 106537 dataSourceCode_thirdMatchSourceId_standardHomeScore_standardAwayScore
     */
    public static String getScoreValidationKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:ScoreDiff:{%s_%s_%s_%s}";
    }

    public static String getScoreValidationIndexKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:ScoreDiff:Index:{%s_%s}";
    }

    public static String getBusinessEventLockKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:BusinessEventSwitchLock:{%s}";
    }

    public static String getPlayerModifyAlertKey() {
        return RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportPlayer:ModifyAlterKey:%s_%s_%s";
    }

    /**
     * R01足球下发了100阶段和999阶段事件后,又会下发一次100阶段和999阶段事件 单号109329
     * %s : 数据源编码
     * %s : 数据源赛事ID
     */
    public static String getMatchPeriod999KeyForCheck() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:c999:%s_%s";
    }

    public static String getMatchPeriod100KeyForCheck() {
        return RedisConfig.REDIS_KEY_DATABASE + "::MatchEventInfo:c100:%s_%s";
    }

}
