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

    public static void main(String[] args) {
        SpringApplication.run(RealtimeAdminApplication.class, args);
    }

}
