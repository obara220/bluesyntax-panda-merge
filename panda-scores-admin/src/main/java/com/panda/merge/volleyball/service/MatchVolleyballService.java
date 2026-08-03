package com.panda.merge.volleyball.service;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.*;

/**
 * 排球报球板 service 接口。和 {@link com.panda.merge.snooker.service.MatchSnookerService} 一一对应，
 * 负责的是排球独有的几个端点；公共动作复用父类 AbsMatchCommonProcessor。
 */
public interface MatchVolleyballService {

    Response scoreList(EventListV2Dto eventListV2Dto);

    Response batchEditScores(EditScoreV2Dto editScoreV2Dto);

    /**
     * 发送不影响 setScore 的辅助事件（kill / block / expulsion / disqualification / penalty /
     * error / current_serve_volleyball）。该方法会调用 doCalculation 把对应统计字段累加 1，
     * 不会改 setScore。
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
