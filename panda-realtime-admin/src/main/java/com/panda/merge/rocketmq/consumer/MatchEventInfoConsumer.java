package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.rocketmq.processor.MatchEventInfoProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;

 /**
 * 第三方赛事盘中事件接收
 * @author    Aison
 * @since     2020年10月22日11:04:05
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_EVENT_INFO_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_EVENT_INFO_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class MatchEventInfoConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    MatchEventInfoProcessor matchEventInfoProcessor;
     @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
     private boolean realtimeSwitch;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
    private boolean realtimeEventSwitch;
     @Resource
     private DataCenterProducer dataCenterProducer;

//    @ConsumerSwitch("realtime")
    @Override
    public void onMessage(MessageExt ext) {
         if (!realtimeSwitch && !realtimeEventSwitch) {
             dataCenterProducer.send(ext,THIRD_MATCH_EVENT_INFO_API);
             return;
         }
        matchEventInfoProcessor.putMatchEventInfo(ext,false);
    }
}
