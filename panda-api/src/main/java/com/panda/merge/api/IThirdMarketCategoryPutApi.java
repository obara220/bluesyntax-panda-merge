package com.panda.merge.api;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.ThirdMatchMarketDTO;


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

}
