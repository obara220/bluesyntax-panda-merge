package com.panda.merge.rocketmq.processor;


import com.panda.merge.common.BaseProcessor;
import com.panda.merge.dto.Request;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.odds.service.PreSoldReportService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * PresoldMessageToOddsProcessor
 *
 * @description: 预售处理
 * @date: 1/25/2025
 **/
@Component
@Slf4j
public class PreSoldMessageToOddsProcessor extends BaseProcessor {

    @Autowired
    private PreSoldReportService preSoldReportService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    public void process(Request<StandardSportMarketSell> request) {
        StandardSportMarketSell data = request.getData();
        if (!StringUtils.equalsIgnoreCase(data.getStatus(), "Enable") || Objects.isNull(data.getMatchInfoId())) {
            log.warn("{} unsatisfied pre sold message: {}", request.getLinkId(), data);
            return;
        }

        preSoldReportService.setCache(data);
        standardSportMarketSellService.evictCache(data.getMatchInfoId());
    }
}
