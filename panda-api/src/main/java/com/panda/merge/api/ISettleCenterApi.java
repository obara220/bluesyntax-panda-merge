package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchSettleInfo;


import java.util.List;
import java.util.Map;

/**
 *  结算中心2.0
 *  idol
 *  2022-2-18 16:25:30
 * */
public interface ISettleCenterApi {


    /**
     *  赛事冻结
     * 传参：
     * 返回:
     * */
    Response MatchFreeze(MatchFreezeDto matchFreezeDto);

    /**
     *  比分阶段冻结
     * 传参：
     * 返回:
     * */
    Response ScoresPeriodFreeze(ScoresPeriodFreezeDto freezeDto);

    /**
     *  比分次序冻结(事件冻结)
     * 传参：
     * 返回:
     * */
    Response ScoresPeriodOrderFreeze(ScoresPeriodOrderFreezeDto freezeDto);


    /**
     *  结算2.0切换 下发数据到结算服务
     * 传参：
     * 返回:
     * */
    Response settleSwitcher(MatchSettleSwitcherDto matchSettleSwitcherDto);

    /**
     *  进球类型
     * 传参：
     * 返回:
     * */
    Response goalType(String linkId, Long sportId);
    /**
     *  球员查询
     * 传参：
     * 返回:
     * */
    Response goalPlayer(String linkId, Long sportId, Long matchId);


    /**
     * 获取系统级数据商结算状态
     */
    Response getGlobalAutoSettleStatus();

    /**
     * 更改系统级数据商结算状态
     * @param isEnableAutoSettle 是否打开
     * @param userName 用户名
     * @param ipAddress ip地址
     * @return
     */
    Response changeGlobalAutoSettleStatus(Boolean isEnableAutoSettle,
                                      String userName,String ipAddress);

    /**
     * 更改赛事级数据商结算状态
     * @param standardMatchId 标准赛事id
     * @param isEnableAutoSettle 是否打开
     * @param userName 用户名
     * @param ipAddress ip地址
     * @return
     */
    Response changeMatchAutoSettleStatus(String standardMatchId,Boolean isEnableAutoSettle,
                                     String userName,String ipAddress);
    /**
     * 更改赛事级玩法数据商结算状态
     * @param standardMatchId 标准赛事id
     * @param isEnableAutoSettle 是否打开
     * @param userName 用户名
     * @param ipAddress ip地址
     * @return
     */
    Response changeEventTypeAutoSettleStatus(String type,Long standardMatchId,Boolean isEnableAutoSettle,
                                         String userName,String ipAddress);

    /**
     * 获取系统级结算同步比分中心状态
     * @return
     */
    Response getGlobalSyncScoresStatus();

    /**
     * 更改系统级结算同步比分中心状态
     * @param isEnableSyncScores 是否打开
     * @param userName 用户名
     * @param ipAddress ip地址
     */
    Response changeGlobalSyncScoresStatus(Boolean isEnableSyncScores,
                                      String userName,String ipAddress);


    /**
     * 异常结算
     * @param list  需要结算的信息

     */
    Response matchSettleAbnormal(List<SettleMatchScoreDto> list);


    /**
     * 结算顺序修改
     */
    Response setSettleOrderClosed(MatchSettleOrderClosedDTO dto);

    /**
     * 五分钟玩法开关修改
     */
    Response setFiveMinSwitch(MatchSettleFiveMinSwitchDTO dto);

    /**
     * 设置数据商灰色区间
     * @param grayIntervalDtoList
     * @return
     */
    Response setDataSourceGrayInterval(List<DataSourceGrayIntervalDto> grayIntervalDtoList);

    /**
     * 根据联赛等级查询数据商的灰色区间列表
     * @param dto
     * @return
     */
    Response getGrayIntervalByTournamentLevel(DataSourceGrayIntervalDto dto);

    /**
     * 根据球种类型获取对于的结算数据源的开关
     * @param matchSettleDataSourceDto
     * @return
     */
    Response getMatchSettleDataSources(MatchSettleDataSourceDto matchSettleDataSourceDto);

    /**
     * 根据球种类型和联赛等级,设置联赛等级对应的结算数据源的开关列表
     * @param matchSettleDataSourceDto
     * @return
     */
    Response setLeagueMatchSettleDataSource(MatchSettleDataSourceDto matchSettleDataSourceDto);
    /**
     * 查询篮球即时结算的结束时间限制
     * */
    Response getBasketInSettleTimeLimit(Long sportId);
    /**
     * 设置篮球即时结算的结束时间限制
     * */
    Response setBasketInSettleTimeLimit(SettleTimeLimitDto dto);

    /**
     *根据球种类型获取对应的结算数据源的权重及开关
     * @param matchSettleDataSourceWeightDto
     * @return
     */
    Response getMatchSettleDataSourcesWeight(MatchSettleDataSourceWeightDto matchSettleDataSourceWeightDto);


    Response refreshMatchSettleInfo(MatchSettleInfo info);

    Response<Map<String, Object>> getRedisInfo(List<String> keys, String flag);

    /**
     * 扫描所有正在进行的赛事，检查数据商连接状态并返回需要推送的状态列表
     * @return 需要推送的连接状态列表（只返回状态改变的数据）
     */
    Response<List<DataSourceConnectionStatusDto>> scanAllMatchesConnectionStatus();

    /**
     * 根据标准赛事ID从Redis读取数据商连接状态（不重新计算）
     * @param standardMatchId 标准赛事ID
     * @return 数据商连接状态
     */
    Response<DataSourceConnectionStatusDto> scanMatchConnectionStatus(Long standardMatchId);
}
