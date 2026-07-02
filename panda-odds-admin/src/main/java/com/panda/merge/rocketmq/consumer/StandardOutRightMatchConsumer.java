package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardOutrightMatchDTO;
import com.panda.merge.rocketmq.processor.StandardOutRightMatchProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.STANDARD_OUTRIGHT_MATCH;

/**
 * 消费标准冠军赛事消息
 * @author    Aison
 * @since    2020年11月18日16:56:10
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = STANDARD_OUTRIGHT_MATCH,
        consumerGroup = "odds-group-"+STANDARD_OUTRIGHT_MATCH,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class StandardOutRightMatchConsumer implements RocketMQListener<Request<StandardOutrightMatchDTO>> {

    @Autowired
    private StandardOutRightMatchProcessor standardOutRightMatchProcessor;

    @Override
    public void onMessage(Request<StandardOutrightMatchDTO> request) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        standardOutRightMatchProcessor.processStandardOutRightMatch(request);
    }
}
