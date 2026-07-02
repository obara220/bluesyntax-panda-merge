package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
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
public class TradeMarketPlaceConfigDTO implements Serializable {

    private static final long serialVersionUID = 7140382087626912703L;

    /**
     * 标准赛事ID
     */
    private Long standardMatchInfoId;

    /**
     * 标准赛事类型：0.普通赛事、1.冠军赛事
     */
    private String matchType;

    /**
     * 盘口位置集合
     */
    private List<MarketPlaceDtlDTO> marketPlaceDtlDTOList;

}
