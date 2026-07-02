package com.panda.merge.odds.model;


import com.panda.merge.common.enums.MatchPeriodForMatchOverEnum;
import com.panda.merge.dto.FootballCacheScores;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.odds.enums.MarketHandlingEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MatchMarketMessageData
 *
 * @description: 赛事级盘口信息模型 用于参数传递及中间状态保存
 * @date: 4/15/2025
 **/

public class MatchMarketMessageData {

    public String linkId;

    public StandardMatchInfo standardMatchInfo;

    public Map<Long, CategoryMarketMessageData> categoryMarkets;

    public Map<String, MarketCategorySell> categorySellMap;

    public Integer marketType;

    public List<StandardMarketMessage> marketMessageList;

    public MarketHandlingEnum handlingType;

    public FootballCacheScores footballCacheScores;

    public boolean isScoreCacheEmpty;

    public MatchPeriodForMatchOverEnum matchPeriodEnum;

    public MatchTime matchTime;

    public boolean isMatchTimeCacheEmpty;


    public MatchMarketMessageData(String linkId, StandardMatchInfo standardMatchInfo,
                                  MarketHandlingEnum handlingType) {
        this.linkId = linkId;
        this.standardMatchInfo = standardMatchInfo;
        this.handlingType = handlingType;
    }

    public static MatchMarketMessageData create(String linkId,
                                                StandardMatchInfo standardMatchInfo,
                                                List<StandardMarketMessage> marketMessageList,
                                                MarketHandlingEnum operationType) {
        if (CollectionUtils.isEmpty(marketMessageList)) {
            return new MatchMarketMessageData(linkId, standardMatchInfo,operationType);
        }
        MatchMarketMessageData matchMarketMessageData = new MatchMarketMessageData(linkId, standardMatchInfo,operationType);
        matchMarketMessageData.marketMessageList = marketMessageList;
        matchMarketMessageData.categoryMarkets = marketMessageList
                .stream()
                .collect(HashMap::new, (map, message) -> map.compute(message.getMarketCategoryId(), (key, value) -> {
                    if (value == null) {
                        value = new CategoryMarketMessageData(message.getMarketCategoryId(),matchMarketMessageData);
                    }
                    value.marketMessages.add(message);
                    return value;
                }), (m1, m2) -> {
                    for (Map.Entry<Long, CategoryMarketMessageData> entry : m2.entrySet()) {
                        m1.compute(entry.getKey(), (key, data) -> {
                            if (data == null) {
                                data = entry.getValue();
                            } else {
                                data.marketMessages.addAll(entry.getValue().marketMessages);
                            }
                            return data;
                        });
                    }
                });
        return matchMarketMessageData;
    }

    public List<StandardMarketMessage> toList() {
        if (MapUtils.isEmpty(categoryMarkets)) {
            return Collections.emptyList();
        }
        List<StandardMarketMessage> result = new ArrayList<>();
        for (CategoryMarketMessageData category : categoryMarkets.values()) {
            result.addAll(category.marketMessages);
        }
        return result;
    }

    public Long getMatchId(){
        return this.standardMatchInfo.getId();
    }
}
