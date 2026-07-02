package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchLineupSimpleDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchLineupProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_LINEUP_API;

/**
 * 泰森赛事首发阵容信息
 * @author  tell
 * @since   2021年2月6日17:28:46
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_LINEUP_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_LINEUP_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchLineupConsumer implements RocketMQListener<Request<ThirdMatchLineupSimpleDTO>> {

    @Autowired
    private ThirdMatchLineupProcessor thirdMatchLineupProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchLineupSimpleDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchLineupSimpleDTO> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MATCH_LINEUP_API);
            return;
        }
        thirdMatchLineupProcessor.processMatchLineupData(request);
    }
}
