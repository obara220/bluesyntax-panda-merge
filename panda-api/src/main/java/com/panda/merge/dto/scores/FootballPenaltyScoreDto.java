package com.panda.merge.dto.scores;


import com.panda.merge.cache.CommonItem;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 点球大战比分主要数据
 */
@Slf4j
@Data
public class FootballPenaltyScoreDto implements Serializable{

    private Integer firstNum;
    private Integer pointNum;
    private CommonItem round5Scores;
    private List<Map> roundScores;
    private String shootFirst;

}
