package com.panda.merge.api;

import com.panda.merge.bo.*;
import com.panda.merge.bo.thirdmatch.ThirdMatchPromotionChartBO;
import com.panda.merge.bo.thirdmatch.ThirdMatchTeamSkillStatisticsBO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.dto.nonrealttime.query.QueryThirdSportTournamentDTO;
import com.panda.merge.dto.nonrealttime.query.ThirdMatchInfoDTO;

import java.util.List;

/**
 * 赛事分析，赛事级别数据同步
 * @author :  tell
 * @Date:     2020年9月9日16:10:45
 */
public interface IThirdMatchInfoQueryApi {
    /**
     *根据三方数据源赛事ID查询三方赛事信息
     * @param request
     * @return
     */
    @Deprecated
    Response<ThirdMatchInfoBO> queryThirdMatchInfoByThirdSourceId(Request<ThirdMatchInfoDTO> request);

    /**
     *获取三方联赛列表信息
     * @param request
     * @return
     */
    @Deprecated
    Response<List<ThirdSportTournamentBO>> queryThirdSportTournamentList(Request<QueryThirdSportTournamentDTO> request);

    /**
     * 三方赛事视频数据同步(暂时没用)
     * @param    request
     * @return   List<VideoAnimationBO>
     */
    @Deprecated
    Response<List<VideoAnimationBO>> queryThirdMatchVideoPage(Request<ThirdMatchInfoDTO> request);

    /**
     * 分页同步三方赛事历史对阵数据（根据修改时间同步）
     * */
    Response<PageModel<List<ThirdMatchHistoryStatisticsBO>>> queryThirdMatchHistoryStatisticsPage(Request<PageModel<StandardMatchInfoDTO>> request);

    /**
     * 分页同步三方赛事阵容数据（根据修改时间同步）
     * */
    Response<PageModel<List<ThirdMatchLineupBO>>> queryThirdMatchLineupPage(Request<PageModel<ThirdMatchInfoDTO>> request);

    /**
     * 分页同步三方赛事百家赔数据（根据修改时间同步）
     * */
    Response<PageModel<List<ThirdMatchHistoryOddsBO>>> queryThirdMatchHistoryOddsPage(Request<PageModel<ThirdMatchInfoDTO>> request);

    /**
     * 分页同步三方赛事伤停球员数据（根据修改时间同步）
     * */
    Response<PageModel<List<ThirdMatchSidelinedBO>>> queryThirdMatchSidelinedPage(Request<PageModel<ThirdMatchInfoDTO>> request);

    /**
     * 分页同步三方赛事比赛情报综合资讯数据（根据修改时间同步）
     * */
    Response<PageModel<List<ThirdMatchExInfomationBO>>> queryThirdMatchExInfomationPage(Request<PageModel<ThirdMatchInfoDTO>> request);


    /**
     * 分页同步三方赛事正面交手数据 （根据修改时间同步）
     * */
    Response<PageModel<List<ThirdMatchFrontStatisticsBO>>> queryThirdMatchFrontStatisticsPage(Request<PageModel<ThirdMatchInfoDTO>> request);


    /**
     * 分页同步三方赛事球队技术统计数据 （根据修改时间同步）
     * */
    Response<PageModel<List<ThirdMatchTeamSkillStatisticsBO>>> queryThirdMatchTeamSkillStatisticsPage(Request<PageModel<ThirdMatchInfoDTO>> request);

    /**
     * 分页同步三方杯赛淘汰赛事数据 （根据修改时间同步）
     * */
    Response<PageModel<List<ThirdMatchPromotionChartBO>>> queryThirdMatchPromotionChartPage(Request<PageModel<ThirdMatchInfoDTO>> request);
}
