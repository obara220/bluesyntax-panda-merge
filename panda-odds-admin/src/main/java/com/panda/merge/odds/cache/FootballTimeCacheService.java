package com.panda.merge.odds.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.odds.model.MatchMarketMessageData;
import com.panda.merge.odds.model.MatchTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * FootballTimeCacheService
 *
 * @description:
 * @date: 5/18/2025
 **/
@Slf4j
@Service
public class FootballTimeCacheService {

    @Autowired
    private RedisService redisService;


    public MatchTime get(MatchMarketMessageData matchData) {
        if (matchData.isMatchTimeCacheEmpty) {
            return null;
        }
        if (matchData.matchTime != null) {
            return matchData.matchTime;
        }

        String redisKey =
                String.format(ConstantSystem.getStandardSecondsMatchStartKey(), matchData.standardMatchInfo.getId());
        Object o = redisService.get(redisKey);
        if (Objects.nonNull(o)) {
             matchData.matchTime = JSON.toJavaObject((JSONObject) o, MatchTime.class);
             log.info("linkId:{},matchId:{}, match time:{}",matchData.linkId,matchData.standardMatchInfo.getId(),matchData.matchTime);
             return matchData.matchTime;
        }
        matchData.isMatchTimeCacheEmpty = true;
        log.info("linkId:{},matchId:{}, empty match time",matchData.linkId,matchData.standardMatchInfo.getId());
        return null;
    }



}
