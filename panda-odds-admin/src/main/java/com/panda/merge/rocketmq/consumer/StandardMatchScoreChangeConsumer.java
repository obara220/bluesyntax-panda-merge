package com.panda.merge.rocketmq.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.scores.StandardMatchScoreChangeDTO;
import com.panda.merge.rocketmq.processor.StandardMatchScoreChangeProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.STANDARD_MATCH_SCORE_CHANGE_API;


@Slf4j
@Component
@RocketMQMessageListener(
        topic = STANDARD_MATCH_SCORE_CHANGE_API,
        consumerGroup = "odds-group-STANDARD_MATCH_SCORE_CHANGE_API",
        consumeThreadMax = 20
)
@DependsOn("oddsAdminApplication")
public class StandardMatchScoreChangeConsumer implements RocketMQListener<Request<StandardMatchScoreChangeDTO>> {

    @Autowired
    private StandardMatchScoreChangeProcessor standardMatchScoreChangeProcessor;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<StandardMatchScoreChangeDTO> standardMatchScoreChangeDTORequest) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", STANDARD_MATCH_SCORE_CHANGE_API, standardMatchScoreChangeDTORequest.getData());
            String toTopic = STANDARD_MATCH_SCORE_CHANGE_API + DATACENTER;
            String destination = !StringUtils.isEmpty(standardMatchScoreChangeDTORequest.getTag()) ? toTopic + ":" + standardMatchScoreChangeDTORequest.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<StandardMatchScoreChangeDTO>> builder = MessageBuilder.withPayload(standardMatchScoreChangeDTORequest)
                    .setHeader(MessageConst.PROPERTY_KEYS, standardMatchScoreChangeDTORequest.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", standardMatchScoreChangeDTORequest.getLinkId(), toTopic, JSON.toJSONString(standardMatchScoreChangeDTORequest));
            return;
        }
        standardMatchScoreChangeProcessor.execute(standardMatchScoreChangeDTORequest);
    }
}
