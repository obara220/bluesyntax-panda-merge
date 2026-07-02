package com.panda.merge.dto.settle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditMatchSettleEventDto extends AbstructMatchSettleDto {

    /**
     * 事件ID
     * */
    private Long eventId;
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

    private String matchPlayerNameCode;

    @ApiModelProperty(value = "附加字段:进球方式等")
    private String extryInfo;


    @ApiModelProperty(value = "二次结算原因")
    private Integer settleReason;

    @ApiModelProperty(value = "其他详细原因")
    private String settleReasonDetail;

    @ApiModelProperty(value = "总结算次数(不能回滚)")
    private Integer settleCount;
    /**
     * 0 不走水 1走水
     * */
    private Integer goWaterStatus;
    /**
     * 0 不是点球大战 1是点球大战
     * */
    private Integer isDianQiu;
    /**
     * 五分钟区间（5，10，15......90，每个区间+5；上半场绝杀49，下半场绝杀99，无进球0
     * */
    private String fiveMinSection;

    /**
     * 事件的 15分钟区间
     * 由上面字段赋值改变下发的时候触发
     * */
    private String fifteenMinSection;
}
