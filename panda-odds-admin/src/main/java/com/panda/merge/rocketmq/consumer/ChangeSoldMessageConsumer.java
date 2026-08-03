package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.ChangeSoldMessage;
import com.panda.merge.rocketmq.processor.ChangeSoldMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;
import static com.panda.merge.constant.ConstantSystem.STANDARD_CATEGORYID_CLEAR_DIFF;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "CHANGE_SOLD_MESSAGE",
        consumerGroup = "odds-group-CHANGE_SOLD_MESSAGE",
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ChangeSoldMessageConsumer implements RocketMQListener<Request<ChangeSoldMessage>> {
    @Lazy
    @Autowired
    ChangeSoldMessageProcessor changeSoldMessageProcessor;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Override
    public void onMessage(Request<ChangeSoldMessage> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            String fromTopic = "CHANGE_SOLD_MESSAGE";
            log.info("收到 ::{}:: Topic的消息：{}", fromTopic, request.getData());
            String toTopic = fromTopic + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<ChangeSoldMessage>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        changeSoldMessageProcessor.changeSoldMessage(request);
    }
}
