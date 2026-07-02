package com.panda.merge.v2.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.model.MatchSettleDataSourceSwitch;
import com.panda.merge.model.MatchSettleDataSourceSwitchExample;
import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
import com.panda.merge.v2.converter.MatchSettleDataSourceSwitchConverter;
import com.panda.merge.v2.entity.MatchSettleDataSourceSwitchEntity;
import com.panda.merge.v2.mapper.MatchSettleDataSourceSwitchV2Mapper;
import com.panda.merge.v2.repository.MatchSettleDataSourceSwitchRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Repository("MatchSettleDataSourceSwitchRepositoryV2")
public class MatchSettleDataSourceSwitchRepositoryImpl extends ServiceImpl<MatchSettleDataSourceSwitchV2Mapper, MatchSettleDataSourceSwitchEntity> implements MatchSettleDataSourceSwitchRepository {
    @Autowired
    private MatchSettleDataSourceSwitchConverter matchSettleDataSourceSwitchConverter;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchSettleSPOddsProducer matchSettleSPOddsProducer;

    @Override
    public boolean updateDataSourceSwitchToRedis(MatchSettleDataSourceSwitch dataSourceSwitch, boolean isInsert) {
        String key = "MATCH_SETTLE_DATA_SOURCE_SWITCH_INFO:"+dataSourceSwitch.getSportId()+"_"+dataSourceSwitch.getDataSourceCode();
        try {
            redisService.set(key,JSONObject.toJSON(Arrays.asList(dataSourceSwitch)), RepositoryConstant.REDIS_THREE_TIME);
            MatchSettleDataSourceSwitchEntity entity = matchSettleDataSourceSwitchConverter.convertDataSourceSwitchToEntity(dataSourceSwitch);
            return applicationContext.getBean(MatchSettleDataSourceSwitchRepositoryImpl.class).updateOrInsertAsync(entity, isInsert);
        } catch (Exception e) {
            log.error("updateMatchSettleInfoToRedis:redis插入异常：key=[{}]MatchSettleInfo[{}]Msg[{}]", key,JSONObject.toJSON(dataSourceSwitch), e);
            return false;
        }
    }

    @Override
    public List<MatchSettleDataSourceSwitch> getModelBySportIdAndDataSource(Long sportId, String dataSource, String gray) {
        LambdaQueryWrapper<MatchSettleDataSourceSwitchEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(sportId != null, MatchSettleDataSourceSwitchEntity::getSportId, sportId)
                .eq(StringUtils.isNotBlank(dataSource), MatchSettleDataSourceSwitchEntity::getDataSourceCode, dataSource)
                .eq(StringUtils.isNotBlank(gray), MatchSettleDataSourceSwitchEntity::getGray, gray);
        List<MatchSettleDataSourceSwitchEntity> entities = this.list(queryWrapper);
        return matchSettleDataSourceSwitchConverter.convertEntityToSettleDataSource(entities);
    }

    @Override
    public List<MatchSettleDataSourceSwitch> getMatchSettleDataSourceSwitchByRedis(Long sportId, String dataSource) {
        String key = "MATCH_SETTLE_DATA_SOURCE_SWITCH_INFO:"+sportId+"_"+dataSource;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常MatchSettleDataSourceSwitch：key:"+key, e);
        }
        List<MatchSettleDataSourceSwitch> switches  =null;
        if (o != null) {
            switches = JSONObject.parseArray(o.toString(), MatchSettleDataSourceSwitch.class);
            return switches;
        }else{
            switches = getModelBySportIdAndDataSource(sportId, dataSource, null);
            if (null!= switches){
                try{
                    redisService.set(key,JSONObject.toJSON(switches),RepositoryConstant.REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("MatchSettleDataSourceSwitch:redis写入异常getMatchSettleDataSourceSwitchByRedis：key=[{}]StandardMatchInfo[{}]", key,JSONObject.toJSON(switches), e);
                }

            }
        }
        return switches;
    }

    @Override
    public boolean delMatchSettleDataSourceSwitchBy(Long sportId, String dataSource) {
        LambdaQueryWrapper<MatchSettleDataSourceSwitchEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(sportId != null, MatchSettleDataSourceSwitchEntity::getSportId, sportId)
                .eq(StringUtils.isNotBlank(dataSource), MatchSettleDataSourceSwitchEntity::getDataSourceCode, dataSource)
                ;
        return this.remove(queryWrapper);
    }

    boolean updateOrInsertAsync(MatchSettleDataSourceSwitchEntity info, boolean isInsert){
        String linkId = "match-settle-data-source-switch-"+info.getId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, Arrays.asList(info),
                CommonConstant.SETTLE_DATA_SOURCE_SWITCH_TABLE, isInsert);
        return true;
    }

}
