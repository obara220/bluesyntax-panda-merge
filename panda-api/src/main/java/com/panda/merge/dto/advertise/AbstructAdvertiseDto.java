package com.panda.merge.dto.advertise;

import lombok.Data;

import java.io.Serializable;

@Data
public class AbstructAdvertiseDto implements Serializable {

    private String linkedId;
    /**
     * 操作人id
     */
    private String operatorId;
    /**
     * 操作人姓名
     */
    private String operatorName;
    /**
     * ip地址
     */
    private String  ipAddress;

    /**
     * 链路ID
     */
    private String requestId;

    /**
     * 语言
     */
    private String language;
}
