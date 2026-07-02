package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.dto
 * @Description :  TODO
 * @Date: 2020-07-15 15:14
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TradeMarketMarginConfigDTO implements Serializable {

    private static final long serialVersionUID = 7140382087626912703L;
    /**
     * 标准赛事ID
     */
    private Long standardMatchInfoId;
    /**
     * 标准玩法ID
     */
    private Long standardCategoryId;

    /**
     * 盘口位置
     */
    private Integer placeNum;


    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    private Integer marketType;

    /**
     * 投注项margin集合
     */
    private List<MarketMarginDtlDTO> marketMarginDtlDTOList;

}
