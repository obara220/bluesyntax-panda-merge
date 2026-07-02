package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ResDiscountOddsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "投注项原始赔率.单位:0.0001")
    private Integer originalOddsValue;

    private Long oddsId;

}
