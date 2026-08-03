//package com.panda.merge.component;
//
//
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.client.log.ClientLogger;
//import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//@AutoConfigureBefore(RocketMQAutoConfiguration.class)
//public class InitConfig {
//
//    @NacosValue(value = "${rocketmq.panda.log.switch:false}", autoRefreshed = true)
//    boolean rocketmqPandaLogSwitch;
//
//    @Bean
//    public void initializeConfig() {
//        if (rocketmqPandaLogSwitch) {
//            //设置系统属性，启用SLF4J日志输出, rocketmqclient
//            System.setProperty(ClientLogger.CLIENT_LOG_USESLF4J, "true");
//            log.info("InitConfig,设置rocketmq日志交给Slf4j处理,systemProperty={}",System.getProperty(ClientLogger.CLIENT_LOG_USESLF4J));
//        }
//    }
//}
