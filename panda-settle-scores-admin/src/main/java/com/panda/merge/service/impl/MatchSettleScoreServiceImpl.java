package com.panda.merge.service.impl;

import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.respository.MatchSettleScoreRepository;
import com.panda.merge.service.IMatchSettleScoreService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MatchSettleScoreServiceImpl implements IMatchSettleScoreService {

    @Resource
    private MatchSettleScoreRepository matchSettleScoreRepository;

    @Override
    public void saveOrUpdateBatch(List<MatchSettleScore> matchSettleScore) {
        if(CollectionUtils.isEmpty(matchSettleScore)){
            return;
        }
        matchSettleScoreRepository.saveOrUpdateBatch(matchSettleScore);
    }

    @Override
    public void saveBatch(List<MatchSettleScore> matchSettleScore) {
        if(CollectionUtils.isEmpty(matchSettleScore)){
            return;
        }
        matchSettleScoreRepository.saveBatch(matchSettleScore);
    }

    @Override
    public List<MatchSettleScore> getByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        return matchSettleScoreRepository.listByIds(ids);
    }
}
