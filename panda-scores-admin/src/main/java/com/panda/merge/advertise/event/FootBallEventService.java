package com.panda.merge.advertise.event;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.model.MatchScoresEventInfo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface FootBallEventService {
    Response possibleEvent(MatchScoreAndTimeVo data, PossibleEventDto possibleEventDto);

    Response cancelEvent(MatchScoreAndTimeVo data, CancelEventDto cancelEventDto);

    Response confirmEvent(MatchScoreAndTimeVo data, ConfirmEventDto confirmEventDto);

    Response confirmPenaltyEvent( MatchScoreAndTimeVo data, ConfirmPenaltyEventDTO confirmPenaltyEventDTO);

    void sendKickOffEventAfterGoal(MatchEventInfoDTO matchEventInfoDTO);

    Response isDanger(MatchScoreAndTimeVo data, IsDangerDto isDangerDto);

    Response kickOff(MatchScoreAndTimeVo data, KickOffDto kickOff);

    Response overTimeEvent(MatchScoreAndTimeVo data, OverTimeEventDto overTimeEventDto);

    Response deleteEvent(MatchScoreAndTimeVo data, DeleteEventDto deleteEventDto);

    Response editEvent(MatchScoreAndTimeVo data, EditEventDto editEventDto);

    Response eventList(MatchScoreAndTimeVo data, EventListDto eventListDto);

    Boolean matchIsDanger(Long thirdMatchId);

    Response editPenaltyScore(MatchScoreAndTimeVo data, PenaltyScoresEditDto penaltyScoresEditDto);

    Response addPenaltyRounds(MatchScoreAndTimeVo data, PenaltyAddRoundsDto penaltyAddRoundsDto);

    Response changePenaltyRounds(MatchScoreAndTimeVo data, PenaltyChangeRoundsDto penaltyChangeRoundsDto);

    Response changePenaltyFirst(MatchScoreAndTimeVo data, PenaltyFirstDto penaltyFirstDto);

    Response edit15MinGoal(MatchScoreAndTimeVo data,Goal15MinDto confirmEventDto);

    Response edit15MinCorner(MatchScoreAndTimeVo data,Goal15MinDto confirmEventDto);

    Response edit15MinYellowCard(MatchScoreAndTimeVo data,Goal15MinDto confirmEventDto);

    Response edit15MinRedCard(MatchScoreAndTimeVo data,Goal15MinDto confirmEventDto);

    Response edit5MinGoal(MatchScoreAndTimeVo data,Goal5MinDto confirmEventDto);

    void checkAndCreateMinutsScore(MatchScoreAndTimeVo data, Long timeFromStartSecond);

    List<PDFootBallEventDto> buildEventList(String thirdMatchSourceId, String dataSourceCode, EventListDto eventListDto);

    /**
     * 点球重踢
     *
     * @param res          响应数据
     * @param retakePenDto 重踢
     * @param matchScoresEventInfo 原始数据(点球确认时数据)
     * @return 响应
     */
    Response retakePen(Response res, RetakePenDto retakePenDto, MatchScoresEventInfo matchScoresEventInfo);

    /**
     * 没有重踢
     *
     * @param res                  响应数据
     * @param noRetakePen          没有重踢
     * @param originMatchInfo      原始数据(点球确认时数据)
     * @param matchScoresEventInfo 当前没有重踢时数据
     * @return 响应
     */
    Response noRetakePen(Response res, NoRetakePenDto noRetakePen, MatchScoresEventInfo originMatchInfo, MatchScoresEventInfo matchScoresEventInfo);

    /**
     * 点球开踢
     *
     * @param data
     * @param takePenaltyDto
     * @return
     */
    Response executeTakePenalty(MatchScoreAndTimeVo data, TakePenaltyDTO takePenaltyDto);
}
