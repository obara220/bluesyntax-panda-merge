package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SaleUpdateLiveBusinessEventMessage;
import com.panda.merge.rocketmq.processor.LiveBusinessEventUpdateProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.LIVE_BUSINESS_EVENT_UPDATE_MESSAGE;

/**
 * 切换数据源后后补发事件给业务
 * @author      Aison
 * @since       2020年10月23日09:57:07
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = LIVE_BUSINESS_EVENT_UPDATE_MESSAGE,
        consumerGroup = CONSUME_REALTIME_GROUP + LIVE_BUSINESS_EVENT_UPDATE_MESSAGE,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class LiveBusinessEventUpdateConsumer implements RocketMQListener<Request<SaleUpdateLiveBusinessEventMessage>> {

    @Autowired
    LiveBusinessEventUpdateProcessor liveBusinessEventUpdateProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
    private boolean realtimeEventSwitch;
    @Resource
    private DataCenterProducer<SaleUpdateLiveBusinessEventMessage> dataCenterProducer;

//    @ConsumerSwitch("realtime")
    @Override
    public void onMessage(Request<SaleUpdateLiveBusinessEventMessage> request) {
        if (!realtimeSwitch && !realtimeEventSwitch) {
            dataCenterProducer.send(request,LIVE_BUSINESS_EVENT_UPDATE_MESSAGE);
            return;
        }
        liveBusinessEventUpdateProcessor.reissueEventInfo(request);
    }
}
