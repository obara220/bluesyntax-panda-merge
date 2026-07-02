package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class FtsMatchRelation implements Serializable {
    @ApiModelProperty(value = "新标准赛事Id")
    private Long newMatchId;

    @ApiModelProperty(value = "新赛事主队原赛事Id")
    private Long newHomeMatchId;

    @ApiModelProperty(value = "新赛事客队原赛事Id")
    private Long newAwayMatchId;

    @ApiModelProperty(value = "新赛事原主队球队Id")
    private Long newHomeTeamId;

    @ApiModelProperty(value = "新赛事原客队球队Id")
    private Long newAwayTeamId;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    private static final long serialVersionUID = 1L;

}