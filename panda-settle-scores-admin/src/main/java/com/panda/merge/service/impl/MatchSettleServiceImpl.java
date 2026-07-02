package com.panda.merge.service.impl;

import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.constant.FootballPeriodValidateEnum;
import com.panda.merge.constant.SettleEventCodeEnum;
import com.panda.merge.constant.SettleMentionEnum;
import com.panda.merge.constant.converter.SettleMentionConverter;
import com.panda.merge.dto.settle.*;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.respository.*;
import com.panda.merge.service.*;
import com.panda.merge.service.settleMention.dto.AbstractMentionStatus;
import com.panda.merge.service.settleMention.dto.FootballMentionStatus;
import com.panda.merge.service.settleMention.service.SettleMentionFactory;
import com.panda.merge.utils.BasketBallSettleScoreUtils;
import com.panda.merge.utils.FootBallMatchSettleScoreUtils;
import com.panda.merge.v2.repository.MatchSettleEventV2Repository;
import com.panda.merge.v2.repository.MatchSettleRollBackInfoRepository;
import com.panda.merge.v2.service.helper.MatchSettleInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 结算服务方法
 * */
@Slf4j
@Service
public class MatchSettleServiceImpl  implements IMatchSettleService {

    @Autowired
    MatchSettleScoreMapper matchSettleScoreMapper;
    @Autowired
    MatchSettleEventMapper matchSettleEventMapper;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    IFootballPenaltySettleService footballPenaltySettleService;
//    @Autowired
//    MatchEventInfoMapper matchEventInfoMapper;
    @Autowired
    MatchSettleCenterProducer matchSettleCenterProducer;
    @Autowired
    MatchSettleRollBackInfoMapper matchSettleRollBackInfoMapper;

    @Autowired
    SettleMentionFactory settleMentionFactory;

    @Autowired
    RedisService redisService;

    @Autowired
    SettleMentionConverter settleMentionConverter;

    @Autowired
    IMatchSettleLogService iMatchSettleLogService;
    @Autowired
    MatchSettleInfoHelper matchSettleInfoHelper;

    @Autowired
    IMatchSettleRollBackInfoService matchSettleRollBackInfoService;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;
    @Autowired
    MatchEventInfoRepository matchEventInfoRepository;
    @Autowired
    MatchSettleScoreRepository matchSettleScoreRepository;
    @Autowired
    IMatchSettleEventService matchSettleEventServiceImpl;
    @Autowired
    IMatchSettleScoreService matchSettleScoreServiceImpl;
    @Autowired
    MatchSettleEventV2Repository matchSettleEventV2Repository;
    private static final List<String> allMins15Codes = Arrays.asList(FootballPeriodValidateEnum.GOAL_2.getCode().toString(),
            FootballPeriodValidateEnum.GOAL_3.getCode().toString(),FootballPeriodValidateEnum.GOAL_4.getCode().toString(),FootballPeriodValidateEnum.GOAL_6.getCode().toString(),
            FootballPeriodValidateEnum.GOAL_7.getCode().toString(),FootballPeriodValidateEnum.GOAL_8.getCode().toString());

    @Value("${spring.profiles.active}")
    private String env;
    /**
     * 赛事切换结算2.0后自动生成 阶段比分数据
     * */
    @Override
    public void initMatchSettleScore(Long standardMatchId) {
        MatchSettleScoreExample example =new MatchSettleScoreExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        List<MatchSettleScore> list =matchSettleScoreMapper.selectByExample(example);
        //如果已经生成直接返回
        if(list.size()!=0){
            return;
        }
        List<MatchSettleScore> matchSettleScores= FootBallMatchSettleScoreUtils.createInitMatchSettleScores(standardMatchId);
//        for (MatchSettleScore matchSettleScore : matchSettleScores) {
////            matchSettleScore.setId(getIdById(matchSettleScore.getId(),matchSettleScore.getStandardMatchId(),matchSettleScore.getSettleNum()));
//            matchSettleScoreMapper.insert(matchSettleScore);
//
//        }
        matchSettleScoreMapper.batchInsert(matchSettleScores);
        //自动新增点球大战
        try {
        footballPenaltySettleService.autoAddPenaltySettleEvent(standardMatchId);
        } catch (Exception e) {
            log.error("结算切换初始化出错::::",e);
        }
        //自动新增事件比分
        // 先查询已存在的事件，避免重复初始化（传入null表示查询所有状态的事件）
        List<MatchSettleEvent> existingEvents = matchSettleEventV2Repository.getModelByStandardMatchIdAndNotStatus(standardMatchId, null);
        
        // 创建初始化事件列表，过滤掉已存在的事件
        List<MatchSettleEvent> matchSettleEvents = FootBallMatchSettleScoreUtils.createInitMatchSettleEvents(standardMatchId, existingEvents);

//        for (MatchSettleEvent matchSettleScore : matchSettleEvents) {
////            matchSettleScore.setId(getIdById(matchSettleScore.getId(),matchSettleScore.getStandardMatchId(),matchSettleScore.getSettleNum()));
//            matchSettleEventMapper.insert(matchSettleScore);
//        }
        // 只有当存在需要初始化的事件时才进行保存
        if (!CollectionUtils.isEmpty(matchSettleEvents)) {
            matchSettleEventServiceImpl.saveOrUpdateBatch(matchSettleEvents);
        }
    }

    private Long getIdById(Long id, Long standardMatchId, String settleNum) {
        String idS =id.toString();
        idS= idS.substring(idS.length()-5,idS.length());
        idS=standardMatchId+settleNum+idS;
        return Long.parseLong(idS);
    }

    @Override
    public MatchSettleEvent getExtryEvent(MatchSettleEvent matchSettleEvent) {
        if(matchSettleEvent.getThirdEventSourceId()==null){
            return null;
        }
        MatchSettleEventExample eventExample =new MatchSettleEventExample();
        eventExample.createCriteria().andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId())
                .andThirdEventSourceIdEqualTo(matchSettleEvent.getThirdEventSourceId())
                .andIdNotEqualTo(matchSettleEvent.getId()).andEventTypeEqualTo(2);
        List<MatchSettleEvent> extryEvents =matchSettleEventMapper.selectByExample(eventExample);
        if(extryEvents.size()!=0){
            return extryEvents.get(0);
        }
        return null;
    }

    @Override
    public MatchSettleEvent getExtryEvent(MatchSettleEventDto matchSettleScoreDto) {
        MatchSettleEventExample eventExample =new MatchSettleEventExample();
        eventExample.createCriteria().andStandardMatchIdEqualTo(matchSettleScoreDto.getStandardMatchId())
                .andThirdEventSourceIdEqualTo(matchSettleScoreDto.getThirdEventSourceId())
                .andIdNotEqualTo(Long.parseLong(matchSettleScoreDto.getId())).andEventTypeEqualTo(2);
        List<MatchSettleEvent> extryEvents =matchSettleEventMapper.selectByExample(eventExample);
        if(extryEvents.size()!=0){
            return extryEvents.get(0);
        }
        return null;
    }

    @Override
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

    @Override
    public boolean checkIfEventBeforeAllEdit(MatchSettleEvent matchSettleEvent) {
        List<String> eventCodes=new ArrayList<>();
        //进球方式或者球员不受影响
        if(matchSettleEvent.getEventType()==null||matchSettleEvent.getEventType()==2){
            return true ;
        }
        //事件类型分类计算
        if(matchSettleEvent.getEventCode().equals("goal")){
            eventCodes.add("goal");
        }else if(matchSettleEvent.getEventCode().equals("corner")){
            eventCodes.add("corner");
        }else if(matchSettleEvent.getEventCode().equals("yellow_card")||matchSettleEvent.getEventCode().equals("red_card")||matchSettleEvent.getEventCode().equals("fa_card")){
            eventCodes.add("yellow_card");eventCodes.add("red_card");eventCodes.add("fa_card");
        }else {
            return true;
        }
        //阶段类型分类计算
        List<Long> periods=new ArrayList<>();
        if(matchSettleEvent.getPeriodId().equals(6l)){
            periods.add(6l);
        }else if(matchSettleEvent.getPeriodId().equals(7l)){
            periods.add(6l);   periods.add(7l);
        }else if(matchSettleEvent.getPeriodId().equals(41l)){
            periods.add(41l);
        }else if(matchSettleEvent.getPeriodId().equals(42l)){
            periods.add(41l);  periods.add(42l);
        }else if(matchSettleEvent.getPeriodId().equals(50l)){
            periods.add(50l);
        }
        MatchSettleEventExample eventInfoExample = new MatchSettleEventExample();
        eventInfoExample.createCriteria().andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId()).andEventCodeIn(eventCodes).andPeriodIdIn(periods)
                .andIdNotEqualTo(matchSettleEvent.getId()).andStatusEqualTo(0).andEventTypeEqualTo(1);
        List<MatchSettleEvent> eventInfos = matchSettleEventMapper.selectByExample(eventInfoExample);
        if(eventInfos==null){
            //查询不到该类型事件中未被编辑的事件
            return true;
        }
        for (MatchSettleEvent eventInfo : eventInfos) {
            //1.先判断阶段是否相同，如果相同就要比较 事件次序，次序小的必须被编辑
            if(matchSettleEvent.getPeriodId().equals(eventInfo.getPeriodId())){
                if(matchSettleEvent.getEventOrder()>eventInfo.getEventOrder()){
                    if(eventInfo.getStatus()==0){
                        return false;
                    }
                }
            }else {
                //不相同的阶段，肯定是之前的事件，直接判断是否已经被编辑
                if(eventInfo.getStatus()==0){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean checkIfEventAfterSettled(MatchSettleEvent matchSettleEvent) {
        List<String> eventCodes=new ArrayList<>();
        //进球方式或者球员不受影响
        if(matchSettleEvent.getEventType()==null||matchSettleEvent.getEventType()==2){
            return true ;
        }
        //事件类型分类计算
        if(matchSettleEvent.getEventCode().equals("goal")){
            eventCodes.add("goal");
        }else if(matchSettleEvent.getEventCode().equals("corner")){
            eventCodes.add("corner");
        }else if(matchSettleEvent.getEventCode().equals("yellow_card")||matchSettleEvent.getEventCode().equals("red_card")||matchSettleEvent.getEventCode().equals("fa_card")){
            eventCodes.add("yellow_card");eventCodes.add("red_card");eventCodes.add("fa_card");
        }else {
            return true;
        }
        //阶段类型分类计算
        List<Long> periods=new ArrayList<>();
        if(matchSettleEvent.getPeriodId().equals(6l)){
            periods.add(6l);   periods.add(7l);
        }else if(matchSettleEvent.getPeriodId().equals(7l)){
            periods.add(7l);
        }else if(matchSettleEvent.getPeriodId().equals(41l)){
            periods.add(41l);  periods.add(42l);
        }else if(matchSettleEvent.getPeriodId().equals(42l)){
            periods.add(42l);
        }else if(matchSettleEvent.getPeriodId().equals(50l)){
            periods.add(50l);
        }
        MatchSettleEventExample eventInfoExample = new MatchSettleEventExample();
        eventInfoExample.createCriteria().andStandardMatchIdEqualTo(matchSettleEvent.getStandardMatchId()).andEventCodeIn(eventCodes).andPeriodIdIn(periods)
                .andIdNotEqualTo(matchSettleEvent.getId()).andStatusEqualTo(3).andEventTypeEqualTo(1);
        List<MatchSettleEvent> eventInfos = matchSettleEventMapper.selectByExample(eventInfoExample);
        if(eventInfos==null){
            //查询不到该类型事件中未被编辑的事件
            return true;
        }
        for (MatchSettleEvent eventInfo : eventInfos) {
            //1.先判断阶段是否相同，如果相同就要比较 事件次序，次序大的事件不能已结算
            if(matchSettleEvent.getPeriodId().equals(eventInfo.getPeriodId())){
                if(matchSettleEvent.getEventOrder()<eventInfo.getEventOrder()){
                    if(eventInfo.getStatus()==3){
                        return false;
                    }
                }
            }else {
                //不相同的阶段，肯定是之后的事件
                if(eventInfo.getStatus()==3){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void manGoEarlyWarning(MatchEventInfo matchEventInfo){
        long start = System.currentTimeMillis();
        List<MatchEventInfo> oldMatchInfos =matchEventInfoRepository.getMatchEventInfoCaseOne(matchEventInfo.getThirdMatchId(),matchEventInfo.getExtraInfo(),matchEventInfo.getDataSourceCode(),matchEventInfo.getSportId());
        if(oldMatchInfos != null && oldMatchInfos.size() > 0){
            MatchEventInfo oldMatchInfo = oldMatchInfos.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            String sport = "FootBall";//默认足球
            if(matchEventInfo.getSportId().equals(2L)){
                sport = "BasketBall";
            }
            /*for(StandardSportTypeEnum em:StandardSportTypeEnum.values()){
                if(oldMatchInfo.getSportId().equals(em.code)){
                    sport = em.toString();
                }
            }*/
            long standMatchId = oldMatchInfo.getStandardMatchId();//标准赛事id
            long seconds = oldMatchInfo.getSecondsFromStart();//开始多少秒
            int t1 = oldMatchInfo.getT1();//主队进球
            int t2 = oldMatchInfo.getT2();//客队进球
            String eventCode = oldMatchInfo.getEventCode();//事件
            String homeAway = oldMatchInfo.getHomeAway();
            //查询标准赛事表
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standMatchId);
            if(standardMatchInfo != null ){
                String match = standardMatchInfo.getHomeAwayInfo();
                String matchs[] = match.split("vs.");
                if("home".equals(homeAway)){
                    homeAway = matchs[0].trim();
                } else {
                    homeAway = matchs[1].trim();
                }
                String event = (seconds/60)+"'"+(seconds%60)+"''-"+(t1+t2)+"nd "+eventCode+"-("+homeAway+" "+t1+":"+t2+")";
                String data = "[Env]:" + env + "\n" +
                        "[Time]:" + time + "\n" +
                        "[Sport]:" +sport+"\n"+
                        "[Match ID]:" + standardMatchInfo.getMatchManageId() + "\n" +
                        "[Match]:" + match + "\n"+
                        "[Event]:" + event;
                String linkId = IdWorker.getId() + "_MATCH_DELETE_MANGO_EARLY_WARNING";
                matchSettleCenterProducer.manGoEarlyWarning(linkId,data,"删除事件芒果预警");
            } else {
                log.info("linkId::{}::eventId:{} 删除事件芒果预警未找到相关赛事：{}", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId(), standMatchId);
            }
        }
        log.info("linkId::{}::eventId:{} manGoEarlyWarning 耗时{}ms处理事件完成", matchEventInfo.getLinkId(), matchEventInfo.getThirdEventId(), System.currentTimeMillis()-start);
    }

    //结算时把回滚订单数清零
    @Override
    public void settleRollBackSetNullOrderCount(Long id){
        MatchSettleRollBackInfo info = matchSettleRollBackInfoRepository.getModelMatchSettleRollBackInfo(id);
        if(info != null){
            info.setRollBackOrderCount(0l);
            info.setOrderCount(0L);
            info.setModifyTime(System.currentTimeMillis());
            matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(info,false);
        }
    }

    @Override
    public void batchSettleRollBackSetNullOrderCount(List<Long> ids){
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            settleRollBackSetNullOrderCount(id);
        }
//        List<MatchSettleRollBackInfo> infos = matchSettleRollBackInfoService.getByIds(ids);
//        infos.forEach(info->{
//            info.setRollBackOrderCount(0l);
//            info.setOrderCount(0L);
//            info.setModifyTime(System.currentTimeMillis());
//        });
//        matchSettleRollBackInfoService.saveOrUpdateBatch(infos);
    }

    @Override
    public void updateGoWaterPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto) {
        //查询
        MatchSettleEventExample eventExample =new MatchSettleEventExample();
        eventExample.createCriteria().andStandardMatchIdEqualTo(settleScoreSearchDto.getStandardMatchId()).andSettleNumEqualTo("1054");
        List<MatchSettleEvent> list =matchSettleEventMapper.selectByExample(eventExample);
        if(list.size()==0){
            return;
        }
        MatchSettleEvent matchSettleEvent= list.get(0);
        //结算后不做编辑
        if(matchSettleEvent.getStatus()!=null&&matchSettleEvent.getStatus()==3){
            return;
        }
//        if(matchSettleEvent.getSettleTimes()>0||matchSettleEvent.getSettleCount()>0){
            matchSettleEvent.setStatus(1);
//        }
        matchSettleEvent.setT1(settleScoreSearchDto.getT1());
        matchSettleEvent.setT2(settleScoreSearchDto.getT2());
        matchSettleEvent.setModifyTime(System.currentTimeMillis());
        matchSettleEvent.setOperater(settleScoreSearchDto.getOperatorName());
        matchSettleEventMapper.updateByPrimaryKey(matchSettleEvent);
    }

    @Override
    public void initBasketballSettleScore(Long standardMatchId) {
        MatchSettleScoreExample example =new MatchSettleScoreExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        List<MatchSettleScore> list =matchSettleScoreMapper.selectByExample(example);
        //如果已经生成直接返回
        if(list.size()!=0){
            return;
        }
        //需要新增即时比分
        List<MatchSettleScore> matchSettleScores= BasketBallSettleScoreUtils.createInitMatchSettleScores(standardMatchId);
//        for (MatchSettleScore matchSettleScore : matchSettleScores) {
////            matchSettleScore.setId(getIdById(matchSettleScore.getId(),matchSettleScore.getStandardMatchId(),matchSettleScore.getSettleNum()));
//            matchSettleScoreMapper.insert(matchSettleScore);
//        }
        matchSettleScoreRepository.saveOrUpdateBatch(matchSettleScores);

    }

    @Override
    public FootballMentionStatus getFootballMentionStatus(MentionQueryRequest mentionQueryRequest) {
        SettleMentionEnum mentionType = SettleMentionEnum.getEnumBySportIdAndCode(mentionQueryRequest.getSportId(), mentionQueryRequest.getMentionType());
        if (mentionType == null) {
            throw new RuntimeException("Mention type is not correct!");
        }
        try {
            FootballMentionStatus mentionDto = (FootballMentionStatus) settleMentionFactory.getProcessor(mentionType).querySettleMention(mentionQueryRequest.getMatchId());
            if (mentionQueryRequest.getMentionDetail() == 0 && mentionDto != null) {
                mentionDto.setDetailNull();
            }
            return mentionDto;
        } catch (Exception e) {
            log.error("getFootballMentionStatus error:", e);
            return null;
        }
    }

    @Override
    public Map<String, AbstractMentionStatus> getAllMentionStatus(MentionQueryRequest mentionQueryRequest) {
        String key = CommonConstant.SETTLE_MENTION_KEY + mentionQueryRequest.getMatchId();
        return redisService.hGetAll(key);
    }

    @Override
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
            iMatchSettleLogService.settleMentionLog(null, settleEventDeleteRequest);
        }
        log.info("[MatchSettleServiceImpl] cancelSettleEventMention end!");
    }

    // TODO 未来会删除
    private void processDeleteEvent(SettleEventDeleteRequest settleEventDeleteRequest) {
        MatchSettleScore settleScore = matchSettleScoreMapper.selectByPrimaryKey(settleEventDeleteRequest.getMatchScoreId());
        if(settleScore!=null){
            settleScore.setHasDeleteEvent(0);
            settleScore.setCurrentEventStatus(settleScore.getIsGrey());
            matchSettleScoreMapper.updateByPrimaryKey(settleScore);
        }else {
            MatchSettleEvent settleEvent = matchSettleEventMapper.selectByPrimaryKey(settleEventDeleteRequest.getMatchScoreId());
            if(settleEvent!=null){
                settleEvent.setHasDeleteEvent(0);
                settleEvent.setCurrentEventStatus(settleEvent.getIsGrey());
                matchSettleEventMapper.updateByPrimaryKey(settleEvent);
            }
        }
        matchSettleInfoHelper.updateMatchCurrentEventStatus(settleEventDeleteRequest.getMatchId());
    }

    // 获取所有应该删除的事件(当为15分钟进球删除事件时，也需要找到其所有子节点(5分钟)进行删除)
    private List<Long> obtainAllDeleteEvent(Long matchScoreId){
        List<Long> result = new ArrayList<>();
        result.add(matchScoreId);
        MatchSettleScore settleScore = matchSettleScoreMapper.selectByPrimaryKey(matchScoreId);
        if(settleScore != null && allMins15Codes.contains(settleScore.getSettleNum())) {
            List<String> childSettleNumList = FootballPeriodValidateEnum.getChildSettleNumList(String.valueOf(settleScore.getSettleNum()));
            MatchSettleScoreExample example =new MatchSettleScoreExample();
            example.createCriteria().andStandardMatchIdEqualTo(settleScore.getStandardMatchId()).andSettleNumIn(childSettleNumList);
            List<MatchSettleScore> matchSettleScores =matchSettleScoreMapper.selectByExample(example);
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
}
