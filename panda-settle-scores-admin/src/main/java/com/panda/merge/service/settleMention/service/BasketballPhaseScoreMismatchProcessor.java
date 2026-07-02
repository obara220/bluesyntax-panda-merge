package com.panda.merge.service.settleMention.service;

import com.panda.merge.constant.*;
import com.panda.merge.service.settleMention.dto.BasketballMentionStatus;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * @description: settle mention football score mismatch implementation
 * @author: Henry Wang
 * @create: 2024-08-28 13:39
 **/
@Slf4j
@Component
public class BasketballPhaseScoreMismatchProcessor extends FootballPhaseScoreMismatchProcessor{
    @Override
    protected SettleMentionEnum settleMention(){
        return SettleMentionEnum.BASKETBALL_PHASE_SCORE_MISMATCH;
    }

    @Override
    protected Object buildData(Object object) {
        Map<String, Object> parameters = (Map<String, Object>) object;
        FootballMentionStatus footballMentionStatus = (FootballMentionStatus) querySettleMentionFromRedis(String.valueOf(parameters.get("redisKey")));
        BasketballMentionStatus basketballMentionStatus = null;
        if (footballMentionStatus == null) {
            basketballMentionStatus = BasketballMentionStatus.buildInstance();
        } else {
            basketballMentionStatus = footballMentionStatus.convertToBasketball();
        }
        log.info("[AbstractFootballProcessor] addSettleMention basketballMentionStatus:{}", basketballMentionStatus);
        SettleEventCodeEnum eventCode = (SettleEventCodeEnum)parameters.get("settleEventCodeEnum");
        log.info("[AbstractFootballProcessor] addSettleMention eventCode:{}", eventCode);
        if (basketballMentionStatus == null) {
            basketballMentionStatus = BasketballMentionStatus.buildInstance();
        }
        log.info("[AbstractFootballProcessor] addSettleMention interval");
        BasketballMentionStatus.EventStatus eventStatus = basketballMentionStatus.getDetailStatusFieldByEventCode(eventCode);
        log.info("[AbstractFootballProcessor] addSettleMention eventStatus:{}", eventStatus);
        Map<String, Integer> deleteEventMap = (Map<String, Integer>) parameters.get("redisValue");
        log.info("[AbstractFootballProcessor] addSettleMention deleteEventMap0:{}", deleteEventMap);
        if (eventStatus.getDetailStatus() != null) {
            // 将 Map<String, Object> 中的值转换为 Integer
            for (Map.Entry<String, Object> entry : eventStatus.getDetailStatus().entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Integer) {
                    deleteEventMap.put(entry.getKey(), (Integer) value);
                } else if (value instanceof Map) {
                    // 如果是 Map 对象，提取 status 值
                    Map<String, Object> valueMap = (Map<String, Object>) value;
                    Object statusValue = valueMap.get("status");
                    if (statusValue instanceof Integer) {
                        deleteEventMap.put(entry.getKey(), (Integer) statusValue);
                    }
                }
            }
        }
        log.info("[AbstractFootballProcessor] addSettleMention deleteEventMap:{}", deleteEventMap);
        basketballMentionStatus.setDataByEventCode(eventCode, deleteEventMap);
        Map<String, Object> result = new HashMap<>();
        result.put("redisKey", parameters.get("redisKey"));
        result.put("redisValue", basketballMentionStatus);
        log.info("[AbstractFootballProcessor] addSettleMention buildData with result:{} start!", result);
        return result;
    }

 }
