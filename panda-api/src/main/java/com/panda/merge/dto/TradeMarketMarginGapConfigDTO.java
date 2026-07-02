package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author BEVAN
 */
@Data
public class TradeMarketMarginGapConfigDTO implements Serializable {
    private static final long serialVersionUID = 7140382087626912703L;

    /**
     * 标准赛事ID
     */
    private Long standardMatchInfoId;
    /**
     * 标准玩法ID
     */
    private Long standardCategoryId;

    /**
     *  联动模式：0(否),1(是)
     */
    private Integer linkageMode;

    /**
     *  位置
     */
    private Integer placeNum;

    /**
     * 投注项margin集合
     */
    private List<MarketMarginGapDtlDTO> list;

    /**
     * 子玩法ID
     */
    private Long childStandardCategoryId;
}
