package com.panda.merge.mq.producer;


import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchTimeInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MatchTimeInfoProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 赛事时间信息异步修改通知
     * @param
     */
    public void updateMatchTimesInfoByMq(MatchTimeInfo matchTimeInfo) {

        Request<MatchTimeInfo> reqMessage = new Request<>();
        reqMessage.setLinkId(matchTimeInfo.getThirdMatchId()+"");
        reqMessage.setData(matchTimeInfo);
        MessageBuilder<Request<MatchTimeInfo>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("MATCH_TIME_INFO_UPDATE:" + matchTimeInfo.getThirdMatchId(), builder.build());
        log.info("::{}::开始组装结算比分异步更新,topic:MATCH_TIME_INFO_UPDATE,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }

}
