package com.panda.merge.advertise.dto;

import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchScoreAndTimeVo implements Serializable {
    private MatchScoresInfo matchScoresInfo;
    private MatchTimeInfo matchTimeInfo;
    private ThirdMatchInfo thirdMatchInfo;
    private StandardMatchInfo standardMatchInfo;
}
