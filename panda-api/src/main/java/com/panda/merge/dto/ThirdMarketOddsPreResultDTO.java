package com.panda.merge.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 三方 提前结算投注项信息
 *
 * @author Mirro
 * @Project Name :  panda_data_realtime
 * @Package Name :  com.panda.sport.data.realtime.dto
 * @Description:
 * @date 2019/10/4 16:07
 * @ModificationHistory Who    When    What
 */
@Setter
@Getter
public class ThirdMarketOddsPreResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 第三方投注项原始ID.
     */
    private String thirdOddsFieldSourceId;
    /**
     * 投注项类型
     */
    private String oddsType;
    /**
     * 附加字段1
     */
    private String addition1;
    /**
     * 附加字段2
     */
    private String addition2;
    /**
     * 附加字段3
     */
    private String addition3;
    /**
     * 附加字段4
     */
    private String addition4;
    /**
     * 附加字段5
     */
    private String addition5;

    /**
     * 提前结算概率
     */
    private BigDecimal probabilities;
    /**
     * 提前结算概率:赢概率
     */
    private BigDecimal winPro;
    /**
     * 提前结算概率：输概率
     */
    private BigDecimal losePro;
    /**
     * 提前结算概率：赢半概率
     */
    private BigDecimal halfWinPro;
    /**
     * 提前结算概率：走水概率
     */
    private BigDecimal refundPro;
    /**
     * 提前结算概率：输半概率
     */
    private BigDecimal loseWinPro;
    /**
     * 无和局概率盘口重新计算后的概率
     */
    private BigDecimal calcProbability;
}
