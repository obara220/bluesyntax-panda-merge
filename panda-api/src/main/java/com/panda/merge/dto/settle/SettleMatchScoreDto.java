package com.panda.merge.dto.settle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SettleMatchScoreDto extends AbstructMatchSettleDto {

    /**
     *  赛事比分ID
     * */
    private Long matchScoreId;
    /**
     * 赛事ID
     * */
    private Long standardMatchId;
    /**
     * 主队比分
     * */
    private Integer t1;
    /**
     * 客队比分
     * */
    private Integer t2;

    private String eventCode;
    /**
     * 结算比分序号
     * */
    private Integer settleNum;

    @ApiModelProperty(value = "二次结算原因")
    private Integer settleReason;

    @ApiModelProperty(value = "其他详细原因")
    private String settleReasonDetail;

    @ApiModelProperty(value = "总结算次数(不能回滚)")
    private Integer settleCount;

}
