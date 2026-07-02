package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.*;

import java.util.List;
import java.util.Map;

/**
 * 结算2.0 dubbo服务
 * */
public interface IFootballNewMatchScoresSettleApi {


    /**
     * 比分编辑
     * */
    Response   editMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto);
    /**
     * 比分确认
     * */
    Response   confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto);

    /**
     * 事件编辑
     * */
    Response editMatchSettleEvent(EditMatchSettleEventDto editMatchSettleEventDto);

    /**
     * 事件确认
     * */
    Response   confirmMatchSettleEvent(EditMatchSettleEventDto matchSettleEventDto);

    /**
     * 点球大战设置比分
     * */
    Response setPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto);
    /**
     * 点球大战确认
     * */
    Response   confirmPenaltyScores(EditMatchSettleEventDto matchSettleEventDto);
    /**
     * 判断赛事是否被限制操作
     * */
     boolean isLockedByMatchSettle(Long standardMatchId,String userName);

    /**
     * 球员进球方式确认
     * */
    Response   confirmMatchSettlePlayerAndMethod(EditMatchSettleEventDto matchSettleEventDto);

    /**
     * 球员进球方式结算
     * */
    Response   settleMatchSettlePlayerAndMethod(EditMatchSettleEventDto matchSettleScoreDto);

    /**
     * 点球大战谁先踢球设置
     * */
    Response   setPenaltyTeamFirst(EditMatchSettleEventDto matchSettleScoreDto);
    /**
     * 点球大战谁先踢球单独结算
     * */
    Response   settlePenaltyTeamFirst(EditMatchSettleEventDto matchSettleEventDto);

    /**
     * 点球大战谁先踢球设置 高权重接口
     * */
    Response   setPenaltyTeamFirstHighL(EditMatchSettleEventDto matchSettleScoreDto);

    /**
     *  赛事级删除标记清理
     * */
    Response cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto);

    Response getPlayerCancelDeleteStatus(Long standardMatchId);

    Response<AbstractMentionQueryDto> getSettleEventMentionStatus(MentionQueryRequest mentionQueryRequest);

    Response<Map<Long, AbstractMentionQueryDto>> getSettleEventMentionStatus(List<Long> matchIds, Long sportId);

    Response<String> cancelSettleEventMention(SettleEventDeleteRequest settleEventDeleteRequest);
}
