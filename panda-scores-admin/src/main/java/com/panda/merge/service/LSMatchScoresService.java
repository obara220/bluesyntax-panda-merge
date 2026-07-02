package com.panda.merge.service;

import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchScoresInfo;

public interface LSMatchScoresService {

  /**
   * LS更新比分
   * @param matchScoresInfo
   * @param request
   */
  void   updateScores(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request);
}
