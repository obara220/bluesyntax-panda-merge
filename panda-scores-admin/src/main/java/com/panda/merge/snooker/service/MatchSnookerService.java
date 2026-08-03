package com.panda.merge.snooker.service;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.*;

/**
 * 斯诺克报球板Service接口
 */
public interface MatchSnookerService {

    /**
     * 查询比分列表
     */
    Response scoreList(EventListV2Dto eventListV2Dto);

    /**
     * 重新排球
     */
    Response rerack(MatchScoreAndTimeVo matchScoreAndTimeVo, RerackV2Dto rerackV2Dto) throws Exception;

    /**
     * 批量编辑比分
     */
    void batchEditScores(EditScoreV2Dto editScoreV2Dto);

    /**
     * 发送事件（不影响比分的事件，如持杆、未进球等）
     */
    Response sendEvent(MatchScoreAndTimeVo data, SendEventDto dto) throws Exception;

    /**
     * 开球
     */
    Response kickOff(MatchScoreAndTimeVo data, KickOffV2Dto dto) throws Exception;

    /**
     * 切换阶段
     */
    Response changeMatchPeriod(MatchScoreAndTimeVo data, ChangeMatchPeriodV2Dto dto);

    /**
     * 改变比赛状态
     */
    Response changeMatchStatus(MatchScoreAndTimeVo data, ChangeMatchStatusV2Dto dto);

    /**
     * 修改赛制
     */
    Response changeMatchLength(MatchScoreAndTimeVo data, ChangeMatchLengthV2Dto dto);

}
