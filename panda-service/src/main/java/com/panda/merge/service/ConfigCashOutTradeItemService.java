package com.panda.merge.service;

import com.panda.merge.dto.ConfigCashOutTradeItemDTO;
import com.panda.merge.model.ConfigCashOutTradeItem;

import java.util.List;

public interface ConfigCashOutTradeItemService {

    List<ConfigCashOutTradeItem> getItemList(Long matchId, Integer marketType);

    ConfigCashOutTradeItem getItem(Long matchId, Integer marketType,Integer leve);

    ConfigCashOutTradeItem getItem(Long matchId, Integer marketType, Long marketCategoryId);

    ConfigCashOutTradeItem getItem(Long matchId, Integer marketType, Integer leve,Integer category_pre_status);


    void create(ConfigCashOutTradeItemDTO itemDTO);

    void update(ConfigCashOutTradeItem cashOutTradeItem, ConfigCashOutTradeItemDTO itemDTO);

}
