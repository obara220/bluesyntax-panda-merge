package com.panda.merge.advertise.dto;


import com.panda.merge.model.MatchScoresInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchScoreCommonVo implements Serializable {
    private Integer T1;
    private Integer T2;
    private Integer periodT1;
    private Integer periodT2;
    private String homeAway;
    private Integer addT1;
    private Integer addT2;
    private MatchScoresInfo matchScoresInfo;
}
