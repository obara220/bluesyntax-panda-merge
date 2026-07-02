package com.panda.merge.rocketmq.processor;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.dto.Request;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.rocketmq.common.RcsHisDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_NOREALTIME;

@Slf4j
@Validated
@Component
public class RcsHisDataCleanProcessor extends BaseProcessor {
    @Autowired
    RcsHisDataService rcsHisDataService;

    /**
     * STANDARD_MATCH_OVER_DAY_CLEAN
     * 历史数据异步清理
     * @param listRequest
     */
    @ExceptionHelper
    public void cleanStandardData(Request<List<Long>> listRequest)
    {
        String linkId = listRequest.getLinkId();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ "STANDARD_MATCH_OVER_DAY_CLEAN" +"】【::"+ linkId +"::】历史数据异步清理开始...");
        if(CollectionUtils.isEmpty(listRequest.getData()))
        {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ "STANDARD_MATCH_OVER_DAY_CLEAN" +"】【::"+ linkId +"::】历史数据异步清理结束...");
            return;
        }
        listRequest.getData().forEach(e->{
            rcsHisDataService.configDataHandler(listRequest.getLinkId(),e);
            rcsHisDataService.marketCategorySellDataHandler(listRequest.getLinkId(),e);
            rcsHisDataService.operatorDataHandler(listRequest.getLinkId(),e);
            rcsHisDataService.standardSportMarketDataHandler(listRequest.getLinkId(),e);
        });

        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ "STANDARD_MATCH_OVER_DAY_CLEAN" +"】【::"+ linkId +"::】历史数据异步清理结束...");
    }

    /**
     * THIRD_MATCH_OVER_DAY_CLEAN
     * 历史数据异步清理
     * @param mapRequest
     */
    @ExceptionHelper
    public void cleanThirdData(Request<Map<Long, String>> mapRequest)
    {
        String linkId = mapRequest.getLinkId();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ "THIRD_MATCH_OVER_DAY_CLEAN" +"】【::"+ linkId +"::】历史数据异步清理开始...");
        if(MapUtils.isEmpty(mapRequest.getData()))
        {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ "THIRD_MATCH_OVER_DAY_CLEAN" +"】【::"+ linkId +"::】历史数据异步清理结束...");
            return;
        }
//        mapRequest.getData().forEach((k,v)->{
            rcsHisDataService.thirdSportMarketDataHandler(mapRequest.getLinkId(),mapRequest.getData(),mapRequest.getDataSourceCode());
            rcsHisDataService.thirdTradeDataHandler(mapRequest.getLinkId(),mapRequest.getData(),mapRequest.getDataSourceCode());
//        });
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ "THIRD_MATCH_OVER_DAY_CLEAN" +"】【::"+ linkId +"::】历史数据异步清理结束...");
    }
}
