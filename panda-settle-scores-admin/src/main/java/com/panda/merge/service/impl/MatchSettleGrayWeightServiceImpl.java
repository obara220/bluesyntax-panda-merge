package com.panda.merge.service.impl;

import com.panda.merge.model.MatchSettleGrayWeight;
import com.panda.merge.respository.MatchSettleGrayWeightRepository;
import com.panda.merge.service.IMatchSettleGrayWeightService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class MatchSettleGrayWeightServiceImpl implements IMatchSettleGrayWeightService {

    @Resource
    private MatchSettleGrayWeightRepository matchSettleGrayWeightRepository;


    @Override
    public void saveOrUpdateBatch(List<MatchSettleGrayWeight> matchSettleGrayWeights) {
        if (CollectionUtils.isEmpty(matchSettleGrayWeights)) {
            return;
        }
        matchSettleGrayWeightRepository.saveOrUpdateBatch(matchSettleGrayWeights);
    }

    @Override
    public List<MatchSettleGrayWeight> getByIds(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return matchSettleGrayWeightRepository.listByIds(ids);
    }


}
