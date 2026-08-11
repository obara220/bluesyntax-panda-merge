package com.panda.merge.component;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.model.ConfigMarketMarginGap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 足球数据源margin玩法：数据商抽水赔率 -> 赛事模板margin抽水赔率 转换
 * <p>
 * 需求98442：足球margin玩法直接使用数据商抽水赔率，抽水由数据商决定，我方不可控且存在赔率联动问题投诉。
 * 开关打开后，先把数据商赔率还原为公平赔率，再按赛事模板(自由模板)配置的margin重新抽水，
 * 转换后的赔率继续走原有的概率差(跳水)、水差、优美优化等逻辑。
 * <p>
 * 转换公式：
 * margin-ds  = 1/odds1-ds + 1/odds2-ds + 1/odds3-ds + ...   (数据商抽水，>1)
 * 概率维度： p n-new  = (p n-ds / margin-ds) * margin-panda   (需求单给出的公式)
 * 赔率维度： odds n-new = 1 / p n-new = odds n-ds * margin-ds / margin-panda
 * margin-ds <= 1 时数据不合法，不转换，走原有逻辑。
 * <p>
 * 说明：需求单里 "odds n-new = (odds n-ds / margin-ds) * margin-panda" 是概率的算法，
 * 直接套在赔率上得到的抽水是 margin-ds²/margin-panda 而不是 margin-panda，达不到"可控抽水"的目的。
 * 例：数据商 1.26/7.02/9.54(margin-ds=1.0409)，模板margin=104%
 * 按赔率直接套公式 -> 1.2589/7.0138/9.5315，实际抽水 104.18%
 * 按本类实现     -> 1.2611/7.0262/9.5485，实际抽水 104.00%
 */
@Slf4j
@Component
public class FootballMarginTemplateConverter {

    /**
     * 赔率放大倍数，赔率以 1.27 -> 127000 的形式存储
     */
    private static final double ODDS_MULTIPLE = 100000D;

    /**
     * 赛事模板margin合法区间(百分比)，超出区间视为配置异常，不转换，走原有逻辑
     */
    private static final double MIN_TEMPLATE_MARGIN = 100D;
    private static final double MAX_TEMPLATE_MARGIN = 200D;

    /**
     * 足球数据源margin玩法走赛事模板抽水开关：true-开(默认)，false-关(走原有数据商抽水逻辑)
     * 沿用滚球侧原有的 nacos key，运维配置不需要变更
     */
    @NacosValue(value = "${market.98442.football:true}", autoRefreshed = true)
    private boolean footballMarginTemplateSwitch;

    /**
     * A99线是否同步启用，默认关闭。需求只明确了足球早盘/滚球，A99线待产品确认后在 nacos 打开
     */
    @NacosValue(value = "${market.98442.football.a99:false}", autoRefreshed = true)
    private boolean footballMarginTemplateA99Switch;

    /**
     * 早盘/滚球：是否需要按赛事模板margin重新抽水
     * 需求限定"只处理足球的数据源接入的margin"，A01(自营操盘)的赔率不是数据商抽水，不做转换
     */
    public boolean isOpen(Long sportId, String dataSourceCode) {
        return footballMarginTemplateSwitch
                && StandardSportTypeEnum.FootBall.code.equals(sportId)
                && !DataSourceCodeEnum.AO.code.equals(dataSourceCode);
    }

    /**
     * A99线：是否需要按赛事模板margin重新抽水
     */
    public boolean isA99Open(Long sportId, String dataSourceCode) {
        return footballMarginTemplateA99Switch && isOpen(sportId, dataSourceCode);
    }

    /**
     * 数据商抽水赔率按赛事模板margin重新抽水
     *
     * @param oddsList     盘口投注项(赔率为数据商抽水赔率 oddsValue)
     * @param marginGapMap 赛事模板margin配置，key=投注项类型
     * @return key=投注项类型，value=转换后赔率(小数)。不满足转换条件时返回空map，调用方继续走原有逻辑
     */
    public Map<String, Double> convertOddsByTemplateMargin(String linkId, Long standardMatchInfoId, Long marketCategoryId,
                                                          List<StandardMarketOddsDataMessage> oddsList,
                                                          Map<String, ConfigMarketMarginGap> marginGapMap) {
        if (CollectionUtils.isEmpty(oddsList)) {
            return Collections.emptyMap();
        }
        //赛事模板margin(margin-panda)。没有配置就没有"赛事模板抽水"可言，不能拿代码默认值110去改赔率，直接走原有逻辑
        Double templateMargin = getTemplateMargin(marginGapMap);
        if (templateMargin == null || templateMargin < MIN_TEMPLATE_MARGIN || templateMargin > MAX_TEMPLATE_MARGIN) {
            log.info("::{}::足球margin玩法赛事模板抽水,赛事模板margin不存在或不合法不转换,标准赛事ID:{},玩法ID:{},margin:{}",
                    linkId, standardMatchInfoId, marketCategoryId, templateMargin);
            return Collections.emptyMap();
        }
        //margin-ds = 1/odds1-ds + 1/odds2-ds + ...
        double marginDs = 0D;
        for (StandardMarketOddsDataMessage marketOdds : oddsList) {
            Integer oddsValue = marketOdds.getOddsValue();
            if (oddsValue == null || oddsValue <= 0) {
                log.info("::{}::足球margin玩法赛事模板抽水,存在无效数据商赔率不转换,标准赛事ID:{},玩法ID:{},投注项:{},赔率:{}",
                        linkId, standardMatchInfoId, marketCategoryId, marketOdds.getOddsType(), oddsValue);
                return Collections.emptyMap();
            }
            double dsOdds = BigDecimalUtils.divide(oddsValue, ODDS_MULTIPLE, 2);
            if (dsOdds <= 0) {
                log.info("::{}::足球margin玩法赛事模板抽水,数据商赔率转换后为0不转换,标准赛事ID:{},玩法ID:{},投注项:{},赔率:{}",
                        linkId, standardMatchInfoId, marketCategoryId, marketOdds.getOddsType(), oddsValue);
                return Collections.emptyMap();
            }
            marginDs = BigDecimalUtils.add(marginDs, BigDecimalUtils.divide(1, dsOdds, 8));
        }
        //margin-ds <= 1 不合法，走原有逻辑
        if (marginDs <= 1) {
            log.info("::{}::足球margin玩法赛事模板抽水,数据商margin<=1不合法不转换,标准赛事ID:{},玩法ID:{},margin-ds:{}",
                    linkId, standardMatchInfoId, marketCategoryId, marginDs);
            return Collections.emptyMap();
        }
        //margin-panda 百分比转换为倍数：104 -> 1.04
        double marginPanda = BigDecimalUtils.divide(templateMargin, 100D, 4);
        //抽水置换系数 = margin-ds / margin-panda：先按数据商margin还原公平赔率，再按赛事模板margin重新抽水
        double marginRate = BigDecimalUtils.divide(marginDs, marginPanda, 8);
        Map<String, Double> convertOddsMap = new HashMap<>(oddsList.size());
        for (StandardMarketOddsDataMessage marketOdds : oddsList) {
            double dsOdds = BigDecimalUtils.divide(marketOdds.getOddsValue(), ODDS_MULTIPLE, 2);
            //odds-new = odds-ds * margin-ds / margin-panda
            double convertOdds = BigDecimalUtils.multiply(dsOdds, marginRate, 4);
            convertOddsMap.put(marketOdds.getOddsType(), convertOdds);
        }
        //盘口级别的日志，滚球下发量大，只打一条标量日志：转换前赔率见下游三项盘独赢计算日志，
        //转换后赔率 = 转换前赔率 × 置换系数，可反推，不再额外拼装赔率map
        log.info("::{}::足球margin玩法赛事模板抽水,标准赛事ID:{},玩法ID:{},数据商margin:{},赛事模板margin:{},置换系数:{}",
                linkId, standardMatchInfoId, marketCategoryId, marginDs, marginPanda, marginRate);
        return convertOddsMap;
    }

    /**
     * 取赛事模板margin(百分比)，玩法下各投注项共用一个margin，配置不一致时取最大值。
     * 没有配置、或配置里没有一个有效margin时返回null，由调用方走原有逻辑，不使用代码默认值。
     */
    private Double getTemplateMargin(Map<String, ConfigMarketMarginGap> marginGapMap) {
        if (CollectionUtils.isEmpty(marginGapMap)) {
            return null;
        }
        Double templateMargin = null;
        for (ConfigMarketMarginGap configMarketMarginGap : marginGapMap.values()) {
            if (configMarketMarginGap == null || configMarketMarginGap.getMargin() == null) {
                continue;
            }
            if (templateMargin == null || configMarketMarginGap.getMargin() > templateMargin) {
                templateMargin = configMarketMarginGap.getMargin();
            }
        }
        return templateMargin;
    }
}
