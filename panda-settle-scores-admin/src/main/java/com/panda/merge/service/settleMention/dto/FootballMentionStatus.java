package com.panda.merge.service.settleMention.dto;

import com.panda.merge.config.SettleMentionProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @description: mention status football class
 * @author: Henry Wang
 * @create: 2024-08-28 12:37
 **/

@Data
@Slf4j
public class FootballMentionStatus extends AbstractMentionStatus {
    @SettleMentionProperty(eventCode = {"goal"})
    private EventStatus goalStatus;

    @SettleMentionProperty(eventCode = {"fa_card", "red_card", "yellow_card"})
    private EventStatus facardStatus;

    @SettleMentionProperty(eventCode = {"corner"})
    private EventStatus cornerStatus;

    public static FootballMentionStatus buildInstance() {
        FootballMentionStatus footballMentionStatus = new FootballMentionStatus();
        footballMentionStatus.setGoalStatus(new EventStatus());
        footballMentionStatus.setCornerStatus(new EventStatus());
        footballMentionStatus.setFacardStatus(new EventStatus());
        return footballMentionStatus;
    }

    public BasketballMentionStatus convertToBasketball(){
        BasketballMentionStatus basketballMentionStatus = new BasketballMentionStatus();
        basketballMentionStatus.setGoalStatus(this.goalStatus);
        return basketballMentionStatus;
    }

}
