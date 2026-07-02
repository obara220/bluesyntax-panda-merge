package com.panda.merge.v2.service;

import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;

import java.util.List;

public interface IMatchSettleScoreService {
    List<MatchSettleScoreDto> searchMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto);
    Response updateMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto);
     Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto);

    Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto);

    Response reSettleMatchScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto);

    Response rollBackSettleMatchScores(UpdateBasketBallSettleScoreDto matchSettleScoreDto);

    void updateMatchFifteenMinGraySettleFactor(Long standardMatchId,String settleNum);

    void endEventSettleByScore(MatchSettleScore matchSettleScore);
    Response querySettleType(Long StandardMatchId);

    Response cancelSettleEventTag(MatchSettleSwitcherDto matchSettleSwitcherDto);

    Response showPopupScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto);

    Response confirmBringInScore(BasketBallPutInJsonDto basketBallPutInJsonDto);

    Response editShowScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto);

    Response cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto);

     Response editMatchSettleScoreV2(UpdateBasketBallSettleScoreDto matchSettleScoreDto);

    boolean isPeriodScoresBeforeSettledByEvent(MatchSettleEvent matchSettleEvent);

    boolean isFiveMinPeriodScoresBeforeSettled(MatchSettleScore matchSettleScore);

    boolean isAllPeriodScoresBeforeSettled(MatchSettleScore matchSettleScore);

     Response confirmMatchSettleScoreV2(ConfirmMatchSettleScoreDto matchSettleScoreDto);

    Response matchReplayAndFreezeV2(SettleQueryDTO settleQueryDTO);

    Response basketBallPlayReSettleV2(SettleQueryDTO settleQueryDTO);

    Response playReplayAndFreezeV2(SettleQueryDTO settleQueryDTO);

    Response basketBallMatchAndPlayFreezeV2(SettleQueryDTO settleQueryDTO);
    Response basketBallMatchReSettleV2(SettleQueryDTO settleQueryDTO);

    Response confirmBringInScoreV2(BasketBallPutInJsonDto basketBallPutInJsonDto);

    void verifyScoresIsSame(MatchSettleScore matchSettleScore);

    void verifyScoresIsSame(Long standardMatchId);
}
