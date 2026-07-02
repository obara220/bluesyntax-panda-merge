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

    /** 三方盘口、赔率入库处理线程池*/
    @Bean("ThirdSportMarketThreadPool")
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
        executor.setThreadNamePrefix("ThreadPool-ThirdSportMarket-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 标准盘口、赔率处理线程池*/
    @Bean("StandardSportMarketThreadPool")
    public TaskExecutor getStandardSportMarketThreadPoolPool() {
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
        executor.setThreadNamePrefix("ThreadPool-match-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 三方联赛处理线程池*/
    @Bean("getTournamentThreadPool")
    public TaskExecutor getTournamentThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(16);
        //配置最大线程数
        executor.setMaxPoolSize(32);
        //配置队列大小
        executor.setQueueCapacity(64);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-ThirdTournamentInfo-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 三方赛事处理线程池*/
    @Bean("getMatchThreadPool")
    public TaskExecutor getMatchThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-ThirdMatchInfo-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 三方球队人员处理线程池*/
    @Bean("getTeamPayerThreadPool")
    public TaskExecutor getTeamPayerThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(16);
        //配置最大线程数
        executor.setMaxPoolSize(32);
        //配置队列大小
        executor.setQueueCapacity(64);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-ThirdSportPlayer-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 赔率源 线程池*/
    @Bean("AccessMatchMarketData")
    public TaskExecutor getAccessMatchMarketData() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-access-match-market-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 告警任务 线程池*/
    @Bean("reportExecutor")
    public TaskExecutor getReportExecutor() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //告警任务对实时要求不严格，队列适当扩大
        executor.setQueueCapacity(1024);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-report-");
        // 允许核心线程退出降低资源消耗
        executor.setAllowCoreThreadTimeOut(true);
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，抛弃告警任务避免影响主流程
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.error("report task {} rejected by {} ", r.toString(), executor.toString());
            }
        });
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 告警任务 线程池*/
    @Bean("monitorExecutor")
    public TaskExecutor getMonitorExecutor() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(16);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //监控任务对实时要求不严格，队列适当扩大
        executor.setQueueCapacity(1024);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-monitor-");
        // 允许核心线程退出降低资源消耗
        executor.setAllowCoreThreadTimeOut(true);
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，抛弃告警任务避免影响主流程
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.error("monitor task {} rejected by {} ", r.toString(), executor.toString());
            }
        });
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 事件源 线程池*/
    @Bean("EventInfoThreadPool")
    public TaskExecutor getEventInfoThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-access-eventInfo-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /** 事件数据去DB线程池*/
    @Bean("EventInfoDbThreadPool")
    public TaskExecutor getEventInfoDbThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //告警任务对实时要求不严格，队列适当扩大
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
//        executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-EventInfoDb-");
        // 允许核心线程退出降低资源消耗
        executor.setAllowCoreThreadTimeOut(true);
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，抛弃告警任务避免影响主流程
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.error("EventInfoDb task {} rejected by {} ", r.toString(), executor.toString());
            }
        });
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 操盘后台配置,异步处理 */
    @Bean("ProcessTradeSystemThreadPool")
    public TaskExecutor getProcessTradeSystemThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(64);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(256);
        executor.setThreadFactory(r -> {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler((t, e) -> log.error("ProcessTradeSystem task {} uncaughtException  ",
                                                                   t.getName(),
                                                                   e));
            return thread;
        });
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-ProcessTradeSystemt-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 盘口处理,异步处理 */
    @Bean("ProcessOddsByPandaThreadPool")
    public TaskExecutor getProcessOddsByPandaThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-ProcessOddsByPanda-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /** 统一盘口异步处理*/
    @Bean("InitSportMarketRelation")
    public TaskExecutor getInitSportMarketRelation() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-InitSportMarketRelation-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 统计信息异步线程池*/
    @Bean("PaDataServiceLogDTOThreadPool")
    public TaskExecutor getPaDataServiceLogDTOThreadPool() {
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
        executor.setThreadNamePrefix("ThreadPool-PaDataServiceLogDTOThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 提前结算 线程池
     */
    @Bean("ThirdMarketPreResultThreadPool")
    public TaskExecutor ThirdMarketPreResultThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-ThirdMarketPreResultThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 三方赛事处理线程池*/
    @Bean("cleanHisDataPool")
    public TaskExecutor cleanHisDataPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-cleanHisDataPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 三方所有盘口,异步处理 */
    @Bean("ProcessAllThirdMarketThreadPool")
    public TaskExecutor getProcessAllThirdMarketThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-ProcessAllThirdMarket-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 100s 关盘*/
    @Bean("CloseConvertMarketThreadPool")
    public TaskExecutor getCloseConvertMarketThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-access-CloseConvertMarketThreadPool-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 操盘后台接口配置,异步处理 */
    @Bean("ProcessUiInterfaceThreadPool")
    public TaskExecutor getProcessUiInterfaceThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(64);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(256);
        // 设置线程活跃时间（秒）ProcessTradeSystemt
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-Ui-Interface--data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    /** 三方盘口、标准盘口流程 */
    @Bean("thirdAndStandardMarketProcess")
    public TaskExecutor getThirdAndStandardMarketProcess() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-access-third-standard-market-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 冠军盘的处理
     *
     * @return
     */
    @Bean("championMarketThreadPool")
    public TaskExecutor getChampionMarketThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-access-champion-market-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /** 玩法挡板线程处理 */
    @Bean("ProcessCategoryStatusThreadPool")
    public TaskExecutor getProcessCategoryStatusThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-ProcessCategoryStatusThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /** 去db三方盘口 入库线程 */
    @Bean("thirdMarketInsertAndUpdate")
    public TaskExecutor getThirdMarketInsertAndUpdateProcess() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdMarketInsertAndUpdate-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    /** 去db三方盘口赔率 入库线程 */
    @Bean("thirdMarketOddsInsertAndUpdate")
    public TaskExecutor getThirdMarketOddsInsertAndUpdateProcess() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdMarketOddsInsertAndUpdate-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /** 去db标准盘口 入库线程 */
    @Bean("standardMarketInsertAndUpdate")
    public TaskExecutor getStandardMarketInsertAndUpdateProcess() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-standardMarketInsertAndUpdate-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 调用redis处理 */
    @Bean("CallRedisThreadPool")
    public ThreadPoolTaskExecutor callRedisThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-CallRedisThreadPool-");
        executor.setThreadFactory(r -> {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler((t, e) -> log.error("redis task {} uncaughtException  ",
                                                                   t.getName(),
                                                                   e));
            return thread;
        });
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 调用赔率 redis处理 */
    @Bean("CallOddsRedisThreadPool")
    public ThreadPoolTaskExecutor CallOddsRedisThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-CallOddsRedisThreadPool-");
        executor.setThreadFactory(r -> {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler((t, e) -> log.error("redis task {} uncaughtException  ",
                                                                   t.getName(),
                                                                   e));
            return thread;
        });
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 去db标准盘口赔率 入库线程 */
    @Bean("standardMarketOddsInsertAndUpdate")
    public TaskExecutor getStandardMarketOddsInsertAndUpdateProcess() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-standardMarketOddsInsertAndUpdate-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /** 赔率联动触发 */
    @Bean("sendMarketOddsLinkageThreadPool")
    public TaskExecutor sendMarketOddsLinkageThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-sendMarketOddsLinkageThreadPool-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }



    /** 系统关盘异步线程池*/
    @Bean("sendDeActiveLogThreadPool")
    public TaskExecutor getSendDeActiveLogThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-sendDeActiveLogThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 开售后数据源对应比分,异步处理
     */
    @Bean("SoldMessageStandardScoreThreadPool")
    public TaskExecutor getSoldMessageStandardScoreThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-SoldMessageStandardScore-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    @Bean("SettleDelayProcess")
    public ThreadPoolTaskExecutor settleDelayProcess() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(64);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(2048);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-SettleDelayProcess-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 三方所有盘口,异步处理 */
    @Bean("ProcessA99ThirdMarketThreadPool")
    public TaskExecutor getProcessA99ThirdMarketThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(1024);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-ProcessA99ThirdMarket-data-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 三方所有盘口,异步处理 */
    @Bean("A99JobThreadPool")
    public TaskExecutor getA99JobThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-A99-job-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /** 处理日志记录线程池*/
    @Bean("LogRecordExecutor")
    public TaskExecutor getLogRecordExecutor() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        // 设置线程活跃时间（秒）
        //executor.setKeepAliveSeconds(60);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-LOG-record-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
}
