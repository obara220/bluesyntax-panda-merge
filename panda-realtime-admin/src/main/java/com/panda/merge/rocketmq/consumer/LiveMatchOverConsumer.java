package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchOverMessage;
import com.panda.merge.rocketmq.processor.LiveMatchOverProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.FROM_RCS_MATCH_IS_END;

/**
 * 接收操盘非常规结束通知（异常完赛回调，1220需求）
 * @author      idol
 * @since       2020年10月23日09:57:07
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = FROM_RCS_MATCH_IS_END,
        consumerGroup = CONSUME_REALTIME_GROUP + FROM_RCS_MATCH_IS_END,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class LiveMatchOverConsumer implements RocketMQListener<Request<MatchOverMessage>> {

    @Autowired
    private LiveMatchOverProcessor processor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
    private boolean realtimeEventSwitch;
    @Resource
    private DataCenterProducer<MatchOverMessage> dataCenterProducer;

//    @ConsumerSwitch("realtime")
    @Override
    public void onMessage(Request<MatchOverMessage> request) {
        if (!realtimeSwitch && !realtimeEventSwitch) {
            if (dataCenterProducer.checkForward(request.getData().getMatchId(),request.getLinkId())) {
                dataCenterProducer.send(request,FROM_RCS_MATCH_IS_END);
                return;
            }
        }
        processor.liveMatchOverProcessor(request);
    }
}
