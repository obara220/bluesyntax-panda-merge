package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleBasketballDataSourceAllWeightV2Dto extends AbstructMatchSettleV2Dto implements Serializable {

    /**
     * 联赛的等级0-20，-1为全部联赛开关
     */
    private Integer tournamentLevel;

    /**
     * 数据源名称BC、BG
     */
    private String dataSourceCode;

    /**
     * 进球权重
     */
    private Integer goalWeight;

    /**
     * 灰色区间权重
     */
    private Integer grayWeight;

    /**
     * 篮球进球15分钟灰色区间
     */
    private Integer goal6Min;
    /**
     * 权重上限
     */
    private Integer weightNum;


}
