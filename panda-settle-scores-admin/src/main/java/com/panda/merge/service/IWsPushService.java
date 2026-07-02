package com.panda.merge.service;

import com.panda.merge.dto.settle.AutoSettleDataSourceDto;
import com.panda.merge.dto.settle.MatchListSettleDto;
import com.panda.merge.model.MatchSettleRollBackInfo;

/**
 * WS推送功能
 * */
public interface IWsPushService {
    /**
     * 推送标准比分
     * */
    void pushStandardSettleScores(Long standardMatchId,String eventCode);

     void pushBasketballStandardSettleScores(Long standardMatchId, String eventCode);
    /**
     * 推送三方比分
     * */
    void pushThirdSettleScores(Long standardMatchId,String eventCode );

     void pushThirdBasketballSettleScores(Long standardMatchId);

    /**
     * 推送标准事件
     * */
    /**
     * 推送标准比分
     * */
    void pushStandardSettleEvent(Long standardMatchId,String eventCode);
    /**
     * 推送三方事件
     * */
    void pushThirdSettleEvent(Long standardMatchId,String eventCode );

    /**
     * 推送结算赛事列表
     * @param matchListSettleDto
     */
    void pushSettleMatchList(MatchListSettleDto matchListSettleDto);

    /**
     * 推送数据商自动结算开关状态
     * @param dto
     */
    void pushGlobalAutoSettleStatus(AutoSettleDataSourceDto dto);

    /**
     * 推送赛事回滚状态
     * @param info
     */
    void pushMatchSettleRollBackStatus(MatchSettleRollBackInfo info);
}
