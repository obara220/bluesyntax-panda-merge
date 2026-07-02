package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 盘口位置详情DTO
 */
@Data
public class MarketPlaceDtlDTO implements Serializable {

    private static final long serialVersionUID = 7140382087626912703L;

    /**
     * 标准玩法ID
     */
    private Long standardCategoryId;

    /**
     * 盘口位置
     */
    private Integer placeNum;

    /**
     * 盘口位置开关，开关封锁状态
     */
    private String placeNumStatus ;

    /**
     * 子玩法ID
     */
    private Long childStandardCategoryId;

    /**
     * 风控防封，累封 需求状态透传给风控
     */
    private Integer placeNumStatusDisplay = 1;

}
