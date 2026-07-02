package com.panda.merge.service;

import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchScoresInfo;

/**
 * BT赛事比分中心服务
 */
public interface BTMatchScoresService {

    /**
     * 更新BT比分
     * @param matchScoresInfo
     * @param request
     * @return
     */
    boolean updateScores(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request);
}
