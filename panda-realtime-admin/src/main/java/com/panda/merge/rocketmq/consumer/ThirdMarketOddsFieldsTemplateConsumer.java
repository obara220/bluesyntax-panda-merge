package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketCategoryFieldDTO;
import com.panda.merge.rocketmq.processor.ThirdMarketCategoryProcessor;
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
import static com.panda.merge.constant.ConstantSystem.THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.consumer
 * @description : 第三方玩法投注项数据
 * @date: 2020-09-11 15:27
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */

@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class ThirdMarketOddsFieldsTemplateConsumer implements RocketMQListener<Request<List<ThirdMarketCategoryFieldDTO>>> {

    @Autowired
    private ThirdMarketCategoryProcessor thirdMarketCategoryProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<List<ThirdMarketCategoryFieldDTO>> dataCenterProducer;

    @Override
    public void onMessage(Request<List<ThirdMarketCategoryFieldDTO>> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MARKET_ODDS_FIELDS_TEMPLATE_API);
            return;
        }
        thirdMarketCategoryProcessor.putMarketOddsFields(request);
    }
}
