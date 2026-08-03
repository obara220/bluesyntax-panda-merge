package com.panda.merge.rocketmq.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.bo.StandardSportTournamentBO;
import com.panda.merge.dto.Request;
import com.panda.merge.service.StandardSportTournamentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;
import static com.panda.merge.constant.ConstantSystem.DATACENTER;

/**
 * TournamentModificationConsumer
 *
 * @description: 联赛更新
 * @date: 1/24/2025
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = TOPIC_TOURNAMENT_MODIFICATION,
        consumerGroup = PAND_ODDS_GROUP + TOPIC_TOURNAMENT_MODIFICATION)
public class TournamentModificationConsumer implements RocketMQListener<Request<List<StandardSportTournamentBO>>> {


    @Autowired
    private StandardSportTournamentService tournamentService;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<List<StandardSportTournamentBO>> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", TOPIC_TOURNAMENT_MODIFICATION, request.getData());
            String toTopic = TOPIC_TOURNAMENT_MODIFICATION + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<List<StandardSportTournamentBO>>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            // 融合服务消息转发数据中心服务成功
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        log.info("{} tournament modification request {}", request.getLinkId(), request);
        List<StandardSportTournamentBO> data = request.getData();
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        tournamentService.evitCache(data
                .stream()
                .map(StandardSportTournamentBO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }
}
