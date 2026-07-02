package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.dto.ThirdSportSeasonDetail;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdSportSeason;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * @Author Kepa
 * @Date 2021/2/17 18:22
 * @Version 1.0
 */
@Slf4j
@Component
public class ThirdSportSeasonProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 推送三方赛季给下游
     * @param linkId
     * @param item
     */
    public void pushQueueSeason(String linkId, DataSource dataSource, ThirdSportSeason item) {
        MessageBuilder<String> builder = MessageBuilder.withPayload(CommUtils.getJsonById(item.getId())).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(QUEUE_SEASON +":"+ item.getThirdSourceSeasonId(),builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SEASON_INFO_API+"】【{} : {}】推送三方赛季信息完成【topic : "+QUEUE_SEASON+"】 【id={} : sourceId={}】", dataSource.getCode(),linkId,item.getId(),item.getThirdSourceSeasonId());

    }

    /**
     * 推送三方赛季给比分网后台
     * @param linkId
     * @param item
     */
    public void pushThirdSeasonPLS(String linkId, DataSource dataSource, ThirdSportSeasonDetail item) {
        MessageBuilder<String> builder = MessageBuilder.withPayload(JSON.toJSONString(item)).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(THIRD_SEASON_INFO_PLS +":"+ item.getThirdSourceSeasonId(),builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SEASON_INFO_API+"】【{} : {}】推送三方赛季信息给比分网后台完成【topic : "+ THIRD_SEASON_INFO_PLS +"】 【id={} : sourceId={}】", dataSource.getCode(),linkId,item.getId(),item.getThirdSourceSeasonId());

    }
}
