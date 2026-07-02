package com.panda.merge.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 三方联赛赛季统计数据
 * @author    tell
 * @since     2020年10月20日09:53:20
 * */
@Data
public class ThirdMatchSeasonStatisticsBO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 标准联赛ID*/
    private Long standardTournamentId;
    /** 标准联赛nameCode*/
    private Long standardTournamentNameCode;


    /** value = "三方数据源赛季ID+数据来源+运动类型*/
    private String id;

    /** value = "数据来源*/
    private String dataSourceCode;

    /** 三方数据源联赛ID*/
    private String thirdTournamentSourceId;

    /** value = "三方数据源赛季ID*/
    private String thirdSourceSeasonId;

    /** value = "三方数据源赛季名称*/
    private String thirdSourceSeasonName;

    /** value = "联赛类别(0:其他,1联赛,2杯赛)*/
    private Integer tournamentType;

    /** value = "0:自动1:手动*/
    private Integer editStatus;

    /** value = "运动类型*/
    private Long sportId;

    /** value = "高于1.5占比*/
    private BigDecimal percentThanOne;

    /** value = "高于2.5占比*/
    private BigDecimal percentThanTwo;

    /** value = "两队都得分*/
    private BigDecimal percentThanThree;

    /** value = "均场入球*/
    private BigDecimal averageGoal;

    /** value = "罚牌*/
    private BigDecimal averageCard;

    /** value = "角球*/
    private BigDecimal averageCorner;

    /** value = "修改时间*/
    private Long modifyTime;

    /** value = "创建时间*/
    private Long createTime;

}