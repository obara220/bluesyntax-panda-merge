package com.panda.merge.advertise.service;


import com.panda.merge.advertise.dto.FootBallScoreVo;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public interface FootBallScoreService {
    /**
     * 查询当前赛事的总比分以及阶段比分
     * */
    MatchScoreCommonVo searchCommonMatchScore(MatchScoresInfo matchScoresInfo, Long periodId);

    boolean hasExtryPeriod(MatchScoresInfo matchScoresInfo);

    boolean hasPenaltyAwarded(MatchScoresInfo matchScoresInfo);

    FootBallScoreVo transforScore(MatchScoresInfo matchScoresInfo);

    Pair<MatchEventInfoDTO, Map<String, String>> changeScoresByEvent(MatchScoreAndTimeVo data, ConfirmEventDto confirmEventDto, MatchEventInfoDTO matchEventInfoDTO);

    void updateKickOff(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO);

    CommonItem updateScoresByDeleteEvent(MatchScoreAndTimeVo data, DeleteEventDto deleteEventDto, MatchScoresEventInfo oldEvent);

    CommonItem updateScoresByEditEvent(MatchScoreAndTimeVo data, EditEventDto editEventDto, MatchScoresEventInfo oldEvent);

    Response edit15MinGoal(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto);

    Response edit15MinCorner(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto);

    Response edit5MinGoal(MatchScoreAndTimeVo data, Goal5MinDto confirmEventDto);

    Response edit15MinYellowCard(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto);

    Response edit15MinRedCard(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto);
}
