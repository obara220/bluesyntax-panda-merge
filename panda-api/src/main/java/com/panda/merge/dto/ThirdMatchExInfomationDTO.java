package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 比赛情报综合资讯
 * @author     tell
 * @since      2021年4月23日12:30:01
 */
@Data
public class ThirdMatchExInfomationDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @NotNull(message = "数据源赛事ID不能为null!")
    private String thirdMatchSourceId;

    @NotNull(message = "运动类型不能为null!")
    private Long sportId;

    @NotNull(message = "数据来源不能为null!")
    private String dataSourceCode;

    /**
     * 主队教练信息
     * */
    @Valid
    private ThirdMatchCoachDTO homeCoach;

    /**
     * 客队队教练信息
     * */
    @Valid
    private ThirdMatchCoachDTO awayCoach;

    /**
     * 新闻情报信息
     * */
    @Valid
    private List<ThirdMatchInforMatinsDTO> inforMatinsList;

    /**
     * 指数情报（赔率情况分析）
     * */
    @Valid
    private ThirdMatchWinningOddsDTO winningOdds;

}
