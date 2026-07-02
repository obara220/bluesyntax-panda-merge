package com.panda.merge.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 三方联赛球队历史表现数据
 * @author    tell
 * @since     2020年10月20日09:53:20
 * */
@Data
public class ThirdMatchHistoryExpressionBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 标准联赛ID*/
    private Long standardTournamentId;
    /** 标准联赛nameCode*/
    private Long standardTournamentNameCode;

    /** 标准球队ID*/
    private Long standardTeamId;
    /** 球队logo*/
    private String teamLogo;
    /** 球队 logo缩略图的url地址*/
    private String teamLogoUrlThumb;
    /** 球队多语言*/
    private List<I18nItemBO> teamNameIl8nList;


   /** 三方数据源联赛ID+三方数据源球队ID+数据来源+数据类型+运动类型*/
    private String id;

   /** 三方数据源联赛ID*/
    private String thirdTournamentSourceId;

   /** 三方数据源球队ID*/
    private String thirdTeamSourceId;

   /** 数据来源*/
    private String dataSourceCode;

   /** 球队中文名称*/
    private String teamCnName;

   /** 球队英文名称*/
    private String teamEnName;

   /** 0:自动1:手动*/
    private Integer editStatus;

   /** 运动类型*/
    private Long sportId;

   /** 联赛表现排名:如10/20*/
    private String expressionRanking;

   /** 数据类型,0:总体1:主队2:客队*/
    private Integer expressingType;

   /** 最近第1场赛事状态,0:赢1:平2:输*/
    private Integer firstStatus;

   /** 最近第2场赛事状态,0:赢1:平2:输*/
    private Integer secondStatus;

   /** 最近第3场赛事状态,0:赢1:平2:输*/
    private Integer thirdStatus;

   /** 最近第4场赛事状态,0:赢1:平2:输*/
    private Integer fourthStatus;

   /** 最近第5场赛事状态,0:赢1:平2:输*/
    private Integer fifthStatus;

   /** 最近5场进球数*/
    private Integer goalsForTotal;

   /** 最近5场均进球数*/
    private BigDecimal averageGoal;

   /** 最近5场赢球占比*/
    private BigDecimal winPercent;

   /** 两队都得分占比*/
    private BigDecimal bothGoalPercent;

   /** 没有失球占比*/
    private BigDecimal notLostPercent;

   /** 第一队入球占比*/
    private BigDecimal firstGoalPercent;

   /** 平均进球占比*/
    private BigDecimal averageGoalPercent;

   /** 得分占比*/
    private BigDecimal goalPercent;

   /** 失球占比*/
    private BigDecimal lostGoalPercent;

   /** xG*/
    private BigDecimal goalXg;

   /** xGA*/
    private BigDecimal goalXga;

   /** 修改时间*/
    private Long modifyTime;

   /** 创建时间*/
    private Long createTime;

}