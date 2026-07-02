package com.panda.merge.service;

import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.model.ConfigTradeType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/21 <br>
 * @see com.panda.merge.service <br>
 */
public interface ConfigTradeTypeService {


    ConfigTradeType getItemMatch(String standardMatchId);

    Map<String,ConfigTradeType> getItemMatchDB(Long standardMatchId);

    ConfigTradeType getItemCategory(String standardMatchId, String standardCategoryId);

    Map<Long, Integer> getItemByMatchAndCategorys(String standardMatchId, Set<Long> marketCategoryIdSet);

    ConfigTradeType createMatch(TradeMarketConfigDTO tradeMarketConfigDTO);

    ConfigTradeType updateMatch(ConfigTradeType configTradeType);

    ConfigTradeType createCategory(TradeMarketConfigDTO tradeMarketConfigDTO, String categoryId);

    ConfigTradeType updateCategory(ConfigTradeType configTradeType);

    ConfigTradeType createCategory(ConfigTradeType configTradeType, String categoryId);

    int deleteCategoryByStandardMatchId(String standardMatchId);

    /**
     * 批量新增
     * @param standardMatchId 标准赛事id
     * @param dataList List<ConfigTradeType>
     */
    void saveBatch(String linkId, Long standardMatchId, List<ConfigTradeType> dataList);

    /**
     * 批量修改
     * @param standardMatchId 标准赛事id
     * @param updatedataList List<ConfigTradeType>
     * @param updateConfigTradeType 更新的内容
     */
    void updateByExample(Long standardMatchId, List<ConfigTradeType> updatedataList, ConfigTradeType updateConfigTradeType);
}
