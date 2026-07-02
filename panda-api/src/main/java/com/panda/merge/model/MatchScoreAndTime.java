package com.panda.merge.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author warren
 * @since 2024/11/08 15:26:06
 */
@Data
public class MatchScoreAndTime implements Serializable {
    private MatchScoresInfo matchScoresInfo;
    private MatchTimeInfo matchTimeInfo;
    private ThirdMatchInfo thirdMatchInfo;
    private StandardMatchInfo standardMatchInfo;
}
