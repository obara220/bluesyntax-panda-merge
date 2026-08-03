package com.panda.merge.api;

import com.panda.merge.bo.ThirdSportMarketCategoryBO;
import com.panda.merge.dto.*;

import java.util.List;


/**
 * 三方盘口赔率相关信息接入API
 * @author  tell
 * @since   2020年11月6日14:59:55
 * */
public interface IThirdMarketCategoryPutApi {

    /**
     * 三方盘口赔率数据接入
     * @param  request  入参
     *  @return  Response
     * */
    Response pushThirdMarketCategory(Request<ThirdMatchMarketDTO> request);


    /**
     * 查询三方盘口列表
     * @param request
     * @return
     */
    Response<List<ThirdSportMarketCategoryBO>> queryThirdMarketCategory(Request<ThirdCategoryDTO> request);

    /**
     * 修改三方玩法配置
     * @param request
     * @return
     */
    Response updateThirdMarketCategory(Request<ThirdCategoryDTO> request);
    
}
