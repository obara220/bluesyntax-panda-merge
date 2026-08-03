package com.panda.merge.job;

import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.mapper.ThirdSportMarketMapper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.MatchFistMarketProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.ThirdSportMarketService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Component
@JobHandler(value = "RefreshThirdMarketCache")
public class RefreshThirdMarketCache extends IJobHandler {
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private ThirdSportMarketService thirdSportMarketService;
    @Autowired
    private ThirdSportMarketMapper thirdSportMarketMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchFistMarketProducer matchFistMarketProducer;

    @Override
    public ReturnT<String> execute(String parKey) {

        //log.info("【RefreshThirdMarketCache】 处理开始");
        XxlJobLogger.log("RefreshThirdMarketCache】 处理开始");
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria().andMatchOverEqualTo(YesNoEnum.N.value);
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(example);
        //log.info("【RefreshThirdMarketCache】 处理开始条数:{}", standardMatchInfos.size());
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(standardMatchInfo.getId());
            if (CollectionUtils.isEmpty(thirdMatchInfos)) {
                continue;
            }
            for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfos) {
                ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
                thirdSportMarketExample.createCriteria().andMatchIdEqualTo(thirdMatchInfo.getId());
                List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
                //log.info("【RefreshThirdMarketCache】 三方赛事:{}:处理开始条数:{}", thirdMatchInfo.getId(), thirdSportMarkets.size());
                thirdSportMarkets.forEach(thirdSportMarket -> {
                    String key = RedisConfig.REDIS_KEY_DATABASE + "::ThirdSportMarket:" + thirdSportMarket.getMatchId() + "-" + thirdSportMarket.getThirdMarketSourceId();
                    redisService.del(key);
                });
            }
        }
        //log.info("【RefreshThirdMarketCache】 处理结束");
        XxlJobLogger.log("RefreshThirdMarketCache】 处理结束");

        return ReturnT.SUCCESS;
    }
}
