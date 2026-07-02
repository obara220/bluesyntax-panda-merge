package com.panda.merge.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dao.ConfigMarketCategoryPlaceDao;
import com.panda.merge.dto.MarketPlaceDtlDTO;
import com.panda.merge.mapper.ConfigMarketCategoryPlaceMapper;
import com.panda.merge.model.ConfigMarketCategoryPlace;
import com.panda.merge.model.ConfigMarketCategoryPlaceExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.service.ConfigMarketCategoryPlaceService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/19 <br>
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketCategoryPlaceServiceImpl implements ConfigMarketCategoryPlaceService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private ConfigMarketCategoryPlaceDao configMarketCategoryPlaceDao;
    @Autowired
    private ConfigMarketCategoryPlaceMapper configMarketCategoryPlaceMapper;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private BaseProcessor baseProcessor;

    @Override
    public ConfigMarketCategoryPlace getItem(Long standardMatchInfoId, Long standardCategoryId,Long childStandardCategoryId, Integer placeNum) {
        /*ConfigMarketCategoryPlaceExample configMarketCategoryPlaceExample = new ConfigMarketCategoryPlaceExample();
        configMarketCategoryPlaceExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchInfoId)
                .andChildStandardCategoryIdEqualTo(childStandardCategoryId)
                .andStandardCategoryIdEqualTo(standardCategoryId).andPlaceNumEqualTo(placeNum);
        List<ConfigMarketCategoryPlace> configMarketCategoryPlaces = configMarketCategoryPlaceMapper.selectByExample(configMarketCategoryPlaceExample);
        if (CollectionUtils.isEmpty(configMarketCategoryPlaces)) {
            return null;
        }*/
        String key = RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryPlace:" + standardMatchInfoId + '-' + standardCategoryId + '-'  + childStandardCategoryId + '-' + placeNum;
        Object obj = redisService.get(key);
        if (obj != null){
            return (ConfigMarketCategoryPlace) obj;
        }
        return null;
    }

    @Override
    public ConfigMarketCategoryPlace create(String linkId, MarketPlaceDtlDTO marketPlaceDtlDTO, Long standardMatchInfoId) {
        ConfigMarketCategoryPlace p = new ConfigMarketCategoryPlace();
        BeanUtils.copyProperties(marketPlaceDtlDTO, p);
        p.setId(UUIdUtils.getId());
        p.setLinkId(linkId);
        p.setStandardMatchInfoId(standardMatchInfoId);
        p.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        p.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        String key = RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryPlace:" + p.getStandardMatchInfoId() + '-' + p.getStandardCategoryId() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchInfoId);
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.set(key,p,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return p;
    }

    @Override
    public ConfigMarketCategoryPlace update(ConfigMarketCategoryPlace p) {
        String key = RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryPlace:" + p.getStandardMatchInfoId() + '-' + p.getStandardCategoryId() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(p.getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        redisService.set(key,p,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        return p;
    }

    @Override
    public int delete(Long standardMatchInfoId, Long standardCategoryId) {
        /*ConfigMarketCategoryPlaceExample configMarketCategoryPlaceExample = new ConfigMarketCategoryPlaceExample();
        configMarketCategoryPlaceExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchInfoId)
                .andStandardCategoryIdEqualTo(standardCategoryId);
        //先清理缓存
        List<ConfigMarketCategoryPlace> configMarketCategoryPlaces = configMarketCategoryPlaceMapper.selectByExample(configMarketCategoryPlaceExample);
        clearCache(configMarketCategoryPlaces);
        //删除
        return configMarketCategoryPlaceMapper.deleteByExample(configMarketCategoryPlaceExample);*/
        return  0;
    }

    @Override
    public int delBatch(Long standardMatchInfoId, Set<Long> delMarketCategoryIdSet, String linkId) {
        /*ConfigMarketCategoryPlaceExample configMarketCategoryPlaceExample = new ConfigMarketCategoryPlaceExample();
        configMarketCategoryPlaceExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchInfoId)
                .andStandardCategoryIdIn(new ArrayList<>(delMarketCategoryIdSet)).andChildStandardCategoryIdIn(new ArrayList<>(delMarketCategoryIdSet));
        //先清理缓存
        List<ConfigMarketCategoryPlace> configMarketCategoryPlaces = configMarketCategoryPlaceMapper.selectByExample(configMarketCategoryPlaceExample);
        clearCache(configMarketCategoryPlaces);
        //删除
        return configMarketCategoryPlaceMapper.deleteByExample(configMarketCategoryPlaceExample);*/
        return 0;
    }

    @Override
    public int delByStandardMatchIds(List<Long> standardMatchIds) {
        /*ConfigMarketCategoryPlaceExample configMarketCategoryPlaceExample = new ConfigMarketCategoryPlaceExample();
        configMarketCategoryPlaceExample.createCriteria().andStandardMatchInfoIdIn(standardMatchIds);
        List<ConfigMarketCategoryPlace> configMarketCategoryPlaces = configMarketCategoryPlaceMapper.selectByExample(configMarketCategoryPlaceExample);
        clearCache(configMarketCategoryPlaces);
        return configMarketCategoryPlaceMapper.deleteByExample(configMarketCategoryPlaceExample);*/
        return 0;
    }

    @Override
    public void insertList(List<ConfigMarketCategoryPlace> configMarketCategoryPlaces, String linkId) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketCategoryPlaces.get(0).getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        for (ConfigMarketCategoryPlace p : configMarketCategoryPlaces) {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryPlace:" + p.getStandardMatchInfoId() + '-' + p.getStandardCategoryId() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum();
            redisService.set(key, p, baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        }
        //configMarketCategoryPlaceDao.insertList(configMarketCategoryPlaces);
    }

    @Override
    public void updateList(List<ConfigMarketCategoryPlace> configMarketCategoryPlaces, String linkId) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketCategoryPlaces.get(0).getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        for (ConfigMarketCategoryPlace p : configMarketCategoryPlaces) {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryPlace:" + p.getStandardMatchInfoId() + '-' + p.getStandardCategoryId() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum();
            redisService.set(key, p, baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        }
        //configMarketCategoryPlaceDao.updateList(configMarketCategoryPlaces);
    }

    @Override
    public List<ConfigMarketCategoryPlace> getItemListCache(Long standardMatchInfoId, Long standardCategoryId) {
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_PLACE + standardMatchInfoId;
        Object obj = redisService.hGetAllBasedBucket(redisKey, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
        List<ConfigMarketCategoryPlace> configMarketCategoryPlaces = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(obj))
        {
            Map<String,ConfigMarketCategoryPlace> marketCategoryPlaceMap = (Map<String,ConfigMarketCategoryPlace>)obj;
            for (int i = 0;i<11;i++)
            {
                String key = standardMatchInfoId+"_"+standardCategoryId+"_"+standardCategoryId+"_"+i;
                if (ObjectUtil.isNotEmpty(marketCategoryPlaceMap.get(key)))
                {
                    configMarketCategoryPlaces.add(marketCategoryPlaceMap.get(key));
                }
            }
        }
        return configMarketCategoryPlaces;
    }

    @Override
    public void cacheConfigMarketPlace(List<ConfigMarketCategoryPlace> configMarketCategoryPlaces, String linkId,Long standardMatchInfoId) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(configMarketCategoryPlaces.get(0).getStandardMatchInfoId());
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Snooker.getCode())){
            standardMatchInfo.setBeginTime(standardMatchInfo.getBeginTime()+(RedisConfig.REDIS_WEEK_TIME * 1000));
        }
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_PLACE + standardMatchInfoId;
        Map<String,ConfigMarketCategoryPlace> map = configMarketCategoryPlaces.stream().collect(Collectors.toMap(e -> e.getStandardMatchInfoId()+"_"+e.getStandardCategoryId()+"_"+e.getChildStandardCategoryId()+"_"+e.getPlaceNum(),
                e -> e,(oldValue,newValue)->newValue));
        redisService.hSetAllBasedBucket(redisKey,ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR, map,baseProcessor.marketCacheTime(standardMatchInfo.getBeginTime()));
        log.info("缓存玩法坑位信息，key:{},数据为:{}", redisKey, JSONUtil.toJsonStr(map));
    }

    @Override
    public ConfigMarketCategoryPlace getConfigMarketPlaceCache(Long standardMatchInfoId, Long StandardCategoryId, Long childStandardCategoryId, Integer placeNum) {
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_PLACE + standardMatchInfoId;
        String fieldKey = standardMatchInfoId+"_"+StandardCategoryId+"_"+childStandardCategoryId+"_"+placeNum;
        String updated = redisService.genNewHashKey(redisKey, fieldKey, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
        Object obj = redisService.hGet(updated,fieldKey);
        if (ObjectUtil.isNotEmpty(obj))
        {
            return (ConfigMarketCategoryPlace)obj;
        }
        log.info("getConfigMarketPlaceCache key:{} field:{} is null", redisKey, fieldKey);
        return null;
    }

    @Override
    public String genNewHashKey(Long standardMatchInfoId, Long StandardCategoryId, Long childStandardCategoryId, Integer placeNum) {
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_PLACE + standardMatchInfoId;
        String fieldKey = standardMatchInfoId+"_"+StandardCategoryId+"_"+childStandardCategoryId+"_"+placeNum;
        String updated = redisService.genNewHashKey(redisKey, fieldKey, ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
        return updated;
    }

    public void clearCache(List<ConfigMarketCategoryPlace> configMarketCategoryPlaces) {
        List keyList = new ArrayList();
        for (ConfigMarketCategoryPlace p : configMarketCategoryPlaces) {
            String key = RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryPlace:" + p.getStandardMatchInfoId() + '-' + p.getStandardCategoryId() + '-'  + p.getChildStandardCategoryId() + '-' + p.getPlaceNum();
            keyList.add(key);
        }
        redisService.del(keyList);
    }
}
