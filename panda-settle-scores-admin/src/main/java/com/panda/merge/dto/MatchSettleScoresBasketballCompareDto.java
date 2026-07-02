package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleScoresBasketballCompareDto implements Serializable {



    @ApiModelProperty(value = "主队比分")
    private Integer t1;

    @ApiModelProperty(value = "客队比分")
    private Integer t2;



    @ApiModelProperty(value = "走水:0不走水1走水")
    private Integer goWaterStatus;

    @ApiModelProperty(value = "是否灰色区间：1是0不是")
    private Integer isGrey;


}
