package com.panda.merge.dto.settle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MatchSettleEventExtryInfoDto extends AbstructMatchSettleDto{
    private String id;

    private Long standardMatchId;

    private Long periodId;

    private Long thirdEventSourceId;

    private String eventCode;

    @ApiModelProperty(value = "结算编码")
    private String settleNum;

    @ApiModelProperty(value = "事件次序")
    private Integer eventOrder;

    private String homeAway;

    @ApiModelProperty(value = "球员名")
    private String playerName;

    @ApiModelProperty(value = "球员namecode")
    private String playerNameCode;

    @ApiModelProperty(value = "附加字段:进球方式等")
    private String extryInfo;

    @ApiModelProperty(value = "盘数")
    private Integer firstNum;

    @ApiModelProperty(value = "局数")
    private Integer secondNum;

    /**
     * 根据key去匹配三方比分
     * */
    private String key;

    private Integer t1;

    private Integer t2;

    @ApiModelProperty(value = "1.进球比分事件2.进球方式和球员")
    private Integer eventType;

    @ApiModelProperty(value = "距离比赛开始多少秒（格式：23:20）")
    private String secondFromStart;
}
