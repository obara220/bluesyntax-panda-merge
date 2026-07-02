package com.panda.merge.service;

import java.util.List;
import java.util.Map;

public interface MatchSettleDataSourceConfigService {
    /**
     * 根据标准赛事Id,联赛等级查询出结算数据源列表
     * @param standardMatchId
     * @return
     */
    List<String> getTournamentLevelDataSources(Long standardMatchId);

    /**
     * 根据标准赛事Id,数据源名称,查询联赛等级对应的结算数据源的开关状态
     * @param standardMatchId
     * @param dataSourceCode
     * @return
     */
    Integer getTournamentLevelStatus(Long standardMatchId,String dataSourceCode,String eventCode);

    Map<String, Integer> getTournamentLevelStatuses(Long standardMatchId, String dataSourceCode, List<String> eventCodes);

}
