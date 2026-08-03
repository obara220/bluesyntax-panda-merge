package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.rocketmq.processor.LSSendRscMatchStatusProcessor;
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
import static com.panda.merge.constant.ConstantSystem.LS_SEND_RSC_MATCH_STATUS;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = LS_SEND_RSC_MATCH_STATUS,
        consumerGroup = "odds-group-"+LS_SEND_RSC_MATCH_STATUS,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class LSSendRscMatchStatusConsumer  implements RocketMQListener<Request<TradeMarketConfigDTO>> {
    @Autowired
    LSSendRscMatchStatusProcessor lsSendRscMatchStatusProcessor;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Override
    public void onMessage(Request<TradeMarketConfigDTO> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", LS_SEND_RSC_MATCH_STATUS, request.getData());
            String toTopic = LS_SEND_RSC_MATCH_STATUS + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<TradeMarketConfigDTO>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        lsSendRscMatchStatusProcessor.processor(request);
    }
}
