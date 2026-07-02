package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CheckDiscountOddsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * matchId不能为null
     */
    @NotNull(message = "matchId不能为null!")
    private Long matchId;

    /**
     * 统一投注项ID不能为null
     */
    @NotNull(message = "oddsId不能为null!")
    private Long oddsId;
}
