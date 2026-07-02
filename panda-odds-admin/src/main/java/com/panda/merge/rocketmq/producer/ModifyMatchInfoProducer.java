package com.panda.merge.rocketmq.producer;

import com.panda.merge.bo.ModifyMatchInfoBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.PUSH_MODIFY_MATCH_INFO;

@Slf4j
@Component
public class ModifyMatchInfoProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 组装标准赛事变更通知数据
     * @param linkId
     * @param standardMatchId
     * @param operateReason
     * @param operateId
     */
    public void pushModifyMatchInfoMessage(String linkId, Long standardMatchId, String  operateReason, Long operateId) {
        log.info("::{}::ModifyMatchInfoProducer,topic:PUSH_MODIFY_MATCH_INFO入参; standardMatchId:{}, operateReason:{}, operateId:{} ", linkId, standardMatchId, operateReason, operateId);
        ModifyMatchInfoBO sendData = new ModifyMatchInfoBO();
        sendData.setOperateReason(operateReason);
        sendData.setLinkId(linkId);
        sendData.setStandardMatchId(standardMatchId);
        sendData.setOperaterId(operateId);
        MessageBuilder<ModifyMatchInfoBO> builder = MessageBuilder.withPayload(sendData).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        log.info("::{}::开始组装标准赛事变更通知数据下发,topic:PUSH_MODIFY_MATCH_INFO:{}", linkId, standardMatchId);
        rocketMqTemplate.send(PUSH_MODIFY_MATCH_INFO +":"+ standardMatchId, builder.build());
        log.info("【"+ PUSH_MODIFY_MATCH_INFO + "】【{}】通知标准赛事变更给下游完成【linkId={} : standardMatchId={}】", operateReason, linkId, standardMatchId );

    }
}
