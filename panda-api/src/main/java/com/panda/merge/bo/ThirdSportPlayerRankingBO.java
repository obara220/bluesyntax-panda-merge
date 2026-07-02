package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 三方联赛球员榜单数据同步参数类
 * @author    tell
 * @since     2020年10月20日09:53:20
 * */
@Data
public class ThirdSportPlayerRankingBO implements Serializable {
    /** ID(三方数据源赛季ID+榜单类型+球员ID)*/
    private String id;

    /** 标准联赛ID*/
    private Long standardTournamentId;

    /** 三方数据源联赛ID*/
    private String thirdTournamentSourceId;

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

    /**
     * 排序规则：根据 rankingValue，rankingValue/matchCount 降序
     * */
    /** 参数场数*/
    private Integer matchCount;

    /** 榜单值*/
    private Integer rankingValue;

    /** 榜单序号(废弃)*/
    @Deprecated
    private Integer rankingSort;

    /** 榜单类型 参考 PlayerRankingTypeEnum类*/
    private Integer rankingType;

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

    /** 三方数据源球员ID*/
    private String thirdPlayerSourceId;

    /** 球员中文名称*/
    private String playerCnName;

    /** 球员英文名称*/
    private String playerEnName;

    /** 球员logo*/
    private String playerLogo;

    /** 修改时间*/
    private Long modifyTime;

    /** 创建时间*/
    private Long createTime;

    private static final long serialVersionUID = 1L;

}