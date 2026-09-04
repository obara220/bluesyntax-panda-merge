package com.panda.merge.v2.service.assemble;

import com.panda.merge.dto.settle.MatchSettleEventDto;
import com.panda.merge.dto.settle.MatchSettleScoreSearchDto;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleEventExample;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.utils.CornerMatchEventSortUtils;
import com.panda.merge.utils.MatchEventInfoSettleUtils;
import com.panda.merge.v2.repository.MatchSettleEventV2Repository;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.service.helper.MatchDelaySettleInfoHelper;
import com.panda.merge.v2.service.helper.MatchSettleCheckInfoHelper;
import com.panda.merge.v2.service.helper.MatchSettleRollBackInfoHelper;
import com.panda.merge.v2.service.helper.MentionStatusHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class MatchSettleEventAssemble {

    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;
    @Autowired
    private MentionStatusHelper mentionStatusHelper;
    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    private MatchSettleCheckInfoHelper matchSettleCheckInfoHelper;
    @Autowired
    private MatchSettleRollBackInfoHelper matchSettleRollBackInfoHelper;
    @Autowired
    private MatchDelaySettleInfoHelper matchDelaySettleInfoHelper;

    public List<MatchSettleEventDto> searchFootballMatchSettleEvent(MatchSettleScoreSearchDto settleScoreSearchDto) {
        List<Long> periods=new ArrayList<>();
        List<String> eventCodes =new ArrayList<>();
        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
            eventCodes.add("yellow_card");
            eventCodes.add("red_card");eventCodes.add("fa_card");
        }else if(settleScoreSearchDto.getEventCode().equals("goal")){
            eventCodes.add("goal");eventCodes.add("no goal");
        }else {
            eventCodes.add("corner");
        }
        periods.add(6l);periods.add(7l);periods.add(41l);periods.add(42l);
        List<MatchSettleEvent> list =matchSettleEventRepository.getModelsByItemsAndOrderBySettleNumAndEventOrder(
                settleScoreSearchDto.getStandardMatchId(),eventCodes,periods,Arrays.asList(1,3));
        List<MatchSettleEventDto> matchSettleScoreDtos=new ArrayList<>();
        Map<String, Integer> deleteStatusMap = new HashMap<>();
        Map<String, Integer> dataMismatchMap = new HashMap<>();
        mentionStatusHelper.obtainDetailInfo(settleScoreSearchDto, deleteStatusMap, dataMismatchMap);
        for (MatchSettleEvent matchSettleScore : list) {
            MatchSettleEventDto matchSettleScoreDto =new MatchSettleEventDto();
            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
            // deleteStatusMap 和 dataMismatchMap 的 key 可能是 matchSettleEventId 或 matchSettleScoreId
            // 对于 MatchSettleEvent，优先使用 matchSettleEventId 查找
            String eventIdKey = String.valueOf(matchSettleScore.getId());
            Integer deleteStatus = deleteStatusMap.get(eventIdKey);
            // 如果通过 eventId 没找到，说明 deleteStatusMap 中存储的是 matchSettleScoreId
            // 这种情况下，MatchSettleEvent 对应的 deleteStatus 应该通过其他方式获取
            // 由于 MatchSettleEvent 和 MatchSettleScore 可能没有直接关联，这里先使用 eventId
            matchSettleScoreDto.setHasDeleteEvent(deleteStatus != null ? deleteStatus : matchSettleScore.getHasDeleteEvent());
            Integer dataMismatchStatus = dataMismatchMap.get(eventIdKey);
            matchSettleScoreDto.setHasDataMismatchEvent(dataMismatchStatus != null ? dataMismatchStatus : 0);
            matchSettleScoreDto.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
            matchSettleScoreDto.setIsGrey(matchSettleScore.getIsGrey());
            if(null==matchSettleScore.getFifteenMinSection()){
                matchSettleScore.setFifteenMinSection(matchSettleScore.getFiveMinSection());
            }
            matchSettleScoreDto.setFifteenMinSection(matchSettleScore.getFifteenMinSection());
            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
            matchSettleScoreDtos.add(matchSettleScoreDto);
        }
        log.info("syncTest matchSettleScoreDtos: {}", matchSettleScoreDtos);
        if(!settleScoreSearchDto.getEventCode().equals("corner")){
            //进球和罚牌要展示多重结算
            for (MatchSettleEventDto matchSettleScoreDto : matchSettleScoreDtos) {
                MatchSettleEventExample eventExample =new MatchSettleEventExample();
                eventExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
                        .andEventCodeIn(eventCodes).andThirdEventSourceIdEqualTo(matchSettleScoreDto.getThirdEventSourceId())
                        .andIdNotEqualTo(Long.parseLong(matchSettleScoreDto.getId()) );
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getExtryEvent(settleScoreSearchDto.getStandardMatchId(),
                        matchSettleScoreDto.getThirdEventSourceId(),Long.parseLong(matchSettleScoreDto.getId()),2,eventCodes);
                if(matchSettleEvent == null) {
                    matchSettleEvent =matchSettleEventRepository.getExtryEvent(settleScoreSearchDto.getStandardMatchId(),
                            matchSettleScoreDto.getPeriodId(),matchSettleScoreDto.getEventOrder(),2,eventCodes);
                }
                MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
                BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
                matchSettleEventDto.setId(matchSettleEvent.getId().toString());
                matchSettleEventDto.setScoresPeriodFreeze(matchSettleEvent.getSettleFreeze());
                matchSettleScoreDto.setExtryEvent(matchSettleEventDto);
                if(null==matchSettleScoreDto.getFifteenMinSection()){
                    matchSettleEventDto.setFifteenMinSection(matchSettleScoreDto.getFiveMinSection());
                }
                matchSettleScoreDto.setFifteenMinSection(matchSettleScoreDto.getFifteenMinSection());
                MatchEventInfoSettleUtils.checkInfoKey(matchSettleEventDto);
            }

            if (settleScoreSearchDto.getEventCode().equals("fa_card")){
                //查询根据settleNum [上半场,下半场,全场,加时赛上半场,加时赛下半场,加时赛全场]
                List<String> settleNumFaCard=new ArrayList<>();
                settleNumFaCard.add("304"); settleNumFaCard.add("308"); settleNumFaCard.add("309");
                settleNumFaCard.add("3013"); settleNumFaCard.add("3017"); settleNumFaCard.add("3018");
                List<MatchSettleScore> matchSettleScores =matchSettleScoreRepository.getModelsByItemsAndSettleNums(
                        settleScoreSearchDto.getStandardMatchId(),Arrays.asList("fa_card"),null,null,settleNumFaCard);
                for (MatchSettleScore matchSettleScore : matchSettleScores) {
                    MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
                    BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
                    matchSettleEvent.setId(matchSettleScore.getId().toString());
                    matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
                    matchSettleEvent.setHasDeleteEvent(deleteStatusMap.getOrDefault(String.valueOf(matchSettleScore.getId()), matchSettleScore.getHasDeleteEvent()));
                    matchSettleEvent.setHasDataMismatchEvent(dataMismatchMap.getOrDefault(String.valueOf(matchSettleScore.getId()), 0));
                    matchSettleEvent.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
                    matchSettleEvent.setIsGrey(matchSettleScore.getIsGrey());
                    MatchEventInfoSettleUtils.checkInfoKey(matchSettleEvent);
                    matchSettleScoreDtos.add(matchSettleEvent);
                }
                matchSettleScoreDtos.sort(new Comparator<MatchSettleEventDto>() {
                    @Override
                    public int compare(MatchSettleEventDto o1, MatchSettleEventDto o2) {
                        return CornerMatchEventSortUtils.compareCornerMatchEventAndScore(o1,o2);
                    }
                });
            }

            //查询 当前用户的 阶段比分的明细的审核状态
            matchSettleCheckInfoHelper.searchCheckStatusByEventList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
            matchSettleRollBackInfoHelper.setRollBackStatusEvent(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
            matchDelaySettleInfoHelper.setDelayEventSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
            return matchSettleScoreDtos;
        }else {
            //查询根据settleNum
            List<String> settleNumCorner=new ArrayList<>();
            settleNumCorner.add("201"); settleNumCorner.add("202"); settleNumCorner.add("203");
            settleNumCorner.add("206"); settleNumCorner.add("207"); settleNumCorner.add("208");
            List<MatchSettleScore> matchSettleScores =matchSettleScoreRepository.getModelsByItemsAndSettleNums(
                    settleScoreSearchDto.getStandardMatchId(),Arrays.asList("corner"),null,null,settleNumCorner);
            for (MatchSettleScore matchSettleScore : matchSettleScores) {
                MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
                BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
                matchSettleEvent.setId(matchSettleScore.getId().toString());
                matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
                matchSettleEvent.setHasDeleteEvent(deleteStatusMap.getOrDefault(String.valueOf(matchSettleScore.getId()), matchSettleScore.getHasDeleteEvent()));
                matchSettleEvent.setHasDataMismatchEvent(dataMismatchMap.getOrDefault(String.valueOf(matchSettleScore.getId()), 0));
                matchSettleEvent.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
                matchSettleEvent.setIsGrey(matchSettleScore.getIsGrey());
                MatchEventInfoSettleUtils.checkInfoKey(matchSettleEvent);
                matchSettleScoreDtos.add(matchSettleEvent);
            }
            matchSettleScoreDtos.sort(new Comparator<MatchSettleEventDto>() {
                @Override
                public int compare(MatchSettleEventDto o1, MatchSettleEventDto o2) {
                    return CornerMatchEventSortUtils.compareCornerMatchEventAndScore(o1,o2);
                }
            });
            matchSettleCheckInfoHelper.searchCheckStatusByEventList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
            matchSettleRollBackInfoHelper.setRollBackStatusEvent(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
            matchDelaySettleInfoHelper.setDelayEventSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
            return matchSettleScoreDtos;
        }

    }

    public List<MatchSettleEventDto> searchFootballMatchSettleEventV3(MatchSettleScoreSearchDto settleScoreSearchDto) {
        List<Long> periods=new ArrayList<>();
        List<String> eventCodes =new ArrayList<>();
        if(settleScoreSearchDto.getEventCode().equals("fa_card")){
            eventCodes.add("yellow_card");
            eventCodes.add("red_card");eventCodes.add("fa_card");
        }else if(settleScoreSearchDto.getEventCode().equals("goal")){
            eventCodes.add("goal");eventCodes.add("no goal");
        }else {
            eventCodes.add("corner");
        }
        periods.add(6l);periods.add(7l);periods.add(41l);periods.add(42l);
        List<MatchSettleEvent> list =matchSettleEventRepository.getModelsByItemsAndOrderBySettleNumAndEventOrder(
                settleScoreSearchDto.getStandardMatchId(),eventCodes,periods,Arrays.asList(1,3));
        List<MatchSettleEventDto> matchSettleScoreDtos=new ArrayList<>();
        Map<String, Integer> deleteStatusMap = new HashMap<>();
        Map<String, Integer> dataMismatchMap = new HashMap<>();
        mentionStatusHelper.obtainDetailInfo(settleScoreSearchDto, deleteStatusMap, dataMismatchMap);
        for (MatchSettleEvent matchSettleScore : list) {
            MatchSettleEventDto matchSettleScoreDto =new MatchSettleEventDto();
            BeanUtils.copyProperties(matchSettleScore,matchSettleScoreDto);
            matchSettleScoreDto.setId(matchSettleScore.getId().toString());
            matchSettleScoreDto.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
            // deleteStatusMap 和 dataMismatchMap 的 key 可能是 matchSettleEventId 或 matchSettleScoreId
            // 对于 MatchSettleEvent，优先使用 matchSettleEventId 查找
            String eventIdKey = String.valueOf(matchSettleScore.getId());
            Integer deleteStatus = deleteStatusMap.get(eventIdKey);
            // 如果通过 eventId 没找到，说明 deleteStatusMap 中存储的是 matchSettleScoreId
            // 这种情况下，MatchSettleEvent 对应的 deleteStatus 应该通过其他方式获取
            // 由于 MatchSettleEvent 和 MatchSettleScore 可能没有直接关联，这里先使用 eventId
            matchSettleScoreDto.setHasDeleteEvent(deleteStatus != null ? deleteStatus : matchSettleScore.getHasDeleteEvent());
            Integer dataMismatchStatus = dataMismatchMap.get(eventIdKey);
            matchSettleScoreDto.setHasDataMismatchEvent(dataMismatchStatus != null ? dataMismatchStatus : 0);
            matchSettleScoreDto.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
            matchSettleScoreDto.setIsGrey(matchSettleScore.getIsGrey());
            if(null==matchSettleScore.getFifteenMinSection()){
                matchSettleScore.setFifteenMinSection(matchSettleScore.getFiveMinSection());
            }
            matchSettleScoreDto.setFifteenMinSection(matchSettleScore.getFifteenMinSection());
            MatchEventInfoSettleUtils.checkInfoKey(matchSettleScoreDto);
            matchSettleScoreDtos.add(matchSettleScoreDto);
        }
        log.info("syncTest matchSettleScoreDtos: {}", matchSettleScoreDtos);
        if(!settleScoreSearchDto.getEventCode().equals("corner")){
            //进球和罚牌要展示多重结算
            for (MatchSettleEventDto matchSettleScoreDto : matchSettleScoreDtos) {
                MatchSettleEventExample eventExample =new MatchSettleEventExample();
                eventExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId())
                        .andEventCodeIn(eventCodes).andThirdEventSourceIdEqualTo(matchSettleScoreDto.getThirdEventSourceId())
                        .andIdNotEqualTo(Long.parseLong(matchSettleScoreDto.getId()) );
                MatchSettleEvent matchSettleEvent =matchSettleEventRepository.getExtryEvent(settleScoreSearchDto.getStandardMatchId(),
                        matchSettleScoreDto.getThirdEventSourceId(),Long.parseLong(matchSettleScoreDto.getId()),2,eventCodes);
                if(matchSettleEvent == null) {
                    matchSettleEvent =matchSettleEventRepository.getExtryEvent(settleScoreSearchDto.getStandardMatchId(),
                            matchSettleScoreDto.getPeriodId(),matchSettleScoreDto.getEventOrder(),2,eventCodes);
                }
                MatchSettleEventDto matchSettleEventDto =new MatchSettleEventDto();
                BeanUtils.copyProperties(matchSettleEvent,matchSettleEventDto);
                matchSettleEventDto.setId(matchSettleEvent.getId().toString());
                matchSettleEventDto.setScoresPeriodFreeze(matchSettleEvent.getSettleFreeze());
                matchSettleScoreDto.setExtryEvent(matchSettleEventDto);
                if(null==matchSettleScoreDto.getFifteenMinSection()){
                    matchSettleEventDto.setFifteenMinSection(matchSettleScoreDto.getFiveMinSection());
                }
                matchSettleScoreDto.setFifteenMinSection(matchSettleScoreDto.getFifteenMinSection());
                MatchEventInfoSettleUtils.checkInfoKey(matchSettleEventDto);
            }

            if (settleScoreSearchDto.getEventCode().equals("fa_card")){
                //查询根据settleNum [上半场,下半场,全场,加时赛上半场,加时赛下半场,加时赛全场]
                List<String> settleNumFaCard=new ArrayList<>();
                settleNumFaCard.add("304"); settleNumFaCard.add("308"); settleNumFaCard.add("309");
                settleNumFaCard.add("3013"); settleNumFaCard.add("3017"); settleNumFaCard.add("3018");
                List<MatchSettleScore> matchSettleScores =matchSettleScoreRepository.getModelsByItemsAndSettleNums(
                        settleScoreSearchDto.getStandardMatchId(),Arrays.asList("fa_card"),null,null,settleNumFaCard);
                for (MatchSettleScore matchSettleScore : matchSettleScores) {
                    MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
                    BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
                    matchSettleEvent.setId(matchSettleScore.getId().toString());
                    matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
                    matchSettleEvent.setHasDeleteEvent(deleteStatusMap.getOrDefault(String.valueOf(matchSettleScore.getId()), matchSettleScore.getHasDeleteEvent()));
                    matchSettleEvent.setHasDataMismatchEvent(dataMismatchMap.getOrDefault(String.valueOf(matchSettleScore.getId()), 0));
                    matchSettleEvent.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
                    matchSettleEvent.setIsGrey(matchSettleScore.getIsGrey());
                    MatchEventInfoSettleUtils.checkInfoKey(matchSettleEvent);
                    matchSettleScoreDtos.add(matchSettleEvent);
                }
                matchSettleScoreDtos.sort(new Comparator<MatchSettleEventDto>() {
                    @Override
                    public int compare(MatchSettleEventDto o1, MatchSettleEventDto o2) {
                        return CornerMatchEventSortUtils.compareCornerMatchEventAndScore(o1,o2);
                    }
                });
            }

            //查询 当前用户的 阶段比分的明细的审核状态
            matchSettleCheckInfoHelper.searchCheckStatusByEventList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
            matchSettleRollBackInfoHelper.setRollBackStatusEvent(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
            matchDelaySettleInfoHelper.setDelayEventSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
            return matchSettleScoreDtos;
        }else {
            //查询根据settleNum
            List<String> settleNumCorner=new ArrayList<>();
            settleNumCorner.add("201"); settleNumCorner.add("202"); settleNumCorner.add("203");
            settleNumCorner.add("206"); settleNumCorner.add("207"); settleNumCorner.add("208");
            List<MatchSettleScore> matchSettleScores =matchSettleScoreRepository.getModelsByItemsAndSettleNums(
                    settleScoreSearchDto.getStandardMatchId(),Arrays.asList("corner"),null,null,settleNumCorner);
            for (MatchSettleScore matchSettleScore : matchSettleScores) {
                MatchSettleEventDto matchSettleEvent =new MatchSettleEventDto();
                BeanUtils.copyProperties(matchSettleScore,matchSettleEvent);
                matchSettleEvent.setId(matchSettleScore.getId().toString());
                matchSettleEvent.setScoresPeriodFreeze(matchSettleScore.getSettleFreeze());
                matchSettleEvent.setHasDeleteEvent(deleteStatusMap.getOrDefault(String.valueOf(matchSettleScore.getId()), matchSettleScore.getHasDeleteEvent()));
                matchSettleEvent.setHasDataMismatchEvent(dataMismatchMap.getOrDefault(String.valueOf(matchSettleScore.getId()), 0));
                matchSettleEvent.setCurrentEventStatus(matchSettleScore.getCurrentEventStatus());
                matchSettleEvent.setIsGrey(matchSettleScore.getIsGrey());
                MatchEventInfoSettleUtils.checkInfoKey(matchSettleEvent);
                matchSettleScoreDtos.add(matchSettleEvent);
            }
            matchSettleScoreDtos.sort(new Comparator<MatchSettleEventDto>() {
                @Override
                public int compare(MatchSettleEventDto o1, MatchSettleEventDto o2) {
                    return CornerMatchEventSortUtils.compareCornerMatchEventAndScore(o1,o2);
                }
            });
            matchSettleCheckInfoHelper.searchCheckStatusByEventList(matchSettleScoreDtos,settleScoreSearchDto.getOperatorName());
            matchSettleRollBackInfoHelper.setRollBackStatusEvent(matchSettleScoreDtos,settleScoreSearchDto.getStandardMatchId());
            matchDelaySettleInfoHelper.setDelayEventSecond(settleScoreSearchDto.getStandardMatchId(),matchSettleScoreDtos);
            return matchSettleScoreDtos;
        }

    }

}
