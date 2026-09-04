package com.panda.merge.service;

import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.odds.StandardMarketModification;
import com.panda.merge.dto.odds.StandardMarketOddsModification;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketOdds;
import com.panda.merge.model.ThirdMarketCategoryField;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/14 <br>
 * @see com.panda.merge.service <br>
 */
public interface StandardSportMarketOddsService {
    StandardSportMarketOdds getItem(String dataSourceCode, String thirdOddsFieldSourceId, Long standardMarketId);

    StandardSportMarketOdds create(String linkId, boolean isOutRight, StandardSportMarket standardSportMarket, ThirdMarketOddsDTO thirdMarketOddsDTO, ThirdMarketCategoryField thirdSportOddsFieldsTemplet);

    void convertStandardTeam(String linkId, StandardSportMarketOdds standardSportMarketOdds, StandardSportMarket standardSportMarket);

    StandardSportMarketOdds create(String linkId,StandardSportMarketOdds standardSportMarketOdds);

    StandardSportMarketOdds updateByPrimaryKeySelective(StandardSportMarketOdds standardSportMarketOdds);

    Long getRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds,Long marketGategoryId);

    Long createRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds, StandardSportMarket standardSportMarket);

    <M extends StandardMarketModification, O extends StandardMarketOddsModification> Long createRelationMarketOddsId(O standardSportMarketOdds,
                                                                                                                     M standardSportMarket);

    List<StandardSportMarketOdds> getItemList(Long marketId);

    String adjustmentTxCreateRelationMarketOddsId(StandardSportMarketOdds standardSportMarketOdds, StandardMarketDataMessage standardSportMarket);

    List<StandardSportMarketOdds> getMarketOddsByMatchIdList(List<Long> standardSportMarketIdList);
}
