package com.panda.merge.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 标准 提前结算投注项信息
 */
@Getter
@Setter
public class StandardMatchMarketOddsPreResultMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 标准投注项ID
     */
    private Long id;

    /**
     * 第三方投注项原始ID.
     */
    private String thirdOddsFieldSourceId;
    /**
     * 投注项类型
     */
    private String oddsType;

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
