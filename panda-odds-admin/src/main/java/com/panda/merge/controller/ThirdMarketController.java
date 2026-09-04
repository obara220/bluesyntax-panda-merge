package com.panda.merge.controller;

import com.panda.merge.api.IThirdMarketStatusApi;
import com.panda.merge.dto.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Api(value = "紧急关盘处理", tags = {"紧急关盘处理"})
@RestController
@RequestMapping(value = "/pushThirdMarketData")
@Slf4j
public class ThirdMarketController {

    @Autowired
    private IThirdMarketStatusApi iThirdMarketStatusApi;

    @PostMapping("/pushThirdMarketData")
    @ApiOperation(value = "根据三方盘口源id推送关盘", httpMethod = "POST")
    public Response pushThirdMarketData(@RequestBody @ApiParam(name = "三方盘口源id", value = "三方盘口源id", required = true) String thirdMarketSourceId) {
        return iThirdMarketStatusApi.putThirdMarketStatus(StringUtils.trim(thirdMarketSourceId));
    }
}