package com.panda.merge.rocketmq.producer;


import com.panda.merge.dto.AoMatchEventsHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import static com.panda.aocollect.common.constant.ConstantSystem.MATCH_EVENT_HISTORY;


@Slf4j
@Component
public class AoEventsHistoryProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 组装标准赛事变更通知数据
     */
    public void pushModifyMatchInfoMessage(AoMatchEventsHistoryDto dto) {
        if(dto.getData().size()==0||dto.getMatchId()==null||dto.getMatchId().equals(0l)){
            log.info("linkId=【{}】ModifyMatchInfoProducer,topic=MATCH_EVENT_HISTORY 无标准赛事Id 或者无事件产生", dto.getLinkId());
            return;
        }
        log.info("linkId=【{}】ModifyMatchInfoProducer,topic=MATCH_EVENT_HISTORY; standardMatchId:{} ", dto.getLinkId(), dto.getMatchId());
        MessageBuilder<AoMatchEventsHistoryDto> builder = MessageBuilder.withPayload(dto).setHeader(MessageConst.PROPERTY_KEYS, dto.getLinkId());
        log.info("linkId=【{}】开始组装标通知AO事件历史通知数据下发,topic=MATCH_EVENT_HISTORY:{}",  dto.getLinkId(),  dto.getMatchId());
        rocketMqTemplate.send(MATCH_EVENT_HISTORY +":"+  dto.getMatchId(), builder.build());
        log.info("【"+ MATCH_EVENT_HISTORY + "】【{}】通知AO事件历史完成【linkId={} : standardMatchId={}】", dto.getLinkId(),  dto.getMatchId() );

    }
}
