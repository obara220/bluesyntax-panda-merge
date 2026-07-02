package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetCancelDTO;
import com.panda.merge.dto.message.StandardBetCancelItemMessage;
import com.panda.merge.dto.message.StandardBetCancelMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 推送至取消注单消息队列
 *
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.producer
 * @description : TODO
 * @date: 2020-09-09 14:58
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
public class StandardBetCancelProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendStandardBetCancel(String linkId, Long standardMatchId, Long sportId, ThirdBetCancelDTO thirdBetCancelDTO, List<StandardBetCancelItemMessage> standardBetCancelItemMessages) {
        StandardBetCancelMessage standardBetCancelMessage = new StandardBetCancelMessage();
        BeanUtils.copyProperties(thirdBetCancelDTO, standardBetCancelMessage);
        standardBetCancelMessage.setMatchId(standardMatchId);
        standardBetCancelMessage.setMarkets(standardBetCancelItemMessages);
        standardBetCancelMessage.setSportId(sportId);

        Request<StandardBetCancelMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(standardBetCancelMessage);
        MessageBuilder<Request<StandardBetCancelMessage>> builder = MessageBuilder.withPayload(messageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.send("STANDARD_BET_CANCEL:" + standardMatchId, builder.build());
        log.info("::{}::开售组装取消注单并下发,topic:STANDARD_BET_CANCEL,标准盘口ID：{},request:{}", linkId, standardMatchId, JSON.toJSONString(messageRequest));
    }


    public void sendStandardODBetCancel(String linkId, Long standardMatchId, Long sportId, ThirdBetCancelDTO thirdBetCancelDTO, List<StandardBetCancelItemMessage> standardBetCancelItemMessages) {
        StandardBetCancelMessage standardBetCancelMessage = new StandardBetCancelMessage();
        BeanUtils.copyProperties(thirdBetCancelDTO, standardBetCancelMessage);
        standardBetCancelMessage.setMatchId(standardMatchId);
        standardBetCancelMessage.setMarkets(standardBetCancelItemMessages);
        standardBetCancelMessage.setSportId(sportId);

        Request<StandardBetCancelMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(standardBetCancelMessage);
        MessageBuilder<Request<StandardBetCancelMessage>> builder = MessageBuilder.withPayload(messageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.send("STANDARD_MARKET_BET_CANCEL:" + standardMatchId, builder.build());
        log.info("::{}::开售组装OD取消注单并下发,topic:STANDARD_MARKET_BET_CANCEL,标准盘口ID：{},request:{}", linkId, standardMatchId, JSON.toJSONString(messageRequest));
    }
}
