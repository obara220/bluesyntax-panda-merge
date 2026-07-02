package com.panda.merge.rocketmq.consumer;


import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardCategoryIdsDiffDTO;
import com.panda.merge.rocketmq.processor.ClearStandardCategoryIdsDiffProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.STANDARD_CATEGORYID_CLEAR_DIFF;

/**
 * 接受AO apply 清除水差
 */

@Slf4j
@Component
@RocketMQMessageListener(
        topic = STANDARD_CATEGORYID_CLEAR_DIFF,
        consumerGroup = "odds-group-" + STANDARD_CATEGORYID_CLEAR_DIFF,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ClearStandardCategoryIdsDiffConsumer implements RocketMQListener<Request<StandardCategoryIdsDiffDTO>> {

    @Autowired
    private ClearStandardCategoryIdsDiffProcessor clearStandardCategoryIdsDiffProcessor;

    @Override
    public void onMessage(Request<StandardCategoryIdsDiffDTO> request) {
        clearStandardCategoryIdsDiffProcessor.processor(request);
    }
}
