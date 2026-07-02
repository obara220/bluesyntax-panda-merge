package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdSportTournamentDetail;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdSportTournament;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 推送三方联赛给下游 <br>
 * @author   tell<br>
 * @since    2020年9月5日19:07:32<br>
 */
@Slf4j
@Component
public class ThirdSportTournamentProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 推送三方联赛给下游
     * @param linkId
     * @param item
     */
    public void pushQueueTournament(String linkId, DataSource dataSource, ThirdSportTournament item) {
            MessageBuilder<String> builder = MessageBuilder.withPayload(CommUtils.getJsonById(item.getId())).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.syncSend(QUEUE_TOURNAMENT  +":"+ item.getThirdTournamentSourceId(),builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_TOURNAMENT_API+"】【{} : {}】推送三方联赛给下游完成【topic : "+QUEUE_TOURNAMENT+"】 【id={} : sourceId={}】", dataSource.getCode(),linkId,item.getId(),item.getThirdTournamentSourceId());
    }

    /**
     * 推送三方联赛给比分网后台
     * @param linkId
     * @param item
     */
    public void pushThirdTournamentPLS(String linkId, DataSource dataSource, ThirdSportTournamentDetail item) {
        Request<ThirdSportTournamentDetail> request = new Request<>();
        request.setData(item);
        request.setLinkId(linkId);
        MessageBuilder<String> builder = MessageBuilder.withPayload(JSON.toJSONString(request)).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(THIRD_TOURNAMENT_INFO_PLS +":"+ item.getThirdTournamentSourceId(),builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_TOURNAMENT_API+"】【{} : {}】推送三方联赛给比分网后台完成【topic : "+ THIRD_TOURNAMENT_INFO_PLS +"】 【id={} : sourceId={}】", dataSource.getCode(),linkId,item.getId(),item.getThirdTournamentSourceId());
    }

}
