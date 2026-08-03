package com.panda.merge.advertise.dubbo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.service.FootBallAdvertiseService;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.api.FootballDashboardAdvertiseApi;
import com.panda.merge.bo.UserLoginStatusBO;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ScoreEventCodeSourceEnum;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.ForceUserSignOutDto;
import com.panda.merge.dto.advertise.InjuryTimeEventDto;
import com.panda.merge.dto.PublicEvent;
import com.panda.merge.dto.advertise.TimeStatusEventDto;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.sports.auth.exception.SessionValidException;
import com.panda.sports.auth.rpc.IAuthRequiredPermission;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.panda.merge.advertise.common.Constant.*;
import static com.panda.merge.common.enums.Constant.MATCH_FOOTBALL_INJURY;
import static com.panda.merge.common.enums.Constant.MATCH_FOOTBALL_TIME_STATUS;
import static com.panda.merge.common.enums.Constant.MATCH_FOOTBALL_TIME_STOP;
import static com.panda.merge.common.enums.Constant.PD_FOOTBALL_EVENT_MONITOR;
import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;

/**
 * PA报球板2.0-事件接口实现类
 *
 * @author warren
 * @since 2023/12/06 13:11:27
 */
@Service
@Slf4j
@DubboService
public class FootballDashboardAdvertiseApiImpl implements FootballDashboardAdvertiseApi {
    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonAdvertiseService commonAdvertiseService;

    @Autowired
    private RedisUtils redisUtils;

//    @Autowired
//    private MatchTimeInfoMapper matchTimeInfoMapper;

//    @Autowired
//    private MatchScoresInfoMapper matchScoresInfoMapper;

    @Autowired
    private EventProducer eventProduce;

    @Autowired
    private IMatchScorePdLogService iMatchScorePdLogService;

    @Autowired
    private FootBallAdvertiseService footBallAdvertiseService;

    @Autowired
    private IMatchScorePdLogService matchScorePdLogService;

    @Autowired
    private ScoresProducer scoresProducer;

    @DubboReference(check = false)
    private IAuthRequiredPermission iAuthRequiredPermission;

    @Autowired
    private MatchFootballBallAdvertiseApiImpl matchFootballBallAdvertiseApi;

    @Autowired
    private PdMatchInfoRepository pdMatchInfoRepository;

    @Override
    public Response injuryTimeEvent(InjuryTimeEventDto injuryTimeEventDto) {
        Long thirdMatchId = injuryTimeEventDto.getThirdMatchId();
        String key = MATCH_FOOTBALL_INJURY + thirdMatchId;
        ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(injuryTimeEventDto.getThirdMatchId(), null);
        if (Objects.isNull(thirdMatchInfo)) {
            log.error("linkId=::{}::injuryTimeEvent::三方赛事表里不存在，thirdMatchId:{}",injuryTimeEventDto.getLinkedId(),injuryTimeEventDto.getThirdMatchId());
            return Response.failed("三方赛事表里不存在");
        }
        // 存放事件时间，用于报球板事件监控
        long eventTime=System.currentTimeMillis();
        String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
        if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
            log.info("::{}::injuryTimeEvent,key:{},eventTime:{}",injuryTimeEventDto.getLinkedId(),actionMonitorKey,eventTime);
            redisService.set(actionMonitorKey,eventTime);
        }
        try {
            Long timeOut = injuryTimeEventDto.getTimeOut();
            if (!String.valueOf(timeOut).matches("([0-9]|[1-9][0-9])")) {
                return Response.failed("请输入0-99之间整数时间");
            }

            // 检查redis linkId已消费则返回
            if (redisUtils.checkRequestLinkId(injuryTimeEventDto.getLinkedId())) {
                return Response.failed("该linkId已被消费");
            }

            if (redisService.tryLock(key, key, 2, 3)) {
                // 校验三方赛事,赛事比分,赛事事件信息
                Response<MatchScoreAndTimeVo> res = commonAdvertiseService.checkMatchScoreAndTimeCreate(thirdMatchId);
                if (BooleanUtil.isFalse(res.isSuccess())) {
                    return res;
                }
                MatchTimeInfo matchTimeInfo = res.getData().getMatchTimeInfo();
                Long period = matchTimeInfo.getPeriod();
                if (period <= 0) {
                    return Response.failed("赛事未开始，无法修改进行时间");
                }
                // 查询并更新伤补时间
                MatchTimeInfo matchTimeInfoRep = pdMatchInfoRepository.getMatchTimeInfo(thirdMatchId, SourceTypeEnum.LIVE_DATA.getCode(), null);
                if (ObjectUtil.isEmpty(matchTimeInfoRep)) {
                    return Response.failed("赛事未开始，无法修改伤补时间");
                }
                String timeOutList = matchTimeInfoRep.getTimeOutList();
                String jsonStr = getScoresJson(timeOutList, period, timeOut);
                // 伤补时间事件日志
                MatchScoreAndTimeVo resData = res.getData();
                Long matchTime = getMatchTime(resData);
                injuryTimeEventDto.setTimeFromStartSecond(matchTime);
                // 公共事件时间统计
                PublicEvent publicEvent = matchFootballBallAdvertiseApi.updateEventTime(res.getData(), period, "injury_time", null);
                // 更新事件时间
                matchFootballBallAdvertiseApi.updateEventTimeByJsonScore(res, res.getData().getMatchTimeInfo().getPeriod(), publicEvent);
                MatchScoresInfo matchScoresInfoRedis = pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(res.getData().getMatchScoresInfo().getId(), null);
                pdMatchInfoRepository.onlyUpdateMatchScoresInfoDB(matchScoresInfoRedis);
                res.getData().setMatchScoresInfo(matchScoresInfoRedis);
                resData.setMatchScoresInfo(matchScoresInfoRedis);
                iMatchScorePdLogService.injuryTimeEventLog(resData, injuryTimeEventDto);
                matchTimeInfo.setTimeOutList(jsonStr);
                matchTimeInfo.setModifyTime(System.currentTimeMillis());
//                matchTimeInfoMapper.updateByPrimaryKeySelective(matchTimeInfo);
                pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
                // 推送WS
                redisUtils.pushFootBallEvent(injuryTimeEventDto.getThirdMatchId());
                redisUtils.pushFootBallScore(injuryTimeEventDto.getThirdMatchId());
                // 发送MQ
                eventProduce.sendPAInjuryStopEvent(injuryTimeEventDto, matchTimeInfo, res.getData());
                // 组装持久化json数据
                MatchScoresInfo matchScoresInfo = res.getData().getMatchScoresInfo();
//                updateTimeAndScoresInfo(injuryTimeEventDto, matchTimeInfo, jsonStr, res);
                // 返回封装数据
                MatchTimeInfo matchTimeInfoRes = pdMatchInfoRepository.getMatchTimeInfo(thirdMatchId, SourceTypeEnum.LIVE_DATA.getCode(), null);
                Map<Object, Object> mapRes = new HashMap<>();
                Long periodRes = matchTimeInfoRes.getPeriod();
                Long thirdMatchIdRes = matchTimeInfoRes.getThirdMatchId();
                timeOutList = matchTimeInfoRes.getTimeOutList();
                mapRes.put("periodId", periodRes);
                mapRes.put("thirdMatchId", thirdMatchIdRes);
                mapRes.put("timeOut", timeOutList);
                return Response.success(mapRes);
            } else {
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {
            ByteArrayOutputStream exception = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(exception));
            log.error("FootballDashboardAdvertiseApiImpl==>injuryTimeEvent==>error: {}", exception);
        } finally {
            redisService.unLock(key, key);
        }
        return Response.failed("服务器错误");
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

//    private void updateTimeAndScoresInfo(InjuryTimeEventDto injuryTimeEventDto, MatchTimeInfo matchTimeInfo, String jsonStr, Response<MatchScoreAndTimeVo> res) {
//        Long timeFromStartSecond = injuryTimeEventDto.getTimeFromStartSecond();
//        long currentTime = System.currentTimeMillis();
//        matchTimeInfo.setSecondFromStart(timeFromStartSecond);
//        matchTimeInfo.setRemainingTime(timeFromStartSecond);
//        matchTimeInfo.setModifyTime(currentTime);
//        matchTimeInfo.setEventTime(currentTime);
//        matchTimeInfo.setTimeOutList(jsonStr);
//        matchTimeInfoMapper.updateByPrimaryKeySelective(matchTimeInfo);
//        MatchScoresInfo matchScoresInfo = res.getData().getMatchScoresInfo();
//        matchScoresInfo.setSecondsMatchStart(timeFromStartSecond);
//        matchScoresInfo.setRemainingTime(timeFromStartSecond);
//        matchScoresInfo.setEventTime(currentTime);
//        matchScoresInfo.setModifyTime(currentTime);
//        matchScoresInfoMapper.updateByPrimaryKeySelective(matchScoresInfo);
//    }

    private static String getScoresJson(String timeOutList, Long period, Long timeOut) {
        Set<Map<String, Long>> list = new HashSet<>();
        Map<String, Long> map = new HashMap<>();
        if (StrUtil.isNotEmpty(timeOutList)) {
            list = JSONObject.parseObject(timeOutList, new TypeReference<Set<Map<String, Long>>>() {
            });
            if (CollectionUtil.isNotEmpty(list)) {
                Set<Long> set = new HashSet<>();
                for (Map<String, Long> stringLongMap : list) {
                    set.add(stringLongMap.get("period"));
                }
                if (set.contains(period)) {
                    for (Map<String, Long> strMap : list) {
                        if (Objects.equals(strMap.get("period"), period)) {
                            strMap.put("timeOut", timeOut);
                        }
                    }
                } else {
                    map.put("period", period);
                    map.put("timeOut", timeOut);
                    list.add(map);
                }
            } else {
                map.put("period", period);
                map.put("timeOut", timeOut);
                list.add(map);
            }
        } else {
            map.put("period", period);
            map.put("timeOut", timeOut);
            list.add(map);
        }
        String jsonStr = JSONObject.toJSONString(list);
        return jsonStr;
    }

    @Override
    public Response timeStatusEvent(TimeStatusEventDto timeStatusEventDto) {
        log.info("pd赛事中断重开，参数：{}",timeStatusEventDto);
        Long thirdMatchId = timeStatusEventDto.getThirdMatchId();
        String key = MATCH_FOOTBALL_TIME_STATUS + thirdMatchId;
        try {
            // 检查redis linkId已消费则返回
            if (redisUtils.checkRequestLinkId(timeStatusEventDto.getLinkedId())) {
                log.info("::{}::timeStatusEvent::该linkId已被消费",timeStatusEventDto.getLinkedId());
                return Response.failed("该linkId已被消费");
            }
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(timeStatusEventDto.getThirdMatchId(), null);
            if (Objects.isNull(thirdMatchInfo)) {
                log.error("linkId=::{}::timeStatusEvent::三方赛事表里不存在，thirdMatchId:{}",timeStatusEventDto.getLinkedId(),timeStatusEventDto.getThirdMatchId());
                return Response.failed("三方赛事表里不存在");
            }
            // 存放事件时间，用于报球板事件监控
            long eventTime=System.currentTimeMillis();
            String actionMonitorKey =String.format(ACTION_MONITER_KEY,thirdMatchInfo.getThirdMatchSourceId());
            if (thirdMatchInfo.getSportId().compareTo(SportTypeEnum.FOOTBALL.getValue()) ==0 && ScoreEventCodeSourceEnum.getResult(thirdMatchInfo.getDataSourceCode())) {
                log.info("::{}::timeStatusEventRedis,key:{},eventTime:{}",timeStatusEventDto.getLinkedId(),actionMonitorKey,eventTime);
                redisService.set(actionMonitorKey,eventTime);
            }
            // 校验三方赛事,赛事比分,赛事事件信息
            Response<MatchScoreAndTimeVo> res = commonAdvertiseService.checkMatchScoreAndTimeCreate(thirdMatchId);
            if (BooleanUtil.isFalse(res.isSuccess())) {
                return res;
            }
            Long matchTime = getMatchTime(res.getData());
            if(StrUtil.isNotEmpty(timeStatusEventDto.getType())){
                matchTime = timeStatusEventDto.getTimeFromStartSecond();
            }
            log.info("pd赛事中断，type={},赛事ID：{}，时间：{}，{}",timeStatusEventDto.getType(),
                    res.getData().getStandardMatchInfo().getId(),matchTime,timeStatusEventDto.getTimeFromStartSecond());
            timeStatusEventDto.setTimeFromStartSecond(matchTime);
            String suspensionKey = thirdMatchId + "suspension";
            if (redisService.tryLock(key, key, 2, 3)) {
                MatchScoreAndTimeVo resData = res.getData();
                // 获取赛事时间表数据
                MatchTimeInfo matchTimeInfo = resData.getMatchTimeInfo();
                MatchScoresInfo matchScoresInfo = resData.getMatchScoresInfo();
                // 暂停时系统时间设置redis，WS响应时，用该时间计算时间
                if (timeStatusEventDto.getTimeGo() == 0) {
                    //1.0 若比赛已经中断阶段 则返回失败
                    if (matchTimeInfo.getPeriod().equals(80L)) {
                        return Response.failed("比赛已经中断");
                    }
                    redisService.set(suspensionKey, timeStatusEventDto.getPeriod()!=null?timeStatusEventDto.getPeriod():matchTimeInfo.getPeriod());
                    //1.2 更新阶段为中断
                    matchTimeInfo.setPeriod(80L);
                    matchScoresInfo.setPeriod(80L);
                    matchTimeInfo.setModifyTime(System.currentTimeMillis());
                    matchTimeInfo.setTimeGo(timeStatusEventDto.getTimeGo());
//                    matchTimeInfo.setSecondFromStart(matchTimeInfo.getSecondFromStart() + (System.currentTimeMillis() - matchTimeInfo.getEventTime()) / 1000);
                    matchTimeInfo.setSecondFromStart(matchTime);
                    matchTimeInfo.setEventTime(System.currentTimeMillis());
                    matchScoresInfo.setModifyTime(System.currentTimeMillis());
                    // 更新赛事时间表数据-重置比赛时间.
//                    matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
//                    matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
                    pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
                    pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
                }
                if (timeStatusEventDto.getTimeGo() == 1) {
                    //2.1 查询当前比赛如果不是中断则返回失败
                    if (!matchTimeInfo.getPeriod().equals(80L)) {
                        return Response.failed("比赛已经中断");
                    }
                    Object obj = redisService.get(suspensionKey);
                    if (obj == null) {
                        return Response.failed("中断的阶段已经丢失");
                    }
                    redisService.del(suspensionKey);
                    //2.2 更新阶段为缓存的阶段
                    Long period = Long.parseLong(obj.toString());
                    if(timeStatusEventDto.getPeriod()!=null){
                        period = timeStatusEventDto.getPeriod();
                    }
                    if(timeStatusEventDto.getTimeFromStartSecond()!=null){
                        matchTime = timeStatusEventDto.getTimeFromStartSecond();
                    }
                    matchTimeInfo.setPeriod(period);
                    matchScoresInfo.setPeriod(period);
                    matchTimeInfo.setTimeGo(timeStatusEventDto.getTimeGo());
                    matchTimeInfo.setModifyTime(System.currentTimeMillis());
                    matchTimeInfo.setEventTime(System.currentTimeMillis());
                    matchScoresInfo.setModifyTime(System.currentTimeMillis());
                    matchTimeInfo.setSecondFromStart(matchTime);
//                    matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
//                    matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
                    pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchTimeInfo, null);
                    pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
                }
                MatchTimeInfo matchTimeInfoUpdated = getMatchTimeInfoUpdated(matchTimeInfo.getThirdMatchId());
                Map<String, Integer> timeGo = new HashMap<>();
                if (ObjectUtil.isNotEmpty(matchTimeInfoUpdated)) {
                    timeGo.put("timeGo", matchTimeInfoUpdated.getTimeGo());
                } else {
                    return Response.failed("没有暂停数据更新");
                }
                // 发送实时服务MQ
                eventProduce.sendTimeStatusEvent(timeStatusEventDto, matchTimeInfo, res.getData());
                //发送赛事状态
                eventProduce.sendMatchStatusTopic(thirdMatchId + "_PD_"+"score_event_operate_"+System.currentTimeMillis(), thirdMatchInfo, thirdMatchInfo.getMatchStatus());
                // 推送WS
                redisUtils.pushFootBallEvent(timeStatusEventDto.getThirdMatchId());
                redisUtils.pushFootBallScore(timeStatusEventDto.getThirdMatchId());
                // 时间状态事件日志
                iMatchScorePdLogService.timeStatusEventLog(resData, timeStatusEventDto);
                // 封装更新数据
                return Response.success(timeGo);
            } else {
                return Response.failed("该事件正在被操作请重试");
            }
        } catch (Exception e) {
            ByteArrayOutputStream exception = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(exception));
            log.error("FootballDashboardAdvertiseApiImpl==>timeStatusEvent==>error: {}", exception);
        } finally {
            redisService.unLock(key, key);
        }
        return Response.failed("服务器错误");
    }

    /**
     * 获取更新后赛事信息
     *
     * @param thirdMatchId 三方ID
     * @return 更新信息
     */
    @Override
    public MatchTimeInfo getMatchTimeInfoUpdated(Long thirdMatchId) {
        return pdMatchInfoRepository.getMatchTimeInfo(thirdMatchId,SourceTypeEnum.LIVE_DATA.getCode(), null);
    }

    @Override
    public Response changeMatchStatus(ChangeMatchStatusDto changeMatchStatus) {
        String linkId = changeMatchStatus.getLinkedId();
        log.info("::{}::changeMatchStatus的入参:{}", linkId, JSON.toJSONString(changeMatchStatus));
        String key = MATCH_FOOTBALL_TIME_STOP + changeMatchStatus.getThirdMatchId();
        try {
            if (redisUtils.checkRequestLinkId(changeMatchStatus.getLinkedId())) {
                return Response.failed("该linkId已被消费");
            }
            if (redisService.tryLock(key, key, 2, 3)) {
                Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(changeMatchStatus.getThirdMatchId());
                if (!response.isSuccess()) {
                    return response;
                }
                if (changeMatchStatus.getControlType() == null || changeMatchStatus.getControlType() > 4 || changeMatchStatus.getControlType() < 2) {
                    return Response.failed("操作不符合定义");
                }
                MatchTimeInfo matchTimeInfo = response.getData().getMatchTimeInfo();
                String changeKey = "PA_changeMatchStatus:" + changeMatchStatus.getThirdMatchId();
                if (!redisService.tryLock(changeKey, changeKey, 1, 2)) {
                    return Response.failed("后台正在响应,请稍后再试。");
                }
                redisService.expire(changeKey, 1);
                String interruptionKey = changeMatchStatus.getThirdMatchId() + "interruption";
                // 1.中断 controlType=2(temporary_interruption)
                if (MATCH_PAUSE.equals(changeMatchStatus.getControlType())) {
                    redisService.set(interruptionKey,changeMatchStatus.getTimeFromStartSecond());
                    MatchScoreAndTimeVo data = response.getData();
                    String linkedId = changeMatchStatus.getLinkedId();
                    Integer controlType = changeMatchStatus.getControlType();
                    Response pauseRes = footBallAdvertiseService.matchPause(data, linkedId, changeMatchStatus);
                    matchScorePdLogService.changeMatchStatusLog(response.getData(), changeMatchStatus);
                    // 暂停 1，不暂停 0
                    response.getData().getThirdMatchInfo().setWhetherStop(1);
                    scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(), response.getData().getMatchScoresInfo(), changeMatchStatus.getLinkedId());
                    redisUtils.cacheRequestLinkId(changeMatchStatus.getLinkedId());
                    // 推送ws
                    redisUtils.pushFootBallScore(changeMatchStatus.getThirdMatchId());
                    redisUtils.pushFootBallPauseContinueEvent(changeMatchStatus.getThirdMatchId(), controlType);
                    return pauseRes;
                }
                // 2.开始(中断后开始) controlType=3(game_on)
                if (MATCH_CONTINUE.equals(changeMatchStatus.getControlType())) {
                    Object interruptionKeyObj = redisService.get(interruptionKey);
                    if (null != interruptionKeyObj) {
                        redisService.del(interruptionKey);
                    }
                    MatchScoreAndTimeVo data = response.getData();
                    String linkedId = changeMatchStatus.getLinkedId();
                    Integer controlType = changeMatchStatus.getControlType();
                    Response continueResponse = footBallAdvertiseService.matchContinue(data, linkedId,controlType);
                    matchScorePdLogService.changeMatchStatusLog(response.getData(), changeMatchStatus);
                    // 暂停 1，不暂停 0
                    response.getData().getThirdMatchInfo().setWhetherStop(0);
                    scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(), response.getData().getMatchScoresInfo(), changeMatchStatus.getLinkedId());
                    redisUtils.cacheRequestLinkId(changeMatchStatus.getLinkedId());
                    // 推送ws
                    redisUtils.pushFootBallScore(changeMatchStatus.getThirdMatchId());
                    redisUtils.pushFootBallPauseContinueEvent(changeMatchStatus.getThirdMatchId(), controlType);
                    return continueResponse;
                }
                // 3.比赛中断(结束)
                if (MATCH_END.equals(changeMatchStatus.getControlType())) {
                    Response endRes = footBallAdvertiseService.matchEnd(response.getData(), changeMatchStatus.getLinkedId());
                    matchScorePdLogService.changeMatchStatusLog(response.getData(), changeMatchStatus);
                    scoresProducer.sendToMQ(response.getData().getThirdMatchInfo(), response.getData().getMatchScoresInfo(), changeMatchStatus.getLinkedId());
                    redisUtils.cacheRequestLinkId(changeMatchStatus.getLinkedId());
                    // 推送ws
                    redisUtils.pushFootBallScore(changeMatchStatus.getThirdMatchId());
                    redisUtils.pushFootBallEvent(changeMatchStatus.getThirdMatchId());
                    return endRes;
                }
                return Response.failed();
            } else {
                throw new RuntimeException("后台正在响应,请稍后再试");
            }
        } catch (Exception e) {
            ByteArrayOutputStream exception = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(exception));
            log.error("::异常信息 {} ::", exception);
            return Response.failed(e.getMessage());
        } finally {
            redisService.unLock(key, key);
        }
    }

    @Override
    public Response<UserLoginStatusBO> forceUserSignOut(ForceUserSignOutDto forceUserSignOutDto) {
        try {
            Map<Object, Object> response = iAuthRequiredPermission.forceUserSignOut(null, forceUserSignOutDto.getPdUserName(),
                    forceUserSignOutDto.getManagerId(),forceUserSignOutDto.getManagerName());
            String userName = (String)response.get("userName");
            Integer loginStatus = (Integer) response.get("loginStatus");
            UserLoginStatusBO userLoginStatusBO = new UserLoginStatusBO();
            userLoginStatusBO.setUserName(userName);
            userLoginStatusBO.setLoginStatus(loginStatus);
            return Response.success(userLoginStatusBO);
        } catch (SessionValidException e) {
            log.error("[FootballDashboardAdvertiseApiImpl] forceUserSignOut error: ", e);
            return Response.failed(e.getMessage());
        }
    }

}
