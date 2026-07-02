package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_data_source_weight_config")
public class MatchSettleDataSourceWeightConfigEntity implements Serializable {
    private static final long serialVersionUID = 4476597168862398346L;

    private Long id;

    @ApiModelProperty(value = "数据商编码,BG、BT")
    private String dataSourceCode;

    @ApiModelProperty(value = "联赛等级")
    private Integer tournamentLevel;

    @ApiModelProperty(value = "球种,1:足球、2:篮球")
    private Long sportId;

    @ApiModelProperty(value = "开关状态,0:关闭、1:开启")
    private Integer status;

    @ApiModelProperty(value = "数据商权重值")
    private Integer weightNum;

    private Long createTime;

    private Long modifyTime;

}