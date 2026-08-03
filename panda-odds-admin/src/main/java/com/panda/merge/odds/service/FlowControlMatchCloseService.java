package com.panda.merge.odds.service;

import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.TradeMarketConfigDTO;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

/**
 * FlowControlMatchCloseService
 *
 * @description:
 * @date: 7/17/2025
 **/
@Service
@Slf4j
public class FlowControlMatchCloseService implements DisposableBean {

    @Lazy
    @Autowired
    private ITradeMarketConfigApi tradeMarketConfigApi;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private final ExecutorService executorService = new ThreadPoolExecutor(2,
                                                                           40,
                                                                           60L,
                                                                           TimeUnit.SECONDS,
                                                                           new LinkedBlockingQueue<>(10000),
                                                                           new ThreadPoolExecutor.DiscardOldestPolicy());

    public void scheduledMatchClose(String linkId, List<Long> fcMatchids) {
        scheduledExecutorService.schedule(() -> {
            try {
                matchClose(linkId, fcMatchids);
                log.info("linkId:{},scheduledMatchClose finished", linkId);
            } catch (Exception e) {
                log.error("scheduledMatchClose error", e);
            }
        }, 2, TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        scheduledExecutorService.shutdown();
        executorService.shutdown();
        scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    private void matchClose(String linkId, List<Long> fcMatchids) {

        List<ThirdMatchInfo> thirdMatches = thirdMatchInfoService.getItems(fcMatchids, null);
        thirdMatches.forEach(thirdMatchInfo -> executorService.submit(() -> {
            String newLinkId = UUIdUtils.getId() + "_FLOW";
            TradeMarketConfigDTO config = new TradeMarketConfigDTO();
            config.setLevel(3);
            config.setTargetId(String.valueOf(thirdMatchInfo.getThirdMatchSourceId()));
            config.setSourceSystem(3);
            config.setAddition1(thirdMatchInfo.getDataSourceCode());
            config.setMarketStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
            Request<TradeMarketConfigDTO> request = new Request<>(config, newLinkId);
            tradeMarketConfigApi.putTradeMarketConfig(request);
            log.info("linkId:{},thirdMatchId:{}, match close", newLinkId, thirdMatchInfo.getId());
        }));

    }

}
