package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMarketCategoryDTO;
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
import static com.panda.merge.constant.ConstantSystem.THIRD_MARKET_CATEGORY_API;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.consumer
 * @description : 第三方玩法数据
 * @date: 2020-09-11 9:18
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_MARKET_CATEGORY_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MARKET_CATEGORY_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class ThirdMarketCategoryConsumer implements RocketMQListener<Request<List<ThirdMarketCategoryDTO>>> {

    @Autowired
    private ThirdMarketCategoryProcessor marketCategoryProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<List<ThirdMarketCategoryDTO>> dataCenterProducer;

    @Override
    public void onMessage(Request<List<ThirdMarketCategoryDTO>> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_MARKET_CATEGORY_API);
            return;
        }
        marketCategoryProcessor.putMarketCategory(request);
    }
}
