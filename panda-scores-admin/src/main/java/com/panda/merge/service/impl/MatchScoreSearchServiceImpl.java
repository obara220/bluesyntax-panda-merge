package com.panda.merge.service.impl;


//import com.baomidou.dynamic.datasource.annotation.DS;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchScoresInfoExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
import com.panda.merge.service.IMatchScoreSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 赛事比分查询服务
 */
@Service
@Slf4j
public class MatchScoreSearchServiceImpl implements IMatchScoreSearchService {

    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;

    /**
     * 查询三方赛事
     * @param thirdMatchInfoExample
     * @return
     */
//    @DS("slave")
    @Override
    @Deprecated
    public ThirdMatchInfo searchThirdMatchInfoByExample(ThirdMatchInfoExample thirdMatchInfoExample) {
        List<ThirdMatchInfo> thirdMatchInfos =thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        if(thirdMatchInfos.size()!=0){
            return thirdMatchInfos.get(0);
        }
        return null;
    }

    /**
     * 查询三方赛事
     * @param thirdMatchId 三方赛事ID
     * @return
     */
    @Override
//    @DS("slave")
    public ThirdMatchInfo selectThirdMatchInfoByPrimaryKey(Long thirdMatchId) {
        return thirdMatchInfoMapper.selectByPrimaryKey(thirdMatchId);
    }

    /**
     * 查询赛事比分
     * @param example
     * @return
     */
    @Override
//    @DS("slave")
    public MatchScoresInfo selectScoresInfoByExample(MatchScoresInfoExample example) {
        List<MatchScoresInfo> list = matchScoresInfoMapper.selectByExample(example);
        if(list.size()!=0){
            return list.get(0);
        }
        return null;
    }

    public List<ThirdMatchInfo> searchAllThirdMatchInfoByExample(ThirdMatchInfoExample thirdMatchInfoExample) {
        List<ThirdMatchInfo> thirdMatchInfos =thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        return thirdMatchInfos;
    }
}
