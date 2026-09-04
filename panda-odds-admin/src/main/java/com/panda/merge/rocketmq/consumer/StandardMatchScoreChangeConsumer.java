package com.panda.merge.rocketmq.consumer;


import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchInternalCode;
import com.panda.merge.dto.scores.StandardMatchScoreChangeDTO;
import com.panda.merge.rocketmq.processor.CategoryCodeProcessor;
import com.panda.merge.rocketmq.processor.StandardMatchScoreChangeProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RocketMQMessageListener(
        topic = ConstantSystem.STANDARD_MATCH_SCORE_CHANGE_API,
        consumerGroup = "odds-group-STANDARD_MATCH_SCORE_CHANGE_API",
        consumeThreadMax = 20
)
@DependsOn("oddsAdminApplication")
public class StandardMatchScoreChangeConsumer implements RocketMQListener<Request<StandardMatchScoreChangeDTO>> {

    @Autowired
    private StandardMatchScoreChangeProcessor standardMatchScoreChangeProcessor;


    @Override
    public void onMessage(Request<StandardMatchScoreChangeDTO> standardMatchScoreChangeDTORequest) {
        standardMatchScoreChangeProcessor.execute(standardMatchScoreChangeDTORequest);
    }
}
