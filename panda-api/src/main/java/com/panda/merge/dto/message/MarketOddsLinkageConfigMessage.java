package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class MarketOddsLinkageConfigMessage implements Serializable {

    /**
     * 赛事类型,0:普通赛事、1冠军赛事
     */
    private Integer matchType;
    /**
     * 标准赛事id
     */
    private Long matchId;
    /**
     * 标准主赛事id
     */
    private Long parentMatchId;
    /**
     * 玩法ID
     */
    private Long categoryId;
    /**
     * 盘口ID
     */
    private String standardMarketId;
    /**
     * 投注项ID
     */
    private String standardMarketOddsId;
    /**
     * 投注项主 ID
     */
    private String parentStandardMarketOddsId;
    /**
     * 类型 0：副、1：主 、2：取消所有关联
     */
    private Integer type;

    /**
     * 主赔率
     */
    private Integer paOddsValue;

    /**
     * 盘口状态
     */
    private Integer status;
}
