package com.panda.merge.controller;

import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.TradeMarketAutoDiffConfigDTO;
import com.panda.merge.model.ConfigMarketTradeItem;
import com.panda.merge.service.ConfigMarketTradeItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@ApiIgnore
@Api("水差配置服务")
@RestController
public class TraceMaxMinOddsConfigController {
    @Autowired
    private ITradeMarketConfigApi iTradeMarketConfigApi;
    @Autowired
    private ConfigMarketTradeItemService configMarketTradeItemService;

    @ApiOperation(value = "查询盘口最大最小水差配置")
    @RequestMapping(value = "/realtime/getMarketMaxMinDiffConfig", method = RequestMethod.POST)
    public Response getMarketMaxMinDiffConfig(@RequestParam String standardMatchId){
        Response response = Response.success();
        List<ConfigMarketTradeItem> marketTradeItemList = configMarketTradeItemService.getRecsByMatchId(standardMatchId);
        response.setData(marketTradeItemList);
        return response;
    };

    @ApiOperation(value = "新增水差配置")
    @RequestMapping(value = "/realtime/putTradeMarketAutoDiffConfig", method = RequestMethod.POST)
    public Response putTradeMarketConfig(@RequestBody Request<TradeMarketAutoDiffConfigDTO> request) {
        return iTradeMarketConfigApi.putTradeMarketAutoDiffConfig(request);
    }

}
