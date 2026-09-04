package com.panda.merge.rocketmq.producer;

import cn.hutool.json.JSONUtil;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdSportPlayerDetail;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdSportPlayer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 推送三方球队人员给下游 <br>
 * @author   tell<br>
 * @since    2020年9月5日19:07:32<br>
 */
@Slf4j
@Component
public class ThirdSportPlayerProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 推送三方球队人员给下游
     * @param linkId
     * @param item
     */
    public void pushQueuePlayer(String linkId, DataSource dataSource, ThirdSportPlayer item) {
        MessageBuilder<String> builder = MessageBuilder.withPayload(CommUtils.getJsonById(item.getId())).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(QUEUE_PLAYER +":"+ item.getThirdSourcePlayerId(),builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SPORT_TEAM_API+"】【{} : {}】推送三方球队人员给下游完成【topic : "+QUEUE_PLAYER+"】 【id={} : sourceId={}】",dataSource.getCode(),linkId,item.getId(),item.getThirdSourcePlayerId());
    }


    /**
     * 推送三方球员给比分网后台
     * @param linkId
     * @param item
     */
    public void pushThirdPlayerPLS(String linkId, DataSource dataSource, ThirdSportPlayerDetail item) {
        Request<ThirdSportPlayerDetail> request = new Request<>();
        request.setData(item);
        request.setLinkId(linkId);
        MessageBuilder<String> builder = MessageBuilder.withPayload(JSONUtil.toJsonStr(request)).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(THIRD_SPORT_PLAYER_PLS +":"+ item.getThirdSourcePlayerId(), builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SPORT_PLAYER_PLS+"】【{} : {}】推送三方球员给比分网后台完成【topic : "+ THIRD_SPORT_PLAYER_PLS +"】 ,id:{},thirdSourcePlayerId:{}", dataSource.getCode(),linkId,item.getId(),item.getThirdSourcePlayerId());
    }
}
