package com.panda.merge.v2.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.model.MatchSettleDataSourceConfig;
import com.panda.merge.model.MatchSettleDataSourceConfigExample;
import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
import com.panda.merge.v2.converter.MatchSettleDataSourceConfigConverter;
import com.panda.merge.v2.entity.MatchSettleDataSourceConfigEntity;
import com.panda.merge.v2.mapper.MatchSettleDataSourceConfigV2Mapper;
import com.panda.merge.v2.repository.MatchSettleDataSourceConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Repository("MatchSettleDataSourceConfigRepositoryV2")
public class MatchSettleDataSourceConfigRepositoryImpl extends ServiceImpl<MatchSettleDataSourceConfigV2Mapper, MatchSettleDataSourceConfigEntity> implements MatchSettleDataSourceConfigRepository {
    @Autowired
    private MatchSettleDataSourceConfigConverter matchSettleDataSourceConfigConverter;

    private MatchSettleDataSourceConfigV2Mapper matchSettleDataSourceConfigV2Mapper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RedisService redisService;

    @Autowired
    private MatchSettleSPOddsProducer matchSettleSPOddsProducer;

    @Override
    public boolean updateDataSourceConfigToRedis(MatchSettleDataSourceConfig matchSettleDataSourceConfig,boolean isInsert) {
        String key1 = RepositoryConstant.MATCH_SETTLE_DATA_SOURCE_CONFIG+matchSettleDataSourceConfig.getId();
        String key2 = RepositoryConstant.MATCH_SETTLE_DATA_SOURCE_CONFIG+matchSettleDataSourceConfig.getTournamentLevel()+matchSettleDataSourceConfig.getDataSourceCode()+matchSettleDataSourceConfig.getSportId();
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(key1, JSONObject.toJSON(matchSettleDataSourceConfig));
            map.put(key2, JSONObject.toJSON(Arrays.asList(matchSettleDataSourceConfig)));
            redisService.mSetExpire(map,RepositoryConstant.REDIS_THREE_TIME);
            MatchSettleDataSourceConfigEntity entity = matchSettleDataSourceConfigConverter.convertSettleDataSourceToEntity(matchSettleDataSourceConfig);
            return applicationContext.getBean(MatchSettleDataSourceConfigRepositoryImpl.class).updateOrInsertAsync(entity, isInsert);
        } catch (Exception e) {
            log.error("MatchSettleDataSourceConfig:redis插入异常：key=[{}]MatchSettleInfo[{}]Msg[{}]", key1,JSONObject.toJSON(matchSettleDataSourceConfig), e);
            return false;
        }
    }

    @Override
    public MatchSettleDataSourceConfig getByIdFromRedis(Long id) {
        String key = RepositoryConstant.MATCH_SETTLE_DATA_SOURCE_CONFIG+id;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常MatchSettleDataSourceConfig：key:"+key, e);
        }
        MatchSettleDataSourceConfig matchSettleDataSourceConfig =null;
        if (o != null) {
            matchSettleDataSourceConfig = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleDataSourceConfig.class);
            return matchSettleDataSourceConfig;
        }else{
            matchSettleDataSourceConfig = getById(id);
            if (null!= matchSettleDataSourceConfig){
                try{
                    redisService.set(key,JSONObject.toJSON(matchSettleDataSourceConfig),RepositoryConstant.REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("MatchSettleDataSourceConfig:redis写入异常key=[{}]MatchSettleDataSourceConfig[{}]", key,JSONObject.toJSON(matchSettleDataSourceConfig), e);
                }

            }
        }
        return matchSettleDataSourceConfig;
    }

    @Override
    public List<MatchSettleDataSourceConfig> getMatchSettleDataSourceConfig(Integer level,Long sportId,String dataSourceCode) {
        String key = RepositoryConstant.MATCH_SETTLE_DATA_SOURCE_CONFIG+level+dataSourceCode+sportId;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常StandardMatchInfo：key:"+key, e);
        }
        List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigList=null;
        if (o != null) {
            matchSettleDataSourceConfigList = JSONObject.parseArray(o.toString(), MatchSettleDataSourceConfig.class);
            return matchSettleDataSourceConfigList;
        }else{
            matchSettleDataSourceConfigList = getModelByLevelAndSportAndDataSourceCode(level, sportId, dataSourceCode);

            if (!CollectionUtils.isEmpty(matchSettleDataSourceConfigList)){
                try{
                    redisService.set(key,JSONObject.toJSON(matchSettleDataSourceConfigList),RepositoryConstant.REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("matchSettleDataSourceConfigList:redis写入异常matchSettleDataSourceConfigList：key=[{}]StandardMatchInfo[{}]", key,JSONObject.toJSON(matchSettleDataSourceConfigList), e);
                }

            }
        }
        return matchSettleDataSourceConfigList;
    }

    @Override
    public List<MatchSettleDataSourceConfig> selectByExample(MatchSettleDataSourceConfigExample example) {
        List<MatchSettleDataSourceConfigEntity> entities = matchSettleDataSourceConfigV2Mapper.selectByExample(example);
        return matchSettleDataSourceConfigConverter.convertEntityToSettleDataSource(entities);
    }

    private List<MatchSettleDataSourceConfig> getModelByLevelAndSportAndDataSourceCode(Integer level, Long sportId, String dataSourceCode) {
        LambdaQueryWrapper<MatchSettleDataSourceConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(level != null, MatchSettleDataSourceConfigEntity::getTournamentLevel, level)
                .eq(sportId != null, MatchSettleDataSourceConfigEntity::getSportId, sportId)
                .eq(StringUtils.isNotBlank(dataSourceCode), MatchSettleDataSourceConfigEntity::getDataSourceCode, dataSourceCode);
        List<MatchSettleDataSourceConfigEntity> entities = this.list(queryWrapper);
        return matchSettleDataSourceConfigConverter.convertEntityToSettleDataSource(entities);
    }

    public MatchSettleDataSourceConfig getById(Long id) {
        MatchSettleDataSourceConfigEntity entity = super.getById(id);
        return matchSettleDataSourceConfigConverter.convertEntityToSettleDataSource(entity);
    }

    boolean updateOrInsertAsync(MatchSettleDataSourceConfigEntity entity, boolean isInsert){
        String linkId = "match-settle-data-source-config-"+entity.getId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, Arrays.asList(entity),
                CommonConstant.SETTLE_DATA_SOURCE_CONFIG_TABLE, isInsert);
        return true;
    }
}
