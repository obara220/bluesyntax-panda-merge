package com.panda.merge.tabletennis.service;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.v2.*;

/**
 * 乒乓球报球板 service 接口。和 {@link com.panda.merge.snooker.service.MatchSnookerService} 一一对应，
 * 负责的是乒乓球独有的几个端点；公共动作复用父类 AbsMatchCommonProcessor。
 */
public interface MatchTableTennisService {

    Response scoreList(EventListV2Dto eventListV2Dto);

    Response batchEditScores(EditScoreV2Dto editScoreV2Dto);

    /**
     * 发送不影响 setScore 的辅助事件（发球 / 重新发球 / 黄牌 / 加速模式 / 红黄牌同手等）。
     * 该方法会调用 doCalculation 把对应统计字段累加 1，不会改 setScore。
     */
    Response sendEvent(MatchScoreAndTimeVo data, SendEventDto dto) throws Exception;

    /**
     * 发球按钮：前端只调用 /kickOff。后端按当局是否首次点击派发先发球事件，全部 inline。
     */
    Response kickOff(MatchScoreAndTimeVo data, KickOffV2Dto dto) throws Exception;

    /**
     * 切换赛事阶段（进入下一局 / 小局休息 / 回跳历史局）。
     */
    Response changeMatchPeriod(MatchScoreAndTimeVo data, ChangeMatchPeriodV2Dto dto);

    /**
     * 改变比赛状态（开赛 / 暂停 / 继续 / 中断 / 重开 / 结束）。
     */
    Response changeMatchStatus(MatchScoreAndTimeVo data, ChangeMatchStatusV2Dto dto);

    /**
     * 修改赛制（best5 of 3 / best7 of 5）。
     */
    Response changeMatchLength(MatchScoreAndTimeVo data, ChangeMatchLengthV2Dto dto);
}
