package com.panda.merge.service;

import com.panda.merge.dto.message.StandardMarketResultMessage;
import com.panda.merge.model.StandardSportMarket;

public interface IThirdMarketResultTransService {

    void transFootballPlayerMarketResult(String linkId, StandardMarketResultMessage data, StandardSportMarket standardSportMarket);

    void transFootballGoalTypeMarketResult(String linkId, StandardMarketResultMessage data, StandardSportMarket standardSportMarket);
}
