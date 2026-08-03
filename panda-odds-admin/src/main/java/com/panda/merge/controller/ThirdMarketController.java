package com.panda.merge.controller;

import com.panda.merge.api.IThirdMarketStatusApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.service.ThirdSportMarketOddsNewService;
import com.panda.merge.service.ThirdSportMarketOddsService;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.rocketmq.consumer.ThirdMatchMarketConsumer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.hint.HintManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "紧急关盘处理", tags = {"紧急关盘处理"})
@RestController
@RequestMapping(value = "/pushThirdMarketData")
@Slf4j
public class ThirdMarketController {

    @Autowired
    private IThirdMarketStatusApi iThirdMarketStatusApi;

    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsService;


    @Resource
    private ThirdMatchMarketConsumer thirdMatchMarketConsumer;

    @PostMapping("/pushThirdMarketData")
    @ApiOperation(value = "根据三方盘口源id推送关盘", httpMethod = "POST")
    public Response pushThirdMarketData(@RequestBody @ApiParam(name = "三方盘口源id", value = "三方盘口源id", required = true) String thirdMarketSourceId) {
        return iThirdMarketStatusApi.putThirdMarketStatus(StringUtils.trim(thirdMarketSourceId));
    }

    @PostMapping("/thirdOddsQuery")
    @ApiOperation(value = "测试盘口批量更新", httpMethod = "POST")
    public String thirdOddsQuery() {
        HintManager.clear();
        HintManager instance = HintManager.getInstance();
        instance.addDatabaseShardingValue("third_sport_market_odds","ds0");
        instance.addTableShardingValue("third_sport_market_odds","ao");
        List<ThirdSportMarketOdds> item = thirdSportMarketOddsService.getItemList("AO", 1553939722455658497L);


        thirdSportMarketOddsService.upThirdOddsAsyncList("CE","AO",item,new ArrayList<>());
        instance.close();
        return item.toString();
    }


    @PostMapping("/thirdMatchMarket")
    public void thirdMatchMarket(@RequestBody List<Request<ThirdMatchMarketDTO>> requests) {
        thirdMatchMarketConsumer.processMessageList(requests);
    }
}