package com.panda.merge.component;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 玩法下全部关盘时的坑位排序：按上一次下发顺序排列，未出现在上次下发中的盘口排在末尾；
 * 排序完成后连续编号 1、2、3…，保证下游始终有主盘口且客户端无空坑位。
 */
@Slf4j
@Component
public class ClosedMarketPlaceSortHelper {

    public static final int UNKNOWN_PLACE_NUM = 999;

    @Autowired
    private RedisService redisService;

    public boolean isAllClosedForPlaceSort(List<StandardMarketMessage> standardMarketMessages) {
        return standardMarketMessages.stream().allMatch(m ->
                m.getThirdMarketSourceStatus() != null
                        && m.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED);
    }

    public void sortClosedStandardMarkets(String linkId, Long matchId, Long childCategoryId,
                                          List<StandardMarketMessage> markets) {
        sortClosedStandardMarkets(linkId, matchId, childCategoryId, markets, null);
    }

    public void sortClosedStandardMarkets(String linkId, Long matchId, Long childCategoryId,
                                          List<StandardMarketMessage> markets,
                                          List<StandardMarketMessage> syncRemoveFrom) {
        if (CollectionUtils.isEmpty(markets)) {
            return;
        }
        dedupeByRelationMarketId(linkId, matchId, childCategoryId, markets, syncRemoveFrom);
        if (CollectionUtils.isEmpty(markets)) {
            return;
        }
        Long marketCategoryId = markets.get(0).getMarketCategoryId();
        LastDistributionPlaceNumContext context = loadLastDistributionPlaceNumContext(matchId, marketCategoryId, childCategoryId);
        sortClosedStandardMarketsByContext(linkId, matchId, childCategoryId, markets, context);
        compactPlaceNumsAfterSort(linkId, matchId, childCategoryId, markets, context);
    }

    public void sortClosedStandardMarketDataMessages(String linkId, Long matchId, Long childCategoryId,
                                                     List<StandardMarketDataMessage> markets) {
        if (CollectionUtils.isEmpty(markets)) {
            return;
        }
        dedupeByRelationMarketId(linkId, matchId, childCategoryId, markets);
        if (CollectionUtils.isEmpty(markets)) {
            return;
        }
        Long marketCategoryId = markets.get(0).getMarketCategoryId();
        LastDistributionPlaceNumContext context = loadLastDistributionPlaceNumContext(matchId, marketCategoryId, childCategoryId);
        sortClosedStandardMarketDataMessagesByContext(linkId, matchId, childCategoryId, markets, context);
        compactPlaceNumsAfterSortForData(linkId, matchId, childCategoryId, markets, context);
    }

    /**
     * 关转封去重后按当前 placeNum 连续编号 1、2、3…，消除空洞。
     */
    public void compactPlaceNumsAfterDedupe(String linkId, Long categoryId, List<StandardMarketMessage> markets) {
        if (CollectionUtils.isEmpty(markets)) {
            return;
        }
        markets.sort(Comparator.comparingInt(m -> effectivePlaceNum(m.getPlaceNum())));
        for (int i = 0; i < markets.size(); i++) {
            StandardMarketMessage market = markets.get(i);
            Integer before = market.getPlaceNum();
            int compact = i + 1;
            market.setPlaceNum(compact);
            if (!Objects.equals(before, compact)) {
                log.info("::{}::关转封去重后compact,玩法:{},统一盘口id:{},变更前坑位:{},变更后坑位:{}",
                        linkId, categoryId, market.getRelationMarketId(), before, compact);
            }
        }
    }

    private void sortClosedStandardMarketsByContext(String linkId, Long matchId, Long childCategoryId,
                                                    List<StandardMarketMessage> markets,
                                                    LastDistributionPlaceNumContext context) {
        if (context.isEmpty()) {
            log.info("::{}::全关盘无上次下发缓存,使用无效盘口排序兜底,标准赛事id:{},子玩法:{}",
                    linkId, matchId, childCategoryId);
            sortByLegacyInvalidMarketOrder(markets);
            return;
        }
        List<StandardMarketMessage> known = new ArrayList<>();
        List<StandardMarketMessage> unknown = new ArrayList<>();
        for (StandardMarketMessage market : markets) {
            if (resolveSortOrder(market, context) == UNKNOWN_PLACE_NUM) {
                unknown.add(market);
            } else {
                known.add(market);
            }
        }
        known.sort(Comparator
                .comparingInt((StandardMarketMessage m) -> resolveSortOrder(m, context))
                .thenComparing(m -> effectivePlaceNum(m.getPlaceNum())));
        if (!unknown.isEmpty()) {
            sortByLegacyInvalidMarketOrder(unknown);
            log.info("::{}::全关盘未命中上次下发顺序的盘口数:{},标准赛事id:{},子玩法:{},使用无效盘口排序兜底",
                    linkId, unknown.size(), matchId, childCategoryId);
        }
        markets.clear();
        markets.addAll(known);
        markets.addAll(unknown);
    }

    private void sortClosedStandardMarketDataMessagesByContext(String linkId, Long matchId, Long childCategoryId,
                                                               List<StandardMarketDataMessage> markets,
                                                               LastDistributionPlaceNumContext context) {
        if (context.isEmpty()) {
            log.info("::{}::全关盘无上次下发缓存,使用无效盘口排序兜底,标准赛事id:{},子玩法:{}",
                    linkId, matchId, childCategoryId);
            // call the data-specific sort to avoid generic erasure conflict
            sortByLegacyInvalidMarketOrderForData(markets);
            return;
        }
        List<StandardMarketDataMessage> known = new ArrayList<>();
        List<StandardMarketDataMessage> unknown = new ArrayList<>();
        for (StandardMarketDataMessage market : markets) {
            if (resolveSortOrder(market, context) == UNKNOWN_PLACE_NUM) {
                unknown.add(market);
            } else {
                known.add(market);
            }
        }
        known.sort(Comparator
                .comparingInt((StandardMarketDataMessage m) -> resolveSortOrder(m, context))
                .thenComparing(m -> effectivePlaceNum(m.getPlaceNum())));
        if (!unknown.isEmpty()) {
            sortByLegacyInvalidMarketOrderForData(unknown);
            log.info("::{}::全关盘未命中上次下发顺序的盘口数:{},标准赛事id:{},子玩法:{},使用无效盘口排序兜底",
                    linkId, unknown.size(), matchId, childCategoryId);
        }
        markets.clear();
        markets.addAll(known);
        markets.addAll(unknown);
    }

    /**
     * 与 {@link MarketOddsPlaceProcessor} 无效盘口排序一致：modifyTime 降序 → thirdStatus 降序 → 赔率差升序。
     */
    private void sortByLegacyInvalidMarketOrder(List<StandardMarketMessage> markets) {
        markets.forEach(this::fillOddsMetricForLegacySort);
        markets.sort(legacyInvalidMarketComparator(this::resolveModifyTimeFormatForMessage,
                StandardMarketMessage::getThirdMarketSourceStatus,
                m -> m.getOddsMetric() == null ? 999999L : m.getOddsMetric().longValue()));
    }

    // Renamed to avoid name clash caused by Java generic type erasure with the
    // List<StandardMarketMessage> overload above.
    private void sortByLegacyInvalidMarketOrderForData(List<StandardMarketDataMessage> markets) {
        markets.forEach(m -> {
            if (m.getModifyTimeFormat() == null && m.getModifyTime() != null) {
                m.setModifyTimeFormat(formatModifyTime(m.getModifyTime()));
            }
            fillOddsMetricForLegacySort(m);
        });
        markets.sort(legacyInvalidMarketComparator(
                m -> m.getModifyTimeFormat() == null ? 0L : m.getModifyTimeFormat(),
                StandardMarketDataMessage::getThirdMarketSourceStatus,
                m -> m.getOddsMetric() == null ? 999999L : m.getOddsMetric().longValue()));
    }

    private <T> Comparator<T> legacyInvalidMarketComparator(java.util.function.Function<T, Long> modifyTimeFn,
                                                            java.util.function.Function<T, Integer> thirdStatusFn,
                                                            java.util.function.ToLongFunction<T> oddsMetricFn) {
        return Comparator
                .comparing(modifyTimeFn, Comparator.reverseOrder())
                .thenComparing(thirdStatusFn, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparingLong(oddsMetricFn);
    }

    private void fillOddsMetricForLegacySort(StandardMarketMessage market) {
        if (CollectionUtils.isEmpty(market.getMarketOddsList())) {
            market.setOddsMetric(999999);
            return;
        }
        market.setOddsMetric(market.getMarketOddsList().stream()
                .map(StandardMarketOddsMessage::getOriginalOddsValue)
                .filter(Objects::nonNull)
                .reduce(0, (a, b) -> a >= b ? a - b : b - a));
    }

    private void fillOddsMetricForLegacySort(StandardMarketDataMessage market) {
        if (CollectionUtils.isEmpty(market.getMarketOddsList())) {
            market.setOddsMetric(999999);
            return;
        }
        market.setOddsMetric(market.getMarketOddsList().stream()
                .map(StandardMarketOddsDataMessage::getOriginalOddsValue)
                .filter(Objects::nonNull)
                .reduce(0, (a, b) -> a >= b ? a - b : b - a));
    }

    private long resolveModifyTimeFormatForMessage(StandardMarketMessage market) {
        if (market.getModifyTime() == null) {
            return 0L;
        }
        return formatModifyTime(market.getModifyTime());
    }

    private long formatModifyTime(Long modifyTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format1 = simpleDateFormat.format(modifyTime);
        try {
            Date date = simpleDateFormat.parse(format1);
            return date.getTime();
        } catch (Exception e) {
            return modifyTime;
        }
    }

    /**
     * 同一批数据内统一盘口 id 唯一；重复时保留坑位最小的一条（null 视为 999）。
     */
    private void dedupeByRelationMarketId(String linkId, Long matchId, Long childCategoryId,
                                          List<StandardMarketMessage> markets,
                                          List<StandardMarketMessage> syncRemoveFrom) {
        int beforeSize = markets.size();
        List<StandardMarketMessage> dropped = new ArrayList<>();
        List<StandardMarketMessage> noRelationId = new ArrayList<>();
        Map<Long, StandardMarketMessage> bestByRelationId = new HashMap<>();
        for (StandardMarketMessage market : markets) {
            Long relationMarketId = market.getRelationMarketId();
            if (relationMarketId == null) {
                noRelationId.add(market);
                continue;
            }
            StandardMarketMessage existing = bestByRelationId.get(relationMarketId);
            if (existing == null) {
                bestByRelationId.put(relationMarketId, market);
                continue;
            }
            StandardMarketMessage kept = pickBySmallerPlaceNum(existing, market);
            StandardMarketMessage removed = kept == existing ? market : existing;
            dropped.add(removed);
            log.info("::{}::全关盘统一盘口id去重,标准赛事id:{},子玩法:{},relationMarketId:{},保留坑位:{},剔除坑位:{}",
                    linkId, matchId, childCategoryId, relationMarketId, kept.getPlaceNum(), removed.getPlaceNum());
            bestByRelationId.put(relationMarketId, kept);
        }
        if (dropped.isEmpty()) {
            return;
        }
        markets.clear();
        markets.addAll(noRelationId);
        markets.addAll(bestByRelationId.values());
        if (syncRemoveFrom != null) {
            syncRemoveFrom.removeAll(dropped);
        }
        log.info("::{}::全关盘统一盘口id去重完成,标准赛事id:{},子玩法:{},去重前:{},去重后:{},剔除:{}",
                linkId, matchId, childCategoryId, beforeSize, markets.size(), dropped.size());
    }

    private void dedupeByRelationMarketId(String linkId, Long matchId, Long childCategoryId,
                                          List<StandardMarketDataMessage> markets) {
        int beforeSize = markets.size();
        List<StandardMarketDataMessage> noRelationId = new ArrayList<>();
        Map<Long, StandardMarketDataMessage> bestByRelationId = new HashMap<>();
        for (StandardMarketDataMessage market : markets) {
            Long relationMarketId = market.getRelationMarketId();
            if (relationMarketId == null) {
                noRelationId.add(market);
                continue;
            }
            StandardMarketDataMessage existing = bestByRelationId.get(relationMarketId);
            if (existing == null) {
                bestByRelationId.put(relationMarketId, market);
                continue;
            }
            StandardMarketDataMessage kept = pickBySmallerPlaceNum(existing, market);
            StandardMarketDataMessage dropped = kept == existing ? market : existing;
            log.info("::{}::全关盘统一盘口id去重,标准赛事id:{},子玩法:{},relationMarketId:{},保留坑位:{},剔除坑位:{}",
                    linkId, matchId, childCategoryId, relationMarketId, kept.getPlaceNum(), dropped.getPlaceNum());
            bestByRelationId.put(relationMarketId, kept);
        }
        if (bestByRelationId.size() + noRelationId.size() == beforeSize) {
            return;
        }
        markets.clear();
        markets.addAll(noRelationId);
        markets.addAll(bestByRelationId.values());
        log.info("::{}::全关盘统一盘口id去重完成,标准赛事id:{},子玩法:{},去重前:{},去重后:{}",
                linkId, matchId, childCategoryId, beforeSize, markets.size());
    }

    private StandardMarketMessage pickBySmallerPlaceNum(StandardMarketMessage m1, StandardMarketMessage m2) {
        return effectivePlaceNum(m1.getPlaceNum()) <= effectivePlaceNum(m2.getPlaceNum()) ? m1 : m2;
    }

    private StandardMarketDataMessage pickBySmallerPlaceNum(StandardMarketDataMessage m1, StandardMarketDataMessage m2) {
        return effectivePlaceNum(m1.getPlaceNum()) <= effectivePlaceNum(m2.getPlaceNum()) ? m1 : m2;
    }

    private int effectivePlaceNum(Integer placeNum) {
        return placeNum == null ? UNKNOWN_PLACE_NUM : placeNum;
    }

    /**
     * 按排序结果连续编号 1、2、3…，相对顺序与上次下发一致，坑位无空洞。
     */
    private void compactPlaceNumsAfterSort(String linkId, Long matchId, Long childCategoryId,
                                           List<StandardMarketMessage> markets,
                                           LastDistributionPlaceNumContext context) {
        for (int i = 0; i < markets.size(); i++) {
            StandardMarketMessage market = markets.get(i);
            Integer before = market.getPlaceNum();
            Integer historical = resolvePlaceNum(market, context);
            int compact = i + 1;
            market.setPlaceNum(compact);
            log.info("::{}::全关盘按上次下发顺序排序,标准赛事id:{},统一盘口id:{},玩法:{},子玩法:{},三方盘口源id:{},球头:{},变更前坑位:{},上次下发坑位:{},变更后坑位:{}",
                    linkId, matchId, market.getRelationMarketId(), market.getMarketCategoryId(),
                    childCategoryId, market.getThirdMarketSourceId(), market.getAddition1(),
                    before, historical, compact);
        }
    }

    private void compactPlaceNumsAfterSortForData(String linkId, Long matchId, Long childCategoryId,
                                           List<StandardMarketDataMessage> markets,
                                           LastDistributionPlaceNumContext context) {
        for (int i = 0; i < markets.size(); i++) {
            StandardMarketDataMessage market = markets.get(i);
            Integer before = market.getPlaceNum();
            Integer historical = resolvePlaceNum(market, context);
            int compact = i + 1;
            market.setPlaceNum(compact);
            log.info("::{}::全关盘按上次下发顺序排序,标准赛事id:{},统一盘口id:{},玩法:{},子玩法:{},三方盘口源id:{},球头:{},变更前坑位:{},上次下发坑位:{},变更后坑位:{}",
                    linkId, matchId, market.getRelationMarketId(), market.getMarketCategoryId(),
                    childCategoryId, market.getThirdMarketSourceId(), market.getAddition1(),
                    before, historical, compact);
        }
    }

    LastDistributionPlaceNumContext loadLastDistributionPlaceNumContext(Long matchId, Long marketCategoryId,
                                                                        Long childCategoryId) {
        LastDistributionPlaceNumContext context = new LastDistributionPlaceNumContext();
        mergeFromCache(matchId, marketCategoryId, childCategoryId,
                Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS, context);
        if (context.isEmpty()) {
            mergeFromCache(matchId, marketCategoryId, childCategoryId,
                    Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_A_MARKETODDS, context);
        }
        return context;
    }

    private void mergeFromCache(Long matchId, Long marketCategoryId, Long childCategoryId,
                                String redisKeyPrefix, LastDistributionPlaceNumContext context) {
        String redisOddsKey = DigestUtil.md5Hex(redisKeyPrefix + matchId);
        Object obj = redisService.hGet(redisOddsKey, String.valueOf(marketCategoryId));
        if (obj == null) {
            return;
        }
        List<StandardMarketMessage> lastMarkets = (List<StandardMarketMessage>) obj;
        List<StandardMarketMessage> childMarkets = new ArrayList<>();
        for (StandardMarketMessage cached : lastMarkets) {
            if (cached.getPlaceNum() == null) {
                continue;
            }
            Long cachedChildCategoryId = cached.getChildMarketCategoryId() != null
                    ? cached.getChildMarketCategoryId() : cached.getMarketCategoryId();
            if (!Objects.equals(childCategoryId, cachedChildCategoryId)) {
                continue;
            }
            childMarkets.add(cached);
        }
        childMarkets.sort(Comparator.comparing(StandardMarketMessage::getPlaceNum));
        int order = 0;
        for (StandardMarketMessage cached : childMarkets) {
            if (cached.getRelationMarketId() != null) {
                context.byRelationMarketId.putIfAbsent(cached.getRelationMarketId(), cached.getPlaceNum());
                context.relationOrderIndex.putIfAbsent(cached.getRelationMarketId(), order);
            }
            if (StringUtils.isNotBlank(cached.getThirdMarketSourceId())) {
                context.byThirdMarketSourceId.putIfAbsent(cached.getThirdMarketSourceId(), cached.getPlaceNum());
                context.thirdSourceOrderIndex.putIfAbsent(cached.getThirdMarketSourceId(), order);
            }
            order++;
        }
    }

    private int resolveSortOrder(StandardMarketMessage market, LastDistributionPlaceNumContext context) {
        if (market.getRelationMarketId() != null) {
            Integer order = context.relationOrderIndex.get(market.getRelationMarketId());
            if (order != null) {
                return order;
            }
        }
        if (StringUtils.isNotBlank(market.getThirdMarketSourceId())) {
            Integer order = context.thirdSourceOrderIndex.get(market.getThirdMarketSourceId());
            if (order != null) {
                return order;
            }
        }
        return UNKNOWN_PLACE_NUM;
    }

    private int resolveSortOrder(StandardMarketDataMessage market, LastDistributionPlaceNumContext context) {
        if (market.getRelationMarketId() != null) {
            Integer order = context.relationOrderIndex.get(market.getRelationMarketId());
            if (order != null) {
                return order;
            }
        }
        if (StringUtils.isNotBlank(market.getThirdMarketSourceId())) {
            Integer order = context.thirdSourceOrderIndex.get(market.getThirdMarketSourceId());
            if (order != null) {
                return order;
            }
        }
        return UNKNOWN_PLACE_NUM;
    }

    private Integer resolvePlaceNum(StandardMarketMessage market, LastDistributionPlaceNumContext context) {
        if (market.getRelationMarketId() != null) {
            Integer placeNum = context.byRelationMarketId.get(market.getRelationMarketId());
            if (placeNum != null) {
                return placeNum;
            }
        }
        if (StringUtils.isNotBlank(market.getThirdMarketSourceId())) {
            Integer placeNum = context.byThirdMarketSourceId.get(market.getThirdMarketSourceId());
            if (placeNum != null) {
                return placeNum;
            }
        }
        return UNKNOWN_PLACE_NUM;
    }

    private Integer resolvePlaceNum(StandardMarketDataMessage market, LastDistributionPlaceNumContext context) {
        if (market.getRelationMarketId() != null) {
            Integer placeNum = context.byRelationMarketId.get(market.getRelationMarketId());
            if (placeNum != null) {
                return placeNum;
            }
        }
        if (StringUtils.isNotBlank(market.getThirdMarketSourceId())) {
            Integer placeNum = context.byThirdMarketSourceId.get(market.getThirdMarketSourceId());
            if (placeNum != null) {
                return placeNum;
            }
        }
        return UNKNOWN_PLACE_NUM;
    }

    static class LastDistributionPlaceNumContext {
        private final Map<Long, Integer> byRelationMarketId = new HashMap<>();
        private final Map<String, Integer> byThirdMarketSourceId = new HashMap<>();
        private final Map<Long, Integer> relationOrderIndex = new HashMap<>();
        private final Map<String, Integer> thirdSourceOrderIndex = new HashMap<>();

        boolean isEmpty() {
            return byRelationMarketId.isEmpty() && byThirdMarketSourceId.isEmpty();
        }
    }
}
