package com.panda.merge.v2.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleEventExample;
import com.panda.merge.v2.converter.MatchSettleEventV2Converter;
import com.panda.merge.v2.entity.MatchSettleEventEntity;
import com.panda.merge.v2.mapper.MatchSettleEventV3Mapper;
import com.panda.merge.v2.repository.MatchSettleEventV2Repository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
public class MatchSettleEventRepositoryImpl extends ServiceImpl<MatchSettleEventV3Mapper, MatchSettleEventEntity> implements MatchSettleEventV2Repository {

    @Autowired
    private MatchSettleEventV3Mapper matchSettleEventV3Mapper;
    @Autowired
    private MatchSettleEventV2Converter matchSettleEventV2Converter;

    @Override
    public boolean save(MatchSettleEvent matchSettleEvent) {
        MatchSettleEventEntity entity = matchSettleEventV2Converter.convertMatchSettleEventToEntity(matchSettleEvent);
        return super.save(entity);
    }

    @Override
    public boolean updateById(MatchSettleEvent matchSettleEvent) {
        MatchSettleEventEntity entity = matchSettleEventV2Converter.convertMatchSettleEventToEntity(matchSettleEvent);
        return super.updateById(entity);
    }

    @Override
    public void saveOrUpdateBatch(List<MatchSettleEvent> matchSettleEvents) {
        if(CollectionUtils.isEmpty(matchSettleEvents)){
            return;
        }
        List<MatchSettleEventEntity> eventEntities = matchSettleEventV2Converter.convertMatchSettleEventToEntity(matchSettleEvents);
        super.saveOrUpdateBatch(eventEntities);
    }

    @Override
    public MatchSettleEvent getById(Long id) {
        MatchSettleEventEntity entity = super.getById(id);
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entity);
    }

    @Override
    public List<MatchSettleEvent> getByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        List<MatchSettleEventEntity> eventEntities = super.listByIds(ids);
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(eventEntities);
    }

    /**
     *根据status进行过滤--开始
     */

    @Override
    public List<MatchSettleEventEntity> getByStandardMatchIdAndStatus(Long standardMatchId, Integer status) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(status != null,MatchSettleEventEntity::getStatus,status);
        return this.list(queryWrapper);
    }

    @Override
    public List<MatchSettleEvent> getModelByStandardMatchIdAndNotStatus(Long standardMatchId, Integer status) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .ne(status != null,MatchSettleEventEntity::getStatus,status);
        List<MatchSettleEventEntity> entities = this.list(queryWrapper);
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entities);
    }

    @Override
    public List<MatchSettleEvent> getModelByStandardMatchIdAndNotStatusAndEventTypeAndIsGrey(Long standardMatchId, Integer status, Integer eventType, Integer isGray) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(eventType != null, MatchSettleEventEntity::getEventType, eventType)
                .eq(isGray != null, MatchSettleEventEntity::getIsGrey, isGray)
                .ne(status != null, MatchSettleEventEntity::getStatus,status);
        List<MatchSettleEventEntity> entities = this.list(queryWrapper);
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entities);
    }

    /**
     *根据settleNum进行过滤--开始
     */

    @Override
    public List<MatchSettleEvent> getModelByStandardMatchIdAndSettleNums(Long standardMatchId, List<String> settleNums) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(settleNums),MatchSettleEventEntity::getSettleNum,settleNums);
        List<MatchSettleEventEntity> entities = this.list(queryWrapper);
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entities);
    }

    @Override
    public List<MatchSettleEventEntity> getByStandardMatchIdAndSportIdAndSettleNum(Long matchId, Integer sportId, List<String> settleNumList) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(matchId != null, MatchSettleEventEntity::getStandardMatchId, matchId)
                .in(CollectionUtils.isNotEmpty(settleNumList), MatchSettleEventEntity::getSettleNum, settleNumList)
                .eq(sportId != null,MatchSettleEventEntity::getSportId,sportId);
        return this.list(queryWrapper);
    }

    @Override
    public List<MatchSettleEvent> getByMatchIdAndSettleNumAndEventOrderAndPeriodId(Long standardMatchId, String settleNum, Integer eventOrder, Long periodId) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(StringUtils.isNotBlank(settleNum), MatchSettleEventEntity::getSettleNum, settleNum)
                .eq(eventOrder!=null,MatchSettleEventEntity::getEventOrder,eventOrder)
                .eq(periodId!=null,MatchSettleEventEntity::getPeriodId,periodId);
        List<MatchSettleEventEntity> entities = this.list(queryWrapper);
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entities);
    }

    /**
     *根据eventCodes进行过滤--开始
     */

    @Override
    public List<MatchSettleEvent> getModelsByItems(Long standardMatchId, List<String> eventCodes, List<Long> periods, Integer status, List<String> homeAway) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .in( CollectionUtils.isNotEmpty(eventCodes), MatchSettleEventEntity::getEventCode, eventCodes)
                .in(periods != null, MatchSettleEventEntity::getPeriodId, periods)
                .eq(status != null, MatchSettleEventEntity::getStatus, status)
                .in(CollectionUtils.isNotEmpty(homeAway), MatchSettleEventEntity::getHomeAway, homeAway);
        List<MatchSettleEventEntity> entities = this.list(queryWrapper);
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entities);
    }

    @Override
    public List<MatchSettleEvent> getModelsByItemsAndOrderBySettleNumAndEventOrder(Long standardMatchId, List<String> eventCodes, List<Long> periods, List<Integer> eventTypes) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(eventCodes), MatchSettleEventEntity::getEventCode, eventCodes)
                .in(CollectionUtils.isNotEmpty(periods), MatchSettleEventEntity::getPeriodId, periods)
                .in(CollectionUtils.isNotEmpty(eventTypes), MatchSettleEventEntity::getEventType, eventTypes)
                .orderByDesc(Arrays.asList(MatchSettleEventEntity::getSettleNum, MatchSettleEventEntity::getEventOrder));
        List<MatchSettleEventEntity> entities = this.list(queryWrapper);
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entities);
    }

    @Override
    public List<MatchSettleEvent> getModelsByItemsAndEventType(Long standardMatchId, List<String> eventCodes, List<Long> periods, Integer status, Integer eventType) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(eventCodes), MatchSettleEventEntity::getEventCode, eventCodes)
                .in(periods != null, MatchSettleEventEntity::getPeriodId, periods)
                .eq(status != null, MatchSettleEventEntity::getStatus, status)
                .eq(eventType != null, MatchSettleEventEntity::getEventType, eventType);
        List<MatchSettleEventEntity> entities = this.list(queryWrapper);
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entities);
    }

    @Override
    public List<MatchSettleEventEntity> getByEventCodeAndPeriodIdAndStatusAndStandardMatchIdAndHomeAway(List<String> eventCodes, List<Long> periods, Integer status, Long standardMatchId, List<String> homeAway) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .in( CollectionUtils.isNotEmpty(eventCodes), MatchSettleEventEntity::getEventCode, eventCodes)
                .in(CollectionUtils.isNotEmpty(periods), MatchSettleEventEntity::getPeriodId, periods)
                .eq(status != null,MatchSettleEventEntity::getStatus,status)
                .in(CollectionUtils.isNotEmpty(homeAway),MatchSettleEventEntity::getHomeAway,homeAway);
        return this.list(queryWrapper);
    }

    @Override
    public List<MatchSettleEventEntity> getByMatchIdAndEventCodeAndSettleNumAndHomeAway(Long standardMatchId, String eventCode, String settleNum, String homeAway) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(StringUtils.isNotBlank(settleNum), MatchSettleEventEntity::getSettleNum, settleNum)
                .eq(StringUtils.isNotBlank(eventCode),MatchSettleEventEntity::getEventCode,eventCode)
                .eq(StringUtils.isNotBlank(homeAway),MatchSettleEventEntity::getHomeAway,homeAway)
        ;
        return this.list(queryWrapper);
    }

    @Override
    public List<MatchSettleEventEntity> getByMatchIdAndEventCodeAndEventTypeAndNotStatus(Long standardMatchId, String eventCode, Integer eventType, Integer status) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(StringUtils.isNotBlank(eventCode), MatchSettleEventEntity::getEventCode, eventCode)
                .eq(eventType != null,MatchSettleEventEntity::getEventType,eventType)
                .ne(status!=null,MatchSettleEventEntity::getStatus,status)
        ;
        return this.list(queryWrapper);
    }

    @Override
    public List<MatchSettleEventEntity> getByMatchIdAndEventCodesAndEventTypeAndNotStatus(Long standardMatchId, List<String> eventCodes, Integer eventType, Integer status) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(eventCodes), MatchSettleEventEntity::getEventCode, eventCodes)
                .eq(eventType != null,MatchSettleEventEntity::getEventType,eventType)
                .ne(status!=null,MatchSettleEventEntity::getStatus,status)
        ;
        return this.list(queryWrapper);
    }


    /**
     *根据id进行过滤--开始
     */
    @Override
    public MatchSettleEvent getExtryEvent(Long standardMatchId, Long thirdEventSourceId, Long id, Integer eventType, List<String> eventCodes) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(thirdEventSourceId != null, MatchSettleEventEntity::getThirdEventSourceId, thirdEventSourceId)
                .ne(id != null, MatchSettleEventEntity::getId, id)
                .eq(eventType != null, MatchSettleEventEntity::getEventType, eventType)
                .in(CollectionUtils.isNotEmpty(eventCodes), MatchSettleEventEntity::getEventCode, eventCodes);
        List<MatchSettleEventEntity> entities = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entities.get(0));
    }

    @Override
    public MatchSettleEvent getExtryEvent(Long standardMatchId, Long periodId, Integer eventOrder, Integer eventType, List<String> eventCodes) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(periodId != null, MatchSettleEventEntity::getPeriodId, periodId)
                .eq(eventOrder != null, MatchSettleEventEntity::getEventOrder, eventOrder)
                .eq(eventType != null, MatchSettleEventEntity::getEventType, eventType)
                .in(CollectionUtils.isNotEmpty(eventCodes), MatchSettleEventEntity::getEventCode, eventCodes);
        List<MatchSettleEventEntity> entities = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return matchSettleEventV2Converter.convertMatchSettleEventEntityToEvent(entities.get(0));
    }

    @Override
    public List<MatchSettleEventEntity> getByStandardMatchIdAndThirdEventSourceIdAndEventTypeAndNotId(Long standardMatchId, Long thirdEventSourceId, Integer eventType, Long id) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(thirdEventSourceId != null, MatchSettleEventEntity::getThirdEventSourceId, thirdEventSourceId)
                .eq(eventType != null,MatchSettleEventEntity::getEventType,eventType);
        return this.list(queryWrapper);
    }

    @Override
    public List<MatchSettleEventEntity> getByMatchIdAndEventCodeAndSettleNumAndEventOrderLeAndNotId(Long standardMatchId, String eventCode, String settleNum, Integer eventOrder, Long id) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(StringUtils.isNotBlank(settleNum), MatchSettleEventEntity::getSettleNum, settleNum)
                .eq(StringUtils.isNotBlank(eventCode),MatchSettleEventEntity::getEventCode,eventCode)
                .le(eventOrder!=null,MatchSettleEventEntity::getEventOrder,eventOrder)
                .ne(id!=null,MatchSettleEventEntity::getId,id)
        ;
        return this.list(queryWrapper);
    }

    @Override
    public List<MatchSettleEventEntity> getByStandardMatchIdAndEventCodeAndPeriodIdLessThanOrEqualAndIdNotAndEventType(Long standardMatchId, String eventCode, Long periodId, Long id, Integer eventType) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .eq(StringUtils.isNotBlank(eventCode), MatchSettleEventEntity::getEventCode, eventCode)
                .le(periodId != null,MatchSettleEventEntity::getPeriodId,periodId)
                .ne(id!=null,MatchSettleEventEntity::getId,id)
                .eq(eventType!=null,MatchSettleEventEntity::getEventType,eventType);
        return this.list(queryWrapper);
    }

    @Override
    public List<MatchSettleEventEntity> getByStandardMatchIdAndSettleNumAndPeriodIdLessThanOrEqualAndIdNotAndEventTypeAndStatus(Long standardMatchId, List<String> settleNum, Long periodId, Long id, Integer eventType, Integer status) {
        LambdaQueryWrapper<MatchSettleEventEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(standardMatchId != null, MatchSettleEventEntity::getStandardMatchId, standardMatchId)
                .in(CollectionUtils.isNotEmpty(settleNum), MatchSettleEventEntity::getSettleNum, settleNum)
                .le(periodId != null,MatchSettleEventEntity::getPeriodId,periodId)
                .ne(id!=null,MatchSettleEventEntity::getId,id)
                .eq(eventType!=null,MatchSettleEventEntity::getEventType,eventType)
                .eq(status!=null,MatchSettleEventEntity::getStatus,status);
        return this.list(queryWrapper);
    }

    /**
     *根据example进行过滤--开始
     */

    @Override
    public List<MatchSettleEventEntity> selectByExample(MatchSettleEventExample example) {
        return matchSettleEventV3Mapper.selectByExample(example);
    }

}
