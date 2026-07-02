package com.panda.merge.dto.settle;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: mention query dto
 * @author: Henry Wang
 * @create: 2024-10-15 14:00
 **/
@Slf4j
@Data
public class BasketballMentionQueryDto extends AbstractMentionQueryDto {
    private BasketballMentionStatus dataMismatchStatus;

    @Data
    public static class BasketballMentionStatus implements Serializable {
        private EventStatus goalStatus;

        public void setDetailNull() {
            goalStatus.setDetailStatus(null);
        }
    }

    @Data
    public static class EventStatus implements Serializable {
        private Integer status;

        private Map<String, Integer> detailStatus;
    }

    public void setDetailNull() {
        if (this.getDataMismatchStatus() != null) {
            this.getDataMismatchStatus().setDetailNull();
        }
    }
}
