package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchSidelinedDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchSidelinedProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_SIDELINED_API;

/**
 * 泰森赛事缺阵球员名单（伤停）信息
 * @author  tell
 * @since   2021年4月17日13:20:38
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_SIDELINED_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_SIDELINED_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchSidelinedConsumer implements RocketMQListener<Request<List<ThirdMatchSidelinedDTO>>> {

    @Autowired
    private ThirdMatchSidelinedProcessor thirdMatchSidelinedProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<List<ThirdMatchSidelinedDTO>> dataCenterProducer;

    @Override
    public void onMessage(Request<List<ThirdMatchSidelinedDTO>> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MATCH_SIDELINED_API);
            return;
        }
        thirdMatchSidelinedProcessor.processMatchSidelinedData(request);
    }
}
