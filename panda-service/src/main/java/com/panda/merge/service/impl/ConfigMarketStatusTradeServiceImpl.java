package com.panda.merge.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.ConfigMarketStatusTradeMapper;
import com.panda.merge.model.ConfigMarketStatusTrade;
import com.panda.merge.model.ConfigMarketStatusTradeExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigMarketStatusTradeService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName ConfigMarketStatusServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2020/11/4 14:08
 **/
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketStatusTradeServiceImpl implements ConfigMarketStatusTradeService {
    @Autowired
    private ConfigMarketStatusTradeMapper configMarketStatusTradeMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BaseProcessor baseProcessor;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Override
    public ConfigMarketStatusTrade getItemOne(Long standardMatchInfoId,Long relationMarketId,int marketType) {
        /*ConfigMarketStatusTradeExample configMarketStatusTradeExample = new ConfigMarketStatusTradeExample();
        configMarketStatusTradeExample.createCriteria().andRelationMarketIdEqualTo(relationMarketId).andMarketTypeEqualTo(marketType);
        List<ConfigMarketStatusTrade> configMarketStatusTrades = configMarketStatusTradeMapper.selectByExample(configMarketStatusTradeExample);
        if (CollectionUtils.isEmpty(configMarketStatusTrades))
        {
            return null;
        }*/
        String key = getConfigMarketStatusTradeKey(standardMatchInfoId);
        return (ConfigMarketStatusTrade) redisService.hGet(key,relationMarketId.toString()+"-"+marketType);
    }

    @Override
    public ConfigMarketStatusTrade create(ConfigMarketStatusTrade configMarketStatusTrade) {
        String key = getConfigMarketStatusTradeKey(configMarketStatusTrade.getStandardMatchInfoId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketStatusTrade.getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.hSet(key,configMarketStatusTrade.getRelationMarketId().toString()+"-"+configMarketStatusTrade.getMarketType(),configMarketStatusTrade,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return configMarketStatusTrade;
    }

    @Override
    public ConfigMarketStatusTrade update(ConfigMarketStatusTrade configMarketStatusTrade) {
        String key = getConfigMarketStatusTradeKey(configMarketStatusTrade.getStandardMatchInfoId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketStatusTrade.getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.hSet(key,configMarketStatusTrade.getRelationMarketId().toString()+"-"+configMarketStatusTrade.getMarketType(),configMarketStatusTrade,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return configMarketStatusTrade;
    }

    @Override
    public List<ConfigMarketStatusTrade> getItemList(Long standardMatchInfoId,int marketType, Set<Long> marketCategoryIdSet) {
        String key = getConfigMarketStatusTradeKey(standardMatchInfoId);
        Map<String,ConfigMarketStatusTrade> configMarketStatusTradeMap = redisService.hGetAll(key);
        List<ConfigMarketStatusTrade> configMarketStatusTrades = new ArrayList<>();
        configMarketStatusTradeMap.forEach((k,v)->{
            if (v.getMarketType() == marketType && marketCategoryIdSet.contains(v.getStandardCategoryId())){
                configMarketStatusTrades.add(v);
            }
        });
        return configMarketStatusTrades;
    }

    private String getConfigMarketStatusTradeKey(Long standardMatchInfoId){
        return DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketStatusTrade:" +standardMatchInfoId);
    }
}
