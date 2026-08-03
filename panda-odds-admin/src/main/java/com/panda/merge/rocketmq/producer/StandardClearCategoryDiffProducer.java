package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.message.RcsClearDiffMessage;
import com.panda.merge.model.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通知风控玩法水差清除
 *
 * @author Administrator
 */
@Slf4j
@Component
public class StandardClearCategoryDiffProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;


    public void sendStandardClearCategoryDiffRisk(String linkId, StandardMatchInfo standardMatchInfo, List<Long> clearDiffList) {
        RcsClearDiffMessage diffMessage = new RcsClearDiffMessage();
        diffMessage.setMatchId(standardMatchInfo.getId());
        diffMessage.setClearType(20);
        diffMessage.setType(0);
        diffMessage.setPlayIds(clearDiffList);
        diffMessage.setSportId(standardMatchInfo.getSportId());
        diffMessage.setBeginTime(standardMatchInfo.getBeginTime());
        diffMessage.setGlobalId(linkId);
        diffMessage.setChangeDataSource(true);

        MessageBuilder<RcsClearDiffMessage> builder = MessageBuilder.withPayload(diffMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        //第一个参数表示topic:tag
        rocketMqTemplate.send("RCS_CLEAR_MATCH_MARKET_TAG", builder.build());
        log.info("::{}::通知风控清除TX/LS玩法水差成功,topic:RCS_CLEAR_MARKET_CONFIG_TOPIC,标准赛事ID：{},request:{}", linkId, standardMatchInfo.getId(), JSON.toJSONString(diffMessage));
    }
}
