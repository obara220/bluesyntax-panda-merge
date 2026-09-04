package com.panda.merge.service;

import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.calculation.CalculationService;
import com.panda.merge.dto.AbstractSportScores;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.model.*;

/**
 * 比分中心服务
 */
public interface IScoresService {

    /**
     * 计算比分
     * @param sportId
     * @return
     */
    CalculationService getCalculationService(Long sportId);

    /**
     * 判断是否Livedata停止事件
     * @param thirdMatchId
     * @return
     */
    boolean  isLivedataStoped(Long thirdMatchId);

    /**
     * 检查标准比分
     * @param thirdMatchInfo
     * @param matchScoresInfo
     * @return
     */
    boolean checkStandardScore( ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo);

    /**
     * 检查标准比分
     * @param thirdMatchInfo
     * @return
     */
    boolean checkStandardScore( ThirdMatchInfo thirdMatchInfo );

    /**
     * 查询标准赛事比分
     * @param standardId
     * @return
     */
    MatchScoresInfo searchMatchScoreByStandardId( Long standardId);

    /**
     * 校验赛事ID
     * @param standardId
     * @return
     */
    Long checkStandardScore(Long standardId);

    /**
     * 主客队比分调换
     * @param standardScore
     */
    void changeHomeAway(MatchScoresBetterDto standardScore);
    /**
     * 主客队比分调换
     * @param standardScore
     */
    void changeHomeAway(MatchScoresInfo standardScore,ThirdMatchInfo thirdMatchInfo);

    /**
     * 创建赛事比分信息
     * @param data
     * @param thirdMatchInfo
     * @return
     */
    MatchScoresInfo  createMatchScoresInfo(MatchEventInfo data, ThirdMatchInfo thirdMatchInfo);

    /**
     * 创建PD赛事比分信息
     * @param thirdMatchInfo
     * @return
     */
    MatchScoresInfo  createPDMatchScoresInfo(ThirdMatchInfo thirdMatchInfo);

    /**
     * 查询Live赛事比分信息
     * @param thirdMatchId
     * @return
     */
    MatchScoresInfo selectLiveMatchScoreInfo(Long thirdMatchId);

    /**
     * 根据第三方赛事查询标准赛事ID
     * @param standardMatchId
     * @return
     */
    Long selectAoMatchId(Long standardMatchId);

    /**
     * 开售校验
     * @param thirdMatchInfo
     * @param standardMatchInfo
     * @return
     */
    boolean  ifMatchSoldByThirdMatchId(ThirdMatchInfo thirdMatchInfo,StandardMatchInfo standardMatchInfo);

    /**
     * 事件阶段比分
     * @param thirdMatchId
     * @return
     */
    Long getSoldEventScoresPeriod(Long thirdMatchId);

    MatchScoresInfo checkBasketPeriodAndSixScore(MatchScoreAndTimeVo matchScoreAndTimeVo, Long period, Long secondFromStart);

    /**
     * 当存在比分数据时，查询赛事时间表，如该表为空，按比分表数据初始化赛事时间表
     *
     * @param matchScoresInfo 比分数据
     * @param roundType       赛制
     */
    void initMatchTimeInfoByMatchScoresInfo(MatchScoresInfo matchScoresInfo, Integer roundType);
}
