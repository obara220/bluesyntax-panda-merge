package com.panda.merge.v2.check.impl;

import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.constant.SettleMentionEnum;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.MatchSettleCenterProducer;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportTournamentService;
import com.panda.merge.service.settleMention.service.SettleMentionFactory;
import com.panda.merge.v2.repository.MatchDelaySettleInfoV2Repository;
import com.panda.merge.v2.repository.MatchSettleCheckInfoRepository;
import com.panda.merge.v2.repository.MatchSettleDataSourceConfigRepository;
import com.panda.merge.v2.repository.MatchSettleDataSourceSwitchRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MatchSettleBatchCheckServiceHelper {

    @Autowired
    private IWsPushService wsPushService;
    @Autowired
    private MatchSettleCheckInfoRepository matchSettleCheckInfoRepository;
    @Autowired
    private MatchDelaySettleInfoV2Repository matchDelaySettleInfoRepository;
    @Autowired
    private SettleMentionFactory settleMentionFactory;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private MatchSettleCenterProducer matchSettleCenterProducer;
    @Autowired
    private MatchSettleDataSourceConfigRepository matchSettleDataSourceConfigRepository;
    @Autowired
    private MatchSettleDataSourceSwitchRepository matchSettleDataSourceSwitchRepository;
    @Autowired
    private StandardSportTournamentService standardSportTournamentService;

    @Value("${spring.profiles.active}")
    private String env;

    public Map<Long, List<MatchSettleCheckInfo>> batchMoveDelayCheckInfo(Long standardMatchId, Map<Long, List<MatchSettleCheckInfo>> checkInfoListByCheckInfoMap, String linkedId){
        log.info("linkedId::{} batchMoveDelayCheckInfo start", linkedId);
        if(CollectionUtils.isEmpty(checkInfoListByCheckInfoMap)){
            return checkInfoListByCheckInfoMap;
        }
        List<MatchDelaySettleInfo> delaySettleInfos = matchDelaySettleInfoRepository.getModelByStandardMatchId(standardMatchId);
        if (CollectionUtils.isEmpty(delaySettleInfos)){
            return checkInfoListByCheckInfoMap;
        }
        Long nowTime = System.currentTimeMillis();
        Map<Long, MatchDelaySettleInfo> delaySettleInfosMap = delaySettleInfos.stream().collect(Collectors.toMap(MatchDelaySettleInfo::getCheckInfoId, Function.identity(), (v1, v2)->v1));

        return checkInfoListByCheckInfoMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, t->t.getValue().stream().filter(inner->{
            MatchDelaySettleInfo info =  delaySettleInfosMap.get(inner.getId());
            if (info != null) {
                if (info.getDelayTime() > nowTime) {
                    log.info("linkedId::{} batchMoveDelayCheckInfo,未到达延迟结算时间,不参与结算核对,standardMatchId: {},id: {},delayTime: {},nowTime: {}",linkedId, standardMatchId,info.getCheckInfoId(),info.getDelayTime(),nowTime);
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList())));
    }

    public Integer getTournamentLevelStatus(Long standardMatchId, String dataSourceCode,String eventCode) {

        Integer status = null;
        try {
            //1、查询标准赛事对应的联赛Id,并根据联赛Id查询出联赛的等级
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
            if (standardMatchInfo != null && standardMatchInfo.getStandardTournamentId() != null) {
                //3139需求 足球开关由结算设置控制
                if (standardMatchInfo.getSportId().equals(1L)){
                    List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getMatchSettleDataSourceSwitchByRedis(standardMatchInfo.getSportId(),dataSourceCode);
                    if (!switches.isEmpty()){
                        MatchSettleDataSourceSwitch sourceSwitch = switches.get(0);
                        if(eventCode.equals("corner")) {
                            status = sourceSwitch.getCorner();
                        }else if(eventCode.equals("goal")||eventCode.equals("kick_off")){
                            status = sourceSwitch.getGoal();
                        }else {
                            status = sourceSwitch.getBooking();
                        }
                    }
                    if (dataSourceCode.equals("PA")){
                        status =1;
                    }
                }else if (standardMatchInfo.getSportId().equals(2L)){
                    List<MatchSettleDataSourceSwitch> switches = matchSettleDataSourceSwitchRepository.getMatchSettleDataSourceSwitchByRedis(standardMatchInfo.getSportId(),dataSourceCode);
                    if (!switches.isEmpty()){
                        MatchSettleDataSourceSwitch sourceSwitch = switches.get(0);
                        if(eventCode.equals("score_change")) {
                            status = sourceSwitch.getGoal();
                        }
                    }
                    if (dataSourceCode.equals("PA")){
                        status =1;
                    }
                } else {
                    StandardSportTournament standardSportTournament = standardSportTournamentService.getItem(standardMatchInfo.getStandardTournamentId());
                    //2、查询联赛对应开启结算的数据商
                    if (!Objects.isNull(standardSportTournament) && standardSportTournament.getTournamentLevel() != null) {
                        List<MatchSettleDataSourceConfig> matchSettleDataSourceConfigList = matchSettleDataSourceConfigRepository.getMatchSettleDataSourceConfig(standardSportTournament.getTournamentLevel(),standardMatchInfo.getSportId(),dataSourceCode);
                        //3、查询数据商状态并返回
                        if (!matchSettleDataSourceConfigList.isEmpty()) {
                            MatchSettleDataSourceConfig matchSettleDataSourceConfig = matchSettleDataSourceConfigList.get(0);
                            status = matchSettleDataSourceConfig.getStatus();
                        }
                    }



                }
            }
        } catch (Exception e) {
            log.error("::::根据标准赛事Id:{},结算查询联赛对应的数据源:{},状态异常信息:{}", standardMatchId,dataSourceCode, e);
        }
        return status;
    }

    public void deleteAuditorCheckInfo(Long settleScoreEventId) {
        MatchSettleCheckInfoExample example = new MatchSettleCheckInfoExample();
        example.createCriteria().andSettleScoreEventIdEqualTo(settleScoreEventId);
        List<MatchSettleCheckInfo> list =  matchSettleCheckInfoRepository.getModelBySettleScoreEventId(settleScoreEventId);
        if (!CollectionUtils.isEmpty(list) && list.size() == 1 && "PA".equals(list.get(0).getDataSourceCode()) && list.get(0).getCheckStatus()==0) {
            matchSettleCheckInfoRepository.removeById(list.get(0).getId());
        }
    }

    //人员结算失败发送芒果预警
    public void sendMango(Long sportId, Long standardMatchId, List<String> userNameList) {
        for (String userName : userNameList) {
            //查询标准赛事表
            StandardMatchInfoExample standardMatchInfoExample = new StandardMatchInfoExample();
            standardMatchInfoExample.createCriteria().andIdEqualTo(standardMatchId);
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);

            if (standardMatchInfo != null) {
                String match = standardMatchInfo.getHomeAwayInfo();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());
                String sport = "FootBall";//默认足球
                if(sportId.equals(2L)){
                    sport = "BasketBall";
                }
                String data = "[Env]:" + env + "\n" +
                        "[Time]:" + time + "\n" +
                        "[Sport]:" + sport + "\n" +
                        "[Match ID]:" + standardMatchInfo.getMatchManageId() + "\n" +
                        "[Match]:" + match + "\n" +
                        "[PIC]:" + userName;
                String linkId = IdWorker.getId() + "_PERSON_ERROR_SETTLE_MANGO_EARLY_WARNING";
                matchSettleCenterProducer.personErrorSettleManGoEarlyWarning(linkId, data, "人员错误结算芒果预警");
            } else {
                log.info("人员错误结算芒果预警未找到相关赛事：" + standardMatchId);
            }
        }
    }

    @Async("settleMentionFactoryThreadPool")
    public void validateDeleteEvent(MatchSettleEvent matchSettleEvent, List<String> deleteSettleNums, MatchEventInfo data){
        log.info("linkId::{}::eventId:{} addSettleMention with settleEventId:{} start!",data.getLinkId(), data.getThirdEventId(), matchSettleEvent.getId());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("matchSettleEvent", matchSettleEvent);
        parameters.put("deleteSettleNums", deleteSettleNums);
        parameters.put("matchEventInfo", data);
        settleMentionFactory.getProcessor(SettleMentionEnum.FOOTBALL_DELETE_EVENT).addSettleMention(parameters);
        log.info("linkId::{}::eventId:{} addSettleMention with settleEventId:{} end!",data.getLinkId(), data.getThirdEventId(), matchSettleEvent.getId());
    }

    @Async("settleMentionFactoryThreadPool")
    public void validateDataScoreMismatch(List<Pair<MatchSettleCheckInfo, MatchSettleThirdScore>> insertCheckInfos, String linkedId) {
        log.info("linkId::{} validateDataScoreMismatch start", linkedId);
        for (Pair<MatchSettleCheckInfo,MatchSettleThirdScore> item : insertCheckInfos) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("matchSettleCheckInfo", item.getLeft());
            parameters.put("settleNum", item.getRight().getSettleNum());
            parameters.put("sportId", item.getRight().getSportId());
            SettleMentionEnum settleMentionEnum = SettleMentionEnum.FOOTBALL_PHASE_SCORE_MISMATCH;
            if (item.getRight().getSportId() == 2) {
                settleMentionEnum = SettleMentionEnum.BASKETBALL_PHASE_SCORE_MISMATCH;
            }
            settleMentionFactory.getProcessor(settleMentionEnum).addSettleMention(parameters);
        }
        log.info("linkId::{} validateDataScoreMismatch end", linkedId);
    }

    @Async("PushStandardSettleScoresThreadPool")
    public void pushStandardSettleScores(Long standardMatchId,String eventCode){
        wsPushService.pushStandardSettleScores(standardMatchId,eventCode);
    }

    @Async("PushStandardSettleEventThreadPool")
    public void pushStandardSettleEvent(Long standardMatchId,String eventCode){
        wsPushService.pushStandardSettleEvent(standardMatchId,eventCode);
    }

}
