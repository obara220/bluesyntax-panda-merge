package com.panda.merge.snooker.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.calculation.impl.SnookerCalculationServiceImpl;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SnookerConstant;
import com.panda.merge.constant.SnookerEventTypeEnum;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.PDFootBallEventDto;
import com.panda.merge.dto.advertise.v2.PDSnookerEventDto;
import com.panda.merge.dto.advertise.v2.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.snooker.converter.ScoreListVoConverter;
import com.panda.merge.snooker.dto.MatchCommonLogDto;
import com.panda.merge.snooker.dto.SnookerV2Scores;
import com.panda.merge.snooker.service.MatchSnookerService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Service
@Slf4j
public class MatchSnookerServiceImpl extends AbsMatchCommonProcessor<MatchScoreCommonVo> implements MatchSnookerService  {

    // eventOperationConverter / eventProducer / pdMatchInfoRepository / redisUtils /
    // matchScorePdLogService / matchScoreCommonHelper / pdOperationLogConverter /
    // commonAdvertiseService 均继承自父类 AbsMatchCommonProcessor，无需重复声明
    @Resource
    private MatchScoreInfoRepository matchScoreInfoRepository;
    @Resource
    private ScoreListVoConverter scoreListVoConverter;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    SnookerCalculationServiceImpl snookerCalculationService;
    @Override
    public Response getCurrentMatchInfo(ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto, MatchScoreAndTimeVo matchScoreAndTimeVo) {
        log.info("[MatchSnookerServiceImpl]getCurrentMatchInfo start linkId::{}:data:{}scores:{}",changeMatchPeriodV2Dto.getLinkedId(),changeMatchPeriodV2Dto,matchScoreAndTimeVo);
        MatchScoresInfo matchScoreInfo = matchScoreAndTimeVo.getMatchScoresInfo();
        JSONObject scoreObject = JSONObject.parseObject(matchScoreAndTimeVo.getMatchScoresInfo().getScoresJson());
        Map<Long, SnookerV2Scores> allPeriodScores= JsonMapUtils.parseSnookerV2Map(scoreObject);
        Long periodId = matchScoreInfo.getPeriod() == 0? -1: matchScoreInfo.getPeriod();
        SnookerV2Scores periodScore = allPeriodScores.get(periodId);
        SnookerV2Scores wholeScore = allPeriodScores.get(-1L);
        
        // 如果periodScore为null，创建一个默认对象
        if (periodScore == null) {
            log.warn("[MatchSnookerServiceImpl]getCurrentMatchInfo periodScore is null for periodId:{} linkId::{}", periodId, changeMatchPeriodV2Dto.getLinkedId());
            periodScore = new SnookerV2Scores();
        }
        
        // 如果wholeScore为null，创建一个默认对象
        if (wholeScore == null) {
            log.warn("[MatchSnookerServiceImpl]getCurrentMatchInfo wholeScore is null linkId::{}", changeMatchPeriodV2Dto.getLinkedId());
            wholeScore = new SnookerV2Scores();
        }
        
        log.info("[MatchSnookerServiceImpl]getCurrentMatchInfo processing linkId::{}:periodScore:{} wholeScore:{} periodId:{}",changeMatchPeriodV2Dto.getLinkedId(),periodScore, wholeScore, changeMatchPeriodV2Dto.getPeriodId());
        PDSnookerEventDto pdSnookerEventDto = scoreListVoConverter.convertSnoookerScoreToScore(periodScore, Integer.parseInt(changeMatchPeriodV2Dto.getPeriodId()+""),
                changeMatchPeriodV2Dto.getThirdMatchId()+"", changeMatchPeriodV2Dto.getSportId());
        // 查询时将“得分”口径映射为“次数”口径（不改变 scoresJson 存储口径）
        mapSnookerScoreToCount(pdSnookerEventDto);
        com.panda.merge.cache.CommonItem matchCommonItem = new com.panda.merge.cache.CommonItem();
        
        // 安全获取matchScore，如果为null则使用默认值0
        if (wholeScore.getMatchScore() != null) {
            matchCommonItem.setAway(wholeScore.getMatchScore().getAway());
            matchCommonItem.setHome(wholeScore.getMatchScore().getHome());
        } else {
            log.warn("[MatchSnookerServiceImpl]getCurrentMatchInfo wholeScore.getMatchScore() is null linkId::{}", changeMatchPeriodV2Dto.getLinkedId());
            matchCommonItem.setAway(0);
            matchCommonItem.setHome(0);
        }
        
        pdSnookerEventDto.setMatchScore(matchCommonItem);
        pdSnookerEventDto.setSetNum(Integer.valueOf(changeMatchPeriodV2Dto.getPeriodId()+""));
        pdSnookerEventDto.setSportId(changeMatchPeriodV2Dto.getSportId());
        pdSnookerEventDto.setThirdMatchId(changeMatchPeriodV2Dto.getThirdMatchId()+"");
        Map<String, Object> matchStatus = matchScoreCommonHelper.getMatchCacheStatus(changeMatchPeriodV2Dto.getThirdMatchId());
        
        // 赛制 matchLength：time/scores 为 null 或 0 时从 standardMatchInfo 取并落库。
        // ChangeMatchPeriodV2Dto 无赛制字段，同步赛制走父类 changeMatchLength（开赛后父类会拒绝修改）。
        Integer matchLength = null;
        MatchTimeInfo timeInfo = matchScoreAndTimeVo.getMatchTimeInfo();
        if (timeInfo != null) {
            matchLength = timeInfo.getMatchLength();
        }
        if ((matchLength == null || matchLength == 0) && matchScoreAndTimeVo.getMatchScoresInfo() != null) {
            Integer mlScores = matchScoreAndTimeVo.getMatchScoresInfo().getMatchLength();
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
                        log.warn("[MatchSnookerServiceImpl]getCurrentMatchInfo sync matchLength from standard failed linkId::{} msg:{}",
                                changeMatchPeriodV2Dto.getLinkedId(), lenResp.getMsg());
                    }
                }
                matchLength = fromStd;
            }
        }
        if (matchLength != null) {
            matchStatus.put("matchLength", matchLength);
        }
        
        log.info("[MatchSnookerServiceImpl]getCurrentMatchInfo linkId::{} matchStatus:{} kickoffFirstClick:{} matchLength:{}", 
                changeMatchPeriodV2Dto.getLinkedId(), matchStatus, matchStatus.get(SnookerConstant.KICKOFF_FIRST_CLICK), matchLength);
        if (matchStatus.get("controlType") != null) {
            pdSnookerEventDto.setControlType(Integer.valueOf(String.valueOf(matchStatus.get("controlType"))));
        }
        matchStatus.putIfAbsent(SnookerConstant.MATCH_CURRENT_PERIOD, 0);
        PDSnookerCurMatchInfoDto curMatchInfoDto = PDSnookerCurMatchInfoDto.builder()
                .pdSnookerEventDto(pdSnookerEventDto)
                .matchStatus(matchStatus)
                .build();
        log.info("[MatchSnookerServiceImpl]getCurrentMatchInfo end linkId::{}",changeMatchPeriodV2Dto.getLinkedId());
        return Response.success(curMatchInfoDto);
    }

    /**
     * PD 查询口径：彩球赢分/彩球罚分/罚4/罚7 在存储中是“分数”，展示时需换算成“次数”。
     * 例如 blackWiningScore=7 表示进黑 1 次；penaltyFour=8 表示罚4 2 次。
     */
    private static void mapSnookerScoreToCount(PDSnookerEventDto dto) {
        if (dto == null) return;
        dto.setRedWiningScore(divideCommonItem(dto.getRedWiningScore(), 1));
        dto.setYellowWiningScore(divideCommonItem(dto.getYellowWiningScore(), 2));
        dto.setGreenWiningScore(divideCommonItem(dto.getGreenWiningScore(), 3));
        dto.setBrownWiningScore(divideCommonItem(dto.getBrownWiningScore(), 4));
        dto.setBlueWiningScore(divideCommonItem(dto.getBlueWiningScore(), 5));
        dto.setPinkWiningScore(divideCommonItem(dto.getPinkWiningScore(), 6));
        dto.setBlackWiningScore(divideCommonItem(dto.getBlackWiningScore(), 7));

        dto.setRedFoulScore(divideCommonItem(dto.getRedFoulScore(), 4));
        dto.setYellowFoulScore(divideCommonItem(dto.getYellowFoulScore(), 4));
        dto.setGreenFoulScore(divideCommonItem(dto.getGreenFoulScore(), 4));
        dto.setBrownFoulScore(divideCommonItem(dto.getBrownFoulScore(), 4));
        dto.setBlueFoulScore(divideCommonItem(dto.getBlueFoulScore(), 5));
        dto.setPinkFoulScore(divideCommonItem(dto.getPinkFoulScore(), 6));
        dto.setBlackFoulScore(divideCommonItem(dto.getBlackFoulScore(), 7));

        dto.setPenaltyFour(divideCommonItem(dto.getPenaltyFour(), 4));
        dto.setPenaltySeven(divideCommonItem(dto.getPenaltySeven(), 7));
    }

    private static com.panda.merge.cache.CommonItem divideCommonItem(com.panda.merge.cache.CommonItem src, int divisor) {
        if (divisor <= 0) return src != null ? src : new com.panda.merge.cache.CommonItem(0, 0);
        int h = src != null && src.getHome() != null ? src.getHome() : 0;
        int a = src != null && src.getAway() != null ? src.getAway() : 0;
        return new com.panda.merge.cache.CommonItem(h / divisor, a / divisor);
    }

    @Override
    public Response eventList(EventListV2Dto eventListDto) {
        log.info("[MatchSnookerServiceImpl]eventList start linkId::{}:data:{}",eventListDto.getLinkedId(),eventListDto);

        if (eventListDto.getThirdMatchId() == null) {
            return Response.failed("thirdMatchId 不能为空");
        }

        MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
        MatchScoresEventInfoExample.Criteria criteria = example.createCriteria()
                .andThirdMatchIdEqualTo(eventListDto.getThirdMatchId());

        List<Long> setNums = eventListDto.getSetNums();
        if (!CollectionUtils.isEmpty(setNums) && !setNums.contains(-1L)) {
            criteria.andMatchPeriodIdIn(setNums);
        }

        example.setOrderByClause("id desc");
        List<MatchScoresEventInfo> list = matchScoresEventInfoMapper.selectByExample(example);
        List<PDFootBallEventDto> eventDtoList = new ArrayList<>();
        for (MatchScoresEventInfo matchScoresEventInfo : list) {
            PDFootBallEventDto pdFootBallEventDto = new PDFootBallEventDto();
            BeanUtils.copyProperties(matchScoresEventInfo, pdFootBallEventDto);
            pdFootBallEventDto.setId(matchScoresEventInfo.getId().toString());
            pdFootBallEventDto.setExtraInfo(matchScoresEventInfo.getExtraInfo());
            pdFootBallEventDto.setT1(matchScoresEventInfo.getFirstT1());
            pdFootBallEventDto.setT2(matchScoresEventInfo.getFirstT2());
            eventDtoList.add(pdFootBallEventDto);
        }
        log.info("[MatchSnookerServiceImpl]eventList end linkId::{} size:{}", eventListDto.getLinkedId(), eventDtoList.size());
        return Response.success(eventDtoList);
    }

    @Override
    public Response deleteEvent(MatchScoreAndTimeVo matchScoreAndTimeVo, DeleteEventV2Dto deleteEventDto) throws Exception {
        log.info("[MatchSnookerServiceImpl]deleteEvent start linkId::{}:data:{}",deleteEventDto.getLinkedId(),deleteEventDto);
        MatchScoresEventInfo oldEvent = matchScoresEventInfoMapper.selectByPrimaryKey(deleteEventDto.getDeleteEventId());
        if (oldEvent == null) {
            log.warn("[MatchSnookerServiceImpl]deleteEvent event not found id:{} linkId::{}",
                    deleteEventDto.getDeleteEventId(), deleteEventDto.getLinkedId());
            return Response.failed("事件不存在，无法删除");
        }
        oldEvent.setAddition10("1");
        // 删除比分事件时，标记原有事件为已取消
        oldEvent.setCanceled(1);
        oldEvent.setModifyTime(System.currentTimeMillis());
        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.changeDelEventToEvent(deleteEventDto,oldEvent.getEventCode(),oldEvent.getHomeAway());
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(
                matchScoreAndTimeVo, eventOperationV2Dto, 0, 0L, null);
        String beforeVal = SnookerConstant.SNOOKER_RERACK;
        if (SnookerConstant.SNOOKER_RERACK.equals(oldEvent.getEventCode())) {
            // 重置比分
            if(!oldEvent.getMatchPeriodId().equals(matchScoreAndTimeVo.getMatchScoresInfo().getPeriod())) {
                return Response.failed("删除的重新排球不是当前阶段！");
            }
            RerackV2Dto rerackV2Dto = RerackV2Dto.builder().sportId(deleteEventDto.getSportId()).thirdMatchId(deleteEventDto.getThirdMatchId()).periodId(oldEvent.getMatchPeriodId()).build();
            doRerackScore(matchScoreAndTimeVo, rerackV2Dto, false);
            // 重置事件
            Object eventIdObject = redisService.hGet(SnookerConstant.SNOOKER_PERIOD_BEGIN_EVENT_ID+rerackV2Dto.getThirdMatchId(), rerackV2Dto.getPeriodId()+"");
            if (eventIdObject == null) {
                return Response.failed("没有成功获取当前阶段的Event ID!");
            }
            Long periodBeginEventId = Long.parseLong(eventIdObject.toString());
            doRerackEvent(rerackV2Dto, periodBeginEventId, false, matchScoreAndTimeVo.getThirdMatchInfo().getThirdMatchSourceId());
            // 标记原有重排事件已取消
            matchScoresEventInfoMapper.updateByPrimaryKeySelective(oldEvent);
        } else {
            // 删除防负数校验已下沉到 updateMatchScore（按事件所属局 setScore 校验），这里不再用“当前局 periodT1/periodT2”做校验
            MatchScoresInfo beforeScores = matchScoreAndTimeVo.getMatchScoresInfo();
            int beforeH = beforeScores != null && beforeScores.getPeriodT1() != null ? beforeScores.getPeriodT1() : 0;
            int beforeA = beforeScores != null && beforeScores.getPeriodT2() != null ? beforeScores.getPeriodT2() : 0;
            updateMatchScore(matchScoreAndTimeVo, matchEventInfoDTO, true);
            matchScoresEventInfoMapper.updateByPrimaryKeySelective(oldEvent);
            beforeVal = beforeH + "-" + beforeA;
        }
        // addition9 标记这是一次“删除后重算”的事件，但该事件本身代表最新有效比分，不能标记为取消
        matchEventInfoDTO.setAddition9("true");
        // 保持 matchEventInfoDTO.canceled 为 0（有效事件），避免把重算后的 12-0 等比分也当作取消事件
        matchEventInfoDTO.setExtrainfo(oldEvent.getThirdEventId());
        // 设置 secondsFromStart，用于 sendPDEventInfo 保存到数据库
        if (eventOperationV2Dto.getSecondFromStart() != null) {
            matchEventInfoDTO.setSecondsFromStart(eventOperationV2Dto.getSecondFromStart());
        }
        // 使用 sendPDEventInfo 发送事件（会保存到数据库并发送到 THIRD_MATCH_EVENT_INFO_API）
        matchEventInfoDTO.setCopyLinkId(deleteEventDto.getLinkedId());
        eventProducer.sendPDSnookerEventInfo(matchEventInfoDTO);
        redisUtils.pushFootBallScore(eventOperationV2Dto.getThirdMatchId());
        redisUtils.pushFootBallEvent(eventOperationV2Dto.getThirdMatchId());
        MatchScoresInfo delLogScores = matchScoreAndTimeVo.getMatchScoresInfo();
        int delAfterH = delLogScores != null && delLogScores.getPeriodT1() != null ? delLogScores.getPeriodT1() : 0;
        int delAfterA = delLogScores != null && delLogScores.getPeriodT2() != null ? delLogScores.getPeriodT2() : 0;
        String afterVal = delAfterH + "-" + delAfterA;
        MatchCommonLogDto matchCommonLogDto = pdOperationLogConverter.convertDeleteEventToLog(deleteEventDto, beforeVal, afterVal);
        matchCommonLogDto.setOperatorId(deleteEventDto.getOperatorId());
        matchCommonLogDto.setOperatorName(deleteEventDto.getOperatorName());
        matchCommonLogDto.setIpAddress(deleteEventDto.getIpAddress());
        matchCommonLogDto.setLanguage(deleteEventDto.getLanguage());
        matchScorePdLogService.setMatchCommonLog(matchScoreAndTimeVo, matchCommonLogDto);
        log.info("[MatchSnookerServiceImpl]deleteEvent end linkId::{}",deleteEventDto.getLinkedId());
        return Response.success();
    }

    public Response scoreList(EventListV2Dto eventListV2Dto) {
        log.info("[MatchSnookerServiceImpl]scoreList start linkId::{}:data:{}",eventListV2Dto.getLinkedId(),eventListV2Dto);
        if (eventListV2Dto.getThirdMatchId() == null) {
            return Response.failed("thirdMatchId 不能为空");
        }
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(eventListV2Dto.getThirdMatchId(), 1);
        if (matchScoresInfo == null || StringUtils.isBlank(matchScoresInfo.getScoresJson())) {
            log.warn("[MatchSnookerServiceImpl]scoreList matchScoresInfo or scoresJson is null linkId::{}", eventListV2Dto.getLinkedId());
            return Response.success(Collections.emptyList());
        }
        JSONObject scoreObject = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, SnookerV2Scores> allPeriodScores= JsonMapUtils.parseSnookerV2Map(scoreObject);
        List<PDSnookerEventDto> res = new ArrayList<>();
        for(Map.Entry<Long, SnookerV2Scores> entrySet : allPeriodScores.entrySet()) {
            PDSnookerEventDto pdSnookerEventDto = scoreListVoConverter.convertSnoookerScoreToScore(entrySet.getValue(), Integer.parseInt(entrySet.getKey()+""),
                    eventListV2Dto.getThirdMatchId()+"",eventListV2Dto.getSportId());
            if (eventListV2Dto.getSetNums().contains(-1l) || eventListV2Dto.getSetNums().contains(entrySet.getKey())) {
                res.add(pdSnookerEventDto);
            }
        }
        log.info("[MatchSnookerServiceImpl]scoreList end linkId::{}",eventListV2Dto.getLinkedId());
        return Response.success(res);
    }

    public void batchEditScores(EditScoreV2Dto editScoreV2Dto) {
        if (editScoreV2Dto == null || editScoreV2Dto.getThirdMatchId() == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        log.info("[MatchSnookerServiceImpl]batchEditScores start linkId::{}:data:{}", editScoreV2Dto.getLinkedId(), editScoreV2Dto);
        
        try {
            if (StringUtils.isBlank(editScoreV2Dto.getScores())) {
                log.warn("[MatchSnookerServiceImpl]batchEditScores scores为空，无需更新 linkId::{}", editScoreV2Dto.getLinkedId());
                return;
            }
            
            // 2. 解析编辑的比分数据（scores为JSON字符串，格式：{"8":{"home":1,"away":1}}）
            Map<Long, CommonItem> editScoreMap = JSON.parseObject(editScoreV2Dto.getScores(), new TypeReference<Map<Long, CommonItem>>() {});
            if (editScoreMap == null || editScoreMap.isEmpty()) {
                log.warn("[MatchSnookerServiceImpl]batchEditScores 解析后的比分数据为空 linkId::{}", editScoreV2Dto.getLinkedId());
                return;
            }
            
            // 3. 查询当前比分信息
            MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(editScoreV2Dto.getThirdMatchId(), 1);
            if (matchScoresInfo == null) {
                throw new IllegalArgumentException("未找到对应的比分信息，thirdMatchId: " + editScoreV2Dto.getThirdMatchId());
            }
            
            // 4. 解析当前比分JSON
            String scoresJson = matchScoresInfo.getScoresJson();
            if (StringUtils.isBlank(scoresJson)) {
                log.warn("[MatchSnookerServiceImpl]batchEditScores 当前比分JSON为空，初始化空比分 linkId::{}", editScoreV2Dto.getLinkedId());
                scoresJson = "{}";
            }
            
            JSONObject scoreObject = JSONObject.parseObject(scoresJson);
            Map<Long, SnookerV2Scores> allPeriodScores = JsonMapUtils.parseSnookerV2Map(scoreObject);
            if (allPeriodScores == null) {
                allPeriodScores = new HashMap<>();
            }
            
            // 5. 更新所有阶段的 setScore（先快照旧局分，用于 diff 日志）
            Map<Long, int[]> batchEditOldSetScores = new LinkedHashMap<>();
            for (Map.Entry<Long, CommonItem> editEntry : editScoreMap.entrySet()) {
                Long periodId = editEntry.getKey();
                if (periodId == null || editEntry.getValue() == null) {
                    continue;
                }
                SnookerV2Scores ps = allPeriodScores.get(periodId);
                CommonItem oldSet = ps != null ? ps.getSetScore() : null;
                batchEditOldSetScores.put(periodId, new int[]{
                        batchEditNz(oldSet != null ? oldSet.getHome() : null),
                        batchEditNz(oldSet != null ? oldSet.getAway() : null)
                });
            }

            boolean hasUpdate = false;
            for (Map.Entry<Long, CommonItem> editEntry : editScoreMap.entrySet()) {
                Long periodId = editEntry.getKey();
                CommonItem editScore = editEntry.getValue();
                
                if (editScore == null) {
                    continue;
                }
                
                // 如果阶段不存在，创建新的阶段比分对象
                SnookerV2Scores periodScores = allPeriodScores.get(periodId);
                if (periodScores == null) {
                    periodScores = new SnookerV2Scores();
                    allPeriodScores.put(periodId, periodScores);
                }
                
                // 更新 setScore
                periodScores.setSetScore(editScore);
                hasUpdate = true;
                log.info("[MatchSnookerServiceImpl]batchEditScores 更新阶段{}的setScore为: home={}, away={} linkId::{}", 
                        periodId, editScore.getHome(), editScore.getAway(), editScoreV2Dto.getLinkedId());
            }
            
            if (!hasUpdate) {
                log.warn("[MatchSnookerServiceImpl]batchEditScores 没有匹配的阶段需要更新 linkId::{}", editScoreV2Dto.getLinkedId());
                return;
            }
            
            // 6. 重新计算全局阶段（-1）的 matchScore
            // 初始化全局阶段比分对象
            SnookerV2Scores wholeSores = allPeriodScores.get(WHOLE_MATCH);
            if (wholeSores == null) {
                wholeSores = new SnookerV2Scores();
                allPeriodScores.put(WHOLE_MATCH, wholeSores);
            }
            // 盘分结算：若当前处于某局进行中（SET_BEGIN），则不把该局临时领先计入盘分
            Long currentPeriodId = matchScoresInfo.getPeriod();
            CommonItem overallMatchScore = calcOverallMatchScore(allPeriodScores, currentPeriodId);
            Integer home = overallMatchScore != null ? overallMatchScore.getHome() : 0;
            Integer away = overallMatchScore != null ? overallMatchScore.getAway() : 0;
            
            // 更新全局的 matchScore
            if (wholeSores.getMatchScore() == null) {
                wholeSores.setMatchScore(new CommonItem());
            }
            wholeSores.getMatchScore().setHome(home);
            wholeSores.getMatchScore().setAway(away);
            
            // 更新 matchScoresInfo 的全局比分
            matchScoresInfo.setT1(home);
            matchScoresInfo.setT2(away);
            
            log.info("[MatchSnookerServiceImpl]batchEditScores 重新计算全局matchScore: home={}, away={} linkId::{}", 
                    home, away, editScoreV2Dto.getLinkedId());
            
            // 7. 保存比分信息
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
            
            // 8. 更新Redis缓存
            pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(matchScoresInfo.getThirdMatchId(), null);

            String convertedScoreJson = snookerCalculationService.buildStandardMatchScoreByMap(matchScoresInfo.getScoresJson(), editScoreV2Dto.getLinkedId());
            matchScoresInfo.setScoresJson(convertedScoreJson);
            scoresProducer.sendToMQ(thirdMatchInfo,matchScoresInfo,thirdMatchInfo.getReferenceId()+"_EDIT_SCORE", "batch_edit_set_scores");
            // 9. 推送比分更新事件
            redisUtils.pushFootBallScore(editScoreV2Dto.getThirdMatchId());
            redisUtils.pushFootBallEvent(editScoreV2Dto.getThirdMatchId());

            String batchDiffLog = buildBatchEditSetScoreDiff(editScoreMap, batchEditOldSetScores);
            if (StringUtils.isNotBlank(batchDiffLog)) {
                log.info("[MatchSnookerServiceImpl]batchEditScores diff linkId::{} {}", editScoreV2Dto.getLinkedId(), batchDiffLog);
                try {
                    MatchScoreAndTimeVo logVo = commonAdvertiseService.searchMatchScoreAndTime(editScoreV2Dto.getThirdMatchId());
                    MatchCommonLogDto logDto = new MatchCommonLogDto();
                    logDto.setSportId(7L);
                    logDto.setThirdMatchId(editScoreV2Dto.getThirdMatchId());
                    logDto.setLinkedId(editScoreV2Dto.getLinkedId());
                    logDto.setEventCode("batch_edit_set_scores");
                    logDto.setOperatorId(editScoreV2Dto.getOperatorId());
                    logDto.setOperatorName(editScoreV2Dto.getOperatorName());
                    logDto.setIpAddress(editScoreV2Dto.getIpAddress());
                    logDto.setLanguage(editScoreV2Dto.getLanguage());
                    logDto.setBeforeVal("-");
                    logDto.setAfterVal(batchDiffLog);
                    matchScorePdLogService.setMatchCommonLog(logVo, logDto);
                } catch (Exception logEx) {
                    log.warn("[MatchSnookerServiceImpl]batchEditScores PD log skipped match:{} linkId::{} err:{}",
                            editScoreV2Dto.getThirdMatchId(), editScoreV2Dto.getLinkedId(), logEx.getMessage());
                }
            }
            
            log.info("[MatchSnookerServiceImpl]batchEditScores end linkId::{} 成功更新{}个阶段的setScore和全局matchScore", 
                    editScoreV2Dto.getLinkedId(), editScoreMap.size());
        } catch (Exception e) {
            log.error("[MatchSnookerServiceImpl]batchEditScores 处理异常 linkId::{}", editScoreV2Dto != null ? editScoreV2Dto.getLinkedId() : "unknown", e);
            throw new RuntimeException("批量编辑比分失败: " + e.getMessage(), e);
        }
    }

    public Response rerack(MatchScoreAndTimeVo data, RerackV2Dto rerackV2Dto) throws Exception {
        log.info("[MatchSnookerServiceImpl]rerack start linkId::{}:data:{}", rerackV2Dto.getLinkedId(), rerackV2Dto);
        
        // 检查比赛是否处于中断状态，如果是则禁止操作
        if (matchScoreCommonHelper.isMatchInterrupted(rerackV2Dto.getThirdMatchId())) {
            log.warn("[MatchSnookerServiceImpl]rerack match is interrupted, cannot rerack match:{} linkId::{}", 
                    rerackV2Dto.getThirdMatchId(), rerackV2Dto.getLinkedId());
            return Response.failed("比赛已中断，不能进行重排操作");
        }
        
        // 检查赛事是否处于中断状态，如果是则禁止操作
        if (matchScoreCommonHelper.isMatchEventInterrupted(rerackV2Dto.getThirdMatchId())) {
            log.warn("[MatchSnookerServiceImpl]rerack match event is interrupted, cannot rerack match:{} linkId::{}", 
                    rerackV2Dto.getThirdMatchId(), rerackV2Dto.getLinkedId());
            return Response.failed("赛事已中断，不能进行重排操作");
        }
        
        // 重置比分
        if(!rerackV2Dto.getPeriodId().equals(data.getMatchScoresInfo().getPeriod())) {
            return Response.failed("重新排球不是当前阶段！");
        }
        doRerackScore(data, rerackV2Dto, true);

        // 重置事件
        Object eventIdObject = redisService.hGet(SnookerConstant.SNOOKER_PERIOD_BEGIN_EVENT_ID+rerackV2Dto.getThirdMatchId(), rerackV2Dto.getPeriodId()+"");
        if (eventIdObject == null) {
            return Response.failed("没有成功获取当前阶段的Event ID!");
        }

        Long periodBeginEventId = Long.parseLong(eventIdObject.toString());
        doRerackEvent(rerackV2Dto, periodBeginEventId,true, data.getThirdMatchInfo().getThirdMatchSourceId());

        //传递到下游
        EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.convertRerackToEvent(rerackV2Dto);
        eventOperationV2Dto.setOperatorId(rerackV2Dto.getOperatorId());
        eventOperationV2Dto.setOperatorName(rerackV2Dto.getOperatorName());
        eventOperationV2Dto.setIpAddress(rerackV2Dto.getIpAddress());
        eventOperationV2Dto.setLanguage(rerackV2Dto.getLanguage());
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(data, eventOperationV2Dto, 0, 0l, null);
        // 重排事件的 homeAway 设置为 "all"
        matchEventInfoDTO.setHomeAway("all");
        MatchCommonLogDto matchCommonLogDto = pdOperationLogConverter.convertRerackToLog(rerackV2Dto);
        matchCommonLogDto.setOperatorId(rerackV2Dto.getOperatorId());
        matchCommonLogDto.setOperatorName(rerackV2Dto.getOperatorName());
        matchCommonLogDto.setIpAddress(rerackV2Dto.getIpAddress());
        matchCommonLogDto.setLanguage(rerackV2Dto.getLanguage());
        matchScoreCommonHelper.commonProcess(data,eventOperationV2Dto, matchEventInfoDTO, matchCommonLogDto);
        return Response.success();
    }

    private void doRerackScore(MatchScoreAndTimeVo data, RerackV2Dto rerackV2Dto, boolean isRerack) throws Exception {
        JSONObject scoreObject = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, SnookerV2Scores> allPeriodScores= JsonMapUtils.parseSnookerV2Map(scoreObject);
        SnookerV2Scores oriWholeSores= allPeriodScores.get(WHOLE_MATCH);
        SnookerV2Scores oriPeriodSores= allPeriodScores.get(data.getMatchTimeInfo().getPeriod());
        
        // 初始化oriWholeSores和oriPeriodSores，如果为null
        if (oriWholeSores == null) {
            log.warn("[MatchSnookerServiceImpl]doRerackScore oriWholeSores is null for match:{} period:{} linkId::{}", 
                    rerackV2Dto.getThirdMatchId(), rerackV2Dto.getPeriodId(), rerackV2Dto.getLinkedId());
            oriWholeSores = new SnookerV2Scores();
            allPeriodScores.put(WHOLE_MATCH, oriWholeSores);
        }
        if (oriPeriodSores == null) {
            log.warn("[MatchSnookerServiceImpl]doRerackScore oriPeriodSores is null for match:{} period:{} linkId::{}", 
                    rerackV2Dto.getThirdMatchId(), rerackV2Dto.getPeriodId(), rerackV2Dto.getLinkedId());
            oriPeriodSores = new SnookerV2Scores();
            allPeriodScores.put(data.getMatchTimeInfo().getPeriod(), oriPeriodSores);
        }
        
        SnookerV2Scores periodSores= new SnookerV2Scores();
        if (isRerack) {
            redisService.hSet(SnookerConstant.SNOOKER_RERACK_PERIOD_SCORE+rerackV2Dto.getThirdMatchId(), rerackV2Dto.getPeriodId()+"", JSONObject.toJSON(oriPeriodSores), RedisConfig.REDIS_WEEK_TIME);
            oriWholeSores.changeWholeScores(oriPeriodSores, true);
            // 安全获取rerack字段，如果为null则初始化
            CommonItem rerackCommonItem = oriPeriodSores.getRerack();
            if (rerackCommonItem == null) {
                log.warn("[MatchSnookerServiceImpl]doRerackScore rerackCommonItem is null, initializing for match:{} period:{} linkId::{}", 
                        rerackV2Dto.getThirdMatchId(), rerackV2Dto.getPeriodId(), rerackV2Dto.getLinkedId());
                rerackCommonItem = new CommonItem();
                oriPeriodSores.setRerack(rerackCommonItem);
            }
            rerackCommonItem.setHome(rerackCommonItem.getHome()+1);
            periodSores.setRerack(rerackCommonItem);
        } else {
            Object periodObject = redisService.hGet(SnookerConstant.SNOOKER_RERACK_PERIOD_SCORE+rerackV2Dto.getThirdMatchId(), rerackV2Dto.getPeriodId()+"");
            if (periodObject == null) {
                throw new Exception("没有成功获取当前阶段的已经存储的比分!");
            }
            // 安全地解析periodObject
            try {
                if (periodObject instanceof SnookerV2Scores) {
                    // 如果已经是SnookerV2Scores对象，直接使用
                    periodSores = (SnookerV2Scores) periodObject;
                } else if (periodObject instanceof JSONObject) {
                    // 如果是JSONObject，直接转换
                    periodSores = JSONObject.toJavaObject((JSONObject) periodObject, SnookerV2Scores.class);
                } else {
                    // 如果是字符串，尝试解析JSON
                    String periodStr = periodObject.toString();
                    if (periodStr.trim().startsWith("{")) {
                        periodSores = JSONObject.parseObject(periodStr, SnookerV2Scores.class);
                    } else {
                        throw new Exception("存储的比分数据格式不正确: " + periodStr);
                    }
                }
            } catch (Exception e) {
                log.error("[MatchSnookerServiceImpl]doRerackScore parse periodObject error for match:{} period:{} linkId::{} error:{}",
                        rerackV2Dto.getThirdMatchId(), rerackV2Dto.getPeriodId(), rerackV2Dto.getLinkedId(), e.getMessage());
                throw new Exception("解析存储的比分数据失败: " + e.getMessage());
            }
            // 删除 rerack 时：全局比分 = 先减去当前阶段（重排后）的比分，再加上 Redis 中保存的原始阶段比分
            oriWholeSores.changeWholeScores(oriPeriodSores, true);
            oriWholeSores.changeWholeScores(periodSores, false);
        }

        allPeriodScores.put(rerackV2Dto.getPeriodId(), periodSores);
        
        // 安全获取setScore，如果为null则初始化
        CommonItem setScore = periodSores.getSetScore();
        if (setScore == null) {
            log.warn("[MatchSnookerServiceImpl]doRerackScore setScore is null, initializing for match:{} period:{} linkId::{}", 
                    rerackV2Dto.getThirdMatchId(), rerackV2Dto.getPeriodId(), rerackV2Dto.getLinkedId());
            setScore = new CommonItem();
            periodSores.setSetScore(setScore);
        }
        data.getMatchScoresInfo().setPeriodT1(setScore.getHome());
        data.getMatchScoresInfo().setPeriodT2(setScore.getAway());
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        matchScoreInfoRepository.updateScoresInfo(data.getMatchScoresInfo());
    }

    private void doRerackEvent(RerackV2Dto rerackV2Dto, Long periodBeginEventId, boolean isRerack, String thirdMatchSourceId) {
        MatchScoresEventInfoExample example= new MatchScoresEventInfoExample();
        // thirdMatchId 是平台唯一标识，优先用它过滤；thirdMatchSourceId 作为辅助条件防止极端数据污染
        example.createCriteria().andThirdMatchIdEqualTo(rerackV2Dto.getThirdMatchId())
                .andThirdMatchSourceIdEqualTo(thirdMatchSourceId)
                .andIdGreaterThan(periodBeginEventId)
                .andMatchPeriodIdEqualTo(rerackV2Dto.getPeriodId());
        example.setOrderByClause("id desc");
        List<MatchScoresEventInfo> oldEvents = matchScoresEventInfoMapper.selectByExample(example);

        if (!CollectionUtils.isEmpty(oldEvents)) {
            Long currentTime = System.currentTimeMillis();
            // 这里只需要更新 addition10 和 modifyTime，避免大字段整体更新
            if (isRerack) {
                // rerack时：标记当前阶段periodBeginEventId之后的所有事件为删除
                oldEvents.forEach(t -> {
                    t.setAddition10("1");
                    // 重排时，这些事件对应的比分都被作废，需要标记为已取消
                    t.setCanceled(1);
                    t.setModifyTime(currentTime);
                });
            } else {
                // 删除rerack时：只恢复那些因为rerack而被删除的事件（addition10="1"）
                // 不应该影响其他正常事件
                for (MatchScoresEventInfo oldEvent : oldEvents) {
                    if ("1".equals(oldEvent.getAddition10())) {
                        // 只恢复被rerack删除的事件
                        oldEvent.setAddition10(null);
                        // 恢复时，将因重排标记取消的事件重新标记为未取消
                        oldEvent.setCanceled(0);
                        oldEvent.setModifyTime(currentTime);
                    }
                    // 如果addition10不是"1"，说明这个事件不是因为rerack被删除的，不应该恢复
                }
            }
            // 逐条更新，避免使用包含多条 update 语句的自定义 SQL（会被防注入拦截为 multi-statement）
            for (MatchScoresEventInfo event : oldEvents) {
                matchScoresEventInfoMapper.updateByPrimaryKeySelective(event);
            }
        }
    }

    public CommonItem updateMatchScore(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO, Boolean isDelete) throws Exception {
        //阶段更新
        Long thirdMatchId = data.getThirdMatchInfo().getId();
        SnookerEventTypeEnum snookerEventTypeEnum = SnookerEventTypeEnum.getByCode(matchEventInfoDTO.getEventCode());
        
        // 检查并初始化 scoresJson，如果为空或无效则重建
        String scoresJson = data.getMatchScoresInfo().getScoresJson();
        if (StringUtils.isEmpty(scoresJson)) {
            log.warn("[MatchSnookerServiceImpl]updateMatchScore scoresJson is empty, initializing for match:{} linkId::{}",
                    thirdMatchId, matchEventInfoDTO.getCopyLinkId());
            scoresJson = buildDefaultScoresJson(data);
            data.getMatchScoresInfo().setScoresJson(scoresJson);
        }

        // 安全地解析 scoresJson
        JSONObject scoreObject;
        try {
            scoreObject = JSONObject.parseObject(scoresJson);
            if (scoreObject == null) {
                log.warn("[MatchSnookerServiceImpl]updateMatchScore scoreObject is null after parsing, re-initializing for match:{} linkId::{}",
                        thirdMatchId, matchEventInfoDTO.getCopyLinkId());
                scoresJson = buildDefaultScoresJson(data);
                data.getMatchScoresInfo().setScoresJson(scoresJson);
                scoreObject = JSONObject.parseObject(scoresJson);
            }
        } catch (Exception e) {
            log.error("[MatchSnookerServiceImpl]updateMatchScore parse scoresJson error, re-initializing for match:{} linkId::{} error:{} scoresJson:{}",
                    thirdMatchId, matchEventInfoDTO.getCopyLinkId(), e.getMessage(), scoresJson);
            scoresJson = buildDefaultScoresJson(data);
            data.getMatchScoresInfo().setScoresJson(scoresJson);
            scoreObject = JSONObject.parseObject(scoresJson);
        }
        
        Map<Long, SnookerV2Scores> allPeriodScores= JsonMapUtils.parseSnookerV2Map(scoreObject);
        
        // 初始化 wholeScore（全局比分），如果不存在则创建
        SnookerV2Scores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if (wholeSores == null) {
            log.info("[MatchSnookerServiceImpl]updateMatchScore initializing wholeScore for match:{} linkId::{}", 
                    thirdMatchId, matchEventInfoDTO.getCopyLinkId());
            wholeSores = new SnookerV2Scores();
            allPeriodScores.put(WHOLE_MATCH, wholeSores);
        }
        
        // 当前局比分：按 matchTimeInfo.period 口径处理（删除事件时若会扣成负数则直接报错）
        Long currentPeriodId = data.getMatchTimeInfo().getPeriod();
        SnookerV2Scores periodSores= allPeriodScores.get(currentPeriodId);
        if (periodSores == null) {
            log.info("[MatchSnookerServiceImpl]updateMatchScore initializing periodScore for periodId:{} match:{} linkId::{}", 
                    currentPeriodId, thirdMatchId, matchEventInfoDTO.getCopyLinkId());
            periodSores = new SnookerV2Scores();
            allPeriodScores.put(currentPeriodId, periodSores);
        }
        
        CommonItem wholeCommonItem = wholeSores.getFieldScoreByEventCode(snookerEventTypeEnum.getEventCode());
        // 对于score为null且没有对应字段的事件（如nopot、Striker等），允许通过，不抛出异常
        if (wholeCommonItem == null) {
            // 如果事件有score值，说明应该影响比分，但没有找到对应字段，这是错误
            if (snookerEventTypeEnum.getScore() != null) {
                throw new Exception("事件编码不正确，不能删除该事件！");
            }
            // 如果事件没有score值（如nopot、Striker等），说明不影响比分，允许通过
            // 创建一个空的CommonItem用于返回，但不进行比分计算
            CommonItem beforeCommonItem = new CommonItem(0, 0);
            // 对于不影响比分的事件，仍然调用doCalculation（虽然不会更新比分，但可能会更新其他统计）
            wholeSores.doCalculation(snookerEventTypeEnum, matchEventInfoDTO.getHomeAway(), isDelete);
            periodSores.doCalculation(snookerEventTypeEnum, matchEventInfoDTO.getHomeAway(),isDelete);
            // 返回空的CommonItem，表示没有比分变化；事件上仍带当前局比分与全局盘比分
            CommonItem periodSet = periodSores.getSetScore();
            CommonItem frameWinScore = calcFrameWinScore(allPeriodScores, currentPeriodId);
            // 事件 DTO：
            // - firstT1/firstT2 = 盘比分（当前局 setScore 分数）
            // - t1/t2 = 局比分（统计已完成局 8..(current-1) 胜负）
            matchEventInfoDTO.setFirstT1(periodSet != null ? periodSet.getHome() : 0);
            matchEventInfoDTO.setFirstT2(periodSet != null ? periodSet.getAway() : 0);
            matchEventInfoDTO.setT1(frameWinScore != null ? frameWinScore.getHome() : 0);
            matchEventInfoDTO.setT2(frameWinScore != null ? frameWinScore.getAway() : 0);
            data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
            data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
            matchScoreInfoRepository.updateScoresInfo(data.getMatchScoresInfo());
            return beforeCommonItem;
        }

        // 删除事件兜底校验：删除后任何比分不能出现 < 0
        // - wholeCommonItem：用于校验全局统计字段（如各色球累计分等）
        // - periodCommonItem：用于校验当前局内该字段（例如蓝球进球分）不能删成负数
        // - setScore：用于校验该局局分（删除某色球/罚分会直接影响 setScore）
        if (Boolean.TRUE.equals(isDelete) && snookerEventTypeEnum.getScore() != null) {
            int delta = snookerEventTypeEnum.getScore();
            String receiving = snookerScoreReceivingSide(snookerEventTypeEnum, matchEventInfoDTO.getHomeAway());

            int curHome = wholeCommonItem.getHome() == null ? 0 : wholeCommonItem.getHome();
            int curAway = wholeCommonItem.getAway() == null ? 0 : wholeCommonItem.getAway();
            if (TeamTypeConstant.AWAY.equals(receiving)) {
                if (curAway - delta < 0) {
                    throw new Exception("事件比分不足，删除后比分将小于0，无法删除");
                }
            } else if (TeamTypeConstant.HOME.equals(receiving)) {
                if (curHome - delta < 0) {
                    throw new Exception("事件比分不足，删除后比分将小于0，无法删除");
                }
            } else {
                throw new Exception("homeAway 不合法，无法删除事件");
            }

            // 当前局内该字段也不能删成负数：若字段未初始化/为空，视为 0
            CommonItem periodCommonItem = periodSores.getFieldScoreByEventCode(snookerEventTypeEnum.getEventCode());
            int ph = (periodCommonItem != null && periodCommonItem.getHome() != null) ? periodCommonItem.getHome() : 0;
            int pa = (periodCommonItem != null && periodCommonItem.getAway() != null) ? periodCommonItem.getAway() : 0;
            if (TeamTypeConstant.AWAY.equals(receiving)) {
                if (pa - delta < 0) {
                    throw new Exception("该球种/统计项次数不足，删除后将小于0，无法删除");
                }
            } else if (TeamTypeConstant.HOME.equals(receiving)) {
                if (ph - delta < 0) {
                    throw new Exception("该球种/统计项次数不足，删除后将小于0，无法删除");
                }
            }

            // 单项字段（如 pinkWiningScore）也不能删成负数
            // 仅当 getSnookerSpecificItem 返回非 null 时才做校验（free_ball_pot 等返回 null，跳过此检查）
            CommonItem specific = getSnookerSpecificItem(periodSores, snookerEventTypeEnum.getEventCode());
            if (specific != null) {
                int sph = specific.getHome() != null ? specific.getHome() : 0;
                int spa = specific.getAway() != null ? specific.getAway() : 0;
                if (TeamTypeConstant.AWAY.equals(receiving)) {
                    if (spa - delta < 0) {
                        throw new Exception("该球种次数不足，删除后将小于0，无法删除");
                    }
                } else if (TeamTypeConstant.HOME.equals(receiving)) {
                    if (sph - delta < 0) {
                        throw new Exception("该球种次数不足，删除后将小于0，无法删除");
                    }
                }
            }

            CommonItem setScore = periodSores.getSetScore();
            int sh = setScore != null && setScore.getHome() != null ? setScore.getHome() : 0;
            int sa = setScore != null && setScore.getAway() != null ? setScore.getAway() : 0;
            if (TeamTypeConstant.AWAY.equals(receiving)) {
                if (sa - delta < 0) {
                    throw new Exception("当前局分不足，删除后将小于0，无法删除");
                }
            } else if (TeamTypeConstant.HOME.equals(receiving)) {
                if (sh - delta < 0) {
                    throw new Exception("当前局分不足，删除后将小于0，无法删除");
                }
            }
        }
        CommonItem beforeCommonItem = new CommonItem(wholeCommonItem.getHome(),wholeCommonItem.getAway());

        // 盘比分（matchScore）结算时机：
        // - 小局进行中（period 为 set begin）不应因为局分(setScore)变化而提前结算盘分
        // - 认输/判输等“小局结束事件”允许立即结算盘分（与 sendEvent/frame-loss 分支保持一致）
        CommonItem oriMatchScore = wholeSores.getMatchScore();
        CommonItem matchScoreSnapshot = oriMatchScore != null ? new CommonItem(oriMatchScore.getHome(), oriMatchScore.getAway()) : new CommonItem(0, 0);

        wholeSores.doCalculation(snookerEventTypeEnum, matchEventInfoDTO.getHomeAway(), isDelete);
        periodSores.doCalculation(snookerEventTypeEnum, matchEventInfoDTO.getHomeAway(),isDelete);

        boolean frameLossEvent = SnookerEventTypeEnum.CONCEDE.getEventCode().equals(snookerEventTypeEnum.getEventCode())
                || SnookerEventTypeEnum.FOUL_LOSE.getEventCode().equals(snookerEventTypeEnum.getEventCode());
        boolean inPlayingSet = SnookerConstant.SNOOKER_SET_BEGIN.containsKey(currentPeriodId);
        if (!inPlayingSet || frameLossEvent) {
            // 重算并写回全场盘分（matchScore）：回看/编辑历史局比分后，总盘分需要同步更新
            CommonItem overallMatchScore = calcOverallMatchScore(allPeriodScores);
            if (wholeSores.getMatchScore() == null) {
                wholeSores.setMatchScore(new CommonItem());
            }
            wholeSores.getMatchScore().setHome(overallMatchScore.getHome());
            wholeSores.getMatchScore().setAway(overallMatchScore.getAway());
        } else {
            // 小局进行中：保持盘分不变
            if (wholeSores.getMatchScore() == null) {
                wholeSores.setMatchScore(matchScoreSnapshot);
            } else {
                wholeSores.getMatchScore().setHome(matchScoreSnapshot.getHome());
                wholeSores.getMatchScore().setAway(matchScoreSnapshot.getAway());
            }
        }
        //计算最高值
        Object curScore = redisService.hGet(SnookerConstant.SNOOKER_CUR_HIGHEST_SCORE+thirdMatchId, data.getMatchTimeInfo().getPeriod()+"");
        CommonItem curScoreItem;
        if (curScore == null) {
            // 如果Redis中没有缓存，初始化为0
            curScoreItem = new CommonItem();
        } else if (curScore instanceof CommonItem) {
            // 如果已经是CommonItem对象，直接使用
            curScoreItem = (CommonItem) curScore;
        } else {
            // 尝试解析JSON字符串
            try {
                String curScoreStr = curScore.toString();
                // 检查是否是有效的JSON格式（以{开头）
                if (curScoreStr.trim().startsWith("{")) {
                    curScoreItem = JSONObject.toJavaObject(JSONObject.parseObject(curScoreStr), CommonItem.class);
                } else {
                    // 如果不是JSON格式，可能是对象toString()的结果，初始化新的对象
                    log.warn("[MatchSnookerServiceImpl]updateMatchScore invalid curScore format, re-initializing for match:{} period:{} linkId::{} curScore:{}",
                            thirdMatchId, data.getMatchTimeInfo().getPeriod(), matchEventInfoDTO.getCopyLinkId(), curScoreStr);
                    curScoreItem = new CommonItem();
                }
            } catch (Exception e) {
                // JSON解析失败，初始化新的对象
                log.warn("[MatchSnookerServiceImpl]updateMatchScore curScore parse error, re-initializing for match:{} period:{} linkId::{} error:{} curScore:{}",
                        thirdMatchId, data.getMatchTimeInfo().getPeriod(), matchEventInfoDTO.getCopyLinkId(), e.getMessage(), curScore);
                curScoreItem = new CommonItem();
            }
        }
        if(snookerEventTypeEnum.getEventCode().equals(SnookerEventTypeEnum.STRIKER.getEventCode())) {
            curScoreItem.setAway(0);
            curScoreItem.setHome(0);
            redisService.hSet(SnookerConstant.SNOOKER_CUR_HIGHEST_SCORE+thirdMatchId, data.getMatchTimeInfo().getPeriod()+"", curScoreItem, RedisConfig.REDIS_WEEK_TIME);
        } else if (SnookerConstant.WINNING_SCORE_EVENT_TYPE.containsKey(snookerEventTypeEnum.getEventCode())){
            wholeSores.updateHighestSingleShot(snookerEventTypeEnum, matchEventInfoDTO.getHomeAway(), curScoreItem, true);
            periodSores.updateHighestSingleShot(snookerEventTypeEnum, matchEventInfoDTO.getHomeAway(), curScoreItem, false);
            redisService.hSet(SnookerConstant.SNOOKER_CUR_HIGHEST_SCORE+thirdMatchId, data.getMatchTimeInfo().getPeriod()+"", curScoreItem, RedisConfig.REDIS_WEEK_TIME);
        }
        //更新比分
        wholeCommonItem = wholeSores.getFieldScoreByEventCode(snookerEventTypeEnum.getEventCode());
        // MatchScoresInfo：全场盘分（小局进行中保持不提前结算）
        CommonItem finalMatchScore = wholeSores.getMatchScore() != null ? wholeSores.getMatchScore() : matchScoreSnapshot;
        data.getMatchScoresInfo().setT1(finalMatchScore.getHome());
        data.getMatchScoresInfo().setT2(finalMatchScore.getAway());
        data.getMatchScoresInfo().setPeriodT1(periodSores.getSetScore().getHome());
        data.getMatchScoresInfo().setPeriodT2(periodSores.getSetScore().getAway());

        // 事件 DTO：
        // - firstT1/firstT2 = 盘比分（当前局 setScore 分数）
        // - t1/t2 = 局比分（统计已完成局 8..(current-1) 胜负）
        CommonItem periodSetScore = periodSores.getSetScore();
        CommonItem frameWinScore = calcFrameWinScore(allPeriodScores, currentPeriodId);
        matchEventInfoDTO.setFirstT1(periodSetScore != null ? periodSetScore.getHome() : 0);
        matchEventInfoDTO.setFirstT2(periodSetScore != null ? periodSetScore.getAway() : 0);
        matchEventInfoDTO.setT1(frameWinScore != null ? frameWinScore.getHome() : 0);
        matchEventInfoDTO.setT2(frameWinScore != null ? frameWinScore.getAway() : 0);
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
        matchScoreInfoRepository.updateScoresInfo(data.getMatchScoresInfo());
        
        // 认输或直接判输：局已结束时的盘分兜底（小局休息主要由 changeScore 后续 changeMatchPeriod 处理）
        if (SnookerEventTypeEnum.CONCEDE.getEventCode().equals(snookerEventTypeEnum.getEventCode())
                || SnookerEventTypeEnum.FOUL_LOSE.getEventCode().equals(snookerEventTypeEnum.getEventCode())) {
            log.info("[MatchSnookerServiceImpl]updateMatchScore frame-loss event detected, checking period end match:{} period:{} linkId::{}", 
                    thirdMatchId, currentPeriodId, matchEventInfoDTO.getCopyLinkId());
            
            // 判断当前局是否结束（根据setScore判断）
            CommonItem setScore = periodSores.getSetScore();
            if (setScore != null && (setScore.getHome() > 0 || setScore.getAway() > 0)) {
                // 当前局有比分，说明局已结束，需要切换到小局休息
                log.info("[MatchSnookerServiceImpl]updateMatchScore period ended, switching to rest period (445) match:{} period:{} linkId::{}", 
                        thirdMatchId, currentPeriodId, matchEventInfoDTO.getCopyLinkId());
                
                // 计算matchScore（盘比分）
                Integer home = 0;
                Integer away = 0;
                for(Map.Entry<Long, SnookerV2Scores> entrySet : allPeriodScores.entrySet()) {
                    Long periodId = entrySet.getKey();
                    // 跳过全局比分（-1）和当前阶段
                    if (periodId.equals(WHOLE_MATCH) || periodId.equals(currentPeriodId)) {
                        continue;
                    }
                    SnookerV2Scores periodScore = entrySet.getValue();
                    if (periodScore != null) {
                        CommonItem entrySetScore = periodScore.getSetScore();
                        if (entrySetScore != null) {
                            if (entrySetScore.getHome() > entrySetScore.getAway()) {
                                home += 1;
                            } else if (entrySetScore.getHome() < entrySetScore.getAway()) {
                                away += 1;
                            }
                        }
                    }
                }
                // 加上当前局的胜负
                if (setScore.getHome() > setScore.getAway()) {
                    home += 1;
                } else if (setScore.getHome() < setScore.getAway()) {
                    away += 1;
                }
                
                // 更新matchScore
                wholeSores.getMatchScore().setHome(home);
                wholeSores.getMatchScore().setAway(away);
                data.getMatchScoresInfo().setT1(home);
                data.getMatchScoresInfo().setT2(away);
                // 认输时才需要更新事件中的盘比分（t1/t2）
                matchEventInfoDTO.setT1(home);
                matchEventInfoDTO.setT2(away);
                data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
                matchScoreInfoRepository.updateScoresInfo(data.getMatchScoresInfo());
                
                log.info("[MatchSnookerServiceImpl]updateMatchScore calculated matchScore after concede: home={}, away={} match:{} linkId::{}", 
                        home, away, thirdMatchId, matchEventInfoDTO.getCopyLinkId());
                
                // 切换到小局休息（periodId=445）
                // 注意：这里不直接调用changeMatchPeriod，而是通过事件机制触发
                // 前端或业务层应该根据认输事件自动切换到小局休息
                log.info("[MatchSnookerServiceImpl]updateMatchScore period ended after concede, should switch to rest period (445) match:{} linkId::{}", 
                        thirdMatchId, matchEventInfoDTO.getCopyLinkId());
            }
        }
        
        return beforeCommonItem;
    }

    /**
     * 计算“局比分”：统计已完成局（periodId 从 8 开始，且 < 当前 periodId）的胜负。
     *
     * 约定：
     * - 只统计局阶段 periodId: [8, 80)（80/100/445 等为中断/结束/休息，不计入局比分）
     * - 仅当该局 setScore 有明确胜负（home != away）才计入
     */
    private CommonItem calcFrameWinScore(Map<Long, SnookerV2Scores> allPeriodScores, Long currentPeriodId) {
        int home = 0;
        int away = 0;
        if (allPeriodScores == null || currentPeriodId == null) {
            return new CommonItem(home, away);
        }
        for (Map.Entry<Long, SnookerV2Scores> e : allPeriodScores.entrySet()) {
            Long pid = e.getKey();
            if (pid == null) {
                continue;
            }
            // 仅统计已完成局：8..(current-1)
            if (pid < 8L || pid >= 80L || pid >= currentPeriodId) {
                continue;
            }
            SnookerV2Scores ps = e.getValue();
            CommonItem setScore = ps != null ? ps.getSetScore() : null;
            if (setScore == null) {
                continue;
            }
            Integer sh = setScore.getHome();
            Integer sa = setScore.getAway();
            if (sh == null || sa == null) {
                continue;
            }
            if (sh > sa) {
                home += 1;
            } else if (sh < sa) {
                away += 1;
            }
        }
        return new CommonItem(home, away);
    }

    /**
     * 计算“全场盘分”(matchScore)：按 scoresJson 内所有已决出胜负的局(setScore home!=away)统计，
     * 与“当前切换/回看到了哪一局”无关。
     */
    private CommonItem calcOverallMatchScore(Map<Long, SnookerV2Scores> allPeriodScores) {
        return calcOverallMatchScore(allPeriodScores, null);
    }

    /**
     * 计算“全场盘分”(matchScore)（支持排除“当前正在进行中的小局”）：
     * - 若 currentPeriodId 属于 SET_BEGIN（小局进行中），则不把该局 setScore 的临时领先计入盘分
     * - 其它情况与 {@link #calcOverallMatchScore(Map)} 一致
     */
    private CommonItem calcOverallMatchScore(Map<Long, SnookerV2Scores> allPeriodScores, Long currentPeriodId) {
        int home = 0;
        int away = 0;
        if (allPeriodScores == null) {
            return new CommonItem(0, 0);
        }

        // 若当前处于某局 SET_BEGIN，需要区分：
        // - 真正进行中：仅当“已结束局数(maxEndedSetNo)” < 当前局号，才排除当前局的临时领先
        // - 回看历史：若已结束局数 >= 当前局号，说明只是回看历史局，不应排除（否则会从 10-0 误变 9-0）
        Integer curSetNo = (currentPeriodId != null) ? SnookerConstant.SNOOKER_SET_BEGIN.get(currentPeriodId) : null;
        int maxEndedSetNo = 0;
        if (curSetNo != null) {
            for (Long pid : allPeriodScores.keySet()) {
                if (pid == null) continue;
                Integer endedNo = SnookerConstant.SNOOKER_SET_END.get(pid);
                if (endedNo != null && endedNo > maxEndedSetNo) {
                    maxEndedSetNo = endedNo;
                }
            }
        }
        for (Map.Entry<Long, SnookerV2Scores> e : allPeriodScores.entrySet()) {
            Long pid = e.getKey();
            if (pid == null || pid.equals(WHOLE_MATCH)) {
                continue;
            }
            // 只统计小局开始阶段（8/9/.../441/442/500+），避免漏掉第6局及以后（periodId>=80）
            if (!SnookerConstant.SNOOKER_SET_BEGIN.containsKey(pid)) {
                continue;
            }
            // 当前局进行中：不应提前结算盘分（回看历史局不排除）
            if (currentPeriodId != null
                    && pid.equals(currentPeriodId)
                    && curSetNo != null
                    && maxEndedSetNo < curSetNo) {
                continue;
            }
            SnookerV2Scores ps = e.getValue();
            CommonItem setScore = ps != null ? ps.getSetScore() : null;
            if (setScore == null || setScore.getHome() == null || setScore.getAway() == null) {
                continue;
            }
            if (setScore.getHome() > setScore.getAway()) {
                home += 1;
            } else if (setScore.getHome() < setScore.getAway()) {
                away += 1;
            }
        }
        return new CommonItem(home, away);
    }

    protected void kickOffPostProcess(KickOffV2Dto kickOffV2Dto, Map<String, Object> paramObject) throws Exception {
        // 保存开球方选择状态到 Redis（与 changeScore 校验 kickOffEventCheck 一致）
        String who = StringUtils.trimToEmpty(kickOffV2Dto.getWhoKickOff());
        if (!TeamTypeConstant.HOME.equalsIgnoreCase(who) && !TeamTypeConstant.AWAY.equalsIgnoreCase(who)) {
            throw new Exception("请选择开球方 home 或 away");
        }
        int home = TeamTypeConstant.HOME.equalsIgnoreCase(who) ? 1 : 0;
        int away = TeamTypeConstant.AWAY.equalsIgnoreCase(who) ? 1 : 0;
        // 统一成小写，后续 convertKickOffToEvent / 比分计算里 homeAway 与 Redis 一致
        kickOffV2Dto.setWhoKickOff(home == 1 ? TeamTypeConstant.HOME : TeamTypeConstant.AWAY);
        matchScoreCommonHelper.setMatchCacheStatus(kickOffV2Dto.getThirdMatchId(), SnookerConstant.KICKOFF_FIRST_CLICK, home, away);
        log.info("[MatchSnookerServiceImpl]kickOffPostProcess saved kickoffFirstClick for match:{} whoKickOff:{} home:{} away:{}", 
                kickOffV2Dto.getThirdMatchId(), who, home, away);
        // 初始化当前局最高分缓存
        CommonItem curScoreItem = new CommonItem();
        redisService.hSet(SnookerConstant.SNOOKER_CUR_HIGHEST_SCORE+kickOffV2Dto.getThirdMatchId(), kickOffV2Dto.getPeriodId()+"", curScoreItem, RedisConfig.REDIS_WEEK_TIME);
    }

    @Override
    public void addMatchPeriod(MatchScoreAndTimeVo data, MatchEventInfoDTO matchEventInfoDTO) {
        Long targetPeriodId = matchEventInfoDTO.getMatchPeriodId();
        // 回看历史小局时，先快照全场盘分（例如当前第5局休息时 4-1，切回第2局仍应保持 4-1）
        Integer keepTotalHome = data.getMatchScoresInfo().getT1();
        Integer keepTotalAway = data.getMatchScoresInfo().getT2();

        // 先保存之前的阶段ID，用于后续计算matchScore
        Long previousPeriodId = data.getMatchTimeInfo().getPeriod();
        
        //阶段更新
        data.getMatchTimeInfo().setPeriod(targetPeriodId);
        data.getMatchScoresInfo().setPeriod(targetPeriodId);

        //计算比分
        JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, SnookerV2Scores> allPeriodScores= JsonMapUtils.parseSnookerV2Map(periodFootballScores);
        
        // 确保 wholeScore（全局比分）存在，如果不存在则初始化
        SnookerV2Scores wholePeriodSores = allPeriodScores.get(WHOLE_MATCH);
        if (wholePeriodSores == null) {
            log.info("[MatchSnookerServiceImpl]addMatchPeriod initializing wholeScore for match:{} periodId:{}", 
                    data.getThirdMatchInfo().getId(), matchEventInfoDTO.getMatchPeriodId());
            wholePeriodSores = new SnookerV2Scores();
            allPeriodScores.put(WHOLE_MATCH, wholePeriodSores);
            data.getMatchScoresInfo().setT1(0);
            data.getMatchScoresInfo().setT2(0);
        }
        
        SnookerV2Scores periodSores= allPeriodScores.get(targetPeriodId);
        
        if (periodSores == null && SnookerConstant.SNOOKER_SET_BEGIN.containsKey(targetPeriodId)) {
            log.info("[MatchSnookerServiceImpl]addMatchPeriod initializing periodScore for periodId:{} match:{}", 
                    targetPeriodId, data.getThirdMatchInfo().getId());
            periodSores= new SnookerV2Scores();
            allPeriodScores.put(targetPeriodId, periodSores);
            data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
            data.getMatchScoresInfo().setPeriodT1(0);
            data.getMatchScoresInfo().setPeriodT2(0);
        } else {
            // 优化：仅在“局结束”场景增量更新 matchScore，其余阶段切换保持不变（避免每次都全量遍历重算）。
            if (wholePeriodSores.getMatchScore() == null) {
                wholePeriodSores.setMatchScore(new CommonItem());
            }
            int home = wholePeriodSores.getMatchScore().getHome() != null ? wholePeriodSores.getMatchScore().getHome() : 0;
            int away = wholePeriodSores.getMatchScore().getAway() != null ? wholePeriodSores.getMatchScore().getAway() : 0;

            boolean isRestTarget = targetPeriodId != null
                    && (SnookerConstant.SNOOKER_SET_END.containsKey(targetPeriodId) || targetPeriodId.equals(445L));

            if (isRestTarget) {
                // 切到“小局休息/445”前必须确保上一局已分出胜负，否则不允许进入休息阶段
                if (previousPeriodId != null && SnookerConstant.SNOOKER_SET_BEGIN.containsKey(previousPeriodId)) {
                    SnookerV2Scores previousPeriodScores = allPeriodScores.get(previousPeriodId);
                    CommonItem prevSet = previousPeriodScores != null ? previousPeriodScores.getSetScore() : null;
                    int ph = prevSet != null && prevSet.getHome() != null ? prevSet.getHome() : 0;
                    int pa = prevSet != null && prevSet.getAway() != null ? prevSet.getAway() : 0;
                    if (ph == pa) {
                        throw new IllegalArgumentException("小局比分相同，不能切换到小局休息阶段");
                    }
                }
                // Case 1：切到小局休息（SET_END / 445）：保留原口径，全量遍历所有局按 setScore 重算 matchScore（避免历史回改比分导致累计误差）
                home = 0;
                away = 0;
                for (Map.Entry<Long, SnookerV2Scores> entrySet : allPeriodScores.entrySet()) {
                    Long periodId = entrySet.getKey();
                    if (periodId == null || periodId.equals(WHOLE_MATCH)) {
                        continue;
                    }
                    SnookerV2Scores periodScore = entrySet.getValue();
                    if (periodScore == null) {
                        continue;
                    }
                    CommonItem commonItem = periodScore.getSetScore();
                    if (commonItem == null || commonItem.getHome() == null || commonItem.getAway() == null) {
                        continue;
                    }
                    if (commonItem.getHome() > commonItem.getAway()) {
                        home += 1;
                    } else if (commonItem.getHome() < commonItem.getAway()) {
                        away += 1;
                    }
                }

                wholePeriodSores.getMatchScore().setHome(home);
                wholePeriodSores.getMatchScore().setAway(away);
                data.getMatchScoresInfo().setT1(home);
                data.getMatchScoresInfo().setT2(away);

                // 如果切换到小局休息，同时更新当前局（previousPeriodId）的 matchScore（因为该局已结束）
                if (previousPeriodId != null && SnookerConstant.SNOOKER_SET_BEGIN.containsKey(previousPeriodId)) {
                    SnookerV2Scores previousPeriodScores = allPeriodScores.get(previousPeriodId);
                    if (previousPeriodScores != null) {
                        if (previousPeriodScores.getMatchScore() == null) {
                            previousPeriodScores.setMatchScore(new CommonItem());
                        }
                        previousPeriodScores.getMatchScore().setHome(home);
                        previousPeriodScores.getMatchScore().setAway(away);
                        allPeriodScores.put(previousPeriodId, previousPeriodScores);
                        log.info("[MatchSnookerServiceImpl]addMatchPeriod updated previous period matchScore after recompute: home={}, away={} for periodId:{} match:{}",
                                home, away, previousPeriodId, data.getThirdMatchInfo().getId());
                    }
                }

                log.info("[MatchSnookerServiceImpl]addMatchPeriod recompute matchScore at rest: home={}, away={} targetPeriodId:{} previousPeriodId:{} match:{}",
                        home, away, targetPeriodId, previousPeriodId, data.getThirdMatchInfo().getId());

                // 更新 scoresJson，确保 previousPeriodId 的 matchScore 被保存
                data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
            } else {
                // Case 2/3：切到 SET_BEGIN（前进/回看）或 80/100/其它：matchScore 不变，仅同步到 MatchScoresInfo
                data.getMatchScoresInfo().setT1(home);
                data.getMatchScoresInfo().setT2(away);
            }
        }

        // 回看历史局：恢复全场盘分（避免切回旧局导致 t1/t2 变成“截至该局”或被覆盖）
        boolean backToOldSet = targetPeriodId != null
                && SnookerConstant.SNOOKER_SET_BEGIN.containsKey(targetPeriodId)
                && previousPeriodId != null
                && targetPeriodId < previousPeriodId;
        if (backToOldSet) {
            data.getMatchScoresInfo().setT1(keepTotalHome);
            data.getMatchScoresInfo().setT2(keepTotalAway);
            if (wholePeriodSores != null) {
                if (wholePeriodSores.getMatchScore() == null) {
                    wholePeriodSores.setMatchScore(new CommonItem());
                }
                wholePeriodSores.getMatchScore().setHome(keepTotalHome == null ? 0 : keepTotalHome);
                wholePeriodSores.getMatchScore().setAway(keepTotalAway == null ? 0 : keepTotalAway);
                allPeriodScores.put(WHOLE_MATCH, wholePeriodSores);
                data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
            }
        }

        //阶段比分更新
        JSONObject noPperiodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
        Map<Long, SnookerV2Scores> nowAllPeriodScores= JsonMapUtils.parseSnookerV2Map(noPperiodFootballScores);
        SnookerV2Scores nowPeriodSores = nowAllPeriodScores.get(targetPeriodId);
        Integer periodHomeScore = data.getMatchScoresInfo().getPeriodT1();
        Integer periodAwayScore = data.getMatchScoresInfo().getPeriodT2();
        if(nowPeriodSores != null && nowPeriodSores.getSetScore() != null && nowPeriodSores.getSetScore().getHome() != null) {
            periodHomeScore = nowPeriodSores.getSetScore().getHome();
            periodAwayScore = nowPeriodSores.getSetScore().getAway();
        }


        matchEventInfoDTO.setFirstT1(periodHomeScore);
        matchEventInfoDTO.setFirstT2(periodAwayScore);
        matchEventInfoDTO.setT1(data.getMatchScoresInfo().getT1());
        matchEventInfoDTO.setT2(data.getMatchScoresInfo().getT2());

        long currentTime = System.currentTimeMillis();
        data.getMatchScoresInfo().setSecondsMatchStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchScoresInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchScoresInfo().setModifyTime(currentTime);

        Integer timeGo = SnookerConstant.SNOOKER_SET_BEGIN.containsKey(matchEventInfoDTO.getMatchPeriodId()) ? 1 :0;
        data.getMatchTimeInfo().setSecondFromStart(matchEventInfoDTO.getSecondsFromStart());
        data.getMatchTimeInfo().setRemainingTime(matchEventInfoDTO.getPeriodRemainingSeconds());
        data.getMatchTimeInfo().setTimeGo(timeGo);
        data.getMatchTimeInfo().setEventTime(currentTime);
        data.getMatchTimeInfo().setModifyTime(currentTime);
        matchTimeInfoRepository.updateByPrimaryKey(data.getMatchTimeInfo());
        matchScoreInfoRepository.updateScoresInfo(data.getMatchScoresInfo());
    }
    
    /**
     * 记录阶段开始事件ID到Redis（用于重排时删除事件）
     * 此方法在事件插入后立即调用，确保事件ID正确记录
     * 
     * @param thirdMatchId 第三方赛事ID
     * @param periodId 阶段ID
     * @param eventId 事件ID
     */
    private void recordPeriodBeginEventId(Long thirdMatchId, Long periodId, Long eventId) {
        if (thirdMatchId != null && periodId != null && eventId != null 
                && SnookerConstant.SNOOKER_SET_BEGIN.containsKey(periodId)) {
            redisService.hSet(SnookerConstant.SNOOKER_PERIOD_BEGIN_EVENT_ID + thirdMatchId,
                    periodId + "", eventId, RedisConfig.REDIS_WEEK_TIME);
            log.info("[MatchSnookerServiceImpl]recordPeriodBeginEventId recorded eventId:{} for periodId:{} in match:{}", 
                    eventId, periodId, thirdMatchId);
        }
    }

    @Override
    protected void afterSnookerMatchStartStatusEvent(Long thirdMatchId, Long periodId, Long eventId, Integer controlType) {
        if (Integer.valueOf(1).equals(controlType)) {
            recordPeriodBeginEventId(thirdMatchId, periodId, eventId);
        }
    }

    /**
     * 比赛结束（controlType=4）：若当前仍停在某一局进行中（未先点小局结束），
     * 需要把该局 setScore 胜负结算进全场盘分，否则全局 t1/t2 会漏掉最后一局。
     */
    @Override
    public Response changeMatchStatus(MatchScoreAndTimeVo data, ChangeMatchStatusV2Dto dto) {
        if (dto != null && Integer.valueOf(4).equals(dto.getControlType())) {
            settleCurrentFrameOnMatchEnd(data);
        }
        return super.changeMatchStatus(data, dto);
    }

    /**
     * 将 scoresJson 中所有已决出胜负的小局（含当前进行中的最后一局）重算为全场盘分并写回。
     */
    private void settleCurrentFrameOnMatchEnd(MatchScoreAndTimeVo data) {
        if (data == null || data.getMatchScoresInfo() == null) {
            return;
        }
        MatchScoresInfo info = data.getMatchScoresInfo();
        if (StringUtils.isBlank(info.getScoresJson())) {
            return;
        }
        try {
            JSONObject periodScoresJson = JSONObject.parseObject(info.getScoresJson());
            Map<Long, SnookerV2Scores> allPeriodScores = JsonMapUtils.parseSnookerV2Map(periodScoresJson);
            // currentPeriodId 传 null：比赛已结束，当前局不再视为“进行中”，必须计入盘分
            CommonItem overall = calcOverallMatchScore(allPeriodScores, null);

            SnookerV2Scores whole = allPeriodScores.get(WHOLE_MATCH);
            if (whole == null) {
                whole = new SnookerV2Scores();
                allPeriodScores.put(WHOLE_MATCH, whole);
            }
            if (whole.getMatchScore() == null) {
                whole.setMatchScore(new CommonItem());
            }
            whole.getMatchScore().setHome(overall.getHome());
            whole.getMatchScore().setAway(overall.getAway());

            Long currentPeriodId = data.getMatchTimeInfo() != null ? data.getMatchTimeInfo().getPeriod() : null;
            if (currentPeriodId != null && SnookerConstant.SNOOKER_SET_BEGIN.containsKey(currentPeriodId)) {
                SnookerV2Scores currentPeriodScores = allPeriodScores.get(currentPeriodId);
                if (currentPeriodScores != null) {
                    if (currentPeriodScores.getMatchScore() == null) {
                        currentPeriodScores.setMatchScore(new CommonItem());
                    }
                    currentPeriodScores.getMatchScore().setHome(overall.getHome());
                    currentPeriodScores.getMatchScore().setAway(overall.getAway());
                }
            }

            info.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            info.setT1(overall.getHome());
            info.setT2(overall.getAway());
            info.setModifyTime(System.currentTimeMillis());
            matchScoreInfoRepository.updateScoresInfo(info);

            log.info("[MatchSnookerServiceImpl]settleCurrentFrameOnMatchEnd matchScore home={}, away={} currentPeriodId:{} match:{}",
                    overall.getHome(), overall.getAway(), currentPeriodId,
                    data.getThirdMatchInfo() != null ? data.getThirdMatchInfo().getId() : null);
        } catch (Exception e) {
            log.error("[MatchSnookerServiceImpl]settleCurrentFrameOnMatchEnd error match:{}",
                    data.getThirdMatchInfo() != null ? data.getThirdMatchInfo().getId() : null, e);
        }
    }

    protected Long sportType() {
        return 7l;
    }

    private static int batchEditNz(Integer v) {
        return v == null ? 0 : v;
    }

    /** 批量编辑日志：set1 对应 SNOOKER_SET_BEGIN，否则用 periodId */
    private static String batchEditSetLabel(Long periodId) {
        if (periodId == null) {
            return "set?";
        }
        Integer n = SnookerConstant.SNOOKER_SET_BEGIN.get(periodId);
        if (n != null) {
            return "set" + n + "(periodId=" + periodId + ")";
        }
        return "periodId=" + periodId;
    }

    /** 仅包含有变化的阶段，格式 [set1(...):{home:a,away:b}->{home:c,away:d}, ...] */
    private static String buildBatchEditSetScoreDiff(Map<Long, CommonItem> editScoreMap, Map<Long, int[]> oldSetScores) {
        if (editScoreMap == null || oldSetScores == null) {
            return "";
        }
        List<Long> periodIds = editScoreMap.keySet().stream().filter(Objects::nonNull).sorted().collect(Collectors.toList());
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
                    batchEditSetLabel(periodId), old[0], old[1], nh, na));
        }
        if (parts.isEmpty()) {
            return "";
        }
        return "[" + String.join(", ", parts) + "]";
    }

    /**
     * 构建初始化的 scoresJson 字符串，包含全局阶段和当前阶段（若有效）。
     */
    private String buildDefaultScoresJson(MatchScoreAndTimeVo data) {
        Map<Long, SnookerV2Scores> allPeriodScores = new HashMap<>();
        allPeriodScores.put(WHOLE_MATCH, new SnookerV2Scores());
        Long currentPeriod = data.getMatchTimeInfo().getPeriod();
        if (currentPeriod != null && currentPeriod > 0 && !currentPeriod.equals(WHOLE_MATCH)) {
            allPeriodScores.put(currentPeriod, new SnookerV2Scores());
        }
        return JSONObject.toJSONString(allPeriodScores);
    }

    /**
     * 当前局局比分（setScore）：从比分 JSON 中读取，仅比赛局阶段有效。
     */
    private CommonItem getCurrentPeriodSetScore(MatchScoreAndTimeVo data) throws Exception {
        if (data.getMatchTimeInfo() == null || data.getMatchScoresInfo() == null) {
            return null;
        }
        Long currentPeriodId = data.getMatchTimeInfo().getPeriod();
        if (currentPeriodId == null || !SnookerConstant.SNOOKER_SET_BEGIN.containsKey(currentPeriodId)) {
            return null;
        }
        String scoresJson = data.getMatchScoresInfo().getScoresJson();
        if (StringUtils.isEmpty(scoresJson)) {
            return null;
        }
        JSONObject scoreObject = JSONObject.parseObject(scoresJson);
        Map<Long, SnookerV2Scores> allPeriodScores = JsonMapUtils.parseSnookerV2Map(scoreObject);
        SnookerV2Scores periodScores = allPeriodScores.get(currentPeriodId);
        if (periodScores == null) {
            return null;
        }
        return periodScores.getSetScore();
    }

    /**
     * 事件分值记在「哪一侧」的局分/分项上：与 {@link SnookerV2Scores#doCalculation} 一致。
     * 犯规罚分、罚4、罚7 等 opposite=true 时 homeAway 表示犯规方，分值记在对手侧。
     */
    private static String snookerScoreReceivingSide(SnookerEventTypeEnum type, String homeAway) {
        if (homeAway == null) {
            return null;
        }
        if (type != null && Boolean.TRUE.equals(type.getOpposite())) {
            return TeamTypeConstant.HOME.equals(homeAway) ? TeamTypeConstant.AWAY : TeamTypeConstant.HOME;
        }
        return homeAway;
    }

    /**
     * 按事件编码获取“单项字段”（例如 snooker_score_pink -> pinkWiningScore）。
     * 用于删除事件时防止单项字段被扣成负数（UI 会显示 -1）。
     */
    private static CommonItem getSnookerSpecificItem(SnookerV2Scores scores, String eventCode) {
        if (scores == null || eventCode == null) return null;
        switch (eventCode) {
            case "snooker_score_red":
                return scores.getRedWiningScore();
            case "snooker_score_yellow":
                return scores.getYellowWiningScore();
            case "snooker_score_green":
                return scores.getGreenWiningScore();
            case "snooker_score_brown":
                return scores.getBrownWiningScore();
            case "snooker_score_blue":
                return scores.getBlueWiningScore();
            case "snooker_score_pink":
                return scores.getPinkWiningScore();
            case "snooker_score_black":
                return scores.getBlackWiningScore();
            case "free_ball_pot":
                return scores.getFreeball();
            case "free_ball_no_pot":
            case "free_ball_nopot":
                return scores.getFreeballNoPot();
            case "Striker":
                return scores.getStrick();
            case "which_team_serves_first":
                return scores.getKickoff();
            case "snooker_foul_red":
                return scores.getRedFoulScore();
            case "snooker_foul_yellow":
                return scores.getYellowFoulScore();
            case "snooker_foul_green":
                return scores.getGreenFoulScore();
            case "snooker_foul_brown":
                return scores.getBrownFoulScore();
            case "snooker_foul_blue":
                return scores.getBlueFoulScore();
            case "snooker_foul_pink":
                return scores.getPinkFoulScore();
            case "snooker_foul_black":
                return scores.getBlackFoulScore();
            case "snooker_foul_4":
                return scores.getPenaltyFour();
            case "snooker_foul_7":
                return scores.getPenaltySeven();
            default:
                return null;
        }
    }

    /**
     * 认输/直接判输校验：输方当前局局比分必须严格低于对方（例如主队认输/判输则主队局比分须小于客队）。
     *
     * @return 错误文案，null 表示通过
     */
    private String validateFrameLossSetScore(String homeAway, CommonItem curFrameSetScore, boolean isConcede) {
        int fh = (curFrameSetScore != null && curFrameSetScore.getHome() != null) ? curFrameSetScore.getHome() : 0;
        int fa = (curFrameSetScore != null && curFrameSetScore.getAway() != null) ? curFrameSetScore.getAway() : 0;
        String action = isConcede ? "认输" : "判输";
        if (TeamTypeConstant.HOME.equals(homeAway)) {
            if (fh >= fa) {
                return "主队" + action + "时，当前局主队局比分必须小于客队";
            }
        } else if (TeamTypeConstant.AWAY.equals(homeAway)) {
            if (fa >= fh) {
                return "客队" + action + "时，当前局客队局比分必须小于主队";
            }
        }
        return null;
    }

    /**
     * 改变比分（重写父类方法）
     * 处理进球和罚分事件
     * 当认输事件发生时，自动切换到小局休息并计算matchScore
     */
    @Override
    public Response changeScore(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchScoreV2Dto changeMatchScoreV2Dto) throws Exception {
        log.info("[MatchSnookerServiceImpl]changeScore start linkId::{}:data:{}", changeMatchScoreV2Dto.getLinkedId(), changeMatchScoreV2Dto);

        boolean frameLoss = SnookerEventTypeEnum.CONCEDE.getEventCode().equals(changeMatchScoreV2Dto.getEventCode())
                || SnookerEventTypeEnum.FOUL_LOSE.getEventCode().equals(changeMatchScoreV2Dto.getEventCode());
        if (frameLoss) {
            boolean isConcede = SnookerEventTypeEnum.CONCEDE.getEventCode().equals(changeMatchScoreV2Dto.getEventCode());
            String err = validateFrameLossSetScore(changeMatchScoreV2Dto.getHomeAway(),
                    getCurrentPeriodSetScore(matchScoreAndTimeVo), isConcede);
            if (err != null) {
                return Response.failed(err);
            }
        }

        // 调用父类方法处理比分更新
        super.changeScore(matchScoreAndTimeVo, changeMatchScoreV2Dto);
        
        // 认输或直接判输：本局结束自动切小局休息（存储：concede=认输，directLoss=直接判输）
        if (SnookerEventTypeEnum.CONCEDE.getEventCode().equals(changeMatchScoreV2Dto.getEventCode())
                || SnookerEventTypeEnum.FOUL_LOSE.getEventCode().equals(changeMatchScoreV2Dto.getEventCode())) {
            // 重新查询最新的比分信息（因为父类方法已经更新了比分）
            MatchScoreAndTimeVo latestData = commonAdvertiseService.searchMatchScoreAndTime(changeMatchScoreV2Dto.getThirdMatchId());

            Long currentPeriodId = latestData.getMatchTimeInfo().getPeriod();
            Long restPeriodId = SnookerConstant.SNOOKER_SET_BEGIN_TO_END.get(currentPeriodId);
            if (restPeriodId == null) {
                log.warn("[MatchSnookerServiceImpl]changeScore cannot find rest period for period:{} match:{} linkId::{}, using default 445",
                        currentPeriodId, changeMatchScoreV2Dto.getThirdMatchId(), changeMatchScoreV2Dto.getLinkedId());
                restPeriodId = 445L;
            }
            log.info("[MatchSnookerServiceImpl]changeScore frame-loss event (concede/foul_lose), switching to rest period:{} match:{} linkId::{}",
                    restPeriodId, changeMatchScoreV2Dto.getThirdMatchId(), changeMatchScoreV2Dto.getLinkedId());

            // 切换到小局休息
            ChangeMatchPeriodV2Dto changeMatchPeriodV2Dto = new ChangeMatchPeriodV2Dto();
            changeMatchPeriodV2Dto.setSportId(changeMatchScoreV2Dto.getSportId());
            changeMatchPeriodV2Dto.setThirdMatchId(changeMatchScoreV2Dto.getThirdMatchId());
            changeMatchPeriodV2Dto.setPeriodId(restPeriodId);
            changeMatchPeriodV2Dto.setLinkedId(changeMatchScoreV2Dto.getLinkedId() + "_FRAME_LOSS_REST");
            changeMatchPeriodV2Dto.setRestTime(120L);
            changeMatchPeriodV2Dto.setOperatorId(changeMatchScoreV2Dto.getOperatorId());
            changeMatchPeriodV2Dto.setOperatorName(changeMatchScoreV2Dto.getOperatorName());
            changeMatchPeriodV2Dto.setIpAddress(changeMatchScoreV2Dto.getIpAddress());
            changeMatchPeriodV2Dto.setLanguage(changeMatchScoreV2Dto.getLanguage());
            
            // 调用changeMatchPeriod切换到小局休息（会自动计算matchScore）
            changeMatchPeriod(latestData, changeMatchPeriodV2Dto);
            
            log.info("[MatchSnookerServiceImpl]changeScore auto switched to rest period after frame-loss match:{} linkId::{}", 
                    changeMatchScoreV2Dto.getThirdMatchId(), changeMatchScoreV2Dto.getLinkedId());
        }
        
        log.info("[MatchSnookerServiceImpl]changeScore end linkId::{}", changeMatchScoreV2Dto.getLinkedId());
        return Response.success();
    }

    /**
     * 发送事件（多数不影响局分的事件，如持杆、未进球等）。
     * 认输 / 直接判输：不修改局分 setScore；存储认输→{@code concede}、直接判输→{@code directLoss}（本局+全场各+1）；重算 matchScore；不自动切小局休息。
     */
    @Override
    public Response sendEvent(MatchScoreAndTimeVo data, SendEventDto dto) throws Exception {
        log.info("[MatchSnookerServiceImpl]sendEvent start linkId::{}:data:{}", dto.getLinkedId(), dto);

        boolean sendEventFrameLoss = SnookerEventTypeEnum.CONCEDE.getEventCode().equals(dto.getEventCode())
                || SnookerEventTypeEnum.FOUL_LOSE.getEventCode().equals(dto.getEventCode());
        // 认输 / 直接判输：局分不变；认输→concede、直接判输→directLoss（本局+全场各+1），并按本局输方结算盘分
        if (sendEventFrameLoss) {
            log.info("[MatchSnookerServiceImpl]sendEvent frame-loss (concede→concede / foul_lose→directLoss), set score unchanged match:{} event:{} linkId::{}",
                    dto.getThirdMatchId(), dto.getEventCode(), dto.getLinkedId());
            
            // 获取当前局信息
            Long currentPeriodId = data.getMatchTimeInfo().getPeriod();
            if (currentPeriodId == null || !SnookerConstant.SNOOKER_SET_BEGIN.containsKey(currentPeriodId)) {
                log.warn("[MatchSnookerServiceImpl]sendEvent current period is not a valid match period:{} match:{} linkId::{}", 
                        currentPeriodId, dto.getThirdMatchId(), dto.getLinkedId());
                return Response.failed("当前阶段不是有效的比赛阶段，无法处理该事件");
            }
            
            // 解析比分数据
            String scoresJson = data.getMatchScoresInfo().getScoresJson();
            if (StringUtils.isEmpty(scoresJson)) {
                log.warn("[MatchSnookerServiceImpl]sendEvent scoresJson is empty for match:{} linkId::{}", 
                        dto.getThirdMatchId(), dto.getLinkedId());
                return Response.failed("比分数据为空，无法处理该事件");
            }
            
            JSONObject scoreObject = JSONObject.parseObject(scoresJson);
            Map<Long, SnookerV2Scores> allPeriodScores = JsonMapUtils.parseSnookerV2Map(scoreObject);
            SnookerV2Scores periodScores = allPeriodScores.get(currentPeriodId);
            
            if (periodScores == null) {
                periodScores = new SnookerV2Scores();
                allPeriodScores.put(currentPeriodId, periodScores);
            }
            
            // 获取或初始化 setScore
            CommonItem setScore = periodScores.getSetScore();
            if (setScore == null) {
                setScore = new CommonItem();
                periodScores.setSetScore(setScore);
            }

            if (SnookerEventTypeEnum.CONCEDE.getEventCode().equals(dto.getEventCode())) {
                String concedeErr = validateFrameLossSetScore(dto.getHomeAway(), setScore, true);
                if (concedeErr != null) return Response.failed(concedeErr);
            }
            if (SnookerEventTypeEnum.FOUL_LOSE.getEventCode().equals(dto.getEventCode())) {
                String foulLoseErr = validateFrameLossSetScore(dto.getHomeAway(), setScore, false);
                if (foulLoseErr != null) return Response.failed(foulLoseErr);
            }

            // 不改变局比分（setScore）；盘分：其它局仍按 setScore 比较，当前局认输/判输时本局胜方为 homeAway 的对方（避免 0-0 判输无法记局胜）
            String homeAway = dto.getHomeAway();
            if (TeamTypeConstant.HOME.equals(homeAway)) {
                log.info("[MatchSnookerServiceImpl]sendEvent home loses frame (concede/foul_lose), setScore unchanged home={} away={} match:{} period:{} linkId::{}",
                        setScore.getHome(), setScore.getAway(), dto.getThirdMatchId(), currentPeriodId, dto.getLinkedId());
            } else if (TeamTypeConstant.AWAY.equals(homeAway)) {
                log.info("[MatchSnookerServiceImpl]sendEvent away loses frame (concede/foul_lose), setScore unchanged home={} away={} match:{} period:{} linkId::{}",
                        setScore.getHome(), setScore.getAway(), dto.getThirdMatchId(), currentPeriodId, dto.getLinkedId());
            } else {
                log.warn("[MatchSnookerServiceImpl]sendEvent invalid homeAway:{} for match:{} linkId::{}", 
                        homeAway, dto.getThirdMatchId(), dto.getLinkedId());
                return Response.failed("无效的主客队标识");
            }

            boolean isConcede = SnookerEventTypeEnum.CONCEDE.getEventCode().equals(dto.getEventCode());
            CommonItem periodCnt = isConcede ? periodScores.getConcede() : periodScores.getDirectLoss();
            if (periodCnt == null) {
                periodCnt = new CommonItem();
                if (isConcede) {
                    periodScores.setConcede(periodCnt);
                } else {
                    periodScores.setDirectLoss(periodCnt);
                }
            }
            SnookerV2Scores wholeSlice = allPeriodScores.get(WHOLE_MATCH);
            if (wholeSlice == null) {
                wholeSlice = new SnookerV2Scores();
                allPeriodScores.put(WHOLE_MATCH, wholeSlice);
            }
            CommonItem wholeCnt = isConcede ? wholeSlice.getConcede() : wholeSlice.getDirectLoss();
            if (wholeCnt == null) {
                wholeCnt = new CommonItem();
                if (isConcede) {
                    wholeSlice.setConcede(wholeCnt);
                } else {
                    wholeSlice.setDirectLoss(wholeCnt);
                }
            }
            if (TeamTypeConstant.AWAY.equals(homeAway)) {
                int pa = periodCnt.getAway() != null ? periodCnt.getAway() : 0;
                int wa = wholeCnt.getAway() != null ? wholeCnt.getAway() : 0;
                periodCnt.setAway(pa + 1);
                wholeCnt.setAway(wa + 1);
            } else {
                int ph = periodCnt.getHome() != null ? periodCnt.getHome() : 0;
                int wh = wholeCnt.getHome() != null ? wholeCnt.getHome() : 0;
                periodCnt.setHome(ph + 1);
                wholeCnt.setHome(wh + 1);
            }
            
            // 更新 periodT1 和 periodT2
            data.getMatchScoresInfo().setPeriodT1(setScore.getHome());
            data.getMatchScoresInfo().setPeriodT2(setScore.getAway());
            
            // 计算并更新全局阶段（-1）的 matchScore
            SnookerV2Scores wholeSores = allPeriodScores.get(WHOLE_MATCH);
            if (wholeSores == null) {
                wholeSores = new SnookerV2Scores();
                allPeriodScores.put(WHOLE_MATCH, wholeSores);
            }
            
            Integer home = 0;
            Integer away = 0;
            // 统计局胜负：非当前局只看 setScore；当前局为认输/判输时胜方为对手（与 changeScore 中认输校验下仅按分比较等价）
            for (Map.Entry<Long, SnookerV2Scores> entrySet : allPeriodScores.entrySet()) {
                Long periodId = entrySet.getKey();
                if (periodId.equals(WHOLE_MATCH)) {
                    continue;
                }
                SnookerV2Scores periodScore = entrySet.getValue();
                if (periodScore == null) {
                    continue;
                }
                if (periodId.equals(currentPeriodId)) {
                    if (TeamTypeConstant.HOME.equals(homeAway)) {
                        away += 1;
                    } else if (TeamTypeConstant.AWAY.equals(homeAway)) {
                        home += 1;
                    }
                    continue;
                }
                CommonItem periodSetScore = periodScore.getSetScore();
                if (periodSetScore != null) {
                    if (periodSetScore.getHome() > periodSetScore.getAway()) {
                        home += 1;
                    } else if (periodSetScore.getHome() < periodSetScore.getAway()) {
                        away += 1;
                    }
                }
            }
            
            // 更新全局的 matchScore（-1）
            wholeSores.getMatchScore().setHome(home);
            wholeSores.getMatchScore().setAway(away);
            data.getMatchScoresInfo().setT1(home);
            data.getMatchScoresInfo().setT2(away);
            
            // 同时更新当前局的 matchScore
            periodScores.getMatchScore().setHome(home);
            periodScores.getMatchScore().setAway(away);
            
            // 保存比分（确保 setScore 和 matchScore 都已正确设置）
            data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
            data.getMatchScoresInfo().setModifyTime(System.currentTimeMillis());
            matchScoreInfoRepository.updateScoresInfo(data.getMatchScoresInfo());
            
            log.info("[MatchSnookerServiceImpl]sendEvent frame-loss: setScore home={}, away={}, matchScore home={}, away={} period:{} match:{} linkId::{}", 
                    setScore.getHome(), setScore.getAway(), home, away, currentPeriodId, dto.getThirdMatchId(), dto.getLinkedId());
            
            // 更新 Redis 缓存
            redisUtils.pushFootBallScore(dto.getThirdMatchId());
        }

        // 认输/判输分支已在上面就地更新 data 内比分；其它事件直接用入参 data
        MatchScoreAndTimeVo eventData = data;

        // 转换为EventOperationV2Dto
        EventOperationV2Dto eventOperationV2Dto = EventOperationV2Dto.builder()
                .sportId(dto.getSportId())
                .thirdMatchId(dto.getThirdMatchId())
                .eventCode(dto.getEventCode())
                .homeAway(dto.getHomeAway())
                .secondFromStart(dto.getSecondFromStart())
                .build();
        eventOperationV2Dto.setOperatorId(dto.getOperatorId());
        eventOperationV2Dto.setOperatorName(dto.getOperatorName());
        eventOperationV2Dto.setIpAddress(dto.getIpAddress());
        eventOperationV2Dto.setLanguage(dto.getLanguage());

        // 创建MatchEventInfoDTO
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(eventData, eventOperationV2Dto, 0, 0l, null);
        // 设置 secondsFromStart，用于 sendPDEventInfo 保存到数据库
        if (eventOperationV2Dto.getSecondFromStart() != null) {
            matchEventInfoDTO.setSecondsFromStart(eventOperationV2Dto.getSecondFromStart());
        }
        
        // 统一使用请求链路ID（便于排查/串联日志）
        matchEventInfoDTO.setCopyLinkId(dto.getLinkedId());

        // Striker（持杆）事件需要累计次数到 scoresJson（与 kickoff 自动追加 Striker 计数口径一致）
        if (SnookerEventTypeEnum.STRIKER.getEventCode().equals(dto.getEventCode())) {
            updateMatchScore(eventData, matchEventInfoDTO, false);
        }
        
        // 使用 sendPDEventInfo 发送事件（会保存到数据库并发送到 THIRD_MATCH_EVENT_INFO_API）
        eventProducer.sendPDSnookerEventInfo(matchEventInfoDTO);
        redisUtils.pushFootBallEvent(eventOperationV2Dto.getThirdMatchId());

        // 如果是 Striker 事件，记录当前持杆方到 Redis
        if (SnookerEventTypeEnum.STRIKER.getEventCode().equals(dto.getEventCode())) {
            String homeAway = dto.getHomeAway();
            int home = TeamTypeConstant.HOME.equals(homeAway) ? 1 : 0;
            int away = TeamTypeConstant.AWAY.equals(homeAway) ? 1 : 0;
            matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(), SnookerConstant.CURRENT_STRIKER, home, away);
            log.info("[MatchSnookerServiceImpl]sendEvent saved currentStriker for match:{} homeAway:{} home:{} away:{} linkId::{}", 
                    dto.getThirdMatchId(), homeAway, home, away, dto.getLinkedId());
        }

        // 记录操作日志
        MatchCommonLogDto matchCommonLogDto = new MatchCommonLogDto();
        matchCommonLogDto.setSportId(dto.getSportId());
        matchCommonLogDto.setThirdMatchId(dto.getThirdMatchId());
        matchCommonLogDto.setEventCode(dto.getEventCode());
        matchCommonLogDto.setHowAway(dto.getHomeAway());
        matchCommonLogDto.setAfterVal(dto.getEventCode() + " - " + dto.getHomeAway());
        matchCommonLogDto.setLinkedId(dto.getLinkedId());
        matchCommonLogDto.setOperatorId(dto.getOperatorId());
        matchCommonLogDto.setOperatorName(dto.getOperatorName());
        matchCommonLogDto.setIpAddress(dto.getIpAddress());
        matchCommonLogDto.setLanguage(dto.getLanguage());
        matchScorePdLogService.setMatchCommonLog(eventData, matchCommonLogDto);

        log.info("[MatchSnookerServiceImpl]sendEvent end linkId::{}", dto.getLinkedId());
        return Response.success();
    }

    /**
     * 开球（重写父类方法，添加斯诺克特殊处理）
     */
    @Override
    public Response kickOff(MatchScoreAndTimeVo data, KickOffV2Dto dto) throws Exception {
        log.info("[MatchSnookerServiceImpl]kickOff start linkId::{}:data:{}", dto.getLinkedId(), dto);
        
        // 父类会先执行 kickOffPostProcess（写 kickoffFirstClick 等 Redis），再算分、发开球事件
        super.kickOff(data, dto);
        
        // 开球后自动发送 Striker 事件，记录当前持杆方（持杆次数仅在 sendEvent 内 updateMatchScore 一次，避免与 sendEvent 重复累加）
        if (StringUtils.isNotBlank(dto.getWhoKickOff())) {
            SendEventDto sendEventDto = new SendEventDto();
            sendEventDto.setSportId(dto.getSportId());
            sendEventDto.setThirdMatchId(dto.getThirdMatchId());
            sendEventDto.setEventCode(SnookerEventTypeEnum.STRIKER.getEventCode());
            sendEventDto.setHomeAway(dto.getWhoKickOff());
            sendEventDto.setLinkedId(dto.getLinkedId());
            sendEventDto.setSecondFromStart(dto.getSecondFromStart());
            // 透传开球操作人的基础信息，确保自动追加的 Striker 事件日志完整
            sendEventDto.setOperatorId(dto.getOperatorId());
            sendEventDto.setOperatorName(dto.getOperatorName());
            sendEventDto.setIpAddress(dto.getIpAddress());
            sendEventDto.setLanguage(dto.getLanguage());
            
            log.info("[MatchSnookerServiceImpl]kickOff auto sending Striker event for match:{} homeAway:{} linkId::{}", 
                    dto.getThirdMatchId(), dto.getWhoKickOff(), dto.getLinkedId());
            sendEvent(data, sendEventDto);
        }
        
        log.info("[MatchSnookerServiceImpl]kickOff end linkId::{}", dto.getLinkedId());
        return Response.success();
    }

    /**
     * 切换阶段（重写父类方法，添加斯诺克特殊处理）
     * 优化：在事件插入后立即记录事件ID，避免时序问题
     * 确保所有阶段切换（包括小局开始、小局休息等）都创建事件
     */
    @Override
    public Response changeMatchPeriod(MatchScoreAndTimeVo data, ChangeMatchPeriodV2Dto dto) {
        log.info("[MatchSnookerServiceImpl]changeMatchPeriod start linkId::{}:data:{} restTime:{}", 
                dto.getLinkedId(), dto, dto.getRestTime());
        
        checkPhase(data, dto);
        // 检查是否已经是当前局
        if (dto.getPeriodId() != null) {
            Long currentPeriod = data.getMatchTimeInfo().getPeriod();
            if (currentPeriod != null && currentPeriod.equals(dto.getPeriodId())) {
                log.warn("[MatchSnookerServiceImpl]changeMatchPeriod periodId:{} is already the current period for match:{} linkId::{}", 
                        dto.getPeriodId(), dto.getThirdMatchId(), dto.getLinkedId());
                return Response.failed("已经为当前局了");
            }
        }
        
        // 如果是阶段开始事件，使用优化的方法获取事件ID
        if (dto.getPeriodId() != null && SnookerConstant.SNOOKER_SET_BEGIN.containsKey(dto.getPeriodId())) {
            EventOperationV2Dto eventOperationV2Dto = eventOperationConverter.changeMatchPeriodToEvent(dto);
            eventOperationV2Dto.setOperatorId(dto.getOperatorId());
            eventOperationV2Dto.setOperatorName(dto.getOperatorName());
            eventOperationV2Dto.setIpAddress(dto.getIpAddress());
            eventOperationV2Dto.setLanguage(dto.getLanguage());
            Long remainTime = dto.getRestTime() != null ? dto.getRestTime() : 0L;
            MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createCommonMatchEvent(
                    data, eventOperationV2Dto, 0, remainTime, dto.getPeriodId());
            
            // 先执行阶段切换逻辑
            addMatchPeriod(data, matchEventInfoDTO);
            
            // 插入事件并获取事件ID
            MatchCommonLogDto matchCommonLogDto = pdOperationLogConverter.convertChangePeriodToLog(dto);
            matchCommonLogDto.setOperatorId(dto.getOperatorId());
            matchCommonLogDto.setOperatorName(dto.getOperatorName());
            matchCommonLogDto.setIpAddress(dto.getIpAddress());
            matchCommonLogDto.setLanguage(dto.getLanguage());
            Long eventId = matchScoreCommonHelper.commonProcessAndReturnEventId(data, eventOperationV2Dto, matchEventInfoDTO, matchCommonLogDto);
            
            // 立即记录阶段开始事件ID到Redis（用于重排时删除事件）
            recordPeriodBeginEventId(dto.getThirdMatchId(), dto.getPeriodId(), eventId);
            
            // 小局结束/阶段切换时 currentStriker、kickoffFirstClick 置为默认 0,0
            if (dto.getPeriodId() != 80 && dto.getPeriodId() != 100) {
                matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(), SnookerConstant.KICKOFF_FIRST_CLICK, 0, 0);
                matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(), SnookerConstant.CURRENT_STRIKER, 0, 0);
            }
        } else {
            // 非阶段开始事件（如小局休息、其他阶段切换），也需要创建事件
            // 使用父类方法，它会创建事件
            super.changeMatchPeriod(data, dto);
            
            // 如果有restTime，需要更新事件中的剩余时间
            if (dto.getRestTime() != null && dto.getRestTime() > 0) {
                log.info("[MatchSnookerServiceImpl]changeMatchPeriod restTime detected:{} for periodId:{} linkId::{}", 
                        dto.getRestTime(), dto.getPeriodId(), dto.getLinkedId());
                // restTime已经在父类方法中通过MatchEventUtils.createCommonMatchEvent处理
                // periodRemainingSeconds会被设置为restTime
            }
        }
        matchScoreCommonHelper.setMatchCacheStatus(dto.getThirdMatchId(), SnookerConstant.MATCH_CURRENT_PERIOD, dto.getPeriodId());
        log.info("[MatchSnookerServiceImpl]changeMatchPeriod end linkId::{}", dto.getLinkedId());
        return Response.success();
    }

    void checkPhase(MatchScoreAndTimeVo data, ChangeMatchPeriodV2Dto dto){
        // 斯诺克阶段跳转规则限制：
        // 1）总体上只能往已进行过的小局跳（不能直接跳到未来小局，例如当前最高局数为10时不能直接跳到31局）；
        // 2）特殊：当处于第N局的小局休息（该局结束阶段）时，可以跳到第N+1局的开始阶段；
        // 3）第N局进行中时，允许从当前局开始阶段跳到当前局结束阶段。
        try {
            Long targetPeriodId = dto.getPeriodId();
            if (targetPeriodId != null) {
                // 构造结束阶段 -> 局序号 的映射
                Map<Long, Long> endPeriodIndexMap = new HashMap<>();
                for (Map.Entry<Long, Long> entry : SnookerConstant.SNOOKER_SET_BEGIN_TO_END.entrySet()) {
                    Long beginId = entry.getKey();
                    Long endId = entry.getValue();
                    Integer idx = SnookerConstant.SNOOKER_SET_BEGIN.get(beginId);
                    if (idx != null) {
                        endPeriodIndexMap.put(endId, idx.longValue());
                    }
                }

                boolean isBeginTarget = SnookerConstant.SNOOKER_SET_BEGIN.containsKey(targetPeriodId);
                boolean isEndTarget = endPeriodIndexMap.containsKey(targetPeriodId);

                // 仅对斯诺克局开始/结束阶段做限制，其他阶段直接放行
                if (isBeginTarget || isEndTarget) {
                    // 计算当前局序号
                    Long currentPeriodId = data.getMatchTimeInfo() != null ? data.getMatchTimeInfo().getPeriod() : null;
                    Integer currentFrameIndex = null;
                    if (currentPeriodId != null) {
                        if (SnookerConstant.SNOOKER_SET_BEGIN.containsKey(currentPeriodId)) {
                            currentFrameIndex = SnookerConstant.SNOOKER_SET_BEGIN.get(currentPeriodId);
                        } else if (endPeriodIndexMap.containsKey(currentPeriodId)) {
                            currentFrameIndex = endPeriodIndexMap.get(currentPeriodId).intValue();
                        }
                    }

                    // 根据 scoresJson 计算当前已进行的最高局数（maxFrameIndex）
                    int maxFrameIndex = 0;
                    String scoresJson = data.getMatchScoresInfo() != null ? data.getMatchScoresInfo().getScoresJson() : null;
                    if (StringUtils.isNotBlank(scoresJson)) {
                        JSONObject periodScoresJson = JSONObject.parseObject(scoresJson);
                        Map<Long, SnookerV2Scores> allPeriodScores = JsonMapUtils.parseSnookerV2Map(periodScoresJson);
                        for (Long periodId : allPeriodScores.keySet()) {
                            if (SnookerConstant.SNOOKER_SET_BEGIN.containsKey(periodId)) {
                                maxFrameIndex = Math.max(maxFrameIndex, SnookerConstant.SNOOKER_SET_BEGIN.get(periodId));
                            } else if (endPeriodIndexMap.containsKey(periodId)) {
                                maxFrameIndex = Math.max(maxFrameIndex, endPeriodIndexMap.get(periodId).intValue());
                            }
                        }
                    }

                    // 目标局序号
                    Integer targetFrameIndex = null;
                    if (isBeginTarget) {
                        targetFrameIndex = SnookerConstant.SNOOKER_SET_BEGIN.get(targetPeriodId);
                    } else {
                        Long idx = endPeriodIndexMap.get(targetPeriodId);
                        if (idx != null) {
                            targetFrameIndex = idx.intValue();
                        }
                    }

                    if (targetFrameIndex != null) {
                        boolean allowed = false;

                        if (isBeginTarget) {
                            // 跳到某局开始：
                            // 0) 未开赛 / scoresJson 尚无任一局局阶段键：允许进入第 1 局（否则开始比赛会一直报 jump conditions）
                            if (targetFrameIndex == 1 && maxFrameIndex == 0) {
                                allowed = true;
                            }
                            // a) 若该局已经在 scoresJson 中出现过（targetIndex <= maxFrameIndex），则允许自由回跳；
                            if (!allowed && maxFrameIndex > 0 && targetFrameIndex <= maxFrameIndex) {
                                allowed = true;
                            } else if (!allowed && currentFrameIndex != null && currentPeriodId != null) {
                                // b) 若当前处于第N局小局休息（结束阶段），允许跳到第N+1局开始
                                boolean atRestOfCurrent =
                                        endPeriodIndexMap.containsKey(currentPeriodId)
                                                && endPeriodIndexMap.get(currentPeriodId).intValue() == currentFrameIndex;
                                if (atRestOfCurrent && targetFrameIndex == currentFrameIndex + 1) {
                                    allowed = true;
                                }
                            }
                        } else if (isEndTarget) {
                            // 跳到某局结束（小局休息）：
                            // a) 若该局在 scoresJson 已经出现（targetIndex <= maxFrameIndex），则允许；
                            if (maxFrameIndex > 0 && targetFrameIndex <= maxFrameIndex) {
                                allowed = true;
                            } else if (currentFrameIndex != null) {
                                // b) 允许从当前局开始阶段直接跳到当前局结束阶段
                                if (targetFrameIndex.equals(currentFrameIndex)) {
                                    allowed = true;
                                }
                            }
                        }

                        if (!allowed) {
                            log.warn("[MatchSnookerServiceImpl]changeMatchPeriod illegal period jump, currentPeriodId:{} currentFrameIndex:{} targetPeriodId:{} targetFrameIndex:{} maxFrameIndex:{} match:{} linkId::{}",
                                    currentPeriodId, currentFrameIndex, targetPeriodId, targetFrameIndex, maxFrameIndex,
                                    dto.getThirdMatchId(), dto.getLinkedId());
                            throw new RuntimeException("The current target frame does not meet the jump conditions");
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            log.error("[MatchSnookerServiceImpl]changeMatchPeriod validate period error match:{} linkId::{}", dto.getThirdMatchId(), dto.getLinkedId(), e);
            throw e;
        } catch (Exception e) {
            log.error("[MatchSnookerServiceImpl]changeMatchPeriod validate period error match:{} linkId::{}", dto.getThirdMatchId(), dto.getLinkedId(), e);
        }
    }
}
