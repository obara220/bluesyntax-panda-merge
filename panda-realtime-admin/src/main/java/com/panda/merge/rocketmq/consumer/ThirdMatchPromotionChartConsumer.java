package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.thirdmatch.ThirdMatchPromotionChartDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchPromotionChartProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_PROMOTION_CHART_API;

/**
 * 杯赛淘汰赛
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_PROMOTION_CHART_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_PROMOTION_CHART_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdMatchPromotionChartConsumer implements RocketMQListener<Request<ThirdMatchPromotionChartDTO>> {

    @Autowired
    private ThirdMatchPromotionChartProcessor thisProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<ThirdMatchPromotionChartDTO> dataCenterProducer;

    @Override
    public void onMessage(Request<ThirdMatchPromotionChartDTO> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MATCH_PROMOTION_CHART_API);
            return;
        }
        thisProcessor.processData(request);
    }
}
