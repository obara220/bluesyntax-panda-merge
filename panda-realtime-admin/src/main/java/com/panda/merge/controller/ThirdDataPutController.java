package com.panda.merge.controller;

import com.alibaba.fastjson.JSON;
import com.panda.merge.api.IThirdMarketCategoryPutApi;
import com.panda.merge.api.IThirdMatchInfoPutApi;
import com.panda.merge.dto.*;
import com.panda.merge.dto.nonrealttime.put.ThirdMatchInfoDTO;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.rocketmq.processor.ThirdMarketCategoryProcessor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * swagger API（主要用于调试使用）
 * */
@Slf4j
@Deprecated
@Api(value = "接入三方赛事相关信息", tags = {"接入三方赛事相关信息"})
@RestController
@RequestMapping(value = "/thirdDataPut")
public class ThirdDataPutController {

    @DubboReference
    private IThirdMatchInfoPutApi thirdMatchInfoPutApi;

    @ApiOperation(value = "推送三方联赛信息", httpMethod = "POST")
    @PostMapping("/pushThirdSportTournament")
    public Response pushThirdSportTournament(@ApiParam(name = "request", value = "请求入参", required = true) @RequestBody Request<List<ThirdSportTournamentDTO>> request){
        log.info("【ThirdDataPutController ：pushThirdSportTournament】【::"+request.getLinkId()+"::】推送三方联赛信息入参，request：{}", JSON.toJSONString(request));
        thirdMatchInfoPutApi.pushThirdSportTournament(request);
        return Response.success();
    }

    @ApiOperation(value = "推送三方赛事信息", httpMethod = "POST")
    @PostMapping("/pushThirdMatchInfo")
    public Response pushThirdMatchInfo(@ApiParam(name = "request", value = "请求入参", required = true) @RequestBody Request<List<ThirdMatchInfoDTO>> request) {
        log.info("【ThirdDataPutController ：pushThirdMatchInfo】【::"+request.getLinkId()+"::】推送三方赛事信息入参，request：{}", JSON.toJSONString(request));
        thirdMatchInfoPutApi.pushThirdMatchInfo(request);
        return Response.success();
    }


    @Autowired
    private ThirdMarketCategoryProcessor thirdMarketCategoryProcessor;


    @ApiOperation(value = "推送三方玩法信息", httpMethod = "POST")
    @PostMapping("/putPlay")
    @ExceptionHelper
    public Response putPlay(@ApiParam(name = "request", value = "请求入参", required = true) @RequestBody Request<List<ThirdMarketCategoryDTO>> request) {
        log.info("【ThirdDataPutController ：putPlay】【::"+request.getLinkId()+"::】推送三方玩法信息入参，data：{}", JSON.toJSONString(request));
        thirdMarketCategoryProcessor.putMarketCategory(request);
        return Response.success();
    }

    @ApiOperation(value = "推送三方玩法投注项信息", httpMethod = "POST")
    @PostMapping("/pushOddsFields")
    @ExceptionHelper
    public Response pushOddsFields(@ApiParam(name = "request", value = "请求入参", required = true) @RequestBody Request<List<ThirdMarketCategoryFieldDTO>> request) {
        log.info("【ThirdDataPutController ：pushOddsFields】【::"+request.getLinkId()+"::】推送三方玩法投注项信息入参，data：{}", JSON.toJSONString(request));
        thirdMarketCategoryProcessor.putMarketOddsFields(request);
        return Response.success();
    }


    @DubboReference
    private IThirdMarketCategoryPutApi thirdMarketCategoryPutApi;

    @ApiOperation(value = "推送三方盘口赔率信息", httpMethod = "POST")
    @PostMapping("/putMatchMarket")
    public Response putMatchMarket(@ApiParam(name = "request", value = "请求入参", required = true) @RequestBody Request<ThirdMatchMarketDTO> request) {
        log.info("【ThirdDataPutController ：putMatchMarket】【::"+request.getLinkId()+"::】推送三方盘口赔率信息入参，data：{}", JSON.toJSONString(request));
        return thirdMarketCategoryPutApi.pushThirdMarketCategory(request);
    }



}