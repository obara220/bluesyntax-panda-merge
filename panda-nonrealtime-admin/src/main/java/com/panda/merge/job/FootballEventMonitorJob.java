package com.panda.merge.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.MatchEventMonitorEnum;
import com.panda.merge.common.enums.TimeStatusEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.MessageGZIP;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.OperateMatchStatusEnum;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.panda.merge.common.enums.Constant.PD_FOOTBALL_EVENT_MONITOR;
import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;
import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_EVENT_INFO_API;

/**
 * 赛事级监控赛事执行动作，赛事未结束超过20秒无赛事事件，执行该job
 *
 * @author warren
 * @since 2024/10/22 18:40:52
 */
@Slf4j
@Component
@JobHandler(value = "footballEventMonitorJob")
public class FootballEventMonitorJob extends IJobHandler {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    /**
     * 默认20秒
     */
    Integer secondTime = 20;

    /**
     * 报球板操作事件时间Redis Key，由 EventProducer.sendPDEventInfo 写入。
     * 与 com.panda.merge.advertise.common.Constant.ACTION_MONITER_KEY 保持一致。
     */
    private static final String ACTION_MONITER_KEY = "action_monitor_key:%s";

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public ReturnT<String> execute(String param) {
        try {
            if (StringUtils.isNotBlank(param)) {
                JSONObject jsonObj = JSON.parseObject(param);
                if (null != jsonObj.getInteger("secondTime")) {
                    this.secondTime = jsonObj.getInteger("secondTime");
                }
                log.info("足球执行offline事件下发监控 start");
                footballEventMonitorJob();
                log.info("足球执行offline事件下发监控 end");
            }
        } catch (Exception e) {
            log.error("【" + "footballEventMonitorJob" + " 足球报球板在线离线执行事件】 异常,Exception:", e);
            XxlJobLogger.log("【" + "footballEventMonitorJob" + " 足球报球板在线离线执行事件】 异常,Exception:" + e.getMessage());
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 定时请求redis下发对应数据给实时再给风控
     */
    private void footballEventMonitorJob() {
        long currentTime = System.currentTimeMillis();
        String showTime = sdf.format(currentTime);
        String monitorLockKey = PD_FOOTBALL_EVENT_MONITOR + ":LOCK";
        String lockValue = IdWorker.getId() + "_FOOTBALL_EVENT_MONITOR_JOB";
        if (!redisService.tryLock(monitorLockKey, lockValue, 10, 5)) {
            log.error("获取PD_FOOTBALL_EVENT_MONITOR分布式锁失败，本次offline/online监控任务跳过，等待下一次调度");
            return;
        }
        try {
            Object monitorObj = redisService.get(PD_FOOTBALL_EVENT_MONITOR);
            StringBuilder sb = new StringBuilder();
            sb.append( showTime + ":开始执行--->");
            if (!ObjectUtils.isEmpty(monitorObj)) {
                List<FootballEventMonitor> monitorList = JSON.parseObject(monitorObj.toString(), new TypeReferenceChild<List<FootballEventMonitor>>() {});
                // 赛事Id对应系统时间超过6小时未更新或赛事时间为空，移除该赛事
                monitorList.forEach(monitor -> monitor.getThirdMatchInfo().removeIf(eventInfoDTO -> ObjectUtils.isEmpty(eventInfoDTO.getEventTime()) || (currentTime - eventInfoDTO.getEventTime()) / 1000 > REDIS_HOUR_TIME * 6));
                // 赛事Id事件对应系统时间超过20s变更在线状态，下发实时服务
                // online变为offline只下发一次
                List<String> pdList = Arrays.asList(DataSourceCodeEnum.PD.getCode(), DataSourceCodeEnum.PD2.getCode());
                monitorList.forEach(
                        monitor -> monitor.getThirdMatchInfo().forEach(eventInfoDTO -> {
                            sb.append( "原始赛事id:" + eventInfoDTO.getThirdMatchSourceId() +";链路id:" + eventInfoDTO.getCopyLinkId() + ";");
                            // 非PD数据源返回。
                            // 注意：addition3 由 AOP 写为 standard_sport_market_sell.business_event，
                            // PD 赛事可自动切到 BG/SR/KO/RB（见 DataSourceCodeEnum.getBusinessCode），
                            // 此时 addition3≠"PD" 会让 offline 永不下发，但 PD 报球板操作仍在发生。
                            // 改用 dataSourceCode 作为判断依据。
                            if (!pdList.contains(eventInfoDTO.getDataSourceCode())) {
                                sb.append( "执行结束-1;");
                                return;
                            }
                            // 两次赛事时间相同返回，避免消息重复
                            if (eventInfoDTO.getAddition7().equals(String.valueOf(eventInfoDTO.getSecondsFromStart()))) {
                                sb.append( "执行结束-3;");
                                return;
                            }
                            //赛事级关盘不下发 offline
                            if (isClosedMatch(eventInfoDTO.getCopyLinkId(), eventInfoDTO.getAddition8())) {
                                sb.append( "执行结束-4;");
                                return;
                            }
                            // 事件时间为空返回
                            if (ObjectUtils.isEmpty(eventInfoDTO.getEventTime())) {
                                sb.append( "执行结束-5;");
                                return;
                            }
                            if ((currentTime - eventInfoDTO.getEventTime()) / 1000 >= this.secondTime) {
                                // 当比赛暂停时不下发事件
                                if (!ObjectUtils.isEmpty(eventInfoDTO.getAddition4()) && String.valueOf(TimeStatusEnum.PAUSE.getDesc()).equals(eventInfoDTO.getAddition4())) {
                                    sb.append( "执行结束-6;");
                                    return;
                                }
                                if ("offline".equals(eventInfoDTO.getAddition9()) && MatchEventMonitorEnum.ONLINE_TO_OFFLINE.getCode().equals(eventInfoDTO.getAddition10())) {
                                    sb.append( "执行结束-7;");
                                    return;
                                }
                                // 20s内有PD事件下发（action_monitor_key由EventProducer写入），不应再下发offline。
                                // 防止 PD_FOOTBALL_EVENT_MONITOR 缓存中残留旧 eventTime 时误判
                                String actionMonitorKey = String.format(ACTION_MONITER_KEY, eventInfoDTO.getThirdMatchSourceId());
                                Object actionMonitorObj = redisService.get(actionMonitorKey);
                                if (!ObjectUtils.isEmpty(actionMonitorObj)) {
                                    try {
                                        long actionMonitorTime = Long.parseLong(actionMonitorObj.toString());
                                        if ((currentTime - actionMonitorTime) / 1000 < this.secondTime) {
                                            sb.append( "执行结束-7a;");
                                            log.info("::{}::20s内已有PD事件下发({}ms前)，跳过offline下发，三方赛事id:{}",
                                                    eventInfoDTO.getCopyLinkId(), currentTime - actionMonitorTime, eventInfoDTO.getThirdMatchSourceId());
                                            return;
                                        }
                                    } catch (NumberFormatException nfe) {
                                        log.error("::{}::解析action_monitor_key失败,value:{}", eventInfoDTO.getCopyLinkId(), actionMonitorObj);
                                    }
                                }
                                // 睡100ms避免消息乱序
    //                            try {
    //                                Thread.sleep(100);
    //                            } catch (InterruptedException e) {
    //                                throw new RuntimeException(e);
    //                            }
                                if ( !isParsableLong(eventInfoDTO.getAddition6()) ) {
                                    log.info("::{}::执行offline跳过id为空，matchTimeId:{}", eventInfoDTO.getCopyLinkId(), eventInfoDTO.getAddition6() );
                                    sb.append( "赛事阶段id为空;");
                                    return;
                                }
                                MatchTimeInfo matchTimeInfo = selectByPrimaryKey(Long.valueOf(eventInfoDTO.getAddition6()));
                                //88478-bug 中场休息和 加时塞休息不需要下发offline
                                List<Long> periods = Arrays.asList(31L, 33L,80L,999L);
                                if (periods.contains(matchTimeInfo.getPeriod())) {
                                    sb.append( "执行结束-8;");
                                    log.info("::{}::当前赛事属于中场休息或加时塞不需要下发offline，赛事id:{},阶段:{}",eventInfoDTO.getCopyLinkId(),eventInfoDTO.getAddition6(),matchTimeInfo.getPeriod() );
                                    return;
                                }
                                String linkedId = IdWorker.getId() + "_PD_ACTION_MONITOR";
                                eventInfoDTO.setCopyLinkId(linkedId);
                                // addition7缓存上次比赛时间
                                eventInfoDTO.setAddition7(String.valueOf(eventInfoDTO.getSecondsFromStart()));
                                eventInfoDTO.setAddition9("offline");
                                eventInfoDTO.setAddition10(MatchEventMonitorEnum.ONLINE_TO_OFFLINE.getCode());
                                Long oldEventTime = eventInfoDTO.getEventTime();
                                Long matchTime = getMatchTime(matchTimeInfo);
                                eventInfoDTO.setSecondsFromStart(matchTime);
                                eventInfoDTO.setPeriodRemainingSeconds(matchTime);
                                eventInfoDTO.setMatchPeriodId(matchTimeInfo.getPeriod());
                                Request<MatchEventInfoDTO> reqMessage = new Request<>();
                                MatchEventInfoDTO sendEventInfoDTO = new MatchEventInfoDTO();
                                BeanUtils.copyProperties(eventInfoDTO, sendEventInfoDTO);
                                sendEventInfoDTO.setEventTime(sendEventInfoDTO.getEventTime() + this.secondTime * 1000);
                                sendEventInfoDTO.setSecondsFromStart(sendEventInfoDTO.getSecondsFromStart() / 1000);
                                sendEventInfoDTO.setPeriodRemainingSeconds(sendEventInfoDTO.getPeriodRemainingSeconds() / 1000);
                                reqMessage.setLinkId(sendEventInfoDTO.getCopyLinkId());
                                reqMessage.setData(sendEventInfoDTO);
                                MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                                        .setHeader(MessageConst.PROPERTY_KEYS, sendEventInfoDTO.getCopyLinkId());
                                rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + sendEventInfoDTO.getCopyLinkId(), builder.build());
                                log.info("::{}::开始组装赛事状态监控事件并下发离线,topic:THIRD_MATCH_EVENT_INFO_API", sendEventInfoDTO.getCopyLinkId());
                                eventInfoDTO.setEventTime(oldEventTime);
                                eventInfoDTO.setSecondsFromStart(matchTime);
                                eventInfoDTO.setPeriodRemainingSeconds(matchTime);
                                redisService.set(PD_FOOTBALL_EVENT_MONITOR, JSONObject.toJSON(monitorList).toString(), REDIS_HOUR_TIME * 6);
                                // offline后保存当前事件时间
                                String currentEventTimeKey = "current_event_time_key:" + eventInfoDTO.getThirdMatchSourceId();
                                redisService.set(currentEventTimeKey, oldEventTime);
                            }
                        })
                );
                log.info("足球执行offline事件下发监控 {}",monitorList);
                // offline变为online只下发一次
                monitorList.forEach(
                        monitor -> monitor.getThirdMatchInfo().forEach(eventInfoDTO -> {
                            sb.append( "第二阶段原始赛事id:" + eventInfoDTO.getThirdMatchSourceId() +";链路id:" + eventInfoDTO.getCopyLinkId() + ";");
                            // 非PD数据源返回（同上：addition3 是 business_event，会因自动切源失真，改用 dataSourceCode）
                            if (!pdList.contains(eventInfoDTO.getDataSourceCode())) {
                                sb.append( "执行结束-9;");
                                return;
                            }
                            // 两次赛事时间相同返回，避免消息重复
                            if (eventInfoDTO.getAddition7().equals(String.valueOf(eventInfoDTO.getSecondsFromStart()))) {
                                sb.append( "执行结束-11;");
                                return;
                            }
                            //赛事级关盘不下发 offline
                            if (isClosedMatch(eventInfoDTO.getCopyLinkId(),eventInfoDTO.getAddition8())) {
                                sb.append( "执行结束-12;");
                                return;
                            }
                            // 事件时间为空返回
                            if (ObjectUtils.isEmpty(eventInfoDTO.getEventTime())) {
                                sb.append( "执行结束-13;");
                                return;
                            }
                            log.info("足球执行offline事件下发监控 {}",eventInfoDTO.getThirdMatchSourceId());
                            if ((currentTime - eventInfoDTO.getEventTime()) / 1000 < this.secondTime) {
                                if ("online".equals(eventInfoDTO.getAddition9()) && MatchEventMonitorEnum.onlineStatus(eventInfoDTO.getAddition10())) {
                                    sb.append( "执行结束-14;");
                                    return;
                                }
                                // offline后无事件返回
                                String currentEventTimeKey = "current_event_time_key:" + eventInfoDTO.getThirdMatchSourceId();
                                Object o = redisService.get(currentEventTimeKey);
                                if (!ObjectUtils.isEmpty(o) && eventInfoDTO.getEventTime().equals(Long.valueOf(o.toString()))) {
                                    sb.append( "执行结束-15;");
                                    return;
                                }
                                // 睡100ms避免消息乱序
    //                            try {
    //                                Thread.sleep(100);
    //                            } catch (InterruptedException e) {
    //                                throw new RuntimeException(e);
    //                            }

                                if ( !isParsableLong(eventInfoDTO.getAddition6()) ) {
                                    log.info("::{}::执行offline跳过id为空，matchTimeId:{}", eventInfoDTO.getCopyLinkId(), eventInfoDTO.getAddition6() );
                                    sb.append( "赛事阶段id为空;");
                                    return;
                                }
                                //88478-bug 中场休息和 加时塞休息不需要下发offline
                                MatchTimeInfo matchTimeInfo = selectByPrimaryKey(Long.valueOf(eventInfoDTO.getAddition6()));
                                List<Long> periods = Arrays.asList(31L, 33L,80L,999L);
                                if (periods.contains(matchTimeInfo.getPeriod())) {
                                    sb.append( "执行结束-15;");
                                    log.info("::{}::当前赛事属于中场休息或加时塞不需要下发online，赛事id:{},阶段:{}",eventInfoDTO.getCopyLinkId(),eventInfoDTO.getAddition6(),matchTimeInfo.getPeriod() );
                                    return;
                                }
                                String linkedId = IdWorker.getId() + "_PD_ACTION_MONITOR";
                                eventInfoDTO.setCopyLinkId(linkedId);
                                // addition7缓存上次比赛时间
                                eventInfoDTO.setAddition7(String.valueOf(eventInfoDTO.getSecondsFromStart()));
                                eventInfoDTO.setAddition9("online");
                                eventInfoDTO.setAddition10(MatchEventMonitorEnum.OFFLINE_TO_ONLINE.getCode());
                                Long oldEventTime = eventInfoDTO.getEventTime();
                                Long oldMatchTime = eventInfoDTO.getSecondsFromStart();
                                Long matchTime = getMatchTime(matchTimeInfo);
                                eventInfoDTO.setSecondsFromStart(matchTime);
                                eventInfoDTO.setPeriodRemainingSeconds(matchTime);
                                eventInfoDTO.setMatchPeriodId(matchTimeInfo.getPeriod());
                                Request<MatchEventInfoDTO> reqMessage = new Request<>();
                                MatchEventInfoDTO sendEventInfoDTO = new MatchEventInfoDTO();
                                BeanUtils.copyProperties(eventInfoDTO, sendEventInfoDTO);
                                sendEventInfoDTO.setEventTime(sendEventInfoDTO.getEventTime() + 1);
                                sendEventInfoDTO.setSecondsFromStart(oldMatchTime / 1000);
                                sendEventInfoDTO.setPeriodRemainingSeconds(oldMatchTime / 1000);
                                reqMessage.setLinkId(sendEventInfoDTO.getCopyLinkId());
                                reqMessage.setData(sendEventInfoDTO);
                                MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(reqMessage)
                                        .setHeader(MessageConst.PROPERTY_KEYS, sendEventInfoDTO.getCopyLinkId());
                                rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + sendEventInfoDTO.getCopyLinkId(), builder.build());
                                log.info("::{}::足球执行offline事件下发监控，开始组装赛事状态监控事件并下发上线,topic:THIRD_MATCH_EVENT_INFO_API", sendEventInfoDTO.getCopyLinkId());
                                eventInfoDTO.setEventTime(oldEventTime);
                                eventInfoDTO.setSecondsFromStart(matchTime);
                                eventInfoDTO.setPeriodRemainingSeconds(matchTime);
                                redisService.set(PD_FOOTBALL_EVENT_MONITOR, JSONObject.toJSON(monitorList).toString(), REDIS_HOUR_TIME * 6);
                            }
                        })
                );
            } else {
                sb.append("缓存中无数据");
            }
            log.info( "footballEventMonitorJob执行日志:{}", sb.toString());
        } finally {
            redisService.unLock(monitorLockKey, lockValue);
        }
    }

    /**
     * 是否赛事级关盘
     * @param thirdMatchId
     * @return
     */
    private boolean isClosedMatch(String linkId,String thirdMatchId){

        try {
            long matchId = Long.valueOf(thirdMatchId);
            ThirdMatchInfo thirdMatchInfo= thirdMatchInfoService.getItem(matchId);
            if (Objects.isNull(thirdMatchInfo)) {
                log.info("::{}::当前三方赛事无法获取,三方赛事id:{}",linkId,thirdMatchId);
                return false;
            }
           StandardMatchInfo standardMatchInfo= standardMatchInfoService.getItem(thirdMatchInfo.getReferenceId());
            if (Objects.isNull(standardMatchInfo)) {
                log.info("::{}::当前三方赛事无法获取到标准赛事信息,三方赛事id:{},标准赛事id:{}",linkId,thirdMatchId,thirdMatchInfo.getReferenceId());
                return false;
            }
            if (standardMatchInfo.getOperateMatchStatus().compareTo(OperateMatchStatusEnum.CLOSED.getValue())!=0) {
                log.info("::{}::当前赛事不属于关盘状态,三方赛事id:{},状态值:{}",linkId,thirdMatchId,standardMatchInfo.getOperateMatchStatus());
                return false;
            }
            log.info("::{}::当前赛事属于关盘状态:{}",linkId,thirdMatchId);
            return true;

        } catch (Exception exception) {
               log.error("::{}::判断赛事关盘状态时发生异常，三方赛事id:{}",linkId,thirdMatchId,exception);
        }
        return false;

    }

    /**
     * 获取当前时间之前时间戳
     *
     * @param days 当前时间前N天
     * @param now  当前时间戳
     * @return 当前时间前N天时间戳
     */
    public long plusDays(Integer days, LocalDateTime now) {
        long time = 0;
        try {
            LocalDateTime beforeDays = now.plusDays(days);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String beforeDaysFormat = beforeDays.format(dateTimeFormatter);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            time = sdf.parse(beforeDaysFormat).getTime();
        } catch (Exception e) {
            log.error("::足球定时发送时间转换异常:" + e);
        }
        return time;
    }

    private Long getMatchTime(MatchTimeInfo matchTimeInfo) {
        List<Long> periods = Arrays.asList(6L, 7L, 41L, 42L, 50L);
        Long matchTime;
        if (periods.contains(matchTimeInfo.getPeriod())) {
            matchTime = matchTimeInfo.getSecondFromStart() * 1000 + (System.currentTimeMillis() - matchTimeInfo.getEventTime());
        } else {
            matchTime = matchTimeInfo.getSecondFromStart();
        }
        return matchTime;
    }

    public MatchTimeInfo selectByPrimaryKey(Long id) {
        MatchTimeInfo matchTimeInfo = null;
        try {
            String key = "REPOSITORY:MATCH_TIME_INFO:" + id;
            Object o = redisService.get(key);
            if (o != null) {
                if (o instanceof MatchTimeInfo) {
                    matchTimeInfo = JSON.parseObject(JSON.toJSONString(o), MatchTimeInfo.class);
                } else {
                    String str = MessageGZIP.uncompressToString((byte[]) o);
                    matchTimeInfo = JSONObject.toJavaObject(JSONObject.parseObject(str), MatchTimeInfo.class);
                }
                return matchTimeInfo;
            }
            matchTimeInfo = matchTimeInfoMapper.selectByPrimaryKey(id);
            if (matchTimeInfo != null) {
                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchTimeInfo);
                redisService.set(key, MessageGZIP.compressToByte(jsonObject.toJSONString()));
            }
        } catch (Exception e) {
            log.error("查询赛事时间异常：", e);
        }
        return matchTimeInfo;
    }


    public static boolean isParsableLong(String str) {
        if ( str == null || str.trim().isEmpty() ) {
            return false;
        }

        try {
            Long.parseLong(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
