package com.panda.merge.odds.validate;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * FootballMarketOddsValidateService
 *
 * @description: 足球投注项校验
 * @date: 6/20/2025
 **/
@Service
@Slf4j
public class FootballMarketOddsValidateService {

    /**
     * 校验足球 35 150 球员玩法 去激活无进球球员投注项
     * @param oddsMessage
     * @param categoryId
     */
    public void validatePlayerOdds(StandardMarketOddsMessage oddsMessage, Long categoryId) {
        if (categoryId != 35L && categoryId != 150L) {
            return;
        }
        String oddsType = oddsMessage.getOddsType();
        if (StringUtils.equalsAnyIgnoreCase(oddsType,"None","No Goal")) {
            oddsMessage.setActive(Constant.SPORT_MARKET.ODDS_STATUS.UNACTIVE);
        }
    }

}
