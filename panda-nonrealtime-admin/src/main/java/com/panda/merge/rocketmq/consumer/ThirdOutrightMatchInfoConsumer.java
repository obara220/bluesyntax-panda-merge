package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdOutrightMatchInfoDTO;
import com.panda.merge.rocketmq.processor.ThirdOutrightMatchInfoProcessor;
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
 * 接收MQ冠军赛事信息 (未写完，暂时不用 注：topic加了各后缀，要用时取消后缀)<br>
 * @author tell<br>
 * @since 2020年9月2日19:32:11 <br>
 */
@Slf4j
@Component
@Order
@RocketMQMessageListener(topic = THIRD_OUTRIGHT_MATCH_INFO_API,
        consumerGroup = CONSUME_NONREALTIME_GROUP + THIRD_OUTRIGHT_MATCH_INFO_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("nonrealtimeAdminApplication")
public class ThirdOutrightMatchInfoConsumer implements RocketMQListener<Request<List<ThirdOutrightMatchInfoDTO>>> {

    @Autowired
    ThirdOutrightMatchInfoProcessor thirdOutrightMatchInfoProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<List<ThirdOutrightMatchInfoDTO>> dataCenterProducer;

    @Override
    public void onMessage(Request<List<ThirdOutrightMatchInfoDTO>> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,THIRD_OUTRIGHT_MATCH_INFO_API);
            return;
        }
        thirdOutrightMatchInfoProcessor.processMatchData(request);
    }
}
