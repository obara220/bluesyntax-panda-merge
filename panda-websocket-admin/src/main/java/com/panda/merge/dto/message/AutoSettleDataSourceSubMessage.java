package com.panda.merge.dto.message;

import com.panda.merge.constant.SubscriptionTypeEnum;
import lombok.Data;

@Data
public class AutoSettleDataSourceSubMessage extends AbstructMessage{

    private Integer command = SubscriptionTypeEnum.AUTO_SETTLE_DATA_SOURCE_SUB.getCode();

    private String msg =SubscriptionTypeEnum.AUTO_SETTLE_DATA_SOURCE_SUB.getVal();

    private Long index;

    private Long timestamp;

    public AutoSettleDataSourceSubMessage(Long index) {
        this.index = index;
        timestamp=System.currentTimeMillis();
    }
}
