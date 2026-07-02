package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMatchPreStatusMessage;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StandardMatchPreStatusMessageProducer extends BaseProcessor {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendStandardMatchPreStatus(String linkId, StandardMatchInfo standardMatchInfo, Double value) {
        StandardMatchPreStatusMessage standardMatchPreStatusMessage = new StandardMatchPreStatusMessage();
        standardMatchPreStatusMessage.setStandardMatchId(standardMatchInfo.getId());
        standardMatchPreStatusMessage.setValue(value);

        Request<StandardMatchPreStatusMessage> request = new Request<>();
        request.setData(standardMatchPreStatusMessage);
        request.setLinkId(linkId);
        MessageBuilder<Request<StandardMatchPreStatusMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始下发提前结算赛事状态,topic:STANDARD_MATCH_PRE_STATUS", linkId);
        rocketMqTemplate.asyncSend("STANDARD_MATCH_PRE_STATUS:" + standardMatchInfo.getId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,STANDARD_MATCH_PRE_STATUS send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MATCH_PRE_STATUS", throwable);
            }
        });
    }

}
