package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchPreResultDTO;
import com.panda.merge.rocketmq.processor.ThirdMarketPreResultProcessor;
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
import static com.panda.merge.constant.ConstantSystem.THIRD_MARKET_PRE_RESULT_API;

/**
 * 消费数据源盘口提前结算信息
 *
 * @author Aison
 * @since 2020年11月18日16:55:03
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_MARKET_PRE_RESULT_API,
        consumerGroup = "odds-group-" + THIRD_MARKET_PRE_RESULT_API,
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class ThirdMarketPreResultConsumer implements RocketMQListener<Request<ThirdMatchPreResultDTO>> {

    @Lazy
    @Autowired
    private ThirdMarketPreResultProcessor thirdMarketPreResultProcessor;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<ThirdMatchPreResultDTO> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", THIRD_MARKET_PRE_RESULT_API, request.getData());
            String toTopic = THIRD_MARKET_PRE_RESULT_API + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<ThirdMatchPreResultDTO>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        thirdMarketPreResultProcessor.thirdMarketPreResultApi(request);
    }
}
