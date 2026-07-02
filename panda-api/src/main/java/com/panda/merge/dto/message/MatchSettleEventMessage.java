package com.panda.merge.dto.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleEventMessage implements Serializable {
    private Long id;

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

    @ApiModelProperty(value = "总结算次数(不能回滚)")
    private Integer settleCount;

    @ApiModelProperty(value = "二次结算原因")
    private Integer settleReason;

    @ApiModelProperty(value = "其他详细原因")
    private String settleReasonDetail;

    @ApiModelProperty(value = "结算事件冻结0:未冻结1:冻结")
    private Integer settleFreeze;

    @ApiModelProperty(value = "走水:0不走水1走水")
    private Integer goWaterStatus;

    @ApiModelProperty(value = "操作级别: 1赛事,2玩法级,3比分阶段级")
    private Integer level;

    @ApiModelProperty(value = "玩法级,1进球,2角球,3罚牌")
    private Integer playCategory;

    @ApiModelProperty(value = "是否异常结算 1:是  0:否")
    private Integer isAbnormal  = 0;

    @ApiModelProperty(value = "是否次序结算1是0不是")
    private Integer isSequenceSettle;

    @ApiModelProperty(value = "是否灰色区间：1是0不是")
    private Integer isGrey;

   @ApiModelProperty(value = "是否自动结算:1是0不是")
    private Integer isAutoSettle;

    @ApiModelProperty(value = "当前已核对人员序号")
    private Integer checkNumber;

    private Long modifyTime;

    private Long createTime;

    private String addition1;

    private String addition2;

    /**
     * 事件时间
     * */
    private Long eventTime;

    @ApiModelProperty(value = "五分钟区间（5，10，15......90，每个区间+5；上半场绝杀49，下半场绝杀99，无进球0）")
    private String fiveMinSection;

    private static final long serialVersionUID = 1L;

}