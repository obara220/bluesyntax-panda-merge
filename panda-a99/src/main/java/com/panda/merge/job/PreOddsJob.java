package com.panda.merge.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.job.common.A99MarketOddsCommon;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_A99_PRE_TASK_CRON;

/**
 * 早盘赔率(默认30秒计算一次)
 */
@Slf4j
@Component
@JobHandler(value = "PreOddsJob")
public class PreOddsJob extends IJobHandler {

    String HOST_ADDRESS = "";
    String KEY = Constant.REDIS_KEY.RONGHE_A99_PRE_TASK_KEY;

    @Autowired
    RedisService redisService;

    @Autowired
    private A99MarketOddsCommon marketOddsCommon;

    private String cronExpression = "0/30 * * * * ?";

    public void updateCronExpression(String newCron){
        this.cronExpression = newCron;
        redisService.set(RONGHE_A99_PRE_TASK_CRON, newCron, RedisConfig.REDIS_YEAR_TIME);
    }


    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始执行调度任务===>PreOddsJob!,param=" + param);
        Set<Long> preSet = marketOddsCommon.getA99MatchIds(Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS);
        if (CollectionUtil.isNotEmpty(preSet)) {
            List<Long> list = new ArrayList<>(preSet);
            int numberOfThreads = 5;
            int size = list.size();
            int chunkSize = (int) Math.ceil((double) size / numberOfThreads);

            for (int i = 0; i < numberOfThreads; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, size);
                List<Long> subList = new ArrayList<>(list.subList(start, end));
                if (CollectionUtil.isNotEmpty(subList)) {
                    marketOddsCommon.calculateMarketOdds(subList, 1, 2);
                }
            }
        }
        //bug-107772
        Object cacheCronObj = redisService.get(RONGHE_A99_PRE_TASK_CRON);
        if (ObjectUtil.isNotNull(cacheCronObj)) {
            String cacheCron = (String)cacheCronObj;
            if (!StringUtils.equals(cronExpression, cacheCron)) {
                log.info("检测到A99系统参数早盘下发间隔秒数已调整为:{}", cacheCron);
                this.cronExpression = cacheCron;
            }
        }
        XxlJobLogger.log("结束执行调度任务===>PreOddsJob!");
        return ReturnT.SUCCESS;
    }

 /*   @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Object cacheCron = redisService.get(RONGHE_A99_PRE_TASK_CRON);
        if (cacheCron != null) {
            cronExpression = (String) cacheCron;
        }

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("pre-job-thread-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.initialize();

        taskRegistrar.setTaskScheduler(scheduler);

        //注册动态定时任务
        taskRegistrar.addTriggerTask(
                //1.任务执行内容
                () -> executeTask(scheduler),
                //2.触发器，动态获取cron表达式
                triggerContext -> {
                    CronTrigger trigger = new CronTrigger(cronExpression);
                    return trigger.nextExecutionTime(triggerContext);
                }
        );
    }
*/
/*
    public void executeTask(ThreadPoolTaskScheduler scheduler){
        if (StringUtils.isEmpty(HOST_ADDRESS)) {
            InetAddress address = null;
            try {
                address = getLocalHostExactAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HOST_ADDRESS = address.getHostAddress();
        }
        Object oldAddress = redisService.get(KEY);
        if (oldAddress == null || StringUtils.equals((String) oldAddress, HOST_ADDRESS)) {
            log.info("执行早盘定时任务,当前执行节点:{}", oldAddress);
            redisService.set(KEY, HOST_ADDRESS, RedisConfig.REDIS_SIXTY_SECOND);
            //获取需要计算A99赔率的滚球赛事id
//            Object matchIds = redisService.get(Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS);
//            if(matchIds != null) {
//                Set<Long> set = (Set)matchIds;
//                marketOddsCommon.calculateMarketOdds(new ArrayList<>(set), 1);
//            }
            Set<Long> preSet = marketOddsCommon.getA99MatchIds(Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS);
            if (CollectionUtil.isNotEmpty(preSet)) {
                List<Long> list = new ArrayList<>(preSet);
                int numberOfThreads = 5;
                int size = list.size();
                int chunkSize = (int) Math.ceil((double) size / numberOfThreads);

                for (int i = 0; i < numberOfThreads; i++) {
                    int start = i * chunkSize;
                    int end = Math.min(start + chunkSize, size);
                    List<Long> subList = list.subList(start, end);

                    scheduler.submit(new Runnable() {
                        @Override
                        public void run() {
                            marketOddsCommon.calculateMarketOdds(subList, 0, 3);
                        }
                    });
                }
            }
            //bug-107772
            Object cacheCronObj = redisService.get(RONGHE_A99_PRE_TASK_CRON);
            if (ObjectUtil.isNotNull(cacheCronObj)) {
                String cacheCron = (String)cacheCronObj;
                if (!StringUtils.equals(cronExpression, cacheCron)) {
                    log.info("检测到A99系统参数早盘下发间隔秒数已调整为:{}", cacheCron);
                    this.cronExpression = cacheCron;
                }
            }
        }
    }
*/
//    @Override
//    public ReturnT<String> execute(String param) throws Exception {
//        log.info("执行早盘定时任务，入参:{}", param);
//        return ReturnT.SUCCESS;
//    }
}
