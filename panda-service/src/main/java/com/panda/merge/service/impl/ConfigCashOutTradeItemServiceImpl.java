package com.panda.merge.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.collect.Lists;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.ConfigCashOutTradeItemDTO;
import com.panda.merge.mapper.ConfigCashOutTradeItemMapper;
import com.panda.merge.model.ConfigCashOutTradeItem;
import com.panda.merge.model.ConfigCashOutTradeItemExample;
import com.panda.merge.service.ConfigCashOutTradeItemService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigCashOutTradeItemServiceImpl implements ConfigCashOutTradeItemService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private ConfigCashOutTradeItemMapper cashOutTradeItemMapper;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Override
    public List<ConfigCashOutTradeItem> getItemList(Long matchId, Integer marketType) {
        ConfigCashOutTradeItem configCashOutTradeItem = new ConfigCashOutTradeItem();
        configCashOutTradeItem.setMatchId(matchId);
        configCashOutTradeItem.setMarketType(marketType);
        Map<String,String> keyMap = getkeyMap(configCashOutTradeItem);
        Object obj = redisService.get(keyMap.get("ConfigCashOutTradeItemList"));
        if (obj != null){
            return (List<ConfigCashOutTradeItem>) redisService.get(keyMap.get("ConfigCashOutTradeItemList"));
        }
        return null;
    }

    @Override
    public ConfigCashOutTradeItem getItem(Long matchId, Integer marketType, Integer leve) {
        ConfigCashOutTradeItem configCashOutTradeItem = new ConfigCashOutTradeItem();
        configCashOutTradeItem.setMatchId(matchId);
        configCashOutTradeItem.setMarketType(marketType);
        configCashOutTradeItem.setLeve(leve);
        Map<String,String> keyMap = getkeyMap(configCashOutTradeItem);
        Object obj = redisService.get(keyMap.get("ConfigCashOutTradeItemMatch"));
        if (null != obj){
            return (ConfigCashOutTradeItem) obj;
        }
        return null;
    }

    @Override
    public ConfigCashOutTradeItem getItem(Long matchId, Integer marketType, Long marketCategoryId) {
        ConfigCashOutTradeItem configCashOutTradeItem = new ConfigCashOutTradeItem();
        configCashOutTradeItem.setMatchId(matchId);
        configCashOutTradeItem.setMarketType(marketType);
        configCashOutTradeItem.setMarketCategoryId(marketCategoryId);
        Map<String,String> keyMap = getkeyMap(configCashOutTradeItem);
        Object obj = redisService.get(keyMap.get("ConfigCashOutTradeItemCategory"));
        if (null != obj){
            return (ConfigCashOutTradeItem) obj;
        }
        return null;
    }

    @Override
    public ConfigCashOutTradeItem getItem(Long matchId, Integer marketType, Integer leve, Integer match_pre_status) {
        ConfigCashOutTradeItem configCashOutTradeItem = new ConfigCashOutTradeItem();
        configCashOutTradeItem.setMatchId(matchId);
        configCashOutTradeItem.setMarketType(marketType);
        configCashOutTradeItem.setLeve(leve);
        Map<String,String> keyMap = getkeyMap(configCashOutTradeItem);
        Object obj = redisService.get(keyMap.get("ConfigCashOutTradeItemMatch"));
        if (null != obj){
            ConfigCashOutTradeItem configCashOutTradeItem1 =  (ConfigCashOutTradeItem) obj;
            if (configCashOutTradeItem1.getMatchPreStatus() == match_pre_status){
                return configCashOutTradeItem1;
            }
        }
        return null;
    }

    @Override
    public void create(ConfigCashOutTradeItemDTO itemDTO) {
        /*ConfigCashOutTradeItem cashOutTradeItem = new ConfigCashOutTradeItem();
        BeanUtils.copyProperties(itemDTO, cashOutTradeItem);
        cashOutTradeItem.setId(UUIdUtils.getId());
        cashOutTradeItem.setCreateTime(System.currentTimeMillis());
        cashOutTradeItemMapper.insert(cashOutTradeItem);
        refreshCache(cashOutTradeItem);*/

    }

    @Override
    public void update(ConfigCashOutTradeItem cashOutTradeItem, ConfigCashOutTradeItemDTO itemDTO) {
        /*ConfigCashOutTradeItem upCashOutTradeItem = new ConfigCashOutTradeItem();
        BeanUtils.copyProperties(itemDTO, upCashOutTradeItem);
        upCashOutTradeItem.setId(cashOutTradeItem.getId());
        upCashOutTradeItem.setModifyTime(System.currentTimeMillis());
        cashOutTradeItemMapper.updateByPrimaryKeySelective(upCashOutTradeItem);
        Map<String,String> keyMap = getkeyMap(upCashOutTradeItem);*/
    }

    public void refreshCache(ConfigCashOutTradeItem cashOutTradeItem) {
        redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ConfigCashOutTradeItemList:" + cashOutTradeItem.getMatchId() + "-" + cashOutTradeItem.getMarketType());
        redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ConfigCashOutTradeItemMatch:" + cashOutTradeItem.getMatchId() + "-" + cashOutTradeItem.getMarketType() + "-" + cashOutTradeItem.getLeve());
        redisService.del(RedisConfig.REDIS_KEY_DATABASE + "::ConfigCashOutTradeItemCategory:" + cashOutTradeItem.getMatchId() + "-" + cashOutTradeItem.getMarketType() + "-" + cashOutTradeItem.getMarketCategoryId());
    }
    private Map<String,String> getkeyMap(ConfigCashOutTradeItem cashOutTradeItem){
        Map<String,String> map = new HashMap<>();
        map.put("ConfigCashOutTradeItemList",DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE + "::ConfigCashOutTradeItemList:" + cashOutTradeItem.getMatchId() + "-" + cashOutTradeItem.getMarketType()));
        map.put("ConfigCashOutTradeItemMatch",DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE + "::ConfigCashOutTradeItemMatch:" + cashOutTradeItem.getMatchId() + "-" + cashOutTradeItem.getMarketType() + "-" + cashOutTradeItem.getLeve()));
        map.put("ConfigCashOutTradeItemCategory",DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE + "::ConfigCashOutTradeItemCategory:" + cashOutTradeItem.getMatchId() + "-" + cashOutTradeItem.getMarketType() + "-" + cashOutTradeItem.getMarketCategoryId()));
        return map;
    }
}
