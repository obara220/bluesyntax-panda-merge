package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateMarketCategoryDataSourceCodeDTO implements Serializable {
    private static final long serialVersionUID = -8358357118098456868L;

    /**
     * 标准赛事id
     */
    private Long matchId;

    /**
     * 盘口类型    0 滚球     1 早盘
     */
    private String marketType;

    /**
     * 玩法id
     */
    private Long marketCategoryId;

    /**
     * 开售类型  Unsold 未售    Sold已售
     */
    private String sellStatus;

    /**
     * 玩法开售数据源
     */
    private String dataSourceCode;

    /**
     * 操作人名称
     */
    private String userName;
}
