package com.panda.merge.dto.message;

import com.panda.merge.dto.ThirdMarketDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AO 球头
 */
@Data
public class StandardMatchMarketAoMessage implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 标准比赛ID
     */
    private Long standardMatchInfoId;

    /**
     * 运动类型
     */
    private Long sportId;

    /**
     * 三方数据源赛事ID
     */
    private String thirdMatchInfoId;

    /**
     * 三方赛事ID
     */
    private String dataSourceCode;

    /**
     * 盘口
     */
    private List<StandardMarketDataMessage> marketList;

    /**
     * 三方数据源盘口球头
     */
    private Map<Long, ThirdMarketDTO> thirdMarketBallHeadMap;
    /**
     * 三方数据源盘口球头 篮球
     */
    private Map<Long, List<ThirdMarketDTO>> thirdBasketballMarketBallHeadMap;

    /**
     * 三方数据源集合
     */
    private Set<String> dataSourceCodes;

}
