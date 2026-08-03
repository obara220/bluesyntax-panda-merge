package com.panda.merge.v2.dubbo;

import com.panda.merge.api.ISettleCenterApi;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchSettleInfo;
import com.panda.merge.v2.controllerv2.MatchSettleCenterController;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 结算2.0 dubbo服务
 */
@Service
@DubboService
@Slf4j
public class MatchSettleCenterApiImpl implements ISettleCenterApi {

    @Autowired
    private MatchSettleCenterController matchSettleCenterController;

    @Override
    public Response MatchFreeze(MatchFreezeDto matchFreezeDto) {
        return matchSettleCenterController.MatchFreeze(matchFreezeDto);
    }

    @Override
    public Response ScoresPeriodFreeze(ScoresPeriodFreezeDto scoresPeriodFreezeDto) {
        return matchSettleCenterController.ScoresPeriodFreeze(scoresPeriodFreezeDto);
    }

    @Override
    public Response ScoresPeriodOrderFreeze(ScoresPeriodOrderFreezeDto freezeDto) {
        return matchSettleCenterController.ScoresPeriodOrderFreeze(freezeDto);
    }

    @Override
    public Response settleSwitcher(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        return matchSettleCenterController.settleSwitcher(matchSettleSwitcherDto);
    }

    @Override
    public Response goalType(String linkId, Long sportId) {
        return matchSettleCenterController.goalType(linkId, sportId);
    }

    @Override
    public Response goalPlayer(String linkId, Long sportId, Long matchId) {
        return matchSettleCenterController.goalPlayer(linkId, sportId, matchId);
    }


    @Override
    public Response getGlobalAutoSettleStatus() {
        return matchSettleCenterController.getGlobalAutoSettleStatus();
    }

    @Override
    public Response changeGlobalAutoSettleStatus(Boolean isEnableAutoSettle, String userName, String ipAddress) {
        return matchSettleCenterController.changeGlobalAutoSettleStatus(isEnableAutoSettle, userName, ipAddress);
    }


    @Override
    public Response changeMatchAutoSettleStatus(String standardMatchId, Boolean isEnableAutoSettle, String userName, String ipAddress) {
        return matchSettleCenterController.changeMatchAutoSettleStatus(standardMatchId, isEnableAutoSettle, userName, ipAddress);
    }

    @Override
    public Response changeEventTypeAutoSettleStatus(String type, Long standardMatchId, Boolean isEnableAutoSettle, String userName, String ipAddress) {
        return matchSettleCenterController.changeEventTypeAutoSettleStatus(type, standardMatchId, isEnableAutoSettle, userName, ipAddress);
    }


    @Override
    public Response getGlobalSyncScoresStatus() {
        return matchSettleCenterController.getGlobalSyncScoresStatus();
    }

    @Override
    public Response changeGlobalSyncScoresStatus(Boolean isEnableSyncScores, String userName, String ipAddress) {
        return matchSettleCenterController.changeGlobalSyncScoresStatus(isEnableSyncScores, userName, ipAddress);
    }

    @Override
    public Response matchSettleAbnormal(List<SettleMatchScoreDto> list) {
        return matchSettleCenterController.matchSettleAbnormal(list);
    }

    @Override
    public Response setSettleOrderClosed(MatchSettleOrderClosedDTO dto) {
        return matchSettleCenterController.setSettleOrderClosed(dto);
    }


    @Override
    public Response setFiveMinSwitch(MatchSettleFiveMinSwitchDTO dto) {
        return matchSettleCenterController.setFiveMinSwitch(dto);
    }

    @Override
    public Response setDataSourceGrayInterval(List<DataSourceGrayIntervalDto> grayIntervalDtoList) {
        return matchSettleCenterController.setDataSourceGrayInterval(grayIntervalDtoList);
    }

    @Override
    public Response getGrayIntervalByTournamentLevel(DataSourceGrayIntervalDto dto) {
        return matchSettleCenterController.getGrayIntervalByTournamentLevel(dto);
    }

    /**
     * 根据参数SportId,球种类型，获取对应的联赛等级数据源的开关列表
     *
     * @param matchSettleDataSourceDto
     * @return
     */
    @Override
    public Response getMatchSettleDataSources(MatchSettleDataSourceDto matchSettleDataSourceDto) {
        return matchSettleCenterController.getMatchSettleDataSources(matchSettleDataSourceDto);
    }

    /**
     * 根据球种类型和联赛等级,设置联赛等级对应的结算数据源的开关列表
     *
     * @param matchSettleDataSourceDto
     * @return
     */
    @Override
    public Response setLeagueMatchSettleDataSource(MatchSettleDataSourceDto matchSettleDataSourceDto) {
        return matchSettleCenterController.setLeagueMatchSettleDataSource(matchSettleDataSourceDto);
    }

    @Override
    public Response getBasketInSettleTimeLimit(Long sportId) {
        return matchSettleCenterController.getBasketInSettleTimeLimit(sportId);
    }

    @Override
    public Response setBasketInSettleTimeLimit(SettleTimeLimitDto dto) {
        return matchSettleCenterController.setBasketInSettleTimeLimit(dto);
    }

    /**
     * 根据参数SportId,球种类型，获取对应的联赛等级数据源的权重及开关列表
     *
     * @param matchSettleDataSourceWeightDto
     * @return
     */
    @Override
    public Response getMatchSettleDataSourcesWeight(MatchSettleDataSourceWeightDto matchSettleDataSourceWeightDto) {
        return matchSettleCenterController.getMatchSettleDataSourcesWeight(matchSettleDataSourceWeightDto);
    }

    @Override
    public Response refreshMatchSettleInfo(MatchSettleInfo info) {
        return matchSettleCenterController.refreshMatchSettleInfo(info);
    }

    @Override
    public Response<Map<String, Object>> getRedisInfo(List<String> keys, String flag) {
        return matchSettleCenterController.getRedisInfo(keys, flag);
    }

    @Override
    public Response<List<DataSourceConnectionStatusDto>> scanAllMatchesConnectionStatus() {
        return matchSettleCenterController.scanAllMatchesConnectionStatus();
    }

    /**
     * 根据标准赛事ID从Redis读取数据商连接状态（不重新计算）
     * @param standardMatchId 标准赛事ID
     * @return 数据商连接状态
     */
    public Response<DataSourceConnectionStatusDto> scanMatchConnectionStatus(Long standardMatchId) {
        return matchSettleCenterController.scanMatchConnectionStatus(standardMatchId);
    }

}
