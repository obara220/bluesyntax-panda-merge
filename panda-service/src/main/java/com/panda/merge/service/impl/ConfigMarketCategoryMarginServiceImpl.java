package com.panda.merge.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.ConfigMarketCategoryMarginDao;
import com.panda.merge.mapper.ConfigMarketCategoryMarginMapper;
import com.panda.merge.model.ConfigCashOutTradeItem;
import com.panda.merge.model.ConfigMarketCategoryMargin;
import com.panda.merge.model.ConfigMarketCategoryMarginExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigMarketCategoryMarginService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/26 <br>
 * @see com.panda.merge.service.impl <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketCategoryMarginServiceImpl implements ConfigMarketCategoryMarginService {

    @Autowired
    private ConfigMarketCategoryMarginMapper configMarketCategoryMarginMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    ConfigMarketCategoryMarginDao configMarketCategoryMarginDao;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private BaseProcessor baseProcessor;

    @Override
    public ConfigMarketCategoryMargin getItemThree(Long standardMatchInfoId, Long standardCategoryId,Long childStandardCategoryId, Integer placeNum, String oddsType) {
        Map<String,String> keyMap = getkeyMap(standardMatchInfoId);
        Object obj = redisService.hGet(keyMap.get("ConfigMarketCategoryMargin_Tree"),standardCategoryId + '-' + childStandardCategoryId + '-' + placeNum + '-' + oddsType);
        if (obj != null){
            return (ConfigMarketCategoryMargin) obj;
        }
        return null;
    }

    @Override
    public ConfigMarketCategoryMargin createThree(ConfigMarketCategoryMargin p) {
        Map<String,String> keyMap = getkeyMap(p.getStandardMatchInfoId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(p.getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.hSet(keyMap.get("ConfigMarketCategoryMargin_Tree"),p.getStandardCategoryId() + '-' + p.getChildStandardCategoryId() + '-' + p.getPlaceNum() + '-' + p.getOddsType(),p,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return p;
    }

    @Override
    public ConfigMarketCategoryMargin updateThree(ConfigMarketCategoryMargin p) {
        Map<String,String> keyMap = getkeyMap(p.getStandardMatchInfoId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(p.getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.hSet(keyMap.get("ConfigMarketCategoryMargin_Tree"),p.getStandardCategoryId() + '-' + p.getChildStandardCategoryId() + '-' + p.getPlaceNum() + '-' + p.getOddsType(),p,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return p;
    }

    @Override
    public ConfigMarketCategoryMargin getItemTwo(String linkId,Long standardMatchInfoId, Long standardCategoryId,Long childStandardCategoryId, Integer placeNum) {
        Map<String,String> keyMap = getkeyMap(standardMatchInfoId);
        Object obj = redisService.hGet(keyMap.get("ConfigMarketCategoryMargin_Two"),standardCategoryId.toString() + '-' + childStandardCategoryId + '-' + placeNum);
        if (obj != null){
            return (ConfigMarketCategoryMargin) obj;
        }
        return null;
    }

    @Override
    public ConfigMarketCategoryMargin createTwo(ConfigMarketCategoryMargin p) {
        Map<String,String> keyMap = getkeyMap(p.getStandardMatchInfoId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(p.getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.hSet(keyMap.get("ConfigMarketCategoryMargin_Two"),p.getStandardCategoryId().toString() + '-' + p.getChildStandardCategoryId() + '-'  + p.getPlaceNum(),p,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return p;
    }

    @Override
    public ConfigMarketCategoryMargin updateTwo(ConfigMarketCategoryMargin p) {
        Map<String,String> keyMap = getkeyMap(p.getStandardMatchInfoId());
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(p.getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.hSet(keyMap.get("ConfigMarketCategoryMargin_Two"),p.getStandardCategoryId().toString() + '-' + p.getChildStandardCategoryId() + '-'  + p.getPlaceNum(),p,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return p;
    }

    @Override
    public void insertListTwo(List<ConfigMarketCategoryMargin> configMarketCategoryMarginSaveListTwo) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketCategoryMarginSaveListTwo.get(0).getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        Map<String,String> keyMap = getkeyMap(standardMatchInfo.getId());
        Map<String,ConfigMarketCategoryMargin> configMarketCategoryMarginMap = configMarketCategoryMarginSaveListTwo.stream().collect(Collectors.toMap(p->p.getStandardCategoryId().toString() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum(), Function.identity(), (v1, v2)->v1));
        redisService.hSetAll(keyMap.get("ConfigMarketCategoryMargin_Two"),configMarketCategoryMarginMap,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
    }

    @Override
    public void updateListTwo(List<ConfigMarketCategoryMargin> configMarketCategoryMarginUpdateListTwo) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketCategoryMarginUpdateListTwo.get(0).getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        Map<String,String> keyMap = getkeyMap(standardMatchInfo.getId());
        Map<String,ConfigMarketCategoryMargin> configMarketCategoryMarginMap = configMarketCategoryMarginUpdateListTwo.stream().collect(Collectors.toMap(p->p.getStandardCategoryId().toString() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum(), Function.identity(), (v1, v2)->v1));
        redisService.hSetAll(keyMap.get("ConfigMarketCategoryMargin_Two"),configMarketCategoryMarginMap,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
    }

    @Override
    public void insertListThree(List<ConfigMarketCategoryMargin> configMarketCategoryMarginSaveListThree) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketCategoryMarginSaveListThree.get(0).getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        Map<String,String> keyMap = getkeyMap(standardMatchInfo.getId());
        Map<String,ConfigMarketCategoryMargin> configMarketCategoryMarginMap = configMarketCategoryMarginSaveListThree.stream().collect(Collectors.toMap(p->p.getStandardCategoryId().toString() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum() + '-' + p.getOddsType(), Function.identity(), (v1, v2)->v1));
        redisService.hSetAll(keyMap.get("ConfigMarketCategoryMargin_Tree"),configMarketCategoryMarginMap,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));

    }

    @Override
    public void updateListThree(List<ConfigMarketCategoryMargin> configMarketCategoryMarginUpdateListThree) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketCategoryMarginUpdateListThree.get(0).getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        Map<String,String> keyMap = getkeyMap(standardMatchInfo.getId());
        Map<String,ConfigMarketCategoryMargin> configMarketCategoryMarginMap = configMarketCategoryMarginUpdateListThree.stream().collect(Collectors.toMap(p->p.getStandardCategoryId().toString() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum() + '-' + p.getOddsType(), Function.identity(), (v1, v2)->v1));
        redisService.hSetAll(keyMap.get("ConfigMarketCategoryMargin_Tree"),configMarketCategoryMarginMap,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));

    }

    @Override
    public void updateByCategory(String linkId, Long standardMatchInfoId, Long standardCategoryId, Double margin) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        Map<String,String> keyMap = getkeyMap(standardMatchInfo.getId());
        Map<String,ConfigMarketCategoryMargin> configTwo = redisService.hGetAll(keyMap.get("ConfigMarketCategoryMargin_Two"));
        Map<String,ConfigMarketCategoryMargin> configThree = redisService.hGetAll(keyMap.get("ConfigMarketCategoryMargin_Tree"));
        configTwo.forEach((k, v) -> {
            if (v.getStandardCategoryId().equals(standardCategoryId)) {
                v.setMargin(margin);
            }
        });
        configThree.forEach((k, v) -> {
            if (v.getStandardCategoryId().equals(standardCategoryId)) {
                v.setMargin(margin);
            }
        });
        redisService.hSetAll(keyMap.get("ConfigMarketCategoryMargin_Two"),configTwo,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        redisService.hSetAll(keyMap.get("ConfigMarketCategoryMargin_Tree"),configThree,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        log.info("::{}::分时margin总玩法，更新子玩法margin成功,赛事ID:{}，玩法ID:{},条数", linkId, standardMatchInfoId, standardCategoryId);
    }
    private Map<String,String> getkeyMap(Long standardMatchId){
        Map<String,String> map = new HashMap<>();
        map.put("ConfigMarketCategoryMargin_Tree",DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryMargin_Tree:" + standardMatchId));
        map.put("ConfigMarketCategoryMargin_Two",DigestUtil.md5Hex(RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryMargin_Two:" + standardMatchId));
        return map;
    }
}
