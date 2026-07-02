package com.panda.merge.v2.service.helper;

import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleEventCodeEnum;
import com.panda.merge.constant.SettleMentionEnum;
import com.panda.merge.dto.settle.MatchSettleScoreSearchDto;
import com.panda.merge.dto.settle.MentionQueryRequest;
import com.panda.merge.service.settleMention.dto.AbstractMentionStatus;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class MentionStatusHelper {

    @Autowired
    private RedisService redisService;

    public void obtainDetailInfo(MatchSettleScoreSearchDto settleScoreSearchDto, Map<String, Integer> deleteStatusMap, Map<String, Integer> dataMismatchMap){
        try {
            SettleEventCodeEnum settleEventCodeEnum = SettleEventCodeEnum.getEventCodeEnum(settleScoreSearchDto.getEventCode());
            if (settleEventCodeEnum == null) {
                return;
            }
            MentionQueryRequest queryRequest = new MentionQueryRequest();
            queryRequest.setMatchId(settleScoreSearchDto.getStandardMatchId());
            Map<String, AbstractMentionStatus> mentionStatusMap = getAllMentionStatus(queryRequest);
            log.info("syncTest mentionStatusMap: {}", mentionStatusMap);
            if (MapUtils.isEmpty(mentionStatusMap)) {
                return;
            }

            if (mentionStatusMap.containsKey(SettleMentionEnum.FOOTBALL_DELETE_EVENT.getValue())) {
                AbstractMentionStatus.EventStatus eventStatus = mentionStatusMap.get(SettleMentionEnum.FOOTBALL_DELETE_EVENT.getValue())
                        .getDetailStatusFieldByEventCode(settleEventCodeEnum);
                if(eventStatus != null && !MapUtils.isEmpty(eventStatus.getDetailStatus())) {
                    // 处理新的 detailStatus 格式：对于删除事件，value 可能是 Map 对象（包含 status 和 dataSourceCode）
                    for (Map.Entry<String, Object> entry : eventStatus.getDetailStatus().entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        if (value instanceof Map) {
                            // 新格式：Map 对象，提取 status 值
                            Map<String, Object> valueMap = (Map<String, Object>) value;
                            Object statusValue = valueMap.get("status");
                            if (statusValue instanceof Integer) {
                                deleteStatusMap.put(key, (Integer) statusValue);
                            }
                        } else if (value instanceof Integer) {
                            // 旧格式：直接是 Integer，向后兼容
                            deleteStatusMap.put(key, (Integer) value);
                        }
                    }
                }
            }
            // 处理数据不匹配：需要同时检查 FOOTBALL_SCORE_MISMATCH 和 FOOTBALL_PHASE_SCORE_MISMATCH
            // 因为它们都使用相同的 value "dataMismatchStatus"，所以直接检查 key 是否为 "dataMismatchStatus"
            // 参考 getSettleEventMentionStatus 的实现方式，遍历所有 mentionStatusMap 来查找
            if (mentionStatusMap.containsKey("dataMismatchStatus")) {
                AbstractMentionStatus mentionStatus = mentionStatusMap.get("dataMismatchStatus");
                if (mentionStatus instanceof FootballMentionStatus) {
                    FootballMentionStatus.EventStatus eventStatus = ((FootballMentionStatus) mentionStatus)
                            .getDetailStatusFieldByEventCode(settleEventCodeEnum);
                    if(eventStatus != null && !MapUtils.isEmpty(eventStatus.getDetailStatus())) {
                        // 数据不匹配场景，value 通常仍是 Integer 类型，但也需要支持 Object 类型（向后兼容）
                        for (Map.Entry<String, Object> detailEntry : eventStatus.getDetailStatus().entrySet()) {
                            String detailKey = detailEntry.getKey();
                            Object detailValue = detailEntry.getValue();
                            if (detailValue instanceof Integer) {
                                dataMismatchMap.put(detailKey, (Integer) detailValue);
                            } else if (detailValue instanceof Map) {
                                // 如果将来数据不匹配也改为新格式，这里可以提取 status
                                Map<String, Object> valueMap = (Map<String, Object>) detailValue;
                                Object statusValue = valueMap.get("status");
                                if (statusValue instanceof Integer) {
                                    dataMismatchMap.put(detailKey, (Integer) statusValue);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            log.error("obtainDetailInfo error::{}",e.getMessage());
            return;
        }

    }

    public Map<String, AbstractMentionStatus> getAllMentionStatus(MentionQueryRequest mentionQueryRequest) {
        try {
            String key = CommonConstant.SETTLE_MENTION_KEY + mentionQueryRequest.getMatchId();
            Map<String, AbstractMentionStatus> result = redisService.hGetAll(key);
            return result;
        } catch (Exception e) {
            log.error("Failed to get mention status from Redis for matchId: {}, error: {}", 
                    mentionQueryRequest.getMatchId(), e.getMessage(), e);
            // 如果反序列化失败，可能是由于包名不匹配导致的
            // FastJsonRedisSerializer 已经处理了类型映射，但如果仍有问题，这里会记录详细错误
            return null;
        }
    }

}
