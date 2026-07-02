package com.panda.merge.job;

import cn.hutool.core.collection.CollectionUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.job.common.A99MarketOddsCommon;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 计算A99赔率(默认每3秒计算一次)
 */
@Slf4j
@Component
@JobHandler(value = "CalculateOddsJob")
public class CalculateOddsJob extends IJobHandler {

    String HOST_ADDRESS = "";
    String KEY = Constant.REDIS_KEY.RONGHE_A99_CALCULATE_TASK_KEY;

    @Value("${calculate.job.cron:0/3 * * * * ?}")
    private String cronExpression;

    @Autowired
    RedisService redisService;

    @Autowired
    private A99MarketOddsCommon marketOddsCommon;

    public void updateCronExpression(String newCron) {
        this.cronExpression = newCron;
    }
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("开始执行调度任务===>CalculateOddsJob!,param=" + param);
        Set<Long> preSet = marketOddsCommon.getA99MatchIds(Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS);
        log.info("::即将计算早盘赔率, 早盘赛事id:{}", preSet);
        if (CollectionUtil.isNotEmpty(preSet)) {
            List<Long> list = new ArrayList<>(preSet);
            int numberOfThreads = 5;
            int size = list.size();
            int chunkSize = (int) Math.ceil((double) size / numberOfThreads);

            for (int i = 0; i < numberOfThreads; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, size);
                List<Long> subList = new ArrayList<>(list.subList(start, end));
                marketOddsCommon.calculateMarketOdds(subList, 1, 3);
            }
        }
        //获取需要计算A99赔率的滚球赛事id
//            Object liveMatch = redisService.get(Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS);
//            if(liveMatch != null) {
//                Set<Long> liveSet = (Set)liveMatch;
//                marketOddsCommon.calculateMarketOdds(new ArrayList<>(liveSet), 0);
//            }
        Set<Long> liveSet = marketOddsCommon.getA99MatchIds(Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS);
        log.info("::即将计算滚球赔率, 滚球赛事id:{}", liveSet);
        if (CollectionUtil.isNotEmpty(liveSet)) {
            List<Long> list = new ArrayList<>(liveSet);
            int numberOfThreads = 5;
            int size = list.size();
            int chunkSize = (int) Math.ceil((double) size / numberOfThreads);

            for (int i = 0; i < numberOfThreads; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, size);
                List<Long> subList = new ArrayList<>(list.subList(start, end));

                marketOddsCommon.calculateMarketOdds(subList, 0, 3);
            }
        }
        XxlJobLogger.log("结束执行调度任务===>CalculateOddsJob!");
        return ReturnT.SUCCESS;
    }
/*    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("calculate-job-thread-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.initialize();
        //注册动态定时任务
    *//*    taskRegistrar.addTriggerTask(
                //1.任务执行内容
                this::executeTask,
                //2.触发器，动态获取cron表达式
                triggerContext -> {
                    CronTrigger trigger = new CronTrigger(cronExpression);
                    return trigger.nextExecutionTime(triggerContext);
                }
        );*//*
        taskRegistrar.addTriggerTask(
                () -> executeTask(scheduler), // 传入 scheduler
                triggerContext -> {
                    CronTrigger trigger = new CronTrigger(cronExpression);
                    return trigger.nextExecutionTime(triggerContext);
                }
        );
    }*/

   /* private void executeTask(ThreadPoolTaskScheduler scheduler) {
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
            log.info("执行赔率定时计算定时任务,当前执行节点:{}", oldAddress);
            redisService.set(KEY, HOST_ADDRESS, RedisConfig.REDIS_SIXTY_SECOND);
            //获取需要计算A99赔率的早盘赛事id
//            Object preMatch = redisService.get(Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS);
//            if(preMatch != null) {
//                Set<Long> preSet = (Set)preMatch;
//                marketOddsCommon.calculateMarketOdds(new ArrayList<>(preSet), 1);
//            }
            Set<Long> preSet = marketOddsCommon.getA99MatchIds(Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS);
            log.info("::即将计算早盘赔率, 早盘赛事id:{}", preSet);
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
            //获取需要计算A99赔率的滚球赛事id
//            Object liveMatch = redisService.get(Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS);
//            if(liveMatch != null) {
//                Set<Long> liveSet = (Set)liveMatch;
//                marketOddsCommon.calculateMarketOdds(new ArrayList<>(liveSet), 0);
//            }
            Set<Long> liveSet = marketOddsCommon.getA99MatchIds(Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS);
            log.info("::即将计算滚球赔率, 滚球赛事id:{}", liveSet);
            if (CollectionUtil.isNotEmpty(liveSet)) {
                List<Long> list = new ArrayList<>(liveSet);
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
        }
    }*/



//    @Override
//    public ReturnT<String> execute(String param) throws Exception {
//        log.info("执行定时计算定时任务，入参:{}", param);
//        return ReturnT.SUCCESS;
//    }
}
