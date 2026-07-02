package com.panda.merge;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author kb
 * */
@SpringBootApplication
@EnableDubbo
@EnableCaching
@EnableScheduling
@NacosPropertySource(dataId = "panda-es-admin",autoRefreshed = true)
//@NacosPropertySource(dataId = "panda-realtime-admin",autoRefreshed = true)
public class EsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsAdminApplication.class, args);
    }

}
