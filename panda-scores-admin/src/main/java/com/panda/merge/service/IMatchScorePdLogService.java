package com.panda.merge.service;


import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.BasketballScoresPDDto;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.model.MatchFreezeSettlePdLogEvent;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.snooker.dto.MatchCommonLogDto;

import java.util.Map;

/**
 * 比分操作日志
 */
public interface IMatchScorePdLogService {

    /**
     * 比赛开始操作日志
     * @param matchDto
     * @param matchTimeInfo
     */
    void matchBeginLog(TennisAdvertiseDto matchDto , MatchTimeInfo matchTimeInfo);

    /**
     * 比赛结束操作日志
     * @param matchDto
     * @param matchTimeInfo
     */
    void matchEndLog(TennisAdvertiseDto matchDto , MatchTimeInfo matchTimeInfo);

    /**
     * 比赛恢复操作日志
     * @param matchDto
     */
    void matchStatusReSetLog(TennisAdvertiseDto matchDto );

    /**
     * 比赛赛制设置
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param matchDto
     */
    void setRoundTypeLog(MatchTimeInfo matchTimeInfoOid , MatchTimeInfo matchTimeInfo, PDRoundTypeEditDto matchDto );

    /**
     * 比赛局制设置
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param matchDto
     */
    void setMatchLengthLog(MatchTimeInfo matchTimeInfoOid , MatchTimeInfo matchTimeInfo, TennisAdvertiseDto matchDto);

    /**
     * 局内比分设置
     * @param matchScoresInfoOid
     * @param matchScoresInfo
     * @param dto
     */
    void setMatchSecondScoreLog(MatchScoresInfo matchScoresInfoOid , MatchScoresInfo matchScoresInfo, TennisEditSecondScoreDto dto);

    /**
     * 更新局内比分
     * @param matchScoresInfoOid
     * @param matchScoresInfo
     * @param dto
     */
    void updateSecondScoreLog(MatchScoresInfo matchScoresInfoOid , MatchScoresInfo matchScoresInfo, TennisEditSecondScoreDto dto);

    /**
     * 盘开始  盘结束
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param pdTennisSetStatusDto
     */
    void changeSetStatusLog(MatchTimeInfo matchTimeInfoOid , MatchTimeInfo matchTimeInfo, PDTennisSetStatusDto pdTennisSetStatusDto);

    /**
     * 局开始  局结束
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param pdTennisRoundStatusDto
     */
    void changeRoundStatusLog(MatchTimeInfo matchTimeInfoOid , MatchTimeInfo matchTimeInfo, PDTennisRoundStatusDto pdTennisRoundStatusDto);

    /**
     * 设置最大局数
     * @param matchTimeInfoOid
     * @param matchTimeInfo
     * @param pdTennisRoundStatusDto
     * @param id
     */
    void setMaxRoundLog(MatchTimeInfo matchTimeInfoOid , MatchTimeInfo matchTimeInfo, MatchTennisEditMaxRoundDto pdTennisRoundStatusDto,Long id);

    /**
     * 直接编辑盘比分
     * @param matchScoresInfoOid
     * @param matchScoresInfo
     * @param dto
     * @param id
     */
    void setSetScoreLog(MatchScoresInfo matchScoresInfoOid  , MatchScoresInfo matchScoresInfo, MatchTennisEditSetScoreDto dto,Long id);

    /**
     * 修改开赛时间操作日志
     * @param changeMatchStartTimeDto 修改后传参数
     * @param oldBeginTime 修改前传参数 1
     */
    void changeMatchStartTimeLog(ChangeMatchStartTimeDto changeMatchStartTimeDto, Long oldBeginTime, StandardMatchInfo standardMatchInfo);

    /**
     * 开球
     * @param kickOff
     * @param matchScoreAndTimeVo
     */
    void kickOffLog(KickOffDto kickOff,MatchScoreAndTimeVo matchScoreAndTimeVo);

    /**
     * 进球后开球
     *
     * @param kickOff             开球
     * @param matchScoreAndTimeVo 三方数据
     */
    void kickOffAfterGoalLog(KickOffDto kickOff, MatchScoreAndTimeVo matchScoreAndTimeVo);

    /**
     * 赛事 危险安全设置日志
     * @param isDangerDto
     * @param matchScoreAndTimeVo
     */
    void isDangerLog(IsDangerDto isDangerDto,MatchScoreAndTimeVo matchScoreAndTimeVo);

    /**
     * 重新计算盘比分
     * @param matchScoresInfoOid
     * @param matchScoresInfo
     * @param tennisReSetScoreDto
     * @param id
     */
    void reCountSetScoreLog(MatchScoresInfo matchScoresInfoOid  , MatchScoresInfo matchScoresInfo ,
                            MatchTennisReSetScoreDto tennisReSetScoreDto, Long id);

    /**
     * 赛事开关封锁
     * @param matchScoreAndTimeVo
     * @param changeMatchStatus
     */
     void changeMatchStatusLog(MatchScoreAndTimeVo matchScoreAndTimeVo,ChangeMatchStatusDto changeMatchStatus);

    /**
     *  点击结束赛事事件
     * @param matchScoreAndTimeVo
     * @param changeMatchStatus
     */
    void setMatchEndLog(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusDto changeMatchStatus);

    void setMatchCommonLog(MatchScoreAndTimeVo matchScoreAndTimeVo, MatchCommonLogDto matchCommonLogDto);

    /**
     * 记录删除赛事事件，区分事件类型：进球、黄牌、角球等
     * @param matchScoreAndTimeVo
     * @param deleteEventDto
     */
    void deleteEventLog(MatchScoreAndTimeVo matchScoreAndTimeVo, DeleteEventDto deleteEventDto, CommonItem commonOldItem,CommonItem commonNewItem,MatchScoresEventInfo oldEvent);

    /**
     * 点球重踢
     */
    void retakePenLog(MatchScoreAndTimeVo matchScoreAndTimeVo, MatchScoresEventInfo oldEvent, RetakePenDto retakePenDto);

    /**
     * 没有重踢
     */
    void noRetakePenLog(MatchScoreAndTimeVo matchScoreAndTimeVo, MatchScoresEventInfo oldEvent, NoRetakePenDto noRetakePenDto);

    /**
     * 记录编辑事件
     *
     * @param matchScoreAndTimeVo 赛事相关信息
     * @param editEventDto        编辑数据
     * @param commonOldItem       编辑前比分
     * @param commonNewItem       编辑后比分
     * @param oldEvent            赛事消息记录
     */
    void editEventLog(MatchScoreAndTimeVo matchScoreAndTimeVo, PDBasketBallEditEventDto editEventDto, CommonItem commonOldItem, CommonItem commonNewItem, MatchScoresEventInfo oldEvent);

    /**
     * 可能比分事件
     * @param possibleEventDto
     */
    void possibleEventLog(PossibleEventDto possibleEventDto);

    /**
     * 确认比分事件
     * @param confirmEventDto
     */
    void confirmEventLog(ConfirmEventDto confirmEventDto);

    /**
     * 取消比分事件
     * @param cancelEventDto
     */
    void cancelEventLog(CancelEventDto cancelEventDto);

    /**
     * 点球重踢后的确认日志
     * @param data
     * @param confirmPenaltyEventDTO
     */
    void confirmPenaltyEventLog( MatchScoreAndTimeVo data, ConfirmPenaltyEventDTO confirmPenaltyEventDTO);

    /**
     * 人工下发的var事件日志
     * @param eventOperationDto
     * @param matchScoreAndTimeVo 三方赛事ID
     */
    void addVarEventLog(EventOperationDto eventOperationDto,MatchScoreAndTimeVo matchScoreAndTimeVo);

    /**
     * 更改赛事时间
     * @param matchScoreAndTimeVo
     * @param secondFromStart
     * @param changeMatchTimeDto
     */
    void changeMatchTimeLog(MatchScoreAndTimeVo matchScoreAndTimeVo,Long secondFromStart,ChangeMatchTimeDto changeMatchTimeDto);

    /**
     * 打印PD报球版结算冻结日志
     * @param matchFreezeSettlePdLogEvent
     */
    void matchFreezeSettleLog(MatchFreezeSettlePdLogEvent matchFreezeSettlePdLogEvent);

    /**
     * 修改比分日志
     * @param data
     * @param changeMatchScoreDto
     */
    void changeMatchScoreLog(Map<String,String> oldScoreMap, MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto);

    /**
     * 修改篮球、冰球的赛事阶段
     * @param matchScoreAndTimeVo
     * @param changeMatchPeriodDto
     */
    void changeMatchPeriodLog(MatchScoreAndTimeVo matchScoreAndTimeVo,Long periodId, ChangeMatchPeriodDto changeMatchPeriodDto);

    /**
     * 报球板足球5分钟进球编辑
     * @param data
     * @param scoresJson
     * @param confirmEventDto
     */
    void modify5MinScoreLog(String scoresJson,MatchScoreAndTimeVo data, Goal5MinDto confirmEventDto);


    /**
     * 报球板足球15分钟进球编辑
     * @param data
     * @param scoresJson
     * @param confirmEventDto
     */
    void modify15MinScoreLog(String scoresJson,MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto);

    /**
     * 报球板足球5分钟角球编辑
     * @param linkId
     * @param scoresJson
     * @param data
     * @param confirmEventDto
     */
    void modify15MinCornerLog(String linkId, String scoresJson,MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto);

    /**
     * 罚牌15分钟编辑日志
     * @param scoresJson
     * @param data
     * @param confirmEventDto
     */
    void modify15MinCardLog(String linkId, String scoresJson, MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto);


    /**
     * 点球大战编辑比分
     * @param extryScore
     * @param afterData
     * @param penaltyScoresEditDto
     */
    void editPenaltyScoreLog(String extryScore,MatchScoreAndTimeVo afterData, PenaltyScoresEditDto penaltyScoresEditDto);

    /**
     * 点球大战阶段切换
     * @param data
     * @param penaltyChangeRoundsDto
     */
    void changePenaltyRoundsLog(MatchScoreAndTimeVo data,PenaltyChangeRoundsDto penaltyChangeRoundsDto);

    /**
     * 新增点球大战阶段
     * @param data
     * @param penaltyAddRoundsDto
     */
    void addPenaltyRoundsLog(MatchScoreAndTimeVo data, PenaltyAddRoundsDto penaltyAddRoundsDto);

    /**
     * 改变点球大战先罚
     * @param data
     * @param penaltyFirstDto
     */
    void changePenaltyFirstLog(MatchScoreAndTimeVo data, PenaltyFirstDto penaltyFirstDto);


    /**
     * 开始点球事件日志操作
     *
     * @param data
     * @param takePenaltyDto
     */
    void takePenaltyLog(MatchScoreAndTimeVo data, TakePenaltyDTO takePenaltyDto);

    /**
     *
     * @param data
     * @param penaltyAboutToBeTakenDTO
     */
    void penaltyAboutToBeTakenLog(MatchScoreAndTimeVo data, PenaltyAboutToBeTakenDto penaltyAboutToBeTakenDTO);

    /**
     * 修改赛制时间
     * @param data
     * @param changeMatchLengthDto
     */
    void changeMatchLenthLog(MatchScoreAndTimeVo data,ChangeMatchLengthDto changeMatchLengthDto);

    /**
     * 冰球大、小罚比分记录
     * @param data
     * @param editFaScoreDto
     */
    void editFaScoreLog(MatchScoreAndTimeVo data,String scoresJson, EditFaScoreDto editFaScoreDto);

    /**
     * 网球球权日志
     * @param data
     * @param tennisAdvertiseDto
     */
    void setMatchOpenBallLog(MatchScoreAndTimeVo data,TennisEditSecondScoreDto tennisAdvertiseDto);

    /**
     * 滚球中途切PD,参与结算的开关更新
     * @param updateSettleStatusDto
     */
    void updateSettleStatusLog(UpdateSettleStatusDto updateSettleStatusDto);

    void modifyCancelMatchEndLog(Integer secondFromStart, Long periodId, String userName, String address, String userId, MatchScoreAndTimeVo data);

    void modifyEditAllScoreLog(AllFootballScoreEditDto allFootballScoreEditDto, MatchScoreAndTimeVo data);

    void modifyEditAllScoreLog(AllFootballScoreEditDto allFootballScoreEditDto, MatchScoreAndTimeVo data, Map<Long, FootballScores> oldScores, Map<Long, FootballScores> allPeriodScores);

    /**
     * 伤停补时事件日志
     *
     * @param data               赛事积分时间
     * @param injuryTimeEventDto 伤停补时数据
     */
    void injuryTimeEventLog(MatchScoreAndTimeVo data, InjuryTimeEventDto injuryTimeEventDto);

    /**
     * 伤停补时事件日志
     *
     * @param data               赛事积分时间
     * @param timeStatusEventDto 时间状态数据
     */
    void timeStatusEventLog(MatchScoreAndTimeVo data, TimeStatusEventDto timeStatusEventDto);

    /**
     * 确认事件
     *
     * @param response     oldScore 旧比分
     * @param response     newScore 新比分
     * @param response     response data
     * @param sendEventDto 发送事件
     */
    void sendEventLog(BasketballScoresPDDto oldScore, BasketballScoresPDDto newScore, Response<MatchScoreAndTimeVo> response, PDBasketBallSendEventDto sendEventDto);

    /**
     * 下发球未命中 命中 取消(不发) // 1 未命中 2投篮命中 3取消投篮
     *
     * @param responseBuffer 旧数据
     * @param response       新数据
     * @param sendBallDto    发球事件
     */
    void sendBallLog(Response<MatchScoreAndTimeVo> responseBuffer,Response<MatchScoreAndTimeVo> response, PDBasketBallSendBallDto sendBallDto);

    /**
     * 2分&3分球
     * 1 未命中 2投篮命中 3取消投篮
     *
     * @param response    data
     * @param sendBallDto 发球事件
     */
    public void sendBallLog(BasketballScores oldScore, BasketballScores newScore, Response<MatchScoreAndTimeVo> response, PDBasketBallSendBallDto sendBallDto);

    void sendFreeThrowLog(SetFreeThrowDto setFreeThrowDto, Response<MatchScoreAndTimeVo> response);

    /**
     * 跳球
     *
     * @param thirdMatchId 三方赛事ID
     * @param homeAway     主客队
     */
    void wonJumpBallLog(Long thirdMatchId, String homeAway);

    /**
     * 比赛开始
     *
     * @param thirdMatchId 三方赛事ID
     */
    void gameStartLog(Long thirdMatchId);

    /**
     * 比赛直接结束
     *
     * @param thirdMatchId 三方赛事ID
     */
    void gameEndLog(Long thirdMatchId);

    /**
     * 下一个开打阶段需要传阶段
     *
     * @param periodDto 阶段数据
     */
    void nextPeriodLog(PDBasketBallNextPeriodDto periodDto);

    /**
     * 小节休息或者中场休息
     *
     * @param takeRestDto 小节休息
     */
    void takeRestLog(PDBasketBallTakeRestDto takeRestDto);

    /**
     * 暂停或者继续
     *
     * @param response 数据
     * @param parseContinueDto 比赛进行状态
     */
    void parseContinueLog(Response<MatchScoreAndTimeVo> response, PDBasketBallPauseDto parseContinueDto);

    /**
     * 中断或者重开
     *
     * @param response 数据
     * @param parseContinueDto 比赛进行状态
     */
    void breakOrReStartLog(Response<MatchScoreAndTimeVo> response, PDBasketBallParseContinueDto parseContinueDto);

    /**
     * 修改跳球比分
     *
     * @param response data
     * @param dto      跳球数据
     */
    void changeJumpWonScoreLog(Response<MatchScoreAndTimeVo> response, PDBaskectBallMatchStartDto dto);

    /**
     * 编辑6分钟比分
     *
     * @param oldAllScores 旧数据
     * @param response     数据
     * @param dto          比分数据
     */
    void editSixScoreLog(Map<Long, BasketballScores> oldAllScores, Response<MatchScoreAndTimeVo> response, PDBasketBallEditSixScoreDto dto);
}
