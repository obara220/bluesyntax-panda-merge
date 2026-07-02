package com.panda.merge.dubbo;

import com.panda.merge.api.IThirdMarketCategoryPutApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 三方盘口赔率相关信息接入API
 * @author  tell
 * @since   2020年11月6日14:59:55
 * */
@Slf4j
@Component
@DubboService
public class ThirdMarketCategoryPutApiImpl extends BaseProcessor implements IThirdMarketCategoryPutApi {
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Override
    public Response pushThirdMarketCategory(Request<ThirdMatchMarketDTO> request){
        Response response = Response.success();
        try{
            thirdMatchMarketProcessor.accessMatchMarketData(request);
        }catch (Exception e){
            response = Response.failed();
        }
        return response;
    }



}
