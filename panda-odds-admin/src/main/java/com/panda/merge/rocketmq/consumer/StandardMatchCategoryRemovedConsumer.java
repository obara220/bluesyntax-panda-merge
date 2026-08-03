package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.odds.model.StandardMatchCategoryRemovedDto;
import com.panda.merge.rocketmq.processor.StandardMatchCategoryRemovedProcessor;
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
import static com.panda.merge.constant.ConstantSystem.STANDARD_MATCH_CATEGORY_REMOVED;


/**
 * 4184【操盘】联赛模板支持批量编辑/同步赛事/中途下架
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = STANDARD_MATCH_CATEGORY_REMOVED,
        consumerGroup = "odds-group-" + STANDARD_MATCH_CATEGORY_REMOVED,
        consumeThreadMax = 50)
@DependsOn("oddsAdminApplication")
public class StandardMatchCategoryRemovedConsumer implements RocketMQListener<Request<StandardMatchCategoryRemovedDto>> {

    @Autowired
    private StandardMatchCategoryRemovedProcessor standardMatchCategoryRemovedProcessor;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<StandardMatchCategoryRemovedDto> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", STANDARD_MATCH_CATEGORY_REMOVED, request.getData());
            String toTopic = STANDARD_MATCH_CATEGORY_REMOVED + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<StandardMatchCategoryRemovedDto>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        standardMatchCategoryRemovedProcessor.processor(request);
    }
}
