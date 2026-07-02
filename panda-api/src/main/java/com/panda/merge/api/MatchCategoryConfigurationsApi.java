package com.panda.merge.api;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.message.MatchMarketCategoryConfigurationMessage;

/**
 * @descriptions: 开盘数据服务商及需要开盘玩法的配置
 * @author: una
 * @date: 2026/3/2 17:28
 */
public interface MatchCategoryConfigurationsApi {
    /**
     * 设置操盘手根据风控请求的联赛玩法模板数据进行玩法配置
     * @param messageRequest
     * @return
     */
    Response handleCategoryConfigurations(Request<MatchMarketCategoryConfigurationMessage> messageRequest);
}
