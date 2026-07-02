package com.panda.merge.service;


import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresInfoExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;

import java.util.List;

public interface IMatchScoreSearchService {
    /**
     * 查询三方赛事
     * @param thirdMatchInfoExample
     * @return
     */
    ThirdMatchInfo searchThirdMatchInfoByExample(ThirdMatchInfoExample thirdMatchInfoExample);

    /**
     * 查询三方赛事
     * @param thirdMatchId
     * @return
     */
    ThirdMatchInfo selectThirdMatchInfoByPrimaryKey(Long thirdMatchId);

    /**
     * 查询赛事比分
     * @param example
     * @return
     */
    MatchScoresInfo selectScoresInfoByExample(MatchScoresInfoExample example);

    public List<ThirdMatchInfo> searchAllThirdMatchInfoByExample(ThirdMatchInfoExample thirdMatchInfoExample);

}
