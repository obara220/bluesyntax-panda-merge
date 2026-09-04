package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetSettlementRollbackDTO;
import com.panda.merge.rocketmq.processor.ThirdBetSettlementRollbackProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.THIRD_BET_SETTLEMENT_ROLLBACK_API;

/**
 * 回滚盘口结算操作
 * @author : bevan
 * @since    2020年11月18日16:52:39
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_BET_SETTLEMENT_ROLLBACK_API,
        consumerGroup = "odds-group-"+THIRD_BET_SETTLEMENT_ROLLBACK_API,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ThirdBetSettlementRollbackConsumer implements RocketMQListener<Request<ThirdBetSettlementRollbackDTO>> {

    @Autowired
    private ThirdBetSettlementRollbackProcessor thirdBetSettlementRollbackProcessor;

    @Override
    public void onMessage(Request<ThirdBetSettlementRollbackDTO> request) {
        thirdBetSettlementRollbackProcessor.thirdBetSettlementRollback(request);
    }
}
