package com.panda.merge;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @Author Kepa
 * @Date 2020/12/12 14:37
 * @Version 1.0
 */
@SpringBootApplication
@EnableScheduling
@EnableDubbo
@EnableCaching
@NacosPropertySource(dataId = "panda-websocket-admin", autoRefreshed = true)
@NacosPropertySource(dataId = "panda-data-common", autoRefreshed = true)
public class MergeWebSocketApplication {


    public static void main(String[] args) {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(false);
        SpringApplication.run(MergeWebSocketApplication.class, args);
    }

}
