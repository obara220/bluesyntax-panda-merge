package com.panda.merge.rocketmq.producer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.ListUtils;
import com.panda.merge.common.utils.MergeFunctionUtils;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.message.*;
import com.panda.merge.enums.RequestTypeEnum;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.rocketmq.RocketMQDelegate;
import com.panda.merge.rocketmq.processor.A99ThirdAllBatchMarketProcessor;
import com.panda.merge.rocketmq.processor.A99ThirdMatchMarketProcessor;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 数据商盘口合并下发
 */
@Slf4j
@Component
public class A99ThirdSportMarketMergeProducer extends BaseProcessor {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Autowired
    private RocketMQDelegate mqDelegate;

    @Autowired
    @Lazy
    private A99ThirdMatchMarketProcessor a99ThirdMatchMarketProcessor;

    /**
     * 下发给报表服务 与 操盘
     * @param linkId
     * @param standardMatchInfo
     * @param thirdSportMarketMessages
     * @param modifyTime
     */
    public void sendThirdSportMarketMessageToMQ(String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> thirdSportMarketMessages,Long modifyTime)
    {
//        if (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())
//                && !MarginCategoryConfig.SPORT_TX_LOGIC.contains(thirdSportMarketMessages.get(0).getDataSourceCode())) {
//            log.info("::{}::sendThirdSportMarketMessageToMQ,开始排序", linkId);
//            setPlaceNum(thirdSportMarketMessages);
//            log.info("::{}::sendThirdSportMarketMessageToMQ,排序完成", linkId);
//        }
        log.info("::准备组装A99赔率, 赛事id:{}, linkId:{}", standardMatchInfo.getId(), linkId);
        StandardMatchThirdMarketMessage standardMatchThirdMarketMessage = new StandardMatchThirdMarketMessage();
        standardMatchThirdMarketMessage.setMarketList(thirdSportMarketMessages);
        standardMatchThirdMarketMessage.setMatchType(standardMatchInfo.getMatchType());
        standardMatchThirdMarketMessage.setSportId(standardMatchInfo.getSportId());
        standardMatchThirdMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());

        Request<StandardMatchThirdMarketMessage> standardMatchThirdMarketMessageRequest = new Request<>();
        standardMatchThirdMarketMessageRequest.setData(standardMatchThirdMarketMessage);
        standardMatchThirdMarketMessageRequest.setLinkId(linkId);
        standardMatchThirdMarketMessageRequest.setDataSourceTime(modifyTime);
        standardMatchThirdMarketMessageRequest.setDataSourceCode(thirdSportMarketMessages.get(0).getDataSourceCode());
        MessageBuilder<Request<StandardMatchThirdMarketMessage>> builder = MessageBuilder.withPayload(standardMatchThirdMarketMessageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, standardMatchThirdMarketMessageRequest.getLinkId());
        log.info("::{}::开始下发三方数据商盘口合并消息,topic:A99_STANDARD_ODDS_TO_A01", standardMatchThirdMarketMessageRequest.getLinkId());
        rocketMqTemplate.asyncSend(A99_STANDARD_ODDS_TO_A01, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,A99_STANDARD_ODDS_TO_A01 send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "A99_STANDARD_ODDS_TO_A01", throwable);
            }
        });

//        List<StandardMarketDataMessage> standardMarketDataMessages = thirdSportMarketMessages.stream().map(e -> {
//            StandardMarketDataMessage v = thirdMarketConvertStandard(e);
//            if (null != v) {
//                v.setChildMarketCategoryId((CategoryUtils.getChildCategoryId(linkId, v.getMarketCategoryId(), v.getAddition1(), v.getAddition2(), v.getAddition3(), v.getAddition4(), v.getAddition5(), String.valueOf(v.getStandardMatchInfoId()))));
//            }
//            return v;
//        }).collect(Collectors.toList());
//        log.info("::{},三方盘口转换标准盘口:{}", linkId, standardMarketDataMessages);
//
//        /**
//         * 缓存下发的A99赔率，当操盘后台关闭A99时，需要给融合下发关盘
//         */
//        Long marketCategoryId = thirdSportMarketMessages.get(0).getMarketCategoryId();
//        redisService.hSet(Constant.REDIS_KEY.RONGHE_A99_PUSHED_MARKET_ODDS, marketCategoryId.toString(), standardMarketDataMessages, 3*24*60*60);
//
//        /**
//         * 下发标准A99赔率给融合
//         */
//        sendA99OddsToRonghe(standardMarketDataMessages, linkId+"_A99");

        //TX/AO主列表玩法百家赔 只下发足球
        /*if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
            return;
        }*/
//        int marketType = isOddsLive(standardMatchInfo.getId());
//        List<ThirdSportMarketMessage> txThirdSportMarketMessages = new ArrayList<>();
//        thirdSportMarketMessages.forEach(thirdMarket -> {
//            if (MarginCategoryConfig.FootBall_3446_3447_CATEGORY.contains(thirdMarket.getMarketCategoryId())
//                ||MarginCategoryConfig.BasketBall_3446_3447_CATEGORY.contains(thirdMarket.getMarketCategoryId())) {
//                if (thirdMarket.getMarketType() == marketType) {
////                    log.info("::{}::下发足球TX主列表玩法百家赔,三方盘口ID:{},三方盘口类型:{},滚球标识:{}"
////                            ,linkId,thirdMarket.getThirdMarketSourceId(),thirdMarket.getMarketType(),marketType);
//                    txThirdSportMarketMessages.add(thirdMarket);
//                }
//            }
//        });
//        if (!CollectionUtils.isEmpty(txThirdSportMarketMessages)) {
//            standardMatchThirdMarketMessage.setMarketList(txThirdSportMarketMessages);
//            standardMatchThirdMarketMessageRequest.setData(standardMatchThirdMarketMessage);
//            MessageBuilder<Request<StandardMatchThirdMarketMessage>> builder1 = MessageBuilder.withPayload(standardMatchThirdMarketMessageRequest)
//                    .setHeader(MessageConst.PROPERTY_KEYS, standardMatchThirdMarketMessageRequest.getLinkId());
//            mqDelegate.asyncSend("STANDARD_TX_THIRD_MARKET_ODDS:"+standardMatchInfo.getId(), builder1.build(),
//                        new SendCallback() {
//                @Override
//                public void onSuccess(SendResult sendResult) {
//                    log.info("::{}::,STANDARD_TX_THIRD_MARKET_ODDS send successful", linkId);
//                }
//
//                @Override
//                public void onException(Throwable throwable) {
//                    log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_TX_THIRD_MARKET_ODDS", throwable);
//                }
//            },standardMatchInfo.getId());
//            cacheAOMarketOdds(standardMatchInfo, txThirdSportMarketMessages);
//        }
    }

    public void sendA99OddsToRonghe(List<StandardMarketDataMessage> standardMarketDataMessages, String linkId){
        Request<List<StandardMarketDataMessage>> listRequest = new Request<>();
        listRequest.setData(standardMarketDataMessages);
        listRequest.setLinkId(linkId+"_A99");
        MessageBuilder<Request<List<StandardMarketDataMessage>>> builder1 = MessageBuilder.withPayload(listRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, listRequest.getLinkId());
        log.info("::{}::开始下发三方数据商盘口合并消息,topic:A99_STANDARD_ODDS_API", listRequest.getLinkId());
        rocketMqTemplate.asyncSend(A99_STANDARD_ODDS_API, builder1.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,A99_STANDARD_ODDS_API send successful", listRequest.getLinkId());
                //下发A01赔率
//                sendA99OddsToA01(listRequest.getLinkId(), standardMatchInfo, standardMatchThirdMarketMessage);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", listRequest.getLinkId(), "A99_STANDARD_ODDS_API ", throwable);
            }
        });
    }

    /**
     * 下发A99赔率给A01
     * @param linkId
     * @param standardMatchId
     * @param requestTypes
     */
    public void sendA99OddsToA01(String linkId, Long standardMatchId, List<String> requestTypes){
        StandardMatchA99MarketMessage standardMatchA99MarketMessage = new StandardMatchA99MarketMessage();

        Request<StandardMatchA99MarketMessage> request = new Request<>();
        standardMatchA99MarketMessage.setRequestType(requestTypes);
        standardMatchA99MarketMessage.setStandardMatchId(standardMatchId);

        log.info("::组装A01数据,linkId:{}, 消息内容:{}", linkId, standardMatchA99MarketMessage);
        request.setData(standardMatchA99MarketMessage);
        request.setLinkId(linkId);
        request.setDataSourceTime(System.currentTimeMillis());
        request.setDataSourceCode("A99");
        MessageBuilder<Request<StandardMatchA99MarketMessage>> builder = MessageBuilder
                .withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId())
                .setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, 2);

        log.info("::{}::开始下发A01赔率,topic:A01_NINETYNINE_ODDS_TRIGGER", request.getLinkId());
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        rocketMqTemplate.asyncSend(A99_MARKET_ODDS_TO_A01, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,A01_NINETYNINE_ODDS_TRIGGER send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "A01_NINETYNINE_ODDS_TRIGGER", throwable);
            }
        });
    }

    /**
     * 三方盘口设置主盘口
     * @param thirdSportMarketMessages
     */
    public void setPlaceNum(List<ThirdSportMarketMessage> thirdSportMarketMessages)
    {
        //取本次有改变的玩法,排序
        Map<Long, List<ThirdSportMarketMessage>> standardMarketMapMTS = thirdSportMarketMessages.stream().collect(Collectors.groupingBy(ThirdSportMarketMessage::getMarketCategoryId));
        if (CollectionUtils.isEmpty(standardMarketMapMTS)) {
            return;
        }
        //循环遍历盘口信息,设置低赔和赔率差
        for (Map.Entry<Long, List<ThirdSportMarketMessage>> entry : standardMarketMapMTS.entrySet()) {
            //获取key对应的盘口对象集合
            List<ThirdSportMarketMessage> standardMarketDataMessages = entry.getValue();
            //下发的三方盘口数据需要增加盘口值，主盘口，盘口状态
            List<ThirdSportMarketMessage> thirdSportMarketMessagesValid = standardMarketDataMessages.stream().filter(e -> !CollectionUtils.isEmpty(e.getThirdSportMarketOddsList())&&e.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
            //排序字段placeNum
            int placeNum = 1;
            //------------处理有效盘口的排序-----------
            if (!CollectionUtils.isEmpty(thirdSportMarketMessagesValid)) {
                //第一步：计算赔率差和低赔
                for (ThirdSportMarketMessage standardMarketDataMessage : thirdSportMarketMessagesValid) {
                    //获取盘口投注项
                    List<ThirdSportMarketOdds> marketOddsList = standardMarketDataMessage.getThirdSportMarketOddsList();
                    Integer minOddsValue = 0;
                    Integer maxOddsValue = 0;
                    //循环遍历盘口投注项
                    for (ThirdSportMarketOdds standardMarketOddsDataMessage : marketOddsList) {
                        //设置pa赔率：数据源抽水赔率
                        if (null == standardMarketOddsDataMessage.getOddsValue()) {
                            standardMarketOddsDataMessage.setOddsValue(0);
                        }
                        if (null == standardMarketOddsDataMessage.getOriginalOddsValue()) {
                            standardMarketOddsDataMessage.setOriginalOddsValue(0);
                        }
                        if (standardMarketOddsDataMessage.getOriginalOddsValue() > maxOddsValue) {
                            maxOddsValue = standardMarketOddsDataMessage.getOriginalOddsValue();
                        }
                        if (standardMarketOddsDataMessage.getOriginalOddsValue() < minOddsValue || minOddsValue == 0) {
                            minOddsValue = standardMarketOddsDataMessage.getOriginalOddsValue();
                        }
                    }
                    //计算赔率差
                    Integer oddsMetric = maxOddsValue - minOddsValue;
                    standardMarketDataMessage.setOddsMetric(Long.valueOf(oddsMetric));
                }
                //第二步：排序，依据三方源盘口状态、赔率差、低赔
                ListUtils.sort(thirdSportMarketMessagesValid, true, "thirdMarketSourceStatus", "oddsMetric", "oddsValue");
                for (ThirdSportMarketMessage standardMarketDataMessage : thirdSportMarketMessagesValid){
                    standardMarketDataMessage.setPlaceNum(placeNum++);
                }
            }
            List<ThirdSportMarketMessage> thirdSportMarketMessagesInvalid = standardMarketDataMessages.stream().filter(e -> (CollectionUtils.isEmpty(e.getThirdSportMarketOddsList()) || e.getThirdMarketSourceStatus()>= Constant.SPORT_MARKET.STATUS.DEACTIVATED)&&e.getPlaceNum()!=null).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(thirdSportMarketMessagesInvalid)) {
                for (ThirdSportMarketMessage standardMarketDataMessage : thirdSportMarketMessagesInvalid){
                    standardMarketDataMessage.setPlaceNum(placeNum++);
                }
            }
        }
    }

    /**
     * 主玩法赔率异常关闭提前结算 缓存Ao赔率
     *
     * @param ThirdSportMarketMessageList
     */
    private void cacheAOMarketOdds(StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> ThirdSportMarketMessageList) {
        List<ThirdSportMarketMessage> thirdSportMarketMessages = ThirdSportMarketMessageList.stream().filter(thirdSportMarketMessage ->
                MarginCategoryConfig.CHECK_MAIN_CATEGORY.contains(thirdSportMarketMessage.getMarketCategoryId())
                        && DataSourceCodeEnum.AO.getCode().equals(thirdSportMarketMessage.getDataSourceCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(thirdSportMarketMessages)) {
            return;
        }
        String key = Constant.REDIS_KEY.RONGHE_AO_THIRD_MARKET_ODDS + standardMatchInfo.getId();
        thirdSportMarketMessages.forEach(thirdSportMarketMessage -> {
            redisService.hSet(key, thirdSportMarketMessage.getRelationMarketId().toString(), thirdSportMarketMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
        });

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
        log.info("::{}::百家赔开始组装三方AO球头赔率消息并下发,topic:THIRD_MARKET_BALL_HEAD,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("THIRD_MARKET_BALL_HEAD:" + standardMatchMarketMessage.getStandardMatchInfoId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::百家赔 THIRD_MARKET_BALL_HEAD,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "百家赔 THIRD_MARKET_BALL_HEAD", throwable);
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
        log.info("::{}::百家赔 开始组装三方AO球头赔率消息并下发,topic:THIRD_MARKET_BASKETBALL_HEAD,request:{}", linkId, JSON.toJSONString(request));
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("THIRD_MARKET_BASKETBALL_HEAD:" + standardMatchMarketMessage.getStandardMatchInfoId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::百家赔 THIRD_MARKET_BASKETBALL_HEAD,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "百家赔 THIRD_MARKET_BASKETBALL_HEAD", throwable);
            }
        });
    }

    public void sendAutoOpenDataSourceCodeNewToMq(String linkId, Long standardMatchId, Map<String, String> newDataSourceCodeMap) {
        JSONObject obj = new JSONObject();
        obj.put("standardMatchId", standardMatchId);
        obj.put("newDataSourceCodeMap", newDataSourceCodeMap);
        obj.put("linkId", linkId);
        MessageBuilder<String> builder = MessageBuilder.withPayload(obj.toJSONString()).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::通知风控切换回数据源,topic:AUTO_OPEN_DATA_SOURCE_CODE_NEW_DATA,request:{}", linkId, JSON.toJSONString(obj));
        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("AUTO_OPEN_DATA_SOURCE_CODE_NEW_DATA:" + standardMatchId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::AUTO_OPEN_DATA_SOURCE_CODE_NEW_DATA,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "AUTO_OPEN_DATA_SOURCE_CODE_NEW_DATA", throwable);
            }
        });
    }

    public StandardMarketDataMessage thirdMarketConvertStandard(ThirdSportMarketMessage thirdSportMarketMessage) {
        StandardMarketDataMessage standardMarketDataMessage = new StandardMarketDataMessage();
        org.springframework.beans.BeanUtils.copyProperties(thirdSportMarketMessage, standardMarketDataMessage);
        standardMarketDataMessage.setRelationMarketId(thirdSportMarketMessage.getRelationMarketId());
        standardMarketDataMessage.setThirdMarketSourceStatus(thirdSportMarketMessage.getThirdMarketSourceStatus());
        standardMarketDataMessage.setStatus(thirdSportMarketMessage.getStatus());
        standardMarketDataMessage.setMarketType(thirdSportMarketMessage.getMarketType());
        standardMarketDataMessage.setPlaceNum(thirdSportMarketMessage.getPlaceNum());
        standardMarketDataMessage.setRelationMarketId(thirdSportMarketMessage.getRelationMarketId());
        MergeFunctionUtils.setNumberOfWinners( standardMarketDataMessage, thirdSportMarketMessage.getNumberOfWinners());
        standardMarketDataMessage.setAddition1(thirdSportMarketMessage.getAddition1());
        standardMarketDataMessage.setAddition2(thirdSportMarketMessage.getAddition2());
        standardMarketDataMessage.setAddition3(thirdSportMarketMessage.getAddition3());
        standardMarketDataMessage.setAddition4(thirdSportMarketMessage.getAddition4());
        standardMarketDataMessage.setAddition5(thirdSportMarketMessage.getAddition5());
        standardMarketDataMessage.setMarketCategoryId(thirdSportMarketMessage.getMarketCategoryId());
        standardMarketDataMessage.setOldThirdMarketSourceStatus(thirdSportMarketMessage.getThirdMarketSourceStatus());
        standardMarketDataMessage.setDataSourceCode(thirdSportMarketMessage.getDataSourceCode());
        standardMarketDataMessage.setThirdMarketSourceId(thirdSportMarketMessage.getThirdMarketSourceId());
        standardMarketDataMessage.setStandardMatchInfoId(thirdSportMarketMessage.getReferenceId());
        if (!CollectionUtils.isEmpty(thirdSportMarketMessage.getThirdSportMarketOddsList())) {
            standardMarketDataMessage.setMarketOddsList(thirdSportMarketMessage.getThirdSportMarketOddsList().stream().map(e -> {
                StandardMarketOddsDataMessage standardMarketOddsDataMessage = new StandardMarketOddsDataMessage();
                BeanUtils.copyProperties(e, standardMarketOddsDataMessage);
                standardMarketOddsDataMessage.setRelationMarketOddsId(e.getId());
                //N01/N02数据源默认使用公平赔率
                standardMarketOddsDataMessage.setPaOddsValue(e.getOriginalOddsValue());
                standardMarketOddsDataMessage.setOddsValue(e.getOriginalOddsValue());
                standardMarketOddsDataMessage.setOriginalOddsValue(e.getOriginalOddsValue());
                standardMarketOddsDataMessage.setThirdOddsFieldSourceId(e.getThirdOddsFieldSourceId());
                standardMarketOddsDataMessage.setOddsFieldsTemplateId(e.getOddsFieldsTemplateId());
                return standardMarketOddsDataMessage;
            }).collect(Collectors.toList()));
        }
        return standardMarketDataMessage;
    }

}
