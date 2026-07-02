package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class PDBasketBallScoreDto implements Serializable {
    /**
     * 比分类型
     * */
    private String eventCode;
    /**
     *  KEY 阶段比分 根据赛制划分  40 16 15 14 13 -1   40 2 1 -1   40  1  -1
     *  VALUE 主客队的某一边的比分
     * */
    private Map<String,Integer> periodScore;

    public PDBasketBallScoreDto(){}
    public PDBasketBallScoreDto(String eventCode){
        this.eventCode=eventCode;
    }
}
