//package com.panda.merge.task;
//
//import cn.hutool.core.date.DateUtil;
//import com.panda.merge.config.RedisService;
//import com.panda.merge.dto.TradeCategoryAutoDiffConfigItemDTO;
//import com.panda.merge.dto.TradeMarketAutoDiffConfigItemDTO;
//import com.panda.merge.dto.TradePlaceNumAutoDiffConfigItemDTO;
//import com.panda.merge.mapper.*;
//import com.panda.merge.model.*;
//import com.panda.merge.service.ConfigCategoryAutoDiffTradeService;
//import com.panda.merge.service.ConfigMarketAutoDiffTradeService;
//import com.panda.merge.service.ConfigMarketMarginGapService;
//import com.panda.merge.service.ConfigPlaceNumAutoDiffTradeService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * 临时任务类： 处理水差配置，将数据库的水差配置同步到redis缓存中
// *
// */
//@Slf4j
//@Component
//public class ConfigMarketDiffSyncTempTask {
//
//    @Autowired
//    private StandardMatchInfoMapper standardMatchInfoMapper;
//
//    @Autowired
//    ConfigCategoryAutoDiffTradeMapper configCategoryAutoDiffTradeMapper;
//
//    @Autowired
//    ConfigCategoryAutoDiffTradeService configCategoryAutoDiffTradeService;
//
//    @Autowired
//    private ConfigMarketAutoDiffTradeMapper configMarketAutoDiffTradeMapper;
//
//    @Autowired
//    ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;
//
//    @Autowired
//    ConfigMarketMarginGapService configMarketMarginGapService;
//
//    @Autowired
//    ConfigMarketMarginGapMapper configMarketMarginGapMapper;
//
//    @Autowired
//    ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;
//
//    @Autowired
//    ConfigPlacenumAutoDiffTradeMapper configPlacenumAutoDiffTradeMapper;
//
//    @Autowired
//    RedisService redisService;
//
//    private static String REDIS_KEY_GATEGORY = "REDIS_KEY_DIFF_GATEGORY";
//    private static String REDIS_KEY_MARKET = "REDIS_KEY_DIFF_MARKET";
//    private static String REDIS_KEY_MARGIN_GAP = "REDIS_KEY_DIFF_MARGIN_GAP";
//    private static String REDIS_KEY_PLACENUM = "REDIS_KEY_DIFF_PLACENUM";
//
//    /**
//     *  1.获取大于当前时间（未开赛）的所有标准赛事id
//     *  2.根据标准赛事id，获取对应水差配置
//     *  3.刷新水差配置到redis
//     *
//     */
//    @PostConstruct
//    public void initSyncDiffConfig() {
//        long startTime = System.currentTimeMillis();
//        String dateStr1 = "2023-12-28 22:33:23";
//        Date date1 = DateUtil.parse(dateStr1);
//        long time = date1.getTime();
//        if(startTime > time){
//            log.info("::initSyncDiffConfig::水差同步不需要执行！");
//            return;
//        }
//        log.info("::initSyncDiffConfig::同步水差配置开始11，startTime::{}",startTime);
//        List<Long> matchIds = new ArrayList<>();
//        StandardMatchInfoExample example = new StandardMatchInfoExample();
//        //赛事开始时间，大于当前时间的赛事
//        example.createCriteria().andBeginTimeGreaterThanOrEqualTo(startTime);
//        if(!redisService.hasKey(REDIS_KEY_GATEGORY)){
//            redisService.set(REDIS_KEY_GATEGORY,"配置同步开始11：startTime:"+startTime);
//            log.info("::initSyncDiffConfig::{}::开始执行水差配置！",REDIS_KEY_GATEGORY);
//            List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
//            matchIds = standardMatchInfoList.stream().map(StandardMatchInfo::getId).collect(Collectors.toList());
//            log.info("::initSyncDiffConfig::{}::获取到标准赛事条数：：{}",REDIS_KEY_GATEGORY,matchIds.size());
//            ConfigCategoryAutoDiffTradeExample configCategoryAutoDiffTradeExample = new ConfigCategoryAutoDiffTradeExample();
//            configCategoryAutoDiffTradeExample.createCriteria().andStandardMatchIdIn(matchIds);
//            List<ConfigCategoryAutoDiffTrade> categoryConfigList = configCategoryAutoDiffTradeMapper.selectByExample(configCategoryAutoDiffTradeExample);
//            log.info("::initSyncDiffConfig::{}::根据标准赛事id获取到配置条数：：{}",REDIS_KEY_GATEGORY,categoryConfigList.size());
//            TradeCategoryAutoDiffConfigItemDTO itemDTO = new TradeCategoryAutoDiffConfigItemDTO();
//            for(ConfigCategoryAutoDiffTrade categoryAutoDiffTrade : categoryConfigList){
//                itemDTO.setOddType(categoryAutoDiffTrade.getOddsType());
//                itemDTO.setDiffValue(categoryAutoDiffTrade.getDiffValue());
//                itemDTO.setMarketCategoryId(categoryAutoDiffTrade.getStandardCategoryId());
//                itemDTO.setChildStandardCategoryId(categoryAutoDiffTrade.getChildStandardCategoryId());
//                configCategoryAutoDiffTradeService.create(categoryAutoDiffTrade.getLinkId(),itemDTO,
//                        categoryAutoDiffTrade.getStandardMatchId(),categoryAutoDiffTrade.getOperaterId());
//            }
//            log.info("::initSyncDiffConfig::{}::配置同步缓存完成，耗时：：{}",REDIS_KEY_GATEGORY,System.currentTimeMillis()-startTime);
//            redisService.set(REDIS_KEY_GATEGORY,"配置同步结束：startTime:"+startTime+",结束时间："+System.currentTimeMillis()
//                    +", 耗时："+(System.currentTimeMillis()-startTime)+", 标准赛事条数："+matchIds.size()+", 配置条数："+categoryConfigList.size());
//        }
//
//        startTime = System.currentTimeMillis();
//        if(!redisService.hasKey(REDIS_KEY_MARKET)){
//            redisService.set(REDIS_KEY_MARKET,"配置同步开始11：startTime:"+startTime);
//            log.info("::initSyncDiffConfig::{}::开始执行水差配置！",REDIS_KEY_MARKET);
//            if(matchIds.size() == 0){
//                List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
//                matchIds = standardMatchInfoList.stream().map(StandardMatchInfo::getId).collect(Collectors.toList());
//            }
//            ConfigMarketAutoDiffTradeExample configMarketAutoDiffTradeExample = new ConfigMarketAutoDiffTradeExample();
//            configMarketAutoDiffTradeExample.createCriteria().andStandardMatchIdIn(matchIds);
//            List<ConfigMarketAutoDiffTrade> configMarketAutoDiffTrades = configMarketAutoDiffTradeMapper.selectByExample(configMarketAutoDiffTradeExample);
//            log.info("::initSyncDiffConfig::{}::根据标准赛事id获取到配置条数：：{}",REDIS_KEY_MARKET,configMarketAutoDiffTrades.size());
//            TradeMarketAutoDiffConfigItemDTO marketItemDTO = new TradeMarketAutoDiffConfigItemDTO();
//            for(ConfigMarketAutoDiffTrade diffTrade : configMarketAutoDiffTrades){
//                marketItemDTO.setMarketId(diffTrade.getStandardMarketId());
//                marketItemDTO.setDiffValue(diffTrade.getDiffValue());
//                marketItemDTO.setMarketCategoryId(diffTrade.getStandardCategoryId());
//                marketItemDTO.setOddType(diffTrade.getOddsType());
//                configMarketAutoDiffTradeService.create(diffTrade.getLinkId(),marketItemDTO,diffTrade.getStandardMatchId(),diffTrade.getOperaterId());
//            }
//            log.info("::initSyncDiffConfig::{}::配置同步缓存完成，耗时：：{}",REDIS_KEY_MARKET,System.currentTimeMillis()-startTime);
//            redisService.set(REDIS_KEY_MARKET,"配置同步结束：startTime:"+startTime+",结束时间："+System.currentTimeMillis()
//                    +", 耗时："+(System.currentTimeMillis()-startTime)+", 标准赛事条数："+matchIds.size()+", 配置条数："+configMarketAutoDiffTrades.size());
//        }
//
//        startTime = System.currentTimeMillis();
//        if(!redisService.hasKey(REDIS_KEY_PLACENUM)){
//            redisService.set(REDIS_KEY_PLACENUM,"配置同步开始：startTime:"+startTime);
//            log.info("::initSyncDiffConfig::{}::开始执行球头水差配置！",REDIS_KEY_PLACENUM);
//            if(matchIds.size() == 0){
//                List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
//                matchIds = standardMatchInfoList.stream().map(StandardMatchInfo::getId).collect(Collectors.toList());
//            }
//            ConfigPlacenumAutoDiffTradeExample configPlacenumAutoDiffTradeExample = new ConfigPlacenumAutoDiffTradeExample();
//            configPlacenumAutoDiffTradeExample.createCriteria().andStandardMatchIdIn(matchIds);
//            List<ConfigPlacenumAutoDiffTrade> list =  configPlacenumAutoDiffTradeMapper.selectByExample(configPlacenumAutoDiffTradeExample);
//            log.info("::initSyncDiffConfig::{}::根据标准赛事id获取到配置条数：：{}",REDIS_KEY_PLACENUM,list.size());
//            TradePlaceNumAutoDiffConfigItemDTO configItemDTO = new TradePlaceNumAutoDiffConfigItemDTO();
//            for(ConfigPlacenumAutoDiffTrade diffTrade : list ){
//                configItemDTO.setChildStandardCategoryId(diffTrade.getChildStandardCategoryId());
//                configItemDTO.setDiffValue(diffTrade.getDiffValue());
//                configItemDTO.setMarketCategoryId(diffTrade.getStandardCategoryId());
//                configItemDTO.setPlaceNum(diffTrade.getPlaceNum());
//                configItemDTO.setOddType(diffTrade.getOddsType());
//                configPlaceNumAutoDiffTradeService.create(diffTrade.getLinkId(),configItemDTO,diffTrade.getStandardMatchId(),diffTrade.getOperaterId());
//            }
//            log.info("::initSyncDiffConfig::{}::配置同步缓存完成，耗时：：{}", REDIS_KEY_PLACENUM, System.currentTimeMillis()-startTime);
//            redisService.set(REDIS_KEY_PLACENUM,"配置同步结束：startTime:"+startTime+",结束时间："+System.currentTimeMillis()
//                    +", 耗时："+(System.currentTimeMillis()-startTime)+", 标准赛事条数："+matchIds.size()+", 配置条数：" + list.size());
//
//        }
//
//        startTime = System.currentTimeMillis();
//        if(!redisService.hasKey(REDIS_KEY_MARGIN_GAP)){
//            redisService.set(REDIS_KEY_MARGIN_GAP,"配置同步开始：startTime:"+startTime);
//            log.info("::initSyncDiffConfig::{}::开始执行球头水差配置！",REDIS_KEY_MARGIN_GAP);
//            if(matchIds.size() == 0){
//                List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
//                matchIds = standardMatchInfoList.stream().map(StandardMatchInfo::getId).collect(Collectors.toList());
//            }
//            ConfigMarketMarginGapExample configMarketMarginExample = new ConfigMarketMarginGapExample();
//            configMarketMarginExample.createCriteria().andMatchIdIn(matchIds);
//            List<ConfigMarketMarginGap> configMarketMarginGaps = configMarketMarginGapMapper.selectByExample(configMarketMarginExample);
//            log.info("::initSyncDiffConfig::{}::根据标准赛事id获取到配置条数：：{}",REDIS_KEY_MARGIN_GAP,configMarketMarginGaps.size());
//            if(configMarketMarginGaps != null && configMarketMarginGaps.size() > 0){
//                Map<Long, List<ConfigMarketMarginGap>> collect = configMarketMarginGaps.stream().collect(Collectors.groupingBy(ConfigMarketMarginGap::getMatchId));
//                collect.forEach((key,value) -> {
//                    configMarketMarginGapService.insertList("sync",key,value);
//                });
//            }
//            log.info("::initSyncDiffConfig::{}::配置同步缓存完成，耗时：：{}",REDIS_KEY_MARGIN_GAP,System.currentTimeMillis()-startTime);
//            redisService.set(REDIS_KEY_MARGIN_GAP,"配置同步结束：startTime:"+startTime+",结束时间："+System.currentTimeMillis()
//                    +", 耗时："+(System.currentTimeMillis()-startTime)+", 标准赛事条数："+matchIds.size()+", 配置条数："+configMarketMarginGaps.size());
//        }
//
//
//    }
//}
