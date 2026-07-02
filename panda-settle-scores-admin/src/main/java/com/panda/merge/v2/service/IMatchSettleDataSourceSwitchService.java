package com.panda.merge.v2.service;

import java.util.List;
import java.util.Map;

public interface IMatchSettleDataSourceSwitchService {

    Map<String, Integer> getTournamentLevelStatuses(Long standardMatchId, String dataSourceCode, List<String> eventCodes);
}
