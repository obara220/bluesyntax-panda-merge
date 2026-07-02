package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdSportPlayerRankingDTO;
import com.panda.merge.rocketmq.processor.ThirdSportPlayerRankingProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 联赛下球员排行榜单
 * @author   tell
 * @since    2020年10月17日19:23:03
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_SPORT_PAYER_RANKING_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_SPORT_PAYER_RANKING_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class ThirdSportPlayerRankingConsumer implements RocketMQListener<Request<List<ThirdSportPlayerRankingDTO>>> {

    @Autowired
    private ThirdSportPlayerRankingProcessor thirdSportPlayerRankingProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<List<ThirdSportPlayerRankingDTO>> dataCenterProducer;

    @Override
    public void onMessage(Request<List<ThirdSportPlayerRankingDTO>> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_SPORT_PAYER_RANKING_API);
            return;
        }
        thirdSportPlayerRankingProcessor.processPlayerRankingData(request);
    }
}
