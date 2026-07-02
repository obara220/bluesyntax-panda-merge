package com.panda.merge.advertise.service;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.EditFaScoreDto;

public interface IceHockeyAdvertiseService {

    /**
     * 冰球报球板修改赛事阶段
     * @param data
     * @param periodId
     * @param linkId
     * @return
     */
    Response changeMatchPeriod(MatchScoreAndTimeVo data, Long periodId, String linkId,String userName);

    /**
     * 修改报球板比分
     * @param data
     * @param changeMatchScoreDto
     * @return
     */
    Response changeMatchScore(String lingkId, MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto);

    /**
     * 获取赛事的比分信息
     * @param data
     * @return
     */
    Response buildIceHockeyAdvertiseVo(MatchScoreAndTimeVo data);

    /**
     * 冰球事件状态切换:开始
     * @param matchScoreAndTimeVo
     * @param linkId
     * @return
     */
    Response matchStart(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId);

    /**
     * 冰球事件状态切换:暂停
     * @param matchScoreAndTimeVo
     * @param linkId
     * @return
     */
    Response matchPause(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId);

    /**
     * 冰球事件状态切换:继续
     * @param matchScoreAndTimeVo
     * @param linkId
     * @return
     */
    Response matchContinue(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId);

    /**
     * 冰球事件状态切换:结束
     * @param matchScoreAndTimeVo
     * @param linkId
     * @return
     */
    Response matchEnd(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId);

    /**
     * 999 赛事结束
     * @param matchScoreAndTimeVo
     * @param linkId
     * @return
     */
    Response match999End(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId);

    Response editFaScore(MatchScoreAndTimeVo data ,EditFaScoreDto editFaScore);
}
