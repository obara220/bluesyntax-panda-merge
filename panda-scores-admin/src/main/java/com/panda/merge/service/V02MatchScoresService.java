package com.panda.merge.service;

import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.ThirdMatchInfo;

/**
 * BT赛事比分中心服务
 */
public interface V02MatchScoresService {


    void processVideoScore(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchEventInfo event);
}
