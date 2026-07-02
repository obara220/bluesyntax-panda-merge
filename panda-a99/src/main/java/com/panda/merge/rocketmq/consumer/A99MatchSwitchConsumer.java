package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.A99MatchSwitchDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.A99MatchSwitchProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 赛事开启/关闭A99赔率
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = A99_MATCH_SWITCH,
        consumerGroup = CONSUMER_PANDA_A99_GROUP + A99_MATCH_SWITCH,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
public class A99MatchSwitchConsumer implements RocketMQListener<Request<A99MatchSwitchDTO>> {

    @Autowired
    private A99MatchSwitchProcessor a99MatchSwitchProcessor;

    @Override
    public void onMessage(Request<A99MatchSwitchDTO> request) {
        a99MatchSwitchProcessor.execute(request);
    }
}
