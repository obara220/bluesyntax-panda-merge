package com.panda.merge.dto.message;

import lombok.Data;

@Data
public class AbstructMessage {
    /**
     * 订阅类型
     */
    private Integer command;

    private String msg;

}
