package com.panda.merge.dubbo;

import java.util.List;

import com.panda.merge.service.ThirdSportMarketOddsNewService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.panda.merge.api.IThirdMarketCategoryPutApi;
import com.panda.merge.bo.ThirdSportMarketCategoryBO;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdCategoryDTO;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.service.ThirdMarketCategoryService;
import com.panda.merge.service.ThirdSportMarketOddsService;

import lombok.extern.slf4j.Slf4j;

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

    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    
    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsService;

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

    @Override
    public Response<List<ThirdSportMarketCategoryBO>> queryThirdMarketCategory(Request<ThirdCategoryDTO> request) {
        Response response = Response.success();
        try{
            List<ThirdSportMarketCategoryBO> sportMarketCategoryBOList = thirdMarketCategoryService.queryThirdMarketCategory(request.getData());
            response.setData(sportMarketCategoryBOList);
        }catch (Exception e){
            response = Response.failed();
        }
        return response;
    }

    @Override
    public Response updateThirdMarketCategory(Request<ThirdCategoryDTO> request) {
        Response response = Response.success();
        try{
            List<ThirdSportMarketCategoryBO> sportMarketCategoryBOList = thirdMarketCategoryService.queryThirdMarketCategory(request.getData());
            response.setData(sportMarketCategoryBOList);
        }catch (Exception e){
            response = Response.failed();
        }
        return response;
    }

}
