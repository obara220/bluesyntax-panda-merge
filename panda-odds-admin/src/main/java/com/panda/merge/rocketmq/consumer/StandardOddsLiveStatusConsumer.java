package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardOddsLiveStatusMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
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

import java.util.ArrayList;

import static com.panda.merge.constant.ConstantSystem.DATACENTER;

/**
 * 下发滚球标识
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "STANDARD_ODDS_LIVE_STATUS", consumerGroup = "odds-group-STANDARD_ODDS_LIVE_STATUS")
@DependsOn("oddsAdminApplication")
public class StandardOddsLiveStatusConsumer implements RocketMQListener<Request<StandardOddsLiveStatusMessage>> {
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<StandardOddsLiveStatusMessage> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            String fromTopic = "STANDARD_ODDS_LIVE_STATUS";
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", fromTopic, request.getData());
            String toTopic = fromTopic + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<StandardOddsLiveStatusMessage>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        String linkId = request.getLinkId();
        StandardOddsLiveStatusMessage standardOddsLiveStatusMessage = request.getData();
        Long standardMatchId = standardOddsLiveStatusMessage.getStandardMatchId();
        Long sportId = standardOddsLiveStatusMessage.getSportId();
        String dataSourceCode = standardOddsLiveStatusMessage.getDataSourceCode();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        thirdMatchMarketProcessor.newClosePreMarkets(linkId, standardSportMarketSell, 0, standardMatchInfo,
                request.getDataSourceTime(), Boolean.TRUE, new ArrayList<>(), standardOddsLiveStatusMessage.getAdvance());
    }
}
