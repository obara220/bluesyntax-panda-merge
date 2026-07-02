package com.panda.merge.aop;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.MatchEventMonitorEnum;
import com.panda.merge.common.enums.TimeStatusEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.advertise.CancelEventDto;
import com.panda.merge.dto.advertise.ChangeMatchPeriodDto;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.ChangeMatchTimeDto;
import com.panda.merge.dto.advertise.ConfirmEventDto;
import com.panda.merge.dto.advertise.DeleteEventDto;
import com.panda.merge.dto.advertise.EventOperationDto;
import com.panda.merge.dto.advertise.ForceUserSignOutDto;
import com.panda.merge.dto.advertise.InjuryTimeEventDto;
import com.panda.merge.dto.advertise.IsDangerDto;
import com.panda.merge.dto.advertise.KickOffDto;
import com.panda.merge.dto.advertise.NoRetakePenDto;
import com.panda.merge.dto.advertise.PenaltyChangeRoundsDto;
import com.panda.merge.dto.advertise.PenaltyFirstDto;
import com.panda.merge.dto.advertise.PenaltyScoresEditDto;
import com.panda.merge.dto.advertise.PossibleEventDto;
import com.panda.merge.dto.advertise.RetakePenDto;
import com.panda.merge.dto.advertise.TimeStatusEventDto;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.model.FootballEventMonitor;
import com.panda.merge.model.FootballKeyboardSet;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.StandardSportMarketSellExample;
import com.panda.merge.model.TypeReferenceChild;
import com.panda.sports.auth.exception.SessionValidException;
import com.panda.sports.auth.rpc.IAuthRequiredPermission;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.advertise.common.Constant.ACTION_MONITER_KEY;
import static com.panda.merge.common.enums.Constant.PD_FOOTBALL_EVENT_MONITOR;
import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;

/**
 * 足球报球板监控事件时间间隔区间，点击事件系统时间存入redis
 *
 * @author warren
 * @since 2024/11/02 11:23:48
 */
@Aspect
@Component
@Slf4j
public class FootballEventMonitorAspect {
    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonAdvertiseService commonAdvertiseService;

    @DubboReference(check = false)
    private IAuthRequiredPermission iAuthRequiredPermission;

    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Pointcut("execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.confirmEvent(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.cancelEvent(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.possibleEvent(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.changeMatchPeriod(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.FootballDashboardAdvertiseApiImpl.timeStatusEvent(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.FootballDashboardAdvertiseApiImpl.injuryTimeEvent(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.isDanger(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchBasketBallAdvertiseApiImpl.changeMatchTime(..))"
            + "|| execution(* com.panda.merge.dubbo.ScoresCenterApiImpl.addVarEvent(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.kickOffAfterGoal(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.retakePen(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.deleteEvent(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.setMatchEnd(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.cancelMatchEnd(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.FootballDashboardHotKeyApiImpl.addKeyboardInfo(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.FootballDashboardHotKeyApiImpl.updateKeyboardByUserName(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.changePenaltyFirst(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.editPenaltyScore(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.changePenaltyRounds(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.kickOff(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.FootballDashboardAdvertiseApiImpl.forceUserSignOut(..))"
            + "|| execution(* com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl.noRetakePen(..))"
    )

    public void pointCut() {
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void around(JoinPoint joinPoint, Object result) {
        Map<String, Object> params = getNameAndValue(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        long currentTime = System.currentTimeMillis();
        Long thirdMatchId = null;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof ConfirmEventDto) {
                ConfirmEventDto paramValue = (ConfirmEventDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof CancelEventDto) {
                CancelEventDto paramValue = (CancelEventDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof PossibleEventDto) {
                PossibleEventDto paramValue = (PossibleEventDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof ChangeMatchPeriodDto) {
                ChangeMatchPeriodDto paramValue = (ChangeMatchPeriodDto) value;
                List<Long> periods = Arrays.asList( 31L, 32L, 33L, 34L, 50L,80L, 100L, 110L,999L);
                int pauseStatus = TimeStatusEnum.CONTINUE.getDesc();
                if (periods.contains(paramValue.getPeriodId())) {
                    pauseStatus = TimeStatusEnum.PAUSE.getDesc();
                }
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, pauseStatus);
            }
            if (value instanceof TimeStatusEventDto) {
                TimeStatusEventDto paramValue = (TimeStatusEventDto) value;
                int pauseStatus = TimeStatusEnum.CONTINUE.getDesc();
                if (paramValue.getTimeGo().equals(TimeStatusEnum.PAUSE.getDesc())) {
                    pauseStatus = TimeStatusEnum.PAUSE.getDesc();
                }
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, pauseStatus);
            }
            if (value instanceof InjuryTimeEventDto) {
                InjuryTimeEventDto paramValue = (InjuryTimeEventDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof IsDangerDto) {
                IsDangerDto paramValue = (IsDangerDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof ChangeMatchTimeDto) {
                ChangeMatchTimeDto paramValue = (ChangeMatchTimeDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof EventOperationDto) {
                EventOperationDto paramValue = (EventOperationDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof KickOffDto) {
                KickOffDto paramValue = (KickOffDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof RetakePenDto) {
                RetakePenDto paramValue = (RetakePenDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof DeleteEventDto) {
                DeleteEventDto paramValue = (DeleteEventDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof ChangeMatchStatusDto) {
                ChangeMatchStatusDto paramValue = (ChangeMatchStatusDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof FootballKeyboardSet) {
                FootballKeyboardSet paramValue = (FootballKeyboardSet) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
            if (value instanceof PenaltyFirstDto) {
                PenaltyFirstDto paramValue = (PenaltyFirstDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.PAUSE.getDesc());
            }
            if (value instanceof PenaltyScoresEditDto) {
                PenaltyScoresEditDto paramValue = (PenaltyScoresEditDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.PAUSE.getDesc());
            }
            if (value instanceof PenaltyChangeRoundsDto) {
                PenaltyChangeRoundsDto paramValue = (PenaltyChangeRoundsDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.PAUSE.getDesc());
            }
            if (value instanceof ForceUserSignOutDto) {
                ForceUserSignOutDto paramValue = (ForceUserSignOutDto) value;
                deleteUserInfoWhenLogout(paramValue);
            }
            if (value instanceof NoRetakePenDto) {
                NoRetakePenDto paramValue = (NoRetakePenDto) value;
                thirdMatchId = updateRedisInfo(paramValue.getThirdMatchId(), paramValue.getOperatorName(), currentTime, TimeStatusEnum.CONTINUE.getDesc());
            }
        }
        if ("cancelMatchEnd".equals(joinPoint.getSignature().getName())) {
            Long thirdMatchIdValue = 0L;
            String userName = "";
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if ("thirdMatchId".equals(entry.getKey())) {
                    thirdMatchIdValue = (Long) entry.getValue();
                }
                if ("userName".equals(entry.getKey())) {
                    userName = (String) entry.getValue();
                }
            }
            thirdMatchId = updateRedisInfo(thirdMatchIdValue, userName, currentTime, TimeStatusEnum.CONTINUE.getDesc());
        }
        log.info("FootballEventMonitorAspect-around, thirdMatchId:{}", methodName, thirdMatchId);
    }

    /**
     * 更新redis数据
     *
     * @param thirdMatchId 三方赛事Id
     * @param operatorName 当前登录人
     * @param currentTime  操作当前事件系统时间
     * @param pauseStatus  赛事时间暂停=0 继续=1
     * @return 赛事Id
     */
    private Long updateRedisInfo(Long thirdMatchId, String operatorName, long currentTime, Integer pauseStatus) {
        String linkedId = IdWorker.getId() + "_PD_ACTION_MONITOR";
        Object monitorObj = redisService.get(PD_FOOTBALL_EVENT_MONITOR);

        Response<MatchScoreAndTimeVo> response = commonAdvertiseService.checkMatchScoreAndTimeCreate(thirdMatchId);
        if (response.getData().getMatchScoresInfo().getSportId() != 1) {
            return thirdMatchId;
        }
        MatchScoreAndTimeVo data = response.getData();
        Long matchTime = getMatchTime(data);
        Integer timeGo = data.getMatchTimeInfo().getTimeGo();
        Long period = data.getMatchScoresInfo().getPeriod();
        String dataSourceCode = data.getMatchScoresInfo().getDataSourceCode();
        Long matchTimeInfoId = data.getMatchTimeInfo().getId();
        StandardSportMarketSellExample example = new StandardSportMarketSellExample();
        example.createCriteria().andMatchInfoIdEqualTo(data.getStandardMatchInfo().getId());
        List<StandardSportMarketSell> standardSportMarketSellList = standardSportMarketSellMapper.selectByExample(example);
        String businessEvent = null;
        if (!CollectionUtils.isEmpty(standardSportMarketSellList)) {
            businessEvent = standardSportMarketSellList.get(0).getBusinessEvent();
        }

        // 获取报球板操作事件时的系统时间
        String actionMonitorKey =String.format(ACTION_MONITER_KEY,data.getThirdMatchInfo().getThirdMatchSourceId());
        Object o = redisService.get(actionMonitorKey);
        log.info("::报球板足球事件监控thirdMatchSourceId={}::事件时间={}", data.getThirdMatchInfo().getThirdMatchSourceId(), o);
        Long eventCurrentTime = null;
        if (!ObjectUtils.isEmpty(o)) {
            eventCurrentTime = Long.parseLong(o.toString());
        }

        // 首次初始化redis数据
        if (monitorObj == null) {
            List<FootballEventMonitor> list = new ArrayList<>();
            FootballEventMonitor monitor = new FootballEventMonitor(operatorName, new ArrayList<>());
            // 创建事件
            MatchEventInfoDTO eventInfoDTO = MatchEventUtils.createMatchTimeEvent(data, matchTime, matchTime, currentTime, timeGo, period, linkedId);
            eventInfoDTO.setCopyLinkId(linkedId);
            eventInfoDTO.setEventCode("action_monitor");
            eventInfoDTO.setAddition3(businessEvent);
            eventInfoDTO.setAddition4(String.valueOf(TimeStatusEnum.CONTINUE.getDesc()));
            eventInfoDTO.setAddition6(String.valueOf(matchTimeInfoId));
            // addition7缓存上次比赛时间
            eventInfoDTO.setAddition7(String.valueOf(TimeStatusEnum.INIT_PERSIST.getDesc()));
            eventInfoDTO.setAddition8(String.valueOf(thirdMatchId));
            eventInfoDTO.setAddition9("online");
            eventInfoDTO.setAddition10(MatchEventMonitorEnum.ONLINE_INIT.getCode());
            if (DataSourceCodeEnum.PD.getCode().equals(dataSourceCode)) {
                eventInfoDTO.setPlayer1Name(operatorName);
            }
            if (DataSourceCodeEnum.PD2.getCode().equals(dataSourceCode)) {
                eventInfoDTO.setPlayer2Name(operatorName);
            }
            eventInfoDTO.setRemark(operatorName);
            monitor.getThirdMatchInfo().add(eventInfoDTO);
            list.add(monitor);
            redisService.set(PD_FOOTBALL_EVENT_MONITOR, JSONObject.toJSON(list).toString(), REDIS_HOUR_TIME * 6);
        } else {
            List<FootballEventMonitor> monitorList = JSON.parseObject(monitorObj.toString(), new TypeReferenceChild<List<FootballEventMonitor>>() {
            });
            List<String> userNameList = monitorList.stream().map(FootballEventMonitor::getUserName).collect(Collectors.toList());
            // 当前用户不存在时，初始化用户及对应三方赛事信息
            if (!userNameList.contains(operatorName)) {
                FootballEventMonitor monitorNew = new FootballEventMonitor(operatorName, new ArrayList<>());
                // 创建事件
                MatchEventInfoDTO eventInfoDTO = MatchEventUtils.createMatchTimeEvent(data, matchTime, matchTime, currentTime, timeGo, period, linkedId);
                eventInfoDTO.setCopyLinkId(linkedId);
                eventInfoDTO.setEventCode("action_monitor");
                eventInfoDTO.setAddition3(businessEvent);
                eventInfoDTO.setAddition4(String.valueOf(TimeStatusEnum.CONTINUE.getDesc()));
                eventInfoDTO.setAddition6(String.valueOf(matchTimeInfoId));
                // addition7缓存上次比赛时间
                eventInfoDTO.setAddition7(String.valueOf(TimeStatusEnum.INIT_PERSIST.getDesc()));
                eventInfoDTO.setAddition8(String.valueOf(thirdMatchId));
                eventInfoDTO.setAddition9("online");
                eventInfoDTO.setAddition10(MatchEventMonitorEnum.ONLINE_INIT.getCode());
                if (DataSourceCodeEnum.PD.getCode().equals(dataSourceCode)) {
                    eventInfoDTO.setPlayer1Name(operatorName);
                }
                if (DataSourceCodeEnum.PD2.getCode().equals(dataSourceCode)) {
                    eventInfoDTO.setPlayer2Name(operatorName);
                }
                eventInfoDTO.setRemark(operatorName);
                monitorNew.getThirdMatchInfo().add(eventInfoDTO);
                monitorList.add(monitorNew);
            } else {
                for (FootballEventMonitor monitor : monitorList) {
                    if (!operatorName.equals(monitor.getUserName())) {
                        continue;
                    }
                    List<String> thirdMatchIds = monitor.getThirdMatchInfo().stream().map(MatchEventInfoDTO::getAddition8).collect(Collectors.toList());
                    // 当前用户存在三方赛事时，更新赛事当前系统时间
                    if (thirdMatchIds.contains(String.valueOf(thirdMatchId))) {
                        for (MatchEventInfoDTO matchEventInfoDTO : monitor.getThirdMatchInfo()) {
                            if (matchEventInfoDTO.getAddition8().equals(String.valueOf(thirdMatchId)) && operatorName.equals(matchEventInfoDTO.getRemark())) {
                                matchEventInfoDTO.setEventTime(eventCurrentTime);
                                if (pauseStatus == 1) {
                                    // 时间走表时，把addition4置为1
                                    matchEventInfoDTO.setAddition4(String.valueOf(TimeStatusEnum.CONTINUE.getDesc()));
                                }
                                if (pauseStatus == 0 || period.equals(50L)) {
                                    // 时间暂停时，把addition4置为0
                                    matchEventInfoDTO.setAddition4(String.valueOf(TimeStatusEnum.PAUSE.getDesc()));
                                }
                                matchEventInfoDTO.setAddition3(businessEvent);
                                // addition7缓存上次比赛时间
                                matchEventInfoDTO.setAddition7(String.valueOf(matchEventInfoDTO.getSecondsFromStart()));
                                matchEventInfoDTO.setSecondsFromStart(matchTime);
                                matchEventInfoDTO.setPeriodRemainingSeconds(matchTime);
                                matchEventInfoDTO.setMatchPeriodId(period);
                                matchEventInfoDTO.setMatchLength(data.getThirdMatchInfo().getMatchLength());
                                matchEventInfoDTO.setCopyLinkId(linkedId);
                                log.info("::三方赛事ID={}::PD赛事状态监控事件更新,eventCode={},赛事时间::{}", thirdMatchId, matchEventInfoDTO.getEventCode(), matchTime);
                            }
                        }
                    } else {
                        // 当前用户不存在三方赛事时，初始化三方赛事
                        MatchEventInfoDTO eventInfoDTO = MatchEventUtils.createMatchTimeEvent(data, matchTime, matchTime, currentTime, timeGo, period, linkedId);
                        eventInfoDTO.setCopyLinkId(linkedId);
                        eventInfoDTO.setEventCode("action_monitor");
                        eventInfoDTO.setAddition3(businessEvent);
                        eventInfoDTO.setAddition4(String.valueOf(TimeStatusEnum.CONTINUE.getDesc()));
                        eventInfoDTO.setAddition6(String.valueOf(matchTimeInfoId));
                        // addition7缓存上次比赛时间
                        eventInfoDTO.setAddition7(String.valueOf(TimeStatusEnum.INIT_PERSIST.getDesc()));
                        eventInfoDTO.setAddition8(String.valueOf(thirdMatchId));
                        eventInfoDTO.setAddition9("online");
                        eventInfoDTO.setAddition10(MatchEventMonitorEnum.ONLINE_INIT.getCode());
                        if (DataSourceCodeEnum.PD.getCode().equals(dataSourceCode)) {
                            eventInfoDTO.setPlayer1Name(operatorName);
                        }
                        if (DataSourceCodeEnum.PD2.getCode().equals(dataSourceCode)) {
                            eventInfoDTO.setPlayer2Name(operatorName);
                        }
                        eventInfoDTO.setRemark(operatorName);
                        monitor.getThirdMatchInfo().add(eventInfoDTO);
                    }
                }
            }
            redisService.set(PD_FOOTBALL_EVENT_MONITOR, JSONObject.toJSON(monitorList).toString(), REDIS_HOUR_TIME * 6);
        }
        return thirdMatchId;
    }

    /**
     * 获取参数Map集合
     *
     * @param joinPoint 切入点
     * @return 参数key-value
     */
    public Map<String, Object> getNameAndValue(JoinPoint joinPoint) {
        Map<String, Object> param = new HashMap<>(16);
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }
        return param;
    }

    /**
     * 登出后，清空当前用户redis三方赛事ID
     *
     * @param forceUserSignOutDto 登录信息
     */
    public void deleteUserInfoWhenLogout(ForceUserSignOutDto forceUserSignOutDto) {
        try {
            Map<Object, Object> response = iAuthRequiredPermission.forceUserSignOut(null, forceUserSignOutDto.getPdUserName(),
                    forceUserSignOutDto.getManagerId(), forceUserSignOutDto.getManagerName());
            String userName = (String) response.get("userName");
            // 登出后，清空当前用户redis三方赛事ID
            Object monitorObj = redisService.get(PD_FOOTBALL_EVENT_MONITOR);
            if (!ObjectUtils.isEmpty(monitorObj)) {
                List<FootballEventMonitor> monitorList = JSON.parseObject(monitorObj.toString(), new TypeReference<List<FootballEventMonitor>>() {
                });
                monitorList.removeIf(monitor -> userName.equals(monitor.getUserName()));
                redisService.set(PD_FOOTBALL_EVENT_MONITOR, JSONObject.toJSON(monitorList).toString(), REDIS_HOUR_TIME * 6);
            }
        } catch (SessionValidException e) {
            log.error("[FootballDashboardAdvertiseApiImpl] forceUserSignOut error: ", e);
        }
    }

    private Long getMatchTime(MatchScoreAndTimeVo data) {
        MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
        Long matchTime;
        if (SportPeriodConstant.FootballPeriod.contans(matchTimeInfo.getPeriod())) {
            matchTime = matchTimeInfo.getSecondFromStart() * 1000 + (System.currentTimeMillis() - matchTimeInfo.getEventTime());
        } else {
            matchTime = matchTimeInfo.getSecondFromStart();
        }
        return matchTime;
    }
}
