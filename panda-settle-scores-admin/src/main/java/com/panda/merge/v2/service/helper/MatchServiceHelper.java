package com.panda.merge.v2.service.helper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.*;
import com.panda.merge.dto.LimitSwitchDto;
import com.panda.merge.dto.settle.MentionQueryRequest;
import com.panda.merge.dto.settle.SettleEventDeleteRequest;
import com.panda.merge.model.*;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.settleMention.dto.AbstractMentionStatus;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import com.panda.merge.service.settleMention.service.SettleMentionFactory;
import com.panda.merge.v2.entity.MatchSettleEventEntity;
import com.panda.merge.v2.entity.MatchSettleRollBackInfoEntity;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import com.panda.merge.v2.repository.MatchSettleEventV2Repository;
import com.panda.merge.v2.repository.MatchSettleRollBackInfoRepository;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.service.IMatchSettleOperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Slf4j
public class MatchServiceHelper {

    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;
    @Autowired
    RedisService redisService;
    @Autowired
    IMatchSettleOperateLogService matchSettleOperateLogService;
    @Autowired
    private MatchSettleEventV2Repository matchSettleEventRepository;
    @Autowired
    SettleMentionFactory settleMentionFactory;
    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;
    @Autowired
    MatchSettleCheckInfoHelper matchSettleCheckInfoHelper;
    private static final List<String> allMins15Codes = Arrays.asList(FootballPeriodValidateEnum.GOAL_2.getCode().toString(),
            FootballPeriodValidateEnum.GOAL_3.getCode().toString(),FootballPeriodValidateEnum.GOAL_4.getCode().toString(),FootballPeriodValidateEnum.GOAL_6.getCode().toString(),
            FootballPeriodValidateEnum.GOAL_7.getCode().toString(),FootballPeriodValidateEnum.GOAL_8.getCode().toString());
    public boolean checkIfOverSettleTime(Long standardMatchId) {
        StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(standardMatchId);
        Long startTime =standardMatchInfo.getBeginTime();
        Date date =new Date(startTime);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR,37);
        date=calendar.getTime();
        Date now = new Date();
        return  now.after(date);
    }
    public void settleRollBackSetNullOrderCount(Long id){
        MatchSettleRollBackInfoEntity info = matchSettleRollBackInfoRepository.getMatchSettleRollBackInfo(id);
        if(info != null){
            info.setRollBackOrderCount(0l);
            info.setOrderCount(0L);
            info.setModifyTime(System.currentTimeMillis());
            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(info,false);
        }
    }
    public void insertRollbackData(Long standardMatchId,Long scoreEventId,Integer type,String eventCode,String settleNum){
        MatchSettleRollBackInfoEntity oldInfo = matchSettleRollBackInfoRepository.getMatchSettleRollBackInfo(scoreEventId);
        Integer isPenalty =0;
        if(settleNum.equals("1030")||settleNum.equals("1029")||settleNum.equals("1028")){
            isPenalty=1;
        }
        //多次回滚，存在更新，不存在新增
        if(oldInfo != null){
            oldInfo.setRollBackStatus(1);
            oldInfo.setRollBackOrderCount(0l);
            oldInfo.setOrderCount(0l);
            oldInfo.setRollBackTime(System.currentTimeMillis());
            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(oldInfo,false);
        } else {
            MatchSettleRollBackInfoEntity info = new MatchSettleRollBackInfoEntity();
            info.setId(scoreEventId);
            info.setSettleScoreEventId(scoreEventId);
            info.setDataType(type);
            info.setRollBackStatus(1);
            info.setRollBackTime(System.currentTimeMillis());
            info.setStandardMatchId(standardMatchId);
            info.setCreateTime(System.currentTimeMillis());
            info.setModifyTime(System.currentTimeMillis());
            info.setEventCode(eventCode);
            info.setIsDianQiu(isPenalty);
            info.setOrderCount(0L);
            info.setRollBackOrderCount(0L);
            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(info,true);
        }
    }
    public List<LimitSwitchDto> getBasketInSettleTimeLimit(Long sportId ) {
        List<LimitSwitchDto> list = new ArrayList<>();
        try {
            Object o     =redisService.get("REDIS_KEY_BASKET_IN_TIME_LIMIT_"+sportId);
            if (o != null) {
                try {
                    JSONArray array = JSONArray.parseArray(o.toString());

                    for (Object object :array){
                        LimitSwitchDto dto =   JSONObject.toJavaObject((JSONObject)object,LimitSwitchDto.class);
                        list.add(dto);
                    }
                } catch (Exception e) {
                    log.error("getBasketInSettleTimeLimit_Error");
                }
            }else {
                //假如初始化给前台展示
                for (int i = 0;i<=20;i++){
                    LimitSwitchDto dto = new LimitSwitchDto();
                    dto.setLevel(i);
                    dto.setLimitSecond(30);
                    dto.setOnOff(true);
                    list.add(dto);
                }
            }
        }catch (Exception e){
            log.error("getBasketInSettleTimeLimit_getRedisData_Error");
        }
        return list;
    }
    public MatchSettleEvent getExtryEvent(MatchSettleEvent matchSettleEvent) {
        if(matchSettleEvent.getThirdEventSourceId()==null){
            return null;
        }
        List<MatchSettleEventEntity> extryEvents =matchSettleEventRepository.getByStandardMatchIdAndThirdEventSourceIdAndEventTypeAndNotId(matchSettleEvent.getStandardMatchId(),matchSettleEvent.getThirdEventSourceId(),2,matchSettleEvent.getId());
        if(extryEvents.size()!=0){
            MatchSettleEvent event = new MatchSettleEvent();
            BeanUtils.copyProperties(extryEvents.get(0),event);
            return event;
        }
        return null;
    }

    public Map<String, AbstractMentionStatus> getAllMentionStatus(MentionQueryRequest mentionQueryRequest) {
        String key = CommonConstant.SETTLE_MENTION_KEY + mentionQueryRequest.getMatchId();
        return redisService.hGetAll(key);
    }
    public FootballMentionStatus getFootballMentionStatus(MentionQueryRequest mentionQueryRequest) {
        SettleMentionEnum mentionType = SettleMentionEnum.getEnumBySportIdAndCode(mentionQueryRequest.getSportId(), mentionQueryRequest.getMentionType());
        if (mentionType == null) {
            throw new RuntimeException("Mention type is not correct!");
        }
        FootballMentionStatus mentionDto = (FootballMentionStatus) settleMentionFactory.getProcessor(mentionType).querySettleMention(mentionQueryRequest.getMatchId());
        if (mentionQueryRequest.getMentionDetail() == 0 && mentionDto != null) {
            mentionDto.setDetailNull();
        }
        return mentionDto;
    }
    public void cancelSettleEventMention(SettleEventDeleteRequest settleEventDeleteRequest) {
        log.info("[MatchSettleServiceImpl] cancelSettleEventMention start with parameter: {}", settleEventDeleteRequest);
        if (settleEventDeleteRequest.getMatchId() == null || settleEventDeleteRequest.getMatchScoreId() == null) {
            throw new RuntimeException("Mention id/matchScoreId can not be null!");
        }
        SettleMentionEnum mentionType = SettleMentionEnum.getEnumByMentionCode(settleEventDeleteRequest.getMentionType());
        if (mentionType == null) {
            throw new RuntimeException("Mention type is not correct!");
        }
        List<Long> allMatchScoreIds = obtainAllDeleteEvent(settleEventDeleteRequest.getMatchScoreId());
        for(Long scoreId : allMatchScoreIds) {
            log.info("[MatchSettleServiceImpl] cancelSettleEventMention process scoreId: {}", scoreId);
            settleEventDeleteRequest.setMatchScoreId(scoreId);
            List<String> keys = Arrays.asList(String.valueOf(settleEventDeleteRequest.getMatchScoreId()));

            SettleEventCodeEnum eventCode = SettleEventCodeEnum.getEventCodeEnum(settleEventDeleteRequest.getEventCode());
            settleMentionFactory.getProcessor(mentionType).deleteSettleMention(settleEventDeleteRequest.getMatchId(), keys, eventCode);

            // 对于删除事件单独处理
            if (mentionType == SettleMentionEnum.FOOTBALL_DELETE_EVENT) {
                processDeleteEvent(settleEventDeleteRequest);
            }
            matchSettleOperateLogService.settleMentionLog(null, settleEventDeleteRequest);
        }
        log.info("[MatchSettleServiceImpl] cancelSettleEventMention end!");
    }
    private List<Long> obtainAllDeleteEvent(Long matchScoreId){
        List<Long> result = new ArrayList<>();
        result.add(matchScoreId);
        MatchSettleScore settleScore = matchSettleScoreRepository.getById(matchScoreId);
        if(settleScore != null && allMins15Codes.contains(settleScore.getSettleNum())) {
            List<String> childSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(String.valueOf(settleScore.getSettleNum()));
            List<MatchSettleScore> matchSettleScores =matchSettleScoreRepository.getModelBySettleNumAndMatchIdIdAndStatus(childSettleNumList, settleScore.getStandardMatchId(),null);
            if(!CollectionUtils.isEmpty(matchSettleScores)) {
                List<Long> scoreIds = matchSettleScores.stream().filter(t->{
                    if (t.getHasDeleteEvent() == null) {
                        return false;
                    }
                    return t.getHasDeleteEvent()==1;
                }).map(MatchSettleScore::getId).collect(Collectors.toList());
                result.addAll(scoreIds);
            }
        }
        return result;
    }
    private void processDeleteEvent(SettleEventDeleteRequest settleEventDeleteRequest) {
        MatchSettleScore settleScore = matchSettleScoreRepository.getById(settleEventDeleteRequest.getMatchScoreId());
        if(settleScore!=null){
            settleScore.setHasDeleteEvent(0);
            settleScore.setCurrentEventStatus(settleScore.getIsGrey());
            matchSettleScoreRepository.updateById(settleScore);
        }else {
            MatchSettleEvent settleEvent = matchSettleEventRepository.getById(settleEventDeleteRequest.getMatchScoreId());
            if(settleEvent!=null){
                settleEvent.setHasDeleteEvent(0);
                settleEvent.setCurrentEventStatus(settleEvent.getIsGrey());
                matchSettleEventRepository.updateById(settleEvent);
            }
        }
        matchSettleCheckInfoHelper.updateMatchCurrentEventStatus(settleEventDeleteRequest.getMatchId());
    }
}
