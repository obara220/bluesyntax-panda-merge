package com.panda.merge.service;

import com.panda.merge.dto.CommonThirdScoresDto;
import com.panda.merge.dto.LimitSwitchDto;
import com.panda.merge.dto.Request;
import com.panda.merge.model.MatchSettleInfo;
import com.panda.merge.model.MatchSettleScore;

import java.util.List;

/**
 * 单数据商自动结算流程
 */
public interface IBasketballInSettleService {
    //单个结算逻辑
    void settleInScoreBySingleDataSource(Request<CommonThirdScoresDto> request, Integer settleSumScore, Integer cacheSumScore, MatchSettleInfo matchSettleInfo);

    void cleanBasketInSettleCacheScore(String standardMatchId);

    void closeInAutoSettleBySoldMsgChange(Long matchId, String dataSourceCode);

    List<LimitSwitchDto> getBasketInSettleTimeLimit(Long sportId);

    boolean rollBackSettleInScore(MatchSettleScore matchSettleScore);

    boolean getRealtimeSwitchOfLevel(Long sportId, Long standardTournamentId);

    boolean checkRealtimeAndPSwitch(Long sportId, Long standardMatchId, Long standardTournamentId);

}
