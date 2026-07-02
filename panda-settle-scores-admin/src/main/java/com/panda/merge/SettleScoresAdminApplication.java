package com.panda.merge;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@NacosPropertySource(dataId = "panda-settle-scores-admin",autoRefreshed = true)
@NacosPropertySource(dataId = "panda-data-common",autoRefreshed = true)
@EnableScheduling
public class SettleScoresAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SettleScoresAdminApplication.class, args);
        System.out.println("结算服务启动完成 X= " + args);
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
        log.info("-----------------SettleScoresAdminApplication 726.0 success fully start!!-------");
    }

}
