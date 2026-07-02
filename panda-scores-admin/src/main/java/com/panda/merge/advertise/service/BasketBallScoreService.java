package com.panda.merge.advertise.service;


import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.BasketballScoresPDDto;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresInfo;

public interface BasketBallScoreService {
    /**
     * 查询当前赛事的总比分以及阶段比分
     * */
    MatchScoreCommonVo searchCommonMatchScore(MatchScoresInfo matchScoresInfo,Long periodId);

    boolean checkScoreChangeDelete(MatchScoresInfo matchScoresInfo, ChangeMatchScoreDto changeMatchScoreDto);

    MatchScoreCommonVo countScore(MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto);
    MatchScoreCommonVo countScoreBasketball(MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto);

    int updateScore(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, ChangeMatchScoreDto changeMatchScoreDto);
    void updateScoreBasketball(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, Long period, Long startTimeSecond, String linkedId);

    void updateTime(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO);

    void updateTimePause(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO);

    boolean hasExtryPeriod(MatchScoresInfo matchScoresInfo);

    BasketballScoresPDDto changeScoreByHomeAwayAndEventCode(Response<MatchScoreAndTimeVo> response, String homeAway, String eventCode);

    BasketballScores changeScoreBySendBallDto(Response<MatchScoreAndTimeVo> response, PDBasketBallSendBallDto sendBallDto);

    BasketballScores getPeriodScore(Response<MatchScoreAndTimeVo> response);

    BasketballScores  changeJumpWonScore(Response<MatchScoreAndTimeVo> response, PDBaskectBallMatchStartDto pdBaskectBallMatchStartDto);

    MatchScoreCommonVo doDeleteEvent(Response<MatchScoreAndTimeVo> response, MatchScoresEventInfo matchScoresEventInfo, PDBasketBallDeleteEventDto pdBasketBallDeleteEventDto);

    void addPauseScore(Response<MatchScoreAndTimeVo> response, PDBasketBallPauseDto pdBasketBallPauseDto);

    MatchScoreCommonVo editEvent(Response<MatchScoreAndTimeVo> response, MatchScoresEventInfo matchScoresEventInfo, PDBasketBallEditEventDto editEventDto);

    int changeSixPeriodScore(  Response<MatchScoreAndTimeVo> response,PDBasketBallEditSixScoreDto editSixScoreDto);
}
