package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_third_score")
public class MatchSettleThirdScoreEntity implements Serializable {
    private static final long serialVersionUID = -8405448971333043921L;

    private Long id;

    @ApiModelProperty(value = "比分事件编码")
    private String eventCode;

    @ApiModelProperty(value = "三方赛事ID")
    private Long thirdMatchId;

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

    @ApiModelProperty(value = "是否灰色区间:1是0不是")
    private Integer isGrey;

    private Long modifyTime;

    private Long createTime;

}