package com.panda.merge.dto.settle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MatchSettleEventDto extends AbstructMatchSettleDto{
    private String id;

    private Long standardMatchId;

    private Long periodId;

    private Long thirdEventSourceId;

    @ApiModelProperty(value = "1.进球比分事件2.进球方式和球员")
    private Integer eventType;

    private String eventCode;

    private Integer t1;

    private Integer t2;

    @ApiModelProperty(value = "结算编码")
    private String settleNum;

    @ApiModelProperty(value = "事件次序")
    private Integer eventOrder;

    private String eventName;

    @ApiModelProperty(value = "1.未确认2.已确认3.已结算")
    private Integer status;

    private String homeAway;

    @ApiModelProperty(value = "球员名")
    private String playerName;

    @ApiModelProperty(value = "球员namecode")
    private String playerNameCode;

    private String dataSourceCode;

    private Long sportId;

    @ApiModelProperty(value = "附加字段:进球方式等")
    private String extryInfo;

    @ApiModelProperty(value = "盘数")
    private Integer firstNum;

    @ApiModelProperty(value = "局数")
    private Integer secondNum;

    @ApiModelProperty(value = "主队盘比分")
    private Integer firstT1;

    @ApiModelProperty(value = "客队盘比分")
    private Integer firstT2;

    @ApiModelProperty(value = "主队局比分")
    private Integer secondT1;

    @ApiModelProperty(value = "客队局比分")
    private Integer secondT2;

    @ApiModelProperty(value = "操作类型:1.结算2.回滚结算3.重新结算")
    private Integer operateType;

    @ApiModelProperty(value = "操作人")
    private String operater;

    @ApiModelProperty(value = "用户ID")
    private String userid;

    @ApiModelProperty(value = "结算次数")
    private Integer settleTimes;

    @ApiModelProperty(value = "结算事件冻结0:未冻结1:冻结")
    private Integer scoresPeriodFreeze;

    private Long modifyTime;

    private Long createTime;

    private MatchSettleEventDto extryEvent;

    @ApiModelProperty(value = "总结算次数(不能回滚)")
    private Integer settleCount;

    /**
     * 根据key去匹配三方比分
     * */
    private String key;
    /**
     * 走水 : 0不走水 1走水
     * */
    private Integer goWaterStatus;

    /**
     * 是否需要审核  1 需要 0 不需要
     * */
    private Integer needCheck;

    /**
     * 回滚状态0未回滚，1回滚中
     * */
    private Integer rollBackStatus;
    /**
     * 回滚订单数
     * */
    private Long rollBackOrderCount;

    /**
     * 五分钟区间（5，10，15......90，每个区间+5；上半场绝杀49，下半场绝杀99，无进球0
     * */
    private String fiveMinSection;
    /**
     * 事件的 15分钟区间
     * 由上面字段赋值改变下发的时候触发
     * */
    private String fifteenMinSection;

    @ApiModelProperty(value = "有删除事件:1是0否")
    private Integer hasDeleteEvent;

    @ApiModelProperty(value = "有赛果不匹配事件:1是0否")
    private Integer hasDataMismatchEvent;

    @ApiModelProperty(value = "当前事件状态：0无1灰色区间2删除事件")
    private Integer currentEventStatus;

    @ApiModelProperty(value = "是否灰色区间：1是0不是")
    private Integer isGrey;

    /**
     * 延时结算秒数
     */
    private Long delayTimeSecond;

    @ApiModelProperty(value = "距离比赛开始多少秒（格式：23:20）")
    private String secondFromStart;
}
