package com.panda.merge.advertise.service;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.PDBasketBallPauseDto;
import com.panda.merge.model.ThirdMatchInfo;

public interface BasketBallAdvertiseService {
    boolean createMatchScoresInfo(ThirdMatchInfo thirdMatchInfo,String dataSourceCodeOld);
    Response matchStart(MatchScoreAndTimeVo matchScoreAndTimeVo,String linkedId);
    Response matchPause(MatchScoreAndTimeVo matchScoreAndTimeVo,String linkedId);
    Response matchPauseBasketball(MatchScoreAndTimeVo matchScoreAndTimeVo, PDBasketBallPauseDto dto);
    Response matchContinueBasketball(MatchScoreAndTimeVo matchScoreAndTimeVo,PDBasketBallPauseDto dto);
    Response matchContinue(MatchScoreAndTimeVo matchScoreAndTimeVo,String linkedId);
    Response matchEnd(MatchScoreAndTimeVo matchScoreAndTimeVo,String linkedId);
    Response match999End(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusDto changeMatchStatus);

    Response changeMatchScore(MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto);

    Response changeMatchPeriod(MatchScoreAndTimeVo data, Long periodId, String linkedId);

    Response buildBasketBallAdvertiseVo(MatchScoreAndTimeVo data);

    Response buildPDBasketBallAdvertiseVo(MatchScoreAndTimeVo data);

    Response buildPDAllScore(MatchScoreAndTimeVo data);
}
