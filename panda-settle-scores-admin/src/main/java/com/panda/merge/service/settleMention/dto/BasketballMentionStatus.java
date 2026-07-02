package com.panda.merge.service.settleMention.dto;

import com.panda.merge.config.SettleMentionProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: mention status football class
 * @author: Henry Wang
 * @create: 2024-10-15 12:37
 **/

@Data
@Slf4j
public class BasketballMentionStatus extends AbstractMentionStatus {
    @SettleMentionProperty(eventCode = {"score_change"})
    private EventStatus goalStatus;

    public static BasketballMentionStatus buildInstance() {
        BasketballMentionStatus basketballMentionStatus = new BasketballMentionStatus();
        basketballMentionStatus.setGoalStatus(new EventStatus());
        return basketballMentionStatus;
    }

}
