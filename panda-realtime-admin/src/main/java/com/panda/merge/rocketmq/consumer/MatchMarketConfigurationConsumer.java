package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchMarketConfigurationMessage;
import com.panda.merge.rocketmq.processor.MatchMarketConfigruationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;

/**
 * @Description :  数据源权重及事件审核的配置（含事件审核，结束审核自动结算倒计时信息）
 * @author :  Riben
 * @since :  2020年12月9日13:43:05
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "Tournament_Template_Match",
        consumerGroup = CONSUME_REALTIME_GROUP + "TOURNAMENT_TEMPLATE_MATCH",
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class MatchMarketConfigurationConsumer implements RocketMQListener<Request<MatchMarketConfigurationMessage>> {

    @Autowired
    private MatchMarketConfigruationProcessor processor;


    @Override
    public void onMessage(Request<MatchMarketConfigurationMessage> matchMarketConfigurationMessageRequest) {
        processor.handleMatchMarketConfigurationData(matchMarketConfigurationMessageRequest);
    }
}
