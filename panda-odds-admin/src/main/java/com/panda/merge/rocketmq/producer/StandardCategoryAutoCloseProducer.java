package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetCancelDTO;
import com.panda.merge.dto.message.StandardBetCancelItemMessage;
import com.panda.merge.dto.message.StandardBetCancelMessage;
import com.panda.merge.dto.message.StandardCategoryAutoCloseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 增加自动关盘的玩法推送
 *
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.producer
 * @description : TODO
 * @date: 2020-09-09 14:58
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
public class StandardCategoryAutoCloseProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendStandardCategoryAutoClose(String linkId, StandardCategoryAutoCloseMessage standardCategoryAutoCloseMessage) {

        Request<StandardCategoryAutoCloseMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(standardCategoryAutoCloseMessage);
        MessageBuilder<Request<StandardCategoryAutoCloseMessage>> builder = MessageBuilder.withPayload(messageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.send("STANDARD_CATEGORY_AUTOCLOSE:" + standardCategoryAutoCloseMessage.getStandardMatchId(), builder.build());
        log.info("::{}::自动关闭玩法集合下发,topic:STANDARD_CATEGORY_AUTOCLOSE,标准赛事ID：{},request:{}", linkId, standardCategoryAutoCloseMessage.getStandardMatchId(), JSON.toJSONString(messageRequest));
    }


    public void sendStandardChildCategoryAutoClose(String linkId, StandardCategoryAutoCloseMessage standardCategoryAutoCloseMessage) {
        Request<StandardCategoryAutoCloseMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(standardCategoryAutoCloseMessage);
        MessageBuilder<Request<StandardCategoryAutoCloseMessage>> builder = MessageBuilder.withPayload(messageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.send("STANDARD_CHILD_CATEGORY_AUTOCLOSE:" + standardCategoryAutoCloseMessage.getStandardMatchId(), builder.build());
        log.info("::{}::自动关闭子玩法集合下发,topic:STANDARD_CHILD_CATEGORY_AUTOCLOSE,标准赛事ID：{},request:{}", linkId, standardCategoryAutoCloseMessage.getStandardMatchId(), JSON.toJSONString(messageRequest));
    }
}
