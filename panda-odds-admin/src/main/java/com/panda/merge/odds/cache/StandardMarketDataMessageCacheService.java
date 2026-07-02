package com.panda.merge.odds.cache;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * StandardMarketDataMessageCacheService
 *
 * @description: 标准盘口消息缓存服务
 * cache key format  md5Hex(Ronghe:StandardCategoryMarketData:{standardMatchId}_{dataSourceCode}_{marketCategoryId})
 * cache value format hash{relationMarketId: StandardMarketDataMessage}
 * @date: 3/24/2025
 **/
@Slf4j
@Service
public class StandardMarketDataMessageCacheService {

    @Autowired
    private RedisService redisService;

    public Map<Long,Map<String, StandardMarketDataMessage>> getCategoryMarketsMap(Set<Long> marketCategoryIds,
                                                                        String linkId,
                                                                        Long standardMatchId,
                                                                        int marketType) {
        if (CollectionUtils.isEmpty(marketCategoryIds)) {
            return Collections.emptyMap();
        }
        Map<Long, String> cateogyDataSourceMap =
                getCategoryDataSourceMap(marketCategoryIds, linkId, standardMatchId, marketType);
        log.info("linkId: {}, matchId: {}, category datasource {} ", linkId, standardMatchId, cateogyDataSourceMap);
        if (MapUtils.isEmpty(cateogyDataSourceMap)) {
            return Collections.emptyMap();
        }
        List<String> cacheKeys = cateogyDataSourceMap
                .entrySet()
                .stream()
                .map(entry -> getCacheKey(standardMatchId, entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());

        Map<Long,Map<String, StandardMarketDataMessage>> standardMarketMessageMap =
                redisService.syncObtainMultiGetAllWithoutMerge(cateogyDataSourceMap.keySet(),
                                                               key->getCacheKey(standardMatchId,
                                                                                cateogyDataSourceMap.get(key),key));
        log.info("::{}::{}:: markets data message cache redisKey={},size：{} ",
                 linkId,
                 standardMatchId,
                 cacheKeys,
                 standardMarketMessageMap.size());

        return standardMarketMessageMap;
    }




    private Map<Long, String> getCategoryDataSourceMap(@NotEmpty Collection<Long> marketCategoryIds,
                                                       String linkId,
                                                       Long standardMatchId,
                                                       int marketType) {

        String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchId + "_" + marketType;
        Map<String, String> cacheMap = redisService.hGetAll(categoryRedisKey);
        if (MapUtil.isEmpty(cacheMap)) {
            return Collections.emptyMap();
        }
        Map<Long, String> result = new HashMap<>();
        for (Map.Entry<String, String> categoryDataSourceEntry : cacheMap.entrySet()) {
            Long marketCategoryId = Long.valueOf(categoryDataSourceEntry.getKey());
            if (!marketCategoryIds.contains(marketCategoryId)) {
                continue;
            }
            result.put(marketCategoryId, categoryDataSourceEntry.getValue());
        }
        return result;
    }

    private String getCacheKey(Long standardMatchId, String dataSourceCode, Long categoryId) {
        return DigestUtil.md5Hex(
                Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchId + "_" + dataSourceCode + "_" +
                        categoryId);
    }

}
