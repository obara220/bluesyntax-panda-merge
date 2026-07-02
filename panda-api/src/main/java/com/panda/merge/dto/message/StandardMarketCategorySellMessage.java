package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardMarketCategorySellMessage  implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 标准赛事ID
     */
    private Long matchId;
    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;
    /**
     * 玩法集合List
     */
    private Long marketCategoryId;
    /**
     * 开售状态未售Unsold,开售 Sold
     */
    private String sellStatus;
    /**
     * 玩法开售数据源
     */
    private String dataSourceCode;
}
