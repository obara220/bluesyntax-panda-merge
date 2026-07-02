package com.panda.merge.advertise.dto;

import com.panda.merge.dto.TennisExtryScores;
import com.panda.merge.dto.TennisScores;
import lombok.Data;

import java.util.Map;

@Data
public class TennisInitScoreVo {
    private Map<Long, TennisScores> allPeriodScores;
    TennisExtryScores tennisExtryScores;
}
