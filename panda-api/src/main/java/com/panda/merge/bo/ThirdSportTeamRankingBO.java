package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 联赛下球队榜单排行榜信息（泰森独有）
 * @author    tell
 * @since     2020年10月20日09:53:20
 * */
@Data
public class ThirdSportTeamRankingBO implements Serializable {
    /** 三方数据源赛季ID+运动类型+球队ID*/
    private String id;

    /** 标准联赛ID*/
    private Long standardTournamentId;
    /** 标准联赛nameCode*/
    private Long standardTournamentNameCode;

    /** 三方数据源联赛ID*/
    private String thirdTournamentSourceId;

    /** 联赛类别(0:其他,1联赛,2杯赛)*/
    private Integer tournamentType;

    /** 运动类型*/
    private Long sportId;

    /** 三方数据源赛季ID*/
    private String thirdSourceSeasonId;

    /** 三方数据源赛季名称*/
    private String thirdSourceSeasonName;

    /** 三方数据源赛季开始时间*/
    private Date thirdSourceSeasonBeginTime;

    /** 三方数据源赛季结束始时间*/
    private Date thirdSourceSeasonEndTime;

    /** 榜单ID(类似于该赛季下榜单类型)*/
    private String rankingId;
    /** 榜单中文名称*/
    private String rankingCnName;
    /** 榜单英文名称*/
    private String rankingEnName;

    /** 组ID*/
    private String groupId;
    /** 组名称*/
    private String groupCnName;

    /** 标准球队ID*/
    private Long standardTeamId;
    /** 三方数据源球队ID*/
    private String thirdTeamSourceId;
    /** 球队logo*/
    private String teamLogo;
    /** 球队 logo缩略图的url地址*/
    private String teamLogoUrlThumb;
    /** 球队多语言*/
    private List<I18nItemBO> teamNameIl8nList;

    /** 参数场数*/
    private Integer matchCount;
    /** 排名值*/
    private Integer positionTotal;
    /** 胜场数*/
    private Integer winTotal;
    /** 平局数*/
    private Integer drawTotal;
    /** 负场数*/
    private Integer lossTotal;
    /** 积分数*/
    private Integer pointsTotal;
    /** 进球数*/
    private Integer goalsForTotal;
    /** 失球数*/
    private Integer goalsAgainstTotal;
    /** 净胜球数*/
    private Integer goalDiffTotal;

    /** 修改时间*/
    private Long modifyTime;

    /** 创建时间*/
    private Long createTime;

    private static final long serialVersionUID = 1L;

    /** 0:自动1:手动*/
    private Boolean editStatus;

    /**
     * 球队最近5场战绩 D-W-L-N-N
     */
    private String record5;

    /**===========以下是世界杯专属===========*/

    /** 榜单明星球员（含球员多语言） JOSN字符串
     * 字段示例：
     * [{"player_id":"数据源球员ID","player_logo":"数据源球员logo","zs":"中文名称","en":"英文名称"} ,...]
     * */
    private String starPlayers;

    /** 球队名称多语言 JOSN字符串
     * 字段示例：
     *{"zs":"中文简体","zh":"中文繁体","en":"英文","team_badge":"队伍logo"}
     * */
    private String teamNames;

    /** 组名称多语言 JOSN字符串
     * 字段示例：
     *{"zs":"中文简体","zh":"中文繁体","en":"英文"}
     * */
    private String groupNames;

    /** 教练信息（含名称多语言） JOSN字符串
     * 字段示例：
     * {"coach_logo":"教练logo","zs":"中文名称","en":"英文名称""}
     * */
    private String coachInfo;

    /** 小组赛冠军赛事盘口投注id*/
    private String winnerMarketOddsid;

    /** 小组赛进度赛事盘口投注id*/
    private String advanceMarketOddsid;

    /** 3071需求*/
    /** 数据来源*/
    private String dataSourceCode;


    /**
     * 自建赛事主客队是否包含在联赛球队榜单中（0:否  1:是）
     * */
    private Integer status;

    /** 是否失效(0:否,1:是)*/
    private Integer invalid;

    /** 赛事总数 */
    private Long totalMatches;

    /** 已完成赛事数 */
    private Long matchesCompleted;

    /** 球队中文名称 */
    private String teamCnName;

    /** 球队英文名称 */
    private String teamEnName;


    /** 需求4024 【赛程管理】足篮综合开赛时间变动提示 #3 */
    /** 晋级中文名*/
    private String promotionCnName;
    /** 晋级英文名*/
    private String promotionEnName;
    /** 晋级id*/
    private String promotionId;


    /** 需求4299 比分网榜单积分下发 **/

    /** 标准赛季ID **/
    private Long seasonId;

    /** 赛季展示开关 0-开 1-关 **/
    private Integer isDisplay;

    /** 主场场次 */
    private Integer homeMatchesTotal;

    /** 客场场次 */
    private Integer awayMatchesTotal;

    /** * 篮球近10场胜 */
    private Integer winLast10;

    /** 篮球近10场负 */
    private Integer lossLast10;

    /** 连续战绩: +连胜,-连败 */
    private Integer streak;

    /** 胜率 */
    private String winPctTotal;

    /** 胜场差 */
    private String gameBehind;

    /** 是否当前赛季 0:否,1:是 */
    private Integer isCurrentSeason;
}