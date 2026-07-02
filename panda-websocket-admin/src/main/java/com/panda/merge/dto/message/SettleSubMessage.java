package com.panda.merge.dto.message;

import com.panda.merge.constant.SubscriptionTypeEnum;
import lombok.Data;

@Data
public class SettleSubMessage extends AbstructMessage {

    private Integer command = SubscriptionTypeEnum.SETTLE_MATCH_SUB.getCode();

    private String msg =SubscriptionTypeEnum.SETTLE_MATCH_SUB.getVal();

    private Long index;

    private Long timestamp;

    public SettleSubMessage(Long index){
        this.index=index;
        timestamp=System.currentTimeMillis();
    }
}
