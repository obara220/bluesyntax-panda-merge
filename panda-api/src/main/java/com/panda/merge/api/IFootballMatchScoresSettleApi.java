package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchDelaySettleInfo;
import com.panda.merge.model.MatchSettleCheckInfo;
import com.panda.merge.model.MatchSettleEvent;

import java.util.List;

/**
 * 结算2.0 dubbo服务
 * */
public interface IFootballMatchScoresSettleApi {

    /**
     * 比分查询
     * */
    List<MatchSettleScoreDto>  searchMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto);
    /**
     * 三方比分查询
     * */
    ThirdMatchSettleScoresDto  searchThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto);

    /**
     * 事件查询
     * */
    List<MatchSettleEventDto>  searchMatchSettleEvent(MatchSettleScoreSearchDto settleScoreSearchDto);

    /**
     * 三方事件查询
     * */
    ThirdMatchSettleEventDto  searchThirdMatchSettleEvent(MatchSettleScoreSearchDto settleScoreSearchDto);
    /**
     * 比分编辑
     * */
    Response   updateMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto);
    /**
     * 比分确认
     * */
    Response   confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto);
    /**
     * 比分结算
     * */
    Response   settleMatchScore(SettleMatchScoreDto matchSettleScoreDto);
    /**
     * 重新比分结算(程序重跑)
     * */
    Response  reSettleMatchScore(UpdateMatchSettleScoreDto matchSettleScoreDto);

    /**
     * 回滚比分结算(回滚比分)
     * */
    Response  rollBackSettleMatchScores(UpdateMatchSettleScoreDto matchSettleScoreDto);

    /**
     * 事件编辑
     * */
    Response editMatchSettleEvent(EditMatchSettleEventDto editMatchSettleEventDto);

    /**
     * 事件比分确认
     * */
    Response   confirmMatchSettleEvent(EditMatchSettleEventDto matchSettleEventDto);

    /**
     * 事件比分结算
     * */
    Response   settleMatchSettleEvent(EditMatchSettleEventDto matchSettleScoreDto);
    /**
     * 重新事件结算(程序重跑)
     * */
    Response  reSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto);

    /**
     * 回滚事件结算
     * */
    Response  rollBackSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto);

    /**
     * 事件新增
     * */
    Response  addMatchSettleEvent(AddMatchSettleEventDto addMatchSettleEventDto);

    /**
     * 点球大战查询 三方暂无
     * */
    Response<PenaltyScoresVo> searchPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto);
    /**
     * 点球大战新增
     * */
    Response addPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto);
    /**
     * 点球大战设置比分
     * */
    Response setPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto);

    /**
     * 设置球员和进球方式
     * */
    Response editMatchSettleEventMethodAndPlayer(EditMatchSettleEventDto editMatchSettleEventDto);


    /**
     * 根据赛事阶段查询比分
     * */
    Response  matchPeriodQuery(MatchPeriodQueryDto matchPeriodQueryDto);

    /**
     * 判断比分是否一致
     * */
    Response checkScoresOrEvent(MatchCheckSettleScoreEventDto matchCheckSettleScoreEventDto);
    /**
     *  查询结算方式 1 还是 2
     * 传参：
     * 返回:
     * */
    Response querySettleType(Long StandardMatchId);
    /**
     *  玩法级重跑和冻结
     * 传参：
     * 返回:
     * */
    Response playCategoryFreezeAndReSettle(SettleQueryDTO settleQueryDTO);


    void updateGoalAndCornerEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway, String eventCode);

    void updateFaCardEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway);

    boolean countPenaltyScores(EditMatchSettleEventDto matchSettleEvent, MatchSettleEvent settleEvent);

    boolean isTeamFirstSettled(Long standardMatchId);

    List<MatchDelaySettleInfo> queryMatchDelaySettleInfoById(Long standardId);

    void delayCheckCommonMatchSettleScoreEvent(Object matchSettleScoreEvent, MatchSettleCheckInfo matchSettleCheckInfo, boolean createCheck);

    void slaveRocketMqStopResume(Integer isStop);

}
