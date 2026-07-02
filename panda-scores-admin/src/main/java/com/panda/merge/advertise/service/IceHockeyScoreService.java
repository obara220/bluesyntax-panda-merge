package com.panda.merge.advertise.service;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.model.MatchScoresInfo;

public interface IceHockeyScoreService {

    /**
     * 查询当前赛事的总比分以及阶段比分
     * */
    MatchScoreCommonVo searchCommonMatchScore(MatchScoresInfo matchScoresInfo, Long periodId);

    /**
     *
     * @param linkId
     * @param matchScoresInfo
     * @param changeMatchScoreDto
     * @return
     */
    boolean checkScoreChangeDelete(String linkId, MatchScoresInfo matchScoresInfo, ChangeMatchScoreDto changeMatchScoreDto);

    /**
     * 计算总比分
     * @param linkId
     * @param data
     * @param changeMatchScoreDto
     * @return
     */
    MatchScoreCommonVo countScore(String linkId, MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto);

    /**
     * 修改比分
     * @param data
     * @param matchScoreCommonVo
     * @param period
     * @param linkId
     */
    void updateScore(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, Long period, String linkId);

}
