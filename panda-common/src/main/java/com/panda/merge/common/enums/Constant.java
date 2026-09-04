package com.panda.merge.common.enums;


import com.google.common.collect.Lists;

import java.util.*;

/**
 *
 */
public interface Constant {
    /**
     * 通过Addition3确定球队ID的玩法
     */
    Long[] CATEGORY_ADDITION3 = {13L, 171L};

    /**
     * 通过Additionl确定球队ID的玩法
     */
    Long[] CATEGORY_ADDITION1 = {1L, 3L, 4L, 5L, 8L, 9L, 10L, 11L, 17L, 19L, 21L, 22L, 25L, 27L, 28L, 29L, 30L, 32L, 33L, 37L, 39L, 43L, 44L, 46L, 48L, 49L, 50L, 52L, 54L, 55L, 56L, 58L, 60L, 61L, 62L, 64L, 66L, 67L, 69L, 71L, 78L, 79L, 80L, 81L, 82L, 83L, 84L, 85L, 86L, 87L, 88L, 89L, 90L, 92L, 93L, 94L, 95L, 96L, 97L, 98L, 99L, 100L, 111L, 112L, 113L, 115L, 116L, 119L, 120L, 121L, 123L, 124L, 125L, 126L, 128L, 129L, 130L, 132L, 135L, 136L, 137L, 139L, 140L, 141L, 142L, 143L, 145L, 146L, 147L, 144L, 149L, 153L, 154L, 155L, 156L, 157L, 162L, 163L, 167L, 168L, 172L, 175L, 176L, 179L, 181L, 184L, 185L, 188L, 189L, 192L, 193L, 195L, 196L, 224L, 225L, 231L, 310L, 311L, 320L, 321L, 322L, 323L, 394L};

    /**
     * 通过Addition2确定球队ID的玩法
     */
    Long[] CATEGORY_ADDITION2 = {101L, 105L, 106L};
    /**
     * 通过Addition1、Addition2确定球队ID的玩法
     */
    Long[] CATEGORY_ADDITION1_ADDITION2 = {77L, 91L};
    /**
     * 通过Addition4、Addition2确定球队ID的玩法
     */
    Long[] CATEGORY_ADDITION2_ADDITION4 = {6L, 70L, 72L, 104L, 107L, 161L, 1100421L, 1100422L, 1100425L, 1100426L};

    /**
     * 有效冠军数据源
     */
    String[] ACTIVE_CHAMPION_DATA_SOURCE = {"SR","F01"};

    /**
     * INTEGER 标记类型  0
     */
    Integer INTEGER_FLAG_ZERO = 0;

    /**
     * INTEGER 标记类型  1
     */
    Integer INTEGER_FLAG_ONE = 1;

    /**
     * 冠军赛事（自动操盘状态、盘口处理类型）
     */
    Integer OUTRIGHT_ZERO = 0;

    /**
     * 冠军赛事（手动操盘状态、盘口处理类型）
     */
    Integer OUTRIGHT_ONE = 1;


    /**
     * 即将开赛 标记类型 1
     */
    Integer ODDS_LIVE = 1;

    /**
     * 非即将开赛 标记类型 0
     */
    Integer NOT_ODDS_LIVE = 0;

    /**
     * 自动关盘标识:0开，1关
     */
    Integer AOTU_CLOSE_STATUS = 1;

    /**
     * 足球赔率告警不下发阶段：未开赛、中场休息
     */
    List<Long> FOOT_BALL_PERIOD_FILTER_WARNING = Arrays.asList(0L, 31L, 33L);
    /**
     * 2分钟没有更新需要关盘的数据源
     */
    List<String> WARNING_DATA_SOURCE_CODE = Arrays.asList(DataSourceCodeEnum.LS.getCode(),DataSourceCodeEnum.BG.getCode(),DataSourceCodeEnum.BC.getCode());

    //综合球种ID：乒乓球/羽毛球/排球/斯诺克
    List<Long> COMPLEX_SPORTIDS = new ArrayList<>(Arrays.asList(7L, 8L, 9L, 10L));

    String LS_Bet365 = "L01-Bet365";

    interface REDIS_KEY {
        String RONGHE_STANDARDMARKET_TRADETYPE = "Ronghe:StandardMarket:TradeType:";
        String RONGHE_STANDARD_MATCH_TRADETYPE = "Ronghe:StandardMatch:TradeType:";
        //操盘配置缓存
        String RONGHE_TRADETYPE_MATCH = "Ronghe:TradeType:Match";
        String RONGHE_TRADETYPE_CATEGORY = "Ronghe:TradeType:Category";
        String RONGHE_TRADETYPE_MARKET = "Ronghe:TradeType:Market";
        //赔率最大最小配置缓存
        String RONGHE_MARKET_MAX_MIN_DIFF_CONFIG = "Ronghe:Market:MaxMin:DiffConfig:";
        String RONGHE_STANDARD_MARKET_DIFF_CONFIG = "Ronghe:StandardMarket:DiffConfig:";
        String RONGHE_LINK_ID = "Ronghe:LinkId:";
        String RONGHE_MARKET_CATEGORY_MARGIN = "Ronghe:MarketCategoryMargin:";
        String RONGHE_STANDARD_MARKET_ID = "Ronghe:StandardMarket:ID:";

        String RONGHE_STANDARD_MARKET = "Ronghe:StandardMarketData:";
        //赛事+数据源+玩法
        String RONGHE_STANDARD_CATEGORY_MARKET = "Ronghe:StandardCategoryMarketData:";
        String RONGHE_STANDARD_MARKET_ODDS_ID = "Ronghe:StandardMarketOdds:ID:";
        String RONGHE_THRID_MARKET_DATASOURCE_TIME = "Ronghe:ThridMarket:dataSourceTime:";
        String RONGHE_STANDARD_MARKET_RELATION_MARKET_ID = "Ronghe:StandardMarket:RelationMarketId:";
        String RONGHE_STANDARD_MARKET_ODDS_RELATION_MARKET_ODDS_ID = "Ronghe:StandardMarketOdds:RelationMarketOddsId:";
        /** hash {categoryId: dataSourceCode} **/
        String RONGHE_MARKET_CATEGORY_SELL = "Ronghe:StandardMarketData:MarketCategorySell:";
        String RONGHE_STANDARD_MARKET_MARGIN_CONFIG = "Ronghe:Market:margin:";
        String RONGHE_TOURNAMENT_ID = "Ronghe:tournamentId:";
        String RONGHE_LOCK = "Ronghe:lock:";

        String RONGHE_CATEGORY_LOCK = "Ronghe:category:lock:";
        String RONGHE_LOCK100 = "Ronghe:lock100s:";
        //玩法自动关盘
        String RONGHE_STANDARD_CATEGORY_AUTO_CLOSE = "Ronghe:standardCategoryAutoClose:";
        //赛前切滚球
        String RONGHE_STANDARD_MARKET_SWITCH_STATUS = "Ronghe:standardMarketSwitchStatus:";
        //数据商第一次滚球赔率时间
        String RONGHE_STANDARD_MARKET_LIVE_TIME = "Ronghe:standardMarketLiveTime:";
        //TX特殊处理三方盘口信息
        String RONGHE_TX_STANDARD_MARKET = "Ronghe:TxStandardMarketData:";
        //玩法赔率告警
        String RONGHE_MATCH_CATEGORY_ODDS_WARNING = "Ronghe:MatchCategoryOddsWarning";
        String RONGHE_MATCH_CATEGORY_ODDS_WARNING_NEW = "Ronghe:MatchCategoryOddsWarning:new";
        //TX三方盘口数据
        String RONGHE_TX_THIRD_MARKET = "Ronghe:ThirdTxMarketData:";
        //三方赛事不存在缓存三方盘口数据
        String RONGHE_THIRD_MARKET = "Ronghe:ThirdMarketData:";
        //三方赛事开赛时间总缓存key
        String RONGHE_THIRD_PER_MARKET = "Ronghe:ThirdPreMarketData";
        //标准赛事是否已经下发过自动构建赔率key
        String RONGHE_STANDARD_PER_MARKET = "Ronghe:StandardPreMarketData:";
        //赛前转滚球构建缓存
        String RONGHE_STANDARD_CONVERT_MARKET = "Ronghe:StandardConvertMarketData:";
        //提前结算三方信息
        String THIRD_MARKET_PRE_RESULT = "Ronghe:ThirdMarketPreResult:";
        //系统层级提前结算开关参数信息
        String SYSTEM_THIRD_MARKET_PRE_PARAMS = "Ronghe:SystemThirdMarketPreParams:";
        //提前结算标准盘口信息
        String STANDARD_MARKET_PRE_RESULT = "Ronghe:StandardMarketPreResult:";
        //提前结算关盘校验参数
        String CHECK_STANDARD_MATCH_INFO = "Ronghe:CheckStandardMatchInfo:";
        //提前结算三方信息时间戳
        String THIRD_MARKET_PRE_RESULT_DATASOURCE_TIME = "Ronghe:ThridMarket:PreResultDataSourceTime:";
        //缓存A+模式球头
        String RONGHE_STANDARD_CATEGORY_BALL = "Ronghe:standardCategoryBall:";
        //挡板上一次坑位1盘口赔率
        String RONGHE_STANDARD_CATEGORY_MARKET_ODDS_LAST = "Ronghe:standardCategoryMarketOddsLast:";
        //最新下发盘口缓存时间戳key
        String RONGHE_STANDARD_THE_LAST_MARKETODDS_DATE = "Ronghe:theLastMarketOddsDateKey:";
        //最新下发A+盘口缓存时间戳key
        String RONGHE_STANDARD_THE_LAST_A_MARKETODDS_DATE = "Ronghe:theLastAMarketOddsDateKey:";
        //最新下发盘口赔率缓存key
        String RONGHE_STANDARD_THE_LAST_MARKETODDS = "Ronghe:theLastMarketOddsKey:";
        //最新下发A+盘口赔率缓存key
        String RONGHE_STANDARD_THE_LAST_A_MARKETODDS = "Ronghe:theLastAMarketOddsKey:";
        //赛事玩法集状态
        String RONGHE_STANDARD_CATEGORY_SET_STATUS = "Ronghe:standardCategorySetStatus:";
        //赛事玩法集缓存key
        String RONGHE_STANDARD_CATEGORY_SETCODE_CACHE = "Ronghe:standardCategorySetCodeCache:";

        String RONGHE_STANDARD_THE_LAST_MARKETODDS_TIME = "Ronghe:theLastAMarketOddsTimeKey:";
        /**
         * 4405 玩法级操盘模式（Hash）
         * key: Ronghe:playRiskManager:{matchId}:{marketType}
         * field: {categoryId}
         * value: {riskManagerCode}
         */
        String RONGHE_PLAY_RISK_MANAGER = "Ronghe:playRiskManager:";
        //标准赔率topic心跳key
        String RONGHE_STANDARD_MARKET_ODDS_HEARTBEAT = "Ronghe:standardMarketOddsHeartBeat";
        String RONGHE_STANDARD_OUTRIGHT_MARKETALARMTASK = "Ronghe:outrightMarketAlarmTask";
        //融合独赢让分key
        String RONGHE_STANDARD_MARKET_ODDS_WINNER_HANDCIP = "Ronghe:standard:odds:winner:handcip:";

        //三方赛事不存在缓存三方盘口数据 TX/AO
        String RONGHE_THIRD_STANDARD_MARKET = "Ronghe:ThirdStandardMarketData:";
        //三方赛事不存在缓存三方盘口数据 LS
        String RONGHE_LS_THIRD_STANDARD_MARKET = "Ronghe:LsThirdStandardMarketData:";
        //三方数据源球头
        String THIRD_MARKET_HEAD = "Ronghe:third:market:head:";
        String THIRD_MARKET_HEAD_CLOSE = "Ronghe:third:market:head:close:";
        String THIRD_ALL_MARKET_HEAD = "Ronghe:third:all:market:head:";
        //三方篮球数据源球头
        String THIRD_BASKETBALL_MARKET_HEAD = "Ronghe:third:basketball:market:head:";
        String THIRD_ALL_BASKETBALL_MARKET_HEAD = "Ronghe:third:all:basketball:market:head:";
        //三方篮球数据源球头 T01 A01
        String THIRD_T_A_BASKETBALL_MARKET_HEAD = "Ronghe:third:ta:basketball:market:head:";
        //三方篮球次要玩法数据源球头
        String THIRD_BASKETBALL_MARKET_MAINLY_NOT_HEAD = "Ronghe:third:basketball:market:mainly:nothead:";
        //初盘赛事
        String THIRD_FIST_MATCH = "Ronghe:third:fist:match";
        //各个数据源初盘赔率
        String THIRD_FIST_MARKET_HEAD = "Ronghe:third:fist:market:head:";
        //VR事件
        String STANDARD_EVENT_VR_CODE = "Ronghe:standard:event:vr:code:";
        //auto open切换的数据源 赛事
        String AUTO_OPEN_DATA_SOURCE_CODE_MATCH = "Ronghe:auto:open:dataSourceCode:match";
        //auto open切换的数据源 <玩法ID,数据源>
        String AUTO_OPEN_DATA_SOURCE_CODE = "Ronghe:auto:open:dataSourceCode:";
        //A01延长开售状态 延迟开售 1 开  0关
        String A01_EXTENDED_TIME_STATUS_KEY = "Ronghe:a01:extended:time:status:";
        //维护数据源 Map<数据源,enableSwitch#开始时间#结束时间>
        String AO_MAINTAIN_DATA_SOURCE = "Ronghe:maintain:datasource";

        String CACHE_KEY_PRE_SOLD_REPORT = "Ronghe:preSold:Report:";

        //108048
        String THIRD_MARKET_108048 = "Ronghe:CACHE:THIRD_MARKET_108048:";
        /**
         * 将废弃
         */
        @Deprecated
        String RONGHE_TEMPLATE_CONFIGURATION_ID = "Ronghe:templateId:";
        /**
         * 将废弃
         */
        @Deprecated
        String RONGHE_TEMPLATE_CONFIGURATION_DETAIL = "Ronghe:templateDetail:";

        String RONGHE_MATCH_MARKET_CONFIGURATION_EVENT = "Ronghe:MatchMarketConfiguration:Event:";
        String RONGHE_MATCH_MARKET_CONFIGURATION_CATEGORY = "Ronghe:StandardMarketData:MarketCategorySell:";

        String RONGHE_OUTRIGHTMARKET_ALARM = "Ronghe:OutrightMarket:Alarm:v:";

        String RONGHE_OUTRIGHTMARKET_ALARM_K = "Ronghe:OutrightMarket:Alarm:k:";
        /**
         * 抢单机制key
         */
        String RONGHE_ORDER_STANDARD_MARKET = "Ronghe:Order:StandardMarketData:";

        String RONGHE_STANDARD_MARKET_PLACE = "Ronghe:StandardMarketData:Place:";

        String RONGHE_STANDARD_MARKET_HEAD = "Ronghe:StandardMarketData:HEAD:";

        String RONGHE_BASKET_MARKET_WINNER_CONFIG = "Ronghe:BasketballWinnerConfig:";
        String RONGHE_FOOT_MARKET_WINNER_CONFIG = "Ronghe:FootballWinnerConfig:";

        String RONGHE_BASKET_EARLY_CONFIG = "Ronghe:BasketballEarlyConfig:";
        String RONGHE_BASKET_ADD_CONFIG = "Ronghe:BasketballAddConfig:";
        String RONGHE_BASKET_HAVE_SEND_A = "Ronghe:BasketballHAVESENDA:";
        //AO 投注项原始赔率缓存key
        String RONGHE_AO_MARKET_ORIGINAL_ODDS = "Ronghe:AoMarketOriginalOdds:";
        String RONGHE_AO_MARKET_ORIGINAL_ODDS2 = "Ronghe:AoMarketOriginalOdds2:";

        //1848赛事玩法 赔率最新更新时间
        String RONGHE_MATCH_CATEGORY_ODDS_UPDATETIME = "Ronghe:matchCategoryOddsUpdateTime:";
        //1848赛事玩法 赔率最新更新时间_跟新时间戳
        String RONGHE_MATCH_CATEGORY_ODDS_UPDATETIME_DATE = "Ronghe:matchCategoryOddsUpdateTimeDateKey:";

        //1852赛事时间戳记录
        String RONGHE_MATCH_1852_TIMESTAMP = "Ronghe:matchLockTimestamp:";
        //1852赛事盘口 赔率最新更新时间
        String RONGHE_MATCH_MARKET_ODDS_UPDATETIME = "Ronghe:matchMarketOddsUpdateTime:";
        //1852赛事盘口 赔率最新更新时间_跟新时间戳
        String RONGHE_MATCH_MARKET_ODDS_UPDATETIME_DATE = "Ronghe:matchMarketOddsUpdateTimeDateKey:";
        //1852赛事玩法盘口 开盘状态下最后一次赔率数据
        String RONGHE_MATCH_MARKET_LAST_ACTIVE_ODDS = "Ronghe:matchMarketLastActiveOdds:";
        //1852赛事玩法盘口 已经关盘状态赔率数据 标准盘口ID/senData
        String RONGHE_MATCH_MARKET_DEA_ODDS = "Ronghe:matchMarketDeaOdds:";
        //1852赛事玩法盘口 已经关盘状态赔率数据_跟新时间戳 
        String RONGHE_MATCH_MARKET_DEA_ODDS_DATE = "Ronghe:matchMarketDeaOddsDateKey:";
        //1852赛事当前阶段
        String RONGHE_MATCH_CURRENT_PERIODID = "Ronghe:matchCurrentPeriodId:";
        //1852 拒接关封 配置全量数据
        String RONGHE_CONFIG_MATCH_STATUS_DATA = "Ronghe:ConfigMatchStatusData:";
        //1852 赛事盘口锁
        String RONGHE_MATCH_MARKET_LOCK = "Ronghe:matchMarketLock:";
        //35705 记录全场强开标识
        String RONGHE_MATCH_CATEGORY_TAG = "Ronghe:matchCategoryTag:";
        //2249 记录让分跳动后的盘口值
        String RONGHE_HEAD_HANDICAP_ADD1 = "Ronghe:HEAD:HANDICAP:ADD1:";
        //39924 缓存AO三方赔率
        String RONGHE_AO_THIRD_MARKET_ODDS = "Ronghe:ao:third:market:odds:";
        //39924 计算出赔率value
        String RONGHE_MATCH_PRE_VALUE = "Ronghe:match:pre:value:";
        //篮球事件自动开盘
        String RONGHE_AUTO_OPEN_MARKET_CATEGORY = "Ronghe:autoOpenMarketCategory:";
        //缓存操盘模式支持的子玩法
        String RONGHE_CHILD_MARKET_CATEGORY_ID = "Ronghe:childMarketCategoryId:";
        //缓存操盘模式支持的子玩法 的M模式盘口
        String RONGHE_CHILD_MARKET_M_CATEGORY = "Ronghe:childMarketMCategory:";
        //缓存赔率分组开关
        String RONGHE_TRAD_CONFIG = "Ronghe:ODDS_TRAD_CONFIG";
        //事件提供主客队比分
        String STANDARD_MATCH_SCORES = "STANDARD_MATCH_SCORES:";
        String FOOTBALL_STANDARD_MATCH_SCORES = "FOOTBALL_STANDARD_MATCH_SCORES:";
        // 比分中心提供比分
        String SCORE_CENTER_SCORES = "ABSCORES:";
        //接入数据切换数据源
        String THIRD_MATCH_WITCH_DATA_SOURCE_KEY = "Ronghe:thirdMatchwitchDataSourceKey:";

        //服务器ip Map<ip,Worker>
        String RONGE_SERVE_IP = "Ronghe:serveIp::";
        //2868 赛事级别
        String RONGE_MATCH_ODDS_LINKAGE_CONFIG = "Ronghe:match_odds_linkage_config_";
        //2868 投注项
        String RONGE_ODDS_LINKAGE_CONFIG = "Ronghe:odds_linkage_config_";
        //2868 自动水差
        String RONGE_MARKET_ODDS_AUTO_DIFF = "Ronghe:market_odds_auto_diff_";
        /**
         * 3484
         * 玩法下盘口全封或全关状态定时处理
         */
        String STANDARD_CATEGORY_TIMING_PROCESSING = "Ronghe:standardCategoryTimingProcessing:";
        /**
         * 3484
         * 存放赛事id
         */
        String STANDARD_CATEGORY_TIMING_MATCHIDS = "Ronghe:standard_category_timing_match_ids";
        /**
         * 优惠盘口赔率配置
         */
        String RONGHE_DISCOUNT_ODDS_CONFIG = "Ronghe:discountOddsConfig:";
        /**
         * 内部数据源
         */
        String RONGHE_INTERNAL_CODE = "Ronghe:internalCode:";
        /**
         * 内部当前使用数据源
         */
        String RONGHE_CURRENT_INTERNAL_CODE = "Ronghe:currentinternalCode:";
        /**
         * 带x玩法自动关盘
         */
        String MATCH_EVENT_MARKET_X_CLOSE = "Ronghe:matchEventMarketXClose:";
        /**
         * 接收篮球独赢独赢原始赔率限制
         */
        String RCS_BASKETBALL_ORIGINAL_ODDS_LIMIT   = "Ronghe:rcsBasketballOriginalOddsLimit";

        /**
         * xts 切换标识  赛事 _早滚标识
         */
        String RONGHE_XTS_MATCH_AUTO_SWITCH = "Ronghe:xtsMatchAutoSwitch:";
        /**
         * 玩法中途下架
         */
        String RONGHE_STANDARD_MATCH_CATEGORY_REMOVED = "Ronghe:standardMatchCategoryRemoved:";

        //---------------------------PD报球板---------------------------
        /**
         * 报球板足球主客队及公共事件时间key
         */
        String RONGHE_PD_FOOTBALL_PUBLIC_EVENT = "footballPdPublicEvent:";

        /**
         * 缓存已操作的三方赛事信息key，用于定时任务,主客队超过5分钟未操作，更改主客队事件为公共事件2
         */
        String RONGHE_PD_FOOTBALL_THIRDMATCHID_MAP = "pd_thirdMatchId_map";

        /**
         * 报球板进球后开球key
         */
        String RONGHE_PD_FOOTBALL_KICK_OFF = "kick_off:";

        //---------------------------PD报球板---------------------------

        //---------------------------A99---------------------------
        String RONGHE_A99_THIRD_MARKET_ODDS_PRE = "Ronghe:a99:thirdMatchMarketOdds:pre:";

        String RONGHE_A99_THIRD_MARKET_ODDS_LIVE = "Ronghe:a99:thirdMatchMarketOdds:live:";

        String RONGHE_A99_PRE_MATCH_IDS = "Ronghe:a99:match:pre:ids";
        String RONGHE_A99_LIVE_MATCH_IDS = "Ronghe:a99:match:live:ids";

        String RONGHE_A99_DATA_SOURCE_WEIGHT = "Ronghe:a99:datasource:weight";

        String RONGHE_A99_CALCULATE_TASK_KEY = "Ronghe:a99:calculate:task:key";

        String RONGHE_A99_PRE_TASK_KEY = "Ronghe:a99:pre:task:key";

        String RONGHE_A99_LIVE_TASK_KEY = "Ronghe:a99:live:task:key";

        String RONGHE_A99_ODDS_CHANGE_DIFFERENCE_PRE = "Ronghe:a99:odds:change:difference:pre:";

        String RONGHE_A99_ODDS_CHANGE_DIFFERENCE_LIVE = "Ronghe:a99:odds:change:difference:live:";

        String RONGHE_A99_ODDS_UNDER_ODDS_VALUE = "Ronghe:a99:odds:under_odds:value:";

        String RONGHE_A99_ODDS_OVER_ODDS_VALUE = "Ronghe:a99:odds:over_odds:value:";

        String RONGHE_A99_HEARTBEAT = "Ronghe:a99:heartbeat";

        String RONGHE_A99_DATA_SOURCE_CAUTION_VALUE = "Ronghe:a99:datasource:caution:value";

        String RONGHE_A99_PUSHED_MARKET_ODDS = "Ronghe:a99:pushed:market:odds";

        String RONGHE_A99_PRE_TASK_CRON = "Ronghe:a99:pre:task:cron";

        String RONGHE_A99_LIVE_TASK_CRON = "Ronghe:a99:live:task:cron";

        //---------------------------A99 End---------------------------
    }

    interface STANDARD_MATCH_SELL {
        interface SELL_STATUS {
            String SOLD = "Sold";
            String STOP_SOLD = "Stop_Sold";
            String APPLY_STOP_SOLD = "Apply_Stop_Sold";
            String EXPECTED_END_SOLD = "Expected_End_Sold";
            String[] BEEN_SOLD_STATUS = {"Sold", "Stop_Sold", "Apply_Stop_Sold", "Expected_End_Sold"};
        }
    }

    interface TRADE_MARKET_CONFIG {
        Integer[] ALLOW_MARKET_STATUS = {0, 1, 2, 11};

        interface ACTIVE {
            Integer ACTIVE = 1;
            Integer UNACTIVE = 0;
        }

        interface LEVEL {
            /*玩法*/
            Integer MARKET_CATEGORY = 1;
            /*联赛*/
            Integer TOURNAMENT = 2;
            /*赛事*/
            Integer MATCH = 3;
            /*盘口*/
            Integer MARKET = 4;
        }

        interface TRADE_TYPE {
            Integer AUTO = 0;
            Integer MANUAL = 1;
            Integer AUTO_PLUS = 2;
            Integer L_MODEL = 3;
            Integer L_NEW = 4;
        }

        interface MARKET_STATUS {
            Integer ACTIVE = 0;
            Integer SUSPENDED = 1;
            Integer DEACTIVATED = 2;
            Integer SETTLED = 3;
            Integer CANCELLED = 4;
            Integer HANDEDOVER = 5;
            Integer LOCK = 11;
            Integer ENDED = 13;//收盘
        }

        interface SOURCE_SYSTEM {
            Integer MATCH_MANAGE = 1;
            Integer TRADER_SYSEM = 2;
            Integer THIRD_DATA_SOURCE = 3;
        }
    }

    interface SPORT_MARKET {
        interface STATUS {
            Integer ACTIVE = 0;
            Integer SUSPENDED = 1;
            Integer DEACTIVATED = 2;
            Integer SETTLED = 3;
            Integer CANCELLED = 4;
            Integer HANDEDOVER = 5;
            Integer LOCK = 11;
            Integer LOSE = 12;//操盘后台弃用状态
            Integer ENDED = 13;//收盘
        }
        //0-启用 1-停用 2-删除
        interface DISCOUNT_STATUS{
            Integer ACTIVE = 0;
            Integer SUSPENDED = 1;
            Integer DEACTIVATED = 2;
        }

        interface ODDS_STATUS {
            Integer UNACTIVE = 0;//非激活
            Integer ACTIVE = 1;//激活
            Integer SUSPENDED = 2;//投注项封盘
            Integer DEACTIVATED = 3;//投注项关盘
        }

        interface MARKET_TYPE {
            Integer OUTRIGHT_BUSINESS = 2;
            Integer PRE_MATCH_BUSINESS = 1;
            Integer LIVE_ODD_BUSINESS = 0;
        }
        Map<Integer,Integer> MARKET_STATUS_ORDER_MAP = new HashMap<Integer,Integer>(){{
            put(STATUS.ACTIVE,0);
            put(STATUS.ENDED,1);
            put(STATUS.LOCK,2);
            put(STATUS.SUSPENDED,3);
            put(STATUS.LOSE,4);
            put(STATUS.SETTLED,5);
            put(STATUS.CANCELLED,6);
            put(STATUS.HANDEDOVER,7);
            put(STATUS.DEACTIVATED,8);
        }};
        Map<Integer,Integer> MARKET_STATUS_RESULT_MAP = new HashMap<Integer,Integer>(){{
            put(0,STATUS.ACTIVE);
            put(1,STATUS.ENDED);
            put(2,STATUS.LOCK);
            put(3,STATUS.SUSPENDED);
            put(4,STATUS.LOSE);
            put(5,STATUS.SETTLED);
            put(6,STATUS.CANCELLED);
            put(7,STATUS.HANDEDOVER);
            put(8,STATUS.DEACTIVATED);
        }};

        Map<Integer, Integer> MARKET_STATUS_ORDER2_MAP = new HashMap<Integer, Integer>() {{
            put(STATUS.ACTIVE, 0);
            put(STATUS.LOCK, 1);
            put(STATUS.SUSPENDED, 2);
            put(STATUS.LOSE, 3);
            put(STATUS.SETTLED, 4);
            put(STATUS.CANCELLED, 5);
            put(STATUS.HANDEDOVER, 6);
            put(STATUS.DEACTIVATED, 7);
            put(STATUS.ENDED, 8);
        }};
        Map<Integer, Integer> MARKET_STATUS_RESULT2_MAP = new HashMap<Integer, Integer>() {{
            put(0, STATUS.ACTIVE);
            put(1, STATUS.LOCK);
            put(2, STATUS.SUSPENDED);
            put(3, STATUS.LOSE);
            put(4, STATUS.SETTLED);
            put(5, STATUS.CANCELLED);
            put(6, STATUS.HANDEDOVER);
            put(7, STATUS.DEACTIVATED);
            put(8, STATUS.ENDED);
        }};
    }

    interface SPORT_MARKET_CATEGORY {  //赛种玩法
        interface STATUS {
            Integer INVALID = 0;  //无效
            Integer EFFICIENT = 1; //有效
        }
    }

    interface STANDARD_MATCH {
        interface OPERATOR_MATCH_STATUS {
            Integer UNOPEN = -1;// 未开盘;0 -》-1
            Integer ACTIVE = 0;// 开盘;  1 -》0
            Integer SUSPENDED = 1;// 封盘; 3 - 》1
            Integer DEACTIVATED = 2;//关盘;
            Integer LOCK = 11;
            Integer ENDED = 13;//收盘
        }

        interface PRE_MATCH_BUSINESS {
            Integer SUPPORT = 1;
            Integer NO_SUPPORT = 0;
        }

        interface LIVE_ODD_BUSINESS {
            Integer SUPPORT = 1;
            Integer NO_SUPPORT = 0;
        }
    }

    interface MARKET_MARGIN {
        interface TIME_FRAME_VALUE {
            Integer M30 = 30 * 60 * 1000;
            Integer H1 = 60 * 60 * 1000;
            Integer H12 = 12 * 60 * 60 * 1000;
            Integer H24 = 24 * 60 * 60 * 1000;
            Integer LIVE = 0;
        }

        interface TIME_FRAME_NAME {
            Integer M30 = 30;
            Integer H1 = 60;
            Integer H12 = 12;
            Integer H24 = 24;
            Integer LIVE = 0;
        }
    }

    interface INIT_TYPE {
        String NORMAL = "normal";
        String DIRECT_SOLD = "directSold";
    }

    interface OUTRIGHT_TYPE {
        /*三方冠军赛事*/
        Integer THRIH_OUTRIGHT = 1;
        /*标准冠军赛事*/
        Integer STANDARD_OUTRIGHT = 2;

    }

    interface CONFIG_MARKET_ODDS_STATUS {
    	Integer CLOSE = 0;
    	Integer OPEN = 1;
    }

    interface CONFIG_MATCH_STATUS {
    	Integer CLOSE = 0;
    	Integer OPEN = 1;
    }


    //====================RocketMq常量开始=========================
    /**
     * 回滚取消注单消息队列
     */
    String STANDARD_BET_CANCEL_ROLLBACK = "STANDARD_BET_CANCEL_ROLLBACK";

    /**
     * 取消注单消息队列
     */
    String STANDARD_BET_CANCEL = "STANDARD_BET_CANCEL";

    /**
     * 取消结算消息队列
     */
    String STANDARD_BET_SETTLEMENT_ROLLBACK = "STANDARD_BET_SETTLEMENT_ROLLBACK";

    //====================RocketMq常量结束=========================

    /**
     * 赛事状态常量值
     */
    public static final String MATCH_STATUS = "matchStatus";

    /**
     * 分隔符
     */
    public static final String STR_SEPARATION = "_";

    /*** 接入上游上游rollback bet settlement数据*/
    public static final String PUT_BET_SETTLEMENT_ROLLBACK = "putBetSettlementRollback";
    /*** 接入上游上游rollback bet cancel数据 */
    public static final String PUT_BET_CANCEL_ROLLBACK = "putBetCancelRollback";
    /*** 接入上游bet-cancel事件数据*/
    public static final String PUT_BET_CANCEL = "putBetCancel";

    /**
     * 自动关盘标识
     */
    public static final String AUTO_CLOSE = "autoClose";
    /**
     *  赛事中途切PD,当场赛事之前的数据源不参与结算
     */
    public static final String MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE="MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE:";

    /**
     * 伤补时间
     */
    String MATCH_FOOTBALL_INJURY = "MATCH_FOOTBALL_INJURY_TIME:";
    /**
     * 时间状态
     */
    String MATCH_FOOTBALL_TIME_STATUS = "MATCH_FOOTBALL_TIME_STATUS:";
    /**
     * 足球报球板时间暂停
     */
    String MATCH_FOOTBALL_TIME_STOP = "MATCH_FOOTBALL_TIME_STOP:";


    /**
     * 足球PD报球板事件监控key
     */
    public final static String PD_FOOTBALL_EVENT_MONITOR = "PD_FOOTBALL_EVENT_MONITOR";

    /**
     * 数据源 内部数据源
     */
    public static Map<String, List<String>> DATA_SOURCE_CODE_INTERNAL = new HashMap<String, List<String>>() {{
        put("LS", Lists.newArrayList("LS-Bet365", "LS-1XBet", "LS-188Bet", "LS-Fonbet", "LS-Marath"));
        put("L02", Lists.newArrayList("L02-Bet365", "L02-1XBet", "L02-188Bet", "L02-Fonbet", "L02-Marath", "L02-12Bet"));
        put("TX", Lists.newArrayList("T01-IBCbet", "T01-188bet"));
    }};

}
