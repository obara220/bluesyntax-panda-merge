package com.panda.merge.v2.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleEventExample;
import com.panda.merge.v2.entity.MatchSettleEventEntity;

import java.util.List;

public interface MatchSettleEventV2Repository extends IService<MatchSettleEventEntity> {

    boolean save(MatchSettleEvent matchSettleEvent);

    boolean updateById(MatchSettleEvent matchSettleEvent);

    void saveOrUpdateBatch(List<MatchSettleEvent> matchSettleEvents);

    MatchSettleEvent getById(Long id);

    List<MatchSettleEvent> getByIds(List<Long> ids);

    /**
     *根据status进行过滤--开始
     */
    List<MatchSettleEventEntity> getByStandardMatchIdAndStatus(Long standardMatchId, Integer status);

    List<MatchSettleEvent> getModelByStandardMatchIdAndNotStatus(Long standardMatchId, Integer status);

    List<MatchSettleEvent> getModelByStandardMatchIdAndNotStatusAndEventTypeAndIsGrey(Long standardMatchId, Integer status, Integer eventType, Integer isGray);

    /**
     *根据settleNum进行过滤--开始
     */
    List<MatchSettleEvent> getModelByStandardMatchIdAndSettleNums(Long standardMatchId, List<String> settleNums);

    List<MatchSettleEventEntity> getByStandardMatchIdAndSportIdAndSettleNum(Long matchId, Integer sportId, List<String> settleNumList);

    List<MatchSettleEvent> getByMatchIdAndSettleNumAndEventOrderAndPeriodId(Long standardMatchId,String settleNum,Integer eventOrder,Long periodId);

    /**
     *根据eventCodes进行过滤--开始
     */
    List<MatchSettleEvent> getModelsByItems(Long standardMatchId, List<String> eventCodes, List<Long> periods, Integer status, List<String> homeAway);

    List<MatchSettleEvent> getModelsByItemsAndOrderBySettleNumAndEventOrder(Long standardMatchId, List<String> eventCodes,List<Long> periods, List<Integer> eventTypes);

    List<MatchSettleEvent> getModelsByItemsAndEventType(Long standardMatchId, List<String> eventCodes, List<Long> periods, Integer status, Integer eventType);

    List<MatchSettleEventEntity> getByEventCodeAndPeriodIdAndStatusAndStandardMatchIdAndHomeAway(List<String> eventCodes,List<Long> periods,Integer status,Long standardMatchId, List<String> homeAway);

    List<MatchSettleEventEntity> getByMatchIdAndEventCodeAndSettleNumAndHomeAway(Long standardMatchId,String eventCode,String settleNum,String homeAway);

    List<MatchSettleEventEntity> getByMatchIdAndEventCodeAndEventTypeAndNotStatus(Long standardMatchId,String eventCode,Integer eventType,Integer status);

    List<MatchSettleEventEntity> getByMatchIdAndEventCodesAndEventTypeAndNotStatus(Long standardMatchId, List<String> eventCodes, Integer eventType, Integer status);

    /**
     *根据id进行过滤--开始
     */
    MatchSettleEvent getExtryEvent(Long standardMatchId, Long thirdEventSourceId, Long id, Integer eventType, List<String> eventCodes);

    MatchSettleEvent getExtryEvent(Long standardMatchId, Long periodId, Integer eventOrder, Integer eventType, List<String> eventCodes);

    List<MatchSettleEventEntity> getByStandardMatchIdAndThirdEventSourceIdAndEventTypeAndNotId(Long standardMatchId,Long thirdEventSourceId,Integer eventType,Long id);

    List<MatchSettleEventEntity> getByMatchIdAndEventCodeAndSettleNumAndEventOrderLeAndNotId(Long standardMatchId,String eventCode,String settleNum,Integer eventOrder,Long id);

    List<MatchSettleEventEntity> getByStandardMatchIdAndEventCodeAndPeriodIdLessThanOrEqualAndIdNotAndEventType(Long standardMatchId,String eventCode,Long periodId,Long id,Integer eventType);

    List<MatchSettleEventEntity> getByStandardMatchIdAndSettleNumAndPeriodIdLessThanOrEqualAndIdNotAndEventTypeAndStatus(Long standardMatchId,List<String> settleNum,Long periodId,Long id,Integer eventType,Integer status);

    /**
     *根据example进行过滤--开始
     */
    List<MatchSettleEventEntity> selectByExample(MatchSettleEventExample example);

}