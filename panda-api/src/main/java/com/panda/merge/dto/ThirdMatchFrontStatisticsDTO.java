package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author aldrich
 * @since 2024/10/16
 */
@Data
public class ThirdMatchFrontStatisticsDTO implements Serializable {
    private static final long serialVersionUID = 4740891136954491312L;

    @NotNull(message = "数据来源不能为空")
    private String dataSourceCode;

    @NotNull(message = "数据源赛事id不能为空")
    private String thirdMatchSourceId;

    @NotNull(message = "数据源主队ID不能为空")
    private String homeTeamId;

    @NotNull(message = "数据源客队ID不能为空")
    private String awayTeamId;

    //主队名称
    private String homeTeamName;

    //客队名称
    private String awayTeamName;

    @NotNull(message = "运动类型不能为空")
    private Long sportId;

    //0:自动1:手动
    private Integer editStatus;

    @NotNull(message = "总场数不能为空")
    private Integer countTotal;

    @NotNull(message = "主队赢场数不能为空")
    private Integer homeWin;

    @NotNull(message = "客队赢场数不能为空")
    private Integer awayWin;

    @NotNull(message = "和局场数不能为空")
    private Integer dogfallTotal;

    @NotNull(message = "高于1.5场数不能为空")
    private Integer moreThanOne;

    @NotNull(message = "高于2.5场数不能为空")
    private Integer moreThanTwo;

    @NotNull(message = "高于3.5场数不能为空")
    private Integer moreThanThree;

    @NotNull(message = "两队都得分场数不能为空")
    private Integer allScores;

    @NotNull(message = "主队没有失球场数不能为空")
    private Integer homeNotLost;

    @NotNull(message = "客队没有失球场数不能为空")
    private Integer awayNotLost;

}
