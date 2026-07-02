package com.panda.merge.v2.dubbo;

import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.panda.merge.api.IFootballNewMatchScoresSettleApi;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.converter.SettleMentionConverter;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.*;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.service.IMatchSettleLogService;
import com.panda.merge.service.IMatchSettleService;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.service.syncScore.SyncScoreFactory;
import com.panda.merge.v2.controllerv2.MatchSettleScoreFootBallController;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;



@Service
@DubboService
@Slf4j
public class FootballNewMatchScoresSettleApiImpl implements IFootballNewMatchScoresSettleApi {


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
    SettleMentionConverter settleMentionConverter;
    @Autowired
    SyncScoreFactory syncScoreFactory;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    private MatchSettleScoreFootBallController matchSettleScoreFootBallController;
    @Override
    public Response editMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.editMatchSettleScorev2(matchSettleScoreDto);
    }

    @Override
    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.confirmMatchSettleScoreV2(matchSettleScoreDto);
    }

    @Override
    public Response editMatchSettleEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
       return matchSettleScoreFootBallController.editMatchSettleEventV2(editMatchSettleEventDto);
    }



    @Override
    public Response confirmMatchSettleEvent(EditMatchSettleEventDto matchSettleEventDto) {
        return matchSettleScoreFootBallController.confirmMatchSettleEventV2(matchSettleEventDto);
    }
    /**
     * 新编辑点球大战比分
     * 编辑逻辑: 1. 如果有 二次编辑权限直接编辑  要重写 需要重新计算主客队谁先踢球 而且判断是否主客队谁先踢球已经结算 否则无法编辑
     *     2.如果没有 则走多人编辑  需要重新计算主客队谁先踢球 而且判断是否主客队谁先踢球已经结算 否则无法编辑
     *     确认逻辑: 1.如果有二次编辑权限直接走确认 结算
     *     2.如果没有则走多人确认公共事件方法
     *
     * 点球大战设置比分
     * */
    @Override
    public Response setPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto) {
        return matchSettleScoreFootBallController.setPenaltyScoresV2(settleScoreSearchDto);
    }
    /**
     * 点球大战确认
     * */
    @Override
    public Response confirmPenaltyScores(EditMatchSettleEventDto matchSettleEventDto) {
       return matchSettleScoreFootBallController.confirmPenaltyScoresV2(matchSettleEventDto);
    }


    @Override
    public boolean isLockedByMatchSettle(Long standardMatchId,String userName) {
        return matchSettleScoreFootBallController.isLockedByMatchSettleV2(standardMatchId,userName);
    }

    @Override
    public Response confirmMatchSettlePlayerAndMethod(EditMatchSettleEventDto matchSettleEventDto) {
        return matchSettleScoreFootBallController.confirmMatchSettlePlayerAndMethodV2(matchSettleEventDto);
    }

    @Override
    public Response settleMatchSettlePlayerAndMethod(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.settleMatchSettlePlayerAndMethodV2(matchSettleScoreDto);
    }
    /**
     * 点球大战谁先踢球设置(按操盘手顺序)
     * */
    @Override
    public Response setPenaltyTeamFirst(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.setPenaltyTeamFirstV2(matchSettleScoreDto);
    }
    /**
     * 点球大战谁先踢球单独结算
     * */
    @Override
    public Response settlePenaltyTeamFirst(EditMatchSettleEventDto matchSettleEventDto) {
       return matchSettleScoreFootBallController.settlePenaltyTeamFirstV2(matchSettleEventDto);
    }

    @Override
    public Response setPenaltyTeamFirstHighL(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.setPenaltyTeamFirstHighLV2(matchSettleScoreDto);
    }

    @Override
    public Response cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto) {
        return matchSettleScoreFootBallController.cancelDeleteStatusV2(matchSettleSwitcherDto);
    }

    @Override
    public Response getPlayerCancelDeleteStatus(Long standardMatchId) {
       return matchSettleScoreFootBallController.getPlayerCancelDeleteStatusV2(standardMatchId);
    }

    @Override
    public Response<AbstractMentionQueryDto> getSettleEventMentionStatus(MentionQueryRequest mentionQueryRequest) {
        return matchSettleScoreFootBallController.getSettleEventMentionStatusV2(mentionQueryRequest);
    }

    @Override
    public Response<Map<Long, AbstractMentionQueryDto>> getSettleEventMentionStatus(List<Long> matchIds, Long sportId) {
        return matchSettleScoreFootBallController.getSettleEventMentionStatusV2(matchIds,sportId);
    }

    @Override
    public Response<String> cancelSettleEventMention(SettleEventDeleteRequest settleEventDeleteRequest) {
        return matchSettleScoreFootBallController.cancelSettleEventMentionV2(settleEventDeleteRequest);
    }
}
