package com.panda.merge.api;

import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;


/**
 * 紧急赛事状态,开售状态修改
 *
 * @author :  idol
 * @Date: 2020年11月29日16:10:45
 */
public interface IStandardStatusApi {
    /**
     * 紧急修改赛事状态
     *
     * @param standardId    标准赛事id
     * @param matchManageId 赛事管理id
     * @param status        需要更改的状态
     * @param periodId      赛事阶段
     * @return
     */
    Response updataMatchStatus(Long standardId, String matchManageId, Integer status, Long periodId);


    /**
     * 紧急修改赛事状态(含修改人)
     *
     * @param standardId    标准赛事id
     * @param matchManageId 赛事管理id
     * @param status        需要更改的状态
     * @param periodId      赛事阶段
     * @param updataUser    修改人
     * @return
     */
    Response updataMatchStatusByUpdataUser(Long standardId, String matchManageId, Integer status, Long periodId, String updataUser);


    /**
     * 紧急修改赛事开售状态
     *
     * @param standardId    标准赛事id
     * @param matchManageId 赛事管理id
     * @param status        需要更改的状态
     * @return
     */
    Response updataMatchSellStatus(Long standardId, String matchManageId, Integer status);


    /**
     * 三方赛事事件API
     *
     * @return
     */
    Response thirdMatchEventInfoApi(Request<MatchEventInfoDTO> request);

    /**
     * 三方赛事统计API
     *
     * @return
     */
    Response thirdMatchStatisticsInfoApi(Request<MatchStatisticsInfoDTO> request);

    /**
     * 修改3795需求事件缓存秒数
     * @return
     */
    @Deprecated
    Response updataCacheSeconds3795(Request<Integer> request);

    /** 手动更新-自研动画Z01各赛种事件缓存秒数*/
    Response setZ01AnimationEventCacheSeconds(Request<String> request);

    /**
     * 手动更新赛事分析相关表对应数据的修改时间，方便下游同步
     * */
    Response updataMatchAnalysisModifyTime(Request<String> request);

    /**
     * 紧急修改赛事状态
     *
     * @param plsStandardMatchId    pls赛事id
     * @param matchManageId 赛事管理id
     * @param status        需要更改的状态
     * @param periodId      赛事阶段
     * @return
     */
    Response updataMatchStatusPls(Long plsStandardMatchId, String matchManageId, Integer status, Long periodId);
}
