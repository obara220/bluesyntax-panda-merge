package com.panda.merge.rocketmq.producer;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.FootballMarketsSoreProcessor;
import com.panda.merge.component.MaintainDataSourceProcessor;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.*;
import com.panda.merge.dto.odds.BasketballConfigDTO;
import com.panda.merge.model.*;
import com.panda.merge.odds.service.OddsCalcVersionService;
import com.panda.merge.rocketmq.RocketMQDelegate;
import com.panda.merge.rocketmq.processor.StandardMatchCategoryRemovedProcessor;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.service.*;
import com.panda.merge.util.CalculateOddsUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_TRAD_CONFIG;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/16 <br>
 * @see com.panda.merge.rocketmq.producer <br>
 */
@Slf4j
@Component
public class StandardMarketOddsProducer {
    @Autowired
    private RocketMQDelegate mqDelegate;

    @Autowired
    private RocketMQTemplate mqTemplate;

    @Autowired
    private ConfigMarketLevelService configMarketLevelService;

    @Autowired
    private StandardSportTournamentService standardSportTournamentService;
    @Autowired
    private FootballMarketsSoreProcessor checkMarketsSoreProcessor;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private MaintainDataSourceProcessor maintainDataSourceProcessor;
    @Autowired
    private StandardMatchCategoryRemovedProcessor standardMatchCategoryRemovedProcessor;
    @Lazy
    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ConfigSportCategoryGroupService configSportCategoryGroupService;

    @Autowired
    private OddsCalcVersionService oddsCalcVersionService;

    @Autowired
    private OutrightTradeTypeConfigService outrightTradeTypeConfigService;

    @Autowired
    private ConfigTournamentTradeItemService configTournamentTradeItemService;

    @Autowired
    private ConfigMarketTradeItemService configMarketTradeItemService;

    private Integer NUMBER_TWO = 2;

    //4233 开关
    @NacosValue(value = "${odds.4233.enabled:true}", autoRefreshed = true)
    private Boolean isEnabled = true;

    @Getter
    @NacosValue(value = "${market.98386.15.value:300}", autoRefreshed = true)
    private Integer market5Value;

    @Getter
    @NacosValue(value = "${market.98386.5.value:500}", autoRefreshed = true)
    private Integer market15Value;

    @Autowired
    private ConfigMarketCategoryMarginService configMarketCategoryMarginService;

    /**
     * 下发赛事盘口数据到下游，异步推送
     *
     * @param linkId
     * @param standardMarketMessageAllList
     */
    public StandardMatchMarketMessage standardMarketOddsAsyncSend(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList, Long dataSourceTime, boolean matchTradType) {
        boolean isXts = matchTradType;
        checkWinnerBasket(linkId,standardMatchInfo,standardMarketMessageAllList,isXts);
        //所有维护中数据源盘口关闭
        maintainDataSourceProcessor.underMaintenanceMarketClose(linkId, standardMatchInfo.getId(), standardMarketMessageAllList);
        standardMatchCategoryRemovedProcessor.marketRemovedClose(linkId, standardMatchInfo.getId(), standardMarketMessageAllList);
        //通过配置移除多盘口玩法中多余的盘口
        StandardMatchMarketMessage standardMatchMarketMessage = buildStandardMatchMarketMessage(linkId, standardMatchInfo, standardMarketMessageAllList);
        Request<StandardMatchMarketMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        if (isXts&&!CollectionUtils.isEmpty(standardMarketMessageAllList) && !Objects.equals(NUMBER_TWO, standardMarketMessageAllList.get(0).getMarketType())) {
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
            matchTradType = getMatchTradeCacheConfig(standardMarketMessageAllList.get(0).getMarketType() == 1 ? standardSportMarketSell.getPreRiskManagerCode() : standardSportMarketSell.getLiveRiskManagerCode());
            log.info("::{}::,计算赔率分组 代操盘 数据源Code:{},是否计算分组赔率 ：{}", linkId,standardMatchInfo.getRiskManagerCode(),matchTradType);
        }
        //赔率分组计算
        calculateOdds(linkId, standardMatchInfo, matchTradType, standardMatchMarketMessage, dataSourceTime);

        //第一个参数表示topic:tag
        //2780需求： 常规赛事赔率和电子赔率topic拆分
        if (1 != standardMatchInfo.getMatchType()) {
            StandardMatchInfo standartMatch = standardMatchInfoService.getItem(standardMatchInfo.getId());
            if (null != standartMatch && NUMBER_TWO == standartMatch.getMatchType()) {
                mqDelegate.asyncSend(
                        "STANDARD_MARKET_ODDS_ESPORT:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                        builder.build(),
                        new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("::{}::,STANDARD_MARKET_ODDS_ESPORT send successful", linkId);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS_ESPORT", throwable);
                    }
                        },
                        standardMatchInfo.getId());
                return standardMatchMarketMessage;
            }
        }
        return sendMq(standardMatchMarketMessage,linkId,standardMatchInfo,dataSourceTime,isXts);
    }

    public StandardMatchMarketMessage standardMarketOddsAsyncSendByRisk(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList, Long dataSourceTime, boolean matchTradType) {
        boolean isXts = matchTradType;
        checkWinnerBasket(linkId,standardMatchInfo,standardMarketMessageAllList,isXts);
        //所有维护中数据源盘口关闭
        maintainDataSourceProcessor.underMaintenanceMarketClose(linkId, standardMatchInfo.getId(), standardMarketMessageAllList);
        standardMatchCategoryRemovedProcessor.marketRemovedClose(linkId, standardMatchInfo.getId(), standardMarketMessageAllList);
        //通过配置移除多盘口玩法中多余的盘口
        StandardMatchMarketMessage standardMatchMarketMessage = buildStandardMatchMarketMessage(linkId, standardMatchInfo, standardMarketMessageAllList);
        Request<StandardMatchMarketMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        if (isXts&&!CollectionUtils.isEmpty(standardMarketMessageAllList) && !Objects.equals(NUMBER_TWO, standardMarketMessageAllList.get(0).getMarketType())) {
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
            matchTradType = getMatchTradeCacheConfig(standardMarketMessageAllList.get(0).getMarketType() == 1 ? standardSportMarketSell.getPreRiskManagerCode() : standardSportMarketSell.getLiveRiskManagerCode());
            log.info("::{}::,计算赔率分组 代操盘 数据源Code:{},是否计算分组赔率 ：{}", linkId,standardMatchInfo.getRiskManagerCode(),matchTradType);
        }
        calculateOdds(linkId, standardMatchInfo, matchTradType, standardMatchMarketMessage, dataSourceTime);

        //第一个参数表示topic:tag
        //2780需求： 常规赛事赔率和电子赔率topic拆分
        if (1 != standardMatchInfo.getMatchType()) {
            StandardMatchInfo standartMatch = standardMatchInfoService.getItem(standardMatchInfo.getId());
            if (null != standartMatch && NUMBER_TWO == standartMatch.getMatchType()) {
                mqDelegate.asyncSend(
                        "STANDARD_MARKET_ODDS_ESPORT:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                        builder.build(),
                        new SendCallback() {
                            @Override
                            public void onSuccess(SendResult sendResult) {
                                log.info("::{}::,STANDARD_MARKET_ODDS_ESPORT send successful", linkId);
                            }

                            @Override
                            public void onException(Throwable throwable) {
                                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS_ESPORT", throwable);
                            }
                        },
                        standardMatchInfo.getId());
                return standardMatchMarketMessage;
            }
        }
        mqDelegate.asyncSend("STANDARD_MARKET_ODDS:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                builder.build(),
                new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("::{}:: STANDARD_MARKET_ODDS,send successful", linkId);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS", throwable);
                    }
                },
                standardMatchInfo.getId());
        //sendLevel1Odds( linkId, standardMatchInfo,matchTradType, standardMatchMarketMessage,dataSourceTime);
        return standardMatchMarketMessage;
    }
    private StandardMatchMarketMessage sendMq(StandardMatchMarketMessage standardMatchMarketMessage,
                                              String linkId,StandardMatchInfo standardMatchInfo,Long dataSourceTime,boolean isXts){
        String key = Constant.REDIS_KEY.RONGHE_BASKET_ADD_CONFIG;
        Map<String,Boolean> map = redisService.hGetAll(key);
        //冠军等赛事standardTournamentId可能为null，为null时不查询联赛，避免@Cacheable生成null key报错
        Long standardTournamentId = standardMatchInfo.getStandardTournamentId();
        StandardSportTournament standardSportTournament = null == standardTournamentId ? null :
                standardSportTournamentService.getItem(standardTournamentId);
        boolean tournamentStatusBoolean = true;
        boolean tournamentStatus = false;
        if (map == null || standardSportTournament==null || standardSportTournament.getTournamentLevel()==null){
            tournamentStatusBoolean = false;
            log.info("::{}::tournamentStatus 11111,isEnabled:{},tournamentStatusBoolean:{},isXts:{}", linkId,isEnabled,tournamentStatusBoolean,isXts);
        } else {
            tournamentStatus = map.getOrDefault(standardSportTournament.getTournamentLevel().toString(),Boolean.FALSE);
            if (!tournamentStatus){
                tournamentStatusBoolean = false;
                log.info("::{}::tournamentStatus 22222,isEnabled:{},tournamentStatusBoolean:{},isXts:{}", linkId,isEnabled,tournamentStatusBoolean,isXts);
            }
        }
        String sendKey = Constant.REDIS_KEY.RONGHE_BASKET_HAVE_SEND_A+standardMatchInfo.getId();
        Map<String,Long> obj = redisService.hGetAll(sendKey);
        boolean marketListNull = standardMatchMarketMessage.getMarketList()==null;
        if (marketListNull){
            Request<StandardMatchMarketMessage> request = new Request<>();
            request.setData(standardMatchMarketMessage);
            request.setLinkId(linkId);
            request.setDataSourceTime(dataSourceTime);
            MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            mqDelegate.asyncSend("STANDARD_MARKET_ODDS:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                    builder.build(),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("::{}:: STANDARD_MARKET_ODDS,send successful", linkId);
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS", throwable);
                        }
                    },
                    standardMatchInfo.getId());
            return standardMatchMarketMessage;
        }
        //支持A+的玩法
        boolean finalTournamentStatusBoolean = tournamentStatusBoolean;
        List<StandardMarketMessage> marketListA = standardMatchMarketMessage.getMarketList().stream().filter(e->(standardMatchInfo.getSportId()==2 && MarginCategoryConfig.A_MARGIN_CATEGORY.contains(e.getMarketCategoryId())&& !isXts&& finalTournamentStatusBoolean)||(obj!=null&&obj.containsKey(e.getMarketCategoryId().toString()))).collect(Collectors.toList());
        List<Long> marketListAId = marketListA.stream().map(e->e.getRelationMarketId()).collect(Collectors.toList());
        //普通玩法
        List<StandardMarketMessage> marketListB = standardMatchMarketMessage.getMarketList().stream().filter(e->!marketListAId.contains(e.getRelationMarketId())).collect(Collectors.toList());
        log.info("::{}::tournamentStatus:{},isEnabled:{},tournamentStatusBoolean:{},obj:{},isXts:{},marketListA:{},marketListB:{}", linkId, tournamentStatus,isEnabled,tournamentStatusBoolean, obj,isXts,marketListA.size(),marketListB.size());

        if (marketListA!=null&&!marketListA.isEmpty()){
            standardMatchMarketMessage.setMarketList(marketListA);
            Request<StandardMatchMarketMessage> request = new Request<>();
            request.setData(standardMatchMarketMessage);
            request.setLinkId(linkId);
            request.setDataSourceTime(dataSourceTime);
            MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            mqDelegate.asyncSend("STANDARD_MARKET_ODDS_A_SPECIAL:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                    builder.build(),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("::{}:: STANDARD_MARKET_ODDS_A_SPECIAL,send successful", linkId);
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS_A_SPECIAL", throwable);
                        }
                    },
                    standardMatchInfo.getId());
            //sendLevel1Odds( linkId, standardMatchInfo,matchTradType, standardMatchMarketMessage,dataSourceTime);
            Map<String,Long> sendCategoryMap = new HashMap<>();
            for(StandardMarketMessage standardMarketMessage : marketListA){
                sendCategoryMap.put(standardMarketMessage.getMarketCategoryId().toString(),standardSportTournament.getId());
            }
            redisService.hSetAll(sendKey,sendCategoryMap,marketCacheTime(standardMatchInfo.getBeginTime()));
        }
        if (marketListB!=null&&!marketListB.isEmpty()){
            standardMatchMarketMessage.setMarketList(marketListB);
            Request<StandardMatchMarketMessage> request = new Request<>();
            request.setData(standardMatchMarketMessage);
            request.setLinkId(linkId);
            request.setDataSourceTime(dataSourceTime);
            MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            mqDelegate.asyncSend("STANDARD_MARKET_ODDS:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                    builder.build(),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("::{}:: STANDARD_MARKET_ODDS,send successful", linkId);
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS", throwable);
                        }
                    },
                    standardMatchInfo.getId());
            //sendLevel1Odds( linkId, standardMatchInfo,matchTradType, standardMatchMarketMessage,dataSourceTime);
        }
        return standardMatchMarketMessage;
    }
    public StandardMatchMarketMessage standardMarketOddsAsyncSend(String linkId,int oddsSource,Long operaterId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList, Long dataSourceTime, boolean matchTradType) {
        boolean isXts = matchTradType;
        checkWinnerBasket(linkId,standardMatchInfo,standardMarketMessageAllList,isXts);
        //所有维护中数据源盘口关闭
        maintainDataSourceProcessor.underMaintenanceMarketClose(linkId, standardMatchInfo.getId(), standardMarketMessageAllList);
        standardMatchCategoryRemovedProcessor.marketRemovedClose(linkId, standardMatchInfo.getId(), standardMarketMessageAllList);
        //通过配置移除多盘口玩法中多余的盘口
        StandardMatchMarketMessage standardMatchMarketMessage = buildStandardMatchMarketMessage(linkId, standardMatchInfo, standardMarketMessageAllList);
        standardMatchMarketMessage.setOddsSource(oddsSource);
        Request<StandardMatchMarketMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        request.setOperaterId(operaterId);
        request.setOddsSource(oddsSource);
        MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        if (isXts&&!CollectionUtils.isEmpty(standardMarketMessageAllList) && !Objects.equals(NUMBER_TWO, standardMarketMessageAllList.get(0).getMarketType())) {
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
            matchTradType = getMatchTradeCacheConfig(standardMarketMessageAllList.get(0).getMarketType() == 1 ? standardSportMarketSell.getPreRiskManagerCode() : standardSportMarketSell.getLiveRiskManagerCode());
            log.info("::{}::,计算赔率分组 代操盘 数据源Code:{},是否计算分组赔率 ：{}", linkId,standardMatchInfo.getRiskManagerCode(),matchTradType);
        }
        calculateOdds(linkId, standardMatchInfo, matchTradType, standardMatchMarketMessage, dataSourceTime);

        //第一个参数表示topic:tag
        //2780需求： 常规赛事赔率和电子赔率topic拆分
        if (1 != standardMatchInfo.getMatchType()) {
            StandardMatchInfo standartMatch = standardMatchInfoService.getItem(standardMatchInfo.getId());
            if (null != standartMatch && NUMBER_TWO == standartMatch.getMatchType()) {
                mqDelegate.asyncSend(
                        "STANDARD_MARKET_ODDS_ESPORT:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                        builder.build(),
                        new SendCallback() {
                            @Override
                            public void onSuccess(SendResult sendResult) {
                                log.info("::{}::,STANDARD_MARKET_ODDS_ESPORT send successful", linkId);
                            }

                            @Override
                            public void onException(Throwable throwable) {
                                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS_ESPORT", throwable);
                            }
                        },
                        standardMatchInfo.getId());
                return standardMatchMarketMessage;
            }
        }
        return sendMq(standardMatchMarketMessage,linkId,standardMatchInfo,dataSourceTime,isXts);
    }
    @Autowired
    private MarketCategorySellService marketCategorySellService;

    /**
     * 足球独赢赔率0.5球头兜底
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageAllList
     * @param isXts
     */
    private void checkWinnerFootBall(String linkId,StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList,boolean isXts){
        log.info("::{}:: 足球独赢赔率重新计算成功 true:{},matchType:{},matchType2:{}", linkId,(!StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) || isXts),standardMatchInfo.getMatchType(),standardMatchInfo.getMatchType()!=1);
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) || isXts){
            return;
        }
        if (standardMarketMessageAllList == null || CollectionUtils.isEmpty(standardMarketMessageAllList)){
            return;
        }
        int marketType = standardMarketMessageAllList.get(0).getMarketType();
        Set<String> marketSellKeys = MarginCategoryConfig.HANDICAP_WINNER_FOOTBALL_WINNER_LIST.stream().map(inner->{
            return standardMatchInfo.getId() + "-" + inner + "-" + marketType;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        List<MarketCategorySell> marketCategorySells = marketCategorySellService.getItems(marketSellKeys.stream().collect(Collectors.toList()));
        Map<Long,Integer> map = marketCategorySells.stream().filter(e->e.getMarketCategoryId()!=null&&e.getMarketCount()!=null).collect(Collectors.toMap(MarketCategorySell::getMarketCategoryId,MarketCategorySell::getMarketCount));
        List<StandardMarketMessage> marketList = standardMarketMessageAllList.stream().filter(e->MarginCategoryConfig.HANDICAP_WINNER_FOOTBALL_WINNER_LIST.contains(e.getMarketCategoryId())
                &&e.getThirdMarketSourceStatus()==Constant.SPORT_MARKET.STATUS.ACTIVE
                &&((map.containsKey(e.getMarketCategoryId()) && map.get(e.getMarketCategoryId())!=null &&map.get(e.getMarketCategoryId())>=e.getPlaceNum())||!map.containsKey(e.getMarketCategoryId()))).collect(Collectors.toList());
        Map<Long,List<StandardMarketMessage>> mapList = marketList.stream().collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
        if (mapList.isEmpty()){
            return;
        }
        String key = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_FOOTBALL_WINNER_HANDCIP+standardMatchInfo.getId()+"-"+marketType;
        Map<String,List<StandardMarketMessage>> temp = redisService.hGetAll(key);
        log.info("::{}:: 足球独赢赔率重新计算成功 marketList:{},temp:{}", linkId,marketList.size(),temp.size());

        for (Map.Entry<String, List<StandardMarketMessage>> entry : temp.entrySet()) {
            List<StandardMarketMessage> messageList = entry.getValue();
            Iterator<StandardMarketMessage> iterator = messageList.iterator();
            while (iterator.hasNext()) {
                StandardMarketMessage message = iterator.next();
                if (!Constant.SPORT_MARKET.STATUS.ACTIVE.equals(message.getThirdMarketSourceStatus())) {
                    iterator.remove();
                }
            }
        }
        log.info("::{}:: 足球独赢赔率重新计算成功 marketList:{},temp:{}", linkId,marketList.size(),temp.size());
        Map<String,List<StandardMarketMessage>> mapListTmep = standardMarketMessageAllList.stream().filter(e->MarginCategoryConfig.HANDICAP_WINNER_LIST_BASKET.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(e->e.getMarketCategoryId().toString()));
        redisService.hSetAll(key,mapListTmep,marketCacheTime(standardMatchInfo.getBeginTime()));
        mapList.forEach((k,v)->{
            if (MarginCategoryConfig.FOOT_BALL_HANDICAP_WINNER_MAP.containsKey(k)){
                log.info("::{}:: 足球独赢赔率重新计算成功 calculateWinnerByHandicap:{}", linkId,k);
                calculateFootBallWinnerByHandicap(linkId,standardMatchInfo.getId(),v,mapList.get(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET.get(k)),temp,standardMarketMessageAllList,standardMatchInfo);
            }
            //根据独赢触发校验独赢赔率
            if (MarginCategoryConfig.FOOT_BALL_WINNER_HANDICAP_MAP.containsKey(k) && !mapList.containsKey(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET_2.get(k))){
                log.info("::{}:: 足球独赢赔率重新计算成功 calculateWinnerByWinner:{}", linkId,k);
                calculateFootBallWinnerByWinner(linkId,standardMatchInfo.getId(),v,mapList.get(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET_2.get(k)),temp,standardMarketMessageAllList,standardMatchInfo);
            }
        });

    }
    /**
     * 投递下游，让分独赢玩法特殊球头兜底
     * 篮球 +-1.5,+-0.5
     */
    private void checkWinnerBasket(String linkId,StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList,boolean isXts){
        log.info("::{}:: 独赢赔率重新计算成功 true:{},matchType:{},matchType2:{}", linkId,(!StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) || isXts),standardMatchInfo.getMatchType(),standardMatchInfo.getMatchType()!=1);
        if (!StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) || isXts){
            return;
        }
        if (standardMarketMessageAllList == null || CollectionUtils.isEmpty(standardMarketMessageAllList)){
            return;
        }
        int marketType = standardMarketMessageAllList.get(0).getMarketType();
        // 获取玩法开售
        Set<String> marketSellKeys = MarginCategoryConfig.HANDICAP_WINNER_LIST_BASKET.stream().map(inner->{
            return standardMatchInfo.getId() + "-" + inner + "-" + marketType;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        List<MarketCategorySell> marketCategorySells = marketCategorySellService.getItems(marketSellKeys.stream().collect(Collectors.toList()));
        //log.info("::{}:: 独赢赔率重新计算成功 marketCategorySells:{}", linkId,JSONUtil.toJsonStr(marketCategorySells));
        Map<Long,Integer> map = marketCategorySells.stream().filter(e->e.getMarketCategoryId()!=null&&e.getMarketCount()!=null).collect(Collectors.toMap(MarketCategorySell::getMarketCategoryId,MarketCategorySell::getMarketCount));
        List<StandardMarketMessage> marketList = standardMarketMessageAllList.stream().filter(e->MarginCategoryConfig.HANDICAP_WINNER_LIST_BASKET.contains(e.getMarketCategoryId())
                &&e.getThirdMarketSourceStatus()==Constant.SPORT_MARKET.STATUS.ACTIVE
                &&((map.containsKey(e.getMarketCategoryId()) && map.get(e.getMarketCategoryId())!=null &&map.get(e.getMarketCategoryId())>=e.getPlaceNum())||!map.containsKey(e.getMarketCategoryId()))).collect(Collectors.toList());
        Map<Long,List<StandardMarketMessage>> mapList = marketList.stream().collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
        String key = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_ODDS_WINNER_HANDCIP+standardMatchInfo.getId()+"-"+marketType;
        Map<String,List<StandardMarketMessage>> temp = redisService.hGetAll(key);
        for (Map.Entry<String, List<StandardMarketMessage>> entry : temp.entrySet()) {
            List<StandardMarketMessage> messageList = entry.getValue();
            Iterator<StandardMarketMessage> iterator = messageList.iterator();
            while (iterator.hasNext()) {
                StandardMarketMessage message = iterator.next();
                if (!Constant.SPORT_MARKET.STATUS.ACTIVE.equals(message.getThirdMarketSourceStatus())) {
                    iterator.remove();
                }
            }
        }
        log.info("::{}:: 独赢赔率重新计算成功 marketList:{},temp:{}", linkId,marketList.size(),temp.size());
        Map<String,List<StandardMarketMessage>> mapListTmep = standardMarketMessageAllList.stream().filter(e->MarginCategoryConfig.HANDICAP_WINNER_LIST_BASKET.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(e->e.getMarketCategoryId().toString()));
        redisService.hSetAll(key,mapListTmep,marketCacheTime(standardMatchInfo.getBeginTime()));
        mapList.forEach((k,v)->{
            //根据让分触发校验独赢赔率
            if (MarginCategoryConfig.HANDICAP_WINNER_LIST_BASKET_1.contains(k)){
                log.info("::{}:: 独赢赔率重新计算成功 calculateWinnerByHandicap:{}", linkId,k);
                calculateWinnerByHandicap(linkId,standardMatchInfo.getId(),v,mapList.get(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET.get(k)),temp,standardMarketMessageAllList,standardMatchInfo);
            }
            //根据独赢触发校验独赢赔率
            if (MarginCategoryConfig.HANDICAP_WINNER_LIST_BASKET_2.contains(k) && !mapList.containsKey(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET_2.get(k))){
                log.info("::{}:: 独赢赔率重新计算成功 calculateWinnerByWinner:{}", linkId,k);
                calculateWinnerByWinner(linkId,standardMatchInfo.getId(),v,mapList.get(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET_2.get(k)),temp,standardMarketMessageAllList,standardMatchInfo);
            }
        });
    }
    private Long marketCacheTime(Long beginTime) {
        if (beginTime == null || beginTime == 0) {
            return RedisConfig.REDIS_DEFAULT_TIME.longValue();
        }
        //获取剩余开赛时间 =  开赛时间-当前时间
        Long cacheTime = (beginTime - Calendar.getInstance().getTimeInMillis());
        if (cacheTime <= 0) {
            return RedisConfig.REDIS_DEFAULT_TIME.longValue();
        }
        //redis过期时间为秒 = 剩余开赛时间 + 2天时间 ，为redis过期时间
        return (cacheTime / 1000) + (2L * RedisConfig.REDIS_DEFAULT_TIME);
    }
    private void calculateFootBallWinnerByHandicap(String linkId,
                                           List<StandardMarketMessage> winnerMarket,
                                           List<StandardMarketMessage> handicapMarket,
                                           Map<String,List<StandardMarketMessage>> temp,
                                           StandardMatchInfo standardMatchInfo){
        if (winnerMarket == null || handicapMarket.isEmpty()){
            return;
        }
    }
    private void calculateFootBallWinnerByHandicap(String linkId,Long matchId,
                                           List<StandardMarketMessage> standardMarketMessages1,
                                           List<StandardMarketMessage> standardMarketMessages2,
                                           Map<String,List<StandardMarketMessage>> temp,
                                           List<StandardMarketMessage> resultList,
                                           StandardMatchInfo standardMatchInfo){
        if (standardMarketMessages1 == null || standardMarketMessages1.isEmpty()){
            log.info("::{}:: 足球独赢赔率重新计算成功 message1:{}", (standardMarketMessages1 == null || standardMarketMessages1.isEmpty()));
            return;
        }
        Long categoryId = standardMarketMessages1.get(0).getMarketCategoryId();
        //如果独赢在这个linkid没有，需要新增
        boolean needAdd = false;
        if (standardMarketMessages2 == null || standardMarketMessages2.isEmpty()){
            standardMarketMessages2 = temp.get(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET.get(categoryId).toString());
            log.info("::{}:: 足球独赢赔率重新计算成功 message2:{}", linkId,(standardMarketMessages2 == null || standardMarketMessages2.isEmpty()));
            needAdd = true;
        }
        if (standardMarketMessages2 ==null || standardMarketMessages2.isEmpty()){
            log.info("::{}:: 足球独赢赔率重新计算成功 message22:{}", linkId,(standardMarketMessages2 ==null || standardMarketMessages2.isEmpty()));
            return;
        }
        boolean flag = calculateFootBallWinnerMarket(linkId,matchId,categoryId,standardMarketMessages1,standardMarketMessages2,standardMatchInfo);
        if (needAdd && flag){
            log.info("::{}:: 足球独赢赔率重新计算成功 needAdd:{}",linkId,needAdd );
            resultList.addAll(standardMarketMessages2);
        }
    }
    //根据让分计算校验独赢赔率
    private void calculateWinnerByHandicap(String linkId,Long matchId,
                                           List<StandardMarketMessage> standardMarketMessages1,
                                           List<StandardMarketMessage> standardMarketMessages2,
                                           Map<String,List<StandardMarketMessage>> temp,
                                           List<StandardMarketMessage> resultList,
                                           StandardMatchInfo standardMatchInfo){
        if (standardMarketMessages1 == null || standardMarketMessages1.isEmpty()){
            log.info("::{}:: 独赢赔率重新计算成功 message1:{}", (standardMarketMessages1 == null || standardMarketMessages1.isEmpty()));
            return;
        }
        Long categoryId = standardMarketMessages1.get(0).getMarketCategoryId();
        //如果独赢在这个linkid没有，需要新增
        boolean needAdd = false;
        if (standardMarketMessages2 == null || standardMarketMessages2.isEmpty()){
            standardMarketMessages2 = temp.get(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET.get(categoryId).toString());
            log.info("::{}:: 独赢赔率重新计算成功 message2:{}", linkId,(standardMarketMessages2 == null || standardMarketMessages2.isEmpty()));
            needAdd = true;
        }
        if (standardMarketMessages2 ==null || standardMarketMessages2.isEmpty()){
            log.info("::{}:: 独赢赔率重新计算成功 message22:{}", linkId,(standardMarketMessages2 ==null || standardMarketMessages2.isEmpty()));
            return;
        }
        boolean flag = calculateWinnerMarket(linkId,matchId,categoryId,standardMarketMessages1,standardMarketMessages2,standardMatchInfo);
        if (needAdd && flag){
            log.info("::{}:: 独赢赔率重新计算成功 needAdd:{}",linkId,needAdd );
            resultList.addAll(standardMarketMessages2);
        }
    }
    //根据独赢计算校验独赢赔率
    private void calculateFootBallWinnerByWinner(String linkId,Long matchId,
                                         List<StandardMarketMessage> standardMarketMessages1,
                                         List<StandardMarketMessage> standardMarketMessages2,
                                         Map<String,List<StandardMarketMessage>> temp,
                                         List<StandardMarketMessage> resultList,
                                         StandardMatchInfo standardMatchInfo){
        if (standardMarketMessages1 == null || standardMarketMessages1.isEmpty()){
            return;
        }
        Long categoryId = standardMarketMessages1.get(0).getMarketCategoryId();
        if (standardMarketMessages2 == null || standardMarketMessages2.isEmpty()){
            standardMarketMessages2 = temp.get(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET_2.get(categoryId).toString());
        }
        if (standardMarketMessages2 ==null || standardMarketMessages2.isEmpty()){
            return;
        }
        calculateFootBallWinnerMarket(linkId,matchId,categoryId,standardMarketMessages2,standardMarketMessages1,standardMatchInfo);
    }
    private void calculateWinnerByWinner(String linkId,Long matchId,
                                         List<StandardMarketMessage> standardMarketMessages1,
                                         List<StandardMarketMessage> standardMarketMessages2,
                                         Map<String,List<StandardMarketMessage>> temp,
                                         List<StandardMarketMessage> resultList,
                                         StandardMatchInfo standardMatchInfo){
        if (standardMarketMessages1 == null || standardMarketMessages1.isEmpty()){
            return;
        }
        Long categoryId = standardMarketMessages1.get(0).getMarketCategoryId();
        if (standardMarketMessages2 == null || standardMarketMessages2.isEmpty()){
            standardMarketMessages2 = temp.get(MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET_2.get(categoryId).toString());
        }
        if (standardMarketMessages2 ==null || standardMarketMessages2.isEmpty()){
            return;
        }
        calculateWinnerMarket(linkId,matchId,categoryId,standardMarketMessages2,standardMarketMessages1,standardMatchInfo);
    }
    private boolean calculateFootBallWinnerMarket(String linkId,Long matchId,Long categoryId,List<StandardMarketMessage> handcipMarkets,
                                          List<StandardMarketMessage> winnerMarkets,StandardMatchInfo standardMatchInfo){
        return false;
    }

    private boolean calculateWinnerMarket(String linkId,Long matchId,Long categoryId,List<StandardMarketMessage> handcipMarkets,
                                       List<StandardMarketMessage> winnerMarkets,StandardMatchInfo standardMatchInfo){
        boolean result = false;
        String key = Constant.REDIS_KEY.RONGHE_BASKET_MARKET_WINNER_CONFIG;
        Object obj  = redisService.hGetAll(key);
        log.info("::{}:: 独赢赔率重新计算成功 obj:{}", linkId, JSONUtil.toJsonStr(obj));
        if (obj == null){
            return result;
        }
        Map<String,BasketballConfigDTO> basketballConfigDTOS = (Map<String,BasketballConfigDTO>)obj;
        //独赢赔率
        StandardMarketMessage standardMarketMessage = winnerMarkets.get(0);
        if (standardMarketMessage.getStatus()  >= 2){
            return result;
        }

        StandardMarketOddsMessage winnerOddsOddsType1 = standardMarketMessage.getMarketOddsList().stream().filter(e->e.getOddsType().equals("1")).findFirst().get();
        StandardMarketOddsMessage winnerOddsOddsType2 = standardMarketMessage.getMarketOddsList().stream().filter(e->e.getOddsType().equals("2")).findFirst().get();
        for (StandardMarketMessage handcip : handcipMarkets){
            if (handcip.getStatus() >= 2 ||
                    handcip.getMarketOddsList() == null ||
                    handcip.getMarketOddsList().isEmpty()) {
                continue;
            }
            if (handcip.getAddition1().equals("1.5")
                    ||handcip.getAddition1().equals("-1.5")
                    ||handcip.getAddition1().equals("0.5")
                    ||handcip.getAddition1().equals("-0.5")){
                double add1 = Double.valueOf(handcip.getAddition1());
                if (standardMarketMessage.getMarketCategoryId() == 37 && Math.abs(add1) == 0.5){
                    continue;
                }
/*                if (MarginCategoryConfig.HANDICAP_WINNER_MAP_BASKET_2.containsKey(standardMarketMessage.getMarketCategoryId())
                    &&standardMarketMessage.getMarketCategoryId() != 37){
                    continue;
                }*/
                log.info("::{}:: 独赢赔率重新计算成功 basketballConfigDTOS:{}", linkId, JSONUtil.toJsonStr(basketballConfigDTOS));
                if (!basketballConfigDTOS.containsKey(String.valueOf(Math.abs(add1)))){
                    log.info("::{}:: 独赢赔率重新计算成功 continue:{}",linkId,(!basketballConfigDTOS.containsKey(String.valueOf(Math.abs(add1)))) );
                    continue;
                }
                BasketballConfigDTO basketballConfigDTO = basketballConfigDTOS.get(String.valueOf(Math.abs(add1)));
                log.info("::{}:: 独赢赔率重新计算成功 basketballConfigDTO:{}", linkId, JSONUtil.toJsonStr(basketballConfigDTO));
                if (basketballConfigDTO.getStatus() == 0){
                    continue;
                }

                List<StandardMarketOddsMessage> marketOddsList = handcip.getMarketOddsList();
                StandardMarketOddsMessage handcipOddsType1 = marketOddsList.stream().filter(e->e.getOddsType().equals("1")).findFirst().get();
                StandardMarketOddsMessage handcipOddsType2 = marketOddsList.stream().filter(e->e.getOddsType().equals("2")).findFirst().get();
                log.info("::{}:: 独赢赔率重新计算成功 win1:{},win2:{},h1:{},h2:{}", linkId,winnerOddsOddsType1.getPaOddsValue(),winnerOddsOddsType2.getPaOddsValue(),
                        handcipOddsType1.getPaOddsValue(),handcipOddsType2.getPaOddsValue());
                if (check(add1,winnerOddsOddsType1.getPaOddsValue(),winnerOddsOddsType2.getPaOddsValue(),
                        handcipOddsType1.getPaOddsValue(),handcipOddsType2.getPaOddsValue())){
                    continue;
                }
                if (add1 > 0){
                    log.info("::{}:: 独赢赔率重新计算成功 win1:{},win2:{}", linkId,handcipOddsType1.getPaOddsValue(),handcipOddsType2.getPaOddsValue());
                    Integer win1 =handcipOddsType1.getPaOddsValue() + getIngegerOdds(basketballConfigDTO);
                    Integer win2 = calculateOtherOdds(linkId,matchId,winnerMarkets.get(0).getMarketCategoryId(),win1);
                    log.info("::{}:: 独赢赔率重新计算成功 win1:{},win2:{}", linkId,win1,win2);
                    if (!check(add1,win1,win2,handcipOddsType1.getPaOddsValue(),handcipOddsType2.getPaOddsValue())){
                        win2 = handcipOddsType2.getPaOddsValue() - getIngegerOdds(basketballConfigDTO);
                        win1 = calculateOtherOdds(linkId,matchId,winnerMarkets.get(0).getMarketCategoryId(),win2);
                        log.info("::{}:: 独赢赔率重新计算成功 win1:{},win2:{}", linkId,win1,win2);
                    }
                    if(check(add1,win1,win2,handcipOddsType1.getPaOddsValue(),handcipOddsType2.getPaOddsValue())){
                        winnerOddsOddsType1.setPaOddsValue(win1);
                        winnerOddsOddsType2.setPaOddsValue(win2);
                        standardMarketMessage.setRemark(standardMarketMessage.getRemark()+",独赢赔率重新计算");
                        result = true;
                        log.info("::{}:: 独赢赔率重新计算成功 win1:{},win2:{} 设置成功", linkId,win1,win2);
                    }
                }
                if (add1 < 0){
                    log.info("::{}:: 独赢赔率重新计算成功 win1:{},win2:{}", linkId,handcipOddsType1.getPaOddsValue(),handcipOddsType2.getPaOddsValue());
                    Integer win1 =handcipOddsType1.getPaOddsValue() - getIngegerOdds(basketballConfigDTO);
                    Integer win2 = calculateOtherOdds(linkId,matchId,winnerMarkets.get(0).getMarketCategoryId(),win1);
                    log.info("::{}:: 独赢赔率重新计算成功 win1:{},win2:{}", linkId,win1,win2);
                    if (!check(add1,win1,win2,handcipOddsType1.getPaOddsValue(),handcipOddsType2.getPaOddsValue())){
                        win2 = handcipOddsType2.getPaOddsValue() + getIngegerOdds(basketballConfigDTO);
                        win1 = calculateOtherOdds(linkId,matchId,winnerMarkets.get(0).getMarketCategoryId(),win2);
                        log.info("::{}:: 独赢赔率重新计算成功 win1:{},win2:{}", linkId,win1,win2);
                    }
                    if(check(add1,win1,win2,handcipOddsType1.getPaOddsValue(),handcipOddsType2.getPaOddsValue())){
                        winnerOddsOddsType1.setPaOddsValue(win1);
                        winnerOddsOddsType2.setPaOddsValue(win2);
                        standardMarketMessage.setRemark(standardMarketMessage.getRemark()+",独赢赔率重新计算");
                        result = true;
                        log.info("::{}:: 独赢赔率重新计算成功 win1:{},win2:{} 设置成功", linkId,win1,win2);
                    }
                }

            }
        }
        if (true){
            Map<String, ConfigMarketTradeItem> configMarketTradeItemMap = configMarketTradeItemService.getItemByMatchAndCategorys(standardMatchInfo.getId(), new HashSet<>(Arrays.asList(standardMarketMessage.getMarketCategoryId())));
            ConfigTournamentTradeItem tournamentTradeItem = configTournamentTradeItemService.getItem(standardMatchInfo.getSportId(), standardMatchInfo.getStandardTournamentId(), standardMarketMessage.getMarketType());
            thirdMatchMarketProcessor.marketOddsVerify(linkId, standardMatchInfo, configMarketTradeItemMap, standardMarketMessage, tournamentTradeItem);
            processOddsValueDecimals( linkId,  standardMarketMessage,  standardMatchInfo);
        }
        return result;
    }
    public void processOddsValueDecimals(String linkId, StandardMarketMessage standardMarketMessage, StandardMatchInfo standardMatchInfo) {
        //校验赛种,玩法MY赔率不走赔率优化计算
        if (MarginCategoryConfig.ODDS_GRACEFUL_SPORT.contains(standardMatchInfo.getSportId())) {
            List<Long> marketCategoryIdMALAY = MarginCategoryConfig.SPORT_MY_CATEGORY.get(standardMatchInfo.getSportId());
            Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
            if (marketCategoryIdMALAY.contains(marketCategoryId)) {
                log.info("::{}::MY玩法不做赔率优化,赛事ID:{},标准盘口ID:{},玩法:{}", linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), marketCategoryId);
                return;
            }
        }
        //使用数据源抽水赔，走赔率优化
        if (!CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList())) {
            log.info("::{}:: 开始处理 processOddsValueDecimals,标准盘口id:{}", linkId, standardMarketMessage.getId());
            for (StandardMarketOddsMessage oddsMessage : standardMarketMessage.getMarketOddsList()) {
                try {
                    if (null == oddsMessage.getPaOddsValue() || 0 == oddsMessage.getPaOddsValue()) {
                        continue;
                    }
                    BigDecimal bigDecimal = new BigDecimal(oddsMessage.getPaOddsValue()).divide(new BigDecimal(100000), 2, BigDecimal.ROUND_DOWN);
                    int left = bigDecimal.intValue();
                    int right = bigDecimal.subtract(new BigDecimal(left)).multiply(new BigDecimal(100)).intValue();
                    Integer paOddsValue = 0;
                    if (left >= 3 && left < 5) {
                        if (right < 5) {
                            paOddsValue = bigDecimal.intValue() * 100000;
                        } else {
                            BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(5), 0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(0.05));
                            paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
                        }
                    } else if (left >= 5 && left < 10) {
                        if (right < 10) {
                            paOddsValue = bigDecimal.intValue() * 100000;
                        } else {
                            BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(100), 1, BigDecimal.ROUND_DOWN);
                            paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
                        }
                    } else if (left >= 10 && left < 20) {
                        if (right < 50) {
                            paOddsValue = bigDecimal.intValue() * 100000;
                        } else {
                            paOddsValue = new BigDecimal(left).add(new BigDecimal(0.5)).multiply(new BigDecimal(100000)).intValue();
                        }
                    } else if (left >= 20) {
                        paOddsValue = left * 100000;
                    }
                    BigDecimal oneHundredThousand = new BigDecimal("100000");
                    log.info("::{}::processOddsValueDecimals,投注项id:{},left:{},right:{},优化前paOddsValue:{},优化后paOddsValue:{}", linkId, oddsMessage.getId(), left, right, oddsMessage.getPaOddsValue(), paOddsValue);
                    if (paOddsValue != 0) {
                        oddsMessage.setPaOddsValue(new BigDecimal(paOddsValue).divide(oneHundredThousand).setScale(2, BigDecimal.ROUND_DOWN).multiply(oneHundredThousand).intValue());
                    } else {
                        oddsMessage.setPaOddsValue(bigDecimal.multiply(oneHundredThousand).intValue());
                    }
                } catch (Exception e) {
                    log.error("::{}::processOddsValueDecimals标准投注项id:{},两项盘小数位优化error:{}", linkId, oddsMessage.getId(), e);
                }
            }
        }
    }



    private Integer calculateOtherOdds(String linkId,Long matchId,Long marketCategoryId,Integer win1){
        ConfigMarketCategoryMargin configMargin = configMarketCategoryMarginService.getItemTwo(linkId, matchId, marketCategoryId, marketCategoryId, 1);
        Double marginOdds = 110D;
        if (configMargin != null && configMargin.getMargin() >= 1) {
            marginOdds = configMargin.getMargin();
        }
        double marginAverage = BigDecimalUtils.divide(BigDecimalUtils.subtract(marginOdds, 100), 200);
        double temp = BigDecimalUtils.divide(win1, 100000);
        double temp2 = BigDecimalUtils.subtract(BigDecimalUtils.divide(marginOdds,100),BigDecimalUtils.divide(1,temp));
        //最终PA赔率概率
        double paOdds = BigDecimalUtils.divide(1, temp2);
        return BigDecimalUtils.multiply(paOdds, 100000).intValue();
    }

    public static void main(String[] args) {
        Integer win1 = 198000;
        Double marginOdds = 106D;
        double marginAverage = BigDecimalUtils.divide(BigDecimalUtils.subtract(marginOdds, 100), 200);
        double temp = BigDecimalUtils.divide(win1, 100000);
        double temp2 = BigDecimalUtils.subtract(BigDecimalUtils.divide(marginOdds,100),BigDecimalUtils.divide(1,temp));
        //最终PA赔率概率
        double paOdds = BigDecimalUtils.divide(1, temp2);
        System.out.println(BigDecimalUtils.multiply(paOdds, 100000).intValue());
    }


    private Integer getIngegerOdds(BasketballConfigDTO basketballConfigDTO){
        return BigDecimalUtils.multiply(basketballConfigDTO.getValue(), 100000).intValue();
    }
    private boolean check(double add1,Integer win1,Integer win2,Integer h1,Integer h2){
        if (add1 > 0 && win1>h1
                &&win2<h2){
            return true;
        }
        if (add1 < 0 && win1<h1
                &&win2>h2){
            return true;
        }
        return false;
    }

    /**
     * 赔率topic心跳
     *
     * @param linkId
     * @param dataSourceTime
     */
    public void standardMarketOddsHeartBeatSend(String linkId, Long dataSourceTime) {
        Request<StandardMatchMarketMessage> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(new StandardMatchMarketMessage());
        request.setDataType("HeartBeat");
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        mqDelegate.asyncSend("STANDARD_MARKET_ODDS:" + dataSourceTime, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}:: STANDARD_MARKET_ODDS_HEARTBEAT,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS", throwable);
            }
        });
    }

    /**
     * bug 47948 : 操盘赛事级别 开关封锁，单独下发topic，以免赔率topic堆积，导致赛事状态不能及时更新
     *
     * @param linkId
     * @param dataSourceTime
     */
    public void standardMarketOddsStateSend(String linkId, StandardMatchInfo standardMatchInfo, Long dataSourceTime) {
        StandardMatchMarketMessage standardMatchMarketMessage = buildStandardMatchMarketMessage(linkId, standardMatchInfo, null);
        Request<StandardMatchMarketMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        mqTemplate.asyncSend("STANDARD_MARKET_ODDS_STATE:" + dataSourceTime, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::, STANDARD_MARKET_ODDS_STATE send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS_STATE", throwable);
            }
        });
    }


    /**
     * 同步发送
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageAllList
     * @param dataSourceTime
     * @param matchTradType
     * @return
     */
    public StandardMatchMarketMessage standardMarketOddsSyncSend(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList, Long dataSourceTime, boolean matchTradType) {
        //通过配置移除多盘口玩法中多余的盘口
        StandardMatchMarketMessage standardMatchMarketMessage = buildStandardMatchMarketMessage(linkId, standardMatchInfo, standardMarketMessageAllList);
        Request<StandardMatchMarketMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装标准赔率消息并下发,topic:STANDARD_MARKET_ODDS,request:{}", linkId, JSON.toJSONString(request));
        mqDelegate.send("STANDARD_MARKET_ODDS:" + standardMatchMarketMessage.getStandardMatchInfoId(), builder.build());
        sendLevel1Odds(linkId, standardMatchInfo, matchTradType, standardMatchMarketMessage, dataSourceTime);
        return standardMatchMarketMessage;
    }

    private StandardMatchMarketMessage buildStandardMatchMarketMessage(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList) {
        StandardMatchMarketMessage standardMatchMarketMessage = new StandardMatchMarketMessage();
        standardMatchMarketMessage.setBeginTime(standardMatchInfo.getBeginTime());
        standardMatchMarketMessage.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setDataSourceCode(standardMatchInfo.getDataSourceCode());
        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());
        standardMatchMarketMessage.setMatchType(0);
        if (standardMatchInfo instanceof StandardMatchInfoDetail) {
            standardMatchMarketMessage.setMatchType(1);
            if (!CollectionUtils.isEmpty(standardMarketMessageAllList) && standardMarketMessageAllList.get(0).getMarketType() != 2) {
                standardMatchMarketMessage.setMatchType(0);
            }
        }
        log.info("::{}::buildStandardMatchMarketMessage,标准赛事:{}", linkId, JSONObject.toJSONString(standardMatchInfo));
        standardMatchMarketMessage.setMarketList(standardMarketMessageAllList);
        //判断冠军赛事
        boolean isOutRight = standardMatchMarketMessage.getMatchType() == 1 ? Boolean.TRUE : Boolean.FALSE;
        //兼容冠军玩法，获取标准赛事信息
        StandardMatchInfoDetail standardMatch = thirdMatchMarketProcessor.getStandardMatchInfo(isOutRight, standardMatchInfo.getId());
        log.info("::{}::buildStandardMatchMarketMessage:{},标准赛事id:{},new:{}", linkId, isOutRight, standardMatchInfo.getId(), JSONObject.toJSONString(standardMatch));
        standardMatchMarketMessage.setStatus(standardMatch.getOperateMatchStatus() == -1 ? 0 : standardMatch.getOperateMatchStatus());
        standardMatchMarketMessage.setStandardTournamentId(standardMatchInfo.getStandardTournamentId());
        return standardMatchMarketMessage;
    }

    /**
     * 下发赛事盘口数据到下游风控，异步推送
     *
     * @param linkId
     * @param standardMarketMessageAllList
     */
    public StandardMatchMarketMessage standardMarketOddsRiskAsyncSend(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList, Long dataSourceTime) {
        //所有维护中数据源盘口关闭
        maintainDataSourceProcessor.underMaintenanceMarketClose(linkId, standardMatchInfo.getId(), standardMarketMessageAllList);
        standardMatchCategoryRemovedProcessor.marketRemovedClose(linkId, standardMatchInfo.getId(), standardMarketMessageAllList);
        //通过配置移除多盘口玩法中多余的盘口
        StandardMatchMarketMessage standardMatchMarketMessage = new StandardMatchMarketMessage();
        standardMatchMarketMessage.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setDataSourceCode(standardMatchInfo.getDataSourceCode());
        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());
        standardMatchMarketMessage.setMarketList(standardMarketMessageAllList);
        standardMatchMarketMessage.setStatus(standardMatchInfo.getOperateMatchStatus() == -1 ? 0 : standardMatchInfo.getOperateMatchStatus());
        Request<StandardMatchMarketMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
//        log.info("::{}::开始组装标准赔率消息并下发,topic:STANDARD_MARKET_ODDS_RISK,request:{}", linkId, JSON.toJSONString(request));
        mqTemplate.asyncSend("STANDARD_MARKET_ODDS_RISK:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                             builder.build(),
                             new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::STANDARD_MARKET_ODDS_RISK,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS", throwable);
            }
                             });
        return standardMatchMarketMessage;
    }

    /**
     * 下发盘口名称国际化给下游
     *
     * @param linkId
     * @param i18nOutrightMarketList
     */
    public void marketNameI18nSend(String linkId,
                                   List<I18nOutrightMarket> i18nOutrightMarketList,
                                   Long standardMatchId) {
        Request<List<I18nOutrightMarket>> request = new Request<>();
        request.setData(i18nOutrightMarketList);
        request.setLinkId(linkId);
        MessageBuilder<Request<List<I18nOutrightMarket>>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装盘口名称国际化消息并下发,topic:MARKET_NAME_I18N_LIST, request:{}", linkId, JSON.toJSONString(request));
        mqDelegate.asyncSend("MARKET_NAME_I18N_LIST:", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::MARKET_NAME_I18N_LIST,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "MARKET_NAME_I18N_LIST", throwable);
            }
        }, standardMatchId);
    }

    /**
     * 下发投注项名称国际化给下游
     *
     * @param linkId
     * @param i18nOutrightMarketOddsList
     */
    public void marketOddsNameI18nSend(String linkId,
                                       List<I18nOutrightMarketOdds> i18nOutrightMarketOddsList,
                                       Long standardMatchId) {
        Request<List<I18nOutrightMarketOdds>> request = new Request<>();
        request.setData(i18nOutrightMarketOddsList);
        request.setLinkId(linkId);
        MessageBuilder<Request<List<I18nOutrightMarketOdds>>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装投注项名称国际化消息并下发,topic:MARKET_ODDS_NAME_I18N_LIST, request:{}", linkId, JSON.toJSONString(request));
        mqDelegate.asyncSend("MARKET_ODDS_NAME_I18N_LIST:", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::MARKET_ODDS_NAME_I18N_LIST,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "MARKET_NAME_I18N_LIST", throwable);
            }
        }, standardMatchId);
    }

    /**
     * 紧急关盘，推送三方关盘消息
     *
     * @param linkId
     * @param thirdMatchMarketDTO
     */
    public void closeMarket(String linkId, ThirdMatchMarketDTO thirdMatchMarketDTO) {
        Request<ThirdMatchMarketDTO> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(thirdMatchMarketDTO);
        request.setDataSourceTime(System.currentTimeMillis());
        MessageBuilder<Request<ThirdMatchMarketDTO>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装紧急关盘消息并下发,topic:THIRD_MATCH_MARKET_API, request:{}", linkId, JSON.toJSONString(request));
        mqDelegate.asyncSend("THIRD_MATCH_MARKET_API:", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::THIRD_MATCH_MARKET_API,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_MATCH_MARKET_API", throwable);
            }
        });
    }

    /**
     * 初始化风控的赔率操盘方式
     *
     * @param linkId
     * @param standardSportMarket
     */
    public void toInitTradeType(String linkId, StandardSportMarket standardSportMarket) {

        Set<Long> marketIdSet = new HashSet<>();
        marketIdSet.add(standardSportMarket.getRelationMarketId());
        Map<Long, Integer> tradeTypeMap =
                outrightTradeTypeConfigService.getTradeTypeMapByMatchId(standardSportMarket.getStandardMatchInfoId(), marketIdSet);
        Request<OutrightMarketDTO> request = new Request<>();
        OutrightMarketDTO outrightMarketDTO = new OutrightMarketDTO();
        outrightMarketDTO.setRelationMarketId( standardSportMarket.getRelationMarketId() );
        outrightMarketDTO.setStandardMatchId( standardSportMarket.getStandardMatchInfoId() );
        if ( MapUtils.isNotEmpty(tradeTypeMap) && tradeTypeMap.containsKey(standardSportMarket.getRelationMarketId()) ) {
            outrightMarketDTO.setTradeType( tradeTypeMap.get(standardSportMarket.getRelationMarketId()));
        } else {
            if (DataSourceCodeEnum.PA.name().equals(standardSportMarket.getDataSourceCode())) {
                outrightMarketDTO.setTradeType( ConstantSystem.ONE );
            } else {
                outrightMarketDTO.setTradeType( ConstantSystem.ZERO );
            }
        }
        try {
            outrightMarketDTO.setMarketStartTime( Long.parseLong(standardSportMarket.getAddition2()) );
            outrightMarketDTO.setMarketEndTime( Long.parseLong(standardSportMarket.getAddition3()) );
            outrightMarketDTO.setMarketNextCloseTime( Long.parseLong(standardSportMarket.getAddition1()) );
        } catch ( Exception e) {
            log.info("初始化盘口开始时间与结束时间异常");
            e.printStackTrace();
        }
        request.setLinkId(linkId);
        request.setData(outrightMarketDTO);
        request.setDataSourceTime(System.currentTimeMillis());
        MessageBuilder<Request<OutrightMarketDTO>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装风控操盘方式初始化消息并下发,topic:INIT_MARKET_TRADETYPE_API, request:{}", linkId, JSON.toJSONString(request));
        mqTemplate.asyncSend("INIT_MARKET_TRADETYPE_API:", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::INIT_MARKET_TRADETYPE_API,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "INIT_MARKET_TRADETYPE_API", throwable);
            }
        });
    }

    /**
     * 下发业务玩法集 状态
     *
     * @param linkId
     * @param dto
     */
    public void sendCategorySetConfig(String linkId, PutTraderConfigDTO dto) {
        Request<StandardCategorySetConfigMessage> request = new Request<>();
        StandardCategorySetConfigMessage configMessage = new StandardCategorySetConfigMessage();
        BeanUtils.copyProperties(dto, configMessage);
        request.setLinkId(linkId);
        request.setData(configMessage);
        request.setDataSourceTime(System.currentTimeMillis());
        MessageBuilder<Request<StandardCategorySetConfigMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装下发业务玩法集消息并下发,topic:STANDARD_CATEGORY_SET, request:{}", linkId, JSON.toJSONString(request));
        mqDelegate.asyncSend("STANDARD_CATEGORY_SET:", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::STANDARD_CATEGORY_SET,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_CATEGORY_SET", throwable);
            }
        });
    }

    /**
     * 下发A0球头标准赔率
     *
     * @param linkId
     * @param thirdMatchInfo
     * @param standardMatchInfo
     * @param ballHeadMarketList
     * @param dataSourceTime
     * @return
     */
    public void sendStandardBallHeadMarketAoAsync(String linkId, ThirdMatchInfo thirdMatchInfo, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> ballHeadMarketList, Long dataSourceTime) {
        StandardMatchMarketAoMessage standardMatchMarketMessage = new StandardMatchMarketAoMessage();
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setThirdMatchInfoId(thirdMatchInfo.getThirdMatchSourceId());
        standardMatchMarketMessage.setMarketList(ballHeadMarketList);
        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());

        Request<StandardMatchMarketAoMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketAoMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装标准AO球头赔率消息并下发,topic:STANDARD_AO_MARKET_ODDS,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        mqTemplate.asyncSend("STANDARD_AO_MARKET_ODDS:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                             builder.build(),
                             new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::STANDARD_AO_MARKET_ODDS,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS", throwable);
            }
                             }, 1000L);
    }

    /**
     * 下发A0球头三方赔率
     *
     * @param linkId
     * @param thirdMatchInfo
     * @param standardMatchInfo
     * @param marketBallHeadMap
     * @param dataSourceCode
     * @param dataSourceTime
     * @return
     */
    public void sendThirdBallHeadMarketAoAsync(String linkId, ThirdMatchInfo thirdMatchInfo, StandardMatchInfo standardMatchInfo, Map<Long, ThirdMarketDTO> marketBallHeadMap, String dataSourceCode, Long dataSourceTime) {
        StandardMatchMarketAoMessage standardMatchMarketMessage = new StandardMatchMarketAoMessage();
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setThirdMatchInfoId(thirdMatchInfo.getThirdMatchSourceId());
        standardMatchMarketMessage.setDataSourceCode(dataSourceCode);
        standardMatchMarketMessage.setThirdMarketBallHeadMap(marketBallHeadMap);
        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());

        Request<StandardMatchMarketAoMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketAoMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装三方AO球头赔率消息并下发,topic:THIRD_MARKET_BALL_HEAD,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        mqTemplate.asyncSend("THIRD_MARKET_BALL_HEAD:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                             builder.build(),
                             new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::THIRD_MARKET_BALL_HEAD,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_MARKET_BALL_HEAD", throwable);
            }
                             });
    }

    /**
     * 通知AO删除球头缓存
     *
     * @param linkId
     * @return
     */
    public void sendAoThirdMarketUpStatusAsync(String linkId, String aoMatchId, Long sportId, Set<String> dataSourceCodes) {
        StandardMatchMarketAoMessage standardMatchMarketMessage = new StandardMatchMarketAoMessage();
        standardMatchMarketMessage.setThirdMatchInfoId(aoMatchId);
        standardMatchMarketMessage.setDataSourceCodes(dataSourceCodes);
        standardMatchMarketMessage.setSportId(sportId);
        Request<StandardMatchMarketAoMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        MessageBuilder<Request<StandardMatchMarketAoMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装三方AO球头修改缓存消息并下发,topic:THIRD_MARKET_UP_STATUS,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        mqTemplate.asyncSend("THIRD_MARKET_UP_STATUS:" + standardMatchMarketMessage.getThirdMatchInfoId(),
                             builder.build(),
                             new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::THIRD_MARKET_UP_STATUS,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_MARKET_UP_STATUS", throwable);
            }
                             });
    }

    /**
     * 下发A0篮球 三方球头
     *
     * @param linkId
     * @param thirdMatchInfo
     * @param standardMatchInfo
     * @param dataSourceTime
     * @return
     */
    public void sendBasketballThirdBallHeadMarketAoAsync(String linkId, ThirdMatchInfo thirdMatchInfo, StandardMatchInfo standardMatchInfo, Map<Long, List<ThirdMarketDTO>> marketBallHeadMap, String dataSourceCode, Long dataSourceTime) {
        StandardMatchMarketAoMessage standardMatchMarketMessage = new StandardMatchMarketAoMessage();
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setThirdMatchInfoId(thirdMatchInfo.getThirdMatchSourceId());
        standardMatchMarketMessage.setDataSourceCode(dataSourceCode);
        standardMatchMarketMessage.setThirdBasketballMarketBallHeadMap(marketBallHeadMap);
        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());

        Request<StandardMatchMarketAoMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketAoMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装三方AO球头赔率消息并下发,topic:THIRD_MARKET_BASKETBALL_HEAD,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        mqTemplate.asyncSend("THIRD_MARKET_BASKETBALL_HEAD:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                             builder.build(),
                             new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::THIRD_MARKET_BASKETBALL_HEAD,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_MARKET_BASKETBALL_HEAD", throwable);
            }
                             });
    }

    /**
     * 下发A0乒乓球 三方球头
     *
     * @param linkId
     * @param thirdMatchInfo
     * @param standardMatchInfo
     * @param dataSourceTime
     * @return
     */
    public void sendTableTennisThirdBallHeadMarketAoAsync(String linkId, ThirdMatchInfo thirdMatchInfo, StandardMatchInfo standardMatchInfo, Map<Long, List<ThirdMarketDTO>> marketBallHeadMap, String dataSourceCode, Long dataSourceTime) {
        StandardMatchMarketAoMessage standardMatchMarketMessage = new StandardMatchMarketAoMessage();
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setThirdMatchInfoId(thirdMatchInfo.getThirdMatchSourceId());
        standardMatchMarketMessage.setDataSourceCode(dataSourceCode);
        standardMatchMarketMessage.setThirdBasketballMarketBallHeadMap(marketBallHeadMap);
        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());

        Request<StandardMatchMarketAoMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketAoMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装三方AO球头赔率消息并下发,topic:THIRD_MARKET_TABLE_TENNIS_HEAD,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        mqTemplate.asyncSend("THIRD_MARKET_TABLE_TENNIS_HEAD:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                             builder.build(),
                             new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::THIRD_MARKET_TABLE_TENNIS_HEAD,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_MARKET_TABLE_TENNIS_HEAD", throwable);
            }
                             });
    }

    /**
     * 下发A0篮球 次要玩法三方球头
     *
     * @param linkId
     * @param thirdMatchInfo
     * @param standardMatchInfo
     * @param dataSourceTime
     * @return
     */
    public void sendBasketballThirdBallHeadMainlyNotMarketAoAsync(String linkId, ThirdMatchInfo thirdMatchInfo, StandardMatchInfo standardMatchInfo, Map<Long, ThirdMarketDTO> marketBallHeadMap, String dataSourceCode, Long dataSourceTime) {
        StandardMatchMarketAoMessage standardMatchMarketMessage = new StandardMatchMarketAoMessage();
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setThirdMatchInfoId(thirdMatchInfo.getThirdMatchSourceId());
        standardMatchMarketMessage.setDataSourceCode(dataSourceCode);
        standardMatchMarketMessage.setThirdBasketballMarketBallMainlyNotHeadMap(marketBallHeadMap);
        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());

        Request<StandardMatchMarketAoMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketAoMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装三方AO次要玩法球头赔率消息并下发,topic:THIRD_MARKET_BASKETBALL_MAINLY_NOT_HEAD,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        mqDelegate.asyncSend(
                "THIRD_MARKET_BASKETBALL_MAINLY_NOT_HEAD:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                builder.build(),
                new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::THIRD_MARKET_BASKETBALL_MAINLY_NOT_HEAD,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_MARKET_BASKETBALL_MAINLY_NOT_HEAD", throwable);
            }
        });
    }

    /**
     * 下发爬虫内部数据源到A01
     *
     * @param linkId
     * @return
     */
    public void sendInternalDataSourceCodeAoAsync(String linkId,Long standardMatchId, JSONObject object) {
        Request<JSONObject> request = new Request<>();
        request.setData(object);
        request.setLinkId(linkId);
        MessageBuilder<Request<JSONObject>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装爬虫内部数据源消息并下发,topic:THIRD_INTERNAL_DATA_SOURCE_AO,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        mqTemplate.asyncSend("THIRD_INTERNAL_DATA_SOURCE_AO:" + standardMatchId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::THIRD_INTERNAL_DATA_SOURCE_AO,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "THIRD_INTERNAL_DATA_SOURCE_AO", throwable);
            }
        });
    }

    public void sendChampionMarketCloseWarn(String linkId, String param, Long matchId) {
        MessageBuilder<String> builder = MessageBuilder.withPayload(param).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装冠军盘口结束时间变更消息并下发,topic:CHAMPION_MARKET_CLOSE_WARN_INFO,championMarketCloseWarnDTO:{}", linkId, JSON.toJSONString(param));
        //第一个参数表示topic:tag
        mqTemplate.asyncSend("CHAMPION_MARKET_CLOSE_WARN_INFO:" + matchId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::CHAMPION_MARKET_CLOSE_WARN_INFO,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "CHAMPION_MARKET_CLOSE_WARN_INFO", throwable);
            }
        });
    }

    /**
     * 下发玩法赔率最新更新时间到监控
      * @param linkId
     * @param sportId
     * @param matchId
     * @param matchPeriodId
     * @param marketCategoryIdSet
     * @param dataSourceTime
     * @param marketType
     */

//    @Async
    public void thirdCategoryOddsUpdateTimeSend(String linkId, Long sportId, Long matchId, Long matchPeriodId, Set<Long> marketCategoryIdSet, Long dataSourceTime, Integer marketType) {
        if (CollectionUtils.isEmpty(marketCategoryIdSet)) {
            return;
        }
        List<MatchMonitorMessage> message = new ArrayList<MatchMonitorMessage>();
        MatchMonitorMessage m;
        try {
            for (Long categoryId : marketCategoryIdSet) {
                String redisDateKey = Constant.REDIS_KEY.RONGHE_MATCH_CATEGORY_ODDS_UPDATETIME_DATE + matchId + "_" + categoryId;
                Object oldTime = redisService.get(redisDateKey);
                if (oldTime == null || ((Long) oldTime) <= dataSourceTime) {
                    m = new MatchMonitorMessage();
                    m.setMatchId(matchId);
                    m.setCategoryId(categoryId);
                    m.setDataSourceTime(dataSourceTime);
                    m.setMarketType(marketType);
                    m.setMatchPeriodId(matchPeriodId);
                    m.setSportId(sportId);
                    message.add(m);
                }
            }
        } catch (Exception e) {
            log.error("::{}::thirdCategoryOddsUpdateTimeSend出现异常:", linkId, e);
            e.printStackTrace();
        } finally {

        }
        if (CollectionUtils.isEmpty(message)) {
            return;
        }
        Request<List<MatchMonitorMessage>> request = new Request<List<MatchMonitorMessage>>();
        request.setData(message);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<List<MatchMonitorMessage>>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, matchId);
        log.info("::{}::开始组装赔率最新更新时间消息并下发,topic:" + ConstantSystem.MATCH_OPERATE_EX + ",request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        mqTemplate.asyncSend(ConstantSystem.MATCH_OPERATE_EX + ":" + matchId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::MATCH_OPERATE_EX,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, ConstantSystem.MATCH_OPERATE_EX, throwable);
            }
        });
    }

    /**
     * 下发赛事盘口数据到下游，异步推送
     *
     * @param linkId
     * @param standardMarketMessageAllList
     */
    public void scoreSettleSpMarketAsyncSend(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList, Long dataSourceTime) {
        if (CollectionUtils.isEmpty(standardMarketMessageAllList)) {
            return;
        }
        StandardMatchMarketMessage standardMatchMarketMessage = new StandardMatchMarketMessage();
        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
        standardMatchMarketMessage.setMarketList(standardMarketMessageAllList);
        Request<StandardMatchMarketMessage> request = new Request<>();
        request.setData(standardMatchMarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        mqTemplate.asyncSend("SCORE_SETTLE_SP_MARKET:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                             builder.build(),
                             new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::SCORE_SETTLE_SP_MARKET,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "SCORE_SETTLE_SP_MARKET", throwable);
            }
                             });
    }

    private void calculateOdds(String linkId,
                               StandardMatchInfo standardMatchInfo,
                               boolean matchTradType,
                               StandardMatchMarketMessage standardMatchMarketMessage,
                               Long dataSourceTime) {
        if (null == standardMatchInfo.getStandardTournamentId() || standardMatchInfo.getStandardTournamentId() == 0) {
            return;
        }
        if (matchTradType) {
            log.info("::{}::不计算赔率分组信息。", linkId);
            return;
        }
        StandardSportTournament standardSportTournament =
                standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
        int tournamentLevel = standardSportTournament.getTournamentLevel();
        Integer level =
                (tournamentLevel <= 3 && tournamentLevel >= 1) ? tournamentLevel : tournamentLevel == 20 ? 20 : 21;
        //log.info("::{}::开始组装标准赔率消息并下发,联赛等级:{},运动种类：{}", linkId, level, standardMatchInfo.getSportId());
        List<ConfigMarketLevel> configs = configMarketLevelService.getItemLevel(
                MarginCategoryConfig.SOPRT_TYPE.contains(standardMatchInfo.getSportId()) ?
                        standardMatchInfo.getSportId() : -1, level);
        //log.info("::{}::开始组装标准赔率消息并下发,联赛等级:{},运动种类：{},等级配置参数：{}", linkId, level, standardMatchInfo.getSportId(), JSON.toJSON(configs));
        Map<String, DiscountOddsConfigDTO> discountOddsConfigDTOMap = redisService.hGetAll(DigestUtil.md5Hex(
                Constant.REDIS_KEY.RONGHE_DISCOUNT_ODDS_CONFIG + standardMatchInfo.getId()));
        String key2 = Constant.REDIS_KEY.RONGHE_AO_MARKET_ORIGINAL_ODDS2 + standardMatchInfo.getId();
        Map<String, Integer> originalOddsValueMap = redisService.hGetAll(key2);
        int version = oddsCalcVersionService.getVersion();
        if (version == 0) {
            Map<Long, Integer> oddsCalcCategoryMap =
                    configSportCategoryGroupService.getBySportId(standardMatchInfo.getSportId());
            CalculateOddsUtils.calculateOddsByMatchLevelV1(configs,
                                                           standardMatchMarketMessage,
                                                           oddsCalcCategoryMap,
                                                           matchTradType,
                                                           discountOddsConfigDTOMap,
                                                           originalOddsValueMap);
        } else {
            log.warn("odds calc using old version");
            CalculateOddsUtils.calculateOddsByMatchLevel(configs,
                                                         standardMatchMarketMessage,
                                                         matchTradType,
                                                         discountOddsConfigDTOMap,
                                                         originalOddsValueMap);
        }
    }

    /**
     * 信用等级1  1-3,20-特殊,4-19:其他
     *
     * @param linkId
     * @param standardMatchInfo
     * @param matchTradType
     * @param standardMatchMarketMessage
     * @param dataSourceTime
     */
    private void sendLevel1Odds(String linkId,
                                StandardMatchInfo standardMatchInfo,
                                boolean matchTradType,
                                StandardMatchMarketMessage standardMatchMarketMessage,
                                Long dataSourceTime) {
        //MarginCategoryConfig
        if (null == standardMatchInfo.getStandardTournamentId() || standardMatchInfo.getStandardTournamentId() == 0) {
            return;
        }
        StandardSportTournament standardSportTournament =
                standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
        int tournamentLevel = standardSportTournament.getTournamentLevel();
        Integer level =
                (tournamentLevel <= 3 && tournamentLevel >= 1) ? tournamentLevel : tournamentLevel == 20 ? 20 : 21;
        log.info("::{}::开始组装标准赔率消息并下发,联赛等级:{},运动种类：{}",
                 linkId,
                 level,
                 standardMatchInfo.getSportId());
        List<ConfigMarketLevel> configs = configMarketLevelService.getItemLevel(
                MarginCategoryConfig.SOPRT_TYPE.contains(standardMatchInfo.getSportId()) ?
                        standardMatchInfo.getSportId() : -1, level);
        log.info("::{}::开始组装标准赔率消息并下发,联赛等级:{},运动种类：{},等级配置参数：{}",
                 linkId,
                 level,
                 standardMatchInfo.getSportId(),
                 JSON.toJSON(configs));
        List<String> topicList = new ArrayList<>();
        if (!ObjectUtil.isEmpty(configs) && !CollectionUtils.isEmpty(standardMatchMarketMessage.getMarketList())) {
            Map<Integer, List<ConfigMarketLevel>> configsMap =
                    configs.stream().collect(Collectors.groupingBy(ConfigMarketLevel::getLevel));
            for (Map.Entry<Integer, List<ConfigMarketLevel>> entry : configsMap.entrySet()) {
                topicList.add("STANDARD_MARKET_ODDS_LEVEL_" + entry.getKey());
            }
        }
        //pa操盘
        if (!matchTradType) {
            //盘口类型细分：21：常规玩法，22：50/50玩法，31：1.0-2.0 32：2.01-5.0 33：5.01-10.0
            if (!ObjectUtil.isEmpty(configs) && !CollectionUtils.isEmpty(standardMatchMarketMessage.getMarketList())) {
                Map<Integer, List<ConfigMarketLevel>> configsMap =
                        configs.stream().collect(Collectors.groupingBy(ConfigMarketLevel::getLevel));
                for (Map.Entry<Integer, List<ConfigMarketLevel>> entry : configsMap.entrySet()) {
                    StandardMatchMarketMessage standardMatchMarketMessageLevel = new StandardMatchMarketMessage();
                    BeanUtils.copyProperties(standardMatchMarketMessage, standardMatchMarketMessageLevel);
                    List<StandardMarketMessage> marketList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(standardMatchMarketMessage.getMarketList())) {
                        for (StandardMarketMessage s : standardMatchMarketMessage.getMarketList()) {
                            StandardMarketMessage s1 = new StandardMarketMessage();
                            BeanUtils.copyProperties(s, s1);
                            List<StandardMarketOddsMessage> marketOddsList = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(s1.getMarketOddsList())) {
                                for (StandardMarketOddsMessage ss : s1.getMarketOddsList()) {
                                    StandardMarketOddsMessage s2 = new StandardMarketOddsMessage();
                                    BeanUtils.copyProperties(ss, s2);
                                    marketOddsList.add(s2);
                                }
                            }
                            s1.setMarketOddsList(new ArrayList<>());
                            s1.setMarketOddsList(marketOddsList);
                            marketList.add(s1);
                        }
                    }
                    standardMatchMarketMessageLevel.setMarketList(new ArrayList<>());
                    standardMatchMarketMessageLevel.setMarketList(marketList);

                    List<ConfigMarketLevel> configsValue = entry.getValue();
                    if (!CollectionUtils.isEmpty(configsValue)) {
                        //log.info("::{}::开始组装标准赔率消息并下发,信用等级:{},联赛等级:{},运动种类：{},开始计算", linkId, entry.getKey(),level,standardMatchInfo.getSportId());
                        ConfigMarketLevel cnormal = new ConfigMarketLevel();
                        ConfigMarketLevel c50 = new ConfigMarketLevel();
                        ConfigMarketLevel cthree1 = new ConfigMarketLevel();
                        ConfigMarketLevel cthree2 = new ConfigMarketLevel();
                        ConfigMarketLevel cthree3 = new ConfigMarketLevel();
                        for (ConfigMarketLevel c : configsValue) {
                            if (c.getDiffValue() == null) {
                                c.setDiffValue(0.0);
                            }
                            if (c.getMarketTypeDetail() == 21) {
                                cnormal = c;
                            }
                            if (c.getMarketTypeDetail() == 22) {
                                c50 = c;
                            }
                            if (c.getMarketTypeDetail() == 31) {
                                cthree1 = c;
                            }
                            if (c.getMarketTypeDetail() == 32) {
                                cthree2 = c;
                            }
                            if (c.getMarketTypeDetail() == 33) {
                                cthree3 = c;
                            }
                        }
                        ConfigMarketLevel finalCnormal = cnormal;
                        ConfigMarketLevel finalC5 = c50;
                        ConfigMarketLevel finalCthree = cthree1;
                        ConfigMarketLevel finalCthree1 = cthree2;
                        ConfigMarketLevel finalCthree2 = cthree3;
                        /*log.info("::{}::开始组装标准赔率消息并下发,联赛等级:{},运动种类：{},开始计算，finalCnormal:{},finalC5:{},finalCthree:{},finalCthree1:{},finalCthree2:{}",
                                linkId, level,standardMatchInfo.getSportId(),JSON.toJSON(finalCnormal),JSON.toJSON(finalC5),JSON.toJSON(finalCthree),JSON.toJSON(finalCthree1),JSON.toJSON(finalCthree2));*/
                        standardMatchMarketMessageLevel.getMarketList().forEach(e -> {
                            if (!e.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED) &&
                                    !CollectionUtils.isEmpty(e.getMarketOddsList())) {
                                if (MarginCategoryConfig.NORMAL_CATEGORY.contains(e.getMarketCategoryId()) &&
                                        (MarginCategoryConfig.SOPRT_TYPE.contains(standardMatchInfo.getSportId()) ?
                                                standardMatchInfo.getSportId() : -1) == (configs.get(0).getSportId())) {
                                    AtomicBoolean flag = new AtomicBoolean(false);
                                    e.getMarketOddsList().forEach(y -> {
                                        if (y.getPaOddsValue() + (finalCnormal.getDiffValue() * 100000) <= 100000) {
                                            flag.set(true);
                                        } else {
                                            Double odds = y.getPaOddsValue() + (finalCnormal.getDiffValue() * 100000);
                                            y.setPaOddsValue(odds.intValue());
                                        }
                                    });
                                    if (flag.get()) {
                                        e.getMarketOddsList().forEach(y -> {
                                            y.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                        });
                                    }
                                } else if (MarginCategoryConfig.CATEGORY_50.contains(e.getMarketCategoryId()) &&
                                        (MarginCategoryConfig.SOPRT_TYPE.contains(standardMatchInfo.getSportId()) ?
                                                standardMatchInfo.getSportId() : -1) == (configs.get(0).getSportId())) {
                                    e.getMarketOddsList().forEach(y -> {
                                        if (y.getPaOddsValue() + (finalC5.getDiffValue() * 100000) <= 100000) {
                                            y.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                        } else {
                                            Double odds = y.getPaOddsValue() + (finalC5.getDiffValue() * 100000);
                                            y.setPaOddsValue(odds.intValue());
                                        }
                                    });
                                } else if (MarginCategoryConfig.THREE_ODDS_CATEGORY.contains(e.getMarketCategoryId()) &&
                                        (MarginCategoryConfig.SOPRT_TYPE.contains(standardMatchInfo.getSportId()) ?
                                                standardMatchInfo.getSportId() : -1) == (configs.get(0).getSportId())) {
                                    e.getMarketOddsList().forEach(y -> {
                                        if (y.getPaOddsValue() >= 100000 && y.getPaOddsValue() <= 200000) {
                                            if (y.getPaOddsValue() + (finalCthree.getDiffValue() * 100000) <= 100000) {
                                                y.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                            } else {
                                                Double odds =
                                                        y.getPaOddsValue() + (finalCthree.getDiffValue() * 100000);
                                                y.setPaOddsValue(odds.intValue());
                                            }
                                        } else if (y.getPaOddsValue() > 200000 && y.getPaOddsValue() <= 500000) {
                                            if (y.getPaOddsValue() + (finalCthree1.getDiffValue() * 100000) <= 100000) {
                                                y.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                            } else {
                                                Double odds =
                                                        y.getPaOddsValue() + (finalCthree1.getDiffValue() * 100000);
                                                y.setPaOddsValue(odds.intValue());
                                            }
                                        } else if (y.getPaOddsValue() > 500000 && y.getPaOddsValue() <= 1000000) {
                                            if (y.getPaOddsValue() + (finalCthree2.getDiffValue() * 100000) <= 100000) {
                                                y.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                            } else {
                                                Double odds =
                                                        y.getPaOddsValue() + (finalCthree2.getDiffValue() * 100000);
                                                y.setPaOddsValue(odds.intValue());
                                            }
                                        }
                                    });
                                } else if (e.getMarketOddsList().size() == 2) {
                                    AtomicBoolean flag = new AtomicBoolean(false);
                                    e.getMarketOddsList().forEach(y -> {
                                        if (y.getPaOddsValue() + (finalCnormal.getDiffValue() * 100000) <= 100000) {
                                            flag.set(true);
                                        } else {
                                            Double odds = y.getPaOddsValue() + (finalCnormal.getDiffValue() * 100000);
                                            y.setPaOddsValue(odds.intValue());
                                        }
                                    });
                                    if (flag.get()) {
                                        e.getMarketOddsList().forEach(y -> {
                                            y.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                        });
                                    }
                                } else if (e.getMarketOddsList().size() >= 3) {
                                    e.getMarketOddsList().forEach(y -> {
                                        if (y.getPaOddsValue() >= 100000 && y.getPaOddsValue() <= 200000) {
                                            if (y.getPaOddsValue() + (finalCthree.getDiffValue() * 100000) <= 100000) {
                                                y.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                            } else {
                                                Double odds =
                                                        y.getPaOddsValue() + (finalCthree.getDiffValue() * 100000);
                                                y.setPaOddsValue(odds.intValue());
                                            }
                                        } else if (y.getPaOddsValue() > 200000 && y.getPaOddsValue() <= 500000) {
                                            if (y.getPaOddsValue() + (finalCthree1.getDiffValue() * 100000) <= 100000) {
                                                y.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                            } else {
                                                Double odds =
                                                        y.getPaOddsValue() + (finalCthree1.getDiffValue() * 100000);
                                                y.setPaOddsValue(odds.intValue());
                                            }
                                        } else if (y.getPaOddsValue() > 500000 && y.getPaOddsValue() <= 1000000) {
                                            if (y.getPaOddsValue() + (finalCthree2.getDiffValue() * 100000) <= 100000) {
                                                y.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                                            } else {
                                                Double odds =
                                                        y.getPaOddsValue() + (finalCthree2.getDiffValue() * 100000);
                                                y.setPaOddsValue(odds.intValue());
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    //下发各个等级topic赔率数据
                    Request<StandardMatchMarketMessage> request = new Request<>();
                    request.setData(standardMatchMarketMessageLevel);
                    request.setLinkId(linkId);
                    request.setDataSourceTime(dataSourceTime);
                    MessageBuilder<Request<StandardMatchMarketMessage>> builder =
                            MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
                    log.info("::{}::开始组装标准赔率消息并下发,topic:STANDARD_MARKET_ODDS_LEVEL_:{}",
                             linkId,
                             entry.getKey());
                    //第一个参数表示topic0:tag
                    mqDelegate.asyncSend("STANDARD_MARKET_ODDS_LEVEL_" + entry.getKey() + ":" +
                                                 standardMatchMarketMessageLevel.getStandardMatchInfoId(),
                                         builder.build(),
                                         new SendCallback() {
                                             @Override
                                             public void onSuccess(SendResult sendResult) {
                                                 log.info("::{}::,send successful to STANDARD_MARKET_ODDS_LEVEL_" +
                                                                  entry.getKey(), linkId);
                                             }

                                             @Override
                                             public void onException(Throwable throwable) {
                                                 log.error("::{}::TOPIC={}，send fail; ",
                                                           linkId,
                                                           "STANDARD_MARKET_ODDS_LEVEL_" + entry.getKey(),
                                                           throwable);
                                             }
                                         });
                }
            }
        } else {
            Request<StandardMatchMarketMessage> request = new Request<>();
            request.setData(standardMatchMarketMessage);
            request.setLinkId(linkId);
            request.setDataSourceTime(dataSourceTime);
            MessageBuilder<Request<StandardMatchMarketMessage>> builder =
                    MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            if (!CollectionUtils.isEmpty(topicList)) {
                for (String topicStr : topicList) {
                    log.info("::{}::开始组装标准赔率消息并下发,topic:{},request:{}",
                             linkId,
                             topicStr,
                             JSON.toJSONString(request));
                    //第一个参数表示topic0:tag
                    mqDelegate.asyncSend(topicStr + ":" + standardMatchMarketMessage.getStandardMatchInfoId(),
                                         builder.build(),
                                         new SendCallback() {
                                             @Override
                                             public void onSuccess(SendResult sendResult) {
                                                 log.info("::{}::,send successful to :{}", linkId, topicStr);
                                             }

                                             @Override
                                             public void onException(Throwable throwable) {
                                                 log.error("::{}::TOPIC={}，send fail; ", linkId, topicStr, throwable);
                                             }
                                         });
                }
            } else {
                log.info("::{}::开始组装标准赔率消息并下发,topic:STANDARD_MARKET_ODDS_LEVEL_1,request:{}",
                         linkId,
                         JSON.toJSONString(request));
                //第一个参数表示topic0:tag
                mqDelegate.asyncSend(
                        "STANDARD_MARKET_ODDS_LEVEL_1:" + standardMatchMarketMessage.getStandardMatchInfoId(),
                        builder.build(),
                        new SendCallback() {
                            @Override
                            public void onSuccess(SendResult sendResult) {
                                log.info("::{}::,send successful to STANDARD_MARKET_ODDS_LEVEL_1", linkId);
                            }

                            @Override
                            public void onException(Throwable throwable) {
                                log.error("::{}::TOPIC={}，send fail; ",
                                          linkId,
                                          "STANDARD_MARKET_ODDS_LEVEL_1",
                                          throwable);
                            }
                        });
            }
        }
    }

    /**
     * 3027赔率分组计算开关
     *
     * @return  false计算/true 不计算
     */
    private boolean getMatchTradeCacheConfig(String riskManagerCode) {
        Map<String, Integer> configMap = (Map<String, Integer>) redisService.get(RONGHE_TRAD_CONFIG);
        if (!CollectionUtils.isEmpty(configMap)) {
            if (configMap.containsKey(riskManagerCode)) {
                Integer isOpen = configMap.get(riskManagerCode);
                return 1 == isOpen ? false : true;
            }
        }
        return true;
    }

//    /**
//     * 批量下发赛事盘口数据到下游，异步推送
//     *
//     * @param linkId
//     * @param standardMarketMessageAllList
//     */
//    public StandardMatchMarketMessage standardMarketOddsAsyncBatchSend(List<OddsWrapper<ThirdMarketDTO>> outrightMarketDTOs, Map<Long, StandardMatchInfoDetail> standardMatchInfoMap, Map<Long, List<StandardMarketMessage>> standardMarketMessageSendMap, boolean matchTradType) {
//        thirdMarketPreResultProcessor.sendThirdPreMarket(linkId, standardMatchInfo, standardMarketMessageAllList, dataSourceTime);
//        //通过配置移除多盘口玩法中多余的盘口
//
//        Map<Long, OddsWrapper<ThirdMarketDTO>> oddsWrapperMap = outrightMarketDTOs.stream().collect(Collectors.toMap(OddsWrapper::getStandardSourceId, Function.identity(), (v1, v2)->v1));
//        List<Message> messages = new ArrayList<>();
//        for (Map.Entry<Long, List<StandardMarketMessage>> entry : standardMarketMessageSendMap.entrySet()) {
//            StandardMatchInfoDetail standardMatchInfo = standardMatchInfoMap.get(entry.getKey());
//            OddsWrapper<ThirdMarketDTO> oddsWrapper = oddsWrapperMap.get(entry.getKey());
//            String linkId = oddsWrapper.getLinkId();
//            Long dataSourceTime = oddsWrapper.getDataSourceTime();
//            StandardMatchMarketMessage standardMatchMarketMessage = buildStandardMatchMarketMessage(standardMatchInfo, entry.getValue());
//            Request<StandardMatchMarketMessage> request = new Request<>();
//            request.setData(standardMatchMarketMessage);
//            request.setLinkId(linkId);
//            request.setDataSourceTime(dataSourceTime);
//            Message<Request<StandardMatchMarketMessage>> message = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId).build();
//            //查询赛事的开售信息
//            if (!CollectionUtils.isEmpty(standardMarketMessageAllList) && !Objects.equals(NUMBER_TWO, standardMarketMessageAllList.get(0).getMarketType())) {
//                StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
//                matchTradType = getMatchTradeCacheConfig(standardMarketMessageAllList.get(0).getMarketType() == 1 ? standardSportMarketSell.getPreRiskManagerCode() : standardSportMarketSell.getLiveRiskManagerCode());
//            }
//            //赔率分组计算
//            calculateOdds(linkId, standardMatchInfo, matchTradType, standardMatchMarketMessage, dataSourceTime);
//            messages.add(message);
//        }
//
////        StandardMatchMarketMessage standardMatchMarketMessage = buildStandardMatchMarketMessage(standardMatchInfo, standardMarketMessageAllList);
////        Request<StandardMatchMarketMessage> request = new Request<>();
////        request.setData(standardMatchMarketMessage);
////        request.setLinkId(linkId);
////        request.setDataSourceTime(dataSourceTime);
////        MessageBuilder<Request<StandardMatchMarketMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
//
//        //第一个参数表示topic:tag
//        //2780需求： 常规赛事赔率和电子赔率topic拆分
//        if (1 != standardMatchInfo.getMatchType()) {
//            StandardMatchInfo standartMatch = standardMatchInfoService.getItem(standardMatchInfo.getId());
//            if (null != standartMatch && NUMBER_TWO == standartMatch.getMatchType()) {
//                rocketMqTemplate.asyncSend("STANDARD_MARKET_ODDS_ESPORT:" + standardMatchMarketMessage.getStandardMatchInfoId(), builder.build(), new SendCallback() {
//                    @Override
//                    public void onSuccess(SendResult sendResult) {
//                        log.info("::{}::,STANDARD_MARKET_ODDS_ESPORT send successful", linkId);
//                    }
//
//                    @Override
//                    public void onException(Throwable throwable) {
//                        log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS_ESPORT", throwable);
//                    }
//                });
//                return standardMatchMarketMessage;
//            }
//        }
//        rocketMqTemplate.asyncSend("STANDARD_MARKET_ODDS:" + standardMatchMarketMessage.getStandardMatchInfoId(), builder.build(), new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//                log.info("::{}::,send successful", linkId);
//            }
//
//            @Override
//            public void onException(Throwable throwable) {
//                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS", throwable);
//            }
//        });
//        //sendLevel1Odds( linkId, standardMatchInfo,matchTradType, standardMatchMarketMessage,dataSourceTime);
//        return standardMatchMarketMessage;
//    }
//
//    private StandardMatchMarketMessage buildStandardMatchMarketMessage(StandardMatchInfoDetail standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList) {
//        StandardMatchMarketMessage standardMatchMarketMessage = new StandardMatchMarketMessage();
//        standardMatchMarketMessage.setBeginTime(standardMatchInfo.getBeginTime());
//        standardMatchMarketMessage.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
//        standardMatchMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());
//        standardMatchMarketMessage.setDataSourceCode(standardMatchInfo.getDataSourceCode());
//        standardMatchMarketMessage.setSportId(standardMatchInfo.getSportId());
//        standardMatchMarketMessage.setMatchType(0);
//        if (standardMatchInfo instanceof StandardMatchInfoDetail) {
//            standardMatchMarketMessage.setMatchType(1);
//            if (!CollectionUtils.isEmpty(standardMarketMessageAllList) && standardMarketMessageAllList.get(0).getMarketType() != 2) {
//                standardMatchMarketMessage.setMatchType(0);
//            }
//        }
//        standardMatchMarketMessage.setMarketList(standardMarketMessageAllList);
//        //判断冠军赛事
//        boolean isOutRight = standardMatchMarketMessage.getMatchType() == 1 ? Boolean.TRUE : Boolean.FALSE;
//        //兼容冠军玩法，获取标准赛事信息
//        standardMatchMarketMessage.setStatus(standardMatchInfo.getOperateMatchStatus() == -1 ? 0 : standardMatchInfo.getOperateMatchStatus());
//        standardMatchMarketMessage.setStandardTournamentId(standardMatchInfo.getStandardTournamentId());
//        return standardMatchMarketMessage;
//    }

}
