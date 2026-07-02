package com.panda.merge.service.impl;

import com.panda.merge.model.MatchSettleThirdScore;
import com.panda.merge.respository.MatchSettleThirdScoreRepository;
import com.panda.merge.service.IMatchSettleThirdScoreService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class MatchSettleThirdScoreServiceImpl implements IMatchSettleThirdScoreService {

    @Resource
    private MatchSettleThirdScoreRepository matchSettleThirdScoreRepository;

    @Override
    public void saveOrUpdateBatch(List<MatchSettleThirdScore> matchSettleThirdScore) {
        if(CollectionUtils.isEmpty(matchSettleThirdScore)){
            return;
        }
        matchSettleThirdScoreRepository.saveOrUpdateBatch(matchSettleThirdScore);
    }
}
