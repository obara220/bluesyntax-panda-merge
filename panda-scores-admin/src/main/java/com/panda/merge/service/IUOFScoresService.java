package com.panda.merge.service;

import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;

public interface IUOFScoresService {
    MatchScoresInfo checkScores(ThirdMatchInfo thirdMatchInfo, MatchStatisticsInfoDTO data,MatchScoresInfo matchScoresInfo ) throws Exception;
    void processUofScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data, StandardMatchInfo standardMatchInfo) throws Exception;
}
