package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchHistoryExpressionDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchHistoryExpressionProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_HISTORY_EXPRESSION_API;

/**
 * 联赛球队历史表现
 *
 * @author aldrich
 * @since 2024/10/14
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_HISTORY_EXPRESSION_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_HISTORY_EXPRESSION_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchHistoryExpressionConsumer implements RocketMQListener<Request<ThirdMatchHistoryExpressionDTO>> {

    @Autowired
    ThirdMatchHistoryExpressionProcessor thirdMatchHistoryExpressionProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchHistoryExpressionDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchHistoryExpressionDTO> message) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(message,THIRD_MATCH_HISTORY_EXPRESSION_API);
            return;
        }
        thirdMatchHistoryExpressionProcessor.processMatchHistoryExpressionData(message);
    }
}
