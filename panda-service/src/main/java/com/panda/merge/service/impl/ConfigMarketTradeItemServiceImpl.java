package com.panda.merge.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.TradeMarketConfigItemDTO;
import com.panda.merge.mapper.ConfigMarketTradeItemMapper;
import com.panda.merge.model.ConfigMarketTradeItem;
import com.panda.merge.model.ConfigMarketTradeItemExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigMarketTradeItemService;
import com.panda.merge.service.StandardMatchInfoService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
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
 * @createDate 2020/8/19 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketTradeItemServiceImpl implements ConfigMarketTradeItemService {

    @Autowired
    private ConfigMarketTradeItemMapper configMarketTradeItemMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BaseProcessor baseProcessor;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Override
    public ConfigMarketTradeItem getItem(Long standardMatchId, Long marketCategoryId,Long childMarketCategoryId, Integer placeNum){
        String key = DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketTradeItem:" +standardMatchId);
        Object obj = redisService.hGet(key,marketCategoryId+"-"+childMarketCategoryId+"-"+placeNum);
        if (obj != null){
            return (ConfigMarketTradeItem)obj;
        }
        return null;
    }

    @Override
    public Map<String, ConfigMarketTradeItem> getItemByMatchAndCategorys(Long standardMatchId, Set<Long> marketCategoryIdSet) {
        String key = DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketTradeItem:" +standardMatchId);
        return redisService.hGetAll(key);
    }

    @Override
    public ConfigMarketTradeItem create(String linkId, TradeMarketConfigItemDTO tradeMarketConfigItemDTO, Long standardMatchId, Integer placeNum, Long operaterId) {
        ConfigMarketTradeItem configMarketTradeItem = new ConfigMarketTradeItem();
        BeanUtils.copyProperties(tradeMarketConfigItemDTO, configMarketTradeItem);
        //这个id作为主键id，不做业务使用
        configMarketTradeItem.setMarketId(UUIdUtils.getId());
        configMarketTradeItem.setPlaceNum(placeNum);
        configMarketTradeItem.setMatchId(standardMatchId);
        configMarketTradeItem.setLinkId(linkId);
        configMarketTradeItem.setOperaterId(operaterId);
        configMarketTradeItem.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketTradeItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        String key = DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketTradeItem:" +standardMatchId);
        redisService.hSet(key,configMarketTradeItem.getMarketCategoryId()+"-"+configMarketTradeItem.getChildStandardCategoryId()+"-"+placeNum,configMarketTradeItem,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));

        return configMarketTradeItem;
    }

    @Override
    public ConfigMarketTradeItem update(ConfigMarketTradeItem configMarketTradeItem) {
        configMarketTradeItem.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketTradeItem.getMatchId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        String key = DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketTradeItem:" +configMarketTradeItem.getMatchId());
        redisService.hSet(key,configMarketTradeItem.getMarketCategoryId()+"-"+configMarketTradeItem.getChildStandardCategoryId()+"-"+configMarketTradeItem.getPlaceNum(),configMarketTradeItem,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return configMarketTradeItem;
    }

    @Override
    public List<ConfigMarketTradeItem> getRecsByMatchId(String standardMatchId) {
        String key = DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE+ "::ConfigMarketTradeItem:" +standardMatchId);
        Map<String,ConfigMarketTradeItem> configMarketTradeItemMap = redisService.hGetAll(key);
        if (MapUtils.isNotEmpty(configMarketTradeItemMap)){
            return new ArrayList<ConfigMarketTradeItem>(configMarketTradeItemMap.values());
        }
        return null;

    }


}
