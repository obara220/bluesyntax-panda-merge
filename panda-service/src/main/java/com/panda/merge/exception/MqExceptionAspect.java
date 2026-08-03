//package com.panda.merge.exception;
//
//import com.panda.merge.service.MQWarnService;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.exception.MQBrokerException;
//import org.apache.rocketmq.common.message.MessageConst;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.messaging.Message;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//
///**
// * KB
// * rocketMq 堵塞异常-预警处理
// * 2021-10-02
// */
//@Slf4j
//@Aspect
//@Component
//@Service
//@Data
//public class MqExceptionAspect {
//
////    @Value("${mq.warn.broker.on:0}")
//    private Integer MQ_WARN_BROKER=1;
//    @Autowired
//    MQWarnService mqWarnService;
//
//    @Pointcut("execution(* org.apache.rocketmq.spring.core.RocketMQTemplate.send(..))" )
//    private void send() {
//    }
////    @Pointcut("execution(* com.panda.merge.common.MQTest.testMQ(..))" )
////    private void testPoint() {
////    }
//    /**
//     堵塞异常 发送到预警MQ
//     */
//    @AfterThrowing(pointcut = "send()" ,throwing = "e")
////    @AfterThrowing(pointcut = "send()" ,throwing = "e")
//    public void handleThrowing(JoinPoint jp ,Exception e) {
//        if(MQ_WARN_BROKER==null||MQ_WARN_BROKER!=1){
//            if(e instanceof MQBrokerException){
//                log.error("MQBrokerException!");
//            }
//            return;
//        }
//        try{
//            if(e instanceof MQBrokerException){
//                //获得参数，组装MQ 发送MQ
//                Object[] args=  jp.getArgs();
//                if(args!=null&&args.length>1){
//                    Object msgo= args[1];
//                    if(msgo instanceof Message){
//                        //获取MQ异常信息
//                        Message msg=(Message) msgo;
//                        String linkedId= msg.getHeaders().get(MessageConst.PROPERTY_KEYS)!=null?msg.getHeaders().get(MessageConst.PROPERTY_KEYS).toString(): UUID.randomUUID().toString();
//                        String topic= args[0]!=null?args[0].toString():"";
//                        mqWarnService.mqBrockerWarn(e,topic,linkedId);
//                    }
//                }
//            }
//        }catch (Exception ex){
//            log.error("MqExceptionAspect error:{}",ex.getStackTrace());
//        }
//    }
//}