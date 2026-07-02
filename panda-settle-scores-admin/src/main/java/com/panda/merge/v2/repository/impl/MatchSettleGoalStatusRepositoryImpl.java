package com.panda.merge.v2.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.model.MatchSettleGoalStatus;
import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
import com.panda.merge.v2.converter.MatchSettleGoalStatusConverter;
import com.panda.merge.v2.entity.MatchSettleGoalStatusEntity;
import com.panda.merge.v2.mapper.MatchSettleGoalStatusV2Mapper;
import com.panda.merge.v2.repository.MatchSettleGoalStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Arrays;


@Slf4j
@Repository("MatchSettleGoalStatusRepositoryV2")
public class MatchSettleGoalStatusRepositoryImpl extends ServiceImpl<MatchSettleGoalStatusV2Mapper, MatchSettleGoalStatusEntity> implements MatchSettleGoalStatusRepository {
    @Autowired
    private MatchSettleGoalStatusConverter matchSettleGoalStatusConverter;
    @Autowired
    private RedisService redisService;
    @Autowired
    private  ApplicationContext applicationContext;
    @Autowired
    private MatchSettleSPOddsProducer matchSettleSPOddsProducer;

    @Override
    public MatchSettleGoalStatus getById(Long id) {
        return getByIdFromRedis(id);
    }

    public MatchSettleGoalStatus getByIdFromRedis(Long id) {
        String key = RepositoryConstant.MATCH_SETTLE_GOAL_STATUS+id;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常MatchSettleGoalStatus：key:"+key, e);
        }
        MatchSettleGoalStatus matchSettleGoalStatus =null;
        if (o != null) {
            matchSettleGoalStatus = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleGoalStatus.class);
            return matchSettleGoalStatus;
        }else{
            MatchSettleGoalStatusEntity entity = super.getById(id);
            matchSettleGoalStatus = matchSettleGoalStatusConverter.convertEntityToSettleGoalStatus(entity);
            if (null!= matchSettleGoalStatus){
                try{
                    redisService.set(key,JSONObject.toJSON(matchSettleGoalStatus), RepositoryConstant.REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("MatchSettleGoalStatus:redis写入异常key=[{}]MatchSettleGoalStatus[{}]", key,JSONObject.toJSON(matchSettleGoalStatus), e);
                }

            }

        }
        return matchSettleGoalStatus;
    }

    @Override
    public void updateOrInsertMatchSettleGoalStatus(MatchSettleGoalStatus matchSettleGoalStatus, boolean isInsert){
        String key = RepositoryConstant.MATCH_SETTLE_GOAL_STATUS+matchSettleGoalStatus.getId();
        try{
            redisService.set(key,JSONObject.toJSON(matchSettleGoalStatus),RepositoryConstant.REDIS_THREE_TIME);
            MatchSettleGoalStatusEntity entity = matchSettleGoalStatusConverter.convertSettleGoalStatusToEntity(matchSettleGoalStatus);
            applicationContext.getBean(MatchSettleGoalStatusRepositoryImpl.class).updateOrInsertMatchSettleGoalStatusByDataBase(entity, isInsert);
        }catch (Exception e){
            log.error("redis写入异常MatchSettleGoalStatus：key=[{}]", key, e);
        }
    }

    public void updateOrInsertMatchSettleGoalStatusByDataBase(MatchSettleGoalStatusEntity matchSettleGoalStatus, boolean isInsert){
        String linkId = "match-settle-goal-status-"+matchSettleGoalStatus.getId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, Arrays.asList(matchSettleGoalStatus),
                CommonConstant.SETTLE_GOAL_STATUS_TABLE, isInsert);
    }
}
