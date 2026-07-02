package com.panda.merge.advertise.event.impl;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.dto.FootballMatchEventStatusVo;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.event.FootBallEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.BasketBallScoreService;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.PDEventCodeEnum;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.advertise.ChangeMatchTimeDto;
import com.panda.merge.dto.advertise.PDBasketBallPauseDto;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Objects;

import static com.panda.merge.advertise.common.Constant.*;

@Service
@Slf4j
public class CommonEventServiceImpl implements CommonEventService {
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private BasketBallScoreService basketBallScoreService;
    @Autowired
    private CommonAdvertiseService commonAdvertiseService;
    @Autowired
    private RedisService redisService;
    @Autowired
    FootBallEventService footBallEventService;
    /**
     * 时间修正事件下发
     * 比如滚球开赛阶段中下发
     * */
    @Override
    public boolean updateMatchTimeEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, Integer isGo, String linkedId) {
        //1.创建事件
        MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createMatchTimeEvent(data,secondFromStart,remainTimeMini,eventTime,isGo,period,linkedId);
        //2.更新时间
        basketBallScoreService.updateTime(data,matchEventInfoDTO);
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        matchEventInfoDTO.setT1(matchScoresInfo.getT1());
        matchEventInfoDTO.setT2(matchScoresInfo.getT2());
        matchEventInfoDTO.setFirstT1(matchScoresInfo.getPeriodT1());
        matchEventInfoDTO.setFirstT2(matchScoresInfo.getPeriodT2());
        //3.发送MQ且记录事件
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        if(data.getThirdMatchInfo().getSportId().equals(1l)){
            //根据当前赛事是安全还是危险下发危险进攻或者是安全事件
            boolean isDanger =footBallEventService.matchIsDanger(data.getThirdMatchInfo().getId());
            String eventCode="ball_safe";
            if(isDanger){
                eventCode="dangerous_attack";
            }
            matchEventInfoDTO.setEventCode(eventCode);
            matchEventInfoDTO.setCopyLinkId(matchEventInfoDTO.getCopyLinkId()+"_PD");
            eventProducer.sendPDEventInfo(matchEventInfoDTO);
        }
        return true;
    }

    @Override
    public boolean updateBasketballMatchTimeEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, Integer isGo, ChangeMatchTimeDto dto) {
        //1.创建事件
        MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createMatchTimeEvent(data,secondFromStart,remainTimeMini,eventTime,isGo,period,dto.getLinkedId());
        //2.更新时间
        basketBallScoreService.updateTime(data,matchEventInfoDTO);
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        matchEventInfoDTO.setT1(matchScoresInfo.getT1());
        matchEventInfoDTO.setT2(matchScoresInfo.getT2());
        matchEventInfoDTO.setFirstT1(matchScoresInfo.getPeriodT1());
        matchEventInfoDTO.setFirstT2(matchScoresInfo.getPeriodT2());
        //3.发送MQ且记录事件
        eventProducer.sendBasketballPDEventInfo(matchEventInfoDTO, dto);
        if(data.getThirdMatchInfo().getSportId().equals(1l)){
            //根据当前赛事是安全还是危险下发危险进攻或者是安全事件
            boolean isDanger =footBallEventService.matchIsDanger(data.getThirdMatchInfo().getId());
            String eventCode="ball_safe";
            if(isDanger){
                eventCode="dangerous_attack";
            }
            matchEventInfoDTO.setEventCode(eventCode);
            matchEventInfoDTO.setCopyLinkId(matchEventInfoDTO.getCopyLinkId()+"_PD");
            eventProducer.sendPDEventInfo(matchEventInfoDTO);
        }
        return true;
    }

    @Override
    public boolean updateBasketballPauseAndContinue(MatchScoreAndTimeVo data, PDBasketBallPauseDto dto) {
        //1.计算时间 当前剩余秒 = 上次倒计时- (现在系统时间-上次事件时间)/1000
        Long secondFromStart =  data.getMatchTimeInfo().getSecondFromStart()-(System.currentTimeMillis()- data.getMatchTimeInfo().getEventTime())/1000;
        if (secondFromStart < 0) {
            secondFromStart = 0L;
        }
        long eventTime = System.currentTimeMillis();
        Long period = data.getMatchTimeInfo().getPeriod();
        String linkedId = dto.getLinkedId();
        int timeGo = 0;
        if (1 != dto.getType()) {
            timeGo = 1;
        }
        int clickTime = 1;
        // 非加时赛暂停
        if (dto.getPeriodId() != 40L) {
            if ("home".equals(dto.getHomeAway())) {
                String key = MATCH_BASKETBALL_PERIOD + dto.getHomeAway() + ":" + dto.getThirdMatchId();
                Object obj = redisService.get(key);
                if (obj == null) {
                    redisService.set(key, String.valueOf(clickTime));
                } else {
                    clickTime = Integer.parseInt(obj.toString());
                    redisService.set(key, String.valueOf(++clickTime));
                    int redisNum = Integer.parseInt(redisService.get(key).toString());
                    if (7 == redisNum) {
                        redisService.del(key);
                    }
                }
            } else {
                String key = MATCH_BASKETBALL_PERIOD + dto.getHomeAway() + ":" + dto.getThirdMatchId();
                Object obj = redisService.get(key);
                if (obj == null) {
                    redisService.set(key, String.valueOf(clickTime));
                } else {
                    clickTime = Integer.parseInt(obj.toString());
                    redisService.set(key, String.valueOf(++clickTime));
                    int redisNum = Integer.parseInt(redisService.get(key).toString());
                    if (7 == redisNum) {
                        redisService.del(key);
                    }
                }
            }
        }
        // 加时赛暂停
        if (dto.getPeriodId() == 40L) {
            if ("home".equals(dto.getHomeAway())) {
                String key = MATCH_BASKETBALL_PERIOD_OT + dto.getHomeAway() + ":" + dto.getThirdMatchId();
                Object obj = redisService.get(key);
                if (obj == null) {
                    redisService.set(key, String.valueOf(clickTime));
                } else {
                    clickTime = Integer.parseInt(obj.toString());
                    redisService.set(key, String.valueOf(++clickTime));
                    int redisNum = Integer.parseInt(redisService.get(key).toString());
                    if (2 == redisNum) {
                        redisService.del(key);
                    }
                }
            } else {
                String key = MATCH_BASKETBALL_PERIOD_OT + dto.getHomeAway() + ":" + dto.getThirdMatchId();
                Object obj = redisService.get(key);
                if (obj == null) {
                    redisService.set(key, String.valueOf(clickTime));
                } else {
                    clickTime = Integer.parseInt(obj.toString());
                    redisService.set(key, String.valueOf(++clickTime));
                    int redisNum = Integer.parseInt(redisService.get(key).toString());
                    if (2 == redisNum) {
                        redisService.del(key);
                    }
                }
            }
        }
        //1.创建事件
        MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createMatchTimeEvent(data,secondFromStart,secondFromStart,eventTime,timeGo,period,linkedId);
        matchEventInfoDTO.setHomeAway(dto.getHomeAway());
        //2.更新时间
        basketBallScoreService.updateTimePause(data,matchEventInfoDTO);
        matchEventInfoDTO.setAddition6(clickTime + "");
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        matchEventInfoDTO.setT1(matchScoresInfo.getT1());
        matchEventInfoDTO.setT2(matchScoresInfo.getT2());
        matchEventInfoDTO.setFirstT1(matchScoresInfo.getPeriodT1());
        matchEventInfoDTO.setFirstT2(matchScoresInfo.getPeriodT2());
        if (matchEventInfoDTO.getSportId() == 2 && dto.getType() == 1) {
            matchEventInfoDTO.setCopyLinkId(linkedId + "_pause");
        }
        if (matchEventInfoDTO.getSportId() == 2 && dto.getType() == 2) {
            matchEventInfoDTO.setCopyLinkId(linkedId + "_continue");
        }
        //3.发送MQ且记录事件
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        if(data.getThirdMatchInfo().getSportId().equals(1l)){
            //根据当前赛事是安全还是危险下发危险进攻或者是安全事件
            boolean isDanger =footBallEventService.matchIsDanger(data.getThirdMatchInfo().getId());
            String eventCode="ball_safe";
            if(isDanger){
                eventCode="dangerous_attack";
            }
            matchEventInfoDTO.setEventCode(eventCode);
            matchEventInfoDTO.setCopyLinkId(matchEventInfoDTO.getCopyLinkId()+"_PD");
            eventProducer.sendPDEventInfo(matchEventInfoDTO);
        }
        return true;
    }
    /**
     * 足球 时间修正事件下发
     * 比如滚球开赛阶段中下发
     * */
    @Override
    public boolean updateFootballMatchTimeEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, Integer isGo, String linkedId, Integer controlType) {
        //1.创建事件
        MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createFootballMatchTimeEvent(data,secondFromStart,remainTimeMini,eventTime,isGo,period,linkedId);
        //2.更新时间
        basketBallScoreService.updateTime(data,matchEventInfoDTO);
        //3.发送MQ且记录事件
        matchEventInfoDTO.setControlType(controlType);
        eventProducer.sendPDFootballEventInfo(matchEventInfoDTO);
        return true;
    }

//    @Override
//    public boolean updateBasketballEvent(MatchScoreAndTimeVo data, PDBasketBallSendEventDto sendEventDto) {
//        //1.创建事件
//        MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createBasketballEvent(data,sendEventDto);
//        eventProducer.sendPDBasketballEvent(matchEventInfoDTO);
//        return true;
//    }

    public boolean updateBasketballEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, Integer isGo, String linkedId, Integer controlType) {
        //1.创建事件
        MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createFootballMatchTimeEvent(data,secondFromStart,remainTimeMini,eventTime,isGo,period,linkedId);
        //2.更新时间
        basketBallScoreService.updateTime(data,matchEventInfoDTO);
        //3.发送MQ且记录事件
        matchEventInfoDTO.setControlType(controlType);
        eventProducer.sendPDFootballEventInfo(matchEventInfoDTO);
        return true;
    }

    @Override
    public boolean changeMatchPeriodEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini,
                                          Long eventTime, MatchScoreCommonVo matchScoreCommonVo, String linkedId,String userName) {
        //1.创建事件
        MatchEventInfoDTO matchEventInfoDTO =
                MatchEventUtils.createMatchStatusEvent( data, secondFromStart, remainTimeMini, eventTime, matchScoreCommonVo, period, linkedId, userName);
        matchEventInfoDTO.setExtrainfo( matchEventInfoDTO.getMatchPeriodId()+"");
        Long oldPeriodId = data.getMatchTimeInfo().getPeriod();
        //2.更新阶段
        commonAdvertiseService.updateMatchStatus( data, matchEventInfoDTO);
        if ((6L == period && oldPeriodId == 7L) || (6L == period && oldPeriodId == 31L)) {
            eventProducer.sendChangePeriodPDEventInfo(matchEventInfoDTO);
            // 下发风控
//            eventProducer.sendMatchEventInfo(matchEventInfoDTO);
        }
        //3.发送MQ且记录事件
        eventProducer.sendPDEventInfo( matchEventInfoDTO);
        return true;
    }

    @Override
    public void sendChangeMatchTimeInfo(ChangeMatchTimeDto changeMatchTimeDto, MatchTimeInfo timeInfo, ThirdMatchInfo thirdMatchInfo) {
        Long period = timeInfo.getPeriod();
        long matchFromStart = timeInfo.getSecondFromStart() + (System.currentTimeMillis() - timeInfo.getEventTime()) / 1000;
        String linkedId = changeMatchTimeDto.getLinkedId();
        String userName = changeMatchTimeDto.getOperatorName();
        Long matchTime = changeMatchTimeDto.getMatchTime();
        // 上半场期间44分钟后的修改时间，修改回44分钟以前; 下半场期间，89分钟后，修改回89分钟前。下发MQ给赔率服务
        if (period == 6L && matchFromStart > 2640 && matchTime < 2640 || period == 7L && matchFromStart > 5340 && matchTime < 5340) {
            MatchEventInfoDTO matchEventInfoDTO = new MatchEventInfoDTO();
            matchEventInfoDTO.setAddition1(matchTime + "");
            matchEventInfoDTO.setSecondsFromStart(matchFromStart);
            matchEventInfoDTO.setPeriodRemainingSeconds(matchFromStart);
            matchEventInfoDTO.setCopyLinkId(linkedId);
            matchEventInfoDTO.setRemark(userName);
            matchEventInfoDTO.setMatchPeriodId(period);
            matchEventInfoDTO.setExtrainfo(period + "");
            matchEventInfoDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
            matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
            matchEventInfoDTO.setMatchLength(thirdMatchInfo.getMatchLength());
            matchEventInfoDTO.setSportId(thirdMatchInfo.getSportId());
            matchEventInfoDTO.setCanceled(0);
            eventProducer.sendChangePeriodPDEventInfo(matchEventInfoDTO);
        }
    }


    @Override
    public boolean changeFootBallMatchPeriodEvent(MatchScoreAndTimeVo data, Long period, Long secondFromStart, Long remainTimeMini, Long eventTime, MatchScoreCommonVo matchScoreCommonVo, String linkedId,String userName) {

        //1.创建事件
        MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createMatchStatusEvent(data,secondFromStart,remainTimeMini,eventTime,matchScoreCommonVo,period,linkedId,userName);
        matchEventInfoDTO.setExtrainfo(matchEventInfoDTO.getMatchPeriodId()+"");
        //2.更新阶段
        if(data.getMatchTimeInfo().getPeriod().equals(period)){
            return true;
        }
        commonAdvertiseService.updateMatchStatus(data,matchEventInfoDTO);
        //3.发送MQ且记录事件
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        return true;
    }

    @Override
    public void updateMatchEventStatus(Long thirdMatchId, String possibleEventCode,String homeAway,String penaltyGoal) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String key =MATCH_ADVERTIS_EVENT_STATUS +thirdMatchId;
        Object cacheEventStatus=redisService.get(key);
        FootballMatchEventStatusVo footballMatchEventStatusVo= FootballMatchEventStatusVo.init();
        if(cacheEventStatus!=null){
            try{
                footballMatchEventStatusVo = JSONObject.toJavaObject(JSONObject.parseObject(cacheEventStatus.toString()) ,FootballMatchEventStatusVo.class);
            }catch (Exception e ){
                log.error("buildCacheMatchStatus error::",e);
                
            }
        }else {
            footballMatchEventStatusVo= FootballMatchEventStatusVo.init();
        }
        //可能点球
       if(possibleEventCode.equals("possible_penalty")){
           if(homeAway.equals(TeamTypeEnum.HOME.code)){
               footballMatchEventStatusVo.setHasHomePenalty(true);
           }else {
               footballMatchEventStatusVo.setHasAwayPenalty(true);
           }

       }
        if(possibleEventCode.equals("possible_red_card")){
            if(homeAway.equals(TeamTypeEnum.HOME.code)){
                footballMatchEventStatusVo.setHasHomeRedCard(true);
            }else {
                footballMatchEventStatusVo.setHasAwayRedCard(true);
            }
        }
        if(possibleEventCode.equals("possible_yellow_card")){
            if(homeAway.equals(TeamTypeEnum.HOME.code)){
                footballMatchEventStatusVo.setHasHomeYellowCard(true);
            }else {
                footballMatchEventStatusVo.setHasAwayYellowCard(true);
            }
        }
        if(possibleEventCode.equals("possible_goal")){
            if(homeAway.equals(TeamTypeEnum.HOME.code)){
                footballMatchEventStatusVo.setHasHomeGoal(true);
            }else {
                footballMatchEventStatusVo.setHasAwayGoal(true);
            }
        }
        if(possibleEventCode.equals("possible_corner")){
            if(homeAway.equals(TeamTypeEnum.HOME.code)){
                footballMatchEventStatusVo.setHasHomeCorner(true);
            }else {
                footballMatchEventStatusVo.setHasAwayCorner(true);
            }
        }
        if (possibleEventCode.equals("possible_free_kick")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeFreeKick(true);
            } else {
                footballMatchEventStatusVo.setHasAwayFreeKick(true);
            }
        }
        if (PDEventCodeEnum.POSSIBLE_VAR_RED_CARD.getEventCode().equals(possibleEventCode)) {
            footballMatchEventStatusVo.setHasVARRedCard(true);
        }
        if (PDEventCodeEnum.POSSIBLE_VAR_PENALTY.getEventCode().equals(possibleEventCode)) {
            footballMatchEventStatusVo.setHasVARPenalty(true);
        }
        if (PDEventCodeEnum.POSSIBLE_VAR_GOAL.getEventCode().equals(possibleEventCode)) {
            footballMatchEventStatusVo.setHasVARGoal(true);
        }
        //取消事件或者确认事件都会修改状态为false
        if(possibleEventCode.equals("canceled_penalty")||possibleEventCode.equals("penalty")){
            if(homeAway.equals(TeamTypeEnum.HOME.code)){
                footballMatchEventStatusVo.setHasHomePenalty(false);
                if(possibleEventCode.equals("penalty")) {
                    footballMatchEventStatusVo.setHasHomeConfirmPenalty(true);
                }
            }else {
                footballMatchEventStatusVo.setHasAwayPenalty(false);
                if(possibleEventCode.equals("penalty")) {
                    footballMatchEventStatusVo.setHasAwayConfirmPenalty(true);
                }
            }
        }
        if(possibleEventCode.equals("canceled_red_card")||possibleEventCode.equals("red_card")){
            if(homeAway.equals(TeamTypeEnum.HOME.code)){
                footballMatchEventStatusVo.setHasHomeRedCard(false);
            }else {
                footballMatchEventStatusVo.setHasAwayRedCard(false);
            }
        }
        if(possibleEventCode.equals("canceled_yellow_card")||possibleEventCode.equals("yellow_card")){
            if(homeAway.equals(TeamTypeEnum.HOME.code)){
                footballMatchEventStatusVo.setHasHomeYellowCard(false);
            }else {
                footballMatchEventStatusVo.setHasAwayYellowCard(false);
            }
        }
        if(possibleEventCode.equals("canceled_goal")||possibleEventCode.equals("goal")){
            if(homeAway.equals(TeamTypeEnum.HOME.code)){
                footballMatchEventStatusVo.setHasHomeGoal(false);
            }else {
                footballMatchEventStatusVo.setHasAwayGoal(false);
            }
        }
        if(possibleEventCode.equals("canceled_corner")||possibleEventCode.equals("corner")){
            if(homeAway.equals(TeamTypeEnum.HOME.code)){
                footballMatchEventStatusVo.setHasHomeCorner(false);
            }else {
                footballMatchEventStatusVo.setHasAwayCorner(false);
            }
        }
        if(possibleEventCode.equals("dangerous_attack")){
            footballMatchEventStatusVo.setIsDanger(true);
        }
        if(possibleEventCode.equals("ball_safe")){
            footballMatchEventStatusVo.setIsDanger(false);
        }
        if (possibleEventCode.equals("throw_in")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeThrowIn(false);
            } else {
                footballMatchEventStatusVo.setHasAwayThrowIn(false);
            }
        }
        if (possibleEventCode.equals("possession")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomePossession(false);
            } else {
                footballMatchEventStatusVo.setHasAwayPossession(false);
            }
        }
        if (possibleEventCode.equals("attack")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeAttack(false);
            } else {
                footballMatchEventStatusVo.setHasAwayAttack(false);
            }
        }
        if (possibleEventCode.equals("yellow_red_card")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeYellowRedCard(false);
            } else {
                footballMatchEventStatusVo.setHasAwayYellowRedCard(false);
            }
        }
        if (possibleEventCode.equals("goal_kick")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeGoalKick(false);
            } else {
                footballMatchEventStatusVo.setHasAwayGoalKick(false);
            }
        }
        if (possibleEventCode.equals("free_kick") || possibleEventCode.equals("canceled_free_kick")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeFreeKick(false);
            } else {
                footballMatchEventStatusVo.setHasAwayFreeKick(false);
            }
        }
        if (possibleEventCode.equals("offside")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeOffside(false);
            } else {
                footballMatchEventStatusVo.setHasAwayOffside(false);
            }
        }
        if (possibleEventCode.equals("shot_on_target")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeShotOnTarget(false);
            } else {
                footballMatchEventStatusVo.setHasAwayShotOnTarget(false);
            }
        }
        if (possibleEventCode.equals("shot_off_target")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeShotOffTarget(false);
            } else {
                footballMatchEventStatusVo.setHasAwayShotOffTarget(false);
            }
        }
        if (possibleEventCode.equals("dangerous_attack")) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeDangerousAttack(false);
            } else {
                footballMatchEventStatusVo.setHasAwayDangerousAttack(false);
            }
        }

        if (PDEventCodeEnum.VAR_RED_CARD.getEventCode().equals(possibleEventCode) || PDEventCodeEnum.VAR_YELLOW_CARD.getEventCode().equals(possibleEventCode) ||
                PDEventCodeEnum.CANCELED_VAR_RED_CARD.getEventCode().equals(possibleEventCode)) {
            footballMatchEventStatusVo.setHasVARConfirmRedCard(false);
        }

        if (PDEventCodeEnum.VAR_PENALTY.getEventCode().equals(possibleEventCode) || PDEventCodeEnum.CANCELED_VAR_PENALTY.getEventCode().equals(possibleEventCode)) {
            footballMatchEventStatusVo.setHasVARConfirmPenalty(false);
            if(PDEventCodeEnum.CANCELED_VAR_PENALTY.getEventCode().equals(possibleEventCode)) {
                footballMatchEventStatusVo.setHasHomeConfirmPenalty(false);
                footballMatchEventStatusVo.setHasAwayConfirmPenalty(false);
            }
        }

        if (PDEventCodeEnum.PENALTY_GOAL.getEventCode().equals(possibleEventCode) || PDEventCodeEnum.PENALTY_MISSED.getEventCode().equals(possibleEventCode) ||
                PDEventCodeEnum.PENALTY_CANCELED.getEventCode().equals(possibleEventCode) || PDEventCodeEnum.CANCELED_VAR_PENALTY.getEventCode().equals(possibleEventCode)) {
            if (homeAway.equals(TeamTypeEnum.HOME.code)) {
                footballMatchEventStatusVo.setHasHomeConfirmPenalty(false);
            } else {
                footballMatchEventStatusVo.setHasAwayConfirmPenalty(false);
            }
        }

        if (PDEventCodeEnum.VAR_GOAL.getEventCode().equals(possibleEventCode) || PDEventCodeEnum.CANCELED_VAR_GOAL.getEventCode().equals(possibleEventCode)) {
            footballMatchEventStatusVo.setHasVARConfirmGoal(false);
        }

        footballMatchEventStatusVo.setCurrentEventCode(possibleEventCode);
        redisService.set(key,JSONObject.toJSONString(footballMatchEventStatusVo),36000);
        stopWatch.stop();
        log.info("CommonEventServiceImpl-updateMatchEventStatus-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchId);
    }

    @Override
    public void setDangerOrSafe(Boolean isDanger,Long thirdMatchId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String key =MATCH_ADVERTIS_EVENT_STATUS +thirdMatchId;
        Object cacheEventStatus=redisService.get(key);
        FootballMatchEventStatusVo footballMatchEventStatusVo= FootballMatchEventStatusVo.init();
        if(cacheEventStatus!=null){
            try{
                footballMatchEventStatusVo = JSONObject.toJavaObject(JSONObject.parseObject(cacheEventStatus.toString()) ,FootballMatchEventStatusVo.class);
            }catch (Exception e ){
                log.error("buildCacheMatchStatus error::",e);
                
            }
        }else {
            footballMatchEventStatusVo= FootballMatchEventStatusVo.init();
        }
        footballMatchEventStatusVo.setIsDanger(isDanger);
        redisService.set(key,JSONObject.toJSONString(footballMatchEventStatusVo),36000);
        stopWatch.stop();
        log.info("CommonEventServiceImpl-setDangerOrSafe-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),thirdMatchId);
    }

}
