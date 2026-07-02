package com.panda.merge.rocketmq.consumer;

import static com.panda.merge.constant.ConstantSystem.STANDARD_MATCH_ODDS_ISSUED;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.MatchOddsIssuedProcessor;

/**
 * 处理开赛后的赔率及时下发
 * @author   damian
 * @since    2022年06月04日11:58:21
 */
@Component
@RocketMQMessageListener(
        topic = STANDARD_MATCH_ODDS_ISSUED,
        consumerGroup = "odds-group-"+STANDARD_MATCH_ODDS_ISSUED,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class MatchOddsIssuedConsumer implements RocketMQListener<Request<Long>>  {

    @Autowired
    private MatchOddsIssuedProcessor matchOddsIssuedProcessor;

    
    
    @Override
    public void onMessage(Request<Long> request) {
    	matchOddsIssuedProcessor.processor(request);
    }
}
