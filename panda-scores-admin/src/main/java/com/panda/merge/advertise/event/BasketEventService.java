package com.panda.merge.advertise.event;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.model.ThirdMatchInfo;

public interface BasketEventService {

    int addScoreChangeEvent(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, ChangeMatchScoreDto changeMatchScoreDto);

    void addScoreCorrectEvent(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, ChangeMatchScoreDto changeMatchScoreDto, String remark);
}
