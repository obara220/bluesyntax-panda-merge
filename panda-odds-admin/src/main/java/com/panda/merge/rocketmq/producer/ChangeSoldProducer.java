package com.panda.merge.rocketmq.producer;


import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SoldMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ChangeSoldProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendChangeSoldMessageToMQ(Request<SoldMessage> soldMessageRequest)
    {
        MessageBuilder<Request<SoldMessage>> builder = MessageBuilder.withPayload(soldMessageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, soldMessageRequest.getLinkId());
        rocketMqTemplate.send("SOLD_MESSAGE",builder.build());
        log.info("::{}::开售组装切换赔率数据源数据并下发,topic:SOLD_MESSAGE,request:{}", soldMessageRequest.getLinkId(), JSON.toJSONString(soldMessageRequest));
    }
}
