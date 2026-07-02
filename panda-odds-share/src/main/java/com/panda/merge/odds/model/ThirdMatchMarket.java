package com.panda.merge.odds.model;

import com.panda.merge.common.OddsWrapper;
import com.panda.merge.dto.ThirdMarketDTO;

import java.io.Serializable;
import java.util.List;

/**
 * ThirdMatchMarket
 *
 * @description:
 * @date: 7/12/2025
 **/
public class ThirdMatchMarket implements Serializable {

    public Long standardMatchId;

    public List<OddsWrapper<ThirdMarketDTO>> marketList;


    public ThirdMatchMarket(Long standardMatchId, List<OddsWrapper<ThirdMarketDTO>> marketList) {
        this.standardMatchId = standardMatchId;
        this.marketList = marketList;
    }

    public void addMarket(OddsWrapper<ThirdMarketDTO> wrapper) {
        marketList.add(wrapper);
    }

}
