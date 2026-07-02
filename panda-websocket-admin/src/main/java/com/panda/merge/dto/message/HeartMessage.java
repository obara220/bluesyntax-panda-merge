package com.panda.merge.dto.message;

import com.panda.merge.constant.SubscriptionTypeEnum;
import lombok.Data;

@Data
public class HeartMessage extends AbstructMessage {

    private Integer command = SubscriptionTypeEnum.HEART.getCode();

    private String msg =SubscriptionTypeEnum.HEART.getVal();

    private Long index;

    private Long timestamp;

    public HeartMessage(Long index){
        this.index=index;
        timestamp=System.currentTimeMillis();
    }
}
