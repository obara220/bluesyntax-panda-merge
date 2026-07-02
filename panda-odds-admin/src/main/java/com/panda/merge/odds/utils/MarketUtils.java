package com.panda.merge.odds.utils;

import com.panda.merge.dto.message.StandardMarketMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * MarketUtils
 *
 * @description:
 * @date: 6/13/2025
 **/
public final class MarketUtils {

    public static String getBallhead(StandardMarketMessage market) {
        String obh = market.getObh();
        if (StringUtils.isNotEmpty(obh)) {
            return obh;
        }
        return market.getAddition1();

    }

    public static void setBallhead(StandardMarketMessage market, String ballHead) {

        if (StringUtils.isEmpty(market.getObh())) {
            market.setObh(getBallhead(market));
        }
        market.setAddition1(ballHead);
    }

}
