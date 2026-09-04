package com.panda.merge.odds;

import com.panda.merge.cache.CacheConstant;
import com.panda.merge.common.enums.MarketTypeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.LocalCacheRefreshMessage;
import com.panda.merge.dto.odds.CategoryDataSourceHighPriority;
import com.panda.merge.dto.odds.DataSourceAutoSwitchConfig;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.odds.cache.CacheService;
import com.panda.merge.odds.cache.LocalCacheRefreshProducer;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.panda.merge.odds.constants.CacheConstant.TOPIC_NONREALTIME_CACHE_UPDATE;

/**
 * ThirdMarketMonitorConfig
 *
 * @description: 三方盘口监视器配置
 * @date: 4/28/2025
 **/
@Component
@Slf4j
@CacheConfig(cacheNames = CacheConstant.CACHE_AUTO_SWITCH_CONFIG, cacheManager = "localCacheExpireManager")
public class AutoSwitchConfigService implements CacheService, BeanNameAware {

    @Autowired
    private RedisService redisService;

    @Autowired
    private LocalCacheRefreshProducer localCacheRefreshProducer;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Resource(name = "localCacheExpireManager")
    private CacheManager localCacheExpireManager;

    @Autowired
    private AutoSwitchConfigService self;

    private String beanName;


    public Integer getExpireSeconds(int marketType, Long matchId) {
        DataSourceAutoSwitchConfig oc = self.getConfig(matchId, marketType);
        Integer validSecond = oc.getValidSecond();
        if (validSecond == null || validSecond <= 0) {
            return 0;
        }
        return validSecond;
    }

    public List<String> getDataSourceList(Long matchId, int marketType) {
        return self.getConfig(matchId,marketType).getDataSourceList();
    }

    private static String getKey(Long matchId, int marketType) {
        return matchId + "_" + marketType;
    }

    public void update(DataSourceAutoSwitchConfig config, String linkId) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(config.getMatchId());
        if (config.getMarketType() == MarketTypeEnum.PREMATCH.getCode()
                && Objects.equals(standardMatchInfo.getSportId(), StandardSportTypeEnum.FootBall.code)){
            //早盘只用风控的状态
            if (config.getSource() == 0){
  /*              DataSourceAutoSwitchConfig tempConfig = getConfig(config.getMatchId(), config.getMarketType());
                if (tempConfig == null || tempConfig.getMatchId() == null){*/
                    redisService.set(getRedisKey(config.getMatchId(), config.getMarketType()), config,
                            getConfigExpireTime(config, linkId),
                            TimeUnit.MILLISECONDS);
/*                }else{
                    tempConfig.setChangeStatus(config.getChangeStatus());
                    redisService.set(getRedisKey(config.getMatchId(), config.getMarketType()), tempConfig,
                            getConfigExpireTime(config, linkId),
                            TimeUnit.MILLISECONDS);
                }*/
            }else if (config.getSource() == 1){
                DataSourceAutoSwitchConfig tempConfig = getConfig(config.getMatchId(), config.getMarketType());
                if (tempConfig == null || tempConfig.getMatchId() == null){
                    config.setChangeStatus(1);
                }else{
                    config.setChangeStatus(tempConfig.getChangeStatus());
                }
                redisService.set(getRedisKey(config.getMatchId(), config.getMarketType()), config,
                        getConfigExpireTime(config, linkId),
                        TimeUnit.MILLISECONDS);
            }
        }else{
            redisService.set(getRedisKey(config.getMatchId(), config.getMarketType()), config,
                    getConfigExpireTime(config, linkId),
                    TimeUnit.MILLISECONDS);
        }

        LocalCacheRefreshMessage message = new LocalCacheRefreshMessage(linkId,beanName, getKey(config.getMatchId(),
                                                                                         config.getMarketType()),null);
        localCacheRefreshProducer.send(message,linkId);
        // 通知非实时服务 百家赔监听配置刷新
        localCacheRefreshProducer.send(message,TOPIC_NONREALTIME_CACHE_UPDATE,linkId);
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void refresh(String key) {
        Cache cache = localCacheExpireManager.getCache(CacheConstant.CACHE_AUTO_SWITCH_CONFIG);
        if (Objects.nonNull(cache)) {
            cache.evict(key);
        }
    }

    @Cacheable(key = "#matchId + '_' + #marketType")
    public DataSourceAutoSwitchConfig getConfig(Long matchId, int marketType) {
        Object cache = redisService.get(getRedisKey(matchId, marketType));
        if (cache != null) {
            return (DataSourceAutoSwitchConfig) cache;
        }
        //return new DataSourceAutoSwitchConfig(matchId,marketType);
        return DataSourceAutoSwitchConfig.EMPTY;
    }

    public CategoryDataSourceHighPriority getHighPriority(Long uuid, Set<String> dsSet,
                                                          MarketCategorySell marketCategorySell,
                                                          DataSourceAutoSwitchConfig config, Long sportId) {
        if (Objects.isNull(config) || !config.isEnabled()) {
            return CategoryDataSourceHighPriority.LOW_PRIORITY;
        }

        String ods = marketCategorySell.getDataSourceCode();
        Integer op = Optional.ofNullable(config.getDsPriorityMap().get(ods)).orElse(Integer.MAX_VALUE);
        Integer tp = op;
        String tds = null;
        for (String nds : dsSet) {
            int np = Optional.ofNullable(config.getDsPriorityMap().get(nds)).orElse(Integer.MAX_VALUE);
            if (np < tp) {
                tp = np;
                tds = nds;
            }
        }
//        log.info("uuid:{},priority,matchId:{},categoryId:{},  ods:{},op:{}, tds:{},tp:{},priorityMap:{} ",
//                 uuid,
//                 marketCategorySell.getMatchId(),
//                 marketCategorySell.getMarketCategoryId(),
//                 ods,
//                 op,
//                 tds,
//                 tp,config.getDsPriorityMap());
        if (StringUtils.isNoneEmpty(tds)) {
            return CategoryDataSourceHighPriority
                    .builder()
                    .matchId(marketCategorySell.getMatchId())
                    .categoryId(marketCategorySell.getMarketCategoryId())
                    .marketType(Integer.parseInt(marketCategorySell.getMarketType()))
                    .ods(ods)
                    .tds(tds)
                    .op(op)
                    .tp(tp)
                    .sportId(sportId)
                    .build();
        }
        return CategoryDataSourceHighPriority.LOW_PRIORITY;
    }

    private String getRedisKey(Long matchId, int marketType) {
        return String.format("dss_config:%s:%s", matchId, marketType);
    }

    public long getConfigExpireTime(DataSourceAutoSwitchConfig config, String linkId) {
        Long matchId = config.getMatchId();
        int marketType = config.getMarketType();
        StandardMatchInfo match = standardMatchInfoService.getItem(matchId);
        if (match == null) {
            throw new IllegalArgumentException("linkId:" + linkId + " matchId:" + matchId + " not found");
        }
        if (match.getBeginTime() == null) {
            throw new IllegalArgumentException("linkId:" + linkId + " matchId:" + matchId + " beginTime is null");
        }

        //获取剩余开赛时间 =  开赛时间-当前时间
        Long cacheTime = (match.getBeginTime()  - Calendar.getInstance().getTimeInMillis());
        if (cacheTime <= 0) {
            return RedisConfig.REDIS_DEFAULT_TIME.longValue()*1000L;
        }
        //redis过期时间为秒 = 剩余开赛时间 + 2天时间 ，为redis过期时间
        return (cacheTime ) + (2L * RedisConfig.REDIS_DEFAULT_TIME*1000L);

        /*if (marketType == MarketTypeEnum.PREMATCH.getCode()) {
            return match.getBeginTime() - System.currentTimeMillis() + 24 * 60 * 60 * 1000L;
        } else
            return 120 * 60 * 60 * 1000L;*/

    }

}
