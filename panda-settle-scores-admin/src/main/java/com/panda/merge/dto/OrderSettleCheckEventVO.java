package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderSettleCheckEventVO implements Serializable {

    //赛事id
    private Long matchId;
    //体种id
    private Integer sportId;
    //事件id集合
    private String eventIds;
    //创建时间
    private Long createTime = System.currentTimeMillis();
    //订单
    private String settleOrderNums;
}
