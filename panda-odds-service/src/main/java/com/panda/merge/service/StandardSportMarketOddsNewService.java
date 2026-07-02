package com.panda.merge.service;

import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketOdds;
import com.panda.merge.model.ThirdMarketCategoryField;

import java.util.List;

public interface StandardSportMarketOddsNewService {

    StandardSportMarketOdds getItem(String dataSourceCode, String thirdOddsFieldSourceId, Long standardMarketId);

    List<StandardSportMarketOdds> getItems(List<String> dataSourceIdAndMarketId);

    StandardSportMarketOdds create(String linkId, boolean isOutRight, StandardSportMarket standardSportMarket, ThirdMarketOddsDTO thirdMarketOddsDTO, ThirdMarketCategoryField thirdSportOddsFieldsTemplet);

    void convertStandardTeam(String linkId, StandardSportMarketOdds standardSportMarketOdds, StandardSportMarket standardSportMarket);

    StandardSportMarketOdds create(String linkId, StandardSportMarketOdds standardSportMarketOdds);

    StandardSportMarketOdds updateByPrimaryKeySelective(StandardSportMarketOdds standardSportMarketOdds);

    Long getRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds, Long marketGategoryId);

    Long createRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds, StandardSportMarket standardSportMarket);

    List<StandardSportMarketOdds> getItemList(Long marketId);

    List<StandardSportMarketOdds> getMarketOddsByMatchIdList(List<Long> standardSportMarketIdList);

    String adjustmentTxCreateRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds, StandardMarketDataMessage standardSportMarket);

    void upStandardOddsList(String linkId, Long standardMatchId, List<StandardSportMarketOdds> upOddsList);

}
