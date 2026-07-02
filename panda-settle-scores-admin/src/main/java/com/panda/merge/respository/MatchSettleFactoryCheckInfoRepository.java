package com.panda.merge.respository;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.MatchSettleFactorCheckInfoMapper;
import com.panda.merge.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.RepositoryConstant.*;


@Service
@Slf4j
public class MatchSettleFactoryCheckInfoRepository {

    @Autowired
    RedisService redisService;
    @Autowired
    MatchSettleFactorCheckInfoMapper matchSettleFactorCheckInfoMapper;

    @Autowired
    ApplicationContext applicationContext;

    public List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoListCaseOne(Long standardMatchId,String settleNum){
        String key = MATCH_SETTLE_FACTOR_CHECK_INFO+standardMatchId+"_"+settleNum;
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
            MatchSettleFactorCheckInfoExample matchSettleFactorCheckInfoExample = new MatchSettleFactorCheckInfoExample();
            matchSettleFactorCheckInfoExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId)
                    .andSettleNumEqualTo(settleNum);
            matchSettleFactorCheckInfos =matchSettleFactorCheckInfoMapper.selectByExample(matchSettleFactorCheckInfoExample);

            if (!CollectionUtils.isEmpty(matchSettleFactorCheckInfos)){
                try{
                    redisService.set(key,JSONObject.toJSON(matchSettleFactorCheckInfos),REDIS_THREE_TIME);
                }catch (Exception e){
                    log.error("matchSettleFactorCheckInfo:redis写入异常matchSettleFactorCheckInfoListCaseOne：key=[{}]matchSettleFactorCheckInfos[{}]", key,JSONObject.toJSON(matchSettleFactorCheckInfos), e);
                }

            }
        }
        return matchSettleFactorCheckInfos;
    }

    public List<MatchSettleFactorCheckInfo> matchSettleFactorCheckInfoListCaseTwo(Long standardMatchId,List<String> settleNums){
        List<String> redisKeys = settleNums.stream().map(t->MATCH_SETTLE_FACTOR_CHECK_INFO+standardMatchId+"_"+t).collect(Collectors.toList());
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
            MatchSettleFactorCheckInfoExample matchSettleFactorCheckInfoExample = new MatchSettleFactorCheckInfoExample();
            matchSettleFactorCheckInfoExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId)
                    .andSettleNumIn(unExistSettleNums);
            List<MatchSettleFactorCheckInfo> checkInfoList =matchSettleFactorCheckInfoMapper.selectByExample(matchSettleFactorCheckInfoExample);
            matchSettleFactorCheckInfos.addAll(checkInfoList);
            if (!CollectionUtils.isEmpty(checkInfoList)){
                try{
                    Map<String, List<MatchSettleFactorCheckInfo>> redisValue = checkInfoList.stream().collect(Collectors.groupingBy(t->MATCH_SETTLE_FACTOR_CHECK_INFO+standardMatchId+"_"+t.getSettleNum()));
                    Map<String, Object> normRedisValue = redisValue.entrySet().stream().collect(Collectors.toMap(t->t.getKey(), t->JSONObject.toJSON(t.getValue())));
                    redisService.mSetExpire(normRedisValue, REDIS_THREE_TIME);
                    log.info("temp test standardMatchId:{} settleNums:{} normRedisValue:{}", standardMatchId, settleNums, normRedisValue);
                }catch (Exception e){
                    log.error("matchSettleFactorCheckInfo:redis写入异常", e);
                }
            }
        }
        return matchSettleFactorCheckInfos;
    }
    public void updateMatchSettleFactorCheckInfoToRedis(MatchSettleFactorCheckInfo info,boolean isInsert){
        String key = MATCH_SETTLE_FACTOR_CHECK_INFO+info.getStandardMatchId()+"_"+info.getSettleNum();
        try {
            redisService.set(key,JSONObject.toJSON(info),REDIS_THREE_TIME);
            applicationContext.getBean(MatchSettleFactoryCheckInfoRepository.class).updateOrInsertMatchSettleFactorCheckInfoByDataBase(info, isInsert);
        }catch (Exception e){
            log.error("updateStandardMatchInfoToRedis:redis插入异常：key=[{}]StandardMatchInfo[{}]Msg[{}]", key,JSONObject.toJSON(info), e);
        }
    }

    @Async("RemoveDBThreadPool")
    void updateOrInsertMatchSettleFactorCheckInfoByDataBase(MatchSettleFactorCheckInfo info, boolean isInsert){
        if (isInsert) {
            matchSettleFactorCheckInfoMapper.insert(info);
        } else {
            matchSettleFactorCheckInfoMapper.updateByPrimaryKey(info);
        }
    }
}
