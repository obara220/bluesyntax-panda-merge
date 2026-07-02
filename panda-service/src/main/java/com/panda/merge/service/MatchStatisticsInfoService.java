package com.panda.merge.service;

import com.panda.merge.model.MatchStatisticsInfo;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/10 <br>
 * @see com.panda.merge.service <br>
 */
public interface MatchStatisticsInfoService {
    MatchStatisticsInfo saveOrUpdate(MatchStatisticsInfo matchStatisticsInfo, String linkId);

    MatchStatisticsInfo getItem(String thirdSourceMatchId, String dataSourceCode, String linkId);
}
