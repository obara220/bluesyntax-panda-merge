package com.panda.merge.v2.service;

import com.panda.merge.dto.settle.MatchSettleScoreSearchDto;
import com.panda.merge.dto.settle.ThirdMatchSettleScoresDto;

public interface IMatchSettleThirdScoreService {
    ThirdMatchSettleScoresDto searchThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto);
}
