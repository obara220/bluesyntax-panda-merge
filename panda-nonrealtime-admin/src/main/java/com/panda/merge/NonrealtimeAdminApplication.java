package com.panda.merge;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author aison
 * */
@EnableScheduling
@EnableCaching
@EnableDubbo
@EnableAsync
@SpringBootApplication(exclude = {RocketMQAutoConfiguration.class})
@NacosPropertySource(dataId = "panda-nonrealtime-admin",autoRefreshed = true)
@NacosPropertySource(dataId = "panda-data-common", autoRefreshed = true)
public class NonrealtimeAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NonrealtimeAdminApplication.class, args);
    }

}
