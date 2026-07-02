package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DiscountOddsConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * matchId不能为null
     */
    @NotNull(message = "matchId不能为null!")
    private Long matchId;

    /**
     * 统一盘口ID不能为null
     */
    @NotNull(message = "marketId不能为null!")
    private Long marketId;

    /**
     * 统一投注项ID不能为null
     */
    @NotNull(message = "oddsId不能为null!")
    private Long oddsId;

    /**
     * discount不能为null
     */
    @NotNull(message = "scale不能为null!")
    private BigDecimal scale;

    /**
     * createTime不能为null，毫秒时间戳
     */
    @NotNull(message = "createTime不能为null!")
    private Long createTime;

    /**
     * status不能为null
     * 0-启用 1-停用 2-删除
     */
    @NotNull(message = "status不能为null!")
    private Integer status;

    /**
     * 玩法ID
     */
    @NotNull(message = "marketCategoryId不能为null!")
    private Long marketCategoryId;

    private Integer card;
}
