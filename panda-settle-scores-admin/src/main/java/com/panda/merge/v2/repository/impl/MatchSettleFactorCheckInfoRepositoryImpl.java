package com.panda.merge.v2.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.model.MatchSettleFactorCheckInfo;
import com.panda.merge.mq.producer.MatchSettleSPOddsProducer;
import com.panda.merge.v2.converter.MatchSettleFactorCheckInfoConverter;
import com.panda.merge.v2.entity.MatchSettleFactorCheckInfoEntity;
import com.panda.merge.v2.mapper.MatchSettleFactorCheckInfoV2Mapper;
import com.panda.merge.v2.repository.MatchSettleFactorCheckInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository("MatchSettleFactorCheckInfoRepositoryV2")
public class MatchSettleFactorCheckInfoRepositoryImpl extends ServiceImpl<MatchSettleFactorCheckInfoV2Mapper, MatchSettleFactorCheckInfoEntity> implements MatchSettleFactorCheckInfoRepository {
    @Autowired
    private MatchSettleFactorCheckInfoConverter matchSettleFactorCheckInfoConverter;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchSettleSPOddsProducer matchSettleSPOddsProducer;

    public List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoListCaseOne(Long standardMatchId, String settleNum){
        String key = RepositoryConstant.MATCH_SETTLE_FACTOR_CHECK_INFO+standardMatchId+"_"+settleNum;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常MatchEventInfo：key:"+key, e);
        }
        List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfos =null;
        if (o != null) {
            matchSettleFactorCheckInfos = JSONObject.parseArray(o.toString(), MatchSettleFactorCheckInfo.class);
            return matchSettleFactorCheckInfos;
        }else{
            matchSettleFactorCheckInfos = getByStandardMatchIdAndSettleNums(standardMatchId, Arrays.asList(settleNum));
            if (!CollectionUtils.isEmpty(matchSettleFactorCheckInfos)){
                try{
                    redisService.set(key,JSONObject.toJSON(matchSettleFactorCheckInfos), RepositoryConstant.REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("matchSettleFactorCheckInfo:redis写入异常matchSettleFactorCheckInfoListCaseOne：key=[{}]matchSettleFactorCheckInfos[{}]", key,JSONObject.toJSON(matchSettleFactorCheckInfos), e);
                }

            }
        }
        return matchSettleFactorCheckInfos;
    }

    public List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoListCaseTwo(Long standardMatchId,List<String> settleNums){
        List<String> redisKeys = settleNums.stream().map(t->RepositoryConstant.MATCH_SETTLE_FACTOR_CHECK_INFO+standardMatchId+"_"+t).collect(Collectors.toList());
        List<Object> objects = new ArrayList<>();
        try{
            objects = redisService.mGet(redisKeys);
            log.info("temp test standardMatchId:{} settleNums:{} objects:{}", standardMatchId, settleNums, objects);
        }catch (Exception e){
            log.error("redis读异常MatchEventInfo：key:"+redisKeys, e);
        }
        List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfos = new ArrayList<>();
        List<String> unExistSettleNums = new ArrayList<>();
        for(int i =0; i < objects.size(); i++) {
            Object o = objects.get(i);
            if (o != null) {
                List<MatchSettleFactorCheckInfo> checkInfoList = JSONObject.parseArray(o.toString(), MatchSettleFactorCheckInfo.class);
                matchSettleFactorCheckInfos.addAll(checkInfoList);
            } else {
                unExistSettleNums.add(settleNums.get(i));
            }
        }

        if (!CollectionUtils.isEmpty(unExistSettleNums)) {
            List<MatchSettleFactorCheckInfo> checkInfoList = getByStandardMatchIdAndSettleNums(standardMatchId, unExistSettleNums);
            matchSettleFactorCheckInfos.addAll(checkInfoList);
            if (!CollectionUtils.isEmpty(checkInfoList)){
                try{
                    Map<String, List<MatchSettleFactorCheckInfo>> redisValue = checkInfoList.stream().collect(Collectors.groupingBy(t->RepositoryConstant.MATCH_SETTLE_FACTOR_CHECK_INFO+standardMatchId+"_"+t.getSettleNum()));
                    Map<String, Object> normRedisValue = redisValue.entrySet().stream().collect(Collectors.toMap(t->t.getKey(), t->JSONObject.toJSON(t.getValue())));
                    redisService.mSetExpire(normRedisValue, RepositoryConstant.REDIS_THREE_TIME);
                    log.info("temp test standardMatchId:{} settleNums:{} normRedisValue:{}", standardMatchId, settleNums, normRedisValue);
                }catch (Exception e){
                    log.error("matchSettleFactorCheckInfo:redis写入异常", e);
                }
            }
        }
        return matchSettleFactorCheckInfos;
    }
@Override
    public void updateMatchSettleFactorCheckInfoToRedis(MatchSettleFactorCheckInfo info,boolean isInsert){
        String key = RepositoryConstant.MATCH_SETTLE_FACTOR_CHECK_INFO+info.getStandardMatchId()+"_"+info.getSettleNum();
        try {
            redisService.set(key,JSONObject.toJSON(Arrays.asList(info)),RepositoryConstant.REDIS_THREE_TIME);
            MatchSettleFactorCheckInfoEntity entity = matchSettleFactorCheckInfoConverter.convertSettleFactorCheckInfoToEntity(info);
            applicationContext.getBean(MatchSettleFactorCheckInfoRepositoryImpl.class).updateOrInsertAsync(entity, isInsert);
        }catch (Exception e){
            log.error("updateMatchSettleFactorCheckInfoToRedis:redis插入异常：key=[{}]StandardMatchInfo[{}]Msg[{}]", key,JSONObject.toJSON(info), e);
        }
    }

    private List<MatchSettleFactorCheckInfo> getByStandardMatchIdAndSettleNums(Long standardMatchId, List<String> settleNums) {
        LambdaQueryWrapper<MatchSettleFactorCheckInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleFactorCheckInfoEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(settleNums), MatchSettleFactorCheckInfoEntity::getSettleNum, settleNums);
        List<MatchSettleFactorCheckInfoEntity> entities = this.list(queryWrapper);
        return matchSettleFactorCheckInfoConverter.convertEntityToSettleFactorCheckInfo(entities);
    }

    void updateOrInsertAsync(MatchSettleFactorCheckInfoEntity entity, boolean isInsert){
        String linkId = "match-settle-factor-check-info-"+entity.getId();
        matchSettleSPOddsProducer.sendToSlaveMq(CommonConstant.SETTLE_SLAVE_DB_TOPIC, linkId, Arrays.asList(entity),
                CommonConstant.SETTLE_FACTOR_CHECK_INFO_TABLE, isInsert);
    }
}
