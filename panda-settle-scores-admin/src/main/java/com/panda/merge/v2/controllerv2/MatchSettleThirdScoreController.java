package com.panda.merge.v2.controllerv2;

import com.panda.merge.dto.settle.MatchSettleScoreSearchDto;
import com.panda.merge.dto.settle.ThirdMatchSettleScoresDto;
import com.panda.merge.v2.service.IMatchSettleThirdScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class MatchSettleThirdScoreController {

    @Autowired
    IMatchSettleThirdScoreService matchSettleThirdScoreService;

    public ThirdMatchSettleScoresDto searchThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleThirdScoreService.searchThirdMatchSettleScores(settleScoreSearchDto);
    }
}
