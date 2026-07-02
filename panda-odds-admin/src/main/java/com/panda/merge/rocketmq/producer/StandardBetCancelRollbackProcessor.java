package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetCancelRollbackDTO;
import com.panda.merge.dto.message.StandardBetCancelRollbackItemMessage;
import com.panda.merge.dto.message.StandardBetCancelRollbackMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.producer
 * @description : 回滚盘口取消操作时调用
 * @date: 2020-09-09 19:24
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
public class StandardBetCancelRollbackProcessor {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendStandardBetCancelRollback(String linkId, Long standardMatchId, Long sportId, ThirdBetCancelRollbackDTO rollbackDTO, List<StandardBetCancelRollbackItemMessage> standardBetCancelItemMessages) {
        StandardBetCancelRollbackMessage betCancelRollbackMessage = new StandardBetCancelRollbackMessage();
        BeanUtils.copyProperties(rollbackDTO, betCancelRollbackMessage);
        betCancelRollbackMessage.setMatchId(standardMatchId);
        betCancelRollbackMessage.setMarkets(standardBetCancelItemMessages);
        betCancelRollbackMessage.setSportId(sportId);

        Request<StandardBetCancelRollbackMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(betCancelRollbackMessage);
        MessageBuilder<Request<StandardBetCancelRollbackMessage>> builder = MessageBuilder.withPayload(messageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.send("STANDARD_BET_CANCEL_ROLLBACK:" + standardMatchId, builder.build());
        log.info("::{}::开始组装回滚盘口取消并下发,topic:STANDARD_BET_CANCEL_ROLLBACK,request:{}", linkId, JSON.toJSONString(messageRequest));
    }

}
