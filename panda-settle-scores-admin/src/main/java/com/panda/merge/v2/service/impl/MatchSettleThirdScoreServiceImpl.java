package com.panda.merge.v2.service.impl;

import com.panda.merge.dto.settle.MatchSettleScoreSearchDto;
import com.panda.merge.dto.settle.ThirdMatchSettleScoresDto;
import com.panda.merge.v2.repository.MatchSettleThirdScoreV2Repository;
import com.panda.merge.v2.service.IMatchSettleThirdScoreService;
import com.panda.merge.v2.service.assemble.MatchSettleThirdScoreAssemble;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("MatchSettleThirdScoreServiceImpl")
public class MatchSettleThirdScoreServiceImpl implements IMatchSettleThirdScoreService {
    @Autowired
    private MatchSettleThirdScoreV2Repository matchSettleThirdScoreRepository;

    @Autowired
    private MatchSettleThirdScoreAssemble matchSettleThirdScoreAssemble;


    @Override
    public ThirdMatchSettleScoresDto searchThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleThirdScoreAssemble.searchBasketballThirdMatchSettleScores(settleScoreSearchDto);
    }
}
