package com.panda.merge.job;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.job
 * @description : TODO
 * @date: 2021-02-27 12:16
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
@JobHandler(value = "UpCacheThirdMarketStatusJob")
public class UpCacheThirdMarketStatusJob extends IJobHandler {

    @Autowired
    private RedisService redisService;

    /**
     * TX数据源关盘 刷缓存
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Set<String> keys = redisService.keys("Ronghe:StandardMarketData:*");
        AtomicInteger num = new AtomicInteger();
        keys.forEach(key -> {
            if (key.contains("TX")) {
                Map<String, StandardMarketDataMessage> standardMarketLiveDataMessageMap = redisService.hGetAll(key);
                for (StandardMarketDataMessage standardMarketDataMessage : standardMarketLiveDataMessageMap.values()) {
                    if (!standardMarketDataMessage.getStatus().equals(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED)) {
                        standardMarketDataMessage.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                        //这里是唯一改变数据源状态的地方
                        standardMarketDataMessage.setThirdMarketSourceStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                        standardMarketDataMessage.setLinkId("");
                        standardMarketDataMessage.setRemark("TX旧数据缓存关盘");
                        Long time = standardMarketDataMessage.getMarketType() == 0 ? RedisConfig.REDIS_WEEK_TIME.longValue() : RedisConfig.REDIS_MONTH_TIME.longValue();
                        redisService.hSet(key, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, time);
                    }
                    num.getAndIncrement();
                }
            }
        });
        XxlJobLogger.log("【UpThirdMarketStatusJob TX旧数据关盘】 处理成功：{}", num);
        return ReturnT.SUCCESS;
    }
}
