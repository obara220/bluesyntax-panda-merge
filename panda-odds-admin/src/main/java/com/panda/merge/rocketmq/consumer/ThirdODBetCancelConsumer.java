package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetCancelDTO;
import com.panda.merge.rocketmq.processor.ThirdODBetCancelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.THIRD_MARKET_BET_CANCEL;

/**
 * 盘口取消时调用，对应上游bet-cancel事件
 * @author : bevan
 * @since    2024年08月20日13:53:38
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_MARKET_BET_CANCEL,
        consumerGroup = "odds-group-"+THIRD_MARKET_BET_CANCEL,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ThirdODBetCancelConsumer implements RocketMQListener<Request<ThirdBetCancelDTO>> {

    @Autowired
    private ThirdODBetCancelProcessor thirdBetCancelProcessor;

    @Override
    public void onMessage(Request<ThirdBetCancelDTO> thirdBetCancelRequest) {
        thirdBetCancelProcessor.thirdBetCancel(thirdBetCancelRequest);
    }
}
