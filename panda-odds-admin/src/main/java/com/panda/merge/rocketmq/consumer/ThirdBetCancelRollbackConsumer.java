package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetCancelRollbackDTO;
import com.panda.merge.rocketmq.processor.ThirdBetCancelRollbackProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.THIRD_BET_CANCEL_ROLLBACK_API;

/**
 * 回滚盘口取消操作时调用，对应上游rollback bet cancel
 * @author : bevan
 * @since    2020年11月18日16:53:22
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_BET_CANCEL_ROLLBACK_API,
        consumerGroup = "odds-group-"+THIRD_BET_CANCEL_ROLLBACK_API,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ThirdBetCancelRollbackConsumer implements RocketMQListener<Request<ThirdBetCancelRollbackDTO>> {

    @Autowired
    private ThirdBetCancelRollbackProcessor thirdBetCancelRollbackProcessor;

    @Override
    public void onMessage(Request<ThirdBetCancelRollbackDTO> thirdBetCancelRequest) {
        thirdBetCancelRollbackProcessor.thirdBetCancelRollback(thirdBetCancelRequest);
    }

}
