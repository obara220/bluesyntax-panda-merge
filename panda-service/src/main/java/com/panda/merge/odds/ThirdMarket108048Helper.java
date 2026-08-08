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

    public static String buildFieldKey(Long categoryId, String dataSourceCode) {
        return categoryId + ":" + normalizeDataSourceCode(dataSourceCode);
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

    public static ThirdMarketModifytimeDTO fromMarkets(Long matchId, Long categoryId, String dataSourceCode, List<ThirdMarketDTO> markets) {
        if (matchId == null || categoryId == null || markets == null || markets.isEmpty()) {
            return null;
        }
        ThirdMarketDTO lastMarket = markets.stream()
                .filter(Objects::nonNull)
                .max(Comparator.comparing(m -> m.getModifyTime() != null ? m.getModifyTime() : 0L))
                .orElse(null);
        if (lastMarket == null) {
            return null;
        }
        ThirdMarketModifytimeDTO dto = new ThirdMarketModifytimeDTO();
        dto.setMatchId(matchId);
        dto.setCategoryId(categoryId);
        dto.setDateSourceCode(resolveMarketDataSourceCode(lastMarket, dataSourceCode));
        dto.setLastModifyTime(lastMarket.getModifyTime() != null ? lastMarket.getModifyTime() : System.currentTimeMillis());
        //需求：红灯=赔率源"最后一笔数据"为关盘，所以状态取时间最新的那一笔，不做多盘口聚合
        dto.setStatus(normalizeStatus(lastMarket.getStatus()));
        return dto;
    }

    /**
     * 合法的field格式为 玩法ID:数据源编码。
     * 历史版本写入过只有玩法ID的field(如 "4")，风控按 玩法ID:数据源编码 取不到，且缓存不过期会一直残留，读写两侧都要剔除。
     */
    public static boolean isValidFieldKey(String fieldKey) {
        if (StringUtils.isBlank(fieldKey)) {
            return false;
        }
        int idx = fieldKey.indexOf(':');
        return idx > 0 && idx < fieldKey.length() - 1 && parseCategoryId(fieldKey) != null;
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
        return incomingTime >= existingTime ? incoming : existing;
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
            // 黄灯：60秒未更新(开盘/封盘/锁盘)，或60秒内有更新但为封盘/锁盘等非开盘状态
            dto.setLevel(LEVEL_YELLOW);
        }
    }

    /**
     * 取本次下发的数据源编码，作为"盘口自己没有任何编码"时的兜底。
     * 百家赔一次下发同属一个站点，站点编码可能挂在 internalDataSourceCode 上，也可能直接是盘口的
     * dataSourceCode(如 L01-Bet365)，取值口径与 A99ThirdAllBatchMarketProcessor#getInternalDataSourceCode 一致：
     * 先找有 internalDataSourceCode 的盘口，其次找有 dataSourceCode 的盘口，都没有才用下发的数据源编码。
     */
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

    /**
     * 解析 108048 使用的数据源编码：内部站点走 internalDataSourceCode，否则走 dataSourceCode。
     */
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
