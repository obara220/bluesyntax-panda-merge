package com.panda.merge;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author aison
 * */
@SpringBootApplication(exclude = {RocketMQAutoConfiguration.class})
@EnableDubbo
@EnableCaching
@EnableScheduling
//@NacosPropertySource(dataId = "panda-scores-admin-taiguo-yuenan",autoRefreshed = true)
@NacosPropertySource(dataId = "panda-scores-admin",autoRefreshed = true)
@NacosPropertySource(dataId = "panda-data-common",autoRefreshed = true)
public class ScoresAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScoresAdminApplication.class, args);
        System.out.println("比分服务启动完毕 !!! ");
        System.out.println("tag 20231122 !!! ");
    }

}
