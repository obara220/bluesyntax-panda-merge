package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;

public interface IMatchIcehockeyAdvertiseApi {

    /**
     * 修改开赛时间并返回最新开赛时间
     */
    Response changeMatchStartTime(ChangeMatchStartTimeDto changeMatchStartTimeDto);

    /**
     * 修改倒计时并返回倒计时
     */
    Response changeMatchTime(ChangeMatchTimeDto changeMatchTimeDto);

    /**
     * 暂停/继续/结束/开始
     * Integer type:  1 开始  2 暂停  3 继续 4  阶段结束
     * 返回状态
     */
    Response changeMatchStatus(ChangeMatchStatusDto changeMatchStatus);

    /**
     * 变更比赛的阶段
     * @param changeMatchPeriodDto
     * @return
     */
    Response changeMatchPeriod(ChangeMatchPeriodDto changeMatchPeriodDto);

    /**
     * 比赛结束
     * */
    Response setMatchEnd(ChangeMatchStatusDto changeMatchStatus);

    /**
     * 修改比分并返回赛事的比分
     */
    Response changeMatchScore(ChangeMatchScoreDto changeMatchScoreDto);

    /**
     * 赛事详情查询接口(返回赛事信息)
     */
    Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto);

    /**
     * 大罚小罚编辑
     * */
    Response editFaScore(EditFaScoreDto editFaScoreDto);
}
