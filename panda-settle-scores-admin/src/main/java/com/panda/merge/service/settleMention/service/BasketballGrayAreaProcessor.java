package com.panda.merge.service.settleMention.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleEventCodeEnum;
import com.panda.merge.constant.SettleMentionEnum;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.service.settleMention.dto.BasketballMentionStatus;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @description: settle mention football score mismatch implementation
 * @author: Henry Wang
 * @create: 2024-08-28 13:39
 **/
@Slf4j
@Component
public class BasketballGrayAreaProcessor extends AbstractSettleMentionProcessor<BasketballMentionStatus>{
    @Override
    protected Object obtainData(Map<String, Object> parameters) {
        return parameters.get("matchSettleScore");
    }

    @Override
    protected Object buildData(Object object) {
        MatchSettleScore matchSettleScore = (MatchSettleScore) object;
        String redisKey = redisKey(matchSettleScore.getStandardMatchId());
        BasketballMentionStatus basketballMentionStatus = (BasketballMentionStatus) querySettleMentionFromRedis(redisKey);
        if (basketballMentionStatus == null && matchSettleScore.getIsGrey() == 0) {
            return null;
        } else {
            basketballMentionStatus = BasketballMentionStatus.buildInstance();
        }
        FootballMentionStatus.EventStatus eventStatus = basketballMentionStatus.getGoalStatus();
        if (eventStatus.getDetailStatus() != null && eventStatus.getDetailStatus().containsKey(matchSettleScore.getId().toString()) && matchSettleScore.getIsGrey() == 0) {
            eventStatus.getDetailStatus().remove(matchSettleScore.getId().toString());
            if(eventStatus.getDetailStatus().isEmpty()) {
                eventStatus.setStatus(null);
                eventStatus.setDetailStatus(null);
            }
        }
        if (matchSettleScore.getIsGrey() == 1) {
            eventStatus.getDetailStatus().put(matchSettleScore.getId().toString(), CommonConstant.COMMON_TRUE_FLAG);
            eventStatus.setStatus(CommonConstant.COMMON_TRUE_FLAG);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("redisKey", redisKey);
        result.put("redisValue", basketballMentionStatus);
        log.info("[BasketballGrayAreaProcessor] addSettleMention buildData with result:{}!", result);
        return result;
    }

    @Override
    protected Object buildDataForDelete(Long matchId, List<String> keys, SettleEventCodeEnum settleEventCodeEnum) {
        Map<String, Integer> keyMap = keys.stream().collect(Collectors.toMap(t->t, t->1, (t1, t2)->t1));
        BasketballMentionStatus mentionStatus = (BasketballMentionStatus) querySettleMentionFromRedis(redisKey(matchId));
        if(mentionStatus == null || mentionStatus.getGoalStatus() == null || mentionStatus.getGoalStatus().getDetailStatus() == null) {
            return null;
        }
        BasketballMentionStatus.EventStatus eventStatus = mentionStatus.getGoalStatus();
        Iterator<Map.Entry<String, Object>> it = eventStatus.getDetailStatus().entrySet().iterator();
        while (it.hasNext()) {
            if(keyMap.containsKey(it.next().getKey())){
                it.remove();
            }
        }
        if(MapUtils.isEmpty(eventStatus.getDetailStatus())) {
            eventStatus.setStatus(null);
            eventStatus.setDetailStatus(null);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("redisKey", redisKey(matchId));
        result.put("redisValue", mentionStatus);
        log.info("[BasketballGrayAreaProcessor] addSettleMention buildDataForDelete with result:{}!", result);
        return result;
    }

    @Override
    protected SettleMentionEnum settleMention(){
        return SettleMentionEnum.BASKETBALL_GRAY_AREA;
    }

    @Override
    TypeReference<BasketballMentionStatus> typeReference() {
        return new TypeReference<BasketballMentionStatus>(){};
    }

}
