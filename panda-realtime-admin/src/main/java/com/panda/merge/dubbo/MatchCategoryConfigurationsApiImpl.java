package com.panda.merge.dubbo;

import com.panda.merge.api.MatchCategoryConfigurationsApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.message.MatchMarketCategoryConfigurationMessage;
import com.panda.merge.rocketmq.processor.MatchCategoryConfigruationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @descriptions: 开盘数据服务商及需要开盘玩法的配置
 * @author: una
 * @date: 2026/3/2 17:38
 */

@Slf4j
@Component
@DubboService
public class MatchCategoryConfigurationsApiImpl implements MatchCategoryConfigurationsApi {
    @Autowired
    MatchCategoryConfigruationProcessor matchCategoryConfigruationProcessor;

    @Override
    public Response handleCategoryConfigurations(Request<MatchMarketCategoryConfigurationMessage> messageRequest) {
        String linkId = messageRequest.getLinkId();
        boolean isSuccessFlag = true;
        log.info("::{}::handleCategoryConfigurations 接口请求开始", linkId);
        try {
            matchCategoryConfigruationProcessor.handleCategoryConfigrations(messageRequest);
        } catch (Exception e) {
            isSuccessFlag = false;
            log.error("::{}::handleCategoryConfigurations 接口请求处理异常,异常原因:{}", linkId, e.getMessage(), e);
        }
        log.info("::{}::handleCategoryConfigurations 接口请求结束", linkId);
        if (!isSuccessFlag) {
            return Response.failed();
        }
        return Response.success();
    }
}
