package com.panda.merge.volleyball.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.constant.*;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.VolleyballScores;
import com.panda.merge.dto.advertise.PDFootBallEventDto;
import com.panda.merge.dto.advertise.v2.*;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.model.MatchScoresEventInfoExample;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.calculation.impl.VolleyballCalculationServiceImpl;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.snooker.dto.MatchCommonLogDto;
import com.panda.merge.snooker.service.impl.AbsMatchCommonProcessor;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.volleyball.converter.VolleyballPDOperationLogConverter;
import com.panda.merge.volleyball.converter.VolleyballScoreListConverter;
import com.panda.merge.volleyball.dto.VolleyballV2Scores;
import com.panda.merge.volleyball.service.MatchVolleyballService;
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
 * 排球报球板处理器：sportId = 9。
 * <p>
 * 通过继承 {@link AbsMatchCommonProcessor} 接管 MatchFactory 分发到 9 号球种的所有公共动作
 * （kickOff / changeScore / changeMatchPeriod / changeMatchStatus / deleteEvent / eventList / 热键）。
 * 排球独有的 scoreList / batchEditScores 通过 {@link MatchVolleyballService} 暴露给 Dubbo 控制器。
 */
@Service
@Slf4j
public class MatchVolleyballServiceImpl extends AbsMatchCommonProcessor<Object> implements MatchVolleyballService {

    // eventOperationConverter / eventProducer / pdMatchInfoRepository / redisUtils /
    // matchScorePdLogService / matchScoreCommonHelper / pdOperationLogConverter /
    // commonAdvertiseService 均继承自父类 AbsMatchCommonProcessor，无需重复声明。
    // 父类注入的 pdOperationLogConverter 是斯诺克版（局标签走 SNOOKER_SET_BEGIN/END），
    // 排球的 changePeriod / deleteEvent 走下面这个专属 converter，
    // 局标签按 VOLLEYBALL_SET_BEGIN/END 渲染。
    @Resource
    private MatchScoreInfoRepository matchScoreInfoRepository;
    @Resource
    private VolleyballScoreListConverter volleyballScoreListConverter;
    @Resource
    private VolleyballPDOperationLogConverter volleyballPdOperationLogConverter;
    @Resource
    private VolleyballCalculationServiceImpl volleyballCalculationServiceImpl;
    @Resource
    private ScoresProducer scoresProducer;

    /**
     * 排球 controlType=4（比赛结束）时前端允许传入的结束阶段：
     * - 93/94 WALKOVER（一方未参赛），95/96 RETIRED（一方退赛），999 正常完赛。
     * 见 {@code MatchPeriodForMatchOverEnum}。其它值视为未指定，回退到 999L。
     */
    private static final Set<Long> VOLLEYBALL_MATCH_END_PERIODS =
            new HashSet<>(Arrays.asList(93L, 94L, 95L, 96L, 999L));

    @Override
    protected Long sportType() {
        return VolleyballConstant.SPORT_ID;
    }

    /**
     * 排球「发球」按钮：前端只调用 /kickOff。后端按当局是否首次点击派发事件，全部 inline，
     * 不再走父类 {@link AbsMatchCommonProcessor#kickOff} 链路：
     * - 当局首次点击 → 写 Redis KICKOFF_FIRST_CLICK + 发 which_team_serves_first（kickoff++）
     * 并接着发 current_serve_volleyball（serve++）；
     * - 之后的点击 → 仅发 current_serve_volleyball（serve++）；
     * - 不论首发与否，最后都刷新 VOLLEYBALL_CURRENT_SERVER。
     * <p>
     * 通过 Redis KICKOFF_FIRST_CLICK 判断「当局是否已记录过先发球」：home/away 全为 null 或 0
     * 视为未记录（changeMatchPeriod 切局时父类已重置为 0/0）。
     * <p>
     * 若 matchTimeInfo.period 还没初始化（新建赛事直接进入报球板），首次 kickOff 隐式将
     * 比赛进入第 1 局（periodId=8），并把 MATCH_CURRENT_PERIOD 同步写入 Redis hash，
     * 这样 getCurrentMatchInfo 才会返回 currentPeriodId=8 而不是 0。
     */
    @Override
    public Response kickOff(MatchScoreAndTimeVo matchScoreAndTimeVo, KickOffV2Dto kickOffV2Dto) throws Exception {
        log.info("[MatchVolleyballServiceImpl]kickOff start linkId::{} whoKickOff:{}", kickOffV2Dto.getLinkedId(), kickOffV2Dto.getWhoKickOff());

        // 中断校验
        if (matchScoreCommonHelper.isMatchInterrupted(kickOffV2Dto.getThirdMatchId())) {
            return Response.failed("比赛已中断，不能进行开球操作");
        }
        if (matchScoreCommonHelper.isMatchEventInterrupted(kickOffV2Dto.getThirdMatchId())) {
            return Response.failed("赛事已中断，不能进行开球操作");
        }

        // 入参规范化：whoKickOff 必须是 home/away 小写
        String who = StringUtils.trimToEmpty(kickOffV2Dto.getWhoKickOff());
        if (!TeamTypeConstant.HOME.equalsIgnoreCase(who) && !TeamTypeConstant.AWAY.equalsIgnoreCase(who)) {
            return Response.failed("请选择发球方 home 或 away");
        }
        String normalized = TeamTypeConstant.HOME.equalsIgnoreCase(who) ? TeamTypeConstant.HOME : TeamTypeConstant.AWAY;
        kickOffV2Dto.setWhoKickOff(normalized);
        int home = TeamTypeConstant.HOME.equals(normalized) ? 1 : 0;
        int away = TeamTypeConstant.AWAY.equals(normalized) ? 1 : 0;

        // 比赛 period 尚未初始化时，首次 kickOff 隐式开第 1 局
        ensureMatchInFirstSet(matchScoreAndTimeVo, kickOffV2Dto);

        boolean firstClickInSet = isFirstServeOfSet(kickOffV2Dto.getThirdMatchId());

        // 1) 当局首次点击：写 KICKOFF_FIRST_CLICK Redis + 发 which_team_serves_first
        if (firstClickInSet) {
            matchScoreCommonHelper.setMatchCacheStatus(kickOffV2Dto.getThirdMatchId(), VolleyballConstant.KICKOFF_FIRST_CLICK, home, away);
            emitVolleyballEvent(matchScoreAndTimeVo, kickOffV2Dto, VolleyballEventTypeEnum.KICK_OFF.getEventCode(), normalized);
        }

        // 2) 不论是否首发，都发 current_serve_volleyball + 刷新当前发球方
        emitVolleyballEvent(matchScoreAndTimeVo, kickOffV2Dto, VolleyballEventTypeEnum.CURRENT_SERVE_VOLLEYBALL.getEventCode(), normalized);
        matchScoreCommonHelper.setMatchCacheStatus(kickOffV2Dto.getThirdMatchId(), VolleyballConstant.VOLLEYBALL_CURRENT_SERVER, home, away);

        log.info("[MatchVolleyballServiceImpl]kickOff end linkId::{} firstClick:{} side:{}", kickOffV2Dto.getLinkedId(), firstClickInSet, normalized);
        return Response.success();
    }

    /**
     * 给定 eventCode + homeAway，构造事件并完成：
     * updateMatchScore（累加 scoresJson 对应统计字段）+ commonProcess（落库 + MQ + 操作日志）。
     * 用于 kickOff 内部 inline 派发 which_team_serves_first / current_serve_volleyball 两条事件。
     */
    private void emitVolleyballEvent(MatchScoreAndTimeVo data, KickOffV2Dto kickOffV2Dto, String eventCode, String homeAway) throws Exception {
        EventOperationV2Dto operationDto = EventOperationV2Dto.builder().sportId(kickOffV2Dto.getSportId()).thirdMatchId(kickOffV2Dto.getThirdMatchId()).eventCode(eventCode).homeAway(homeAway).secondFromStart(kickOffV2Dto.getSecondFromStart() != null ? kickOffV2Dto.getSecondFromStart() : 0L).build();
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

        // 累加统计字段到 scoresJson（kickoff++ 或 serve++）
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

    /**
     * 比赛 period 还没初始化时，把 matchTimeInfo / matchScoresInfo / Redis hash
     * 同步到第 1 局 (periodId=8)。已经在某局的赛事保持不动，
     * 避免覆盖正在进行的局或回看的历史局。
     */
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
        matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(), VolleyballConstant.MATCH_CURRENT_PERIOD, firstSet);
    }

    /**
     * 当局是否还没记录过先发球。
     * KICKOFF_FIRST_CLICK = null 视为未记录；
     * home/away 都为 null 或都为 0（changeMatchPeriod 切局时被父类置零）也视为未记录。
     */
    private boolean isFirstServeOfSet(Long thirdMatchId) {
        CommonItem item = matchScoreCommonHelper.getMatchCacheStatus(thirdMatchId, VolleyballConstant.KICKOFF_FIRST_CLICK);
        if (item == null) {
            return true;
        }
        Integer home = item.getHome();
        Integer away = item.getAway();
        return (home == null || home == 0) && (away == null || away == 0);
    }

    // ---------------------------------------------------------------- common processor hooks

    @Override
    public Response getCurrentMatchInfo(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto, MatchScoreAndTimeVo matchScoreAndTimeVo) {
        log.info("[MatchVolleyballServiceImpl]getCurrentMatchInfo start linkId::{}", changeMatchPeriodV2Dto.getLinkedId());
        if (matchScoreAndTimeVo == null) {
            return Response.failed("matchScoreAndTimeVo 为空，三方赛事不存在或尚未初始化");
        }
        MatchScoresInfo info = matchScoreAndTimeVo.getMatchScoresInfo();
        Map<Long, VolleyballV2Scores> all = parseScoresJson(info != null ? info.getScoresJson() : null);

        Long requestPeriod = changeMatchPeriodV2Dto.getPeriodId();
        Long rawPeriod = requestPeriod != null && requestPeriod != 0 ? requestPeriod : (info != null && info.getPeriod() != null && info.getPeriod() != 0 ? info.getPeriod() : 0L);
        // scoresJson 只有 SET_BEGIN key，SET_END 需先翻译再查找
        Long lookupPeriod = toSetBeginKey(rawPeriod);
        VolleyballV2Scores periodScores = all.getOrDefault(lookupPeriod, new VolleyballV2Scores());
        VolleyballV2Scores wholeScores = all.getOrDefault(WHOLE_MATCH, new VolleyballV2Scores());

        PDVolleyballEventDto eventDto = volleyballScoreListConverter.toPdEventDto(periodScores, lookupPeriod != null ? lookupPeriod.intValue() : null, String.valueOf(changeMatchPeriodV2Dto.getThirdMatchId()), changeMatchPeriodV2Dto.getSportId());

        com.panda.merge.cache.CommonItem matchScoreItem = new com.panda.merge.cache.CommonItem();
        if (wholeScores.getMatchScore() != null) {
            matchScoreItem.setHome(wholeScores.getMatchScore().getHome());
            matchScoreItem.setAway(wholeScores.getMatchScore().getAway());
        }
        eventDto.setMatchScore(matchScoreItem);

        Map<String, Object> matchStatus = matchScoreCommonHelper.getMatchCacheStatus(changeMatchPeriodV2Dto.getThirdMatchId());
        // CONTROL_TYPE 可能被斯诺克遗留值（CommonItem JSON、""、"--" 等）污染：
        // String.valueOf(...) 后 Integer.valueOf 会抛 NumberFormatException，
        // 这里降级为 warn 并放弃该字段，避免整个接口 500。
        Object controlTypeRaw = matchStatus.get(VolleyballConstant.CONTROL_TYPE);
        if (controlTypeRaw != null) {
            try {
                eventDto.setControlType(Integer.valueOf(String.valueOf(controlTypeRaw)));
            } catch (NumberFormatException nfe) {
                log.error("[MatchVolleyballServiceImpl]getCurrentMatchInfo controlType not numeric linkId::{} raw:{}",
                        changeMatchPeriodV2Dto.getLinkedId(), controlTypeRaw);
            }
        }
        // currentPeriodId：以 MatchTimeInfo/MatchScoresInfo.period（DB 落地值）为权威，
        // Redis 缓存仅在其确实拿不到 DB 值时才兜底。当 DB 有值时主动覆写 Redis，消除
        // ensureMatchInFirstSet / ct=1 等旧路径污染 Redis 后导致前端与后端认知当前阶段不一致的问题。
        Long dbPeriod = null;
        if (matchScoreAndTimeVo.getMatchTimeInfo() != null
                && matchScoreAndTimeVo.getMatchTimeInfo().getPeriod() != null
                && matchScoreAndTimeVo.getMatchTimeInfo().getPeriod() != 0L) {
            dbPeriod = matchScoreAndTimeVo.getMatchTimeInfo().getPeriod();
        } else if (info != null && info.getPeriod() != null && info.getPeriod() != 0L) {
            dbPeriod = info.getPeriod();
        }
        if (dbPeriod != null) {
            matchStatus.put(VolleyballConstant.MATCH_CURRENT_PERIOD, dbPeriod);
        } else {
            Object cachedPeriod = matchStatus.get(VolleyballConstant.MATCH_CURRENT_PERIOD);
            if (cachedPeriod != null && !isZero(cachedPeriod)) {
                matchStatus.put(VolleyballConstant.MATCH_CURRENT_PERIOD, cachedPeriod);
            } else {
                matchStatus.put(VolleyballConstant.MATCH_CURRENT_PERIOD, 0L);
            }
        }

        // 赛制 matchLength：time/scores 为 null 或 0 时从 standardMatchInfo 回退；开赛前尝试经父类
        // changeMatchLength 反写一次，让 time/scores/third/standard 四处对齐（开赛后父类会拒绝）。
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
            Integer fromStd = null;
            if (std != null) {
                fromStd = std.getMatchLength();
                if (fromStd == null || fromStd == 0) {
                    fromStd = std.getRoundType();
                }
            }
            if (fromStd != null && fromStd > 0) {
                boolean preMatch = timeInfo == null || timeInfo.getPeriod() == null || timeInfo.getPeriod() <= 0;
                if (preMatch) {
                    // 开赛前尝试反写 standardMatchInfo.matchLength 到 time/scores 三处对齐；
                    // 抛错时（父类 changeMatchLength 在某些状态下会 throw）继续返回 fromStd，
                    // 避免内联同步把整个 getCurrentMatchInfo 拖到 500。
                    try {
                        ChangeMatchLengthV2Dto lenDto = new ChangeMatchLengthV2Dto();
                        lenDto.setSportId(changeMatchPeriodV2Dto.getSportId());
                        lenDto.setThirdMatchId(changeMatchPeriodV2Dto.getThirdMatchId());
                        lenDto.setMinutes(fromStd);
                        lenDto.setLinkedId(changeMatchPeriodV2Dto.getLinkedId());
                        lenDto.setOperatorId(changeMatchPeriodV2Dto.getOperatorId());
                        lenDto.setOperatorName(changeMatchPeriodV2Dto.getOperatorName());
                        lenDto.setIpAddress(changeMatchPeriodV2Dto.getIpAddress());
                        lenDto.setLanguage(changeMatchPeriodV2Dto.getLanguage());
                        Response lenResp = changeMatchLength(matchScoreAndTimeVo, lenDto);
                        if (!lenResp.isSuccess()) {
                            log.info("[MatchVolleyballServiceImpl]getCurrentMatchInfo sync matchLength from standard failed linkId::{} msg:{}",
                                    changeMatchPeriodV2Dto.getLinkedId(), lenResp.getMsg());
                        }
                    } catch (Exception lenEx) {
                        log.error("[MatchVolleyballServiceImpl]getCurrentMatchInfo sync matchLength threw linkId::{} err:{}",
                                changeMatchPeriodV2Dto.getLinkedId(), lenEx.getMessage());
                    }
                }
                matchLength = fromStd;
            }
        }
        if (matchLength != null) {
            matchStatus.put("matchLength", matchLength);
        }

        // 暂停/中断标志规范化：父类 procInterruptedEvent 写 Boolean，Redis 序列化后可能成 String "true"/"false"；
        // 且字段在「未触发过对应 controlType」时根本不存在。这里统一转布尔并保证始终在响应里出现：
        // - matchInterrupted     = ct=2/3 切换的 timeout 状态
        // - matchEventInterrupted = ct=5/6 切换的 suspension 状态
        matchStatus.put(VolleyballConstant.MATCH_INTERRUPTED,
                toBool(matchStatus.get(VolleyballConstant.MATCH_INTERRUPTED)));
        matchStatus.put(VolleyballConstant.MATCH_EVENT_INTERRUPTED,
                toBool(matchStatus.get(VolleyballConstant.MATCH_EVENT_INTERRUPTED)));

        PDVolleyballCurMatchInfoDto resp = PDVolleyballCurMatchInfoDto.builder().pdVolleyballEventDto(eventDto).matchStatus(matchStatus).build();
        log.info("[MatchVolleyballServiceImpl]getCurrentMatchInfo end linkId::{}", changeMatchPeriodV2Dto.getLinkedId());
        return Response.success(resp);
    }

    @Override
    public Response eventList(EventListV2Dto eventListDto) {
        log.info("[MatchVolleyballServiceImpl]eventList start linkId::{}", eventListDto.getLinkedId());
        if (eventListDto.getThirdMatchId() == null) {
            return Response.failed("thirdMatchId 不能为空");
        }
        MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
        MatchScoresEventInfoExample.Criteria criteria = example.createCriteria().andThirdMatchIdEqualTo(eventListDto.getThirdMatchId());
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
        log.info("[MatchVolleyballServiceImpl]eventList end linkId::{} size:{}", eventListDto.getLinkedId(), result.size());
        return Response.success(result);
    }

    @Override
    public Response deleteEvent(MatchScoreAndTimeVo data, com.panda.merge.dto.advertise.v2.DeleteEventV2Dto deleteEventDto) throws Exception {
        log.info("[MatchVolleyballServiceImpl]deleteEvent start linkId::{}", deleteEventDto.getLinkedId());
        MatchScoresEventInfo oldEvent = matchScoresEventInfoMapper.selectByPrimaryKey(deleteEventDto.getDeleteEventId());
        if (oldEvent == null) {
            return Response.failed("事件不存在，无法删除");
        }
        oldEvent.setAddition10("1");
        oldEvent.setCanceled(1);
        oldEvent.setModifyTime(System.currentTimeMillis());

        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.changeDelEventToEvent(deleteEventDto, oldEvent.getEventCode(), oldEvent.getHomeAway());
        eventOperationV2Dto.setOperatorId(deleteEventDto.getOperatorId());
        eventOperationV2Dto.setOperatorName(deleteEventDto.getOperatorName());
        eventOperationV2Dto.setIpAddress(deleteEventDto.getIpAddress());
        eventOperationV2Dto.setLanguage(deleteEventDto.getLanguage());

        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(data, eventOperationV2Dto, 0, 0L, null);
        VolleyballEventTypeEnum type = VolleyballEventTypeEnum.getByCode(oldEvent.getEventCode());
        Integer delta = type != null ? type.getScore() : null;
        if (delta != null && delta > 0) {
            int curHome = data.getMatchScoresInfo() != null && data.getMatchScoresInfo().getPeriodT1() != null ? data.getMatchScoresInfo().getPeriodT1() : 0;
            int curAway = data.getMatchScoresInfo() != null && data.getMatchScoresInfo().getPeriodT2() != null ? data.getMatchScoresInfo().getPeriodT2() : 0;
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

        eventProducer.sendPDSnookerEventInfo(matchEventInfoDTO);
        redisUtils.pushFootBallScore(eventOperationV2Dto.getThirdMatchId());
        redisUtils.pushFootBallEvent(eventOperationV2Dto.getThirdMatchId());

        MatchScoresInfo info = data.getMatchScoresInfo();
        int afterH = info != null && info.getPeriodT1() != null ? info.getPeriodT1() : 0;
        int afterA = info != null && info.getPeriodT2() != null ? info.getPeriodT2() : 0;
        String afterVal = afterH + "-" + afterA;
        MatchCommonLogDto logDto = volleyballPdOperationLogConverter.convertDeleteEventToLog(deleteEventDto, beforeVal, afterVal);
        logDto.setOperatorId(deleteEventDto.getOperatorId());
        logDto.setOperatorName(deleteEventDto.getOperatorName());
        logDto.setIpAddress(deleteEventDto.getIpAddress());
        logDto.setLanguage(deleteEventDto.getLanguage());
        matchScorePdLogService.setMatchCommonLog(data, logDto);
        log.info("[MatchVolleyballServiceImpl]deleteEvent end linkId::{}", deleteEventDto.getLinkedId());
        return Response.success();
    }

    /**
     * 比赛状态切换（覆盖父类）：
     * <p>
     * ct=1（开赛）：父类已把 matchTimeInfo / matchScoresInfo / MATCH_CURRENT_PERIOD 同步到第 1 局，
     * 这里额外兜底写入 KICKOFF_FIRST_CLICK / VOLLEYBALL_CURRENT_SERVER / MATCH_INTERRUPTED /
     * MATCH_EVENT_INTERRUPTED 四个 Redis hash 字段，保证下游（getCurrentMatchInfo / ws /
     * 监控等）拿到的报球板状态结构完整一致，而不是 hash 里缺这几个字段。
     * <p>
     * ct=4（结束）：通过 {@link #resolveMatchEndPeriod} 钩子让父类直接使用排球语义的结束阶段
     * （93/94/95/96/999），事件行 + DB period 一次写对，不再像旧版那样事后改写造成
     * "事件行 matchPeriodId=100" 的不一致。super 完成后仍需补一次 Redis 同步：父类的
     * updatePeriodToDb 只写 MySQL，不会刷新 matchTimeInfo / matchScoresInfo 的 Redis
     * 缓存，也不会写 MATCH_CURRENT_PERIOD 哈希字段——getCurrentMatchInfo 依赖这些 Redis 值。
     */
    @Override
    public Response changeMatchStatus(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {

        Integer ct = changeMatchStatusV2Dto.getControlType();
        // ct=1（开赛）：父类已把 matchTimeInfo / matchScoresInfo / MATCH_CURRENT_PERIOD 同步到第 1 局，
        // 这里再兜底写入报球板所需的剩余 Redis hash 字段（首发 / 当前发球方 / 中断标记），
        // 让所有下游（getCurrentMatchInfo / ws / 监控）读取到完整稳定的结构。
        if (Integer.valueOf(1).equals(ct)) {
            Long thirdMatchId = changeMatchStatusV2Dto.getThirdMatchId();
            matchScoreCommonHelper.setMatchCacheStatus(thirdMatchId, VolleyballConstant.KICKOFF_FIRST_CLICK, 0, 0);
            matchScoreCommonHelper.setMatchCacheStatus(thirdMatchId, VolleyballConstant.VOLLEYBALL_CURRENT_SERVER, 0, 0);
            matchScoreCommonHelper.setMatchCacheStatus(thirdMatchId, VolleyballConstant.MATCH_INTERRUPTED, false);
            matchScoreCommonHelper.setMatchCacheStatus(thirdMatchId, VolleyballConstant.MATCH_EVENT_INTERRUPTED, false);
            log.info("[MatchVolleyballServiceImpl]changeMatchStatus ct=1 报球板状态初始化 match:{} linkId::{}",
                    thirdMatchId, changeMatchStatusV2Dto.getLinkedId());
        }
        if (Integer.valueOf(4).equals(ct)) {
            // 此处 in-memory matchTimeInfo/matchScoresInfo 已经被父类 updatePeriodToDb 改成 endPeriod，
            // 这里只需把同样的对象同步进 Redis（setRedisAndMatch* 同时写 DB+Redis，DB 写是冗余但无害）。
            Long endPeriod = resolveMatchEndPeriod(changeMatchStatusV2Dto);

            // 父类 ct=4 只 updatePeriodToDb，不会重算总分；这里按当前各局 setScore 重算 WHOLE_MATCH 的盘分与局分总和，
            // 否则末局（match was ended in N-th set）：
            // - matchScore 漏算末局胜出方
            // - setScore（局分总和）漏算末局得分
            // 都会让 scoreList(setNums=[-1]) / getCurrentMatchInfo 的"总计"停留在末局开局前的快照。
            MatchScoresInfo info = matchScoreAndTimeVo.getMatchScoresInfo();
            if (info != null && StringUtils.isNotBlank(info.getScoresJson())) {
                Map<Long, VolleyballV2Scores> all = parseScoresJson(info.getScoresJson());
                VolleyballV2Scores whole = all.computeIfAbsent(WHOLE_MATCH, k -> new VolleyballV2Scores());
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
            }

            if (matchScoreAndTimeVo.getMatchTimeInfo() != null) {
                pdMatchInfoRepository.setRedisAndMatchTimeInfo(matchScoreAndTimeVo.getMatchTimeInfo(), null);
            }
            if (matchScoreAndTimeVo.getMatchScoresInfo() != null) {
                pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoreAndTimeVo.getMatchScoresInfo(), null);
            }
            matchScoreCommonHelper.setMatchCacheStatus(changeMatchStatusV2Dto.getThirdMatchId(), VolleyballConstant.MATCH_CURRENT_PERIOD, endPeriod);
            log.info("[MatchVolleyballServiceImpl]changeMatchStatus ct=4 比赛结束阶段:{} match:{} linkId::{}",
                    endPeriod, changeMatchStatusV2Dto.getThirdMatchId(), changeMatchStatusV2Dto.getLinkedId());
        }
        //最后下发事件
        Response resp = super.changeMatchStatus(matchScoreAndTimeVo, changeMatchStatusV2Dto);
        return resp;
    }

    /**
     * 排球 ct=4 比赛结束的 periodId 来源：
     * - 前端传入 93/94（WALKOVER）、95/96（RETIRED）、999（正常完赛）→ 直接采用；
     * - 传入 100L（controller 在 periodId 缺省时填的斯诺克语义默认值）→ 静默回退到 999；
     * - 其它非白名单值 → 回退到 999 并记一条 info，便于排查前端传错。
     */
    @Override
    protected Long resolveMatchEndPeriod(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        Long requested = changeMatchStatusV2Dto.getPeriodId();
        if (requested != null && VOLLEYBALL_MATCH_END_PERIODS.contains(requested)) {
            return requested;
        }
        if (requested != null && !Long.valueOf(100L).equals(requested)) {
            log.info("[MatchVolleyballServiceImpl]resolveMatchEndPeriod 非法的比赛结束阶段:{} 回退到:{} match:{} linkId::{}",
                    requested, VolleyballConstant.PERIOD_MATCH_END,
                    changeMatchStatusV2Dto.getThirdMatchId(), changeMatchStatusV2Dto.getLinkedId());
        }
        return VolleyballConstant.PERIOD_MATCH_END;
    }

    /**
     * 排球状态事件特化：仅 ct=2/3（比赛暂停/继续）改写 eventCode 为 "timeout"/"timeout_over"。
     * <p>
     * ct=5/6（比赛中断/重开）按 spec 走父类默认 "match_status"——"suspension"/"suspension_over"
     * 是英文显示名而非 eventCode，前端通过事件附带的 matchStatus(80/1) + period(80/restore)
     * 自行渲染中文标签。
     */
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

    /**
     * 排球 ct=2/3（比赛暂停/继续）按 spec "赛事阶段不变"，不广播 matchStatus(80/1)
     * 到 standard MQ / standardMatchInfo / matchStatusTopic。否则下游市场会被挂起到 10。
     * ct=5/6（真正的赛事中断/重开）保持默认广播。
     */
    @Override
    protected boolean shouldBroadcastMatchStatus(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        Integer ct = changeMatchStatusV2Dto.getControlType();
        return !(Integer.valueOf(2).equals(ct) || Integer.valueOf(3).equals(ct));
    }

    /**
     * 排球的 periodId 默认值与斯诺克的差异点：
     * - ct=2（比赛暂停 / timeout）：spec "赛事阶段不变"——不能让默认值 80L 把事件行 period 改成中断哨兵，
     *   留 null 让父类回退到 matchTimeInfo.period（当前局）。
     * - 其它 ct（1/4/5）：默认值与斯诺克一致，复用父类逻辑。
     */
    @Override
    protected void applyDefaultPeriodId(ChangeMatchStatusV2Dto changeMatchStatusV2Dto) {
        if (Integer.valueOf(2).equals(changeMatchStatusV2Dto.getControlType())) {
            return;
        }
        super.applyDefaultPeriodId(changeMatchStatusV2Dto);
    }

    /**
     * 排球版本的状态广播：处理两类 null 风险，避免 controlType 已经写进 Redis 后再被 NPE 中断
     * （前端表现为 500 / msg=NullPointerException，但 controlType 已经变更）。
     * - standardMatchInfo 可能为空（测试 / panda-only 赛事未挂 standard 维度）→ 跳过 market-sell 同步段；
     * - dto.dataSourceCode 可能为空 → 用 Objects.equals 替代 .equals(...)。
     * 其余流程（sendStandardMatchStatus / sendMatchStatusTopic）保持与父类一致。
     */
    @Override
    protected void doBroadcastMatchStatus(MatchScoreAndTimeVo matchScoreAndTimeVo,
                                          ChangeMatchStatusV2Dto changeMatchStatusV2Dto,
                                          MatchInfoConvertEnum convertEnum) {
        Integer broadcastStatus = convertEnum.getMatchStatus();
        eventProducer.sendStandardMatchStatus(matchScoreAndTimeVo.getThirdMatchInfo(), changeMatchStatusV2Dto.getLinkedId(), broadcastStatus);
        StandardMatchInfo standardMatchInfo = matchScoreAndTimeVo.getStandardMatchInfo();
        if (standardMatchInfo != null && standardMatchInfo.getId() != null) {
            StandardSportMarketSell marketSell1 = pdMatchInfoRepository.getStandardSportMarketSell(standardMatchInfo.getId(), null);
            if (marketSell1 != null && Objects.equals(changeMatchStatusV2Dto.getDataSourceCode(), marketSell1.getBusinessEvent())) {
                StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
                newStandardMatchInfo.setId(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
                newStandardMatchInfo.setMatchStatus(broadcastStatus == 80 ? 10 : broadcastStatus);
                newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
                pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
            }
        }
        eventProducer.sendMatchStatusTopic(changeMatchStatusV2Dto.getLinkedId(), matchScoreAndTimeVo.getThirdMatchInfo(), broadcastStatus);
    }

    /**
     * 切换赛事阶段（覆盖父类）：父类的实现是按斯诺克语义写的，会在局切换时把
     * SnookerConstant.CURRENT_STRIKER 写进 ronghe:pd:match:status hash —— 排球没有
     * 「持杆人」概念，这条记录会污染 getCurrentMatchInfo 返回的 matchStatus。
     * <p>
     * 这里完整复刻父类流程（生成阶段事件 + 调用本类 addMatchPeriod 落库 + 写
     * MATCH_CURRENT_PERIOD），但只重置排球真正使用的字段：
     * - KICKOFF_FIRST_CLICK：每局重新决定先发球方，置 0,0；
     * - VOLLEYBALL_CURRENT_SERVER：小局切换/休息时无人发球，置 0,0，下一次 kickOff 重新登记。
     * 暂停 (80) 和 比赛结束 (100) 不重置以上两项，保留中断前的状态供恢复。
     */
    @Override
    public Response changeMatchPeriod(MatchScoreAndTimeVo data, ChangeMatchPeriodV2Dto dto) {
        log.info("[MatchVolleyballServiceImpl]changeMatchPeriod start linkId::{}:data:{} restTime:{}", dto.getLinkedId(), dto, dto.getRestTime());

        validateFivbPeriodTransition(data, dto);
        if (dto.getPeriodId() != null) {
            Long currentPeriod = data.getMatchTimeInfo() != null ? data.getMatchTimeInfo().getPeriod() : null;
            if (currentPeriod != null && currentPeriod.equals(dto.getPeriodId())) {
                // 记录详细上下文帮助诊断为什么期已经到达目标
                log.info("[MatchVolleyballServiceImpl]changeMatchPeriod periodId:{}==currentPeriod：{} is already the current period for match:{} linkId::{}, " +
                        "returning success (idempotent)", dto.getPeriodId(), currentPeriod, dto.getThirdMatchId(), dto.getLinkedId());
                // 返回成功而不是失败，以处理UI竞争/幂等情况
                // 例如：当周期由计分逻辑自动切换到休息阶段后用户仍点击"休息"按钮
                return Response.success();
            }
        }

        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.changeMatchPeriodToEvent(dto);
        // 父类 fillBaseOperatorFields 是 private，这里 inline 同等行为
        eventOperationV2Dto.setOperatorId(dto.getOperatorId());
        eventOperationV2Dto.setOperatorName(dto.getOperatorName());
        eventOperationV2Dto.setIpAddress(dto.getIpAddress());
        eventOperationV2Dto.setLanguage(dto.getLanguage());

        Long remainTime = dto.getRestTime() != null ? dto.getRestTime() : 0L;
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(data, eventOperationV2Dto, 0, remainTime, dto.getPeriodId());
        // 保存切换前的 period，用于从 scoresJson 取当前局 setScore
//        Long beforePeriod = data.getMatchScoresInfo().getPeriod();
        Long beforePeriod = dto.getPeriodId();
        log.info("赛事ID:{},下发阶段事件---：beforePeriod：{}--{},，比分：{}:{}",dto.getThirdMatchId(), dto.getPeriodId(), data.getMatchScoresInfo().getPeriod(),data.getMatchScoresInfo().getPeriodT1(),data.getMatchScoresInfo().getPeriodT2());
        addMatchPeriod(data, matchEventInfoDTO);
        JSONObject periodVolleyballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        // 使用 V2 版本解析，与 addMatchPeriod 中存储的类型保持一致
        Map<Long, VolleyballV2Scores> allPeriodScores = JsonMapUtils.parseVolleyballV2Map(periodVolleyballScores);
        // 用切换前的 period 取值，因为 addMatchPeriod 后 period 已变成目标（如 301），
        // scoresJson 中只有 SET_BEGIN（8/9/10...）的 key，没有 SET_END（301/302...）
        VolleyballV2Scores periodScores = allPeriodScores.get(toSetBeginKey(beforePeriod));
        MatchCommonLogDto matchCommonLogDto = volleyballPdOperationLogConverter.convertChangePeriodToLog(dto);
        matchCommonLogDto.setOperatorId(dto.getOperatorId());
        matchCommonLogDto.setOperatorName(dto.getOperatorName());
        matchCommonLogDto.setIpAddress(dto.getIpAddress());
        matchCommonLogDto.setLanguage(dto.getLanguage());
        matchEventInfoDTO.setT1(data.getMatchScoresInfo().getT1());
        matchEventInfoDTO.setT2(data.getMatchScoresInfo().getT2());
        // 从当前局 setScore 取值；休息阶段（301..）的 beforePeriod 不在 scoresJson 中时，data 已有 periodT1/T2 兜底
        if (periodScores != null && periodScores.getSetScore() != null) {
            log.info("赛事ID:{},下发阶段事件000：阶段：{}，比分：{}:{}",dto.getThirdMatchId(), dto.getPeriodId(), data.getMatchScoresInfo().getPeriodT1(),data.getMatchScoresInfo().getPeriodT2());
            matchEventInfoDTO.setFirstT1(periodScores.getSetScore().getHome());
            matchEventInfoDTO.setFirstT2(periodScores.getSetScore().getAway());
        } else {
            log.info("赛事ID:{},下发阶段事件111：阶段：{}，比分：{}:{}",dto.getThirdMatchId(), dto.getPeriodId(), data.getMatchScoresInfo().getPeriodT1(),data.getMatchScoresInfo().getPeriodT2());
            matchEventInfoDTO.setFirstT1(data.getMatchScoresInfo().getPeriodT1());
            matchEventInfoDTO.setFirstT2(data.getMatchScoresInfo().getPeriodT2());
        }


        Long pid = dto.getPeriodId();
        boolean pauseOrEnd = pid != null && (pid.equals(VolleyballConstant.PERIOD_SUSPENDED) || pid.equals(VolleyballConstant.PERIOD_MATCH_END));
        if (!pauseOrEnd) {
            matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(), VolleyballConstant.KICKOFF_FIRST_CLICK, 0, 0);
            matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(), VolleyballConstant.VOLLEYBALL_CURRENT_SERVER, 0, 0);
        }
        matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(), VolleyballConstant.MATCH_CURRENT_PERIOD, dto.getPeriodId());
        log.info("赛事ID:{},下发阶段事件222：阶段：{}，比分：{}:{}",dto.getThirdMatchId(), dto.getPeriodId(), matchEventInfoDTO.getFirstT1(),matchEventInfoDTO.getFirstT2());
        matchScoreCommonHelper.commonProcess(data, eventOperationV2Dto, matchEventInfoDTO, matchCommonLogDto);
        log.info("[MatchVolleyballServiceImpl]changeMatchPeriod end linkId::{}", dto.getLinkedId());
        return Response.success();
    }

    /**
     * 校验阶段切换的合法性（跳转规则对齐斯诺克 checkPhase，外加 FIVB 比分校验）。
     * 不合法时抛出 RuntimeException（与斯诺克一致）。
     * <ul>
     *   <li>放行：suspension(80) / walkover-retired(93/94/95/96)；</li>
     *   <li>scope 与斯诺克一致：仅对 SET_BEGIN / SET_END 目标做校验，其它（含 PERIOD_MATCH_END=999）直接放行；</li>
     *   <li>跳转到 SET_BEGIN：仅当 a) 未开赛跳到第 1 局；b) 该局已在 scoresJson 出现（自由回跳）；
     *       c) 当前处于第 N 局小局休息(SET_END)，跳到第 N+1 局开始；</li>
     *   <li>跳转到 SET_END：仅当 a) 该局已在 scoresJson 出现（回跳已结束小局）；
     *       b) 当前处于本局 SET_BEGIN 关本局；</li>
     *   <li>从本局 SET_BEGIN 关本局（SET_END）时附加 FIVB 胜局校验
     *       （普通局 25 / 决胜局 15，且领先 ≥ 2）。</li>
     * </ul>
     * target == current 的同局拦截放在 caller，与斯诺克一致。
     */
    private void validateFivbPeriodTransition(MatchScoreAndTimeVo data, ChangeMatchPeriodV2Dto dto) {
        try {
            if (data == null || data.getMatchScoresInfo() == null || data.getMatchTimeInfo() == null || dto == null) {
                return;
            }
            Long currentPid = data.getMatchTimeInfo().getPeriod();
            Long targetPid = dto.getPeriodId();
            if (targetPid == null) {
                return;
            }
            // 1) 放行的目标：暂停、walkover/retired 结束
            if (targetPid.equals(VolleyballConstant.PERIOD_SUSPENDED)
                    || targetPid.equals(93L) || targetPid.equals(94L)
                    || targetPid.equals(95L) || targetPid.equals(96L)) {
                return;
            }

            // 2) 与斯诺克 checkPhase 一致：仅对 SET_BEGIN / SET_END 目标做校验，其它直接放行
            boolean isBeginTarget = VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(targetPid);
            boolean isEndTarget = VolleyballConstant.VOLLEYBALL_SET_END.containsKey(targetPid);
            if (!isBeginTarget && !isEndTarget) {
                return;
            }

            // 当前局序号（currentPid 可能在 SET_BEGIN 或 SET_END）
            Integer currentSetIndex = null;
            if (currentPid != null) {
                if (VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(currentPid)) {
                    currentSetIndex = VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(currentPid);
                } else if (VolleyballConstant.VOLLEYBALL_SET_END.containsKey(currentPid)) {
                    currentSetIndex = VolleyballConstant.VOLLEYBALL_SET_END.get(currentPid);
                }
            }

            // 从 scoresJson 计算已进行过的最高局序号 maxSetIndex
            Map<Long, VolleyballV2Scores> all = parseScoresJson(data.getMatchScoresInfo().getScoresJson());
            int maxSetIndex = 0;
            for (Long pid : all.keySet()) {
                if (VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(pid)) {
                    maxSetIndex = Math.max(maxSetIndex, VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(pid));
                } else if (VolleyballConstant.VOLLEYBALL_SET_END.containsKey(pid)) {
                    maxSetIndex = Math.max(maxSetIndex, VolleyballConstant.VOLLEYBALL_SET_END.get(pid));
                }
            }

            // 3) 跳转合法性（对齐斯诺克 checkPhase）
            Integer targetSetIndex = isBeginTarget
                    ? VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(targetPid)
                    : VolleyballConstant.VOLLEYBALL_SET_END.get(targetPid);
            if (targetSetIndex != null) {
                boolean allowed = false;
                if (isBeginTarget) {
                    // a) 未开赛跳到第 1 局开始
                    if (targetSetIndex == 1 && maxSetIndex == 0) {
                        allowed = true;
                    }
                    // b) 该局已在 scoresJson 出现，允许自由回跳
                    if (!allowed && maxSetIndex > 0 && targetSetIndex <= maxSetIndex) {
                        allowed = true;
                    }
                    // c) 当前在第 N 局小局休息(SET_END)，允许跳到第 N+1 局开始
                    if (!allowed && currentSetIndex != null && currentPid != null
                            && VolleyballConstant.VOLLEYBALL_SET_END.containsKey(currentPid)
                            && targetSetIndex == currentSetIndex + 1) {
                        allowed = true;
                    }
                } else {
                    // a) 该局已在 scoresJson 出现，允许回跳到已结束小局
                    if (maxSetIndex > 0 && targetSetIndex <= maxSetIndex) {
                        allowed = true;
                    }
                    // b) 当前局开始阶段直接跳到当前局结束阶段
                    if (!allowed && currentSetIndex != null && targetSetIndex.equals(currentSetIndex)) {
                        allowed = true;
                    }
                }
                if (!allowed) {
                    log.warn("[MatchVolleyballServiceImpl]changeMatchPeriod illegal period jump, currentPid:{} currentIndex:{} targetPid:{} targetIndex:{} maxIndex:{} linkId::{}",
                            currentPid, currentSetIndex, targetPid, targetSetIndex, maxSetIndex, dto.getLinkedId());
                    throw new RuntimeException("The current target set does not meet the jump conditions");
                }
            }

            // 4) FIVB 比分校验：仅在"从本局 SET_BEGIN 关本局"时触发
            boolean closingCurrentSet = isEndTarget
                    && currentPid != null
                    && VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(currentPid)
                    && currentSetIndex != null
                    && targetSetIndex != null
                    && targetSetIndex.equals(currentSetIndex);
            if (!closingCurrentSet) {
                return;
            }

            VolleyballV2Scores curSet = all.get(currentPid);
            CommonItem setScore = curSet != null ? curSet.getSetScore() : null;
            int sh = setScore != null && setScore.getHome() != null ? setScore.getHome() : 0;
            int sa = setScore != null && setScore.getAway() != null ? setScore.getAway() : 0;

            Integer matchLength = data.getMatchScoresInfo().getMatchLength();
            int target = isDecidingSet(currentPid, matchLength)
                    ? VolleyballConstant.GOLDEN_SET_MIN_SCORE
                    : VolleyballConstant.NORMAL_SET_MIN_SCORE;
            int diff = VolleyballConstant.MIN_SCORE_DIFF;

            boolean homeWon = sh >= target && sh - sa >= diff;
            boolean awayWon = sa >= target && sa - sh >= diff;
            if (!homeWon && !awayWon) {
                log.warn("[MatchVolleyballServiceImpl]changeMatchPeriod current set not finished linkId::{} cur:{} target:{} setScore:{}:{}",
                        dto.getLinkedId(), currentPid, targetPid, sh, sa);
                throw new RuntimeException(String.format("Current set is not finished (score %d:%d), cannot close the set", sh, sa));
            }
        } catch (RuntimeException e) {
            log.error("[MatchVolleyballServiceImpl]changeMatchPeriod validate period error match:{} linkId::{}",
                    dto != null ? dto.getThirdMatchId() : null, dto != null ? dto.getLinkedId() : null, e);
            throw e;
        } catch (Exception e) {
            log.error("[MatchVolleyballServiceImpl]changeMatchPeriod validate period error match:{} linkId::{}",
                    dto != null ? dto.getThirdMatchId() : null, dto != null ? dto.getLinkedId() : null, e);
        }
    }

    /**
     * /batchEditScores 校验：每个被编辑的局必须满足 FIVB 比分约束。
     * - 负分：拒绝；
     * - 超过 target（25/15）时，双方分差必须 ≤ 2（只有 deuce 阶段才能突破 target）。
     */
    private Response validateFivbEditScores(Map<Long, CommonItem> editMap, MatchScoresInfo info, String linkedId) {
        if (editMap == null || editMap.isEmpty() || info == null) {
            return null;
        }
        Integer matchLength = info.getMatchLength();
        for (Map.Entry<Long, CommonItem> entry : editMap.entrySet()) {
            Long periodId = entry.getKey();
            CommonItem newScore = entry.getValue();
            if (periodId == null || newScore == null) {
                continue;
            }
            // 只校验小局开始的 periodId；其它 key（如 -1 全场聚合）跳过
            if (!VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(periodId)) {
                continue;
            }
            int h = newScore.getHome() != null ? newScore.getHome() : 0;
            int a = newScore.getAway() != null ? newScore.getAway() : 0;
            if (h < 0 || a < 0) {
                String msg = String.format("第%d局比分 %d:%d 不合法（不允许负分）",
                        VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(periodId), h, a);
                log.warn("[MatchVolleyballServiceImpl]batchEditScores 拦截：比分含负数 linkId::{} pid:{} score:{}:{}",
                        linkedId, periodId, h, a);
                return cannotEditScoreResponse(msg);
            }
            int target = isDecidingSet(periodId, matchLength)
                    ? VolleyballConstant.GOLDEN_SET_MIN_SCORE
                    : VolleyballConstant.NORMAL_SET_MIN_SCORE;
            int higher = Math.max(h, a);
            int lower = Math.min(h, a);
            if (higher > target && higher - lower > VolleyballConstant.MIN_SCORE_DIFF) {
                String msg = String.format("第%d局比分 %d:%d 不合法（超过 %d 分时分差不能大于 %d）",
                        VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(periodId), h, a, target, VolleyballConstant.MIN_SCORE_DIFF);
                log.warn("[MatchVolleyballServiceImpl]batchEditScores 拦截：分差超出 deuce 规则 linkId::{} pid:{} score:{}:{} target:{}",
                        linkedId, periodId, h, a, target);
                return cannotEditScoreResponse(msg);
            }
        }
        return null;
    }

    /** 决胜局判定：黄金局(442L) 或 当前局号等于赛制总局数（BO3→3, BO5→5, BO7→7）。 */
    private boolean isDecidingSet(Long periodId, Integer matchLength) {
        if (periodId == null) {
            return false;
        }
        if (VolleyballConstant.GOLDEN_SET_BEGIN.equals(periodId)) {
            return true;
        }
        Integer setNum = VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(periodId);
        return setNum != null && matchLength != null && setNum.equals(matchLength);
    }

    private Response<Map<String, Object>> cannotEditScoreResponse(String msg) {
        Map<String, Object> data = new HashMap<>();
        data.put("cannotEditScore", true);
        return Response.success(data, msg);
    }

    @Override
    public void addMatchPeriod(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO) {
        Long targetPeriodId = matchEventInfoDTO.getMatchPeriodId();
        Integer keepHome = data.getMatchScoresInfo().getT1();
        Integer keepAway = data.getMatchScoresInfo().getT2();
        Long previousPeriodId = data.getMatchTimeInfo().getPeriod();

        data.getMatchTimeInfo().setPeriod(targetPeriodId);
        data.getMatchScoresInfo().setPeriod(targetPeriodId);
        Long firstNum = SportPeriodConstant.SnookerPeriod.SnookerPeriodScores.periodMaps.getOrDefault(matchEventInfoDTO.getMatchPeriodId(), null);
        log.info("[EventProducer]addMatchPeriod 设置firstNum:{} linkId::{}:data:{}",matchEventInfoDTO.getCopyLinkId(),firstNum,matchEventInfoDTO);
        matchEventInfoDTO.setFirstNum(firstNum==null?null : Integer.valueOf(firstNum+""));
        Map<Long, VolleyballV2Scores> all = parseScoresJson(data.getMatchScoresInfo().getScoresJson());
        VolleyballV2Scores whole = all.computeIfAbsent(WHOLE_MATCH, k -> new VolleyballV2Scores());

        VolleyballV2Scores periodScores = all.get(targetPeriodId);
        if (periodScores == null && VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(targetPeriodId)) {
            periodScores = new VolleyballV2Scores();
            all.put(targetPeriodId, periodScores);
            data.getMatchScoresInfo().setPeriodT1(0);
            data.getMatchScoresInfo().setPeriodT2(0);
        } else {
            // 盘比分（matchScore）结算时机（与斯诺克 addMatchPeriod 一致）：
            // - 切到 SET_END（小局休息）/ PERIOD_MATCH_END（比赛结束）：按 setScore 重算盘分；
            // - 切到 SET_BEGIN（前进/回看）/ 其它（如 SUSPENDED=80）：盘分不变，仅同步到 MatchScoresInfo。
            if (whole.getMatchScore() == null) {
                whole.setMatchScore(new CommonItem());
            }
            int curHome = whole.getMatchScore().getHome() != null ? whole.getMatchScore().getHome() : 0;
            int curAway = whole.getMatchScore().getAway() != null ? whole.getMatchScore().getAway() : 0;

            boolean isSetEnd = targetPeriodId != null
                    && (VolleyballConstant.VOLLEYBALL_SET_END.containsKey(targetPeriodId)
                    || VolleyballConstant.PERIOD_MATCH_END.equals(targetPeriodId));
            if (isSetEnd) {
                // 切到小局休息/比赛结束前的兜底校验（与斯诺克 addMatchPeriod 同款二次校验一致）：
                // 若上一局仍处于 SET_BEGIN（局内进行中），必须先确认该局已按 FIVB 规则真正分出胜负，
                // 否则不能据此把当前比分当作"赢下一局"去结算盘分——validateFivbPeriodTransition 只在特定跳转形态下触发，
                // 这里作为第二道防线，避免任何绕过前置校验的路径算出错误盘分。
                if (previousPeriodId != null && VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(previousPeriodId)) {
                    VolleyballV2Scores prevSetScores = all.get(previousPeriodId);
                    CommonItem prevSet = prevSetScores != null ? prevSetScores.getSetScore() : null;
                    int ph = prevSet != null && prevSet.getHome() != null ? prevSet.getHome() : 0;
                    int pa = prevSet != null && prevSet.getAway() != null ? prevSet.getAway() : 0;
                    Integer matchLength = data.getMatchScoresInfo().getMatchLength();
                    int target = isDecidingSet(previousPeriodId, matchLength)
                            ? VolleyballConstant.GOLDEN_SET_MIN_SCORE
                            : VolleyballConstant.NORMAL_SET_MIN_SCORE;
                    int diff = VolleyballConstant.MIN_SCORE_DIFF;
                    boolean homeWon = ph >= target && ph - pa >= diff;
                    boolean awayWon = pa >= target && pa - ph >= diff;
                    if (!homeWon && !awayWon) {
                        log.warn("[MatchVolleyballServiceImpl]addMatchPeriod current set not finished linkId::{} previousPeriodId:{} targetPeriodId:{} setScore:{}:{}",
                                matchEventInfoDTO.getCopyLinkId(), previousPeriodId, targetPeriodId, ph, pa);
                        throw new IllegalArgumentException(String.format("局比分未分出胜负(%d:%d)，不能切换到小局休息/比赛结束阶段", ph, pa));
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

        // 回看历史局：恢复全场盘分。用局序号比较而非 raw periodId，因为
        // SET_END（301..）> SET_BEGIN（8..）在数值上，但 SET_END→下局 SET_BEGIN 是正常前进。
        Integer previousSetIndex = null;
        if (previousPeriodId != null) {
            if (VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(previousPeriodId)) {
                previousSetIndex = VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(previousPeriodId);
            } else if (VolleyballConstant.VOLLEYBALL_SET_END.containsKey(previousPeriodId)) {
                previousSetIndex = VolleyballConstant.VOLLEYBALL_SET_END.get(previousPeriodId);
            }
        }
        Integer targetSetIndex = VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(targetPeriodId);
        boolean backToOldSet = targetSetIndex != null
                && previousSetIndex != null
                && targetSetIndex < previousSetIndex;
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

        Integer timeGo = VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(targetPeriodId) ? 1 : 0;
        data.getMatchTimeInfo().setSecondFromStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchTimeInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchTimeInfo().setTimeGo(timeGo);
        data.getMatchTimeInfo().setEventTime(now);
        data.getMatchTimeInfo().setModifyTime(now);

        pdMatchInfoRepository.setRedisAndMatchScoresInfo(data.getMatchScoresInfo(), null);
        pdMatchInfoRepository.setRedisAndMatchTimeInfo(data.getMatchTimeInfo(), null);
    }

    @Override
    public CommonItem updateMatchScore(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO, Boolean isDelete) throws Exception {
        VolleyballEventTypeEnum type = VolleyballEventTypeEnum.getByCode(matchEventInfoDTO.getEventCode());
        if (type == null) {
            log.info("[MatchVolleyballServiceImpl]updateMatchScore unknown eventCode:{} match:{}", matchEventInfoDTO.getEventCode(), data.getThirdMatchInfo() != null ? data.getThirdMatchInfo().getId() : null);
            return new CommonItem(0, 0);
        }

        Map<Long, VolleyballV2Scores> all = parseScoresJson(data.getMatchScoresInfo().getScoresJson());
        VolleyballV2Scores whole = all.computeIfAbsent(WHOLE_MATCH, k -> new VolleyballV2Scores());
        Long curPeriodId = data.getMatchTimeInfo().getPeriod();
        Long effectivePeriodId = curPeriodId != null ? curPeriodId : 8L;
        VolleyballV2Scores periodScores = all.computeIfAbsent(effectivePeriodId, k -> new VolleyballV2Scores());

        CommonItem fieldBefore = whole.getFieldScoreByEventCode(type.getEventCode());
        CommonItem before;
        if (fieldBefore != null) {
            before = new CommonItem(fieldBefore.getHome(), fieldBefore.getAway());
        } else {
            before = new CommonItem(0, 0);
        }

        if (Boolean.TRUE.equals(isDelete) && type.getScore() != null && fieldBefore != null) {
            // 统计字段（如 serviceError / out）按新 doCalculation 语义记在「事件持有人」homeAway 一侧，
            // 因此删除时也只能从这一侧扣减；用 receivingSide 会扣错边，导致永远校验为 0 通过。
            int delta = type.getScore();
            int curHome = fieldBefore.getHome() == null ? 0 : fieldBefore.getHome();
            int curAway = fieldBefore.getAway() == null ? 0 : fieldBefore.getAway();
            String actor = matchEventInfoDTO.getHomeAway();
            if (TeamTypeConstant.AWAY.equals(actor) && curAway - delta < 0) {
                throw new Exception("事件比分不足，删除后比分将小于 0，无法删除");
            }
            if (TeamTypeConstant.HOME.equals(actor) && curHome - delta < 0) {
                throw new Exception("事件比分不足，删除后比分将小于 0，无法删除");
            }
        }

        // 盘比分（matchScore）结算时机（与斯诺克 updateMatchScore 一致）：
        // - 小局进行中（curPeriodId ∈ VOLLEYBALL_SET_BEGIN）：单次得分不应提前结算盘分，
        //   先快照旧值，doCalculation 之后还原，避免开局得 1 分就把 matchScore 算成 1-0；
        // - 已切到 SET_END / PERIOD_MATCH_END 等阶段：照常按 setScore 全量重算盘分。
        CommonItem oriMatchScore = whole.getMatchScore();
        CommonItem matchScoreSnapshot = oriMatchScore != null
                ? new CommonItem(oriMatchScore.getHome(), oriMatchScore.getAway())
                : new CommonItem(0, 0);

        whole.doCalculation(type, matchEventInfoDTO.getHomeAway(), isDelete);
        periodScores.doCalculation(type, matchEventInfoDTO.getHomeAway(), isDelete);

        boolean inPlayingSet = VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(effectivePeriodId);
        if (whole.getMatchScore() == null) {
            whole.setMatchScore(new CommonItem());
        }
        if (inPlayingSet) {
            // 小局进行中：保持盘分不变
            whole.getMatchScore().setHome(matchScoreSnapshot.getHome());
            whole.getMatchScore().setAway(matchScoreSnapshot.getAway());
        } else {
            // 已离开 SET_BEGIN：按已结束局的 setScore 全量重算
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

        matchEventInfoDTO.setFirstT1(periodScores.getSetScore().getHome() != null ? periodScores.getSetScore().getHome() : 0);
        matchEventInfoDTO.setFirstT2(periodScores.getSetScore().getAway() != null ? periodScores.getSetScore().getAway() : 0);
        // 计数类事件（ace/kill/block/...）：t1/t2 携带当前局该字段的累计次数，便于 PD 直接读取
        if (type.isPerSetCounter()) {
            CommonItem periodCounter = periodScores.getFieldScoreByEventCode(type.getEventCode());
            matchEventInfoDTO.setT1(periodCounter != null && periodCounter.getHome() != null ? periodCounter.getHome() : 0);
            matchEventInfoDTO.setT2(periodCounter != null && periodCounter.getAway() != null ? periodCounter.getAway() : 0);
        } else {
            matchEventInfoDTO.setT1(finalMatchScore.getHome());
            matchEventInfoDTO.setT2(finalMatchScore.getAway());
        }

        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(all));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        matchScoreInfoRepository.updateScoresInfo(data.getMatchScoresInfo());
        return before;
    }

//    /**
//     * 改变比分（重写父类方法）
//     * 处理进球和罚分事件
//     */
//    @Override
//    public Response changeScore(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchScoreV2Dto changeMatchScoreV2Dto) throws Exception {
//        log.info("[MatchSnookerServiceImpl]changeScore start linkId::{}:data:{}", changeMatchScoreV2Dto.getLinkedId(), changeMatchScoreV2Dto);
//        // 调用父类方法处理比分更新
//        super.changeScore(matchScoreAndTimeVo, changeMatchScoreV2Dto);
//
//        // 认输或直接判输：本局结束自动切小局休息（存储：concede=认输，directLoss=直接判输）
//        if (SnookerEventTypeEnum.CONCEDE.getEventCode().equals(changeMatchScoreV2Dto.getEventCode())
//                || SnookerEventTypeEnum.FOUL_LOSE.getEventCode().equals(changeMatchScoreV2Dto.getEventCode())) {
//            // 重新查询最新的比分信息（因为父类方法已经更新了比分）
//            MatchScoreAndTimeVo latestData = commonAdvertiseService.searchMatchScoreAndTime(changeMatchScoreV2Dto.getThirdMatchId());
//
//            Long currentPeriodId = latestData.getMatchTimeInfo().getPeriod();
//            Long restPeriodId = SnookerConstant.SNOOKER_SET_BEGIN_TO_END.get(currentPeriodId);
//            if (restPeriodId == null) {
//                log.warn("[MatchSnookerServiceImpl]changeScore cannot find rest period for period:{} match:{} linkId::{}, using default 445",
//                        currentPeriodId, changeMatchScoreV2Dto.getThirdMatchId(), changeMatchScoreV2Dto.getLinkedId());
//                restPeriodId = 445L;
//            }
//            log.info("[MatchSnookerServiceImpl]changeScore frame-loss event (concede/foul_lose), switching to rest period:{} match:{} linkId::{}",
//                    restPeriodId, changeMatchScoreV2Dto.getThirdMatchId(), changeMatchScoreV2Dto.getLinkedId());
//
//            // 切换到小局休息
//            ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto = new ChangeMatchPeriodV2Dto();
//            changeMatchPeriodV2Dto.setSportId(changeMatchScoreV2Dto.getSportId());
//            changeMatchPeriodV2Dto.setThirdMatchId(changeMatchScoreV2Dto.getThirdMatchId());
//            changeMatchPeriodV2Dto.setPeriodId(restPeriodId);
//            changeMatchPeriodV2Dto.setLinkedId(changeMatchScoreV2Dto.getLinkedId() + "_FRAME_LOSS_REST");
//            changeMatchPeriodV2Dto.setRestTime(120L);
//            changeMatchPeriodV2Dto.setOperatorId(changeMatchScoreV2Dto.getOperatorId());
//            changeMatchPeriodV2Dto.setOperatorName(changeMatchScoreV2Dto.getOperatorName());
//            changeMatchPeriodV2Dto.setIpAddress(changeMatchScoreV2Dto.getIpAddress());
//            changeMatchPeriodV2Dto.setLanguage(changeMatchScoreV2Dto.getLanguage());
//
//            // 调用changeMatchPeriod切换到小局休息（会自动计算matchScore）
//            changeMatchPeriod(latestData, changeMatchPeriodV2Dto);
//
//            log.info("[MatchSnookerServiceImpl]changeScore auto switched to rest period after frame-loss match:{} linkId::{}",
//                    changeMatchScoreV2Dto.getThirdMatchId(), changeMatchScoreV2Dto.getLinkedId());
//        }
//
//        log.info("[MatchSnookerServiceImpl]changeScore end linkId::{}", changeMatchScoreV2Dto.getLinkedId());
//        return Response.success();
//    }

    // ---------------------------------------------------------------- volleyball-only endpoints

    @Override
    public Response scoreList(EventListV2Dto eventListV2Dto) {
        log.info("[MatchVolleyballServiceImpl]scoreList start linkId::{}", eventListV2Dto.getLinkedId());
        if (eventListV2Dto.getThirdMatchId() == null) {
            return Response.failed("thirdMatchId 不能为空");
        }
        MatchScoresInfo info = matchScoreInfoRepository.selectByExample(eventListV2Dto.getThirdMatchId(), 1);
        if (info == null || StringUtils.isBlank(info.getScoresJson())) {
            return Response.success(Collections.emptyList());
        }
        Map<Long, VolleyballV2Scores> all = parseScoresJson(info.getScoresJson());
        List<Long> filter = eventListV2Dto.getSetNums();
        boolean returnAll = CollectionUtils.isEmpty(filter) || filter.contains(-1L);
        List<PDVolleyballEventDto> result = new ArrayList<>();
        for (Map.Entry<Long, VolleyballV2Scores> entry : all.entrySet()) {
            if (!returnAll && !filter.contains(entry.getKey())) {
                continue;
            }
            result.add(volleyballScoreListConverter.toPdEventDto(entry.getValue(), entry.getKey().intValue(), String.valueOf(eventListV2Dto.getThirdMatchId()), eventListV2Dto.getSportId()));
        }
        log.info("[MatchVolleyballServiceImpl]scoreList end linkId::{} size:{}", eventListV2Dto.getLinkedId(), result.size());
        return Response.success(result);
    }

    /**
     * 发送辅助事件：kill / block / expulsion / disqualification / penalty / error /
     * current_serve_volleyball。这些事件 score=null 不改 setScore，但会通过 doCalculation
     * 把对应字段（kill / block / expulsion …）的次数累加，从而让 getCurrentMatchInfo
     * 真正反映 spec 响应里的 kill / block / expulsion 等计数。
     */
    @Override
    public Response sendEvent(MatchScoreAndTimeVo data, SendEventDto dto) throws Exception {
        log.info("[MatchVolleyballServiceImpl]sendEvent start linkId::{}:eventCode:{}", dto.getLinkedId(), dto.getEventCode());

        // 1) 构造事件对象
        EventOperationV2Dto eventOperationV2Dto = EventOperationV2Dto.builder().sportId(dto.getSportId()).thirdMatchId(dto.getThirdMatchId()).eventCode(dto.getEventCode()).homeAway(dto.getHomeAway()).secondFromStart(dto.getSecondFromStart() != null ? dto.getSecondFromStart() : 0L).build();
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

        // 2) 关键：累加统计字段到 scoresJson（这是 sendCommonEvent 缺失的部分）
        VolleyballEventTypeEnum type = VolleyballEventTypeEnum.getByCode(dto.getEventCode());
        // 操作日志的修改前/修改后：与 updateMatchScore 算出的 after（matchEventInfoDTO.getT1()/getT2()）口径一致——
        // 计数类事件取当前局该字段的次数，赢分等真实计分事件取盘分，下单据为「0-0 -> 1-0」这种简单数对；
        // score=null 且非计数类的纯发球流程事件（kickoff/current_serve_volleyball）不影响计分，记为 "-"
        String beforeValStr = "-";
        String afterValStr = "-";
        if (type != null) {
            boolean noScoreEffect = type.getScore() == null && !type.isPerSetCounter();
            if (!noScoreEffect) {
                CommonItem beforeVal = new CommonItem(0, 0);
                Map<Long, VolleyballV2Scores> snapshotAll = parseScoresJson(data.getMatchScoresInfo().getScoresJson());
                Long curPeriodId = data.getMatchTimeInfo() != null ? data.getMatchTimeInfo().getPeriod() : null;
                Long effectivePeriodId = curPeriodId != null ? curPeriodId : 8L;
                if (type.isPerSetCounter()) {
                    VolleyballV2Scores periodScoresSnapshot = snapshotAll.get(effectivePeriodId);
                    CommonItem fb = periodScoresSnapshot != null ? periodScoresSnapshot.getFieldScoreByEventCode(type.getEventCode()) : null;
                    if (fb != null) {
                        beforeVal = new CommonItem(fb.getHome() != null ? fb.getHome() : 0, fb.getAway() != null ? fb.getAway() : 0);
                    }
                } else {
                    VolleyballV2Scores wholeSnapshot = snapshotAll.get(WHOLE_MATCH);
                    CommonItem ms = wholeSnapshot != null ? wholeSnapshot.getMatchScore() : null;
                    if (ms != null) {
                        beforeVal = new CommonItem(ms.getHome() != null ? ms.getHome() : 0, ms.getAway() != null ? ms.getAway() : 0);
                    }
                }
                beforeValStr = beforeVal.getHome() + "-" + beforeVal.getAway();
            }
            updateMatchScore(data, matchEventInfoDTO, false);
            if (!noScoreEffect) {
                afterValStr = matchEventInfoDTO.getT1() + "-" + matchEventInfoDTO.getT2();
            }
        } else {
            log.info("[MatchVolleyballServiceImpl]sendEvent unknown eventCode:{} match:{} linkId::{}", dto.getEventCode(), dto.getThirdMatchId(), dto.getLinkedId());
        }
        List<String> eventList = new ArrayList<>(Arrays.asList("penalty","expulsion","block","kill","disqualification"));

        // 3) 落库 + MQ + 推送 + 操作日志
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
        //处罚、驱逐、拦网、扣杀、取消资格，日志不记录前后比分
        if(eventList.contains(dto.getEventCode())){
            logDto.setBeforeVal("-");
            logDto.setAfterVal("-");
        }
        matchScoreCommonHelper.commonProcess(data, eventOperationV2Dto, matchEventInfoDTO, logDto);

        // 4) 排球独有：current_serve_volleyball 同步更新当前发球方
        if (VolleyballEventTypeEnum.CURRENT_SERVE_VOLLEYBALL.getEventCode().equals(dto.getEventCode())) {
            int home = TeamTypeConstant.HOME.equals(dto.getHomeAway()) ? 1 : 0;
            int away = TeamTypeConstant.AWAY.equals(dto.getHomeAway()) ? 1 : 0;
            matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(), VolleyballConstant.VOLLEYBALL_CURRENT_SERVER, home, away);
        }

        log.info("[MatchVolleyballServiceImpl]sendEvent end linkId::{}", dto.getLinkedId());
        return Response.success();
    }

    @Override
    public Response batchEditScores(EditScoreV2Dto editScoreV2Dto) {
        if (editScoreV2Dto == null || editScoreV2Dto.getThirdMatchId() == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        log.info("[MatchVolleyballServiceImpl]batchEditScores start linkId::{}", editScoreV2Dto.getLinkedId());
        try {
            if (StringUtils.isBlank(editScoreV2Dto.getScores())) {
                return Response.success();
            }
            Map<Long, CommonItem> editMap = JSON.parseObject(editScoreV2Dto.getScores(), new TypeReference<Map<Long, CommonItem>>() {
            });
            if (editMap == null || editMap.isEmpty()) {
                return Response.success();
            }
            MatchScoresInfo info = matchScoreInfoRepository.selectByExample(editScoreV2Dto.getThirdMatchId(), 1);
            if (info == null) {
                throw new IllegalArgumentException("未找到对应的比分信息，thirdMatchId: " + editScoreV2Dto.getThirdMatchId());
            }

            Response editGuard = validateFivbEditScores(editMap, info, editScoreV2Dto.getLinkedId());
            if (editGuard != null) {
                return editGuard;
            }

            Map<Long, VolleyballV2Scores> all = parseScoresJson(info.getScoresJson());

            Map<Long, int[]> oldSetSnap = new LinkedHashMap<>();
            for (Map.Entry<Long, CommonItem> editEntry : editMap.entrySet()) {
                Long periodId = editEntry.getKey();
                if (periodId == null || editEntry.getValue() == null) {
                    continue;
                }
                VolleyballV2Scores ps = all.get(periodId);
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
                VolleyballV2Scores ps = all.computeIfAbsent(periodId, k -> new VolleyballV2Scores());
                ps.setSetScore(entry.getValue());
            }

            VolleyballV2Scores whole = all.computeIfAbsent(WHOLE_MATCH, k -> new VolleyballV2Scores());
            CommonItem overall = computeOverallMatchScore(all, info.getMatchLength(), info.getPeriod());
            CommonItem overallSet = computeOverallSetScore(all);
            if (whole.getMatchScore() == null) {
                whole.setMatchScore(new CommonItem());
            }
            whole.getMatchScore().setHome(overall.getHome());
            whole.getMatchScore().setAway(overall.getAway());
            // 各局 setScore 被直接覆盖（非 doCalculation 累加路径），需重算 WHOLE_MATCH 局分总和
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
                // 推送三方比分到操盘/结算2.0（THIRD_MATCH_SCORES），与斯诺克 batchEditScores 的 "_EDIT_SCORE" 推送对齐；
                // 局编号需先转换成标准序号，否则下游按 1.. 顺序解析会读到排球内部的 8/9/10.. 局ID
                MatchScoresInfo standardInfo = new MatchScoresInfo();
                BeanUtils.copyProperties(info, standardInfo);
                standardInfo.setScoresJson(volleyballCalculationServiceImpl.buildStandardMatchScoreByMap(info.getScoresJson(), editScoreV2Dto.getLinkedId()));
                scoresProducer.sendToMQ(thirdMatchInfo, standardInfo, editScoreV2Dto.getLinkedId() + "_EDIT_SCORE", "batch_edit_set_scores");
            }

            redisUtils.pushFootBallScore(editScoreV2Dto.getThirdMatchId());
            redisUtils.pushFootBallEvent(editScoreV2Dto.getThirdMatchId());

            String diffLog = buildBatchEditSetScoreDiff(editMap, oldSetSnap);
            if (StringUtils.isNotBlank(diffLog)) {
                log.info("[MatchVolleyballServiceImpl]batchEditScores diff linkId::{} {}", editScoreV2Dto.getLinkedId(), diffLog);
                MatchScoreAndTimeVo logVo = commonAdvertiseService.searchMatchScoreAndTime(editScoreV2Dto.getThirdMatchId());
                List<Long> changedPeriodIds = editMap.keySet().stream().filter(Objects::nonNull).sorted().collect(Collectors.toList());
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
                        MatchCommonLogDto logDto = new MatchCommonLogDto();
                        logDto.setSportId(VolleyballConstant.SPORT_ID);
                        logDto.setThirdMatchId(editScoreV2Dto.getThirdMatchId());
                        logDto.setLinkedId(editScoreV2Dto.getLinkedId() + "_set_" + periodId);
                        logDto.setEventCode("batch_edit_set_scores");
                        logDto.setPeriodId(periodId);
                        logDto.setOperatorId(editScoreV2Dto.getOperatorId());
                        logDto.setOperatorName(editScoreV2Dto.getOperatorName());
                        logDto.setIpAddress(editScoreV2Dto.getIpAddress());
                        logDto.setLanguage(editScoreV2Dto.getLanguage());
                        logDto.setBeforeVal(old[0] + "-" + old[1]);
                        logDto.setAfterVal(newHome + "-" + newAway);
                        matchScorePdLogService.setMatchCommonLog(logVo, logDto);
                    } catch (Exception logEx) {
                        log.error("[MatchVolleyballServiceImpl]batchEditScores PD log skipped match:{} linkId::{} periodId:{} err:{}",
                                editScoreV2Dto.getThirdMatchId(), editScoreV2Dto.getLinkedId(), periodId, logEx.getMessage());
                    }
                }
                // 在循环外统一发送一次事件，使用编辑后的最新比分
                if (logVo != null && logVo.getStandardMatchInfo() != null) {
                    Long standardMatchId = logVo.getStandardMatchInfo().getId();
                    Long eventPeriodId = changedPeriodIds.get(0);
                    VolleyballV2Scores ps = all != null ? all.get(eventPeriodId) : null;
                    CommonItem periodSetScore = ps != null ? ps.getSetScore() : null;

                    // 通过 commonProcess 统一处理事件发送（自动走 isProfessionalSavePath → sendPDSnookerEventInfo）
                    EventOperationV2Dto eventOpDto = EventOperationV2Dto.builder()
                            .sportId(VolleyballConstant.SPORT_ID)
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

                    MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(logVo, eventOpDto, 0, 0L, eventPeriodId);
                    matchEventInfoDTO.setCopyLinkId("PD_" + UUID.randomUUID());
                    matchEventInfoDTO.setThirdEventId(matchEventInfoDTO.getCopyLinkId());
                    matchEventInfoDTO.setStandardMatchId(standardMatchId);
                    matchEventInfoDTO.setExtrainfo("edit-scores");
                    matchEventInfoDTO.setRemark(editScoreV2Dto.getOperatorName());
                    matchEventInfoDTO.setAddition5("1");
                    // t1/t2 使用当前编辑局的 setScore（局分），而非全场盘分
                    if (periodSetScore != null) {
                        matchEventInfoDTO.setT1(periodSetScore.getHome() != null ? periodSetScore.getHome() : 0);
                        matchEventInfoDTO.setT2(periodSetScore.getAway() != null ? periodSetScore.getAway() : 0);
                        matchEventInfoDTO.setFirstT1(periodSetScore.getHome() != null ? periodSetScore.getHome() : 0);
                        matchEventInfoDTO.setFirstT2(periodSetScore.getAway() != null ? periodSetScore.getAway() : 0);
                    } else {
                        matchEventInfoDTO.setT1(0);
                        matchEventInfoDTO.setT2(0);
                        matchEventInfoDTO.setFirstT1(0);
                        matchEventInfoDTO.setFirstT2(0);
                    }

                    MatchCommonLogDto logDto = new MatchCommonLogDto();
                    logDto.setSportId(VolleyballConstant.SPORT_ID);
                    logDto.setThirdMatchId(editScoreV2Dto.getThirdMatchId());
                    logDto.setLinkedId(editScoreV2Dto.getLinkedId() + "_event");
                    logDto.setEventCode("batch_edit_set_scores");
                    logDto.setPeriodId(eventPeriodId);
                    logDto.setOperatorId(editScoreV2Dto.getOperatorId());
                    logDto.setOperatorName(editScoreV2Dto.getOperatorName());
                    logDto.setIpAddress(editScoreV2Dto.getIpAddress());
                    logDto.setLanguage(editScoreV2Dto.getLanguage());
                    logDto.setBeforeVal("-");
                    logDto.setAfterVal("edit-scores");

                    matchScoreCommonHelper.commonProcess(logVo, eventOpDto, matchEventInfoDTO, logDto);
                }
            }

            log.info("[MatchVolleyballServiceImpl]batchEditScores end linkId::{} updated:{}", editScoreV2Dto.getLinkedId(), editMap.size());
            return Response.success();
        } catch (Exception e) {
            log.error("[MatchVolleyballServiceImpl]batchEditScores 处理异常 linkId::{}", editScoreV2Dto.getLinkedId(), e);
            throw new RuntimeException("批量编辑比分失败: " + e.getMessage(), e);
        }
    }


    // ---------------------------------------------------------------- helpers

    /**
     * SET_END（301..307）→ 对应 SET_BEGIN（8/9..442），其它 periodId 原样返回。
     * scoresJson 只用 SET_BEGIN 做 key，当 period 处于 SET_END 时需先翻译再查找。
     */
    private static Long toSetBeginKey(Long periodId) {
        if (periodId != null && VolleyballConstant.VOLLEYBALL_SET_END.containsKey(periodId)) {
            for (Map.Entry<Long, Long> e : VolleyballConstant.VOLLEYBALL_SET_BEGIN_TO_END.entrySet()) {
                if (e.getValue().equals(periodId)) {
                    return e.getKey();
                }
            }
        }
        return periodId;
    }

    /**
     * 把 scoresJson 解析为 periodId → VolleyballV2Scores。返回前确保至少包含 -1（全场）键。
     */
    private Map<Long, VolleyballV2Scores> parseScoresJson(String scoresJson) {
        if (StringUtils.isBlank(scoresJson)) {
            Map<Long, VolleyballV2Scores> map = new HashMap<>();
            map.put(WHOLE_MATCH, new VolleyballV2Scores());
            return map;
        }
        JSONObject parsed = JSONObject.parseObject(scoresJson);
        Map<Long, VolleyballV2Scores> map = JsonMapUtils.parseVolleyballV2Map(parsed);
        if (!map.containsKey(WHOLE_MATCH)) {
            map.put(WHOLE_MATCH, new VolleyballV2Scores());
        }
        return map;
    }

    /**
     * 全场局分总和：所有 SET_BEGIN periodId 的 setScore.home / setScore.away 各自相加。
     * 与 computeOverallMatchScore 配对使用——前者算盘分（set wins），这里算总得分。
     * 在 changeMatchStatus(ct=4) / batchEditScores 等终结点调用，保证 WHOLE_MATCH.setScore
     * 与各局 setScore 之和保持一致；否则 doCalculation 累加路径之外的写入（如 batchEditScores
     * 直接覆盖 setScore）会让 WHOLE_MATCH.setScore 漏算末局。
     */
    private CommonItem computeOverallSetScore(Map<Long, VolleyballV2Scores> all) {
        int home = 0;
        int away = 0;
        if (all == null) {
            return new CommonItem(0, 0);
        }
        for (Map.Entry<Long, VolleyballV2Scores> entry : all.entrySet()) {
            Long pid = entry.getKey();
            if (pid == null || pid.equals(WHOLE_MATCH)) {
                continue;
            }
            if (!VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(pid)) {
                continue;
            }
            VolleyballV2Scores scores = entry.getValue();
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

    /**
     * 全场盘分：所有 SET_BEGIN periodId 的 setScore 中胜出方累加 1。
     * 仅在「已离开 SET_BEGIN」的时机调用（updateMatchScore 非进行中分支 / addMatchPeriod 切到 SET_END / batchEditScores），
     * 此时各局 setScore 已是终值，简单全量遍历即正确——与斯诺克 calcOverallMatchScore(all) 一致。
     */
    private CommonItem computeOverallMatchScore(Map<Long, VolleyballV2Scores> all, Integer matchLength, Long excludePeriodId) {
        int home = 0;
        int away = 0;
        if (all == null) {
            return new CommonItem(0, 0);
        }
        for (Map.Entry<Long, VolleyballV2Scores> entry : all.entrySet()) {
            Long pid = entry.getKey();
            if (pid == null || pid.equals(WHOLE_MATCH)) {
                continue;
            }
            if (!VolleyballConstant.VOLLEYBALL_SET_BEGIN.containsKey(pid)) {
                continue;
            }
            if (pid.equals(excludePeriodId)) {
                continue;
            }
            VolleyballV2Scores scores = entry.getValue();
            CommonItem set = scores != null ? scores.getSetScore() : null;
            if (set == null || set.getHome() == null || set.getAway() == null) {
                continue;
            }
            int ph = set.getHome();
            int pa = set.getAway();
            int target = isDecidingSet(pid, matchLength)
                    ? VolleyballConstant.GOLDEN_SET_MIN_SCORE : VolleyballConstant.NORMAL_SET_MIN_SCORE;
            int diff = VolleyballConstant.MIN_SCORE_DIFF;
            if (ph >= target && ph - pa >= diff) {
                home += 1;
            } else if (pa >= target && pa - ph >= diff) {
                away += 1;
            }
        }
        return new CommonItem(home, away);
    }

    /**
     * Redis hash 取出来的字段可能是 Integer / Long / String，统一判 0。
     */
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

    /** Redis hash 取出的布尔字段可能是 Boolean 或 String "true"/"false"，缺失时统一回退为 false。 */
    private static boolean toBool(Object value) {
        return value != null
                && (Boolean.TRUE.equals(value) || "true".equalsIgnoreCase(String.valueOf(value)));
    }

    /**
     * 事件得分应记到哪一侧：opposite=true 时事件持有人为犯错方，分值记到对方。
     */
    private static String receivingSide(VolleyballEventTypeEnum type, String homeAway) {
        if (homeAway == null) {
            return null;
        }
        if (type != null && Boolean.TRUE.equals(type.getOpposite())) {
            return TeamTypeConstant.HOME.equals(homeAway) ? TeamTypeConstant.AWAY : TeamTypeConstant.HOME;
        }
        return homeAway;
    }

    private static String buildBatchEditSetScoreDiff(Map<Long, CommonItem> editScoreMap, Map<Long, int[]> oldSetScores) {
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
        Integer n = VolleyballConstant.VOLLEYBALL_SET_BEGIN.get(periodId);
        if (n != null) {
            return "set" + n + "(periodId=" + periodId + ")";
        }
        return "periodId=" + periodId;
    }
}
