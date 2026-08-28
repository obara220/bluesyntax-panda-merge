package com.panda.merge.odds;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMarketModifytimeDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 108048 赔率源红绿灯：缓存写入与读侧 level 计算。
 * <p>
 * Redis field 格式：{@code 玩法ID:marketType:数据源编码}，marketType 0=滚球 1=早盘。
 * <p>
 * 数据源编码规则：
 * <ul>
 *   <li>内部站点（LS/TX/L02 等聚合源下的子站）：优先使用 {@code internalDataSourceCode}，完整保留如 LS-BET365，供下游区分</li>
 *   <li>公开源（N01/AO/SR 等）：使用 {@code dataSourceCode}</li>
 *   <li>融合侧不做脱敏变换，仅 trim；脱敏展示由下游处理</li>
 * </ul>
 */
public final class ThirdMarket108048Helper {

    public static final long STALE_SECONDS = 60L;
    public static final int LEVEL_GREEN = 0;
    public static final int LEVEL_YELLOW = 1;
    public static final int LEVEL_RED = 2;

    private ThirdMarket108048Helper() {
    }

    public static String buildFieldKey(Long categoryId, Integer marketType, String dataSourceCode) {
        return categoryId + ":" + normalizeMarketType(marketType) + ":" + normalizeDataSourceCode(dataSourceCode);
    }

    public static Long parseCategoryId(String fieldKey) {
        if (StringUtils.isBlank(fieldKey)) {
            return null;
        }
        int idx = fieldKey.indexOf(':');
        String categoryPart = idx > 0 ? fieldKey.substring(0, idx) : fieldKey;
        try {
            return Long.parseLong(categoryPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Integer parseMarketType(String fieldKey) {
        if (StringUtils.isBlank(fieldKey)) {
            return null;
        }
        int firstIdx = fieldKey.indexOf(':');
        if (firstIdx < 0) {
            return null;
        }
        int secondIdx = fieldKey.indexOf(':', firstIdx + 1);
        if (secondIdx <= firstIdx + 1) {
            return null;
        }
        try {
            return Integer.parseInt(fieldKey.substring(firstIdx + 1, secondIdx));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String parseDataSourceCode(String fieldKey) {
        if (StringUtils.isBlank(fieldKey)) {
            return null;
        }
        int firstIdx = fieldKey.indexOf(':');
        if (firstIdx < 0) {
            return null;
        }
        int secondIdx = fieldKey.indexOf(':', firstIdx + 1);
        if (secondIdx < 0 || secondIdx >= fieldKey.length() - 1) {
            return null;
        }
        return fieldKey.substring(secondIdx + 1);
    }

    public static Integer normalizeMarketType(Integer marketType) {
        if (marketType == null) {
            return null;
        }
        if (marketType == 0 || marketType == 1) {
            return marketType;
        }
        return null;
    }

    public static Integer resolveMarketType(ThirdMarketDTO market, Integer fallbackMarketType) {
        Integer marketType = market != null ? normalizeMarketType(market.getMarketType()) : null;
        if (marketType != null) {
            return marketType;
        }
        return normalizeMarketType(fallbackMarketType);
    }

    public static String normalizeDataSourceCode(String dataSourceCode) {
        if (StringUtils.isBlank(dataSourceCode)) {
            return "";
        }
        return dataSourceCode.trim();
    }

    public static int normalizeStatus(Integer status) {
        if (status == null) {
            return Constant.SPORT_MARKET.STATUS.DEACTIVATED;
        }
        if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(status)
                || Constant.SPORT_MARKET.STATUS.SUSPENDED.equals(status)
                || Constant.SPORT_MARKET.STATUS.LOCK.equals(status)) {
            return status;
        }
        return Constant.SPORT_MARKET.STATUS.DEACTIVATED;
    }

    /**
     * 同一玩法+早滚+站点下多盘口比较：modifyTime 更大者优先；时间相同则 开盘 &gt; 封盘/锁盘 &gt; 关盘。
     */
    private static final Comparator<ThirdMarketDTO> MARKET_LATEST_COMPARATOR = Comparator
            .comparing((ThirdMarketDTO m) -> m.getModifyTime() != null ? m.getModifyTime() : 0L)
            .thenComparing(m -> statusTieBreakPriority(normalizeStatus(m.getStatus())));

    public static ThirdMarketModifytimeDTO fromMarkets(Long matchId, Long categoryId, Integer marketType,
                                                       String dataSourceCode, List<ThirdMarketDTO> markets) {
        if (matchId == null || categoryId == null || normalizeMarketType(marketType) == null
                || markets == null || markets.isEmpty()) {
            return null;
        }
        ThirdMarketDTO lastMarket = markets.stream()
                .filter(Objects::nonNull)
                .max(MARKET_LATEST_COMPARATOR)
                .orElse(null);
        if (lastMarket == null) {
            return null;
        }
        ThirdMarketModifytimeDTO dto = new ThirdMarketModifytimeDTO();
        dto.setMatchId(matchId);
        dto.setCategoryId(categoryId);
        dto.setMarketType(normalizeMarketType(marketType));
        dto.setDateSourceCode(resolveMarketDataSourceCode(lastMarket, dataSourceCode));
        dto.setLastModifyTime(lastMarket.getModifyTime() != null ? lastMarket.getModifyTime() : System.currentTimeMillis());
        dto.setStatus(normalizeStatus(lastMarket.getStatus()));
        return dto;
    }

    /**
     * 同 modifyTime 下的状态优先级，值越大越优先：开盘 &gt; 封盘/锁盘 &gt; 关盘。
     */
    static int statusTieBreakPriority(int normalizedStatus) {
        if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(normalizedStatus)) {
            return 3;
        }
        if (Constant.SPORT_MARKET.STATUS.SUSPENDED.equals(normalizedStatus)
                || Constant.SPORT_MARKET.STATUS.LOCK.equals(normalizedStatus)) {
            return 2;
        }
        return 1;
    }

    /**
     * 合法的 field 格式为 {@code 玩法ID:marketType:数据源编码}，marketType 仅支持 0/1。
     * 历史版本写入过 {@code 玩法ID} 或 {@code 玩法ID:数据源编码}，读写两侧都要剔除。
     */
    public static boolean isValidFieldKey(String fieldKey) {
        if (StringUtils.isBlank(fieldKey)) {
            return false;
        }
        Integer marketType = parseMarketType(fieldKey);
        return parseCategoryId(fieldKey) != null
                && marketType != null
                && StringUtils.isNotBlank(parseDataSourceCode(fieldKey));
    }

    public static ThirdMarketModifytimeDTO mergeLatest(ThirdMarketModifytimeDTO existing, ThirdMarketModifytimeDTO incoming) {
        if (existing == null) {
            return incoming;
        }
        if (incoming == null) {
            return existing;
        }
        Long existingTime = existing.getLastModifyTime();
        Long incomingTime = incoming.getLastModifyTime();
        if (existingTime == null) {
            return incoming;
        }
        if (incomingTime == null) {
            return existing;
        }
        if (incomingTime > existingTime) {
            return incoming;
        }
        if (incomingTime < existingTime) {
            return existing;
        }
        int existingPriority = statusTieBreakPriority(normalizeStatus(existing.getStatus()));
        int incomingPriority = statusTieBreakPriority(normalizeStatus(incoming.getStatus()));
        return incomingPriority >= existingPriority ? incoming : existing;
    }

    public static void applyLevel(ThirdMarketModifytimeDTO dto, long nowMillis) {
        if (dto == null || dto.getLastModifyTime() == null) {
            return;
        }
        long diffSeconds = (nowMillis - dto.getLastModifyTime()) / 1000;
        boolean stale = diffSeconds >= STALE_SECONDS;
        int status = dto.getStatus();
        if (status == Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            dto.setLevel(LEVEL_RED);
        } else if (!stale && status == Constant.SPORT_MARKET.STATUS.ACTIVE) {
            dto.setLevel(LEVEL_GREEN);
        } else {
            dto.setLevel(LEVEL_YELLOW);
        }
    }

    public static String resolvePushDataSourceCode(List<ThirdMarketDTO> markets, String fallbackDataSourceCode) {
        String marketSourceCode = null;
        if (markets != null) {
            for (ThirdMarketDTO market : markets) {
                if (market == null) {
                    continue;
                }
                if (StringUtils.isNotBlank(market.getInternalDataSourceCode())) {
                    return normalizeDataSourceCode(market.getInternalDataSourceCode());
                }
                if (marketSourceCode == null && StringUtils.isNotBlank(market.getDataSourceCode())) {
                    marketSourceCode = market.getDataSourceCode();
                }
            }
        }
        return normalizeDataSourceCode(marketSourceCode != null ? marketSourceCode : fallbackDataSourceCode);
    }

    public static String resolveDataSourceCode(ThirdMarketDTO market, String fallbackDataSourceCode) {
        if (market != null && StringUtils.isNotBlank(market.getInternalDataSourceCode())) {
            return normalizeDataSourceCode(market.getInternalDataSourceCode());
        }
        if (market != null && StringUtils.isNotBlank(market.getDataSourceCode())) {
            return normalizeDataSourceCode(market.getDataSourceCode());
        }
        return normalizeDataSourceCode(fallbackDataSourceCode);
    }

    private static String resolveMarketDataSourceCode(ThirdMarketDTO market, String fallbackDataSourceCode) {
        return resolveDataSourceCode(market, fallbackDataSourceCode);
    }
}
