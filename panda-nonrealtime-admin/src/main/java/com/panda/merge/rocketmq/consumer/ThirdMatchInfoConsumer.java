package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchInfoProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_INFO_API;

/**
 * 接收MQ赛事信息<br>
 * @author tell<br>
 * @since 2020年9月2日19:32:11 <br>
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_INFO_API,
        consumerGroup = CONSUME_NONREALTIME_GROUP + THIRD_MATCH_INFO_API,
        consumeThreadMax = 256,
        consumeTimeout = 10000L)
@DependsOn("nonrealtimeAdminApplication")
public class ThirdMatchInfoConsumer implements RocketMQListener<Request<List<ThirdMatchInfoDTO>>> {

    @Autowired
    ThirdMatchInfoProcessor thirdMatchInfoProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<List<ThirdMatchInfoDTO>> dataCenterProducer;

    @Override
    public void onMessage(Request<List<ThirdMatchInfoDTO>> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MATCH_INFO_API);
            return;
        }
        thirdMatchInfoProcessor.processMatchData(request);
    }
}
