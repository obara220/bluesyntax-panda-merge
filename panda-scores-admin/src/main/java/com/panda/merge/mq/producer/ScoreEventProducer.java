package com.panda.merge.mq.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.mq.spare.SpareBaseProducer;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;


/**
 * 比分事件消息类
 */
@Service
@Slf4j
public class ScoreEventProducer {
    @NacosValue(value = "${panda.data.mq.gateway.event:1}", autoRefreshed = true)
    private int pandaDataMqGatewayevent;
    @NacosValue(value = "${panda.data.mq.gateway.matchId:1,}", autoRefreshed = true)
    private String pandaDataMqGatewayMatchId;
    @Autowired
    RedisService redisService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Autowired
    private SpareBaseProducer spareBaseProducer;
    private static final String SCORE_EVENT="SCORE_EVENT:";
    private static final String KICK_OFF_EVENT="KICK_OFF_EVENT:";
    public void sendBTEvent( MatchEventInfoDTO matchEventInfo) {
        //根据link 缓存事件
        redisService.set(SCORE_EVENT+matchEventInfo.getCopyLinkId(),matchEventInfo,3600);
        Request<MatchEventInfoDTO> reqMessage = new Request<>();
        reqMessage.setLinkId(matchEventInfo.getCopyLinkId());
        reqMessage.setData(matchEventInfo);
        MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        boolean spareMqFlag = getSpareMqFlag(matchEventInfo.getThirdMatchSourceId(),matchEventInfo.getDataSourceCode());
        if (pandaDataMqGatewayevent == 2 && spareMqFlag) {
            String dataSourceCode = matchEventInfo.getDataSourceCode();
            Request<MatchEventInfoDTO> request = new Request<>(matchEventInfo, reqMessage.getLinkId(), THIRD_MATCH_EVENT_INFO_API, reqMessage.getLinkId(), dataSourceCode);
            spareBaseProducer.send(request);
        } else {
            rocketMqTemplate.send("THIRD_MATCH_EVENT_INFO_API:" + matchEventInfo.getThirdMatchSourceId(), builder.build());
        }
        log.info("::{}::开始组装BT比分事件下发,topic:THIRD_MATCH_EVENT_INFO_API,request={}", reqMessage.getLinkId(), JSON.toJSONString(reqMessage));
    }
    public boolean getSpareMqFlag(String thirdSourceId,String dataSourceCode) {
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode,thirdSourceId);
        if(thirdMatchInfo==null){
            return false;
        }
        // 快速失败：不满足基本条件直接返回
        if (pandaDataMqGatewayevent != 2 || thirdMatchInfo.getReferenceId()==null) {
            return false;
        }

        // 处理备用MQ配置
        if (org.apache.commons.lang3.StringUtils.isBlank(pandaDataMqGatewayMatchId)) {
            return true;
        }

        // 转换为set集合
        Set<String> spareMatchIds = new HashSet<>(
                Arrays.asList(pandaDataMqGatewayMatchId.split(","))
        );
        return spareMatchIds.contains(thirdMatchInfo.getReferenceId()+"");
    }
}
