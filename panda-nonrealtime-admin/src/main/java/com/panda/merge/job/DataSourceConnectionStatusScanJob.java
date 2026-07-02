package com.panda.merge.job;

import com.panda.merge.api.ISettleCenterApi;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.settle.DataSourceConnectionStatusDto;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.merge.config.RedisConfig.REDIS_FOUR_SECOND;

/**
 * 数据商连接状态扫描定时任务
 * 通过XXL-Job触发，调用结算服务拉取所有赛事的数据商连接状态并推送到前端
 * 
 * @author system
 * @date 2026-01-04
 */
@Slf4j
@Component
@JobHandler(value = "DataSourceConnectionStatusScanJob")
public class DataSourceConnectionStatusScanJob extends IJobHandler {

    @Autowired
    private RedisService redisService;
    
    @DubboReference(check = false)
    private ISettleCenterApi settleCenterApi;

    @Override
    public ReturnT<String> execute(String param) {
        log.info("DataSourceConnectionStatusScanJob start...");
        XxlJobLogger.log("DataSourceConnectionStatusScanJob start...");
        String lockKey = RedisConfig.REDIS_KEY_DATABASE + "::job:scanDataSourceConnectionStatus";
        try {
            // 获取分布式锁，避免多实例重复执行
            if (!redisService.tryLockOnce(lockKey, lockKey, REDIS_FOUR_SECOND)) {
                XxlJobLogger.log("DataSourceConnectionStatusScanJob 数据商连接状态扫描任务正在其他实例执行，跳过本次执行");
                log.debug("DataSourceConnectionStatusScanJob 数据商连接状态扫描任务正在其他实例执行，跳过本次执行");
                return ReturnT.SUCCESS;
            }
            
            long startTime = System.currentTimeMillis();
            
            // 调用结算服务，扫描所有赛事的数据商连接状态
            // scanAllMatchesConnectionStatus() 方法内部会自动推送状态改变的赛事
            XxlJobLogger.log("DataSourceConnectionStatusScanJob process scanAllMatchesConnectionStatus ...");
            log.info("DataSourceConnectionStatusScanJob process scanAllMatchesConnectionStatus ...");
            settleCenterApi.scanAllMatchesConnectionStatus();
            XxlJobLogger.log("DataSourceConnectionStatusScanJob process finish!");
            log.info("DataSourceConnectionStatusScanJob process finish!");
            return ReturnT.SUCCESS;
            
        } catch (Exception e) {
            XxlJobLogger.log("DataSourceConnectionStatusScanJob 数据商连接状态扫描任务执行失败");
            log.error("DataSourceConnectionStatusScanJob 数据商连接状态扫描任务执行失败", e);
            return ReturnT.FAIL;
        } finally {
            redisService.unLock(lockKey, lockKey);
        }
    }
}

