package com.panda.merge.advertise.service;


import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.KickOffDto;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportTeam;

public interface CommonAdvertiseService {
    Response<MatchScoreAndTimeVo> checkMatchScoreAndTimeCreate(Long thirdMatchId);

    /**
     * 滚球阶段数据校验与同步
     *
     * @param matchScoresInfo
     * @param timeInfo
     */
    void matchPeriodValid( String linkId, ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchTimeInfo timeInfo);

    Response<MatchScoreAndTimeVo> checkMatchScoreAndTimeCreateApi(Long thirdMatchId);
    Response changeMatchStartStatus(ThirdMatchInfo thirdMatchInfo, String linkId);

    Response changeFootballMatchStartStatus(ThirdMatchInfo thirdMatchInfo, KickOffDto kickOff);

    void updateMatchStatus(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO);

    ThirdSportTeam  getThirdSportTeamByThirdMatch(ThirdMatchInfo thirdMatchInfo,String homeAway);
    MatchScoreAndTimeVo searchMatchScoreAndTime(Long thirdMatchId) throws Exception;
}
