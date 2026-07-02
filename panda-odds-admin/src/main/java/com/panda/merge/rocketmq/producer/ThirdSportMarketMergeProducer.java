package com.panda.merge.rocketmq.producer;


import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.ListUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMatchThirdMarketMessage;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdSportMarketOdds;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据商盘口合并下发
 */
@Slf4j
@Component
public class ThirdSportMarketMergeProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    public void sendThirdSportMarketMessageToMQ(String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> thirdSportMarketMessages)
    {
        if (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()))
        {
            log.info("::{}::开始下发三方数据商盘口合并消息setPlaceNum",linkId);
            setPlaceNum(thirdSportMarketMessages);
        }
        StandardMatchThirdMarketMessage standardMatchThirdMarketMessage = new StandardMatchThirdMarketMessage();
        standardMatchThirdMarketMessage.setMarketList(thirdSportMarketMessages);
        standardMatchThirdMarketMessage.setMatchType(standardMatchInfo.getMatchType());
        standardMatchThirdMarketMessage.setSportId(standardMatchInfo.getSportId());
        standardMatchThirdMarketMessage.setStandardMatchInfoId(standardMatchInfo.getId());

        Request<StandardMatchThirdMarketMessage> standardMatchThirdMarketMessageRequest = new Request<>();
        standardMatchThirdMarketMessageRequest.setData(standardMatchThirdMarketMessage);
        standardMatchThirdMarketMessageRequest.setLinkId(linkId);
        MessageBuilder<Request<StandardMatchThirdMarketMessage>> builder = MessageBuilder.withPayload(standardMatchThirdMarketMessageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, standardMatchThirdMarketMessageRequest.getLinkId());
        log.info("::{}::开始下发三方数据商盘口合并消息,topic:STANDARD_THIRD_MARKET_ODDS", standardMatchThirdMarketMessageRequest.getLinkId());
        rocketMqTemplate.asyncSend("STANDARD_THIRD_MARKET_ODDS", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,STANDARD_THIRD_MARKET_ODDS send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_THIRD_MARKET_ODDS", throwable);
            }
        },standardMatchInfo.getId());
    }

    /**
     * 三方盘口设置主盘口
     * @param thirdSportMarketMessages
     */
    private void setPlaceNum(List<ThirdSportMarketMessage> thirdSportMarketMessages)
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
}
