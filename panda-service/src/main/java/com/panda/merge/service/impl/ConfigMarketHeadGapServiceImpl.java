package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.ConfigMarketCategoryHeadMapper;
import com.panda.merge.model.ConfigMarketCategoryHead;
import com.panda.merge.model.ConfigMarketCategoryHeadExample;
import com.panda.merge.service.ConfigMarketHeadGapService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author :  myname
 * @Project Name :  panda-merge
 * @Package Name :  com.panda.merge.service.impl
 * @Description :  TODO
 * @Date: 2020-10-03 11:35
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class ConfigMarketHeadGapServiceImpl implements ConfigMarketHeadGapService {

    @Autowired
    private ConfigMarketCategoryHeadMapper configMarketCategoryHeadMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ConfigMarketHeadGapService configMarketHeadGapService;

    @Override
    @Cacheable(key = "'ConfigMarketCategoryHead:' + #standardMatchInfoId + '-'+ #standardCategoryId+ '-'+ #childStandardCategoryId", unless = "#result == null ")
    public ConfigMarketCategoryHead getItem(String linkId, Long standardMatchInfoId, Long standardCategoryId, Long childStandardCategoryId) {
        ConfigMarketCategoryHeadExample configMarketCategoryHeadExample = new ConfigMarketCategoryHeadExample();
        configMarketCategoryHeadExample.createCriteria()
                .andStandardMatchInfoIdEqualTo(standardMatchInfoId)
                .andStandardCategoryIdEqualTo(standardCategoryId)
                .andChildStandardCategoryIdEqualTo(childStandardCategoryId);
        // 至于Id 我觉得给UUID是可行的~
        StopWatch swCalculate = new StopWatch(UUID.randomUUID().toString());
        swCalculate.start("数据库查询盘口差耗时");
        List<ConfigMarketCategoryHead> configMarketCategoryHeads = configMarketCategoryHeadMapper.selectByExample(configMarketCategoryHeadExample);
        swCalculate.stop();
        log.info("::{}::数据库查询盘口差耗时{}ms，标准玩法id={}", linkId, swCalculate.getTotalTimeMillis(), standardCategoryId);
        if (CollectionUtils.isEmpty(configMarketCategoryHeads)) {
            return null;
        }
        return configMarketCategoryHeads.get(0);
    }

    @Override
    @CachePut(key = "'ConfigMarketCategoryHead:' + #configMarketCategoryHead.standardMatchInfoId + '-'+ #configMarketCategoryHead.standardCategoryId + '-'+ #configMarketCategoryHead.childStandardCategoryId ")
    public ConfigMarketCategoryHead create(ConfigMarketCategoryHead configMarketCategoryHead) {
        configMarketCategoryHeadMapper.insertSelective(configMarketCategoryHead);
        return configMarketCategoryHead;
    }

    @Override
    @CacheEvict(key = "'ConfigMarketCategoryHead:' + #configMarketCategoryHead.standardMatchInfoId + '-'+ #configMarketCategoryHead.standardCategoryId + '-'+ #configMarketCategoryHead.childStandardCategoryId ")
    public ConfigMarketCategoryHead update(ConfigMarketCategoryHead configMarketCategoryHead) {
        configMarketCategoryHead.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        configMarketCategoryHeadMapper.updateByPrimaryKey(configMarketCategoryHead);
        return configMarketCategoryHead;
    }

    @Override
    @CacheEvict(key = "'ConfigMarketCategoryHead:' + #configMarketCategoryHead.standardMatchInfoId + '-'+ #configMarketCategoryHead.standardCategoryId + '-'+ #configMarketCategoryHead.childStandardCategoryId ")
    public void del(ConfigMarketCategoryHead configMarketCategoryHead) {
        configMarketCategoryHeadMapper.deleteByPrimaryKey(configMarketCategoryHead.getId());
    }

    @Override
    public void delHeadByMatchInfoId(Long matchId, String linkId) {
        ConfigMarketCategoryHeadExample configMarketCategoryHeadExample = new ConfigMarketCategoryHeadExample();
        configMarketCategoryHeadExample.createCriteria().andStandardMatchInfoIdEqualTo(matchId);
        List<ConfigMarketCategoryHead> configMarketCategoryHeadList = configMarketCategoryHeadMapper.selectByExample(configMarketCategoryHeadExample);
        if (CollectionUtils.isEmpty(configMarketCategoryHeadList)) {
            return;
        }
        clearCache(matchId, linkId, configMarketCategoryHeadList);
    }

    @Override
    public void delHeadByMatchIdAndCategoryList(Long matchId, String linkId, List<Long> list) {
        ConfigMarketCategoryHeadExample configMarketCategoryHeadExample = new ConfigMarketCategoryHeadExample();
        configMarketCategoryHeadExample.createCriteria().andStandardMatchInfoIdEqualTo(matchId).andStandardCategoryIdIn(list);
        List<ConfigMarketCategoryHead> configMarketCategoryHeadList = configMarketCategoryHeadMapper.selectByExample(configMarketCategoryHeadExample);
        if (CollectionUtils.isEmpty(configMarketCategoryHeadList)) {
            return;
        }
        clearCache(matchId, linkId, configMarketCategoryHeadList);
    }

    private void clearCache(Long matchId, String linkId, List<ConfigMarketCategoryHead> configMarketCategoryHeadList) {
        for (ConfigMarketCategoryHead head : configMarketCategoryHeadList) {
            configMarketHeadGapService.del(head);

            String key = RedisConfig.REDIS_KEY_DATABASE + "::ConfigMarketCategoryHead:" + head.getStandardMatchInfoId() + '-' + head.getStandardCategoryId() + '-' + head.getChildStandardCategoryId();
            redisService.del(key);
            log.info("::{}::标准赛事ID:{},清除盘口差成功,KEY:{},redis:{}", linkId, matchId, JSON.toJSONString(head), key);
        }
    }


    @Override
    public ConfigMarketCategoryHead getItemCache(String linkId, Long standardMatchInfoId, Long standardCategoryId, Long childStandardCategoryId) {
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_HEAD + standardMatchInfoId;
        Map<String, Map<String, ConfigMarketCategoryHead>> standardCategoryMap = (Map) redisService.hGet(redisKey, String.valueOf(standardCategoryId));
        if (MapUtils.isNotEmpty(standardCategoryMap)) {
            Map<String, ConfigMarketCategoryHead> childStandardCategoryMap = (Map) standardCategoryMap.get(String.valueOf(standardCategoryId));
            if (MapUtils.isNotEmpty(childStandardCategoryMap)) {
                return childStandardCategoryMap.get(String.valueOf(childStandardCategoryId));
            }
        }
        return null;
    }

    @Override
    public void saveOrUpdateCache(String linkId, ConfigMarketCategoryHead head) {
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_HEAD + head.getStandardMatchInfoId();
        //缓存格式 map<玩法ID,Map<子玩法，球头数据>> 会有子玩法球头差
        Map<String, Map<String, ConfigMarketCategoryHead>> standardCategoryMap = (Map) redisService.hGet(redisKey, String.valueOf(head.getStandardCategoryId()));
        if (MapUtils.isEmpty(standardCategoryMap)) {
            standardCategoryMap = new HashMap<>();
        }
        //Map<子玩法，球头数据>
        Map<String, ConfigMarketCategoryHead> childStandardCategoryMap = standardCategoryMap.get(String.valueOf(head.getStandardCategoryId()));
        if (null == childStandardCategoryMap) {
            childStandardCategoryMap = new HashMap<>();
        }
        childStandardCategoryMap.put(String.valueOf(head.getChildStandardCategoryId()), head);

        standardCategoryMap.put(String.valueOf(head.getStandardCategoryId()), childStandardCategoryMap);
        redisService.hSet(redisKey, String.valueOf(head.getStandardCategoryId()), standardCategoryMap, RedisConfig.REDIS_WEEK_TIME);
        log.info("::{}::保存球头差,赛事ID:{},玩法ID:{},子玩法ID:{},缓存key:{},缓存后数据:{}",
                linkId, head.getStandardCategoryId(), head.getStandardCategoryId(), head.getChildStandardCategoryId(), redisKey, JSONObject.toJSONString(standardCategoryMap));
    }

    @Override
    public void delCacheByStandardMatchInfoId(Long matchId, String linkId) {
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_HEAD + matchId;
        redisService.del(redisKey);
        log.info("::{}::删除赛事级别球头差,赛事ID:{},key:{}", linkId, matchId, redisKey);
    }

    @Override
    public void delCacheByCategoryIdList(String linkId, Long matchId, List<Long> categoryIdList) {
        //切换数据源清除盘口差
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_HEAD + matchId;
        categoryIdList.forEach(id -> {
            redisService.hDel(redisKey, id);
        });
        log.info("::{}::赛事ID:{},清除批量玩法球头差:{}", linkId, matchId, categoryIdList);
    }
}
