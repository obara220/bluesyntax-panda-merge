package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchFrontStatisticsDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchFrontStatisticsProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_FRONT_STATISTICS_API;

/**
 * 正面交手统计信息
 *
 * @author aldrich
 * @since 2024/10/16
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_FRONT_STATISTICS_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_FRONT_STATISTICS_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchFrontStatisticsConsumer implements RocketMQListener<Request<ThirdMatchFrontStatisticsDTO>> {

    @Autowired
    private ThirdMatchFrontStatisticsProcessor thirdMatchFrontStatisticsProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchFrontStatisticsDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchFrontStatisticsDTO> message) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(message,THIRD_MATCH_FRONT_STATISTICS_API);
            return;
        }
        thirdMatchFrontStatisticsProcessor.processMatchFrontStatisticsData(message);
    }
}
