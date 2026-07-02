package com.panda.merge.v2.dubbo;


import com.panda.merge.api.IBasketballNewMatchScoresSettleApi;
import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.settle.*;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.service.IMatchSettleLogService;
import com.panda.merge.service.IMatchSettleService;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.v2.controllerv2.MatchSettleScoreBasketBallController;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@DubboService
@Slf4j
public class BasketballNewMatchScoresSettleApiImpl implements IBasketballNewMatchScoresSettleApi {
    @Autowired
    IMatchSettleService matchSettleService;

    @Autowired
    IWsPushService wsPushService;

    @Autowired
    RedisService redisService;

    @Autowired
    IMatchSettleBatchCheckService matchSettleBatchCheckService;

    @Autowired
    IFootballMatchScoresSettleApi footballMatchScoresSettleApi;

    @Autowired
    IMatchSettleLogService iMatchSettleLogService;

    @Autowired
    MatchSettleScoresProducer matchSettleScoresProducer;

    @Autowired
    MatchSettleCenterProducer matchSettleCenterProducer;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private MatchSettleScoreBasketBallController basketballMatchScoresSettleController;

    @Override
    public Response editMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto) {
        return basketballMatchScoresSettleController.editMatchSettleScoreV2(matchSettleScoreDto);
    }

    @Override
    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        return basketballMatchScoresSettleController.confirmMatchSettleScoreV2(matchSettleScoreDto);
    }

    @Override
    public Response matchReplayAndFreeze(SettleQueryDTO settleQueryDTO) {
        return basketballMatchScoresSettleController.matchReplayAndFreezeV2(settleQueryDTO);

    }

    @Override
    public Response playReplayAndFreeze(SettleQueryDTO settleQueryDTO) {
        return basketballMatchScoresSettleController.playReplayAndFreezeV2(settleQueryDTO);
    }




    /**
     * 篮球赛事级、玩法级的冻结、解冻
     *
     * @param settleQueryDTO
     * @return
     */
    public Response basketBallMatchAndPlayFreeze(SettleQueryDTO settleQueryDTO) {
        return basketballMatchScoresSettleController.basketBallMatchAndPlayFreezeV2(settleQueryDTO);
    }

    /**
     * 篮球玩法级的重跑
     *
     * @param settleQueryDTO
     * @return
     */
    public Response basketBallPlayReSettle(SettleQueryDTO settleQueryDTO) {
        return basketballMatchScoresSettleController.basketBallPlayReSettleV2(settleQueryDTO);
    }

    /**
     * 篮球赛事级的重跑
     * 查询已结算的比分、事件更新后，重新下发到下游，下游重新结算
     *
     * @param settleQueryDTO
     * @return
     */
    public Response basketBallMatchReSettle(SettleQueryDTO settleQueryDTO) {
        return  basketballMatchScoresSettleController.basketBallMatchReSettleV2(settleQueryDTO);

    }

    @Override
    public Response confirmBringInScore(BasketBallPutInJsonDto basketBallPutInJsonDto) {
        return basketballMatchScoresSettleController.confirmBringInScoreV2(basketBallPutInJsonDto);
    }

}
