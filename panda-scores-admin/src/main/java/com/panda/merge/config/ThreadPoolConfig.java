package com.panda.merge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync
@Configuration
public class ThreadPoolConfig {

    /**
     * 处理标准比分
     */
//    @Bean("StandardMatchScoresThreadPool")
//    public TaskExecutor getStandardMatchScoresThreadPool() {
//        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
//        //配置核心线程数
//        executor.setCorePoolSize(32);
//        //配置最大线程数
//        executor.setMaxPoolSize(64);
//        //配置队列大小
//        executor.setQueueCapacity(128);
//        // 设置线程活跃时间（秒）
//        //executor.setKeepAliveSeconds(60);
//        //配置线程池中的线程的名称前缀
//        executor.setThreadNamePrefix("ThreadPool-StandardMatchScores-data-");
//        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        // 等待所有任务结束后再关闭线程池
//        executor.setWaitForTasksToCompleteOnShutdown(true);
//        //线程池初始化
//        executor.initialize();
//        return executor;
//    }
}
