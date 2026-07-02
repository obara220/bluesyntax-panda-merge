package com.panda.merge.rocketmq.consumer;


import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_PRE_ODDS_ADMIN",
        consumerGroup = "odds-group-MATCH_PRE_ODDS_ADMIN",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class MatchBeginLiveOddsConsumer  implements RocketMQListener<Request<Long>> {
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Override
    public void onMessage(Request<Long> longRequest) {
        thirdMatchMarketProcessor.accessMatchLiveOddsData(longRequest);
    }
}
