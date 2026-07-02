package com.panda.merge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * OddsThreadPoolConfig
 *
 * @description:  
 * @date:  5/6/2025
 **/
@Slf4j
@EnableAsync
@Configuration
public class OddsThreadPoolConfig {
    /** 数据源切换线程池 */
    @Bean("dataSourceSwitchPool")
    public ThreadPoolTaskExecutor callRedisThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-CallRedisThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 允许核心线程退出降低资源消耗
        executor.setAllowCoreThreadTimeOut(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
}
