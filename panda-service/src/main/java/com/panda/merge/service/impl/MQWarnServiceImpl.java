//package com.panda.merge.service.impl;
//
//import com.panda.merge.dto.PaServiceWarnInfoDTO;
//import com.panda.merge.service.MQWarnService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.exception.MQBrokerException;
//import org.apache.rocketmq.common.message.MessageConst;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//
//@Service
//@Slf4j
//public class MQWarnServiceImpl implements MQWarnService {
//    /**当前服务名称*/
////    @Value("${spring.application.name}")
//    private String SERVER_NAME="merge";
//    /**当前研发分组*/
//    private String SOURCE="merge";
//    /**MQ异常名称*/
//    private String   MQ_BROKER_EXCCEPTION="MQ异常 MQBrokerException";
//    /**预警mq topic*/
//    private String  PA_SERVICE_WARN_INFO="PA_SERVICE_WARN_INFO";
//    @Autowired
//    RocketMQTemplate rocketMQTemplate;
//
//    @Override
//    public void mqBrockerWarn(Throwable e, String topic, String linkedId) {
//        if(e instanceof MQBrokerException){
//            //获得参数，组装MQ 发送MQ
//                    //获取MQ异常信息
//                    String warnMsg= SERVER_NAME+" topic: "+topic+":"+MQ_BROKER_EXCCEPTION+",linkedId:"+linkedId;
//                    //组装预警消息体
//                    PaServiceWarnInfoDTO warnInfoDTO=new PaServiceWarnInfoDTO();
//                    warnInfoDTO.setLevel(2);
//                    warnInfoDTO.setDataSourceCode(SOURCE);
//                    warnInfoDTO.setLinkId( SERVER_NAME+"_"+UUID.randomUUID().toString());
//                    warnInfoDTO.setModifyTime(System.currentTimeMillis());
//                    warnInfoDTO.setMessage(warnMsg);
//                    //发送预警
//                    MessageBuilder<PaServiceWarnInfoDTO> builder = MessageBuilder.withPayload(warnInfoDTO)
//                            .setHeader(MessageConst.PROPERTY_KEYS, linkedId);
//                    rocketMQTemplate.send(PA_SERVICE_WARN_INFO,builder.build());
//                }
//
//        }
//
//}
