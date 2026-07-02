package com.panda.merge.dto.settle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConfirmMatchSettleScoreDto extends AbstructMatchSettleDto{
    /**
     *  赛事比分ID
     * */
    private Long matchScoreId;

    private Long standardMatchId;

    private String eventCode;

    private Integer t1;

    private Integer t2;

    @ApiModelProperty(value = "结算编码")
    private String settleNum;

    @ApiModelProperty(value = "事件次序")
    private Integer eventOrder;

    private String homeAway;

    @ApiModelProperty(value = "球员namecode")
    private String playerNameCode;

    @ApiModelProperty(value = "附加字段:进球方式等")
    private String extryInfo;
}
