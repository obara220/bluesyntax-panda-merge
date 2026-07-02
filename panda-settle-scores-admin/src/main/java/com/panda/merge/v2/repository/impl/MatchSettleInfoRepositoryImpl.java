package com.panda.merge.v2.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.model.MatchSettleInfo;
import com.panda.merge.model.MatchSettleInfoExample;
import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
import com.panda.merge.v2.converter.MatchSettleInfoConverter;
import com.panda.merge.v2.entity.MatchSettleInfoEntity;
import com.panda.merge.v2.mapper.MatchSettleInfoV2Mapper;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Repository("MatchSettleInfoRepositoryV2")
public class MatchSettleInfoRepositoryImpl extends ServiceImpl<MatchSettleInfoV2Mapper, MatchSettleInfoEntity> implements MatchSettleInfoRepository {
    @Autowired
    RedisService redisService;
    @Autowired
    private MatchSettleInfoConverter matchSettleInfoConverter;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MatchSettleInfoV2Mapper matchSettleInfoV2Mapper;
    @Autowired
    private MatchSettleSPOddsProducer matchSettleSPOddsProducer;

    @Override
    public MatchSettleInfoEntity getMatchSettleInfo(Long id) {
        String key = RepositoryConstant.MATCH_SETTLE_INFO+id;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常MatchSettleInfo：key:"+key, e);
        }
        MatchSettleInfoEntity matchSettleInfo =null;
        if (o != null) {
            matchSettleInfo = JSONObject.toJavaObject(JSONObject.parseObject(o.toString()), MatchSettleInfoEntity.class);
            return matchSettleInfo;
        }else{
            matchSettleInfo = super.getById(id);
            if (null!= matchSettleInfo){
                try{
                    redisService.set(key,JSONObject.toJSON(matchSettleInfo),RepositoryConstant.REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("MatchSettleInfo:redis写入异常key=[{}]MatchSettleInfo[{}]", key,JSONObject.toJSON(matchSettleInfo), e);
                }

            }
        }
        return matchSettleInfo;
    }

    @Override
    public MatchSettleInfo getModelMatchSettleInfo(Long id) {
        MatchSettleInfoEntity entity = getMatchSettleInfo(id);
        return matchSettleInfoConverter.convertMatchSettleInfoEntityToInfo(entity);
    }

    @Override
    public  void updateMatchSettleInfoToRedis(MatchSettleInfoEntity info,boolean isInsert ){
        String key = RepositoryConstant.MATCH_SETTLE_INFO+info.getId();
        try {
            redisService.set(key,JSONObject.toJSON(info), RepositoryConstant.REDIS_THREE_TIME);
            applicationContext.getBean(MatchSettleInfoRepositoryImpl.class).updateOrInsertMatchSettleInfoByDataBase(Arrays.asList(info), isInsert);
        }catch (Exception e){
            log.error("updateMatchSettleInfoToRedis:redis插入异常：key=[{}]MatchSettleInfo[{}]Msg[{}]", key,JSONObject.toJSON(info), e);
        }
    }

    @Override
    public  void batchSaveOrUpdateToRedis(List<MatchSettleInfoEntity> info,boolean isInsert){
        Map<String, Object> settleInfoMap = info.stream().collect(Collectors.toMap(t->RepositoryConstant.MATCH_SETTLE_INFO+t.getId(),t->JSONObject.toJSON(t), (v1, v2)->v1));
        try {
            redisService.mSetExpire(settleInfoMap, RepositoryConstant.REDIS_THREE_TIME);
            applicationContext.getBean(MatchSettleInfoRepositoryImpl.class).updateOrInsertMatchSettleInfoByDataBase(info, isInsert);
        }catch (Exception e){
            log.error("updateMatchSettleInfoToRedis:redis插入异常：",  e);
        }
    }

    @Override
    public void updateMatchSettleInfoToRedis(MatchSettleInfo matchSettleInfo, boolean tag) {
        MatchSettleInfoEntity entity = matchSettleInfoConverter.convertMatchSettleInfoToEntity(matchSettleInfo);
        updateMatchSettleInfoToRedis(entity, tag);
    }

    @Override
    public MatchSettleInfo getById(Long id) {
        MatchSettleInfoEntity entity = getMatchSettleInfo(id);
        return matchSettleInfoConverter.convertMatchSettleInfoEntityToInfo(entity);
    }

    @Override
    public List<MatchSettleInfoEntity> selectByExample(MatchSettleInfoExample example) {
        return matchSettleInfoV2Mapper.selectByExample(example);
    }

    /**
     * 使用时需要非常小心-尽量不要使用这个方法 - 如果非要使用，需要进行相应的redis缓存同步
     */
    @Override
    public int updateByExampleSelective(MatchSettleInfoEntity record, MatchSettleInfoExample example) {
        return matchSettleInfoV2Mapper.updateByExampleSelective(record,example);
    }

    @Override
    public List<MatchSettleInfo> selectByCurIdAndLimit(Long curId, int limit) {
        LambdaQueryWrapper<MatchSettleInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .select(MatchSettleInfoEntity::getId)
                .gt(curId != null, MatchSettleInfoEntity::getId, curId)
                .orderByAsc(MatchSettleInfoEntity::getId)
                .last(" limit " + limit);
        List<MatchSettleInfoEntity> entities = this.list(queryWrapper);
        return matchSettleInfoConverter.convertMatchSettleInfoEntityToInfo(entities);
    }

    @Override
    public MatchSettleInfo getOneFromDB(Long id) {
        MatchSettleInfoEntity entity = super.getById(id);
        return matchSettleInfoConverter.convertMatchSettleInfoEntityToInfo(entity);
    }

    void updateOrInsertMatchSettleInfoByDataBase(List<MatchSettleInfoEntity> info, boolean isInsert){
        String linkId = "match-settle-info-"+info.get(0).getStandardMatchId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, Arrays.asList(info.toArray()),
                CommonConstant.SETTLE_INFO_TABLE, isInsert);
    }



}
