package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.rocketmq.processor.LSSendRscMatchStatusProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.LS_SEND_RSC_MATCH_STATUS;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = LS_SEND_RSC_MATCH_STATUS,
        consumerGroup = "odds-group-"+LS_SEND_RSC_MATCH_STATUS,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class LSSendRscMatchStatusConsumer  implements RocketMQListener<Request<TradeMarketConfigDTO>> {
    @Autowired
    LSSendRscMatchStatusProcessor lsSendRscMatchStatusProcessor;
    @Override
    public void onMessage(Request<TradeMarketConfigDTO> tradeMarketConfigDTORequest) {
        lsSendRscMatchStatusProcessor.processor(tradeMarketConfigDTORequest);
    }
}
