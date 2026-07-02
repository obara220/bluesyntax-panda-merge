package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 当前赛季统计信息DTO
 *
 * @author aldrich
 * @since 2024/10/14
 */
@Data
public class ThirdMatchSeasonStatisticsDTO implements Serializable {

    private static final long serialVersionUID = -1477381309758772204L;

    @NotNull(message = "数据源编码不能为空")
    private String dataSourceCode;

    @NotNull(message = "三方数据源赛季ID不能为空")
    private String thirdSourceSeasonId;

    @NotNull(message = "三方数据源联赛ID不能为空")
    private String thirdTournamentSourceId;

    //三方数据源赛季名称
    private String thirdSourceSeasonName;

    //联赛类别,0:其他,1:联赛,2:杯赛
    @NotNull(message = "联赛类别不能为空")
    private Integer tournamentType;

    //数据同步类型,0:自动 1:手动
    private Integer editStatus;

    @NotNull(message = "运动类型不能为空")
    private Long sportId;

    @NotNull(message = "高于1.5占比不能为空")
    private BigDecimal percentThanOne;

    @NotNull(message = "高于2.5占比不能为空")
    private BigDecimal percentThanTwo;

    @NotNull(message = "高于3.5占比不能为空")
    private BigDecimal percentThanThree;

    @NotNull(message = "均场入球不能为空")
    private BigDecimal averageGoal;

    @NotNull(message = "罚牌不能为空")
    private BigDecimal averageCard;

    @NotNull(message = "角球不能为空")
    private BigDecimal averageCorner;
}
