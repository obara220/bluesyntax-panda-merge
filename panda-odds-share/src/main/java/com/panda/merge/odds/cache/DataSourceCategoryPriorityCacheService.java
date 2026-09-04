package com.panda.merge.odds.cache;

import com.panda.merge.dto.UpdateMarketCategoryDataSourceCodeDTO;
import com.panda.merge.dto.odds.CategoryDataSourceHighPriority;
import com.panda.merge.odds.AutoSwitchConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DataSourceCategoryPriorityService
 *
 * @description:
 *
 * key format dshp:standardMatchId:market_type:standardMarketCategoryId
 * value priority
 * expire  2 * validaSecond
 *
 * @date: 6/7/2025
 **/
@Service
@Slf4j
public class DataSourceCategoryPriorityCacheService {

    // 更新较小值
    private static final String SCRIPT_SMALL = "local current = redis.call('get', KEYS[1]) " +
            "if current and tonumber(current) <= tonumber(ARGV[1]) then " + "   return '0' " + "else " +
            "   redis.call('set', KEYS[1], ARGV[1], 'ex', ARGV[2]) " + "   return '1' " + "end";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AutoSwitchConfigService autoSwitchConfigService;

    public Boolean updatePriority(CategoryDataSourceHighPriority priority) {
        if (Objects.isNull(priority) || Objects.isNull(priority.getTp()) || priority.getTp() < 0) {
            return false;
        }
        if (priority.getTp() < priority.getOp()){
            return true;
        }

        Long matchId = priority.getMatchId();
        int marketType = priority.getMarketType();
        Long categoryCode = priority.getCategoryId();
        int tp = priority.getTp();
        int expireSeconds =  autoSwitchConfigService.getExpireSeconds(marketType, matchId);
        Boolean result = updateSmall(getKey(matchId, marketType, categoryCode), (long) tp, expireSeconds);
        if (result) {
            log.info("updatePriority matchId:{},marketType:{},categoryCode:{},priority:{}",
                     matchId,
                     marketType,
                     categoryCode,
                     tp);
        }
        return result;
    }

    public void clearPriority(String linkId, Long matchId, int marketType, Long categoryCode) {
        String key = getKey(matchId, marketType, categoryCode);
        redisTemplate.delete(key);
        log.info("linkId:{},clearPriority matchId:{},marketType:{},categoryId:{}",
                 linkId,
                 matchId,
                 marketType,
                 categoryCode);
    }

    public void batchClearPriority(String linkId, List<UpdateMarketCategoryDataSourceCodeDTO> data) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        List<String> keys = data
                .stream()
                .map(cds -> getKey(cds.getMatchId(), Integer.parseInt(cds.getMarketType()), cds.getMarketCategoryId()))
                .collect(Collectors.toList());
        redisTemplate.delete(keys);
        log.info("linkId:{},batchClearPriority category:{}", linkId, keys);
    }

    private Boolean updateSmall(String key, Long priority, Integer expireSeconds) {
        if (priority == null || expireSeconds == null || expireSeconds <= 0) {
            return false;
        }
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>(SCRIPT_SMALL, String.class);
        String result = redisTemplate.execute(redisScript,
                                              RedisSerializer.string(),
                                              RedisSerializer.string(),
                                              Collections.singletonList(key),
                                              String.valueOf(priority),
                                              String.valueOf(expireSeconds));
        return Integer.parseInt(result) == 1;
    }

    private String getKey(Long matchId, int marketType, Long categoryCode) {
        return String.format("dshp:%s:%s:%s", matchId, marketType, categoryCode);
    }

}
