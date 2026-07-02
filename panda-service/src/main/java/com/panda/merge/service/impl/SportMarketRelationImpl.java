package com.panda.merge.service.impl;

import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dao.SportMarketRelationDao;
import com.panda.merge.mapper.SportMarketRelationMapper;
import com.panda.merge.model.SportMarketRelation;
import com.panda.merge.model.SportMarketRelationExample;
import com.panda.merge.service.SportMarketRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author : bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.service.impl
 * @description : TODO
 * @date: 2020-10-16 16:25
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Service
@CacheConfig(cacheNames = RedisConfig.REDIS_KEY_DATABASE)
public class SportMarketRelationImpl implements SportMarketRelationService {

    @Autowired
    private SportMarketRelationDao sportMarketRelationDao;

    @Autowired
    private SportMarketRelationMapper sportMarketRelationMapper;

    @Autowired
    private RedisService redisService;

    @Override
    @Cacheable(key = "'SportMarketRelation:' + #key ", unless = "#result == null ")
    public SportMarketRelation getItem(String key) {
        SportMarketRelationExample sportMarketRelationExample = new SportMarketRelationExample();
        sportMarketRelationExample.createCriteria().andMarketRelationKeyEqualTo(key);
        List<SportMarketRelation> sportMarketRelations = sportMarketRelationMapper.selectByExample(sportMarketRelationExample);
        if (CollectionUtils.isEmpty(sportMarketRelations)) {
            return null;
        }
        return sportMarketRelations.get(0);
    }

    @Override
    @CachePut(key = "'SportMarketRelation:'+#redisKey", unless = "#result == null")
    public void insert(String redisKey, Long relationMarketId) {
        try {
            SportMarketRelation sportMarketRelation = new SportMarketRelation();
            sportMarketRelation.setId(IdWorker.getId());
            sportMarketRelation.setMarketRelationKey(redisKey);
            sportMarketRelation.setRelationMarketId(relationMarketId);
            sportMarketRelation.setCreateTime(System.currentTimeMillis());
            sportMarketRelation.setModifyTime(null);
            sportMarketRelationMapper.insert(sportMarketRelation);
        } catch (DuplicateKeyException e) {
            //此处只打印异常，即使入库失败该盘口投注项依然需要投递给下游
            log.info("marketRelationKey：{} 出现唯一约束冲突relationMarketId:{}", redisKey, relationMarketId);
        }
    }

    @Override
    public void insertBatch(Long standardMatchId, List<SportMarketRelation> sportMarketRelation) {
        try {
            sportMarketRelationDao.insertBatch(sportMarketRelation);
            log.info("【InitializeMatchMarketCache】比赛id:{},插入条数:{}", standardMatchId, sportMarketRelation.size());
        } catch (Exception e){
            log.info("【InitializeMatchMarketCache】统一ID批量入库异常,标准id:{}", standardMatchId);
        }
    }

    @Override
    public void delDbAndCache(Set<String> relationMarketKeys) {
        //批量清除redis
        redisService.delete(relationMarketKeys);
        //批量清除库
        sportMarketRelationDao.delBatch(relationMarketKeys);
    }
}
