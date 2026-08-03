package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardOutrightMatchDTO;
import com.panda.merge.rocketmq.processor.StandardOutRightMatchProcessor;
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

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 消费标准冠军赛事消息
 *
 * @author Aison
 * @since 2020年11月18日16:56:10
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = STANDARD_OUTRIGHT_MATCH,
        consumerGroup = "odds-group-" + STANDARD_OUTRIGHT_MATCH,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class StandardOutRightMatchConsumer implements RocketMQListener<Request<StandardOutrightMatchDTO>> {

    @Autowired
    private StandardOutRightMatchProcessor standardOutRightMatchProcessor;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<StandardOutrightMatchDTO> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", STANDARD_OUTRIGHT_MATCH, request.getData());
            String toTopic = STANDARD_OUTRIGHT_MATCH + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<StandardOutrightMatchDTO>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        standardOutRightMatchProcessor.processStandardOutRightMatch(request);
    }
}
