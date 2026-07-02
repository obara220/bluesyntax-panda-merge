package com.panda.merge.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.TradeMarketDisplayConfigDTO;
import com.panda.merge.mapper.ConfigMarketDisplayTradeMapper;
import com.panda.merge.model.ConfigMarketDisplayTrade;
import com.panda.merge.model.ConfigMarketDisplayTradeExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigMarketDisplayTradeService;
import com.panda.merge.service.StandardMatchInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/16 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketDisplayTradeServiceImpl implements ConfigMarketDisplayTradeService {

    @Autowired
    private ConfigMarketDisplayTradeMapper configMarketDisplayTradeMapper;
    @Autowired
    private BaseProcessor baseProcessor;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private RedisService redisService;

    @Override
    public ConfigMarketDisplayTrade getItem(Long standardMatchInfoId) {
        String key = getConfigMarketDisplayTradekey(standardMatchInfoId);
        return (ConfigMarketDisplayTrade) redisService.get(key);
    }

    @Override
    public List<ConfigMarketDisplayTrade> getItems(List<Long> standardMatchIds){
        if(CollectionUtils.isEmpty(standardMatchIds)){
            return new LinkedList<>();
        }
        List<ConfigMarketDisplayTrade> result = new ArrayList<>();
        for(Long standardMatchId : standardMatchIds){
            String key = getConfigMarketDisplayTradekey(standardMatchId);
            result.add((ConfigMarketDisplayTrade) redisService.get(key));
        }
        return result;
    }


    @Override
    public ConfigMarketDisplayTrade create(TradeMarketDisplayConfigDTO displayConfigDTO) {
        ConfigMarketDisplayTrade configMarketDisplayTrade = new ConfigMarketDisplayTrade();
        configMarketDisplayTrade.setId(UUIdUtils.getId());
        configMarketDisplayTrade.setDisplayCorner(!displayConfigDTO.isDisplayCorner() ? "N"  : "Y");
        configMarketDisplayTrade.setDisplayPenaltyCard(!displayConfigDTO.isDisplayPenalty() ? "N"  : "Y");
        int displayMarketCount = null == displayConfigDTO.getPreMarketNum() ? 3 : displayConfigDTO.getPreMarketNum();
        int liveMarketCount = null == displayConfigDTO.getLiveMarketNum() ? 3 : displayConfigDTO.getLiveMarketNum();
        configMarketDisplayTrade.setDisplayMarketCount(displayMarketCount);
        configMarketDisplayTrade.setLiveMarketCount(liveMarketCount);
        configMarketDisplayTrade.setStandardMatchId(displayConfigDTO.getMatchId());
        configMarketDisplayTrade.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        String key = getConfigMarketDisplayTradekey(configMarketDisplayTrade.getStandardMatchId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketDisplayTrade.getStandardMatchId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.set(key,configMarketDisplayTrade,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return configMarketDisplayTrade;
    }

    @Override
    public ConfigMarketDisplayTrade update(ConfigMarketDisplayTrade configMarketDisplayTrade) {
        configMarketDisplayTrade.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        String key = getConfigMarketDisplayTradekey(configMarketDisplayTrade.getStandardMatchId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketDisplayTrade.getStandardMatchId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.set(key,configMarketDisplayTrade,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return configMarketDisplayTrade;
    }
    private String getConfigMarketDisplayTradekey(Long standardMatchId){
        return DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketDisplayTrade:" +standardMatchId);
    }
}
