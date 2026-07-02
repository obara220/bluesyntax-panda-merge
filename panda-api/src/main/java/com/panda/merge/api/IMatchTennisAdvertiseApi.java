package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;


/**
 * KB
 * 网球报球版的dubbo接口
 * */
public interface IMatchTennisAdvertiseApi {


    /**
     * 网球开始
     * Integer type:
     * 返回状态
     * */
    Response matchBegin(TennisAdvertiseDto tennisAdvertiseDto);

    /**
     * 比赛结束
     * */
    Response matchEnd(TennisAdvertiseDto tennisAdvertiseDto);
    /**
     * 赛事状态恢复
     * */
    Response matchStatusReSet(TennisAdvertiseDto tennisAdvertiseDto);


    /**
     * 赛事详情查询接口
     * 返回赛事信息
     * */
    Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto);


    /**
     *  网球报球网球跳分接口
     * */
    Response setMatchSecondScore(TennisEditSecondScoreDto tennisEditSecondScoreDto);

    /**
     * 局制设置
     * */
    Response setMatchLength(PDMatchLengthEditDto pdMatchLengthEditDto);

    /**
     * 赛制设置
     * */
    Response setRoundType(PDRoundTypeEditDto pdRoundTypeEditDto);
    /**
     * 盘切换按钮 这里可能不会处理
     * */
    Response setFirstNum(PDFirstNumSetDto pdFirstNumSetDto);
    /**
     * 盘开始，盘结束
     * */
    Response changeSetStatus(PDTennisSetStatusDto pdTennisSetStatusDto);

    /**
     * 局开始，局结束
     * */
    Response changeRoundStatus(PDTennisRoundStatusDto pdTennisRoundStatusDto);
    /**
     * 日志查询
     * */
    Response searchOperatorDetail(MatchAdvertiseQueryDto matchAdvertiseQueryDto);
    /**
     * 设置盘最大局数
     * */
    Response setMaxRound(MatchTennisEditMaxRoundDto matchTennisEditMaxRoundDto);
    /**
     *
     * 直接编辑盘比分
     * */
    Response setSetScore(MatchTennisEditSetScoreDto matchAdvertiseQueryDto);
    /**
     * 重新计算盘比分
     * */
    Response reCountSetScore(MatchTennisReSetScoreDto tennisReSetScoreDto);

    /**
     * 查询事件列表
     * @param eventListDto
     * @return
     */
    Response eventList(EventListDto eventListDto);

    /**
     * 设置每一局率先开球员
     * @param tennisAdvertiseDto
     * @return
     */
    Response setMatchOpenBallPlayer(TennisEditSecondScoreDto tennisAdvertiseDto);
}
