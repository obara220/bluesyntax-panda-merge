package com.panda.merge.rocketmq.processor;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.MarketTipsLanguageEnum;
import com.panda.merge.component.StandardMatchMarketOddsLinkageProcessor;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.MarketOrderDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.OutrightMarketOrderMessage;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 冠军盘口排序处理
 *
 * @author raulvii<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2021/1/14 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Component
@Slf4j
@Validated
public class OutrightMarketOrderProcessor extends BaseProcessor {
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;

    @Autowired
    private OutrightTradeTypeConfigService outrightTradeTypeConfigService;

    @Autowired
    private OutrightTradeMarketConfigService outrightTradeMarketConfigService;

    @Autowired
    private OutrightTradeOddsConfigService outrightTradeOddsConfigService;

    @Autowired
    private OutrightTradeProbabilityConfigService outrightTradeProbabilityConfigService;
    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;

    @Autowired
    private StandardMatchMarketOddsLinkageProcessor standardMatchMarketOddsLinkageProcessor;

    /**
     * 仅限于排序的赛事的本村存储
     */
    public static ThreadLocal<Long> orderMatchLocal = new ThreadLocal<Long>();

    public void processOutrightMarketOrder(@Valid Request<OutrightMarketOrderMessage> request) {
        String linkId = request.getLinkId();
        validateLinkId("processOutrightMarketOrder", request);
        log.info("::{}::processOutrightMarketOrder,req:{}", linkId, JSON.toJSONString(request));
        //获取当前数据源缓存中所有的盘口
        OutrightMarketOrderMessage marketOrderMessage = request.getData();
        String dataSourceCode = request.getDataSourceCode();

        StandardMatchInfo standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(true, marketOrderMessage.getStandardMatchId());
        if (null == standardMatchInfo) {
            log.info("::{}::processOutrightMarketOrder standardMatchId:{},未找到标准赛事", linkId, marketOrderMessage.getStandardMatchId());
            return;
        }

        String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + marketOrderMessage.getStandardMatchId() + "_" + standardMatchInfo.getDataSourceCode();
        log.info("::{}:: processOutrightMarketOrder redisKey={} ", linkId, marketKey);
        Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(marketKey);
        log.info("::{}:: processOutrightMarketOrder redisKey={} standardMarketMessageMap::{}", linkId, marketKey, JSON.toJSONString(standardMarketMessageMap) );
        if (CollectionUtils.isEmpty(standardMarketMessageMap)) {
            log.info("::{}:: processOutrightMarketOrder standardMarketMessageMap is null", linkId);
            return;
        }

        List<StandardOutrightMarket> outrightMarketList = standardOutrightMarketService.selectOutrightMarketSellList(standardMatchInfo.getId());
        if (CollectionUtils.isEmpty(outrightMarketList)) {
            log.info("::{}:: 冠军赛事未开售盘口,赔率不下发,冠军赛事id:{}", linkId, standardMatchInfo.getId());
            return;
        }
        //过滤出开售的盘口
        List<String> marketIdList = outrightMarketList.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        Set<String> collect = marketOrderMessage.getMarketOrderDTOList().stream().filter(marketOrderDTO -> marketIdList.contains(marketOrderDTO.getStandardMarketId()))
                .map(MarketOrderDTO::getStandardMarketId).collect(Collectors.toSet());
        Map<String, StandardMarketDataMessage> sendMap = standardMarketMessageMap.entrySet().stream()
                .filter(map -> collect.contains(String.valueOf(map.getValue().getRelationMarketId())))
                .collect(Collectors.toMap((e) -> (String) e.getKey(), (e) -> e.getValue()));
        if (CollectionUtils.isEmpty(sendMap)) {
            log.info("::{}:: processOutrightMarketOrder sendMap is null", linkId);
            return;
        }
        //盘口排序赋值
        Map<String, Integer> map = marketOrderMessage.getMarketOrderDTOList().stream().collect(Collectors.toMap(MarketOrderDTO::getStandardMarketId, MarketOrderDTO::getMarketOrderNumber));
        sendMap.forEach((k, v) -> {
            if (null != map.get(k)) {
                v.setOrderNo(map.get(k));
            }
        });
        String lockValue = UUIdUtils.getId() + "_" + request.getLinkId();
        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + standardMatchInfo.getId();
        try {
            log.info("::{}::processOutrightMarketOrder,redisLocKey:{},准备获取分布式锁,lockValue:{}", request.getLinkId(), redisLocKey, lockValue);
            redisService.tryLock(redisLocKey, lockValue, 5, 3);
            log.info("::{}::processOutrightMarketOrder,redisLocKey:{},获取到分布式锁,lockValue:{}", request.getLinkId(), redisLocKey, lockValue);
            //刷新缓存中的所有相关盘口的排序
            Boolean result = redisService.hSetAll(marketKey, sendMap, marketCacheTime(standardMatchInfo.getBeginTime()));
        } catch (Exception e) {
            log.error("::{}::processOutrightMarketOrder,error:{}", linkId, e);
        } finally {
            redisService.unLock(redisLocKey, lockValue);
            log.info("::{}::processOutrightMarketOrder,redisLocKey:{},释放分布式锁,lockValue:{}", request.getLinkId(), redisLocKey, lockValue);
        }
        //下发当前最新赔率
        Set<Long> marketIdSet = sendMap.values().stream().map(StandardMarketDataMessage::getRelationMarketId).collect(Collectors.toSet());
        try {
            //设置本地排序记录
            orderMatchLocal.set(standardMatchInfo.getId());
            if (DataSourceCodeEnum.PA.name().equals(standardMatchInfo.getDataSourceCode())) {
                this.editChampionMarketOrder(linkId, standardMatchInfo, marketIdSet, sendMap, System.currentTimeMillis(), new HashMap<>());
            } else {
                thirdMatchMarketProcessor.processOddsByOutright(linkId, standardMatchInfo, marketIdSet, sendMap, System.currentTimeMillis(), new HashMap<>());
            }
        } finally {
            //删除本地排序记录
            orderMatchLocal.remove();
        }
        log.info("::{}:: processOutrightMarketOrder process success", linkId);
    }

    /**
     * 组装下发的赔率
     * @param linkId
     * @param standardMatchInfo
     * @param marketIdSet
     * @param standardMarketMessageMap
     * @param dataSourceTime
     * @param changeCategoryOddsType
     */
    public void editChampionMarketOrder(String linkId, StandardMatchInfo standardMatchInfo, Set<Long> marketIdSet, Map<String, StandardMarketDataMessage> standardMarketMessageMap, Long dataSourceTime, Map<Long, List<String>> changeCategoryOddsType) {
        log.info("::{}::editChampionMarketOrder标准赛事id:{},冠军操盘本次处理的标准玩法id:{},缓存map集合大小:{}", linkId, standardMatchInfo.getId(), marketIdSet, standardMarketMessageMap.size());
        if(CollectionUtils.isEmpty(marketIdSet)){
            log.info("::{}::editChampionMarketOrder标准赛事id:{},盘口集合为空,赔率不下发,盘口集合:{}", linkId, standardMatchInfo.getId(), JSON.toJSONString(marketIdSet));
            return;
        }
        //计算盘口及排序
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("editChampionMarketOrder冠军操盘获取玩法手自动配置");
        //盘口维度的操盘配置,直接一次性从库查出赛事玩法级的手自动类型，比循环查更快
        Map<Long, Integer> tradeTypeMap = outrightTradeTypeConfigService.getTradeTypeMapByMatchId(standardMatchInfo.getId(),marketIdSet);
        log.info("::{}::editChampionMarketOrder标准赛事id:{},盘口维度的操盘配置:{}", linkId, standardMatchInfo.getId(), JSON.toJSONString(tradeTypeMap));
        swCalculate.stop();
        swCalculate.start("editChampionMarketOrder冠军操盘全部盘口计算耗时");
        List<StandardMarketDataMessage> collect = standardMarketMessageMap.values().stream().filter(e -> marketIdSet.contains(e.getRelationMarketId())).collect(Collectors.toList());
        log.info("::{}:: editChampionMarketOrder processOddsByOutright:{}", linkId, JSON.toJSONString(collect));
        //构建下发给下游的list集合
        List<StandardMarketMessage> standardMarketMessageSendListAUTO = new ArrayList<>();
        for (StandardMarketDataMessage marketDataMsg : collect) {
            //数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
            StandardMarketMessage standardMarketMessage = thirdMatchMarketProcessor.convertStandardMarketMessage(linkId, marketDataMsg, standardMatchInfo.getOperateMatchStatus(), true,true, changeCategoryOddsType);
            log.info("::{}::editChampionMarketOrder 数据组装及转换后的赔率:{}", linkId, JSON.toJSONString(standardMarketMessage));
            //盘口状态处理
            ConfigOutrightTradeMarket configOutrightTradeMarket = outrightTradeMarketConfigService.selectItem(standardMatchInfo.getId(), standardMarketMessage.getId());
            if(null != configOutrightTradeMarket){
                Integer status = standardMarketMessage.getStatus();
                if(configOutrightTradeMarket.getMarketStatus() > status){
                    status = configOutrightTradeMarket.getMarketStatus();
                    standardMarketMessage.setPaStatusReason(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.CHAMPION_HANDICAP_STATUS.getCode(),configOutrightTradeMarket.getMarketStatus().toString()));
                    standardMarketMessage.setPaStatus(configOutrightTradeMarket.getMarketStatus());
                }
                standardMarketMessage.setStatus(status);
            }

            if(!CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())){
                for(StandardMarketOddsMessage marketOddsMessage : standardMarketMessage.getMarketOddsList()){
                    //三方数据源状态赋值
                    marketOddsMessage.setThirdSourceActive(marketOddsMessage.getActive());
                    //投注项状态处理
                    ConfigOutrightTradeOdds configOutrightTradeOdds = outrightTradeOddsConfigService.selectItem(standardMatchInfo.getId(), marketOddsMessage.getId());
                    if(null != configOutrightTradeOdds){
                        marketOddsMessage.setActive(configOutrightTradeOdds.getOddsStatus());
                        marketOddsMessage.setPaActiveReason("冠军操盘，投注项状态为：【"+configOutrightTradeOdds.getOddsStatus() + "】");
                    }
                    /**
                     * P1=1/抽水赔率，（截取保留4位小数）
                     * P2=概率变化/100 （截取保留4位小数）
                     * odds=1/(P1+P2)；(赔率截取保留2位小数)；
                     */
                    ConfigOutrightTradeProbability configOutrightTradeProbability = outrightTradeProbabilityConfigService.selectItem(standardMatchInfo.getId(), marketOddsMessage.getId());
                    Integer paOddsValue = marketOddsMessage.getOddsValue();
                    if(null != configOutrightTradeProbability && null != paOddsValue  && 0 != paOddsValue){
                        BigDecimal p1 = new BigDecimal(100000).divide(new BigDecimal(paOddsValue),4, BigDecimal.ROUND_DOWN);
                        BigDecimal p2 = new BigDecimal(configOutrightTradeProbability.getProbability()).divide(new BigDecimal(100),4, BigDecimal.ROUND_DOWN);
                        BigDecimal divide = new BigDecimal(1).divide(p1.add(p2), 4, BigDecimal.ROUND_HALF_UP);
                        DecimalFormat dFormat = new DecimalFormat();
                        dFormat.setMaximumFractionDigits(2);
                        dFormat.setGroupingSize(0);
                        dFormat.setRoundingMode(RoundingMode.FLOOR);
                        Integer oddsValue = new BigDecimal(dFormat.format(divide)).multiply(new BigDecimal(100000)).intValue();
                        marketOddsMessage.setPaOddsValue(oddsValue);
                        if(oddsValue < 1.01 * 100000){
                            marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                            marketOddsMessage.setPaActiveReason("跳水后，赔率值不得低于1.01,賠率值:" + oddsValue);
                        }else if(oddsValue > 1001 * 100000){
                            marketOddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                            marketOddsMessage.setPaActiveReason("跳水后，赔率值最高不能超过1001,賠率值:" + oddsValue);
                        }
                        log.info("::{}::editChampionMarketOrder processOddsByOutright,盘口id:{},投注项id:{},概率差:{}，计算前赔率：{}，计算后赔率：{}", linkId, standardMarketMessage.getId(), marketOddsMessage.getId(),
                                configOutrightTradeProbability.getProbability(), paOddsValue, oddsValue);

                    }
                }
            }
            //赔率合法性校验
            if (null != standardMarketMessage.getMarketOddsList()) {
                for (StandardMarketOddsMessage message : standardMarketMessage.getMarketOddsList()) {
                    if(message.getPaOddsValue() == null || message.getPaOddsValue() <= 100000){
                        //投注项赔率不合法时，只封当前投注项
                        message.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                        message.setPaActiveReason("投注项赔率不合法，赔率小于1，投注项封盘");
                        log.info("::{}::editChampionMarketOrder processOddsByOutright赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率:{}",
                                linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getThirdMarketSourceId(), message.getPaOddsValue());
                    }
                }
            }
            //赔率优化(两项盘小数位优化)
            thirdMatchMarketProcessor.processOddsValueDecimals(linkId, standardMarketMessage, standardMatchInfo);
            standardMarketMessageSendListAUTO.add(standardMarketMessage);
        }
        //-------------赔率下发-----------------
        if(!CollectionUtils.isEmpty(standardMarketMessageSendListAUTO)){
            standardMatchMarketOddsLinkageProcessor.championMarketOddsMainLinkage(linkId, standardMatchInfo, standardMarketMessageSendListAUTO);
            int batchSize = 5;
            for (int i = 0; i < standardMarketMessageSendListAUTO.size(); i += batchSize) {
                List<StandardMarketMessage> batch = standardMarketMessageSendListAUTO.subList(i, Math.min(i + batchSize, standardMarketMessageSendListAUTO.size()));
                String batchLinkId = linkId + (i / batchSize + 1);
                List<Long> batchMarketIds = batch.stream().map(StandardMarketMessage::getId).collect(Collectors.toList());
                log.info("::{}::editChampionMarketOrder 赔率分批下发,批次:{},本批standardMarketId:{}", batchLinkId, i / batchSize + 1, batchMarketIds);
                standardMarketOddsProducer.standardMarketOddsAsyncSend(batchLinkId, standardMatchInfo, batch, dataSourceTime, false);
            }
        }
        log.info("::{}::editChampionMarketOrder 冠军操盘全部盘口计算耗时{}ms," + swCalculate.prettyPrint(), linkId, swCalculate.getTotalTimeMillis());
    }

}

