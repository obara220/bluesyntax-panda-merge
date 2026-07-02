package com.panda.merge.rocketmq.producer;

import com.panda.merge.bo.ModifyMatchInfoBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

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
        log.info("linkId=【{}】ModifyMatchInfoProducer,topic=PUSH_MODIFY_MATCH_INFO入参; standardMatchId:{}, operateReason:{}, operateId:{} ", linkId, standardMatchId, operateReason, operateId);
        ModifyMatchInfoBO sendData = new ModifyMatchInfoBO();
        sendData.setOperateReason(operateReason);
        sendData.setLinkId(linkId);
        sendData.setStandardMatchId(standardMatchId);
        sendData.setOperaterId(operateId);
        MessageBuilder<ModifyMatchInfoBO> builder = MessageBuilder.withPayload(sendData).setHeader(MessageConst.PROPERTY_KEYS, linkId);
//        log.info("linkId=【{}】通知赛程服务,视频数据发生改变,topic=PUSH_MODIFY_MATCH_INFO:{}", linkId, standardMatchId);
        rocketMqTemplate.send(PUSH_MODIFY_MATCH_INFO +":"+ standardMatchId, builder.build());
        log.info("【::"+ linkId + "::】【{}】通知赛程服务,视频数据发生改变完成【topic={} : standardMatchId={}】", operateReason, PUSH_MODIFY_MATCH_INFO , standardMatchId );
    }
}
