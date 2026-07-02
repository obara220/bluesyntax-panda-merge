package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 联赛下球队积分排行榜(泰森独有)
 * @author   tell
 * @since    2020年10月17日19:20:31
 * */
@Data
public class ThirdSportTeamRankingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "三方数据源联赛ID不能为null!")
    private String thirdTournamentSourceId;
    /** 联赛类别(0:其他,1联赛,2杯赛)*/
    private Integer tournamentType;

    @NotNull(message = "三方数据源联赛运动类型不能为null!")
    private Long sportId;

    /** 数据来源*/
    private String dataSourceCode;

    @NotNull(message = "三方数据源赛季ID不能为null!")
    private String thirdSourceSeasonId;
    private String thirdSourceSeasonName;
    /** 三方数据源赛季开始时间*/
    private Long thirdSourceSeasonBeginTime;
    /** 三方数据源赛季结束时间*/
    private Long thirdSourceSeasonEndTime;

    @NotNull(message = "榜单ID不能为null!")
    private String rankingId;
    /** 榜单中文名称*/
    private String rankingCnName;
    /** 榜单英文名称*/
    private String rankingEnName;


    @NotNull(message = "参赛场数不能为null!")
    private Integer matchCount;

    @NotNull(message = "三方数据源球队ID不能为null!")
    private String thirdTeamSourceId;
    /** 球队中文名称*/
    private String teamCnName;
    /** 球队英文名称*/
    private String teamEnName;
    /** 球队logo*/
    private String teamLogo;

    /** 组ID*/
    private String groupId;
     /** 组名称*/
    private String groupCnName;



    @NotNull(message = "胜场数不能为null!")
    private Integer winTotal;
    @NotNull(message = "平局场数不能为null!")
    private Integer drawTotal;
    @NotNull(message = "负场数不能为null!")
    private Integer lossTotal;
    @NotNull(message = "积分数不能为null!")
    private Integer pointsTotal;
    @NotNull(message = "进球数不能为null!")
    private Integer goalsForTotal;
    @NotNull(message = "失球数不能为null!")
    private Integer goalsAgainstTotal;
    @NotNull(message = "净胜球数不能为null!")
    private Integer goalDiffTotal;
    @NotNull(message = "排名值不能为null!")
    private Integer positionTotal;

    /** 球队最近5场战绩 JOSN字符串（废弃，不需要数据商数据，改为赛程服务维护）
     * 字段示例：
     * [{“id”:"赛事ID"，“winner”:“胜平负（WDL）”},...]
     * */
    @Deprecated
    private String record5;

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

    /** 教练名称*/
    private String coachName;
    /** 教练logo*/
    private String coachLogo;

    /** 是否失效(0:否,1:是)*/
    private Integer invalid;

    /** 赛事总数 */
    private Long totalMatches;

    /** 已完成赛事数 */
    private Long matchesCompleted;

    /** 需求4024 【赛程管理】足篮综合开赛时间变动提示 #3 */
    /** 晋级中文名*/
    private String promotionCnName;
    /** 晋级英文名*/
    private String promotionEnName;
    /** 晋级id*/
    private String promotionId;

    /** 主场场次 */
    private Integer homeMatchesTotal;
    /** 客场场次 */
    private Integer awayMatchesTotal;
    /** 篮球近10场胜 */
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