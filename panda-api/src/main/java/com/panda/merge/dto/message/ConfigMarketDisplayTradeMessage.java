package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jimmy
 */
@Data
public class ConfigMarketDisplayTradeMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long standardMatchId;

    /**
     * 滚球盘口显示数量
     */
    private Integer liveMarketCount;

    /**
     * 早盘盘口显示数量
     */
    private Integer displayMarketCount;

    /**
     * Y:展示，N：不展示
     */
    private String displayCorner;

    /**
     * Y:展示，N：不展示
     */
    private String displayPenaltyCard;

    private Long createTime;

    private Long modifyTime;
}
