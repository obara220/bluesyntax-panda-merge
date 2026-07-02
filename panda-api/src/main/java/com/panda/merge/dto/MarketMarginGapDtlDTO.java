package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author BEVAN
 */
@Data
public class MarketMarginGapDtlDTO implements Serializable {

    private static final long serialVersionUID = -1513165566508017861L;
    /**
     * 投注项类型
     */
    private String oddsType;
    /**
     * 水差
     */
    private Double diffValue;
    /**
     * 概率差
     */
    private Double probability;
    /**
     * 描点
     */
    private Integer anchor;

    /**
     * margin
     */
    private Double margin;

    /**
     * 子玩法ID
     */
    private Long childStandardCategoryId;
}
