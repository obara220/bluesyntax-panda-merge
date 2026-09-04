package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.rocketmq.processor.SoldMessageToEventProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 开售处理后补发事件
 * @author       Aison
 * @createDate  2020年10月23日10:00:10
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = SOLD_MESSAGE,
        consumerGroup = CONSUME_REALTIME_GROUP + SOLD_MESSAGE,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class SoldMessageToEventConsumer implements RocketMQListener<Request<SoldMessage>> {

    @Autowired
    SoldMessageToEventProcessor soldMessageToEventProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
    private boolean realtimeEventSwitch;
    @Resource
    private DataCenterProducer<SoldMessage> dataCenterProducer;

//    @ConsumerSwitch("realtime")
    @Override
    public void onMessage(Request<SoldMessage> request) {
        if (!realtimeSwitch && !realtimeEventSwitch) {
            dataCenterProducer.send(request,SOLD_MESSAGE);
            return;
        }
        log.info("【"+ PROJECT_ID_REALTIME+" ："+ SOLD_MESSAGE+"】【::"+request.getLinkId()+"::】开售处理后补发事件开始");
        soldMessageToEventProcessor.soldMessageToEvent(request);
        log.info("【"+ PROJECT_ID_REALTIME+" ："+ SOLD_MESSAGE+"】【::"+request.getLinkId()+"::】开售处理后补发事件结束");
    }
}
