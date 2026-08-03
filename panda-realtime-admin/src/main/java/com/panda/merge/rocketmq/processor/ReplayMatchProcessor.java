package com.panda.merge.rocketmq.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.*;
import com.panda.merge.dto.*;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.ThreadFactoryImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 事件,赛事状态(开赛)重播
 * @author  darwinxi
 * @since   2025年4月5日
 */
@Slf4j
@Validated
@Component
public class ReplayMatchProcessor extends BaseProcessor {
    private final ConcurrentHashMap<Long/* 重播标准赛事ID */, List<MatchEventInfoExtDTO>/* 三方赛事事件 */> replayMatchEventTable = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long/* 重播标准赛事ID */, List<ThirdMatchStatusExtDTO> /* 三方赛事状态 */> replayMatchStatusTable = new ConcurrentHashMap<>();
    private final static long MOVE_TIME = 1000 * 60 * 10;
    private final ScheduledExecutorService forwardMatchEventScheduledExecutorService = new ScheduledThreadPoolExecutor(1,new ThreadFactoryImpl(
            "RMEScheduledThread"));
    private final ScheduledExecutorService forwardMatchStatusScheduledExecutorService = new ScheduledThreadPoolExecutor(1,new ThreadFactoryImpl(
            "RMSScheduledThread"));

    @Resource(name = "ReplayMatchThreadPool")
    private ScheduledExecutorService scheduledExecutorService;
    @Resource
    private ThirdMatchInfoService thirdMatchInfoService;
    @Resource
    private StandardMatchInfoService standardMatchInfoService;
    @Resource
    private StandardSportMarketSellService standardSportMarketSellService;
    @Resource
    private MatchEventInfoService matchEventInfoService;
    @Resource
    private MatchEventInfoProcessor matchEventInfoProcessor;
    @Resource
    private ThirdMatchStatusProcessor thirdMatchStatusProcessor;
    @Resource
    private ReplayMatchService replayMatchService;
    @Resource
    public ThirdSportTypeService thirdSportTypeService;

    /**
     *  重播赛事开关（false:关，true：开）
     * */
    @NacosValue(value = "${replay.match.switch:false}", autoRefreshed = true)
    private boolean replayMatchSwitch;

    @PostConstruct
    private void init() {
        if(!replayMatchSwitch){
            log.info("事件重播未打开,无需处理！,replayMatchSwitch={}",replayMatchSwitch);
            return;
        }
        forwardMatchEventScheduledExecutorService.scheduleAtFixedRate(this::scanForwardMatchEvent,5,15, TimeUnit.SECONDS);

        forwardMatchStatusScheduledExecutorService.scheduleAtFixedRate(this::scanForwardMatchStatus,5,15, TimeUnit.SECONDS);
    }

    private void scanForwardMatchStatus() {
        if (!replayMatchSwitch) {
            return;
        }
        try {
            log.info("赛事状态重播,遍历赛事状态集合开始,replayMatchStatusTable size:{}",replayMatchStatusTable.size());
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            int forwardCount = 0;
            int removeCount = 0;
            Iterator<Map.Entry<Long, List<ThirdMatchStatusExtDTO>>> it = replayMatchStatusTable.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, List<ThirdMatchStatusExtDTO>> next = it.next();
                Iterator<ThirdMatchStatusExtDTO> itSub = next.getValue().iterator();
                while (itSub.hasNext()) {
                    ThirdMatchStatusExtDTO nextSub = itSub.next();
                    if (nextSub.getBeginTime()<System.currentTimeMillis()+MOVE_TIME) {
                        scheduleMatchStatus(nextSub);
                        itSub.remove();
                        forwardCount++;
                    }
                }
                if (next.getValue().size()==0) {
                    it.remove();
                    removeCount++;
                }
            }
            stopWatch.stop();
            log.info("赛事状态重播,遍历赛事状态集合结束,耗时{}毫秒",stopWatch.getTotalTimeMillis());
            if (stopWatch.getTotalTimeMillis()>10000L) {
                log.error("赛事状态重播,遍历赛事状态集合耗时超过period,耗时{}毫秒",stopWatch.getTotalTimeMillis());
            }
        } catch (Exception e) {
            log.error("scanForwardMatchStatus,发生异常",e);
        }
    }

    private void scanForwardMatchEvent() {
        if (!replayMatchSwitch) {
            return;
        }
        try {
            log.info("事件重播,遍历事件集合开始,replayMatchEventTable size:{}",replayMatchEventTable.size());
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            int forwardCount = 0;
            int removeCount = 0;
            Iterator<Map.Entry<Long, List<MatchEventInfoExtDTO>>> it = replayMatchEventTable.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, List<MatchEventInfoExtDTO>> next = it.next();
                Iterator<MatchEventInfoExtDTO> itSub = next.getValue().iterator();
                while (itSub.hasNext()) {
                    MatchEventInfoExtDTO nextSub = itSub.next();
                    if (nextSub.getEventTime()<System.currentTimeMillis()+MOVE_TIME) {
                        scheduleMatchEvent(nextSub);
                        itSub.remove();
                        forwardCount++;
                    }
                }
                if (next.getValue().size()==0) {
                    it.remove();
                    removeCount++;
                }
            }
            stopWatch.stop();
            log.info("事件重播,遍历事件集合结束,耗时{}毫秒",stopWatch.getTotalTimeMillis());
            if (stopWatch.getTotalTimeMillis()>10000L) {
                log.error("事件重播,遍历事件集合耗时超过period,耗时{}毫秒",stopWatch.getTotalTimeMillis());
            }
        } catch (Exception e) {
            log.error("scanForwardMatchEvent,发生异常",e);
        }
    }

    private void scheduleMatchStatus(ThirdMatchStatusExtDTO thirdMatchStatusExtDTO) {
        log.info("重播赛事添加赛事状态任务开始,{}",JSON.toJSONString(thirdMatchStatusExtDTO));
        scheduledExecutorService.schedule(() -> {
            try {
                Map<Long, String> replayMatchMap = thirdMatchStatusExtDTO.getReplayMatchMap();
                Iterator<Map.Entry<Long, String>> it = replayMatchMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Long, String> next = it.next();
                    StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(next.getKey());
                    if (standardMatchInfo == null) {
                        log.error("重播赛事状态,重播原标准赛事不存在,standardMatchId={}",next.getKey());
                        continue;
                    }
                    String uuid = UUID.fastUUID().toString().replace("-", "");
                    ThirdMatchStatusDTO thirdMatchStatusDTO = new ThirdMatchStatusDTO();
                    BeanUtils.copyProperties(thirdMatchStatusExtDTO,thirdMatchStatusDTO);
                    thirdMatchStatusDTO.setThirdMatchSourceId(next.getValue());

                    Request<ThirdMatchStatusDTO> newRequest = new Request<>();
                    newRequest.setData(thirdMatchStatusDTO);
                    newRequest.setLinkId(uuid + "_" +next.getValue() );
                    log.info("重播赛事执行下发赛事状态任务,{}",JSON.toJSONString(newRequest));
                    thirdMatchStatusProcessor.putMatchStatus(newRequest);
                }
            } catch (Exception e) {
                log.error("scheduleMatchStatus,发生异常",e);
            }
        }, thirdMatchStatusExtDTO.getBeginTime()- System.currentTimeMillis(),TimeUnit.MILLISECONDS);
        log.info("重播赛事添加赛事状态任务结束");
    }

    private void scheduleMatchEvent(MatchEventInfoExtDTO matchEventInfoExtDTO) {
        log.info("重播赛事添加事件任务开始,{}",JSON.toJSONString(matchEventInfoExtDTO));
        long delayTime = 0L;
        if (EventCodeEnum.MATCH_STATUS.code.equals(matchEventInfoExtDTO.getEventCode()) && MatchPeriodForMatchOverEnum.Ended999.value.equals(matchEventInfoExtDTO.getMatchPeriodId())) {
            delayTime = 2000L;
        }
        scheduledExecutorService.schedule(() -> {
            try {
                Map<Long,String> replayMatchMap = matchEventInfoExtDTO.getReplayMatchMap();
                Iterator<Map.Entry<Long, String>> it = replayMatchMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Long, String> next = it.next();
                    StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(next.getKey());
                    if (standardMatchInfo == null) {
                        log.error("重播赛事事件,原标准赛事不存在,standardMatchId={}",next.getKey());
                        continue;
                    }
                    StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(next.getKey());
                    if (standardSportMarketSell == null) {
                        log.error("重播赛事事件,重播原开售不存在,standardMatchId={}",next.getKey());
                        continue;
                    }
                    String businessEvent = StringUtils.isNotBlank(standardSportMarketSell.getBusinessEvent()) ? standardSportMarketSell.getBusinessEvent() : standardMatchInfo.getDataSourceCode();
//                    if (!businessEvent.equals(matchEventInfoExtDTO.getDataSourceCode())) {
//                        log.error("重播赛事事件,{}与开售数据源{}不匹配,不下发,thirdEventId={}",matchEventInfoExtDTO.getDataSourceCode(),businessEvent,matchEventInfoExtDTO.getThirdEventId());
//                        continue;
//                    }
                    MatchEventInfoDTO matchEventInfoDTO = new MatchEventInfoDTO();
                    BeanUtils.copyProperties(matchEventInfoExtDTO,matchEventInfoDTO);
                    matchEventInfoDTO.setThirdMatchSourceId(next.getValue());
                    // 调用数据处理逻辑
                    Request<MatchEventInfoDTO> request = new Request<>();
                    request.setLinkId(matchEventInfoExtDTO.getLinkId()+"_replay_"+next.getValue());
                    request.setTag(next.getValue());
                    request.setDataType(THIRD_MATCH_EVENT_INFO_API);
                    request.setData(matchEventInfoDTO);
//                    if (EventCodeEnum.MATCH_STATUS.code.equals(matchEventInfoExtDTO.getEventCode()) && MatchPeriodForMatchOverEnum.Ended999.value.equals(matchEventInfoExtDTO.getMatchPeriodId())) {
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    log.info("重播赛事执行下发赛事事件任务,{}",JSON.toJSONString(request));
                    matchEventInfoProcessor.putMatchEventInfo(request);
                    if (EventCodeEnum.MATCH_STATUS.code.equals(matchEventInfoExtDTO.getEventCode()) && MatchPeriodForMatchOverEnum.Ended999.value.equals(matchEventInfoExtDTO.getMatchPeriodId())) {
                        replayMatchService.updateReplayFinish(matchEventInfoExtDTO.getOriginalStandardMatchId());
                    }

                }
            } catch (Exception e) {
                log.error("事件重播,发生异常",e);
            }
        }, matchEventInfoExtDTO.getEventTime()- System.currentTimeMillis()+delayTime,TimeUnit.MILLISECONDS);
        log.info("重播赛事添加事件任务结束");
    }

    @ExceptionHelper
    public void processReplayMatch(@Valid Request<ReplayMatchDTO> request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ReplayMatchDTO replayMatchDTO = request.getData();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事添加任务开始,参数:{}", JSONUtil.toJsonStr(replayMatchDTO));
        ReplayStandardMatchInfo oldReplayStandardMatchInfo = replayMatchService.getReplayStandardMatchInfo(replayMatchDTO.getStandardMatchId());
        if (oldReplayStandardMatchInfo == null) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事,该赛事不是重播赛事,standardMatchId={}",replayMatchDTO.getStandardMatchId());
            return;
        }
        if (!ReplaySyncStatusEnum.SYNCED.getCode().equals(oldReplayStandardMatchInfo.getSyncStatus())) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事,同步赛事相关数据未完成,standardMatchId={}",replayMatchDTO.getStandardMatchId());
            return;
        }
        if (!ReplayStatusEnum.STOP.getCode().equals(oldReplayStandardMatchInfo.getReplayStatus())) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事,重播状态不是停止重播,standardMatchId={}",replayMatchDTO.getStandardMatchId());
//            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(replayMatchDTO.getStandardMatchId());
        if (standardMatchInfo == null) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事,标准赛事不存在,standardMatchId={}",replayMatchDTO.getStandardMatchId());
            return;
        }
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(replayMatchDTO.getStandardMatchId());
        if (CollectionUtil.isEmpty(thirdMatchInfos)) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事,三方赛事不存在,standardMatchId={}",replayMatchDTO.getStandardMatchId());
            return;
        }
        thirdMatchInfos = thirdMatchInfos.stream().filter(thirdMatchInfo -> DataSourceCodeEnum.getEventCodeList().contains(thirdMatchInfo.getDataSourceCode())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(thirdMatchInfos)) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事,含事件的三方赛事不存在,standardMatchId={}",replayMatchDTO.getStandardMatchId());
            return;
        }
        List<Long> replayStandardMatchIds = replayMatchDTO.getReplayStandardMatchIds();
        List<ThirdMatchInfo> replayThirdMatchInfos = thirdMatchInfoService.getItems(replayStandardMatchIds, null);
        if (CollectionUtil.isEmpty(replayThirdMatchInfos)) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事,复制的三方赛事不存在,standardMatchId={}",replayMatchDTO.getStandardMatchId());
            return;
        }
        Map<Long, List<ThirdMatchInfo>> replayMatchInfoMap = replayThirdMatchInfos.stream().collect(Collectors.groupingBy(ThirdMatchInfo::getReferenceId));
        List<MatchEventInfo> allMatchEventInfos = new ArrayList<>();
        List<ThirdMatchStatusExtDTO> thirdMatchStatusDTOS = new ArrayList<>();
        for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfos) {
            Map<Long, String> replayMatchMap = new HashedMap();
            replayMatchInfoMap.forEach((k, values) -> {
                values.forEach(replayThirdMatchInfo -> {
                    if (thirdMatchInfo.getDataSourceCode().equals(replayThirdMatchInfo.getDataSourceCode())) {
                        replayMatchMap.put(k,replayThirdMatchInfo.getThirdMatchSourceId());
                    }
                });
            });
            List<MatchEventInfo> matchEventInfos = matchEventInfoService.getItemByThirdMatchIdAndDataSoureCode(thirdMatchInfo.getId(),thirdMatchInfo.getDataSourceCode());
            allMatchEventInfos.addAll(matchEventInfos);

            String thirdSportId = thirdSportTypeService.getThirdSportId(thirdMatchInfo.getSportId(), thirdMatchInfo.getDataSourceCode());
            ThirdMatchStatusExtDTO thirdMatchStatusDTO = new ThirdMatchStatusExtDTO();
            BeanUtils.copyProperties(thirdMatchInfo,thirdMatchStatusDTO);
            thirdMatchStatusDTO.setMatchStatus(MatchStatusEnum.Live.value);
            thirdMatchStatusDTO.setReplayMatchMap(replayMatchMap);
            thirdMatchStatusDTO.setBeginTime(replayMatchDTO.getReplayBeginTime());
            thirdMatchStatusDTO.setSportId(Long.valueOf(thirdSportId));
            thirdMatchStatusDTOS.add(thirdMatchStatusDTO);

        }
        int matchEventCount = allMatchEventInfos.size();
        allMatchEventInfos = allMatchEventInfos.stream().filter(matchEventInfo -> matchEventInfo.getMatchPeriodId()>0).collect(Collectors.toList());
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事,事件总数:{},重播事件总数:{}",matchEventCount,allMatchEventInfos.size());
        if (CollectionUtil.isEmpty(allMatchEventInfos)) {
            log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事,事件不存在,standardMatchId={}",replayMatchDTO.getStandardMatchId());
            return;
        }
        allMatchEventInfos = allMatchEventInfos.stream().sorted(Comparator.comparingLong(MatchEventInfo::getEventTime)).collect(Collectors.toList());
        List<MatchEventInfoExtDTO> matchEventInfoExtDTOS = new ArrayList<>();
        for (MatchEventInfo matchEventInfo : allMatchEventInfos) {
            Map<Long, String> replayMatchMap = new HashedMap();
            replayMatchInfoMap.forEach((k, values) -> {
                values.forEach(replayThirdMatchInfo -> {
                    if (matchEventInfo.getDataSourceCode().equals(replayThirdMatchInfo.getDataSourceCode())) {
                        replayMatchMap.put(k,replayThirdMatchInfo.getThirdMatchSourceId());
                    }
                });
            });

            //新的时间戳计算方式： 新开赛时间戳 + （旧事件下发时间戳-旧开赛时间戳）
            long eventTimeNew = replayMatchDTO.getReplayBeginTime() + (matchEventInfo.getEventTime() - standardMatchInfo.getBeginTime());
            MatchEventInfoExtDTO matchEventInfoExtDTO = new MatchEventInfoExtDTO();
            BeanUtils.copyProperties(matchEventInfo,matchEventInfoExtDTO);
            matchEventInfoExtDTO.setEventTime(eventTimeNew);
            matchEventInfoExtDTO.setLinkId(matchEventInfo.getLinkId());
            matchEventInfoExtDTO.setReplayMatchMap(replayMatchMap);
            matchEventInfoExtDTO.setOriginalStandardMatchId(matchEventInfo.getStandardMatchId());
            matchEventInfoExtDTO.setSourceType(String.valueOf(matchEventInfo.getSourceType()));
            matchEventInfoExtDTOS.add(matchEventInfoExtDTO);
        }
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事添加任务开始,事件:{},赛事状态:{}", JSON.toJSONString(matchEventInfoExtDTOS),JSON.toJSONString(thirdMatchStatusDTOS));
        replayMatchEventTable.put(replayMatchDTO.getStandardMatchId(),matchEventInfoExtDTOS);
        replayMatchStatusTable.put(replayMatchDTO.getStandardMatchId(),thirdMatchStatusDTOS);

        ReplayStandardMatchInfo replayStandardMatchInfo = new ReplayStandardMatchInfo();
        replayStandardMatchInfo.setId(oldReplayStandardMatchInfo.getId());
        replayStandardMatchInfo.setReplayMatchCount(replayMatchDTO.getReplayStandardMatchIds().size());
//        replayStandardMatchInfo.setReplayNumber(1);
        replayStandardMatchInfo.setReplayCount(1);
        replayStandardMatchInfo.setReplayBeginTime(replayMatchDTO.getReplayBeginTime());
        replayStandardMatchInfo.setReplayStatus(ReplayStatusEnum.RUN.getCode());
        replayMatchService.updateReplayStandardMatchInfo(replayStandardMatchInfo);
        stopWatch.stop();
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ REPLAY_MATCH_SEND_BEGIN+"】【::"+request.getLinkId()+"::】重播赛事添加任务结束,耗时{}毫秒",stopWatch.getTotalTimeMillis());
    }

}
