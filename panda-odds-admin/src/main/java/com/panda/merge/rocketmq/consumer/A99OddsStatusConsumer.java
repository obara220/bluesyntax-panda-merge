/*
package com.panda.merge.rocketmq.consumer;

import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.A99MatchSwitchDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.odds.A99OddsStatusDTO;
import com.panda.merge.rocketmq.processor.A99OddsStatusProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;



@Slf4j
@Component
@RocketMQMessageListener(topic = ConstantSystem.A99_MATCH_SWITCH,
        consumerGroup = "odds-group-" + ConstantSystem.A99_MATCH_SWITCH,
        consumeThreadMax = 50)
@DependsOn("oddsAdminApplication")
public class A99OddsStatusConsumer implements RocketMQListener<Request<A99MatchSwitchDTO>> {

    @Autowired
    private A99OddsStatusProcessor a99OddsStatusProcessor;


    @Override
    public void onMessage(Request<A99MatchSwitchDTO> request) {
        a99OddsStatusProcessor.execute(request);
    }


}
*/
