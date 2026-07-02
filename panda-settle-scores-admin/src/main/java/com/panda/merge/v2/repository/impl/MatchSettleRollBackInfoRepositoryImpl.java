package com.panda.merge.v2.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.model.MatchSettleRollBackInfo;
import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
import com.panda.merge.v2.converter.MatchSettleRollBackInfoConverter;
import com.panda.merge.v2.entity.MatchSettleRollBackInfoEntity;
import com.panda.merge.v2.mapper.MatchSettleRollBackInfoV3Mapper;
import com.panda.merge.v2.repository.MatchSettleRollBackInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository("MatchSettleRollBackInfoRepositoryV2")
public class MatchSettleRollBackInfoRepositoryImpl extends ServiceImpl<MatchSettleRollBackInfoV3Mapper, MatchSettleRollBackInfoEntity> implements MatchSettleRollBackInfoRepository {
    @Autowired
    RedisService redisService;
    @Autowired
    private MatchSettleRollBackInfoConverter matchSettleRollBackInfoConverter;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MatchSettleSPOddsProducer matchSettleSPOddsProducer;
    @Override
    public List<MatchSettleRollBackInfoEntity> getByMatchId(Long standardMatchId) {
        LambdaQueryWrapper<MatchSettleRollBackInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId!= null, MatchSettleRollBackInfoEntity::getStandardMatchId, standardMatchId);
        return super.list(queryWrapper);
    }

    @Override
    public List<MatchSettleRollBackInfo> getModelByMatchId(Long standardMatchId) {
        List<MatchSettleRollBackInfoEntity> entities = getByMatchId(standardMatchId);
        return matchSettleRollBackInfoConverter.convertEntityToRollBackInfo(entities);
    }

    @Override
    public MatchSettleRollBackInfoEntity getMatchSettleRollBackInfo(Long id) {
        String key = RepositoryConstant.MATCH_SETTLE_ROLL_BACK_INFO+id;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常MatchSettleRollBackInfo：key:"+key, e);
        }
        MatchSettleRollBackInfoEntity matchSettleRollBackInfo =null;
        if (o != null) {
            matchSettleRollBackInfo = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleRollBackInfoEntity.class);
            return matchSettleRollBackInfo;
        }else{
            matchSettleRollBackInfo =super.getById(id);
            if (null!= matchSettleRollBackInfo){
                try{
                    redisService.set(key,JSONObject.toJSON(matchSettleRollBackInfo),RepositoryConstant.REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("MatchSettleRollBackInfo:redis写入异常key=[{}]MatchSettleRollBackInfo[{}]", key,JSONObject.toJSON(matchSettleRollBackInfo), e);
                }

            }
        }
        return matchSettleRollBackInfo;
    }

    @Override
    public MatchSettleRollBackInfo getModelMatchSettleRollBackInfo(Long id) {
        MatchSettleRollBackInfoEntity entity = getMatchSettleRollBackInfo(id);
        return matchSettleRollBackInfoConverter.convertEntityToRollBackInfo(entity);
    }

    @Override
    public  void updateMatchSettleRollBackInfoToRedis(MatchSettleRollBackInfoEntity info,boolean isInsert){
        String key = RepositoryConstant.MATCH_SETTLE_ROLL_BACK_INFO+info.getId();
        try {
            redisService.set(key,JSONObject.toJSON(info), RepositoryConstant.REDIS_THREE_TIME);
            applicationContext.getBean(MatchSettleRollBackInfoRepositoryImpl.class).updateOrInsertMatchSettleRollBackInfoByDataBase(info,isInsert);
        }catch (Exception e){
            log.error("updateMatchSettleRollBackInfoToRedis:redis插入异常：key=[{}]MatchSettleRollBackInfo[{}]Msg[{}]", key,JSONObject.toJSON(info), e);
        }
    }

    @Override
    public void updateMatchSettleRollBackInfoToRedis(MatchSettleRollBackInfo info, boolean isInsert) {
        MatchSettleRollBackInfoEntity entity = matchSettleRollBackInfoConverter.convertRollBackInfoToEntity(info);
        updateMatchSettleRollBackInfoToRedis(info,isInsert);
    }

    void updateOrInsertMatchSettleRollBackInfoByDataBase(MatchSettleRollBackInfoEntity info, boolean isInsert){
        String linkId = "match-settle-rollback-info-"+info.getId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, Arrays.asList(info),
                CommonConstant.SETTLE_ROLL_BACK_INFO_TABLE, isInsert);
    }
}
