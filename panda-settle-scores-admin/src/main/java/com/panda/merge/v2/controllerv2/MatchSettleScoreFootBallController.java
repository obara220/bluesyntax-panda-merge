package com.panda.merge.v2.controllerv2;

import com.panda.merge.config.RedisService;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.service.*;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.v2.service.IMatchSettleScoreFootBallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.*;


@Controller
public class MatchSettleScoreFootBallController {
    @Autowired
    MatchSettleScoresProducer matchSettleScoresProducer;
    @Autowired
    IMatchSettleService matchSettleService;
    @Autowired
    IMatchSettleLogService iMatchSettleLogService;
    @Autowired
    RedisService redisService;
    @Autowired
    IWsPushService wsPushService;
    @Autowired
    MatchSettleCenterProducer matchSettleCenterProducer;
    @Autowired
    IMatchSettleBatchCheckService matchSettleBatchCheckService;
    @Autowired
    IBasketballInSettleService basketballInSettleService;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;

    @Autowired
    IMatchSettleScoreFootBallService matchSettleScoreFootBallService;
    public Response updateMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.updateMatchSettleScore(matchSettleScoreDto);
    }

    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.confirmMatchSettleScore(matchSettleScoreDto);
    }
    public Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto) {
      return matchSettleScoreFootBallService.settleMatchScore(matchSettleScoreDto);
    }
    public Response addMatchSettleEvent(AddMatchSettleEventDto addMatchSettleEventDto) {
        return matchSettleScoreFootBallService.addMatchSettleEvent(addMatchSettleEventDto);
    }
    public Response editMatchSettleEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
        return matchSettleScoreFootBallService.editMatchSettleEvent(editMatchSettleEventDto);
    }
    public Response editMatchSettleEventMethodAndPlayer(EditMatchSettleEventDto editMatchSettleEventDto) {
        return matchSettleScoreFootBallService.editMatchSettleEventMethodAndPlayer(editMatchSettleEventDto);
    }
    public Response confirmMatchSettleEvent(EditMatchSettleEventDto matchSettleEventDto) {
        return matchSettleScoreFootBallService.confirmMatchSettleEvent(matchSettleEventDto);
    }
    public Response settleMatchSettleEvent(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.settleMatchSettleEvent(matchSettleScoreDto);
    }
    public Response<PenaltyScoresVo> searchPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleScoreFootBallService.searchPenaltyScores(settleScoreSearchDto);
    }
    public Response addPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleScoreFootBallService.addPenaltyScores(settleScoreSearchDto);
    }

    public Response setPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto) {
       return matchSettleScoreFootBallService.setPenaltyScores(settleScoreSearchDto);
    }
    public Response reSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto) {
      return matchSettleScoreFootBallService.reSettleMatchEvent(matchSettleScoreDto);
    }
    public Response reSettleMatchScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
       return matchSettleScoreFootBallService.reSettleMatchScore(matchSettleScoreDto);
    }
    public Response rollBackSettleMatchScores(UpdateMatchSettleScoreDto matchSettleScoreDto) {
       return matchSettleScoreFootBallService.rollBackSettleMatchScores(matchSettleScoreDto);
    }
    public Response rollBackSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto) {
       return matchSettleScoreFootBallService.rollBackSettleMatchEvent(matchSettleScoreDto);
    }
    public Response matchPeriodQuery(MatchPeriodQueryDto matchPeriodQueryDto) {
        return matchSettleScoreFootBallService.matchPeriodQuery(matchPeriodQueryDto);
    }

    public Response checkScoresOrEvent(MatchCheckSettleScoreEventDto dto) {
       return matchSettleScoreFootBallService.checkScoresOrEvent(dto);
    }
    public Response querySettleType(Long StandardMatchId) {
        return matchSettleScoreFootBallService.querySettleType(StandardMatchId);
    }
    public Response playCategoryFreezeAndReSettle(SettleQueryDTO settleQueryDTO) {
        return matchSettleScoreFootBallService.playCategoryFreezeAndReSettle(settleQueryDTO);

    }
    public List<MatchDelaySettleInfo> queryMatchDelaySettleInfoById(Long standardId) {
        return matchSettleScoreFootBallService.queryMatchDelaySettleInfoById(standardId);
    }

    public Response editMatchSettleScorev2(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.editMatchSettleScorev2(matchSettleScoreDto);

    }

    public Response confirmMatchSettleScoreV2(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.confirmMatchSettleScoreV2(matchSettleScoreDto);
    }
    public Response editMatchSettleEventV2(EditMatchSettleEventDto editMatchSettleEventDto) {
        return matchSettleScoreFootBallService.editMatchSettleEventV2(editMatchSettleEventDto);
    }
    public Response confirmMatchSettleEventV2(EditMatchSettleEventDto matchSettleEventDto) {
        return matchSettleScoreFootBallService.confirmMatchSettleEventV2(matchSettleEventDto);
    }
    public Response setPenaltyScoresV2(EditMatchSettleEventDto settleScoreSearchDto) {
        return matchSettleScoreFootBallService.setPenaltyScoresV2(settleScoreSearchDto);
    }

    public Response confirmPenaltyScoresV2(EditMatchSettleEventDto matchSettleEventDto) {
        return matchSettleScoreFootBallService.confirmPenaltyScoresV2(matchSettleEventDto);
    }

    public boolean isLockedByMatchSettleV2(Long standardMatchId,String userName) {
       return matchSettleScoreFootBallService.isLockedByMatchSettleV2(standardMatchId,userName);
    }

    public Response confirmMatchSettlePlayerAndMethodV2(EditMatchSettleEventDto matchSettleEventDto) {
        return matchSettleScoreFootBallService.confirmMatchSettlePlayerAndMethodV2(matchSettleEventDto);
    }
    public Response settleMatchSettlePlayerAndMethodV2(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.settleMatchSettlePlayerAndMethodV2(matchSettleScoreDto);
    }
    public Response setPenaltyTeamFirstV2(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.setPenaltyTeamFirstV2(matchSettleScoreDto);
    }
    public Response settlePenaltyTeamFirstV2(EditMatchSettleEventDto matchSettleEventDto) {
        return matchSettleScoreFootBallService.settlePenaltyTeamFirstV2(matchSettleEventDto);
    }
    public Response setPenaltyTeamFirstHighLV2(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.setPenaltyTeamFirstHighLV2(matchSettleScoreDto);
    }
    public Response cancelDeleteStatusV2(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        return matchSettleScoreFootBallService.cancelDeleteStatusV2(matchSettleSwitcherDto);
    }
    public Response getPlayerCancelDeleteStatusV2(Long standardMatchId) {
        return matchSettleScoreFootBallService.getPlayerCancelDeleteStatusV2(standardMatchId);
    }
    public Response<AbstractMentionQueryDto> getSettleEventMentionStatusV2(MentionQueryRequest mentionQueryRequest) {

      return matchSettleScoreFootBallService.getSettleEventMentionStatusV2(mentionQueryRequest);
    }
    public Response<Map<Long, AbstractMentionQueryDto>> getSettleEventMentionStatusV2(List<Long> matchIds, Long sportId) {
        return matchSettleScoreFootBallService.getSettleEventMentionStatusV2(matchIds,sportId);

    }
    public Response<String> cancelSettleEventMentionV2(SettleEventDeleteRequest settleEventDeleteRequest) {
        return matchSettleScoreFootBallService.cancelSettleEventMentionV2(settleEventDeleteRequest);

    }

    public void updateGoalAndCornerEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway, String eventCode) {
        matchSettleScoreFootBallService.updateGoalAndCornerEventByInfo(matchSettleEvent, homeAway, eventCode);
    }

    public void updateFaCardEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway) {
        matchSettleScoreFootBallService.updateFaCardEventByInfo(matchSettleEvent,homeAway);
    }

    public boolean countPenaltyScores(EditMatchSettleEventDto matchSettleEvent, MatchSettleEvent settleEvent) {
        return matchSettleScoreFootBallService.countPenaltyScores(matchSettleEvent,settleEvent);
    }

    public boolean isTeamFirstSettled(Long standardMatchId) {
        return matchSettleScoreFootBallService.isTeamFirstSettled(standardMatchId);
    }

    public Response updateMatchSettleScoreV3(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.updateMatchSettleScoreV3(matchSettleScoreDto);
    }

    public Response confirmMatchSettleScoreV3(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallService.confirmMatchSettleScoreV3(matchSettleScoreDto);
    }
}
