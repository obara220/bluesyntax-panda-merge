package com.panda.merge.controller;

import com.panda.merge.api.SystemMarketPreApi;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(value = "系统层级开关", tags = {"系统层级开关"})
@RestController
@RequestMapping(value = "/getDeterAoSrSwitch")
@Slf4j
public class SystemMarketPreController {
    @DubboReference
    private SystemMarketPreApi systemMarketPreApi;
    @Autowired
    private RedisService redisService;

    /**
     * 前端摸板{SR:开/关，AO:开/关}  1/0
     */
    @PostMapping("/SwiftSystemPreResult")
    @ApiOperation(value = "根据三方盘口源id推送关盘", httpMethod = "POST")
    public Response SwiftSystemPreResult(@RequestBody String params) {
        log.info("【SystemMarketPreController ：SwiftSystemPreResult】【::" + params + "::】成功接受请求!");
        systemMarketPreApi.saveSystemPreResultAndPush(params);
        Response response = Response.success(params);
        return response;
    }

    /**
     * 前端摸板{SR:开/关，AO:开/关}  1/0
     */
    @PostMapping("/SearchSystemPreResult")
    @ApiOperation(value = "根据三方盘口源id推送关盘", httpMethod = "POST")
    public Response SearchSystemPreResult() {
        log.info("【SystemMarketPreController ：SearchSystemPreResult】成功接受请求!");
        String SystemThirdMarketPreParams = Constant.REDIS_KEY.SYSTEM_THIRD_MARKET_PRE_PARAMS;
        Map maps = redisService.hGetAll(SystemThirdMarketPreParams);
        if (maps.isEmpty()) {
            Map<String, Integer> paramsMap = new HashMap<>();
            paramsMap.put("SR", 0);
            paramsMap.put("AO", 0);
            redisService.hSetAll(SystemThirdMarketPreParams, paramsMap);
            return Response.success(maps);
        }
        return Response.success(maps);
    }
}
