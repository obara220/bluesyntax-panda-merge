package com.panda.merge.advertise.event.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.advertise.common.Constant;
import com.panda.merge.advertise.dto.FootballMatchEventStatusVo;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.event.FootBallEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.CommonScoreEventService;
import com.panda.merge.advertise.service.FootBallAdvertiseService;
import com.panda.merge.advertise.service.FootBallScoreService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.PDEventCodeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresEventInfoExample;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.util.*;

import static com.panda.merge.advertise.common.Constant.*;
import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_PD_FOOTBALL_KICK_OFF;
import static com.panda.merge.config.RedisConfig.REDIS_WEEK_TIME;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Service
@Slf4j
public class FootBallEventServiceImpl implements FootBallEventService {

    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private FootBallScoreService footBallScoreService;
    @Autowired
    private CommonEventService commonEventService;
    @Autowired
    private MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    private PdMatchInfoRepository pdMatchInfoRepository;

    @Autowired
    private CommonScoreEventService commonScoreEventService;

    public static final List<String> DELETE_EVENT_CODES = Arrays.asList(PDEventCodeEnum.VAR_GOAL.getEventCode(), PDEventCodeEnum.PENALTY_GOAL.getEventCode(),
            PDEventCodeEnum.VAR_RED_CARD.getEventCode(), PDEventCodeEnum.VAR_YELLOW_CARD.getEventCode());
    @Override
    public Response possibleEvent(MatchScoreAndTimeVo data, PossibleEventDto possibleEventDto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //1.组装事件下发
        //计算倒计时
        Long startTimeSecond =possibleEventDto.getTimeFromStartSecond();
        MatchEventInfoDTO matchEventInfoDTO =MatchEventUtils.createSimpleMatchEvent(data,possibleEventDto.getHomeAway(),startTimeSecond,startTimeSecond,
                System.currentTimeMillis(),possibleEventDto.getPossibleEventCode(),data.getMatchTimeInfo().getPeriod(),possibleEventDto.getLinkedId(),possibleEventDto.getOperatorName());
        matchEventInfoDTO.setAddition9("true");
        // 主客队比分设置到addiotion5下发风控
        String scoresJson = data.getMatchScoresInfo().getScoresJson();
        Map<Long, FootballScores> scoresMap = JSON.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
        });
        FootballScores wholeScore = scoresMap.get(WHOLE_MATCH);
        CommonItem goal = wholeScore.getGoal();
        matchEventInfoDTO.setAddition5(goal.getHome() + ":" + goal.getAway());
//        Long timeFromStartSecond = possibleEventDto.getTimeFromStartSecond();
        eventProducer.sendPDFootballClickEventInfo(matchEventInfoDTO,startTimeSecond, null);

        //2.修改当前事件状态 通用接口
        commonEventService.updateMatchEventStatus(possibleEventDto.getThirdMatchId(),possibleEventDto.getPossibleEventCode(),possibleEventDto.getHomeAway(),null);
        //3.推送WS 事件接口
        commonEventService.setDangerOrSafe(true,data.getThirdMatchInfo().getId());
        if (!StringUtils.isAnyEmpty(possibleEventDto.getPossibleEventCode()) && (possibleEventDto.getPossibleEventCode().equals("possible_goal") ||
                possibleEventDto.getPossibleEventCode().equals("possible_red_card") || possibleEventDto.getPossibleEventCode().equals("possible_yellow_card") ||
                possibleEventDto.getPossibleEventCode().equals("possible_penalty") || PDEventCodeEnum.containVAREvent(possibleEventDto.getPossibleEventCode(), CommonConstant.PD_EVENT_TYPE_POSSIBLE))) {
            MatchEventCodeDto matchEventCodeDto = new MatchEventCodeDto();
            matchEventCodeDto.setThirdMatchId(possibleEventDto.getThirdMatchId());
            matchEventCodeDto.setPossibleEventCode(possibleEventDto.getPossibleEventCode());
            matchEventCodeDto.setPossibleEventTime(matchEventInfoDTO.getEventTime());
            matchEventCodeDto.setPossibleEventStarTime(startTimeSecond);
            matchEventCodeDto.setPossibleEventId(matchEventInfoDTO.getPossibleEventId());
            redisService.set(MATCH_FOOTBALL_CONFIRM_EVENT_TIME + possibleEventDto.getThirdMatchId() + ":" + possibleEventDto.getPossibleEventCode() + ":" + possibleEventDto.getHomeAway(), JSON.toJSONString(matchEventCodeDto));
        }
//        data.getMatchTimeInfo().setSecondFromStart(startTimeSecond);
//        data.getMatchTimeInfo().setEventTime(System.currentTimeMillis());
        data.getMatchTimeInfo().setModifyTime(System.currentTimeMillis());
//        matchTimeInfoMapper.updateByPrimaryKey(data.getMatchTimeInfo());
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(data.getMatchTimeInfo(), null);
        stopWatch.stop();
        log.info("FootBallEventServiceImpl-possibleEvent-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),possibleEventDto.getThirdMatchId());
        return Response.success();
    }

    @Override
    public Response cancelEvent(MatchScoreAndTimeVo data, CancelEventDto cancelEventDto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //计算倒计时
        Long startTimeSecond =cancelEventDto.getTimeFromStartSecond();
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createSimpleMatchEvent(data,cancelEventDto.getHomeAway(),startTimeSecond,startTimeSecond,
                System.currentTimeMillis(),cancelEventDto.getCancelEventCode(),data.getMatchTimeInfo().getPeriod(),cancelEventDto.getLinkedId(),cancelEventDto.getOperatorName());
        //1.计算比分
        Map<String, String> eventCodeNumMap = null;
        if(PDEventCodeEnum.containVAREvent(cancelEventDto.getCancelEventCode(), CommonConstant.PD_EVENT_TYPE_CANCEL)) {
            ConfirmEventDto confirmEventDto = new ConfirmEventDto();
            BeanUtils.copyProperties(cancelEventDto,confirmEventDto);
            confirmEventDto.setConfirmEventCode(cancelEventDto.getCancelEventCode());
            Pair<MatchEventInfoDTO, Map<String, String>> scoreResult = footBallScoreService.changeScoresByEvent(data,confirmEventDto,matchEventInfoDTO);
            matchEventInfoDTO = scoreResult.getLeft();
            eventCodeNumMap = scoreResult.getRight();
        }
        matchEventInfoDTO.setAddition9("false");
        // 主客队比分设置到addiotion5下发风控
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        String scoresJson = matchScoresInfo.getScoresJson();
        Map<Long, FootballScores> scoresMap = JSON.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
        });
        FootballScores wholeScore = scoresMap.get(WHOLE_MATCH);
        CommonItem goal = wholeScore.getGoal();
        matchEventInfoDTO.setAddition5(goal.getHome() + ":" + goal.getAway());
//        Long timeFromStartSecond = cancelEventDto.getTimeFromStartSecond();
//        if ("penalty_missed".equals(cancelEventDto.getCancelEventCode())) {
//            MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
//            example.createCriteria().andHomeAwayEqualTo(cancelEventDto.getHomeAway()).andEventCodeEqualTo("penalty");
//            example.setOrderByClause("id desc limit 1");
//            List<MatchScoresEventInfo> matchScoresEventInfos = matchScoresEventInfoMapper.selectByExample(example);
//            if (!CollectionUtils.isEmpty(matchScoresEventInfos)) {
//                // 将赛事消息表事件存入缓存字段，用于重踢使用
//                matchEventInfoDTO.setAddition3(String.valueOf(matchScoresEventInfos.get(0).getId()));
//                if ("retake_pen".equals(matchScoresEventInfos.get(0).getAddition6())) {
//                    matchEventInfoDTO.setAddition3(matchScoresEventInfos.get(0).getAddition3());
//                }
//            }
//        }
        if ("penalty_missed".equals(cancelEventDto.getCancelEventCode())) {
            if (!ObjectUtils.isEmpty(cancelEventDto.getDeleteEventId())) {
                MatchScoresEventInfo preMatchScoresEventInfo = matchScoresEventInfoMapper.selectByPrimaryKey(cancelEventDto.getDeleteEventId());
                if (("retake_pen".equals(preMatchScoresEventInfo.getAddition6()) || "no_retake_pen".equals(preMatchScoresEventInfo.getAddition6()))) {
                    matchEventInfoDTO.setAddition3(preMatchScoresEventInfo.getAddition3());
                    matchEventInfoDTO.setAddition6(preMatchScoresEventInfo.getAddition6());
                }
                String preventDuplicationKey = String.format(PREVENT_DUPLICATION_KEY, cancelEventDto.getThirdMatchId());
                if ( redisService.hasKey(preventDuplicationKey) ) {
                    log.info("::{}::prevent_duplication_ke,eventId:{}", cancelEventDto.getLinkedId(), cancelEventDto.getDeleteEventId());
                    matchEventInfoDTO.setAddition8("1");
                }
            } else if ("99".equals(cancelEventDto.getRetakeStatus())) {
                MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
                String thirdMatchSourceId = data.getThirdMatchInfo().getThirdMatchSourceId();
                example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId).andHomeAwayEqualTo(cancelEventDto.getHomeAway())
                        .andEventCodeEqualTo("penalty").andAddition6In(Arrays.asList("retake_pen", "no_retake_pen"));
                example.setOrderByClause("id desc limit 1");
                List<MatchScoresEventInfo> matchScoresEventInfos = matchScoresEventInfoMapper.selectByExample(example);
                if (!CollectionUtils.isEmpty(matchScoresEventInfos)) {
                    // 将赛事消息表事件存入缓存字段，用于重踢使用
                    matchEventInfoDTO.setAddition3(matchScoresEventInfos.get(0).getAddition3());
                    matchEventInfoDTO.setAddition4(cancelEventDto.getRetakeStatus());
                    matchEventInfoDTO.setAddition6(matchScoresEventInfos.get(0).getAddition6());
                }
            }
            // VAR进球时页面取消重踢显示
            MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
            String thirdMatchSourceId = data.getThirdMatchInfo().getThirdMatchSourceId();
            example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId);
            example.setOrderByClause("id desc limit 1");
            List<MatchScoresEventInfo> matchScoresEventInfos = matchScoresEventInfoMapper.selectByExample(example);
            if (!CollectionUtils.isEmpty(matchScoresEventInfos)) {
                MatchScoresEventInfo matchScoresEventInfo = matchScoresEventInfos.get(0);
                if ("var_penalty".equals(matchScoresEventInfo.getEventCode()) && matchScoresEventInfo.getHomeAway().equals(cancelEventDto.getHomeAway())) {
                    matchEventInfoDTO.setAddition8("1");
                }
            }
        }
        eventProducer.sendPDFootballClickEventInfo( matchEventInfoDTO, startTimeSecond, eventCodeNumMap);

        //2.修改当前事件状态 通用接口
        commonEventService.updateMatchEventStatus(cancelEventDto.getThirdMatchId(),cancelEventDto.getCancelEventCode(),cancelEventDto.getHomeAway(),null);
        //3.推送WS 事件接口
        commonEventService.setDangerOrSafe(false,data.getThirdMatchInfo().getId());
        String cancelEventCode = cancelEventDto.getCancelEventCode();
        boolean flag = cancelEventCode.equals("canceled_goal") || cancelEventCode.equals("canceled_red_card")
                || cancelEventCode.equals("canceled_yellow_card") || cancelEventCode.equals("canceled_penalty") || PDEventCodeEnum.containVAREvent(cancelEventCode, CommonConstant.PD_EVENT_TYPE_CANCEL);
        if (!StringUtils.isAnyEmpty(cancelEventDto.getCancelEventCode()) && flag) {
            String possibleEventCode = null;
            switch (cancelEventDto.getCancelEventCode()){
                case "canceled_goal":
                    possibleEventCode = "possible_goal";
                    break;
                case "canceled_red_card":
                    possibleEventCode = "possible_red_card";
                    break;
                case "canceled_yellow_card":
                    possibleEventCode = "possible_yellow_card";
                    break;
                case "canceled_penalty":
                    possibleEventCode = "possible_penalty";
                    break;
                case "canceled_var_red_card":
                    possibleEventCode = "possible_var_red_card";
                    break;
                case "canceled_var_goal":
                    possibleEventCode = "possible_var_goal";
                    break;
                case "canceled_var_penalty":
                    possibleEventCode = "possible_var_penalty";
                    break;
            }
            redisService.del(MATCH_FOOTBALL_CONFIRM_EVENT_TIME + cancelEventDto.getThirdMatchId() + ":" + possibleEventCode + ":" + cancelEventDto.getHomeAway());
        }
        stopWatch.stop();
        log.info("FootBallEventServiceImpl-cancelEvent-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),cancelEventDto.getThirdMatchId());
        return Response.success();
    }

    @Autowired
    FootBallAdvertiseService footBallAdvertiseService;

    @Override
    public Response confirmEvent(MatchScoreAndTimeVo data, ConfirmEventDto confirmEventDto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //判断当前是否是 可能点球
        Long startTimeSecond =confirmEventDto.getTimeFromStartSecond();
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createSimpleMatchEvent(data,confirmEventDto.getHomeAway(),startTimeSecond,startTimeSecond,
                System.currentTimeMillis(),confirmEventDto.getConfirmEventCode(),data.getMatchTimeInfo().getPeriod(),confirmEventDto.getLinkedId(),confirmEventDto.getOperatorName());
        MatchEventCodeDto matchEventCodeDto = new MatchEventCodeDto();
        String eventCode = confirmEventDto.getConfirmEventCode();
        boolean flag = PDEventCodeEnum.containVAREvent(eventCode, CommonConstant.PD_EVENT_TYPE_CONFIRM) || PDEventCodeEnum.containConfirmRegularEvent(eventCode);

        if (!StringUtils.isAnyEmpty(confirmEventDto.getConfirmEventCode()) && flag) {
            String possibleEventCode = null;
            switch (confirmEventDto.getConfirmEventCode()){
                case "goal":
                    possibleEventCode = "possible_goal";
                    break;
                case "red_card":
                    possibleEventCode = "possible_red_card";
                    break;
                case "yellow_card":
                    possibleEventCode = "possible_yellow_card";
                    break;
                    // 红黄牌
                case "yellow_red_card":
                    possibleEventCode = "yellow_red_card";
                    break;
                    // 界外球
                case "throw_in":
                    possibleEventCode = "throw_in";
                    break;
                case "possession":
                    possibleEventCode = "possession";
                    break;
                case "possession_count":
                    possibleEventCode = "possession_count";
                    break;
                    // 进攻
                case "attack":
                    possibleEventCode = "attack";
                    break;
                // 球门球
                case "goal_kick":
                    possibleEventCode = "goal_kick";
                    break;
                // 任意球
                case "free_kick":
                    possibleEventCode = "possible_free_kick";
                    break;
                // 越位
                case "offside":
                    possibleEventCode = "offside";
                    break;
                // 射正
                case "shot_on_target":
                    possibleEventCode = "shot_on_target";
                    break;
                // 射偏
                case "shot_off_target":
                    possibleEventCode = "shot_off_target";
                    break;
                // 危险进攻
                case "dangerous_attack":
                    possibleEventCode = "dangerous_attack";
                    break;
                // 点球
                case "penalty":
                    possibleEventCode = "possible_penalty";
                    break;
                // VAR 罚牌
                case "var_red_card":
                case "var_yellow_card":
                    possibleEventCode = PDEventCodeEnum.POSSIBLE_VAR_RED_CARD.getEventCode();
                    break;
                // VAR 进球
                case "var_goal":
                    possibleEventCode = PDEventCodeEnum.POSSIBLE_VAR_GOAL.getEventCode();
                    break;
                // VAR 点球
                case "var_penalty":
                case "var_penalty_goal":
                    possibleEventCode = PDEventCodeEnum.POSSIBLE_VAR_PENALTY.getEventCode();
                    break;
            }
            boolean confirmFlag = "yellow_red_card".equals(eventCode) || "throw_in".equals(eventCode)
                    || "attack".equals(eventCode) || "goal_kick".equals(eventCode) || "offside".equals(eventCode)
                    || "shot_on_target".equals(eventCode) || "shot_off_target".equals(eventCode)
                    || "dangerous_attack".equals(eventCode) || "possession".equals(eventCode) || "possession_count".equals(eventCode);

            if (confirmFlag) {
                matchEventCodeDto.setThirdMatchId(confirmEventDto.getThirdMatchId());
                matchEventCodeDto.setPossibleEventCode(confirmEventDto.getConfirmEventCode());
                matchEventCodeDto.setPossibleEventTime(matchEventInfoDTO.getEventTime());
                matchEventCodeDto.setPossibleEventStarTime(startTimeSecond);
                matchEventCodeDto.setPossibleEventId(matchEventInfoDTO.getPossibleEventId());
                redisService.set(MATCH_FOOTBALL_CONFIRM_EVENT_TIME + confirmEventDto.getThirdMatchId() + ":" +
                        possibleEventCode + ":" + confirmEventDto.getHomeAway(), JSON.toJSONString(matchEventCodeDto));
            }
            Object obj = redisService.get(MATCH_FOOTBALL_CONFIRM_EVENT_TIME + confirmEventDto.getThirdMatchId() +":"+ possibleEventCode+":"+confirmEventDto.getHomeAway());
            if (obj != null) {
                matchEventCodeDto = JSONObject.parseObject(obj.toString(), MatchEventCodeDto.class);
                if (matchEventCodeDto != null && matchEventCodeDto.getPossibleEventTime() != null && matchEventCodeDto.getPossibleEventStarTime()!=null) {
                    matchEventInfoDTO.setPossibleEventTime(matchEventCodeDto.getPossibleEventTime());
                    matchEventInfoDTO.setPossibleEventStarTime(matchEventCodeDto.getPossibleEventStarTime());
                    redisService.del(MATCH_FOOTBALL_CONFIRM_EVENT_TIME + confirmEventDto.getThirdMatchId() +":"+ possibleEventCode+":"+confirmEventDto.getHomeAway());
                }
            }
        }

        //1.计算比分
        Pair<MatchEventInfoDTO, Map<String, String>> scoreResult = footBallScoreService.changeScoresByEvent(data,confirmEventDto,matchEventInfoDTO);
        matchEventInfoDTO = scoreResult.getLeft();
        matchEventInfoDTO.setAddition9("true");
        if(matchEventInfoDTO.getPossibleEventTime()!=null && matchEventInfoDTO.getPossibleEventTime()>0L){
            matchEventInfoDTO.setEventTime(matchEventInfoDTO.getPossibleEventTime());
        }
        // 主客队比分设置到addiotion5下发风控
        String scoresJson = data.getMatchScoresInfo().getScoresJson();
        Map<Long, FootballScores> scoresMap = JSON.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
        });
        FootballScores wholeScore = scoresMap.get(WHOLE_MATCH);
        CommonItem goal = wholeScore.getGoal();
        matchEventInfoDTO.setAddition5(goal.getHome() + ":" + goal.getAway());

        if ("penalty_goal".equals(confirmEventDto.getConfirmEventCode()) && "penaltyConfirm".equals(confirmEventDto.getPenaltyGoal())) {
            if (!ObjectUtils.isEmpty(confirmEventDto.getDeleteEventId())) {
                MatchScoresEventInfo preMatchScoresEventInfo = matchScoresEventInfoMapper.selectByPrimaryKey(confirmEventDto.getDeleteEventId());
                if ("retake_pen".equals(preMatchScoresEventInfo.getAddition6()) || "no_retake_pen".equals(preMatchScoresEventInfo.getAddition6())) {
                    matchEventInfoDTO.setAddition3(preMatchScoresEventInfo.getAddition3());
                    matchEventInfoDTO.setAddition6(preMatchScoresEventInfo.getAddition6());
                }
            } else if ("99".equals(confirmEventDto.getRetakeStatus())) {
                MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
                String thirdMatchSourceId = data.getThirdMatchInfo().getThirdMatchSourceId();
                example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId).andHomeAwayEqualTo(confirmEventDto.getHomeAway())
                        .andEventCodeEqualTo("penalty").andAddition6In(Arrays.asList("retake_pen", "no_retake_pen"));
                example.setOrderByClause("id desc limit 1");
                List<MatchScoresEventInfo> matchScoresEventInfos = matchScoresEventInfoMapper.selectByExample(example);
                if (!CollectionUtils.isEmpty(matchScoresEventInfos)) {
                    // 将赛事消息表事件存入缓存字段，用于重踢使用
                    matchEventInfoDTO.setAddition3(matchScoresEventInfos.get(0).getAddition3());
                    matchEventInfoDTO.setAddition4(confirmEventDto.getRetakeStatus());
                    matchEventInfoDTO.setAddition6(matchScoresEventInfos.get(0).getAddition6());
                }
            }
            // VAR进球时页面取消重踢显示
            MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
            String thirdMatchSourceId = data.getThirdMatchInfo().getThirdMatchSourceId();
            example.createCriteria().andThirdMatchSourceIdEqualTo(thirdMatchSourceId);
            example.setOrderByClause("id desc limit 1");
            List<MatchScoresEventInfo> matchScoresEventInfos = matchScoresEventInfoMapper.selectByExample(example);
            if (!CollectionUtils.isEmpty(matchScoresEventInfos)) {
                MatchScoresEventInfo matchScoresEventInfo = matchScoresEventInfos.get(0);
                if ("var_penalty".equals(matchScoresEventInfo.getEventCode()) && matchScoresEventInfo.getHomeAway().equals(confirmEventDto.getHomeAway())) {
                    matchEventInfoDTO.setAddition8("1");
                }
            }
        }

        // 自动补充射正事件
        Boolean pdCode = Constant.PD.equals(matchEventInfoDTO.getDataSourceCode()) || Constant.PD2.equals(matchEventInfoDTO.getDataSourceCode());
        if ( pdCode && PDEventCodeEnum.GOAL.getEventCode().equals(eventCode) )
        {
            matchEventInfoDTO.setEventCode(eventCode);
            addShotOnEvent( data, confirmEventDto, matchEventInfoDTO);
        }

        // 下游需要自动补充事件时间比进球快5毫秒
        startTimeSecond += 5L;
        matchEventInfoDTO.setSecondsFromStart( matchEventInfoDTO.getSecondsFromStart() + 5L);
        matchEventInfoDTO.setEventTime( matchEventInfoDTO.getEventTime() + 5L);

        eventProducer.sendPDFootballClickEventInfo( matchEventInfoDTO, startTimeSecond, scoreResult.getRight());
        if (matchEventCodeDto!=null && matchEventCodeDto.getPossibleEventId()!=null){
            redisService.set(MATCH_FOOTBALL_CONFIRM_POSSIBLE_TIME + matchEventInfoDTO.getPossibleEventId(),matchEventCodeDto.getPossibleEventId().toString(),129600L);
        }
        //xiafakick_off
        if(matchEventInfoDTO.getEventCode().equals("goal")||"penalty_goal".equals(matchEventInfoDTO.getEventCode())){
            log.info("::{}::进球确认事件后的开球事件下发开始", matchEventInfoDTO.getCopyLinkId());
            MatchEventInfoDTO finalMatchEventInfoDTO = matchEventInfoDTO;
            log.info("::{}::进球确认事件后的开球事件接手参数:{}", matchEventInfoDTO.getCopyLinkId(), JSON.toJSONString(finalMatchEventInfoDTO) );
//            CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() -> sendKickOffEvent(finalMatchEventInfoDTO) ));
            String homeAway = null;
            boolean eventCodeChk = "goal".equals(confirmEventDto.getConfirmEventCode())
                    || "penalty_goal".equals(confirmEventDto.getConfirmEventCode()) && "penaltyConfirm".equals(confirmEventDto.getPenaltyGoal());
            if (!StringUtils.isAnyEmpty(confirmEventDto.getHomeAway()) && (eventCodeChk)) {
                switch (confirmEventDto.getHomeAway()) {
                    case "home":
                        homeAway = "away";
                        break;
                    case "away":
                        homeAway = "home";
                        break;
                    default:
                        break;
                }
            }
            String key = RONGHE_PD_FOOTBALL_KICK_OFF + confirmEventDto.getThirdMatchId() + ":" + homeAway;
            redisService.set(key, JSONObject.toJSON(finalMatchEventInfoDTO).toString(),REDIS_WEEK_TIME);
        }

        //2.修改当前事件状态 通用接口
        commonEventService.updateMatchEventStatus(confirmEventDto.getThirdMatchId(),confirmEventDto.getConfirmEventCode(),confirmEventDto.getHomeAway(),confirmEventDto.getPenaltyGoal());
        //3.推送WS 事件接口
        commonEventService.setDangerOrSafe(true,data.getThirdMatchInfo().getId());
        stopWatch.stop();
        log.info("FootBallEventServiceImpl-confirmEvent-耗时={}, thirdMatchId={}", stopWatch.getTotalTimeMillis(),confirmEventDto.getThirdMatchId());
        //4.推送比分变更到风控
        return Response.success();
    }

    @Override
    public Response confirmPenaltyEvent( MatchScoreAndTimeVo data, ConfirmPenaltyEventDTO confirmPenaltyEventDTO) {
        String linkId = confirmPenaltyEventDTO.getLinkedId();
        String extendScore = data.getMatchScoresInfo().getScoresJsonExtra();
        FootballPenaltyScores footballPenaltyScores = null;
        if( StringUtils.isEmpty(extendScore) ){
            log.error("::{}::赛事拓展的信息为空", linkId);
            return Response.failed("点球比分的信息缺失");
        } else {
            footballPenaltyScores = JSONObject.toJavaObject( (JSONObject.parseObject(extendScore)) , FootballPenaltyScores.class);
        }

        Integer setScore = PDEventCodeEnum.PENALTY_MISSED.getEventCode().equals( confirmPenaltyEventDTO.getConfirmEventCode()) ? 0 :
                PDEventCodeEnum.PENALTY_GOAL.getEventCode().equals( confirmPenaltyEventDTO.getConfirmEventCode()) ? 1 : 0;

        String targetRound = confirmPenaltyEventDTO.getTargetRound().toString();
        Map<String,CommonItem> roundScores = footballPenaltyScores.getRoundScores();
        if ( MapUtils.isEmpty(roundScores) || !roundScores.containsKey(targetRound) ) {
            log.error("::{}::当前编辑的轮次不存在", linkId);
            return Response.failed("当前编辑的轮次不存在");
        }

        // 点球大战某轮比分编辑
        CommonItem commonItem = roundScores.get( targetRound);
        if ( TeamTypeEnum.HOME.code.equals( confirmPenaltyEventDTO.getHomeAway() )) {
            commonItem.setHome( setScore);
        } else {
            commonItem.setAway( setScore);
        }

        for ( Map.Entry<String,CommonItem> entry : roundScores.entrySet() ) {
            if ( targetRound.equals(entry.getKey()) ) {
                entry.setValue(commonItem);
            }
        }

        footballPenaltyScores.setRoundScores(roundScores);

        // 根据每轮比分重新计算前5轮比分以及返回点球大战总比分
        CommonItem allRoundsScore = footballPenaltyScores.editRoundsScoreByRounds();
        log.info("::{}::计算总点球数{}", linkId, allRoundsScore);

        // 记录点球大战总比分
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores penaltyScore = allPeriodScores.get(50L);
        penaltyScore.setGoal( allRoundsScore);
        log.info("::{}::confirmPenaltyEvent-confirmPenaltyEvent:{}", linkId, JSON.toJSONString(footballPenaltyScores));

        // 更新入库
        data.getMatchScoresInfo().setScoresJsonExtra( JSONObject.toJSONString( footballPenaltyScores, SerializerFeature.WriteMapNullValue));
        data.getMatchScoresInfo().setScoresJson( JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime( System.currentTimeMillis());
        log.info("::{}::confirmPenaltyEvent-matchScoresInfo:{}", linkId, JSON.toJSONString(data.getMatchScoresInfo()));
        pdMatchInfoRepository.setRedisAndMatchScoresInfo( data.getMatchScoresInfo(), null);

        // 防止重复提交
        preventDuplication( linkId, confirmPenaltyEventDTO.getConfirmEventId());

        // 取消点球
        if ( PDEventCodeEnum.PENALTY_MISSED.getEventCode().equals( confirmPenaltyEventDTO.getConfirmEventCode()) ) {
        } else if ( PDEventCodeEnum.PENALTY_GOAL.getEventCode().equals( confirmPenaltyEventDTO.getConfirmEventCode()) ) {
            // 确认点球
            PenaltyScoresEditDto penaltyScoresEditDto = new PenaltyScoresEditDto();
            BeanUtils.copyProperties( confirmPenaltyEventDTO, penaltyScoresEditDto);
            eventProducer.sendPenaltyEvent( penaltyScoresEditDto, data);
        }
        return Response.success();
    }

    private void sendKickOffEvent(MatchEventInfoDTO matchEventInfoDTO) {
        String linkId = matchEventInfoDTO.getCopyLinkId();
        log.info("::{}::进球确认事件后的开球事件下发", linkId);
        try {
            Thread.sleep(10000);//2399改成10秒后下发
        } catch (InterruptedException e) {

        }
        MatchEventInfoDTO kickoff = new MatchEventInfoDTO();
        BeanUtils.copyProperties(matchEventInfoDTO, kickoff);
        log.info("::{}::进球确认事件后的开球事件参数:{}", linkId, JSON.toJSONString(kickoff) );
        kickoff.setEventCode("kick_off");
        if (!StringUtils.isAnyEmpty(kickoff.getHomeAway())){
            switch (kickoff.getHomeAway()){
                case "home":
                    kickoff.setHomeAway("away");
                    break;
                case "away":
                    kickoff.setHomeAway("home");
                    break;
                default:break;
            }
        }
        kickoff.setCopyLinkId(matchEventInfoDTO.getCopyLinkId()+"_kick_off");
        eventProducer.sendPDEventInfo(kickoff);
    }


    private void preventDuplication( String linkId, Long eventId) {
        log.info("::{}::preventDuplication设置, eventId:{}", linkId, eventId);
        MatchScoresEventInfo matchScoresEventInfo = matchScoresEventInfoMapper.selectByPrimaryKey(eventId);
        if ( null != matchScoresEventInfo ) {
            matchScoresEventInfo.setAddition8("1");
            matchScoresEventInfoMapper.updateByPrimaryKey(matchScoresEventInfo);
        }
    }

    private void addShotOnEvent( MatchScoreAndTimeVo data, ConfirmEventDto confirmEventDto, MatchEventInfoDTO matchEventInfoDTO)
    {
        String linkId = confirmEventDto.getLinkedId();
        if ( StringUtils.isEmpty(linkId) )
        {
            linkId = UUID.randomUUID().toString();
        }
        log.info("::{}::addShotOnEvent入参:{}", linkId, JSON.toJSONString(matchEventInfoDTO));
        // 1. 射正的累加
        MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
        if ( Objects.isNull(matchTimeInfo) || 50L == matchTimeInfo.getPeriod() )
        {
            log.info("::{}::无效的赛事阶段", linkId);
            return;
        }
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        Long currentPeriod = data.getMatchTimeInfo().getPeriod();
        JSONObject periodFootballScores = JSONObject.parseObject( matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        FootballScores periodSores = allPeriodScores.get(currentPeriod);

        if ( confirmEventDto.getHomeAway().equals(TeamTypeEnum.HOME.code) )
        {
            if (null == wholeSores.getShotOn() || null == periodSores.getShotOn())
            {
                CommonItem commonItem = new CommonItem();
                wholeSores.setShotOn(commonItem);
                periodSores.setShotOn(commonItem);
            }
            wholeSores.getShotOn().setHome(wholeSores.getShotOn().getHome() + 1);
            periodSores.getShotOn().setHome(periodSores.getShotOn().getHome() + 1);
        }
        if ( confirmEventDto.getHomeAway().equals(TeamTypeEnum.AWAY.code))
        {
            if (null == wholeSores.getShotOn() || null == periodSores.getShotOn())
            {
                CommonItem commonItem = new CommonItem();
                wholeSores.setShotOn(commonItem);
                periodSores.setShotOn(commonItem);
            }
            wholeSores.getShotOn().setAway(wholeSores.getShotOn().getAway() + 1);
            periodSores.getShotOn().setAway(periodSores.getShotOn().getAway() + 1);
        }
//        matchEventInfoDTO.setT1(wholeSores.getShotOn().getHome());
//        matchEventInfoDTO.setT2(wholeSores.getShotOn().getAway());

        allPeriodScores.put( WHOLE_MATCH, wholeSores);
        allPeriodScores.put( currentPeriod, periodSores);
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        // 2.更新新
        pdMatchInfoRepository.setRedisAndMatchScoresInfo( data.getMatchScoresInfo(), null);

        String shotLinkId = linkId + "_" + PDEventCodeEnum.SHOT_ON_TARGET.getEventCode();
        MatchEventInfoDTO shotTargetEventInfo = new MatchEventInfoDTO();
        BeanUtils.copyProperties( matchEventInfoDTO, shotTargetEventInfo);
        shotTargetEventInfo.setEventCode( PDEventCodeEnum.SHOT_ON_TARGET.getEventCode());
        shotTargetEventInfo.setThirdEventId( IdWorker.getId() + "");
        shotTargetEventInfo.setCopyLinkId(shotLinkId);
        shotTargetEventInfo.setT1(wholeSores.getShotOn().getHome());
        shotTargetEventInfo.setT2(wholeSores.getShotOn().getAway());
        // 3.通知下游
        eventProducer.sendShotOnEvent( shotLinkId, matchEventInfoDTO.getDataSourceCode(), shotTargetEventInfo);
    }

    @Override
    public void sendKickOffEventAfterGoal(MatchEventInfoDTO matchEventInfoDTO) {
        String linkId = matchEventInfoDTO.getCopyLinkId();
        log.info("::{}::进球确认事件后的开球事件下发", linkId);
//        try {
//            Thread.sleep(10000);//2399改成10秒后下发
//        } catch (InterruptedException e) {
//
//        }
        MatchEventInfoDTO kickoff = new MatchEventInfoDTO();
        BeanUtils.copyProperties(matchEventInfoDTO, kickoff);
        log.info("::{}::进球确认事件后的开球事件参数:{}", linkId, JSON.toJSONString(kickoff) );
//        kickoff.setEventCode("kick_off");
//        if (!StringUtils.isAnyEmpty(kickoff.getHomeAway())){
//            switch (kickoff.getHomeAway()){
//                case "home":
//                    kickoff.setHomeAway("away");
//                    break;
//                case "away":
//                    kickoff.setHomeAway("home");
//                    break;
//                default:break;
//            }
//        }
        kickoff.setCopyLinkId(matchEventInfoDTO.getCopyLinkId()+"_kick_off");
        kickoff.setThirdEventId(PD+"_"+ UUID.randomUUID().toString());
        eventProducer.sendPDEventInfo(kickoff);
    }

    @Override
    public Response isDanger(MatchScoreAndTimeVo data, IsDangerDto isDangerDto) {
        Long startTimeSecond =isDangerDto.getTimeFromStartSecond();
        //判断是否安全
        String eventCode="ball_safe";
        if(isDangerDto.getIsDanger()==1){
            eventCode="dangerous_attack";
        }
        MatchEventInfoDTO matchEventInfoDTO =MatchEventUtils.createSimpleMatchEvent(data,"",startTimeSecond,startTimeSecond,
                System.currentTimeMillis(),eventCode,data.getMatchTimeInfo().getPeriod(),isDangerDto.getLinkedId(),isDangerDto.getOperatorName());
        if(isDangerDto.getIsDanger()==1){
            matchEventInfoDTO.setAddition9("true");
        }else {
            matchEventInfoDTO.setAddition9("false");
        }
        // 危险事件更新比赛时间和持续时间
//        updateTimeAndScoresInfo(isDangerDto,data);

        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        //修改事件状态
        commonEventService.updateMatchEventStatus(isDangerDto.getThirdMatchId(),eventCode,"none",null);
        return Response.success();
    }

    /**
     * 危险事件更新比赛时间和持续时间
     *
     * @param isDangerDto 危险事件
     * @param data        原始数据
     */
    private void updateTimeAndScoresInfo(IsDangerDto isDangerDto, MatchScoreAndTimeVo data) {
        Long timeFromStartSecond = isDangerDto.getTimeFromStartSecond();
        long currentTime = System.currentTimeMillis();
        MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
        matchTimeInfo.setSecondFromStart(timeFromStartSecond);
        matchTimeInfo.setRemainingTime(timeFromStartSecond);
        matchTimeInfo.setModifyTime(currentTime);
        matchTimeInfo.setEventTime(currentTime);
//        matchTimeInfoMapper.updateByPrimaryKeySelective(matchTimeInfo);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        matchScoresInfo.setSecondsMatchStart(timeFromStartSecond);
        matchScoresInfo.setRemainingTime(timeFromStartSecond);
        matchScoresInfo.setEventTime(currentTime);
        matchScoresInfo.setModifyTime(currentTime);
//        matchScoresInfoMapper.updateByPrimaryKeySelective(matchScoresInfo);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
    }

    @Override
    public Response kickOff(MatchScoreAndTimeVo data, KickOffDto kickOff) {
        Long startTimeSecond =0L;
        //判断是否安全
        String eventCode="kick_off_team";
        // 野鸡比赛没有视频场景，不区分主客队
        if (StringUtils.isEmpty(kickOff.getWhoKickOff())) {
            eventCode = "kick_off_team_none";
        }
        MatchEventInfoDTO matchEventInfoDTO =MatchEventUtils.createSimpleMatchEvent(data,kickOff.getWhoKickOff(),startTimeSecond,startTimeSecond,
                System.currentTimeMillis(),eventCode,data.getMatchTimeInfo().getPeriod(),kickOff.getLinkedId(),kickOff.getOperatorName());
        matchEventInfoDTO.setAddition9(matchIsDanger(kickOff.getThirdMatchId()).toString());
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        matchEventInfoDTO.setExtrainfo("1");
        matchEventInfoDTO.setEventCode("time_start");
        matchEventInfoDTO.setThirdEventId(matchEventInfoDTO.getCopyLinkId()+"_time_start");
        matchEventInfoDTO.setCopyLinkId(matchEventInfoDTO.getCopyLinkId()+"_time_start");
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        //修改事件状态
        footBallScoreService.updateKickOff(data,matchEventInfoDTO);
        //下发WS
        /**
         * 新增报球员
         * */
        data.getMatchScoresInfo().setScoresJsonType(kickOff.getOperatorName());
        //推送比分
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),kickOff.getLinkedId());
        return Response.success();
    }

    @Override
    public Response overTimeEvent(MatchScoreAndTimeVo data, OverTimeEventDto overTimeEventDto) {
        if(overTimeEventDto.getMinute()==null){
            return Response.failed("补时必须录入分钟");
        }
        Long startTimeSecond =overTimeEventDto.getTimeFromStartSecond();
        //判断是否安全
        String eventCode="injury_time";

        MatchEventInfoDTO matchEventInfoDTO =MatchEventUtils.createSimpleMatchEvent(data,"",startTimeSecond,startTimeSecond,
                System.currentTimeMillis(),eventCode,data.getMatchTimeInfo().getPeriod(),overTimeEventDto.getLinkedId(),overTimeEventDto.getOperatorName());
        matchEventInfoDTO.setExtrainfo(overTimeEventDto.getMinute().toString());
        matchEventInfoDTO.setAddition9(matchIsDanger(overTimeEventDto.getThirdMatchId()).toString());
        eventProducer.sendPDEventInfo(matchEventInfoDTO);

        return Response.success();
    }

    @Override
    public Response deleteEvent(MatchScoreAndTimeVo data, DeleteEventDto deleteEventDto)
    {
        Long startTimeSecond =deleteEventDto.getTimeFromStartSecond();
        //1.删除事件计算比分得到新的比分
        MatchScoresEventInfo oldEvent = matchScoresEventInfoMapper.selectByPrimaryKey(deleteEventDto.getDeleteEventId());
        if (!"1".equals(oldEvent.getAddition7()) && DELETE_EVENT_CODES.contains(oldEvent.getEventCode())) {
            throw new RuntimeException("Cannot be delete for upholding the original verdict event!");
        }
        oldEvent.setAddition10("1");
        oldEvent.setModifyTime(System.currentTimeMillis());
        Object obj = redisService.get(MATCH_FOOTBALL_CONFIRM_POSSIBLE_TIME + deleteEventDto.getDeleteEventId());
        if (obj != null) {
            Long possibleEventId = Long.valueOf(obj.toString());
            if (possibleEventId>0L){
                deleteEventDto.setPossibleEventId(possibleEventId);
            }
        }
        CommonItem commonItem = footBallScoreService.updateScoresByDeleteEvent(data,deleteEventDto,oldEvent);
        if( commonItem==null )
        {
            return Response.failed("事件比分已经为0无法删除");
        }
        String homeAway = "";
        if ("home".equals(oldEvent.getHomeAway())) {
            homeAway = "away";
        }
        if ("away".equals(oldEvent.getHomeAway())) {
            homeAway = "home";
        }
        String kickoffKey = RONGHE_PD_FOOTBALL_KICK_OFF + deleteEventDto.getThirdMatchId() + ":" + homeAway;
        Object kickoffObj = redisService.get(kickoffKey);
        boolean deleteRedis = !ObjectUtils.isEmpty(kickoffObj) && ("goal".equals(oldEvent.getEventCode()) || "penalty_goal".equals(oldEvent.getEventCode()));
        if (deleteRedis) {
            redisService.del(kickoffKey);
        }
        matchScoresEventInfoMapper.updateByPrimaryKeySelective(oldEvent);
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createSimpleMatchEvent(data,"",startTimeSecond,startTimeSecond,
                System.currentTimeMillis(),oldEvent.getEventCode(),data.getMatchTimeInfo().getPeriod(),deleteEventDto.getLinkedId(),deleteEventDto.getOperatorName());
        matchEventInfoDTO.setAddition9("true");
        matchEventInfoDTO.setHomeAway(oldEvent.getHomeAway());
        matchEventInfoDTO.setCanceled(1);
        //2.下发比分
        matchEventInfoDTO.setExtrainfo(oldEvent.getThirdEventId());
        matchEventInfoDTO.setT1(commonItem.getHome());
        matchEventInfoDTO.setT2(commonItem.getAway());
        //3.下发新的比分事件
        eventProducer.sendPDEventInfo(matchEventInfoDTO);

        //4.推送比分变更到风控
        return Response.success();
    }

    @Override
    public Response editEvent(MatchScoreAndTimeVo data, EditEventDto editEventDto) {
        Long startTimeSecond =editEventDto.getTimeFromStartSecond();
        //1.删除事件计算比分得到新的比分
        MatchScoresEventInfo oldEvent =matchScoresEventInfoMapper.selectByPrimaryKey(editEventDto.getEditEventId());
        oldEvent.setAddition10("1");
        oldEvent.setModifyTime(System.currentTimeMillis());
        CommonItem commonItem= footBallScoreService.updateScoresByEditEvent(data,editEventDto,oldEvent);
        MatchEventInfoDTO matchEventInfoDTO =MatchEventUtils.createSimpleMatchEvent(data,"",startTimeSecond,startTimeSecond,
                System.currentTimeMillis(),oldEvent.getEventCode(),data.getMatchTimeInfo().getPeriod(),editEventDto.getLinkedId(),editEventDto.getOperatorName());
        matchEventInfoDTO.setAddition9("true");
        matchEventInfoDTO.setCanceled(1);
        matchEventInfoDTO.setHomeAway(oldEvent.getHomeAway());
        //2.下发比分
        matchEventInfoDTO.setT1(commonItem.getHome());
        matchEventInfoDTO.setT2(commonItem.getAway());
        matchEventInfoDTO.setExtrainfo(editEventDto.getEditEventId().toString());
        //3.下发新的比分事件
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
//        matchEventInfoDTO.setCanceled(0);
//        matchEventInfoDTO.setExtrainfo("");
//        matchEventInfoDTO.setCopyLinkId(editEventDto.getLinkedId()+"_PD");
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            
//        }
//        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        //WS推送
        //4.推送比分变更到风控
        return Response.success();
    }

    @Override
    public Response<List<PDFootBallEventDto>> eventList(MatchScoreAndTimeVo data, EventListDto eventListDto) {
        List<PDFootBallEventDto> eventDtoList = buildEventList( data.getThirdMatchInfo().getThirdMatchSourceId(), data.getThirdMatchInfo().getDataSourceCode(), eventListDto);
        return Response.success(eventDtoList);
    }

    @Override
    public Boolean matchIsDanger(Long thirdMatchId){
        String key =MATCH_ADVERTIS_EVENT_STATUS +thirdMatchId;
        Object cacheEventStatus=redisService.get(key);
        FootballMatchEventStatusVo footballMatchEventStatusVo= FootballMatchEventStatusVo.init();
        if(cacheEventStatus!=null){
            try{
                footballMatchEventStatusVo =JSONObject.toJavaObject(JSONObject.parseObject(cacheEventStatus.toString()) ,FootballMatchEventStatusVo.class);
            }catch (Exception e ){
                log.error("buildCacheMatchStatus error::",e);

                return false;
            }
        }
        if(footballMatchEventStatusVo.getIsDanger()!=null){
            return footballMatchEventStatusVo.getIsDanger();
        }
        return false;
    }

    @Override
    public Response editPenaltyScore(MatchScoreAndTimeVo data, PenaltyScoresEditDto penaltyScoresEditDto) {
        String extendScore = data.getMatchScoresInfo().getScoresJsonExtra();
        FootballPenaltyScores footballPenaltyScores = null;
        if( StringUtils.isEmpty(extendScore) ){
            footballPenaltyScores = new FootballPenaltyScores( penaltyScoresEditDto.getTargetRound(),penaltyScoresEditDto.getTargetRound());
            log.error("数据错误，不存在点球大战初始化比分，重新初始化赛事ID:{}", penaltyScoresEditDto.getThirdMatchId());
        } else {
            footballPenaltyScores = JSONObject.toJavaObject( (JSONObject.parseObject(extendScore)) , FootballPenaltyScores.class);
        }
        // 轮次校验 被编辑的轮次不能大于当前轮次
        if( penaltyScoresEditDto.getTargetRound() > footballPenaltyScores.getFirstNum() && penaltyScoresEditDto.getTargetRound() != 1 ){
            return Response.failed("当前编辑的轮次还未开打");
        }
        // 点球大战某轮比分编辑
        CommonItem commonItem = footballPenaltyScores.getRoundScores().get(penaltyScoresEditDto.getTargetRound().toString());
        if( null == commonItem ){
            return Response.failed("当前编辑的轮次还未开打");
        }
        commonItem.setHome(penaltyScoresEditDto.getHome());
        commonItem.setAway(penaltyScoresEditDto.getAway());

        // 根据每轮比分重新计算前5轮比分以及返回点球大战总比分
        CommonItem allRoundsScore = footballPenaltyScores.editRoundsScoreByRounds();
        log.info("计算点球射失：计算后比分：this.getPenaltyMiss={}", footballPenaltyScores.getPenaltyMiss());

        // 记录点球大战总比分
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores penaltyScore = allPeriodScores.get(50L);
        penaltyScore.setGoal( allRoundsScore);
        // 兼容1.0 当没有先罚时，取当前进球队更新先罚
        String shootFirst = footballPenaltyScores.getShootFirst();
        if ( ObjectUtils.isEmpty(shootFirst) ) {
            footballPenaltyScores.setShootFirst(penaltyScoresEditDto.getHomeAway());
        }
        // 更新入库
        data.getMatchScoresInfo().setScoresJsonExtra( JSONObject.toJSONString( footballPenaltyScores, SerializerFeature.WriteMapNullValue));
        data.getMatchScoresInfo().setScoresJson( JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime( System.currentTimeMillis());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo( data.getMatchScoresInfo(), null);
        // 发送实时服务
        eventProducer.sendPenaltyEvent(penaltyScoresEditDto,data);
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),penaltyScoresEditDto.getLinkedId());
        return Response.success();
    }

    @Override
    public Response addPenaltyRounds(MatchScoreAndTimeVo data, PenaltyAddRoundsDto penaltyAddRoundsDto) {
        String extryScore =data.getMatchScoresInfo().getScoresJsonExtra();
        FootballPenaltyScores footballPenaltyScores=null;
        if(StringUtils.isEmpty(extryScore)){
            footballPenaltyScores =new FootballPenaltyScores(true);
            log.error("数据错误，不存在点球大战初始化比分，重新初始化赛事ID:{}",penaltyAddRoundsDto.getThirdMatchId());
        }else {
            footballPenaltyScores= JSONObject.toJavaObject((JSONObject.parseObject(extryScore)) , FootballPenaltyScores.class);
        }
        if ( MapUtils.isNotEmpty(footballPenaltyScores.getRoundScores()) && footballPenaltyScores.getRoundScores().size() >= ConstantSystem.TWENTY_FOUR ) {
            log.error("三方赛事:{}最大不能超过24轮", penaltyAddRoundsDto.getThirdMatchId());
            return Response.failed();
        }
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        footballPenaltyScores.addPenaltyRounds();
        data.getMatchScoresInfo().setScoresJsonExtra(JSONObject.toJSONString(footballPenaltyScores, SerializerFeature.WriteMapNullValue));
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        return Response.success();
    }

    @Override
    public Response changePenaltyRounds(MatchScoreAndTimeVo data, PenaltyChangeRoundsDto penaltyChangeRoundsDto) {
        String extryScore =data.getMatchScoresInfo().getScoresJsonExtra();
        FootballPenaltyScores footballPenaltyScores=null;
        if(penaltyChangeRoundsDto.getTargetRound()==null||penaltyChangeRoundsDto.getTargetRound()<0){
            return Response.failed("数据错误");
        }
        if(StringUtils.isEmpty(extryScore)){
//            footballPenaltyScores =new FootballPenaltyScores(true);
            footballPenaltyScores =new FootballPenaltyScores(penaltyChangeRoundsDto.getTargetRound(),penaltyChangeRoundsDto.getTargetRound());
            log.error("数据错误，不存在点球大战初始化比分，重新初始化赛事ID:{}",penaltyChangeRoundsDto.getThirdMatchId());
        }else {
            footballPenaltyScores= JSONObject.toJavaObject((JSONObject.parseObject(extryScore)) , FootballPenaltyScores.class);
        }
        if(penaltyChangeRoundsDto.getTargetRound()!=footballPenaltyScores.getFirstNum()+1){
            return Response.failed("只能开打下一轮");
        }
        footballPenaltyScores.setFirstNum(penaltyChangeRoundsDto.getTargetRound());
        data.getMatchScoresInfo().setScoresJsonExtra(JSONObject.toJSONString(footballPenaltyScores, SerializerFeature.WriteMapNullValue));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        return Response.success();
    }

    @Override
    public Response changePenaltyFirst(MatchScoreAndTimeVo data, PenaltyFirstDto penaltyFirstDto) {
        String extryScore =data.getMatchScoresInfo().getScoresJsonExtra();
        FootballPenaltyScores footballPenaltyScores=null;
        if(StringUtils.isEmpty(extryScore)){
            footballPenaltyScores =new FootballPenaltyScores();
        }else {
            footballPenaltyScores= JSONObject.toJavaObject((JSONObject.parseObject(extryScore)) , FootballPenaltyScores.class);
        }
        footballPenaltyScores.setShootFirst(penaltyFirstDto.getHomeAway());
        data.getMatchScoresInfo().setScoresJsonExtra(JSONObject.toJSONString(footballPenaltyScores, SerializerFeature.WriteMapNullValue));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey( data.getMatchScoresInfo());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        return Response.success();
    }

    @Override
    public Response edit15MinGoal(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto) {
        //1.计算比分
        return footBallScoreService.edit15MinGoal(data,confirmEventDto);

    }

    @Override
    public Response edit15MinCorner(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto) {
        //1.计算比分
        return footBallScoreService.edit15MinCorner(data,confirmEventDto);

    }

    @Override
    public Response edit15MinYellowCard(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto) {
        return footBallScoreService.edit15MinYellowCard(data,confirmEventDto);
    }

    @Override
    public Response edit15MinRedCard(MatchScoreAndTimeVo data, Goal15MinDto confirmEventDto) {
        return footBallScoreService.edit15MinRedCard(data,confirmEventDto);
    }

    @Override
    public Response edit5MinGoal(MatchScoreAndTimeVo data, Goal5MinDto confirmEventDto) {
        //1.计算比分
        return footBallScoreService.edit5MinGoal(data,confirmEventDto);

    }

    @Override
    public void checkAndCreateMinutsScore(MatchScoreAndTimeVo data, Long timeFromStartSecond) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //5分钟阶段和 15分钟阶段 计算
        Long period5 =SportPeriodConstant.FootballPeriod.get5MinPeriod(data.getMatchScoresInfo().getPeriod(),timeFromStartSecond);
        Long period15 = SportPeriodConstant.FootballPeriod.get15MinPeriod(data.getMatchScoresInfo().getPeriod(),timeFromStartSecond);
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        //判断是否有这个5分钟阶段比分
        if(period5!=null){
            FootballScores periodScores5= allPeriodScores.get(period5);
            //新建该阶段值
            if(periodScores5==null) {
                periodScores5 = FootballScores.createMinFootballScores();
                allPeriodScores.put(period5, periodScores5);
                data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
                data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//                matchScoresInfoMapper.updateByPrimaryKey(data.getMatchScoresInfo());
//                pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
                pdMatchInfoRepository.onlyUpdateMatchScoresInfoRedis(data.getMatchScoresInfo());
            }
        }
        //没有则生成
        //判断是否有这个15分钟比分
        if(period15!=null){
            FootballScores periodScores15= allPeriodScores.get(period15);
            //新建该阶段值
            if(periodScores15==null) {
                periodScores15 = FootballScores.createMinFootballScores();
                allPeriodScores.put(period15, periodScores15);
                data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
//                matchScoresInfoMapper.updateByPrimaryKey(data.getMatchScoresInfo());
//                pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
                pdMatchInfoRepository.onlyUpdateMatchScoresInfoRedis(data.getMatchScoresInfo());
            }
        }
        stopWatch.stop();
        log.info("FootBallEventServiceImpl-checkAndCreateMinutsScore-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),data.getMatchTimeInfo().getThirdMatchId());
        //没有则生成
    }

    @Override
    public List<PDFootBallEventDto> buildEventList(String thirdMatchSourceId, String dataSourceCode, EventListDto eventListDto)
    {
        MatchScoresEventInfoExample example =new MatchScoresEventInfoExample();
        example.createCriteria()
                .andThirdMatchSourceIdEqualTo(thirdMatchSourceId)
                .andDataSourceCodeEqualTo(dataSourceCode)
                .andEventCodeIn(SCORE_EVENT_LIST);
        example.setOrderByClause("create_time desc");
        List<MatchScoresEventInfo> list = matchScoresEventInfoMapper.selectByExample(example);

        List<PDFootBallEventDto> eventDtoList = new ArrayList<>();
        for (MatchScoresEventInfo matchScoresEventInfo : list) {
            PDFootBallEventDto pdFootBallEventDto=new PDFootBallEventDto();
            BeanUtils.copyProperties( matchScoresEventInfo, pdFootBallEventDto);
            pdFootBallEventDto.setId(matchScoresEventInfo.getId().toString());
            pdFootBallEventDto.setDanger( matchScoresEventInfo.getAddition9() == null || matchScoresEventInfo.getAddition9().equals("false") ? false : true );
            pdFootBallEventDto.setExtraInfo(matchScoresEventInfo.getExtraInfo());
            String eventCode = matchScoresEventInfo.getEventCode();
            boolean varFlag = "possible_video_assistant_referee".equals(eventCode)
                    || "video_assistant_referee_over".equals(eventCode)
                    || "canceled_video_assistant_referee".equals(eventCode)
                    ||"var_reason".equals(eventCode);
            boolean flag = "match_status".equals(eventCode) || "water_break".equals(eventCode) || varFlag;
            if (flag) {
                pdFootBallEventDto.setHomeAway("all");
            }
            eventDtoList.add(pdFootBallEventDto);
        }

        eventDtoList.sort(new Comparator<PDFootBallEventDto>() {
            @Override
            public int compare(PDFootBallEventDto o1, PDFootBallEventDto o2) {
                return Long.parseLong(o1.getId())-Long.parseLong(o2.getId())>0? -1:0;
            }
        });

        // flag == 1 重要事件
        if (null != eventListDto && eventListDto.getFlag() != null && eventListDto.getFlag() == 1)
        {
            List<PDFootBallEventDto> importantEventList = new ArrayList<>();
            // 重要事件eventCode集合
            List<String> importantEventCode = Arrays.asList(
                    // 进球
                    "goal", "possible_goal", "canceled_goal",
                    // 角球
                    "corner", "possible_corner", "canceled_corner",
                    // VAR(video assistant referee)
                    "video_assistant_referee_over", "possible_video_assistant_referee", "canceled_video_assistant_referee",
                    // 黄牌
                    "yellow_card", "possible_yellow_card", "canceled_yellow_card",
                    // 红牌
                    "red_card", "possible_red_card", "canceled_red_card",
                    // 红黄牌
                    "yellow_red_card",
                    // 换人
                    "substitution",
                    // 点球
                    "possible_penalty", "penalty", "canceled_penalty", "penalty_missed");
            // 封装重要事件返回页面
            for (PDFootBallEventDto dto : eventDtoList) {
                if (importantEventCode.contains(dto.getEventCode())) {
                    importantEventList.add(dto);
                }
            }
            eventDtoList.sort((o1, o2) -> o1.getEventTime() - o2.getEventTime() > 0 ? -1 : 0);
            return importantEventList;
        }
        return eventDtoList;
    }

    @Override
    public Response retakePen( Response res, RetakePenDto retakePenDto, MatchScoresEventInfo matchScoresEventInfo) {
        String linkId = retakePenDto.getLinkedId();
        if ( StringUtils.isEmpty(linkId))  {
            linkId = UUID.randomUUID().toString();
        }
        MatchScoreAndTimeVo data = (MatchScoreAndTimeVo) res.getData();
        // 更新事件状态
        commonEventService.updateMatchEventStatus( retakePenDto.getThirdMatchId(), retakePenDto.getEventCode(), retakePenDto.getHomeAway(), null);

        // 1.删除事件计算比分得到新的比分
        MatchScoresEventInfo oldEvent = matchScoresEventInfoMapper.selectByPrimaryKey(retakePenDto.getDeleteEventId());
        String cacheKey = oldEvent.getHomeAway() + "possible_penalty" + retakePenDto.getThirdMatchId();
        redisService.set( cacheKey, "retake_pen");
        oldEvent.setModifyTime(System.currentTimeMillis());
        Object obj = redisService.get(MATCH_FOOTBALL_CONFIRM_POSSIBLE_TIME + retakePenDto.getDeleteEventId());
        DeleteEventDto deleteEventDto = new DeleteEventDto();
        BeanUtils.copyProperties( retakePenDto, deleteEventDto);
        if (obj != null) {
            Long possibleEventId = Long.valueOf(obj.toString());
            if (possibleEventId > 0L) {
                deleteEventDto.setPossibleEventId(possibleEventId);
            }
        }

        Long period = data.getMatchScoresInfo().getPeriod();
        Boolean setStatus = false;
        if ( 50L != period) {
            if ("penalty_goal".equals(oldEvent.getEventCode()) && !"1".equals(oldEvent.getAddition10())) {
                if (oldEvent.getT1() > 0 && "home".equals(retakePenDto.getHomeAway()) || oldEvent.getT2() > 0 && "away".equals(retakePenDto.getHomeAway())) {
                    CommonItem commonItem = footBallScoreService.updateScoresByDeleteEvent(data, deleteEventDto, oldEvent);
                    if (commonItem == null) {
                        return Response.failed("事件比分已经为0无法删除");
                    }
                }
            }
            // addition10设置为1，页面禁用删除入口
            if ("penalty_goal".equals(oldEvent.getEventCode())) {
                oldEvent.setAddition10("1");
                oldEvent.setAddition8("1");
                oldEvent.setAddition7("");
            }
            // addition8设置为1，页面禁用重踢入口
            if ("penalty_missed".equals(oldEvent.getEventCode())) {
                oldEvent.setAddition8("1");
            }
            setStatus = true;
        } else {
            Integer homeScore = commonScoreEventService.getCurrentPenaltyScore( linkId, data.getMatchScoresInfo(), TeamTypeEnum.HOME.code );
            Integer awayScore = commonScoreEventService.getCurrentPenaltyScore( linkId, data.getMatchScoresInfo(), TeamTypeEnum.AWAY.code );
            oldEvent.setAddition1( null == homeScore ? null : homeScore.toString());
            oldEvent.setAddition2(  null == awayScore ? null : awayScore.toString());
            // addition8设置为1，页面禁用重踢入口
            oldEvent.setAddition8("1");
            setStatus = true;
        }

        if ( setStatus ) {
            matchScoresEventInfoMapper.updateByPrimaryKeySelective(oldEvent);
        }

        Long startTimeSecond = retakePenDto.getTimeFromStartSecond();
        String homeAway = retakePenDto.getHomeAway();
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createSimpleMatchEvent( data, homeAway, startTimeSecond, startTimeSecond,
                System.currentTimeMillis(), oldEvent.getEventCode(), data.getMatchTimeInfo().getPeriod(), deleteEventDto.getLinkedId(), deleteEventDto.getOperatorName() );
        matchEventInfoDTO.setAddition9("true");
//
//        if (  50L == period && "penalty_missed".equals(retakePenDto.getEventCode()) ) {
//            matchEventInfoDTO.setAddition8("1");
//        }
        matchEventInfoDTO.setAddition7("1");
        // 重踢
        matchEventInfoDTO.setAddition6("retake_pen");
        matchEventInfoDTO.setEventCode(retakePenDto.getEventCode());
        matchEventInfoDTO.setHomeAway(oldEvent.getHomeAway());
        matchEventInfoDTO.setCanceled(1);
        matchEventInfoDTO.setAddition3(oldEvent.getAddition3());
        // 前端下发 99 区分重踢 和 点球确认
        matchEventInfoDTO.setAddition4(retakePenDto.getRetakeStatus());
        //2.下发比分
        matchEventInfoDTO.setExtrainfo(oldEvent.getThirdEventId());

        String scoresJson = data.getMatchScoresInfo().getScoresJson();
        Map<Long, FootballScores> scoresMap = JSON.parseObject( scoresJson, new TypeReference<Map<Long, FootballScores>>() {});
        FootballScores wholeScore = scoresMap.get(WHOLE_MATCH);
        CommonItem penaltyAwardedTotal = wholeScore.getPenaltyAwardedTotal();
        matchEventInfoDTO.setT1(penaltyAwardedTotal.getHome());
        matchEventInfoDTO.setT2(penaltyAwardedTotal.getAway());
        if ( "retake_pen".equals(matchScoresEventInfo.getAddition6()) || "no_retake_pen".equals(matchScoresEventInfo.getAddition6())) {
            matchEventInfoDTO.setAddition3(matchScoresEventInfo.getAddition3());
        } else {
            matchEventInfoDTO.setAddition3(String.valueOf(matchScoresEventInfo.getId()));
        }
        String possibleKey = homeAway + "possible_penalty" + retakePenDto.getThirdMatchId();
        Object possibleObj = redisService.get(possibleKey);
        if (ObjectUtils.isEmpty(possibleObj)) {
            redisService.set(possibleKey, possibleKey);
        }
        // 3.下发新的比分事件
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        return res;
    }

    @Override
    public Response noRetakePen(Response res, NoRetakePenDto noRetakePenDto, MatchScoresEventInfo originMatchInfo, MatchScoresEventInfo matchScoresEventInfo) {
        Long deleteEventId = noRetakePenDto.getDeleteEventId();
        MatchScoreAndTimeVo data = (MatchScoreAndTimeVo) res.getData();
        Long startTimeSecond = noRetakePenDto.getTimeFromStartSecond();
        String homeAway = noRetakePenDto.getHomeAway();
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createSimpleMatchEvent( data, homeAway, startTimeSecond, startTimeSecond,
                System.currentTimeMillis(), originMatchInfo.getEventCode(), data.getMatchTimeInfo().getPeriod(), noRetakePenDto.getLinkedId(), noRetakePenDto.getOperatorName());
        matchEventInfoDTO.setT1(originMatchInfo.getT1());
        matchEventInfoDTO.setT2(originMatchInfo.getT2());
        matchEventInfoDTO.setAddition3( matchScoresEventInfo.getAddition3());
        // 前端下发 99 区分重踢 和 点球确认
        matchEventInfoDTO.setAddition4( noRetakePenDto.getRetakeStatus());
        matchEventInfoDTO.setAddition5(originMatchInfo.getAddition5());
        matchEventInfoDTO.setAddition6("no_retake_pen");
        matchEventInfoDTO.setAddition9(originMatchInfo.getAddition9());
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        Integer originT1 = matchScoresInfo.getT1();
        Integer originT2 = matchScoresInfo.getT2();
        boolean confirmFlag = originMatchInfo.getT1() > originT1 && "home".equals(noRetakePenDto.getHomeAway())
                || originMatchInfo.getT2() > originT2 && "away".equals(noRetakePenDto.getHomeAway());
        if ( confirmFlag && !"50".equals(matchScoresInfo.getPeriod().toString()) ) {
            ConfirmEventDto confirmEventDto = new ConfirmEventDto();
            BeanUtils.copyProperties(noRetakePenDto, confirmEventDto);
            confirmEventDto.setConfirmEventCode(originMatchInfo.getEventCode());
            confirmEventDto.setPenaltyGoal("penaltyConfirm");
            noRetakePenDto.setEventCode("penalty_goal");
            matchEventInfoDTO.setEventCode(originMatchInfo.getEventCode());
            matchEventInfoDTO.setAddition2("no_retake_pen");
            // 计算比分
            footBallScoreService.changeScoresByEvent(data, confirmEventDto, matchEventInfoDTO);
            // 设置开球redis
            String redisHomeAway = noRetakePenDto.getHomeAway();
            if (!StringUtils.isAnyEmpty(confirmEventDto.getHomeAway())) {
                switch (confirmEventDto.getHomeAway()) {
                    case "home":
                        redisHomeAway = "away";
                        break;
                    case "away":
                        redisHomeAway = "home";
                        break;
                    default:
                        break;
                }
            }
            String key = RONGHE_PD_FOOTBALL_KICK_OFF + confirmEventDto.getThirdMatchId() + ":" + redisHomeAway;
            redisService.set(key, JSONObject.toJSON(matchEventInfoDTO).toString(), REDIS_WEEK_TIME);
            matchEventInfoDTO.setAddition7("1");
        }

        if ( null != deleteEventId && "50".equals(matchScoresInfo.getPeriod().toString()) ) {
            MatchScoresEventInfo dbMatchScoresEventInfo = matchScoresEventInfoMapper.selectByPrimaryKey(deleteEventId);
            if ( !Objects.isNull(dbMatchScoresEventInfo) ) {
                Integer homeScore = commonScoreEventService.getCurrentPenaltyScore( noRetakePenDto.getLinkedId(), matchScoresInfo, TeamTypeEnum.HOME.code );
                Integer awayScore = commonScoreEventService.getCurrentPenaltyScore( noRetakePenDto.getLinkedId(), matchScoresInfo, TeamTypeEnum.AWAY.code );
                dbMatchScoresEventInfo.setAddition1( null == homeScore ? null : homeScore.toString());
                dbMatchScoresEventInfo.setAddition2(  null == awayScore ? null : awayScore.toString());
                dbMatchScoresEventInfo.setAddition8("1");
                log.info("::{}::noRetakePen操作, eventId:{}", noRetakePenDto.getLinkedId(), dbMatchScoresEventInfo.getId());
                matchScoresEventInfoMapper.updateByPrimaryKey(dbMatchScoresEventInfo);
            }
        }

        boolean cancelFlag = originMatchInfo.getT1().equals(originT1) && "home".equals(noRetakePenDto.getHomeAway())
                || originMatchInfo.getT2().equals(originT2) && "away".equals(noRetakePenDto.getHomeAway());
        if (cancelFlag) {
            noRetakePenDto.setEventCode("penalty_missed");
            matchEventInfoDTO.setEventCode(originMatchInfo.getEventCode());
        }
        // 更新事件状态
        commonEventService.updateMatchEventStatus(noRetakePenDto.getThirdMatchId(), noRetakePenDto.getEventCode(), noRetakePenDto.getHomeAway(), null);
        String possibleKey = homeAway + "possible_penalty" + noRetakePenDto.getThirdMatchId();
        Object possibleObj = redisService.get(possibleKey);
        if (!ObjectUtils.isEmpty(possibleObj)) {
            redisService.del(possibleKey);
        }
        // 3.下发新的比分事件
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        return res;
    }

    @Override
    public Response executeTakePenalty(MatchScoreAndTimeVo data, TakePenaltyDTO takePenaltyDto) {
        String extraScore = data.getMatchScoresInfo().getScoresJsonExtra();
        FootballPenaltyScores footballPenaltyScores = null;
        if ( StringUtils.isEmpty(extraScore) ) {
            footballPenaltyScores = new FootballPenaltyScores();
        } else {
            footballPenaltyScores = JSONObject.toJavaObject( (JSONObject.parseObject(extraScore)), FootballPenaltyScores.class);
        }

        Boolean validStatus = false;
        Map<String, Object> map = Maps.newHashMap();
        map.put("homeAway", takePenaltyDto.getHomeAway() );
        map.put("penaltyRound", takePenaltyDto.getPenaltyRound());
        List<Map<String, Object>> roundPenaltyKick = Lists.newLinkedList();
        List<Map<String, Object>> dbPenaltyKickList = footballPenaltyScores.getRoundPenaltyKick();
        if ( CollectionUtils.isEmpty(dbPenaltyKickList) ) {
            validStatus = true;
        } else {
            roundPenaltyKick = dbPenaltyKickList;
            boolean exists = dbPenaltyKickList.stream()
                    .anyMatch(item ->
                            Objects.equals( item.get("homeAway").toString(), takePenaltyDto.getHomeAway() ) &&
                                    Objects.equals( Integer.parseInt(item.get("penaltyRound").toString()), takePenaltyDto.getPenaltyRound() )
                    );
            validStatus = !exists;

        }
        if (validStatus)  {
            roundPenaltyKick.add(map);
        }
        footballPenaltyScores.setRoundPenaltyKick(roundPenaltyKick);

        data.getMatchScoresInfo().setScoresJsonExtra(JSONObject.toJSONString(footballPenaltyScores, SerializerFeature.WriteMapNullValue));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        return Response.success();
    }
}
