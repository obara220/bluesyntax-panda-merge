package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.A99MatchOddsDiffenceDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.A99MatchOddsDifferenceProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = A99_MATCH_ODDS_CHANGE_DIFFERENCE,
        consumerGroup = CONSUMER_PANDA_A99_GROUP + A99_MATCH_ODDS_CHANGE_DIFFERENCE,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
public class A99MatchOddsDifferenceConsumer implements RocketMQListener<Request<A99MatchOddsDiffenceDTO>> {

    @Autowired
    private A99MatchOddsDifferenceProcessor matchOddsDifferenceProcessor;

    @Override
    public void onMessage(Request<A99MatchOddsDiffenceDTO> a99MatchOddsDiffenceDTORequest) {
        matchOddsDifferenceProcessor.execute(a99MatchOddsDiffenceDTORequest);
    }
}
