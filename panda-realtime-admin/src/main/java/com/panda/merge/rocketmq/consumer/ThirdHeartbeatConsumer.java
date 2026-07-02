package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.HeartMessage;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.rocketmq.producer.ThirdHeartbeatProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 第三方心跳验证（业务调用）<br>
 * @author   tell
 * @since    2020年10月4日15:33:10
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_HEARTBEAT,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_HEARTBEAT,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdHeartbeatConsumer implements RocketMQListener<Request<HeartMessage>> {

    @Autowired
    private ThirdHeartbeatProducer thirdHeartbeatProducer;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<HeartMessage> dataCenterProducer;

    @Override
    public void onMessage(Request<HeartMessage> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_HEARTBEAT);
            return;
        }
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_HEARTBEAT+"】MQ 收到三方心跳验证开始，入参：request：{}",request);
        thirdHeartbeatProducer.sendThirdHeartbeat(request);
        log.info("【"+ PROJECT_ID_REALTIME +" ："+ THIRD_HEARTBEAT+"】MQ 收到三方心跳验证结束，入参：request：{}",request);
    }
}
