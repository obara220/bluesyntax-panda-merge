package com.panda.merge.dto.scores;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 标准比分中心页面-足球15分钟比分
 */
@Slf4j
@Data
public class StandardScoresDetailDTO implements Serializable{
    private Long periodId;
    private Integer homeGoal;
    private Integer awayGoal;
    private int goalSwitch;
    private int goalIndex;

    private Integer homeCorner;
    private Integer awayCorner;
    private int cornerSwitch;
    private int cornerIndex;

    private Integer homeYellowCard;
    private Integer awayYellowCard;
    private int yellowSwitch;
    private int yellowIndex;

    private Integer homeRedCard;
    private Integer awayRedCard;
    private int redSwitch;
    private int redIndex;
}
