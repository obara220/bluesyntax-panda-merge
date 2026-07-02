package com.panda.merge.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class MarketCategorySellBO  implements Serializable {

    @ApiModelProperty(value = "玩法id")
    private Long marketCategoryId;


    @ApiModelProperty(value = "比赛进程时间")
    private Integer matchProgressTime;


    @ApiModelProperty(value = "足球自动关盘时间设置：6、上半场期间41、加时赛上半场7、下半场期间42、加时赛下半场篮球自动关盘时间设置：13、第1节14、第2节15、第3节16、第4节40、加时")
    private Integer autoCloseMarket;


    @ApiModelProperty(value = "补时时间")
    private Integer injuryTime;

    @ApiModelProperty(value = "自动开盘阶段")
    private Integer autoOpenMarket;

    @ApiModelProperty(value = "自动开盘时间")
    private Integer autoOpenTime;
}
