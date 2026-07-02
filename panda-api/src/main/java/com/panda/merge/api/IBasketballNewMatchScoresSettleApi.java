package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.settle.BasketBallPutInJsonDto;
import com.panda.merge.dto.settle.ConfirmMatchSettleScoreDto;
import com.panda.merge.dto.settle.UpdateBasketBallSettleScoreDto;

import java.util.List;

/**
 * 结算2.0 dubbo服务
 * */
public interface IBasketballNewMatchScoresSettleApi {


    /**
     * 比分编辑
     * */
    Response   editMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto);
    /**
     * 比分确认
     * */
    Response   confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto);

    /**
     * 赛事级的重跑、冻结
     * @param settleQueryDTO
     * @return
     */
    Response matchReplayAndFreeze(SettleQueryDTO settleQueryDTO);

    /**
     * 玩法级重跑、冻结
     * @param settleQueryDTO
     * @return
     */
    Response playReplayAndFreeze(SettleQueryDTO settleQueryDTO);


    /**
     * 多审核员比分带入
     * @param basketBallPutInJsonDto
     * @return
     */
    Response  confirmBringInScore(BasketBallPutInJsonDto basketBallPutInJsonDto);


}
