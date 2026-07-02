package com.panda.merge.respository;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.mapper.MatchEventInfoMapper;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchEventInfoExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.panda.merge.constant.RepositoryConstant.*;


@Service
@Slf4j
public class MatchEventInfoRepository {

    @Autowired
    RedisService redisService;
    @Autowired
    private MatchEventInfoMapper matchEventInfoMapper;


    public List<MatchEventInfo> getMatchEventInfoCaseOne(Long thirdMatchId,String extraInfo,String dataSourceCode,Long sportId) {
        String key = MATCH_EVENT_INFO+thirdMatchId+"_"+extraInfo+"_"+dataSourceCode+"_"+sportId;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常MatchEventInfo：key:"+key, e);
        }
        List<MatchEventInfo> matchEventInfos =null;
        if (o != null) {
            matchEventInfos = JSONObject.parseArray(o.toString(), MatchEventInfo.class);
                return matchEventInfos;
        }else{
            log.info("matcheventInfo1: {}, {}, {}, {}", thirdMatchId, extraInfo, dataSourceCode, sportId);
            MatchEventInfoExample matchEventInfoExample=new MatchEventInfoExample();
            matchEventInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andThirdEventIdEqualTo(extraInfo).andDataSourceCodeEqualTo(dataSourceCode).andSportIdEqualTo(sportId);
            matchEventInfos =matchEventInfoMapper.selectByExample(matchEventInfoExample);
            log.info("matcheventInfo1: {}", matchEventInfos);
            if (!CollectionUtils.isEmpty(matchEventInfos)){
                try{
                    redisService.set(key,JSONObject.toJSON(matchEventInfos),REDIS_TWELVE_TIME);
                }catch (Exception e){
                    log.error("MatchEventInfo:redis写入异常getMatchEventInfoCaseOne：key=[{}]MatchSettleFactorCheckInfo[{}]", key,JSONObject.toJSON(matchEventInfos), e);
                }
            }
        }
        return matchEventInfos;
    }

    public void cacheMatchEventInfo(MatchEventInfo matchEventInfo){
        String keySingle = MATCH_EVENT_INFO+matchEventInfo.getThirdMatchId()+"_"+matchEventInfo.getThirdEventId()+"_"+matchEventInfo.getDataSourceCode()+"_"+matchEventInfo.getSportId();
        String keyMul = MATCH_EVENT_INFO+matchEventInfo.getThirdMatchId()+"_"+matchEventInfo.getDataSourceCode();

        try{
            redisService.set(keySingle,JSONObject.toJSON(Arrays.asList(matchEventInfo)),REDIS_TWELVE_TIME);
            Object o = redisService.get(keyMul);
            List<MatchEventInfo> matchEventInfos = new ArrayList<>();
            if (o != null) {
                matchEventInfos = JSONObject.parseArray(o.toString(), MatchEventInfo.class);
            }
            matchEventInfos.add(matchEventInfo);
            redisService.set(keyMul,JSONObject.toJSON(matchEventInfos),REDIS_TWELVE_TIME);
        }catch (Exception e){
            log.error("redis缓存cacheMatchEventInfo异常", e);
        }
    }

    public List<MatchEventInfo> getMatchEventInfoCaseTwo(Long thirdMatchId,List<String>  eventCodes,List<Long> periods,String dataSourceCode ,Long id,Long eventTime) {
        String key = MATCH_EVENT_INFO+thirdMatchId+"_"+dataSourceCode;
        Object o = null;
        try{
            o = redisService.get(key);
        }catch (Exception e){
            log.error("redis读异常MatchEventInfo：key:"+key, e);
        }
        List<MatchEventInfo> matchEventInfos =null;
        if (o != null) {
            matchEventInfos = JSONObject.parseArray(o.toString(), MatchEventInfo.class);
        }else{
            try{
                MatchEventInfoExample eventInfoExample = new MatchEventInfoExample();
                eventInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchId).andEventCodeIn(eventCodes).andMatchPeriodIdIn(periods)
                        .andDataSourceCodeEqualTo(dataSourceCode).andIdNotEqualTo(id).andEventTimeLessThanOrEqualTo(eventTime).andSourceTypeEqualTo(1);
                matchEventInfos =matchEventInfoMapper.selectByExample(eventInfoExample);
                log.info("matcheventInfo2: {}", matchEventInfos);
                redisService.set(key,JSONObject.toJSON(matchEventInfos),REDIS_TWELVE_TIME);
            }catch (Exception e){
                log.error("MatchEventInfo:redis写入异常getMatchEventInfoCaseOne：key=[{}]MatchSettleFactorCheckInfo[{}]", key,JSONObject.toJSON(matchEventInfos), e);
            }
        }
        if (!CollectionUtils.isEmpty(matchEventInfos)){
            matchEventInfos = matchEventInfos.stream().filter(t->{
                if(eventCodes.contains(t.getEventCode()) && periods.contains(t.getMatchPeriodId()) && (!t.getId().equals(id))
                        && eventTime >= t.getEventTime() && t.getSourceType() == 1){
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }
        return matchEventInfos;
    }
}
