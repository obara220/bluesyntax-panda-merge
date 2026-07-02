package com.panda.merge.mq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SoldMessage;
import com.panda.merge.mq.processor.SoldMessageStandardScoreProcessor;
import com.panda.merge.mq.producer.CommonProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.SOLD_MESSAGE;
import static com.panda.merge.constant.ConstantSystem.SOLD_MESSAGE_STANDARD_SCORES;

/**
 * @author warren
 * @since 2025/01/03 16:23:13
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = SOLD_MESSAGE,
        consumerGroup = "scores-group-" + SOLD_MESSAGE_STANDARD_SCORES,
        consumeThreadMax = 2,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class SoldMessageStandardScoreConsumer implements RocketMQListener<Request<SoldMessage>> {
    @Autowired
    private SoldMessageStandardScoreProcessor soldMessageStandardScoreProcessor;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Override
    public void onMessage(Request<SoldMessage> soldMessageRequest) {
        log.info("SoldMessageStandardScoreConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(soldMessageRequest.getData().getMatchId().toString())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(soldMessageRequest, "datacenter-SOLD_MESSAGE",soldMessageRequest.getLinkId());
            return;
        }
        soldMessageStandardScoreProcessor.execute(soldMessageRequest);
    }
}
