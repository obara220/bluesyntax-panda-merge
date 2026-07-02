package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 联赛下球员排行榜(泰森独有)
 * @author   tell
 * @since    2020年10月17日19:20:31
 * */
@Data
public class ThirdSportPlayerRankingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "三方数据源联赛ID不能为null!")
    private String thirdTournamentSourceId;
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

    /** 榜单类型对应 PlayerRankingTypeEnum 类*/
    @NotNull(message = "榜单类型不能为null!")
    private Integer rankingType;

    @NotNull(message = "三方数据源球队ID不能为null!")
    private String thirdTeamSourceId;
    /** 球队中文名称*/
    private String teamCnName;
    /** 球队英文名称*/
    private String teamEnName;
    /** 球队logo*/
    private String teamLogo;
    @NotNull(message = "三方数据源球员ID不能为null!")
    private String thirdPlayerSourceId;
    /** 球员中文名称*/
    private String playerCnName;
    /** 球员英文名称*/
    private String playerEnName;
    /** 球员logo*/
    private String playerLogo;

    /**
     * 排序规则：根据 rankingValue，rankingValue/matchCount 降序
     * */
    @NotNull(message = "参赛场数不能为null!")
    private Integer matchCount;
    @NotNull(message = "榜单值不能为null!")
    private Integer rankingValue;

}