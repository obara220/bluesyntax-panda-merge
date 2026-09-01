/*
package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.rocketmq.processor.A99OddsProcessor;
import com.panda.merge.service.ThirdMatchInfoService;
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

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.A99_STANDARD_ODDS_API;
import static com.panda.merge.constant.ConstantSystem.DATACENTER;


*/
/**
 * a01 延长开售
 *//*

@Slf4j
@Component
@RocketMQMessageListener(topic = A99_STANDARD_ODDS_API,
        consumerGroup = "odds-group-" + A99_STANDARD_ODDS_API,
        consumeThreadMax = 50)
@DependsOn("oddsAdminApplication")
public class A99OddsConsumer implements RocketMQListener<Request<List<StandardMarketDataMessage>>> {

    @Autowired
    private A99OddsProcessor a99OddsProcessor;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    */
/**
     * 数据中心赔率状态开关 1开 0关
     *//*

    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<List<StandardMarketDataMessage>> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", A99_STANDARD_ODDS_API, request.getData());
            String toTopic = A99_STANDARD_ODDS_API + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<List<StandardMarketDataMessage>>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        a99OddsProcessor.execute(request);
    }
}
*/
