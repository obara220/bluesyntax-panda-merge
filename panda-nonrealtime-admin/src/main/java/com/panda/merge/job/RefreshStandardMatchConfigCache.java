package com.panda.merge.job;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.service.StandardMatchInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@JobHandler(value = "RefreshStandardMatchConfigCache")
public class RefreshStandardMatchConfigCache  extends IJobHandler {
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ConfigMarketTradeItemMapper configMarketTradeItemMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private ConfigTradeTypeMapper configTradeTypeMapper;
    @Autowired
    private ConfigMarketCategoryMarginMapper configMarketCategoryMarginMapper;
    @Autowired
    private ConfigMarketDisplayTradeMapper configMarketDisplayTradeMapper;
    @Autowired
    private ConfigMarketStatusTradeMapper configMarketStatusTradeMapper;
    @Autowired
    private RedisService redisService;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        //log.info("【RefreshStandardMatchConfigCache】 处理开始");
        XxlJobLogger.log("RefreshStandardMatchConfigCache】 处理开始");
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria().andMatchOverEqualTo(YesNoEnum.N.value);
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(example);
        //log.info("【RefreshStandardMatchConfigCache】 处理开始条数:{}", standardMatchInfos.size());
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
            if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
                standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
            }
        }
        //log.info("【RefreshStandardMatchConfigCache】 ConfigTradeType 处理开始");
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
            ConfigTradeTypeExample configTradeTypeExample = new ConfigTradeTypeExample();
            configTradeTypeExample.createCriteria()
                    .andStandardMatchIdEqualTo(standardMatchInfo.getId().toString());
            List<ConfigTradeType> configTradeTypes = configTradeTypeMapper.selectByExample(configTradeTypeExample);
            if (CollectionUtils.isNotEmpty(configTradeTypes)){
                List<ConfigTradeType> match = configTradeTypes.stream().filter(e->e.getLevel()==Constant.TRADE_MARKET_CONFIG.LEVEL.MATCH).collect(Collectors.toList());
                match.parallelStream().forEach(e->{
                    String key = getMatchKey(Long.parseLong(e.getStandardMatchId()));
                    redisService.set(key,e,marketCacheTime(standardMatchInfo.getBeginTime()));
                });
                List<ConfigTradeType> category = configTradeTypes.stream().filter(e->e.getLevel()==Constant.TRADE_MARKET_CONFIG.LEVEL.MARKET_CATEGORY).collect(Collectors.toList());
                Map<String, ConfigTradeType> map = category.stream().collect(Collectors.toMap(e->e.getStandardCategoryId(), Function.identity(),(o, n)->o));
                String key = getCategoryKey(standardMatchInfo.getId());
                redisService.hSetAll(key,map,marketCacheTime(standardMatchInfo.getBeginTime()));
            }
        }
        //log.info("【RefreshStandardMatchConfigCache】 ConfigTradeType 处理结束");

        //log.info("【RefreshStandardMatchConfigCache】 ConfigMarketTradeItem 处理开始");
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
            ConfigMarketTradeItemExample configMarketTradeItemExample = new ConfigMarketTradeItemExample();
            configMarketTradeItemExample.createCriteria().andMatchIdEqualTo(standardMatchInfo.getId());
            List<ConfigMarketTradeItem> configMarketTradeItemList = configMarketTradeItemMapper.selectByExample(configMarketTradeItemExample);
            if (CollectionUtils.isNotEmpty(configMarketTradeItemList)){
                Map<String,ConfigMarketTradeItem> map = configMarketTradeItemList.stream().collect(Collectors.toMap(configMarketTradeItem->configMarketTradeItem.getMarketCategoryId()+"-"+configMarketTradeItem.getChildStandardCategoryId()+"-"+configMarketTradeItem.getPlaceNum(), Function.identity(),(o, n)->o));
                String key = DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketTradeItem:" +standardMatchInfo.getId());
                redisService.hSetAll(key,map,marketCacheTime(standardMatchInfo.getBeginTime()));
            }
        }
        //log.info("【RefreshStandardMatchConfigCache】 ConfigMarketTradeItem 处理结束");

        //log.info("【RefreshStandardMatchConfigCache】 ConfigMarketCategoryMargin 处理开始");
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos){
            //根据赛事ID查出margin配置
            ConfigMarketCategoryMarginExample configMarketCategoryMarginExample = new ConfigMarketCategoryMarginExample();
            configMarketCategoryMarginExample.createCriteria()
                    .andStandardMatchInfoIdEqualTo(standardMatchInfo.getId());
            List<ConfigMarketCategoryMargin> configMarketCategoryMargins = configMarketCategoryMarginMapper.selectByExample(configMarketCategoryMarginExample);
            Map<String,ConfigMarketCategoryMargin> configThree = configMarketCategoryMargins.stream().filter(e->e.getOddsType()!=null).collect(Collectors.toMap(p->p.getStandardCategoryId().toString() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum() + '-' + p.getOddsType(), Function.identity(), (v1, v2)->v1));
            Map<String,ConfigMarketCategoryMargin> configTwo = configMarketCategoryMargins.stream().collect(Collectors.toMap(p->p.getStandardCategoryId().toString() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum(), Function.identity(), (v1, v2)->v1));
            Map<String,String> keyMap = getkeyMap(standardMatchInfo.getId());
            redisService.hSetAll(keyMap.get("ConfigMarketCategoryMargin_Tree"),configThree,marketCacheTime(standardMatchInfo.getBeginTime()));
            redisService.hSetAll(keyMap.get("ConfigMarketCategoryMargin_Two"),configTwo,marketCacheTime(standardMatchInfo.getBeginTime()));
        }
        //log.info("【RefreshStandardMatchConfigCache】 ConfigMarketCategoryMargin 处理结束");

        //log.info("【RefreshStandardMatchConfigCache】 ConfigMarketDisplayTrade 处理开始");
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos){
            ConfigMarketDisplayTradeExample configMarketDisplayTradeExample = new ConfigMarketDisplayTradeExample();
            configMarketDisplayTradeExample.createCriteria().andStandardMatchIdEqualTo(standardMatchInfo.getId());
            List<ConfigMarketDisplayTrade> configMarketDisplayTrades = configMarketDisplayTradeMapper.selectByExample(configMarketDisplayTradeExample);
            configMarketDisplayTrades.stream().forEach(e->{
                String key = getConfigMarketDisplayTradekey(e.getStandardMatchId());
                redisService.set(key,e,marketCacheTime(standardMatchInfo.getBeginTime()));
            });
        }
        //log.info("【RefreshStandardMatchConfigCache】 ConfigMarketDisplayTrade 处理结束");

        //log.info("【RefreshStandardMatchConfigCache】 ConfigMarketStatusTrade 处理开始");
        for (StandardMatchInfo standardMatchInfo : standardMatchInfos){
            ConfigMarketStatusTradeExample configMarketStatusTradeExample = new ConfigMarketStatusTradeExample();
            configMarketStatusTradeExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchInfo.getId());
            List<ConfigMarketStatusTrade> configMarketStatusTrades = configMarketStatusTradeMapper.selectByExample(configMarketStatusTradeExample);
            Map<String,ConfigMarketStatusTrade> configMarketStatusTradeMap = configMarketStatusTrades.stream().collect(Collectors.toMap(e->e.getRelationMarketId().toString()+"-"+e.getMarketStatus(),Function.identity(),(o,v)->v));
            String key = getConfigMarketStatusTradeKey(standardMatchInfo.getId());
            redisService.hSetAll(key,configMarketStatusTradeMap,marketCacheTime(standardMatchInfo.getBeginTime()));
        }
        //log.info("【RefreshStandardMatchConfigCache】 ConfigMarketStatusTrade 处理结束");


        //log.info("【RefreshStandardMatchConfigCache】 处理结束");
        XxlJobLogger.log("RefreshStandardMatchConfigCache】 处理结束");

        return ReturnT.SUCCESS;
    }
    private String getConfigMarketStatusTradeKey(Long standardMatchInfoId){
        return DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketStatusTrade:" +standardMatchInfoId);
    }
    private String getConfigMarketDisplayTradekey(Long standardMatchId){
        return DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketDisplayTrade:" +standardMatchId);
    }
    private String getCategoryKey(Long standardMatchId){
        return DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigTradeType:1_" +standardMatchId);
    }
    private String getMatchKey(Long standardMatchId){
        return DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigTradeType:3_" +standardMatchId);
    }
    private Map<String,String> getkeyMap(Long standardMatchId){
        Map<String,String> map = new HashMap<>();
        map.put("ConfigMarketCategoryMargin_Tree",DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryMargin_Tree:" + standardMatchId));
        map.put("ConfigMarketCategoryMargin_Two",DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryMargin_Two:" + standardMatchId));
        return map;
    }
    private Long marketCacheTime(Long beginTime) {
        if (beginTime == null || beginTime == 0) {
            return RedisConfig.REDIS_DEFAULT_TIME.longValue();
        }
        //获取剩余开赛时间 =  开赛时间-当前时间
        Long cacheTime = (beginTime - Calendar.getInstance().getTimeInMillis());
        if (cacheTime <= 0) {
            return RedisConfig.REDIS_DEFAULT_TIME.longValue();
        }
        //redis过期时间为秒 = 剩余开赛时间 + 2天时间 ，为redis过期时间
        return (cacheTime / 1000) + (2L * RedisConfig.REDIS_DEFAULT_TIME);
    }
}
