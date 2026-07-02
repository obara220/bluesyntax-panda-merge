package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.A99SystemConfigParam;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.A99SystemParamProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * A99系统参数
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "a99.system.param.mq.enabled", havingValue = "true", matchIfMissing = true)
@RocketMQMessageListener(
        topic = A99_SYSTEM_PARAM_CONFIG,
        consumerGroup = CONSUMER_PANDA_A99_GROUP + A99_SYSTEM_PARAM_CONFIG,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
public class A99ConfigParamConsumer implements RocketMQListener<Request<A99SystemConfigParam>> {

    @Autowired
    private A99SystemParamProcessor a99SystemParamProcessor;

    @Override
    public void onMessage(Request<A99SystemConfigParam> request) {
        log.info("{}::接收到A99系统参数:{}", request.getLinkId(), request);
        a99SystemParamProcessor.execute(request);
    }
}
