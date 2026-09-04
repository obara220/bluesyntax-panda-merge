package com.panda.merge.job;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.cache.FootballCacheScores;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardMatchInfoExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.MatchFistMarketProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@JobHandler(value = "RefreshStandardMarketCache")
public class RefreshStandardMarketCache extends IJobHandler {
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchFistMarketProducer matchFistMarketProducer;

    @Override
    public ReturnT<String> execute(String parKey) {
        log.info("【RefreshStandardMarketCache】 处理开始");
        XxlJobLogger.log("RefreshStandardMarketCache】 处理开始");
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria().andMatchOverEqualTo(YesNoEnum.N.value);
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(example);
        log.info("【RefreshStandardMarketCache】 处理开始条数:{}", standardMatchInfos.size());
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(standardMatchInfo.getId());
            if (CollectionUtils.isEmpty(thirdMatchInfos)) {
                continue;
            }
            //赔率
            for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfos) {
                String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode();
                Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(marketKey);
                if (MapUtils.isNotEmpty(standardMarketMessageMap)) {
                    standardMarketMessageMap.forEach((k, v) -> {
                        String key = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + thirdMatchInfo.getDataSourceCode() + "_" + v.getMarketCategoryId();
                        log.info("RefreshStandardMarketCache,缓存key:{}", key);
                        redisService.hSet(DigestUtil.md5Hex(key), k, v, RedisConfig.REDIS_WEEK_TIME);
                    });
                }
            }
            // 玩法集 状态
            String setStatus = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_SET_STATUS + standardMatchInfo.getId();
            redisService.del(DigestUtil.md5Hex(setStatus));
            Map<String, Integer> categorySetStatusMap = redisService.hGetAll(setStatus);
            if (MapUtils.isNotEmpty(categorySetStatusMap)) {
                log.info("RefreshStandardMarketCache,缓存key:{},开始缓存玩法集状态", setStatus);
                redisService.hSetAll(DigestUtil.md5Hex(setStatus), categorySetStatusMap, RedisConfig.REDIS_WEEK_TIME.longValue());
            }
        }
        log.info("【RefreshStandardMarketCache】 处理结束");
        XxlJobLogger.log("RefreshStandardMarketCache】 处理结束");
        return ReturnT.SUCCESS;
    }
}
