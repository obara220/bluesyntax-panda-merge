package com.panda.merge.dto.message;

import com.panda.merge.constant.SubscriptionTypeEnum;
import lombok.Data;

@Data
public class MatchSettleRollBackMessage extends AbstructMessage{

    private Integer command = SubscriptionTypeEnum.MATCH_SETTLE_ROLL_BACK_STATUS_PUSH.getCode();

    private String msg =SubscriptionTypeEnum.MATCH_SETTLE_ROLL_BACK_STATUS_PUSH.getVal();

    private Long index;

    private Long timestamp;

    public MatchSettleRollBackMessage(Long index) {
        this.index = index;
        timestamp=System.currentTimeMillis();
    }
}
