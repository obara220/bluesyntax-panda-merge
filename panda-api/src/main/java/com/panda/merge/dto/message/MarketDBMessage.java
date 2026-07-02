package com.panda.merge.dto.message;

import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketOdds;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.model.ThirdSportMarketOdds;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Data
public class MarketDBMessage implements Serializable {
    private String linkId;
    /**
     * 三方盘口
     */
    private List<ThirdSportMarket> thirdSportMarkets;
    /**
     * 三方盘口赔率
     */
    private List<ThirdSportMarketOdds> thirdSportMarketOdds;
    /**
     * 标准盘口
     */
    private List<StandardSportMarket> standardSportMarkets;
    /**
     * 标准盘口赔率
     */
    private List<StandardSportMarketOdds> standardSportMarketOdds;
}
