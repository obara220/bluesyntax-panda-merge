package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.ConfigMarketDisplayTradeMessage;
import com.panda.merge.model.ConfigMarketDisplayTrade;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/26 <br>
 * @see com.panda.merge.rocketmq.producer <br>
 */
@Slf4j
@Component
public class ConfigMarketDisplayProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendConfigMarketDisplayToMQ(String linkId, ConfigMarketDisplayTrade configMarketDisplayTrade) {
        ConfigMarketDisplayTradeMessage configMarketDisplayTradeMessage = new ConfigMarketDisplayTradeMessage();
        BeanUtils.copyProperties(configMarketDisplayTrade, configMarketDisplayTradeMessage);
        Request<ConfigMarketDisplayTradeMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(configMarketDisplayTradeMessage);
        MessageBuilder<Request<ConfigMarketDisplayTradeMessage>> builder = MessageBuilder.withPayload(messageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("CONFIG_MARKET_DISPLAY_TRADE",builder.build());
        log.info("::{}::开始组装标操盘配置并下发,topic:CONFIG_MARKET_DISPLAY_TRADE,标准盘口ID：{},request:{}", linkId, configMarketDisplayTrade.getStandardMatchId(), JSON.toJSONString(messageRequest));
    }
}
