package com.panda.merge.controller;

import com.panda.merge.api.I18nMarketCategoryApi;
import com.panda.merge.dto.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author raulvii
 */
@Api(value = "标准玩法投注项多语言初始化", tags = {"标准玩法投注项多语言初始化"})
@RestController
@RequestMapping(value = "/i18nMarketCategory")
@Slf4j
public class I18nMarketCategoryController {

   /* @Autowired
    private I18nMarketCategoryApi i18nMarketCategoryApi;

    @PostMapping("/init")
    @ApiOperation(value = "标准玩法投注项多语言初始化", httpMethod = "POST")
    public Response pushThirdMarketData(@RequestBody String param) {
        log.info("开始初始化标准玩法投注项多语言,param:{}", param);
        return i18nMarketCategoryApi.initI18nMarketCategory();
    }*/
}