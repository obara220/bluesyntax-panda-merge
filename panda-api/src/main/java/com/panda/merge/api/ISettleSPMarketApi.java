package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.settle.*;


public interface ISettleSPMarketApi {

    /**
     * 查询
     * */
    Response searchSPMarketSettleList(SPMarketSettleListRequest spMarketSettleListRequest);

    /**
     * 编辑投注项
     *   2 走水 3输  4赢  7取消 
     * */
    Response editSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto);
    /**
     * 确认投注项
     * */
    Response confirmSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto);

    /**
     * 结算投注项
     * */
    Response settleSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto);

    /**
     * 回滚投注项
     * */
    Response rollbackSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto);


    /**
     * 重跑
     * */
    Response reSettleSpOddsResult(EditMatchSettleSPOddsDto editMatchSettleSPOddsDto);
}
