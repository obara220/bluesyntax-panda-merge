package com.panda.merge.mq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.model.ThirdMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 推送三方赛事
 *
 * @author warren
 * @since 2025/02/11 18:40:25
 */
@Slf4j
@Service
public class ThirdMatchInfoProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 三方赛事信息异步修改通知
     *
     * @param thirdMatchInfo 三方赛事
     */
    public void updateMatchTimesInfoByMq(ThirdMatchInfo thirdMatchInfo) {
        Request<ThirdMatchInfo> reqMessage = new Request<>();
        reqMessage.setLinkId(thirdMatchInfo.getId() + "");
        reqMessage.setData(thirdMatchInfo);
        MessageBuilder<Request<ThirdMatchInfo>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("DATA_THIRD_MATCH_INFO_DB:" + thirdMatchInfo.getId(), builder.build());
        log.info("::{}::开始组装三方赛事信息异步修改通知,topic:DATA_THIRD_MATCH_INFO_DB,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }
}
