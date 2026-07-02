package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *心跳验证
 * @author  tell
 * */
@Data
public class HeartMessage implements Serializable {

    /**
     * 心跳发送时间戳：10位，秒级
     */
    private long timestamp;

    /**
     * 数据源编号
     */
    private String dataSourceCode;
}
