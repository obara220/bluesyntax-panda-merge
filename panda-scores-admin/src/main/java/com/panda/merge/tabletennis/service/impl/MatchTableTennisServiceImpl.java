package com.panda.merge.tabletennis.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.PDFootBallEventDto;
import com.panda.merge.dto.advertise.v2.*;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresEventInfoExample;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.calculation.impl.TableTennisCalculationServiceImpl;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.snooker.dto.MatchCommonLogDto;
import com.panda.merge.snooker.service.impl.AbsMatchCommonProcessor;
import com.panda.merge.tabletennis.converter.TableTennisPDOperationLogConverter;
import com.panda.merge.tabletennis.converter.TableTennisScoreListConverter;
import com.panda.merge.tabletennis.dto.TableTennisV2Scores;
import com.panda.merge.tabletennis.service.MatchTableTennisService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 乒乓球报球板处理器：sportId = 8。
 * <p>
 * 通过继承 {@link AbsMatchCommonProcessor} 接管 MatchFactory 分发到 8 号球种的所有公共动作
 * （kickOff / changeScore / changeMatchPeriod / changeMatchStatus / deleteEvent / eventList / 热键）。
 * 乒乓球独有的 scoreList / batchEditScores 通过 {@link MatchTableTennisService} 暴露给 Dubbo 控制器。
 * <p>
 * 赛制差异（与排球对比）：
 * - 局分获胜条件：至少一方 ≥ 11 分且分差 ≥ 2（Volleyball 为 25/15）
 * - 无黄金局（Volleyball 有 GOLDEN_SET_BEGIN = 442L）
 * - 发球规则：正常模式每人连发 2 分（计数 0/2→2/2 切换），加速模式每人连发 1 分
 * - 红牌：自动为对方 +1 分（opposite=true）
 * - 黄红牌同手：确认后比赛直接进入 999 结束状态
 */
@Service
@Slf4j
public class MatchTableTennisServiceImpl extends AbsMatchCommonProcessor<Object> implements MatchTableTennisService {

    @Resource
    private MatchScoreInfoRepository matchScoreInfoRepository;
    @Resource
    private TableTennisScoreListConverter tableTennisScoreListConverter;
    @Resource
    private TableTennisPDOperationLogConverter tableTennisPdOperationLogConverter;
    @Resource
    private TableTennisCalculationServiceImpl tableTennisCalculationServiceImpl;
    @Resource
    private ScoresProducer scoresProducer;

    @Override
    protected Long sportType() {
        return TableTennisConstant.SPORT_ID;
    }

    // ---------------------------------------------------------------- kickOff

    /**
     * 乒乓球「发球」按钮：
     * - 当局首次点击 → 发 which_team_serves_first（kickoff++）；
     * - 每次点击 → 发 current_serve_tabletennis（serve++），并更新当前发球方 Redis。
     * - 计数到发球上限后切换发球方（由前端根据 Redis 状态判断）。
     */
    @Override
    public Response kickOff(MatchScoreAndTimeVo matchScoreAndTimeVo, KickOffV2Dto kickOffV2Dto) throws Exception {
        log.info("[MatchTableTennisServiceImpl]kickOff start linkId::{} whoKickOff:{}",
                kickOffV2Dto.getLinkedId(), kickOffV2Dto.getWhoKickOff());

        if (matchScoreCommonHelper.isMatchInterrupted(kickOffV2Dto.getThirdMatchId())) {
            return Response.failed("比赛已中断，不能进行发球操作");
        }
        if (matchScoreCommonHelper.isMatchEventInterrupted(kickOffV2Dto.getThirdMatchId())) {
            return Response.failed("赛事已中断，不能进行发球操作");
        }

        String who = StringUtils.trimToEmpty(kickOffV2Dto.getWhoKickOff());
        if (!TeamTypeConstant.HOME.equalsIgnoreCase(who) && !TeamTypeConstant.AWAY.equalsIgnoreCase(who)) {
            return Response.failed("请选择发球方 home 或 away");
        }
        String normalized = TeamTypeConstant.HOME.equalsIgnoreCase(who) ? TeamTypeConstant.HOME : TeamTypeConstant.AWAY;
        kickOffV2Dto.setWhoKickOff(normalized);

        ensureMatchInFirstSet(matchScoreAndTimeVo, kickOffV2Dto);

        boolean firstClickInSet = isFirstServeOfSet(kickOffV2Dto.getThirdMatchId());

        if (firstClickInSet) {
            int home = TeamTypeConstant.HOME.equals(normalized) ? 1 : 0;
            int away = TeamTypeConstant.AWAY.equals(normalized) ? 1 : 0;
            matchScoreCommonHelper.setMatchCacheStatus(kickOffV2Dto.getThirdMatchId(),
                    TableTennisConstant.KICKOFF_FIRST_CLICK, home, away);
            emitTableTennisEvent(matchScoreAndTimeVo, kickOffV2Dto,
                    TableTennisEventTypeEnum.KICK_OFF.getEventCode(), normalized);
        }

        emitTableTennisEvent(matchScoreAndTimeVo, kickOffV2Dto,
                TableTennisEventTypeEnum.CURRENT_SERVE_TABLE_TENNIS.getEventCode(), normalized);
        int home = TeamTypeConstant.HOME.equals(normalized) ? 1 : 0;
        int away = TeamTypeConstant.AWAY.equals(normalized) ? 1 : 0;
        matchScoreCommonHelper.setMatchCacheStatus(kickOffV2Dto.getThirdMatchId(),
                TableTennisConstant.TABLE_TENNIS_CURRENT_SERVER, home, away);

        log.info("[MatchTableTennisServiceImpl]kickOff end linkId::{} firstClick:{} side:{}",
                kickOffV2Dto.getLinkedId(), firstClickInSet, normalized);
        return Response.success();
    }

    private void emitTableTennisEvent(MatchScoreAndTimeVo data, KickOffV2Dto kickOffV2Dto,
                                       String eventCode, String homeAway) throws Exception {
        EventOperationV2Dto operationDto = EventOperationV2Dto.builder()
                .sportId(kickOffV2Dto.getSportId())
                .thirdMatchId(kickOffV2Dto.getThirdMatchId())
                .eventCode(eventCode)
                .homeAway(homeAway)
                .secondFromStart(kickOffV2Dto.getSecondFromStart() != null ? kickOffV2Dto.getSecondFromStart() : 0L)
                .build();
        operationDto.setLinkedId(kickOffV2Dto.getLinkedId());
        operationDto.setOperatorId(kickOffV2Dto.getOperatorId());
        operationDto.setOperatorName(kickOffV2Dto.getOperatorName());
        operationDto.setIpAddress(kickOffV2Dto.getIpAddress());
        operationDto.setLanguage(kickOffV2Dto.getLanguage());

        Long periodId = kickOffV2Dto.getPeriodId();
        if (periodId == null && data.getMatchTimeInfo() != null) {
            periodId = data.getMatchTimeInfo().getPeriod();
        }
        MatchEventInfoDTO eventInfo = MatchEventUtils.createCommonMatchEvent(data, operationDto, 0, 0L, periodId);
        eventInfo.setCopyLinkId(kickOffV2Dto.getLinkedId());

        updateMatchScore(data, eventInfo, false);

        MatchCommonLogDto logDto = new MatchCommonLogDto();
        logDto.setSportId(kickOffV2Dto.getSportId());
        logDto.setThirdMatchId(kickOffV2Dto.getThirdMatchId());
        logDto.setEventCode(eventCode);
        logDto.setHowAway(homeAway);
        logDto.setLinkedId(kickOffV2Dto.getLinkedId());
        logDto.setOperatorId(kickOffV2Dto.getOperatorId());
        logDto.setOperatorName(kickOffV2Dto.getOperatorName());
        logDto.setIpAddress(kickOffV2Dto.getIpAddress());
        logDto.setLanguage(kickOffV2Dto.getLanguage());
        logDto.setAfterVal(eventCode + " - " + homeAway);
        matchScoreCommonHelper.commonProcess(data, operationDto, eventInfo, logDto);
    }

    private void ensureMatchInFirstSet(MatchScoreAndTimeVo data, KickOffV2Dto dto) {
        if (data == null || data.getMatchTimeInfo() == null || data.getMatchScoresInfo() == null) {
            return;
        }
        Long currentPeriod = data.getMatchTimeInfo().getPeriod();
        if (currentPeriod != null && currentPeriod != 0L) {
            return;
        }
        Long firstSet = 8L;
        long now = System.currentTimeMillis();
        data.getMatchTimeInfo().setPeriod(firstSet);
        data.getMatchTimeInfo().setTimeGo(1);
        data.getMatchTimeInfo().setEventTime(now);
        data.getMatchTimeInfo().setModifyTime(now);
        data.getMatchScoresInfo().setPeriod(firstSet);
        if (data.getMatchScoresInfo().getPeriodT1() == null) {
            data.getMatchScoresInfo().setPeriodT1(0);
        }
        if (data.getMatchScoresInfo().getPeriodT2() == null) {
            data.getMatchScoresInfo().setPeriodT2(0);
        }
        data.getMatchScoresInfo().setModifyTime(now);
        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(data.getMatchTimeInfo(), null);
        matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(),
                TableTennisConstant.MATCH_CURRENT_PERIOD, firstSet);
        log.info("[MatchTableTennisServiceImpl]ensureMatchInFirstSet auto-init match:{} period:8 linkId::{}",
                dto.getThirdMatchId(), dto.getLinkedId());
    }

    private boolean isFirstServeOfSet(Long thirdMatchId) {
        CommonItem item = matchScoreCommonHelper.getMatchCacheStatus(thirdMatchId,
                TableTennisConstant.KICKOFF_FIRST_CLICK);
        if (item == null) {
            return true;
        }
        Integer home = item.getHome();
        Integer away = item.getAway();
        return (home == null || home == 0) && (away == null || away == 0);
    }

    // ---------------------------------------------------------------- common processor hooks

    @Override
    public Response getCurrentMatchInfo(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto,
                                         MatchScoreAndTimeVo matchScoreAndTimeVo) {
        log.info("[MatchTableTennisServiceImpl]getCurrentMatchInfo start linkId::{}",
                changeMatchPeriodV2Dto.getLinkedId());
        if (matchScoreAndTimeVo == null) {
            return Response.failed("matchScoreAndTimeVo 为空，三方赛事不存在或尚未初始化");
        }
        MatchScoresInfo info = matchScoreAndTimeVo.getMatchScoresInfo();
        Map<Long, TableTennisV2Scores> all = parseScoresJson(info != null ? info.getScoresJson() : null);

        Long requestPeriod = changeMatchPeriodV2Dto.getPeriodId();
        Long lookupPeriod = requestPeriod != null && requestPeriod != 0
                ? requestPeriod
                : (info != null && info.getPeriod() != null && info.getPeriod() != 0 ? info.getPeriod() : 0L);
        TableTennisV2Scores periodScores = all.getOrDefault(lookupPeriod, new TableTennisV2Scores());
        TableTennisV2Scores wholeScores = all.getOrDefault(WHOLE_MATCH, new TableTennisV2Scores());

        PDTableTennisEventDto eventDto = tableTennisScoreListConverter.toPdEventDto(
                periodScores, lookupPeriod != null ? lookupPeriod.intValue() : null,
                String.valueOf(changeMatchPeriodV2Dto.getThirdMatchId()), changeMatchPeriodV2Dto.getSportId());

        com.panda.merge.cache.CommonItem matchScoreItem = new com.panda.merge.cache.CommonItem();
        if (wholeScores.getMatchScore() != null) {
            matchScoreItem.setHome(wholeScores.getMatchScore().getHome());
            matchScoreItem.setAway(wholeScores.getMatchScore().getAway());
        }
        eventDto.setMatchScore(matchScoreItem);

        Map<String, Object> matchStatus = matchScoreCommonHelper.getMatchCacheStatus(changeMatchPeriodV2Dto.getThirdMatchId());
        Object controlTypeRaw = matchStatus.get(TableTennisConstant.CONTROL_TYPE);
        if (controlTypeRaw != null) {
            try {
                eventDto.setControlType(Integer.valueOf(String.valueOf(controlTypeRaw)));
            } catch (NumberFormatException nfe) {
                log.error("[MatchTableTennisServiceImpl]getCurrentMatchInfo controlType not numeric linkId::{} raw:{}",
                        changeMatchPeriodV2Dto.getLinkedId(), controlTypeRaw);
            }
        }

        Long dbPeriod = null;
        if (matchScoreAndTimeVo.getMatchTimeInfo() != null
                && matchScoreAndTimeVo.getMatchTimeInfo().getPeriod() != null
                && matchScoreAndTimeVo.getMatchTimeInfo().getPeriod() != 0L) {
            dbPeriod = matchScoreAndTimeVo.getMatchTimeInfo().getPeriod();
        } else if (info != null && info.getPeriod() != null && info.getPeriod() != 0L) {
            dbPeriod = info.getPeriod();
        }
        if (dbPeriod != null) {
            matchStatus.put(TableTennisConstant.MATCH_CURRENT_PERIOD, dbPeriod);
        } else {
            Object cachedPeriod = matchStatus.get(TableTennisConstant.MATCH_CURRENT_PERIOD);
            if (cachedPeriod != null && !isZero(cachedPeriod)) {
                matchStatus.put(TableTennisConstant.MATCH_CURRENT_PERIOD, cachedPeriod);
            } else {
                matchStatus.put(TableTennisConstant.MATCH_CURRENT_PERIOD, 0L);
            }
        }

        Integer matchLength = null;
        MatchTimeInfo timeInfo = matchScoreAndTimeVo.getMatchTimeInfo();
        if (timeInfo != null) {
            matchLength = timeInfo.getMatchLength();
        }
        if ((matchLength == null || matchLength == 0) && info != null) {
            Integer mlScores = info.getMatchLength();
            if (mlScores != null && mlScores > 0) {
                matchLength = mlScores;
            }
        }
        if (matchLength == null || matchLength == 0) {
            StandardMatchInfo std = matchScoreAndTimeVo.getStandardMatchInfo();
            if (std != null) {
                matchLength = std.getRoundType();
            }
        }
        if (matchLength != null) {
            matchStatus.put("matchLength", matchLength);
        }
        matchStatus.putIfAbsent(TableTennisConstant.CONTROL_TYPE, 1);
        matchStatus.put(TableTennisConstant.MATCH_INTERRUPTED, toBool(matchStatus.get(TableTennisConstant.MATCH_INTERRUPTED)));
        matchStatus.put(TableTennisConstant.MATCH_EVENT_INTERRUPTED, toBool(matchStatus.get(TableTennisConstant.MATCH_EVENT_INTERRUPTED)));

//        // 加速模式状态 ps:加速模式已移除
//        Long currentPeriodId = lookupPeriod;
//        if (currentPeriodId != null && TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(currentPeriodId)) {
//            Object expedite = matchStatus.get("expediteMode");
//            if (expedite == null) {
//                // 从 scoresJson 中读取当前局的 expediteMode 字段
//                TableTennisV2Scores ps = all.get(currentPeriodId);
//                boolean hasExpedite = ps != null && ps.getExpediteMode() != null
//                        && (ps.getExpediteMode().getHome() != null && ps.getExpediteMode().getHome() > 0
//                        || ps.getExpediteMode().getAway() != null && ps.getExpediteMode().getAway() > 0);
//                matchStatus.put("expediteMode", hasExpedite);
//            }
//        }

        PDTableTennisCurMatchInfoDto resp = PDTableTennisCurMatchInfoDto.builder()
                .pdTableTennisEventDto(eventDto)
                .matchStatus(matchStatus)
                .build();
        log.info("[MatchTableTennisServiceImpl]getCurrentMatchInfo end linkId::{}", changeMatchPeriodV2Dto.getLinkedId());
        return Response.success(resp);
    }

    @Override
    public Response eventList(EventListV2Dto eventListDto) {
        log.info("[MatchTableTennisServiceImpl]eventList start linkId::{}", eventListDto.getLinkedId());
        if (eventListDto.getThirdMatchId() == null) {
            return Response.failed("thirdMatchId 不能为空");
        }
        MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
        MatchScoresEventInfoExample.Criteria criteria = example.createCriteria()
                .andThirdMatchIdEqualTo(eventListDto.getThirdMatchId());
        if (!CollectionUtils.isEmpty(eventListDto.getSetNums()) && !eventListDto.getSetNums().contains(-1L)) {
            criteria.andMatchPeriodIdIn(eventListDto.getSetNums());
        }
        example.setOrderByClause("id desc");
        List<MatchScoresEventInfo> list = matchScoresEventInfoMapper.selectByExample(example);

        List<PDFootBallEventDto> result = new ArrayList<>(list.size());
        for (MatchScoresEventInfo info : list) {
            PDFootBallEventDto eventDto = new PDFootBallEventDto();
            BeanUtils.copyProperties(info, eventDto);
            eventDto.setId(String.valueOf(info.getId()));
            eventDto.setExtraInfo(info.getExtraInfo());
            eventDto.setT1(info.getFirstT1());
            eventDto.setT2(info.getFirstT2());
            result.add(eventDto);
        }
        log.info("[MatchTableTennisServiceImpl]eventList end linkId::{} size:{}", eventListDto.getLinkedId(), result.size());
        return Response.success(result);
    }

    @Override
    public Response deleteEvent(MatchScoreAndTimeVo data, DeleteEventV2Dto deleteEventDto) throws Exception {
        log.info("[MatchTableTennisServiceImpl]deleteEvent start linkId::{} thirdMatchId:{} deleteEventId:{}",
                deleteEventDto.getLinkedId(), deleteEventDto.getThirdMatchId(),
                deleteEventDto.getDeleteEventId());
        MatchScoresEventInfo oldEvent = matchScoresEventInfoMapper.selectByPrimaryKey(deleteEventDto.getDeleteEventId());
        if (oldEvent == null) {
            return Response.failed("事件不存在，无法删除");
        }
        log.info("[MatchTableTennisServiceImpl]deleteEvent found event linkId::{} thirdMatchId:{} eventCode:{} homeAway:{}",
                deleteEventDto.getLinkedId(), deleteEventDto.getThirdMatchId(),
                oldEvent.getEventCode(), oldEvent.getHomeAway());
        oldEvent.setAddition10("1");
        oldEvent.setCanceled(1);
        oldEvent.setModifyTime(System.currentTimeMillis());

        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.changeDelEventToEvent(
                deleteEventDto, oldEvent.getEventCode(), oldEvent.getHomeAway());
        eventOperationV2Dto.setOperatorId(deleteEventDto.getOperatorId());
        eventOperationV2Dto.setOperatorName(deleteEventDto.getOperatorName());
        eventOperationV2Dto.setIpAddress(deleteEventDto.getIpAddress());
        eventOperationV2Dto.setLanguage(deleteEventDto.getLanguage());

        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(data, eventOperationV2Dto, 0, 0L, null);
        TableTennisEventTypeEnum type = TableTennisEventTypeEnum.getByCode(oldEvent.getEventCode());
        Integer delta = type != null ? type.getScore() : null;
        if (delta != null && delta > 0) {
            int curHome = data.getMatchScoresInfo() != null && data.getMatchScoresInfo().getPeriodT1() != null
                    ? data.getMatchScoresInfo().getPeriodT1() : 0;
            int curAway = data.getMatchScoresInfo() != null && data.getMatchScoresInfo().getPeriodT2() != null
                    ? data.getMatchScoresInfo().getPeriodT2() : 0;
            String receiving = receivingSide(type, oldEvent.getHomeAway());
            if (TeamTypeConstant.AWAY.equals(receiving) && curAway - delta < 0) {
                return Response.failed("当前局分不足，删除后将小于 0，无法删除");
            }
            if (TeamTypeConstant.HOME.equals(receiving) && curHome - delta < 0) {
                return Response.failed("当前局分不足，删除后将小于 0，无法删除");
            }
        }
        CommonItem before = updateMatchScore(data, matchEventInfoDTO, true);
        matchScoresEventInfoMapper.updateByPrimaryKeySelective(oldEvent);
        String beforeVal = before.getHome() + "-" + before.getAway();

        matchEventInfoDTO.setAddition9("true");
        matchEventInfoDTO.setExtrainfo(oldEvent.getThirdEventId());
        if (eventOperationV2Dto.getSecondFromStart() != null) {
            matchEventInfoDTO.setSecondsFromStart(eventOperationV2Dto.getSecondFromStart());
        }
        matchEventInfoDTO.setCopyLinkId(deleteEventDto.getLinkedId());

        MatchScoresInfo info = data.getMatchScoresInfo();
        int afterH = info != null && info.getPeriodT1() != null ? info.getPeriodT1() : 0;
        int afterA = info != null && info.getPeriodT2() != null ? info.getPeriodT2() : 0;
        String afterVal = afterH + "-" + afterA;
        MatchCommonLogDto logDto = tableTennisPdOperationLogConverter.convertDeleteEventToLog(
                deleteEventDto, beforeVal, afterVal);
        logDto.setOperatorId(deleteEventDto.getOperatorId());
        logDto.setOperatorName(deleteEventDto.getOperatorName());
        logDto.setIpAddress(deleteEventDto.getIpAddress());
        logDto.setLanguage(deleteEventDto.getLanguage());
        matchScoreCommonHelper.commonProcess(data, eventOperationV2Dto, matchEventInfoDTO, logDto);
        log.info("[MatchTableTennisServiceImpl]deleteEvent end linkId::{} thirdMatchId:{} before:{} after:{}",
                deleteEventDto.getLinkedId(), deleteEventDto.getThirdMatchId(), beforeVal, afterVal);
        return Response.success();
    }

    @Override
    public Response changeMatchStatus(MatchScoreAndTimeVo matchScoreAndTimeVo,
                                       ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        Integer ct = changeMatchStatusV2Dto.getControlType();
        if (Integer.valueOf(1).equals(ct)) {
            Long thirdMatchId = changeMatchStatusV2Dto.getThirdMatchId();
            matchScoreCommonHelper.setMatchCacheStatus(thirdMatchId, TableTennisConstant.KICKOFF_FIRST_CLICK, 0, 0);
            matchScoreCommonHelper.setMatchCacheStatus(thirdMatchId, TableTennisConstant.TABLE_TENNIS_CURRENT_SERVER, 0, 0);
            matchScoreCommonHelper.setMatchCacheStatus(thirdMatchId, TableTennisConstant.MATCH_INTERRUPTED, false);
            matchScoreCommonHelper.setMatchCacheStatus(thirdMatchId, TableTennisConstant.MATCH_EVENT_INTERRUPTED, false);
            log.info("[MatchTableTennisServiceImpl]changeMatchStatus ct=1 报球板状态初始化 match:{} periodId:{} linkId::{} operator:{}",
                    thirdMatchId, changeMatchStatusV2Dto.getPeriodId(),
                    changeMatchStatusV2Dto.getLinkedId(), changeMatchStatusV2Dto.getOperatorName());
        }
        if (Integer.valueOf(4).equals(ct)) {
            Long endPeriod = resolveMatchEndPeriod(changeMatchStatusV2Dto);
            MatchScoresInfo info = matchScoreAndTimeVo.getMatchScoresInfo();
            if (info != null && StringUtils.isNotBlank(info.getScoresJson())) {
                Map<Long, TableTennisV2Scores> all = parseScoresJson(info.getScoresJson());
                TableTennisV2Scores whole = all.computeIfAbsent(WHOLE_MATCH, k -> new TableTennisV2Scores());
                CommonItem overall = computeOverallMatchScore(all, info.getMatchLength(), null);
                CommonItem overallSet = computeOverallSetScore(all);
                if (whole.getMatchScore() == null) {
                    whole.setMatchScore(new CommonItem());
                }
                whole.getMatchScore().setHome(overall.getHome());
                whole.getMatchScore().setAway(overall.getAway());
                if (whole.getSetScore() == null) {
                    whole.setSetScore(new CommonItem());
                }
                whole.getSetScore().setHome(overallSet.getHome());
                whole.getSetScore().setAway(overallSet.getAway());
                info.setScoresJson(JSONObject.toJSONString(all));
                info.setT1(overall.getHome());
                info.setT2(overall.getAway());
                info.setModifyTime(System.currentTimeMillis());
                matchScoreInfoRepository.updateScoresInfo(info);
                matchScoreAndTimeVo.setMatchScoresInfo(info);
            }
            if (matchScoreAndTimeVo.getMatchTimeInfo() != null) {
                pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchScoreAndTimeVo.getMatchTimeInfo(), null);
            }
            if (matchScoreAndTimeVo.getMatchScoresInfo() != null) {
                pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoreAndTimeVo.getMatchScoresInfo(), null);
            }
            matchScoreCommonHelper.setMatchCacheStatus(changeMatchStatusV2Dto.getThirdMatchId(),
                    TableTennisConstant.MATCH_CURRENT_PERIOD, endPeriod);
            log.info("[MatchTableTennisServiceImpl]changeMatchStatus ct=4 比赛结束阶段:{} match:{} linkId::{} operator:{}",
                    endPeriod, changeMatchStatusV2Dto.getThirdMatchId(),
                    changeMatchStatusV2Dto.getLinkedId(), changeMatchStatusV2Dto.getOperatorName());
        }
        // 黄红牌同手：ct=4 时额外触发比赛结束事件
        if (Integer.valueOf(4).equals(ct)) {
            handleYellowRedCardMatchEnd(matchScoreAndTimeVo, changeMatchStatusV2Dto);
        }
        Response resp = super.changeMatchStatus(matchScoreAndTimeVo, changeMatchStatusV2Dto);
        return resp;
    }

    /**
     * 黄红牌同手场景：通过 changeMatchStatus(ct=4) 触发比赛结束。
     * 此处额外下发一个 yellowred_card_same_hand 状态事件，确保事件审核链路感知该终态事件。
     */
    private void handleYellowRedCardMatchEnd(MatchScoreAndTimeVo matchScoreAndTimeVo,
                                              ChangeMatchStatusV2Dto dto) {
        try {
            // 从 scoresJson 检查当前局是否有黄红牌同手事件记录
            MatchScoresInfo info = matchScoreAndTimeVo.getMatchScoresInfo();
            if (info == null || StringUtils.isBlank(info.getScoresJson())) {
                log.info("[MatchTableTennisServiceImpl]handleYellowRedCardMatchEnd skip: no scoresJson match:{} linkId::{}",
                        dto.getThirdMatchId(), dto.getLinkedId());
                return;
            }
            Map<Long, TableTennisV2Scores> all = parseScoresJson(info.getScoresJson());
            Long curPeriod = info.getPeriod();
            TableTennisV2Scores periodScores = curPeriod != null ? all.get(curPeriod) : null;
            boolean hasYellowRed = false;
            int h = 0, a = 0;
            if (periodScores != null && periodScores.getYellowRedCardSameHand() != null) {
                h = periodScores.getYellowRedCardSameHand().getHome() != null
                        ? periodScores.getYellowRedCardSameHand().getHome() : 0;
                a = periodScores.getYellowRedCardSameHand().getAway() != null
                        ? periodScores.getYellowRedCardSameHand().getAway() : 0;
                hasYellowRed = h > 0 || a > 0;
            }
            if (!hasYellowRed) {
                log.info("[MatchTableTennisServiceImpl]handleYellowRedCardMatchEnd skip: no yellow-red record match:{} period:{} linkId::{}",
                        dto.getThirdMatchId(), curPeriod, dto.getLinkedId());
                return;
            }
            log.info("[MatchTableTennisServiceImpl]handleYellowRedCardMatchEnd emit yellowred event match:{} period:{} home:{} away:{} linkId::{}",
                    dto.getThirdMatchId(), curPeriod, h, a, dto.getLinkedId());
            EventOperationV2Dto opDto = EventOperationV2Dto.builder()
                    .sportId(dto.getSportId())
                    .thirdMatchId(dto.getThirdMatchId())
                    .eventCode(TableTennisEventTypeEnum.YELLOW_RED_CARD_SAME_HAND.getEventCode())
                    .homeAway("all")
                    .secondFromStart(0L)
                    .build();
            opDto.setLinkedId(dto.getLinkedId() + "_yellowred_end");
            opDto.setOperatorId(dto.getOperatorId());
            opDto.setOperatorName(dto.getOperatorName());
            opDto.setIpAddress(dto.getIpAddress());
            opDto.setLanguage(dto.getLanguage());
            MatchEventInfoDTO eventInfo = MatchEventUtils.createCommonMatchEvent(matchScoreAndTimeVo, opDto, 0, 0L, null);
            eventInfo.setCopyLinkId("PD_" + UUID.randomUUID().toString());
            eventInfo.setT1(info.getT1() != null ? info.getT1() : 0);
            eventInfo.setT2(info.getT2() != null ? info.getT2() : 0);
            eventInfo.setFirstT1(info.getPeriodT1() != null ? info.getPeriodT1() : 0);
            eventInfo.setFirstT2(info.getPeriodT2() != null ? info.getPeriodT2() : 0);
            eventInfo.setExtrainfo(opDto.getLinkedId());
            MatchCommonLogDto logDto = new MatchCommonLogDto();
            logDto.setSportId(dto.getSportId());
            logDto.setThirdMatchId(dto.getThirdMatchId());
            logDto.setLinkedId(opDto.getLinkedId());
            logDto.setEventCode(TableTennisEventTypeEnum.YELLOW_RED_CARD_SAME_HAND.getEventCode());
            logDto.setHowAway("all");
            logDto.setOperatorId(dto.getOperatorId());
            logDto.setOperatorName(dto.getOperatorName());
            logDto.setIpAddress(dto.getIpAddress());
            logDto.setLanguage(dto.getLanguage());
            logDto.setBeforeVal("-");
            logDto.setAfterVal("red-yellow-same-hand && red-yellow-same-hand");
            matchScoreCommonHelper.commonProcess(matchScoreAndTimeVo, opDto, eventInfo, logDto);
            log.info("[MatchTableTennisServiceImpl]handleYellowRedCardMatchEnd 下发黄红牌同手终态事件 match:{} linkId::{}",
                    dto.getThirdMatchId(), dto.getLinkedId());
        } catch (Exception e) {
            log.error("[MatchTableTennisServiceImpl]handleYellowRedCardMatchEnd error match:{} linkId::{}",
                    dto.getThirdMatchId(), dto.getLinkedId(), e);
        }
    }

    @Override
    protected Long resolveMatchEndPeriod(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        Long requested = changeMatchStatusV2Dto.getPeriodId();
        if (requested != null && requested.equals(TableTennisConstant.PERIOD_MATCH_END)) {
            return requested;
        }
        return TableTennisConstant.PERIOD_MATCH_END;
    }

    @Override
    protected void customizeStatusEvent(ChangeMatchStatusV2Dto changeMatchStatusV2Dto,
                                         EventOperationV2Dto eventOperationV2Dto,
                                         MatchEventInfoDTO matchEventInfoDTO,
                                         MatchCommonLogDto matchCommonLogDto) {
        Integer ct = changeMatchStatusV2Dto.getControlType();
        if (ct == null) {
            return;
        }
        String overrideCode = null;
        if (Integer.valueOf(2).equals(ct)) {
            overrideCode = "timeout";
        } else if (Integer.valueOf(3).equals(ct)) {
            overrideCode = "timeout_over";
        }
        if (overrideCode != null) {
            eventOperationV2Dto.setEventCode(overrideCode);
            matchEventInfoDTO.setEventCode(overrideCode);
            matchCommonLogDto.setEventCode(overrideCode);
        }
    }

    @Override
    protected boolean shouldBroadcastMatchStatus(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        Integer ct = changeMatchStatusV2Dto.getControlType();
        return !(Integer.valueOf(2).equals(ct) || Integer.valueOf(3).equals(ct));
    }

    @Override
    protected void applyDefaultPeriodId(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        if (Integer.valueOf(2).equals(changeMatchStatusV2Dto.getControlType())) {
            return;
        }
        super.applyDefaultPeriodId(changeMatchStatusV2Dto);
    }

    @Override
    protected void doBroadcastMatchStatus(MatchScoreAndTimeVo matchScoreAndTimeVo,
                                           ChangeMatchStatusV2Dto changeMatchStatusV2Dto,
                                           MatchInfoConvertEnum convertEnum) {
        Integer broadcastStatus = convertEnum.getMatchStatus();
        eventProducer.sendStandardMatchStatus(matchScoreAndTimeVo.getThirdMatchInfo(),
                changeMatchStatusV2Dto.getLinkedId(), broadcastStatus);
        StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
        if (standardMatchInfo != null && standardMatchInfo.getId() != null) {
            com.panda.merge.model.StandardSportMarketSell marketSell1 =
                    pdMatchInfoRepository.getStandardSportMarketSell(standardMatchInfo.getId(), null);
            if (marketSell1 != null && Objects.equals(changeMatchStatusV2Dto.getDataSourceCode(),
                    marketSell1.getBusinessEvent())) {
                StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
                newStandardMatchInfo.setId(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
                newStandardMatchInfo.setMatchStatus(broadcastStatus == 80 ? 10 : broadcastStatus);
                newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
                pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
            }
        }
        eventProducer.sendMatchStatusTopic(changeMatchStatusV2Dto.getLinkedId(),
                matchScoreAndTimeVo.getThirdMatchInfo(), broadcastStatus);
    }

    @Override
    public Response changeMatchPeriod(MatchScoreAndTimeVo data, ChangeMatchPeriodV2Dto dto) {
        log.info("[MatchTableTennisServiceImpl]changeMatchPeriod start linkId::{}:data:{} restTime:{}",
                dto.getLinkedId(), dto, dto.getRestTime());

        validatePeriodTransition(data, dto);
        if (dto.getPeriodId() != null) {
            Long currentPeriod = data.getMatchTimeInfo() != null ? data.getMatchTimeInfo().getPeriod() : null;
            if (currentPeriod != null && currentPeriod.equals(dto.getPeriodId())) {
                log.error("[MatchTableTennisServiceImpl]changeMatchPeriod periodId:{} is already the current period for match:{} linkId::{}",
                        dto.getPeriodId(), dto.getThirdMatchId(), dto.getLinkedId());
                return Response.failed("已经为当前局了");
            }
        }

        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.changeMatchPeriodToEvent(dto);
        eventOperationV2Dto.setOperatorId(dto.getOperatorId());
        eventOperationV2Dto.setOperatorName(dto.getOperatorName());
        eventOperationV2Dto.setIpAddress(dto.getIpAddress());
        eventOperationV2Dto.setLanguage(dto.getLanguage());

        Long remainTime = dto.getRestTime() != null ? dto.getRestTime() : 0L;
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(data, eventOperationV2Dto, 0, remainTime, dto.getPeriodId());
        Long beforePeriod = dto.getPeriodId();
        log.info("赛事ID:{},下发阶段事件---：beforePeriod：{}--{},，比分：{}:{}",
                dto.getThirdMatchId(), dto.getPeriodId(),
                data.getMatchScoresInfo().getPeriod(),
                data.getMatchScoresInfo().getPeriodT1(), data.getMatchScoresInfo().getPeriodT2());
        addMatchPeriod(data, matchEventInfoDTO);
        JSONObject periodTableTennisScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, TableTennisV2Scores> allPeriodScores = parseScoresJson(data.getMatchScoresInfo().getScoresJson());
        TableTennisV2Scores periodScores = allPeriodScores.get(getPeriod(beforePeriod));
        log.info("赛事ID:{},下发阶段事件---：beforePeriod：{}--{},，比分：{}:{}，获取阶段比分：{}",
                dto.getThirdMatchId(), dto.getPeriodId(), data.getMatchScoresInfo().getPeriod(),
                data.getMatchScoresInfo().getPeriodT1(), data.getMatchScoresInfo().getPeriodT2(), periodScores);

        MatchCommonLogDto matchCommonLogDto = tableTennisPdOperationLogConverter.convertChangePeriodToLog(dto);
        matchCommonLogDto.setOperatorId(dto.getOperatorId());
        matchCommonLogDto.setOperatorName(dto.getOperatorName());
        matchCommonLogDto.setIpAddress(dto.getIpAddress());
        matchCommonLogDto.setLanguage(dto.getLanguage());
        matchEventInfoDTO.setT1(data.getMatchScoresInfo().getT1());
        matchEventInfoDTO.setT2(data.getMatchScoresInfo().getT2());
        if (periodScores != null && periodScores.getSetScore() != null) {
            matchEventInfoDTO.setFirstT1(periodScores.getSetScore().getHome());
            matchEventInfoDTO.setFirstT2(periodScores.getSetScore().getAway());
        } else {
            matchEventInfoDTO.setFirstT1(data.getMatchScoresInfo().getPeriodT1());
            matchEventInfoDTO.setFirstT2(data.getMatchScoresInfo().getPeriodT2());
        }

        Long pid = dto.getPeriodId();
        boolean pauseOrEnd = pid != null
                && (pid.equals(TableTennisConstant.PERIOD_SUSPENDED) || pid.equals(TableTennisConstant.PERIOD_MATCH_END));
        if (!pauseOrEnd) {
            matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(),
                    TableTennisConstant.KICKOFF_FIRST_CLICK, 0, 0);
            matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(),
                    TableTennisConstant.TABLE_TENNIS_CURRENT_SERVER, 0, 0);
        }
        matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(),
                TableTennisConstant.MATCH_CURRENT_PERIOD, dto.getPeriodId());
        log.info("赛事ID:{},下发阶段事件222：阶段：{}，比分：{}:{}",
                dto.getThirdMatchId(), dto.getPeriodId(),
                matchEventInfoDTO.getFirstT1(), matchEventInfoDTO.getFirstT2());
        matchScoreCommonHelper.commonProcess(data, eventOperationV2Dto, matchEventInfoDTO, matchCommonLogDto);
        log.info("[MatchTableTennisServiceImpl]changeMatchPeriod end linkId::{}", dto.getLinkedId());
        return Response.success();
    }

    private Long getPeriod(Long periodId) {
        if (periodId == null) {
            return null;
        }
        // periodId 可能是 SET_END（301~306/100）或 SET_BEGIN（8~442）
        // 返回对应的 SET_BEGIN periodId，用于从 scoresJson 取该局比分
        if (TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(periodId)) {
            return periodId;
        }
        Integer setNum = TableTennisConstant.TABLE_TENNIS_SET_END.get(periodId);
        if (setNum != null) {
            return TableTennisConstant.TABLE_TENNIS_SET_BEGIN.entrySet().stream()
                    .filter(e -> e.getValue().equals(setNum))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(periodId);
        }
        return periodId;
    }

    private void validatePeriodTransition(MatchScoreAndTimeVo data, ChangeMatchPeriodV2Dto dto) {
        try {
            if (data == null || data.getMatchScoresInfo() == null || data.getMatchTimeInfo() == null || dto == null) {
                return;
            }
            Long currentPid = data.getMatchTimeInfo().getPeriod();
            Long targetPid = dto.getPeriodId();
            if (targetPid == null) {
                return;
            }
            // 放行：suspension(80) / 比赛结束(999)
            if (targetPid.equals(TableTennisConstant.PERIOD_SUSPENDED)
                    || targetPid.equals(TableTennisConstant.PERIOD_MATCH_END)) {
                return;
            }

            boolean isBeginTarget = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(targetPid);
            boolean isEndTarget = TableTennisConstant.TABLE_TENNIS_SET_END.containsKey(targetPid);
            if (!isBeginTarget && !isEndTarget) {
                return;
            }

            Integer currentSetIndex = null;
            if (currentPid != null) {
                if (TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(currentPid)) {
                    currentSetIndex = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.get(currentPid);
                } else if (TableTennisConstant.TABLE_TENNIS_SET_END.containsKey(currentPid)) {
                    currentSetIndex = TableTennisConstant.TABLE_TENNIS_SET_END.get(currentPid);
                }
            }

            Map<Long, TableTennisV2Scores> all = parseScoresJson(data.getMatchScoresInfo().getScoresJson());
            int maxSetIndex = 0;
            for (Long pid : all.keySet()) {
                if (TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(pid)) {
                    maxSetIndex = Math.max(maxSetIndex, TableTennisConstant.TABLE_TENNIS_SET_BEGIN.get(pid));
                } else if (TableTennisConstant.TABLE_TENNIS_SET_END.containsKey(pid)) {
                    maxSetIndex = Math.max(maxSetIndex, TableTennisConstant.TABLE_TENNIS_SET_END.get(pid));
                }
            }

            Integer targetSetIndex = isBeginTarget
                    ? TableTennisConstant.TABLE_TENNIS_SET_BEGIN.get(targetPid)
                    : TableTennisConstant.TABLE_TENNIS_SET_END.get(targetPid);
            if (targetSetIndex != null) {
                boolean allowed = false;
                if (isBeginTarget) {
                    if (targetSetIndex == 1 && maxSetIndex == 0) {
                        allowed = true;
                    }
                    if (!allowed && maxSetIndex > 0 && targetSetIndex <= maxSetIndex) {
                        allowed = true;
                    }
                    if (!allowed && currentSetIndex != null && currentPid != null
                            && TableTennisConstant.TABLE_TENNIS_SET_END.containsKey(currentPid)
                            && targetSetIndex == currentSetIndex + 1) {
                        allowed = true;
                    }
                } else {
                    if (maxSetIndex > 0 && targetSetIndex <= maxSetIndex) {
                        allowed = true;
                    }
                    if (!allowed && currentSetIndex != null && targetSetIndex.equals(currentSetIndex)) {
                        allowed = true;
                    }
                }
                if (!allowed) {
                    log.error("[MatchTableTennisServiceImpl]changeMatchPeriod illegal period jump, currentPid:{} currentIndex:{} targetPid:{} targetIndex:{} maxIndex:{} linkId::{}",
                            currentPid, currentSetIndex, targetPid, targetSetIndex, maxSetIndex, dto.getLinkedId());
                    throw new RuntimeException("The current target set does not meet the jump conditions");
                }
            }

            // 乒乓球局分校验：从本局 SET_BEGIN 关本局（SET_END）时，至少一方 ≥ 11 且分差 ≥ 2
            boolean closingCurrentSet = isEndTarget
                    && currentPid != null
                    && TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(currentPid)
                    && currentSetIndex != null
                    && targetSetIndex != null
                    && targetSetIndex.equals(currentSetIndex);
            if (!closingCurrentSet) {
                return;
            }

            TableTennisV2Scores curSet = all.get(currentPid);
            CommonItem setScore = curSet != null ? curSet.getSetScore() : null;
            int sh = setScore != null && setScore.getHome() != null ? setScore.getHome() : 0;
            int sa = setScore != null && setScore.getAway() != null ? setScore.getAway() : 0;
            int target = TableTennisConstant.NORMAL_SET_MIN_SCORE;
            int diff = TableTennisConstant.MIN_SCORE_DIFF;
            boolean homeWon = sh >= target && sh - sa >= diff;
            boolean awayWon = sa >= target && sa - sh >= diff;
            if (!homeWon && !awayWon) {
                log.error("[MatchTableTennisServiceImpl]changeMatchPeriod current set not finished linkId::{} cur:{} target:{} setScore:{}:{}",
                        dto.getLinkedId(), currentPid, targetPid, sh, sa);
                throw new RuntimeException(String.format("Current set is not finished (score %d:%d), cannot close the set", sh, sa));
            }
        } catch (RuntimeException e) {
            log.error("[MatchTableTennisServiceImpl]changeMatchPeriod validate period error match:{} linkId::{}",
                    dto != null ? dto.getThirdMatchId() : null, dto != null ? dto.getLinkedId() : null, e);
            throw e;
        } catch (Exception e) {
            log.error("[MatchTableTennisServiceImpl]changeMatchPeriod validate period error match:{} linkId::{}",
                    dto != null ? dto.getThirdMatchId() : null, dto != null ? dto.getLinkedId() : null, e);
        }
    }

    @Override
    public void addMatchPeriod(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO) {
        Long targetPeriodId = matchEventInfoDTO.getMatchPeriodId();
        Integer keepHome = data.getMatchScoresInfo().getT1();
        Integer keepAway = data.getMatchScoresInfo().getT2();
        Long previousPeriodId = data.getMatchTimeInfo().getPeriod();

        data.getMatchTimeInfo().setPeriod(targetPeriodId);
        data.getMatchScoresInfo().setPeriod(targetPeriodId);
        // 乒乓球 firstNum 映射：8→1, 9→2, ..., 442→7；不在 TABLE_TENNIS_SET_BEGIN 中时不设置
        Integer firstNum = null;
        if (TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(targetPeriodId)) {
            firstNum = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.get(targetPeriodId);
        }
        matchEventInfoDTO.setFirstNum(firstNum);
        Map<Long, TableTennisV2Scores> all = parseScoresJson(data.getMatchScoresInfo().getScoresJson());
        TableTennisV2Scores whole = all.computeIfAbsent(WHOLE_MATCH, k -> new TableTennisV2Scores());

        TableTennisV2Scores periodScores = all.get(targetPeriodId);
        if (periodScores == null && TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(targetPeriodId)) {
            periodScores = new TableTennisV2Scores();
            all.put(targetPeriodId, periodScores);
            data.getMatchScoresInfo().setPeriodT1(0);
            data.getMatchScoresInfo().setPeriodT2(0);
            log.info("[MatchTableTennisServiceImpl]addMatchPeriod new set initialized linkId::{} match:{} previousPeriod:{} targetPeriod:{} setNum:{}",
                    matchEventInfoDTO.getCopyLinkId(), data.getMatchScoresInfo().getThirdMatchId(),
                    previousPeriodId, targetPeriodId, firstNum);
        } else {
            if (whole.getMatchScore() == null) {
                whole.setMatchScore(new CommonItem());
            }
            int curHome = whole.getMatchScore().getHome() != null ? whole.getMatchScore().getHome() : 0;
            int curAway = whole.getMatchScore().getAway() != null ? whole.getMatchScore().getAway() : 0;

            boolean isSetEnd = targetPeriodId != null
                    && (TableTennisConstant.TABLE_TENNIS_SET_END.containsKey(targetPeriodId)
                    || TableTennisConstant.PERIOD_MATCH_END.equals(targetPeriodId));
            if (isSetEnd) {
                if (previousPeriodId != null && TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(previousPeriodId)) {
                    TableTennisV2Scores prevSetScores = all.get(previousPeriodId);
                    CommonItem prevSet = prevSetScores != null ? prevSetScores.getSetScore() : null;
                    int ph = prevSet != null && prevSet.getHome() != null ? prevSet.getHome() : 0;
                    int pa = prevSet != null && prevSet.getAway() != null ? prevSet.getAway() : 0;
                    int target = TableTennisConstant.NORMAL_SET_MIN_SCORE;
                    int diff = TableTennisConstant.MIN_SCORE_DIFF;
                    boolean homeWon = ph >= target && ph - pa >= diff;
                    boolean awayWon = pa >= target && pa - ph >= diff;
                    if (!homeWon && !awayWon) {
                        log.error("[MatchTableTennisServiceImpl]addMatchPeriod current set not finished linkId::{} previousPeriodId:{} targetPeriodId:{} setScore:{}:{}",
                                matchEventInfoDTO.getCopyLinkId(), previousPeriodId, targetPeriodId, ph, pa);
                        throw new IllegalArgumentException(
                                String.format("局比分未分出胜负(%d:%d)，不能切换到小局休息/比赛结束阶段", ph, pa));
                    }
                }
                CommonItem overall = computeOverallMatchScore(all, data.getMatchScoresInfo().getMatchLength(), null);
                whole.getMatchScore().setHome(overall.getHome());
                whole.getMatchScore().setAway(overall.getAway());
                data.getMatchScoresInfo().setT1(overall.getHome());
                data.getMatchScoresInfo().setT2(overall.getAway());
            } else {
                data.getMatchScoresInfo().setT1(curHome);
                data.getMatchScoresInfo().setT2(curAway);
            }
        }

        boolean backToOldSet = targetPeriodId != null
                && TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(targetPeriodId)
                && previousPeriodId != null && targetPeriodId < previousPeriodId;
        if (backToOldSet) {
            data.getMatchScoresInfo().setT1(keepHome);
            data.getMatchScoresInfo().setT2(keepAway);
            if (whole.getMatchScore() == null) {
                whole.setMatchScore(new CommonItem());
            }
            whole.getMatchScore().setHome(keepHome == null ? 0 : keepHome);
            whole.getMatchScore().setAway(keepAway == null ? 0 : keepAway);
        }

        matchEventInfoDTO.setFirstT1(data.getMatchScoresInfo().getPeriodT1());
        matchEventInfoDTO.setFirstT2(data.getMatchScoresInfo().getPeriodT2());
        matchEventInfoDTO.setT1(data.getMatchScoresInfo().getT1());
        matchEventInfoDTO.setT2(data.getMatchScoresInfo().getT2());

        long now = System.currentTimeMillis();
        data.getMatchScoresInfo().setSecondsMatchStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchScoresInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchScoresInfo().setModifyTime(now);
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(all));

        Integer timeGo = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(targetPeriodId) ? 1 : 0;
        data.getMatchTimeInfo().setSecondFromStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchTimeInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchTimeInfo().setTimeGo(timeGo);
        data.getMatchTimeInfo().setEventTime(now);
        data.getMatchTimeInfo().setModifyTime(now);

        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(data.getMatchTimeInfo(), null);
        log.info("[MatchTableTennisServiceImpl]addMatchPeriod done linkId::{} match:{} previousPeriod:{} targetPeriod:{} setNum:{} matchScore:{},{} periodScore:{},{}",
                matchEventInfoDTO.getCopyLinkId(), data.getMatchScoresInfo().getThirdMatchId(),
                previousPeriodId, targetPeriodId, firstNum,
                data.getMatchScoresInfo().getT1(), data.getMatchScoresInfo().getT2(),
                data.getMatchScoresInfo().getPeriodT1(), data.getMatchScoresInfo().getPeriodT2());
    }

    @Override
    public CommonItem updateMatchScore(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO,
                                        Boolean isDelete) throws Exception {
        TableTennisEventTypeEnum type = TableTennisEventTypeEnum.getByCode(matchEventInfoDTO.getEventCode());
        if (type == null) {
            log.info("[MatchTableTennisServiceImpl]updateMatchScore unknown eventCode:{} match:{}",
                    matchEventInfoDTO.getEventCode(),
                    data.getThirdMatchInfo() != null ? data.getThirdMatchInfo().getId() : null);
            return new CommonItem(0, 0);
        }

        Map<Long, TableTennisV2Scores> all = parseScoresJson(data.getMatchScoresInfo().getScoresJson());
        TableTennisV2Scores whole = all.computeIfAbsent(WHOLE_MATCH, k -> new TableTennisV2Scores());
        Long curPeriodId = data.getMatchTimeInfo().getPeriod();
        Long effectivePeriodId = curPeriodId != null ? curPeriodId : 8L;
        TableTennisV2Scores periodScores = all.computeIfAbsent(effectivePeriodId, k -> new TableTennisV2Scores());

        CommonItem fieldBefore = whole.getFieldScoreByEventCode(type.getEventCode());
        CommonItem before;
        if (fieldBefore != null) {
            before = new CommonItem(fieldBefore.getHome(), fieldBefore.getAway());
        } else {
            before = new CommonItem(0, 0);
        }

        if (Boolean.TRUE.equals(isDelete) && type.getScore() != null && fieldBefore != null) {
            int delta = type.getScore();
            int curHome = fieldBefore.getHome() == null ? 0 : fieldBefore.getHome();
            int curAway = fieldBefore.getAway() == null ? 0 : fieldBefore.getAway();
            String actor = matchEventInfoDTO.getHomeAway();
            if (TeamTypeConstant.AWAY.equals(actor) && curAway - delta < 0) {
                log.error("[MatchTableTennisServiceImpl]updateMatchScore delete score underflow match:{} period:{} eventCode:{} actor:{} curAway:{} delta:{}",
                        data.getThirdMatchInfo() != null ? data.getThirdMatchInfo().getId() : null,
                        effectivePeriodId, type.getEventCode(), actor, curAway, delta);
                throw new Exception("事件比分不足，删除后比分将小于 0，无法删除");
            }
            if (TeamTypeConstant.HOME.equals(actor) && curHome - delta < 0) {
                log.error("[MatchTableTennisServiceImpl]updateMatchScore delete score underflow match:{} period:{} eventCode:{} actor:{} curHome:{} delta:{}",
                        data.getThirdMatchInfo() != null ? data.getThirdMatchInfo().getId() : null,
                        effectivePeriodId, type.getEventCode(), actor, curHome, delta);
                throw new Exception("事件比分不足，删除后比分将小于 0，无法删除");
            }
        }

        CommonItem oriMatchScore = whole.getMatchScore();
        CommonItem matchScoreSnapshot = oriMatchScore != null
                ? new CommonItem(oriMatchScore.getHome(), oriMatchScore.getAway())
                : new CommonItem(0, 0);

        whole.doCalculation(type, matchEventInfoDTO.getHomeAway(), isDelete);
        periodScores.doCalculation(type, matchEventInfoDTO.getHomeAway(), isDelete);

        boolean inPlayingSet = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(effectivePeriodId);
        if (whole.getMatchScore() == null) {
            whole.setMatchScore(new CommonItem());
        }
        if (inPlayingSet) {
            whole.getMatchScore().setHome(matchScoreSnapshot.getHome());
            whole.getMatchScore().setAway(matchScoreSnapshot.getAway());
        } else {
            CommonItem overall = computeOverallMatchScore(all, data.getMatchScoresInfo().getMatchLength(), null);
            whole.getMatchScore().setHome(overall.getHome());
            whole.getMatchScore().setAway(overall.getAway());
        }

        if (periodScores.getSetScore() == null) {
            periodScores.setSetScore(new CommonItem());
        }
        CommonItem finalMatchScore = whole.getMatchScore();
        data.getMatchScoresInfo().setT1(finalMatchScore.getHome());
        data.getMatchScoresInfo().setT2(finalMatchScore.getAway());
        data.getMatchScoresInfo().setPeriodT1(periodScores.getSetScore().getHome());
        data.getMatchScoresInfo().setPeriodT2(periodScores.getSetScore().getAway());

        matchEventInfoDTO.setFirstT1(periodScores.getSetScore().getHome() != null
                ? periodScores.getSetScore().getHome() : 0);
        matchEventInfoDTO.setFirstT2(periodScores.getSetScore().getAway() != null
                ? periodScores.getSetScore().getAway() : 0);
        if (type.isPerSetCounter()) {
            CommonItem periodCounter = periodScores.getFieldScoreByEventCode(type.getEventCode());
            matchEventInfoDTO.setT1(periodCounter != null && periodCounter.getHome() != null
                    ? periodCounter.getHome() : 0);
            matchEventInfoDTO.setT2(periodCounter != null && periodCounter.getAway() != null
                    ? periodCounter.getAway() : 0);
        } else {
            matchEventInfoDTO.setT1(finalMatchScore.getHome());
            matchEventInfoDTO.setT2(finalMatchScore.getAway());
        }

        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(all));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        matchScoreInfoRepository.updateScoresInfo(data.getMatchScoresInfo());
        log.info("[MatchTableTennisServiceImpl]updateMatchScore match:{} period:{} eventCode:{} homeAway:{} delete:{} setScore:{},{}->{}:{}",
                data.getThirdMatchInfo() != null ? data.getThirdMatchInfo().getId() : null,
                effectivePeriodId, type.getEventCode(), matchEventInfoDTO.getHomeAway(),
                isDelete, before.getHome(), before.getAway(),
                matchEventInfoDTO.getFirstT1(), matchEventInfoDTO.getFirstT2());
        return before;
    }

    // ---------------------------------------------------------------- table-tennis-only endpoints

    @Override
    public Response scoreList(EventListV2Dto eventListV2Dto) {
        log.info("[MatchTableTennisServiceImpl]scoreList start linkId::{} thirdMatchId:{} setNums:{}",
                eventListV2Dto.getLinkedId(), eventListV2Dto.getThirdMatchId(), eventListV2Dto.getSetNums());
        if (eventListV2Dto.getThirdMatchId() == null) {
            return Response.failed("thirdMatchId 不能为空");
        }
        MatchScoresInfo info = matchScoreInfoRepository.selectByExample(eventListV2Dto.getThirdMatchId(), 1);
        if (info == null || StringUtils.isBlank(info.getScoresJson())) {
            log.info("[MatchTableTennisServiceImpl]scoreList no scoresJson thirdMatchId:{} linkId::{}",
                    eventListV2Dto.getThirdMatchId(), eventListV2Dto.getLinkedId());
            return Response.success(Collections.emptyList());
        }
        Map<Long, TableTennisV2Scores> all = parseScoresJson(info.getScoresJson());
        List<Long> filter = eventListV2Dto.getSetNums();
        boolean returnAll = CollectionUtils.isEmpty(filter) || filter.contains(-1L);
        List<PDTableTennisEventDto> result = new ArrayList<>();
        for (Map.Entry<Long, TableTennisV2Scores> entry : all.entrySet()) {
            if (!returnAll && !filter.contains(entry.getKey())) {
                continue;
            }
            result.add(tableTennisScoreListConverter.toPdEventDto(
                    entry.getValue(), entry.getKey().intValue(),
                    String.valueOf(eventListV2Dto.getThirdMatchId()), eventListV2Dto.getSportId()));
        }
        log.info("[MatchTableTennisServiceImpl]scoreList end linkId::{} thirdMatchId:{} periods:{} size:{}",
                eventListV2Dto.getLinkedId(), eventListV2Dto.getThirdMatchId(), all.keySet(), result.size());
        return Response.success(result);
    }

    @Override
    public Response sendEvent(MatchScoreAndTimeVo data, SendEventDto dto) throws Exception {
        log.info("[MatchTableTennisServiceImpl]sendEvent start linkId::{} thirdMatchId:{} eventCode:{} homeAway:{} periodId:{}",
                dto.getLinkedId(), dto.getThirdMatchId(), dto.getEventCode(), dto.getHomeAway(),
                data.getMatchTimeInfo() != null ? data.getMatchTimeInfo().getPeriod() : null);

        EventOperationV2Dto eventOperationV2Dto = EventOperationV2Dto.builder()
                .sportId(dto.getSportId())
                .thirdMatchId(dto.getThirdMatchId())
                .eventCode(dto.getEventCode())
                .homeAway(dto.getHomeAway())
                .secondFromStart(dto.getSecondFromStart() != null ? dto.getSecondFromStart() : 0L)
                .build();
        eventOperationV2Dto.setLinkedId(dto.getLinkedId());
        eventOperationV2Dto.setOperatorId(dto.getOperatorId());
        eventOperationV2Dto.setOperatorName(dto.getOperatorName());
        eventOperationV2Dto.setIpAddress(dto.getIpAddress());
        eventOperationV2Dto.setLanguage(dto.getLanguage());

        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(data, eventOperationV2Dto, 0, 0L, null);
        matchEventInfoDTO.setCopyLinkId(dto.getLinkedId());
        if (dto.getSecondFromStart() != null) {
            matchEventInfoDTO.setSecondsFromStart(dto.getSecondFromStart());
        }

        TableTennisEventTypeEnum type = TableTennisEventTypeEnum.getByCode(dto.getEventCode());
        String beforeValStr = "-";
        String afterValStr = "-";
        if (type != null) {
            boolean noScoreEffect = type.getScore() == null && !type.isPerSetCounter();
            if (!noScoreEffect) {
                CommonItem beforeVal = new CommonItem(0, 0);
                Map<Long, TableTennisV2Scores> snapshotAll = parseScoresJson(data.getMatchScoresInfo().getScoresJson());
                Long curPeriodId = data.getMatchTimeInfo() != null ? data.getMatchTimeInfo().getPeriod() : null;
                Long effectivePeriodId = curPeriodId != null ? curPeriodId : 8L;
                if (type.isPerSetCounter()) {
                    TableTennisV2Scores periodScoresSnapshot = snapshotAll.get(effectivePeriodId);
                    CommonItem fb = periodScoresSnapshot != null
                            ? periodScoresSnapshot.getFieldScoreByEventCode(type.getEventCode()) : null;
                    if (fb != null) {
                        beforeVal = new CommonItem(fb.getHome() != null ? fb.getHome() : 0,
                                fb.getAway() != null ? fb.getAway() : 0);
                    }
                } else {
                    TableTennisV2Scores wholeSnapshot = snapshotAll.get(WHOLE_MATCH);
                    CommonItem ms = wholeSnapshot != null ? wholeSnapshot.getMatchScore() : null;
                    if (ms != null) {
                        beforeVal = new CommonItem(ms.getHome() != null ? ms.getHome() : 0,
                                ms.getAway() != null ? ms.getAway() : 0);
                    }
                }
                beforeValStr = beforeVal.getHome() + "-" + beforeVal.getAway();
            }
            updateMatchScore(data, matchEventInfoDTO, false);
            if (!noScoreEffect) {
                afterValStr = matchEventInfoDTO.getT1() + "-" + matchEventInfoDTO.getT2();
            }
            log.info("[MatchTableTennisServiceImpl]sendEvent afterUpdateScore linkId::{} thirdMatchId:{} eventCode:{} homeAway:{} before:{} after:{}",
                    dto.getLinkedId(), dto.getThirdMatchId(), dto.getEventCode(), dto.getHomeAway(),
                    beforeValStr, afterValStr);
        } else {
            log.info("[MatchTableTennisServiceImpl]sendEvent unknown eventCode:{} match:{} linkId::{}",
                    dto.getEventCode(), dto.getThirdMatchId(), dto.getLinkedId());
        }

        MatchCommonLogDto logDto = new MatchCommonLogDto();
        logDto.setSportId(dto.getSportId());
        logDto.setThirdMatchId(dto.getThirdMatchId());
        logDto.setEventCode(dto.getEventCode());
        logDto.setHowAway(dto.getHomeAway());
        logDto.setPeriodId(data.getMatchTimeInfo() != null ? data.getMatchTimeInfo().getPeriod() : null);
        logDto.setLinkedId(dto.getLinkedId());
        logDto.setOperatorId(dto.getOperatorId());
        logDto.setOperatorName(dto.getOperatorName());
        logDto.setIpAddress(dto.getIpAddress());
        logDto.setLanguage(dto.getLanguage());
        logDto.setBeforeVal(beforeValStr);
        logDto.setAfterVal(afterValStr);
        matchScoreCommonHelper.commonProcess(data, eventOperationV2Dto, matchEventInfoDTO, logDto);

        // 发球事件：同步更新当前发球方
        if (TableTennisEventTypeEnum.CURRENT_SERVE_TABLE_TENNIS.getEventCode().equals(dto.getEventCode())) {
            int home = TeamTypeConstant.HOME.equals(dto.getHomeAway()) ? 1 : 0;
            int away = TeamTypeConstant.AWAY.equals(dto.getHomeAway()) ? 1 : 0;
            matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(),
                    TableTennisConstant.TABLE_TENNIS_CURRENT_SERVER, home, away);
        }

        log.info("[MatchTableTennisServiceImpl]sendEvent end linkId::{}", dto.getLinkedId());
        return Response.success();
    }

    @Override
    public Response batchEditScores(EditScoreV2Dto editScoreV2Dto) {
        if (editScoreV2Dto == null || editScoreV2Dto.getThirdMatchId() == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        log.info("[MatchTableTennisServiceImpl]batchEditScores start linkId::{}", editScoreV2Dto.getLinkedId());
        try {
            if (StringUtils.isBlank(editScoreV2Dto.getScores())) {
                return Response.success();
            }
            Map<Long, CommonItem> editMap = JSON.parseObject(editScoreV2Dto.getScores(),
                    new TypeReference<Map<Long, CommonItem>>() {});
            if (editMap == null || editMap.isEmpty()) {
                return Response.success();
            }
            MatchScoresInfo info = matchScoreInfoRepository.selectByExample(editScoreV2Dto.getThirdMatchId(), 1);
            if (info == null) {
                throw new IllegalArgumentException("未找到对应的比分信息，thirdMatchId: " + editScoreV2Dto.getThirdMatchId());
            }

            Response editGuard = validateEditScores(editMap, info, editScoreV2Dto.getLinkedId());
            if (editGuard != null) {
                return editGuard;
            }

            Map<Long, TableTennisV2Scores> all = parseScoresJson(info.getScoresJson());

            Map<Long, int[]> oldSetSnap = new LinkedHashMap<>();
            for (Map.Entry<Long, CommonItem> editEntry : editMap.entrySet()) {
                Long periodId = editEntry.getKey();
                if (periodId == null || editEntry.getValue() == null) {
                    continue;
                }
                TableTennisV2Scores ps = all.get(periodId);
                CommonItem oldSet = ps != null ? ps.getSetScore() : null;
                oldSetSnap.put(periodId, new int[]{
                        batchEditNz(oldSet != null ? oldSet.getHome() : null),
                        batchEditNz(oldSet != null ? oldSet.getAway() : null)
                });
            }

            for (Map.Entry<Long, CommonItem> entry : editMap.entrySet()) {
                Long periodId = entry.getKey();
                if (periodId == null || entry.getValue() == null) {
                    continue;
                }
                TableTennisV2Scores ps = all.computeIfAbsent(periodId, k -> new TableTennisV2Scores());
                ps.setSetScore(entry.getValue());
            }

            TableTennisV2Scores whole = all.computeIfAbsent(WHOLE_MATCH, k -> new TableTennisV2Scores());
            CommonItem overall = computeOverallMatchScore(all, info.getMatchLength(), info.getPeriod());
            CommonItem overallSet = computeOverallSetScore(all);
            if (whole.getMatchScore() == null) {
                whole.setMatchScore(new CommonItem());
            }
            whole.getMatchScore().setHome(overall.getHome());
            whole.getMatchScore().setAway(overall.getAway());
            if (whole.getSetScore() == null) {
                whole.setSetScore(new CommonItem());
            }
            whole.getSetScore().setHome(overallSet.getHome());
            whole.getSetScore().setAway(overallSet.getAway());

            info.setT1(overall.getHome());
            info.setT2(overall.getAway());
            info.setScoresJson(JSONObject.toJSONString(all));
            info.setModifyTime(System.currentTimeMillis());
            matchScoreInfoRepository.updateScoresInfo(info);
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(info, null);

            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(info.getThirdMatchId(), null);
            if (thirdMatchInfo != null) {
                MatchScoresInfo standardInfo = new MatchScoresInfo();
                BeanUtils.copyProperties(info, standardInfo);
                standardInfo.setScoresJson(buildTableTennisStandardMatchScoreMap(info.getScoresJson(),
                        editScoreV2Dto.getLinkedId()));
            }

            redisUtils.pushFootBallScore(editScoreV2Dto.getThirdMatchId());
            redisUtils.pushFootBallEvent(editScoreV2Dto.getThirdMatchId());

            String diffLog = buildBatchEditSetScoreDiff(editMap, oldSetSnap);
            if (StringUtils.isNotBlank(diffLog)) {
                log.info("[MatchTableTennisServiceImpl]batchEditScores diff linkId::{} {}",
                        editScoreV2Dto.getLinkedId(), diffLog);
                MatchScoreAndTimeVo logVo = commonAdvertiseService.searchMatchScoreAndTime(editScoreV2Dto.getThirdMatchId());
                List<Long> changedPeriodIds = editMap.keySet().stream()
                        .filter(Objects::nonNull).sorted().collect(Collectors.toList());
                for (Long periodId : changedPeriodIds) {
                    CommonItem edit = editMap.get(periodId);
                    if (edit == null) {
                        continue;
                    }
                    int newHome = batchEditNz(edit.getHome());
                    int newAway = batchEditNz(edit.getAway());
                    int[] old = oldSetSnap.get(periodId);
                    if (old == null || (old[0] == newHome && old[1] == newAway)) {
                        continue;
                    }
                    try {
                        MatchCommonLogDto logD = new MatchCommonLogDto();
                        logD.setSportId(TableTennisConstant.SPORT_ID);
                        logD.setThirdMatchId(editScoreV2Dto.getThirdMatchId());
                        logD.setLinkedId(editScoreV2Dto.getLinkedId() + "_set_" + periodId);
                        logD.setEventCode("batch_edit_set_scores");
                        logD.setPeriodId(periodId);
                        logD.setOperatorId(editScoreV2Dto.getOperatorId());
                        logD.setOperatorName(editScoreV2Dto.getOperatorName());
                        logD.setIpAddress(editScoreV2Dto.getIpAddress());
                        logD.setLanguage(editScoreV2Dto.getLanguage());
                        logD.setBeforeVal(old[0] + "-" + old[1]);
                        logD.setAfterVal(newHome + "-" + newAway);
                        matchScorePdLogService.setMatchCommonLog(logVo, logD);
                    } catch (Exception logEx) {
                        log.error("[MatchTableTennisServiceImpl]batchEditScores PD log skipped match:{} linkId::{} periodId:{} err:{}",
                                editScoreV2Dto.getThirdMatchId(), editScoreV2Dto.getLinkedId(), periodId, logEx.getMessage());
                    }
                }
                if (logVo != null && logVo.getStandardMatchInfo() != null) {
                    Long standardMatchId = logVo.getStandardMatchInfo().getId();
                    Long eventPeriodId = changedPeriodIds.get(0);
                    TableTennisV2Scores ps = all != null ? all.get(eventPeriodId) : null;
                    CommonItem periodSetScore = ps != null ? ps.getSetScore() : null;

                    EventOperationV2Dto eventOpDto = EventOperationV2Dto.builder()
                            .sportId(TableTennisConstant.SPORT_ID)
                            .thirdMatchId(editScoreV2Dto.getThirdMatchId())
                            .eventCode("batch_edit_set_scores")
                            .homeAway("all")
                            .secondFromStart(0L)
                            .build();
                    eventOpDto.setLinkedId(editScoreV2Dto.getLinkedId());
                    eventOpDto.setOperatorId(editScoreV2Dto.getOperatorId());
                    eventOpDto.setOperatorName(editScoreV2Dto.getOperatorName());
                    eventOpDto.setIpAddress(editScoreV2Dto.getIpAddress());
                    eventOpDto.setLanguage(editScoreV2Dto.getLanguage());

                    MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(
                            logVo, eventOpDto, 0, 0L, eventPeriodId);
                    matchEventInfoDTO.setCopyLinkId("PD_" + UUID.randomUUID().toString());
                    matchEventInfoDTO.setThirdEventId(matchEventInfoDTO.getCopyLinkId());
                    matchEventInfoDTO.setStandardMatchId(standardMatchId);
                    matchEventInfoDTO.setExtrainfo("edit-scores");
                    matchEventInfoDTO.setRemark(editScoreV2Dto.getOperatorName());
                    matchEventInfoDTO.setAddition5("1");
                    matchEventInfoDTO.setT1(whole.getMatchScore().getHome() != null
                            ? whole.getMatchScore().getHome() : 0);
                    matchEventInfoDTO.setT2(whole.getMatchScore().getAway() != null
                            ? whole.getMatchScore().getAway() : 0);
                    if (periodSetScore != null) {
                        matchEventInfoDTO.setFirstT1(periodSetScore.getHome() != null
                                ? periodSetScore.getHome() : 0);
                        matchEventInfoDTO.setFirstT2(periodSetScore.getAway() != null
                                ? periodSetScore.getAway() : 0);
                    }

                    MatchCommonLogDto logD = new MatchCommonLogDto();
                    logD.setSportId(TableTennisConstant.SPORT_ID);
                    logD.setThirdMatchId(editScoreV2Dto.getThirdMatchId());
                    logD.setLinkedId(editScoreV2Dto.getLinkedId() + "_event");
                    logD.setEventCode("batch_edit_set_scores");
                    logD.setPeriodId(eventPeriodId);
                    logD.setOperatorId(editScoreV2Dto.getOperatorId());
                    logD.setOperatorName(editScoreV2Dto.getOperatorName());
                    logD.setIpAddress(editScoreV2Dto.getIpAddress());
                    logD.setLanguage(editScoreV2Dto.getLanguage());
                    logD.setBeforeVal("-");
                    logD.setAfterVal("edit-scores");

                    matchScoreCommonHelper.commonProcess(logVo, eventOpDto, matchEventInfoDTO, logD);
                }
            }

            log.info("[MatchTableTennisServiceImpl]batchEditScores end linkId::{} updated:{}",
                    editScoreV2Dto.getLinkedId(), editMap.size());
            return Response.success();
        } catch (Exception e) {
            log.error("[MatchTableTennisServiceImpl]batchEditScores 处理异常 linkId::{}",
                    editScoreV2Dto.getLinkedId(), e);
            throw new RuntimeException("批量编辑比分失败: " + e.getMessage(), e);
        }
    }

    private Response<Map<String, Object>> validateEditScores(Map<Long, CommonItem> editMap,
                                                               MatchScoresInfo info, String linkedId) {
        if (editMap == null || editMap.isEmpty() || info == null) {
            return null;
        }
        for (Map.Entry<Long, CommonItem> entry : editMap.entrySet()) {
            Long periodId = entry.getKey();
            CommonItem newScore = entry.getValue();
            if (periodId == null || newScore == null) {
                continue;
            }
            if (!TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(periodId)) {
                continue;
            }
            int h = newScore.getHome() != null ? newScore.getHome() : 0;
            int a = newScore.getAway() != null ? newScore.getAway() : 0;
            if (h < 0 || a < 0) {
                int setNum = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.get(periodId);
                String msg = String.format("第%d局比分 %d:%d 不合法（不允许负分）", setNum, h, a);
                log.error("[MatchTableTennisServiceImpl]batchEditScores 拦截：比分含负数 linkId::{} pid:{} score:{}:{}",
                        linkedId, periodId, h, a);
                return cannotEditScoreResponse(msg);
            }
            int higher = Math.max(h, a);
            int lower = Math.min(h, a);
            if (higher > TableTennisConstant.NORMAL_SET_MIN_SCORE
                    && higher - lower > TableTennisConstant.MIN_SCORE_DIFF) {
                int setNum = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.get(periodId);
                String msg = String.format("第%d局比分 %d:%d 不合法（超过 %d 分时分差不能大于 %d）",
                        setNum, h, a, TableTennisConstant.NORMAL_SET_MIN_SCORE,
                        TableTennisConstant.MIN_SCORE_DIFF);
                log.error("[MatchTableTennisServiceImpl]batchEditScores 拦截：分差超出规则 linkId::{} pid:{} score:{}:{} target:{}",
                        linkedId, periodId, h, a, TableTennisConstant.NORMAL_SET_MIN_SCORE);
                return cannotEditScoreResponse(msg);
            }
        }
        return null;
    }

    private Response<Map<String, Object>> cannotEditScoreResponse(String msg) {
        Map<String, Object> data = new HashMap<>();
        data.put("cannotEditScore", true);
        return Response.success(data, msg);
    }

    // ---------------------------------------------------------------- helpers

    private Map<Long, TableTennisV2Scores> parseScoresJson(String scoresJson) {
        if (StringUtils.isBlank(scoresJson)) {
            Map<Long, TableTennisV2Scores> map = new HashMap<>();
            map.put(WHOLE_MATCH, new TableTennisV2Scores());
            return map;
        }
        JSONObject parsed = JSONObject.parseObject(scoresJson);
        Map<Long, TableTennisV2Scores> map = JsonMapUtils.parseTableTennisV2Map(parsed);
        if (!map.containsKey(WHOLE_MATCH)) {
            map.put(WHOLE_MATCH, new TableTennisV2Scores());
        }
        return map;
    }

    private CommonItem computeOverallSetScore(Map<Long, TableTennisV2Scores> all) {
        int home = 0;
        int away = 0;
        if (all == null) {
            return new CommonItem(0, 0);
        }
        for (Map.Entry<Long, TableTennisV2Scores> entry : all.entrySet()) {
            Long pid = entry.getKey();
            if (pid == null || pid.equals(WHOLE_MATCH)) {
                continue;
            }
            if (!TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(pid)) {
                continue;
            }
            TableTennisV2Scores scores = entry.getValue();
            CommonItem set = scores != null ? scores.getSetScore() : null;
            if (set == null) {
                continue;
            }
            if (set.getHome() != null) {
                home += set.getHome();
            }
            if (set.getAway() != null) {
                away += set.getAway();
            }
        }
        return new CommonItem(home, away);
    }

    private CommonItem computeOverallMatchScore(Map<Long, TableTennisV2Scores> all,
                                                 Integer matchLength, Long excludePeriodId) {
        int home = 0;
        int away = 0;
        if (all == null) {
            return new CommonItem(0, 0);
        }
        for (Map.Entry<Long, TableTennisV2Scores> entry : all.entrySet()) {
            Long pid = entry.getKey();
            if (pid == null || pid.equals(WHOLE_MATCH)) {
                continue;
            }
            if (!TableTennisConstant.TABLE_TENNIS_SET_BEGIN.containsKey(pid)) {
                continue;
            }
            if (pid.equals(excludePeriodId)) {
                continue;
            }
            TableTennisV2Scores scores = entry.getValue();
            CommonItem set = scores != null ? scores.getSetScore() : null;
            if (set == null || set.getHome() == null || set.getAway() == null) {
                continue;
            }
            int ph = set.getHome();
            int pa = set.getAway();
            int target = TableTennisConstant.NORMAL_SET_MIN_SCORE;
            int diff = TableTennisConstant.MIN_SCORE_DIFF;
            if (ph >= target && ph - pa >= diff) {
                home += 1;
            } else if (pa >= target && pa - ph >= diff) {
                away += 1;
            }
        }
        return new CommonItem(home, away);
    }

    private static boolean isZero(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue() == 0L;
        }
        String s = String.valueOf(value).trim();
        if (s.isEmpty()) {
            return true;
        }
        try {
            return Long.parseLong(s) == 0L;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean toBool(Object value) {
        return value != null
                && (Boolean.TRUE.equals(value) || "true".equalsIgnoreCase(String.valueOf(value)));
    }

    private static String receivingSide(TableTennisEventTypeEnum type, String homeAway) {
        if (homeAway == null) {
            return null;
        }
        if (type != null && Boolean.TRUE.equals(type.getOpposite())) {
            return TeamTypeConstant.HOME.equals(homeAway) ? TeamTypeConstant.AWAY : TeamTypeConstant.HOME;
        }
        return homeAway;
    }

    private static String buildBatchEditSetScoreDiff(Map<Long, CommonItem> editScoreMap,
                                                      Map<Long, int[]> oldSetScores) {
        if (editScoreMap == null || oldSetScores == null) {
            return "";
        }
        List<Long> periodIds = new ArrayList<>();
        for (Long p : editScoreMap.keySet()) {
            if (p != null) {
                periodIds.add(p);
            }
        }
        Collections.sort(periodIds);
        List<String> parts = new ArrayList<>();
        for (Long periodId : periodIds) {
            CommonItem edit = editScoreMap.get(periodId);
            if (edit == null) {
                continue;
            }
            int nh = batchEditNz(edit.getHome());
            int na = batchEditNz(edit.getAway());
            int[] old = oldSetScores.get(periodId);
            if (old == null) {
                continue;
            }
            if (old[0] == nh && old[1] == na) {
                continue;
            }
            parts.add(String.format("%s:{home:%d,away:%d}->{home:%d,away:%d}",
                    setLabel(periodId), old[0], old[1], nh, na));
        }
        if (parts.isEmpty()) {
            return "";
        }
        return "[" + String.join(", ", parts) + "]";
    }

    private static int batchEditNz(Integer v) {
        return v == null ? 0 : v;
    }

    private static String setLabel(Long periodId) {
        if (periodId == null) {
            return "set?";
        }
        Integer n = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.get(periodId);
        if (n != null) {
            return "set" + n + "(periodId=" + periodId + ")";
        }
        return "periodId=" + periodId;
    }

    /**
     * 把乒乓球 PA scoresJson 的局编号从 8/9/10/11/12/441/442 转换成简单序号 1-7 后返回，
     * 供 standard MQ 下游使用。与 VolleyballCalculationServiceImpl.buildStandardMatchScoreByMap 对齐。
     * 不在 TABLE_TENNIS_SET_BEGIN 中的 key（如 -1 全场桶、80 中断、999 结束）保持原样。
     */
    private String buildTableTennisStandardMatchScoreMap(String scoresJson, String linkId) {
        if (StringUtils.isEmpty(scoresJson)) {
            return scoresJson;
        }
        try {
            JSONObject src = JSONObject.parseObject(scoresJson);
            JSONObject dst = new JSONObject();
            for (String key : src.keySet()) {
                String newKey = key;
                try {
                    Integer mapped = TableTennisConstant.TABLE_TENNIS_SET_BEGIN.get(Long.valueOf(key));
                    if (mapped != null) {
                        newKey = mapped.toString();
                    }
                } catch (NumberFormatException nfe) {
                    // 非数字 key 直接原样保留
                }
                dst.put(newKey, src.get(key));
            }
            log.info("[MatchTableTennisServiceImpl]buildTableTennisStandardMatchScoreMap linkId:{} mapped:{}", linkId, dst);
            return dst.toJSONString();
        } catch (Exception e) {
            log.error("[MatchTableTennisServiceImpl]buildTableTennisStandardMatchScoreMap error linkId:{}",
                    linkId, e);
            return scoresJson;
        }
    }
}
