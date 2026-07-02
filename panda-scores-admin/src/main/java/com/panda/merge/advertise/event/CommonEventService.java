package com.panda.merge.advertise.event;


import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.dto.advertise.ChangeMatchTimeDto;
import com.panda.merge.dto.advertise.PDBasketBallPauseDto;
import com.panda.merge.dto.advertise.PDBasketBallSendEventDto;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;


public interface CommonEventService {
    /**
     * 时间修正事件下发 isGo  1  go   0  stop
     * */
     boolean updateMatchTimeEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, Integer isGo, String linkedId);

    boolean updateBasketballMatchTimeEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, Integer isGo, ChangeMatchTimeDto dto);

    /**
     * 暂停/继续 isGo  1  go   0  stop
     */
    boolean updateBasketballPauseAndContinue(MatchScoreAndTimeVo data, PDBasketBallPauseDto dto);

    /**
     * 足球 时间修正事件下发 isGo  1  go   0  stop
     * */
    boolean updateFootballMatchTimeEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, Integer isGo, String linkedId, Integer controlType);

    /**
     * 篮球进攻相关事件
     *
     * @param data         数据
     * @param sendEventDto 入参
     * @return true/false
     */
//    boolean updateBasketballEvent(MatchScoreAndTimeVo data, PDBasketBallSendEventDto sendEventDto);
     /**
      * 下发阶段变更事件
      * */
     boolean changeMatchPeriodEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, MatchScoreCommonVo matchScoreCommonVo, String linkedId,String userName);

    /**
     * 1.上半场期间44分钟后的修改时间，修改回44分钟以前
     * 2.下半场期间，89分钟后，修改回89分钟前
     * 下发赔率服务
     *
     * @param changeMatchTimeDto 更改时间
     * @param timeInfo           赛事时间信息
     * @param thirdMatchInfo     三方赛事信息
     */
    void sendChangeMatchTimeInfo(ChangeMatchTimeDto changeMatchTimeDto, MatchTimeInfo timeInfo, ThirdMatchInfo thirdMatchInfo);

    /**
     * 下发阶段变更事件
     * */
    boolean changeFootBallMatchPeriodEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, MatchScoreCommonVo matchScoreCommonVo, String linkedId,String userName);


    void updateMatchEventStatus(Long thirdMatchId, String possibleEventCode,String homeAway,String penaltyGoal);

    void setDangerOrSafe(Boolean isDanger,Long thirdMatchId);
}
