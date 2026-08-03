package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.bo.ModifyMatchInfoBO;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StandardMatchInfoProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 标准赛事修改后通知赛程
     * @param linkId
     * @param standardMatchInfo
     */
    public void pushStandardMatchModify(String linkId, StandardMatchInfo standardMatchInfo) {
        ModifyMatchInfoBO modifyMatchInfoBO = new ModifyMatchInfoBO();
        modifyMatchInfoBO.setStandardMatchId(standardMatchInfo.getId());
        modifyMatchInfoBO.setLinkId(linkId);
        try {
            Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(modifyMatchInfoBO))
                    .setHeader(MessageConst.PROPERTY_KEYS, linkId)
                    .build();
            rocketMqTemplate.send(ConstantSystem.PUSH_MODIFY_MATCH_INFO  +":"+ standardMatchInfo.getId(), message);
        } catch (Exception e) {
            log.error("标准赛事修改通知下发异常, linkId={}, topic={} 赛事id={}", linkId, ConstantSystem.PUSH_MODIFY_MATCH_INFO  , standardMatchInfo.getId(), e);
        }
        log.info("标准赛事修改通知下发, linkId={}, topic={} 赛事id={}", linkId, ConstantSystem.PUSH_MODIFY_MATCH_INFO  , standardMatchInfo.getId());
    }
}
