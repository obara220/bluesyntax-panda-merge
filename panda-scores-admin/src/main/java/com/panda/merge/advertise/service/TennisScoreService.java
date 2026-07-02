package com.panda.merge.advertise.service;


import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.dto.advertise.TennisEditSecondScoreDto;
import com.panda.merge.model.MatchScoresInfo;

public interface TennisScoreService {
    /**
     * 查询当前赛事的总比分以及阶段比分
     * */
    MatchScoreCommonVo searchCommonMatchScore(MatchScoresInfo matchScoresInfo, Long periodId);


    MatchScoreCommonVo countScore(MatchScoreAndTimeVo data, TennisEditSecondScoreDto editSecondScoreDto);

}
