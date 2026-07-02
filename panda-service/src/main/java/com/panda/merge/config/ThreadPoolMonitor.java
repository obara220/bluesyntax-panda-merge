package com.panda.merge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class ThreadPoolMonitor {

    public static void printThreadPoolStats(ThreadPoolTaskExecutor executor,String linkId,String name) {
        ThreadPoolExecutor pool = executor.getThreadPoolExecutor();
        /** taskCount:任务条数，completedTaskCount：已完成条数，activeCount：正在进行条数，queueSize:等待条数*/
        log.info("linkId={}, 线程名称={},核心线程数 [{}], 活跃线程数 [{}], 最大线程数 [{}], 队列任务数 [{}], 已完成任务数 [{}], 总任务数 [{}]",
                linkId, name,
                pool.getCorePoolSize(),
                pool.getActiveCount(),
                pool.getMaximumPoolSize(),
                pool.getQueue().size(),
                pool.getCompletedTaskCount(),
                pool.getTaskCount());
    }
}
