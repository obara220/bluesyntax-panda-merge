package com.panda.merge.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.ThirdSportMarket;
import com.panda.merge.model.ThirdSportMarketOdds;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/15 <br>
 * @see com.panda.merge.service <br>
 */
public interface ThirdSportMarketOddsService {
    ThirdSportMarketOdds getItem(String dataSourceCode, String thirdOddsFieldSourceId, Long thirdMarketId);

    List<ThirdSportMarketOdds> getItemList(String dataSourceCode,Long marketId);

    ThirdSportMarketOdds create(String dataSourceCode,String linkId, boolean isOutRight, ThirdMarketOddsDTO thirdMarketOddsDTO, ThirdSportMarket thirdSportMarket, Long thirdMarketCategoryField);

    ThirdSportMarketOdds updateByPrimaryKeySelective(String dataSourceCode,ThirdSportMarketOdds thirdSportMarketOdds);

    void upThirdOddsList(String linkId, String dataSourceCode, List<ThirdSportMarketOdds> upOddsList, List<ThirdMarketOddsDTO> thirdMarketOddsDTOS);

    void upThirdOddsAsyncList(String linkId, String dataSourceCode, List<ThirdSportMarketOdds> upOddsList, List<ThirdMarketOddsDTO> thirdMarketOddsDTOS);

    void upThirdOddsListByDataSourceCode(String linkId, String dataSourceCode, List<ThirdSportMarketOdds> upOddsList);

    Long getRelationMarketOddsId(Long relationMarketId, String oddsType,String thirdOddsFieldSourceId,String addition1, Long marketGategoryId);

    Integer delOdds();

    List<ThirdSportMarketOdds> getItemListByParam(Long id,Long limit);

    void insert(List<ThirdSportMarketOdds> thirdSportMarketOdds);

    Long getMaxId();
    
    void insertMatchCategoryOddsOfRedis(String linkId, Long matchId, Set<Long> marketCategoryIdSet, Long beginTime, Long dataSourceTime);
    
    void insertMatchMarketOddsOfRedis(String linkId, Long matchId, List<StandardMarketDataMessage> standardMarketDataMessageList, Long beginTime, Long dataSourceTime);

    void deleteMatchMarketOddsOfRedis(String linkId, Long matchId, Set<StandardMarketDataMessage> standardMarketDataMessageList, Long dataSourceTime);
    
    void deleteMatchMarketOddsByActive(String linkId, Long matchId, List<StandardMarketDataMessage> standardMarketDataMessageList, Long dataSourceTime);

    void deleteMatchMarketOddsOfRedisByCategory(String linkId, Long matchId, Set<Long> categoryIds, Long dataSourceTime);
    
    void deleteMatchMarketOddsOfRedisByActive(String linkId, Long matchId, Set<StandardMarketDataMessage> marketActive, Long dataSourceTime);
}
