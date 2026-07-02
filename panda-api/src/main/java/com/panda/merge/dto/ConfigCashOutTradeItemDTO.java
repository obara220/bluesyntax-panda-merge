package com.panda.merge.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 提前结算配置
 */
@Data
public class ConfigCashOutTradeItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 赛前：1  滚球：0
     */
    private Integer marketType;

    private Integer leve ;

    /**
     * 玩法ID
     */
    private Long marketCategoryId;

    /**
     * 赛事级别提前结算开关,0:关1:开
     */
    private Integer matchPreStatus;

    /**
     * 玩法级别提前结算开关,0:关1:开
     */
    private Integer categoryPreStatus;

    /**
     * cashOutMargin
     */
    private Long cashOutMargin;

    /**
     * 预约投注开关 0:关 1:开 ,该字段不做任何处理
     */
    private Integer pendingOrderStatus;

    /**
     * 数据源编码
     */
    private String dataSourceCode;
}
