package com.panda.merge.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.panda.merge.model.MatchDelaySettleInfo;
import com.panda.merge.respository.MatchDelaySettleInfoRepository;
import com.panda.merge.service.IMatchDelaySettleInfoService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class MatchDelaySettleInfoServiceImpl implements IMatchDelaySettleInfoService {
    @Resource
    private MatchDelaySettleInfoRepository matchDelaySettleInfoRepository;

    @Override
    public void saveOrUpdateBatch(List<MatchDelaySettleInfo> matchDelaySettleInfos) {
        if(CollectionUtils.isEmpty(matchDelaySettleInfos)){
            return;
        }
        matchDelaySettleInfoRepository.saveOrUpdateBatch(matchDelaySettleInfos);
    }

    @Override
    public void updateStatusByScoreIds(List<Long> scoreIds, Integer status) {
        if(CollectionUtils.isEmpty(scoreIds)){
            return;
        }
        LambdaUpdateWrapper<MatchDelaySettleInfo> chainWrapper = new LambdaUpdateWrapper<>();
        chainWrapper.set(MatchDelaySettleInfo::getSettleStatus, status).set(MatchDelaySettleInfo::getModifyTime, System.currentTimeMillis()).in(MatchDelaySettleInfo::getScoreId, scoreIds);
        matchDelaySettleInfoRepository.update(chainWrapper);
    }

    @Override
    public void updateStatusByCheckInfoIds(List<Long> checkInfoIds, Integer status) {
        if(CollectionUtils.isEmpty(checkInfoIds)){
            return;
        }
        LambdaUpdateWrapper<MatchDelaySettleInfo> chainWrapper = new LambdaUpdateWrapper<>();
        chainWrapper.set(MatchDelaySettleInfo::getSettleStatus, status).set(MatchDelaySettleInfo::getModifyTime, System.currentTimeMillis()).in(MatchDelaySettleInfo::getCheckInfoId, checkInfoIds);
        matchDelaySettleInfoRepository.update(chainWrapper);
    }
}
