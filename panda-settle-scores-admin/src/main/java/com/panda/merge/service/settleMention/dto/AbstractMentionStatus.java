package com.panda.merge.service.settleMention.dto;

import com.panda.merge.config.SettleMentionProperty;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.SettleEventCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: mention status abstract class
 * @author: Henry Wang
 * @create: 2024-08-28 12:35
 **/
@Slf4j
@Data
public abstract class AbstractMentionStatus {

    @Data
    public static class EventStatus {
        private Integer status;

        /**
         * 原有字段：key 为 matchSettleScoreId 或 matchSettleEventId
         * 对于删除事件场景，value 为 Map 对象，包含 status 和 dataSourceCode: {"status": 1, "dataSourceCode": "BC"}
         * 对于数据不匹配场景，value 仍为 Integer 类型（保持向后兼容）
         * 对于数据不匹配场景，下一个5/15分钟阶段也会直接加入到这个 Map 中
         */
        private Map<String, Object> detailStatus;
        
        /**
         * 删除事件的数据源映射（仅用于删除事件场景）
         * key: matchSettleScoreId，value: 被删除的数据源编码列表
         * 用于前端显示删除线，标记哪个数据源的比分被删除了
         */
        private Map<String, List<String>> deletedDataSourceMap;
    }

    public EventStatus getDetailStatusFieldByEventCode(SettleEventCodeEnum settleEventCodeEnum) {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(SettleMentionProperty.class)) {
                    continue;
                }
                SettleMentionProperty item = field.getAnnotation(SettleMentionProperty.class);
                if (Arrays.asList(item.eventCode()).contains(settleEventCodeEnum.getValue())){
                    return (EventStatus) field.get(this);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("[AbstractMentionStatus] getDetailStatusFieldByEventCode error: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<EventStatus> getAllDetailStatusField() {
        try {
            List<EventStatus> result = new ArrayList<>();
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(SettleMentionProperty.class)) {
                    continue;
                }
                result.add((EventStatus) field.get(this));
            }
            return result;
        } catch (Exception e) {
            log.error("[AbstractMentionStatus] getAllDetailStatusField error: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setDataByEventCode(SettleEventCodeEnum eventCode, Map<String, Integer> value) {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(SettleMentionProperty.class)) {
                    continue;
                }
                SettleMentionProperty item = field.getAnnotation(SettleMentionProperty.class);
                if (Arrays.asList(item.eventCode()).contains(eventCode.getValue())){
                    EventStatus eventStatus = (EventStatus) field.get(this);
                    // 将 Map<String, Integer> 转换为 Map<String, Object>
                    Map<String, Object> detailStatusMap = new HashMap<>();
                    if (value != null) {
                        for (Map.Entry<String, Integer> entry : value.entrySet()) {
                            detailStatusMap.put(entry.getKey(), entry.getValue());
                        }
                    }
                    eventStatus.setDetailStatus(detailStatusMap);
                    if (MapUtils.isEmpty(value)) {
                        eventStatus.setStatus(CommonConstant.COMMON_FALSE_FLAG);
                    } else {
                        eventStatus.setStatus(CommonConstant.COMMON_TRUE_FLAG);
                    }
                    return;
                }
            }
        } catch (Exception e) {
            log.error("[AbstractMentionStatus] setDataByEventCode error: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setDetailNull() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(SettleMentionProperty.class)) {
                    continue;
                }
                EventStatus eventStatus = (EventStatus) field.get(this);
                eventStatus.setDetailStatus(null);
                // 清空新增的扩展字段
                if (eventStatus.getDeletedDataSourceMap() != null) {
                    eventStatus.getDeletedDataSourceMap().clear();
                }
            }
        } catch (Exception e) {
            log.error("[AbstractMentionStatus] setDetailNull error: ", e);
            throw new RuntimeException(e.getMessage());
        }
    };
}
