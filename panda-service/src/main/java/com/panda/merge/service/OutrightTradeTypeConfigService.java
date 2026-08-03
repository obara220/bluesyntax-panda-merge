package com.panda.merge.service;

import com.panda.merge.dto.OutrightTradeTypeConfigDTO;
import com.panda.merge.model.ConfigOutrightTradeType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <Description> <br>
 * 操作表config_outright_trade_type
 * @author raulvii<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2021/6/10 <br>
 * @see com.panda.merge.service <br>
 */
public interface OutrightTradeTypeConfigService {

    /**
     * 新增
     *
     *
     * @param linkId
     * @param outrightTradeTypeConfigDTO
     * @return
     */
    ConfigOutrightTradeType insertItem(String linkId, OutrightTradeTypeConfigDTO outrightTradeTypeConfigDTO);

    /**
     * 修改
     *
     * @param configOutrightTradeType
     * @return
     */
    ConfigOutrightTradeType updateItem(ConfigOutrightTradeType configOutrightTradeType);

    /**
     * 查询
     *
     * @param outrightTradeTypeConfigDTO
     * @return
     */
    ConfigOutrightTradeType selectItem(OutrightTradeTypeConfigDTO outrightTradeTypeConfigDTO);

    /**
     * 查询map集合(Map<marketId, tradeType>)
     * @param standardMatchId
     * @param marketIdSet
     * @return
     */
    Map<Long, Integer> getTradeTypeMapByMatchId(Long standardMatchId, Set<Long> marketIdSet);


    /**
     * 获取完整的操盘方式
     * @param marketIds
     * @return
     */
    List<ConfigOutrightTradeType> getTradeTypeList( List<Long> marketIds);

    /**
     * 获取操盘方式
     *
     * @param standardMatchId
     * @param marketIds
     * @param tradeType
     * @return
     */
    List<ConfigOutrightTradeType> getTradeTypeList(Long standardMatchId, List<Long> marketIds, Integer tradeType);


    /**
     * 操盘方式集合
     * @param matchAndmarketIdsMap
     * @return
     */
    Map<Long, Integer> getTradeTypeMapByMatchIds(Map<Long, Set<Long>> matchAndmarketIdsMap);


}
