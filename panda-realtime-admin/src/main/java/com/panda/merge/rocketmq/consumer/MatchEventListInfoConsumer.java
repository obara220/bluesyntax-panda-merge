package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.MatchEventInfoProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_LIST_INFO_API;

/**
* 第三方赛事盘中事件列表接收（list增量事件）
* @author    tell
* @since     2020年11月25日08:27:57
*/
@Slf4j
@Component
@RocketMQMessageListener(topic = THIRD_MATCH_EVENT_LIST_INFO_API,
        consumerGroup = CONSUME_REALTIME_GROUP + THIRD_MATCH_EVENT_LIST_INFO_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class MatchEventListInfoConsumer implements RocketMQListener<MessageExt> {

   @Autowired
   MatchEventInfoProcessor matchEventInfoProcessor;
   @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
   private boolean realtimeSwitch;
   @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
   private boolean realtimeEventSwitch;
   @Resource
   private DataCenterProducer dataCenterProducer;

//   @ConsumerSwitch("realtime")
   @Override
   public void onMessage(MessageExt ext) {
      if (!realtimeSwitch && !realtimeEventSwitch) {
         if (dataCenterProducer.checkForward(ext,THIRD_MATCH_EVENT_LIST_INFO_API)) {
            dataCenterProducer.send(ext,THIRD_MATCH_EVENT_LIST_INFO_API);
            return;
         }
      }
       matchEventInfoProcessor.putMatchEventListInfo(ext,false);
   }
}
