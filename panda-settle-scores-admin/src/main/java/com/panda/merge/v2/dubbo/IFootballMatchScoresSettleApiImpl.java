package com.panda.merge.v2.dubbo;

import com.panda.merge.api.IFootballMatchScoresSettleApi;
import com.panda.merge.config.SpareRocketmqConsumerConfig;
import com.panda.merge.v2.check.IMatchSettleBatchCheckService;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.*;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.settle.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.mq.producer.MatchSettleScoresProducer;
import com.panda.merge.v2.repository.MatchSettleCheckInfoRepository;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import com.panda.merge.service.IMatchSettleLogService;
import com.panda.merge.service.IMatchSettleService;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.syncScore.SyncScoreFactory;
import com.panda.merge.v2.controllerv2.MatchSettleScoreFootBallController;
import com.panda.merge.v2.repository.MatchSettleEventV2Repository;
import com.panda.merge.v2.repository.MatchSettleScoreV2Repository;
import com.panda.merge.v2.service.assemble.MatchSettleEventAssemble;
import com.panda.merge.v2.service.assemble.MatchSettleScoreAssemble;
import com.panda.merge.v2.service.assemble.MatchSettleThirdEventAssemble;
import com.panda.merge.v2.service.assemble.MatchSettleThirdScoreAssemble;
import com.panda.merge.v2.service.helper.MatchSettleTemplateHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ONE;


/**
 * 结算2.0 dubbo服务 KB
 * */
@Service
@DubboService
@Slf4j
public class IFootballMatchScoresSettleApiImpl implements IFootballMatchScoresSettleApi {
    @Autowired
    MatchSettleScoresProducer matchSettleScoresProducer;
    @Autowired
    IMatchSettleService matchSettleService;
    @Autowired
    IMatchSettleLogService iMatchSettleLogService;
    @Autowired
    RedisService redisService;
    @Autowired
    IWsPushService wsPushService;
    @Autowired
    MatchSettleCenterProducer matchSettleCenterProducer;
    @Autowired
    IMatchSettleBatchCheckService matchSettleBatchCheckService;
    @Autowired
    SyncScoreFactory syncScoreFactory;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private MatchSettleScoreAssemble matchSettleScoreAssemble;
    @Autowired
    private MatchSettleEventAssemble matchSettleEventAssemble;
    @Autowired
    private MatchSettleThirdScoreAssemble matchSettleThirdScoreAssemble;
    @Autowired
    private MatchSettleThirdEventAssemble matchSettleThirdEventAssemble;
    @Autowired
    private MatchSettleScoreFootBallController matchSettleScoreFootBallController;
    @Autowired
    private MatchSettleTemplateHelper matchSettleTemplateHelper;
    @Autowired
    private SpareRocketmqConsumerConfig spareRocketmqConsumerConfig;
    @Autowired
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;
    @Autowired
    private MatchSettleScoreV2Repository matchSettleScoreRepository;

    @Override
    public List<MatchSettleScoreDto> searchMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleScoreAssemble.searchFootballMatchSettleScores(settleScoreSearchDto);
    }
    /**
     * 查询三方结算阶段比分
     * */
    @Override
    public ThirdMatchSettleScoresDto searchThirdMatchSettleScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleThirdScoreAssemble.searchFootballThirdMatchSettleScores(settleScoreSearchDto);
    }

    @Override
    public List<MatchSettleEventDto> searchMatchSettleEvent(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleEventAssemble.searchFootballMatchSettleEvent(settleScoreSearchDto);
    }
    /**
     * 查询三方结算事件
     * */
    @Override
    public ThirdMatchSettleEventDto searchThirdMatchSettleEvent(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleThirdEventAssemble.searchFootballThirdMatchSettleEvent(settleScoreSearchDto);
    }

    @Override
    public Response updateMatchSettleScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
    return matchSettleScoreFootBallController.updateMatchSettleScore(matchSettleScoreDto);
    }

    @Override
    public Response confirmMatchSettleScore(ConfirmMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.confirmMatchSettleScore(matchSettleScoreDto);
    }


    @Override
    public Response settleMatchScore(SettleMatchScoreDto matchSettleScoreDto) {
       return matchSettleScoreFootBallController.settleMatchScore(matchSettleScoreDto);
    }

    @Override
    public Response addMatchSettleEvent(AddMatchSettleEventDto addMatchSettleEventDto) {
      return matchSettleScoreFootBallController.addMatchSettleEvent(addMatchSettleEventDto);
    }



    /**
     * 事件比分编辑
     * */
    @Override
    public Response editMatchSettleEvent(EditMatchSettleEventDto editMatchSettleEventDto) {
        return matchSettleScoreFootBallController.editMatchSettleEvent(editMatchSettleEventDto);
    }



    //进球方式和球员(setGoalMethodAndPlayer)
    @Override
    public Response editMatchSettleEventMethodAndPlayer(EditMatchSettleEventDto editMatchSettleEventDto) {
        return  matchSettleScoreFootBallController.editMatchSettleEventMethodAndPlayer(editMatchSettleEventDto);
    }

    //事件确认
    @Override
    public Response confirmMatchSettleEvent(EditMatchSettleEventDto matchSettleEventDto) {
        return matchSettleScoreFootBallController.confirmMatchSettleEvent(matchSettleEventDto);
    }

    @Override
    public Response settleMatchSettleEvent(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.settleMatchSettleEvent(matchSettleScoreDto);
    }

    @Override
    public Response<PenaltyScoresVo> searchPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleScoreFootBallController.searchPenaltyScores(settleScoreSearchDto);
    }



    @Override
    public Response addPenaltyScores(MatchSettleScoreSearchDto settleScoreSearchDto) {
        return matchSettleScoreFootBallController.addPenaltyScores(settleScoreSearchDto);
    }


    @Override
    public Response setPenaltyScores(EditMatchSettleEventDto settleScoreSearchDto) {
        return matchSettleScoreFootBallController.setPenaltyScores(settleScoreSearchDto);
    }

    /**
     * 重新结算
     * */
    @Override
    public Response reSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.reSettleMatchEvent(matchSettleScoreDto);
    }

    @Override
    public Response reSettleMatchScore(UpdateMatchSettleScoreDto matchSettleScoreDto) {
       return matchSettleScoreFootBallController.reSettleMatchScore(matchSettleScoreDto);
    }

    /**
     * 回滚结算
     * */
    @Override
    public Response rollBackSettleMatchScores(UpdateMatchSettleScoreDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.rollBackSettleMatchScores(matchSettleScoreDto);
    }

    @Override
    public Response rollBackSettleMatchEvent(EditMatchSettleEventDto matchSettleScoreDto) {
        return matchSettleScoreFootBallController.rollBackSettleMatchEvent(matchSettleScoreDto);
    }
    @Override
    public Response matchPeriodQuery(MatchPeriodQueryDto matchPeriodQueryDto) {
        return matchSettleScoreFootBallController.matchPeriodQuery(matchPeriodQueryDto);
    }

    @Override
    public Response checkScoresOrEvent(MatchCheckSettleScoreEventDto dto) {
       return matchSettleScoreFootBallController.checkScoresOrEvent(dto);
    }





    @Override
    public Response querySettleType(Long StandardMatchId) {
        return matchSettleScoreFootBallController.querySettleType(StandardMatchId);
    }

    //玩法级重跑和冻结
    @Override
    public Response playCategoryFreezeAndReSettle(SettleQueryDTO settleQueryDTO) {
        return matchSettleScoreFootBallController.playCategoryFreezeAndReSettle(settleQueryDTO);
    }

    @Override
    public void updateGoalAndCornerEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway, String eventCode) {
        matchSettleScoreFootBallController.updateGoalAndCornerEventByInfo(matchSettleEvent, homeAway, eventCode);
    }

    @Override
    public void updateFaCardEventByInfo(MatchSettleEvent matchSettleEvent, String homeAway) {
        matchSettleScoreFootBallController.updateFaCardEventByInfo(matchSettleEvent,homeAway);
    }

    @Override
    public boolean countPenaltyScores(EditMatchSettleEventDto matchSettleEvent, MatchSettleEvent settleEvent) {
        return matchSettleScoreFootBallController.countPenaltyScores(matchSettleEvent,settleEvent);
    }

    @Override
    public boolean isTeamFirstSettled(Long standardMatchId) {
        return matchSettleScoreFootBallController.isTeamFirstSettled(standardMatchId);
    }

    //设置五分钟阶段玩法数据
//    private List<MatchSettleScoreDto> setFiveMinList(Long matchId,List<MatchSettleScoreDto> matchSettleScoreDtos){
//        MatchSettleInfo settleInfo = matchSettleInfoRepository.getMatchSettleInfo(matchId);
//        List<MatchSettleScoreDto> scoreList = new ArrayList<>();
//        if (settleInfo!=null){
//
//            if(settleInfo.getFiveMinSwitch() == 1){
//                List<MatchSettleScoreDto> oneList = new ArrayList<>();
//                List<MatchSettleScoreDto> twoList = new ArrayList<>();
//                List<MatchSettleScoreDto> threeList = new ArrayList<>();
//                List<MatchSettleScoreDto> fourList = new ArrayList<>();
//                List<MatchSettleScoreDto> fiveList = new ArrayList<>();
//                List<MatchSettleScoreDto> sixList = new ArrayList<>();
//                for (int i=0;i<matchSettleScoreDtos.size();i++) {
//                    MatchSettleScoreDto matchSettleScoreDto = matchSettleScoreDtos.get(i);
//                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
//                    if(settleNum == 1034 || settleNum == 1035 || settleNum == 1036){
//                        oneList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1037 || settleNum == 1038 || settleNum == 1039){
//                        twoList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1040 || settleNum == 1041 || settleNum == 1042 || settleNum == 1043){
//                        threeList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1044 || settleNum == 1045 || settleNum == 1046){
//                        fourList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1047 || settleNum == 1048 || settleNum == 1049){
//                        fiveList.add(matchSettleScoreDto);
//                    }else if(settleNum == 1050 || settleNum == 1051 || settleNum == 1052 || settleNum == 1053){
//                        sixList.add(matchSettleScoreDto);
//                    }
//                }
//                for (MatchSettleScoreDto matchSettleScoreDto : matchSettleScoreDtos) {
//                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
//                    if(settleNum == 102){
//                        matchSettleScoreDto.setFiveMinList(oneList);
//                    }
//                    if(settleNum == 103){
//                        matchSettleScoreDto.setFiveMinList(twoList);
//                    }
//                    if(settleNum == 104){
//                        matchSettleScoreDto.setFiveMinList(threeList);
//                    }
//                    if(settleNum == 106){
//                        matchSettleScoreDto.setFiveMinList(fourList);
//                    }
//                    if(settleNum == 107){
//                        matchSettleScoreDto.setFiveMinList(fiveList);
//                    }
//                    if(settleNum == 108){
//                        matchSettleScoreDto.setFiveMinList(sixList);
//                    }
//                }
//
//                for (MatchSettleScoreDto score:matchSettleScoreDtos) {
//                    MatchSettleScoreDto matchSettleScoreDto = score;
//                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
//                    if(settleNum == 1034 || settleNum == 1035 || settleNum == 1036
//                            || settleNum == 1037 || settleNum == 1038 || settleNum == 1039
//                            || settleNum == 1040 || settleNum == 1041 || settleNum == 1042 || settleNum == 1043
//                            ||settleNum == 1044 || settleNum == 1045 || settleNum == 1046
//                            || settleNum == 1047 || settleNum == 1048 || settleNum == 1049
//                            || settleNum == 1050 || settleNum == 1051 || settleNum == 1052 || settleNum == 1053){
//
//                    } else {
//                        scoreList.add(score);
//                    }
//                }
//            } else {
//                for (MatchSettleScoreDto score:matchSettleScoreDtos) {
//                    MatchSettleScoreDto matchSettleScoreDto = score;
//                    Integer settleNum =  Integer.parseInt(matchSettleScoreDto.getSettleNum());
//                    if(settleNum == 1034 || settleNum == 1035 || settleNum == 1036
//                            || settleNum == 1037 || settleNum == 1038 || settleNum == 1039
//                            || settleNum == 1040 || settleNum == 1041 || settleNum == 1042 || settleNum == 1043
//                            ||settleNum == 1044 || settleNum == 1045 || settleNum == 1046
//                            || settleNum == 1047 || settleNum == 1048 || settleNum == 1049
//                            || settleNum == 1050 || settleNum == 1051 || settleNum == 1052 || settleNum == 1053){
//
//                    } else {
//                        scoreList.add(score);
//                    }
//                }
//            }
//        }
//
//        return scoreList;
//    }

//    private void obtainDetailInfo(MatchSettleScoreSearchDto settleScoreSearchDto, Map<String, Integer> deleteStatusMap, Map<String, Integer> dataMismatchMap){
//        SettleEventCodeEnum settleEventCodeEnum = SettleEventCodeEnum.getEventCodeEnum(settleScoreSearchDto.getEventCode());
//        if (settleEventCodeEnum == null) {
//            return;
//        }
//        MentionQueryRequest queryRequest = new MentionQueryRequest();
//        queryRequest.setMatchId(settleScoreSearchDto.getStandardMatchId());
//        Map<String, AbstractMentionStatus> mentionStatusMap = matchSettleService.getAllMentionStatus(queryRequest);
//        log.info("syncTest mentionStatusMap: {}", mentionStatusMap);
//        if (MapUtils.isEmpty(mentionStatusMap)) {
//            return;
//        }
//
//        if (mentionStatusMap.containsKey(SettleMentionEnum.FOOTBALL_DELETE_EVENT.getValue())) {
//            AbstractMentionStatus.EventStatus eventStatus = mentionStatusMap.get(SettleMentionEnum.FOOTBALL_DELETE_EVENT.getValue())
//                    .getDetailStatusFieldByEventCode(settleEventCodeEnum);
//            if(eventStatus != null && !MapUtils.isEmpty(eventStatus.getDetailStatus())) {
//                deleteStatusMap.putAll(eventStatus.getDetailStatus());
//            }
//        }
//        if (mentionStatusMap.containsKey(SettleMentionEnum.FOOTBALL_SCORE_MISMATCH.getValue())) {
//            FootballMentionStatus.EventStatus eventStatus = mentionStatusMap.get(SettleMentionEnum.FOOTBALL_SCORE_MISMATCH.getValue())
//                    .getDetailStatusFieldByEventCode(settleEventCodeEnum);
//            if(eventStatus != null && !MapUtils.isEmpty(eventStatus.getDetailStatus())) {
//                dataMismatchMap.putAll(eventStatus.getDetailStatus());
//            }
//        }
//    }

    @Override
    public List<MatchDelaySettleInfo> queryMatchDelaySettleInfoById(Long standardId) {
        return matchSettleScoreFootBallController.queryMatchDelaySettleInfoById(standardId);
    }




//    private void setDelayEventSecond(Long standardMatchIfo,List<MatchSettleEventDto> matchSettleEventDtos){
//        MatchDelaySettleInfoExample example = new MatchDelaySettleInfoExample();
//        example.createCriteria().andStandardMatchIdEqualTo(standardMatchIfo);
//        List<MatchDelaySettleInfo> matchDelaySettleInfos = matchDelaySettleInfoMapper.selectByExample(example);
//        if (CollectionUtils.isNotEmpty(matchDelaySettleInfos)){
//            matchDelaySettleInfos.forEach(d->{
//                matchSettleEventDtos.forEach(s->{
//                    if (d.getScoreId().toString().equals(s.getId())){
//                        String key = "delaySettle:"+s.getId();
//                        Object second = redisService.get(key);
//                        if (null!=second){
//                            s.setDelayTimeSecond(Long.valueOf(second.toString()));
//                        }
//
//                    }
//                });
//            });
//        }
//    }
    @Override
    public void delayCheckCommonMatchSettleScoreEvent(Object matchSettleScoreEvent, MatchSettleCheckInfo matchSettleCheckInfo, boolean createCheck) {
        String linkedId = "Delay-settle-" + matchSettleCheckInfo.getSettleScoreEventId() + "-" + matchSettleCheckInfo.getThirdSettleScoreEventId() + "-" + System.currentTimeMillis();
        log.info("linkedId::{} job-delayCheckCommonMatchSettleScoreEvent 延迟结算job开始触发", linkedId);
        
        // 判断是否为MatchSettleScore类型（阶段类型），如果是则需要进行5/15分钟过滤
        boolean isScoreFlag = !(matchSettleScoreEvent instanceof MatchSettleEvent);
        if (isScoreFlag) {
            MatchSettleScore original = (MatchSettleScore) matchSettleScoreEvent;
            Long standardMatchId = matchSettleCheckInfo.getStandardMatchId();
            String dataSourceCode = matchSettleCheckInfo.getDataSourceCode();
            String eventCode = original.getEventCode();
            
            // 延迟结算时，需要查询该赛事同一事件类型的所有5/15分钟阶段（包括之前的阶段）
            // 这样才能检查之前的阶段是否有数据不匹配或删除事件
            List<MatchSettleScore> allStages = matchSettleScoreRepository.getByStandardMatchIdAndEventCode(standardMatchId, eventCode);
            
            // 过滤出所有5/15分钟阶段（包括已结算和未结算的，因为需要检查之前阶段的状态）
            List<MatchSettleScore> all5Or15MinStages = allStages.stream()
                    .filter(score -> is5Or15MinPeriod(score.getSettleNum()))
                    .collect(Collectors.toList());
            
            // 如果当前阶段不在5/15分钟阶段列表中，说明不是5/15分钟阶段，不需要过滤
            boolean isCurrentStage5Or15Min = all5Or15MinStages.stream()
                    .anyMatch(score -> score.getId().equals(original.getId()));
            
            if (isCurrentStage5Or15Min) {
                // 对5/15分钟阶段进行过滤，过滤掉有数据不匹配或删除事件的阶段
                // 传入所有5/15分钟阶段，这样能检查之前的阶段是否有问题
                List<MatchSettleScore> filteredScores = matchSettleBatchCheckService.filterUnsettled5Or15MinPeriods(
                        standardMatchId, dataSourceCode, all5Or15MinStages, linkedId);
                
                // 检查当前阶段是否在过滤后的列表中
                boolean currentStagePassed = filteredScores.stream()
                        .anyMatch(score -> score.getId().equals(original.getId()));
                
                if (!currentStagePassed) {
                    log.info("linkId::{} 当前5/15分钟阶段{}因之前阶段有数据不匹配或删除事件被过滤，延迟结算job结束", 
                            linkedId, original.getSettleNum());
                    return;
                }
                
                // 如果当前阶段通过了过滤，使用原始数据继续处理
            }
        }
        
        MatchSettleTemplate countDownTemplate = matchSettleTemplateHelper.getTemplateByStandardMatchId(matchSettleCheckInfo.getStandardMatchId(), SettleTemplateTypeEnum.COUNT_DOWEN.code);
        matchSettleCheckInfo.setCheckStatus(MatchSettleCheckConstant.CheckStatus.CONFIRM);
        matchSettleCheckInfoRepository.updateById(matchSettleCheckInfo);
        matchSettleBatchCheckService.batchCheckCommonMatchSettleScoreEvent(Arrays.asList(Pair.of(matchSettleScoreEvent, matchSettleCheckInfo)),createCheck,linkedId, countDownTemplate);
    }

    /**
     * 判断是否为5/15分钟阶段
     */
    private boolean is5Or15MinPeriod(String settleNum) {
        if (settleNum == null) {
            return false;
        }
        // 5分钟阶段
        List<String> ALL_5_SETTLE_NUMS = Arrays.asList("1034", "1035", "1036", "1037", "1038", "1039", "1040",
                "1041", "1042", "1043", "1044", "1045", "1046", "1047", "1048", "1049", "1050", "1051", "1052", "1053");
        if (ALL_5_SETTLE_NUMS.contains(settleNum)) {
            return true;
        }
        // 15分钟阶段
        List<String> ALL_15_SETTLE_NUMS = Arrays.asList("102", "103", "104", "106", "107", "108", "2011",
                "2012", "2013", "2014", "2015", "2016", "301", "302", "303", "305", "306", "307");
        if (ALL_15_SETTLE_NUMS.contains(settleNum)) {
            return true;
        }
        return false;
    }

    @Override
    public void slaveRocketMqStopResume(Integer isStop) {
        DefaultMQPushConsumer consumer = spareRocketmqConsumerConfig.getConsumer();
        if (consumer != null) {
            if(ONE.equals(isStop)){
                consumer.suspend();
                log.info("【slaveRocketMqStopResume 手动触发MQ暂停消费（部分topic）:{}】 已暂停消费!", System.currentTimeMillis());
            }else{
                consumer.resume();
                log.info("【slaveRocketMqStopResume 手动触发MQ暂停消费（部分topic）:{}】 已恢复消费!", System.currentTimeMillis());
            }
        }else{
            log.info("【slaveRocketMqStopResume 手动触发MQ暂停消费（部分topic）:{}】 consumer为空,无法处理!", System.currentTimeMillis());
        }
    }
}
