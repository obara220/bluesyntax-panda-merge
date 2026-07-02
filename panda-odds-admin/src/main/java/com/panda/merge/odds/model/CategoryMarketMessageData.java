package com.panda.merge.odds.model;

import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.MarketCategorySell;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * CategoryMarketMessageData
 *
 * @description: 玩法级盘口信息 用于参数传递及中间状态保存
 * @date: 4/15/2025
 **/
public class CategoryMarketMessageData {

    public final MatchMarketMessageData matchData;

    public Long categoryId;

    public List<StandardMarketMessage> marketMessages;

    public MarketCategorySell marketCategorySell;

    public boolean categoryClose;

    // 操盘设置状态盘口数量
    public int operatorCount;

    public Boolean multipleDataSource;

    public CategoryMarketMessageData(Long categoryId, MatchMarketMessageData matchMarketMessageData) {
        this.categoryId = categoryId;
        this.marketMessages = new ArrayList<>();
        this.matchData = matchMarketMessageData;
    }

    public boolean allClosed() {
        if (CollectionUtils.isEmpty(marketMessages)) {
            return false;
        }
        for (StandardMarketMessage message : marketMessages) {
            if (message.getStatus() != 2) {
                return false;
            }
        }
        return true;
    }

    public boolean marketSource(){
        if (CollectionUtils.isEmpty(marketMessages)) {
            return false;
        }
        for (StandardMarketMessage message : marketMessages) {
            if (message.getMarketSource() == 1) {
                return false;
            }
        }
        return true;
    }

    public boolean hasMultipleDataSources() {
        if (multipleDataSource == null) {

            multipleDataSource = marketMessages
                    .stream()
                    .map(market -> market.getDataSourceCode() + "_" + market.getInternalDataSourceCode())
                    .distinct()
                    .count() > 1;
        }
        return multipleDataSource;
    }

}
