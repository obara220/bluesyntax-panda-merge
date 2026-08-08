package com.panda.merge.snooker.service.helper;

import com.alibaba.fastjson.JSON;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.CommonAdvertiseService;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MatchInfoConvertEnum;
import com.panda.merge.constant.SnookerConstant;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.advertise.v2.ChangeMatchStatusV2Dto;
import com.panda.merge.dto.advertise.v2.EventOperationV2Dto;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.service.IMatchScorePdLogService;
import com.panda.merge.snooker.converter.EventOperationConverter;
import com.panda.merge.snooker.dto.MatchCommonLogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MatchScoreCommonHelper {
    @Resource
    private RedisService redisService;

    @Resource
    private EventProducer eventProducer;
    @Resource
    private MatchTimeInfoRepository timeInfoRepository;
    @Resource
    private MatchScoreInfoRepository matchScoreInfoRepository;
    @Resource
    private EventOperationConverter eventOperationConverter;
    @Autowired
    PdMatchInfoRepository pdMatchInfoRepository;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    RedisUtils redisUtils;
    @Resource
    IMatchScorePdLogService matchScorePdLogService;
    @Autowired
    CommonAdvertiseService commonAdvertiseService;
    @Resource
    private MatchScoreCommonHelper matchScoreCommonHelper;

    public void procInterruptedEvent(MatchScoreAndTimeVo matchScoreAndTimeVo, ChangeMatchStatusV2Dto changeMatchStatusV2Dto, MatchInfoConvertEnum convertEnum) {

        Integer timeGo = 1;
        if (convertEnum == MatchInfoConvertEnum.MATCH_INTERRUPTED) {
            matchScoreCommonHelper.setMatchCacheStatus(changeMatchStatusV2Dto.getThirdMatchId(), SnookerConstant.MATCH_INTERRUPTED, true);
//            timeGo = 0;
        } else if (convertEnum == MatchInfoConvertEnum.MATCH_RESTART) {
            matchScoreCommonHelper.setMatchCacheStatus(changeMatchStatusV2Dto.getThirdMatchId(), SnookerConstant.MATCH_INTERRUPTED, false);
        } else if (convertEnum == MatchInfoConvertEnum.MATCH_EVENT_INTERRUPTED) {
//            timeGo = 0;
            matchScoreCommonHelper.setMatchCacheStatus(changeMatchStatusV2Dto.getThirdMatchId(), SnookerConstant.MATCH_EVENT_INTERRUPTED, true);
        } else if (convertEnum == MatchInfoConvertEnum.MATCH_EVENT_RESTART) {
            matchScoreCommonHelper.setMatchCacheStatus(changeMatchStatusV2Dto.getThirdMatchId(), SnookerConstant.MATCH_EVENT_INTERRUPTED, false);
        } else {
            return;
        }
//        long currentTime = System.currentTimeMillis();
//        matchScoreAndTimeVo.getMatchTimeInfo().setPeriod(changeMatchStatusV2Dto.getPeriodId());
//        matchScoreAndTimeVo.getMatchTimeInfo().setTimeGo(timeGo);
//        matchScoreAndTimeVo.getMatchTimeInfo().setModifyTime(currentTime);
//        matchScoreAndTimeVo.getMatchTimeInfo().setEventTime(currentTime);
//
//        matchScoreAndTimeVo.getMatchScoresInfo().setPeriod(changeMatchStatusV2Dto.getPeriodId());
//        matchScoreAndTimeVo.getMatchScoresInfo().setModifyTime(currentTime);
//        matchTimeInfoRepository.updateByPrimaryKey(matchScoreAndTimeVo.getMatchTimeInfo());
//        matchScoreInfoRepository.updateScoresInfo(matchScoreAndTimeVo.getMatchScoresInfo());
    }

    public void commonProcess(MatchScoreAndTimeVo matchScoreAndTimeVo, EventOperationV2Dto eventOperationV2Dto, MatchEventInfoDTO matchEventInfoDTO, MatchCommonLogDto matchCommonLogDto){
        // 设置 secondsFromStart，用于 sendPDEventInfo 保存到数据库
        if (eventOperationV2Dto.getSecondFromStart() != null) {
            matchEventInfoDTO.setSecondsFromStart(eventOperationV2Dto.getSecondFromStart());
        }
        // 设置 extraInfo 到 addition7（斯诺克特殊处理，与 updateEventInfo 保持一致）
        if (matchEventInfoDTO.getExtraInfo() != null) {
            matchEventInfoDTO.setAddition7(matchEventInfoDTO.getExtraInfo());
            // 同时设置 extrainfo（已废弃字段），确保 sendPDEventInfo 能正确获取
            matchEventInfoDTO.setExtrainfo(matchEventInfoDTO.getExtraInfo());
        }
        // 对于中断/重开事件，设置 homeAway="all"（在 sendPDEventInfo 之前设置）
        // 注意：排球 ct=2/3 的 eventCode 已被 customizeStatusEvent 改写为 "timeout"/"timeout_over"，
        // 需与斯诺克的 "suspension"/"suspension_over" 一并覆盖。
        if (MatchInfoConvertEnum.MATCH_INTERRUPTED.getValue().equals(matchEventInfoDTO.getEventCode()) ||
                MatchInfoConvertEnum.MATCH_RESTART.getValue().equals(matchEventInfoDTO.getEventCode()) ||
                MatchInfoConvertEnum.MATCH_EVENT_INTERRUPTED.getValue().equals(matchEventInfoDTO.getEventCode()) ||
                MatchInfoConvertEnum.MATCH_EVENT_RESTART.getValue().equals(matchEventInfoDTO.getEventCode()) ||
                "timeout".equals(matchEventInfoDTO.getEventCode()) ||
                "timeout_over".equals(matchEventInfoDTO.getEventCode())) {
            matchEventInfoDTO.setHomeAway("all");
        }
        // 斯诺克(7) / 排球(9) 走 sendPDSnookerEventInfo：补齐 third_match_id、按局映射 firstNum、extraInfo→addition7；其它球种沿用 sendPDEventInfo
        if (isProfessionalSavePath(matchEventInfoDTO.getSportId())) {
            eventProducer.sendPDSnookerEventInfo(matchEventInfoDTO);
        } else {
            eventProducer.sendPDEventInfo(matchEventInfoDTO);
        }
        redisUtils.pushFootBallScore(eventOperationV2Dto.getThirdMatchId());
        redisUtils.pushFootBallEvent(eventOperationV2Dto.getThirdMatchId());
        matchScorePdLogService.setMatchCommonLog(matchScoreAndTimeVo, matchCommonLogDto);
    }

    private static boolean isProfessionalSavePath(Long sportId) {
        return sportId != null && (sportId.equals(7L) || sportId.equals(9L));
    }
    
    /**
     * 通用处理流程，返回插入的事件ID
     * 用于需要立即获取事件ID的场景（如阶段切换）
     * 
     * @param matchScoreAndTimeVo 比赛信息
     * @param eventOperationV2Dto 事件操作DTO
     * @param matchEventInfoDTO 事件信息DTO
     * @param matchCommonLogDto 日志DTO
     * @return 插入的事件ID
     */
    public Long commonProcessAndReturnEventId(MatchScoreAndTimeVo matchScoreAndTimeVo, EventOperationV2Dto eventOperationV2Dto, MatchEventInfoDTO matchEventInfoDTO, MatchCommonLogDto matchCommonLogDto){
        // 设置 secondsFromStart，用于 sendPDEventInfo 保存到数据库
        if (eventOperationV2Dto.getSecondFromStart() != null) {
            matchEventInfoDTO.setSecondsFromStart(eventOperationV2Dto.getSecondFromStart());
        }
        // 设置 extraInfo 到 addition7（斯诺克特殊处理，与 updateEventInfo 保持一致）
        if (matchEventInfoDTO.getExtraInfo() != null) {
            matchEventInfoDTO.setAddition7(matchEventInfoDTO.getExtraInfo());
            // 同时设置 extrainfo（已废弃字段），确保 sendPDEventInfo 能正确获取
            matchEventInfoDTO.setExtrainfo(matchEventInfoDTO.getExtraInfo());
        }
        // 对于中断/重开事件，设置 homeAway="all"（在 sendPDEventInfo 之前设置）
        // 注意：排球 ct=2/3 的 eventCode 已被 customizeStatusEvent 改写为 "timeout"/"timeout_over"，
        // 需与斯诺克的 "suspension"/"suspension_over" 一并覆盖。
        if (MatchInfoConvertEnum.MATCH_INTERRUPTED.getValue().equals(matchEventInfoDTO.getEventCode()) ||
                MatchInfoConvertEnum.MATCH_RESTART.getValue().equals(matchEventInfoDTO.getEventCode()) ||
                MatchInfoConvertEnum.MATCH_EVENT_INTERRUPTED.getValue().equals(matchEventInfoDTO.getEventCode()) ||
                MatchInfoConvertEnum.MATCH_EVENT_RESTART.getValue().equals(matchEventInfoDTO.getEventCode()) ||
                "timeout".equals(matchEventInfoDTO.getEventCode()) ||
                "timeout_over".equals(matchEventInfoDTO.getEventCode())) {
            matchEventInfoDTO.setHomeAway("all");
        }
        if (isProfessionalSavePath(matchEventInfoDTO.getSportId())) {
            eventProducer.sendPDSnookerEventInfo(matchEventInfoDTO);
        } else {
            eventProducer.sendPDEventInfo(matchEventInfoDTO);
        }
        Long eventId = matchEventInfoDTO.getPossibleEventId();
        redisUtils.pushFootBallScore(eventOperationV2Dto.getThirdMatchId());
        redisUtils.pushFootBallEvent(eventOperationV2Dto.getThirdMatchId());
        matchScorePdLogService.setMatchCommonLog(matchScoreAndTimeVo, matchCommonLogDto);
        return eventId;
    }

    public void kickOffEventCheck(Long thirdMatchId, String language) throws Exception {
        CommonItem item = getMatchCacheStatus(thirdMatchId, SnookerConstant.KICKOFF_FIRST_CLICK);
        if (item == null || (item.getAway() == null && item.getHome() == null)) {
            throw new Exception("请选择开球方");
        }
    }

    public void updateEventInfo(MatchEventInfoDTO eventInfoDTO, Long secondFromStart) {
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setAddition7(eventInfoDTO.getExtraInfo());
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        matchScoresEventInfo.setId(IdWorker.getId());
        matchScoresEventInfo.setSecondsFromStart(secondFromStart);
        if (MatchInfoConvertEnum.MATCH_INTERRUPTED.getValue().equals(eventInfoDTO.getEventCode()) ||
                MatchInfoConvertEnum.MATCH_RESTART.getValue().equals(eventInfoDTO.getEventCode())) {
            matchScoresEventInfo.setHomeAway("all");
            matchScoresEventInfo.setSecondsFromStart(eventInfoDTO.getSecondsFromStart());
        }
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        // 将事件ID设置回eventInfoDTO，方便后续使用
//        eventInfoDTO.setEventId(matchScoresEventInfo.getId());
    }
    
    /**
     * 更新事件信息并返回事件ID
     * @param eventInfoDTO 事件信息
     * @param secondFromStart 距离开始秒数
     * @return 插入的事件ID
     */
    public Long updateEventInfoAndReturnId(MatchEventInfoDTO eventInfoDTO, Long secondFromStart) {
        MatchScoresEventInfo matchScoresEventInfo = new MatchScoresEventInfo();
        BeanUtils.copyProperties(eventInfoDTO, matchScoresEventInfo);
        matchScoresEventInfo.setAddition7(eventInfoDTO.getExtraInfo());
        matchScoresEventInfo.setCreateTime(System.currentTimeMillis());
        matchScoresEventInfo.setExtraInfo(eventInfoDTO.getExtrainfo());
        matchScoresEventInfo.setLinkId(eventInfoDTO.getCopyLinkId());
        Long eventId = IdWorker.getId();
        matchScoresEventInfo.setId(eventId);
        matchScoresEventInfo.setSecondsFromStart(secondFromStart);
        if (MatchInfoConvertEnum.MATCH_INTERRUPTED.getValue().equals(eventInfoDTO.getEventCode()) ||
                MatchInfoConvertEnum.MATCH_RESTART.getValue().equals(eventInfoDTO.getEventCode())) {
            matchScoresEventInfo.setHomeAway("all");
            matchScoresEventInfo.setSecondsFromStart(eventInfoDTO.getSecondsFromStart());
        }
        matchScoresEventInfoMapper.insert(matchScoresEventInfo);
        // 将事件ID设置回eventInfoDTO，方便后续使用
//        eventInfoDTO.setEventId(eventId);
        return eventId;
    }

    public CommonItem getMatchCacheStatus(Long thirdMatchId, String field) {
        String key = SnookerConstant.RONGHE_PD_MATCH_STATUS + thirdMatchId;
        Object obj = redisService.hGet(key, field);
        if (obj == null) {
            return null;
        }
        
        // 如果已经是 CommonItem 对象，直接返回
        if (obj instanceof CommonItem) {
            return (CommonItem) obj;
        }
        
        // 尝试解析 JSON 字符串
        try {
            String objStr = obj.toString();
            // 检查是否是有效的 JSON 格式（以{开头）
            if (objStr.trim().startsWith("{")) {
                return JSON.parseObject(objStr, CommonItem.class);
            } else {
                // 如果不是 JSON 格式，可能是对象 toString() 的结果，返回 null
                log.warn("[MatchScoreCommonHelper]getMatchCacheStatus invalid CommonItem format for key:{} field:{} match:{} value:{}",
                        key, field, thirdMatchId, objStr);
                return null;
            }
        } catch (Exception e) {
            // JSON 解析失败，记录日志并返回 null
            log.warn("[MatchScoreCommonHelper]getMatchCacheStatus parse error for key:{} field:{} match:{} error:{} value:{}",
                    key, field, thirdMatchId, e.getMessage(), obj);
            return null;
        }
    }

    public Map<String, Object> getMatchCacheStatus(Long thirdMatchId) {
        String key = SnookerConstant.RONGHE_PD_MATCH_STATUS + thirdMatchId;
        Map<String, Object> obj = redisService.hGetAll(key);
        
        // 确保返回的 Map 中的值可以被正确序列化为 JSON
        // 如果值是 CommonItem 对象，需要转换为可序列化的格式
        Map<String, Object> result = new HashMap<>();
        if (obj != null && !obj.isEmpty()) {
            for (Map.Entry<String, Object> entry : obj.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof CommonItem) {
                    // 如果是 CommonItem 对象，转换为 Map 格式以便 JSON 序列化
                    CommonItem commonItem = (CommonItem) value;
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("home", commonItem.getHome());
                    itemMap.put("away", commonItem.getAway());
                    result.put(entry.getKey(), itemMap);
                } else if (value != null && value.toString().startsWith("CommonItem")) {
                    // 如果是 CommonItem 的 toString() 结果，初始化为空值
                    log.warn("[MatchScoreCommonHelper]getMatchCacheStatus invalid CommonItem format for key:{} match:{} value:{}", 
                            entry.getKey(), thirdMatchId, value);
                    result.put(entry.getKey(), "");
                } else {
                    // 其他类型直接使用（包括controlType等Integer/String类型）
                    result.put(entry.getKey(), value);
                }
            }
        }
        log.info("result 获取controlType：{},{}",thirdMatchId,result);
        return result;
    }

    public void setMatchCacheStatus(Long thirdMatchId, String field, Integer home, Integer away) {
        String key = SnookerConstant.RONGHE_PD_MATCH_STATUS + thirdMatchId;
        CommonItem commonItem = new CommonItem(home, away, false);
        redisService.hSet(key, field, commonItem, RedisConfig.REDIS_WEEK_TIME);
    }
    public void setMatchCacheStatus(Long thirdMatchId, String field, Object value) {
        String key = SnookerConstant.RONGHE_PD_MATCH_STATUS + thirdMatchId;
        redisService.hSet(key, field, value, RedisConfig.REDIS_WEEK_TIME);
    }

    /**
     * 获取 Redis 中的任意类型值（不仅限于 CommonItem）
     * @param thirdMatchId 比赛ID
     * @param field 字段名
     * @return 字段值，可能为 Boolean、String、Integer 等类型
     */
    public Object getMatchCacheStatusObject(Long thirdMatchId, String field) {
        String key = SnookerConstant.RONGHE_PD_MATCH_STATUS + thirdMatchId;
        return redisService.hGet(key, field);
    }

    /**
     * 检查比赛是否处于中断状态
     * @param thirdMatchId 比赛ID
     * @return true 如果比赛处于中断状态，false 否则
     */
    public boolean isMatchInterrupted(Long thirdMatchId) {
        Object interruptedStatus = getMatchCacheStatusObject(thirdMatchId, SnookerConstant.MATCH_INTERRUPTED);
        if (interruptedStatus != null) {
            if (interruptedStatus instanceof Boolean) {
                return (Boolean) interruptedStatus;
            } else if (interruptedStatus instanceof String) {
                return "true".equalsIgnoreCase(interruptedStatus.toString());
            } else if (interruptedStatus.toString().equals("true")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查赛事是否处于中断状态
     * @param thirdMatchId 比赛ID
     * @return true 如果赛事处于中断状态，false 否则
     */
    public boolean isMatchEventInterrupted(Long thirdMatchId) {
        Object eventInterruptedStatus = getMatchCacheStatusObject(thirdMatchId, SnookerConstant.MATCH_EVENT_INTERRUPTED);
        if (eventInterruptedStatus != null) {
            if (eventInterruptedStatus instanceof Boolean) {
                return (Boolean) eventInterruptedStatus;
            } else if (eventInterruptedStatus instanceof String) {
                return "true".equalsIgnoreCase(eventInterruptedStatus.toString());
            } else if (eventInterruptedStatus.toString().equals("true")) {
                return true;
            }
        }
        return false;
    }

}
