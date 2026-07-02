//package com.panda.merge.mqtest;
//
//import com.panda.merge.common.MQTest;
//import com.panda.merge.dto.PaServiceWarnInfoDTO;
//
//import com.panda.merge.dto.Request;
//import org.apache.rocketmq.client.exception.MQBrokerException;
//import org.apache.rocketmq.common.message.MessageConst;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//
//
//@Component
//public class MQBrockerExceptionTest {
//
//    @Autowired
//    MQTest mqTest;
//    @Scheduled(cron="*/5 * * * * ?")   //每5秒执行一次
//    public void test() throws MQBrokerException {
//        PaServiceWarnInfoDTO paServiceWarnInfoDTO=new PaServiceWarnInfoDTO();
//        Request<PaServiceWarnInfoDTO> reqMessage = new Request<>();
//        reqMessage.setLinkId("testLinkedId");
//        reqMessage.setData(paServiceWarnInfoDTO);
//        MessageBuilder<Request<PaServiceWarnInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
//                .setHeader(MessageConst.PROPERTY_KEYS, "testLinkedId");
//        mqTest.testMQ("testTopic",builder.build());
//    }
//
//
//}
