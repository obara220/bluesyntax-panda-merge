package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchExpectationDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchExpectationProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EXPECTATION_API;

/**
 * 三方赛事预期信息更新
 *
 * @author aldrich
 * @since 2024/11/6
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_EXPECTATION_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_EXPECTATION_API,
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchExpectationConsumer implements RocketMQListener<Request<ThirdMatchExpectationDTO>> {


    @Autowired
    private ThirdMatchExpectationProcessor thirdMatchExpectationProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchExpectationDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchExpectationDTO> message) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(message,THIRD_MATCH_EXPECTATION_API);
            return;
        }
        thirdMatchExpectationProcessor.processMatchExpectatioData(message);
    }
}
