package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.dto.message.MarketOddsLinkageConfigMessage;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StandardMarketOddsLinkageProcessor {

    @Autowired
    RedisService redisService;

    @Lazy
    @Autowired
    ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private OutrightMarketOrderProcessor outrightMarketOrderProcessor;

    public void process(Request<List<MarketOddsLinkageConfigMessage>> request) {
        log.info("接收赔率联动配置：{}", JSONObject.toJSONString(request));
        String linkId = request.getLinkId();
        List<MarketOddsLinkageConfigMessage> marketOddsLinkageConfigMessageList = request.getData();
        Long parentMatchId = marketOddsLinkageConfigMessageList.get(0).getParentMatchId();
        Long dataSourceTime = request.getDataSourceTime();


        //1、收到新配置，先删除旧配置，通知普通赛事 ，冠军赛事触发赔率下发
        //旧主赛事分组缓存
        List<MarketOddsLinkageConfigMessage> marketOddsLinkageConfigMessages = marketOddsLinkageConfigMessageList.stream().filter(config -> config.getType() != 0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(marketOddsLinkageConfigMessages)) {
            log.info("::{}::接收赔率联动配置，主配置不存在,赛事ID:{}", linkId, parentMatchId);
            return;
        }
        String standardMarketOddsId = marketOddsLinkageConfigMessages.get(0).getStandardMarketOddsId();
        String matchKey = Constant.REDIS_KEY.RONGE_MATCH_ODDS_LINKAGE_CONFIG + standardMarketOddsId;
        List<MarketOddsLinkageConfigMessage> marketOddsLinkageConfigMessageOld = (List<MarketOddsLinkageConfigMessage>) redisService.get(matchKey);
        if (!CollectionUtils.isEmpty(marketOddsLinkageConfigMessageOld)) {
            log.info("::{}::接收赔率联动配置，开始处理旧数据下发,赛事ID:{}", linkId, parentMatchId);
            //先删除旧的所有联动缓存
            List<String> redisDels = new ArrayList<>();
            //删除赛事级缓存
            redisDels.add(matchKey);
            marketOddsLinkageConfigMessageOld.forEach(old -> {
                //删除投注项缓存
                redisDels.add(Constant.REDIS_KEY.RONGE_ODDS_LINKAGE_CONFIG + old.getStandardMarketOddsId());
            });
            redisService.del(redisDels);
            //触发旧赔率下发
            sendCacheMarketOdds(linkId + "_old", marketOddsLinkageConfigMessageOld, false, dataSourceTime);
        }
        log.info("::{}::接收赔率联动配置，开始处理新数据下发,赛事ID:{}", linkId, parentMatchId);
        //2、新配置写入缓存，再触发主赔率下发,这里只处理主赔率下发，主流程再触发副赔率下发
        //根据主副标识分组
        Map<Integer, List<MarketOddsLinkageConfigMessage>> marketOddsLinkageConfigMessageMapNew = marketOddsLinkageConfigMessageList.stream().filter(config -> config.getType() != 2).collect(Collectors.groupingBy(obj -> obj.getType()));
        if (MapUtils.isEmpty(marketOddsLinkageConfigMessageMapNew)) {
            return;
        }
        if (marketOddsLinkageConfigMessageList.size() == 1) {
            log.info("::{}::接收赔率联动配置，开始处理新数据下发,配置只有一个删除，赛事ID:{}", linkId, parentMatchId);
            redisService.del(matchKey);
            return;
        }
        //写入新赛事缓存
        redisService.set(matchKey, marketOddsLinkageConfigMessageList, RedisConfig.REDIS_MONTH_TIME);
        //主
        MarketOddsLinkageConfigMessage marketOddsLinkageConfigMessageListMain = marketOddsLinkageConfigMessageMapNew.get(1).get(0);
        //副
        List<MarketOddsLinkageConfigMessage> marketOddsLinkageConfigMessageListMapDeputy = marketOddsLinkageConfigMessageMapNew.get(0);
        //写入投注项缓存
        for (MarketOddsLinkageConfigMessage marketOddsLinkageConfigMessageDeputy : marketOddsLinkageConfigMessageListMapDeputy) {
            //副写入投注项主id,方便主流程直接获取到主赔率
            marketOddsLinkageConfigMessageDeputy.setParentStandardMarketOddsId(marketOddsLinkageConfigMessageListMain.getStandardMarketOddsId());
            redisService.set(Constant.REDIS_KEY.RONGE_ODDS_LINKAGE_CONFIG + marketOddsLinkageConfigMessageDeputy.getStandardMarketOddsId(), marketOddsLinkageConfigMessageDeputy,RedisConfig.REDIS_MONTH_TIME);
        }
        redisService.set(Constant.REDIS_KEY.RONGE_ODDS_LINKAGE_CONFIG + marketOddsLinkageConfigMessageListMain.getStandardMarketOddsId(), marketOddsLinkageConfigMessageListMain,RedisConfig.REDIS_MONTH_TIME);

        if (MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY.contains(marketOddsLinkageConfigMessageListMain.getCategoryId())) {
            Set<String> mainMarketIds = new HashSet<>();
            mainMarketIds.add(marketOddsLinkageConfigMessageListMain.getStandardMarketId());
            //异步通知冠军赔率触发
            sendOutrightMarketOdds(linkId, dataSourceTime, parentMatchId, mainMarketIds);
        } else {
            Set<Long> mainCategoryIds = new HashSet<>();
            mainCategoryIds.add(marketOddsLinkageConfigMessageListMain.getCategoryId());
            //异步通知普通主赔率触发
            sendMarketOdds(linkId + "_new_" + parentMatchId, dataSourceTime, parentMatchId, mainCategoryIds);
        }
    }

    /**
     * 触发主副赔率下发
     *
     * @param linkId
     * @param marketOddsLinkageConfigMessageOld
     * @param isTrue                            true 通知只副赔率下发 ，false 主副都下发
     * @param dataSourceTime
     */
    public void sendCacheMarketOdds(String linkId, List<MarketOddsLinkageConfigMessage> marketOddsLinkageConfigMessageOld, Boolean isTrue, Long dataSourceTime) {
        Map<Long, List<MarketOddsLinkageConfigMessage>> marketOddsLinkageConfigMessageMapOld = marketOddsLinkageConfigMessageOld.stream().collect(Collectors.groupingBy(MarketOddsLinkageConfigMessage::getMatchId));
        for (Map.Entry<Long, List<MarketOddsLinkageConfigMessage>> entryOld : marketOddsLinkageConfigMessageMapOld.entrySet()) {
            Long matchId = entryOld.getKey();
            //冠军赛事盘口集合
            Set<String> outrightMarketIds = new HashSet<>();
            //普通赛事玩法集合
            Set<Long> categoryIds = new HashSet<>();
            //副赔率自动水差
            Set<String> autoDiffDel = new HashSet<>();
            List<MarketOddsLinkageConfigMessage> value = entryOld.getValue();
            for (MarketOddsLinkageConfigMessage marketOddsLinkageConfigMessage : value) {
                if (isTrue) {
                    //不下发主盘口
                    if (1 == marketOddsLinkageConfigMessage.getType()) {
                        continue;
                    }
                    //删除副赔率自动水差
                    autoDiffDel.add(Constant.REDIS_KEY.RONGE_MARKET_ODDS_AUTO_DIFF + marketOddsLinkageConfigMessage.getStandardMarketId());
                }
                //配置判断冠军还是 ，还是普通赛事,，通知赔率下发
                if (MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY.contains(marketOddsLinkageConfigMessage.getCategoryId())) {
                    outrightMarketIds.add(marketOddsLinkageConfigMessage.getStandardMarketId());
                } else {
                    categoryIds.add(marketOddsLinkageConfigMessage.getCategoryId());
                }
            }
            //通知冠军赔率触发
            if (!CollectionUtils.isEmpty(outrightMarketIds)) {
                log.info("::{}::赔率联动，冠军：{}，盘口触发下发:{}", linkId, matchId, outrightMarketIds);
                //异步通知冠军赔率触发
                sendOutrightMarketOdds(linkId, dataSourceTime, matchId, outrightMarketIds);
                continue;
            }
            //通知普通赔率触发
            if (!CollectionUtils.isEmpty(categoryIds)) {
                redisService.delete(autoDiffDel);
                log.info("::{}::赔率联动，普通赛事：{}，盘口触发下发:{},删除盘口自动水差：{}", linkId, matchId, categoryIds, autoDiffDel);
                sendMarketOdds(linkId + "_" + matchId, dataSourceTime, matchId, categoryIds);
            }

        }
    }

    /**
     * 主流程通知赔率下发
     */
    @Async("sendMarketOddsLinkageThreadPool")
    public void mainSendMarketOddsProcessor(String linkId, String standardMarketOddsId) {
        log.info("::{}::赔率联动，主流程通知赔率下发：{}", linkId, standardMarketOddsId);
        String matchKey = Constant.REDIS_KEY.RONGE_MATCH_ODDS_LINKAGE_CONFIG + standardMarketOddsId;
        List<MarketOddsLinkageConfigMessage> marketOddsLinkageConfigMessageOld = (List<MarketOddsLinkageConfigMessage>) redisService.get(matchKey);
        if (CollectionUtils.isEmpty(marketOddsLinkageConfigMessageOld)) {
            return;
        }
        //触发赔率下发
        sendCacheMarketOdds(linkId + "_main_new", marketOddsLinkageConfigMessageOld, true, System.currentTimeMillis());
    }

    /**
     * 普通赛事触发下发
     *
     * @param linkId
     * @param dataSourceTime
     * @param matchId
     * @param categoryIds
     */
    @Async("sendMarketOddsLinkageThreadPool")
    public void sendMarketOdds(String linkId, Long dataSourceTime, Long matchId, Set<Long> categoryIds) {
        try {
            //查找标准赛事
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchId);
            if (null == standardMatchInfo) {
                log.info("::{}::赔率联动,标准赛事为空,标准赛事id={}", linkId, matchId);
                return;
            }
            //查找开售信息
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(matchId);
            if (null == standardSportMarketSell) {
                log.info("::{}::赔率联动,未找到预开售信息,标准赛事id={}", linkId, matchId);
                return;
            }
            Map<String, StandardMarketDataMessage> standardMarketDataMessageMap = thirdMatchMarketProcessor.getStringStandardMarketDataMessageMap(categoryIds, linkId, standardMatchInfo, standardSportMarketSell);
            thirdMatchMarketProcessor.processOddsByAll(linkId,-1,null, standardMatchInfo, categoryIds, standardMarketDataMessageMap, dataSourceTime, standardSportMarketSell, new HashMap<>());
        } catch (Exception e) {
            log.error("{}::赔率联动,普通赛事触发赔率下发异常 ERROR:", linkId, e);
        }
    }

    /**
     * 冠军赛事触发下发
     *
     * @param linkId
     * @param dataSourceTime
     * @param matchId
     * @param marketIds
     */

    @Async("sendMarketOddsLinkageThreadPool")
    public void sendOutrightMarketOdds(String linkId, Long dataSourceTime, Long matchId, Set<String> marketIds) {
        try {
            //获取标准赛事
            StandardMatchInfoDetail standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(true, matchId);
            if (null == standardMatchInfo) {
                log.info("::{}::冠军赔率联动,未找到冠军赛事,冠军赛事id:{}", linkId, matchId);
                return;
            }

            StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell(true, matchId);
            if (null == standardSportMarketSell) {
                log.info("::{}::冠军赔率联动,未找到冠军开售信息,冠军赛事id:{}", linkId, matchId);
                return;
            }

            String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + matchId + "_" + standardMatchInfo.getDataSourceCode();
            log.info("::{}:: 冠军赔率联动 redisKey:{} ", linkId, marketKey);
            Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(marketKey);
            log.info("::{}:: 冠军赔率联动 redisKey:{} standardMarketMessageMap::{}", linkId, marketKey, JSON.toJSONString(standardMarketMessageMap));
            if (CollectionUtils.isEmpty(standardMarketMessageMap)) {
                log.info("::{}:: 冠军赔率联动 standardMarketMessageMap is null", linkId);
                return;
            }

            Map<String, StandardMarketDataMessage> sendMap = standardMarketMessageMap.entrySet().stream()
                    .filter(map -> marketIds.contains(map.getValue().getRelationMarketId().toString()))
                    .collect(Collectors.toMap((e) -> (String) e.getKey(), (e) -> e.getValue()));
            if (CollectionUtils.isEmpty(sendMap)) {
                log.info("::{}:: 冠军赔率联动 sendMap is null", linkId);
                return;
            }

            Set<Long> marketIdSet = sendMap.values().stream().map(StandardMarketDataMessage::getRelationMarketId).collect(Collectors.toSet());

            // thirdMatchMarketProcessor.processOddsByOutright(linkId, standardMatchInfo, marketIdSet, sendMap, dataSourceTime, new HashMap<>());

            outrightMarketOrderProcessor.editChampionMarketOrder(linkId, standardMatchInfo, marketIdSet, sendMap, dataSourceTime, new HashMap<>());

        } catch (Exception e) {
            log.error("{}::冠军赔率联动,冠军赛事触发赔率下发异常 ERROR:", linkId, e);
        }
    }


}
