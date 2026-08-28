package com.panda.merge.odds;

import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMarketModifytimeDTO;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ThirdMarket108048CacheService.class);

    /**
     * 缓存过期时间(秒)，每次写入刷新
     */
    private static final long CACHE_EXPIRE_SECONDS = 24 * 60 * 60L;

    @Autowired
    private RedisService redisService;

    public void cacheFromMarkets(Long matchId, Long categoryId, String dataSourceCode, List<ThirdMarketDTO> markets) {
        if (matchId == null || categoryId == null || CollectionUtils.isEmpty(markets)) {
            return;
        }
        String pushSourceCode = ThirdMarket108048Helper.resolvePushDataSourceCode(markets, dataSourceCode);
        Map<Integer, Map<String, List<ThirdMarketDTO>>> marketsByTypeAndSource = new HashMap<>();
        for (ThirdMarketDTO market : markets) {
            if (market == null) {
                continue;
            }
            Integer marketType = ThirdMarket108048Helper.resolveMarketType(market, null);
            if (marketType == null) {
                continue;
            }
            String sourceCode = ThirdMarket108048Helper.resolveDataSourceCode(market, pushSourceCode);
            marketsByTypeAndSource
                    .computeIfAbsent(marketType, k -> new HashMap<>())
                    .computeIfAbsent(sourceCode, k -> new ArrayList<>())
                    .add(market);
        }
        Map<String, ThirdMarketModifytimeDTO> updates = new HashMap<>();
        for (Map.Entry<Integer, Map<String, List<ThirdMarketDTO>>> typeEntry : marketsByTypeAndSource.entrySet()) {
            for (Map.Entry<String, List<ThirdMarketDTO>> entry : typeEntry.getValue().entrySet()) {
                ThirdMarketModifytimeDTO dto = ThirdMarket108048Helper.fromMarkets(
                        matchId, categoryId, typeEntry.getKey(), entry.getKey(), entry.getValue());
                if (dto == null) {
                    continue;
                }
                dto.setDateSourceCode(entry.getKey());
                String fieldKey = ThirdMarket108048Helper.buildFieldKey(categoryId, typeEntry.getKey(), dto.getDateSourceCode());
                if (!ThirdMarket108048Helper.isValidFieldKey(fieldKey)) {
                    continue;
                }
                updates.put(fieldKey, dto);
            }
        }
        log.info("::108048::cacheFromMarkets 写缓存,matchId={},categoryId={},下发编码={},写入field={}",
                matchId, categoryId, dataSourceCode, updates.keySet());
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
            Integer marketType = ThirdMarket108048Helper.resolveMarketType(market, wrapper.getMarketType());
            if (marketType == null) {
                continue;
            }
            String dataSourceCode = ThirdMarket108048Helper.resolveDataSourceCode(market, wrapper.getDataSourceCode());
            String fieldKey = ThirdMarket108048Helper.buildFieldKey(categoryId, marketType, dataSourceCode);
            if (!ThirdMarket108048Helper.isValidFieldKey(fieldKey)) {
                continue;
            }
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
                Integer marketType = ThirdMarket108048Helper.parseMarketType(fieldKey);
                String dataSourceCode = ThirdMarket108048Helper.parseDataSourceCode(fieldKey);
                ThirdMarketModifytimeDTO dto = ThirdMarket108048Helper.fromMarkets(
                        matchEntry.getKey(), categoryId, marketType, dataSourceCode, fieldEntry.getValue());
                if (dto != null) {
                    updates.put(fieldEntry.getKey(), dto);
                }
            }
            log.info("::108048::cacheFromWrappers 写缓存,matchId={},写入field={}", matchEntry.getKey(), updates.keySet());
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
        log.info("::108048::flush 写入redis,matchId={},本次field={},已有field={}", matchId, updates.keySet(), existing.keySet());
        redisService.hSetAll(redisKey, merged, CACHE_EXPIRE_SECONDS);
        cleanLegacyFields(redisKey, existing);
    }

    /**
     * 清理历史版本写入的非法 field（无 marketType 或缺少数据源编码）。
     */
    private void cleanLegacyFields(String redisKey, Map<String, ThirdMarketModifytimeDTO> existing) {
        if (MapUtils.isEmpty(existing)) {
            return;
        }
        List<String> legacyFields = new ArrayList<>();
        for (String fieldKey : existing.keySet()) {
            if (!ThirdMarket108048Helper.isValidFieldKey(fieldKey)) {
                legacyFields.add(fieldKey);
            }
        }
        if (!legacyFields.isEmpty()) {
            log.info("::108048::清理历史非法field(非玩法ID:marketType:数据源编码),redisKey={},删除={}", redisKey, legacyFields);
            redisService.hDel(redisKey, legacyFields.toArray());
        }
    }
}
