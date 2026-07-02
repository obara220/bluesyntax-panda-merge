package com.panda.merge.mq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 推送标准赛事
 *
 * @author warren
 * @since 2025/02/11 18:53:20
 */
@Slf4j
@Service
public class StandardMatchInfoProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 标准赛事信息异步修改通知
     *
     * @param standardMatchInfo 标准赛事
     */
    public void updateMatchTimesInfoByMq(StandardMatchInfo standardMatchInfo) {
        Request<StandardMatchInfo> reqMessage = new Request<>();
        reqMessage.setLinkId(standardMatchInfo.getId() + "");
        reqMessage.setData(standardMatchInfo);
        MessageBuilder<Request<StandardMatchInfo>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("DATA_STANDARD_MATCH_INFO_DB:" + standardMatchInfo.getId(), builder.build());
        log.info("::{}::开始组装标准赛事信息异步修改通知,topic:DATA_STANDARD_MATCH_INFO_DB,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }
}
