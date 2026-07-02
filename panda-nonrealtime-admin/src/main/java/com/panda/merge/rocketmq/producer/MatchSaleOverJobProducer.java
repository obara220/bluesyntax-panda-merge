package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchSaleOverMessage;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 兜底完赛通知预售开售 赛事完赛消息
 * @author   tell
 * @since    2021年1月17日16:06:05
 */
@Slf4j
@Component
public class MatchSaleOverJobProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 通知预售开售 赛事完赛消息
     * @param linkId               全局请求唯一追踪id号
     * @param standardMatchInfo    标准赛事信息
     */
    public void sendMatchSaleOverMessage(String linkId, StandardMatchInfo standardMatchInfo) {
        MatchSaleOverMessage message = new MatchSaleOverMessage(linkId, standardMatchInfo.getId(), standardMatchInfo.getMatchManageId());
        MessageBuilder<MatchSaleOverMessage> builder = MessageBuilder.withPayload(message)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //通知预售开售赛事完赛
        rocketMqTemplate.send("Match_Sale_Over:"+standardMatchInfo.getId(), builder.build());
        log.info("::{}::MatchSaleOverJobProducer 通知预售开售,赛事完赛消息结束,topic=MATCH_SALE_OVER,matchStatus={}, request={}", linkId, standardMatchInfo.getMatchStatus(), JSON.toJSONString(message));
    }


    /**
     * 通知其它服务，需要清理的完赛超过7天赛事ID信息
     */
    public void sendCleanEndedDayMatch(Request request) {
        MessageBuilder<Request> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
        rocketMqTemplate.syncSend(request.getDataType()+":"+request.getTag(), builder.build(), SECOND_1 * THREE,ONE);
        log.info("::{}::sendCleanEndedDayMatch 通知其它服务,需要清理的完赛超过7天赛事信息,request={}",request.getLinkId(),JSON.toJSONString(request));
    }

}
