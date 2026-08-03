package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 三方赛事历史统计信息
 * @author  tell
 * @since   2021年2月9日15:52:11
 */
@Data
//@ToString(exclude = {"tournamentIl8nList","homeTeamNameIl8nList","awayTeamNameIl8nList"})
public class ThirdMatchHistoryStatisticsBO implements Serializable{

	private static final long serialVersionUID = 1L;

    /** 数据源编码ID+数据源赛事id */
    private String id;

    /** 数据源赛事id */
    private String thirdMatchSourceId;

    /** 三方赛事id */
    private Long thirdMatchId;

    /**标准赛事id */
    private Long standardMatchId;

    /** 数据源联赛id */
    private String thirdTournamentSourceId;

    /** 标准联赛id */
    private Long standardTournamentId;

    /** 联赛多语言列表（存在标准联赛则为标准联赛多语言，否则为三方联赛多语言） */
    private List<I18nItemBO> tournamentIl8nList;

    /** 数据源赛季id */
    private String thirdSeasonSourceId;

    /** 运动类型 */
    private Long sportId;

    /** 数据来源 */
    private String dataSourceCode;

    /** 开赛时间 */
    private Long beginTime;

    /** 赛事状态 */
    private String matchStatus;

    /** 联赛类别(0:其他,1联赛,2杯赛)*/
    private Integer tournamentType;

    /** 是否分组赛（0：否，1：是）*/
    private Integer matchGroup;
    /** 分组id 对应分组信息*/
    private String groupId;

    /** 数据源主队ID */
    private String homeTeamId;

    /** 数据源客队ID */
    private String awayTeamId;

    /** 主队名称 */
    private String homeTeamName;

    /** 客队名称 */
    private String awayTeamName;

    /** 标准主队ID */
    private Long standardHomeTeamId;

    /** 标准客队ID */
    private Long standardAwayTeamId;

    /** 主队多语言 */
    private List<I18nItemBO> homeTeamNameIl8nList;

    /** 客队多语言 */
    private List<I18nItemBO> awayTeamNameIl8nList;

    /** 主队得分(全场) */
    private String homeTeamScore;

    /** 主队得分(全场) */
    private String awayTeamScore;

    /** 初盘让球盘口值 */
    private String handicapVal;

    /** 初盘大小盘口值 */
    private String overUnderVal;

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

    /** 初盘胜平负投注项值 */
    @Deprecated
    private String winnerOdds;

    /** 初盘让球投注项值 */
    @Deprecated
    private String handicapOdds;

    /** 初盘大小投注项值 */
    @Deprecated
    private String overUnderOdds;

    /** 修改时间 */
    private Long modifyTime;

    /** 创建时间 */
    private Long createTime;


    /** 主队得分(点球大战) */
    private String homeTeamPtScore;

    /** 客队得分(点球大战) */
    private String awayTeamPtScore;

    /** 坑位排序（目前是方便欧洲杯对阵图排序）*/
    private String orderNo;

    /** 3071需求*/
    /** 主队得分(常规赛比分) */
    private String homeTeamScoreD01;

    /** 客队得分(常规赛比分) */
    private String awayTeamScoreD01;

    /** 球队主队logo */
    private String teamHomeLogo;

    /** 球队主队 logo缩略图的url地址 */
    private String teamHomeLogoUrlThumb;

    /** 球队客队logo */
    private String teamAwayLogo;

    /** 球队客队 logo缩略图的url地址 */
    private String teamAwayLogoUrlThumb;
}
