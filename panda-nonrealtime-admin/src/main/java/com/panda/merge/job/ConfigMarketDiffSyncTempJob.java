package com.panda.merge.job;

import cn.hutool.core.util.ObjectUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.TradeCategoryAutoDiffConfigItemDTO;
import com.panda.merge.dto.TradeMarketAutoDiffConfigItemDTO;
import com.panda.merge.dto.TradePlaceNumAutoDiffConfigItemDTO;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.service.ConfigCategoryAutoDiffTradeService;
import com.panda.merge.service.ConfigMarketAutoDiffTradeService;
import com.panda.merge.service.ConfigMarketMarginGapService;
import com.panda.merge.service.ConfigPlaceNumAutoDiffTradeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@JobHandler(value = "ConfigMarketDiffSyncTempJob")
public class ConfigMarketDiffSyncTempJob extends IJobHandler {
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    ConfigCategoryAutoDiffTradeMapper configCategoryAutoDiffTradeMapper;

    @Autowired
    ConfigCategoryAutoDiffTradeService configCategoryAutoDiffTradeService;

    @Autowired
    private ConfigMarketAutoDiffTradeMapper configMarketAutoDiffTradeMapper;

    @Autowired
    ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;

    @Autowired
    ConfigMarketMarginGapService configMarketMarginGapService;

    @Autowired
    ConfigMarketMarginGapMapper configMarketMarginGapMapper;

    @Autowired
    ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;

    @Autowired
    ConfigPlacenumAutoDiffTradeMapper configPlacenumAutoDiffTradeMapper;

    @Autowired
    RedisService redisService;

    private static String REDIS_KEY_GATEGORY = "REDIS_KEY_DIFF_GATEGORY";
    private static String REDIS_KEY_MARKET = "REDIS_KEY_DIFF_MARKET";
    private static String REDIS_KEY_MARGIN_GAP = "REDIS_KEY_DIFF_MARGIN_GAP";
    private static String REDIS_KEY_PLACENUM = "REDIS_KEY_DIFF_PLACENUM";

    @Override
    public ReturnT<String> execute(String params) {
        long startTime = System.currentTimeMillis();
        String linkId = IdWorker.getId() + "_ConfigMarketDiffSyncTempJob";
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria().andMatchOverEqualTo(YesNoEnum.N.value);
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(example);
        //log.info("::{}::【initSyncDiffConfig】:{} 处理开始条数:{}", linkId, params, standardMatchInfos.size());
        try {
            List<Long> matchIds = standardMatchInfos.stream().map(StandardMatchInfo::getId).collect(Collectors.toList());
            //log.info("::{}::initSyncDiffConfig::{}::获取到标准赛事条数：：{}", linkId, REDIS_KEY_GATEGORY, matchIds.size());
            ConfigCategoryAutoDiffTradeExample configCategoryAutoDiffTradeExample = new ConfigCategoryAutoDiffTradeExample();
            configCategoryAutoDiffTradeExample.createCriteria().andStandardMatchIdIn(matchIds);
            List<ConfigCategoryAutoDiffTrade> categoryConfigList = configCategoryAutoDiffTradeMapper.selectByExample(configCategoryAutoDiffTradeExample);
            //log.info("::{}::initSyncDiffConfig::{}::根据标准赛事id获取到配置条数：：{}", linkId, REDIS_KEY_GATEGORY, categoryConfigList.size());
            TradeCategoryAutoDiffConfigItemDTO itemDTO = new TradeCategoryAutoDiffConfigItemDTO();
            for (ConfigCategoryAutoDiffTrade categoryAutoDiffTrade : categoryConfigList) {
                itemDTO.setOddType(categoryAutoDiffTrade.getOddsType());
                itemDTO.setDiffValue(categoryAutoDiffTrade.getDiffValue());
                itemDTO.setMarketCategoryId(categoryAutoDiffTrade.getStandardCategoryId());
                itemDTO.setChildStandardCategoryId(categoryAutoDiffTrade.getChildStandardCategoryId());
                configCategoryAutoDiffTradeService.create(categoryAutoDiffTrade.getLinkId(), itemDTO, categoryAutoDiffTrade.getStandardMatchId(), categoryAutoDiffTrade.getOperaterId());
            }
            //log.info("::{}::initSyncDiffConfig::{}::配置同步缓存完成，耗时：：{}", linkId, REDIS_KEY_GATEGORY, System.currentTimeMillis() - startTime);
            redisService.set(REDIS_KEY_GATEGORY, "配置同步结束：startTime:" + startTime + ",结束时间：" + System.currentTimeMillis() + ", 耗时：" + (System.currentTimeMillis() - startTime) + ", 标准赛事条数：" + matchIds.size() + ", 配置条数：" + categoryConfigList.size());

            //log.info("::{}::initSyncDiffConfig::{}::开始执行水差配置！", linkId, REDIS_KEY_MARKET);
            ConfigMarketAutoDiffTradeExample configMarketAutoDiffTradeExample = new ConfigMarketAutoDiffTradeExample();
            configMarketAutoDiffTradeExample.createCriteria().andStandardMatchIdIn(matchIds);
            List<ConfigMarketAutoDiffTrade> configMarketAutoDiffTrades = configMarketAutoDiffTradeMapper.selectByExample(configMarketAutoDiffTradeExample);
            //log.info("::{}::initSyncDiffConfig::{}::根据标准赛事id获取到配置条数：：{}", linkId, REDIS_KEY_MARKET, configMarketAutoDiffTrades.size());
            for (ConfigMarketAutoDiffTrade diffTrade : configMarketAutoDiffTrades) {
                TradeMarketAutoDiffConfigItemDTO marketItemDTO = new TradeMarketAutoDiffConfigItemDTO();
                marketItemDTO.setMarketId(diffTrade.getStandardMarketId());
                marketItemDTO.setDiffValue(diffTrade.getDiffValue());
                marketItemDTO.setMarketCategoryId(diffTrade.getStandardCategoryId());
                marketItemDTO.setOddType(diffTrade.getOddsType());
                configMarketAutoDiffTradeService.create(diffTrade.getLinkId(), marketItemDTO, diffTrade.getStandardMatchId(), diffTrade.getOperaterId());
            }
            //log.info("::{}::initSyncDiffConfig::{}::配置同步缓存完成，耗时：：{}", linkId, REDIS_KEY_MARKET, System.currentTimeMillis() - startTime);

            //log.info("::{}::initSyncDiffConfig::{}::开始执行球头水差配置！", linkId, REDIS_KEY_PLACENUM);
            ConfigPlacenumAutoDiffTradeExample configPlacenumAutoDiffTradeExample = new ConfigPlacenumAutoDiffTradeExample();
            configPlacenumAutoDiffTradeExample.createCriteria().andStandardMatchIdIn(matchIds);
            List<ConfigPlacenumAutoDiffTrade> list = configPlacenumAutoDiffTradeMapper.selectByExample(configPlacenumAutoDiffTradeExample);
            //log.info("::{}::initSyncDiffConfig::{}::根据标准赛事id获取到配置条数：：{}", linkId, REDIS_KEY_PLACENUM, list.size());
            for (ConfigPlacenumAutoDiffTrade diffTrade : list) {
                TradePlaceNumAutoDiffConfigItemDTO configItemDTO = new TradePlaceNumAutoDiffConfigItemDTO();
                configItemDTO.setChildStandardCategoryId(diffTrade.getChildStandardCategoryId());
                configItemDTO.setDiffValue(diffTrade.getDiffValue());
                configItemDTO.setMarketCategoryId(diffTrade.getStandardCategoryId());
                configItemDTO.setPlaceNum(diffTrade.getPlaceNum());
                configItemDTO.setOddType(diffTrade.getOddsType());
                configPlaceNumAutoDiffTradeService.create(diffTrade.getLinkId(), configItemDTO, diffTrade.getStandardMatchId(), diffTrade.getOperaterId());
            }
            //log.info("::{}::initSyncDiffConfig::{}::配置同步缓存完成，耗时：：{}", linkId, REDIS_KEY_PLACENUM, System.currentTimeMillis() - startTime);

            //log.info("::{}::initSyncDiffConfig::{}::开始执行球头水差配置！", linkId, REDIS_KEY_MARGIN_GAP);
            ConfigMarketMarginGapExample configMarketMarginExample = new ConfigMarketMarginGapExample();
            configMarketMarginExample.createCriteria().andMatchIdIn(matchIds);
            List<ConfigMarketMarginGap> configMarketMarginGaps = configMarketMarginGapMapper.selectByExample(configMarketMarginExample);
            //log.info("::{}::initSyncDiffConfig::{}::根据标准赛事id获取到配置条数：：{}", linkId, REDIS_KEY_MARGIN_GAP, configMarketMarginGaps.size());
            if (!CollectionUtils.isEmpty(configMarketMarginGaps) && configMarketMarginGaps.size() > 0) {
                Map<Long, List<ConfigMarketMarginGap>> collect = configMarketMarginGaps.stream().collect(Collectors.groupingBy(ConfigMarketMarginGap::getMatchId));
                collect.forEach((key, value) -> {
                    configMarketMarginGapService.insertList("sync", key, value);
                });
            }
            //log.info("::{}::initSyncDiffConfig::盘口状态配置！", linkId);
            for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
                String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_PLACE + standardMatchInfo.getId();
                try {
                    Object obj = redisService.hGetAllBasedBucketOld(redisKey, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                    if (ObjectUtil.isNotEmpty(obj)) {
                        Map<String, ConfigMarketCategoryPlace> marketCategoryPlaceMap = (Map<String, ConfigMarketCategoryPlace>) obj;
                        redisService.hSetAllBasedBucket(redisKey, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR, marketCategoryPlaceMap, RedisConfig.REDIS_WEEK_TIME);
                    }
                } catch (Exception e) {
                    log.error(linkId + "::" + redisKey + "：【initSyncDiffConfig】 出现异常忽略:", e);
                }
            }
            //log.info("::{}::initSyncDiffConfig::盘口状态配置处理完成！", linkId);

        } catch (Exception e) {
            log.error("::" + linkId + "::【initSyncDiffConfig 配置同步缓存失败】 Exception:", e);
            XxlJobLogger.log(linkId + "::【GetRedisCacheJob 根据传入key值获取缓存】 Exception:" + e.getMessage());
        }
        //log.info("::{}::initSyncDiffConfig::{}::配置同步缓存完成，耗时：：{}", linkId, REDIS_KEY_MARGIN_GAP, System.currentTimeMillis() - startTime);
        //log.info("::{}::【initSyncDiffConfig】 处理结束", linkId);
        XxlJobLogger.log(linkId + "::initSyncDiffConfig】 处理结束");
        return ReturnT.SUCCESS;
    }
}
