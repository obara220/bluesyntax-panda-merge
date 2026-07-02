package com.panda.merge.advertise.service;

import com.panda.merge.model.MatchScoresInfo;

/**
 * @author Kepa
 * 通用比分事件计算类
 */
public interface CommonScoreEventService {

    /**
     * 查询当前点球大战阶段的实时比分
     * @param linkId
     * @param matchScoresInfo
     * @param homeAway
     * @return
     */
    Integer getCurrentPenaltyScore(String linkId, MatchScoresInfo matchScoresInfo, String homeAway);


    /**
     * 根据三方源id查询对应的点球大战实时比分
     * @param linkId
     * @param dataSourceCode
     * @param thirdMatchSourceId
     * @param homeAway
     * @return
     */
    Integer getCurrentPenaltyScore(String linkId, String dataSourceCode, String thirdMatchSourceId, String homeAway);
}
