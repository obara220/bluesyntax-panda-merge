package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_gray_weight")
public class MatchSettleGrayWeightEntity implements Serializable {
    private static final long serialVersionUID = 9142407109700890252L;
    
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "灰色类型编码:15进球min15Goal5进球min5Goal15角球min15Corner")
    private String grayCode;

    @ApiModelProperty(value = "灰色区间分钟数5~90")
    private Integer grayAreaMin;

    @ApiModelProperty(value = "灰色区间设置模版id")
    private String dataSourceCode;

    @ApiModelProperty(value = "灰色区间状态0待确认1已确认")
    private Integer grayStatus;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

}