package com.panda.merge.dto.message;

import com.panda.merge.constant.SubscriptionTypeEnum;
import lombok.Data;

@Data
public class BasketballPeriodScoresMessage extends AbstructMessage{

    private Integer command = SubscriptionTypeEnum.BASKETBALL_PERIOD_SCORES_PUSH.getCode();

    private String msg =SubscriptionTypeEnum.BASKETBALL_PERIOD_SCORES_PUSH.getVal();

    private Long index;

    private Long timestamp;

    public BasketballPeriodScoresMessage(Long index) {
        this.index = index;
        timestamp=System.currentTimeMillis();
    }
}
