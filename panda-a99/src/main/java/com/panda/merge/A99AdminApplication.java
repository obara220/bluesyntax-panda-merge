package com.panda.merge;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@EnableDubbo
@EnableAsync
@SpringBootApplication(
        excludeName = {"org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration"}
)
@NacosPropertySource(dataId = "panda-a99-admin",autoRefreshed = true)
public class A99AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(A99AdminApplication.class, args);
    }
}
