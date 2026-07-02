package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketCategorySellMessage;
import com.panda.merge.model.I18nOutrightMarket;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StandardMarketCategorySellProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 投递风控
     * @param linkId
     * @param standardMarketCategorySellMessages
     */
    public void sendStandardMarketCategorySellMessage(String linkId, List<StandardMarketCategorySellMessage> standardMarketCategorySellMessages) {
        Request<List<StandardMarketCategorySellMessage>> request = new Request<>();
        request.setData(standardMarketCategorySellMessages);
        request.setLinkId(linkId);
        MessageBuilder<Request<List<StandardMarketCategorySellMessage>>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装开售玩法消息并下发,topic:STANDARD_MAEKET_CATEGORY_SELL, request:{}", linkId, JSON.toJSONString(request));
        rocketMqTemplate.asyncSend("STANDARD_MAEKET_CATEGORY_SELL:", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MAEKET_CATEGORY_SELL", throwable);
            }
        });
    }
}
