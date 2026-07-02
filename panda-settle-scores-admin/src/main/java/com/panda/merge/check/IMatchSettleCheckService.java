//package com.panda.merge.check;
//
//import com.panda.merge.dto.CheckIsGreyDto;
//import com.panda.merge.dto.settle.MatchSettleEventDto;
//import com.panda.merge.dto.settle.MatchSettleScoreDto;
//import com.panda.merge.dto.settle.PenaltyScoresVo;
//import com.panda.merge.model.*;
//import org.apache.commons.lang3.tuple.Pair;
//
//import java.util.List;
//
//public interface IMatchSettleCheckService {
//    /**
//     *      核对比分
//     */
//    boolean checkMatchThirdSettleScores(MatchSettleThirdScore matchSettleThirdScore ,String linkedId,Long second, CheckIsGreyDto checkIsGreyDto);
//    /**
//     *      事件核对
//     */
//    boolean checkMatchThirdSettleEvent(MatchSettleThirdEvent matchSettleThirdEvent,String linkedId,Long periodSeconds);
//     /**
//     *      * 比分核对标准类X
//     * */
//    Pair<Boolean, Boolean> checkCommonMatchSettleScoreEvent(Object matchSettleScoreEvent , MatchSettleCheckInfo matchSettleCheckInfo, boolean createCheck);
//    /**
//     * 新增赛事限制操作用户群
//     * */
//    boolean lockUserListByCheckPass(Long standardMatchId, List<String> userNameList);
//
//    boolean sendCheckMessage(Object matchSettleScore, MatchSettleCheckInfo matchSettleCheckInfo,boolean createCheck);
//
//    MatchSettleCheckInfo searchCheckInfoByUser(Long scoreEventId,Long standardMatchId,String userName);
//
//     boolean checkIfNotSend(MatchSettleCheckInfo matchSettleCheckInfo);
//
//    void searchCheckStatusByScoresList(List<MatchSettleScoreDto> matchSettleScoreDtos,String OperatorName);
//
//    void searchCheckStatusByEventList(List<MatchSettleEventDto> matchSettleScoreDtos, String OperatorName);
//    /**
//     * 自动结算流程X
//     * */
//    void autoSettle(Object matchSettleScoreEventInfo,MatchSettleCheckInfo checkInfo);
//
//    void saveOrUpdateCheckInfo(MatchSettleCheckInfo checkInfo);
//
//    boolean isAllPeriodScoresBeforeSettled(MatchSettleScore matchSettleScore);
//
//    boolean matchIsAutoSettle(long standardMatchId);
//    //进球事件多数据商确认主逻辑
//    void confirmGoalDoFilter(List<MatchEventInfo> data);
//    //更新当前进球事件状态
//    void updateMatchSettleGoalStatus(MatchEventInfo matchEventInfo);
//
//    boolean isMatchGoalStatusConfirm(Long thirdMatchId, String eventCode);
//
//
//    void canceledCheckMatchThirdSettleEvent(MatchSettleThirdEvent matchSettleEvent, MatchEventInfo data, Integer order);
//
//    String getBussinessEvent(Long standardMatchId);
//
//    void changeHomeAway(List<MatchEventInfo> list);
//
//    void changeHomeAway(MatchEventInfo data);
//
//    void rollbackScores(MatchSettleScore matchSettleScore);
//
//    void rollbackEvent(MatchSettleEvent matchSettleEvent);
//
//
//    Long searchEventTimeByEvent(MatchSettleEvent event,MatchSettleCheckInfo checkInfo);
//
//    Long searchEventTimeByScores(MatchSettleScore settleScore);
//
//    Long searchEventTimeByEvent(MatchSettleEvent event);
//
//    boolean settlePenaltyTeamFirst(MatchSettleEvent event);
//
//    void searchCheckStatusByPenalty(PenaltyScoresVo penaltyScoresVo, String operatorName);
//
//    boolean isFiveMinPeriodScoresBeforeSettled(MatchSettleScore matchSettleScore);
//
//    boolean isPeriodScoresBeforeSettledByEvent(MatchSettleEvent matchSettleEvent);
//
//    Integer isPeriodScoreEquile(MatchSettleScore matchSettleScore,MatchSettleCheckInfo checkInfo, MatchSettleInfo matchSettleInfo);
//
//    void updateMatchSettleCornerStatus(MatchEventInfo matchEventInfo);
//
//     MatchEventInfo getOldMatchInfoByCancel(MatchEventInfo data);
//
//     /**
//      * 1. 如果结算 上半场 下半场 加时上半场 加时 下半场 的  进球 角球 罚牌的时候
//      * 需要查询对应次序事件是否已经结算，如果不结算或者 比分不一致，则返回结算失败
//      * 2. 如果结算了，无 无角球 无 进球 无 罚牌 则 先结算 无事件 后 再 结算 对应阶段比分
//      * */
//     boolean  checkSettleScoreAndAutoSettleNonEvent(MatchSettleScore matchSettleScore,MatchSettleCheckInfo checkInfo);
//
//    boolean checkBasketPeriodScoreOrder(MatchSettleScore matchSettleScore);
//
//    boolean updateMatchGrayStatus(Long standardMatchId);
//
//    void updateMatchCurrentEventStatus(Long standardMatchId);
//
//    void endEventSettleByScore(MatchSettleScore matchSettleScore);
//
//    void endEventSettleByEvent(MatchSettleEvent matchSettleEvent);
//
//    /**
//     * 更新赛事上、下半场15分钟灰色区间的结算因子
//     * 1、 上半场的3个阶段 00:00 - 14:59，15:00 - 29:59，30:00 - 1HT,下半场的3个阶段  1HT - 59:59，60:00 - 74:59，75:00 - FT
//     * 任何一个阶段结算后，从新计算其他区间的结算因子
//     * @param
//     */
//    void updateMatchFifteenMinGraySettleFactor(Long standardMatchId,String settleNum);
//
//}
