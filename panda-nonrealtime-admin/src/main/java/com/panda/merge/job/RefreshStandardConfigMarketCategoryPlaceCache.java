package com.panda.merge.job;

import cn.hutool.core.util.ObjectUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.model.ConfigMarketCategoryPlace;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardMatchInfoExample;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@JobHandler(value = "RefreshStandardConfigMarketCategoryPlace")
public class RefreshStandardConfigMarketCategoryPlaceCache extends IJobHandler {
    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Override
    public ReturnT<String> execute(String params) {
        log.info("【RefreshStandardConfigMarketCategoryPlace】 处理开始");
        XxlJobLogger.log("RefreshStandardConfigMarketCategoryPlace】 处理开始");
        String[] arr = params.split(",");
        if (arr.length < 3) {
            log.error("【RefreshStandardConfigMarketCategoryPlace】 param {} is not valid!", params);
            return ReturnT.FAIL;
        }
        String type = arr[0];
        switch (type) {
            case "Map":
                syncMapToNewKey(arr);
                break;
            case "MarketStatus":
                refreshMarketStatus();
                break;
            default:
                log.info("【RefreshStandardConfigMarketCategoryPlace】 It is in the default!");
        }
        log.info("【RefreshStandardConfigMarketCategoryPlace】 处理结束");
        XxlJobLogger.log("RefreshStandardConfigMarketCategoryPlace】 处理结束");
        return ReturnT.SUCCESS;
    }

    private void syncMapToNewKey(String[] arr){
        String redisKey = arr[1];
        Integer bucketQuantity = Integer.parseInt(arr[2]);
        long expireTime = -1;
        if (arr.length == 4) {
            expireTime = Long.parseLong(arr[3]);
        }

        Map<String, Object> objectMap = redisService.hGetAll(redisKey);
        if (expireTime != -1) {
            redisService.hSetAllBasedBucket(redisKey, bucketQuantity, objectMap, expireTime);
        } else {
            redisService.hSetAllBasedBucket(redisKey, bucketQuantity, objectMap);
        }
    }

    /**
     * 刷新 盘口状态
     */
    private void refreshMarketStatus() {
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria().andMatchOverEqualTo(YesNoEnum.N.value);
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(example);
        log.info("【RefreshStandardConfigMarketCategoryPlace】 处理开始条数:{}", standardMatchInfos.size());
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
            String cacheScoresKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_PLACE + standardMatchInfo.getId();
            Object obj = redisService.hGetAll(cacheScoresKey);
            if (ObjectUtil.isNotEmpty(obj)) {
                Map<String, ConfigMarketCategoryPlace> marketCategoryPlaceMap = (Map<String, ConfigMarketCategoryPlace>) obj;
                redisService.hSetAllBasedBucket(cacheScoresKey, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR, marketCategoryPlaceMap, RedisConfig.REDIS_WEEK_TIME);
            }
        }
        log.info("【RefreshStandardConfigMarketCategoryPlace】 处理完成:{}", standardMatchInfos.size());
    }
}