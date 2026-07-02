package com.panda.merge.service.settleMention.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleEventCodeEnum;
import com.panda.merge.constant.SettleMentionEnum;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.model.MatchSettleScoreExample;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @description: settle mention football delete event implementation
 * @author: Henry Wang
 * @create: 2024-08-28 13:39
 **/
@Slf4j
@Component
public class FootballDeleteEventProcessor extends AbstractFootballProcessor<FootballMentionStatus>{
    
    @Resource
    private com.panda.merge.mapper.MatchSettleScoreMapper matchSettleScoreMapper;
    
    @Override
    protected Object obtainData(Map<String, Object> parameters) {
        MatchSettleEvent matchSettleEvent = (MatchSettleEvent) parameters.get("matchSettleEvent");
        List<String> deleteSettleNums = (List<String>) parameters.get("deleteSettleNums");
        MatchEventInfo matchEventInfo = (MatchEventInfo) parameters.get("matchEventInfo");
        log.info("[FootballDeleteEventProcessor] addSettleMention obtainData with settleEventId:{} start!", matchSettleEvent.getId());
        SettleEventCodeEnum settleEventCodeEnum = SettleEventCodeEnum.getEventCodeEnum(matchSettleEvent.getEventCode());
        if (settleEventCodeEnum == null) {
            return null;
        }
        
        // 获取数据源代码，优先使用 matchEventInfo 中的 dataSourceCode
        String dataSourceCode = matchEventInfo.getDataSourceCode();
        // 5/15分钟：N01、N02、N03、LS 的删除事件不参与卡住逻辑，不写入 mention
        if (dataSourceCode != null && (dataSourceCode.equals("N01") || dataSourceCode.equals("N02") || dataSourceCode.equals("N03") || dataSourceCode.equals("LS"))) {
            log.info("[FootballDeleteEventProcessor] addSettleMention ignore delete from data source {}", dataSourceCode);
            return null;
        }
        
        // 获取所有阶段的 matchSettleScoreId（包含所有数据源），用于标记哪些阶段有删除事件
        // 新的格式：detailStatus 的 value 为 Map 对象，包含 status 和 dataSourceCode
        Map<String, Object> deleteEventMap = new HashMap<>();
        
        // 获取阶段的 matchSettleScoreId
        Map<String, Integer> scoreIdsMap = getScoreIdsBySettleNums(matchSettleEvent.getStandardMatchId(), deleteSettleNums);
        for (Map.Entry<String, Integer> entry : scoreIdsMap.entrySet()) {
            Map<String, Object> detailStatusValue = new HashMap<>();
            detailStatusValue.put("status", entry.getValue());
            detailStatusValue.put("dataSourceCode", dataSourceCode);
            deleteEventMap.put(entry.getKey(), detailStatusValue);
        }
        
        // 添加事件本身的 ID
        Map<String, Object> eventDetailStatusValue = new HashMap<>();
        eventDetailStatusValue.put("status", CommonConstant.COMMON_TRUE_FLAG);
        eventDetailStatusValue.put("dataSourceCode", dataSourceCode);
        deleteEventMap.put(String.valueOf(matchSettleEvent.getId()), eventDetailStatusValue);

        // 获取被删除的数据源编码，用于前端显示删除线
        // deletedDataSourceMap 的 key 是 matchSettleScoreId，value 是被删除的数据源编码列表
        // 这样前端可以根据阶段ID查询到哪些数据源被删除了，从而显示删除线
        String deletedDataSourceCode = dataSourceCode;
        Map<String, List<String>> deletedDataSourceMapping = new HashMap<>();
        
        if (deletedDataSourceCode != null && !CollectionUtils.isEmpty(deleteSettleNums)) {
            // 查询被删除的数据源对应的 matchSettleScore，并记录数据源信息
            // 注意：这里只查询被删除的数据源，因为一个阶段可能有多个数据源，只有被删除的数据源才需要显示删除线
            MatchSettleScoreExample example = new MatchSettleScoreExample();
            example.createCriteria()
                .andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId())
                .andSettleNumIn(deleteSettleNums)
                .andDataSourceCodeEqualTo(deletedDataSourceCode);
            List<MatchSettleScore> matchSettleScores = matchSettleScoreMapper.selectByExample(example);
            
            for (MatchSettleScore score : matchSettleScores) {
                String scoreId = String.valueOf(score.getId());
                List<String> dataSources = deletedDataSourceMapping.getOrDefault(scoreId, new ArrayList<>());
                if (!dataSources.contains(deletedDataSourceCode)) {
                    dataSources.add(deletedDataSourceCode);
                }
                deletedDataSourceMapping.put(scoreId, dataSources);
            }
            
            log.info("[FootballDeleteEventProcessor] addSettleMention obtainData deletedDataSourceMapping:{}", deletedDataSourceMapping);
        }

        // 组装数据返回
        Map<String, Object> result = new HashMap<>();
        result.put("redisKey", redisKey(matchSettleEvent.getStandardMatchId()));
        result.put("settleEventCodeEnum", settleEventCodeEnum);
        result.put("redisValue", deleteEventMap);
        result.put("deletedDataSourceMapping", deletedDataSourceMapping);
        log.info("[FootballDeleteEventProcessor] addSettleMention obtainData with settleEventId:{} result:{} end!", matchSettleEvent.getId(), result);
        return result;
    }
    
    @Override
    protected Object buildData(Object object) {
        Map<String, Object> parameters = (Map<String, Object>) object;
        FootballMentionStatus footballMentionStatus = (FootballMentionStatus) querySettleMentionFromRedis(String.valueOf(parameters.get("redisKey")));
        log.info("[FootballDeleteEventProcessor] addSettleMention footballMentionStatus:{}", footballMentionStatus);
        SettleEventCodeEnum eventCode = (SettleEventCodeEnum)parameters.get("settleEventCodeEnum");
        log.info("[FootballDeleteEventProcessor] addSettleMention eventCode:{}", eventCode);
        if (footballMentionStatus == null) {
            footballMentionStatus = FootballMentionStatus.buildInstance();
        }
        log.info("[FootballDeleteEventProcessor] addSettleMention interval");
        FootballMentionStatus.EventStatus eventStatus = footballMentionStatus.getDetailStatusFieldByEventCode(eventCode);
        log.info("[FootballDeleteEventProcessor] addSettleMention eventStatus:{}", eventStatus);
        Map<String, Object> deleteEventMap = (Map<String, Object>) parameters.get("redisValue");
        log.info("[FootballDeleteEventProcessor] addSettleMention deleteEventMap0:{}", deleteEventMap);
        
        // 获取被删除的数据源映射
        Map<String, List<String>> deletedDataSourceMapping = (Map<String, List<String>>) parameters.get("deletedDataSourceMapping");
        if (deletedDataSourceMapping == null) {
            deletedDataSourceMapping = new HashMap<>();
        }
        
        // 合并现有的 detailStatus（支持新格式和旧格式的兼容）
        if (eventStatus.getDetailStatus() != null) {
            for (Map.Entry<String, Object> entry : eventStatus.getDetailStatus().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                // 如果现有值已经是 Map 格式，直接合并；如果是 Integer 格式，转换为新格式
                if (value instanceof Map) {
                    // 新格式，直接合并
                    deleteEventMap.put(key, value);
                } else if (value instanceof Integer) {
                    // 旧格式，转换为新格式（如果没有 dataSourceCode，使用默认值）
                    Map<String, Object> detailStatusValue = new HashMap<>();
                    detailStatusValue.put("status", value);
                    // 从 deletedDataSourceMapping 中获取数据源代码，如果存在
                    if (deletedDataSourceMapping.containsKey(key) && !deletedDataSourceMapping.get(key).isEmpty()) {
                        detailStatusValue.put("dataSourceCode", deletedDataSourceMapping.get(key).get(0));
                    }
                    deleteEventMap.put(key, detailStatusValue);
                }
            }
        }
        
        // 合并被删除的数据源映射（如果之前已有数据）
        if (eventStatus.getDeletedDataSourceMap() != null) {
            for (Map.Entry<String, List<String>> entry : eventStatus.getDeletedDataSourceMap().entrySet()) {
                if (!deletedDataSourceMapping.containsKey(entry.getKey())) {
                    deletedDataSourceMapping.put(entry.getKey(), entry.getValue());
                } else {
                    // 合并列表，去重
                    List<String> existingList = deletedDataSourceMapping.get(entry.getKey());
                    List<String> newList = new ArrayList<>(existingList);
                    for (String dataSource : entry.getValue()) {
                        if (!newList.contains(dataSource)) {
                            newList.add(dataSource);
                        }
                    }
                    deletedDataSourceMapping.put(entry.getKey(), newList);
                }
            }
        }
        
        log.info("[FootballDeleteEventProcessor] addSettleMention deleteEventMap:{}", deleteEventMap);
        log.info("[FootballDeleteEventProcessor] addSettleMention deletedDataSourceMapping:{}", deletedDataSourceMapping);
        
        // 设置 detailStatus（新格式：Map<String, Object>）
        eventStatus.setDetailStatus(deleteEventMap);
        if (deleteEventMap.isEmpty()) {
            eventStatus.setStatus(CommonConstant.COMMON_FALSE_FLAG);
        } else {
            eventStatus.setStatus(CommonConstant.COMMON_TRUE_FLAG);
        }
        // 设置被删除的数据源映射
        eventStatus.setDeletedDataSourceMap(deletedDataSourceMapping);
        
        Map<String, Object> result = new HashMap<>();
        result.put("redisKey", parameters.get("redisKey"));
        result.put("redisValue", footballMentionStatus);
        log.info("[FootballDeleteEventProcessor] addSettleMention buildData with result:{} start!", result);
        return result;
    }

    @Override
    protected SettleMentionEnum settleMention(){
        return SettleMentionEnum.FOOTBALL_DELETE_EVENT;
    }

    @Override
    protected TypeReference<FootballMentionStatus> typeReference() {
        return new TypeReference<FootballMentionStatus>(){};
    }
 }
