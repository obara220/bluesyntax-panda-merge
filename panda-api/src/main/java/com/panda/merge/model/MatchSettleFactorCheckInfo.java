package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MatchSettleFactorCheckInfo implements Serializable {
    private Long id;

    private Long standardMatchId;

    private String settleScoreEventId;

    private String dataSourceCode;

    private String homeAway;

    private BigDecimal t1;

    private BigDecimal t2;

    private String settleNum;

    @ApiModelProperty(value = "0:未结算,1：已结算")
    private Integer status;

    private Long eventTime;

    private Long modifyTime;

    private Long createTime;

    private static final long serialVersionUID = 1L;

}