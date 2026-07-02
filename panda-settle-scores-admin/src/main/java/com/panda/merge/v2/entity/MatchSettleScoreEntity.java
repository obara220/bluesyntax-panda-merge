package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("match_settle_score")
public class MatchSettleScoreEntity implements Serializable {
    private Long id;

    @ApiModelProperty(value = "比分事件编码")
    private String eventCode;

    @ApiModelProperty(value = "主队比分")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer t1;

    @ApiModelProperty(value = "客队比分")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer t2;

    @ApiModelProperty(value = "盘数")
    private Integer firstNum;

    @ApiModelProperty(value = "局数")
    private Integer secondNum;

    @ApiModelProperty(value = "主队盘比分")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer firstT1;

    @ApiModelProperty(value = "客队盘比分")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer firstT2;

    @ApiModelProperty(value = "主队局比分")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer secondT1;

    @ApiModelProperty(value = "客队局比分")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer secondT2;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
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
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer settleReason;

    @ApiModelProperty(value = "其他详细原因")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String settleReasonDetail;

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

    @ApiModelProperty(value = "当前已核对人员序号")
    private Integer checkNumber;

    @ApiModelProperty(value = "是否自动结算:1是0不是")
    private Integer isAutoSettle;

    @ApiModelProperty(value = "有删除事件:1是0否")
    private Integer hasDeleteEvent;

    @ApiModelProperty(value = "附加字段1")
    private String addition1;

    @ApiModelProperty(value = "附加字段2")
    private String addition2;

    @ApiModelProperty(value = "当前事件状态：0无1灰色区间2删除事件")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer currentEventStatus;

    @ApiModelProperty(value = "是否灰色区间：1是0不是")
    private Integer isGrey;

    @ApiModelProperty(value = "走水:0不走水1走水")
    private Integer goWaterStatus;

    @ApiModelProperty(value = "当前比分标记：0无1有数据商与结算比分不同标记")
    private Integer currentEventTag;

    @ApiModelProperty(value = "同步数据按钮用户信息")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String popupUsers;

    @ApiModelProperty(value = "已结算比分校验标记:1是0否")
    private Integer scoreCheckTag;

    private Long modifyTime;

    private Long createTime;

    @ApiModelProperty(value = "事件时间")
    private Long eventTime;
}