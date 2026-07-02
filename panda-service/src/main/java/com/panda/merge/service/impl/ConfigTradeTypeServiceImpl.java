package com.panda.merge.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.ConfigTradeTypeDao;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.mapper.ConfigTradeTypeMapper;
import com.panda.merge.model.ConfigTradeType;
import com.panda.merge.model.ConfigTradeTypeExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigTradeTypeService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/21 <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigTradeTypeServiceImpl implements ConfigTradeTypeService {

    @Autowired
    private ConfigTradeTypeMapper configTradeTypeMapper;

    @Autowired
    private ConfigTradeTypeDao configTradeTypeDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Override
    public ConfigTradeType getItemMatch(String standardMatchId) {
        String key = getMatchKey(Long.parseLong(standardMatchId));
        Object obj = redisService.get(key);
        if (obj != null){
            return (ConfigTradeType)obj;
        }
        return null;
    }

    @Override
    public Map<String,ConfigTradeType> getItemMatchDB(Long standardMatchId) {
        String key = getCategoryKey(standardMatchId);
        Map<String,ConfigTradeType> configTradeTypeMap = redisService.hGetAll(key);
        return configTradeTypeMap;
    }

    /**
     * 获取玩法级别的手自动操盘
     * @param standardMatchId
     * @param standardCategoryId
     * @return 为null也存缓存
     */
    @Override
    public ConfigTradeType getItemCategory(String standardMatchId, String standardCategoryId) {
        String key = getCategoryKey(Long.parseLong(standardMatchId));
        Object obj = redisService.hGet(key,standardCategoryId);
        if (obj != null){
            return (ConfigTradeType)obj;
        }
        return null;
    }

    /**
     * 从数据库一次查询赛事玩法级的手自动类型
     * @param standardMatchId
     * @return
     */
    @Override
    public Map<Long, Integer> getItemByMatchAndCategorys(String standardMatchId, Set<Long> marketCategoryIdSet){
        Map<Long, Integer> tradeTypeMap = new HashMap<>();
        String key = getCategoryKey(Long.parseLong(standardMatchId));
        Map<String,ConfigTradeType> configTradeTypeMap = redisService.hGetAll(key);
        if (MapUtils.isNotEmpty(configTradeTypeMap)){
            configTradeTypeMap.forEach((k,v)->{
                tradeTypeMap.put(Long.parseLong(k),v.getTradeType());
            });
            if (!CollectionUtils.isEmpty(marketCategoryIdSet))
            {
                marketCategoryIdSet.forEach(e->{
                    if (!tradeTypeMap.containsKey(e)){
                        tradeTypeMap.put(e, 0);
                    }
                });
            }
        }
        return tradeTypeMap;
    }


    @Override
    public ConfigTradeType createMatch(TradeMarketConfigDTO tradeMarketConfigDTO) {
        ConfigTradeType configTradeType = new ConfigTradeType();
        configTradeType.setId(UUIdUtils.getId());
        configTradeType.setLevel(Constant.TRADE_MARKET_CONFIG.LEVEL.MATCH);
        configTradeType.setStandardMatchId(tradeMarketConfigDTO.getTargetId());
        configTradeType.setTradeType(tradeMarketConfigDTO.getTradeType());
        configTradeType.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTradeType.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTradeType.setOperaterId(tradeMarketConfigDTO.getOperaterId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(Long.parseLong(tradeMarketConfigDTO.getTargetId()));
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        String key = getMatchKey(Long.parseLong(configTradeType.getStandardMatchId()));
        redisService.set(key,configTradeType,marketCacheTime(standardMatchInfo.getBeginTime()));
        return configTradeType;
    }

    @Override
    public ConfigTradeType updateMatch(ConfigTradeType configTradeType) {
        Long standardMatchInfoId = Long.parseLong(configTradeType.getStandardMatchId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        String key = getMatchKey(standardMatchInfoId);
        redisService.set(key,configTradeType,marketCacheTime(standardMatchInfo.getBeginTime()));
        return configTradeType;
    }

    @Override
    public ConfigTradeType createCategory(TradeMarketConfigDTO tradeMarketConfigDTO,String categoryId) {
        ConfigTradeType configTradeType = new ConfigTradeType();
        configTradeType.setId(UUIdUtils.getId());
        configTradeType.setLevel(tradeMarketConfigDTO.getLevel());
        configTradeType.setStandardMatchId(tradeMarketConfigDTO.getTargetId());
        configTradeType.setTradeType(tradeMarketConfigDTO.getTradeType());
        configTradeType.setStandardCategoryId(categoryId);
        configTradeType.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTradeType.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configTradeType.setOperaterId(tradeMarketConfigDTO.getOperaterId());
        Long standardMatchInfoId = Long.parseLong(configTradeType.getStandardMatchId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        String key = getCategoryKey(Long.parseLong(configTradeType.getStandardMatchId()));
        redisService.hSet(key,categoryId,configTradeType,marketCacheTime(standardMatchInfo.getBeginTime()));
        return configTradeType;
    }

    @Override
    public ConfigTradeType updateCategory(ConfigTradeType configTradeType) {
        Long standardMatchInfoId = Long.parseLong(configTradeType.getStandardMatchId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        String key = getCategoryKey(Long.parseLong(configTradeType.getStandardMatchId()));
        redisService.hSet(key,configTradeType.getStandardCategoryId(),configTradeType,marketCacheTime(standardMatchInfo.getBeginTime()));
        return configTradeType;
    }

    @Override
    public ConfigTradeType createCategory(ConfigTradeType configTradeType, String categoryId) {
        Long standardMatchInfoId = Long.parseLong(configTradeType.getStandardMatchId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        String key = getCategoryKey(Long.parseLong(configTradeType.getStandardMatchId()));
        redisService.hSet(key,categoryId,configTradeType,marketCacheTime(standardMatchInfo.getBeginTime()));
        return configTradeType;
    }


    @Override
    public int deleteCategoryByStandardMatchId(String standardMatchId){
        String key = getCategoryKey(Long.parseLong(standardMatchId));
        redisService.del(key);
        return 0;
    }

    @Override
    public void saveBatch(String linkId, Long standardMatchId, List<ConfigTradeType> dataList) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        Map<String, ConfigTradeType> map = dataList.stream().collect(Collectors.toMap(e->e.getStandardCategoryId(),Function.identity(),(o,n)->o));
        String key = getCategoryKey(standardMatchId);
        redisService.hSetAll(key,map,marketCacheTime(standardMatchInfo.getBeginTime()));
    }

    @Override
    public void updateByExample(Long standardMatchId, List<ConfigTradeType> updatedataList, ConfigTradeType updateConfigTradeType) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        if (CollectionUtils.isEmpty(updatedataList)) {
            return;
        }
        Map<String, ConfigTradeType> map = updatedataList.stream().collect(Collectors.toMap(e->e.getStandardCategoryId(),Function.identity(),(o,n)->o));
        String key = getCategoryKey(standardMatchId);
        redisService.hSetAll(key,map,marketCacheTime(standardMatchInfo.getBeginTime()));

    }

    private String getCategoryKey(Long standardMatchId){
        return DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigTradeType:1_" +standardMatchId);
    }
    private String getMatchKey(Long standardMatchId){
        return DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigTradeType:3_" +standardMatchId);
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
