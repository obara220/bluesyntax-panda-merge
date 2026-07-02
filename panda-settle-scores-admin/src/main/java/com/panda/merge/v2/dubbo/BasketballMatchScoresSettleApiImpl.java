package com.panda.merge.v2.dubbo;

import com.panda.merge.api.IBasketballMatchScoresSettleApi;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.service.*;
import com.panda.merge.v2.controllerv2.MatchSettleScoreBasketBallController;
import com.panda.merge.v2.controllerv2.MatchSettleThirdScoreController;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@DubboService
@Slf4j
public class BasketballMatchScoresSettleApiImpl implements IBasketballMatchScoresSettleApi {
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
    IMatchSettleScoreService matchSettleScoreService;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    StandardSportTournamentService standardSportTournamentService;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private MatchSettleScoreBasketBallController basketballMatchScoresSettleController;
    @Autowired
    private MatchSettleThirdScoreController matchSettleThirdScoreController;

    @Override
    public List<MatchSettleScoreDto> searchMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return  basketballMatchScoresSettleController.searchMatchSettleScores(settleScoreSearchDto);
    }
    @Override
    public ThirdMatchSettleScoresDto searchThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return  matchSettleThirdScoreController.searchThirdMatchSettleScores(settleScoreSearchDto);
    }

    @Override
    public Boolean getRealTimeOnOff(MatchSettleScoreSearchDto matchSettleScoreSearchDto){
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchSettleScoreSearchDto.getStandardMatchId());
        //查询即时结算开关
        return basketballInSettleService.checkRealtimeAndPSwitch(2L, standardMatchInfo.getId(), standardMatchInfo.getStandardTournamentId());
    }
    @Override
    public Response updateMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return basketballMatchScoresSettleController.updateMatchSettleScore(matchSettleScoreDto);
    }

    @Override
    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        return  basketballMatchScoresSettleController.confirmMatchSettleScore(matchSettleScoreDto);

    }


    @Override
    public Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto) {
        return basketballMatchScoresSettleController.settleMatchScore(matchSettleScoreDto);
    }

    @Override
    public Response reSettleMatchScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return basketballMatchScoresSettleController.reSettleMatchScore(matchSettleScoreDto);
    }

    /**
     * 回滚结算
     * */
    @Override
    public Response rollBackSettleMatchScores(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return basketballMatchScoresSettleController.rollBackSettleMatchScores(matchSettleScoreDto);

    }



    @Override
    public Response querySettleType(Long StandardMatchId) {
        return basketballMatchScoresSettleController.querySettleType(StandardMatchId);
    }




    public Response cancelSettleEventTag(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        return basketballMatchScoresSettleController.cancelSettleEventTag(matchSettleSwitcherDto);
    }

    @Override
    public Response showPopupScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return basketballMatchScoresSettleController.showPopupScore(matchSettleScoreDto);
    }

    @Override
    public Response confirmBringInScore(BasketBallPutInJsonDto basketBallPutInJsonDto) {
        return basketballMatchScoresSettleController.confirmBringInScore(basketBallPutInJsonDto);
    }

    @Override
    public void verifyScoresIsSame(MatchSettleScore matchSettleScore) {
        basketballMatchScoresSettleController.verifyScoresIsSame(matchSettleScore.getStandardMatchId());
    }

    @Override
    public void verifyScoresIsSame(Long standardMatchId) {
        basketballMatchScoresSettleController.verifyScoresIsSame(standardMatchId);
    }

    @Override
    public Response editShowScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return basketballMatchScoresSettleController.editShowScore(matchSettleScoreDto);
    }

    @Override
    public Response cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        return basketballMatchScoresSettleController.cancelDeleteStatus(matchSettleSwitcherDto);
    }

    @Override
    public MatchSettleInfo searchMatchDelStatus(MatchSettleScoreSearchDto settleScoreSearchDto) {
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(settleScoreSearchDto.getStandardMatchId());
        if (null!=matchSettleInfo){
            return matchSettleInfo;
        }
        return null;
    }


}
