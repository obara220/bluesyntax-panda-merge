package com.panda.merge.rocketmq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.MatchCopyDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.MatchCopyProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.COPY_MATCH;

/**
* 拷贝赛事相关信息（拷贝赛事事件）
* @author    tell
* @since     2022年1月30日13:38:32
*/
@Slf4j
@Component
@RocketMQMessageListener(topic = COPY_MATCH,
       consumerGroup = CONSUME_REALTIME_GROUP + COPY_MATCH,
       consumeThreadMax = 128,
       consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class MatchCopyConsumer implements RocketMQListener<Request<MatchCopyDTO>> {

   @Autowired
   private MatchCopyProcessor matchCopyProcessor;

   /**
    *  拷贝赛事开关（false:关，true：开）
    * */
   @NacosValue(value = "${copy.match.switch:false}", autoRefreshed = true)
   private boolean copyMatchSwitch;
   @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
   private boolean realtimeSwitch;
   @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
   private boolean realtimeEventSwitch;
   @Resource
   private DataCenterProducer<MatchCopyDTO> dataCenterProducer;

//   @ConsumerSwitch("realtime")
   @Override
   public void onMessage(Request<MatchCopyDTO> request) {
      if (!realtimeSwitch && !realtimeEventSwitch) {
         dataCenterProducer.send(request,COPY_MATCH);
         return;
      }
      log.info("::{}::"+COPY_MATCH+"拷贝赛事相关信息开始，传入参数：{},copyMatchSwitch={}",request.getLinkId(),copyMatchSwitch);
      if(copyMatchSwitch){
         request.setLinkId(COPY_MATCH+"_linkId_"+request.getLinkId());
         matchCopyProcessor.putMatchCopyProcessor(request);
      }
      log.info("::{}::"+COPY_MATCH+"拷贝赛事相关信息结束",request.getLinkId());
   }
}
