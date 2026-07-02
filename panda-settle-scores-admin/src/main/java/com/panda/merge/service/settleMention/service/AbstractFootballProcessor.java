package com.panda.merge.service.settleMention.service;

import com.panda.merge.common.enums.MatchPeriodEnum;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleEventCodeEnum;
import com.panda.merge.mapper.MatchSettleScoreMapper;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.model.MatchSettleScoreExample;
import com.panda.merge.service.settleMention.dto.AbstractMentionStatus;
import com.panda.merge.service.settleMention.dto.BasketballMentionStatus;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @description: settle mention football delete event implementation
 * @author: Henry Wang
 * @create: 2024-08-28 13:39
 **/
@Slf4j
@Component
public abstract class AbstractFootballProcessor<T> extends AbstractSettleMentionProcessor<T>{

    @Resource
    private MatchSettleScoreMapper matchSettleScoreMapper;

    @Override
    protected Object buildData(Object object) {
        Map<String, Object> parameters = (Map<String, Object>) object;
        FootballMentionStatus footballMentionStatus = (FootballMentionStatus) querySettleMentionFromRedis(String.valueOf(parameters.get("redisKey")));
        SettleEventCodeEnum eventCode = (SettleEventCodeEnum)parameters.get("settleEventCodeEnum");
        if (footballMentionStatus == null) {
            footballMentionStatus = FootballMentionStatus.buildInstance();
        }
        FootballMentionStatus.EventStatus eventStatus = footballMentionStatus.getDetailStatusFieldByEventCode(eventCode);
        Map<String, Integer> deleteEventMap = (Map<String, Integer>) parameters.get("redisValue");

        if (eventStatus.getDetailStatus() != null) {
            // 将 Map<String, Object> 中的值转换为 Integer
            for (Map.Entry<String, Object> entry : eventStatus.getDetailStatus().entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Integer) {
                    deleteEventMap.put(entry.getKey(), (Integer) value);
                } else if (value instanceof Map) {
                    // 如果是 Map 对象，提取 status 值
                    Map<String, Object> valueMap = (Map<String, Object>) value;
                    Object statusValue = valueMap.get("status");
                    if (statusValue instanceof Integer) {
                        deleteEventMap.put(entry.getKey(), (Integer) statusValue);
                    }
                }
            }
        }
        footballMentionStatus.setDataByEventCode(eventCode, deleteEventMap);
        Map<String, Object> result = new HashMap<>();
        result.put("redisKey", parameters.get("redisKey"));
        result.put("redisValue", footballMentionStatus);
        log.info("[AbstractFootballProcessor] addSettleMention buildData with result:{} start!", result);
        return result;
    }

    protected Object buildDataForDelete(Long matchId, List<String> keys, SettleEventCodeEnum settleEventCodeEnum) {
        Map<String, Integer> keyMap = keys.stream().collect(Collectors.toMap(t->t, t->1, (t1, t2)->t1));
        Integer sportId = settleEventCodeEnum == null ? 1 : settleEventCodeEnum.getSportId();
        AbstractMentionStatus mentionStatus = null;
        if (sportId == 1) {
            mentionStatus = (FootballMentionStatus) querySettleMentionFromRedis(redisKey(matchId));
        } else if (sportId == 2) {
            mentionStatus = (BasketballMentionStatus) querySettleMentionFromRedis(redisKey(matchId));
        }
        if(mentionStatus == null) {
            throw new RuntimeException("提示信息为空,不需要删除!");
        }
        List<AbstractMentionStatus.EventStatus> eventStatuses = new ArrayList<>();

        if (settleEventCodeEnum == null) {
            eventStatuses = mentionStatus.getAllDetailStatusField();
        } else {
            FootballMentionStatus.EventStatus eventStatus = mentionStatus.getDetailStatusFieldByEventCode(settleEventCodeEnum);
            if (eventStatus != null) {
                eventStatuses.add(eventStatus);
            }
        }
        if (CollectionUtils.isEmpty(eventStatuses)) {
            throw new RuntimeException("事件编码不正确，不能找到对应删除事件!");
        }
        for (FootballMentionStatus.EventStatus eventStatus : eventStatuses) {
            if(eventStatus.getDetailStatus() == null) {
                continue;
            }
            Iterator<Map.Entry<String, Object>> it = eventStatus.getDetailStatus().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                if(keyMap.containsKey(key)){
                    it.remove();
                    // 同时清理新增的扩展字段中对应的数据
                    if (eventStatus.getDeletedDataSourceMap() != null) {
                        eventStatus.getDeletedDataSourceMap().remove(key);
                    }
                }
            }
            if(MapUtils.isEmpty(eventStatus.getDetailStatus())) {
                eventStatus.setStatus(null);
                eventStatus.setDetailStatus(null);
                // 清空扩展字段
                if (eventStatus.getDeletedDataSourceMap() != null) {
                    eventStatus.getDeletedDataSourceMap().clear();
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("redisKey", redisKey(matchId));
        result.put("redisValue", mentionStatus);
        log.info("[AbstractFootballProcessor] addSettleMention buildDataForDelete with result:{} start!", result);
        return result;
    }

    protected Map<String, Integer> getScoreIdsBySettleNums(Long standardMatchId, List<String> settleNums){
        Map<String, Integer>  result = Collections.emptyMap();
        if (standardMatchId == null || CollectionUtils.isEmpty(settleNums)) {
            return result;
        }
        MatchSettleScoreExample matchSettleScoreExample = new MatchSettleScoreExample();
        matchSettleScoreExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andSettleNumIn(settleNums);
        List<MatchSettleScore> matchSettleScores = matchSettleScoreMapper.selectByExample(matchSettleScoreExample);
        if(CollectionUtils.isEmpty(matchSettleScores)) {
            return result;
        }

        return matchSettleScores.stream().collect(Collectors.toMap(t->String.valueOf(t.getId()), t-> CommonConstant.COMMON_TRUE_FLAG));
    }

    /**
     * 根据当前阶段的 matchSettleScoreId 获取下一个阶段的 matchSettleScoreId 列表
     * 用于数据不匹配时，需要同时标记下一个5/15分钟阶段
     * 
     * @param standardMatchId 标准赛事ID
     * @param currentScoreId 当前阶段的 matchSettleScoreId
     * @return 下一个阶段的 matchSettleScoreId 列表，如果没有下一个阶段则返回空列表
     */
    protected List<String> getNextPhaseScoreIds(Long standardMatchId, String currentScoreId) {
        List<String> nextPhaseScoreIds = new ArrayList<>();
        if (standardMatchId == null || currentScoreId == null) {
            return nextPhaseScoreIds;
        }
        
        try {
            // 根据 matchSettleScoreId 查询当前阶段的 settleNum
            MatchSettleScore currentScore = matchSettleScoreMapper.selectByPrimaryKey(Long.valueOf(currentScoreId));
            if (currentScore == null || currentScore.getSettleNum() == null) {
                log.debug("[AbstractFootballProcessor] getNextPhaseScoreIds: currentScore not found, scoreId: {}", currentScoreId);
                return nextPhaseScoreIds;
            }
            
            String currentSettleNum = currentScore.getSettleNum();
            // 从 MatchPeriodEnum.allNextPhases 获取下一个阶段的 settleNum
            String nextSettleNum = MatchPeriodEnum.allNextPhases.get(currentSettleNum);
            if (nextSettleNum == null) {
                log.debug("[AbstractFootballProcessor] getNextPhaseScoreIds: no next phase for settleNum: {}", currentSettleNum);
                return nextPhaseScoreIds;
            }
            
            // 查询下一个阶段的 matchSettleScore
            MatchSettleScoreExample example = new MatchSettleScoreExample();
            example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andSettleNumEqualTo(nextSettleNum);
            List<MatchSettleScore> nextScores = matchSettleScoreMapper.selectByExample(example);
            
            if (!CollectionUtils.isEmpty(nextScores)) {
                nextPhaseScoreIds = nextScores.stream()
                    .map(score -> String.valueOf(score.getId()))
                    .collect(Collectors.toList());
                log.info("[AbstractFootballProcessor] getNextPhaseScoreIds: currentSettleNum={}, nextSettleNum={}, nextScoreIds={}", 
                    currentSettleNum, nextSettleNum, nextPhaseScoreIds);
            }
        } catch (Exception e) {
            log.error("[AbstractFootballProcessor] getNextPhaseScoreIds error: ", e);
        }
        
        return nextPhaseScoreIds;
    }
    
    /**
     * 根据 settleNum 列表获取下一个阶段的 matchSettleScoreId 列表
     * 
     * @param standardMatchId 标准赛事ID
     * @param settleNums 当前阶段的 settleNum 列表
     * @return 下一个阶段的 matchSettleScoreId 列表
     */
    protected List<String> getNextPhaseScoreIdsBySettleNums(Long standardMatchId, List<String> settleNums) {
        List<String> nextPhaseScoreIds = new ArrayList<>();
        if (standardMatchId == null || CollectionUtils.isEmpty(settleNums)) {
            return nextPhaseScoreIds;
        }
        
        // 获取所有下一个阶段的 settleNum
        List<String> nextSettleNums = new ArrayList<>();
        for (String settleNum : settleNums) {
            String nextSettleNum = MatchPeriodEnum.allNextPhases.get(settleNum);
            if (nextSettleNum != null && !nextSettleNums.contains(nextSettleNum)) {
                nextSettleNums.add(nextSettleNum);
            }
        }
        
        if (CollectionUtils.isEmpty(nextSettleNums)) {
            return nextPhaseScoreIds;
        }
        
        // 查询下一个阶段的 matchSettleScore
        MatchSettleScoreExample example = new MatchSettleScoreExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andSettleNumIn(nextSettleNums);
        List<MatchSettleScore> nextScores = matchSettleScoreMapper.selectByExample(example);
        
        if (!CollectionUtils.isEmpty(nextScores)) {
            nextPhaseScoreIds = nextScores.stream()
                .map(score -> String.valueOf(score.getId()))
                .collect(Collectors.toList());
        }
        
        return nextPhaseScoreIds;
    }
 }
