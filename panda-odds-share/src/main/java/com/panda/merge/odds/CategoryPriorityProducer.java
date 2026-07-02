package com.panda.merge.odds;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.odds.CategoryDataSourceHighPriority;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.odds.MQConstant.TOPIC_DATA_SOURCE_SWITCH_HIGH_PRIORITY;

/**
 * CategoryPriorityProducer
 *
 * @description:
 * @date: 6/10/2025
 **/
@Component
@Slf4j
public class CategoryPriorityProducer {

    @Autowired
    private RocketMQTemplate template;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    public void send(CategoryDataSourceHighPriority priority) {
        String linkId = priority.getLinkId();
        priority.setSportId(standardMatchInfoService.getItem(priority.getMatchId()).getSportId());
        Request<CategoryDataSourceHighPriority> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(priority);
        MessageBuilder<Request<CategoryDataSourceHighPriority>> builder =
                MessageBuilder.withPayload(messageRequest).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        template.send(TOPIC_DATA_SOURCE_SWITCH_HIGH_PRIORITY + ":" + linkId, builder.build());
        log.info("::{}::topic:DATA_SOURCE_AUTO_SWITCH_HIGH_PRIORITY send successfully ,request:{}",
                 linkId,
                 JSON.toJSONString(messageRequest));
    }

}
