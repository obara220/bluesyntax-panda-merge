package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;

/**
 * KB
 * 报球版的dubbo接口
 * */
public interface IMatchBasketBallAdvertiseApi {

    /**
     * 构建PA赛事比分以及事件配置信息接口
     * */
    Response createMatchAdvertise(CreatePDAdvertiseDto createPDAdvertiseDto);

    /**
     * 赛制设定接口
     * 返回赛制
     * */
    Response changeMatchLenth(ChangeMatchLengthDto changeMatchLengthDto);

    /**
     * 修改开赛时间
     * 返回最新开赛时间
     * */
    Response changeMatchStartTime (ChangeMatchStartTimeDto changeMatchStartTimeDto);

    /**
     * 修改倒计时
     * 返回倒计时
     * */
    Response changeMatchTime(ChangeMatchTimeDto changeMatchTimeDto);

    /**
     * 暂停/继续/结束/开始
     * Integer type:  1 开始  2 暂停  3 继续 4  阶段结束
     * 返回状态
     * */
    Response changeMatchStatus(ChangeMatchStatusDto changeMatchStatus);

    /**
     * 赛事阶段的变更
     * 返回状态
     * */
    Response changeMatchPeriod(ChangeMatchPeriodDto changeMatchPeriodDto);

    /**
     * 比赛结束
     * */
    Response setMatchEnd(ChangeMatchStatusDto changeMatchStatus);

    /**
     * 修改比分
     * 返回比分
     * */
    Response changeMatchScore(ChangeMatchScoreDto changeMatchScoreDto);

    /**
     * 赛事详情查询接口
     * 返回赛事信息
     * */
    Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto);

    /**
     * 篮球报球版事件消息查询接口
     * @param matchAdvertiseQueryDto
     * @return
     */
    Response getMatchEventMessage(MatchAdvertiseQueryDto matchAdvertiseQueryDto);


//    /**
//     * 赛事列表查询
//     * 返回赛事信息
//     * */
//    Response getMatchAdvertiseList(MatchAdvertiseQueryDto matchAdvertiseQueryDto);
}
