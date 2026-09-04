package com.panda.merge.advertise.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.merge.advertise.common.Constant;
import com.panda.merge.advertise.dto.FootBallAdvertiseVo;
import com.panda.merge.advertise.dto.FootballMatchEventStatusVo;
import com.panda.merge.advertise.dto.FootballMatchStageVo;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.event.FootBallEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.service.CommonScoreEventService;
import com.panda.merge.advertise.service.FootBallAdvertiseService;
import com.panda.merge.advertise.service.FootBallScoreService;
import com.panda.merge.advertise.service.impl.FootBallScoreServiceImpl;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.api.IMatchFootballBallAdvertiseApi;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.common.enums.PublicEventEnum;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.*;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.CustomThreadPoolExecutor;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.panda.merge.advertise.common.Constant.*;
import static com.panda.merge.common.enums.Constant.MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE;
import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_PD_FOOTBALL_KICK_OFF;
import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_PD_FOOTBALL_PUBLIC_EVENT;
import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_PD_FOOTBALL_THIRDMATCHID_MAP;
import static com.panda.merge.config.RedisConfig.REDIS_FIVE_MINS_TIME;
import static com.panda.merge.config.RedisConfig.REDIS_WEEK_TIME;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;
import static com.panda.merge.constant.SportPeriodConstant.TIME_EVENT_PERIOD;

/** 足球抱球板1.0*/
@Service
@Slf4j
@DubboService
@EnableScheduling
public class MatchFootballBallAdvertiseApiImpl implements IMatchFootballBallAdvertiseApi {

    @Autowired
    FootBallAdvertiseService footBallAdvertiseService;
    @Autowired
    FootBallEventService footBallEventService;
    @Autowired
    RedisService redisService;
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    IScoresService scoresService;
    @Autowired
    CommonEventService commonEventService;
    @Autowired
    CommonAdvertiseService commonAdvertiseService;

    @Autowired
    private CommonScoreEventService commonScoreEventService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    IMatchScorePdLogService matchScorePdLogService;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    MessageBuilderUtils messageBuilderUtils;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    MatchTimeInfoRepository timeInfoRepository;
    @Autowired
    ScoresProducer scoresProducer;
//    @Autowired
//    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    FootBallScoreService footBallScoreService;
    @Autowired
    PdMatchInfoRepository pdMatchInfoRepository;

    /**
     * 赛事开始
     */
    @Override
    public Response matchStart(ChangeMatchStatusDto changeMatchStatus) {
        String key = "PA_createMatchAdvertise:" + changeMatchStatus.getThirdMatchId();
        try {
            if (redisUtils.checkRequestLinkId(changeMatchStatus.getLinkedId())) {
                return Response.failed("该linkId已被消费");
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchStatus.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            if (redisService.tryLock(key, key, 2, 3)) {
                //下发开赛
                Response r = footBallAdvertiseService.matchStart(response.getData(), changeMatchStatus.getLinkedId(),null,changeMatchStatus);
                //下发滚球状态
                if (r.isSuccess()) {
                    response = commonAdvertiseService.changeMatchStartStatus(response.getData().getThirdMatchInfo(),changeMatchStatus.getLinkedId());
                    redisUtils.pushFootBallScore(changeMatchStatus.getThirdMatchId());
                    redisUtils.cacheRequestLinkId(changeMatchStatus.getLinkedId());
                    return response;
                } else {
                    return r;
                }
            }else{
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {
            log.error("::matchStart异常", e);
        } finally {
            redisService.unLock(key, key);
        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response changeMatchPeriod(ChangeMatchPeriodDto changeMatchPeriodDto) {
        String key = "PA_createMatchAdvertise:" + changeMatchPeriodDto.getThirdMatchId();
        try {
            if (redisUtils.checkRequestLinkId(changeMatchPeriodDto.getLinkedId())) {
                log.info("::{}::changeMatchPeriod::该linkId已被消费",changeMatchPeriodDto.getLinkedId());
                return Response.failed("该linkId已被消费");
            }
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(changeMatchPeriodDto.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::changeMatchPeriod::三方赛事表里不存在，thirdMatchId:{}",changeMatchPeriodDto.getLinkedId(),changeMatchPeriodDto.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::changeMatchPeriodRedis,key:{},eventTime:{}",changeMatchPeriodDto.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchPeriodDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            // 当前阶段有可能事件时禁止切换阶段
            List<String> eventCodeList = Arrays.asList("possible_red_card", "possible_yellow_card", "possible_goal",
                    "possible_penalty", "possible_free_kick", "possible_corner", "possible_var_red_card", "possible_var_goal", "possible_var_penalty");
            Set<String> keys = new HashSet<>();
            Long thirdMatchId = changeMatchPeriodDto.getThirdMatchId();
            for (String item : eventCodeList) {
                keys.add("home" + item + thirdMatchId);
                keys.add("away" + item + thirdMatchId);
            }
            for (int i = 0; i < 3; i++) {
                keys.add(i + "possible_video_assistant_referee" + thirdMatchId);
            }
            List<Object> list = getAllRedisValue(new ArrayList<>(keys));
            if (!list.stream().allMatch(Objects::isNull)) {
                return Response.failed("zs".equals(changeMatchPeriodDto.getLanguage()) ? "当前阶段存在未确认或取消的可能事件" : "pls affirm or cancel the possible event current period");
            }
            if (redisService.tryLock(key, key, 2, 3)) {
                Long periodId = null;
                if (response.getData().getThirdMatchInfo().getDataSourceCode().equals("PD") || response.getData().getThirdMatchInfo().getDataSourceCode().equals("PD2")){
                    periodId = response.getData().getMatchTimeInfo().getPeriod();
                }else{
                    periodId = response.getData().getStandardMatchInfo().getMatchPeriodId();
                }
                Response<MatchScoreAndTimeVo> matchPeriodResponse = footBallAdvertiseService.changeMatchPeriod(response.getData(), changeMatchPeriodDto.getPeriodId(), changeMatchPeriodDto.getLinkedId(),changeMatchPeriodDto.getOperatorName());
                matchScorePdLogService.changeMatchPeriodLog(response.getData(), periodId, changeMatchPeriodDto);
                // ws推送前端30002，30003
                redisUtils.pushFootBallScore(changeMatchPeriodDto.getThirdMatchId());
                redisUtils.pushFootBallEventPABoard(changeMatchPeriodDto.getThirdMatchId(),changeMatchPeriodDto.getPeriodId());
                redisUtils.cacheRequestLinkId(changeMatchPeriodDto.getLinkedId());
                return matchPeriodResponse;
            }else{
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常:", e);
        } finally {
            redisService.unLock(key, key);
        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response setMatchEnd(ChangeMatchStatusDto changeMatchStatus) {
        String key = "PA_createMatchAdvertise:" + changeMatchStatus.getThirdMatchId();
        try {
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(changeMatchStatus.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::setMatchEnd::三方赛事表里不存在，thirdMatchId:{}",changeMatchStatus.getLinkedId(),changeMatchStatus.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::setMatchEnd,key:{},eventTime:{}",changeMatchStatus.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchStatus.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            Response<MatchScoreAndTimeVo> responseVo = footBallAdvertiseService.match999End(response.getData(), changeMatchStatus.getLinkedId(), changeMatchStatus);
            redisUtils.pushFootBallScore(changeMatchStatus.getThirdMatchId());
            //打印结束比赛日志X
            matchScorePdLogService.setMatchEndLog(response.getData(), changeMatchStatus);
            return responseVo;
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {

        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response getMatchAdvertiseInfo(MatchAdvertiseQueryDto matchAdvertiseQueryDto) {
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(matchAdvertiseQueryDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            if ("en".equals(matchAdvertiseQueryDto.getLanguage())) {
                Response<FootBallAdvertiseVo> data = footBallAdvertiseService.buildFootBallAdvertiseVo(response.getData());
                FootballMatchStageVo attack = data.getData().getFootballScoreboardVo().getAttack();
                attack.setTechName("attack");
                FootballMatchStageVo corner = data.getData().getFootballScoreboardVo().getCorner();
                corner.setTechName("corner");
                FootballMatchStageVo dangerousAttack = data.getData().getFootballScoreboardVo().getDangerousAttack();
                dangerousAttack.setTechName("dangerousAttack");
                FootballMatchStageVo freeKick = data.getData().getFootballScoreboardVo().getFreeKick();
                freeKick.setTechName("freeKick");
                FootballMatchStageVo goal = data.getData().getFootballScoreboardVo().getGoal();
                goal.setTechName("goal");
                FootballMatchStageVo goalKick = data.getData().getFootballScoreboardVo().getGoalKick();
                goalKick.setTechName("goalKick");
                FootballMatchStageVo offside = data.getData().getFootballScoreboardVo().getOffside();
                offside.setTechName("offside");
                FootballMatchStageVo redCard = data.getData().getFootballScoreboardVo().getRedCard();
                redCard.setTechName("redCard");
                FootballMatchStageVo shotOffTarget = data.getData().getFootballScoreboardVo().getShotOffTarget();
                shotOffTarget.setTechName("shotOffTarget");
                FootballMatchStageVo shotOnTarget = data.getData().getFootballScoreboardVo().getShotOnTarget();
                shotOnTarget.setTechName("shotOnTarget");
                FootballMatchStageVo throwIn = data.getData().getFootballScoreboardVo().getThrowIn();
                throwIn.setTechName("throwIn");
                FootballMatchStageVo yellowCard = data.getData().getFootballScoreboardVo().getYellowCard();
                yellowCard.setTechName("yellowCard");
                FootballMatchStageVo possessionCount = data.getData().getFootballScoreboardVo().getPossessionCount();
                possessionCount.setTechName("possCount");
                FootballMatchStageVo ballPossessionPercentage = data.getData().getFootballScoreboardVo().getBallPossessionPercentage();
                ballPossessionPercentage.setTechName("ballPossPct");
                data.getData().setLiveEventSource(response.getData().getStandardMatchInfo().getLiveEventSource());
                return data;
            } else {
                return footBallAdvertiseService.buildFootBallAdvertiseVo(response.getData());
            }
        } catch (Exception e) {
            log.error("getMatchAdvertiseInfo-error:", e);
        }
        return Response.failed("PA查询异常");
    }

    @Override
    public Response possibleEvent(PossibleEventDto possibleEventDto) {
        log.info("linkId=::{}::,requestId=::{}::足球报球板可能事件，thirdMatchId={},timeFromStartSecond={},eventCode={},homeAway={}",
                possibleEventDto.getLinkedId(), possibleEventDto.getRequestId(), possibleEventDto.getThirdMatchId(),
                possibleEventDto.getTimeFromStartSecond(), possibleEventDto.getPossibleEventCode(), possibleEventDto.getHomeAway());
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(possibleEventDto.getThirdMatchId(), null);
        if (Objects.isNull(thirdMatchInfo)) {
            log.error("linkId=::{}::possibleEvent::三方赛事表里不存在，thirdMatchId:{}",possibleEventDto.getLinkedId(),possibleEventDto.getThirdMatchId());
            return Response.failed("三方赛事表里不存在");
        }
        // 存放事件时间，用于报球板事件监控
        long eventTime=System.currentTimeMillis();
        String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
        if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
            log.info("::{}::possibleEventRedis,key:{},eventTime:{}",possibleEventDto.getLinkedId(),actionMonitorKey,eventTime);
            redisService.set(actionMonitorKey,eventTime);
        }
        if (!PDEventCodeEnum.containDisablePossibleEvent(possibleEventDto.getPossibleEventCode())) {
            Response kickOffEvent = kickOffEventCheck(possibleEventDto.getThirdMatchId(), possibleEventDto.getLanguage());
            if (kickOffEvent != null) {
                return kickOffEvent;
            }
        }
        String key = "PA_createMatchAdvertise:" + possibleEventDto.getThirdMatchId();
        String homeAway = possibleEventDto.getHomeAway();
        String eventCode = possibleEventDto.getPossibleEventCode();
        Long thirdMatchId = possibleEventDto.getThirdMatchId();
        String cacheKey = homeAway + eventCode + thirdMatchId;
        Object obj = redisService.get(cacheKey);
        boolean flag = "possible_red_card".equals(eventCode) || "possible_yellow_card".equals(eventCode)
                || "possible_goal".equals(eventCode) || "possible_free_kick".equals(eventCode)
                || "possible_corner".equals(eventCode) || "possible_penalty".equals(eventCode) ||
                PDEventCodeEnum.containVAREvent(eventCode, CommonConstant.PD_EVENT_TYPE_POSSIBLE);
        if (obj != null) {
            return Response.failed("zs".equals(possibleEventDto.getLanguage()) ? "请刷新或点击确认或取消按钮" : "please refresh or click affirm or cancel button");
        }

        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(possibleEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            if (!SportPeriodConstant.FootballPeriod.contans(response.getData().getMatchTimeInfo().getPeriod())) {
                return Response.failed("赛事不在开打阶段");
            }
            // 点球大战禁止操作事件--开始
            List<String> eventCodeList = Arrays.asList("possible_red_card", "possible_yellow_card", "possible_goal", "possible_penalty", "possible_free_kick", "possible_corner", "possible_penalty", "possible_var_red_card", "possible_var_goal", "possible_var_penalty");
            Long periodScore = response.getData().getMatchScoresInfo().getPeriod();
            Long periodId = response.getData().getMatchTimeInfo().getPeriod();
            if ((50 == periodId || 50 == periodScore) && eventCodeList.contains(eventCode)) {
                return Response.failed("zs".equals(possibleEventDto.getLanguage()) ? "当前阶段禁止该操作，请刷新或切换赛事" : "Disallow the action at this stage,pls refresh or change match");
            }
            // 点球大战禁止操作事件--结束
            if (flag) {
                redisService.set(cacheKey, cacheKey);
            }
            //事件触发15分钟 和 5分钟比分生成
            Long matchTime = getMatchTime(response.getData());
            possibleEventDto.setTimeFromStartSecond(matchTime);
            // 公共事件时间统计
            PublicEvent publicEvent = updateEventTime(response.getData(), periodId, eventCode, homeAway);
            // 更新事件时间
            updateEventTimeByJsonScore(response, periodId, publicEvent);
            MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
            response.getData().setMatchScoresInfo(matchScoresInfo);
            footBallEventService.checkAndCreateMinutsScore(response.getData(),matchTime);
            matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
            pdMatchInfoRepository.onlyUpdateMatchScoresInfoDB(matchScoresInfo);
            response.getData().setMatchScoresInfo(matchScoresInfo);
            response = footBallEventService.possibleEvent(response.getData(), possibleEventDto);

            redisUtils.pushFootBallScore(possibleEventDto.getThirdMatchId());
            redisUtils.pushFootBallEvent(possibleEventDto.getThirdMatchId());
            matchScorePdLogService.possibleEventLog(possibleEventDto);
            return response;
        } catch (Exception e) {
            log.error(":处理数据发生异常:", e);
            return Response.failed(e.getMessage());
        }
    }


    /**
     * 更新事件时间
     *
     * @param response    查询数据
     * @param periodId    阶段ID
     * @param publicEvent 事件时间对象
     */
    public void updateEventTimeByJsonScore(Response<MatchScoreAndTimeVo> response, Long periodId, PublicEvent publicEvent) {
        if (!TIME_EVENT_PERIOD.contains(periodId) || ObjectUtils.isEmpty(publicEvent)) {
            return;
        }
        MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfo(publicEvent.getThirdMatchId(), SourceTypeEnum.LIVE_DATA.getCode(), null);
        Map<Long, FootballScores> scoresMap = JSON.parseObject(matchScoresInfo.getScoresJson(), new TypeReference<Map<Long, FootballScores>>() {
        });
        FootballScores wholeScores = scoresMap.get(WHOLE_MATCH);
        if(wholeScores==null){
            wholeScores = new FootballScores(WHOLE_MATCH);
        }
        FootballScores periodScores = scoresMap.get(periodId);
        if(periodScores==null){
            periodScores = new FootballScores(periodId);
        }
        long homeEvent = publicEvent.getHomePossessionTime();
        long awayEvent = publicEvent.getAwayPossessionTime();
        CommonItem possessionTime = new CommonItem(Math.toIntExact(homeEvent), Math.toIntExact(awayEvent), true);
        if (ObjectUtils.isEmpty(wholeScores.getPossessionTime())) {
            wholeScores.setPossessionTime(new CommonItem());
        }
        if (ObjectUtils.isEmpty(periodScores.getPossessionTime())) {
            periodScores.setPossessionTime(new CommonItem());
        }
        wholeScores.setPossessionTime(possessionTime);
        periodScores.setPossessionTime(possessionTime);
        long sum = Math.addExact(homeEvent, awayEvent);
        long homePercent = Math.round(homeEvent / (double) sum * 100);
        long awayPercent = Math.round(awayEvent / (double) sum * 100);
        CommonItem commonItem = new CommonItem();
        commonItem.setHome(Math.toIntExact(homePercent));
        commonItem.setAway(Math.toIntExact(awayPercent));
        wholeScores.setBallPossessionPercentage(commonItem);
        if (ObjectUtils.isEmpty(wholeScores.getPublicEvent())) {
            wholeScores.setPublicEvent(new CommonItem());
        }
        if (ObjectUtils.isEmpty(periodScores.getPublicEvent())) {
            periodScores.setPublicEvent(new CommonItem());
        }
        if (!ObjectUtils.isEmpty(publicEvent.getPublicEventOne())) {
            wholeScores.getPublicEvent().setHome(Math.toIntExact(publicEvent.getPublicEventOne()));
        }
        if (!ObjectUtils.isEmpty(publicEvent.getPublicEventTwo())) {
            wholeScores.getPublicEvent().setAway(Math.toIntExact(publicEvent.getPublicEventTwo()));
        }
        periodScores.setBallPossessionPercentage(commonItem);
        if (!ObjectUtils.isEmpty(publicEvent.getPublicEventOne())) {
            periodScores.getPublicEvent().setHome(Math.toIntExact(publicEvent.getPublicEventOne()));
        }
        if (!ObjectUtils.isEmpty(publicEvent.getPublicEventTwo())) {
            periodScores.getPublicEvent().setAway(Math.toIntExact(publicEvent.getPublicEventTwo()));
        }
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(scoresMap, SerializerFeature.DisableCircularReferenceDetect));
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
        pdMatchInfoRepository.onlyUpdateMatchScoresInfoRedis(matchScoresInfo);
    }

    /**
     * 时间事件信息
     *
     * @param data      赛事信息
     * @param periodId  阶段Id
     * @param eventCode 事件编码
     * @param homeAway  主客队
     * @return 统计信息
     */
    public PublicEvent updateEventTime(MatchScoreAndTimeVo data, Long periodId, String eventCode, String homeAway) {
        if (!TIME_EVENT_PERIOD.contains(periodId)) {
            return null;
        }
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        String scoresJson = matchScoresInfo.getScoresJson();
        Map<Long, FootballScores> scoresMap = JSONObject.parseObject(scoresJson, new TypeReference<Map<Long, FootballScores>>() {
        });
        CommonItem possessionTimeDb = scoresMap.get(WHOLE_MATCH).getPossessionTime();
        if (ObjectUtils.isEmpty(possessionTimeDb)) {
            possessionTimeDb = new CommonItem();
        }
        CommonItem publicEventDb = scoresMap.get(WHOLE_MATCH).getPublicEvent();
        if (ObjectUtils.isEmpty(publicEventDb)) {
            publicEventDb = new CommonItem();
        }
        Long homePossession = Long.valueOf(possessionTimeDb.getHome());
        Long awayPossession = Long.valueOf(possessionTimeDb.getAway());
        Long publicEventOne = Long.valueOf(publicEventDb.getHome());
        Long publicEventTwo = Long.valueOf(publicEventDb.getAway());

        Long thirdMatchId = matchScoresInfo.getThirdMatchId();
        // 报球板足球时间事件key
        String publicEventKey = RONGHE_PD_FOOTBALL_PUBLIC_EVENT + thirdMatchId;
        Object publicObj = redisService.get(publicEventKey);
        PublicEvent publicEvent;
        if (ObjectUtils.isEmpty(publicObj)) {
            Long eventTime = matchScoresInfo.getEventTime();
            String previousEvent = "";
            if (homePossession > awayPossession) {
                previousEvent = "home";
            } else {
                previousEvent = "away";
            }
            publicEvent = new PublicEvent(thirdMatchId,homePossession, awayPossession, publicEventOne, publicEventTwo, eventTime, previousEvent,0);
            redisService.set(publicEventKey, JSONObject.toJSON(publicEvent).toString(), REDIS_WEEK_TIME);
        } else {
            publicEvent = JSON.parseObject(publicObj.toString(), new TypeReference<PublicEvent>() {
            });
            publicEvent.setHomePossessionTime(homePossession);
            publicEvent.setAwayPossessionTime(awayPossession);
            publicEvent.setPublicEventOne(publicEventOne);
            publicEvent.setPublicEventTwo(publicEventTwo);
            if (ObjectUtils.isEmpty(publicEvent.getEventStatus())) {
                publicEvent.setEventStatus(0);
            }
            if (ObjectUtils.isEmpty(publicEvent.getThirdMatchId())) {
                publicEvent.setThirdMatchId(thirdMatchId);
            }
        }

        long currentTime = System.currentTimeMillis();
        if (PDEventCodeEnum.containHomeAwayEvent(eventCode)) {
            if (TeamTypeEnum.HOME.code.equals(homeAway)) {
                switch (publicEvent.getPreviousEvent()) {
                    case "kickOff":
                        publicEvent.setHomePossessionTime(0L);
                        publicEvent.setPreviousEvent(PublicEventEnum.HOME_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    case "home":
                        publicEvent.setHomePossessionTime(publicEvent.getHomePossessionTime() + (currentTime - publicEvent.getCurrentTime()));
                        publicEvent.setPreviousEvent(PublicEventEnum.HOME_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    case "away":
                        publicEvent.setAwayPossessionTime(publicEvent.getAwayPossessionTime() + (currentTime - publicEvent.getCurrentTime()));
                        publicEvent.setPreviousEvent(PublicEventEnum.HOME_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    case "publicEventOne":
                        publicEvent.setPublicEventOne(publicEvent.getPublicEventOne() + (currentTime - publicEvent.getCurrentTime()));
                        publicEvent.setPreviousEvent(PublicEventEnum.HOME_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    case "publicEventTwo":
                        if (PublicEventEnum.PUBLIC_EVENT_TWO.getCode().equals(publicEvent.getPreviousEvent()) && 1 == publicEvent.getEventStatus()) {
                            publicEvent.setEventStatus(0);
                        }
                        publicEvent.setPublicEventTwo(publicEvent.getPublicEventTwo() + (currentTime - publicEvent.getCurrentTime()));
                        publicEvent.setPreviousEvent(PublicEventEnum.HOME_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    default:
                }
            }
            if (TeamTypeEnum.AWAY.code.equals(homeAway)) {
                switch (publicEvent.getPreviousEvent()) {
                    case "kickOff":
                        publicEvent.setAwayPossessionTime(0L);
                        publicEvent.setPreviousEvent(PublicEventEnum.AWAY_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    case "home":
                        publicEvent.setHomePossessionTime(publicEvent.getHomePossessionTime() + (currentTime - publicEvent.getCurrentTime()));
                        publicEvent.setPreviousEvent(PublicEventEnum.AWAY_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    case "away":
                        publicEvent.setAwayPossessionTime(publicEvent.getAwayPossessionTime() + (currentTime - publicEvent.getCurrentTime()));
                        publicEvent.setPreviousEvent(PublicEventEnum.AWAY_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    case "publicEventOne":
                        publicEvent.setPublicEventOne(publicEvent.getPublicEventOne() + (currentTime - publicEvent.getCurrentTime()));
                        publicEvent.setPreviousEvent(PublicEventEnum.AWAY_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    case "publicEventTwo":
                        if (PublicEventEnum.PUBLIC_EVENT_TWO.getCode().equals(publicEvent.getPreviousEvent()) && 1 == publicEvent.getEventStatus()) {
                            publicEvent.setEventStatus(0);
                        }
                        publicEvent.setPublicEventTwo(publicEvent.getPublicEventTwo() + (currentTime - publicEvent.getCurrentTime()));
                        publicEvent.setPreviousEvent(PublicEventEnum.AWAY_EVENT.getCode());
                        publicEvent.setCurrentTime(currentTime);
                        break;
                    default:
                }
            }
        }
        if (PDEventCodeEnum.containPublicEventOne(eventCode)) {
            switch (publicEvent.getPreviousEvent()) {
                case "kickOff":
                    publicEvent.setPublicEventOne(0L);
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_ONE.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                case "home":
                    publicEvent.setHomePossessionTime(publicEvent.getHomePossessionTime() + (currentTime - publicEvent.getCurrentTime()));
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_ONE.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                case "away":
                    publicEvent.setAwayPossessionTime(publicEvent.getAwayPossessionTime() + (currentTime - publicEvent.getCurrentTime()));
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_ONE.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                case "publicEventOne":
                    publicEvent.setPublicEventOne(publicEvent.getPublicEventOne() + (currentTime - publicEvent.getCurrentTime()));
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_ONE.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                case "publicEventTwo":
                    if (PublicEventEnum.PUBLIC_EVENT_TWO.getCode().equals(publicEvent.getPreviousEvent()) && 1 == publicEvent.getEventStatus()) {
                        publicEvent.setEventStatus(0);
                    }
                    publicEvent.setPublicEventTwo(publicEvent.getPublicEventTwo() + (currentTime - publicEvent.getCurrentTime()));
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_ONE.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                default:
            }
        }
        if (PDEventCodeEnum.containPublicEventTwo(eventCode)) {
            switch (publicEvent.getPreviousEvent()) {
                case "kickOff":
                    publicEvent.setPublicEventTwo(0L);
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_TWO.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                case "home":
                    publicEvent.setHomePossessionTime(publicEvent.getHomePossessionTime() + (currentTime - publicEvent.getCurrentTime()));
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_TWO.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                case "away":
                    publicEvent.setAwayPossessionTime(publicEvent.getAwayPossessionTime() + (currentTime - publicEvent.getCurrentTime()));
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_TWO.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                case "publicEventOne":
                    publicEvent.setPublicEventOne(publicEvent.getPublicEventOne() + (currentTime - publicEvent.getCurrentTime()));
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_TWO.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                case "publicEventTwo":
                    publicEvent.setPublicEventTwo(publicEvent.getPublicEventTwo() + (currentTime - publicEvent.getCurrentTime()));
                    publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_TWO.getCode());
                    publicEvent.setCurrentTime(currentTime);
                    break;
                default:
            }
        }
        redisService.set(publicEventKey, JSONObject.toJSON(publicEvent).toString(), REDIS_WEEK_TIME);
        // 缓存已操作的三方赛事信息，用于定时任务changeEventToPublicTwo()更改事件类型
        Object thirdMatchIdMapObj = redisService.get(RONGHE_PD_FOOTBALL_THIRDMATCHID_MAP);
        if (ObjectUtils.isEmpty(thirdMatchIdMapObj)) {
            Map<Long, Long> map = new HashMap<>(16);
            map.put(thirdMatchId, currentTime);
            redisService.set(RONGHE_PD_FOOTBALL_THIRDMATCHID_MAP, JSONObject.toJSON(map).toString(), REDIS_WEEK_TIME);
        } else {
            Map<Long, Long> map = JSON.parseObject(thirdMatchIdMapObj.toString(), new TypeReference<Map<Long, Long>>() {
            });
            map.put(thirdMatchId, currentTime);
            redisService.set(RONGHE_PD_FOOTBALL_THIRDMATCHID_MAP, JSONObject.toJSON(map).toString(), REDIS_WEEK_TIME);
        }
        return publicEvent;
    }

    /**
     * 数据有缺漏、断连时的值，超出5分钟未有事件下发，将以公共时间2计算，不计为主/客队数据
     */
    @Scheduled(cron = "* * * * * ?")
    public void changeEventToPublicTwo() {
        try {
            long currentTime = System.currentTimeMillis();
            // 获取缓存已操作的三方赛事信息
            Object thirdMatchIdMapObj = redisService.get(RONGHE_PD_FOOTBALL_THIRDMATCHID_MAP);
            if (!ObjectUtils.isEmpty(thirdMatchIdMapObj)) {
                Map<Long, Long> oldMap = JSON.parseObject(thirdMatchIdMapObj.toString(), new TypeReference<Map<Long, Long>>() {
                });
                Map<Long, Long> map = new HashMap<>(oldMap);
                // 超过一周未操作的三方赛事，移除redis
                map.values().removeIf(value -> currentTime - value > REDIS_WEEK_TIME * 1000);
                if (!map.isEmpty()) {
                    List<String> thirdMatchIdKeys = new ArrayList<>();
                    map.forEach((thirdMatchId, eventTime) -> thirdMatchIdKeys.add(RONGHE_PD_FOOTBALL_PUBLIC_EVENT + thirdMatchId));
                    List<Object> thirdMatchIdObjs = redisService.mGet(thirdMatchIdKeys);
                    Map<String, Object> publicEventMultiUpdate = new HashMap<>(16);
                    if (!CollectionUtils.isEmpty(thirdMatchIdObjs)) {
                        thirdMatchIdObjs.forEach(publicObj -> {
                            if (ObjectUtils.isEmpty(publicObj)) {
                                return;
                            }
                            PublicEvent publicEvent = JSON.parseObject(publicObj.toString(), new TypeReference<PublicEvent>() {
                            });
                            if (ObjectUtils.isEmpty(publicEvent)) {
                                return;
                            }
                            if (ObjectUtils.isEmpty(publicEvent.getEventStatus())) {
                                publicEvent.setEventStatus(0);
                            }
                            boolean flag = (0 == publicEvent.getEventStatus()) && currentTime - publicEvent.getCurrentTime() > REDIS_FIVE_MINS_TIME * 1000
                                    && (TeamTypeEnum.AWAY.code.equals(publicEvent.getPreviousEvent()) || TeamTypeEnum.HOME.code.equals(publicEvent.getPreviousEvent()));
                            // 当主客队事件状态5分钟未变化，更新原主客队事件为公共事件2
                            if (flag) {
                                publicEvent.setPreviousEvent(PublicEventEnum.PUBLIC_EVENT_TWO.getCode());
                                publicEvent.setEventStatus(1);
                                publicEventMultiUpdate.put(RONGHE_PD_FOOTBALL_PUBLIC_EVENT + publicEvent.getThirdMatchId(), JSONObject.toJSON(publicEvent).toString());
                            }
                        });
                    }
                    if (!publicEventMultiUpdate.isEmpty()) {
                        redisService.mSetExpire(publicEventMultiUpdate, REDIS_WEEK_TIME);
                    }
                }
                if (map.size() < oldMap.size()) {
                    redisService.set(RONGHE_PD_FOOTBALL_THIRDMATCHID_MAP, JSONObject.toJSON(map).toString(), REDIS_WEEK_TIME);
                }
            }
        } catch (Exception e) {
            log.error("【" + "footballEvent" + " 足球报球板超出5分钟未有事件下发，将以公共时间2计算，不计为主/客队数据】 异常,Exception:", e);
        }
    }

    @Override
    public Response confirmEvent(ConfirmEventDto confirmEventDto) {
        log.info("linkId=::{}::,requestId=::{}::足球报球板确认事件，thirdMatchId={},timeFromStartSecond={},eventCode={},homeAway={}",
                confirmEventDto.getLinkedId(), confirmEventDto.getRequestId(), confirmEventDto.getThirdMatchId(),
                confirmEventDto.getTimeFromStartSecond(), confirmEventDto.getConfirmEventCode(), confirmEventDto.getHomeAway());
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(confirmEventDto.getThirdMatchId(), null);
        if (Objects.isNull(thirdMatchInfo)) {
            log.error("linkId=::{}::confirmEvent::三方赛事表里不存在，thirdMatchId:{}",confirmEventDto.getLinkedId(),confirmEventDto.getThirdMatchId());
            return Response.failed("三方赛事表里不存在");
        }
        // 存放事件时间，用于报球板事件监控
        long eventTime=System.currentTimeMillis();
        String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
        if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
            log.info("::{}::confirmEventRedis,key:{},eventTime:{}",confirmEventDto.getLinkedId(),actionMonitorKey,eventTime);
            redisService.set(actionMonitorKey,eventTime);
        }
        if (PDEventCodeEnum.containDisableConfirmEvent(confirmEventDto.getConfirmEventCode())) {
            Response kickOffEvent = kickOffEventCheck(confirmEventDto.getThirdMatchId(), confirmEventDto.getLanguage());
            if (kickOffEvent != null) {
                log.info("linkId=::{}::当前报球板已有进球事件，不处理",confirmEventDto.getLinkedId());
                return kickOffEvent;
            }
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String key = "PA_createMatchAdvertise:" + confirmEventDto.getThirdMatchId();
        String homeAway = confirmEventDto.getHomeAway();
        String eventCode = confirmEventDto.getConfirmEventCode();
        Long thirdMatchId = confirmEventDto.getThirdMatchId();
        eventCode = FootBallScoreServiceImpl.confirmEventViaPossible(eventCode);
        String cacheKey = homeAway + eventCode + thirdMatchId;
        log.info("当前redis中的值为:{}",cacheKey);
        Object obj = redisService.get(cacheKey);
        try {
            if (redisUtils.checkRequestLinkId(confirmEventDto.getLinkedId())) {
                return Response.failed("该linkId已被消费");
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(confirmEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            if (!SportPeriodConstant.FootballPeriod.contans(response.getData().getMatchTimeInfo().getPeriod())) {
                return Response.failed("赛事不在开打阶段");
            }
            // 点球大战禁止操作事件--开始 剔除:
            List<String> eventCodeList = Arrays.asList("red_card", "yellow_card", "goal", "penalty", "free_kick", "corner", "possession",
                    "shot_on_target","shot_off_target","offside","goal_kick","attack","dangerous_attack","throw_in","var_red_card",
                    "var_goal", "penalty", "var_penalty", "var_yellow_card", "penalty_goal");
            Long periodScore = response.getData().getMatchScoresInfo().getPeriod();
            Long periodId = response.getData().getMatchTimeInfo().getPeriod();
            if ( ( 50 == periodId || 50 == periodScore ) && eventCodeList.contains(confirmEventDto.getConfirmEventCode()) ) {
                return Response.failed("zs".equals(confirmEventDto.getLanguage()) ? "当前阶段禁止该操作，请刷新或切换赛事" :
                        "Disallow the action at this stage,pls refresh or change match");
            }
            // 点球大战禁止操作事件--结束
            //查询link 是否已经被消费 消费的return
            //新增 redis 锁，防止并发 key = key
            //事件触发15分钟 和 5分钟比分生成
            if (redisService.tryLock(key, key, 2, 3)) {
                List<String> list = Arrays.asList("possible_red_card,possible_yellow_card,possible_goal,possible_free_kick,possible_corner,possible_penalty,possible_var_red_card,possible_var_goal,possible_var_penalty".split(","));
                boolean eventCodeFlag = false;
                for (String codeElement : list) {
                    Object o = redisService.get(homeAway + codeElement + thirdMatchId);
                    if (o != null) {
                        eventCodeFlag = true;
                        break;
                    }
                }
                // 确定事件编码
                List<String> confirmList = Arrays.asList("shot_on_target", "shot_off_target", "offside", "goal_kick", "attack", "dangerous_attack", "throw_in", "possession");
                boolean confirmFlag = confirmList.contains(confirmEventDto.getConfirmEventCode());
                boolean possibleFlag = eventCodeFlag && !list.contains(eventCode);

                log.info("当前事件为:{}",confirmEventDto.getConfirmEventCode());

                log.info("possibleFlag:{},confirmFlag:{}",possibleFlag,confirmFlag);

                // 可能事件且非确定事件时，需点击确认或取消。只点击确定事件不走该逻辑
                if (possibleFlag && !confirmFlag) {
                    return Response.failed("zs".equals(confirmEventDto.getLanguage()) ? "请刷新或点击确认或取消按钮" : "please refresh or chick affirm or cancel button");
                }
                if (list.contains(eventCode) && obj == null) {
                    return Response.failed("zs".equals(confirmEventDto.getLanguage()) ? "请稍等或点击事件" : "please wait or click event");
                }
                if (list.contains(eventCode) && obj != null && !PDEventCodeEnum.PENALTY.getEventCode().equals(confirmEventDto.getConfirmEventCode())) {
                    redisService.del(cacheKey);
                }
                Long matchTime = getMatchTime(response.getData());
                confirmEventDto.setTimeFromStartSecond(matchTime);
                // 公共事件时间统计
                PublicEvent publicEvent = updateEventTime(response.getData(), periodId, confirmEventDto.getConfirmEventCode(), homeAway);
                footBallEventService.checkAndCreateMinutsScore(response.getData(), matchTime);
                MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
                response.getData().setMatchScoresInfo(matchScoresInfo);
                response = footBallEventService.confirmEvent(response.getData(), confirmEventDto);
                // 更新事件时间
                updateEventTimeByJsonScore(response, periodId, publicEvent);
                redisUtils.pushFootBallScore(confirmEventDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(confirmEventDto.getThirdMatchId());
                matchScorePdLogService.confirmEventLog(confirmEventDto);
                redisUtils.cacheRequestLinkId(confirmEventDto.getLinkedId());
                //将处理完毕的link 保存redis 保存 360 s
                return response;
            }else {
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常:", e);
            return Response.failed(e.getMessage());
        } finally {
            stopWatch.stop();
            log.info("MatchFootballBallAdvertiseApiImpl-confirmEvent-耗时={}, 链路ID={}, eventCode={}, thirdMatchId={}",
                    stopWatch.getTotalTimeMillis(), confirmEventDto.getRequestId(), confirmEventDto.getConfirmEventCode(), confirmEventDto.getThirdMatchId());
            redisService.unLock(key, key);
        }
    }

    /**
     * 进球或点球进球后，只有点开球才能继续操作
     */
    public Response kickOffEventCheck(Long thirdMatchId, String language) {
        String kickOffKeyAway = RONGHE_PD_FOOTBALL_KICK_OFF + thirdMatchId + ":" + "away";
        String kickOffKeyHome = RONGHE_PD_FOOTBALL_KICK_OFF + thirdMatchId + ":" + "home";
        Object objAway = redisService.get(kickOffKeyAway);
        Object objHome = redisService.get(kickOffKeyHome);
        if (!ObjectUtils.isEmpty(objAway) || !ObjectUtils.isEmpty(objHome)) {
            if ("zs".equals(language)) {
                return Response.failed("请选择开球方");
            } else {
                return Response.failed("Please choose the starting team");
            }
        }
        return null;
    }


    @Override
    public Response confirmPenaltyEvent( ConfirmPenaltyEventDTO confirmPenaltyEventDTO) {
        String linkId = confirmPenaltyEventDTO.getLinkedId();
        if ( StringUtils.isEmpty(linkId) ) {
            linkId = UUID.randomUUID().toString();
            confirmPenaltyEventDTO.setLinkedId(linkId);
        }
        log.info("::{}::confirmPenaltyEvent入参:{}", linkId, JSON.toJSONString(confirmPenaltyEventDTO));

        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(confirmPenaltyEventDTO.getThirdMatchId(), null);
        if (Objects.isNull(thirdMatchInfo)) {
            log.error("::{}::confirmPenaltyEvent三方赛事表里不存在，thirdMatchId:{}", linkId, confirmPenaltyEventDTO.getThirdMatchId());
            return Response.failed("三方赛事表里不存在");
        }
        // 防止重复提交
        String preventDuplicationKey = String.format(PREVENT_DUPLICATION_KEY, confirmPenaltyEventDTO.getThirdMatchId());
        redisService.expire( preventDuplicationKey, 3);
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate( confirmPenaltyEventDTO.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }

            Response confirmPenaltyResponse = footBallEventService.confirmPenaltyEvent( response.getData(), confirmPenaltyEventDTO);
            if ( !confirmPenaltyResponse.isSuccess() ) {
                return confirmPenaltyResponse;
            }
            MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey( response.getData().getMatchScoresInfo().getId(), null);
            response.getData().setMatchScoresInfo(matchScoresInfo);

            Long periodId = response.getData().getMatchScoresInfo().getPeriod();
            String homeAway = confirmPenaltyEventDTO.getHomeAway();
            Long matchTime = getMatchTime(response.getData());
            confirmPenaltyEventDTO.setTimeFromStartSecond(matchTime);
            PublicEvent publicEvent = updateEventTime( response.getData(), periodId, confirmPenaltyEventDTO.getConfirmEventCode(), homeAway);
            updateEventTimeByJsonScore( response, periodId, publicEvent);

            footBallEventService.checkAndCreateMinutsScore( response.getData(), matchTime);
            matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey( response.getData().getMatchScoresInfo().getId(), null);
            response.getData().setMatchScoresInfo(matchScoresInfo);

            // 取消点球
            if ( PDEventCodeEnum.PENALTY_MISSED.getEventCode().equals( confirmPenaltyEventDTO.getConfirmEventCode()) ) {
                CancelEventDto cancelEventDto = new CancelEventDto();
                BeanUtils.copyProperties( confirmPenaltyEventDTO, cancelEventDto);
                // 具体的操作eventCode
                cancelEventDto.setCancelEventCode( confirmPenaltyEventDTO.getConfirmEventCode());
                // 具体的操作eventId
                cancelEventDto.setDeleteEventId( confirmPenaltyEventDTO.getConfirmEventId());
                Response cancelEventResponse = footBallEventService.cancelEvent( response.getData(), cancelEventDto);
                if ( !cancelEventResponse.isSuccess() ) {
                    log.error("::{}::confirmPenaltyEvent-cancelEvent异常", linkId);
                    return cancelEventResponse;
                }
                // 点球大战实时比分的兼容
                MatchScoresEventInfo dbMatchScoresEventInfo = matchScoresEventInfoMapper.selectByPrimaryKey(confirmPenaltyEventDTO.getConfirmEventId());
                if ( !Objects.isNull(dbMatchScoresEventInfo) ) {
                    Integer homeScore = commonScoreEventService.getCurrentPenaltyScore( linkId, matchScoresInfo, TeamTypeEnum.HOME.code );
                    Integer awayScore = commonScoreEventService.getCurrentPenaltyScore( linkId, matchScoresInfo, TeamTypeEnum.AWAY.code );
                    dbMatchScoresEventInfo.setAddition1( null == homeScore ? null : homeScore.toString());
                    dbMatchScoresEventInfo.setAddition2(  null == awayScore ? null : awayScore.toString());
                    dbMatchScoresEventInfo.setAddition8("1");
                    matchScoresEventInfoMapper.updateByPrimaryKey(dbMatchScoresEventInfo);
                }
            } else if ( PDEventCodeEnum.PENALTY_GOAL.getEventCode().equals( confirmPenaltyEventDTO.getConfirmEventCode()) ) {
                ConfirmEventDto confirmEventDto = new ConfirmEventDto();
                BeanUtils.copyProperties( confirmPenaltyEventDTO, confirmEventDto);
                // 具体的操作eventCode
                confirmEventDto.setConfirmEventCode( confirmPenaltyEventDTO.getConfirmEventCode());
                // 具体的操作eventId
                confirmEventDto.setDeleteEventId( confirmPenaltyEventDTO.getConfirmEventId());
//                Response confirmEventResponse = footBallEventService.confirmEvent( response.getData(), confirmEventDto);
//                if ( !confirmEventResponse.isSuccess() ) {
//                    log.error("::{}::confirmPenaltyEvent-confirmEvent异常", linkId);
//                    return confirmEventResponse;
//                }
            }
            redisUtils.pushFootBallScore( confirmPenaltyEventDTO.getThirdMatchId());
            redisUtils.pushFootBallEvent( confirmPenaltyEventDTO.getThirdMatchId());
            redisUtils.cacheRequestLinkId( linkId);
            // 操作记录日志
            if ( PDEventCodeEnum.PENALTY_GOAL.getEventCode().equals( confirmPenaltyEventDTO.getConfirmEventCode()) ){
                confirmPenaltyEventDTO.setConfirmEventCode("penalty");
            }

            // 终止点击
            MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
            example.createCriteria().andDataSourceCodeEqualTo(DataSourceCodeEnum.PD.getCode()).andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId());
            example.setOrderByClause("create_time desc limit 1");
            List<MatchScoresEventInfo> matchScoresEventInfos = matchScoresEventInfoMapper.selectByExample(example);
            if ( !CollectionUtils.isEmpty(matchScoresEventInfos) ) {
                List<Long> scoresEventIds = matchScoresEventInfos.stream().map(MatchScoresEventInfo::getId).collect(Collectors.toList());
                log.info("::{}::preventDuplication-confirmPenaltyEvent, scoresEventIds:{}", linkId, JSON.toJSONString(scoresEventIds));
                for ( MatchScoresEventInfo info : matchScoresEventInfos ) {
                    info.setAddition8("1");
                    matchScoresEventInfoMapper.updateByPrimaryKey(info);
                }
            }

            // 日志异步执行
            CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                    matchScorePdLogService.confirmPenaltyEventLog( response.getData(), confirmPenaltyEventDTO)
            ));

            return response;
        } catch (Exception e) {
            log.error("::{}::处理数据发生异常:", linkId, e);
            return Response.failed(e.getMessage());
        } finally {
            if ( redisService.hasKey(preventDuplicationKey) ) {
                redisService.del(preventDuplicationKey);
            }
        }
    }

    @Override
    public Response cancelEvent(CancelEventDto cancelEventDto) {
        log.info("linkId=::{}::,requestId=::{}::足球报球板取消事件，thirdMatchId={},timeFromStartSecond={},eventCode={},homeAway={}",
                cancelEventDto.getLinkedId(), cancelEventDto.getRequestId(), cancelEventDto.getThirdMatchId(),
                cancelEventDto.getTimeFromStartSecond(), cancelEventDto.getCancelEventCode(), cancelEventDto.getHomeAway());
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(cancelEventDto.getThirdMatchId(), null);
        if (Objects.isNull(thirdMatchInfo)) {
            log.error("linkId=::{}::cancelEvent::三方赛事表里不存在，thirdMatchId:{}",cancelEventDto.getLinkedId(),cancelEventDto.getThirdMatchId());
            return Response.failed("三方赛事表里不存在");
        }
        // 存放事件时间，用于报球板事件监控
        long eventTime=System.currentTimeMillis();
        String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
        if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
            log.info("::{}::cancelEventRedis,key:{},eventTime:{}",cancelEventDto.getLinkedId(),actionMonitorKey,eventTime);
            redisService.set(actionMonitorKey,eventTime);
        }
        String key = "PA_createMatchAdvertise:" + cancelEventDto.getThirdMatchId();
        String homeAway = cancelEventDto.getHomeAway();
        String eventCode = cancelEventDto.getCancelEventCode();
        Long thirdMatchId = cancelEventDto.getThirdMatchId();
        log.info("{}，cancelEvent取消事件删除缓存：{}",cancelEventDto.getThirdMatchId(),eventCode);
        eventCode = FootBallScoreServiceImpl.cancelEventViaPossible(eventCode);
        log.info("{}，cancelEvent取消事件删除缓存：{}",cancelEventDto.getThirdMatchId(),eventCode);
        String cacheKey = homeAway + eventCode + thirdMatchId;
        log.info("{},cancelEvent取消事件删除缓存：{},key：{}",cancelEventDto.getThirdMatchId(),eventCode,cacheKey);
        Object obj = redisService.get(cacheKey);
        try {
            if (redisUtils.checkRequestLinkId(cancelEventDto.getLinkedId())) {
                return Response.failed("该linkId已被消费");
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(cancelEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            if (!SportPeriodConstant.FootballPeriod.contans(response.getData().getMatchTimeInfo().getPeriod())) {
                return Response.failed("赛事不在开打阶段");
            }
            // 点球大战禁止操作事件--开始
            List<String> eventCodeList = Arrays.asList("canceled_red_card", "canceled_yellow_card", "canceled_goal", "penalty_missed", "canceled_penalty",
                    "canceled_free_kick", "canceled_corner", "canceled_var_red_card", "canceled_var_goal", "canceled_var_penalty", "penalty_canceled");
            Long periodScore = response.getData().getMatchScoresInfo().getPeriod();
            Long periodId = response.getData().getMatchTimeInfo().getPeriod();
            if ((50 == periodId || 50 == periodScore) && eventCodeList.contains(cancelEventDto.getCancelEventCode())) {
                return Response.failed("zs".equals(cancelEventDto.getLanguage()) ? "当前阶段禁止该操作，请刷新或切换赛事" : "Disallow the action at this stage,pls refresh or change match");
            }
            // 点球大战禁止操作事件--结束
            if (redisService.tryLock(key, key, 2, 3)) {
                List<String> list = Arrays.asList(("possible_red_card,possible_yellow_card,possible_goal," +
                        "possible_free_kick,possible_corner,possible_penalty," +
                        "possible_var_red_card,possible_var_goal," +
                        "possible_var_penalty").split(","));
                if (list.contains(eventCode) && obj == null) {
                    return Response.failed("zs".equals(cancelEventDto.getLanguage()) ? "请刷新，稍等或点击事件" : "please refresh, wait or click event");
                }
                if (list.contains(eventCode) && obj != null) {
                    log.info("{}，cancelEvent取消事件删除缓存：{},删除key：{}",cancelEventDto.getThirdMatchId(),eventCode,cacheKey);
                    redisService.del(cacheKey);
                }
                //事件触发15分钟 和 5分钟比分生成
                Long matchTime = getMatchTime(response.getData());
                cancelEventDto.setTimeFromStartSecond(matchTime);
                // 公共事件时间统计
                PublicEvent publicEvent = updateEventTime(response.getData(), periodId, cancelEventDto.getCancelEventCode(), homeAway);
                // 更新事件时间
                updateEventTimeByJsonScore(response, periodId, publicEvent);
                MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
                response.getData().setMatchScoresInfo(matchScoresInfo);
                footBallEventService.checkAndCreateMinutsScore(response.getData(),matchTime);
                matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
                response.getData().setMatchScoresInfo(matchScoresInfo);
                response = footBallEventService.cancelEvent(response.getData(), cancelEventDto);

                redisUtils.pushFootBallScore(cancelEventDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(cancelEventDto.getThirdMatchId());
                matchScorePdLogService.cancelEventLog(cancelEventDto);
                redisUtils.cacheRequestLinkId(cancelEventDto.getLinkedId());
                return response;
            }else {
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常:", e);
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, key);
        }
    }

    private Long getMatchTime(MatchScoreAndTimeVo data) {
        MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
        Long matchTime;
        if (SportPeriodConstant.FootballPeriod.contans(matchTimeInfo.getPeriod())) {
            matchTime = matchTimeInfo.getSecondFromStart() + (System.currentTimeMillis() - matchTimeInfo.getEventTime()) / 1000;
        } else {
            matchTime = matchTimeInfo.getSecondFromStart();
        }
        return matchTime;
    }

    /**
     * 赛事 危险安全设置
     */
    @Override
    public Response isDanger(IsDangerDto isDangerDto) {
        log.info("足球报球板thirdMatchId={}，安全入口参数={}",isDangerDto.getThirdMatchId(),isDangerDto.getTimeFromStartSecond());
        String key = "PA_createMatchAdvertise:" + isDangerDto.getThirdMatchId();
        try {
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(isDangerDto.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::isDanger::三方赛事表里不存在，thirdMatchId:{}",isDangerDto.getLinkedId(),isDangerDto.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::isDanger,key:{},eventTime:{}",isDangerDto.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(isDangerDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            if (!SportPeriodConstant.FootballPeriod.contans(response.getData().getMatchTimeInfo().getPeriod())) {
                return Response.failed("赛事不在开打阶段");
            }

            Long matchTime = getMatchTime(response.getData());
            isDangerDto.setTimeFromStartSecond(matchTime);
            //判断是否安全
            String eventCode = "ball_safe";
            if (isDangerDto.getIsDanger() == 1) {
                eventCode = "dangerous_attack";
            }
            if ("ball_safe".equals(eventCode)) {
                Long period = response.getData().getMatchScoresInfo().getPeriod();
                // 公共事件时间统计
                PublicEvent publicEvent = updateEventTime(response.getData(), period, eventCode, null);
                // 更新事件时间
                updateEventTimeByJsonScore(response, period, publicEvent);
                MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
                response.getData().setMatchScoresInfo(matchScoresInfo);
            }
            Response<MatchScoreAndTimeVo> isDangerResponse = footBallEventService.isDanger(response.getData(), isDangerDto);
            //事件触发15分钟 和 5分钟比分生成
            footBallEventService.checkAndCreateMinutsScore(response.getData(),isDangerDto.getTimeFromStartSecond());
            MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
            pdMatchInfoRepository.onlyUpdateMatchScoresInfoDB(matchScoresInfo);
            response.getData().setMatchScoresInfo(matchScoresInfo);
            redisUtils.pushFootBallScore(isDangerDto.getThirdMatchId());
            redisUtils.pushFootBallEvent(isDangerDto.getThirdMatchId());
            matchScorePdLogService.isDangerLog(isDangerDto, response.getData());
            return isDangerResponse;
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {

        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response kickOffAfterGoal(KickOffDto kickOff) {
        try {
            if (redisUtils.checkRequestLinkId(kickOff.getLinkedId())) {
                log.info("::{}::kickOffAfterGoal::该linkId已被消费",kickOff.getLinkedId());
                return Response.failed("该linkId已被消费");
            }
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(kickOff.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::kickOffAfterGoal::三方赛事表里不存在，thirdMatchId:{}",kickOff.getLinkedId(),kickOff.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::kickOffAfterGoal,key:{},eventTime:{}",kickOff.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(kickOff.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            String kickOffKey = RONGHE_PD_FOOTBALL_KICK_OFF + kickOff.getThirdMatchId() + ":" + kickOff.getWhoKickOff();
            Object obj = redisService.get(kickOffKey);
            if (obj != null) {
                MatchEventInfoDTO matchEventInfoDTO = JSON.parseObject(obj.toString(), new TypeReference<MatchEventInfoDTO>() {
                });
                redisService.del(kickOffKey);

                // 点球进球确认后 取消重踢入口
                if (PDEventCodeEnum.PENALTY_GOAL.getEventCode().equals(matchEventInfoDTO.getEventCode())) {
                    MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
                    example.createCriteria().andThirdEventIdEqualTo(matchEventInfoDTO.getThirdEventId()).andEventCodeEqualTo(PDEventCodeEnum.PENALTY_GOAL.getEventCode());
                    example.setOrderByClause("id desc limit 1");
                    List<MatchScoresEventInfo> matchScoresEventInfos = matchScoresEventInfoMapper.selectByExample(example);
                    if (!CollectionUtils.isEmpty(matchScoresEventInfos)) {
                        MatchScoresEventInfo matchScoresEventInfo = matchScoresEventInfos.get(0);
                        // addition8=1页面禁用重踢入口
                        matchScoresEventInfo.setAddition8("1");
                        matchScoresEventInfoMapper.updateByPrimaryKey(matchScoresEventInfo);
                    }
                }

                matchEventInfoDTO.setHomeAway(kickOff.getWhoKickOff());
                matchEventInfoDTO.setEventCode("kick_off");
                Long matchTime = getMatchTime(response.getData());
                matchEventInfoDTO.setSecondsFromStart(matchTime);
                matchEventInfoDTO.setPeriodRemainingSeconds(matchTime);
                matchEventInfoDTO.setEventTime(System.currentTimeMillis());
                Long period = response.getData().getMatchTimeInfo().getPeriod();
                // 公共事件时间统计
                PublicEvent publicEvent = updateEventTime(response.getData(), period, matchEventInfoDTO.getEventCode(), kickOff.getWhoKickOff());
                // 更新事件时间
                updateEventTimeByJsonScore(response, period, publicEvent);
                MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
                response.getData().setMatchScoresInfo(matchScoresInfo);
                // 更新缓存当前eventCode
                String key = MATCH_ADVERTIS_EVENT_STATUS + kickOff.getThirdMatchId();
                Object cacheEventStatus = redisService.get(key);
                if (cacheEventStatus != null) {
                    try {
                        FootballMatchEventStatusVo footballMatchEventStatusVo = JSONObject.toJavaObject(JSONObject.parseObject(cacheEventStatus.toString()), FootballMatchEventStatusVo.class);
                        footballMatchEventStatusVo.setCurrentEventCode("kick_off");
                        redisService.set(MATCH_ADVERTIS_EVENT_STATUS + kickOff.getThirdMatchId(), JSON.toJSONString(footballMatchEventStatusVo));
                    } catch (Exception e) {
                        log.error("buildCacheMatchStatus error::", e);
                    }
                }
                footBallEventService.sendKickOffEventAfterGoal(matchEventInfoDTO);
            }
            redisUtils.pushFootBallScore(kickOff.getThirdMatchId());
            redisUtils.pushFootBallEvent(kickOff.getThirdMatchId());
            matchScorePdLogService.kickOffAfterGoalLog(kickOff, response.getData());
            return Response.success();
        } catch (Exception e) {
            log.error("::进球后开球处理数据发生异常:", e);
        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response retakePen(RetakePenDto retakePenDto) {
        try {
            if (redisUtils.checkRequestLinkId(retakePenDto.getLinkedId())) {
                log.info("::{}::retakePen::该linkId已被消费",retakePenDto.getLinkedId());
                return Response.failed("该linkId已被消费");
            }
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(retakePenDto.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::retakePen::三方赛事表里不存在，thirdMatchId:{}",retakePenDto.getLinkedId(),retakePenDto.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime = System.currentTimeMillis();
            String actionMonitorKey = String.format( ACTION_MONITER_KEY, thirdMatchInfo.getThirdMatchSourceId());
            if ( thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) == 0 &&
                    ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode()) ) {
                log.info("::{}::retakePen,key:{},eventTime:{}",retakePenDto.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }

            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(retakePenDto.getThirdMatchId());
            if ( !response.isSuccess() ) {
                return response;
            }
            MatchScoresEventInfo matchScoresEventInfo = matchScoresEventInfoMapper.selectByPrimaryKey(retakePenDto.getDeleteEventId());
            // 重踢时，如果开球redis非空，则删除开球redis
            String homeAway = "";
            if ("home".equals(retakePenDto.getHomeAway())) {
                homeAway = "away";
            }
            if ("away".equals(retakePenDto.getHomeAway())) {
                homeAway = "home";
            }

            String kickoffKey = RONGHE_PD_FOOTBALL_KICK_OFF + retakePenDto.getThirdMatchId() + ":" + homeAway;
            Object kickoffObj = redisService.get(kickoffKey);
            if ( !ObjectUtils.isEmpty(kickoffObj) ) {
                redisService.del(kickoffKey);
            }

            Long matchTime = getMatchTime(response.getData());
            retakePenDto.setTimeFromStartSecond(matchTime);
            retakePenDto.setEventCode("penalty");
            Long period = response.getData().getMatchTimeInfo().getPeriod();
            // 公共事件时间统计
            PublicEvent publicEvent = updateEventTime(response.getData(), period, retakePenDto.getEventCode(), retakePenDto.getHomeAway());
            // 更新事件时间
            updateEventTimeByJsonScore(response, period, publicEvent);
            MatchScoresInfo matchScoresInfo = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(response.getData().getMatchScoresInfo().getId(), null);
            response.getData().setMatchScoresInfo(matchScoresInfo);
            Response<MatchScoreAndTimeVo> res = footBallEventService.retakePen(response, retakePenDto, matchScoresEventInfo);
            if ( !res.isSuccess() ) {
                return res;
            }
            redisUtils.pushFootBallScore( retakePenDto.getThirdMatchId());
            redisUtils.pushFootBallEvent( retakePenDto.getThirdMatchId());
            redisUtils.cacheRequestLinkId( retakePenDto.getLinkedId());
            matchScorePdLogService.retakePenLog( res.getData(), matchScoresEventInfo, retakePenDto);
            return res;
        } catch (Exception e) {
            log.error("::点球重踢处理数据发生异常:", e);
        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response noRetakePen(NoRetakePenDto noRetakePen) {
        String linkId = noRetakePen.getLinkedId();
        if ( StringUtils.isEmpty(linkId) ) {
            linkId = UUID.randomUUID().toString().replace("-","");
            noRetakePen.setLinkedId(linkId);
        }
        try {
            if (redisUtils.checkRequestLinkId( linkId)) {
                log.info("::{}::noRetakePen::该linkId已被消费", linkId);
                return Response.failed("该linkId已被消费");
            }
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(noRetakePen.getThirdMatchId(), null);
            if ( Objects.isNull(thirdMatchInfo) ) {
                log.error("::{}::noRetakePen::三方赛事表里不存在，thirdMatchId:{}", linkId, noRetakePen.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::noRetakePen,key:{},eventTime:{}", linkId, actionMonitorKey, eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(noRetakePen.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            // 在赛事列表操作时：根据没有重踢事件，获取原点球确认事件消息
            MatchScoresEventInfo matchScoresEventInfo = null;
            MatchScoresEventInfo originMatchInfo = null;
            if (!ObjectUtils.isEmpty(noRetakePen.getDeleteEventId())) {
                matchScoresEventInfo = matchScoresEventInfoMapper.selectByPrimaryKey(noRetakePen.getDeleteEventId());
                originMatchInfo = matchScoresEventInfoMapper.selectByPrimaryKey(Long.valueOf(matchScoresEventInfo.getAddition3()));
            }
            // 不在赛事列表操作时：根据没有重踢事件，获取原点球确认事件消息
            if (ObjectUtils.isEmpty(noRetakePen.getDeleteEventId()) && "99".equals(noRetakePen.getRetakeStatus())) {
                String thirdMatchSourceId = response.getData().getThirdMatchInfo().getThirdMatchSourceId();
                MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
                example.createCriteria()
                        .andThirdMatchSourceIdEqualTo( thirdMatchSourceId)
                        .andHomeAwayEqualTo( noRetakePen.getHomeAway())
                        .andEventCodeEqualTo( "penalty")
                        .andAddition6EqualTo( "retake_pen");
                example.setOrderByClause("id desc limit 1");
                List<MatchScoresEventInfo> matchScoresEventInfos = matchScoresEventInfoMapper.selectByExample(example);
                if (!CollectionUtils.isEmpty(matchScoresEventInfos)) {
                    matchScoresEventInfo = matchScoresEventInfos.get(0);
                    originMatchInfo = matchScoresEventInfoMapper.selectByPrimaryKey(Long.valueOf(matchScoresEventInfo.getAddition3()));
                }
            }
            Long matchTime = getMatchTime(response.getData());
            noRetakePen.setTimeFromStartSecond(matchTime);
            Response<MatchScoreAndTimeVo> res = footBallEventService.noRetakePen( response, noRetakePen, originMatchInfo, matchScoresEventInfo);
            if (!res.isSuccess()) {
                return res;
            }
            redisUtils.pushFootBallScore(noRetakePen.getThirdMatchId());
            redisUtils.pushFootBallEvent(noRetakePen.getThirdMatchId());
            redisUtils.cacheRequestLinkId(noRetakePen.getLinkedId());
            matchScorePdLogService.noRetakePenLog(res.getData(), matchScoresEventInfo, noRetakePen);
            return res;
        } catch (Exception e) {
            log.error("::没有重踢处理数据发生异常:", e);
        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response kickOff(KickOffDto kickOff) {
        log.info("::{}::PA_createMatchAdvertise kickOff::kickOff:{}",kickOff.getLinkedId(),kickOff);
        String key = "PA_createMatchAdvertise:" + kickOff.getThirdMatchId();
        try {
            if (redisUtils.checkRequestLinkId(kickOff.getLinkedId())) {
                log.info("::{}::kickOff::该linkId已被消费",kickOff.getLinkedId());
                return Response.failed("该linkId已被消费");
            }
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(kickOff.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::kickOff::三方赛事表里不存在，thirdMatchId:{}",kickOff.getLinkedId(),kickOff.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::kickOff,key:{},eventTime:{}",kickOff.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(kickOff.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            // 比赛开始，初始化公共事件存入redis，缓存一周
            String publicEventKey = RONGHE_PD_FOOTBALL_PUBLIC_EVENT + kickOff.getThirdMatchId();
            long l = System.currentTimeMillis();
            PublicEvent publicEvent;
            if (ObjectUtils.isEmpty(kickOff.getWhoKickOff())) {
                publicEvent = new PublicEvent(kickOff.getThirdMatchId(), 0L, 0L, 0L, 0L, l, PublicEventEnum.KICK_OFF.getCode(), 0);
            } else {
                publicEvent = new PublicEvent(kickOff.getThirdMatchId(), 0L, 0L, 0L, 0L, l, TeamTypeEnum.HOME.getCode().equals(kickOff.getWhoKickOff()) ? PublicEventEnum.HOME_EVENT.getCode() : PublicEventEnum.AWAY_EVENT.getCode(), 0);
            }
            redisService.set(publicEventKey, JSONObject.toJSON(publicEvent).toString(), REDIS_WEEK_TIME);
            log.info("::{}::PA_createMatchAdvertise matchStart::kickOff:{}",kickOff.getLinkedId(),kickOff);
            //下发开赛
            Response r = footBallAdvertiseService.matchStart(response.getData(), kickOff.getLinkedId() + "_PD", kickOff,null);
            //下发滚球状态
            commonAdvertiseService.changeFootballMatchStartStatus(response.getData().getThirdMatchInfo(),kickOff);
            //开始开球事件
            if (!SportPeriodConstant.FootballPeriod.contans(response.getData().getMatchTimeInfo().getPeriod())) {
                return Response.failed("赛事不在开打阶段");
            }

            MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
            if ( null == matchTimeInfo.getTimeGo() || ConstantSystem.ZERO.equals(matchTimeInfo.getTimeGo())) {
                matchTimeInfo.setTimeGo(ConstantSystem.ONE);
                timeInfoRepository.updateByPrimaryKey(matchTimeInfo);
                response.getData().setMatchTimeInfo(matchTimeInfo);
            }
            Response<MatchScoreAndTimeVo> responseKickOff = footBallEventService.kickOff(response.getData(), kickOff);
            commonAdvertiseService.matchPeriodValid(  kickOff.getLinkedId(), thirdMatchInfo, response.getData().getMatchScoresInfo(), response.getData().getMatchTimeInfo());

            redisUtils.pushFootBallScore(kickOff.getThirdMatchId());
            redisUtils.pushFootBallEvent(kickOff.getThirdMatchId());
            matchScorePdLogService.kickOffLog(kickOff, response.getData());
            return responseKickOff;
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {

        }
        return Response.failed("服务器错误");

    }

    /**
     * 赛事 设置补时
     */
    @Override
    public Response overTimeEvent(OverTimeEventDto overTimeEventDto) {
        String key = "PA_createMatchAdvertise:" + overTimeEventDto.getThirdMatchId();
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(overTimeEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            if (!SportPeriodConstant.FootballPeriod.contans(response.getData().getMatchTimeInfo().getPeriod())) {
                return Response.failed("赛事不在开打阶段");
            }

            response = footBallEventService.overTimeEvent(response.getData(), overTimeEventDto);
            redisUtils.pushFootBallScore(overTimeEventDto.getThirdMatchId());
            return response;
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {

        }
        return Response.failed("服务器错误");
    }

    /**
     * 赛事 删除事件
     */
    @Override
    public Response deleteEvent(DeleteEventDto deleteEventDto) {
        String key = "PA_createMatchAdvertise:" + deleteEventDto.getThirdMatchId();
        try {
            if (redisUtils.checkRequestLinkId(deleteEventDto.getLinkedId())) {
                log.info("::{}::deleteEvent::该linkId已被消费",deleteEventDto.getLinkedId());
                return Response.failed("该linkId已被消费");
            }
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(deleteEventDto.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::deleteEvent::三方赛事表里不存在，thirdMatchId:{}",deleteEventDto.getLinkedId(),deleteEventDto.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::deleteEvent,key:{},eventTime:{}",deleteEventDto.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            if (redisService.tryLock(key, deleteEventDto.getLinkedId(), 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(deleteEventDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                Response<MatchScoreAndTimeVo> res = footBallEventService.deleteEvent(response.getData(), deleteEventDto);
                if (!res.isSuccess()) {
                    return res;
                }
                redisUtils.pushFootBallScore(deleteEventDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(deleteEventDto.getThirdMatchId());
                redisUtils.cacheRequestLinkId(deleteEventDto.getLinkedId());
                return res;
            } else {
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常:", e);
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, deleteEventDto.getLinkedId());
        }
    }

    /**
     * 赛事 修正接口
     */
    @Override
    public Response editEvent(EditEventDto editEventDto) {
        String key = "PA_createMatchAdvertise:" + editEventDto.getThirdMatchId();
        try {
            if (redisUtils.checkRequestLinkId(editEventDto.getLinkedId())) {
                return Response.failed("该linkId已被消费");
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(editEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            if (editEventDto.getAway() < 0 || editEventDto.getHome() < 0) {
                return Response.failed("比分设置错误");
            }
            if (redisService.tryLock(key, editEventDto.getLinkedId(), 2, 3)) {
                response = footBallEventService.editEvent(response.getData(), editEventDto);
                redisUtils.pushFootBallScore(editEventDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(editEventDto.getThirdMatchId());
                redisUtils.cacheRequestLinkId(editEventDto.getLinkedId());
                return response;
            } else {
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {

            log.error(":处理数据发生异常:", e);
        } finally {
            redisService.unLock(key, editEventDto.getLinkedId());
        }
        return Response.failed("服务器错误");
    }

    /**
     * 赛事事件流查询 需推送WS
     */

    @Override
    public Response eventList(EventListDto eventListDto) {
        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(eventListDto.getThirdMatchId());
        if (!response.isSuccess()) {
            return response;
        }

        return footBallEventService.eventList(response.getData(), eventListDto);
    }

    //编辑点球大战比分
    @Override
    public Response editPenaltyScore(PenaltyScoresEditDto penaltyScoresEditDto) {
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo( penaltyScoresEditDto.getThirdMatchId(), null);
        if (Objects.isNull(thirdMatchInfo)) {
            log.error("linkId=::{}::editPenaltyScore::三方赛事表里不存在，thirdMatchId:{}", penaltyScoresEditDto.getLinkedId(), penaltyScoresEditDto.getThirdMatchId());
            return Response.failed("三方赛事表里不存在");
        }
        // 存放事件时间，用于报球板事件监控
        long eventTime = System.currentTimeMillis();
        String actionMonitorKey = String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
        if ( thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) == 0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode()) ) {
            log.info("::{}::editPenaltyScore,key:{},eventTime:{}", penaltyScoresEditDto.getLinkedId(), actionMonitorKey,eventTime);
            redisService.set(actionMonitorKey,eventTime);
        }
        String key = "PA_createMatchAdvertise:" + penaltyScoresEditDto.getThirdMatchId();
        try {
            if ( redisService.tryLock(key, penaltyScoresEditDto.getLinkedId(), 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(penaltyScoresEditDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                // 点球大战期间，当前轮次比分不变直接返回
                String scoresJsonExtra = response.getData().getMatchScoresInfo().getScoresJsonExtra();
                if ( !ObjectUtils.isEmpty(scoresJsonExtra) ) {
                    FootballPenaltyScores footballPenaltyScores = JSON.parseObject( scoresJsonExtra, new TypeReference<FootballPenaltyScores>() {});
                    Map<String, CommonItem> roundScores = footballPenaltyScores.getRoundScores();
                    for (Map.Entry<String, CommonItem> entry : roundScores.entrySet()) {
                        if (penaltyScoresEditDto.getTargetRound() != null && !penaltyScoresEditDto.getTargetRound().equals(Integer.valueOf(entry.getKey()))) {
                            continue;
                        }
                        boolean flag = penaltyScoresEditDto.getTargetRound() != null && penaltyScoresEditDto.getTargetRound().equals(Integer.valueOf(entry.getKey()))
                                && penaltyScoresEditDto.getHome() != null && entry.getValue().getHome() != null && penaltyScoresEditDto.getHome().equals(entry.getValue().getHome())
                                && penaltyScoresEditDto.getAway() != null && entry.getValue().getAway() != null && penaltyScoresEditDto.getAway().equals(entry.getValue().getAway());
                        if (flag) {
                            return Response.failed("zs".equals(penaltyScoresEditDto.getLanguage()) ? "编辑前后比分相同" : "The score have no changed");
                        }
                    }
                }

                if ( null != penaltyScoresEditDto.getAway() && penaltyScoresEditDto.getAway() < 0 || null != penaltyScoresEditDto.getHome() && penaltyScoresEditDto.getHome() < 0 ) {
                    return Response.failed("比分设置错误");
                }
                String extryScore = null;
                if (!StringUtils.isAnyEmpty(response.getData().getMatchScoresInfo().getScoresJsonExtra())) {
                    extryScore = response.getData().getMatchScoresInfo().getScoresJsonExtra();
                }
                Response res = footBallEventService.editPenaltyScore( response.getData(), penaltyScoresEditDto);
                redisUtils.pushFootBallScore(penaltyScoresEditDto.getThirdMatchId());
                matchScorePdLogService.editPenaltyScoreLog(extryScore, response.getData(), penaltyScoresEditDto);
                return res;
            } else {
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {
            log.error(":处理数据发生异常:", e);
        } finally {
            redisService.unLock(key, penaltyScoresEditDto.getLinkedId());
        }
        return Response.failed("服务器错误");
    }

    //点球大战比分新增轮数
    @Override
    public Response addPenaltyRounds(PenaltyAddRoundsDto penaltyAddRoundsDto) {
        String key = "PA_createMatchAdvertise:" + penaltyAddRoundsDto.getThirdMatchId();
        Response kickOffEvent = kickOffEventCheck(penaltyAddRoundsDto.getThirdMatchId(), penaltyAddRoundsDto.getLanguage());
        if (kickOffEvent != null) {
            return kickOffEvent;
        }
        try {
            if (redisService.tryLock(key, penaltyAddRoundsDto.getLinkedId(), 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(penaltyAddRoundsDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                Response res = footBallEventService.addPenaltyRounds(response.getData(), penaltyAddRoundsDto);
                if (!res.isSuccess()) {
                    return response;
                }
                redisUtils.pushFootBallScore(penaltyAddRoundsDto.getThirdMatchId());
                // 下发实时服务
                addPenaltyRounds(response.getData(),penaltyAddRoundsDto);
                //推送比分
//                scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),penaltyAddRoundsDto.getLinkedId());
                matchScorePdLogService.addPenaltyRoundsLog(response.getData(), penaltyAddRoundsDto);
                return res;
            } else {
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {
            redisService.unLock(key, penaltyAddRoundsDto.getLinkedId());
        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response changePenaltyFirst(PenaltyFirstDto penaltyFirstDto) {
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(penaltyFirstDto.getThirdMatchId(), null);
        if (Objects.isNull(thirdMatchInfo)) {
            log.error("linkId=::{}::changePenaltyFirst::三方赛事表里不存在，thirdMatchId:{}",penaltyFirstDto.getLinkedId(),penaltyFirstDto.getThirdMatchId());
            return Response.failed("三方赛事表里不存在");
        }
        // 存放事件时间，用于报球板事件监控
        long eventTime=System.currentTimeMillis();
        String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
        if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
            log.info("::{}::changePenaltyFirst,key:{},eventTime:{}",penaltyFirstDto.getLinkedId(),actionMonitorKey,eventTime);
            redisService.set(actionMonitorKey,eventTime);
        }
        String key = "PA_changePenaltyFirst:" + penaltyFirstDto.getThirdMatchId();
        Response kickOffEvent = kickOffEventCheck(penaltyFirstDto.getThirdMatchId(), penaltyFirstDto.getLanguage());
        if (kickOffEvent != null) {
            return kickOffEvent;
        }
        try {
            if (redisService.tryLock(key, penaltyFirstDto.getLinkedId(), 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(penaltyFirstDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                Response res = footBallEventService.changePenaltyFirst(response.getData(), penaltyFirstDto);
                sendInfoToMatchForPenalty(response.getData(), PDEventCodeEnum.PENALTY_FIRST, penaltyFirstDto.getHomeAway());
                redisUtils.pushFootBallScore(penaltyFirstDto.getThirdMatchId());
                matchScorePdLogService.changePenaltyFirstLog(response.getData(), penaltyFirstDto);
                return res;
            } else {
                return Response.failed("[MatchFootballBallAdvertiseApiImpl] changePenaltyFirst 该事件正在被操作请重试");
            }
        } catch (Exception e) {
            log.error("[MatchFootballBallAdvertiseApiImpl] changePenaltyFirst 处理数据发生异常:", e);
        } finally {
            redisService.unLock(key, penaltyFirstDto.getLinkedId());
        }
        return Response.failed("[MatchFootballBallAdvertiseApiImpl] changePenaltyFirst 服务器错误");
    }

    @Override
    @RequestMapping("/changeAttackDirection")
    public Response changeAttackDirection(AttackDirectionDto attackDirectionDto) {
        String key = "PA_changeAttackDirection:" + attackDirectionDto.getThirdMatchId();
        return null;
    }

    //点球大战变更轮次
    @Override
    public Response changePenaltyRounds(PenaltyChangeRoundsDto penaltyChangeRoundsDto) {
        String linkId = penaltyChangeRoundsDto.getLinkedId();
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(penaltyChangeRoundsDto.getThirdMatchId(), null);
        if (Objects.isNull(thirdMatchInfo)) {
            log.error("linkId=::{}::changePenaltyRounds::三方赛事表里不存在，thirdMatchId:{}", linkId, penaltyChangeRoundsDto.getThirdMatchId());
            return Response.failed("三方赛事表里不存在");
        }

        if ( null == penaltyChangeRoundsDto.getTargetRound() || penaltyChangeRoundsDto.getTargetRound() > ConstantSystem.TWENTY_FOUR) {
            log.error("::{}::changePenaltyRounds轮次异常", linkId);
            return Response.failed("轮次异常");
        }

        // 存放事件时间，用于报球板事件监控
        long eventTime=System.currentTimeMillis();
        String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
        if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
            log.info("::{}::changePenaltyRounds,key:{},eventTime:{}",penaltyChangeRoundsDto.getLinkedId(),actionMonitorKey,eventTime);
            redisService.set(actionMonitorKey,eventTime);
        }
        String key = "PA_createMatchAdvertise:" + penaltyChangeRoundsDto.getThirdMatchId();
        Response kickOffEvent = kickOffEventCheck(penaltyChangeRoundsDto.getThirdMatchId(), penaltyChangeRoundsDto.getLanguage());
        if (kickOffEvent != null) {
            return kickOffEvent;
        }
        try {
            if (redisService.tryLock(key, penaltyChangeRoundsDto.getLinkedId(), 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(penaltyChangeRoundsDto.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                Response res = footBallEventService.changePenaltyRounds(response.getData(), penaltyChangeRoundsDto);
                redisUtils.pushFootBallScore(penaltyChangeRoundsDto.getThirdMatchId());
                matchScorePdLogService.changePenaltyRoundsLog(response.getData(), penaltyChangeRoundsDto);
                return res;
            } else {
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {

            log.error(":处理数据发生异常:", e);
        } finally {
            redisService.unLock(key, penaltyChangeRoundsDto.getLinkedId());
        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response edit15MinGoal(Goal15MinDto confirmEventDto) {
        String key = "edit15MinGoal:" + confirmEventDto.getThirdMatchId();
        try {
            Response<MatchScoreAndTimeVo> res = null;
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(confirmEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            String oldScoresJson = response.getData().getMatchScoresInfo().getScoresJson();
            if (redisService.tryLock(key, key, 2, 3)) {
                res = footBallEventService.edit15MinGoal(response.getData(), confirmEventDto);
                if (!res.isSuccess()) {
                    return res;
                }
                redisUtils.pushFootBallScore(confirmEventDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(confirmEventDto.getThirdMatchId());
                log.info("报球版15分钟进球比分编辑成功，编辑人：{},赛事id：{},比分数据：{}", confirmEventDto.getOperatorName(), confirmEventDto.getThirdMatchId(), confirmEventDto.toString());
            } else {
                return Response.failed("该事件正在被操作请重试");
            }
            matchScorePdLogService.modify15MinScoreLog(oldScoresJson, response.getData(), confirmEventDto);
            return res;
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {

        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response edit15MinCorner(Goal15MinDto confirmEventDto) {
        String linkId = confirmEventDto.getLinkedId();
        log.info("::{}::edit15MinCorner:{}", linkId, JSON.toJSONString(confirmEventDto));
        String key = "edit15MinCorner:" + confirmEventDto.getThirdMatchId();
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(confirmEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            String oldScoresJson = response.getData().getMatchScoresInfo().getScoresJson();
            MatchScoreAndTimeVo matchScoreAndTimeVo = response.getData();
            if (redisService.tryLock(key, key, 2, 3)) {
                response = footBallEventService.edit15MinCorner(response.getData(), confirmEventDto);
                if (!response.isSuccess()) {
                    return response;
                }
                redisUtils.pushFootBallScore(confirmEventDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(confirmEventDto.getThirdMatchId());
                log.info("::{}::报球版15分钟角球比分编辑成功，编辑人：{},赛事id：{},比分数据：{}", linkId, confirmEventDto.getOperatorName(),
                        confirmEventDto.getThirdMatchId(), confirmEventDto.toString());


                CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                            matchScorePdLogService.modify15MinCornerLog(linkId, oldScoresJson, matchScoreAndTimeVo, confirmEventDto)
                        ));

            } else {
                throw new RuntimeException("redis锁失败:");
            }
            return response;
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {

        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response edit15MinYellowCard(Goal15MinDto confirmEventDto)
    {
        String linkId = confirmEventDto.getLinkedId();
        log.info("::{}::edit15MinYellowCard:{}", linkId, JSON.toJSONString(confirmEventDto));
        String key = "panda-scores-admin:edit15MinYellowCard:" + confirmEventDto.getThirdMatchId();
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(confirmEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            MatchScoreAndTimeVo matchScoreAndTimeVo = response.getData();
            String oldScoresJson = response.getData().getMatchScoresInfo().getScoresJson();
            if (redisService.tryLock(key, key, 2, 3)) {
                response = footBallEventService.edit15MinYellowCard(response.getData(), confirmEventDto);
                if (!response.isSuccess()) {
                    return response;
                }
                redisUtils.pushFootBallScore(confirmEventDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(confirmEventDto.getThirdMatchId());
                log.info("报球版15分钟黄牌编辑成功，编辑人：{},赛事id：{},比分数据：{}", confirmEventDto.getOperatorName(), confirmEventDto.getThirdMatchId(), confirmEventDto.toString());

                if ( StringUtils.isEmpty(confirmEventDto.getConfirmEventCode()) )
                {
                    confirmEventDto.setConfirmEventCode(EventCodeEnum.YELLOW_CARD.code);
                }
                CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                        matchScorePdLogService.modify15MinCardLog(linkId, oldScoresJson, matchScoreAndTimeVo, confirmEventDto)
                    ));

            } else {
                throw new RuntimeException("redis锁失败!");
            }
            return response;
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        }
        return Response.failed("服务器异常");
    }

    @Override
    public Response edit15MinRedCard(Goal15MinDto confirmEventDto)
    {
        String linkId = confirmEventDto.getLinkedId();
        log.info("::{}::edit15MinRedCard:{}", linkId, JSON.toJSONString(confirmEventDto));
        String key = "panda-scores-admin:edit15MinYellowCard:" + confirmEventDto.getThirdMatchId();
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(confirmEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            MatchScoreAndTimeVo matchScoreAndTimeVo = response.getData();
            String oldScoresJson = response.getData().getMatchScoresInfo().getScoresJson();
            if (redisService.tryLock(key, key, 2, 3)) {
                response = footBallEventService.edit15MinRedCard(response.getData(), confirmEventDto);
                if (!response.isSuccess()) {
                    return response;
                }
                redisUtils.pushFootBallScore(confirmEventDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(confirmEventDto.getThirdMatchId());
                log.info("报球版15分钟红牌编辑成功，编辑人：{},赛事id：{},比分数据：{}", confirmEventDto.getOperatorName(), confirmEventDto.getThirdMatchId(), confirmEventDto.toString());

                if ( StringUtils.isEmpty(confirmEventDto.getConfirmEventCode()) )
                {
                    confirmEventDto.setConfirmEventCode(EventCodeEnum.RED_CARD.code);
                }
                CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                        matchScorePdLogService.modify15MinCardLog(linkId, oldScoresJson, matchScoreAndTimeVo, confirmEventDto)
                    ));

            } else {
                throw new RuntimeException("redis锁失败:");
            }
            return response;
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        }
        return Response.failed("服务器异常");
    }

    @Override
    public Response edit5MinGoal(Goal5MinDto confirmEventDto) {
        String key = "edit5MinGoal:" + confirmEventDto.getThirdMatchId();
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(confirmEventDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            Response<MatchScoreAndTimeVo> res = null;
            String oldScoresJson = response.getData().getMatchScoresInfo().getScoresJson();
            if (redisService.tryLock(key, key, 2, 3)) {
                res = footBallEventService.edit5MinGoal(response.getData(), confirmEventDto);
                if (!res.isSuccess()) {
                    return res;
                }
                redisUtils.pushFootBallScore(confirmEventDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(confirmEventDto.getThirdMatchId());
                log.info("报球版5分钟比分编辑成功，编辑人：{},赛事id：{},比分数据：{}", confirmEventDto.getOperatorName(), confirmEventDto.getThirdMatchId(), confirmEventDto.toString());
            } else {
                throw new RuntimeException("redis锁失败:");
            }
            matchScorePdLogService.modify5MinScoreLog(oldScoresJson, response.getData(), confirmEventDto);
            return res;
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {

        }
        return Response.failed("服务器错误");
    }

    @Override
    public Response updateSettleStatus(UpdateSettleStatusDto updateSettleStatusDto) {
        //更新缓存 保存 3天
        String key  = MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE +":"+ updateSettleStatusDto.getThirdMatchId();
        redisService.set(key,updateSettleStatusDto.getSettleStatus(),9000);
        matchScorePdLogService.updateSettleStatusLog(updateSettleStatusDto);
        return Response.success();
    }

    @Override
    public Response cancelMatchEnd(Long thirdMatchId, Integer secondFromStart, Long periodId, String userName, String userId, String address) {

        String key = "cancelMatchEnd:" + thirdMatchId;
        try {
            ThirdMatchInfo thirdMatch = pdMatchInfoRepository.getThirdMatchInfo(thirdMatchId, null);
            if (Objects.isNull(thirdMatch)) {
                log.error("cancelMatchEnd::三方赛事表里不存在，thirdMatchId:{}",thirdMatchId);
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatch.getThirdMatchSourceId());
            if (thirdMatch.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatch.getDataSourceCode())) {
                log.info("cancelMatchEnd,key:{},eventTime:{}",actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(thirdMatchId);
            if (!response.isSuccess()) {
                return response;
            }
            // 比赛拉回，初始化公共事件存入redis，缓存一周
            String publicEventKey = RONGHE_PD_FOOTBALL_PUBLIC_EVENT + thirdMatchId;
            long l = System.currentTimeMillis();
            PublicEvent publicEvent = new PublicEvent(thirdMatchId,0L, 0L, 0L, 0L, l, PublicEventEnum.KICK_OFF.getCode(),0);
            redisService.set(publicEventKey, JSONObject.toJSON(publicEvent).toString(), REDIS_WEEK_TIME);
            Response<MatchScoreAndTimeVo> res = null;

            if (redisService.tryLock(key, key, 2, 3)) {
                // 比赛拉回时，根据当前阶段设置赛事时间(页面未传赛事时间时，重置赛事时间)
                Integer matchLength = response.getData().getStandardMatchInfo().getMatchLength();
                if (secondFromStart == null || secondFromStart == 0) {
                    secondFromStart = getSecondFromStart(matchLength, periodId);
                }
                //先变更阶段,再修复时间和比分X
                //针对当前阶段和时间进行初始化检查，需要补充阶段之前和时间之前的 半场  5分钟 15分钟阶段比分
                checkAndCreateInitPeriodScore(response.getData().getMatchScoresInfo(),periodId,secondFromStart);
                //根据三方赛事id查询当前标准赛事Id
                ThirdMatchInfo thirdMatchInfo = response.getData().getThirdMatchInfo();
                //1.三方赛事修改
                updateThirdMatchInfo(thirdMatchInfo,secondFromStart,periodId);
                //1.2 更新标准赛事开售状态信息 滚球改为开售中
                updateStandardMatchInfoLiveSold(response.getData());
                updateScoreAndTime(response.getData().getMatchScoresInfo(),response.getData().getMatchTimeInfo(),secondFromStart,periodId);
                //2.比分回滚阶段修剪
                cancelEndScoreUpdate(thirdMatchInfo,secondFromStart,periodId,response.getData().getMatchScoresInfo());
                //3.下发MQ 通知 对标准赛事进行回滚动作X
                eventProducer.sendCancelMatchEnd(thirdMatchInfo,secondFromStart,periodId,response.getData());
                eventProducer.sendCancelMatchEndStatus(response.getData(),secondFromStart,periodId,response.getData().getMatchScoresInfo());
                redisUtils.pushFootBallScore(thirdMatchId);
                redisUtils.pushFootBallCancelEndEvent(thirdMatchId,periodId);
                log.info("报球版取消比赛结束成功，编辑人：{},赛事id：{},阶段：{},时间:{}", userName, thirdMatchId, periodId,secondFromStart);
                matchScorePdLogService.modifyCancelMatchEndLog(secondFromStart,periodId, userName,address,userId,response.getData());
            } else {
                throw new RuntimeException("redis锁失败");
            }
            return Response.success();
        } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {

        }
        return Response.failed("服务器错误");
    }

    /**
     * 比赛拉回时，根据当前阶段设置赛事时间(页面未传赛事时间时，重置赛事时间)
     *
     * @param matchLength 赛制编码
     * @param periodId    阶段ID
     * @return 比赛时间
     */
    private Integer getSecondFromStart(Integer matchLength, Long periodId) {
        int secondFromStart = 0;
        switch (periodId.intValue()) {
            case 6:
            case 31:
                break;
            case 7:
            case 32:
                secondFromStart = Math.toIntExact(MatchPeriodUtils.getFootBallPeriodTime(matchLength, 7L));
                break;
            case 41:
            case 33:
                secondFromStart = Math.toIntExact(MatchPeriodUtils.getFootBallPeriodTime(matchLength, 41L));
                break;
            case 42:
            case 34:
            case 50:
                secondFromStart = Math.toIntExact(MatchPeriodUtils.getFootBallPeriodTime(matchLength, 42L));
                break;
            default:
                secondFromStart = Math.toIntExact(MatchPeriodUtils.getFootBallPeriodTime(matchLength, periodId));
        }
        return secondFromStart;
    }

    private void updateStandardMatchInfoLiveSold(MatchScoreAndTimeVo data) {
        StandardMatchInfo standardMatchInfo = data.getStandardMatchInfo();
//        StandardSportMarketSellExample example =new StandardSportMarketSellExample();
//        example.createCriteria().andMatchInfoIdEqualTo(standardMatchInfo.getId());
//        List<StandardSportMarketSell> list =standardSportMarketSellMapper .selectByExample(example);
//        if(list.size()==0){
//            return;
//        }
        StandardSportMarketSell marketSell = pdMatchInfoRepository.getStandardSportMarketSell(standardMatchInfo.getId(), null);
        if (ObjectUtils.isEmpty(marketSell)) {
            return;
        }
        ThirdMatchInfo thirdMatchInfo = data.getThirdMatchInfo();
        boolean flag = !ObjectUtils.isEmpty(thirdMatchInfo)
                && !ObjectUtils.isEmpty(thirdMatchInfo.getDataSourceCode())
                && thirdMatchInfo.getDataSourceCode().equals(marketSell.getBusinessEvent());
        if (flag) {
            StandardSportMarketSell standardSportMarketSell = marketSell;
            standardSportMarketSell.setLiveMatchSellStatus("Sold");
            standardSportMarketSell.setPreMatchSellStatus("Sold");
            standardSportMarketSell.setStatus("Enable");
//            standardSportMarketSellMapper.updateByPrimaryKey(standardSportMarketSell);
            pdMatchInfoRepository.setRedisAndStandardSportMarketSell(standardSportMarketSell,null);
            standardMatchInfo.setMatchOver(0);
            StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
            newStandardMatchInfo.setId(standardMatchInfo.getId());
            newStandardMatchInfo.setMatchOver(0);
//            standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
            pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
        }
    }

    private void checkAndCreateInitPeriodScore(MatchScoresInfo matchScoresInfo, Long periodId, Integer secondFromStart) {
        //0 计算当前的 5分钟 15分钟 半场
        //1.计算当前5分钟阶段
        Long period5 =SportPeriodConstant.FootballPeriod.get5MinPeriod(periodId,secondFromStart.longValue());
        //2.计算当前15分钟阶段
        Long period15 =SportPeriodConstant.FootballPeriod.get15MinPeriod(periodId,secondFromStart.longValue());
        //1.得到必须有的 半场 5分钟 15分钟 比分

        //2.半场比分生成

        footBallScoreService.searchCommonMatchScore(matchScoresInfo,periodId);
        //3.5分钟生成

        //4.15分钟生成

    }

    private void updateScoreAndTime(MatchScoresInfo matchScoresInfo, MatchTimeInfo matchTimeInfo, Integer secondFromStart, Long periodId) {
        matchScoresInfo.setPeriod(periodId);
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
        matchTimeInfo.setPeriod(periodId);
        matchTimeInfo.setEventTime(System.currentTimeMillis());
        matchTimeInfo.setSecondFromStart(secondFromStart.longValue());
        matchTimeInfo.setModifyTime(System.currentTimeMillis());
        if (matchTimeInfo.getTimeGo() == 0) {
            matchTimeInfo.setTimeGo(1);
        }
//        matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
    }

    @Override
    public Response editAllScore(AllFootballScoreEditDto allFootballScoreEditDto) {
        String key = "editAllScore:" + allFootballScoreEditDto.getThirdMatchId();
        try {
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(allFootballScoreEditDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }
            if (redisService.tryLock(key, key, 2, 3)) {
                //1.比分展开
                JSONObject periodFootballScores = JSONObject.parseObject(response.getData().getMatchScoresInfo().getScoresJson());
                Map<Long, FootballScores> oldScores= JsonMapUtils.parseFootballMap(periodFootballScores);
                Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
                FootballScores wholeScore  =  allPeriodScores.get(-1L);
                wholeScore.setGoal(new CommonItem());
                wholeScore.setCorner(new CommonItem());
                wholeScore.setYellowCard(new CommonItem());
                wholeScore.setRedCard(new CommonItem());
                //1.当有比分录入的时候需要生成比分
                //2.循环每个比分
                for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
                    if(entry.getKey().equals(6L)){
                        //进球角球
                        entry.getValue().getCorner().setHome(allFootballScoreEditDto.getHtCornerT1());
                        entry.getValue().getCorner().setAway(allFootballScoreEditDto.getHtCornerT2());
                        entry.getValue().getGoal().setHome(allFootballScoreEditDto.getHtGoalT1());
                        entry.getValue().getGoal().setAway(allFootballScoreEditDto.getHtGoalT2());
                        //红黄牌
                        entry.getValue().getYellowCard().setHome(allFootballScoreEditDto.getHtYellowCardT1());
                        entry.getValue().getYellowCard().setAway(allFootballScoreEditDto.getHtYellowCardT2());
                        entry.getValue().getRedCard().setHome(allFootballScoreEditDto.getHtRedCardT1());
                        entry.getValue().getRedCard().setAway(allFootballScoreEditDto.getHtRedCardT2());
                        //罚牌
                        entry.getValue().getFaCard().setHome(allFootballScoreEditDto.getHtRedCardT1()*2+allFootballScoreEditDto.getHtYellowCardT1());
                        entry.getValue().getFaCard().setAway(allFootballScoreEditDto.getHtRedCardT2()*2+allFootballScoreEditDto.getHtYellowCardT2());
                        //总分累加
                        addWholeScore(wholeScore,entry);
                    }
                    if(entry.getKey().equals(7L)){
                        //进球角球
                        entry.getValue().getCorner().setHome(allFootballScoreEditDto.getHt2CornerT1());
                        entry.getValue().getCorner().setAway(allFootballScoreEditDto.getHt2CornerT2());
                        entry.getValue().getGoal().setHome(allFootballScoreEditDto.getHt2GoalT1());
                        entry.getValue().getGoal().setAway(allFootballScoreEditDto.getHt2GoalT2());
                        //红黄牌
                        entry.getValue().getYellowCard().setHome(allFootballScoreEditDto.getHt2YellowCardT1());
                        entry.getValue().getYellowCard().setAway(allFootballScoreEditDto.getHt2YellowCardT2());
                        entry.getValue().getRedCard().setHome(allFootballScoreEditDto.getHt2RedCardT1());
                        entry.getValue().getRedCard().setAway(allFootballScoreEditDto.getHt2RedCardT2());
                        entry.getValue().getFaCard().setHome(allFootballScoreEditDto.getHt2RedCardT1()*2+allFootballScoreEditDto.getHt2YellowCardT1());
                        entry.getValue().getFaCard().setAway(allFootballScoreEditDto.getHt2RedCardT2()*2+allFootballScoreEditDto.getHt2YellowCardT2());
                        //总分累加
                        addWholeScore(wholeScore,entry);
                    }

                    if(entry.getKey().equals(41L)){
                        //进球角球
                        entry.getValue().getCorner().setHome(allFootballScoreEditDto.getExCornerT1());
                        entry.getValue().getCorner().setAway(allFootballScoreEditDto.getExCornerT2());
                        entry.getValue().getGoal().setHome(allFootballScoreEditDto.getExGoalT1());
                        entry.getValue().getGoal().setAway(allFootballScoreEditDto.getExGoalT2());
                        //红黄牌
                        entry.getValue().getYellowCard().setHome(allFootballScoreEditDto.getExYellowCardT1());
                        entry.getValue().getYellowCard().setAway(allFootballScoreEditDto.getExYellowCardT2());
                        entry.getValue().getRedCard().setHome(allFootballScoreEditDto.getExRedCardT1());
                        entry.getValue().getRedCard().setAway(allFootballScoreEditDto.getExRedCardT2());
                        entry.getValue().getFaCard().setHome(allFootballScoreEditDto.getExRedCardT1()*2+allFootballScoreEditDto.getExYellowCardT1());
                        entry.getValue().getFaCard().setAway(allFootballScoreEditDto.getExRedCardT2()*2+allFootballScoreEditDto.getExYellowCardT2());
                        //总分累加
                        addWholeScore(wholeScore,entry);
                    }
                    if(entry.getKey().equals(42L)){
                        //进球角球
                        entry.getValue().getCorner().setHome(allFootballScoreEditDto.getEx2CornerT1());
                        entry.getValue().getCorner().setAway(allFootballScoreEditDto.getEx2CornerT2());
                        entry.getValue().getGoal().setHome(allFootballScoreEditDto.getEx2GoalT1());
                        entry.getValue().getGoal().setAway(allFootballScoreEditDto.getEx2GoalT2());
                        //红黄牌
                        entry.getValue().getYellowCard().setHome(allFootballScoreEditDto.getEx2YellowCardT1());
                        entry.getValue().getYellowCard().setAway(allFootballScoreEditDto.getEx2YellowCardT2());
                        entry.getValue().getRedCard().setHome(allFootballScoreEditDto.getEx2RedCardT1());
                        entry.getValue().getRedCard().setAway(allFootballScoreEditDto.getEx2RedCardT2());
                        entry.getValue().getFaCard().setHome(allFootballScoreEditDto.getEx2RedCardT1()*2+allFootballScoreEditDto.getEx2YellowCardT1());
                        entry.getValue().getFaCard().setAway(allFootballScoreEditDto.getEx2RedCardT2()*2+allFootballScoreEditDto.getEx2YellowCardT2());
                        //总分累加
                        addWholeScore(wholeScore,entry);
                    }
                }
                //比分更新
                response.getData().getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
                response.getData().getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
//                matchScoresInfoMapper.updateByPrimaryKey( response.getData().getMatchScoresInfo());
                pdMatchInfoRepository.setRedisAndMatchScoresInfo(response.getData().getMatchScoresInfo(), null);
                matchScoreInfoRepository.updateScoresInfo( response.getData().getMatchScoresInfo());
                scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(),response.getData().getMatchScoresInfo(),response.getData().getThirdMatchInfo().getReferenceId()+"_EDIT_SCORE");
                matchScorePdLogService.modifyEditAllScoreLog(allFootballScoreEditDto,response.getData(),oldScores,allPeriodScores);
                //9 推送前端
                redisUtils.pushFootBallScore(allFootballScoreEditDto.getThirdMatchId());
                redisUtils.pushFootBallEvent(allFootballScoreEditDto.getThirdMatchId());
                return Response.success();
            }
            return Response.failed();
         } catch (Exception e) {
            
            log.error(":处理数据发生异常:", e);
        } finally {

        }
        return Response.failed("服务器错误");
    }

    @Override
    public List<Object> getAllRedisValue(List<String> keys) {
        return redisService.mGet(keys);
    }

    @Override
    public Response executeTakePenalty(TakePenaltyDTO takePenaltyDto) {
        log.info("executeTakePenalty的入参:{}", JSON.toJSONString(takePenaltyDto));
        String linkId = takePenaltyDto.getLinkedId();
        Long thirdMatchId = takePenaltyDto.getThirdMatchId();
        String eventCode = takePenaltyDto.getEventCode();
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo( thirdMatchId, null);
        if (Objects.isNull(thirdMatchInfo)) {
            log.error("::{}::executeTakePenalty::三方赛事表里不存在，thirdMatchId:{}", linkId, thirdMatchId);
            return Response.failed("三方赛事表里不存在");
        }
        if ( StringUtils.isEmpty(eventCode) || null == PDEventCodeEnum.getEventCodeEnum(eventCode) ) {
            log.error("::{}::executeTakePenalty::三方事件为空或无效", linkId);
            return Response.failed("三方事件为空或无效");
        }
        // 存放事件时间，用于报球板事件监控
        long eventTime = System.currentTimeMillis();
        String actionMonitorKey = String.format(Constant.ACTION_MONITER_KEY, thirdMatchInfo.getThirdMatchSourceId());
        if ( thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) == 0 &&
                ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode()) ) {
            log.info("::{}::executeTakePenalty, key:{}, eventTime:{}", linkId, actionMonitorKey, eventTime);
            redisService.set(actionMonitorKey, eventTime);
        }
        String key = "PA_takePenalty:" + thirdMatchId;
        try {
            if ( redisService.tryLock(key, linkId, 2, 3) ) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate( thirdMatchId );
                if (!response.isSuccess()) {
                    return response;
                }

                Response res = footBallEventService.executeTakePenalty( response.getData(), takePenaltyDto);

                sendInfoToMatchForPenalty(response.getData(), PDEventCodeEnum.getEventCodeEnum(eventCode), takePenaltyDto.getHomeAway());
                redisUtils.pushFootBallScore(thirdMatchId);
                // 记录日志
                matchScorePdLogService.takePenaltyLog(response.getData(), takePenaltyDto);

                return res;
            } else {
                return Response.failed("[MatchFootballBallAdvertiseApiImpl] takePenalty 该事件正在被操作请重试");
            }
        } catch (Exception e) {
            log.error("::{}::executeTakePenalty处理数据发生异常:", linkId, e);
        } finally {
            redisService.unLock( key, linkId);
        }
        return Response.failed("[MatchFootballBallAdvertiseApiImpl] takePenalty 服务器错误");
    }

    /**
     * 点球即将开始
     *
     * @param penaltyAboutToBeTakenDTO 点球即将开始
     * @return 响应点球即将开始数据
     */
    @Override
    public Response penaltyAboutToBeTaken(PenaltyAboutToBeTakenDto penaltyAboutToBeTakenDto) {
        log.info("penaltyAboutToBeTaken执行入参:{}", JSON.toJSONString(penaltyAboutToBeTakenDto));
        try {
            if (redisUtils.checkRequestLinkId( penaltyAboutToBeTakenDto.getLinkedId())) {
                log.info("::{}::penaltyAboutToBeTakenDTO::该linkId已被消费", penaltyAboutToBeTakenDto.getLinkedId());
                return Response.failed("该linkId已被消费");
            }
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo( penaltyAboutToBeTakenDto.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::retakePen::三方赛事表里不存在，thirdMatchId:{}", penaltyAboutToBeTakenDto.getLinkedId(), penaltyAboutToBeTakenDto.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }

            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey = String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if ( thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) == 0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode()) ) {
                log.info("::{}::penaltyAboutToBeTaken, key:{}, eventTime:{}", penaltyAboutToBeTakenDto.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(penaltyAboutToBeTakenDto.getThirdMatchId());
            if (!response.isSuccess()) {
                return response;
            }

            Long matchTime = getMatchTime(response.getData());
            penaltyAboutToBeTakenDto.setTimeFromStartSecond(matchTime);

            sendInfoToMatchForPenalty(response.getData(), PDEventCodeEnum.getEventCodeEnum(penaltyAboutToBeTakenDto.getEventCode()), penaltyAboutToBeTakenDto.getHomeAway());
            redisUtils.pushFootBallScore(penaltyAboutToBeTakenDto.getThirdMatchId());

            // 日志异步执行
            CustomThreadPoolExecutor.cacheThreadExecutor(new Thread(() ->
                    matchScorePdLogService.penaltyAboutToBeTakenLog( response.getData(), penaltyAboutToBeTakenDto)
            ));

            return Response.success();
        } catch (Exception e) {
            log.error("::点球即将开始处理数据发生异常:", penaltyAboutToBeTakenDto.getLinkedId(), e);
        }
        return Response.failed("服务器错误");
    }


    private void addWholeScore(FootballScores wholeScore, Map.Entry<Long, FootballScores> entry) {
        wholeScore.getCorner().setHome(wholeScore.getCorner().getHome()+entry.getValue().getCorner().getHome());
        wholeScore.getCorner().setAway(wholeScore.getCorner().getAway()+entry.getValue().getCorner().getAway());

        wholeScore.getGoal().setHome(wholeScore.getGoal().getHome()+entry.getValue().getGoal().getHome());
        wholeScore.getGoal().setAway(wholeScore.getGoal().getAway()+entry.getValue().getGoal().getAway());

        wholeScore.getYellowCard().setHome(wholeScore.getYellowCard().getHome()+entry.getValue().getYellowCard().getHome());
        wholeScore.getYellowCard().setAway(wholeScore.getYellowCard().getAway()+entry.getValue().getYellowCard().getAway());

        wholeScore.getRedCard().setHome(wholeScore.getRedCard().getHome()+entry.getValue().getRedCard().getHome());
        wholeScore.getRedCard().setAway(wholeScore.getRedCard().getAway()+entry.getValue().getRedCard().getAway());

        wholeScore.getFaCard().setHome(wholeScore.getRedCard().getHome()*2+wholeScore.getYellowCard().getHome());
        wholeScore.getFaCard().setAway(wholeScore.getRedCard().getAway()*2+wholeScore.getYellowCard().getAway());
    }


    public void cancelEndScoreUpdate(ThirdMatchInfo thirdMatchInfo, Integer secondFromStart, Long periodId , MatchScoresInfo matchScoresInfo) {
        //1.计算当前5分钟阶段
        Long period5 =SportPeriodConstant.FootballPeriod.get5MinPeriod(periodId,secondFromStart.longValue());
        //2.计算当前15分钟阶段
        Long period15 =SportPeriodConstant.FootballPeriod.get15MinPeriod(periodId,secondFromStart.longValue());
        //3. 循环比分
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        Map<Long, FootballScores> afterScores= new HashMap<>();
        //被改后的阶段，如果比循环比分的阶段早，则去除
        //3.1 5分钟比分去除
        //3.2 15分钟比分去除
        //3.3 阶段比分去除
        for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
            Long periodNow=  entry.getKey();
            //全部比分不变
            if(periodNow==-1L){
                afterScores.put(periodNow,entry.getValue());
            }
            //5分钟
            if(periodNow>=6005L&&periodNow<=7095L){
                if(period5==null){
                    continue;
                }
                if(period5>=periodNow){
                    afterScores.put(periodNow,entry.getValue());
                }
                continue;
            }
            //15分钟
            if(periodNow>=60899L&&periodNow<=75399L){
                if(period15==null){
                    continue;
                }
                if(period15>=periodNow){
                    afterScores.put(periodNow,entry.getValue());
                }
                continue;
            }
            //标准阶段
            if(periodId>=periodNow){
                afterScores.put(periodNow,entry.getValue());
            }
        }
        //4.更新表和比分缓存
        matchScoresInfo.setPeriod(periodId);
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(afterScores));
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        //5.结束
    }


    private void updateThirdMatchInfo(ThirdMatchInfo thirdMatchInfo, Integer secondFromStart, Long periodId) {
        //1.更新三方赛事
        thirdMatchInfo.setSecondsMatchStart(secondFromStart);
        thirdMatchInfo.setMatchPeriod(periodId.toString());
        thirdMatchInfo.setMatchStatus(1);
        thirdMatchInfo.setMatchOver(0);
        thirdMatchInfo.setModifyTime(System.currentTimeMillis());
//        thirdMatchInfoMapper.updateByPrimaryKey(thirdMatchInfo);
        eventProducer.sendMatchStatusTopic("PD_"+ UUID.randomUUID(), thirdMatchInfo, thirdMatchInfo.getMatchStatus());
    }

    /**
     * 根据当前阶段，重新计算全场比分
     *
     * @param periodNow   当前阶段下的子阶段
     * @param reCalc      重新计算
     * @param afterScores 过程比分
     */
    private void reCalcFullPeriod(Long periodNow, FootballScoreReCalc reCalc, Map<Long, FootballScores> afterScores) {
        FootballScores value = new FootballScores(periodNow);
        if (null != value.getGoal()) {
            value.getGoal().setHome(reCalc.getGoalHome());
        }
        if (null != value.getGoal()) {
            value.getGoal().setAway(reCalc.getGoalAway());
        }
        if (null != value.getCorner()) {
            value.getCorner().setHome(reCalc.getCornerHome());
        }
        if (null != value.getCorner()) {
            value.getCorner().setAway(reCalc.getCornerAway());
        }
        if (null != value.getRedCard()) {
            value.getRedCard().setHome(reCalc.getRedCardHome());
        }
        if (null != value.getRedCard()) {
            value.getRedCard().setAway(reCalc.getRedCardAway());
        }
        if (null != value.getYellowCard()) {
            value.getYellowCard().setHome(reCalc.getYellowCardHome());
        }
        if (null != value.getYellowCard()) {
            value.getYellowCard().setAway(reCalc.getYellowCardAway());
        }
        if (null != value.getFaCard()) {
            value.getFaCard().setHome(reCalc.getFaCardHome());
        }
        if (null != value.getFaCard()) {
            value.getFaCard().setAway(reCalc.getFaCardAway());
        }
        if (null != value.getAttack()) {
            value.getAttack().setHome(reCalc.getAttackHome());
        }
        if (null != value.getAttack()) {
            value.getAttack().setAway(reCalc.getAttackAway());
        }
        if (null != value.getDangerousAttack()) {
            value.getDangerousAttack().setHome(reCalc.getDangerousAttackHome());
        }
        if (null != value.getDangerousAttack()) {
            value.getDangerousAttack().setAway(reCalc.getDangerousAttackAway());
        }
        if (null != value.getBallPossessionPercentage()) {
            value.getBallPossessionPercentage().setHome(reCalc.getPossessionHome());
        }
        if (null != value.getBallPossessionPercentage()) {
            value.getBallPossessionPercentage().setAway(reCalc.getPossessionAway());
        }
        if (null != value.getShotOn()) {
            value.getShotOn().setHome(reCalc.getShotOnHome());
        }
        if (null != value.getShotOn()) {
            value.getShotOn().setAway(reCalc.getShotOnAway());
        }
        if (null != value.getShotOff()) {
            value.getShotOff().setHome(reCalc.getShotOffHome());
        }
        if (null != value.getShotOff()) {
            value.getShotOff().setAway(reCalc.getShotOffAway());
        }
        if (null != value.getShot()) {
            value.getShot().setHome(reCalc.getShotHome());
        }
        if (null != value.getShot()) {
            value.getShot().setAway(reCalc.getShotAway());
        }
        if (null != value.getSubstitution()) {
            value.getSubstitution().setHome(reCalc.getSubstitutionHome());
        }
        if (null != value.getSubstitution()) {
            value.getSubstitution().setAway(reCalc.getSubstitutionAway());
        }
        if (null != value.getOffside()) {
            value.getOffside().setHome(reCalc.getOffsideHome());
        }
        if (null != value.getOffside()) {
            value.getOffside().setAway(reCalc.getOffsideAway());
        }
        if (null != value.getPenaltyAwarded()) {
            value.getPenaltyAwarded().setHome(reCalc.getPenaltyAwardedHome());
        }
        if (null != value.getPenaltyAwarded()) {
            value.getPenaltyAwarded().setAway(reCalc.getPenaltyAwardedAway());
        }
        if (null != value.getFreeKickScore()) {
            value.getFreeKickScore().setHome(reCalc.getFreeKickScoreHome());
        }
        if (null != value.getFreeKickScore()) {
            value.getFreeKickScore().setAway(reCalc.getFreeKickScoreAway());
        }
        if (null != value.getKickOff()) {
            value.getKickOff().setHome(reCalc.getKickOffHome());
        }
        if (null != value.getKickOff()) {
            value.getKickOff().setAway(reCalc.getKickOffAway());
        }
        if (null != value.getThrowIn()) {
            value.getThrowIn().setHome(reCalc.getThrowInHome());
        }
        if (null != value.getThrowIn()) {
            value.getThrowIn().setAway(reCalc.getThrowInAway());
        }
        if (null != value.getGoalKick()) {
            value.getGoalKick().setHome(reCalc.getGoalKickHome());
        }
        if (null != value.getGoalKick()) {
            value.getGoalKick().setAway(reCalc.getGoalKickAway());
        }
        if (null != value.getYellowRedCard()) {
            value.getYellowRedCard().setHome(reCalc.getYellowRedCardHome());
        }
        if (null != value.getYellowRedCard()) {
            value.getYellowRedCard().setAway(reCalc.getYellowRedCardAway());
        }
        afterScores.put(periodNow, value);
    }

    /**
     * 计算当前阶段5分钟和15分钟比分
     *
     * @param entry  比分实例
     * @param reCalc 重新计算
     */
    private void calcPeriodFiveAndFifteen(Map.Entry<Long, FootballScores> entry, FootballScoreReCalc reCalc) {
        FootballScores value = entry.getValue();
        if (null != value.getGoal() && null != value.getGoal().getHome()) {
            reCalc.setGoalHome(reCalc.getGoalHome() + value.getGoal().getHome());
        }
        if (null != value.getGoal() && null != value.getGoal().getAway()) {
            reCalc.setGoalAway(reCalc.getGoalAway() + value.getGoal().getAway());
        }
        if (null != value.getCorner() && null != value.getCorner().getHome()) {
            reCalc.setCornerHome(reCalc.getCornerHome() + value.getCorner().getHome());
        }
        if (null != value.getCorner() && null != value.getCorner().getAway()) {
            reCalc.setCornerAway(reCalc.getCornerAway() + value.getCorner().getAway());
        }
        if (null != value.getRedCard() && null != value.getRedCard().getHome()) {
            reCalc.setRedCardHome(reCalc.getRedCardHome() + value.getRedCard().getHome());
        }
        if (null != value.getRedCard() && null != value.getRedCard().getAway()) {
            reCalc.setRedCardAway(reCalc.getRedCardAway() + value.getRedCard().getAway());
        }
        if (null != value.getYellowCard() && null != value.getYellowCard().getHome()) {
            reCalc.setYellowCardHome(reCalc.getYellowCardHome() + value.getYellowCard().getHome());
        }
        if (null != value.getYellowCard() && null != value.getYellowCard().getAway()) {
            reCalc.setYellowCardAway(reCalc.getYellowCardAway() + value.getYellowCard().getAway());
        }
        if (null != value.getFaCard() && null != value.getFaCard().getHome()) {
            reCalc.setFaCardHome(reCalc.getFaCardHome() + value.getFaCard().getHome());
        }
        if (null != value.getFaCard() && null != value.getFaCard().getAway()) {
            reCalc.setFaCardAway(reCalc.getFaCardAway() + value.getFaCard().getAway());
        }
        if (null != value.getAttack() && null != value.getAttack().getHome()) {
            reCalc.setAttackHome(reCalc.getAttackHome() + value.getAttack().getHome());
        }
        if (null != value.getAttack() && null != value.getAttack().getAway()) {
            reCalc.setAttackAway(reCalc.getAttackAway() + value.getAttack().getAway());
        }
        if (null != value.getDangerousAttack() && null != value.getDangerousAttack().getHome()) {
            reCalc.setDangerousAttackHome(reCalc.getDangerousAttackHome() + value.getDangerousAttack().getHome());
        }
        if (null != value.getDangerousAttack() && null != value.getDangerousAttack().getAway()) {
            reCalc.setDangerousAttackAway(reCalc.getDangerousAttackAway() + value.getDangerousAttack().getAway());
        }
        if (null != value.getBallPossessionPercentage() && null != value.getBallPossessionPercentage().getHome()) {
            reCalc.setPossessionHome(reCalc.getPossessionHome() + value.getBallPossessionPercentage().getHome());
        }
        if (null != value.getBallPossessionPercentage() && null != value.getBallPossessionPercentage().getAway()) {
            reCalc.setPossessionAway(reCalc.getPossessionAway() + value.getBallPossessionPercentage().getAway());
        }
        if (null != value.getShotOn() && null != value.getShotOn().getHome()) {
            reCalc.setShotOnHome(reCalc.getShotOnHome() + value.getShotOn().getHome());
        }
        if (null != value.getShotOn() && null != value.getShotOn().getAway()) {
            reCalc.setShotOnAway(reCalc.getShotOnAway() + value.getShotOn().getAway());
        }
        if (null != value.getShotOff() && null != value.getShotOff().getHome()) {
            reCalc.setShotOffHome(reCalc.getShotOffHome() + value.getShotOff().getHome());
        }
        if (null != value.getShotOff() && null != value.getShotOff().getAway()) {
            reCalc.setShotOffAway(reCalc.getShotOffAway() + value.getShotOff().getAway());
        }
        if (null != value.getShot() && null != value.getShot().getHome()) {
            reCalc.setShotHome(reCalc.getShotHome() + value.getShot().getHome());
        }
        if (null != value.getShot() && null != value.getShot().getAway()) {
            reCalc.setShotAway(reCalc.getShotAway() + value.getShot().getAway());
        }
        if (null != value.getSubstitution() && null != value.getSubstitution().getHome()) {
            reCalc.setSubstitutionHome(reCalc.getSubstitutionHome() + value.getSubstitution().getHome());
        }
        if (null != value.getSubstitution() && null != value.getSubstitution().getAway()) {
            reCalc.setSubstitutionAway(reCalc.getSubstitutionAway() + value.getSubstitution().getAway());
        }
        if (null != value.getOffside() && null != value.getOffside().getHome()) {
            reCalc.setOffsideHome(reCalc.getOffsideHome() + value.getOffside().getHome());
        }
        if (null != value.getOffside() && null != value.getOffside().getAway()) {
            reCalc.setOffsideAway(reCalc.getOffsideAway() + value.getOffside().getAway());
        }
        if (null != value.getPenaltyAwarded() && null != value.getPenaltyAwarded().getHome()) {
            reCalc.setPenaltyAwardedHome(reCalc.getPenaltyAwardedHome() + value.getPenaltyAwarded().getHome());
        }
        if (null != value.getPenaltyAwarded() && null != value.getPenaltyAwarded().getAway()) {
            reCalc.setPenaltyAwardedAway(reCalc.getPenaltyAwardedAway() + value.getPenaltyAwarded().getAway());
        }
        if (null != value.getFreeKickScore() && null != value.getFreeKickScore().getHome()) {
            reCalc.setFreeKickScoreHome(reCalc.getFreeKickScoreHome() + value.getFreeKickScore().getHome());
        }
        if (null != value.getFreeKickScore() && null != value.getFreeKickScore().getAway()) {
            reCalc.setFreeKickScoreAway(reCalc.getFreeKickScoreAway() + value.getFreeKickScore().getAway());
        }
        if (null != value.getKickOff() && null != value.getKickOff().getHome()) {
            reCalc.setKickOffHome(reCalc.getKickOffHome() + value.getKickOff().getHome());
        }
        if (null != value.getKickOff() && null != value.getKickOff().getAway()) {
            reCalc.setKickOffAway(reCalc.getKickOffAway() + value.getKickOff().getAway());
        }
        if (null != value.getThrowIn() && null != value.getThrowIn().getHome()) {
            reCalc.setThrowInHome(reCalc.getThrowInHome() + value.getThrowIn().getHome());
        }
        if (null != value.getThrowIn() && null != value.getThrowIn().getAway()) {
            reCalc.setThrowInAway(reCalc.getThrowInAway() + value.getThrowIn().getAway());
        }
        if (null != value.getGoalKick() && null != value.getGoalKick().getHome()) {
            reCalc.setGoalKickHome(reCalc.getGoalKickHome() + value.getGoalKick().getHome());
        }
        if (null != value.getGoalKick() && null != value.getGoalKick().getAway()) {
            reCalc.setGoalKickAway(reCalc.getGoalKickAway() + value.getGoalKick().getAway());
        }
        if (null != value.getYellowRedCard() && null != value.getYellowRedCard().getHome()) {
            reCalc.setYellowRedCardHome(reCalc.getYellowRedCardHome() + value.getYellowRedCard().getHome());
        }
        if (null != value.getYellowRedCard() && null != value.getYellowRedCard().getAway()) {
            reCalc.setYellowRedCardAway(reCalc.getYellowRedCardAway() + value.getYellowRedCard().getAway());
        }
    }

    private void sendInfoToMatchForPenalty(MatchScoreAndTimeVo data, PDEventCodeEnum eventCode, String homeAway){
        long startTimeSecond = data.getMatchTimeInfo().getSecondFromStart() + (System.currentTimeMillis() - data.getMatchTimeInfo().getEventTime()) / 1000;
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createSimpleMatchEvent(data,homeAway,startTimeSecond,startTimeSecond,
                System.currentTimeMillis(), eventCode.getEventCode(),50L,"","");
        matchEventInfoDTO.setAddition9("false");
        if ( Constant.penaltyDangerEventCode.contains( eventCode.getEventCode())) {
            matchEventInfoDTO.setAddition9("true");
        }
        eventProducer.sendPDFootballClickEventInfo(matchEventInfoDTO,startTimeSecond, null);
    }

    /**
     * 点球大战增加轮次，下发实时服务
     *
     * @param data                查询数据
     * @param penaltyAddRoundsDto 点球大战增加轮次
     */
    private void addPenaltyRounds(MatchScoreAndTimeVo data, PenaltyAddRoundsDto penaltyAddRoundsDto) {
        long startTimeSecond = data.getMatchTimeInfo().getSecondFromStart() + (System.currentTimeMillis() - data.getMatchTimeInfo().getEventTime()) / 1000;
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createSimpleMatchEvent(data, null, startTimeSecond, startTimeSecond,
                System.currentTimeMillis(), "match_status", data.getMatchTimeInfo().getPeriod(), penaltyAddRoundsDto.getLinkedId(), penaltyAddRoundsDto.getOperatorName());
        matchEventInfoDTO.setAddition9("false");
        MatchScoresInfo matchScoresInfo = data.getMatchScoresInfo();
        matchEventInfoDTO.setT1(matchScoresInfo.getT1());
        matchEventInfoDTO.setT2(matchScoresInfo.getT2());
        matchEventInfoDTO.setExtrainfo("penalty_add_after_5_rounds");
        eventProducer.sendPDBasketballEditEventInfo(matchEventInfoDTO);
    }


}
