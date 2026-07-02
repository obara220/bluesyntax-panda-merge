package com.panda.merge.dto.message;

import com.panda.merge.constant.SubscriptionTypeEnum;
import lombok.Data;

@Data
public class MatchScoreSubMessage extends AbstructMessage{
    private Integer command = SubscriptionTypeEnum.MATCH_STANDARD_SCORES_PUSH.getCode();

    private String msg =SubscriptionTypeEnum.MATCH_STANDARD_SCORES_PUSH.getVal();

    private Long index;

    private Long timestamp;

    public MatchScoreSubMessage(Long index) {
        this.index = index;
        timestamp=System.currentTimeMillis();
    }
}
