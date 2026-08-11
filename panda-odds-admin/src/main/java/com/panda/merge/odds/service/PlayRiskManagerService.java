package com.panda.merge.odds.service;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RiskManagerCodeEnums;
import com.panda.merge.model.StandardSportMarketSell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 4405：玩法级操盘模式（赔率服务侧）
 *
 * Redis Hash:
 *   key   : Ronghe:playRiskManager:{matchId}:{marketType}
 *   field : {categoryId}
 *   value : {riskManagerCode}
 */
@Service
@Slf4j
public class PlayRiskManagerService {

    @Autowired
    private RedisService redisService;

    public String buildKey(Long matchId, Integer marketType) {
        return Constant.REDIS_KEY.RONGHE_PLAY_RISK_MANAGER + matchId + ":" + marketType;
    }

    /**
     * 获取玩法级操盘模式，读不到/异常则回落赛事级。
     * 要求：永不抛异常（主流程不阻断）。
     */
    public String getPlayRiskManagerCode(String linkId,
                                         Long matchId,
                                         Integer marketType,
                                         Long categoryId,
                                         StandardSportMarketSell sell) {
        try {
            String key = buildKey(matchId, marketType);
            Object v = redisService.hGet(key, String.valueOf(categoryId));
            if (v != null && StringUtils.isNotBlank(String.valueOf(v))) {
                return String.valueOf(v);
            }
        } catch (Throwable t) {
            log.warn("::{}::getPlayRiskManagerCode read redis failed, matchId={}, marketType={}, categoryId={}",
                    linkId, matchId, marketType, categoryId, t);
        }
        return fallbackRiskManagerCode(marketType, sell);
    }

    public String fallbackRiskManagerCode(Integer marketType, StandardSportMarketSell sell) {
        if (sell == null) {
            return null;
        }
        if (Objects.equals(marketType, 1)) {
            return sell.getPreRiskManagerCode();
        }
        if (Objects.equals(marketType, 0)) {
            return sell.getLiveRiskManagerCode();
        }
        // unknown marketType: keep safe fallback
        return sell.getLiveRiskManagerCode();
    }

    /**
     * 初始化玩法级操盘模式（只补不盖）。
     * 失败策略：只告警不抛异常（弱依赖）。
     */
    public void initIfAbsent(String linkId,
                             Long matchId,
                             Integer marketType,
                             Collection<Long> categoryIds,
                             String defaultRiskManagerCode,
                             Long ttlSeconds) {
        if (matchId == null || marketType == null || CollectionUtils.isEmpty(categoryIds) || StringUtils.isBlank(defaultRiskManagerCode)) {
            return;
        }
        String key = buildKey(matchId, marketType);
        boolean wrote = false;
        try {
            for (Long categoryId : categoryIds) {
                if (categoryId == null) {
                    continue;
                }
                String field = String.valueOf(categoryId);
                /* 初始化时 也需要与下发玩法配置数据源对应 */
/*                Boolean has = redisService.hHasKey(key, field);
                if (Boolean.TRUE.equals(has)) {
                    continue;
                }*/
                redisService.hSet(key, field, defaultRiskManagerCode);
                wrote = true;
            }
            if (wrote && ttlSeconds != null && ttlSeconds > 0) {
                redisService.expire(key, ttlSeconds);
            }
        } catch (Throwable t) {
            log.warn("::{}::initIfAbsent failed, key={}, matchId={}, marketType={}, wroteAny={}",
                    linkId, key, matchId, marketType, wrote, t);
        }
    }

    /**
     * 按玩法批量切换玩法级操盘模式（覆盖写入）。
     */
    public void batchSet(String linkId,
                         Long matchId,
                         Integer marketType,
                         Collection<Long> categoryIds,
                         String riskManagerCode,
                         Long ttlSeconds) {
        if (matchId == null || marketType == null || CollectionUtils.isEmpty(categoryIds) || StringUtils.isBlank(riskManagerCode)) {
            return;
        }
        String key = buildKey(matchId, marketType);
        try {
            for (Long categoryId : categoryIds) {
                if (categoryId == null) {
                    continue;
                }
                redisService.hSet(key, String.valueOf(categoryId), riskManagerCode);
            }
            if (ttlSeconds != null && ttlSeconds > 0) {
                redisService.expire(key, ttlSeconds);
            }
            log.info("::{}::batchSet playRiskManager, key={}, categories={}, code={}", linkId, key, new HashSet<>(categoryIds), riskManagerCode);
        } catch (Throwable t) {
            // 写入口属于强校验入口的后半段，这里抛出由上层决定是否失败返回；因此不吞异常。
            log.error("::{}::batchSet playRiskManager failed, key={}, code={}", linkId, key, riskManagerCode, t);
            throw t;
        }
    }

    /**
     * 是否走 XTS/MTS 类链路（MTS/GTS/OTS/CTS/F2TS/BTS 都视为“XTS链路”）。
     */
    public boolean isMtsFamily(String riskManagerCode) {
        if (StringUtils.isBlank(riskManagerCode)) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.MTS.name())
                || StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.GTS.name())
                || StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.OTS.name())
                || StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.CTS.name())
                || StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.F2TS.name())
                || StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.BTS.name());
    }

    /**
     * 4405：操盘模式 <-> 玩法数据源对应关系（用于默认分配/纠偏）。
     * 当操盘模式为 XTS 家族时，只有对应数据源的玩法才应走该 XTS 链路；
     * 其他数据源玩法默认回落走 PA。
     *
     * @param riskManagerCode 操盘模式
     * @return 对应的数据源编码（如 SR/BG/BC/BE/F01），未知返回 null
     */
    public String expectedDataSourceCodeForRiskManager(String riskManagerCode) {
        if (StringUtils.isBlank(riskManagerCode)) {
            return null;
        }
        if (StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.MTS.name())) {
            return com.panda.merge.common.enums.DataSourceCodeEnum.SR.code;
        }
        if (StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.OTS.name())) {
            return com.panda.merge.common.enums.DataSourceCodeEnum.OD.code;
        }
        if (StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.GTS.name())) {
            return com.panda.merge.common.enums.DataSourceCodeEnum.BG.code;
        }
        if (StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.CTS.name())) {
            return com.panda.merge.common.enums.DataSourceCodeEnum.BC.code;
        }
        if (StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.BTS.name())) {
            return com.panda.merge.common.enums.DataSourceCodeEnum.BE.code;
        }
        if (StringUtils.equalsIgnoreCase(riskManagerCode, RiskManagerCodeEnums.F2TS.name())) {
            return com.panda.merge.common.enums.DataSourceCodeEnum.F01.code;
        }
        return null;
    }

    /**
     * 兼容内部数据源前缀（如 T01 -> TX）。
     */
    public String normalizeSellDataSourceCode(String dataSourceCode) {
        if (StringUtils.isBlank(dataSourceCode)) {
            return dataSourceCode;
        }
        if (dataSourceCode.startsWith("T01")) {
            return com.panda.merge.common.enums.DataSourceCodeEnum.TX.code;
        }
        return dataSourceCode;
    }

    /**
     * 4405：根据玩法数据源推断 XTS 家族操盘模式（用于 PA 赛事下的“非对应数据源玩法”默认分配）。
     *
     * @param dataSourceCode 玩法开售表数据源
     * @return 对应的风险操盘模式（如 SR->MTS, BG->GTS, BC->CTS, BE->BTS, F01->F2TS），未知返回 null
     */
    public String inferRiskManagerCodeByDataSource(String dataSourceCode) {
        String ds = normalizeSellDataSourceCode(dataSourceCode);
        if (StringUtils.isBlank(ds)) {
            return null;
        }
        if (StringUtils.equalsIgnoreCase(ds, com.panda.merge.common.enums.DataSourceCodeEnum.SR.code)) {
            return RiskManagerCodeEnums.MTS.name();
        }
        if (StringUtils.equalsIgnoreCase(ds, com.panda.merge.common.enums.DataSourceCodeEnum.BG.code)) {
            return RiskManagerCodeEnums.GTS.name();
        }
        if (StringUtils.equalsIgnoreCase(ds, com.panda.merge.common.enums.DataSourceCodeEnum.BC.code)) {
            return RiskManagerCodeEnums.CTS.name();
        }
        if (StringUtils.equalsIgnoreCase(ds, com.panda.merge.common.enums.DataSourceCodeEnum.BE.code)) {
            return RiskManagerCodeEnums.BTS.name();
        }
        if (StringUtils.equalsIgnoreCase(ds, com.panda.merge.common.enums.DataSourceCodeEnum.F01.code)) {
            return RiskManagerCodeEnums.F2TS.name();
        }
        return null;
    }
}

