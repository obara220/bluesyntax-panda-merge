package com.panda.merge.service.settleMention.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleEventCodeEnum;
import com.panda.merge.constant.SettleMentionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @description: settle mention abstract class implementation
 * @author: Henry Wang
 * @create: 2024-08-28 13:39
 **/
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSettleMentionProcessor<T> implements ISettleMentionProcessor<T>{
    @Resource
    protected RedisService redisService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void addSettleMention(Map<String, Object> parameters) {
        try {
            log.info("[AbstractSettleMentionProcessor] addSettleMention obtainData with parameters: {}", parameters);
            Object desireddData = obtainData(parameters);
            if (desireddData == null) {
                log.info("[AbstractSettleMentionProcessor] addSettleMention data is null!");
                return;
            }
            log.info("[AbstractSettleMentionProcessor] addSettleMention build data!");
            Map<String, Object> result = (Map<String, Object>) buildData(desireddData);
            if (result == null) {
                log.info("[AbstractSettleMentionProcessor] addSettleMention data result is null!");
                return;
            }
            String redisKey = (String) result.get("redisKey");
            Object redisValue = result.get("redisValue");
            log.info("[AbstractSettleMentionProcessor] addSettleMention cache data!");
            cacheDataToRedis(redisKey, redisValue);
            log.info("[AbstractSettleMentionProcessor] addSettleMention end!");
        } catch (Exception e) {
            log.error("[AbstractSettleMentionProcessor] addSettleMention with parameters:{} error:", parameters, e);
        }

    }

    @Override
    public void deleteSettleMention(Long matchId, List<String> keys, SettleEventCodeEnum settleEventCodeEnum) {
        if (matchId == null || CollectionUtils.isEmpty(keys)) {
            return;
        }
        log.info("[AbstractSettleMentionProcessor] deleteSettleMention build data start with matchId:{} keys:{} eventCode:{}", matchId, keys, settleEventCodeEnum);
        Map<String, Object> result = (Map<String, Object>)buildDataForDelete(matchId, keys, settleEventCodeEnum);
        if (result == null) {
            log.warn("[AbstractSettleMentionProcessor] addSettleMention result is null!");
            return;
        }
        String redisKey = (String) result.get("redisKey");
        Object redisValue = result.get("redisValue");
        log.info("[AbstractSettleMentionProcessor] deleteSettleMention cache data!");
        cacheDataToRedis(redisKey, redisValue);
        log.info("[AbstractSettleMentionProcessor] deleteSettleMention end!");
    }

    @Override
    public T querySettleMention(Long matchId) {
        if (matchId == null) {
            return null;
        }
        return querySettleMentionFromRedis(redisKey(matchId));
    }

    protected T querySettleMentionFromRedis(String redisKey) {
        T settleMentionDto = null;
        try {
            Object value = redisService.hGet(redisKey, settleMention().getValue());
            settleMentionDto = objectMapper.readValue(objectMapper.writeValueAsString(value), typeReference());
        } catch (Exception e) {
            log.error("[AbstractSettleMentionProcessor] querySettleMentionFromRedis error: ", e);
        }
        return settleMentionDto;
    }

    protected abstract Object obtainData(Map<String, Object> parameters);

    protected abstract Object buildData(Object object);

    protected abstract Object buildDataForDelete(Long matchId, List<String> keys, SettleEventCodeEnum settleEventCodeEnum);

    protected String redisKey(Long matchId) {
        return CommonConstant.SETTLE_MENTION_KEY + matchId;
    }

    abstract SettleMentionEnum settleMention();

    public boolean support(SettleMentionEnum settleMentionEnum){
        return settleMention() == settleMentionEnum;
    }

    abstract TypeReference<T> typeReference();

    protected void cacheDataToRedis(String redisKey, Object redisValue) {
        redisService.hSet(redisKey, settleMention().getValue(), redisValue, RedisConfig.REDIS_WEEK_TIME);
    }

}
