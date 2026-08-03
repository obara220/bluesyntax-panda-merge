package com.panda.merge.odds;

import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMarketModifytimeDTO;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 108048 赔率源最后一笔状态 Redis 缓存。
 */
@Component
public class ThirdMarket108048CacheService {

    @Autowired
    private RedisService redisService;

    public void cacheFromMarkets(Long matchId, Long categoryId, String dataSourceCode, List<ThirdMarketDTO> markets) {
        ThirdMarketModifytimeDTO dto = ThirdMarket108048Helper.fromMarkets(matchId, categoryId, dataSourceCode, markets);
        if (dto == null) {
            return;
        }
        Map<String, ThirdMarketModifytimeDTO> updates = new HashMap<>(1);
        updates.put(ThirdMarket108048Helper.buildFieldKey(categoryId, dto.getDateSourceCode()), dto);
        flush(matchId, updates);
    }

    public void cacheFromWrappers(List<OddsWrapper<ThirdMarketDTO>> wrappers) {
        if (CollectionUtils.isEmpty(wrappers)) {
            return;
        }
        Map<Long, Map<String, List<ThirdMarketDTO>>> grouped = new HashMap<>();
        for (OddsWrapper<ThirdMarketDTO> wrapper : wrappers) {
            if (wrapper == null || wrapper.getStandardSourceId() == null || Boolean.TRUE.equals(wrapper.getIsOutRight())) {
                continue;
            }
            ThirdMarketDTO market = wrapper.getData();
            if (market == null) {
                continue;
            }
            Long categoryId = wrapper.getMarketCategoryId() != null ? wrapper.getMarketCategoryId() : market.getMarketCategoryId();
            if (categoryId == null) {
                continue;
            }
            String dataSourceCode = ThirdMarket108048Helper.resolveDataSourceCode(market, wrapper.getDataSourceCode());
            String fieldKey = ThirdMarket108048Helper.buildFieldKey(categoryId, dataSourceCode);
            grouped.computeIfAbsent(wrapper.getStandardSourceId(), k -> new HashMap<>())
                    .computeIfAbsent(fieldKey, k -> new ArrayList<>())
                    .add(market);
        }
        if (grouped.isEmpty()) {
            return;
        }
        for (Map.Entry<Long, Map<String, List<ThirdMarketDTO>>> matchEntry : grouped.entrySet()) {
            Map<String, ThirdMarketModifytimeDTO> updates = new HashMap<>();
            for (Map.Entry<String, List<ThirdMarketDTO>> fieldEntry : matchEntry.getValue().entrySet()) {
                String fieldKey = fieldEntry.getKey();
                Long categoryId = ThirdMarket108048Helper.parseCategoryId(fieldKey);
                String dataSourceCode = fieldKey.contains(":") ? fieldKey.substring(fieldKey.indexOf(':') + 1) : "";
                ThirdMarketModifytimeDTO dto = ThirdMarket108048Helper.fromMarkets(
                        matchEntry.getKey(), categoryId, dataSourceCode, fieldEntry.getValue());
                if (dto != null) {
                    updates.put(fieldEntry.getKey(), dto);
                }
            }
            flush(matchEntry.getKey(), updates);
        }
    }

    public void flush(Long matchId, Map<String, ThirdMarketModifytimeDTO> updates) {
        if (matchId == null || MapUtils.isEmpty(updates)) {
            return;
        }
        String redisKey = Constant.REDIS_KEY.THIRD_MARKET_108048 + matchId;
        Map<String, ThirdMarketModifytimeDTO> existing = redisService.hGetAll(redisKey);
        Map<String, ThirdMarketModifytimeDTO> merged = new HashMap<>(updates.size());
        for (Map.Entry<String, ThirdMarketModifytimeDTO> entry : updates.entrySet()) {
            merged.put(entry.getKey(), ThirdMarket108048Helper.mergeLatest(existing.get(entry.getKey()), entry.getValue()));
        }
        redisService.hSetAll(redisKey, merged);
    }
}
