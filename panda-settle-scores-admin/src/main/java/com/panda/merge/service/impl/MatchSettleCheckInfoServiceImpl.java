package com.panda.merge.service.impl;

import com.panda.merge.constant.converter.MatchSettleCheckInfoConverter;
import com.panda.merge.data.entity.MatchSettleCheckInfoEntity;
import com.panda.merge.model.MatchSettleCheckInfo;
import com.panda.merge.respository.MatchSettleCheckInfoRepository;
import com.panda.merge.service.IMatchSettleCheckInfoService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class MatchSettleCheckInfoServiceImpl implements IMatchSettleCheckInfoService {

    @Resource
    private MatchSettleCheckInfoConverter matchSettleCheckInfoConverter;

    @Resource
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;

    @Override
    public void saveOrUpdateBatch(List<MatchSettleCheckInfo> matchSettleCheckInfos) {
        if(CollectionUtils.isEmpty(matchSettleCheckInfos)){
            return;
        }

        List<MatchSettleCheckInfoEntity> checkInfoEntities = matchSettleCheckInfoConverter.convertCheckInfoToEntity(matchSettleCheckInfos);
        matchSettleCheckInfoRepository.saveOrUpdateBatch(checkInfoEntities);
    }
}
