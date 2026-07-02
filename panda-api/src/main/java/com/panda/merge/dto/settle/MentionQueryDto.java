package com.panda.merge.dto.settle;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @description: mention query dto
 * @author: Henry Wang
 * @create: 2024-08-31 14:00
 **/
@Slf4j
@Data
public class MentionQueryDto extends AbstractMentionQueryDto {

    private FootballMentionStatus deleteStatus;              // 0:未删除   1: 已删除
    private FootballMentionStatus dataMismatchStatus;

    @Data
    public static class FootballMentionStatus implements Serializable {
        private EventStatus goalStatus;

        private EventStatus facardStatus;

        private EventStatus cornerStatus;

        public void setDetailNull() {
            goalStatus.setDetailStatus(null);
            facardStatus.setDetailStatus(null);
            cornerStatus.setDetailStatus(null);
        }
    }

    @Data
    public static class EventStatus implements Serializable {
        private Integer status;

        /**
         * 原有字段：key 为 matchSettleScoreId 或 matchSettleEventId
         * 对于删除事件场景，value 为 Map 对象，包含 status 和 dataSourceCode: {"status": 1, "dataSourceCode": "BC"}
         * 对于数据不匹配场景，value 仍为 Integer 类型（保持向后兼容）
         * 对于数据不匹配场景，下一个5/15分钟阶段也会直接加入到这个 Map 中
         */
        private Map<String, Object> detailStatus;
    }

    public void setDetailNull() {
        if (this.getDeleteStatus() != null) {
            this.getDeleteStatus().setDetailNull();
        }
        if (this.getDataMismatchStatus() != null) {
            this.getDataMismatchStatus().setDetailNull();
        }
    }
}
