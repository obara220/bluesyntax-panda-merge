package com.panda.merge.api;

import com.panda.merge.dto.*;

/**
 * <Description> <br>
 * 冠军操盘
 * @author raulvii<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2021/06 /10 <br>
 * @see com.panda.merge.api <br>
 */
public interface IOutrightTradeConfigApi {

    /**
     * 操盘-手自动切换
     * @param request
     * @return
     */
    Response putOutrightTradeTypeConfig(Request<OutrightTradeTypeConfigDTO> request);


    /**
     * 操盘-盘口状态
     * @param request
     * @return
     */
    Response putOutrightTradeMarketConfig(Request<OutrightTradeMarketConfigDTO> request);

    /**
     * 操盘-投注项状态
     * @param request
     * @return
     */
    Response putOutrightTradeOddsConfig(Request<OutrightTradeOddsConfigDTO> request);


    /**
     * 操盘-概率差
     * @param request
     * @return
     */
    Response putOutrightTradeProbabilityConfig(Request<OutrightTradeProbabilityConfigDTO> request);


    /**
     *  看口对应的投注项信息
     */
    Response getOutrightMarketDetail(Request<OutrightTradeMarketConfigDTO> request);

}
