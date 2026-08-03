package com.panda.merge.job;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.rocketmq.producer.MatchBeginProducer;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@JobHandler(value = "MatchBeginJob")
public class MatchBeginJob extends IJobHandler {
    @Autowired
    public RedisService redisService;
    @Autowired
    public MatchBeginProducer matchBeginProducer;

    @Override
    public ReturnT<String> execute(String param) {
        try {
            String matchBeginStr = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
            //log.info("定时任务开始扫描缓存中赛事开赛时间总数据,key:{}", matchBeginStr);
            Map<String, Long> matchMap = redisService.hGetAllBasedBucket(matchBeginStr, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
            /////log.info("定时任务开始扫描缓存中赛事开赛时间总数据,matchMap:{}", JSON.toJSON(matchMap));
            if (!MapUtils.isEmpty(matchMap)) {
                Map<String, Long> delMap = new HashMap<>();
                for (Map.Entry<String, Long> entry : matchMap.entrySet()) {
                    if (entry.getValue() < TimeUtils.millsSecondsEast8ZoneGmt()) {
                        delMap.put(entry.getKey(), entry.getValue());
                        String updatedKey = redisService.genNewHashKey(matchBeginStr, entry.getKey(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                        redisService.hDel(updatedKey, entry.getKey());
                    }
                }
                if (MapUtils.isNotEmpty(delMap)) {
                    //log.info("定时任务开始扫描缓存中赛事开赛时间总数据,delMap:{}", JSON.toJSON(delMap));
                    for (Map.Entry<String, Long> entry : delMap.entrySet()) {
                        //下发mq消息，通知赔率服务，该赛事满足下发滚球赔率条件
                        String linkId = UUIdUtils.getId() + "_" + entry.getKey();
                        matchBeginProducer.sendMatchBeginToOddsAdmin(linkId, Long.valueOf(entry.getKey()));
                        String key = Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET + entry.getKey();
                        redisService.set(key, 2, 30);
                    }
                }
            }
        } catch (Exception e) {
            log.error("【定时任务开始扫描缓存中赛事开赛时间总数据,异常】 Exception:", e);
            XxlJobLogger.log("定时任务开始扫描缓存中赛事开赛时间总数据】 异常,Exception:" + e.getMessage());
        }
        //log.info("【定时任务开始扫描缓存中赛事开赛时间总数据】 结束");
        XxlJobLogger.log("【定时任务开始扫描缓存中赛事开赛时间总数据】 结束");
        return ReturnT.SUCCESS;
    }
}
