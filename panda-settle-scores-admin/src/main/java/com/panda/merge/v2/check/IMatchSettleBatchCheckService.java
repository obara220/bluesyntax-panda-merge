package com.panda.merge.v2.check;

import com.panda.merge.dto.CheckIsGreyDto;
import com.panda.merge.model.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface IMatchSettleBatchCheckService {
    /**
     *      核对比分
     */
    boolean batchCheckMatchThirdSettleScores(List<MatchSettleThirdScore> matchSettleThirdScores ,String linkedId,Long second, CheckIsGreyDto checkIsGreyDto);
    /**
     *      事件核对
     */
    boolean checkMatchThirdSettleEvent(MatchSettleThirdEvent matchSettleThirdEvent,String linkedId,Long periodSeconds);
    Map<Long, Pair<Boolean, Boolean>> batchCheckCommonMatchSettleScoreEvent(List<Pair<Object, MatchSettleCheckInfo>> checkMessages, boolean createCheck, String linkedId, MatchSettleTemplate downTemplate);
    /**
     * 新增赛事限制操作用户群
     * */
    boolean lockUserListByCheckPass(Long standardMatchId, List<String> userNameList);

    void batchSendCheckMessage(List<Pair<Object, MatchSettleCheckInfo>> checkMessages,boolean createCheck, String linkedId);

     boolean checkIfNotSend(MatchSettleCheckInfo matchSettleCheckInfo);

    //进球事件多数据商确认主逻辑
    void confirmGoalDoFilter(List<MatchEventInfo> data);
    //更新当前进球事件状态

    void canceledCheckMatchThirdSettleEvent(MatchSettleThirdEvent matchSettleEvent, MatchEventInfo data, Integer order);

    void changeHomeAway(List<MatchEventInfo> list);

     MatchEventInfo getOldMatchInfoByCancel(MatchEventInfo data);
     
    /**
     * 过滤5/15分钟阶段：过滤掉有数据不匹配或删除事件的阶段
     * @param standardMatchId 标准赛事ID
     * @param dataSourceCode 数据源编码（可为null）
     * @param matchSettleScores 待过滤的MatchSettleScore列表
     * @param linkedId 链路ID
     * @return 过滤后的MatchSettleScore列表
     */
    List<MatchSettleScore> filterUnsettled5Or15MinPeriods(Long standardMatchId, String dataSourceCode, 
                                                          List<MatchSettleScore> matchSettleScores, String linkedId);
}
