package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetCancelDTO;
import com.panda.merge.rocketmq.processor.ThirdBetCancelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.THIRD_BET_CANCEL_API;

/**
 * 盘口取消时调用，对应上游bet-cancel事件
 * @author : bevan
 * @since    2020年11月18日16:53:38
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_BET_CANCEL_API,
        consumerGroup = "odds-group-"+THIRD_BET_CANCEL_API,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ThirdBetCancelConsumer implements RocketMQListener<Request<ThirdBetCancelDTO>> {

    @Autowired
    private ThirdBetCancelProcessor thirdBetCancelProcessor;

    @Override
    public void onMessage(Request<ThirdBetCancelDTO> thirdBetCancelRequest) {
        thirdBetCancelProcessor.thirdBetCancel(thirdBetCancelRequest);
    }
}
