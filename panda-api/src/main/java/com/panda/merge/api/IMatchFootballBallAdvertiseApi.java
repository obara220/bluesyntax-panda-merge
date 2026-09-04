package com.panda.merge.api;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.*;

import java.util.List;

/**
 * KB
 * 报球版的dubbo接口
 * */
public interface IMatchFootballBallAdvertiseApi {


    /**
     * 开始
     * Integer type:
     * 返回状态
     * redis 锁
     * */
    Response matchStart(ChangeMatchStatusDto changeMatchStatus);
    /**
     * 修改阶段
     * 返回状态
     *  redis 锁
     * */
    Response changeMatchPeriod(ChangeMatchPeriodDto changeMatchPeriodDto);
    /**
     * 比赛结束
     * */
    Response setMatchEnd(ChangeMatchStatusDto changeMatchStatus);

    /**
     * 赛事详情查询接口
     * 返回赛事信息
     * */
    Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto);
    /**
     * 可能比分事件
     * */
    Response possibleEvent(PossibleEventDto possibleEventDto);
    /**
     * 确认比分事件
     *  redis 锁
     * */
    Response confirmEvent(ConfirmEventDto confirmEventDto);
    /**
     * 取消比分事件
     *  redis 锁
     * */
    Response cancelEvent(CancelEventDto cancelEventDto);

    /**
     * 下发安全危险事件
     */
    Response isDanger(IsDangerDto isDangerDto);

    /**
     * 进球后开球事件
     *
     * @param kickOff 开球
     * @return 响应开球数据
     */
    Response kickOffAfterGoal(KickOffDto kickOff);

    /**
     * 点球重踢
     *
     * @param retakePenDto 点球重踢
     * @return 响应重踢数据
     */
    Response retakePen(RetakePenDto retakePenDto);

    /**
     * 没有重踢
     *
     * @param noRetakePenDto 没有重踢
     * @return 响应重踢数据
     */
    Response noRetakePen(NoRetakePenDto noRetakePenDto);

    Response kickOff(KickOffDto kickOff);

    Response overTimeEvent(OverTimeEventDto overTimeEventDto);
    /**
     *  redis 锁
     * */
    Response deleteEvent(DeleteEventDto deleteEventDto);
    /**
     *  redis 锁
     * */
    Response editEvent(EditEventDto editEventDto);

    Response eventList(EventListDto eventListDto);

    /**
     * 点球大战编辑接口
     * */
    Response editPenaltyScore(PenaltyScoresEditDto penaltyScoresEditDto);

    /**
     * 点球大战新增轮次接口
     * */
    Response addPenaltyRounds(PenaltyAddRoundsDto penaltyAddRoundsDto);

    /**
     * 点球大战改变先罚
     * */
    Response changePenaltyFirst(PenaltyFirstDto penaltyFirstDto);

    /**
     * 改变进攻方向
     * */
    Response changeAttackDirection(AttackDirectionDto attackDirectionDto);
    /**
     * 点球大战轮次切换接口
     * */
    Response changePenaltyRounds(PenaltyChangeRoundsDto penaltyChangeRoundsDto);
    /**
     * 15分钟进球编辑接口
     * */
    Response edit15MinGoal(Goal15MinDto confirmEventDto);
    /**
     * 15分钟角球编辑接口
     * */
    Response edit15MinCorner(Goal15MinDto confirmEventDto);

    /**
     * 15分钟黄牌编辑接口
     * */
    Response edit15MinYellowCard(Goal15MinDto confirmEventDto);

    /**
     * 15分钟红牌编辑接口
     * */
    Response edit15MinRedCard(Goal15MinDto confirmEventDto);

    /**
     * 5分钟进球编辑接口
     * */
    Response edit5MinGoal(Goal5MinDto confirmEventDto);

    Response updateSettleStatus(UpdateSettleStatusDto updateSettleStatusDto);
    /**
     * 取消比赛结束
     * */
    Response cancelMatchEnd(Long thirdMatchId, Integer secondFromStart,Long periodId,String userName,String userId,String address);

    Response editAllScore(AllFootballScoreEditDto allFootballScoreEditDto);

    /**
     * 赛程调用比分中心，获取所有未点确认或取消的redis信息
     *
     * @param keys 所有redis key
     * @return 所有value
     */
    List<Object> getAllRedisValue(List<String> keys);


    /**
     * 点球大战改变先罚
     *
     * @param takePenaltyDTO
     * @return
     */
    Response executeTakePenalty(TakePenaltyDTO takePenaltyDTO);

    
    /**
     * 点球即将开始
     *
     * @param penaltyAboutToBeTakenDto 点球即将开始
     * @return 响应点球即将开始数据
     */
    Response penaltyAboutToBeTaken(PenaltyAboutToBeTakenDto penaltyAboutToBeTakenDto);


    /**
     * 点球大战的确认与取消
     *
     * @param confirmPenaltyEventDTO
     * @return
     */
    Response confirmPenaltyEvent(ConfirmPenaltyEventDTO confirmPenaltyEventDTO);

}
