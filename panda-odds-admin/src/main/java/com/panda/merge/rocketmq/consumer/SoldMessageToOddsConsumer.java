package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.rocketmq.processor.SoldMessageToOddsProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

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
        topic = "SOLD_MESSAGE",
        consumerGroup = "odds-group-SOLD_MESSAGE",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class SoldMessageToOddsConsumer implements RocketMQListener<Request<SoldMessage>> {

    @Autowired
    private SoldMessageToOddsProcessor soldMessageToOddsProcessor;

    @Override
    public void onMessage(Request<SoldMessage> soldMessageRequest) {
        soldMessageToOddsProcessor.soldMessageToOdds(soldMessageRequest);
    }
}
