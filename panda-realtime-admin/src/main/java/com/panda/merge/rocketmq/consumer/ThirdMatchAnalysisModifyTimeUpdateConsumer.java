package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchAnalysisModifyTimeDTO;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.rocketmq.processor.ThirdMatchAnalysisModifyTimeUpdateProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.*;

/** 赛事分析修改modify_time */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_ANALYSIS_MODIFY_TIME,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_ANALYSIS_MODIFY_TIME,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchAnalysisModifyTimeUpdateConsumer implements RocketMQListener<Request<ThirdMatchAnalysisModifyTimeDTO>> {

    @Resource
    private ThirdMatchAnalysisModifyTimeUpdateProcessor thirdMatchAnalysisModifyTimeUpdateProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchAnalysisModifyTimeDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchAnalysisModifyTimeDTO> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MATCH_ANALYSIS_MODIFY_TIME);
            return;
        }
        thirdMatchAnalysisModifyTimeUpdateProcessor.updateThirdMatchAnalysisModifyTime(request);
    }
}
