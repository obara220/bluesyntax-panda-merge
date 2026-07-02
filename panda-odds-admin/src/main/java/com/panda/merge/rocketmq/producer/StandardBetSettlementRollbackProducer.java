package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdBetSettlementRollbackDTO;
import com.panda.merge.dto.message.StandardBetSettlementRollbackItemMessage;
import com.panda.merge.dto.message.StandardBetSettlementRollbackMessage;
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
 * @description : 回滚盘口结算操作推送下游
 * @date: 2020-09-09 20:35
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */

@Slf4j
@Component
public class StandardBetSettlementRollbackProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void sendstandardBetSettlementRollback(String linkId, Long standardMatchId, Long sportId, ThirdBetSettlementRollbackDTO settlementRollbackDTO, List<StandardBetSettlementRollbackItemMessage> standardBetCancelItemMessages) {
        StandardBetSettlementRollbackMessage settlementRollbackMessage = new StandardBetSettlementRollbackMessage();
        BeanUtils.copyProperties(settlementRollbackDTO, settlementRollbackMessage);
        settlementRollbackMessage.setMatchId(standardMatchId);
        settlementRollbackMessage.setMarkets(standardBetCancelItemMessages);
        settlementRollbackMessage.setSportId(sportId);
        Request<StandardBetSettlementRollbackMessage> messageRequest = new Request<>();
        messageRequest.setLinkId(linkId);
        messageRequest.setData(settlementRollbackMessage);
        MessageBuilder<Request<StandardBetSettlementRollbackMessage>> builder = MessageBuilder.withPayload(messageRequest)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.send("STANDARD_BET_SETTLEMENT_ROLLBACK:" + standardMatchId, builder.build());
        log.info("::{}::开始组装回滚盘口结算并下发,topic:STANDARD_BET_SETTLEMENT_ROLLBACK,标准盘口ID：{},request:{}", linkId, standardMatchId, JSON.toJSONString(messageRequest));
    }


}
