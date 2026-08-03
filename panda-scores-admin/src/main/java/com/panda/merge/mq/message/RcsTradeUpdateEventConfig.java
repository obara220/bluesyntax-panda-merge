package com.panda.merge.mq.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 3574 玩法集tMax开关配置，风控下发
 */
@Data
public class RcsTradeUpdateEventConfig implements Serializable {

    private String categorySetId;
    /**
     * 拒单事件
     */
    private List<RcsTradeUpdateEventConfigDTO> events;
    private Long categorySetName;
    private Long sportId;


}
