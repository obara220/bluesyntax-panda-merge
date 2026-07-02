package com.panda.merge.task;


import com.panda.merge.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;


@Configuration
@Slf4j
public class CleanCacheScheduleTask {

    @Autowired
    RedisService redisService;
    //3.添加定时任务 3600秒
    @Scheduled(cron = "0 0 12 * * ?")
    private void configureTasks() {
        log.info("执行清理缓存定时任务时间: " + LocalDateTime.now());
        System.gc();
        log.info("执行清理缓存定时任务结束时间: " + LocalDateTime.now());
    }

}
