package com.panda.merge.dto.thirdmatch;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 杯赛淘汰赛
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
@Data
public class ThirdMatchPromotionChartDTO implements Serializable {

    @ApiModelProperty(value = "源联赛ID")
    @NotNull(message = "源联赛ID不能为null!")
    private String tournamentId;

    @ApiModelProperty(value = "源赛季ID")
    @NotNull(message = "源赛季ID不能为null!")
    private String seasonId;

    @ApiModelProperty(value = "数据来源")
    @NotNull(message = "数据来源不能为null!")
    private String dataSourceCode;

    @ApiModelProperty(value = "运动类型")
    @NotNull(message = "运动类型不能为null!")
    private Long sportId;

    @ApiModelProperty(value = "中文榜单名称")
    private String cnName;

    @ApiModelProperty(value = "英文榜单名称")
    private String enName;

    @ApiModelProperty(value = "组ID")
    private Long groupId;

    @ApiModelProperty(value = "系列赛ID")
    private String seriesId;

    @ApiModelProperty(value = "系列赛开始时间")
    private Date beginTime;

    @ApiModelProperty(value = "队伍1的ID(主队)")
    private String team1Id;

    @ApiModelProperty(value = "队伍2的ID(客队)")
    private String team2Id;

    @ApiModelProperty(value = "主队名称，通常在没有队伍1ID的时候，请显示该名称，那时候该名称将表示资格名单编号")
    private String team1Name;

    @ApiModelProperty(value = "客队名称，通常在没有队伍2ID的时候，请显示该名称，那时候该名称将表示资格名单编号")
    private String team2Name;

    @ApiModelProperty(value = "队伍1得分1(5)表示 全场:点球大战)")
    private String team1Score;

    @ApiModelProperty(value = "队伍2得分1(5)表示 全场:点球大战)")
    private String team2Score;

    @ApiModelProperty(value = "队伍1从哪个系列赛来,系列赛ID（仅对双败淘汰赛）")
    private Integer team1ComeFrom;

    @ApiModelProperty(value = "队伍2从哪个系列赛来,系列赛ID（仅对双败淘汰赛）")
    private Integer team2ComeFrom;

    @ApiModelProperty(value = "该系列赛包含的比赛ID列表,多个比赛ID用逗号隔开")
    private String matchIds;

    @ApiModelProperty(value = "轮次序号,从右边数,1开始")
    private Integer roundOrder;

    @ApiModelProperty(value = "纵向序号,从上至下,1开始")
    private Integer lineOrder;

    @ApiModelProperty(value = "双败淘汰赛组别(1.胜者组,2.败者组,3.决赛)")
    private Integer doubleEliminationGroup;

    @ApiModelProperty(value = "系列赛状态(0.占位,1.未开始,2.进行中,3.完成)")
    private Integer status;

    @ApiModelProperty(value = "胜利者(1:主,2:客)")
    private Integer winner;

    @ApiModelProperty(value = "轮次文字描述")
    private String roundDescription;

    @ApiModelProperty(value = "上一级系列赛的ID")
    private Integer parentId;

    @ApiModelProperty(value = "是否失效(0:否,1:是)")
    private Integer invalid;

    @ApiModelProperty(value = "源赛事ID")
    private String matchId;

    @ApiModelProperty(value = "主队标准时间内得分 足篮球：主队标准时间内的分、足球90分钟，篮球40/48分钟")
    private String homeTeamNormalTimeScore;

    @ApiModelProperty(value = "客队标准时间内得分 足篮球：客队标准时间内的分、足球90分钟，篮球40/48分钟")
    private String awayTeamNormalTimeScore;

    @ApiModelProperty(value = "足球主队加时赛上半场得分")
    private String homeExtraTimeFirstHalfScore;

    @ApiModelProperty(value = "足球客队加时赛上半场得分")
    private String awayExtraTimeFirstHalfScore;

    @ApiModelProperty(value = "足球主队加时赛下半场得分")
    private String homeExtraTimeSecondHalfScore;

    @ApiModelProperty(value = "足球客队加时赛下半场得分")
    private String awayExtraTimeSecondHalfScore;

    @ApiModelProperty(value = "主队标准时间内得分足篮球：主队标准时间内的分")
    private String homeTeamHalfTimeScore;

    @ApiModelProperty(value = "客队标准时间内得分足篮球：客队标准时间内的分")
    private String awayTeamHalfTimeScore;

    @ApiModelProperty(value = "是否当前赛季(0:否,1:是)")
    private Integer isCurrentSeason;

    private static final long serialVersionUID = 1L;

}