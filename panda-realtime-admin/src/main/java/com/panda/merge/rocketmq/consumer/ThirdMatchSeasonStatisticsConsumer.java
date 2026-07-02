package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchSeasonStatisticsDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchSeasonStatisticsProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_SEASON_STATISTICS_API;

/**
 * 当前赛季统计信息
 *
 * @author aldrich
 * @since 2024/10/14
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_SEASON_STATISTICS_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_SEASON_STATISTICS_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchSeasonStatisticsConsumer implements RocketMQListener<Request<ThirdMatchSeasonStatisticsDTO>> {

    @Autowired
    ThirdMatchSeasonStatisticsProcessor thirdMatchSeasonStatisticsProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchSeasonStatisticsDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchSeasonStatisticsDTO> message) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(message,THIRD_MATCH_SEASON_STATISTICS_API);
            return;
        }
        thirdMatchSeasonStatisticsProcessor.processMatchSeasonStatisticsData(message);
    }
}
