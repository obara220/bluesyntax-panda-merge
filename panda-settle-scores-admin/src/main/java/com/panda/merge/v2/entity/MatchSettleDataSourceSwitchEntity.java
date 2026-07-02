package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("match_settle_data_source_switch")
public class MatchSettleDataSourceSwitchEntity implements Serializable {

    private Long id;

    @ApiModelProperty(value = "球种")
    private Long sportId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "进球开关")
    private Integer goal;

    @ApiModelProperty(value = "角球开关")
    private Integer corner;
    @ApiModelProperty(value = "罚牌开关")
    private Integer booking;
    @ApiModelProperty(value = "灰色区间开关")
    private Integer gray;
    @ApiModelProperty(value = "权重上限开关")
    private Integer topWeight;

    @ApiModelProperty(value = "数据商心跳开关")
    private Integer dataSourceHeartbeat;

    @ApiModelProperty(value = "单数据源结算开关")
    private Integer singleDataSourceSettle;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;
}