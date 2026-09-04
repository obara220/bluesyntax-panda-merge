package com.panda.merge.rocketmq.producer;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RiskManagerCodeEnums;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.RiskManagerCodeEnums;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.message.DataMerchantMessage;
import com.panda.merge.model.ConfigMarketCategoryPlace;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.odds.service.PlayRiskManagerService;
import com.panda.merge.service.ConfigMarketCategoryPlaceService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author casino
 */
@Slf4j
@Component
public class DataMerchantBaffleProducer extends BaseProcessor {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private ConfigMarketCategoryPlaceService configMarketCategoryPlaceService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private PlayRiskManagerService playRiskManagerService;

    public void sendCategoryListToRiskMQ(String linkId, Long matchId, Long sportId, Set<Long> playIdSet, Integer linkedType) {
        // 4405：不再用赛事级 pre/liveRiskManagerCode 判断；改为玩法级 playRiskManager 过滤（仅过滤被禁用链路的玩法）
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchId);
        int marketType = isOddsLive(matchId);
        Set<Long> filteredPlayIds = playIdSet;
        if (standardMatchInfo != null && MATCH_LENGTH.contains(standardMatchInfo.getMatchLength()) && !CollectionUtils.isEmpty(playIdSet)) {
            String key = playRiskManagerService.buildKey(matchId, marketType);
            filteredPlayIds = playIdSet.stream().filter(playId -> {
                Object v = redisService.hGet(key, String.valueOf(playId));
                String code = v == null ? null : String.valueOf(v);
                return !isIgnoreRiskBaffleTradeCode(code);
            }).collect(Collectors.toSet());
        }
        if (CollectionUtils.isEmpty(filteredPlayIds)) {
            log.info("::{}::sendCategoryListToRiskMQ,赛事：{}，玩法集合全部为ots/bts/f2ts/cts（玩法级），不下发", linkId, matchId);
            return;
        }
        Set<Long> needClose = new HashSet<>();
        int placeNumStatus;
        for (Long l : filteredPlayIds) {
            List<ConfigMarketCategoryPlace> configMarketCategoryPlaces = configMarketCategoryPlaceService.getItemListCache(matchId, l);
            if (CollectionUtils.isEmpty(configMarketCategoryPlaces)) {
                needClose.add(l);
                continue;
            }
            for (ConfigMarketCategoryPlace configMarketCategoryPlace : configMarketCategoryPlaces) {
                placeNumStatus = Integer.parseInt(configMarketCategoryPlace.getPlaceNumStatus());
                if (placeNumStatus == Constant.SPORT_MARKET.STATUS.ACTIVE.intValue() || placeNumStatus == Constant.SPORT_MARKET.STATUS.SUSPENDED.intValue()) {
                    needClose.add(l);
                    break;
                }
            }
        }
        if (CollectionUtils.isEmpty(needClose)) {
            log.info("::{}::sendCategoryListToRiskMQ,标准赛事ID:{},玩法集合:{},needClose为空，不下发数据商挡板", linkId, matchId, playIdSet);
            return;
        }
        //关盘
        Set<Long> deactivatedPlay = new HashSet<>();
        //封盘
        Set<Long> suspendedPlay = new HashSet<>();
        if (sportId.equals(StandardSportTypeEnum.Basketball.getCode())) {
            if (isOddsLive(matchId) == 0){
                for (Long play : needClose) {
                    if (MarginCategoryConfig.HANDICAP_WINNER_MAP.containsValue(play)) {
                        deactivatedPlay.add(play);
                    } else {
                        suspendedPlay.add(play);
                    }
                }
                if (!CollectionUtils.isEmpty(deactivatedPlay)) {
                    sendRcsPlayStatus(linkId + "_DEACTIVATED", matchId, sportId, linkedType, deactivatedPlay, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                }
                if (!CollectionUtils.isEmpty(suspendedPlay)) {
                    sendRcsPlayStatus(linkId + "_DEACTIVATED", matchId, sportId, linkedType, suspendedPlay, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                }
            }
        } else {
            sendRcsPlayStatus(linkId, matchId, sportId, linkedType, needClose, Constant.SPORT_MARKET.STATUS.SUSPENDED);
        }
    }

    private void sendRcsPlayStatus(String linkId, Long matchId, Long sportId, Integer linkedType, Set<Long> needClose, Integer status) {
        DataMerchantMessage dataMerchantMessage = new DataMerchantMessage();
        dataMerchantMessage.setTradeLevel(5);
        dataMerchantMessage.setSportId(sportId);
        dataMerchantMessage.setPlayIdList(new ArrayList<Long>(needClose));
        dataMerchantMessage.setStatus(status);
        dataMerchantMessage.setLinkedType(linkedType);
        dataMerchantMessage.setMatchId(matchId);
        dataMerchantMessage.setIsErrorProofing(0);
        Request<DataMerchantMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(dataMerchantMessage);
        MessageBuilder<Request<DataMerchantMessage>> builder = MessageBuilder.withPayload(messageRequest).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("RCS_TRADE_UPDATE_MARKET_STATUS", builder.build());
        log.info("::{}::开始组装数据商挡板需要封盘的玩法集合数据,topic:RCS_TRADE_UPDATE_MARKET_STATUS,标准赛事ID：{},request:{}", linkId, dataMerchantMessage.getMatchId(), JSON.toJSONString(messageRequest));
    }

    private List<Integer> MATCH_LENGTH = Lists.newArrayList(64, 68, 70);

    /**
     * 判断玩法是否为 ots/bts/f2ts/cts（该类玩法不下发数据商挡板）
     *
     * @param riskManagerCode
     */
    private boolean isIgnoreRiskBaffleTradeCode(String riskManagerCode) {
        if (StringUtils.isBlank(riskManagerCode)) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.OTS.name())
                || StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.BTS.name())
                || StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.F2TS.name())
                || StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.CTS.name());
    }

    /**
     * 网球
     * 让球类玩法特殊球头值 封盘独赢玩法
     */
    public void sendTennisCategoryListToRiskMQ(String linkId, StandardMatchInfo standardMatchInfo, Long playId, Long subPlayId, Integer linkedType) {
        DataMerchantMessage dataMerchantMessage = new DataMerchantMessage();
        dataMerchantMessage.setTradeLevel(2);
        dataMerchantMessage.setSportId(standardMatchInfo.getSportId());
        dataMerchantMessage.setPlayIdList(Arrays.asList(playId));
        dataMerchantMessage.setSubPlayId(subPlayId);
        dataMerchantMessage.setStatus(1);
        dataMerchantMessage.setLinkedType(linkedType);
        dataMerchantMessage.setMatchId(standardMatchInfo.getId());
        Request<DataMerchantMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(dataMerchantMessage);
        MessageBuilder<Request<DataMerchantMessage>> builder = MessageBuilder.withPayload(messageRequest).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("RCS_TRADE_UPDATE_MARKET_STATUS", builder.build());
        log.info("::{}::开始组装让球类特殊球头封独赢数据,topic:RCS_TRADE_UPDATE_MARKET_STATUS,标准赛事ID：{},request:{}", linkId, dataMerchantMessage.getMatchId(), JSON.toJSONString(messageRequest));
    }

    /**
     * 忽略下发赛事级别封数据源
     */
    private static List<String> ignoreDataSourceCode = Arrays.asList(DataSourceCodeEnum.BE.code, DataSourceCodeEnum.OD.code);

    /**
     * TX切换数据源下发风控赛事级别封盘
     *
     * @param linkId
     * @param matchId
     */
    public void switchDataSourceSendRiskMQ(String linkId, Long matchId, Long sportId) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchId);
        if (null != standardMatchInfo && ignoreDataSourceCode.contains(standardMatchInfo.getDataSourceCode())) {
            log.info("::{}::开始组装赛事封盘数据,不下发", linkId);
            return;
        }
        linkId = linkId + "_SWITCH";
        DataMerchantMessage dataMerchantMessage = new DataMerchantMessage();
        dataMerchantMessage.setMatchId(matchId);
        if (sportId.equals(StandardSportTypeEnum.Boxing.code) || sportId.equals(StandardSportTypeEnum.WaterPolo.code)) {
            dataMerchantMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
        } else {
            dataMerchantMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED);
        }
        //风控定义字段
        dataMerchantMessage.setTradeLevel(1);
        dataMerchantMessage.setLinkedType(16);
        Request<DataMerchantMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(dataMerchantMessage);
        MessageBuilder<Request<DataMerchantMessage>> builder = MessageBuilder.withPayload(messageRequest).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("RCS_TRADE_UPDATE_MARKET_STATUS", builder.build());
        log.info("::{}::开始组装赛事封盘数据,topic:RCS_TRADE_UPDATE_MARKET_STATUS,标准赛事ID：{},request:{}", linkId, dataMerchantMessage.getMatchId(), JSON.toJSONString(messageRequest));
    }

    public void batchSendCategoryListToRiskMQ(List<OddsWrapper<ThirdMarketDTO>> thirdMarketDTOS, Map<Long, StandardMatchInfoDetail> standardMatchInfoMap,
                                              Map<Long, StandardSportMarketSell> standardSportMarketSellMap, Map<String, Set<Long>> riskCategoryMap,
                                              Map<Long, ThirdMatchInfo> thirdMatchInfoBasedIdMap, Integer linkedType) {
        //判断赛事为 ots、bts 不下发
        List<OddsWrapper<ThirdMarketDTO>> filteredThirdMarketDTOS = thirdMarketDTOS.stream().filter(t->riskCategoryMap.containsKey(t.getLinkId())).collect(Collectors.toList());
        Map<String, OddsWrapper<ThirdMarketDTO>> filteredThirdMarkeMap = new HashMap<>();
        for (OddsWrapper<ThirdMarketDTO> item : filteredThirdMarketDTOS) {
            Long matchId = item.getStandardSourceId();
            StandardMatchInfo standardMatchInfo = standardMatchInfoMap.get(matchId);
            int marketType = isOddsLive(matchId);
            Set<Long> plays = riskCategoryMap.getOrDefault(item.getLinkId(), new HashSet<>());
            if (standardMatchInfo != null && MATCH_LENGTH.contains(standardMatchInfo.getMatchLength()) && !CollectionUtils.isEmpty(plays)) {
                String key = playRiskManagerService.buildKey(matchId, marketType);
                Set<Long> remaining = plays.stream().filter(playId -> {
                    Object v = redisService.hGet(key, String.valueOf(playId));
                    String code = v == null ? null : String.valueOf(v);
                    return !isIgnoreRiskBaffleTradeCode(code);
                }).collect(Collectors.toSet());
                if (CollectionUtils.isEmpty(remaining)) {
                    continue;
                }
                // 覆盖 riskCategoryMap 中本次要处理的玩法集合
                riskCategoryMap.put(item.getLinkId(), remaining);
            }
            filteredThirdMarkeMap.put(item.getLinkId(), item);
        }
        Map<String, Set<Long>> needCloseMap = new HashMap<>();
        for(Map.Entry<String, OddsWrapper<ThirdMarketDTO>> entry : filteredThirdMarkeMap.entrySet()) {
            String linkId = entry.getKey();
            OddsWrapper<ThirdMarketDTO> item = entry.getValue();
            String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_PLACE + item.getStandardSourceId();
            Map<String,ConfigMarketCategoryPlace> marketCategoryPlaceMap = redisService.hGetAllBasedBucket(redisKey, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
            Set<Long> needClose = new HashSet<>();
            int placeNumStatus;
            for (Long l : riskCategoryMap.get(linkId)) {
                List<ConfigMarketCategoryPlace> configMarketCategoryPlaces = getItemListCache(marketCategoryPlaceMap, item.getStandardSourceId(), l);
                if (CollectionUtils.isEmpty(configMarketCategoryPlaces)) {
                    needClose.add(l);
                    continue;
                }
                for (ConfigMarketCategoryPlace configMarketCategoryPlace : configMarketCategoryPlaces) {
                    placeNumStatus = Integer.parseInt(configMarketCategoryPlace.getPlaceNumStatus());
                    if (placeNumStatus == Constant.SPORT_MARKET.STATUS.ACTIVE.intValue() ||
                            placeNumStatus == Constant.SPORT_MARKET.STATUS.SUSPENDED.intValue()) {
                        needClose.add(l);
                        break;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(needClose)) {
                needCloseMap.put(linkId, needClose);
            }
        }

        for(Map.Entry<String, Set<Long>> entry : needCloseMap.entrySet()) {
            String linkId = entry.getKey() + "_riskCategorySet";
            Long matchId = filteredThirdMarkeMap.get(entry.getKey()).getStandardSourceId();
            Long thirdMatchId = filteredThirdMarkeMap.get(entry.getKey()).getThirdMatchId();
            Long sportId = thirdMatchInfoBasedIdMap.get(thirdMatchId).getSportId();
            //关盘
            Set<Long> deactivatedPlay = new HashSet<>();
            //封盘
            Set<Long> suspendedPlay = new HashSet<>();
            if (sportId.equals(StandardSportTypeEnum.Basketball.getCode())) {
                for (Long play : entry.getValue()) {
                    if (MarginCategoryConfig.HANDICAP_WINNER_MAP.containsValue(play)) {
                        deactivatedPlay.add(play);
                    } else {
                        suspendedPlay.add(play);
                    }
                }
                if (!CollectionUtils.isEmpty(deactivatedPlay)) {
                    sendRcsPlayStatus(linkId + "_DEACTIVATED", matchId, sportId, linkedType, deactivatedPlay, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                }
                if (!CollectionUtils.isEmpty(suspendedPlay)) {
                    sendRcsPlayStatus(linkId + "_DEACTIVATED", matchId, sportId, linkedType, suspendedPlay, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                }

            } else {
                sendRcsPlayStatus(linkId, matchId, sportId, linkedType, entry.getValue(), Constant.SPORT_MARKET.STATUS.SUSPENDED);
            }
        }
    }

    public Map<String, Boolean> batchGetMatchTradeType(Map<String, Long> linkStandardSourceIdMap, Map<Long, StandardMatchInfoDetail> standardMatchInfoMap,
                                                     Map<Long, StandardSportMarketSell> standardSportMarketSellMap) {
        Map<String, Boolean> result = new HashMap<>();
        for (Map.Entry<String, Long> entry : linkStandardSourceIdMap.entrySet()) {
            String linkId = entry.getKey();
            Long matchId = entry.getValue();
            StandardMatchInfo standardMatchInfo = standardMatchInfoMap.get(matchId);
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellMap.get(matchId);
            int marketType = isOddsLive(matchId);
            String pre = standardSportMarketSell.getPreRiskManagerCode();
            String live = standardSportMarketSell.getLiveRiskManagerCode();
            if (MATCH_LENGTH.contains(standardMatchInfo.getMatchLength()) && marketType == 1 && StringUtils.equals(pre, RiskManagerCodeEnums.OTS.name())) {
                result.put(linkId, true);
                continue;
            }
            if (MATCH_LENGTH.contains(standardMatchInfo.getMatchLength()) && marketType == 1 && StringUtils.equals(pre, RiskManagerCodeEnums.BTS.name())) {
                result.put(linkId, true);
                continue;
            }
            if (MATCH_LENGTH.contains(standardMatchInfo.getMatchLength()) && marketType == 1 && StringUtils.equals(pre, RiskManagerCodeEnums.F2TS.name())) {
                result.put(linkId, true);
                continue;
            }
            if (MATCH_LENGTH.contains(standardMatchInfo.getMatchLength()) && marketType == 0 && StringUtils.equals(live, RiskManagerCodeEnums.OTS.name())) {
                result.put(linkId, true);
                continue;
            }
            if (MATCH_LENGTH.contains(standardMatchInfo.getMatchLength()) && marketType == 0 && StringUtils.equals(live, RiskManagerCodeEnums.BTS.name())) {
                result.put(linkId, true);
                continue;
            }
            if (MATCH_LENGTH.contains(standardMatchInfo.getMatchLength()) && marketType == 0 && StringUtils.equals(live, RiskManagerCodeEnums.F2TS.name())) {
                result.put(linkId, true);
                continue;
            }
            result.put(linkId, false);
        }
        return result;
    }

    private List<ConfigMarketCategoryPlace> getItemListCache(Map<String,ConfigMarketCategoryPlace> marketCategoryPlaceMap, Long standardMatchInfoId, Long standardCategoryId){
        List<ConfigMarketCategoryPlace> configMarketCategoryPlaces = new ArrayList<>();
        for (int i = 0;i<11;i++) {
            String key = standardMatchInfoId+"_"+standardCategoryId+"_"+standardCategoryId+"_"+i;
            if (ObjectUtil.isNotEmpty(marketCategoryPlaceMap.get(key))) {
                configMarketCategoryPlaces.add(marketCategoryPlaceMap.get(key));
            }
        }
        return configMarketCategoryPlaces;
    }

    /**
     * 下发风控赛事级别开关状态
     * @param linkId linkId
     * @param matchId 赛事id
     */
    public void changeMatchStatusSendRiskMQ(String linkId, Long matchId, Integer status) {
        linkId = linkId + "_SWITCH";
        DataMerchantMessage dataMerchantMessage = new DataMerchantMessage();
        dataMerchantMessage.setMatchId(matchId);
        dataMerchantMessage.setStatus(status);
        //风控定义字段
        dataMerchantMessage.setTradeLevel(1);
        dataMerchantMessage.setLinkedType(16);
        Request<DataMerchantMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(dataMerchantMessage);
        MessageBuilder<Request<DataMerchantMessage>> builder = MessageBuilder.withPayload(messageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("RCS_TRADE_UPDATE_MARKET_STATUS", builder.build());
        log.info("::{}::开始组装赛事级别状态变更数据,topic:RCS_TRADE_UPDATE_MARKET_STATUS,标准赛事ID：{},request:{}", linkId,
                dataMerchantMessage.getMatchId(), JSON.toJSONString(messageRequest));
    }
}
