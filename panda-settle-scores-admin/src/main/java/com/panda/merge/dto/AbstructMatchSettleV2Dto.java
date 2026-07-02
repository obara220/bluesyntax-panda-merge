package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AbstructMatchSettleV2Dto implements Serializable {
    private String linkedId;
    /**
     * 操作人id
     */
    private String operatorId;
    /**
     * 操作人姓名
     */
    private String operatorName;

    private Long sportId;

    //ip地址
    private String  ipAddress;

    /**
     * 链路ID
     */
    private String requestId;
}
