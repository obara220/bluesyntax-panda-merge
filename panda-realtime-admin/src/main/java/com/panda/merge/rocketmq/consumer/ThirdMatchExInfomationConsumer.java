package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchExInfomationDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchExInfomationProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EX_INFOMATION_API;

/**
 * 泰森赛事比赛情报综合资讯信息
 * @author  tell
 * @since   2021年4月23日12:48:14
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_EX_INFOMATION_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_EX_INFOMATION_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchExInfomationConsumer implements RocketMQListener<Request<ThirdMatchExInfomationDTO>> {

    @Autowired
    private ThirdMatchExInfomationProcessor thirdMatchExInfomationProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchExInfomationDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchExInfomationDTO> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MATCH_EX_INFOMATION_API);
            return;
        }
        thirdMatchExInfomationProcessor.processMatchExInfomationData(request);
    }
}
