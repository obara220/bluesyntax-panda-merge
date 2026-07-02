package com.panda.merge.advertise.dto;

import com.panda.merge.dto.CommonItem;
import lombok.Data;

import java.io.Serializable;

@Data
public class FootBallScoreVo  implements Serializable {

    //角球
    private CommonItem  corner1H;

    private CommonItem  corner2H;

    private CommonItem  cornerExtry1H;

    private CommonItem  cornerExtry2H;

    //进球
    private CommonItem  goal1H;

    private CommonItem  goal2H;

    private CommonItem  goalExtry1H;

    private CommonItem  goalExtry2H;

    //黄牌
    private CommonItem  yellowCard1H;

    private CommonItem  yellowCard2H;

    private CommonItem  yellowCardExtry1H;

    private CommonItem  yellowCardExtry2H;

    //红牌
    private CommonItem  redCard1H;

    private CommonItem  redCard2H;

    private CommonItem  redCardExtry1H;

    private CommonItem  redCardExtry2H;

    // 红黄牌
    private CommonItem  yellowRedCard1H;

    private CommonItem  yellowRedCard2H;

    private CommonItem  yellowRedCardExtry1H;

    private CommonItem  yellowRedCardExtry2H;

    private CommonItem  periodYellowRedCard;

    //任意球
    private CommonItem  freeKick1H;

    private CommonItem  freeKick2H;

    private CommonItem  freeKickExtry1H;

    private CommonItem  freeKickExtry2H;

    /**
     * 加时赛
     * */

    private CommonItem  yellowCard;

    private CommonItem  redCard;

    private CommonItem  faCard;

    private CommonItem  freeKick;

    private CommonItem  yellowRedCard;
//
//
//
    private CommonItem  corner;



    private CommonItem  goal;

    /**
     * 界外球
     */
    private CommonItem  throwIn;
    private CommonItem  throwIn1H;
    private CommonItem  throwIn2H;
    /**
     * 控球权
     */
    private CommonItem  possession;
    private CommonItem  possession1H;
    private CommonItem  possession2H;
    /**
     * 控球率
     */
    private CommonItem  ballPossessionPercentage;
    private CommonItem  ballPossessionPercentage1H;
    private CommonItem  ballPossessionPercentage2H;

    /**
     * 控球时间
     */
    private CommonItem possessionTime;
    private CommonItem possessionTime1H;
    private CommonItem possessionTime2H;

    /**
     * 持球数
     */
    private CommonItem  possessionCount;
    private CommonItem  possessionCount1H;
    private CommonItem  possessionCount2H;

    /**
     * 公共事件：主队-公共事件1，客队-公共事件2
     */
    private CommonItem publicEvent;
    private CommonItem publicEvent1H;
    private CommonItem publicEvent2H;

    /**
     * 进攻
     */
    private CommonItem  attack;
    private CommonItem attack1H;
    private CommonItem attack2H;

    /**
     * 球门球
     */
    private CommonItem  goalKick;
    private CommonItem  goalKick1H;
    private CommonItem  goalKick2H;

    /**
     * 越位
     */
    private CommonItem  offside;
    private CommonItem  offside1H;
    private CommonItem  offside2H;

    /**
     * 射正
     */
    private CommonItem  shotOnTarget;
    private CommonItem  shotOnTarget1H;
    private CommonItem  shotOnTarget2H;

    /**
     * 射偏
     */
    private CommonItem  shotOffTarget;
    private CommonItem  shotOffTarget1H;
    private CommonItem  shotOffTarget2H;
    /**
     * 加时赛
     * */
    private CommonItem  goalExtry;

    private CommonItem  throwInExtry;
    private CommonItem  throwInExtry1H;
    private CommonItem  throwInExtry2H;
    private CommonItem  periodThrowIn;

    private CommonItem  possessionExtry;
    private CommonItem  possessionExtry1H;
    private CommonItem  possessionExtry2H;
    private CommonItem  periodPossession;

    private CommonItem  ballPossessionPercentageExtry;
    private CommonItem  ballPossessionPercentageExtry1H;
    private CommonItem  ballPossessionPercentageExtry2H;
    private CommonItem  periodBallPossessionPercentage;

    private CommonItem possessionTimeExtry;
    private CommonItem possessionTimeExtry1H;
    private CommonItem possessionTimeExtry2H;
    private CommonItem periodPossessionTime;

    private CommonItem  possessionCountExtry;
    private CommonItem  possessionCountExtry1H;
    private CommonItem  possessionCountExtry2H;
    private CommonItem  periodPossessionCount;

    private CommonItem publicEventExtry;
    private CommonItem publicEventExtry1H;
    private CommonItem publicEventExtry2H;
    private CommonItem periodPublicEvent;

    private CommonItem  attackExtry;
    private CommonItem  attackExtry1H;
    private CommonItem  attackExtry2H;
    private CommonItem  periodAttack;

    private CommonItem  goalKickExtry;
    private CommonItem  goalKickExtry1H;
    private CommonItem  goalKickExtry2H;
    private CommonItem  periodGoalKick;

    private CommonItem  offsideExtry;
    private CommonItem  offsideExtry1H;
    private CommonItem  offsideExtry2H;
    private CommonItem  periodOffside;

    private CommonItem  freeKickExtry;
    private CommonItem  periodFreeKick;

    private CommonItem  shotOnTargetExtry;
    private CommonItem  shotOnTargetExtry1H;
    private CommonItem  shotOnTargetExtry2H;
    private CommonItem  periodShotOnTarget;

    private CommonItem  shotOffTargetExtry;
    private CommonItem  shotOffTargetExtry1H;
    private CommonItem  shotOffTargetExtry2H;
    private CommonItem  periodShotOffTarget;
    /**
     * 点球大战
     * */
    private CommonItem  penaltyAwarded;

    private CommonItem  periodGoal;

    private CommonItem  periodCorner;

    private CommonItem  periodFaCard;
    /**
     * 谁开球
     * */
    private CommonItem  periodKickOff;
    /**
     * 危险进攻
     */
    private CommonItem dangerousAttack;
    private CommonItem dangerousAttack1H;
    private CommonItem dangerousAttack2H;
    private CommonItem dangerousAttackExtry;
    private CommonItem dangerousAttackExtry1H;
    private CommonItem dangerousAttackExtry2H;
    private CommonItem periodDangerousAttack;

    /**
     * 点球
     */
    private CommonItem penalty;
    private CommonItem penalty1H;
    private CommonItem penalty2H;
    private CommonItem penaltyExtry;
    private CommonItem penaltyExtry1H;
    private CommonItem penaltyExtry2H;
    private CommonItem periodPenalty;
    private CommonItem penaltyTotal;
}
