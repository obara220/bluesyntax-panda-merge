package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
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
 * 通知预售开售 赛事完赛消息
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.rocketmq.producer <br>
 */
@Slf4j
@Component
public class MatchSaleOverProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;


    /**
     * 通知预售开售 赛事完赛消息
     * @param linkId               全局请求唯一追踪id号
     * @param standardMatchInfo    标准赛事信息
     */
    public void sendMatchSaleOverMessage(String linkId, StandardMatchInfo standardMatchInfo) {
        sendMatchSaleOverMessage(linkId,standardMatchInfo,null);
    }

    /**
     * 通知预售开售 赛事完赛消息
     * @param linkId               全局请求唯一追踪id号
     * @param standardMatchInfo    标准赛事信息
     */
    public void sendMatchSaleOverMessage(String linkId, StandardMatchInfo standardMatchInfo,String updataUser) {
        MatchSaleOverMessage message = new MatchSaleOverMessage(linkId, standardMatchInfo.getId(), standardMatchInfo.getMatchManageId(),updataUser);
        MessageBuilder<MatchSaleOverMessage> builder = MessageBuilder.withPayload(message).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //通知预售开售赛事完赛，设置延迟级别（1:1s,2:5s,3:10s）
        rocketMqTemplate.syncSend("Match_Sale_Over:"+standardMatchInfo.getId(),builder.build(), SECOND_1 * THREE,TWO);
        log.info("linkId=【{}】通知预售开售,赛事完赛消息结束,topic=Match_Sale_Over,matchStatus={}, request={}", linkId, standardMatchInfo.getMatchStatus(), JSON.toJSONString(message));
    }



}
