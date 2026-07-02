package com.panda.merge.dto.message;

import com.panda.merge.constant.SubscriptionTypeEnum;
import lombok.Data;

@Data
public class HeartSuccessMessage extends AbstructMessage {

    private Integer command = SubscriptionTypeEnum.HEART_SUCCESS.getCode();

    private String msg =SubscriptionTypeEnum.HEART_SUCCESS.getVal();

    private Long timestamp=System.currentTimeMillis();
}
