package com.panda.merge.api;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeBusinessEventScoresDto;
import com.panda.merge.dto.advertise.EventOperationDto;
import com.panda.merge.dto.advertise.NewlyMatchEventQuery;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.merge.dto.scores.*;
import com.panda.merge.model.SportScoreShowStatus;

import java.util.List;

/**
 *  比分数据中心的对外api
 *  kb
 *  2020-11-26
 * */
public interface IScoresCenterApi {

    /**
     *  三方比分查询接口
     * 传参：
     * 返回:
     * */
    List<JSONObject> searchListMatchScores(List<MatchScoresRequestDTO> thirdMatchIds);
    /**
     *  三方时间查询
     * 传参：
     * 返回:
     * */
    List<JSONObject> searchListMatchTime(List<MatchScoresRequestDTO> thirdMatchIds);

    /**
     *  手工创建危险/安全事件
     * 传参：
     * 返回:
     * */
    Response<MatchEventInfoMessage> scoresEventOperate(EventOperationDto eventOperationDto);

    /**
     *  手动下发var事件
     * 传参：
     * 返回:
     *
     * @return*/
    Response<MatchEventInfoMessage> addVarEvent(EventOperationDto eventOperationDto);


    /**
     *  拒单事件 reject_event 人为触发的拒单事件
     * 传参：
     * 返回:
     *
     * @return*/
    Response<MatchEventInfoMessage> addRejectEvent(EventOperationDto eventOperationDto);
    /**
     * 提供操盘查询是否有新事件推入
     * @param query
     * @return
     */
    Response<Boolean> queryNewlyMatchEvent(NewlyMatchEventQuery query);

    /**
     * 切换事件源立即下发 暂时不用
     * */
    Response  changeBusinessEventScores(ChangeBusinessEventScoresDto eventSendScoresDto);

    /**
     * 查询B02比分通道
     * @param query
     * @return
     */
    Response queryScoresSource(B02ScoresSourceDTO query);

    /**
     * 切换B02比分通道-非足篮
     * @param data
     * @return
     */
    Response changeScoresSource(B02ScoresSourceDTO data);


    /**
     * 查询标准比分和数据源比分
     * @param standardMatchId
     * @param sportId
     * @return
     */
    Response queryMatchScores(Long standardMatchId,Long sportId);


    /**
     * 修改标准比分
     * @param scores
     * @return
     */
    Response editStandScores(StandardScoreCenter scores);
    /**
     * 修改数据源联动开关
     * @param switchDTO
     * @return
     */
    Response editAccoSwitch(StandardMatchSwitchDTO switchDTO);
    /**
     * 查询比分中心设置
     * @param
     * @return
     */
    Response queryScoreCenterPage();

    /**
     * 查询比分中心设置
     * @param
     * @return
     */
    Response editScoreCenterSettle(SportScoreShowStatus sportScoreShowStatus);

    /**
     * 全部显示/隐藏
     * @return
     */
    Response setSportResuleShowStatus(ScoresCenterDTO scoresCenter);

    Response injuryTimeEventOperate(EventOperationDto isDangerDto);
    /**
     * 查询主数据源比分
     * @param matchIds
     * @return
     */
    public Response searchMatchScores(List<Long> matchIds);

    /**
     * 手动创建tMax事件
     * @param isDangerDto 操作内容
     * @return
     */
    Response operateTmaxEvent(EventOperationDto isDangerDto);

    /**
     * 标准比分下发到结算2.0
     * 中断下发(或常规下发)
     * @param scoreCenter 赛事信息
     * @return
     */
    Response sendToSettlement(StandardScoreCenter scoreCenter);

    /**
     * 风控查询比分
     * @param request 赛事信息
     * @return
     */
    Response rcsQueryMatchScoresByMatchIds(Request<QueryMatchScoresParamDTO> request);

    /**
     * 区间比分是否校验
     * @param checkSwitch
     * @return
     */
    Response checkMinScores(ScoresCenterCheckSwitchDTO checkSwitch);

    /**
     * 区间比分是否校验
     * @param
     * @return
     */
    Response updateScoreShowStatus(StandardScoreCenterDTO scoreCenterDto);

}
