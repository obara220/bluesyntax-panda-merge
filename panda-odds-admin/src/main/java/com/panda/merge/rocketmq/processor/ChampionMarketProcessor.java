package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.constant.SaleMatchSellStausEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.merge.dubbo.TradeMarketOddsApiServiceImpl;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@Validated
public class ChampionMarketProcessor extends BaseProcessor {
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Autowired
    private TradeMarketOddsApiServiceImpl tradeMarketOddsApiService;
    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;

    @Autowired
    private StandardSportMarketOddsNewService standardSportMarketOddsService;

    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;

    @Autowired
    private OutrightTradeOddsConfigService outrightTradeOddsConfigService;

    public void processChampionMarketSetting(@Valid Request<StandardMatchMarketDTO> request) {
        log.info("::{}::processChampionMarketSetting操作入参:{}", request.getLinkId(), JSON.toJSONString(request));
        Long standardMatchId = request.getData().getStandardMatchInfoId();
        String linkId = request.getLinkId();
        String dataSourceCode = request.getDataSourceCode();
        // 赛事
        StandardMatchInfoDetail standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(true, standardMatchId );
        if (null == standardMatchInfo) {
            log.info("::{}::processChampionMarketSetting standardMatchId:{},未找到标准赛事", linkId, standardMatchId);
            return;
        }
        // 盘口
        List<StandardMarketDTO> standardMarketDTOList = request.getData().getMarketList();
        if ( CollectionUtils.isEmpty(standardMarketDTOList)) {
            log.info("::{}::processChampionMarketSetting盘口不能为空", linkId);
            return;
        }
        StandardMarketDTO marketDTO = standardMarketDTOList.get(0);
        StandardSportMarket standardSportMarket = standardSportMarketService.getItem(marketDTO.getDataSourceCode(), marketDTO.getThirdMarketSourceId(), standardMatchInfo.getId());
        if (null == standardSportMarket) {
            log.info("::{}::processChampionMarketSetting未找到标准盘口,三方盘口源id:{}", linkId, marketDTO.getThirdMarketSourceId());
            return;
        }
        // 获取当前数据源缓存中所有的盘口
        String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchId + "_" + dataSourceCode;
        log.info("::{}::processChampionMarketSetting marketKey:{} ", linkId, marketKey);
        StandardMarketDataMessage standardMarketDataMessage = new StandardMarketDataMessage();

        // 投注项
        List<StandardMarketOddsDTO> marketOddsList = marketDTO.getMarketOddsList();
        if (CollectionUtils.isEmpty(marketOddsList)) {
            log.info("::{}::processChampionMarketSetting投注项不能为空", linkId);
            return;
        }

        List<Long> filterOddsId = new ArrayList();

        // 优先从缓存中获取投注项 active 状态，替代 DB 查询
        Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(marketKey);
        log.info("::{}::processChampionMarketSetting redisKey:{} cacheSize:{}", linkId, marketKey,
                standardMarketMessageMap == null ? 0 : standardMarketMessageMap.size());
        StandardMarketDataMessage cachedMarketMessage = CollectionUtils.isEmpty(standardMarketMessageMap) ? null
                : standardMarketMessageMap.get(standardSportMarket.getRelationMarketId().toString());
        if (cachedMarketMessage != null && !CollectionUtils.isEmpty(cachedMarketMessage.getMarketOddsList())) {
            // 从缓存的 marketOddsList 中提取 active=0（锁盘）的投注项 id，并过滤掉
            Map<Long, Integer> cacheOddsActiveMap = cachedMarketMessage.getMarketOddsList().stream()
                    .filter(o -> o.getId() != null && o.getActive() != null)
                    .collect(Collectors.toMap(
                            StandardMarketOddsDataMessage::getId,
                            StandardMarketOddsDataMessage::getActive,
                            (existing, replacement) -> existing));
            List<Long> closeOddsIds = cacheOddsActiveMap.entrySet().stream()
                    .filter(e -> Integer.valueOf(0).equals(e.getValue()) || Integer.valueOf(2).equals(e.getValue()) )
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            log.info("::{}::processChampionMarketSetting 缓存命中，封盘投注项ids:{}", linkId, closeOddsIds);
            if (!CollectionUtils.isEmpty(closeOddsIds)) {
                marketOddsList = marketOddsList.stream()
                        .filter(odds -> !closeOddsIds.contains(Long.parseLong(odds.getId())))
                        .collect(Collectors.toList());
                marketDTO.setMarketOddsList(marketOddsList);
                filterOddsId.addAll(closeOddsIds);
            }
        } else {
            // 缓存未命中，fallback 到 DB 查询
            log.info("::{}::processChampionMarketSetting 缓存未命中，fallback到DB查询", linkId);
            List<Long> oddsIds = marketOddsList.stream().map(odds -> Long.parseLong(odds.getId())).collect(Collectors.toList());
            List<ConfigOutrightTradeOdds> configOutrightTradeOdds = outrightTradeOddsConfigService.selectOddsTradeList(oddsIds);
            if (!CollectionUtils.isEmpty(configOutrightTradeOdds)) {
                List<Long> closeOddsIds = configOutrightTradeOdds.stream()
                        .filter(trade -> Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED.equals(trade.getOddsStatus()))
                        .map(ConfigOutrightTradeOdds::getStandardMarketOddsId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(closeOddsIds)) {
                    marketOddsList = marketOddsList.stream()
                            .filter(odds -> !closeOddsIds.contains(Long.parseLong(odds.getId())))
                            .collect(Collectors.toList());
                    marketDTO.setMarketOddsList(marketOddsList);
                    filterOddsId.addAll(closeOddsIds);
                }
            }
        }

        Set<Long> oddsUnActiveList = marketOddsList.stream().filter(odds -> 0== odds.getActive()).map( odds -> Long.parseLong(odds.getId())).collect(Collectors.toSet());
        StandardOutrightMarket standardOutrightMarket = standardOutrightMarketService.selectByExample(standardSportMarket.getRelationMarketId());

        if ( !CollectionUtils.isEmpty(oddsUnActiveList) ) {
            filterOddsId.addAll(oddsUnActiveList);
        }

        List<StandardSportMarketOdds> standardSportMarketOddsList = Lists.newArrayList();
        for (StandardMarketOddsDTO oddsDTO : marketOddsList) {
            if ( !oddsUnActiveList.contains(oddsDTO.getId()) ) {
                // 先清理投注项缓存,再查询
                String key = RedisConfig.REDIS_KEY_DATABASE + "::StandardSportMarketOdds:" + standardSportMarket.getId() + '-' + oddsDTO.getThirdOddsFieldSourceId();
                Boolean delStatus = redisService.del(key);
                log.info("::{}::processChampionMarketSetting的投注项的清理, key:{}, 清理结果:{}", linkId, key, delStatus);
                StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsService.getItem(oddsDTO.getDataSourceCode(), oddsDTO.getThirdOddsFieldSourceId(), standardSportMarket.getId());
                if ( null != standardSportMarketOdds ) {
                    standardSportMarketOddsList.add(standardSportMarketOdds);
                }
            }
        }

        // 将盘口及盘口投注项封装到一起
        standardMarketDataMessage = thirdMatchMarketProcessor.convertToStandardMarketDataMessage(standardSportMarketOddsList, standardSportMarket, TimeUtils.millsSecondsEast8ZoneGmt()-10*1000);
        if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(standardMarketDataMessage.getStatus()) ){
            standardMarketDataMessage.setShowMarketResult(1);
        }
        standardMarketDataMessage.setOrderNo(standardOutrightMarket.getMarketOrderNumber());

        // 盘口时间的变更
        if ( !marketDTO.getAddition1().equals(standardMarketDataMessage.getAddition1())  || !marketDTO.getAddition2().equals(standardMarketDataMessage.getAddition2()) || !marketDTO.getAddition3().equals(standardMarketDataMessage.getAddition3()) ) {
            standardMarketDataMessage.setAddition1(marketDTO.getAddition1());
            standardMarketDataMessage.setAddition2(marketDTO.getAddition2());
            standardMarketDataMessage.setAddition3(marketDTO.getAddition3());
        }
        log.info("::{}::processChampionMarketSetting,relationMarketId:{}, 缓存的变更:{}", linkId, standardMarketDataMessage.getRelationMarketId().toString(), JSON.toJSONString(standardMarketDataMessage));
        redisService.hSet(marketKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getBeginTime()));

        // 数据下发
        List<StandardMarketMessage> sendStandardMarketMessageList = Lists.newArrayList();

        if ( null != standardOutrightMarket && SaleMatchSellStausEnum.Sold.name().equals(standardOutrightMarket.getMarketSellStatus()) ) {

            if ( !CollectionUtils.isEmpty(filterOddsId) ) {
                for ( StandardMarketDTO standardMarketDTO : standardMarketDTOList) {
                    if ( !CollectionUtils.isEmpty(standardMarketDTO.getMarketOddsList()) ) {
                        for ( StandardMarketOddsDTO standardMarketOddsDTO : standardMarketDTO.getMarketOddsList() ) {
                            if ( filterOddsId.contains(Long.parseLong(standardMarketOddsDTO.getId()))) {
                                standardMarketOddsDTO.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                            }
                        }
                    }
                }
            }

            // 数据的组装
            tradeMarketOddsApiService.processOutrightTradeMarketOdds(request, standardMarketDTOList, standardMatchInfo, sendStandardMarketMessageList);
            // 数据的下发
            standardMarketOddsProducer.standardMarketOddsAsyncSend(request.getLinkId(), standardMatchInfo, sendStandardMarketMessageList, request.getDataSourceTime(),false);
        }

    }

}
