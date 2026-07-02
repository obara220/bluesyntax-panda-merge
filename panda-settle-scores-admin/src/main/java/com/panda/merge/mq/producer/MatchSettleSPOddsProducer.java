package com.panda.merge.mq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchSettleSpOdds;
import com.panda.merge.v2.entity.MatchSettleSpOddsEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
@Slf4j
public class MatchSettleSPOddsProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Resource(name = "secondTemplate")
    private RocketMQTemplate secondTemplate;

    //赛事级别重跑结算比分
    public void sendMatchSettleSPOdds(MatchSettleSpOdds matchSettleSpOdds) {

        Request<MatchSettleSpOdds> reqMessage = new Request<>();
        reqMessage.setLinkId(matchSettleSpOdds.getStandardMatchId()+"_"+matchSettleSpOdds.getId());
        reqMessage.setData(matchSettleSpOdds);
        MessageBuilder<Request<MatchSettleSpOdds>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, matchSettleSpOdds.getStandardMatchId()+"_"+matchSettleSpOdds.getId());
        rocketMqTemplate.send("MATCH_SETTLE_SP_ODDS:" + matchSettleSpOdds.getStandardMatchId(), builder.build());
        log.info("::{}::开始组装赛事投注项赛果信息并下发,topic:MATCH_SETTLE_SP_ODDS,request={}", matchSettleSpOdds.getId(), JSON.toJSONString(reqMessage));
    }

    //赛事级别重跑结算比分
    public void sendMatchSettleSPOdds(MatchSettleSpOddsEntity matchSettleSpOdds) {

        Request<MatchSettleSpOddsEntity> reqMessage = new Request<>();
        reqMessage.setLinkId(matchSettleSpOdds.getStandardMatchId()+"_"+matchSettleSpOdds.getId());
        reqMessage.setData(matchSettleSpOdds);
        MessageBuilder<Request<MatchSettleSpOddsEntity>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, matchSettleSpOdds.getStandardMatchId()+"_"+matchSettleSpOdds.getId());
        rocketMqTemplate.send("MATCH_SETTLE_SP_ODDS:" + matchSettleSpOdds.getStandardMatchId(), builder.build());
        log.info("::{}::开始组装赛事投注项赛果信息并下发,topic:MATCH_SETTLE_SP_ODDS,request={}", matchSettleSpOdds.getId(), JSON.toJSONString(reqMessage));
    }

    public void sendToSlaveMq(String topic, String linkId, List<Object> data, String tableName, boolean isInsert) {
        Request<Object> reqMessage = new Request<>();
        reqMessage.setLinkId(linkId);
        reqMessage.setData(data);
        MessageBuilder<Request<Object>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId)
                .setHeader(RocketMQHeaders.TAGS, tableName)
                .setHeader(CommonConstant.TAG, tableName)
                .setHeader(CommonConstant.IS_INSERT, isInsert);
        secondTemplate.syncSendOrderly(topic, builder.build(), tableName);
        log.info("sendToSlaveMq completed with linkId: {} tableName:{}", linkId, tableName);

    }

}
