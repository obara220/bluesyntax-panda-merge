package com.panda.merge.service.impl;

import com.panda.merge.model.MatchSettleRollBackInfo;
import com.panda.merge.respository.MatchSettleRollBackInfoBatchRepository;
import com.panda.merge.service.IMatchSettleRollBackInfoService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MatchSettleRollBackInfoServiceImpl implements IMatchSettleRollBackInfoService {

    @Resource
    private MatchSettleRollBackInfoBatchRepository matchSettleRollBackInfoBatchRepository;

    @Override
    public void saveOrUpdateBatch(List<MatchSettleRollBackInfo> matchSettleRollBackInfos) {
        if(CollectionUtils.isEmpty(matchSettleRollBackInfos)){
            return;
        }
        matchSettleRollBackInfoBatchRepository.saveOrUpdateBatch(matchSettleRollBackInfos);
    }

    @Override
    public List<MatchSettleRollBackInfo> getByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        return matchSettleRollBackInfoBatchRepository.listByIds(ids);
    }
}
