package com.panda.merge;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用启动入口
 * Created by macro on 2018/4/26.
 */
@EnableScheduling
@SpringBootApplication
@EnableDubbo
@EnableCaching
@NacosPropertySource(dataId = "panda-odds-admin-me",autoRefreshed = true)
@EnableAsync
public class TestAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestAdminApplication.class, args);
    }


}
