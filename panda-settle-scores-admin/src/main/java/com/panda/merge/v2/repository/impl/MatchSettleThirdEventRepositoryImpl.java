package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.model.MatchSettleThirdEvent;
import com.panda.merge.v2.converter.MatchSettleThirdEventConverter;
import com.panda.merge.v2.entity.MatchSettleThirdEventEntity;
import com.panda.merge.v2.entity.MatchSettleThirdScoreEntity;
import com.panda.merge.v2.mapper.MatchSettleThirdEventV2Mapper;
import com.panda.merge.v2.repository.MatchSettleThirdEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Repository("MatchSettleThirdEventRepositoryImplV2")
public class MatchSettleThirdEventRepositoryImpl extends ServiceImpl<MatchSettleThirdEventV2Mapper, MatchSettleThirdEventEntity> implements MatchSettleThirdEventRepository {
    @Autowired
    private MatchSettleThirdEventConverter matchSettleThirdEventConverter;
    @Override
    public boolean updateById(MatchSettleThirdEvent matchSettleThirdEvent) {
        MatchSettleThirdEventEntity entity = matchSettleThirdEventConverter.convertSettleThirdEventToEntity(matchSettleThirdEvent);
        return super.updateById(entity);
    }

    @Override
    public boolean save(MatchSettleThirdEvent matchSettleThirdEvent) {
        MatchSettleThirdEventEntity entity = matchSettleThirdEventConverter.convertSettleThirdEventToEntity(matchSettleThirdEvent);
        return super.save(entity);
    }

    @Override
    public MatchSettleThirdEvent getById(Long id) {
        MatchSettleThirdEventEntity entity = super.getById(id);
        return matchSettleThirdEventConverter.convertEntityToSettleThirdEvent(entity);
    }

    @Override
    public List<MatchSettleThirdEvent> getModelByItemsOrderBySettleNum(Long standardMatchId, List<String> eventCodes, List<Long> periodIds, Integer eventType, Long thirdEventSourceId) {
        LambdaQueryWrapper<MatchSettleThirdEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId!=null,MatchSettleThirdEventEntity::getStandardMatchId, standardMatchId)
                .eq(eventType!=null,MatchSettleThirdEventEntity::getEventType, eventType)
                .eq(thirdEventSourceId!=null,MatchSettleThirdEventEntity::getThirdEventSourceId, thirdEventSourceId)
                .in(CollectionUtils.isNotEmpty(eventCodes),MatchSettleThirdEventEntity::getEventCode,eventCodes)
                .in(CollectionUtils.isNotEmpty(periodIds),MatchSettleThirdEventEntity::getPeriodId,periodIds)
                .orderByDesc(Arrays.asList(MatchSettleThirdEventEntity::getSettleNum,MatchSettleThirdEventEntity::getEventOrder));
        List<MatchSettleThirdEventEntity> entities = this.list(queryWrapper);
        return matchSettleThirdEventConverter.convertEntityToSettleThirdEvent(entities);
    }
}
