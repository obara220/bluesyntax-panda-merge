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
public class ThirdMatchLineupBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 数据源ID:数据赛事源ID:球队源ID:球员源ID*/
    private String id;
    /** 三方赛事id */
    private Long thirdMatchId;
    /**标准赛事id */
    private Long standardMatchId;
    /** 数据源赛事id */
    private String thirdMatchSourceId;
    /** 运动类型*/
    private Long sportId;
    /** 数据来源*/
    private String dataSourceCode;
    /** 球队源ID*/
    private String thirdTeamSourceId;
    /** 球员源ID*/
    private String thirdPlayerSourceId;
    /** 球员中文名称*/
    private String thirdPlayerName;
    /** 球员英文名称*/
    private String thirdPlayerEnName;
    /** 球员头像*/
    private String thirdPlayerPicUrl;

    /** 主客队标识(1主队,2客队)*/
    private Integer homeAway;
    /** 球员综合评分*/
    private String overallRatings;
    /** 球员位置*/
    private Integer position;
    /** 球员位置名称(门将 Goalkeeper，后卫 Defenders，中场 Midfielders，前锋 Forwards)*/
    private String positionName;
    /** 球员位置英文名称(门将 Goalkeeper，后卫 Defenders，中场 Midfielders，前锋 Forwards )*/
    private String positionEnName;

    /** 球员位置多语言*/
    private List<I18nItemBO> positionNameList;

    /** 球衣号码*/
    private Integer shirtNumber;
    /** 是否替补(0否,1是)*/
    private Integer substitute;
    /** 是否失效(0:否,1:是)*/
    private Integer invalid;

    /** 主队阵型(TS)*/
    private String homeFormation;
    /** 客队阵型(TS)*/
    private String awayFormation;

    /** 创建时间.*/
    private Long createTime;
    /** 修改时间.*/
    private Long modifyTime;

    // =================需求4072===================
    /** 上场时间（分钟）*/
    private String playTime;

    /** 助攻*/
    private String assist;

    /** 篮板*/
    private String rebound;

    /** 得分*/
    private String point;

}
