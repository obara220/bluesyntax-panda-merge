package com.panda.merge.api;

import com.panda.merge.bo.ThirdMatchHistoryExpressionBO;
import com.panda.merge.bo.ThirdMatchSeasonStatisticsBO;
import com.panda.merge.bo.ThirdSportPlayerRankingBO;
import com.panda.merge.bo.ThirdSportTeamRankingBO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.nonrealttime.query.QueryThirdRankingInfoDTO;

import java.util.List;

/**
 * 赛事分析，联赛级别数据同步
 * @author    tell
 * @since     2020年10月18日09:54:24
 */
public interface IThirdRankingInfoQueryApi {

    /**
     * 三方联赛球队榜单数据同步
     * @param request
     * @return
     */
    Response<PageModel<List<ThirdSportTeamRankingBO>>> queryThirdSportTeamRanking(Request<PageModel<QueryThirdRankingInfoDTO>> request);

    /**
     * 三方联赛球员榜单数据同步
     * @param request
     * @return
     */
    Response<PageModel<List<ThirdSportPlayerRankingBO>>> queryThirdSportPlayerRanking(Request<PageModel<QueryThirdRankingInfoDTO>> request);


    /**
     * 三方联赛球队历史表现数据同步
     * @param request
     * @return
     */
    Response<PageModel<List<ThirdMatchHistoryExpressionBO>>> queryThirdMatchHistoryExpression(Request<PageModel<QueryThirdRankingInfoDTO>> request);



    /**
     * 三方联赛赛季统计数据同步
     * @param request
     * @return
     */
    Response<PageModel<List<ThirdMatchSeasonStatisticsBO>>> queryThirdMatchSeasonStatistics(Request<PageModel<QueryThirdRankingInfoDTO>> request);


}
