package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchPhraseDetail;
import com.panda.merge.model.ThirdMatchPhrase;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 下发赛事文字直播到下游
 * @author tell
 * @since  2020年12月10日12:12:15
 */
@Slf4j
@Component
public class ThirdMatchPhraseInfoProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void pushThirdMatchPhraseInfo(String linkId, ThirdMatchPhraseDetail thirdMatchPhrase){
        Request<ThirdMatchPhrase> request = new Request<>();
        request.setLinkId(linkId);
        request.setDataSourceCode(thirdMatchPhrase.getDataSourceCode());
        request.setData(thirdMatchPhrase);
        MessageBuilder<Request<ThirdMatchPhrase>> requestMessageBuilder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("THIRD_MATCH_PHRASE_INFO:" + thirdMatchPhrase.getThirdMatchSourceId(), requestMessageBuilder.build());
        log.info("linkId=【{}】组装赛事文字直播信息下发完成,topic=THIRD_MATCH_PHRASE_INFO,request:{}", linkId, JSON.toJSONString(request));
    }
}
