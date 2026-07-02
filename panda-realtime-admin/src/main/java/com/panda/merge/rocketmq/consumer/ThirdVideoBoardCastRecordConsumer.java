package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdVideoBoardCastRecordDTO;
import com.panda.merge.rocketmq.processor.ThirdVideoBoardCastRecordProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_VIDEO_INFO_API;

/**
 * 泰森播控中心接入
 * @author  tell
 * @since   2020年9月15日20:23:41
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_VIDEO_INFO_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_VIDEO_INFO_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdVideoBoardCastRecordConsumer implements RocketMQListener<Request<ThirdVideoBoardCastRecordDTO>> {

    @Autowired
    ThirdVideoBoardCastRecordProcessor thirdVideoBoardCastRecordProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdVideoBoardCastRecordDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdVideoBoardCastRecordDTO> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_VIDEO_INFO_API);
            return;
        }
        thirdVideoBoardCastRecordProcessor.processVideoData(request);
    }
}
