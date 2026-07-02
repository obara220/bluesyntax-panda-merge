package com.panda.merge.service.impl;

import com.panda.merge.constant.converter.MatchSettleEventConverter;
import com.panda.merge.data.entity.MatchSettleEventEntity;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.respository.MatchSettleEventRepository;
import com.panda.merge.service.IMatchSettleEventService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MatchSettleEventServiceImpl implements IMatchSettleEventService {

    @Resource
    private MatchSettleEventConverter matchSettleEventConverter;

    @Resource
    private MatchSettleEventRepository matchSettleEventRepository;

    @Override
    public void saveOrUpdateBatch(List<MatchSettleEvent> matchSettleEvents) {
        if(CollectionUtils.isEmpty(matchSettleEvents)){
            return;
        }

        List<MatchSettleEventEntity> eventEntities = matchSettleEventConverter.convertMatchSettleEventToEntity(matchSettleEvents);
        matchSettleEventRepository.saveOrUpdateBatch(eventEntities);
    }

    @Override
    public List<MatchSettleEvent> getByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        List<MatchSettleEventEntity> eventEntities =  matchSettleEventRepository.listByIds(ids);
        return matchSettleEventConverter.convertMatchSettleEventEntityToEvent(eventEntities);
    }
}
