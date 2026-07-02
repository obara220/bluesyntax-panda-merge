package com.panda.merge.odds.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.message.PreSoldFirstOddsMessage;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportTournament;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.PreSoldReportMessageProducer;
import com.panda.merge.service.StandardSportTournamentService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdSportMarketNewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.CACHE_KEY_PRE_SOLD_REPORT;

/**
 * OperationReportService
 *
 * @description: 预售告警服务
 * @date: 1/24/2025
 **/
@Slf4j
@Service
public class PreSoldReportService {


    /**
     * 告警球种范围
     **/
    private static final List<Long> reportSportIds = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

    @NacosValue(value = "${report.operation.firstOddsAfterPresale:true}", autoRefreshed = true)
    private Boolean isEnabled;

    @Autowired
    private StandardSportTournamentService standardSportTournamentService;

    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PreSoldReportMessageProducer preSoldReportMessageProducer;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    /**
     * 预售告警 预售赛事未开售有三方盘口下发时触发告警
     *
     * @param thirdMarkets               三方盘口
     * @param standardSportMarketSellMap 预售map
     */
    @Async("reportExecutor")
    public void report(List<OddsWrapper<ThirdMarketDTO>> thirdMarkets,
                       Map<Long, StandardSportMarketSell> standardSportMarketSellMap) {
        if (!isEnabled || CollectionUtils.isEmpty(thirdMarkets) || MapUtils.isEmpty(standardSportMarketSellMap)) {
            return;
        }

        Map<Long, OddsWrapper<ThirdMarketDTO>> matchMarketMap = thirdMarkets.stream().filter(wrapper -> {
            if (Objects.isNull(wrapper.getStandardSourceId()) ||
                    (Objects.nonNull(wrapper.getIsOutRight()) && wrapper.getIsOutRight())) {
                return false;
            }
            if (standardSportMarketSellMap.containsKey(wrapper.getStandardSourceId())) {
                StandardSportMarketSell standardSportMarketSell =
                        standardSportMarketSellMap.get(wrapper.getStandardSourceId());
                if (!reportSportIds.contains(standardSportMarketSell.getSportId())) {
                    return false;
                }
                return checkPreSoldUnsoldStatus(standardSportMarketSell);
            }
            return false;

        }).collect(Collectors.toMap(OddsWrapper::getStandardSourceId, Function.identity(), (k1, k2) -> k1));

        if (MapUtils.isEmpty(matchMarketMap)) {
            return;
        }
        List<Long> matchIds = new ArrayList<>(matchMarketMap.keySet());
        List<String> cacheKeys = matchIds.stream().map(this::getCacheKey).collect(Collectors.toList());
        List<Object> caches = redisService.mGet(cacheKeys);
        List<StandardSportMarketSell> reportMarketSells = new ArrayList<>();
        for (int i = 0; i < caches.size(); i++) {
            if (caches.get(i) == null) {
                Long matchId = matchIds.get(i);
                StandardSportMarketSell standardSportMarketSell = standardSportMarketSellMap.get(matchId);
                if (setCacheIfNotExist(standardSportMarketSell)) {
                    reportMarketSells.add(standardSportMarketSell);
                }
            }
        }
        reportPreSaleFirstMarket(reportMarketSells, matchMarketMap);


    }

    /**
     * 预售事件触发 如果预售时已有三方盘口 设置告警状态缓存
     *
     * @param standardSportMarketSell 预售
     */
    public void setCache(StandardSportMarketSell standardSportMarketSell) {
        if (!isEnabled) {
            return;
        }
        Long matchId = standardSportMarketSell.getMatchInfoId();

        List<Long> thirdMatchIds =
                thirdMatchInfoService.getItems(Collections.singletonList(matchId),null).stream().map(
                        ThirdMatchInfo::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(thirdMatchIds)) {
            return;
        }
        Long marketCount = thirdSportMarketService.countByThirdMatchIds(thirdMatchIds);
        if (marketCount > 0) {
            setCacheIfNotExist(standardSportMarketSell);
        }
    }

    public Boolean setCacheIfNotExist(StandardSportMarketSell standardSportMarketSell) {
        return redisService.setIfNotExist(getCacheKey(standardSportMarketSell.getMatchInfoId()),
                                          1,
                                          standardSportMarketSell.getBeginTime() - System.currentTimeMillis() +
                                                  TimeUnit.DAYS.toMillis(1),
                                          TimeUnit.MILLISECONDS);

    }

    private boolean checkPreSoldUnsoldStatus(StandardSportMarketSell standardSportMarketSell) {
        return StringUtils.equalsIgnoreCase(standardSportMarketSell.getStatus(), "Enable") &&
                StringUtils.equalsIgnoreCase(standardSportMarketSell.getPreMatchSellStatus(), "Unsold") &&
                StringUtils.equalsIgnoreCase(standardSportMarketSell.getLiveMatchSellStatus(), "Unsold");
    }

    private void reportPreSaleFirstMarket(List<StandardSportMarketSell> reportMarketSells,
                                          Map<Long, OddsWrapper<ThirdMarketDTO>> matchMarketMap) {
        if (CollectionUtils.isEmpty(reportMarketSells)) {
            return;
        }
        List<Long> tournamentIds = reportMarketSells
                .stream()
                .map(StandardSportMarketSell::getTournamentId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, StandardSportTournament> tournamentMap = standardSportTournamentService
                .getItemsCache(tournamentIds)
                .stream()
                .collect(Collectors.toMap(StandardSportTournament::getId, Function.identity(), (k1, k2) -> k1));
        reportMarketSells
                .stream()
                .filter(marketSell -> Objects.nonNull(marketSell.getTournamentId()) &&
                        Objects.nonNull(tournamentMap.get(marketSell.getTournamentId())))
                .map(marketSell -> {
                    Long matchId = marketSell.getMatchInfoId();
                    StandardSportTournament tournament = tournamentMap.get(marketSell.getTournamentId());
                    PreSoldFirstOddsMessage message = new PreSoldFirstOddsMessage();
                    message.setSourceCode(matchMarketMap.get(matchId).getDataSourceCode());
                    message.setMatchManageId(marketSell.getMatchManageId());
                    message.setMatchInfoId(marketSell.getMatchInfoId());
                    message.setTournamentName(tournament.getName());
                    message.setTournamentNameEn(tournament.getNameSpell());
                    return message;

                })
                .forEach(message -> {
                    preSoldReportMessageProducer.send(message);
                });
    }

    private String getCacheKey(Long matchId) {
        return CACHE_KEY_PRE_SOLD_REPORT + matchId;
    }
}
