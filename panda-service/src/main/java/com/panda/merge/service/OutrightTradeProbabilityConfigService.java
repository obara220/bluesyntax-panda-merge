package com.panda.merge.service;

import com.panda.merge.dto.OutrightTradeProbabilityConfigDTO;
import com.panda.merge.model.ConfigOutrightTradeProbability;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <Description> <br>
 * 操作表config_outright_trade_probability
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2021/6/10 <br>
 * @see com.panda.merge.service <br>
 */
public interface OutrightTradeProbabilityConfigService {

    /**
     * 新增
     *
     *
     * @param linkId
     * @param outrightTradeProbabilityConfigDTO
     * @return
     */
    ConfigOutrightTradeProbability insertItem(String linkId, OutrightTradeProbabilityConfigDTO outrightTradeProbabilityConfigDTO);

    /**
     * 修改
     *
     * @param configOutrightTradeProbability
     * @return
     */
    ConfigOutrightTradeProbability updateItem(ConfigOutrightTradeProbability configOutrightTradeProbability);

    /**
     * 查询
     *
     * @param standardMatchId
     * @param standardMarketOddsId
     * @return
     */
    ConfigOutrightTradeProbability selectItem(Long standardMatchId, Long standardMarketOddsId);

    List<ConfigOutrightTradeProbability> selectItems(Map<Long, Set<Long>> matchAndOddIdsMap);

    /**
     * 根据盘口获取投注项概率差列表
     * @param standardMatchId
     * @param standardMarketId
     * @return
     */
    List<ConfigOutrightTradeProbability> getItemList(Long standardMatchId, Long standardMarketId);

    /**
     * 清理赔率差
     * @param configOutrightTradeProbability
     */
    void del(ConfigOutrightTradeProbability configOutrightTradeProbability);

}
