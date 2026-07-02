package com.panda.merge.calculation;

import com.panda.merge.dto.*;
import com.panda.merge.dto.resultScore.MatchResultScoreMsgVo;
import com.panda.merge.dto.scores.StandardMatchSwitchDTO;
import com.panda.merge.dto.scores.StandardScoreCenter;
import com.panda.merge.dto.scores.StandardScoreCenterDTO;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardMatchScores;

import java.util.Map;


/**
 * 比分计算服务接口
 * */
public interface CalculationService {

    /**
     *  总比分计算
     * @param matchScoresInfo
     * @param data*/
    void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception;

    void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores standardMatchScores, MatchEventInfo data) throws Exception;

    /**
     * 事件取消
     * @param matchScoresInfo
     * @param data
     * @param isAgain
     * @param isReissue
     * @throws Exception
     */
    void cancelEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data, boolean isAgain,Boolean isReissue) throws Exception;

    /**
     * 取消事件逻辑
     * @param data
     * @return
     */
    MatchEventInfo getOldMatchInfoByCancel(MatchEventInfo data);

    /**
     * 保存统计比分
     * @param matchScoresInfo
     * @param data
     */
    void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data,StandardMatchInfo standardMatchInfo);



    /**
     *  其他比分计算 根据球种和 code 自定义  比如2黄换1红
     * */

    /**
     * 查询标准比分和其他数据源比分
     * @param standardMatchId
     * @return
     */
    StandardScoreCenterDTO queryMatchScores(Long standardMatchId);

    /**
     * 修改标准比分
     * @param scores
     * @return
     */
    Response editStandScores(StandardScoreCenter scores, StandardMatchScores standardMatchScores, StandardMatchInfo standardMatchInfo);

    void canceledGoal(MatchScoresInfo matchScoresInfo, MatchEventInfo data);

    Boolean editAccoSwitch(StandardMatchSwitchDTO switchDTO,StandardMatchScores standardMatchScores,MatchScoresInfo matchScoresInfo,StandardMatchInfo standardMatchInfo);

    MatchResultScoreMsgVo getSportMatchResultScores(StandardMatchInfo standardMatchInfo, Map scores);
}
