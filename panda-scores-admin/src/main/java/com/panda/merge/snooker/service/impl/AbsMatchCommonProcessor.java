package com.panda.merge.snooker.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.common.Constant;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import com.panda.merge.dto.advertise.v2.*;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.snooker.converter.EventOperationConverter;
import com.panda.merge.snooker.converter.PDOperationLogConverter;
import com.panda.merge.snooker.dto.MatchCommonLogDto;
import com.panda.merge.snooker.service.IMatchCommonProcessor;
import com.panda.merge.snooker.service.helper.MatchScoreCommonHelper;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.volleyball.dto.VolleyballV2Scores;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbsMatchCommonProcessor<T> implements IMatchCommonProcessor<T> {

    @Resource
    protected EventProducer eventProducer;
    @Resource
    protected EventOperationConverter eventOperationConverter;
    @Resource
    protected PDOperationLogConverter pdOperationLogConverter;
    @Autowired
    protected PdMatchInfoRepository pdMatchInfoRepository;
    @Autowired
    protected MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    protected RedisUtils redisUtils;
    @Resource
    protected IMatchScorePdLogService matchScorePdLogService;
    @Autowired
    protected CommonAdvertiseService commonAdvertiseService;
    @Resource
    protected MatchScoreCommonHelper matchScoreCommonHelper;
    @Autowired
    protected MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    protected MatchScoreInfoRepository matchScoreInfoRepository;

    private void updatePeriodToDb(MatchScoreAndTimeVo data, Long targetPeriodId) {
        if (data == null || targetPeriodId == null) {
            return;
        }
        long now = System.currentTimeMillis();
        try {
            MatchTimeInfo mti = data.getMatchTimeInfo();
            if (mti != null) {
                mti.setPeriod(targetPeriodId);
                mti.setModifyTime(now);
                matchTimeInfoRepository.updateByPrimaryKey(mti);
            }
            MatchScoresInfo msi = data.getMatchScoresInfo();
            if (msi != null) {
                msi.setPeriod(targetPeriodId);
                msi.setModifyTime(now);
                matchScoreInfoRepository.updateScoresInfo(msi);
            }
        } catch (Exception e) {
            log.warn("[AbsMatchCommonServiceImpl]updatePeriodToDb failed match:{} period:{} error:{}",
                    data.getThirdMatchInfo() != null ? data.getThirdMatchInfo().getId() : null,
                    targetPeriodId, e.getMessage(), e);
        }
    }

    private void fillBaseOperatorFields(AbstructAdvertiseDto from, AbstructAdvertiseDto to) {
        if (from == null || to == null) {
            return;
        }
        to.setOperatorId(from.getOperatorId());
        to.setOperatorName(from.getOperatorName());
        to.setIpAddress(from.getIpAddress());
        to.setLanguage(from.getLanguage());
    }

    @Override
    public abstract Response getCurrentMatchInfo(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto, MatchScoreAndTimeVo matchScoreAndTimeVo);

    @Override
    public abstract Response eventList(EventListV2Dto eventListDto);

    @Transactional
    @Override
    public abstract Response deleteEvent(MatchScoreAndTimeVo matchScoreAndTimeVo, DeleteEventV2Dto deleteEventDto) throws Exception;

    @Override
    public Response changeMatchLength(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchLengthV2Dto changeMatchLengthDto) {
        log.info("[AbsMatchCommonServiceImpl]changeMatchLength start linkId::{}:data:{}",changeMatchLengthDto.getLinkedId(),changeMatchLengthDto);
        //1.查询出当前赛事的赛制
        ThirdMatchInfo thirdMatchInfo = matchScoreAndTimeVo.getThirdMatchInfo();
        MatchScoresInfo matchScoresInfo = matchScoreAndTimeVo.getMatchScoresInfo();
        MatchTimeInfo timeInfo = matchScoreAndTimeVo.getMatchTimeInfo();
        if (timeInfo.getPeriod() > 0) {
            return Response.failed("开赛后不能修改赛制");
        }
        Integer oldMatchLength = timeInfo.getMatchLength();
        Integer newMatchLength = changeMatchLengthDto.getMinutes();
        if(changeMatchLengthDto.getSportId() == 1 || changeMatchLengthDto.getSportId() == 2) {
            newMatchLength = Constant.BasketBallConstant.timeMatchLenthMap.get(changeMatchLengthDto.getMinutes());
            if (newMatchLength == null) {
                return Response.failed("时间输入错误必须是 10,12,20,5");
            }
        }
        // timeinfo
        timeInfo.setMatchLength(newMatchLength);
        timeInfo.setModifyTime(System.currentTimeMillis());
        // matchScoresInfo
        matchScoresInfo.setMatchLength(newMatchLength);
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        // thirdMatchInfo
        thirdMatchInfo.setMatchPeriod("0");
        thirdMatchInfo.setMatchLength(newMatchLength);
        thirdMatchInfo.setModifyTime(System.currentTimeMillis());
        // standardMatchInfo
        StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(thirdMatchInfo.getReferenceId(), null);
        if (standardMatchInfo == null) {
            log.warn("[AbsMatchCommonServiceImpl]changeMatchLength standardMatchInfo not found for referenceId:{} linkId::{}",
                    thirdMatchInfo.getReferenceId(), changeMatchLengthDto.getLinkedId());
            return Response.failed("未找到对应的标准赛事信息");
        }
        StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
        newStandardMatchInfo.setId(standardMatchInfo.getId());
        newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
        newStandardMatchInfo.setRoundType(newMatchLength);
        pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(timeInfo, null);
        eventProducer.sendMatchStatusTopic(changeMatchLengthDto.getLinkedId(), thirdMatchInfo, thirdMatchInfo.getMatchStatus());
        MatchCommonLogDto matchCommonLogDto = pdOperationLogConverter.convertChangeMatchLengthToLog(changeMatchLengthDto, oldMatchLength+"", newMatchLength+"");
        fillBaseOperatorFields(changeMatchLengthDto, matchCommonLogDto);
        matchScorePdLogService.setMatchCommonLog(matchScoreAndTimeVo, matchCommonLogDto);
        log.info("[AbsMatchCommonServiceImpl]changeMatchLength end linkId::{}",changeMatchLengthDto.getLinkedId());
        return Response.success();
    }

    @Override
    public Response changeScore(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchScoreV2Dto changeMatchScoreV2Dto) throws Exception {
        log.info("[AbsMatchCommonServiceImpl]changeScore start linkId::{}:data:{}",changeMatchScoreV2Dto.getLinkedId(),changeMatchScoreV2Dto);
        
        // 检查比赛是否处于中断状态，如果是则禁止操作
        if (matchScoreCommonHelper.isMatchInterrupted(changeMatchScoreV2Dto.getThirdMatchId())) {
            log.warn("[AbsMatchCommonServiceImpl]changeScore match is interrupted, cannot change score match:{} linkId::{}", 
                    changeMatchScoreV2Dto.getThirdMatchId(), changeMatchScoreV2Dto.getLinkedId());
            return Response.failed("比赛已中断，不能进行比分操作");
        }
        
        // 检查赛事是否处于中断状态，如果是则禁止操作
        if (matchScoreCommonHelper.isMatchEventInterrupted(changeMatchScoreV2Dto.getThirdMatchId())) {
            log.warn("[AbsMatchCommonServiceImpl]changeScore match event is interrupted, cannot change score match:{} linkId::{}", 
                    changeMatchScoreV2Dto.getThirdMatchId(), changeMatchScoreV2Dto.getLinkedId());
            return Response.failed("赛事已中断，不能进行比分操作");
        }
        //排球过滤此校验-事件源切换PD后能直接使用
        if(!SportTypeEnum.VOLLEYBALL.getValue().equals(changeMatchScoreV2Dto.getSportId())){
            matchScoreCommonHelper.kickOffEventCheck(changeMatchScoreV2Dto.getThirdMatchId(), changeMatchScoreV2Dto.getLanguage());
        }
        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.changeMatchScoreToEvent(changeMatchScoreV2Dto);
        fillBaseOperatorFields(changeMatchScoreV2Dto, eventOperationV2Dto);

        // 日志展示用当前局比分（periodT1/periodT2），不要用事件 DTO 的 t1/t2（斯诺克事件里 t1/t2 表示局胜负统计，常为 0-0）
        MatchScoresInfo logScoresInfo = matchScoreAndTimeVo.getMatchScoresInfo();
        int logBeforeHome = logScoresInfo != null && logScoresInfo.getPeriodT1() != null ? logScoresInfo.getPeriodT1() : 0;
        int logBeforeAway = logScoresInfo != null && logScoresInfo.getPeriodT2() != null ? logScoresInfo.getPeriodT2() : 0;
        String beforeVal = logBeforeHome + "-" + logBeforeAway;

        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(matchScoreAndTimeVo, eventOperationV2Dto, 0, 0l, null);
        updateMatchScore(matchScoreAndTimeVo, matchEventInfoDTO, false);
        // 设置 secondsFromStart，用于 sendPDEventInfo 保存到数据库
        if (eventOperationV2Dto.getSecondFromStart() != null) {
            matchEventInfoDTO.setSecondsFromStart(eventOperationV2Dto.getSecondFromStart());
        }
        // 使用 sendPDEventInfo 发送事件（会保存到数据库并发送到 THIRD_MATCH_EVENT_INFO_API）
        matchEventInfoDTO.setCopyLinkId(changeMatchScoreV2Dto.getLinkedId());
        eventProducer.sendPDSnookerEventInfo(matchEventInfoDTO);
        redisUtils.pushFootBallScore(eventOperationV2Dto.getThirdMatchId());
        redisUtils.pushFootBallEvent(eventOperationV2Dto.getThirdMatchId());

        int logAfterHome = logScoresInfo != null && logScoresInfo.getPeriodT1() != null ? logScoresInfo.getPeriodT1() : 0;
        int logAfterAway = logScoresInfo != null && logScoresInfo.getPeriodT2() != null ? logScoresInfo.getPeriodT2() : 0;
        String afterVal = logAfterHome + "-" + logAfterAway;
        MatchCommonLogDto matchCommonLogDto = pdOperationLogConverter.convertChangeScoreToLog(changeMatchScoreV2Dto, beforeVal, afterVal);
        fillBaseOperatorFields(changeMatchScoreV2Dto, matchCommonLogDto);
        // 确保日志能落库：convert 未带 sportId 时从 vo 补全（否则 setMatchCommonLog 会因 sportId 为空跳过）
        if (matchCommonLogDto.getSportId() == null && matchScoreAndTimeVo.getThirdMatchInfo() != null) {
            matchCommonLogDto.setSportId(matchScoreAndTimeVo.getThirdMatchInfo().getSportId());
        }
        matchScorePdLogService.setMatchCommonLog(matchScoreAndTimeVo, matchCommonLogDto);
        log.info("[AbsMatchCommonServiceImpl]changeScore end linkId::{}",changeMatchScoreV2Dto.getLinkedId());
        return Response.success();
    }

    @Override
    public Response changeMatchStatus(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        // controller 不再按 ct 填默认 periodId，由本钩子完成（球种可覆写）；
        // ct=1 还保留下方 pid==0 的 fallback，因为前端历史上会传 0 等价于「未指定」。
        applyDefaultPeriodId(changeMatchStatusV2Dto);
        if (Integer.valueOf(1).equals(changeMatchStatusV2Dto.getControlType())) {
            Long pid = changeMatchStatusV2Dto.getPeriodId();
            if (pid == null || pid == 0L) {
                changeMatchStatusV2Dto.setPeriodId(8L);
            }
        }
        log.info("[AbsMatchCommonServiceImpl]changeMatchStatus start linkId::{}:data:{}",changeMatchStatusV2Dto.getLinkedId(),changeMatchStatusV2Dto);
        MatchInfoConvertEnum convertEnum = MatchInfoConvertEnum.getByCurCode(changeMatchStatusV2Dto.getControlType());
        if (convertEnum == null) {
            log.warn("[AbsMatchCommonServiceImpl]changeMatchStatus invalid controlType:{} match:{} linkId::{}",
                    changeMatchStatusV2Dto.getControlType(), changeMatchStatusV2Dto.getThirdMatchId(), changeMatchStatusV2Dto.getLinkedId());
            return Response.failed("controlType 不合法");
        }
        
        // 检查中断状态：如果已经是中断状态，不能再次中断
        if (convertEnum == MatchInfoConvertEnum.MATCH_INTERRUPTED) {
            if (matchScoreCommonHelper.isMatchInterrupted(changeMatchStatusV2Dto.getThirdMatchId())) {
                log.warn("[AbsMatchCommonServiceImpl]changeMatchStatus match is already interrupted, cannot interrupt again match:{} linkId::{}", 
                        changeMatchStatusV2Dto.getThirdMatchId(), changeMatchStatusV2Dto.getLinkedId());
                return Response.failed("比赛已经中断，不能重复中断");
            }
        }
        
        // 检查赛事中断状态：如果已经是赛事中断状态，不能再次中断
        if (convertEnum == MatchInfoConvertEnum.MATCH_EVENT_INTERRUPTED) {
            if (matchScoreCommonHelper.isMatchEventInterrupted(changeMatchStatusV2Dto.getThirdMatchId())) {
                log.warn("[AbsMatchCommonServiceImpl]changeMatchStatus match event is already interrupted, cannot interrupt again match:{} linkId::{}", 
                        changeMatchStatusV2Dto.getThirdMatchId(), changeMatchStatusV2Dto.getLinkedId());
                return Response.failed("赛事已经中断，不能重复中断");
            }
        }
        
        // 规则：changeMatchStatus 只做“状态事件 + 状态日志”，不混入阶段链路；阶段切换请单独调用 changeMatchPeriod
        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.convertChangeMatchStatusToEvent(changeMatchStatusV2Dto);
        fillBaseOperatorFields(changeMatchStatusV2Dto, eventOperationV2Dto);
        // 开赛（controlType=1）不仅要下发状态事件，还必须真正“开赛落地”：
        // - 写 match_time_info.period / timeGo / eventTime / secondFromStart
        // - 写 match_scores_info.period（并初始化局 scoresJson 结构）
        // - 写 Redis 当前阶段（MATCH_CURRENT_PERIOD）
        // 注意：这里不额外下发“阶段事件”，只做数据落地。
        if (Integer.valueOf(1).equals(changeMatchStatusV2Dto.getControlType())) {
            Long startPid = changeMatchStatusV2Dto.getPeriodId();
            Long curPid = (matchScoreAndTimeVo.getMatchTimeInfo() != null) ? matchScoreAndTimeVo.getMatchTimeInfo().getPeriod() : null;
            if (startPid != null && startPid > 0 && (curPid == null || curPid <= 0)) {
                Long startSecond = changeMatchStatusV2Dto.getStartTimeSecond() != null ? changeMatchStatusV2Dto.getStartTimeSecond() : 0L;
                // 让状态事件与数据落地的 secondsFromStart 一致
                eventOperationV2Dto.setSecondFromStart(startSecond);
                MatchEventInfoDTO startPeriodPatch = new MatchEventInfoDTO();
                startPeriodPatch.setMatchPeriodId(startPid);
                startPeriodPatch.setSecondsFromStart(startSecond);
                startPeriodPatch.setPeriodRemainingSeconds(0L);
                addMatchPeriod(matchScoreAndTimeVo, startPeriodPatch);
                matchScoreCommonHelper.setMatchCacheStatus(changeMatchStatusV2Dto.getThirdMatchId(), SnookerConstant.MATCH_CURRENT_PERIOD, startPid);
            }
        }
        // 状态事件的 matchPeriodId 必须以请求 periodId 为准：开赛前后 matchTimeInfo.period 常为 0，不能盖掉 DTO（如斯诺克开赛 8）
        Long periodId = changeMatchStatusV2Dto.getPeriodId();
        if (periodId == null && matchScoreAndTimeVo.getMatchTimeInfo() != null) {
            periodId = matchScoreAndTimeVo.getMatchTimeInfo().getPeriod();
        }
        if (changeMatchStatusV2Dto.getControlType() == 5) {
            periodId = 80l;
        } else if (changeMatchStatusV2Dto.getControlType() == 4) {
            periodId = resolveMatchEndPeriod(changeMatchStatusV2Dto);
            log.info("[AbsMatchCommonServiceImpl]changeMatchStatus ct=4 volleyball thirdMatchId:{},periodId:{}", changeMatchStatusV2Dto.getThirdMatchId(),periodId);
        }
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(
                matchScoreAndTimeVo, eventOperationV2Dto, 0, 0L, periodId);
        // 与 changeMatchPeriod/addMatchPeriod 一致：状态事件落库/下发时带上当前盘分与局分
        MatchScoresInfo statusScoresInfo = matchScoreAndTimeVo.getMatchScoresInfo();
        if (statusScoresInfo != null) {
            Integer pt1 = statusScoresInfo.getPeriodT1();
            Integer pt2 = statusScoresInfo.getPeriodT2();
            Integer mt1 = statusScoresInfo.getT1();
            Integer mt2 = statusScoresInfo.getT2();
            matchEventInfoDTO.setFirstT1(pt1 != null ? pt1 : 0);
            matchEventInfoDTO.setFirstT2(pt2 != null ? pt2 : 0);
            matchEventInfoDTO.setT1(mt1 != null ? mt1 : 0);
            matchEventInfoDTO.setT2(mt2 != null ? mt2 : 0);
        }
        matchEventInfoDTO.setControlType(changeMatchStatusV2Dto.getControlType());
        MatchCommonLogDto matchCommonLogDto = pdOperationLogConverter.convertChangeMatchStatusToLog(changeMatchStatusV2Dto);
        fillBaseOperatorFields(changeMatchStatusV2Dto, matchCommonLogDto);
        // 球种特化钩子：状态事件/日志落库前可改写 eventCode 等字段。默认 no-op，不影响其它球种。
        customizeStatusEvent(changeMatchStatusV2Dto, eventOperationV2Dto, matchEventInfoDTO, matchCommonLogDto);
        // 先写 Redis 中断标记与 controlType，再 commonProcess（内含 pushFootBallScore），避免推送仍读到旧状态
        matchScoreCommonHelper.procInterruptedEvent(matchScoreAndTimeVo, changeMatchStatusV2Dto, convertEnum);
        if (changeMatchStatusV2Dto.getControlType() != null) {
            matchScoreCommonHelper.setMatchCacheStatus(changeMatchStatusV2Dto.getThirdMatchId(), SnookerConstant.CONTROL_TYPE, changeMatchStatusV2Dto.getControlType());
        }

        // 状态切换时同步 period 到 DB：
        // - 赛事中断(controlType=5)：period 固定写 80，同时记录中断前的真实 period 供重开恢复
        // - 比赛结束(controlType=4)：period 固定写 100
        // - 赛事重开(controlType=6)：period 恢复为中断前记录的 period
        Integer ct = changeMatchStatusV2Dto.getControlType();
        if (ct != null) {
            if (ct == 5) {
                Long before = matchScoreAndTimeVo.getMatchTimeInfo() != null ? matchScoreAndTimeVo.getMatchTimeInfo().getPeriod() : null;
                if (before != null) {
                    matchScoreCommonHelper.setMatchCacheStatus(changeMatchStatusV2Dto.getThirdMatchId(),
                            SnookerConstant.PREVIOUS_PERIOD_BEFORE_INTERRUPT, before);
                }
                updatePeriodToDb(matchScoreAndTimeVo, 80L);
            } else if (ct == 4) {
                updatePeriodToDb(matchScoreAndTimeVo, resolveMatchEndPeriod(changeMatchStatusV2Dto));
                if(SportTypeEnum.VOLLEYBALL.getValue().equals(eventOperationV2Dto.getSportId())){
                    log.info("[AbsMatchCommonServiceImpl]changeMatchStatus ct=4 volleyball scoresJson:{}", matchScoreAndTimeVo.getMatchScoresInfo());
                    JSONObject periodVolleyballScores = JSONObject.parseObject(matchScoreAndTimeVo.getMatchScoresInfo().getScoresJson());
                    Map<Long, VolleyballV2Scores> allPeriodScores = JsonMapUtils.parseVolleyballV2Map(periodVolleyballScores);
                    log.info("[AbsMatchCommonServiceImpl]changeMatchStatus ct=4 volleyball allPeriodScores keys:{}, size:{}", allPeriodScores != null ? allPeriodScores.keySet() : "null", allPeriodScores != null ? allPeriodScores.size() : 0);
                    Long lastPeriodKey = allPeriodScores.keySet().stream().filter(k -> k != null && k > 0).max(Long::compareTo).orElse(null);
                    log.info("[AbsMatchCommonServiceImpl]changeMatchStatus ct=4 volleyball lastPeriodKey:{}", lastPeriodKey);
                    VolleyballV2Scores periodScores = allPeriodScores.get(lastPeriodKey);
                    log.info("[AbsMatchCommonServiceImpl]changeMatchStatus ct=4 volleyball periodScores:{}", periodScores);
                    MatchEventInfoDTO volleyballEventInfo = new MatchEventInfoDTO();
                    BeanUtil.copyProperties(matchEventInfoDTO,volleyballEventInfo);
                    volleyballEventInfo.setMatchPeriodId(100L);
                    Long period = matchScoreAndTimeVo.getMatchScoresInfo().getPeriod() != null ? matchScoreAndTimeVo.getMatchScoresInfo().getPeriod() : 0;
                    Long firstNum = 0L;
                    List<Long> endPeriods = new ArrayList<>(Arrays.asList(93L, 94L, 95L, 96L, 100L,999L));
                    if(endPeriods.contains(period)){
                        firstNum = getLastNum(matchScoreAndTimeVo);
                    }
                    volleyballEventInfo.setFirstNum(firstNum==null?null : Integer.valueOf(firstNum+""));
                    volleyballEventInfo.setHomeAway("all");
                    volleyballEventInfo.setT1(matchEventInfoDTO.getT1());
                    volleyballEventInfo.setT2(matchEventInfoDTO.getT2());
                    volleyballEventInfo.setFirstT1(periodScores != null && periodScores.getSetScore() != null ? periodScores.getSetScore().getHome() : null);
                    volleyballEventInfo.setFirstT2(periodScores != null && periodScores.getSetScore() != null ? periodScores.getSetScore().getAway() : null);
                    log.info("[AbsMatchCommonServiceImpl]changeMatchStatus ct=4 volleyball matchEventInfoDTO:{}", matchEventInfoDTO);
                    eventProducer.sendPDEventInfo(volleyballEventInfo);
                }
            } else if (ct == 6) {
                Long restorePid = null;
                Object cached = matchScoreCommonHelper.getMatchCacheStatusObject(changeMatchStatusV2Dto.getThirdMatchId(),
                        SnookerConstant.PREVIOUS_PERIOD_BEFORE_INTERRUPT);
                if (cached != null) {
                    try {
                        restorePid = Long.parseLong(cached.toString());
                    } catch (Exception ignore) {
                    }
                }
                if (restorePid == null) {
                    restorePid = changeMatchStatusV2Dto.getPeriodId();
                }
                if (restorePid == null && matchScoreAndTimeVo.getMatchTimeInfo() != null) {
                    restorePid = matchScoreAndTimeVo.getMatchTimeInfo().getPeriod();
                }
                if (restorePid != null) {
                    updatePeriodToDb(matchScoreAndTimeVo, restorePid);
                }
            }
        }
        // 斯诺克开赛（controlType=1）且 period 为某局「开始」阶段：需拿到落库事件 ID，与 changeMatchPeriod(SET开始) 一致写入 Redis，供重排删事件
        boolean snookerMatchStartWithSetBegin = Integer.valueOf(1).equals(changeMatchStatusV2Dto.getControlType())
                && matchScoreAndTimeVo.getThirdMatchInfo() != null
                && Long.valueOf(7L).equals(matchScoreAndTimeVo.getThirdMatchInfo().getSportId())
                && periodId != null
                && SnookerConstant.SNOOKER_SET_BEGIN.containsKey(periodId);
        if (snookerMatchStartWithSetBegin) {
            Long statusEventId = matchScoreCommonHelper.commonProcessAndReturnEventId(
                    matchScoreAndTimeVo, eventOperationV2Dto, matchEventInfoDTO, matchCommonLogDto);
            afterSnookerMatchStartStatusEvent(changeMatchStatusV2Dto.getThirdMatchId(), periodId, statusEventId, changeMatchStatusV2Dto.getControlType());
        } else {
            matchScoreCommonHelper.commonProcess(matchScoreAndTimeVo, eventOperationV2Dto, matchEventInfoDTO, matchCommonLogDto);
        }
        if (convertEnum.getMatchStatus() != null && shouldBroadcastMatchStatus(changeMatchStatusV2Dto)) {
            doBroadcastMatchStatus(matchScoreAndTimeVo, changeMatchStatusV2Dto, convertEnum);
        }
        log.info("[AbsMatchCommonServiceImpl]changeMatchStatus end linkId::{}",changeMatchStatusV2Dto.getLinkedId());
        return Response.success();
    }

    /**
     * 获取最后一局的阶段
     * @param matchScoreAndTimeVo
     * @return
     */
    private Long getLastNum(MatchScoreAndTimeVo matchScoreAndTimeVo) {
        Long firstNum = 0L;
        MatchScoresInfo matchScoresInfo = matchScoreAndTimeVo.getMatchScoresInfo();
        if(matchScoresInfo==null || matchScoresInfo.getPeriod()==null){
            return 0L;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, VolleyballScores> allPeriodScores= JsonMapUtils.parseVolleyballMap(periodFootballScores);
        Long maxKey = 0L;
        for (Map.Entry<Long, VolleyballScores> entry : allPeriodScores.entrySet()) {
            if (entry.getKey() > maxKey) {
                maxKey = entry.getKey();
            }
        }
        firstNum = SportPeriodConstant.SnookerPeriod.SnookerPeriodScores.periodMaps.getOrDefault(maxKey, null);
        log.info("{},赛事结束获取最后一局的局数:{}",matchScoreAndTimeVo.getMatchScoresInfo().getThirdMatchId(),firstNum);
        return firstNum;
    }


    /**
     * 斯诺克：比赛开始状态事件落库后回调（默认空实现）。{@link MatchSnookerServiceImpl} 覆写以写入 SNOOKER_PERIOD_BEGIN_EVENT_ID。
     */
    protected void afterSnookerMatchStartStatusEvent(Long thirdMatchId, Long periodId, Long eventId, Integer controlType) {
    }

    /**
     * 球种特化钩子：在状态事件 / PD 日志落库前对相关 DTO 做最后调整。默认 no-op，
     * 不影响其它球种。当前覆写者：{@link com.panda.merge.volleyball.service.impl.MatchVolleyballServiceImpl} —
     * 把排球 ct=2/3 的 eventCode 改为 timeout / timeout_over，事件行与 PD 日志同步对齐。
     */
    protected void customizeStatusEvent(ChangeMatchStatusV2Dto changeMatchStatusV2Dto,
                                        EventOperationV2Dto eventOperationV2Dto,
                                        MatchEventInfoDTO matchEventInfoDTO,
                                        MatchCommonLogDto matchCommonLogDto) {
    }

    /**
     * 球种特化钩子：是否把本次 changeMatchStatus 的 matchStatus 广播到 standard MQ +
     * standardMatchInfo + matchStatusTopic。默认 true。
     * <p>
     * 覆写场景：排球 ct=2/3（比赛暂停/继续）按 spec "赛事阶段不变"，不应推送公共
     * matchStatus=80（否则下游会把市场挂起 10），返回 false 跳过整段广播。
     */
    protected boolean shouldBroadcastMatchStatus(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        return true;
    }

    /**
     * 球种特化钩子：实际执行广播（standard MQ + standardMatchInfo 同步 + matchStatusTopic）。
     * 默认实现保留原有斯诺克语义；个别球种若需要更稳健的 null 防御 / 错误处理，覆写本方法。
     */
    protected void doBroadcastMatchStatus(MatchScoreAndTimeVo matchScoreAndTimeVo,
                                          ChangeMatchStatusV2Dto changeMatchStatusV2Dto,
                                          MatchInfoConvertEnum convertEnum) {
        eventProducer.sendStandardMatchStatus(matchScoreAndTimeVo.getThirdMatchInfo(), changeMatchStatusV2Dto.getLinkedId(), convertEnum.getMatchStatus());
        StandardSportMarketSell marketSell1 = pdMatchInfoRepository.getStandardSportMarketSell(matchScoreAndTimeVo.getStandardMatchInfo().getId(), null);
        if (marketSell1 != null && changeMatchStatusV2Dto.getDataSourceCode().equals(marketSell1.getBusinessEvent())) {
            StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
            newStandardMatchInfo.setId(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
            newStandardMatchInfo.setMatchStatus(convertEnum.getMatchStatus() == 80 ? 10 : convertEnum.getMatchStatus());
            newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
            pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
        }
        eventProducer.sendMatchStatusTopic(changeMatchStatusV2Dto.getLinkedId(), matchScoreAndTimeVo.getThirdMatchInfo(), convertEnum.getMatchStatus());
    }

    /**
     * 球种特化钩子：ct=4（比赛结束）时使用的 periodId。默认 100L（斯诺克语义：固定的「比赛结束」哨兵值）。
     * <p>
     * 覆写场景：排球允许前端传入 93/94/95/96/999 表示不同结束原因——
     * 见 {@link com.panda.merge.volleyball.service.impl.MatchVolleyballServiceImpl}。
     */
    private static final Set<Long> VOLLEYBALL_MATCH_END_PERIODS =
            new HashSet<>(Arrays.asList(93L, 94L, 95L, 96L, 999L));
    protected Long resolveMatchEndPeriod(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        if (Objects.equals(changeMatchStatusV2Dto.getSportId(), 9L)) {
            Long requested = changeMatchStatusV2Dto.getPeriodId();
            if (requested != null && VOLLEYBALL_MATCH_END_PERIODS.contains(requested)) {
                return requested;
            }
        }
        return 100L;
    }

    /**
     * 球种特化钩子：前端未传 periodId 时，按 controlType 兜底填充 dto.periodId。
     * 原本写在 {@code MatchScoreCommonController.changeMatchStatus} 里、按斯诺克 perid 语义 hardcode；
     * 现在挪到 service 层，让各球种可以独立决定每个 ct 的默认 period。
     * <p>
     * 默认（斯诺克语义）：
     * - ct=1（开赛）→ 8L（第一局开始）
     * - ct=2 / ct=5（比赛中断 / 赛事中断）→ 80L（哨兵 period 「中断中」）
     * - ct=4（比赛结束）→ {@link #resolveMatchEndPeriod}（默认 100L）
     * - ct=3 / ct=6 / 其它 → 不填，后续从 matchTimeInfo.period 回退
     * <p>
     * 排球覆写：ct=2 = "比赛暂停 (timeout)"，spec 要求 "赛事阶段不变"——不应被 80L 覆盖。
     */
    protected void applyDefaultPeriodId(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        if (changeMatchStatusV2Dto.getPeriodId() != null) {
            return;
        }
        Integer ct = changeMatchStatusV2Dto.getControlType();
        if (ct == null) {
            return;
        }
        switch (ct.intValue()) {
            case 1:
                changeMatchStatusV2Dto.setPeriodId(8L);
                break;
            case 2:
            case 5:
                changeMatchStatusV2Dto.setPeriodId(80L);
                break;
            case 4:
                changeMatchStatusV2Dto.setPeriodId(resolveMatchEndPeriod(changeMatchStatusV2Dto));
                break;
            default:
                // ct=3 / ct=6 等场景留 null，由 matchTimeInfo.period / 中断前缓存兜底
                break;
        }
    }

    @Override
    public Response changeMatchPeriod(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto) {
        log.info("[AbsMatchCommonServiceImpl]changeMatchPeriod start linkId::{}:data:{} restTime:{}",changeMatchPeriodV2Dto.getLinkedId(),changeMatchPeriodV2Dto, changeMatchPeriodV2Dto.getRestTime());
        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.changeMatchPeriodToEvent(changeMatchPeriodV2Dto);
        fillBaseOperatorFields(changeMatchPeriodV2Dto, eventOperationV2Dto);
        // 如果有restTime（小局休息时间），将其设置到periodRemainingSeconds中
        Long remainTime = changeMatchPeriodV2Dto.getRestTime() != null ? changeMatchPeriodV2Dto.getRestTime() : 0L;
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(matchScoreAndTimeVo, eventOperationV2Dto, 0, remainTime, changeMatchPeriodV2Dto.getPeriodId());
        addMatchPeriod(matchScoreAndTimeVo, matchEventInfoDTO);
        MatchCommonLogDto matchCommonLogDto = pdOperationLogConverter.convertChangePeriodToLog(changeMatchPeriodV2Dto);
        fillBaseOperatorFields(changeMatchPeriodV2Dto, matchCommonLogDto);
        matchEventInfoDTO.setT1(matchScoreAndTimeVo.getMatchScoresInfo().getT1());
        matchEventInfoDTO.setT2(matchScoreAndTimeVo.getMatchScoresInfo().getT2());
        matchEventInfoDTO.setFirstT1(matchScoreAndTimeVo.getMatchScoresInfo().getT1());
        matchEventInfoDTO.setFirstT2(matchScoreAndTimeVo.getMatchScoresInfo().getT2());
        matchScoreCommonHelper.commonProcess(matchScoreAndTimeVo, eventOperationV2Dto, matchEventInfoDTO, matchCommonLogDto);
        // 小局结束/阶段切换时设置 currentPeriodId，同时将 currentStriker 和 kickoffFirstClick 置为默认 0,0（80/100 除外）
        Long _pid = changeMatchPeriodV2Dto.getPeriodId();
        if (_pid == null || (!_pid.equals(80L) && !_pid.equals(100L))) {
            matchScoreCommonHelper.setMatchCacheStatus(changeMatchPeriodV2Dto.getThirdMatchId(), SnookerConstant.KICKOFF_FIRST_CLICK, 0, 0);
            matchScoreCommonHelper.setMatchCacheStatus(changeMatchPeriodV2Dto.getThirdMatchId(), SnookerConstant.CURRENT_STRIKER, 0, 0);
        }
        matchScoreCommonHelper.setMatchCacheStatus(changeMatchPeriodV2Dto.getThirdMatchId(), SnookerConstant.MATCH_CURRENT_PERIOD, changeMatchPeriodV2Dto.getPeriodId());
        log.info("[AbsMatchCommonServiceImpl]changeMatchPeriod end linkId::{}",changeMatchPeriodV2Dto.getLinkedId());
        return Response.success();
    }

    @Override
    public Response kickOff(MatchScoreAndTimeVo matchScoreAndTimeVo, KickOffV2Dto kickOffV2Dto) throws Exception {
        log.info("[AbsMatchCommonServiceImpl]kickOff start linkId::{}:data:{}",kickOffV2Dto.getLinkedId(),kickOffV2Dto);
        
        // 检查比赛是否处于中断状态，如果是则禁止操作
        if (matchScoreCommonHelper.isMatchInterrupted(kickOffV2Dto.getThirdMatchId())) {
            log.warn("[AbsMatchCommonServiceImpl]kickOff match is interrupted, cannot kick off match:{} linkId::{}", 
                    kickOffV2Dto.getThirdMatchId(), kickOffV2Dto.getLinkedId());
            return Response.failed("比赛已中断，不能进行开球操作");
        }
        
        // 检查赛事是否处于中断状态，如果是则禁止操作
        if (matchScoreCommonHelper.isMatchEventInterrupted(kickOffV2Dto.getThirdMatchId())) {
            log.warn("[AbsMatchCommonServiceImpl]kickOff match event is interrupted, cannot kick off match:{} linkId::{}", 
                    kickOffV2Dto.getThirdMatchId(), kickOffV2Dto.getLinkedId());
            return Response.failed("赛事已中断，不能进行开球操作");
        }
        
        // 先落 Redis（斯诺克 kickoffFirstClick 等）：避免 commonProcess 异常时从未写入；且后续比分/事件与缓存一致
        kickOffPostProcess(kickOffV2Dto, null);

        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.convertKickOffToEvent(kickOffV2Dto);
        // 兜底：convertKickOffToEvent 正常不应返回 null；若返回 null 会导致 createCommonMatchEvent NPE
        if (eventOperationV2Dto == null) {

            eventOperationV2Dto = EventOperationV2Dto.builder().build();
//            eventOperationV2Dto = new EventOperationV2Dto();
            eventOperationV2Dto.setSportId(kickOffV2Dto.getSportId());
            eventOperationV2Dto.setThirdMatchId(kickOffV2Dto.getThirdMatchId());
            eventOperationV2Dto.setLinkedId(kickOffV2Dto.getLinkedId());
            eventOperationV2Dto.setEventCode("which_team_serves_first");
            eventOperationV2Dto.setHomeAway(kickOffV2Dto.getWhoKickOff());
            eventOperationV2Dto.setSecondFromStart(kickOffV2Dto.getSecondFromStart() != null ? kickOffV2Dto.getSecondFromStart() : 0L);
        }
        fillBaseOperatorFields(kickOffV2Dto, eventOperationV2Dto);
        // 开球事件的 matchPeriodId 必须带前端 periodId（如 442）；传 null 会误用 matchTimeInfo.period，可能与当前局不一致
        Long kickPeriodId = kickOffV2Dto.getPeriodId();
        if (kickPeriodId == null && matchScoreAndTimeVo.getMatchTimeInfo() != null) {
            kickPeriodId = matchScoreAndTimeVo.getMatchTimeInfo().getPeriod();
        }
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(matchScoreAndTimeVo, eventOperationV2Dto, 0, 0L, kickPeriodId);
        updateMatchScore(matchScoreAndTimeVo, matchEventInfoDTO, false);
        MatchCommonLogDto matchCommonLogDto = pdOperationLogConverter.convertKickOffToLog(kickOffV2Dto);
        fillBaseOperatorFields(kickOffV2Dto, matchCommonLogDto);
        matchScoreCommonHelper.commonProcess(matchScoreAndTimeVo, eventOperationV2Dto, matchEventInfoDTO, matchCommonLogDto);
        log.info("[AbsMatchCommonServiceImpl]kickOff end linkId::{}",eventOperationV2Dto.getLinkedId());
        return Response.success();
    }

    protected void kickOffPostProcess(KickOffV2Dto kickOffV2Dto, Map<String, Object> paramObject) throws Exception {}

    public abstract void addMatchPeriod(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO);

    public abstract CommonItem updateMatchScore(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO, Boolean isDelete) throws Exception;

    @Override
    public Response saveHotkeys(HotkeysSaveDto dto) {
        log.info("[AbsMatchCommonProcessor]saveHotkeys start sportId:{} userName:{}", dto.getSportId(), dto.getUserName());
        UserKeyboardSet existing = pdMatchInfoRepository.getKeyboardByUserNameAndSportId(dto.getUserName(), dto.getSportId(), null);
        UserKeyboardSet entity = buildKeyboardEntity(dto, existing);
        if (ObjectUtil.isEmpty(existing)) {
            pdMatchInfoRepository.addKeyboardInfo(entity, null);
        } else {
            entity.setModifyTime(System.currentTimeMillis());
            pdMatchInfoRepository.setRedisAndKeyboard(entity, null);
        }
        log.info("[AbsMatchCommonProcessor]saveHotkeys end sportId:{} userName:{}", dto.getSportId(), dto.getUserName());
        return Response.success();
    }

    private UserKeyboardSet buildKeyboardEntity(HotkeysSaveDto dto, UserKeyboardSet existing) {
        UserKeyboardSet entity = new UserKeyboardSet();
        entity.setSportId(dto.getSportId());
        entity.setUserId(dto.getUserId());
        entity.setUserName(dto.getUserName());
        entity.setKeyboardInfo(dto.getKeyboardInfo());
        long now = System.currentTimeMillis();
        entity.setModifyTime(now);

        // update 时保留 create 字段，避免覆盖原始创建信息
        if (existing != null) {
            entity.setId(existing.getId());
            entity.setCreateTime(existing.getCreateTime());
            entity.setCreateUser(existing.getCreateUser());
        } else {
            entity.setCreateTime(now);
            if (dto.getOperatorName() != null) {
                entity.setCreateUser(dto.getOperatorName());
            }
        }
        if (dto.getOperatorName() != null) {
            entity.setModifyUser(dto.getOperatorName());
        }
        return entity;
    }

    @Override
    public Response getHotkeysBySportIdAndUsername(Long sportId, String userName) {
        log.info("[AbsMatchCommonServiceImpl]getHotkeysBySportIdAndUsername start sportId::{}:userName:{}",sportId,userName);
        UserKeyboardSet keyboardSet = pdMatchInfoRepository.getKeyboardByUserNameAndSportId(userName, sportId, null);
        if (ObjectUtil.isEmpty(keyboardSet)) {
            return Response.success(new UserKeyboardSet(), "用户信息不存在");
        }
        log.info("[AbsMatchCommonServiceImpl]getHotkeysBySportIdAndUsername end!");
        return Response.success(keyboardSet, ResultCode.SUCCESS.getMessage());
    }

    @Override
    public Response deleteHotkeysBySportIdAndUsername(Long sportId, String userName) {
        log.info("[AbsMatchCommonServiceImpl]deleteHotkeysBySportIdAndUsername start sportId::{}:userName:{}",sportId,userName);
        UserKeyboardSet keyboardSet = pdMatchInfoRepository.getKeyboardByUserNameAndSportId(userName,sportId,null);
        if (ObjectUtil.isEmpty(keyboardSet)) {
            return Response.failed("用户信息不存在");
        }
        int count = pdMatchInfoRepository.removeKeyboardInfo(keyboardSet.getUserName(),sportId);
        if (count == 0) {
            return Response.failed("删除失败");
        }
        log.info("[AbsMatchCommonServiceImpl]deleteHotkeysBySportIdAndUsername end!");
        return Response.success(count, ResultCode.SUCCESS.getMessage());
    }

    @Override
    public Response deleteHotkeysByUserIds(Long sportId, String userName, List<Long> userIds) {
        log.info("[AbsMatchCommonProcessor]deleteHotkeysByUserIds start sportId:{} userName:{} userIds:{}", sportId, userName, userIds);
        List<String> userIdsString = userIds.stream().map(String::valueOf).collect(Collectors.toList());
        List<UserKeyboardSet> keyboardSetList = pdMatchInfoRepository.getAllKeyboardInfoBySportId(userIdsString, sportId);
        if (CollectionUtil.isEmpty(keyboardSetList)) {
            return Response.failed("用户信息不存在");
        }
        int count = pdMatchInfoRepository.removeAllKeyboardInfoBySportId(userIdsString, sportId);
        if (count == 0) {
            return Response.failed("删除失败");
        }
        log.info("[AbsMatchCommonProcessor]deleteHotkeysByUserIds end!");
        return Response.success(count, ResultCode.SUCCESS.getMessage());
    }

    public boolean support(Long sportId) {
        return sportType().equals(sportId);
    }

    protected abstract Long sportType();
}
