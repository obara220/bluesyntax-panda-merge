package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleInfo;
import com.panda.merge.model.MatchSettleScore;

import java.util.List;

/**
 * 结算2.0 dubbo服务
 * */
public interface IBasketballMatchScoresSettleApi {

    /**
     * 比分查询
     * */
    List<MatchSettleScoreDto>  searchMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto);

    /**
     * 查询赛事所属联赛等级是否开启了即时开关
     * @param matchSettleScoreSearchDto
     * @return
     */
    Boolean getRealTimeOnOff(MatchSettleScoreSearchDto matchSettleScoreSearchDto);

    /**
     * 三方比分查询
     * */
    ThirdMatchSettleScoresDto  searchThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto);


    /**
     * 比分编辑
     * */
    Response   updateMatchSettleScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto);
    /**
     * 比分确认
     * */
    Response   confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto);
    /**
     * 比分结算
     * */
    Response   settleMatchScore(SettleMatchScoreDto matchSettleScoreDto);
    /**
     * 重新比分结算(程序重跑)
     * */
    Response  reSettleMatchScore(UpdateBasketBallSettleScoreDto matchSettleScoreDto);

    /**
     * 回滚比分结算(回滚比分)
     * */
    Response  rollBackSettleMatchScores(UpdateBasketBallSettleScoreDto matchSettleScoreDto);




    /**
     *  查询结算方式 1 还是 2
     * 传参：
     * 返回:
     * */
    Response querySettleType(Long StandardMatchId);

    /**
     * 删除结算比分数据源不一致提示
     * @param matchSettleSwitcherDto
     * @return
     */
    Response cancelSettleEventTag(MatchSettleSwitcherDto matchSettleSwitcherDto) ;


    /**
     * 比分结算后弹出带入比分
     * @param updateBasketBallSettleScoreDto
     * @return
     */
    Response  showPopupScore(UpdateBasketBallSettleScoreDto updateBasketBallSettleScoreDto);

    /**
     * 高级权限带入比分
     * @param basketBallPutInJsonDto
     * @return
     */
    Response  confirmBringInScore(BasketBallPutInJsonDto basketBallPutInJsonDto);

    void verifyScoresIsSame(MatchSettleScore matchSettleScore);

    void verifyScoresIsSame(Long standardMatchId);

    /**
     * 编辑比分时带入主数据源的比分
     * @param updateBasketBallSettleScoreDto
     * @return
     */
    Response  editShowScore(UpdateBasketBallSettleScoreDto updateBasketBallSettleScoreDto);

    Response  cancelDeleteStatus(MatchSettleSwitcherDto matchSettleSwitcherDto);

    MatchSettleInfo searchMatchDelStatus(MatchSettleScoreSearchDto settleScoreSearchDto);
}
