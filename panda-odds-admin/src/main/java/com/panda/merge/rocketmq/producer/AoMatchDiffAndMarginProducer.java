package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.TradeMarketDiffAndMarginConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AO玩法水差 margin
 */
@Slf4j
@Component
public class AoMatchDiffAndMarginProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendConfig(String linkId, List<TradeMarketDiffAndMarginConfigDTO> configDTO) {
        MessageBuilder<List<TradeMarketDiffAndMarginConfigDTO>> builder = MessageBuilder.withPayload(configDTO)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("AO_MATCH_DIFF_MARGIN_CONFIGS", builder.build());
        log.info("::{}::开售下发AO水差margin,topic:SOLD_MESSAGE,request:{}", linkId, JSON.toJSONString(configDTO));
    }

}
