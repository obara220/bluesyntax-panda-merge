package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UpdateTradeTypeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long sportId;
    private Long matchId;
    /**
     * 总玩法
     */
    private List<Long> playIds;
    /**
     * 子玩法存在，以子玩法入库，总玩法触发赔率下发
     */
    private List<Long> childCategoryIds;
    private Integer tradeType;

    /**
     * 非自求封盘参数
     */
    private List<MarketPlaceDtlDTO> placeNumStatusList;
}
