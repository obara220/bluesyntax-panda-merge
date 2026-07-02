package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 联赛球队历史表现DTO
 *
 * @author aldrich
 * @since 2024/10/14
 */
@Data
public class ThirdMatchHistoryExpressionDTO implements Serializable {

    private static final long serialVersionUID = -4476845732066037172L;

    @NotNull(message = "三方数据源联赛ID不能为空")
    private String thirdTournamentSourceId;

    @NotNull(message = "三方数据源球队ID不能为空")
    private String thirdTeamSourceId;

    @NotNull(message = "数据源编码不能为空")
    private String dataSourceCode;
    //球队中文名称
    private String teamCnName;
    //球队英文名称
    private String teamEnName;
    //数据同步类型,0:自动 1:手动
    private Integer editStatus;

    @NotNull(message = "运动类型不能为空")
    private Long sportId;
    //联赛表现排名:如10/20
    private String expressionRanking;
    //数据类型,0:总体 1:主队 2:客队
    private Integer expressingType;

    @NotNull(message = "最近第1场赛事状态不能为空")
    private Integer firstStatus;

    private Integer secondStatus;

    private Integer thirdStatus;

    private Integer fourthStatus;

    private Integer fifthStatus;

    private Integer goalsForTotal;

    @NotNull(message = "最近5场赢球占比不能为空")
    private BigDecimal winPercent;

    @NotNull(message = "两队都得分占比不能为空")
    private BigDecimal bothGoalPercent;

    @NotNull(message = "没有失球占比不能为空")
    private BigDecimal notLostPercent;

    @NotNull(message = "第一队入球占比不能为空")
    private BigDecimal firstGoalPercent;

    @NotNull(message = "平均进球占比不能为空")
    private BigDecimal averageGoalPercent;

    @NotNull(message = "得分占比不能为空")
    private BigDecimal goalPercent;

    @NotNull(message = "失球占比不能为空")
    private BigDecimal lostGoalPercent;

    @NotNull(message = "xG不能为空")
    private BigDecimal goalXg;

    @NotNull(message = "xGA不能为空")
    private BigDecimal goalXga;
}
