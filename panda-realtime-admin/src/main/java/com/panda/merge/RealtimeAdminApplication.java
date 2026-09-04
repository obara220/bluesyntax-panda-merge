package com.panda.merge;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.rocketmq.client.log.ClientLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@Slf4j
@SpringBootApplication
@EnableDubbo
@EnableCaching
@NacosPropertySource(dataId = "panda-realtime-admin", autoRefreshed = true)
@NacosPropertySource(dataId = "panda-data-common",autoRefreshed = true)
public class RealtimeAdminApplication {

    private static boolean rocketmqPandaLogSwitch = true;

    public static void main(String[] args) {
//        initializeConfig();
        SpringApplication.run(RealtimeAdminApplication.class, args);
    }


    public static void initializeConfig() {
        try {
            if (rocketmqPandaLogSwitch) {
                //设置系统属性，启用SLF4J日志输出, rocketmqclient
                System.setProperty(ClientLogger.CLIENT_LOG_USESLF4J, "true");
                log.info("InitConfig,设置rocketmq日志交给Slf4j处理");
            }
        } catch (Exception e) {
            log.info("InitConfig,初始化异常,Exception:", e);
        }
    }

}
