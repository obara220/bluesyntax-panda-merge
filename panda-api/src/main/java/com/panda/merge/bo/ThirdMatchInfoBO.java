package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 三方赛事信息BO
 * @author tell
 * @since  2021年1月14日13:44:05
 */
@Data
public class ThirdMatchInfoBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 三方赛事ID*/
    private Long id;
    /** 运动种类id*/
    private Long sportId;
    /** 三方数据源ID*/
    private String thirdMatchSourceId;
    /** 标准赛事的id*/
    private Long referenceId;
    /** 父赛事id*/
    private Long parentId;
    /** 比赛开始时间. UTC时间*/
    private Long beginTime;
    /** 数据来源编码.*/
    private String dataSourceCode;
    /** 赛事状态*/
    private Integer matchStatus;
    /** 赛事阶段*/
    private String matchPeriod;
    /** 三方联赛ID*/
    private Long tournamentId;
    /** 联赛中文名称*/
    private String tournamentName;
    /** 联赛英文名称*/
    private String tournamentNameEn;
    /** 运动区域 id. 对应  sport_region.id*/
    private Long regionId;
    /** 距离开赛时间.  单位: 秒*/
    private Integer secondsMatchStart;

    /** 对阵信息(主场队名称 VS 客场队名称)*/
    private String homeAwayInfo;
    /** 赛事包含的所有球队多语言信息,json串,冗余字段,用于赛程页面查询*/
    private String teamName;
    /** 主队信息*/
    private ThirdSportTeamBO homeSportTeam;
    /** 客队信息*/
    private ThirdSportTeamBO awaySportTeam;
    /** 赛事对应的球队list*/
    private List<ThirdSportTeamBO> sportTeamList;

    /** 是否支持事件(0:否,1:是)*/
    private Integer eventSupport;

    /** 主客队是否相反(0:否,1:是)*/
    private Integer homeAwayOpposite;
    /** 是否商业数据源(0:否,1:是) （目前是支持赔率的则为商业数据源）*/
    private Integer commerce;
    /** 是否激活(0:否,1:是)*/
    private Integer active;
    /** 是否可见(0:否,1:是)*/
    private Integer visible;
    /** 是否中立场(0:否,1:是)*/
    private Integer neutralGround;
    /** 是否预定(0:否,1:是)*/
    private Integer booked;
    /** 是否支持滚球(0:否,1:是)*/
    private Integer liveOddSupport;
    /** 赛前盘是否可下注(0:否,1:是)*/
    private Integer preMatchBetStatus;
    /** 赛前盘是否可下注(0:否,1:是)*/
    private Integer liveOddsBetStatus;
    /** 赛事可下注状态（0: betstart; 1: betstop）*/
    private Integer betStatus;

    /** 比赛场地名称,仅限中文.用于查看mysql 时 使用.*/
    private String matchPositionName;
    /** 比赛场地的国际化编码.*/
    private Long matchPositionNameCode;
    /** 比赛场地国际化列表*/
    private List<I18nItemBO> il8nMatchPositionList;

    /** 数据源赛季id*/
    private String seasonId;

    /** 主队阵型(TS)*/
    @Deprecated
    private String homeFormation;
    /** 客队阵型(TS)*/
    @Deprecated
    private String awayFormation;
    /** 首发阵容*/
    @Deprecated
    private List<ThirdMatchLineupBO> lineupList;

    /**主客队球员名称(主名称,客名称)*/
    private String homeAwayPlayerName;

    /** 备注.*/
    private String remark;
    /** 创建时间.*/
    private Long createTime;
    /** 修改时间.*/
    private Long modifyTime;
    /**
     * 比赛是否结束. 0: 未结束;  1: 结束. （比赛彻底结束, 双方不再有加时赛, 点球大战, 且裁判宣布结束）
     */
    private Integer matchOver;
}
