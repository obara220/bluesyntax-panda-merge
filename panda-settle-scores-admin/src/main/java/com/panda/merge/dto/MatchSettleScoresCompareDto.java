package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleScoresCompareDto implements Serializable {

    @ApiModelProperty(value = "比分事件编码")
    private String eventCode;

    @ApiModelProperty(value = "主队比分")
    private Integer t1;

    @ApiModelProperty(value = "客队比分")
    private Integer t2;

    @ApiModelProperty(value = "盘数")
    private Integer firstNum;

    @ApiModelProperty(value = "主队盘比分")
    private Integer firstT1;

    @ApiModelProperty(value = "客队盘比分")
    private Integer firstT2;

    @ApiModelProperty(value = "主队局比分")
    private Integer secondT1;

    @ApiModelProperty(value = "客队局比分")
    private Integer secondT2;

    @ApiModelProperty(value = "走水:0不走水1走水")
    private Integer goWaterStatus;

    private Integer isGrey;
}
