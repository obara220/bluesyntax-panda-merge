package com.panda.merge.api;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMatchMarketDTO;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/20 <br>
 * @see com.panda.merge.api <br>
 */
public interface ITradeMarketOddsApi {
    /**
     * 操盘标准盘口及赔率数据处理
     * @param message
     * @return 返回值包括对应盘口信息json
     */
    Response putTradeMarketOdds(Request<StandardMatchMarketDTO> message);
}
