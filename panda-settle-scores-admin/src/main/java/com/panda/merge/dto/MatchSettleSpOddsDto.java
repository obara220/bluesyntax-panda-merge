package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleSpOddsDto implements Serializable {
    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "盘口Id")
    private Long marketId;

    @ApiModelProperty(value = "投注项英文名")
    private String oddsNameEn;

    @ApiModelProperty(value = "投注项中文名")
    private String oddsNameCn;

    @ApiModelProperty(value = "结算状态int(8)默认01编辑2确认3结算")
    private Integer settleStatus;

    @ApiModelProperty(value = "结算总次数，不能回滚")
    private Integer settleCount;

    @ApiModelProperty(value = "当前结算次数")
    private Integer settleTimes;

    @ApiModelProperty(value = "1输2赢3取消4走水")
    private Integer settleResult;

    @ApiModelProperty(value = "审核次序")
    private Integer checkNumber;

    @ApiModelProperty(value = "是否自动结算0否1是")
    private Integer isAutoSettle;

    @ApiModelProperty(value = "二次结算原因")
    private Integer settleReason;

    @ApiModelProperty(value = "二次结算详细原因")
    private String settleReasonDetail;

    @ApiModelProperty(value = "结算冻结0:未冻结1:冻结")
    private Integer settleFreeze;
    @ApiModelProperty(value = "排序")
    private Integer orderOdds;
    @ApiModelProperty(value = "操作人")
    private String operater;

    @ApiModelProperty(value = "用户id")
    private String userid;

    @ApiModelProperty(value = "操作类型1结算2回滚结算3重新结算")
    private Integer operateType;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;


}