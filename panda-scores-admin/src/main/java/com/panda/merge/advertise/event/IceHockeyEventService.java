package com.panda.merge.advertise.event;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;

public interface IceHockeyEventService {

    /**
     * 冰球比分变更事件
     * @param data
     * @param matchScoreCommonVo
     * @param startTimeSecond
     * @param period
     * @param linkId
     * @param remark
     */
    void addScoreChangeEvent(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, Long startTimeSecond, Long period, String linkId, String remark);

    /**
     *
     * @param data
     * @param matchScoreCommonVo
     * @param startTimeSecond
     * @param period
     * @param linkId
     * @param remark
     */
    void addScoreCorrectEvent(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, Long startTimeSecond, Long period, String linkId,String remark);

}
