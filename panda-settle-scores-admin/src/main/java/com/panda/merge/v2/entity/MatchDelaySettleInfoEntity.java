package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_delay_settle_info")
public class MatchDelaySettleInfoEntity implements Serializable {
    private static final long serialVersionUID = -6052728457823864804L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "比分id")
    private Long scoreId;

    @ApiModelProperty(value = "match_check_info_id")
    private Long checkInfoId;

    @ApiModelProperty(value = "标准赛事Id")
    private Long standardMatchId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "延迟结算时间")
    private Long delayTime;


    @ApiModelProperty(value = "延迟结算秒数")
    private Long delayTimeSecond;

    @ApiModelProperty(value = "延迟类型1比分 2事件")
    private Integer delayType;

    @ApiModelProperty(value = "是否已经结算0未结算 3已结算")
    private Integer settleStatus;

    @ApiModelProperty(value = "创建时间.UTC时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间.UTC时间")
    private Long modifyTime;

}