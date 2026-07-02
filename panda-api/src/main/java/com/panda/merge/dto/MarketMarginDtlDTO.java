package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;

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
public class MarketMarginDtlDTO implements Serializable {

    private static final long serialVersionUID = -1513165566508017861L;
    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 分时，单位秒
     */
    private Long timeFrame;

    /**
     * margin值
     */
    private Double margin;

    private Integer placeNum;


    /**
     * 子玩法ID
     */
    private Long childStandardCategoryId;

}
