package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleScoreMessage implements Serializable {
    private Long id;

    @ApiModelProperty(value = "比分事件编码")
    private String eventCode;

    @ApiModelProperty(value = "主队比分")
    private Integer t1;

    @ApiModelProperty(value = "客队比分")
    private Integer t2;

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

    private String extryInfo;

    private String eventName;

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

    @ApiModelProperty(value = "结算比分编号")
    private String settleNum;

    @ApiModelProperty(value = "比分状态:0未确认1已确认2已结算")
    private Integer status;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "标准赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "比分阶段")
    private Long periodId;

    @ApiModelProperty(value = "结算比分冻结0未冻结1冻结")
    private Integer settleFreeze;

    @ApiModelProperty(value = "走水:0不走水1走水")
    private Integer goWaterStatus;

    @ApiModelProperty(value = "操作级别: 1赛事,2玩法级,3比分阶段级")
    private Integer level;

    @ApiModelProperty(value = "玩法级,1进球,2角球,3罚牌")
    private Integer playCategory;

    @ApiModelProperty(value = "是否异常结算 1:是  0:否")
    private Integer isAbnormal  = 0;

    @ApiModelProperty(value = "自动结算 1是0不是")
    private Integer isAutoSettle;

    @ApiModelProperty(value = "当前已核对人员序号")
    private Integer checkNumber;

    @ApiModelProperty(value = "是否灰色区间：1是0不是")
    private Integer isGrey;

    private String addition1;

    private String addition2;

    private Long modifyTime;

    private Long createTime;

    /**
     * 事件时间
     * */
    private Long eventTime;

    private static final long serialVersionUID = 1L;

    private String settleOrderNums;

}