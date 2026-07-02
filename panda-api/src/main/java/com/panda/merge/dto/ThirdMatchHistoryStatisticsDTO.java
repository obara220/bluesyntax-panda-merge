/**
 *
 */
package com.panda.merge.dto;

import com.panda.merge.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 三方赛事历史统计信息DTO
 * @author tell
 * @since  2021年2月9日15:46:47
 */
@Data
public class ThirdMatchHistoryStatisticsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "数据源赛事ID不能为null!")
    private String thirdMatchSourceId;
    /** 数据源联赛ID*/
    private String thirdTournamentSourceId;
    /** 联赛类别(0:其他,1联赛,2杯赛)*/
    private Integer tournamentType;
    /** 数据源赛季ID*/
    private String thirdSeasonSourceId;
    @NotNull(message = "数据源赛事运动类型不能为null!")
    private Long sportId;
    @NotNull(message = "数据来源不能为null!")
    private String dataSourceCode;
    @NotNull(message = "赛事开始时间不能为null!")
    private Long beginTime;
    /** 赛事状态*/
    private String matchStatus;
    /** 是否分组赛（0：否，1：是）*/
    @NotNull(message = "是否分组赛不能为null!")
    @EnumValue(message = "是否分组赛值非法，值应为{0,1}其中之一,请检查",intValues ={0,1})
    private Integer matchGroup;
    /** 分组id 对应分组信息*/
    private String groupId;

    @NotNull(message = "数据源主队ID不能为null!")
    private String homeTeamId;
    @NotNull(message = "数据源客队ID不能为null!")
    private String awayTeamId;
    /** 数据源主队名称*/
    private String homeTeamName;
    /** 数据源客队名称*/
    private String awayTeamName;
    /** 主队全场得分:点球大战比分 (7:6)*/
    private String homeTeamScore;
    /** 客队全场得分:点球大战比分 (6:5)*/
    private String awayTeamScore;
    /** 主队得分(常规赛比分) */
    private String homeTeamScoreD01;
    /** 客队得分(常规赛比分) */
    private String awayTeamScoreD01;
    /** 0:自动 1:手动 */
    private Integer editStatus;

    /** 初盘让球盘口值*/
    private String handicapVal;
    /** 初盘大小盘口值*/
    private String overUnderVal;

    /** 初盘胜平负投注项值*/
    @Deprecated
    private String winnerOdds;
    /** 初盘让球投注项值*/
    @Deprecated
    private String handicapOdds;
    /** 初盘大小投注项值*/
    @Deprecated
    private String overUnderOdds;
    /** 轮次中文名；示例：组A*/
    private String round;
    /** 轮次类型中文名；示例：分组赛*/
    private String roundType;
    /** 天气*/
    private String weatherDesc;
    /**场馆位置**/
    private String googleMapsCoordinates;
    /**场馆名称**/
    private String stadiumNames;

    /** 坑位排序*/
    private String orderNo;
}
