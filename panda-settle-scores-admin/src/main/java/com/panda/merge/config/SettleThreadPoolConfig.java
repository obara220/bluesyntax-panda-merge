package com.panda.merge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EnableAsync
@Configuration
public class SettleThreadPoolConfig {
    /** 即时结算线程池*/
    @Bean("InstantSettleThreadPool")
    public TaskExecutor getLiveMatchThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(64);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(256);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-InstantSettle-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    @Bean("settleMentionFactoryThreadPool")
    public TaskExecutor settleMentionFactoryThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(64);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(256);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-settleMentionFactoryThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 主流程次序事件线程池*/
    @Bean("MatchEventLogThreadPool")
    public TaskExecutor matchEventLogThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(16);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(2048);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-MatchEventLogThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 主流程比分阶段线程池*/
    @Bean("MatchScoreLogThreadPool")
    public TaskExecutor matchScoreLogThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(16);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(2048);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-MatchScoreLogThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    @Bean("PushStandardSettleScoresThreadPool")
    public TaskExecutor pushStandardSettleScoresThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(16);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(2048);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-PushStandardSettleScoresThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    @Bean("PushStandardSettleEventThreadPool")
    public TaskExecutor pushStandardSettleEvent() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(16);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(2048);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-PushStandardSettleEventThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    @Bean("RemoveDBThreadPool")
    public TaskExecutor removeDBThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(16);
        //配置最大线程数
        executor.setMaxPoolSize(16);
        //配置队列大小
        executor.setQueueCapacity(2048);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-removeDBThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

}
