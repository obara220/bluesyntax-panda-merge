package com.panda.merge.api;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.odds.*;
import com.panda.merge.model.ConfigMarketDisplayTrade;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/13 <br>
 * @see com.panda.merge.api <br>
 */

public interface ITradeMarketConfigApi {
    /**
     * 操盘-手自动切换（玩法）及赛事开关封锁（数据商/操盘平台）
     * @param message
     * @return
     */
    Response putTradeMarketConfig(Request<TradeMarketConfigDTO> message);


    /**
     * 滚球操盘-自动水差配置-足球
     * @param message
     * @return
     */
    Response putTradeMarketAutoDiffConfig(Request<TradeMarketAutoDiffConfigDTO> message);

    /**
     * 滚球操盘-自动水差配置-篮球
     * oddstype 取值：2，Under，Even（产品定的）
     * @param message
     * @return
     */
    Response putTradePlaceNumAutoDiffConfig(Request<TradePlaceNumAutoDiffConfigDTO> message);

    /**
     * 操盘-盘口展示配置
     * @param message
     * @return
     */
    Response putTradeMarketDisplayConfig(Request<TradeMarketDisplayConfigDTO> message);

    /**
     * 修改操盘平台
     *
     * @param updateRiskManagerCodeDTORequest
     * @return
     */
    Response updateRiskManagerCode(Request<RiskManagerCodeDTO> updateRiskManagerCodeDTORequest);

    /**
     * 操盘-margin配置
     * @param message
     * @return
     */
    Response putTradeMarketMarginConfig(Request<TradeMarketMarginConfigDTO> message);

    /**
     * 操盘-margin配置（支持同一赛事多个玩法）
     * @param message
     * @return
     */
    Response putTradeMarketMarginConfigList(Request<List<TradeMarketMarginConfigDTO>> message);

    /**
     * 玩法盘口开关封锁状态控制
     * @param message
     * @return
     */
    Response putTradeMarketPlaceConfig(Request<TradeMarketPlaceConfigDTO> message);

    /**
     * 操盘弹窗调价界面调用接口
     * 操盘-盘口位置开关封锁配置
     * 操盘-margin配置
     * 操盘-最大最小值
     * 操盘-水差设置
     * @param message
     * @return
     */
    Response putTradeMarketUiConfig(Request<TradeMarketUiConfigDTO> message);


    /**
     * 获取盘口展示配置
     * @param matchId
     * @return
     */
    ConfigMarketDisplayTrade getConfigMarketDisplayTrade(Long matchId);

    /**
     * 自动关盘
     *
     * @param linkId            linkID
     * @param standardMatchId   标准比赛ID
     * @param marketCategoryIds 需要关盘的标准玩法ID List
     * @param dataSourceTime
     */
    void autoCloseMarket(String linkId, Long standardMatchId, Set<Long> marketCategoryIds, Long dataSourceTime);

    /**
     * 自动开盘
     *
     * @param linkId            linkID
     * @param standardMatchId   标准比赛ID
     * @param marketCategoryIds 需要关盘的标准玩法ID List
     * @param dataSourceTime
     */
    void autoOpenMarket(String linkId, Long standardMatchId, Set<Long> marketCategoryIds, Long dataSourceTime);


    /**
     * 自动关盘子玩法
     * @param linkId
     * @param standardMatchId
     * @param map
     * @param dataSourceTime
     */
    void autoCloseChildMarketCategory(String linkId, Long standardMatchId, Pair<Set<Long>, Map<String, JSONObject>> map, Long dataSourceTime);

    /**
     * 操盘-盘口差配置
     * @param message
     * @return
     */
    Response putTradeMarketHeadGapConfig(Request<TradeMarketHeadGapConfigDTO> message);

    /**
     * 操盘-盘口开启跟弃用
     * @param message
     * @return
     */
    Response putTradeMarketStatusConfig(Request<TradeMarketStatusConfigDTO> message);

    /**
     * 操盘-检查是否需要关盘
     * @param message
     * @return
     */
    Response checkChangeOpeartor(Request<TradeCloseOpeartorDTO> message);

    /**
     * 自动关盘 - 清除缓存玩法已经自动关盘
     *
     * @param standardMatchId  标准比赛ID
     * @param marketCategoryId 标准玩法ID
     */
    Response clearCacheMarketCategoryId(Long standardMatchId, Long marketCategoryId);
    /**
     * 自动关盘 - 清除缓存玩法已经自动关盘
     *
     * @param standardMatchId 标准比赛ID
     * @param period          阶段
     */
    Response clearCacheMarketCategoryIdByPeriod(Long standardMatchId, Integer period);

    /**
     * 操盘-检查是否能切MTS
     * @param message
     * @return
     */
    Response checkChangeMTS(Request<TradeCloseOpeartorDTO> message);

    /**
     * 操盘-检查是否能切GTS
     * @param message
     * @return
     */
    Response checkChangeGTS(Request<TradeCloseOpeartorDTO> message);

    /**
     * 操盘-检查是否能切OTS
     * @param message
     * @return
     */
    Response checkChangeOTS(Request<TradeCloseOpeartorDTO> message);

    /**
     * 操盘-检查是否能切OTS
     * @param message
     * @return
     */
    Response checkChangeCTS(Request<TradeCloseOpeartorDTO> message);
    /**
     * 操盘-按照运动类型，赛事id，清理水差
     * @param message
     * @return
     */
    Response clearDiffValue(Request<TradeClearDiffValueDTO> message);

    /**
     * 自动关盘 查询缓存玩法已经自动关盘
     *
     * @param standardMatchId standardMatchId
     */
    Response queryAutoCloseMarket(Long standardMatchId);

    /**
     * 编辑标准玩法 - 清除玩法缓存
     * @param marketCategoryId
     * @param sportId
     * @return
     */
    Response editCategoryClearCache(Long marketCategoryId, Long sportId,Integer status);

    /**
     * 修改玩法数据源权重
     *
     * @param updateMarketCategoryDataSourceCodeRequest
     * @return
     */
    Response updateMarketCategoryDataSourceCode(Request<List<UpdateMarketCategoryDataSourceCodeDTO>> updateMarketCategoryDataSourceCodeRequest);


    /**
     * 修改比赛滚球标识
     *
     * @param standardMatchId
     * @return
     */
    Response upMatchOddsLiveStatus(Long standardMatchId);

    /**
     * 修改比赛时间判断是否去除滚球标识
     *
     * @param standardMatchId
     * @return
     */
    Response upMatchBeginTimesOddsLiveStatus(Long standardMatchId,Long beginTime,Long oldBeginTime);

    /**
     * margin 优化配置接口
     *
     * @param message
     * @return
     */
    Response putTradeMarketMarginGapConfig(Request<TradeMarketMarginGapConfigDTO> message);

    /**
     * 赛程后台查询赛事有赔率的玩法列表
     * @param matchIds
     * @return
     */
    Map<Long,List<Long>> getCategoryByMatchId(List<Long> matchIds);

    /**
     * 综合盘 联赛维度 赔率最大最小配置
     */
    Response putConfigTournamentTradeItem(Request<ConfigTournamentTradeItemDTO> request);

    /**
     * 切换玩法操盘方式
     * @param request
     * @return
     */
    Response putTradeTypeConfig(Request<UpdateTradeTypeDTO> request);

    /**
     * 玩法级状态挡板接口
     */
    Response putCategoryStatusConfig(Request<PlaySetStatusConfigDTO> request);

    /**
     * 获取缓存中的所有盘口，赛前数据商和滚球数据商
     * @param linkId
     * @param standardMatchInfo
     * @param standardSportMarketSell
     * @return
     */
    Map<String, StandardMarketDataMessage> getStringStandardMarketDataMessageMap(Set<Long> marketCategoryIds,
                                                                                 String linkId, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell);

    /**
     * 1852封关盘/接拒2.0开关
     */
    Response putMarketTwoStatusConfig(Request<MarketTwoStatusConfigDTO> request);
    
    /**
     * 1852 进球事件触发关盘
     * @param linkId
     * @param standardMatchId
     * @param dataSourceTime
     */
    void autoCloseOldMarket(String linkId, Long standardMatchId, Long dataSourceTime);

    /**
     * 紧急工具-赛事级开关盘操作
     *
     * @param matchId  赛事id、可为标准赛事id或赛事管理id
     * @param marketStatus 赛事级别盘口状态
     */
    Response<Boolean> emergencyOperationOfMarket(Long matchId, Integer marketStatus);

    Response putDiscountOddsConfig(Request<List<DiscountOddsConfigDTO>> requests);

    /**
     * 2785 提供风控查询Ls，Tx查询三方赛事支持站点信息dubbo接口
     * @param request
     * @return
     */
    Response<StandardCategoryDataSourceCodeDTO>  getStandardCategoryDataSourceCode(Request<Long> request);

    /**
     * 查询一定时间内有数据下发的数据源
     *
     * @param request
     * @return
     */
    Response<MatchCategoryDataSourcesDTO> getStandardCategoryAvailableDataSources(Request<MatchCategoryDataSourcesDTO> request);

    Response<List<ResDiscountOddsDTO>> checkDisCountOdds(Request<List<CheckDiscountOddsDTO>> request);

    /**
     * 自动切换数据源
     *
     * @param request
     * @return
     */
    Response<MatchDataSourceDTO> autoSwitchDataSource(Request<MatchDataSourceDTO> request);

    /**
     * 98386
     *
     * @param request
     * @return
     */
    Response basketballWinnerConfig(Request<List<BasketballConfigDTO>> request);

    /**
     * 篮球早转滚联赛开关配置
     *
     * @param request
     * @return
     */
    Response basketballEarlyRollLeagueSwitchConfig(Request<List<LeagueSwitchConfigDTO>> request);

    /**
     * 篮球早转滚联赛开关配置
     *
     * @param request
     * @return
     */
    Response tournamentLevelChange(Request<TournamentLevelChangeDTO> request);

    /**
     * 98387
     *
     * @param request
     * @return
     */
    Response footballWinnerConfig(Request<FootballConfigDTO> request);


    /**
     * 根据赛事id，玩法id集合获取三方盘口最后更新时间
     * @param request
     */

    Response<Map<String, ThirdMarketModifytimeDTO>> getThirdMarletLastModifyTime(Request<ThirdMarketLastModifyTimeDTO> request);
}
