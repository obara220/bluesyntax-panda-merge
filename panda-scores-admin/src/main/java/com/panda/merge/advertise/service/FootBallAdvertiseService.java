package com.panda.merge.advertise.service;

import com.panda.merge.advertise.dto.FootBallAdvertiseVo;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.KickOffDto;
import com.panda.merge.dto.advertise.PossibleEventDto;
import com.panda.merge.model.ThirdMatchInfo;

public interface FootBallAdvertiseService {

    Response matchStart(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkedId, KickOffDto kickOff, ChangeMatchStatusDto changeMatchStatus);

    Response match999End(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkedId, ChangeMatchStatusDto changeMatchStatus);

    Response changeMatchPeriod(MatchScoreAndTimeVo data, Long periodId, String linkedId,String userName);



    Response<FootBallAdvertiseVo> buildFootBallAdvertiseVo(MatchScoreAndTimeVo data);

    void checkAndcreateMinuteScore(MatchEventInfoDTO eventInfoDTO);

    /**
     * 比赛暂停
     *
     * @param matchScoreAndTimeVo 入参对象
     * @param linkedId            关联ID
     * @return 响应
     */
    Response matchPause(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkedId, ChangeMatchStatusDto changeMatchStatus);

    /**
     * 比赛继续
     *
     * @param data     入参
     * @param linkedId linkId
     * @return 响应
     */
    Response matchContinue(MatchScoreAndTimeVo data, String linkedId, Integer controlType);

    /**
     * 比赛中断-结束
     *
     * @param data     入参
     * @param linkedId linkId
     * @return 响应
     */
    Response matchEnd(MatchScoreAndTimeVo data, String linkedId);
}
