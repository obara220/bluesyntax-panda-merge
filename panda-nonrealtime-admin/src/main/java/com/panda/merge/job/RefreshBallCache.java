package com.panda.merge.job;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@JobHandler(value = "RefreshBallCache")
public class RefreshBallCache extends IJobHandler {
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchFistMarketProducer matchFistMarketProducer;

    @Override
    public ReturnT<String> execute(String parKey) {
        //log.info("【RefreshBallCache 根据传入key值获取缓存】 处理开始");
        XxlJobLogger.log("RefreshBallCache 根据传入key值获取缓存】 处理开始");
        //找出支持A01的三方赛事
        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria().andSportIdEqualTo(StandardSportTypeEnum.FootBall.getCode()).andDataSourceCodeEqualTo(DataSourceCodeEnum.AO.getCode());
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        //log.info("【RefreshBallCache 根据传入key值获取缓存】 处理开始条数:{}", thirdMatchInfoList.size());
        XxlJobLogger.log("RefreshBallCache 根据传入key值获取缓存】 处理开始条数:{}", thirdMatchInfoList.size());
        if (CollectionUtils.isEmpty(thirdMatchInfoList)) {
            return ReturnT.SUCCESS;
        }
        for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfoList) {
            if (null == thirdMatchInfo.getReferenceId() || 0L == thirdMatchInfo.getReferenceId()) {
                continue;
            }
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(thirdMatchInfo.getReferenceId());
            if (null == standardMatchInfo || YesNoEnum.Y.value.equals(standardMatchInfo.getMatchOver())) {
                continue;
            }
            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(standardMatchInfo.getId());
            if (CollectionUtils.isEmpty(thirdMatchInfos)) {
                continue;
            }
            for (ThirdMatchInfo thirdMatch : thirdMatchInfos) {
                String linkId = IdWorker.getId() + "_send";
                if (thirdMatch.getSportId().equals(StandardSportTypeEnum.FootBall.getCode())) {
                    //获取缓存最新球头盘口 Map<dataSourceCode,Map<标准玩法ID，球头盘口数据>>
                    String key = Constant.REDIS_KEY.THIRD_MARKET_HEAD + standardMatchInfo.getId() + "_" + thirdMatch.getDataSourceCode();
                    Map<String, ThirdMarketDTO> thirdMarketHeadCacheMap = redisService.hGetAll(key);
                    if (MapUtils.isEmpty(thirdMarketHeadCacheMap)) {
                        continue;
                    }
                    Map<Long, ThirdMarketDTO> sendThirdMarketHeadCacheNewMap = new HashMap<>();
                    Map<String, ThirdMarketDTO> sendThirdMarketDTOCacheMap = redisService.hGetAll(key);
                    for (String ThirdMarketDTOKey : sendThirdMarketDTOCacheMap.keySet()) {
                        sendThirdMarketHeadCacheNewMap.put(Long.valueOf(ThirdMarketDTOKey), sendThirdMarketDTOCacheMap.get(ThirdMarketDTOKey));
                    }
                    matchFistMarketProducer.sendThirdBallHeadMarketAoAsync(linkId, thirdMatchInfo, standardMatchInfo, sendThirdMarketHeadCacheNewMap, thirdMatch.getDataSourceCode(), System.currentTimeMillis());

                } else if (thirdMatch.getSportId().equals(StandardSportTypeEnum.Basketball.getCode())) {
                    String key = Constant.REDIS_KEY.THIRD_BASKETBALL_MARKET_HEAD + standardMatchInfo.getId() + "_" + thirdMatch.getDataSourceCode();
                    Map<String, List<ThirdMarketDTO>> thirdMarketHeadCacheMap = redisService.hGetAll(key);
                    if (MapUtils.isEmpty(thirdMarketHeadCacheMap)) {
                        continue;
                    }
                    Map<Long, List<ThirdMarketDTO>> sendThirdMarketHeadCacheNewMap = new HashMap<>();
                    for (String ThirdMarketDTOKey : thirdMarketHeadCacheMap.keySet()) {
                        sendThirdMarketHeadCacheNewMap.put(Long.valueOf(ThirdMarketDTOKey), thirdMarketHeadCacheMap.get(ThirdMarketDTOKey));
                    }
                    matchFistMarketProducer.sendBasketballThirdBallHeadMarketAoAsync(linkId, thirdMatchInfo, standardMatchInfo, sendThirdMarketHeadCacheNewMap, thirdMatch.getDataSourceCode(), System.currentTimeMillis());
                }
            }
        }
        //log.info("【RefreshBallCache 根据传入key值获取缓存】 处理结束");
        XxlJobLogger.log("RefreshBallCache 根据传入key值获取缓存】 处理结束");
        return ReturnT.SUCCESS;
    }
}
