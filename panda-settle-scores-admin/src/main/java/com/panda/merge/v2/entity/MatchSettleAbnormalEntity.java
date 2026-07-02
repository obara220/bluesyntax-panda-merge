package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_abnormal")
public class MatchSettleAbnormalEntity implements Serializable {
    private static final long serialVersionUID = -4436559710065344346L;

    private Long id;

    @ApiModelProperty(value = "比分事件编码")
    private String eventCode;

    @ApiModelProperty(value = "主队比分")
    private Integer t1;

    @ApiModelProperty(value = "客队比分")
    private Integer t2;

    private String extryInfo;

    private String eventName;

    @ApiModelProperty(value = "操作人")
    private String operater;

    @ApiModelProperty(value = "用户ID")
    private String userid;

    @ApiModelProperty(value = "结算次数")
    private Integer settleTimes;

    @ApiModelProperty(value = "总结算次数(不能回滚)")
    private Integer settleCount;

    @ApiModelProperty(value = "结算比分编号")
    private String settleNum;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "标准赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "比分阶段")
    private Long periodId;

    @ApiModelProperty(value = "是比分还是事件1:比分2:事件")
    private Integer isScores;

    @ApiModelProperty(value = "状态0.未编辑1.未确认2.已确认3.已结算")
    private Integer status;

    private Long modifyTime;

    private Long createTime;

}