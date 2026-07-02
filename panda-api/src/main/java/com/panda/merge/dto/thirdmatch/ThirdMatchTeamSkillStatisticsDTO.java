package com.panda.merge.dto.thirdmatch;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 赛事球队技术统计
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
@Data
public class ThirdMatchTeamSkillStatisticsDTO implements Serializable {

    @ApiModelProperty(value = "源赛事ID")
    @NotNull(message = "源赛事ID不能为null!")
    private String matchId;

    @ApiModelProperty(value = "数据来源")
    @NotNull(message = "数据来源不能为null!")
    private String dataSourceCode;

    @ApiModelProperty(value = "运动类型")
    @NotNull(message = "运动类型不能为null!")
    private Long sportId;

    @ApiModelProperty(value = "源球队ID")
    @NotNull(message = "源球队ID不能为null!")
    private String teamId;

    @ApiModelProperty(value = "主客队（1:主,2:客）")
    @NotNull(message = "主客队不能为null!")
    private String homeAway;

    @ApiModelProperty(value = "篮板总数，包括进攻篮板和防守篮板")
    private Integer rebound;

    @ApiModelProperty(value = "进攻篮板数量")
    private Integer offensiveRebound;

    @ApiModelProperty(value = "防守篮板数量")
    private Integer defensiveRebound;

    @ApiModelProperty(value = "助攻数量")
    private Integer assist;

    @ApiModelProperty(value = "盖帽数量")
    private Integer block;

    @ApiModelProperty(value = "抢断数量")
    private Integer steal;

    @ApiModelProperty(value = "失误数量")
    private Integer turnover;

    @ApiModelProperty(value = "得分")
    private Integer score;

    @ApiModelProperty(value = "犯规数量")
    private Integer fouls;

    private static final long serialVersionUID = 1L;

}