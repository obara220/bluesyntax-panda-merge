package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdSeasonInfoDTO;
import com.panda.merge.dto.ThirdSportTournamentDTO;
import com.panda.merge.rocketmq.processor.ThirdSeasonInfoProcessor;
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

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * @Author Kepa
 * @Date 2021/2/5 20:51
 * @Desc 接收MQ赛季信息
 * @Version 1.0
 */
@Slf4j
@Component
@Order
@RocketMQMessageListener(topic = THIRD_SEASON_INFO_API,
        consumerGroup = CONSUME_NONREALTIME_GROUP + THIRD_SEASON_INFO_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("nonrealtimeAdminApplication")
public class ThirdSportSeasonConsumer implements RocketMQListener<Request<ThirdSeasonInfoDTO>> {

    @Autowired
    ThirdSeasonInfoProcessor thirdSeasonInfoProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdSeasonInfoDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdSeasonInfoDTO> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_SEASON_INFO_API);
            return;
        }
        thirdSeasonInfoProcessor.processSeasonData(request);
    }
}
