package com.panda.merge.dto.settle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleCheckEventDto implements Serializable {

    private Long id;

    private String eventCode;

    private Integer t1;

    private Integer t2;

    @ApiModelProperty(value = "结算编码")
    private String settleNum;

    @ApiModelProperty(value = "事件次序")
    private Integer eventOrder;

    private String homeAway;

//    @ApiModelProperty(value = "主队盘比分")
//    private Integer firstT1;
//
//    @ApiModelProperty(value = "客队盘比分")
//    private Integer firstT2;
//
//    @ApiModelProperty(value = "主队局比分")
//    private Integer secondT1;
//
//    @ApiModelProperty(value = "客队局比分")
//    private Integer secondT2;


}
