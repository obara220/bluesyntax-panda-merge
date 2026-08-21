package com.panda.merge.advertise.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.merge.advertise.dto.FootBallScoreVo;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.service.FootBallScoreService;
import com.panda.merge.calculation.impl.FootballCalculationServiceImpl;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.PDEventCodeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.ConfirmEventDto;
import com.panda.merge.dto.advertise.DeleteEventDto;
import com.panda.merge.dto.advertise.EditEventDto;
import com.panda.merge.dto.advertise.Goal15MinDataDto;
import com.panda.merge.dto.advertise.Goal15MinDto;
import com.panda.merge.dto.advertise.Goal5MinDataDto;
import com.panda.merge.dto.advertise.Goal5MinDto;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;


@Service
@Slf4j
public class FootBallScoreServiceImpl implements FootBallScoreService {
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    IMatchScorePdLogService matchScorePdLogService;
    @Autowired
    FootballCalculationServiceImpl footballCalculationService;
    @Autowired
    RedisService redisService;
    @Autowired
    private CommonEventService commonEventService;
    @Autowired
    private PdMatchInfoRepository pdMatchInfoRepository;
    public static final List<PDEventCodeEnum> VAR_CONFIRM_EVENT_CODES = Arrays.asList(PDEventCodeEnum.GOAL, PDEventCodeEnum.YELLOW_CARD,
            PDEventCodeEnum.RED_CARD, PDEventCodeEnum.CORNER, PDEventCodeEnum.YELLOW_RED_CARD, PDEventCodeEnum.THROW_IN, PDEventCodeEnum.ATTACK,
            PDEventCodeEnum.POSSESSION, PDEventCodeEnum.GOAL_KICK, PDEventCodeEnum.FREE_KICK, PDEventCodeEnum.OFFSIDE, PDEventCodeEnum.SHOT_ON_TARGET,
            PDEventCodeEnum.SHOT_OFF_TARGET, PDEventCodeEnum.DANGEROUS_ATTACK, PDEventCodeEnum.PENALTY, PDEventCodeEnum.PENALTY_GOAL,
            PDEventCodeEnum.PENALTY_MISSED, PDEventCodeEnum.PENALTY_CANCELED);

    public static final List<PDEventCodeEnum> VAR_FE_SHOW_CODES = Arrays.asList(PDEventCodeEnum.VAR_PENALTY, PDEventCodeEnum.CANCELED_VAR_PENALTY,
            PDEventCodeEnum.VAR_RED_CARD, PDEventCodeEnum.VAR_YELLOW_CARD, PDEventCodeEnum.CANCELED_VAR_RED_CARD, PDEventCodeEnum.VAR_GOAL,
            PDEventCodeEnum.CANCELED_VAR_GOAL, PDEventCodeEnum.PENALTY_GOAL);


    @Override
    public MatchScoreCommonVo searchCommonMatchScore(MatchScoresInfo matchScoresInfo,Long periodId) {
        MatchScoreCommonVo matchScoreCommonVo=new MatchScoreCommonVo();
        matchScoreCommonVo.setT1(matchScoresInfo.getT1());
        matchScoreCommonVo.setT2(matchScoresInfo.getT2());
        matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
        matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores periodSores= allPeriodScores.get(periodId);
        FootballScores wholeScore= allPeriodScores.get(WHOLE_MATCH);
        matchScoreCommonVo.setT1(wholeScore.getGoal().getHome());
        matchScoreCommonVo.setT2(wholeScore.getGoal().getAway());
        if(periodSores==null){
            if(SportPeriodConstant.FootballPeriod.contans(periodId)){
                periodSores= new FootballScores(0l);
                allPeriodScores.put(periodId,periodSores);
                if(periodId == 50L){
                    FootballPenaltyScores footballPenaltyScores = new FootballPenaltyScores(1,5);
                    matchScoresInfo.setScoresJsonExtra(JSONObject.toJSONString(footballPenaltyScores, SerializerFeature.WriteMapNullValue));
                }
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                matchScoresInfo.setModifyTime(System.currentTimeMillis());
//                matchScoresInfoMapper.updateByPrimaryKey( matchScoresInfo);
//                pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
            }
            matchScoreCommonVo.setMatchScoresInfo(matchScoresInfo);
            matchScoreCommonVo.setPeriodT1(0);
            matchScoreCommonVo.setPeriodT2(0);
        }else {
            matchScoreCommonVo.setMatchScoresInfo(matchScoresInfo);
            matchScoreCommonVo.setPeriodT1(periodSores.getGoal().getHome());
            matchScoreCommonVo.setPeriodT2(periodSores.getShot().getAway());
        }
        if(periodId.equals(999l)){
            matchScoreCommonVo.setMatchScoresInfo(matchScoresInfo);
            matchScoreCommonVo.setPeriodT1(matchScoresInfo.getPeriodT1());
            matchScoreCommonVo.setPeriodT2(matchScoresInfo.getPeriodT2());
        }
        return matchScoreCommonVo;
    }



    @Override
    public boolean hasExtryPeriod(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            return false;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        if(allPeriodScores==null||allPeriodScores.get(41L)==null){
            return false;
        }
        /**
         * 只有加时赛 无点球
         * */
        if(allPeriodScores.get(50L)!=null){
            return false;
        }
        return true;
    }

    @Override
    public boolean hasPenaltyAwarded(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            return false;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        if(allPeriodScores==null||allPeriodScores.get(50L)==null){
            return false;
        }
        return true;
    }

    @Override
    public FootBallScoreVo transforScore(MatchScoresInfo matchScoresInfo) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        FootBallScoreVo footBallScoreVo =new FootBallScoreVo();
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        FootballScores H1Sores= allPeriodScores.get(6l);
        FootballScores H2Sores= allPeriodScores.get(7l);
        FootballScores E1Sores= allPeriodScores.get(41L);
        FootballScores E2Sores= allPeriodScores.get(42L);
        FootballScores PSores= allPeriodScores.get(50L);
        //转化比分
        footBallScoreVo.setFaCard(wholeSores.getFaCard());
        footBallScoreVo.setRedCard(wholeSores.getRedCard());
        footBallScoreVo.setYellowCard(wholeSores.getYellowCard());
        footBallScoreVo.setCorner(wholeSores.getCorner());
        footBallScoreVo.setGoal(wholeSores.getGoal());
        footBallScoreVo.setPeriodKickOff(wholeSores.getKickOff());
        footBallScoreVo.setThrowIn(wholeSores.getThrowIn());
        footBallScoreVo.setBallPossessionPercentage(wholeSores.getBallPossessionPercentage());
        footBallScoreVo.setPossessionTime(wholeSores.getPossessionTime());
        footBallScoreVo.setPossessionCount(wholeSores.getPossessionCount());
        footBallScoreVo.setPublicEvent(wholeSores.getPublicEvent());
        footBallScoreVo.setAttack(wholeSores.getAttack());
        footBallScoreVo.setGoalKick(wholeSores.getGoalKick());
        footBallScoreVo.setOffside(wholeSores.getOffside());
        footBallScoreVo.setShotOnTarget(wholeSores.getShotOn());
        footBallScoreVo.setShotOffTarget(wholeSores.getShotOff());
        footBallScoreVo.setDangerousAttack(wholeSores.getDangerousAttack());
        footBallScoreVo.setPenalty(wholeSores.getPenaltyAwarded());
        footBallScoreVo.setPenaltyTotal(wholeSores.getPenaltyAwardedTotal());
        footBallScoreVo.setYellowRedCard(wholeSores.getYellowRedCard());
        footBallScoreVo.setFreeKick(wholeSores.getFreeKickScore());
        if(H1Sores!=null){
            footBallScoreVo.setGoal1H(H1Sores.getGoal());
            footBallScoreVo.setCorner1H(H1Sores.getCorner());
            footBallScoreVo.setYellowCard1H(H1Sores.getYellowCard());
            footBallScoreVo.setRedCard1H(H1Sores.getRedCard());
            footBallScoreVo.setThrowIn1H(H1Sores.getThrowIn());
            footBallScoreVo.setPossession1H(H1Sores.getBallPossessionPercentage());
            footBallScoreVo.setPossessionTime1H(H1Sores.getPossessionTime());
            footBallScoreVo.setPossessionCount1H(H1Sores.getPossessionCount());
            footBallScoreVo.setPublicEvent1H(H1Sores.getPublicEvent());
            footBallScoreVo.setAttack1H(H1Sores.getAttack());
            footBallScoreVo.setGoalKick1H(H1Sores.getGoalKick());
            footBallScoreVo.setOffside1H(H1Sores.getOffside());
            footBallScoreVo.setShotOnTarget1H(H1Sores.getShotOn());
            footBallScoreVo.setShotOffTarget1H(H1Sores.getShotOff());
            footBallScoreVo.setDangerousAttack1H(H1Sores.getDangerousAttack());
            footBallScoreVo.setPenalty1H(H1Sores.getPenaltyAwarded());
            footBallScoreVo.setYellowRedCard1H(H1Sores.getYellowRedCard());
            footBallScoreVo.setFreeKick1H(H1Sores.getFreeKickScore());
        }
        if(H2Sores!=null){
            footBallScoreVo.setGoal2H(H2Sores.getGoal());
            footBallScoreVo.setCorner2H(H2Sores.getCorner());
            footBallScoreVo.setYellowCard2H(H2Sores.getYellowCard());
            footBallScoreVo.setRedCard2H(H2Sores.getRedCard());
            footBallScoreVo.setThrowIn2H(H2Sores.getThrowIn());
            footBallScoreVo.setBallPossessionPercentage2H(H2Sores.getBallPossessionPercentage());
            footBallScoreVo.setPossessionTime2H(H2Sores.getPossessionTime());
            footBallScoreVo.setPossessionCount2H(H2Sores.getPossessionCount());
            footBallScoreVo.setPublicEvent2H(H2Sores.getPublicEvent());
            footBallScoreVo.setAttack2H(H2Sores.getAttack());
            footBallScoreVo.setGoalKick2H(H2Sores.getGoalKick());
            footBallScoreVo.setOffside2H(H2Sores.getOffside());
            footBallScoreVo.setShotOnTarget2H(H2Sores.getShotOn());
            footBallScoreVo.setShotOffTarget2H(H2Sores.getShotOff());
            footBallScoreVo.setDangerousAttack2H(H2Sores.getDangerousAttack());
            footBallScoreVo.setPenalty2H(H2Sores.getPenaltyAwarded());
            footBallScoreVo.setYellowRedCard2H(H2Sores.getYellowRedCard());
            footBallScoreVo.setFreeKick2H(H2Sores.getFreeKickScore());
        }
//        private CommonItem  goalExtry;
        if(E1Sores!=null){
            footBallScoreVo.setGoalExtry1H(E1Sores.getGoal());
            footBallScoreVo.setCornerExtry1H(E1Sores.getCorner());
            footBallScoreVo.setYellowCardExtry1H(E1Sores.getYellowCard());
            footBallScoreVo.setRedCardExtry1H(E1Sores.getRedCard());
            footBallScoreVo.setThrowInExtry1H(E1Sores.getThrowIn());
            footBallScoreVo.setBallPossessionPercentageExtry1H(E1Sores.getBallPossessionPercentage());
            footBallScoreVo.setPossessionTimeExtry1H(E1Sores.getPossessionTime());
            footBallScoreVo.setPossessionCountExtry1H(E1Sores.getPossessionCount());
            footBallScoreVo.setPublicEventExtry1H(E1Sores.getPublicEvent());
            footBallScoreVo.setAttackExtry1H(E1Sores.getAttack());
            footBallScoreVo.setGoalKickExtry1H(E1Sores.getGoalKick());
            footBallScoreVo.setOffsideExtry1H(E1Sores.getOffside());
            footBallScoreVo.setShotOnTargetExtry1H(E1Sores.getShotOn());
            footBallScoreVo.setShotOffTargetExtry1H(E1Sores.getShotOff());
            footBallScoreVo.setDangerousAttackExtry1H(E1Sores.getDangerousAttack());
            footBallScoreVo.setPenaltyExtry1H(E1Sores.getPenaltyAwarded());
            footBallScoreVo.setYellowRedCardExtry1H(E1Sores.getYellowRedCard());
            footBallScoreVo.setFreeKickExtry1H(E1Sores.getFreeKickScore());
        }
        if(E2Sores!=null){
            footBallScoreVo.setGoalExtry2H(E2Sores.getGoal());
            footBallScoreVo.setCornerExtry2H(E2Sores.getCorner());
            footBallScoreVo.setYellowCardExtry2H(E2Sores.getYellowCard());
            footBallScoreVo.setRedCardExtry2H(E2Sores.getRedCard());
            footBallScoreVo.setThrowInExtry2H(E2Sores.getThrowIn());
            footBallScoreVo.setBallPossessionPercentageExtry2H(E2Sores.getBallPossessionPercentage());
            footBallScoreVo.setPossessionTimeExtry2H(E2Sores.getPossessionTime());
            footBallScoreVo.setPossessionCountExtry2H(E2Sores.getPossessionCount());
            footBallScoreVo.setPublicEventExtry2H(E2Sores.getPublicEvent());
            footBallScoreVo.setAttackExtry2H(E2Sores.getAttack());
            footBallScoreVo.setGoalKickExtry2H(E2Sores.getGoalKick());
            footBallScoreVo.setOffsideExtry2H(E2Sores.getOffside());
            footBallScoreVo.setShotOnTargetExtry2H(E2Sores.getShotOn());
            footBallScoreVo.setShotOffTargetExtry2H(E2Sores.getShotOff());
            footBallScoreVo.setDangerousAttackExtry2H(E2Sores.getDangerousAttack());
            footBallScoreVo.setPenaltyExtry2H(E2Sores.getPenaltyAwarded());
            footBallScoreVo.setYellowRedCardExtry2H(E2Sores.getYellowRedCard());
            footBallScoreVo.setFreeKickExtry2H(E2Sores.getFreeKickScore());
        }

        if(PSores!=null){
            footBallScoreVo.setPenaltyAwarded(PSores.getGoal());
        }
        CommonItem periodGoal =new CommonItem();
        CommonItem  periodCorner =new CommonItem();
        CommonItem  periodFaCard =new CommonItem();
        CommonItem periodThrowIn = new CommonItem();
        CommonItem periodBallPossessionPercentage = new CommonItem();
        CommonItem periodPossessionTime = new CommonItem();
        CommonItem periodPossessionCount = new CommonItem();
        CommonItem periodPublicEvent = new CommonItem();
        CommonItem periodAttack = new CommonItem();
        CommonItem periodGoalKick = new CommonItem();
        CommonItem periodOffSide = new CommonItem();
        CommonItem periodShotOnTarget = new CommonItem();
        CommonItem periodShotOffTarget = new CommonItem();
        CommonItem periodDangerousAttack = new CommonItem();
        CommonItem periodPenaly = new CommonItem();
        CommonItem periodYellowRedCard = new CommonItem();
        CommonItem periodFreeKick = new CommonItem();
        if(E1Sores==null){
            //常规赛比分
            periodGoal=wholeSores.getGoal();
            periodCorner=wholeSores.getCorner();
            periodFaCard=wholeSores.getFaCard();
            periodThrowIn = wholeSores.getThrowIn();
            periodBallPossessionPercentage = wholeSores.getBallPossessionPercentage();
            periodPossessionTime = wholeSores.getPossessionTime();
            periodPossessionCount = wholeSores.getPossessionCount();
            periodPublicEvent = wholeSores.getPublicEvent();
            periodAttack = wholeSores.getAttack();
            periodGoalKick = wholeSores.getGoalKick();
            periodOffSide = wholeSores.getOffside();
            periodShotOnTarget = wholeSores.getShotOn();
            periodShotOffTarget = wholeSores.getShotOff();
            periodDangerousAttack = wholeSores.getDangerousAttack();
            periodPenaly = wholeSores.getPenaltyAwarded();
            periodYellowRedCard = wholeSores.getYellowRedCard();
            periodFreeKick = wholeSores.getFreeKickScore();
        }else{
            if((PSores==null)) {
                //加时赛比分
                if (E2Sores == null) {
                    periodGoal = E1Sores.getGoal();
                    periodCorner = E1Sores.getCorner();
                    periodFaCard = E1Sores.getFaCard();
                    periodThrowIn = E1Sores.getThrowIn();
                    periodBallPossessionPercentage = E1Sores.getBallPossessionPercentage();
                    periodPossessionTime = E1Sores.getPossessionTime();
                    periodPossessionCount = E1Sores.getPossessionCount();
                    periodPublicEvent = E1Sores.getPublicEvent();
                    periodAttack = E1Sores.getAttack();
                    periodGoalKick = E1Sores.getGoalKick();
                    periodOffSide = E1Sores.getOffside();
                    periodShotOnTarget = E1Sores.getShotOn();
                    periodShotOffTarget = E1Sores.getShotOff();
                    periodDangerousAttack = E1Sores.getDangerousAttack();
                    periodPenaly = E1Sores.getPenaltyAwarded();
                    periodYellowRedCard = E1Sores.getYellowRedCard();
                    periodFreeKick = E1Sores.getFreeKickScore();
                }else{
                    periodGoal.setHome(E1Sores.getGoal().getHome() + E2Sores.getGoal().getHome());
                    periodGoal.setAway(E1Sores.getGoal().getAway() + E2Sores.getGoal().getAway());
                    periodCorner.setHome(E1Sores.getCorner().getHome() + E2Sores.getCorner().getHome());
                    periodCorner.setAway(E1Sores.getCorner().getAway() + E2Sores.getCorner().getAway());
                    periodFaCard.setHome(E1Sores.getFaCard().getHome() + E2Sores.getFaCard().getHome());
                    periodFaCard.setAway(E1Sores.getFaCard().getAway() + E2Sores.getFaCard().getAway());
                    periodThrowIn.setHome(E1Sores.getThrowIn().getHome() + E2Sores.getThrowIn().getHome());
                    periodThrowIn.setAway(E1Sores.getThrowIn().getAway() + E2Sores.getThrowIn().getAway());
                    periodBallPossessionPercentage.setHome(E1Sores.getBallPossessionPercentage().getHome() + E2Sores.getBallPossessionPercentage().getHome());
                    periodBallPossessionPercentage.setAway(E1Sores.getBallPossessionPercentage().getAway() + E2Sores.getBallPossessionPercentage().getAway());
                    periodPossessionTime.setHome(E1Sores.getPossessionTime().getHome() + E2Sores.getPossessionTime().getHome());
                    periodPossessionTime.setAway(E1Sores.getPossessionTime().getAway() + E2Sores.getPossessionTime().getAway());
                    periodPossessionCount.setHome(E1Sores.getPossessionCount().getHome() + E2Sores.getPossessionCount().getHome());
                    periodPossessionCount.setAway(E1Sores.getPossessionCount().getAway() + E2Sores.getPossessionCount().getAway());
                    periodPublicEvent.setHome(E1Sores.getPublicEvent().getHome() + E2Sores.getPublicEvent().getHome());
                    periodPublicEvent.setAway(E1Sores.getPublicEvent().getAway() + E2Sores.getPublicEvent().getAway());
                    periodAttack.setHome(E1Sores.getAttack().getHome() + E2Sores.getAttack().getHome());
                    periodAttack.setAway(E1Sores.getAttack().getAway() + E2Sores.getAttack().getAway());
                    periodGoalKick.setHome(E1Sores.getGoalKick().getHome() + E2Sores.getGoalKick().getHome());
                    periodGoalKick.setAway(E1Sores.getGoalKick().getAway() + E2Sores.getGoalKick().getAway());
                    periodOffSide.setHome(E1Sores.getOffside().getHome() + E2Sores.getOffside().getHome());
                    periodOffSide.setAway(E1Sores.getOffside().getAway() + E2Sores.getOffside().getAway());
                    periodShotOnTarget.setHome(E1Sores.getShotOn().getHome() + E2Sores.getShotOn().getHome());
                    periodShotOnTarget.setAway(E1Sores.getShotOn().getAway() + E2Sores.getShotOn().getAway());
                    periodShotOffTarget.setHome(E1Sores.getShotOff().getHome() + E2Sores.getShotOff().getHome());
                    periodShotOffTarget.setAway(E1Sores.getShotOff().getAway() + E2Sores.getShotOff().getAway());
                    periodDangerousAttack.setHome(E1Sores.getDangerousAttack().getHome() + E2Sores.getDangerousAttack().getHome());
                    periodDangerousAttack.setAway(E1Sores.getDangerousAttack().getAway() + E2Sores.getDangerousAttack().getAway());
                    periodPenaly.setHome(E1Sores.getPenaltyAwarded().getHome() + E2Sores.getPenaltyAwarded().getHome());
                    periodPenaly.setAway(E1Sores.getPenaltyAwarded().getAway() + E2Sores.getPenaltyAwarded().getAway());
                    periodYellowRedCard.setHome(E1Sores.getYellowRedCard().getHome() + E2Sores.getYellowRedCard().getHome());
                    periodYellowRedCard.setAway(E1Sores.getYellowRedCard().getAway() + E2Sores.getYellowRedCard().getAway());
                    periodFreeKick.setHome(E1Sores.getFreeKickScore().getHome() + E2Sores.getFreeKickScore().getHome());
                    periodFreeKick.setAway(E1Sores.getFreeKickScore().getAway() + E2Sores.getFreeKickScore().getAway());
                }
            }else {
                //点球大战比分
                periodGoal = footBallScoreVo.getPenaltyAwarded();
            }
        }
        footBallScoreVo.setPeriodGoal(periodGoal);
        footBallScoreVo.setPeriodCorner(periodCorner);
        footBallScoreVo.setPeriodFaCard(periodFaCard);
        footBallScoreVo.setPeriodThrowIn(periodThrowIn);
        footBallScoreVo.setPeriodBallPossessionPercentage(periodBallPossessionPercentage);
        footBallScoreVo.setPeriodPossessionTime(periodPossessionTime);
        footBallScoreVo.setPeriodPossessionCount(periodPossessionCount);
        footBallScoreVo.setPeriodPublicEvent(periodPublicEvent);
        footBallScoreVo.setPeriodAttack(periodAttack);
        footBallScoreVo.setPeriodGoalKick(periodGoalKick);
        footBallScoreVo.setPeriodOffside(periodOffSide);
        footBallScoreVo.setPeriodShotOnTarget(periodShotOnTarget);
        footBallScoreVo.setPeriodShotOffTarget(periodShotOffTarget);
        footBallScoreVo.setPeriodDangerousAttack(periodDangerousAttack);
        footBallScoreVo.setPeriodPenalty(periodPenaly);
        footBallScoreVo.setPeriodYellowRedCard(periodYellowRedCard);
        footBallScoreVo.setPeriodFreeKick(periodFreeKick);
        stopWatch.stop();
        log.info("FootBallScoreServiceImpl-transforScore-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),matchScoresInfo.getThirdMatchId());
        return footBallScoreVo;
    }

    @Override
    public Pair<MatchEventInfoDTO, Map<String, String>> changeScoresByEvent(MatchScoreAndTimeVo data, ConfirmEventDto confirmEventDto, MatchEventInfoDTO matchEventInfoDTO) {
        String homeAway = matchEventInfoDTO.getHomeAway();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
            Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
            FootballScores periodSores= allPeriodScores.get(data.getMatchTimeInfo().getPeriod());
            if (periodSores == null) {
                periodSores = FootballScores.createMinFootballScores();
                allPeriodScores.put(data.getMatchTimeInfo().getPeriod(), periodSores);
            }

            Pair<Pair<Integer, Integer>, String> scoreNumResult = obtainScoreNum(matchEventInfoDTO, data.getThirdMatchInfo());
            Pair<Integer, Integer> matchScoresEventInfo = scoreNumResult.getLeft();
            String oldThirdEventId = scoreNumResult.getRight();
            log.info("[changeScoresByEvent] thirdMatchId:{} eventCode:{} homeAway:{} scores:{}", confirmEventDto.getThirdMatchId(), confirmEventDto.getConfirmEventCode(), confirmEventDto.getHomeAway(), matchScoresEventInfo);
            Integer redHome = wholeSores.getRedCard().getHome();
            Integer redAway = wholeSores.getRedCard().getAway();
            //进球 kb BUG1 点球大战事件可能有问题
            int countNum = 1;
            Map<String, Integer> finalEventCodeMap = new HashMap<>();
            if(matchEventInfoDTO.getEventCode().equals("goal") || matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.VAR_GOAL.getEventCode()) ||
                    matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.CANCELED_VAR_GOAL.getEventCode()) ||
                    matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.PENALTY_GOAL.getEventCode()) ||
                    matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.PENALTY_MISSED.getEventCode())) {
                if(matchScoresEventInfo.getLeft() != null) {
                    countNum = matchScoresEventInfo.getLeft() != 0 ? matchScoresEventInfo.getLeft() : matchScoresEventInfo.getRight();
                }
                finalEventCodeMap.put(PDEventCodeEnum.GOAL.getEventCode(), countNum);
                if(data.getMatchTimeInfo().getPeriod().equals(50l)){
                    if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                        periodSores.getGoal().setHome(calValue(periodSores.getGoal().getHome(), countNum));
                    }else if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                        periodSores.getGoal().setAway(calValue(periodSores.getGoal().getAway(), countNum));
                    }
                    data.getMatchScoresInfo().setPeriodT1(periodSores.getGoal().getHome());
                    data.getMatchScoresInfo().setPeriodT2(periodSores.getGoal().getAway());
                    matchEventInfoDTO.setT1(periodSores.getGoal().getHome());
                    matchEventInfoDTO.setT2(periodSores.getGoal().getAway());
                }else {
                    if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                        wholeSores.getGoal().setHome(calValue(wholeSores.getGoal().getHome(), countNum));
                        periodSores.getGoal().setHome(calValue(periodSores.getGoal().getHome(), countNum));
                    }else if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                        wholeSores.getGoal().setAway(calValue(wholeSores.getGoal().getAway(), countNum));
                        periodSores.getGoal().setAway(calValue(periodSores.getGoal().getAway(), countNum));
                    }
                    data.getMatchScoresInfo().setPeriodT1(periodSores.getGoal().getHome());
                    data.getMatchScoresInfo().setPeriodT2(periodSores.getGoal().getAway());
                    data.getMatchScoresInfo().setT1(wholeSores.getGoal().getHome());
                    data.getMatchScoresInfo().setT2(wholeSores.getGoal().getAway());
                    matchEventInfoDTO.setT1(wholeSores.getGoal().getHome());
                    matchEventInfoDTO.setT2(wholeSores.getGoal().getAway());
                }
            }

            //红牌
            if(matchEventInfoDTO.getEventCode().equals("red_card") || matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.VAR_RED_CARD.getEventCode())
                    || matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.VAR_YELLOW_CARD.getEventCode()) || matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.CANCELED_VAR_RED_CARD.getEventCode()))
            {
                if(matchScoresEventInfo.getLeft() != null) {
                    countNum = matchScoresEventInfo.getLeft();
                }
                finalEventCodeMap.put(PDEventCodeEnum.RED_CARD.getEventCode(), countNum);
                if(data.getMatchTimeInfo().getPeriod().equals(50l))
                {

                }
                else
                {
                    if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                        wholeSores.getRedCard().setHome(calValue(wholeSores.getRedCard().getHome(), countNum));
                        periodSores.getRedCard().setHome(calValue(periodSores.getRedCard().getHome(), countNum));
                    }else if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                        wholeSores.getRedCard().setAway(calValue(wholeSores.getRedCard().getAway(), countNum));
                        periodSores.getRedCard().setAway(calValue(periodSores.getRedCard().getAway(), countNum));
                    }
                }
                periodSores.countFaCard();
                wholeSores.countFaCard();
                matchEventInfoDTO.setT1(wholeSores.getRedCard().getHome());
                matchEventInfoDTO.setT2(wholeSores.getRedCard().getAway());
                redHome = matchEventInfoDTO.getT1();
                redAway = matchEventInfoDTO.getT2();
            }
            //黄牌
            if(matchEventInfoDTO.getEventCode().equals("yellow_card") || matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.VAR_YELLOW_CARD.getEventCode()))
            {
                if(matchScoresEventInfo.getRight() != null) {
                    countNum = matchScoresEventInfo.getRight();
                }
                finalEventCodeMap.put(PDEventCodeEnum.YELLOW_CARD.getEventCode(), countNum);
                if(data.getMatchTimeInfo().getPeriod().equals(50l)){
                }else {
                    if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                        wholeSores.getYellowCard().setHome(wholeSores.getYellowCard().getHome()+1);
                        periodSores.getYellowCard().setHome(periodSores.getYellowCard().getHome()+1);
                    }else if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                        wholeSores.getYellowCard().setAway(wholeSores.getYellowCard().getAway()+1);
                        periodSores.getYellowCard().setAway(periodSores.getYellowCard().getAway()+1);
                    }
                    periodSores.countFaCard();
                    wholeSores.countFaCard();
                    matchEventInfoDTO.setT1(wholeSores.getYellowCard().getHome());
                    matchEventInfoDTO.setT2(wholeSores.getYellowCard().getAway());
                }
            }
            //角球
            if(matchEventInfoDTO.getEventCode().equals("corner")){
                finalEventCodeMap.put(PDEventCodeEnum.CORNER.getEventCode(), countNum);
                if(data.getMatchTimeInfo().getPeriod().equals(50l)){
                }else {
                    if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                        wholeSores.getCorner().setHome(wholeSores.getCorner().getHome()+1);
                        periodSores.getCorner().setHome(periodSores.getCorner().getHome()+1);
                    }else if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                        wholeSores.getCorner().setAway(wholeSores.getCorner().getAway()+1);
                        periodSores.getCorner().setAway(periodSores.getCorner().getAway()+1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getCorner().getHome());
                matchEventInfoDTO.setT2(wholeSores.getCorner().getAway());
            }
            // 红黄牌
            String eventCode = matchEventInfoDTO.getEventCode();
            MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
            if ("yellow_red_card".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.YELLOW_RED_CARD.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        wholeSores.getRedCard().setHome(wholeSores.getRedCard().getHome() + 1);
                        periodSores.getRedCard().setHome(periodSores.getRedCard().getHome() + 1);
                        if (null == wholeSores.getYellowRedCard() || null == periodSores.getYellowRedCard()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setYellowRedCard(commonItem);
                            periodSores.setYellowRedCard(commonItem);
                        }
                        wholeSores.getYellowRedCard().setHome(wholeSores.getYellowRedCard().getHome() + 1);
                        periodSores.getYellowRedCard().setHome(periodSores.getYellowRedCard().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        wholeSores.getRedCard().setAway(wholeSores.getRedCard().getAway() + 1);
                        periodSores.getRedCard().setAway(periodSores.getRedCard().getAway() + 1);
                        if (null == wholeSores.getYellowRedCard() || null == periodSores.getYellowRedCard()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setYellowRedCard(commonItem);
                            periodSores.setYellowRedCard(commonItem);
                        }
                        wholeSores.getYellowRedCard().setAway(wholeSores.getYellowRedCard().getAway() + 1);
                        periodSores.getYellowRedCard().setAway(periodSores.getYellowRedCard().getAway() + 1);
                    }
                }
                periodSores.countFaCard();
                wholeSores.countFaCard();
                matchEventInfoDTO.setT1(wholeSores.getYellowRedCard().getHome());
                matchEventInfoDTO.setT2(wholeSores.getYellowRedCard().getAway());
            }
            // 界外球
            if ("throw_in".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.THROW_IN.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getThrowIn() || null == periodSores.getThrowIn()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setThrowIn(commonItem);
                            periodSores.setThrowIn(commonItem);
                        }
                        wholeSores.getThrowIn().setHome(wholeSores.getThrowIn().getHome() + 1);
                        periodSores.getThrowIn().setHome(periodSores.getThrowIn().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getThrowIn() || null == periodSores.getThrowIn()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setThrowIn(commonItem);
                            periodSores.setThrowIn(commonItem);
                        }
                        wholeSores.getThrowIn().setAway(wholeSores.getThrowIn().getAway() + 1);
                        periodSores.getThrowIn().setAway(periodSores.getThrowIn().getAway() + 1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getThrowIn().getHome());
                matchEventInfoDTO.setT2(wholeSores.getThrowIn().getAway());
            }
            // 持球数
            if ("possession_count".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.POSSESSION_COUNT.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getPossessionCount() || null == periodSores.getPossessionCount()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setPossessionCount(commonItem);
                            periodSores.setPossessionCount(commonItem);
                        }
                        wholeSores.getPossessionCount().setHome(wholeSores.getPossessionCount().getHome() + 1);
                        periodSores.getPossessionCount().setHome(periodSores.getPossessionCount().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getPossessionCount() || null == periodSores.getPossessionCount()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setPossessionCount(commonItem);
                            periodSores.setPossessionCount(commonItem);
                        }
                        wholeSores.getPossessionCount().setAway(wholeSores.getPossessionCount().getAway() + 1);
                        periodSores.getPossessionCount().setAway(periodSores.getPossessionCount().getAway() + 1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getPossessionCount().getHome());
                matchEventInfoDTO.setT2(wholeSores.getPossessionCount().getAway());
            }
            // 进攻
            if ("attack".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.ATTACK.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getAttack() || null == periodSores.getAttack()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setAttack(commonItem);
                            periodSores.setAttack(commonItem);
                        }
                        wholeSores.getAttack().setHome(wholeSores.getAttack().getHome() + 1);
                        periodSores.getAttack().setHome(periodSores.getAttack().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getAttack() || null == periodSores.getAttack()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setAttack(commonItem);
                            periodSores.setAttack(commonItem);
                        }
                        wholeSores.getAttack().setAway(wholeSores.getAttack().getAway() + 1);
                        periodSores.getAttack().setAway(periodSores.getAttack().getAway() + 1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getAttack().getHome());
                matchEventInfoDTO.setT2(wholeSores.getAttack().getAway());
            }
            // 球门球
            if ("goal_kick".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.GOAL_KICK.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getGoalKick() || null == periodSores.getGoalKick()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setGoalKick(commonItem);
                            periodSores.setGoalKick(commonItem);
                        }
                        wholeSores.getGoalKick().setHome(wholeSores.getGoalKick().getHome() + 1);
                        periodSores.getGoalKick().setHome(periodSores.getGoalKick().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getGoalKick() || null == periodSores.getGoalKick()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setGoalKick(commonItem);
                            periodSores.setGoalKick(commonItem);
                        }
                        wholeSores.getGoalKick().setAway(wholeSores.getGoalKick().getAway() + 1);
                        periodSores.getGoalKick().setAway(periodSores.getGoalKick().getAway() + 1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getGoalKick().getHome());
                matchEventInfoDTO.setT2(wholeSores.getGoalKick().getAway());
            }
            // 任意球
            if ("free_kick".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.FREE_KICK.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getFreeKickScore() || null == periodSores.getFreeKickScore()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setFreeKickScore(commonItem);
                            periodSores.setFreeKickScore(commonItem);
                        }
                        wholeSores.getFreeKickScore().setHome(wholeSores.getFreeKickScore().getHome() + 1);
                        periodSores.getFreeKickScore().setHome(periodSores.getFreeKickScore().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getFreeKickScore() || null == periodSores.getFreeKickScore()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setFreeKickScore(commonItem);
                            periodSores.setFreeKickScore(commonItem);
                        }
                        wholeSores.getFreeKickScore().setAway(wholeSores.getFreeKickScore().getAway() + 1);
                        periodSores.getFreeKickScore().setAway(periodSores.getFreeKickScore().getAway() + 1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getFreeKickScore().getHome());
                matchEventInfoDTO.setT2(wholeSores.getFreeKickScore().getAway());
            }
            // 越位
            if ("offside".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.OFFSIDE.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getOffside() || null == periodSores.getOffside()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setOffside(commonItem);
                            periodSores.setOffside(commonItem);
                        }
                        wholeSores.getOffside().setHome(wholeSores.getOffside().getHome() + 1);
                        periodSores.getOffside().setHome(periodSores.getOffside().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getOffside() || null == periodSores.getOffside()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setOffside(commonItem);
                            periodSores.setOffside(commonItem);
                        }
                        wholeSores.getOffside().setAway(wholeSores.getOffside().getAway() + 1);
                        periodSores.getOffside().setAway(periodSores.getOffside().getAway() + 1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getOffside().getHome());
                matchEventInfoDTO.setT2(wholeSores.getOffside().getAway());
            }
            // 射正
            if ("shot_on_target".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.SHOT_ON_TARGET.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getShotOn() || null == periodSores.getShotOn()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setShotOn(commonItem);
                            periodSores.setShotOn(commonItem);
                        }
                        wholeSores.getShotOn().setHome(wholeSores.getShotOn().getHome() + 1);
                        periodSores.getShotOn().setHome(periodSores.getShotOn().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getShotOn() || null == periodSores.getShotOn()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setShotOn(commonItem);
                            periodSores.setShotOn(commonItem);
                        }
                        wholeSores.getShotOn().setAway(wholeSores.getShotOn().getAway() + 1);
                        periodSores.getShotOn().setAway(periodSores.getShotOn().getAway() + 1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getShotOn().getHome());
                matchEventInfoDTO.setT2(wholeSores.getShotOn().getAway());
            }
            // 射偏
            if ("shot_off_target".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.SHOT_OFF_TARGET.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getShotOff() || null == periodSores.getShotOff()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setShotOff(commonItem);
                            periodSores.setShotOff(commonItem);
                        }
                        wholeSores.getShotOff().setHome(wholeSores.getShotOff().getHome() + 1);
                        periodSores.getShotOff().setHome(periodSores.getShotOff().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getShotOff() || null == periodSores.getShotOff()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setShotOff(commonItem);
                            periodSores.setShotOff(commonItem);
                        }
                        wholeSores.getShotOff().setAway(wholeSores.getShotOff().getAway() + 1);
                        periodSores.getShotOff().setAway(periodSores.getShotOff().getAway() + 1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getShotOff().getHome());
                matchEventInfoDTO.setT2(wholeSores.getShotOff().getAway());
            }
            // 危险进攻
            if ("dangerous_attack".equals(eventCode)) {
                finalEventCodeMap.put(PDEventCodeEnum.DANGEROUS_ATTACK.getEventCode(), countNum);
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getDangerousAttack() || null == periodSores.getDangerousAttack()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setDangerousAttack(commonItem);
                            periodSores.setDangerousAttack(commonItem);
                        }
                        wholeSores.getDangerousAttack().setHome(wholeSores.getDangerousAttack().getHome() + 1);
                        periodSores.getDangerousAttack().setHome(periodSores.getDangerousAttack().getHome() + 1);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getDangerousAttack() || null == periodSores.getDangerousAttack()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setDangerousAttack(commonItem);
                            periodSores.setDangerousAttack(commonItem);
                        }
                        wholeSores.getDangerousAttack().setAway(wholeSores.getDangerousAttack().getAway() + 1);
                        periodSores.getDangerousAttack().setAway(periodSores.getDangerousAttack().getAway() + 1);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getDangerousAttack().getHome());
                matchEventInfoDTO.setT2(wholeSores.getDangerousAttack().getAway());
            }
            // 非点球大战中的点球总数
            if(PDEventCodeEnum.PENALTY.getEventCode().equals(eventCode) || PDEventCodeEnum.VAR_PENALTY.getEventCode().equals(matchEventInfoDTO.getEventCode()) ||
                    PDEventCodeEnum.CANCELED_VAR_PENALTY.getEventCode().equals(matchEventInfoDTO.getEventCode()) ||
                    PDEventCodeEnum.PENALTY_CANCELED.getEventCode().equals(matchEventInfoDTO.getEventCode())) {
                if(matchScoresEventInfo.getLeft() != null) {
                    countNum = matchScoresEventInfo.getLeft() != 0 ? matchScoresEventInfo.getLeft() : matchScoresEventInfo.getRight();
                }
                finalEventCodeMap.put(PDEventCodeEnum.PENALTY.getEventCode(), countNum);
                Integer wholePenaltyScore = 0;
                Integer periodPenaltyScore = 0;
                if (wholeSores.getPenaltyAwardedTotal() == null) {
                    wholeSores.setPenaltyAwardedTotal(new CommonItem());
                }
                if (periodSores.getPenaltyAwardedTotal() == null) {
                    periodSores.setPenaltyAwardedTotal(new CommonItem());
                }
                if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                    wholePenaltyScore = calValue(wholeSores.getPenaltyAwardedTotal().getHome(), countNum);
                    periodPenaltyScore = calValue(periodSores.getPenaltyAwardedTotal().getHome(), countNum);
                    validPenalty(wholeSores.getPenaltyAwarded().getHome(), wholePenaltyScore);
                    validPenalty(periodSores.getPenaltyAwarded().getHome(), periodPenaltyScore);
                } else if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                    wholePenaltyScore = calValue(wholeSores.getPenaltyAwardedTotal().getAway(), countNum);
                    periodPenaltyScore = calValue(periodSores.getPenaltyAwardedTotal().getAway(), countNum);
                    validPenalty(wholeSores.getPenaltyAwarded().getAway(), wholePenaltyScore);
                    validPenalty(periodSores.getPenaltyAwarded().getAway(), periodPenaltyScore);
                }
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getPenaltyAwardedTotal() || null == periodSores.getPenaltyAwardedTotal()) {
                            wholeSores.setPenaltyAwardedTotal(new CommonItem());
                            periodSores.setPenaltyAwardedTotal(new CommonItem());
                        }

                        wholeSores.getPenaltyAwardedTotal().setHome(wholePenaltyScore);
                        periodSores.getPenaltyAwardedTotal().setHome(periodPenaltyScore);
                        wholeSores.getPenaltyAwardedTotal().setHome(wholePenaltyScore);
                        periodSores.getPenaltyAwardedTotal().setHome(periodPenaltyScore);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getPenaltyAwardedTotal() || null == periodSores.getPenaltyAwardedTotal()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setPenaltyAwardedTotal(commonItem);
                            periodSores.setPenaltyAwardedTotal(commonItem);
                        }
                        wholeSores.getPenaltyAwardedTotal().setAway(wholePenaltyScore);
                        periodSores.getPenaltyAwardedTotal().setAway(periodPenaltyScore);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getPenaltyAwardedTotal().getHome());
                matchEventInfoDTO.setT2(wholeSores.getPenaltyAwardedTotal().getAway());
            }

            // 非点球大战中的点球进球数
            if (matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.PENALTY_GOAL.getEventCode()) ||
                    matchEventInfoDTO.getEventCode().equals(PDEventCodeEnum.PENALTY_MISSED.getEventCode())) {
                if(matchScoresEventInfo.getRight() != null) {
                    countNum = matchScoresEventInfo.getRight();
                }
                finalEventCodeMap.put(PDEventCodeEnum.PENALTY_GOAL.getEventCode(), countNum);
                Integer wholePenaltyScore = 0;
                Integer periodPenaltyScore = 0;
//            Integer wholeScore = 0;
//            Integer periodScore = 0;
                if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                    wholePenaltyScore = calValue(wholeSores.getPenaltyAwarded().getHome(), countNum);
                    periodPenaltyScore = calValue(periodSores.getPenaltyAwarded().getHome(), countNum);
//                wholeScore = calValue(wholeSores.getGoal().getHome(), countNum);
//                periodScore = calValue(periodSores.getGoal().getHome(), countNum);
                } else if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                    wholePenaltyScore = calValue(wholeSores.getPenaltyAwarded().getAway(), countNum);
                    periodPenaltyScore = calValue(periodSores.getPenaltyAwarded().getAway(), countNum);
//                wholeScore = calValue(wholeSores.getGoal().getAway(), countNum);
//                periodScore = calValue(periodSores.getGoal().getAway(), countNum);
                }
                if (matchTimeInfo != null && matchTimeInfo.getPeriod() != 50L) {
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                        if (null == wholeSores.getPenaltyAwarded() || null == periodSores.getPenaltyAwarded()) {
                            wholeSores.setPenaltyAwarded(new CommonItem());
                            periodSores.setPenaltyAwarded(new CommonItem());
                        }

                        wholeSores.getPenaltyAwarded().setHome(wholePenaltyScore);
                        periodSores.getPenaltyAwarded().setHome(periodPenaltyScore);
                    }
                    if (matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                        if (null == wholeSores.getPenaltyAwarded() || null == periodSores.getPenaltyAwarded()) {
                            CommonItem commonItem = new CommonItem();
                            wholeSores.setPenaltyAwarded(commonItem);
                            periodSores.setPenaltyAwarded(commonItem);
                        }
                        wholeSores.getPenaltyAwarded().setAway(wholePenaltyScore);
                        periodSores.getPenaltyAwarded().setAway(periodPenaltyScore);
                    }
                }
                matchEventInfoDTO.setT1(wholeSores.getGoal().getHome());
                matchEventInfoDTO.setT2(wholeSores.getGoal().getAway());
            }

            for (Map.Entry<String, Integer>  entry : finalEventCodeMap.entrySet()) {
                updateSubtimePeriod(matchEventInfoDTO,allPeriodScores,entry.getKey(),entry.getValue(), 15);
                if(PDEventCodeEnum.GOAL.getEventCode().equals(entry.getKey())) {
                    updateSubtimePeriod(matchEventInfoDTO,allPeriodScores,entry.getKey(),entry.getValue(), 5);
                }
            }
            data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
            data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//        data.getMatchScoresInfo().setSecondsMatchStart(matchEventInfoDTO.getSecondsFromStart());
//        data.getMatchScoresInfo().setEventTime(System.currentTimeMillis());
//        data.getMatchScoresInfo().setRemainingTime(matchEventInfoDTO.getSecondsFromStart());
            data.getMatchTimeInfo().setModifyTime(System.currentTimeMillis());
//        data.getMatchTimeInfo().setSecondFromStart(matchEventInfoDTO.getSecondsFromStart());
//        data.getMatchTimeInfo().setEventTime(System.currentTimeMillis());
//        data.getMatchTimeInfo().setRemainingTime(matchEventInfoDTO.getSecondsFromStart());
//            matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
            /**
             * 新增报球员
             * */
            data.getMatchScoresInfo().setScoresJsonType(confirmEventDto.getOperatorName());
//            matchTimeInfoMapper.updateByPrimaryKey( data.getMatchTimeInfo());
            pdMatchInfoRepository.setRedisAndMatchTimeInfo(data.getMatchTimeInfo(), null);
            //推送比分
//            scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),confirmEventDto.getLinkedId());
            stopWatch.stop();
            log.info("FootBallScoreServiceImpl-changeScoresByEvent-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),confirmEventDto.getThirdMatchId());
            Map<String, String> norfinalEventCodeMap = new HashMap<>();
            for (Map.Entry<String, Integer> item :finalEventCodeMap.entrySet()) {
                norfinalEventCodeMap.put(item.getKey(), String.valueOf(item.getValue()));
            }
            norfinalEventCodeMap.put("oldThirdEventId", oldThirdEventId);
            norfinalEventCodeMap.put("redHome", redHome == null?null:String.valueOf(redHome));
            norfinalEventCodeMap.put("redAway", redAway == null?null:String.valueOf(redAway));
            return Pair.of(matchEventInfoDTO, norfinalEventCodeMap);
        } catch (Exception e) {
            String eventCode = cancelEventViaPossible(matchEventInfoDTO.getEventCode());
            if (matchEventInfoDTO.getEventCode().equals(eventCode)) {
                eventCode = confirmEventViaPossible(matchEventInfoDTO.getEventCode());
            }
            String cacheKey = homeAway + eventCode + data.getThirdMatchInfo().getId();
            redisService.set(cacheKey, cacheKey);
            throw e;
        }
    }

    @Override
    public void updateKickOff(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO) {
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(matchEventInfoDTO.getHomeAway()!=null&&matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)){
            wholeSores.getKickOff().setHome(1);
            wholeSores.getKickOff().setAway(0);
        }else if(matchEventInfoDTO.getHomeAway()!=null&&matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
            wholeSores.getKickOff().setAway(1);
            wholeSores.getKickOff().setHome(0);
        }
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setEventTime(System.currentTimeMillis());
        data.getMatchTimeInfo().setModifyTime(System.currentTimeMillis());
        data.getMatchTimeInfo().setEventTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
    }

    @Override
    public CommonItem updateScoresByDeleteEvent(MatchScoreAndTimeVo data, DeleteEventDto deleteEventDto, MatchScoresEventInfo oldEvent) {
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        FootballScores periodSores= allPeriodScores.get(oldEvent.getMatchPeriodId());
        if(periodSores==null||wholeSores==null){
            log.error("updateScoresByDeleteEvent periodSores==null||wholeSores==null eventId ：{}",deleteEventDto.getDeleteEventId());
            return null;
        }
        CommonItem commonItem=new CommonItem();
        CommonItem commonOldItem=new CommonItem();
        if(oldEvent.getEventCode().equals("goal") || PDEventCodeEnum.VAR_GOAL.getEventCode().equals(oldEvent.getEventCode())){
            commonOldItem.setHome(wholeSores.getGoal().getHome());
            commonOldItem.setAway(wholeSores.getGoal().getAway());
            if(oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                wholeSores.getGoal().setHome(wholeSores.getGoal().getHome()-1);
                periodSores.getGoal().setHome(periodSores.getGoal().getHome()-1);
            }else if(oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                wholeSores.getGoal().setAway(wholeSores.getGoal().getAway()-1);
                periodSores.getGoal().setAway(periodSores.getGoal().getAway()-1);
            }
            commonItem.setHome( wholeSores.getGoal().getHome());
            commonItem.setAway( wholeSores.getGoal().getAway());
            data.getMatchScoresInfo().setT1(wholeSores.getGoal().getHome());
            data.getMatchScoresInfo().setT2( wholeSores.getGoal().getAway());
            data.getMatchScoresInfo().setPeriodT1(periodSores.getGoal().getHome());
            data.getMatchScoresInfo().setPeriodT2( periodSores.getGoal().getAway());
            if(wholeSores.getGoal().getHome()<0||wholeSores.getGoal().getAway()<0){
                return null;
            }
            delete15Min(oldEvent,allPeriodScores,"goal",deleteEventDto.getPossibleEventId());
            delete5Min(oldEvent,allPeriodScores,"goal",deleteEventDto.getPossibleEventId());
        }
        if(oldEvent.getEventCode().equals("yellow_card")  || PDEventCodeEnum.VAR_YELLOW_CARD.getEventCode().equals(oldEvent.getEventCode())){
            commonOldItem.setHome(wholeSores.getYellowCard().getHome());
            commonOldItem.setAway(wholeSores.getYellowCard().getAway());
            if(oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                wholeSores.getYellowCard().setHome(wholeSores.getYellowCard().getHome()-1);
                periodSores.getYellowCard().setHome(periodSores.getYellowCard().getHome()-1);
            }else if(oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                wholeSores.getYellowCard().setAway(wholeSores.getYellowCard().getAway()-1);
                periodSores.getYellowCard().setAway(periodSores.getYellowCard().getAway()-1);
            }
            wholeSores.countFaCard();
            periodSores.countFaCard();
            commonItem.setHome( wholeSores.getYellowCard().getHome());
            commonItem.setAway( wholeSores.getYellowCard().getAway());
            if(wholeSores.getYellowCard().getHome()<0||wholeSores.getYellowCard().getAway()<0){
                return null;
            }
            delete15Min(oldEvent,allPeriodScores,"yellow_card",deleteEventDto.getPossibleEventId());
        }
        if(oldEvent.getEventCode().equals("red_card") || PDEventCodeEnum.VAR_RED_CARD.getEventCode().equals(oldEvent.getEventCode())){
            commonOldItem.setHome(wholeSores.getRedCard().getHome());
            commonOldItem.setAway(wholeSores.getRedCard().getAway());
            if(oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                wholeSores.getRedCard().setHome(wholeSores.getRedCard().getHome()-1);
                periodSores.getRedCard().setHome(periodSores.getRedCard().getHome()-1);
            }else if(oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                wholeSores.getRedCard().setAway(wholeSores.getRedCard().getAway()-1);
                periodSores.getRedCard().setAway(periodSores.getRedCard().getAway()-1);
            }
            wholeSores.countFaCard();
            periodSores.countFaCard();
            commonItem.setHome( wholeSores.getRedCard().getHome());
            commonItem.setAway( wholeSores.getRedCard().getAway());
            if(wholeSores.getRedCard().getHome()<0||wholeSores.getRedCard().getAway()<0){
                return null;
            }
            delete15Min(oldEvent,allPeriodScores,"red_card",deleteEventDto.getPossibleEventId());
        }
        if(oldEvent.getEventCode().equals("corner")){
            commonOldItem.setHome(wholeSores.getCorner().getHome());
            commonOldItem.setAway(wholeSores.getCorner().getAway());
            if(oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                wholeSores.getCorner().setHome(wholeSores.getCorner().getHome()-1);
                periodSores.getCorner().setHome(periodSores.getCorner().getHome()-1);
            }else if(oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                wholeSores.getCorner().setAway(wholeSores.getCorner().getAway()-1);
                periodSores.getCorner().setAway(periodSores.getCorner().getAway()-1);
            }
            commonItem.setHome( wholeSores.getCorner().getHome());
            commonItem.setAway( wholeSores.getCorner().getAway());
            if(wholeSores.getCorner().getHome()<0||wholeSores.getCorner().getAway()<0){
                return null;
            }
            delete15Min(oldEvent,allPeriodScores,"corner",deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("yellow_red_card")) {
            commonOldItem.setHome(wholeSores.getYellowRedCard().getHome());
            commonOldItem.setAway(wholeSores.getYellowRedCard().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getYellowRedCard().setHome(wholeSores.getYellowRedCard().getHome() - 1);
                periodSores.getYellowRedCard().setHome(periodSores.getYellowRedCard().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getYellowRedCard().setAway(wholeSores.getYellowRedCard().getAway() - 1);
                periodSores.getYellowRedCard().setAway(periodSores.getYellowRedCard().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getYellowRedCard().getHome());
            commonItem.setAway(wholeSores.getYellowRedCard().getAway());
            if (wholeSores.getYellowRedCard().getHome() < 0 || wholeSores.getYellowRedCard().getAway() < 0) {
                return null;
            }

            commonOldItem.setHome(wholeSores.getRedCard().getHome());
            commonOldItem.setAway(wholeSores.getRedCard().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getRedCard().setHome(wholeSores.getRedCard().getHome() - 1);
                periodSores.getRedCard().setHome(periodSores.getRedCard().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getRedCard().setAway(wholeSores.getRedCard().getAway() - 1);
                periodSores.getRedCard().setAway(periodSores.getRedCard().getAway() - 1);
            }
            wholeSores.countFaCard();
            periodSores.countFaCard();
            commonItem.setHome(wholeSores.getRedCard().getHome());
            commonItem.setAway(wholeSores.getRedCard().getAway());
            if (wholeSores.getRedCard().getHome() < 0 || wholeSores.getRedCard().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "yellow_red_card", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("throw_in")) {
            commonOldItem.setHome(wholeSores.getThrowIn().getHome());
            commonOldItem.setAway(wholeSores.getThrowIn().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getThrowIn().setHome(wholeSores.getThrowIn().getHome() - 1);
                periodSores.getThrowIn().setHome(periodSores.getThrowIn().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getThrowIn().setAway(wholeSores.getThrowIn().getAway() - 1);
                periodSores.getThrowIn().setAway(periodSores.getThrowIn().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getThrowIn().getHome());
            commonItem.setAway(wholeSores.getThrowIn().getAway());
            if (wholeSores.getThrowIn().getHome() < 0 || wholeSores.getThrowIn().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "throw_in", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("attack")) {
            commonOldItem.setHome(wholeSores.getAttack().getHome());
            commonOldItem.setAway(wholeSores.getAttack().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getAttack().setHome(wholeSores.getAttack().getHome() - 1);
                periodSores.getAttack().setHome(periodSores.getAttack().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getAttack().setAway(wholeSores.getAttack().getAway() - 1);
                periodSores.getAttack().setAway(periodSores.getAttack().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getAttack().getHome());
            commonItem.setAway(wholeSores.getAttack().getAway());
            if (wholeSores.getAttack().getHome() < 0 || wholeSores.getAttack().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "attack", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("goal_kick")) {
            commonOldItem.setHome(wholeSores.getGoalKick().getHome());
            commonOldItem.setAway(wholeSores.getGoalKick().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getGoalKick().setHome(wholeSores.getGoalKick().getHome() - 1);
                periodSores.getGoalKick().setHome(periodSores.getGoalKick().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getGoalKick().setAway(wholeSores.getGoalKick().getAway() - 1);
                periodSores.getGoalKick().setAway(periodSores.getGoalKick().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getGoalKick().getHome());
            commonItem.setAway(wholeSores.getGoalKick().getAway());
            if (wholeSores.getGoalKick().getHome() < 0 || wholeSores.getGoalKick().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "goal_kick", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("free_kick")) {
            commonOldItem.setHome(wholeSores.getFreeKickScore().getHome());
            commonOldItem.setAway(wholeSores.getFreeKickScore().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getFreeKickScore().setHome(wholeSores.getFreeKickScore().getHome() - 1);
                periodSores.getFreeKickScore().setHome(periodSores.getFreeKickScore().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getFreeKickScore().setAway(wholeSores.getFreeKickScore().getAway() - 1);
                periodSores.getFreeKickScore().setAway(periodSores.getFreeKickScore().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getFreeKickScore().getHome());
            commonItem.setAway(wholeSores.getFreeKickScore().getAway());
            if (wholeSores.getFreeKickScore().getHome() < 0 || wholeSores.getFreeKickScore().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "free_kick", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("offside")) {
            commonOldItem.setHome(wholeSores.getOffside().getHome());
            commonOldItem.setAway(wholeSores.getOffside().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getOffside().setHome(wholeSores.getOffside().getHome() - 1);
                periodSores.getOffside().setHome(periodSores.getOffside().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getOffside().setAway(wholeSores.getOffside().getAway() - 1);
                periodSores.getOffside().setAway(periodSores.getOffside().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getOffside().getHome());
            commonItem.setAway(wholeSores.getOffside().getAway());
            if (wholeSores.getOffside().getHome() < 0 || wholeSores.getOffside().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "offside", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("shot_on_target")) {
            commonOldItem.setHome(wholeSores.getShotOn().getHome());
            commonOldItem.setAway(wholeSores.getShotOn().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getShotOn().setHome(wholeSores.getShotOn().getHome() - 1);
                periodSores.getShotOn().setHome(periodSores.getShotOn().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getShotOn().setAway(wholeSores.getShotOn().getAway() - 1);
                periodSores.getShotOn().setAway(periodSores.getShotOn().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getShotOn().getHome());
            commonItem.setAway(wholeSores.getShotOn().getAway());
            if (wholeSores.getShotOn().getHome() < 0 || wholeSores.getShotOn().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "shot_on_target", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("shot_off_target")) {
            commonOldItem.setHome(wholeSores.getShotOff().getHome());
            commonOldItem.setAway(wholeSores.getShotOff().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getShotOff().setHome(wholeSores.getShotOff().getHome() - 1);
                periodSores.getShotOff().setHome(periodSores.getShotOff().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getShotOff().setAway(wholeSores.getShotOff().getAway() - 1);
                periodSores.getShotOff().setAway(periodSores.getShotOff().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getShotOff().getHome());
            commonItem.setAway(wholeSores.getShotOff().getAway());
            if (wholeSores.getShotOff().getHome() < 0 || wholeSores.getShotOff().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "shot_off_target", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("dangerous_attack")) {
            commonOldItem.setHome(wholeSores.getDangerousAttack().getHome());
            commonOldItem.setAway(wholeSores.getDangerousAttack().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getDangerousAttack().setHome(wholeSores.getDangerousAttack().getHome() - 1);
                periodSores.getDangerousAttack().setHome(periodSores.getDangerousAttack().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getDangerousAttack().setAway(wholeSores.getDangerousAttack().getAway() - 1);
                periodSores.getDangerousAttack().setAway(periodSores.getDangerousAttack().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getDangerousAttack().getHome());
            commonItem.setAway(wholeSores.getDangerousAttack().getAway());
            if (wholeSores.getDangerousAttack().getHome() < 0 || wholeSores.getDangerousAttack().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "dangerous_attack", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("penalty")) {
            commonOldItem.setHome(wholeSores.getPenaltyAwardedTotal().getHome());
            commonOldItem.setAway(wholeSores.getPenaltyAwardedTotal().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getPenaltyAwardedTotal().setHome(wholeSores.getPenaltyAwardedTotal().getHome() - 1);
                periodSores.getPenaltyAwardedTotal().setHome(periodSores.getPenaltyAwardedTotal().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getPenaltyAwardedTotal().setAway(wholeSores.getPenaltyAwardedTotal().getAway() - 1);
                periodSores.getPenaltyAwardedTotal().setAway(periodSores.getPenaltyAwardedTotal().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getPenaltyAwardedTotal().getHome());
            commonItem.setAway(wholeSores.getPenaltyAwardedTotal().getAway());
            if (wholeSores.getPenaltyAwardedTotal().getHome() < 0 || wholeSores.getPenaltyAwardedTotal().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "penalty", deleteEventDto.getPossibleEventId());
        }
        if (oldEvent.getEventCode().equals("penalty_goal")) {
            commonOldItem.setHome(wholeSores.getGoal().getHome());
            commonOldItem.setAway(wholeSores.getGoal().getAway());
            if (oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
                wholeSores.getPenaltyAwarded().setHome(wholeSores.getPenaltyAwarded().getHome() - 1);
                periodSores.getPenaltyAwarded().setHome(periodSores.getPenaltyAwarded().getHome() - 1);
                wholeSores.getGoal().setHome(wholeSores.getGoal().getHome() - 1);
                periodSores.getGoal().setHome(periodSores.getGoal().getHome() - 1);
            } else if (oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
                wholeSores.getPenaltyAwarded().setAway(wholeSores.getPenaltyAwarded().getAway() - 1);
                periodSores.getPenaltyAwarded().setAway(periodSores.getPenaltyAwarded().getAway() - 1);
                wholeSores.getGoal().setAway(wholeSores.getGoal().getAway() - 1);
                periodSores.getGoal().setAway(periodSores.getGoal().getAway() - 1);
            }
            commonItem.setHome(wholeSores.getGoal().getHome());
            commonItem.setAway(wholeSores.getGoal().getAway());
            if (wholeSores.getGoal().getHome() < 0 || wholeSores.getGoal().getAway() < 0) {
                return null;
            }
            delete15Min(oldEvent, allPeriodScores, "penalty_goal", deleteEventDto.getPossibleEventId());
            delete5Min(oldEvent,allPeriodScores,"goal",deleteEventDto.getPossibleEventId());
        }
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        data.getMatchScoresInfo().setT1(wholeSores.getGoal().getHome());
        data.getMatchScoresInfo().setT2(wholeSores.getGoal().getAway());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        /**
         * 新增报球员
         * */
        data.getMatchScoresInfo().setScoresJsonType(deleteEventDto.getOperatorName());
        //推送比分
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),deleteEventDto.getLinkedId());
        matchScorePdLogService.deleteEventLog(data, deleteEventDto,commonOldItem,commonItem,oldEvent);
        return commonItem;
    }

    @Override
    public CommonItem updateScoresByEditEvent(MatchScoreAndTimeVo data, EditEventDto editEventDto, MatchScoresEventInfo oldEvent) {
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        FootballScores periodSores= allPeriodScores.get(oldEvent.getMatchPeriodId());
        if(periodSores==null||wholeSores==null){
            log.error("updateScoresByDeleteEvent periodSores==null||wholeSores==null eventId ：{}",editEventDto.getEditEventId());
            return null;
        }
        CommonItem commonItem=new CommonItem();
        commonItem.setHome(editEventDto.getHome());
        commonItem.setAway(editEventDto.getAway());
        if(oldEvent.getEventCode().equals("goal")){
            Integer addHome= editEventDto.getHome()- wholeSores.getGoal().getHome();
            Integer addAway= editEventDto.getAway()- wholeSores.getGoal().getAway();
            wholeSores.getGoal().setHome(editEventDto.getHome());
            wholeSores.getGoal().setAway(editEventDto.getAway());
            periodSores.getGoal().setHome(periodSores.getGoal().getHome()+addHome);
            periodSores.getGoal().setAway(periodSores.getGoal().getAway()+addAway);
            data.getMatchScoresInfo().setT1(wholeSores.getGoal().getHome());
            data.getMatchScoresInfo().setT2( wholeSores.getGoal().getAway());
            data.getMatchScoresInfo().setPeriodT1(periodSores.getGoal().getHome());
            data.getMatchScoresInfo().setPeriodT2( periodSores.getGoal().getAway());
        }
        if(oldEvent.getEventCode().equals("yellow_card")){
            Integer addHome= editEventDto.getHome()- wholeSores.getYellowCard().getHome();
            Integer addAway= editEventDto.getAway()- wholeSores.getYellowCard().getAway();
            wholeSores.getYellowCard().setHome(editEventDto.getHome());
            wholeSores.getYellowCard().setAway(editEventDto.getAway());
            periodSores.getYellowCard().setHome(periodSores.getYellowCard().getHome()+addHome);
            periodSores.getYellowCard().setAway(periodSores.getYellowCard().getAway()+addAway);
            wholeSores.countFaCard();
            wholeSores.countFaCard();
        }
        if(oldEvent.getEventCode().equals("red_card")) {
            Integer addHome= editEventDto.getHome()- wholeSores.getRedCard().getHome();
            Integer addAway= editEventDto.getAway()- wholeSores.getRedCard().getAway();
            wholeSores.getRedCard().setHome(editEventDto.getHome());
            wholeSores.getRedCard().setAway(editEventDto.getAway());
            periodSores.getRedCard().setHome(periodSores.getRedCard().getHome()+addHome);
            periodSores.getRedCard().setAway(periodSores.getRedCard().getAway()+addAway);
            wholeSores.countFaCard();
            wholeSores.countFaCard();
        }

        if(oldEvent.getEventCode().equals("corner")){
            Integer addHome= editEventDto.getHome()- wholeSores.getCorner().getHome();
            Integer addAway= editEventDto.getAway()- wholeSores.getCorner().getAway();
            wholeSores.getCorner().setHome(editEventDto.getHome());
            wholeSores.getCorner().setAway(editEventDto.getAway());
            periodSores.getCorner().setHome(periodSores.getCorner().getHome()+addHome);
            periodSores.getCorner().setAway(periodSores.getCorner().getAway()+addAway);
        }
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        /**
         * 新增报球员
         * */
        data.getMatchScoresInfo().setScoresJsonType(editEventDto.getOperatorName());
        //推送比分
//        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),editEventDto.getLinkedId());
        return commonItem;
    }

    @Override
    public Response edit15MinGoal(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto) {
        log.info("15分钟比分编辑2,edit15MinGoal confirmEventDto:{}",confirmEventDto);
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        Long period15 = get15MinPeriod(confirmEventDto.getPeriod(),confirmEventDto.getTimeFromStartSecond());//当前阶段id
        List<Goal15MinDataDto> dataList = confirmEventDto.getDataList();
        //List<MatchPdOperateLog> logList = new ArrayList<>();//日志数组
        // 15分钟编辑的时候 前端传递全量比分 则 这边会 录入全部
        if(!dataList.isEmpty()){
//            String before = "";
//            String after = "";
            for(Goal15MinDataDto fiveData:dataList){
                //获取比赛阶段
                Long period15Min = fiveData.getPeriod15Min();//编辑的15分钟阶段id
                if(period15 < period15Min){//当前阶段id小于编辑阶段id则不能编辑
                    return Response.failed("不能编辑当前阶段之后的15分钟比分");
                }
                FootballScores periodSores15 = allPeriodScores.get(period15Min);
                log.info("15分钟比分编辑,edit15MinGoal period15Min:{},periodSores15:{}",period15Min,periodSores15);

                if(periodSores15 == null){
                    periodSores15 = FootballScores.createMinFootballScores();
                }
//                before = periodSores15.getGoal().getHome()+" - "+periodSores15.getGoal().getAway();
                if("goal".equals(confirmEventDto.getConfirmEventCode())){
                    periodSores15.getGoal().setHome(fiveData.getHomeScore());
                    periodSores15.getGoal().setAway(fiveData.getAwayScore());
                }
//                after = periodSores15.getGoal().getHome()+" - "+periodSores15.getGoal().getAway();
                allPeriodScores.put(period15Min,periodSores15);
                //记录日志
                //logList.add(getFifteenMinLogData(data.getThirdMatchInfo(),confirmEventDto,fiveData.getPeriod15Min(),before,after,"goal"));
            }
            FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
            Integer wholeScoresHome = wholeSores.getGoal().getHome();//全场比分
            Integer wholeScoresAway = wholeSores.getGoal().getAway();//全场比分
            log.info("15分钟比分编辑,全场比分 wholeScoresHome:{},wholeScoresAway:{}",wholeScoresHome,wholeScoresAway);
            Integer fifteenScoresHome = 0;
            Integer fifteenScoresAway = 0;
            List<Long> period15List = get15MinPeriod(period15);
            log.info("15分钟比分编辑, period15List:{}",period15List);
            for(int i=0;i<period15List.size();i++){//获取15分钟阶段比分之和
                FootballScores periodSores15 = allPeriodScores.get(period15List.get(i));
                if(periodSores15 == null){
                    periodSores15 = FootballScores.createMinFootballScores();
                }
                log.info("15分钟比分编辑, 区间比分,periodSores15:{}",periodSores15.getGoal().doCountScoreStr());
                fifteenScoresHome += periodSores15.getGoal().getHome();
                fifteenScoresAway += periodSores15.getGoal().getAway();
            }
            log.info("15分钟比分编辑,编辑比分 wholeScoresHome:{},wholeScoresAway:{}",wholeScoresHome,wholeScoresAway);
            log.info("15分钟比分编辑,编辑比分 :{},:{},:{}",!(wholeScoresHome == fifteenScoresHome && wholeScoresAway == fifteenScoresAway),wholeScoresHome == fifteenScoresHome,wholeScoresAway == fifteenScoresAway);
            if(!(Objects.equals(wholeScoresHome, fifteenScoresHome) && Objects.equals(wholeScoresAway, fifteenScoresAway))){
                return Response.failed("失败，15分钟进球比分与总比分不一致，请检查。");
            }
        } else {
            return Response.failed("15分钟比分数据有误！");
        }

        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        /*for(MatchPdOperateLog matchPdOperateLog : logList){//记录日志
            matchPdOperateLogMapper.insert(matchPdOperateLog);
        }*/
        confirmEventDto.setLinkedId(confirmEventDto.getThirdMatchId()+"");
        //推送比分
        footballCalculationService.save15MinScores( data.getMatchScoresInfo(), data.getMatchScoresInfo().getThirdMatchId());
        /**
         * 新增报球员
         * */
        data.getMatchScoresInfo().setScoresJsonType(confirmEventDto.getOperatorName());
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),confirmEventDto.getLinkedId());
        return Response.success();
    }

    @Override
    public Response edit15MinCorner(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto)
    {
        String linkId = data.getStandardMatchInfo().getId()+"_PD_CORNER";
        if ( StringUtils.isEmpty(linkId))
        {
            confirmEventDto.setLinkedId(confirmEventDto.getThirdMatchId()+"");
        }
        log.info("::{}::edit15MinCorner入参:{}", linkId, JSON.toJSONString(confirmEventDto));

        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);

        // 获取当前阶段id
        Long period15 = get15MinPeriod(confirmEventDto.getPeriod(),confirmEventDto.getTimeFromStartSecond());

        List<Goal15MinDataDto> dataList = confirmEventDto.getDataList();
        if( null != dataList  && dataList.size() >0 )
        {
            // 校验比分
            if ( !checkScoreBalance( data.getMatchScoresInfo(), allPeriodScores, confirmEventDto) )
            {
                return Response.failed("Failed, inconsistent score with corner score. Please check again.");
            }

            for( Goal15MinDataDto fiveData : dataList )
            {
                // 获取比赛阶段
                Long period15Min = fiveData.getPeriod15Min(); //编辑的15分钟阶段id
                if(period15.longValue() < period15Min.longValue()){ //当前阶段id小于编辑阶段id则不能编辑
                    return Response.failed("不能编辑当前阶段之后的15分钟比分");
                }
                FootballScores periodSores15 = allPeriodScores.get(period15Min);
                if(periodSores15 == null){
                    periodSores15 = FootballScores.createMinFootballScores();
                }
                if("corner".equals(confirmEventDto.getConfirmEventCode())){
                    periodSores15.getCorner().setHome(fiveData.getHomeScore());
                    periodSores15.getCorner().setAway(fiveData.getAwayScore());
                }
                allPeriodScores.put(period15Min,periodSores15);
            }
        }
        else
        {
            return Response.failed("15分钟比分数据有误!");
        }

        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        footballCalculationService.save15MinScores( data.getMatchScoresInfo(), data.getMatchScoresInfo().getThirdMatchId());
        /**
         * 新增报球员
         * */
        data.getMatchScoresInfo().setScoresJsonType(confirmEventDto.getOperatorName());
        //推送比分
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(), linkId);

        return Response.success();
    }


    private boolean checkScoreBalance(MatchScoresInfo matchScoresInfo, Map<Long, FootballScores> allPeriodScores, Goal15MinDto confirmEventDto)
    {
        boolean status = true;
        String eventCode = confirmEventDto.getConfirmEventCode();
        Integer homeCount = 0;
        Integer awayCount = 0;

        Integer editHomeCount = 0;
        Integer editAwayCount = 0;

        if ( null != matchScoresInfo && !allPeriodScores.containsKey(WHOLE_MATCH)) {
            FootballScores footballScores = new FootballScores(0l);
            allPeriodScores.put(WHOLE_MATCH, footballScores);
        }
        FootballScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        if ( EventCodeEnum.RED_CARD.code.toLowerCase(Locale.ROOT).equals(eventCode.toLowerCase(Locale.ROOT)) )
        {
            homeCount = wholeSores.getRedCard().getHome();
            awayCount = wholeSores.getRedCard().getAway();
        }
        else if ( EventCodeEnum.YELLOW_CARD.code.toLowerCase(Locale.ROOT).equals(eventCode.toLowerCase(Locale.ROOT))  )
        {
            homeCount = wholeSores.getYellowCard().getHome();
            awayCount = wholeSores.getYellowCard().getAway();
        }
        else if ( EventCodeEnum.CORNER.code.toLowerCase(Locale.ROOT).equals(eventCode.toLowerCase(Locale.ROOT))  )
        {
            homeCount = wholeSores.getCorner().getHome();
            awayCount = wholeSores.getCorner().getAway();
        }

        if ( null!= confirmEventDto && !CollectionUtils.isEmpty(confirmEventDto.getDataList())) {
            for ( Goal15MinDataDto fifteenData : confirmEventDto.getDataList() )
            {
                editHomeCount += fifteenData.getHomeScore();
                editAwayCount += fifteenData.getAwayScore();
            }
        }
        if( homeCount != editHomeCount || awayCount != editAwayCount ){
            status = false;
        }
        return status;
    }

    private Long get15MinPeriod(Long period,Long secondStart){
        //开局15分钟
        if(period==6&&secondStart<60*15){
            return 60899L;
        }
        //15分钟-30分钟
        if(period==6&&secondStart>=60*15&&secondStart<60*30){
            return 61799L;
        }
        //30分钟-上半场
        if(period==6&&secondStart>=60*30){
            return 62699L;
        }
        //下半场开始-59:59分钟
        if(period==7&&secondStart<60*60){
            return 73599L;
        }
        //下半场 60分钟-74:59
        if(period==7&&secondStart>=60*60&&secondStart<60*75){
            return 74499L;
        }
        //下半场 75分钟-全场
        if(period==7&&secondStart>=60*75){
            return 75399L;
        }
        return null;
    }

    private void delete15Min(MatchScoresEventInfo oldEvent, Map<Long, FootballScores> allPeriodScores, String eventCode,Long possibleEventId)
    {
        //计算删除事件时间阶段
        Long secondsFromStart = oldEvent.getSecondsFromStart();
        if (possibleEventId!=null && possibleEventId>0L){
            MatchScoresEventInfo oldPossibleEvent = matchScoresEventInfoMapper.selectByPrimaryKey(possibleEventId);
            if (oldPossibleEvent!=null && oldPossibleEvent.getSecondsFromStart()!=null && oldPossibleEvent.getSecondsFromStart()>0L){
                secondsFromStart = oldPossibleEvent.getSecondsFromStart();
            }
        }
        Long period15 = get15MinPeriod(oldEvent.getMatchPeriodId(),secondsFromStart);
        if(period15 == null)
        {
            log.info("删除事件时间不在15分钟之内");
            return;
        }
        FootballScores periodSores15 = allPeriodScores.get(period15);
        if(periodSores15 != null)
        {
            if( oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code) )
            {
                if( periodSores15.getGoal().getHome() > 0 && "goal".equals(eventCode) )
                {
                    periodSores15.getGoal().setHome(periodSores15.getGoal().getHome()-1);
                }
                else if ( null != periodSores15.getYellowCard() && periodSores15.getYellowCard().getHome() > 0 && "yellow_card".equals(eventCode) )
                {
                    periodSores15.getYellowCard().setHome( periodSores15.getYellowCard().getHome() -1 );
                    if ( null != periodSores15.getFaCard() && periodSores15.getFaCard().getHome() > 0)
                    {
                        periodSores15.getFaCard().setHome( periodSores15.getFaCard().getHome() - 1 );
                    }
                }
                else if ( null != periodSores15.getRedCard() &&  periodSores15.getRedCard().getHome() > 0 && "red_card".equals(eventCode)  )
                {
                    periodSores15.getRedCard().setHome( periodSores15.getRedCard().getHome() -1 );
                    if ( null != periodSores15.getFaCard() && periodSores15.getFaCard().getHome() > 0)
                    {
                        periodSores15.getFaCard().setHome( periodSores15.getFaCard().getHome() - 2 );
                    }
                }
                else if ( null != periodSores15.getYellowRedCard() &&  periodSores15.getYellowRedCard().getHome() > 0 && "yellow_red_card".equals(eventCode)  )
                {
                    periodSores15.getYellowRedCard().setHome( periodSores15.getYellowRedCard().getHome() -1 );
                    periodSores15.getRedCard().setHome( periodSores15.getRedCard().getHome() -1 );
                    if ( null != periodSores15.getFaCard() && periodSores15.getFaCard().getHome() > 0)
                    {
                        periodSores15.getFaCard().setHome( periodSores15.getFaCard().getHome() - 2 );
                    }
                }
                else if( null != periodSores15.getCorner() &&  periodSores15.getCorner().getHome() > 0 && "corner".equals(eventCode))
                {
                    periodSores15.getCorner().setHome(periodSores15.getCorner().getHome() - 1);
                } else if (null != periodSores15.getThrowIn() && periodSores15.getThrowIn().getHome() > 0 && "throw_in".equals(eventCode)) {
                    periodSores15.getThrowIn().setHome(periodSores15.getThrowIn().getHome() - 1);
                } else if (null != periodSores15.getAttack() && periodSores15.getAttack().getHome() > 0 && "attack".equals(eventCode)) {
                    periodSores15.getAttack().setHome(periodSores15.getAttack().getHome() - 1);
                } else if (null != periodSores15.getGoalKick() && periodSores15.getGoalKick().getHome() > 0 && "goal_kick".equals(eventCode)) {
                    periodSores15.getGoalKick().setHome(periodSores15.getGoalKick().getHome() - 1);
                } else if (null != periodSores15.getFreeKickScore() && periodSores15.getFreeKickScore().getHome() > 0 && "free_kick".equals(eventCode)) {
                    periodSores15.getFreeKickScore().setHome(periodSores15.getFreeKickScore().getHome() - 1);
                } else if (null != periodSores15.getOffside() && periodSores15.getOffside().getHome() > 0 && "offside".equals(eventCode)) {
                    periodSores15.getOffside().setHome(periodSores15.getOffside().getHome() - 1);
                } else if (null != periodSores15.getShotOn() && periodSores15.getShotOn().getHome() > 0 && "shot_on_target".equals(eventCode)) {
                    periodSores15.getShotOn().setHome(periodSores15.getShotOn().getHome() - 1);
                } else if (null != periodSores15.getShotOff() && periodSores15.getShotOff().getHome() > 0 && "shot_off_target".equals(eventCode)) {
                    periodSores15.getShotOff().setHome(periodSores15.getShotOff().getHome() - 1);
                } else if (null != periodSores15.getDangerousAttack() && periodSores15.getDangerousAttack().getHome() > 0 && "dangerous_attack".equals(eventCode)) {
                    periodSores15.getDangerousAttack().setHome(periodSores15.getDangerousAttack().getHome() - 1);
                } else if (null != periodSores15.getPenaltyAwardedTotal() && periodSores15.getPenaltyAwardedTotal().getHome() > 0 && "penalty".equals(eventCode)) {
                    periodSores15.getPenaltyAwardedTotal().setHome(periodSores15.getPenaltyAwardedTotal().getHome() - 1);
                } else if (null != periodSores15.getPenaltyAwarded() && periodSores15.getPenaltyAwarded().getHome() > 0 && "penalty_goal".equals(eventCode)) {
                    periodSores15.getPenaltyAwarded().setHome(periodSores15.getPenaltyAwarded().getHome() - 1);
                    periodSores15.getGoal().setHome(periodSores15.getGoal().getHome() - 1);
                }
            }
            else if( oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code) )
            {
                if( periodSores15.getGoal().getAway() > 0 && "goal".equals(eventCode))
                {
                    periodSores15.getGoal().setAway(periodSores15.getGoal().getAway()-1);
                }
                else if ( null != periodSores15.getYellowCard() &&  periodSores15.getYellowCard().getAway() >0 && "yellow_card".equals(eventCode) )
                {
                    periodSores15.getYellowCard().setAway( periodSores15.getYellowCard().getAway() - 1 );
                    if ( null != periodSores15.getFaCard() && periodSores15.getFaCard().getAway() > 0)
                    {
                        periodSores15.getFaCard().setAway( periodSores15.getFaCard().getAway() - 1 );
                    }
                }
                else if (  null != periodSores15.getRedCard() &&  periodSores15.getRedCard().getAway() >0 && "red_card".equals(eventCode)  )
                {
                    periodSores15.getRedCard().setAway( periodSores15.getRedCard().getAway() - 1 );
                    if ( null != periodSores15.getFaCard() && periodSores15.getFaCard().getAway() > 0)
                    {
                        periodSores15.getFaCard().setAway( periodSores15.getFaCard().getAway() - 2 );
                    }
                }
                else if (  null != periodSores15.getYellowRedCard() &&  periodSores15.getYellowRedCard().getAway() >0 && "yellow_red_card".equals(eventCode)  )
                {
                    periodSores15.getYellowRedCard().setAway( periodSores15.getYellowRedCard().getAway() - 1 );
                    periodSores15.getRedCard().setAway( periodSores15.getRedCard().getAway() - 1 );
                    if ( null != periodSores15.getFaCard() && periodSores15.getFaCard().getAway() > 0)
                    {
                        periodSores15.getFaCard().setAway( periodSores15.getFaCard().getAway() - 2 );
                    }
                }
                else if( null != periodSores15.getCorner() &&  periodSores15.getCorner().getAway() > 0 && "corner".equals(eventCode) )
                {
                    periodSores15.getCorner().setAway(periodSores15.getCorner().getAway() - 1);
                } else if (null != periodSores15.getThrowIn() && periodSores15.getThrowIn().getAway() > 0 && "throw_in".equals(eventCode)) {
                    periodSores15.getThrowIn().setAway(periodSores15.getThrowIn().getAway() - 1);
                } else if (null != periodSores15.getAttack() && periodSores15.getAttack().getAway() > 0 && "attack".equals(eventCode)) {
                    periodSores15.getAttack().setAway(periodSores15.getAttack().getAway() - 1);
                } else if (null != periodSores15.getGoalKick() && periodSores15.getGoalKick().getAway() > 0 && "goal_kick".equals(eventCode)) {
                    periodSores15.getGoalKick().setAway(periodSores15.getGoalKick().getAway() - 1);
                } else if (null != periodSores15.getFreeKickScore() && periodSores15.getFreeKickScore().getAway() > 0 && "free_kick".equals(eventCode)) {
                    periodSores15.getFreeKickScore().setAway(periodSores15.getFreeKickScore().getAway() - 1);
                } else if (null != periodSores15.getOffside() && periodSores15.getOffside().getAway() > 0 && "offside".equals(eventCode)) {
                    periodSores15.getOffside().setAway(periodSores15.getOffside().getAway() - 1);
                } else if (null != periodSores15.getShotOn() && periodSores15.getShotOn().getAway() > 0 && "shot_on_target".equals(eventCode)) {
                    periodSores15.getShotOn().setAway(periodSores15.getShotOn().getAway() - 1);
                } else if (null != periodSores15.getShotOff() && periodSores15.getShotOff().getAway() > 0 && "shot_off_target".equals(eventCode)) {
                    periodSores15.getShotOff().setAway(periodSores15.getShotOff().getAway() - 1);
                } else if (null != periodSores15.getDangerousAttack() && periodSores15.getDangerousAttack().getAway() > 0 && "dangerous_attack".equals(eventCode)) {
                    periodSores15.getDangerousAttack().setAway(periodSores15.getDangerousAttack().getAway() - 1);
                } else if (null != periodSores15.getPenaltyAwardedTotal() && periodSores15.getPenaltyAwardedTotal().getAway() > 0 && "penalty".equals(eventCode)) {
                    periodSores15.getPenaltyAwardedTotal().setAway(periodSores15.getPenaltyAwardedTotal().getAway() - 1);
                } else if (null != periodSores15.getPenaltyAwarded() && periodSores15.getPenaltyAwarded().getAway() > 0 && "penalty_goal".equals(eventCode)) {
                    periodSores15.getPenaltyAwarded().setAway(periodSores15.getPenaltyAwarded().getAway() - 1);
                    periodSores15.getGoal().setAway(periodSores15.getGoal().getAway() - 1);
                }
            }
            allPeriodScores.put(period15, periodSores15);
        }
    }

    private void update15Min(MatchEventInfoDTO matchEventInfoDTO,Map<Long, FootballScores> allPeriodScores,String eventCode){
        //更新15分钟进球数
        Long secondsFromStart = matchEventInfoDTO.getSecondsFromStart();
        if (matchEventInfoDTO.getPossibleEventStarTime()!=null && matchEventInfoDTO.getPossibleEventStarTime()>0L){
            secondsFromStart = matchEventInfoDTO.getPossibleEventStarTime();
        }
        Long period15 = get15MinPeriod(matchEventInfoDTO.getMatchPeriodId(),secondsFromStart);
        if(period15 == null){
            log.info("事件时间不在15分钟之内");
            return;
        }
        FootballScores periodSores15 = allPeriodScores.get(period15);
        if(periodSores15 == null){
            periodSores15 = FootballScores.createMinFootballScores();
        }
        if( matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code) )
        {
            if("goal".equals(eventCode))
            {
                periodSores15.getGoal().setHome(periodSores15.getGoal().getHome()+1);
            }
            else if ( "yellow_card".equals(eventCode) )
            {
                if (null == periodSores15.getYellowCard())
                {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setYellowCard(commonItem);
                } else {
                    periodSores15.getYellowCard().setHome( periodSores15.getYellowCard().getHome()+1 );
                }

                if ( null == periodSores15.getFaCard() ) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setFaCard(commonItem);
                } else {
                    periodSores15.getFaCard().setHome(periodSores15.getFaCard().getHome()+1 );
                }

            }
            else if ( "red_card".equals(eventCode) )
            {
                if ( null == periodSores15.getRedCard() ) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setRedCard(commonItem);
                } else {
                    periodSores15.getRedCard().setHome(periodSores15.getRedCard().getHome()+1 );
                }

                if ( null == periodSores15.getFaCard() ) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(2);
                    commonItem.setAway(0);
                    periodSores15.setFaCard(commonItem);
                } else {
                    periodSores15.getFaCard().setHome(periodSores15.getFaCard().getHome()+2 );
                }

            }
            else if ( "corner".equals(eventCode) )
            {
                periodSores15.getCorner().setHome(periodSores15.getCorner().getHome()+1 );
            } else if ("yellow_red_card".equals(eventCode)) {
                if (null == periodSores15.getYellowRedCard()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setYellowRedCard(commonItem);
                } else {
                    periodSores15.getYellowRedCard().setHome(periodSores15.getYellowRedCard().getHome() + 1);
                }
                if (null == periodSores15.getRedCard()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setRedCard(commonItem);
                } else {
                    periodSores15.getRedCard().setHome(periodSores15.getRedCard().getHome() + 1);
                }
                if (null == periodSores15.getFaCard()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(2);
                    commonItem.setAway(0);
                    periodSores15.setFaCard(commonItem);
                } else {
                    periodSores15.getFaCard().setHome(periodSores15.getFaCard().getHome() + 2);
                }
            } else if ("throw_in".equals(eventCode)) {
                if (null == periodSores15.getThrowIn()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setThrowIn(commonItem);
                } else {
                    periodSores15.getThrowIn().setHome(periodSores15.getThrowIn().getHome() + 1);
                }
            } else if ("attack".equals(eventCode)) {
                if (null == periodSores15.getAttack()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setAttack(commonItem);
                } else {
                    periodSores15.getAttack().setHome(periodSores15.getAttack().getHome() + 1);
                }
            } else if ("goal_kick".equals(eventCode)) {
                if (null == periodSores15.getGoalKick()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setGoalKick(commonItem);
                } else {
                    periodSores15.getGoalKick().setHome(periodSores15.getGoalKick().getHome() + 1);
                }
            } else if ("free_kick".equals(eventCode)) {
                if (null == periodSores15.getFreeKickScore()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setFreeKickScore(commonItem);
                } else {
                    periodSores15.getFreeKickScore().setHome(periodSores15.getFreeKickScore().getHome() + 1);
                }
            } else if ("offside".equals(eventCode)) {
                if (null == periodSores15.getOffside()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setOffside(commonItem);
                } else {
                    periodSores15.getOffside().setHome(periodSores15.getOffside().getHome() + 1);
                }
            } else if ("shot_on_target".equals(eventCode)) {
                if (null == periodSores15.getShotOn()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setShotOn(commonItem);
                } else {
                    periodSores15.getShotOn().setHome(periodSores15.getShotOn().getHome() + 1);
                }
            } else if ("shot_off_target".equals(eventCode)) {
                if (null == periodSores15.getShotOff()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setShotOff(commonItem);
                } else {
                    periodSores15.getShotOff().setHome(periodSores15.getShotOff().getHome() + 1);
                }
            } else if ("dangerous_attack".equals(eventCode)) {
                if (null == periodSores15.getDangerousAttack()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setDangerousAttack(commonItem);
                } else {
                    periodSores15.getDangerousAttack().setHome(periodSores15.getDangerousAttack().getHome() + 1);
                }
            } else if ("penalty".equals(eventCode)) {
                if (null == periodSores15.getPenaltyAwarded()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(1);
                    commonItem.setAway(0);
                    periodSores15.setPenaltyAwarded(commonItem);
                } else {
                    periodSores15.getPenaltyAwarded().setHome(periodSores15.getPenaltyAwarded().getHome() + 1);
                }
            }
        }
        else if( matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code) )
        {
            if("goal".equals(eventCode)){
                periodSores15.getGoal().setAway(periodSores15.getGoal().getAway()+1);
            }
            else if ( "yellow_card".equals(eventCode) )
            {
                if ( null == periodSores15.getYellowCard() ) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setYellowCard(commonItem);
                } else {
                    periodSores15.getYellowCard().setAway( periodSores15.getYellowCard().getAway()+1 );
                }

                if ( null == periodSores15.getFaCard() ) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setFaCard(commonItem);
                } else {
                    periodSores15.getFaCard().setAway(periodSores15.getFaCard().getAway()+1 );
                }

            }
            else if (  "red_card".equals(eventCode)  )
            {
                if ( null == periodSores15.getRedCard() ) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setRedCard(commonItem);
                } else {
                    periodSores15.getRedCard().setAway(periodSores15.getRedCard().getAway()+1 );
                }

                if ( null == periodSores15.getFaCard() ) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(2);
                    periodSores15.setFaCard(commonItem);
                } else {
                    periodSores15.getFaCard().setAway(periodSores15.getFaCard().getAway()+2 );
                }

            }
            else if ( "corner".equals(eventCode) )
            {
                periodSores15.getCorner().setAway(periodSores15.getCorner().getAway()+1 );
            } else if ("yellow_red_card".equals(eventCode)) {
                if (null == periodSores15.getYellowRedCard()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setYellowRedCard(commonItem);
                } else {
                    periodSores15.getYellowRedCard().setAway(periodSores15.getYellowRedCard().getAway() + 1);
                }
                if (null == periodSores15.getRedCard()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setRedCard(commonItem);
                } else {
                    periodSores15.getRedCard().setAway(periodSores15.getRedCard().getAway() + 1);
                }
                if (null == periodSores15.getFaCard()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(2);
                    periodSores15.setFaCard(commonItem);
                } else {
                    periodSores15.getFaCard().setAway(periodSores15.getFaCard().getAway() + 2);
                }
            } else if ("throw_in".equals(eventCode)) {
                if (null == periodSores15.getThrowIn()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setThrowIn(commonItem);
                } else {
                    periodSores15.getThrowIn().setAway(periodSores15.getThrowIn().getAway() + 1);
                }
            } else if ("attack".equals(eventCode)) {
                if (null == periodSores15.getAttack()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setAttack(commonItem);
                } else {
                    periodSores15.getAttack().setAway(periodSores15.getAttack().getAway() + 1);
                }
            } else if ("goal_kick".equals(eventCode)) {
                if (null == periodSores15.getGoalKick()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setGoalKick(commonItem);
                } else {
                    periodSores15.getGoalKick().setAway(periodSores15.getGoalKick().getAway() + 1);
                }
            } else if ("free_kick".equals(eventCode)) {
                if (null == periodSores15.getFreeKickScore()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setFreeKickScore(commonItem);
                } else {
                    periodSores15.getFreeKickScore().setAway(periodSores15.getFreeKickScore().getAway() + 1);
                }
            } else if ("offside".equals(eventCode)) {
                if (null == periodSores15.getOffside()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setOffside(commonItem);
                } else {
                    periodSores15.getOffside().setAway(periodSores15.getOffside().getAway() + 1);
                }
            } else if ("shot_on_target".equals(eventCode)) {
                if (null == periodSores15.getShotOn()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setShotOn(commonItem);
                } else {
                    periodSores15.getShotOn().setAway(periodSores15.getShotOn().getAway() + 1);
                }
            } else if ("shot_off_target".equals(eventCode)) {
                if (null == periodSores15.getShotOff()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setShotOff(commonItem);
                } else {
                    periodSores15.getShotOff().setAway(periodSores15.getShotOff().getAway() + 1);
                }
            } else if ("dangerous_attack".equals(eventCode)) {
                if (null == periodSores15.getDangerousAttack()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setDangerousAttack(commonItem);
                } else {
                    periodSores15.getDangerousAttack().setAway(periodSores15.getDangerousAttack().getAway() + 1);
                }
            } else if ("penalty".equals(eventCode)) {
                if (null == periodSores15.getPenaltyAwarded()) {
                    CommonItem commonItem = new CommonItem();
                    commonItem.setHome(0);
                    commonItem.setAway(1);
                    periodSores15.setPenaltyAwarded(commonItem);
                } else {
                    periodSores15.getPenaltyAwarded().setAway(periodSores15.getPenaltyAwarded().getAway() + 1);
                }
            }
        }
        allPeriodScores.put(period15,periodSores15);
    }

    @Override
    public Response edit5MinGoal(MatchScoreAndTimeVo data, Goal5MinDto confirmEventDto) {
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        Long period5 = get5MinPeriod(confirmEventDto.getPeriod(),confirmEventDto.getTimeFromStartSecond());//当前阶段id
        Long period15 = get15MinPeriod(confirmEventDto.getPeriod(),confirmEventDto.getTimeFromStartSecond());//当前阶段id
        List<Goal5MinDataDto> dataList = confirmEventDto.getDataList();
        //List<MatchPdOperateLog> logList = new ArrayList<>();//日志数组
        if(dataList != null && dataList.size() >0){
            String before = "";
            String after = "";
            for(Goal5MinDataDto fiveData:dataList){
                //获取比赛阶段
                Long period5Min = fiveData.getPeriod5Min();//编辑的5分钟阶段id
                if(period5.longValue() < period5Min.longValue()){//当前阶段id小于编辑阶段id则不能编辑
                    return Response.failed("不能编辑当前阶段之后的5分钟比分");
                }
                FootballScores periodSores5 = allPeriodScores.get(period5Min);
                if(periodSores5 == null){
                    periodSores5 = FootballScores.createMinFootballScores();
                }
                //before = periodSores5.getGoal().getHome()+" - "+periodSores5.getGoal().getAway();
                if("goal".equals(confirmEventDto.getConfirmEventCode())){
                    periodSores5.getGoal().setHome(fiveData.getHomeScore());
                    periodSores5.getGoal().setAway(fiveData.getAwayScore());
                }
                //after = periodSores5.getGoal().getHome()+" - "+periodSores5.getGoal().getAway();
                allPeriodScores.put(period5Min,periodSores5);
                //记录日志
                //logList.add(getFiveMinLogData(data.getThirdMatchInfo(),confirmEventDto,fiveData.getPeriod5Min(),before,after));
            }
            Integer fifteenScoresHome = 0;
            Integer fifteenScoresAway = 0;
            List<Long> period15List = get15MinPeriod(period15);
            for(int i=0;i<period15List.size();i++){//判断每个可编辑的十五分钟比分是否与其所属的五分钟比分一致
                FootballScores periodSores15 = allPeriodScores.get(period15List.get(i));
                if(periodSores15 == null){
                    periodSores15 = FootballScores.createMinFootballScores();
                }
                fifteenScoresHome = periodSores15.getGoal().getHome();
                fifteenScoresAway = periodSores15.getGoal().getAway();
                List<Long> period5List = get5MinPeriod(period15List.get(i));
                Integer fiveScoresHome = 0;
                Integer fiveScoresAway = 0;
                for(int j=0;j<period5List.size();j++){
                    FootballScores periodSores5 = allPeriodScores.get(period5List.get(j));
                    if(periodSores5 == null){
                        periodSores5 = FootballScores.createMinFootballScores();
                    }
                    fiveScoresHome += periodSores5.getGoal().getHome();
                    fiveScoresAway += periodSores5.getGoal().getAway();
                }
                if(!(fifteenScoresHome == fiveScoresHome && fifteenScoresAway == fiveScoresAway)){
                    return Response.failed("失败，与 15 分钟 & 进球比分不一致，请检查。");
                }
            }
            //判断五分钟比分，15分钟比分，总比分是否相同
//            if(!(wholeScoresHome == fiveScoresHome && wholeScoresHome == fifteenScoresHome) || !(wholeScoresAway == fiveScoresAway && wholeScoresAway == fifteenScoresAway)){
//                return Response.failed("失败，与 15 分钟 & 进球比分不一致，请检查。");
//            }
        } else {
            return Response.failed("5分钟比分数据有误！");
        }
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        /*for(MatchPdOperateLog matchPdOperateLog : logList){//记录日志
            matchPdOperateLogMapper.insert(matchPdOperateLog);
        }*/
        confirmEventDto.setLinkedId(confirmEventDto.getThirdMatchId()+"");
        //推送比分
        String linkId = data.getStandardMatchInfo().getId()+"_PD_GOAL";
        /**
         * 新增报球员
         * */
        data.getMatchScoresInfo().setScoresJsonType(confirmEventDto.getOperatorName());
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),linkId);
        return Response.success();
    }

    @Override
    public Response edit15MinYellowCard(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto)
    {
        String linkId = data.getStandardMatchInfo().getId()+"_PD_FA_CARD";
        if ( StringUtils.isEmpty(linkId))
        {
            confirmEventDto.setLinkedId(confirmEventDto.getThirdMatchId()+"");
        }
        log.info("::{}::edit15MinYellowCard入参:{}", linkId, JSON.toJSONString(confirmEventDto));

        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);

        // 获取当前阶段id
        Long period15 = get15MinPeriod(confirmEventDto.getPeriod(),confirmEventDto.getTimeFromStartSecond());

        List<Goal15MinDataDto> dataList = confirmEventDto.getDataList();

        if( null != dataList  && dataList.size() >0 )
        {
            if ( !checkScoreBalance( data.getMatchScoresInfo(), allPeriodScores, confirmEventDto) )
            {
                return Response.failed("Failed, inconsistent with Bookings score. Please check again.");
            }

            for( Goal15MinDataDto fiveData : dataList )
            {
                //获取比赛阶段
                Long period15Min = fiveData.getPeriod15Min();
                if(period15.longValue() < period15Min.longValue()){
                    return Response.failed("不能编辑当前阶段之后的15分钟黄牌");
                }
                FootballScores periodSores15 = allPeriodScores.get(period15Min);
                if(periodSores15 == null){
                    periodSores15 = FootballScores.createMinFootballScores();
                }
                if("yellow_card".equals(confirmEventDto.getConfirmEventCode())){
                    if ( null == periodSores15.getYellowCard() )
                    {
                        CommonItem commonItem = new CommonItem();
                        commonItem.setAway(fiveData.getHomeScore());
                        commonItem.setAway(fiveData.getAwayScore());
                        periodSores15.setYellowCard(commonItem);
                    }
                    else
                    {
                        periodSores15.getYellowCard().setHome(fiveData.getHomeScore());
                        periodSores15.getYellowCard().setAway(fiveData.getAwayScore());
                    }

                    if ( null == periodSores15.getFaCard() )
                    {
                        CommonItem commonItem = new CommonItem();
                        commonItem.setAway(fiveData.getHomeScore());
                        commonItem.setAway(fiveData.getAwayScore());
                        periodSores15.setFaCard(commonItem);
                    }
                    else
                    {
                        Integer redCardHomePoint = 0;
                        Integer redCardAwayPoint = 0;
                        if ( null != periodSores15.getRedCard() ) {
                            redCardHomePoint = periodSores15.getRedCard().getHome() * 2;
                            redCardAwayPoint = periodSores15.getRedCard().getAway() * 2;
                        }
                        periodSores15.getFaCard().setHome(redCardHomePoint + fiveData.getHomeScore());
                        periodSores15.getFaCard().setAway(redCardAwayPoint + fiveData.getAwayScore());
                    }

                }
                allPeriodScores.put(period15Min,periodSores15);
            }
        }
        else
        {
            return Response.failed("15分钟黄牌数据有误!");
        }

        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        footballCalculationService.save15MinScores( data.getMatchScoresInfo(), data.getMatchScoresInfo().getThirdMatchId());
        //推送比分
        /**
         * 新增报球员
         * */
        data.getMatchScoresInfo().setScoresJsonType(confirmEventDto.getOperatorName());
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(), linkId);
        return Response.success();
    }


    @Override
    public Response edit15MinRedCard(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto)
    {
        String linkId    = data.getStandardMatchInfo().getId()+"_PD_FA_CARD";
        if ( StringUtils.isEmpty(linkId))
        {
            confirmEventDto.setLinkedId(confirmEventDto.getThirdMatchId()+"");
        }
        log.info("::{}::edit15MinRedCard入参:{}", linkId, JSON.toJSONString(confirmEventDto));

        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);

        // 获取当前阶段id
        Long period15 = get15MinPeriod(confirmEventDto.getPeriod(),confirmEventDto.getTimeFromStartSecond());

        List<Goal15MinDataDto> dataList = confirmEventDto.getDataList();

        if( null != dataList  && dataList.size() >0 )
        {
            if ( !checkScoreBalance( data.getMatchScoresInfo(), allPeriodScores, confirmEventDto) )
            {
                return Response.failed("Failed, inconsistent with Bookings score. Please check again.");
            }

            for( Goal15MinDataDto fiveData : dataList )
            {
                //获取比赛阶段
                Long period15Min = fiveData.getPeriod15Min();//编辑的15分钟阶段id
                if(period15.longValue() < period15Min.longValue()){//当前阶段id小于编辑阶段id则不能编辑
                    return Response.failed("不能编辑当前阶段之后的15分钟红牌");
                }
                FootballScores periodSores15 = allPeriodScores.get(period15Min);
                if(periodSores15 == null){
                    periodSores15 = FootballScores.createMinFootballScores();
                }
                if("red_card".equals(confirmEventDto.getConfirmEventCode())){
                    if ( null == periodSores15.getRedCard() )
                    {
                        CommonItem commonItem = new CommonItem();
                        commonItem.setAway(fiveData.getHomeScore());
                        commonItem.setAway(fiveData.getAwayScore());
                        periodSores15.setRedCard(commonItem);
                    }
                    else
                    {
                        periodSores15.getRedCard().setHome(fiveData.getHomeScore());
                        periodSores15.getRedCard().setAway(fiveData.getAwayScore());
                    }

                    if ( null == periodSores15.getFaCard() )
                    {
                        CommonItem commonItem = new CommonItem();
                        commonItem.setAway(fiveData.getHomeScore() * 2);
                        commonItem.setAway(fiveData.getAwayScore() * 2);
                        periodSores15.setFaCard(commonItem);
                    }
                    else
                    {
                        Integer yellowCardHomePoint = 0;
                        Integer yellowCardAwayPoint = 0;
                        if ( null != periodSores15.getYellowCard() ) {
                            yellowCardHomePoint = periodSores15.getYellowCard().getHome();
                            yellowCardAwayPoint = periodSores15.getYellowCard().getAway();
                        }
                        periodSores15.getFaCard().setHome( yellowCardHomePoint + fiveData.getHomeScore()*2);
                        periodSores15.getFaCard().setAway( yellowCardAwayPoint + fiveData.getAwayScore()*2);
                    }

                }
                allPeriodScores.put(period15Min,periodSores15);
            }
        }
        else
        {
            return Response.failed("15分钟红牌数据有误!");
        }

        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        footballCalculationService.save15MinScores( data.getMatchScoresInfo(), data.getMatchScoresInfo().getThirdMatchId());
        //推送比分与事件
        /**
         * 新增报球员做结算
         * */
        data.getMatchScoresInfo().setScoresJsonType(confirmEventDto.getOperatorName());
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(), linkId);
        return Response.success();
    }

    /*private MatchPdOperateLog getFiveMinLogData(ThirdMatchInfo thirdMatchInfo, Goal5MinDto dto , Long period5Min, String before, String after){
        MatchPdOperateLog matchPdOperateLog = new MatchPdOperateLog();
        if (thirdMatchInfo != null) {
            matchPdOperateLog.setOperateId(thirdMatchInfo.getId()+"");
            matchPdOperateLog.setOperateName(thirdMatchInfo.getHomeAwayInfo());
        }
        matchPdOperateLog.setOperateModule(OperateLogTypeEnum.type_15.getCode()+"");
        matchPdOperateLog.setOperateForwText(before);
        matchPdOperateLog.setOperateRearText(after);
        matchPdOperateLog.setOperateExtId("-");
        matchPdOperateLog.setOperateExtName("-");
        matchPdOperateLog.setOperateParaName(getFiveParam(period5Min));
        matchPdOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10046.getCode().toString());
        matchPdOperateLog.setOperateUserName(dto.getOperatorName());
        matchPdOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchPdOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchPdOperateLog.setIpAddress(dto.getIpAddress());
        return matchPdOperateLog;
    }
    private MatchPdOperateLog getFifteenMinLogData(ThirdMatchInfo thirdMatchInfo, Goal15MinDto dto , Long period15Min, String before, String after,String type){
        MatchPdOperateLog matchPdOperateLog = new MatchPdOperateLog();
        if (thirdMatchInfo != null) {
            matchPdOperateLog.setOperateId(thirdMatchInfo.getId()+"");
            matchPdOperateLog.setOperateName(thirdMatchInfo.getHomeAwayInfo());
        }
        matchPdOperateLog.setOperateModule(OperateLogTypeEnum.type_15.getCode()+"");
        matchPdOperateLog.setOperateForwText(before);
        matchPdOperateLog.setOperateRearText(after);
        matchPdOperateLog.setOperateExtId("-");
        matchPdOperateLog.setOperateExtName("-");
        matchPdOperateLog.setOperatePlaer(type);
        matchPdOperateLog.setOperateParaName(getFifteenParam(period15Min));
        matchPdOperateLog.setOperateType(OperateLogTypeEnum.SCORES_PD_10047.getCode().toString());
        matchPdOperateLog.setOperateUserName(dto.getOperatorName());
        matchPdOperateLog.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchPdOperateLog.setCreateTime(TimeUtils.millsSecondsEast8ZoneGmt());
        matchPdOperateLog.setIpAddress(dto.getIpAddress());
        return matchPdOperateLog;
    }
    private String getFiveParam(Long period5Min){
        if(period5Min == 6005L){
            return "0:00 - 4:59";
        } else if(period5Min == 6010L){
            return "5:00 - 9:59";
        }else if(period5Min == 6015L){
            return "10:00 - 14:59";
        }else if(period5Min == 6020L){
            return "15:00 - 19:59";
        }else if(period5Min == 6025L){
            return "20:00 - 24:59";
        }else if(period5Min == 6030L){
            return "25:00 - 29:59";
        }else if(period5Min == 6035L){
            return "30:00 - 34:59";
        }else if(period5Min == 6040L){
            return "35:00 - 39:59";
        }else if(period5Min == 6045L){
            return "40:00 - 44:59";
        }else if(period5Min == 6050L){
            return "45:00+";
        }else if(period5Min == 7050L){
            return "45:00 - 49:59";
        }else if(period5Min == 7055L){
            return "50:00 - 54:59";
        }else if(period5Min == 7060L){
            return "55:00 - 59:59";
        }else if(period5Min == 7065L){
            return "60:00 - 64:59";
        }else if(period5Min == 7070L){
            return "65:00 - 69:59";
        }else if(period5Min == 7075L){
            return "70:00 - 74:59";
        }else if(period5Min == 7080L){
            return "75:00 - 79:59";
        }else if(period5Min == 7085L){
            return "80:00 - 84:59";
        }else if(period5Min == 7090L){
            return "85:00 - 89:59";
        }else if(period5Min == 7095L){
            return "90:00+";
        }

        return "";
    }

    private String getFifteenParam(Long period15Min){
        if(period15Min == 60899L){
            return "0:00 - 14:59";
        } else if(period15Min == 61799L){
            return "15:00 - 29:59";
        }else if(period15Min == 62699L){
            return "30:00 - HT";
        }else if(period15Min == 73599L){
            return "1HT - 59:59";
        }else if(period15Min == 74499L){
            return "60:00 - 74:59";
        }else if(period15Min == 75399L){
            return "75:00 - FT";
        }

        return "";
    }*/

    //5分钟 阶段计算
    public static Long get5MinPeriod(Long period,Long secondStart){
        //开场-4:59
        if(period==6&&secondStart<60*5){
            return 6005L;
        }
        //5:00 - 9:59
        if(period==6&&secondStart>=60*5&&secondStart<60*10){
            return 6010L;
        }
        //10:00 - 14:59
        if(period==6&&secondStart>=60*10&&secondStart<60*15){
            return 6015L;
        }
        //15:00 - 19:59
        if(period==6&&secondStart>=60*15&&secondStart<60*20){
            return 6020L;
        }
        //20:00 - 24:59
        if(period==6&&secondStart>=60*20&&secondStart<60*25){
            return 6025L;
        }
        //25:00 - 29:59
        if(period==6&&secondStart>=60*25&&secondStart<60*30){
            return 6030L;
        }
        //30:00 - 34:59
        if(period==6&&secondStart>=60*30&&secondStart<60*35){
            return 6035L;
        }
        //35:00 - 39:59
        if(period==6&&secondStart>=60*35&&secondStart<60*40){
            return 6040L;
        }
        //40:00 - 45:00
        if(period==6&&secondStart>=60*40&&secondStart<60*45){
            return 6045L;
        }
        //1H Last-minute Goal
        if(period==6&&secondStart>60*45){
            return 6050L;
        }

        //下半场- 49:59
        if(period==7&&secondStart<60*50){
            return 7050L;
        }
        //50:00 - 54:59
        if(period==7&&secondStart>=60*50&&secondStart<60*55){
            return 7055L;
        }
        //55:00 - 59:59
        if(period==7&&secondStart>=60*55&&secondStart<60*60){
            return 7060L;
        }
        //60:00 - 64:59
        if(period==7&&secondStart>=60*60&&secondStart<60*65){
            return 7065L;
        }
        //65:00 - 69:59
        if(period==7&&secondStart>=60*65&&secondStart<60*70){
            return 7070L;
        }
        //70:00 - 74:59
        if(period==7&&secondStart>=60*70&&secondStart<60*75){
            return 7075L;
        }
        //75:00 - 79:59
        if(period==7&&secondStart>=60*75&&secondStart<60*80){
            return 7080L;
        }
        //80:00 - 84:59
        if(period==7&&secondStart>=60*80&&secondStart<60*85){
            return 7085L;
        }
        //85:00 - 90:00
        if(period==7&&secondStart>=60*85&&secondStart<60*90){
            return 7090L;
        }
        //2H Last-minute Goal
        if(period==7&&secondStart>60*90){
            return 7095L;
        }


        return null;
    }

    private void delete5Min(MatchScoresEventInfo oldEvent,Map<Long, FootballScores> allPeriodScores,String eventCode,Long possibleEventId){
        //计算删除事件时间阶段
        Long secondsFromStart = oldEvent.getSecondsFromStart();
        if (possibleEventId!=null && possibleEventId>0L){
            MatchScoresEventInfo oldPossibleEvent = matchScoresEventInfoMapper.selectByPrimaryKey(possibleEventId);
            if (oldPossibleEvent!=null && oldPossibleEvent.getSecondsFromStart()!=null && oldPossibleEvent.getSecondsFromStart()>0L){
                secondsFromStart = oldPossibleEvent.getSecondsFromStart();
            }
        }
        Long period5 = get5MinPeriod(oldEvent.getMatchPeriodId(),secondsFromStart);
        if(period5 == null){
            log.info("删除事件时间不在5分钟之内");
            return;
        }
        FootballScores periodSores5 = allPeriodScores.get(period5);
        if(periodSores5 != null){
            if(oldEvent.getHomeAway().equals(TeamTypeEnum.HOME.code)){
                if(periodSores5.getGoal().getHome() > 0 && "goal".equals(eventCode)){
                    periodSores5.getGoal().setHome(periodSores5.getGoal().getHome()-1);
                } else if(periodSores5.getCorner().getHome() > 0 && "corner".equals(eventCode)) {
                    periodSores5.getCorner().setHome(periodSores5.getCorner().getHome() - 1);
                }
            } else if(oldEvent.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
                if(periodSores5.getGoal().getAway() > 0 && "goal".equals(eventCode)){
                    periodSores5.getGoal().setAway(periodSores5.getGoal().getAway()-1);
                } else if(periodSores5.getCorner().getAway() > 0 && "corner".equals(eventCode)) {
                    periodSores5.getCorner().setAway(periodSores5.getCorner().getAway() - 1);
                }
            }
        }
    }

    private void update5Min(MatchEventInfoDTO matchEventInfoDTO,Map<Long, FootballScores> allPeriodScores,String eventCode){
        //更新15分钟进球数
        Long secondsFromStart = matchEventInfoDTO.getSecondsFromStart();
        if (matchEventInfoDTO.getPossibleEventStarTime()!=null && matchEventInfoDTO.getPossibleEventStarTime()>0L){
            secondsFromStart = matchEventInfoDTO.getPossibleEventStarTime();
        }
        Long period5 = get5MinPeriod(matchEventInfoDTO.getMatchPeriodId(),secondsFromStart);
        if(period5 == null){
            log.info("事件时间不在15分钟之内");
            return;
        }
        FootballScores periodSores5 = allPeriodScores.get(period5);
        if(periodSores5 == null){
            periodSores5 = FootballScores.createMinFootballScores();
        }
        if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)){
            if("goal".equals(eventCode)){
                periodSores5.getGoal().setHome(periodSores5.getGoal().getHome()+1);
            } else {
                periodSores5.getCorner().setHome(periodSores5.getCorner().getHome()+1);
            }
        }else if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)){
            if("goal".equals(eventCode)){
                periodSores5.getGoal().setAway(periodSores5.getGoal().getAway()+1);
            } else {
                periodSores5.getCorner().setAway(periodSores5.getCorner().getAway()+1);
            }
        }
        allPeriodScores.put(period5,periodSores5);
    }

    //获取当前阶段前的所有15分钟阶段编码
    private List<Long> get15MinPeriod(Long period15){
        List<Long> list = new ArrayList<>();
        list.add(60899L);
        list.add(61799L);
        list.add(62699L);
        list.add(73599L);
        list.add(74499L);
        list.add(75399L);
        List<Long> returnList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(list.get(i) <= period15){
                returnList.add(list.get(i));
            }
        }
        return returnList;
    }

    //根据15分钟阶段获取所属的5分钟阶段编码
    private List<Long> get5MinPeriod(Long period15){
        List<Long> returnList = new ArrayList<>();
        if(period15.equals(60899L)){
            returnList.add(6005l);
            returnList.add(6010l);
            returnList.add(6015l);
        }
        if(period15.equals(61799L)){
            returnList.add(6020l);
            returnList.add(6025l);
            returnList.add(6030l);
        }
        if(period15.equals(62699L)){
            returnList.add(6035l);
            returnList.add(6040l);
            returnList.add(6045l);
            returnList.add(6050l);
        }
        if(period15.equals(73599L)){
            returnList.add(7050l);
            returnList.add(7055l);
            returnList.add(7060l);
        }
        if(period15.equals(74499L)){
            returnList.add(7065l);
            returnList.add(7070l);
            returnList.add(7075l);
        }
        if(period15.equals(75399L)){
            returnList.add(7080l);
            returnList.add(7085l);
            returnList.add(7090l);
            returnList.add(7095l);
        }
        return returnList;
    }

    public MatchScoresEventInfo getItemByVarEventCode(Long matchPeriodId, ThirdMatchInfo thirdMatchInfo, String possibleEventCode){
        List<String> eventCodes = VAR_CONFIRM_EVENT_CODES.stream().map(PDEventCodeEnum::getEventCode).collect(Collectors.toList());
        if (StringUtils.isNotBlank(possibleEventCode)){
            eventCodes.add(possibleEventCode);
        }
        MatchScoresEventInfoExample example =new MatchScoresEventInfoExample();
        example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId())
                .andDataSourceCodeEqualTo(thirdMatchInfo.getDataSourceCode())
                .andEventCodeIn(eventCodes)
                .andHomeAwayIsNotNull()
                .andMatchPeriodIdEqualTo(matchPeriodId);
        example.setOrderByClause("id desc limit 1");
        List<MatchScoresEventInfo> result = matchScoresEventInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

    /**
     * @param matchEventInfoDTO
     * @param thirdMatchInfo
     * @return 点球：左边->总共的点球数量  右边->点球中的进球数
     *         进球：左边->进球数量       右边->0 不代表任何意思
     *         红牌：左边->红牌          右边->黄牌
     */
    private Pair<Pair<Integer, Integer>, String> obtainScoreNum(MatchEventInfoDTO matchEventInfoDTO, ThirdMatchInfo thirdMatchInfo){
        Pair<Integer, Integer> resNum = Pair.of(null, null);
        Pair<Pair<Integer, Integer>, String> result = Pair.of(resNum, null);
        PDEventCodeEnum eventCodeEnum = PDEventCodeEnum.getEventCodeEnum(matchEventInfoDTO.getEventCode());
        if (eventCodeEnum == null) {
            return result;
        }
        String possibleEventCode = "";
        String relativeEventCode = "";
        switch(eventCodeEnum){
            case VAR_GOAL:
            case CANCELED_VAR_GOAL:
                relativeEventCode = PDEventCodeEnum.GOAL.getEventCode();
                possibleEventCode = "possible_goal";
                break;
            case VAR_PENALTY:
            case CANCELED_VAR_PENALTY:
            case PENALTY_GOAL:
            case PENALTY_MISSED:
            case PENALTY_CANCELED:
                relativeEventCode = PDEventCodeEnum.PENALTY.getEventCode();
                possibleEventCode = "possible_penalty";
                break;
            case VAR_YELLOW_CARD:
            case VAR_RED_CARD:
            case CANCELED_VAR_RED_CARD:
                relativeEventCode = PDEventCodeEnum.RED_CARD.getEventCode();
                possibleEventCode = "possible_red_card";
                break;
            default:
                return result;
        }

        // cal score
        MatchScoresEventInfo matchScoresEventInfo = getItemByVarEventCode(matchEventInfoDTO.getMatchPeriodId(), thirdMatchInfo, possibleEventCode);
        if(matchScoresEventInfo == null) {
            String cacheKey = CommonConstant.HOME_AWAY_ALL + matchEventInfoDTO.getEventCode() + thirdMatchInfo.getId();
            redisService.set(cacheKey, cacheKey);
            throw new RuntimeException("[VAR]目前没有任何的确认/可能事件,请先点击任何一个确认事件!");
        }
        if (StringUtils.isEmpty(matchEventInfoDTO.getHomeAway()) || CommonConstant.HOME_AWAY_ALL.equals(matchEventInfoDTO.getHomeAway())){
            matchEventInfoDTO.setHomeAway(matchScoresEventInfo.getHomeAway());
            if(PDEventCodeEnum.VAR_PENALTY==eventCodeEnum) {
                commonEventService.updateMatchEventStatus(thirdMatchInfo.getId(),PDEventCodeEnum.PENALTY.getEventCode(),matchEventInfoDTO.getHomeAway(),"check");
            }
        }
        if(PDEventCodeEnum.VAR_GOAL==eventCodeEnum || PDEventCodeEnum.VAR_PENALTY==eventCodeEnum) {
            if(relativeEventCode.equals(matchScoresEventInfo.getEventCode())) {
                resNum = Pair.of(0, 0);
            } else {
                resNum = Pair.of(1, 0);
            }
        } else if (PDEventCodeEnum.PENALTY_GOAL==eventCodeEnum) {
            resNum = Pair.of(0, 1);
        } else if (PDEventCodeEnum.PENALTY_MISSED==eventCodeEnum) {
            resNum = Pair.of(0, 0);
        } else if (PDEventCodeEnum.PENALTY_CANCELED==eventCodeEnum) {
            resNum = Pair.of(0, -1);
        } else if (PDEventCodeEnum.CANCELED_VAR_GOAL==eventCodeEnum || PDEventCodeEnum.CANCELED_VAR_PENALTY==eventCodeEnum) {
            if (relativeEventCode.equals(matchScoresEventInfo.getEventCode())) {
                resNum = Pair.of(-1, 0);
            } else {
                resNum = Pair.of(0, 0);
            }
        } else if (PDEventCodeEnum.VAR_YELLOW_CARD==eventCodeEnum || PDEventCodeEnum.VAR_RED_CARD==eventCodeEnum || PDEventCodeEnum.CANCELED_VAR_RED_CARD==eventCodeEnum){
            if("red_card".equals(matchScoresEventInfo.getEventCode())) {
                if(PDEventCodeEnum.VAR_RED_CARD==eventCodeEnum) {
                    resNum = Pair.of(0, 0);
                } else if (PDEventCodeEnum.VAR_YELLOW_CARD==eventCodeEnum) {
                    resNum = Pair.of(-1, 1);
                } else {
                    resNum = Pair.of(-1, 0);
                }
            } else {
                if(PDEventCodeEnum.VAR_RED_CARD==eventCodeEnum) {
                    resNum = Pair.of(1, 0);
                } else if (PDEventCodeEnum.VAR_YELLOW_CARD==eventCodeEnum) {
                    resNum = Pair.of(0, 1);
                } else {
                    resNum = Pair.of(0, 0);
                }
            }
        }
        // 为前端提供addition7值来展示赛事事件中不同的消息
        if(VAR_FE_SHOW_CODES.contains(eventCodeEnum)) {
            int countNum = resNum.getRight() != 0 ? resNum.getRight() : resNum.getLeft();
            matchEventInfoDTO.setExtraInfo(String.valueOf(countNum));
        }
        if (PDEventCodeEnum.CANCELED_VAR_PENALTY==eventCodeEnum ||(possibleEventCode.equals(matchScoresEventInfo.getEventCode())
                && !PDEventCodeEnum.VAR_PENALTY.getEventCode().equals(matchScoresEventInfo.getEventCode()))) {
            removePossibleEventStatus(matchEventInfoDTO, possibleEventCode, relativeEventCode, thirdMatchInfo.getId());
        }
        if(PDEventCodeEnum.VAR_PENALTY==eventCodeEnum) {
            String cacheKey = matchEventInfoDTO.getHomeAway() + possibleEventCode + thirdMatchInfo.getId();
            redisService.set(cacheKey, cacheKey);
        }


        result = Pair.of(resNum, matchScoresEventInfo.getThirdEventId());
        return result;
    }

    private void removePossibleEventStatus(MatchEventInfoDTO matchEventInfoDTO, String possibleEventCode, String relativeEventCode, Long thirdMatchId){
        String cacheKey = matchEventInfoDTO.getHomeAway() + possibleEventCode + thirdMatchId;
        redisService.del(cacheKey);
        commonEventService.updateMatchEventStatus(thirdMatchId,relativeEventCode,matchEventInfoDTO.getHomeAway(),null);
    }

    private Integer calValue(Integer originalVal, Integer addVal) {
        Integer totalVal = originalVal + addVal;
        if(totalVal < 0){
            throw new RuntimeException("PD VAR: value less than 0 in current phase!");
        }
        return totalVal;
    }

    private void validPenalty(Integer penaltyAwarded, Integer penaltyAwardedTotal) {
        if (penaltyAwarded > penaltyAwardedTotal) {
            throw new RuntimeException("Goal number in penalty must be less than or equal to penalty total number!");
        }
    }

    public void updateSubtimePeriod(MatchEventInfoDTO matchEventInfoDTO,Map<Long, FootballScores> allPeriodScores,String eventCode, int scoreNum, int minutes) {
        try {
            if(!PDEventCodeEnum.containConfirmRegularEvent(eventCode) || (minutes == 5 && !PDEventCodeEnum.GOAL.getEventCode().equals(eventCode))) {
                return;
            }
            int traceCount = 2;
            Long lastPeriod = null;
            Long curPeriod = null;
            Long secondsFromStart = matchEventInfoDTO.getSecondsFromStart();
            if (matchEventInfoDTO.getPossibleEventStarTime()!=null && matchEventInfoDTO.getPossibleEventStarTime()>0L){
                secondsFromStart = matchEventInfoDTO.getPossibleEventStarTime();
            }
            while(traceCount > 0) {
                switch (minutes) {
                    case 5:
                        curPeriod = get5MinPeriod(matchEventInfoDTO.getMatchPeriodId(),secondsFromStart);
                        break;
                    case 15:
                        curPeriod = get15MinPeriod(matchEventInfoDTO.getMatchPeriodId(),secondsFromStart);
                        break;
                }

                if (curPeriod == null) {
                    log.info("[FootBallScoreServiceImpl] updateSubtimePeriod 事件时间不在所属分钟之内");
                    return;
                }
                if(Objects.equals(curPeriod, lastPeriod)) {
                    log.info("[FootBallScoreServiceImpl] updateSubtimePeriod 已经对其进行验证过了");
                    return;
                }
                lastPeriod = curPeriod;
                FootballScores periodSores = allPeriodScores.get(curPeriod);
                if(periodSores == null){
                    periodSores = FootballScores.createMinFootballScores();
                }
                CommonItem commonItem = periodSores.getFieldByEventCode(eventCode);
                if(commonItem == null) {
                    return;
                }
                int addVal = matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code) ? commonItem.getHome() + scoreNum : commonItem.getAway() + scoreNum;
                if (addVal >= 0) {
                    calScoresForSubtimePeriod(matchEventInfoDTO, periodSores, eventCode, commonItem, addVal, scoreNum);
                    break;
                }
                secondsFromStart -= CommonConstant.MINUTE * minutes;
                if (secondsFromStart < 0) {
                    return;
                }
                traceCount -= 1;
            }
        } catch (Exception e) {
            log.error("[FootBallScoreServiceImpl] updateSubtimePeriod error: ", e);
        }
    }

    private void calScoresForSubtimePeriod(MatchEventInfoDTO matchEventInfoDTO, FootballScores periodSores, String eventCode, CommonItem commonItem, int addVal, int scoreNum) {
        if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.HOME.code)) {
            commonItem.setHome(addVal);
            /**
             * 特殊事件处理 红黄牌单独处理
             */
            if (PDEventCodeEnum.YELLOW_CARD.getEventCode().equals(eventCode)) {
                periodSores.getFaCard().setHome(periodSores.getFaCard().getHome()+scoreNum);
            } else if (PDEventCodeEnum.RED_CARD.getEventCode().equals(eventCode)) {
                periodSores.getFaCard().setHome(periodSores.getFaCard().getHome()+scoreNum*2);
            } else if (PDEventCodeEnum.YELLOW_RED_CARD.getEventCode().equals(eventCode)) {
                periodSores.getRedCard().setHome(periodSores.getFaCard().getHome()+scoreNum);
                periodSores.getFaCard().setHome(periodSores.getFaCard().getHome()+scoreNum*2);
            }
        } else if(matchEventInfoDTO.getHomeAway().equals(TeamTypeEnum.AWAY.code)) {
            commonItem.setAway(addVal);
            /**
             * 特殊事件处理 红黄牌单独处理
             */
            if (PDEventCodeEnum.YELLOW_CARD.getEventCode().equals(eventCode)) {
                periodSores.getFaCard().setAway(periodSores.getFaCard().getAway()+scoreNum);
            } else if (PDEventCodeEnum.RED_CARD.getEventCode().equals(eventCode)) {
                periodSores.getFaCard().setAway(periodSores.getFaCard().getAway()+scoreNum*2);
            } else if (PDEventCodeEnum.YELLOW_RED_CARD.getEventCode().equals(eventCode)) {
                periodSores.getRedCard().setAway(periodSores.getFaCard().getAway()+scoreNum);
                periodSores.getFaCard().setAway(periodSores.getFaCard().getAway()+scoreNum*2);
            }
        }
    }
    public static String cancelEventViaPossible(String eventCode) {
        if ("canceled_red_card".equals(eventCode)) {
            eventCode = "possible_red_card";
        }
        if ("canceled_yellow_card".equals(eventCode)) {
            eventCode = "possible_yellow_card";
        }
        if ("canceled_goal".equals(eventCode)) {
            eventCode = "possible_goal";
        }
        if ("canceled_free_kick".equals(eventCode)) {
            eventCode = "possible_free_kick";
        }
        if ("canceled_corner".equals(eventCode)) {
            eventCode = "possible_corner";
        }
        if ("canceled_penalty".equals(eventCode) || PDEventCodeEnum.PENALTY_MISSED.getEventCode().equals(eventCode) || PDEventCodeEnum.PENALTY_CANCELED.getEventCode().equals(eventCode)) {
            eventCode = "possible_penalty";
        }
        if (PDEventCodeEnum.CANCELED_VAR_RED_CARD.getEventCode().equals(eventCode)) {
            eventCode = "possible_var_red_card";
        }
        if (PDEventCodeEnum.CANCELED_VAR_GOAL.getEventCode().equals(eventCode)) {
            eventCode = "possible_var_goal";
        }
        if (PDEventCodeEnum.CANCELED_VAR_PENALTY.getEventCode().equals(eventCode)) {
            eventCode = "possible_var_penalty";
        }
        return eventCode;
    }

    public static String confirmEventViaPossible(String eventCode) {
        if ("red_card".equals(eventCode)) {
            eventCode = "possible_red_card";
        }
        if ("yellow_card".equals(eventCode)) {
            eventCode = "possible_yellow_card";
        }
        if ("free_kick".equals(eventCode)) {
            eventCode = "possible_free_kick";
        }
        if ("corner".equals(eventCode)) {
            eventCode = "possible_corner";
        }
        if ("goal".equals(eventCode)) {
            eventCode = "possible_goal";
        }
        if (PDEventCodeEnum.PENALTY.getEventCode().equals(eventCode) || PDEventCodeEnum.PENALTY_GOAL.getEventCode().equals(eventCode)) {
            eventCode = "possible_penalty";
        }
        if(PDEventCodeEnum.VAR_GOAL.getEventCode().equals(eventCode)) {
            eventCode = PDEventCodeEnum.POSSIBLE_VAR_GOAL.getEventCode();
        }
        if(PDEventCodeEnum.VAR_PENALTY.getEventCode().equals(eventCode)) {
            eventCode = PDEventCodeEnum.POSSIBLE_VAR_PENALTY.getEventCode();
        }
        if(PDEventCodeEnum.VAR_RED_CARD.getEventCode().equals(eventCode) || PDEventCodeEnum.VAR_YELLOW_CARD.getEventCode().equals(eventCode)) {
            eventCode = PDEventCodeEnum.POSSIBLE_VAR_RED_CARD.getEventCode();
        }
        return eventCode;
    }
}
