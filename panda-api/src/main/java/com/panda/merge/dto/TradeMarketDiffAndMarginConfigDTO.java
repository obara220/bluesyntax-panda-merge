/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 下发AO水差 margin配置
 * @Description
 */
@Data
public class TradeMarketDiffAndMarginConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * AO赛事ID
     */
    private String aoMatchId;
    /**
     * 标准赛事ID
     */
    private Long standardMatchInfoId;
    /**
     * 标准玩法ID
     */
    private Long standardCategoryId;
    /**
     * 标准子玩法ID
     */
    private Long childStandardCategoryId;
    /**
     * 盘口位置
     */
    private Integer placeNum;
    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;

    /**
     * 水差配置
     */
    private TradeMarketAutoDiffConfigItemDTO diffConfigs;

    /**
     * 投注项margin集合
     */
    private List<MarketMarginDtlDTO> marketMarginDtlDTOList;
    /**
     * 盘口值
     */
    private String addition1;

    private String linkId;
}
