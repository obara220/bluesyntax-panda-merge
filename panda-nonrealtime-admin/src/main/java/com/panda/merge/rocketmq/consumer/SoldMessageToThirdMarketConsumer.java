package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.rocketmq.processor.SoldMessageToThirdMarketProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 开售处理<br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/27 <br>
 * @see com.panda.merge.rocketmq.consumer <br>
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = SOLD_MESSAGE,
        consumerGroup = CONSUME_NONREALTIME_GROUP + SOLD_MESSAGE,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("nonrealtimeAdminApplication")
public class SoldMessageToThirdMarketConsumer implements RocketMQListener<Request<SoldMessage>> {
    @Autowired
    SoldMessageToThirdMarketProcessor soldMessageToThirdMarketProcessor;
    @Override
    public void onMessage(Request<SoldMessage> soldMessageRequest) {
        soldMessageToThirdMarketProcessor.execute(soldMessageRequest);
    }
}
