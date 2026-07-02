package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdSportTeamDTO;
import com.panda.merge.rocketmq.processor.ThirdSportTeampPayerProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_NONREALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_SPORT_TEAM_API;

/**
 * 接收MQ球队人员信息<br>
 * @author    tell<br>
 * @since    2020年9月2日19:32:11 <br>
 */
@Slf4j
@Component
@Order
@RocketMQMessageListener(topic = THIRD_SPORT_TEAM_API,
        consumerGroup = CONSUME_NONREALTIME_GROUP + THIRD_SPORT_TEAM_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("nonrealtimeAdminApplication")
public class ThirdSportPayerConsumer implements RocketMQListener<Request<List<ThirdSportTeamDTO>>> {

    @Autowired
    ThirdSportTeampPayerProcessor thirdSportTeampPayerProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<List<ThirdSportTeamDTO>> dataCenterProducer;

    @Override
    public void onMessage(Request<List<ThirdSportTeamDTO>> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_SPORT_TEAM_API);
            return;
        }
        thirdSportTeampPayerProcessor.processPayerData(request);
    }
}
