package com.panda.merge.service;


import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.ThirdMatchInfo;


/**
 * 比分中心服务
 */
public interface IScoresService {



    /**
     * 主客队比分调换
     * @param standardScore
     */
    void changeHomeAway(MatchScoresBetterDto standardScore);
    /**
     * 主客队比分调换
     * @param standardScore
     */
    void changeHomeAway(MatchScoresInfo standardScore);

    boolean  isLivedataStoped(Long thirdMatchId);

    Long checkStandardScore(Long standardId);

}
