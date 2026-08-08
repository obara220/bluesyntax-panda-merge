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
        //百家赔一次下发默认是同一个站点(L01-Bet365/L01-Fonbet...)，站点编码可能在 internalDataSourceCode，
        //也可能直接是盘口的 dataSourceCode。编码口径与原来完全一致(resolveDataSourceCode)，
        //只是改成按盘口逐个分组，避免一次下发混站点时只保留最后更新的那一个站点；
        //盘口自己没有任何编码时，归到本次下发的编码，而不是丢出一个空编码的field。
        String pushSourceCode = ThirdMarket108048Helper.resolvePushDataSourceCode(markets, dataSourceCode);
        Map<String, List<ThirdMarketDTO>> marketsBySource = new HashMap<>();
        for (ThirdMarketDTO market : markets) {
            if (market == null) {
                continue;
            }
            String sourceCode = ThirdMarket108048Helper.resolveDataSourceCode(market, pushSourceCode);
            marketsBySource.computeIfAbsent(sourceCode, k -> new ArrayList<>()).add(market);
        }
        Map<String, ThirdMarketModifytimeDTO> updates = new HashMap<>(marketsBySource.size());
        for (Map.Entry<String, List<ThirdMarketDTO>> entry : marketsBySource.entrySet()) {
            ThirdMarketModifytimeDTO dto = ThirdMarket108048Helper.fromMarkets(matchId, categoryId, entry.getKey(), entry.getValue());
            if (dto == null) {
                continue;
            }
            //以分组编码为准，保证缓存field与分组一一对应
            dto.setDateSourceCode(entry.getKey());
            String fieldKey = ThirdMarket108048Helper.buildFieldKey(categoryId, dto.getDateSourceCode());
            if (!ThirdMarket108048Helper.isValidFieldKey(fieldKey)) {
                continue;
            }
            updates.put(fieldKey, dto);
        }
        //108048 验证日志：百家赔(W2)写缓存，能看到解析出的站点编码和最终写入的 field
        log.info("::108048::cacheFromMarkets 写缓存,matchId={},categoryId={},下发编码={},解析站点={},写入field={}",
                matchId, categoryId, dataSourceCode, marketsBySource.keySet(), updates.keySet());
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
                String dataSourceCode = fieldKey.contains(":") ? fieldKey.substring(fieldKey.indexOf(':') + 1) : "";
                ThirdMarketModifytimeDTO dto = ThirdMarket108048Helper.fromMarkets(
                        matchEntry.getKey(), categoryId, dataSourceCode, fieldEntry.getValue());
                if (dto != null) {
                    updates.put(fieldEntry.getKey(), dto);
                }
            }
            //108048 验证日志：常规数据源(W1，含 AO)写缓存，AO 正常应写出 "玩法ID:AO"
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
        //缓存必须有过期时间，否则赛事结束后的field会一直残留
        //108048 验证日志：本次写入的 field 与 redis 里已有的 field，可对比确认站点是否被合并保留
        log.info("::108048::flush 写入redis,matchId={},本次field={},已有field={}", matchId, updates.keySet(), existing.keySet());
        redisService.hSetAll(redisKey, merged, CACHE_EXPIRE_SECONDS);
        cleanLegacyFields(redisKey, existing);
    }

    /**
     * 清理历史版本写入的非法field(只有玩法ID、没有数据源编码)，风控取不到且不会过期，会一直返回过期状态
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
            //108048 验证日志：残留的非法 field（如只有 "4"）被清理，能证明旧 AO key 被自愈
            log.info("::108048::清理历史非法field(无数据源编码),redisKey={},删除={}", redisKey, legacyFields);
            redisService.hDel(redisKey, legacyFields.toArray());
        }
    }
}
