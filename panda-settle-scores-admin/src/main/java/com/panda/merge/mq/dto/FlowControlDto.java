package com.panda.merge.mq.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FlowControlDto implements Serializable {
    private Integer FlowControlNotificationStatus;
    private Integer FlowControlNotificationTotal;
    private Integer FlowControlNotificationCurrent;
    private Integer FlowControlNotificationStage;
    private List<Long> FlowControlNotificationMatchInIds;
    private List<Long> FlowControlNotificationMatchNotInIds;
}

