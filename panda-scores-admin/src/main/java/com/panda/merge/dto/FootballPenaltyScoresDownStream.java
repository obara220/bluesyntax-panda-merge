package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class FootballPenaltyScoresDownStream implements Serializable {

    /**
     * 当前点球大战局数
     * */
    private Integer firstNum;
    /**
     * 主客队射门次数
     * */
    private Integer pointNum;
    /**
     *每局比分
     **/
    private Map<String,CommonItem> roundScores;
    /**
     * 前五轮比分
     * */
    private CommonItem round5Scores;
    /**
     * 谁先射门
     * */
    private String shootFirst;
}
