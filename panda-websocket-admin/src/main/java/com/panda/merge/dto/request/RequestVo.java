package com.panda.merge.dto.request;

import com.panda.merge.dto.message.AbstructMessage;
import lombok.Data;

import java.io.Serializable;

@Data
public class RequestVo<T> extends AbstructMessage implements Serializable {

    /**
     * 订阅类型
     */
    private Integer command;

    private String msg;

    /**
     * 运动种类id
     */
    private Long sportId;

    /**
     * 订阅对象
     */
    private T para;

    /**
     * channelId
     */
    private String channelId;


}
