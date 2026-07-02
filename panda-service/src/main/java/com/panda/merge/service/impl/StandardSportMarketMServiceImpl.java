package com.panda.merge.service.impl;

import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.StandardSportMarketMDao;
import com.panda.merge.mapper.StandardSportMarketMMapper;
import com.panda.merge.model.StandardSportMarketM;
import com.panda.merge.model.StandardSportMarketMExample;
import com.panda.merge.service.StandardSportMarketMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class StandardSportMarketMServiceImpl implements StandardSportMarketMService {
    @Autowired
    private StandardSportMarketMMapper standardSportMarketMMapper;
    @Autowired
    private StandardSportMarketMDao standardSportMarketMDao;

    @Override
    @Cacheable(key = "'StandardSportMarketM:'+ #standardMatchInfoId + '-' +#relationMarketId", unless = "#result == null ")
    public StandardSportMarketM getItem(Long standardMatchInfoId, Long relationMarketId) {
        StandardSportMarketMExample sportMarketMExample = new StandardSportMarketMExample();
        sportMarketMExample.createCriteria().andStandardMatchInfoIdEqualTo(standardMatchInfoId).andRelationMarketIdEqualTo(relationMarketId);
        List<StandardSportMarketM> standardSportMarketMList = standardSportMarketMMapper.selectByExample(sportMarketMExample);
        if (CollectionUtils.isEmpty(standardSportMarketMList)) {
            return null;
        }
        return standardSportMarketMList.get(0);
    }

    @Override
    @Async("InitSportMarketRelation")
    public void insertList(String linkId, List<StandardSportMarketM> list) {
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("M模式标准盘口入库耗时");
        try {
            standardSportMarketMDao.insertList(list);
        } catch (Exception e) {
        }finally {
            sw.stop();
            log.info("::{}::M模式标准盘口入库耗时:{}", linkId, sw.getTotalTimeMillis());
        }
    }

    @Override
    @Async("InitSportMarketRelation")
    public void updateBatch(String linkId, List<StandardSportMarketM> list) {
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("M模式标准盘口入库修改耗时");
        try {
            standardSportMarketMDao.updateBatch(list);
        } catch (Exception e) {
        }finally {
            sw.stop();
            log.info("::{}::M模式标准盘口入库修改耗时:{}", linkId, sw.getTotalTimeMillis());
        }
    }
}
