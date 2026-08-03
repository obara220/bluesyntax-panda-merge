package com.panda.merge.mq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 90420 玩法集拒单事件集合，风控下发
 */
@Data
public class RcsTradeUpdateEventConfigDTO implements Serializable {

    private Integer autoOpen;
    private Integer autoOpenDelayTime;
    private Integer categorySetId;
    private String eventCode;
    private String eventName;
    private String eventType;
    private Integer rejectType;


}
