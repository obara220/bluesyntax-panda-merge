package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchHistoryStatisticsDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchHistoryStatisticsProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_HISTORY_STATISTICS_API;

/**
 * 赛事历史对阵信息（含现在，未来赛事）
 * @author  tell
 * @since   2021年2月9日15:52:11
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_HISTORY_STATISTICS_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_HISTORY_STATISTICS_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchHistoryStatisticsConsumer implements RocketMQListener<Request<ThirdMatchHistoryStatisticsDTO>> {

    @Autowired
    private ThirdMatchHistoryStatisticsProcessor thirdMatchHistoryStatisticsProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchHistoryStatisticsDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchHistoryStatisticsDTO> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MATCH_HISTORY_STATISTICS_API);
            return;
        }
        thirdMatchHistoryStatisticsProcessor.processMatchHistoryStatisticsData(request);
    }
}
