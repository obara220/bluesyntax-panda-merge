package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Kepa
 * @Date 2021/7/15 20:37
 * @Version 1.0
 * @Desc 用於風控的賠率
 */
@Data
public class OutrightMarketDTO implements Serializable {

    private static final long serialVersionUID = -2951045180023421548L;

    private Long standardMatchId;

    private Long relationMarketId;

    private String LinkId;

    private Long marketStartTime;

    private Long marketEndTime;

    private Long marketNextCloseTime;

    private Long operateTime;

    private Integer tradeType;
}
