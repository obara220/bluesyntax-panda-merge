package com.panda.merge.service;

import com.panda.merge.model.ConfigMarketStatusTrade;

import java.util.List;
import java.util.Set;

public interface ConfigMarketStatusTradeService {
    ConfigMarketStatusTrade getItemOne(Long standardMatchInfoId, Long relationMarketId,int marketType);

    ConfigMarketStatusTrade create(ConfigMarketStatusTrade configMarketStatusTrade);

    ConfigMarketStatusTrade update(ConfigMarketStatusTrade configMarketStatusTrade);

    List<ConfigMarketStatusTrade> getItemList(Long standardMatchInfoId,int marketType, Set<Long> marketCategoryIdSet);
}
