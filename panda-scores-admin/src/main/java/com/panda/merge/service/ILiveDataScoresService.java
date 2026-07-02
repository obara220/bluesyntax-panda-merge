package com.panda.merge.service;

import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchScores;

/**
 * livedata事件比分中心服务
 */
public interface ILiveDataScoresService {

    /**
     * 计算比分 matchScoresInfo
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    void calculation(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception;
    /**
     * 计算标准比分 standardMatchScores
     * @param matchScoresInfo
     * @param standardMatchScores
     * @throws Exception
     */
    void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores standardMatchScores,MatchEventInfo data) throws Exception;
    /**
     * 事件取消
     * @param matchScoresInfo
     * @param data
     * @param isReissue
     * @throws Exception
     */
    void cancelEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data,Boolean isReissue) throws Exception;

    /**
     * 进球取消
     * @param matchScoresInfo
     * @param data
     */
    void canceledGoal(MatchScoresInfo matchScoresInfo, MatchEventInfo data);
}
