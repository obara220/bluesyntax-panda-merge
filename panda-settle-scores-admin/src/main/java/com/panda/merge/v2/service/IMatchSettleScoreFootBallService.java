package com.panda.merge.v2.service;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchDelaySettleInfo;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;

import java.util.List;
import java.util.Map;

public interface IMatchSettleScoreFootBallService {
    Response updateMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto);
    Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto);
    Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto);

    Response addMatchSettleEvent(AddMatchSettleEventDto addMatchSettleEventDto);

    Response editMatchSettleEvent(EditMatchSettleEventDto editMatchSettleEventDto);

    Response editMatchSettleEventMethodAndPlayer(EditMatchSettleEventDto editMatchSettleEventDto);

    Response confirmMatchSettleEvent(EditMatchSettleEventDto matchSettleEventDto);

    Response settleMatchSettleEvent(EditMatchSettleEventDto matchSettleScoreDto);

    Response<PenaltyScoresVo> searchPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto);

    Response addPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto);

    Response setPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto);

    Response reSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto);
    Response reSettleMatchScore(UpdateMatchSettleScoreDto matchSettleScoreDto);

    Response rollBackSettleMatchScores(UpdateMatchSettleScoreDto matchSettleScoreDto);

    Response rollBackSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto);

    Response matchPeriodQuery(MatchPeriodQueryDto matchPeriodQueryDto);

    Response checkScoresOrEvent(MatchCheckSettleScoreEventDto dto);

    Response querySettleType(Long StandardMatchId);
    Response playCategoryFreezeAndReSettle(SettleQueryDTO settleQueryDTO);

    List<MatchDelaySettleInfo> queryMatchDelaySettleInfoById(Long standardId);


    Response editMatchSettleScorev2(UpdateMatchSettleScoreDto matchSettleScoreDto);

    Response confirmMatchSettleScoreV2(ConfirmMatchSettleScoreDto matchSettleScoreDto);

    Response editMatchSettleEventV2(EditMatchSettleEventDto editMatchSettleEventDto);

    Response confirmMatchSettleEventV2(EditMatchSettleEventDto matchSettleEventDto);

    Response setPenaltyScoresV2(EditMatchSettleEventDto settleScoreSearchDto);

    Response confirmPenaltyScoresV2(EditMatchSettleEventDto matchSettleEventDto);

    boolean isLockedByMatchSettleV2(Long standardMatchId,String userName);

    Response confirmMatchSettlePlayerAndMethodV2(EditMatchSettleEventDto matchSettleEventDto);

    Response settleMatchSettlePlayerAndMethodV2(EditMatchSettleEventDto matchSettleScoreDto);

    Response setPenaltyTeamFirstV2(EditMatchSettleEventDto matchSettleScoreDto);

    Response settlePenaltyTeamFirstV2(EditMatchSettleEventDto matchSettleEventDto);

    Response setPenaltyTeamFirstHighLV2(EditMatchSettleEventDto matchSettleScoreDto);

    Response cancelDeleteStatusV2(MatchSettleSwitcherDto matchSettleSwitcherDto);

    Response getPlayerCancelDeleteStatusV2(Long standardMatchId);

    Response<AbstractMentionQueryDto> getSettleEventMentionStatusV2(MentionQueryRequest mentionQueryRequest);

    Response<Map<Long, AbstractMentionQueryDto>> getSettleEventMentionStatusV2(List<Long> matchIds, Long sportId);

    Response<String> cancelSettleEventMentionV2(SettleEventDeleteRequest settleEventDeleteRequest);

    void updateGoalAndCornerEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway,String eventCode);

    void updateFaCardEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway);

    boolean countPenaltyScores(EditMatchSettleEventDto matchSettleEvent, MatchSettleEvent settleEvent);

    boolean isTeamFirstSettled(Long standardMatchId);
}
