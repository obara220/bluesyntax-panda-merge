package com.panda.merge.advertise.service;


import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;


public interface TennisAdvertiseService {
    /**
     * 网球开始
     * @param matchDto
     * @return
     */
    Response matchBegin(TennisAdvertiseDto matchDto, Response<MatchScoreAndTimeVo> response);
    /**
     * 网球设置结束
     * @param matchDto
     * @return
     */
    Response matchEnd(TennisAdvertiseDto matchDto,Response<MatchScoreAndTimeVo> response);
    /**
     * 网球恢复
     * @param matchDto
     * @return
     */
    Response matchStatusReSet(TennisAdvertiseDto matchDto,Response<MatchScoreAndTimeVo> response);

    /**
     * 设置网球盘数(赛制)
     * @param matchDto
     * @return
     */
    Response setRoundType(PDRoundTypeEditDto matchDto, Response<MatchScoreAndTimeVo> response);
    /**
     * 设置网球局
     * @param matchDto
     * @return
     */
    Response setMatchLength(TennisAdvertiseDto matchDto, Response<MatchScoreAndTimeVo> response);

    /**
     *  网球报球版查询
     * */
    Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto);

    /**
     *  网球报球网球跳分接口
     * */
    Response setMatchSecondScore(TennisEditSecondScoreDto tennisEditSecondScoreDto, Response<MatchScoreAndTimeVo> response);

    /**
     * 判断局内比分是否合法
     * */
    boolean chargeSecondScore(TennisEditSecondScoreDto tennisEditSecondScoreDto, MatchScoreAndTimeVo matchScoreAndTimeVo);
    /**
     * 更新局内比分
     * */
    void updateSecondScore(TennisEditSecondScoreDto tennisEditSecondScoreDto, Response<MatchScoreAndTimeVo> response);
    /**
     * 更新局比分
     * */
    void updateRoundScore(Integer winFlag, TennisEditSecondScoreDto tennisEditSecondScoreDto, Response<MatchScoreAndTimeVo> response);
    /**
     * 更新盘比分
     * */
    void updateSetScore(Integer winFlag,TennisEditSecondScoreDto tennisEditSecondScoreDto, Response<MatchScoreAndTimeVo> response);

    /**
     * 盘切换按钮
     * */
    Response setFirstNum(PDFirstNumSetDto pdFirstNumSetDto, Response<MatchScoreAndTimeVo> response);
    /**
     * 盘开始，盘结束
     * */
    Response changeSetStatus(PDTennisSetStatusDto pdTennisSetStatusDto, Response<MatchScoreAndTimeVo> response);
    /**
     * 局开始，局结束
     * */
    Response changeRoundStatus(PDTennisRoundStatusDto pdTennisRoundStatusDto, Response<MatchScoreAndTimeVo> response);
    /**
     * 日志查询
     * */
    Response searchOperatorDetail(MatchAdvertiseQueryDto matchAdvertiseQueryDto);
    /**
     * 设置盘最大局数
     * */
    Response setMaxRound(MatchTennisEditMaxRoundDto dto, Response<MatchScoreAndTimeVo> response );
    /**
     * 直接编辑盘比分
     * */
    Response setSetScore(MatchTennisEditSetScoreDto matchAdvertiseQueryDto, Response<MatchScoreAndTimeVo> response);
    /**
     * 重新计算盘比分
     * */
    Response reCountSetScore(MatchTennisReSetScoreDto tennisReSetScoreDto, Response<MatchScoreAndTimeVo> response);

    /**
     * 公共比分下发
     * @param linkId
     * @param thirdMatchInfo
     * @param matchScoresInfo
     * @param matchTimeInfo
     * @param eventCode
     */
    void noteEventPush(String linkId, ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchTimeInfo matchTimeInfo, String eventCode);


    Response setMatchOpenBall(Response<MatchScoreAndTimeVo> response, TennisEditSecondScoreDto tennisAdvertiseDto);


}
