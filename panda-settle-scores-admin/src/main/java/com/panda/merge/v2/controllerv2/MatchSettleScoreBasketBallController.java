package com.panda.merge.v2.controllerv2;

import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.*;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.service.*;
import com.panda.merge.v2.service.IMatchSettleScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
public class MatchSettleScoreBasketBallController {
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
    IMatchSettleScoreService matchSettleScoreBasketBallService;



    public List<MatchSettleScoreDto> searchMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
       return matchSettleScoreBasketBallService.searchMatchSettleScores(settleScoreSearchDto);
    }



    public Response updateMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreBasketBallService.updateMatchSettleScore( matchSettleScoreDto);
    }


    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreBasketBallService.confirmMatchSettleScore(matchSettleScoreDto);
    }


    public Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto) {
        return matchSettleScoreBasketBallService.settleMatchScore(matchSettleScoreDto);
    }

    public Response reSettleMatchScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreBasketBallService.reSettleMatchScore(matchSettleScoreDto);
    }

    /**
     * 回滚结算
     * */
    public Response rollBackSettleMatchScores(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreBasketBallService.rollBackSettleMatchScores(matchSettleScoreDto);
    }



    public Response querySettleType(Long StandardMatchId) {
        return matchSettleScoreBasketBallService.querySettleType(StandardMatchId);

    }




    public Response cancelSettleEventTag(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        return matchSettleScoreBasketBallService.cancelSettleEventTag(matchSettleSwitcherDto);

    }

    public Response showPopupScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreBasketBallService.showPopupScore(matchSettleScoreDto);
    }

    public Response confirmBringInScore(BasketBallPutInJsonDto basketBallPutInJsonDto) {
        return matchSettleScoreBasketBallService.confirmBringInScore(basketBallPutInJsonDto);

    }



    public Response editShowScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreBasketBallService.editShowScore(matchSettleScoreDto);
    }

    public Response cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        return matchSettleScoreBasketBallService.cancelDeleteStatus(matchSettleSwitcherDto);
    }

    public Response editMatchSettleScoreV2(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreBasketBallService.editMatchSettleScoreV2(matchSettleScoreDto);
    }

    public Response confirmMatchSettleScoreV2(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreBasketBallService.confirmMatchSettleScoreV2(matchSettleScoreDto);
    }
    public Response matchReplayAndFreezeV2(SettleQueryDTO settleQueryDTO) {
        return matchSettleScoreBasketBallService.matchReplayAndFreezeV2( settleQueryDTO);

    }

    public Response playReplayAndFreezeV2(SettleQueryDTO settleQueryDTO) {
        return matchSettleScoreBasketBallService.playReplayAndFreezeV2(settleQueryDTO);
    }

    public Response basketBallMatchAndPlayFreezeV2(SettleQueryDTO settleQueryDTO) {
        return matchSettleScoreBasketBallService.basketBallMatchAndPlayFreezeV2(settleQueryDTO);
    }
    public Response basketBallPlayReSettleV2(SettleQueryDTO settleQueryDTO) {
        return matchSettleScoreBasketBallService.basketBallPlayReSettleV2(settleQueryDTO);
    }
    public Response basketBallMatchReSettleV2(SettleQueryDTO settleQueryDTO) {
       return matchSettleScoreBasketBallService.basketBallMatchReSettleV2(settleQueryDTO);
    }
    public Response confirmBringInScoreV2(BasketBallPutInJsonDto basketBallPutInJsonDto) {
      return matchSettleScoreBasketBallService.confirmBringInScoreV2(basketBallPutInJsonDto);
    }

    public void verifyScoresIsSame(Long standardMatchId) {
        matchSettleScoreBasketBallService.verifyScoresIsSame(standardMatchId);
    }
}
