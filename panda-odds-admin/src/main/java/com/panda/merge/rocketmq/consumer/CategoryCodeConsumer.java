package com.panda.merge.rocketmq.consumer;


import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchInternalCode;
import com.panda.merge.rocketmq.processor.CategoryCodeProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RocketMQMessageListener(
        topic = ConstantSystem.THIRD_INTERNALCODE_API,
        consumerGroup = "odds-group-THIRD_INTERNALCODE_API",
        consumeThreadMax = 20
)
@DependsOn("oddsAdminApplication")
public class CategoryCodeConsumer implements RocketMQListener<Request<ThirdMatchInternalCode>> {

    @Autowired
    private CategoryCodeProcessor categoryCodeProcessor;

    @Override
    public void onMessage(Request<ThirdMatchInternalCode> request) {
        categoryCodeProcessor.processThirdMatchInternalCode(request);
    }
}
