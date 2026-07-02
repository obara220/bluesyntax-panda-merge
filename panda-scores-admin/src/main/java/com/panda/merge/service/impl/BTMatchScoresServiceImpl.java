package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.*;

import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.mq.producer.ScoreEventProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.service.BTMatchScoresService;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * BT事件比分服务
 */
@Service
@Slf4j
public class BTMatchScoresServiceImpl implements BTMatchScoresService {
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    IScoresService scoresService;
    @Autowired
    ScoreEventProducer eventProducer;
    @Autowired
    RedisService redisService;

    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;

    private static final String SCORES_CANCELD_MATCH="SCORES_CANCELD_MATCH:";
    private static final String SCORES_CANCELD_MATCH_CORNER="SCORES_CANCELD_MATCH_CORNER:";
    /**
     * 更新比分
     * matchScoresInfo 三方赛事比分记录
     * request 接入下发统计信息
     * */
    @Override
    public boolean updateScores(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {

        if(request.getData()==null||request.getData().getPeriod()==null){
            log.error("::{}::阶段不存在",request.getLinkId());
            return false;
        }
        try {
            if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
                //创建比分有初始值拦截
                if(checkBTHasScore(request)){
                    return false;
                }
                createScores(matchScoresInfo,request);
                return true;
            }
            //标反
            //1.根据当前开售事件源信息进行覆盖，如果有，则取事件源的阶段
            Long periodId =scoresService.getSoldEventScoresPeriod(matchScoresInfo.getThirdMatchId());
            if(periodId!=null){
                request.getData().setPeriod(periodId.intValue());
            }
            //足球处理
            if (SportTypeEnum.FOOTBALL.getValue().toString().equals(request.getData().getSportId())) {
                //如果上半场不存在，而且阶段不是上半场，则全部过滤
                if(checkHTNotExit(matchScoresInfo,request)){
                    return false;
                }
                //BT赛事比分倒退拦截
                if(checkBTScoreCanceld(matchScoresInfo,request)){
                    log.error("{}赛事{}的比分有倒退:link {}",request.getDataSourceCode(),matchScoresInfo.getThirdMatchId(),request.getLinkId());
                    return false;
                }
                //模拟BT事件下发  将模拟的事件写入缓存 下游 结算的时候会获取这个模拟的BT事件从而判断是否灰色区间
                checkAndInitGoalEvent(matchScoresInfo,request);
                //5分钟比分更新 灰色区间判断
                checkAndInitCornerEvent(matchScoresInfo,request);
                checkAndInitRedCardEvent(matchScoresInfo,request);
                checkAndInitYellowCardEvent(matchScoresInfo,request);
                update5FootballScores(matchScoresInfo,request);
                update15FootballScores(matchScoresInfo,request);
                //15分钟灰色区间判断 比分更新
                updateFootballScores(matchScoresInfo,request);
//                checkAndInitKickOffEvent(matchScoresInfo,request);
                return true;
            }
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
        return  true;
    }

    /**
     * 检查BT是否有比分
     * @param request
     * @return
     */
    private boolean checkBTHasScore(Request<MatchStatisticsInfoDTO> request) {
        for (MatchStatisticsInfoDetailDTO detailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
            if (detailDTO.getCode().equals("corner_score") ) {
                int  t1 = detailDTO.getT1();
                int   t2 = detailDTO.getT2();
               if(t1!=0||t2!=0){
                   return true;
               }
            }
            if (detailDTO.getCode().equals("match_score") ) {
                int  t1 = detailDTO.getT1();
                int   t2 = detailDTO.getT2();
                if(t1!=0||t2!=0){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检查/初始化黄牌事件
     * @param matchScoresInfo
     * @param request
     */
    private void checkAndInitYellowCardEvent(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
        try {
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
            if(wholeScore==null){
                return;
            }
            Integer t1 = 0;
            Integer t2 = 0;
            for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
                if (matchStatisticsInfoDetailDTO.getCode().equals("yellow_card_score") ) {
                    t1 = matchStatisticsInfoDetailDTO.getT1();
                    t2 = matchStatisticsInfoDetailDTO.getT2();
                    break;
                }
            }
            Integer aT1 = t1 - wholeScore.getYellowCard().getHome();
            Integer aT2 = t2 - wholeScore.getYellowCard().getAway();
            boolean needGoal = aT1 > 0 || aT2 > 0;
            String homeAway = aT1 > aT2 ? "home" : "away";
            if (needGoal) {
                MatchEventInfoDTO matchEventInfo = new MatchEventInfoDTO();

                matchEventInfo.setEventCode("yellow_card");
                matchEventInfo.setSecondsFromStart(Long.parseLong(request.getData().getSecondsMatchStart().toString()));
                matchEventInfo.setHomeAway(homeAway);
                matchEventInfo.setT1(t1);
                matchEventInfo.setT2(t2);
                matchEventInfo.setSourceType("1");
                matchEventInfo.setSportId(1L);
                matchEventInfo.setEventTime(System.currentTimeMillis());
                matchEventInfo.setDataSourceCode(request.getData().getDataSourceCode());
                matchEventInfo.setMatchPeriodId(Long.parseLong(request.getData().getPeriod().toString()));
                matchEventInfo.setThirdEventId(request.getLinkId());
                matchEventInfo.setCopyLinkId(matchScoresInfo.getThirdMatchId()+"yellow_card");
                matchEventInfo.setCanceled(0);
                matchEventInfo.setThirdMatchSourceId(matchScoresInfo.getThirdMatchSourceId());
                matchEventInfo.setThirdMatchSourceId(matchScoresInfo.getThirdMatchSourceId());
                eventProducer.sendBTEvent(matchEventInfo);
            }
        }catch (Exception   e){

            log.error("BT UOF LINK:{}:error::{}",request.getLinkId(),e);
        }
    }

    /**
     * 检查/初始化红牌事件
     * @param matchScoresInfo
     * @param request
     */
    private void checkAndInitRedCardEvent(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
        try {
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
            if(wholeScore==null){
                return;
            }
            Integer t1 = 0;
            Integer t2 = 0;
            for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
                if (matchStatisticsInfoDetailDTO.getCode().equals("red_card_score") ) {
                    t1 = matchStatisticsInfoDetailDTO.getT1();
                    t2 = matchStatisticsInfoDetailDTO.getT2();
                    break;
                }
            }
            Integer aT1 = t1 - wholeScore.getRedCard().getHome();
            Integer aT2 = t2 - wholeScore.getRedCard().getAway();
            boolean needGoal = aT1 > 0 || aT2 > 0;
            String homeAway = aT1 > aT2 ? "home" : "away";
            if (needGoal) {
                MatchEventInfoDTO matchEventInfo = new MatchEventInfoDTO();

                matchEventInfo.setEventCode("red_card");
                matchEventInfo.setSecondsFromStart(Long.parseLong(request.getData().getSecondsMatchStart().toString()));
                matchEventInfo.setHomeAway(homeAway);
                matchEventInfo.setT1(t1);
                matchEventInfo.setT2(t2);
                matchEventInfo.setSourceType("1");
                matchEventInfo.setSportId(1L);
                matchEventInfo.setEventTime(System.currentTimeMillis());
                matchEventInfo.setDataSourceCode(request.getData().getDataSourceCode());
                matchEventInfo.setMatchPeriodId(Long.parseLong(request.getData().getPeriod().toString()));
                matchEventInfo.setThirdEventId(request.getLinkId());
                matchEventInfo.setCopyLinkId(matchScoresInfo.getThirdMatchId()+"red_card");
                matchEventInfo.setCanceled(0);
                matchEventInfo.setThirdMatchSourceId(matchScoresInfo.getThirdMatchSourceId());
                matchEventInfo.setThirdMatchSourceId(matchScoresInfo.getThirdMatchSourceId());
                eventProducer.sendBTEvent(matchEventInfo);
            }
        }catch (Exception   e){

            log.error("BT UOF LINK:{}:error::{}",request.getLinkId(),e);
        }
    }

    /**
     * 检查/初始化角球事件
     * @param matchScoresInfo
     * @param request
     */
    private void checkAndInitCornerEvent(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
        try {
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
            if(wholeScore==null){
                return;
            }
            Integer t1 = 0;
            Integer t2 = 0;
            for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
                if (matchStatisticsInfoDetailDTO.getCode().equals("corner_score") ) {
                    t1 = matchStatisticsInfoDetailDTO.getT1();
                    t2 = matchStatisticsInfoDetailDTO.getT2();
                    break;
                }
            }
            Integer aT1 = t1 - wholeScore.getCorner().getHome();
            Integer aT2 = t2 - wholeScore.getCorner().getAway();
            boolean needGoal = aT1 > 0 || aT2 > 0;
            if(aT1>1||aT2>1){
                needGoal=false;
            }
            String homeAway = aT1 > aT2 ? "home" : "away";
            if (needGoal) {
                MatchEventInfoDTO matchEventInfo = new MatchEventInfoDTO();

                matchEventInfo.setEventCode("corner");
                matchEventInfo.setSecondsFromStart(Long.parseLong(request.getData().getSecondsMatchStart().toString()));
                matchEventInfo.setHomeAway(homeAway);
                matchEventInfo.setT1(t1);
                matchEventInfo.setT2(t2);
                matchEventInfo.setSourceType("1");
                matchEventInfo.setSportId(1L);
                matchEventInfo.setEventTime(System.currentTimeMillis());
                matchEventInfo.setDataSourceCode(request.getData().getDataSourceCode());
                matchEventInfo.setMatchPeriodId(Long.parseLong(request.getData().getPeriod().toString()));
                matchEventInfo.setThirdEventId(request.getLinkId());
                matchEventInfo.setCopyLinkId(matchScoresInfo.getThirdMatchId()+"corner");
                matchEventInfo.setCanceled(0);
                matchEventInfo.setThirdMatchSourceId(matchScoresInfo.getThirdMatchSourceId());
                matchEventInfo.setThirdMatchSourceId(matchScoresInfo.getThirdMatchSourceId());
                eventProducer.sendBTEvent(matchEventInfo);
            }
        }catch (Exception   e){

            log.error("BT UOF LINK:{}:error::{}",request.getLinkId(),e);
        }
    }

    /**
     * 检查BT赛事是否比分倒退
     * @param matchScoresInfo
     * @param request
     * @return
     */
    private boolean checkBTScoreCanceld(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
        String key =SCORES_CANCELD_MATCH+request.getData().getThirdMatchSourceId()+request.getData().getDataSourceCode();
        //如果有缓存则说明这个赛事的数据有问题，先拦截住
        if(redisService.get(key)!=null){
            return true;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeScore=allPeriodScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
        Integer oldT1= wholeScore.getGoal().getHome();
        Integer oldT2= wholeScore.getGoal().getAway();
        Integer t1 =0;
        Integer t2 =0;
        for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
            if (matchStatisticsInfoDetailDTO.getCode().equals("match_score") ) {
                t1 = matchStatisticsInfoDetailDTO.getT1();
                t2 = matchStatisticsInfoDetailDTO.getT2();
                break;
            }
        }
        if(oldT1>t1||oldT2>t2){
            redisService.set(key,SCORES_CANCELD_MATCH,39000);
            return true;
        }
        //角球类判断
        String corner_key =SCORES_CANCELD_MATCH_CORNER+request.getData().getThirdMatchSourceId()+request.getData().getDataSourceCode();
        //如果有缓存则说明这个赛事的数据有问题，先拦截住
        if(redisService.get(corner_key)!=null){
            return true;
        }
        Integer cornerOldT1= wholeScore.getCorner().getHome();
        Integer cornerOldT2= wholeScore.getCorner().getAway();
        Integer cornert1 =0;
        Integer cornert2 =0;
        for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
            if (matchStatisticsInfoDetailDTO.getCode().equals("corner_score") ) {
                cornert1 = matchStatisticsInfoDetailDTO.getT1();
                cornert2 = matchStatisticsInfoDetailDTO.getT2();
                break;
            }
        }
        if(cornerOldT1>cornert1||cornerOldT2>cornert2){
            redisService.set(corner_key,SCORES_CANCELD_MATCH_CORNER,39000);
            return true;
        }

        return false;
    }

    /**
     * 5分钟比分更新
     * @param matchScoresInfo
     * @param request
     */
    private void update5FootballScores(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
        try {
            //3.得到当前阶段比分
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores wholeScore = allPeriodScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
            //3.计算15分钟阶段 编码
            Long period5 = SportPeriodConstant.FootballPeriod.get5MinPeriod(request.getData().getPeriod().longValue(), request.getData().getSecondsMatchStart().longValue());
            if (period5 == null) {
                return;
            }
            FootballScores period5Score = allPeriodScores.get(period5);
            if (period5Score == null) {
                period5Score = new FootballScores(period5);
                allPeriodScores.put(period5, period5Score);
            }
            //15分钟进球角球计算
            for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {

                if (matchStatisticsInfoDetailDTO.getCode().equals("match_score")) {
                    Integer xT1 = matchStatisticsInfoDetailDTO.getT1() - wholeScore.getGoal().getHome();
                    Integer xT2 = matchStatisticsInfoDetailDTO.getT2() - wholeScore.getGoal().getAway();
                    period5Score.getGoal().setHome(period5Score.getGoal().getHome() + xT1);
                    period5Score.getGoal().setAway(period5Score.getGoal().getAway() + xT2);

                }
                if (matchStatisticsInfoDetailDTO.getCode().equals("corner_score")) {
                    Integer xT1 = matchStatisticsInfoDetailDTO.getT1() - wholeScore.getCorner().getHome();
                    Integer xT2 = matchStatisticsInfoDetailDTO.getT2() - wholeScore.getCorner().getAway();
                    period5Score.getCorner().setHome(period5Score.getCorner().getHome() + xT1);
                    period5Score.getCorner().setAway(period5Score.getCorner().getAway() + xT2);
                }
            }
            //保存比分数据记录
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }

//    private void checkAndInitKickOffEvent(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
//        eventProducer.checkAndSentKickOff(matchScoresInfo,request);
//    }

    /**
     * 初始化金秋事件
     * @param matchScoresInfo
     * @param request
     */
    private void checkAndInitGoalEvent(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
        try {
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
            if(wholeScore==null){
                return;
            }
            Integer t1 = 0;
            Integer t2 = 0;
            for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
                if (matchStatisticsInfoDetailDTO.getCode().equals("match_score") ) {
                    t1 = matchStatisticsInfoDetailDTO.getT1();
                    t2 = matchStatisticsInfoDetailDTO.getT2();
                    break;
                }
            }
            Integer aT1 = t1 - wholeScore.getGoal().getHome();
            Integer aT2 = t2 - wholeScore.getGoal().getAway();
            boolean needGoal = aT1 > 0 || aT2 > 0;
            if(aT1>1||aT2>1){
                needGoal=false;
            }
            String homeAway = aT1 > aT2 ? "home" : "away";
            if (needGoal) {
                MatchEventInfoDTO matchEventInfo = new MatchEventInfoDTO();

                matchEventInfo.setEventCode("goal");
                matchEventInfo.setSecondsFromStart(Long.parseLong(request.getData().getSecondsMatchStart().toString()));
                matchEventInfo.setHomeAway(homeAway);
                matchEventInfo.setT1(t1);
                matchEventInfo.setT2(t2);
                matchEventInfo.setSourceType("1");
                matchEventInfo.setSportId(1L);
                matchEventInfo.setEventTime(System.currentTimeMillis());
                matchEventInfo.setDataSourceCode(request.getData().getDataSourceCode());
                matchEventInfo.setMatchPeriodId(Long.parseLong(request.getData().getPeriod().toString()));
                matchEventInfo.setThirdEventId(request.getLinkId());
                matchEventInfo.setCopyLinkId(matchScoresInfo.getThirdMatchId()+"_goal");
                matchEventInfo.setCanceled(0);
                matchEventInfo.setThirdMatchSourceId(matchScoresInfo.getThirdMatchSourceId());
                matchEventInfo.setThirdMatchSourceId(matchScoresInfo.getThirdMatchSourceId());
                eventProducer.sendBTEvent(matchEventInfo);
            }
        }catch (Exception   e){

            log.error("BT UOF LINK:{}:error::{}",request.getLinkId(),e);
        }
    }
    /**
     * 判断如果上半场比分不存在而且发的又不是上半场则拦截比分
     * */
    private boolean checkHTNotExit(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores htScore=allPeriodScores.get(6L);
        if(htScore==null&&request.getData().getPeriod()!=6){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 初始化比分数据
     * @param matchScoresInfo
     * @param request
     */
    private void createScores(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
            FootballScores footballScores=new FootballScores(request.getData().getPeriod()+0l);
            FootballScores wholeScore=new FootballScores(WHOLE_MATCH);
            Map<Long, FootballScores> footballScoresHashMap= new HashMap<>();
            footballScoresHashMap.put(request.getData().getPeriod()+0l,footballScores);
            footballScoresHashMap.put(WHOLE_MATCH,wholeScore);
            //保存比分
            if(request.getData().getMatchStatisticsInfoDetailList()==null){
                log.error("createMatchStatistics data:null");
                return;
            }
            for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {

                if(matchStatisticsInfoDetailDTO.getCode().equals("match_score")){
                    wholeScore.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getCode().equals("yellow_card_score")){
                    wholeScore.getYellowCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getYellowCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getCode().equals("red_card_score")){
                    wholeScore.getRedCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getRedCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getCode().equals("corner_score")){
                    wholeScore.getCorner().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getCorner().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                //dangerous_attack_score shot_on_target_score  shot_off_target_score
                if(matchStatisticsInfoDetailDTO.getCode().equals("dangerous_attack_score")){
                    wholeScore.getDangerousAttack().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getDangerousAttack().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getCode().equals("shot_on_target_score")){
                    wholeScore.getShotOn().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getShotOn().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
                if(matchStatisticsInfoDetailDTO.getCode().equals("shot_off_target_score")){
                    wholeScore.getShotOff().setHome(matchStatisticsInfoDetailDTO.getT1());
                    wholeScore.getShotOff().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
            }
            //更新赛事比分表
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(footballScoresHashMap));
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            return;

    }

    /**
     * 5分钟比分更新
     * @param matchScoresInfo
     * @param request
     */
    private void update15FootballScores(MatchScoresInfo matchScoresInfo, Request<MatchStatisticsInfoDTO> request) {
        try {
            //3.得到当前阶段比分
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
            FootballScores wholeScore = allPeriodScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
            Long period15 = SportPeriodConstant.FootballPeriod.get15MinPeriod(request.getData().getPeriod().longValue(), request.getData().getSecondsMatchStart().longValue());
            // 6 7 41 42
            if (period15 == null) {
                return;
            }
            FootballScores period15Score = allPeriodScores.get(period15);
            if (period15Score == null) {
                period15Score = new FootballScores(period15);
                allPeriodScores.put(period15, period15Score);
            }
            //15分钟进球角球计算
            for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {

                if (matchStatisticsInfoDetailDTO.getCode().equals("match_score")) {
                    Integer xT1 = matchStatisticsInfoDetailDTO.getT1() - wholeScore.getGoal().getHome();
                    Integer xT2 = matchStatisticsInfoDetailDTO.getT2() - wholeScore.getGoal().getAway();
                    period15Score.getGoal().setHome(period15Score.getGoal().getHome() + xT1);
                    period15Score.getGoal().setAway(period15Score.getGoal().getAway() + xT2);

                }
                if (matchStatisticsInfoDetailDTO.getCode().equals("corner_score")) {
                    Integer xT1 = matchStatisticsInfoDetailDTO.getT1() - wholeScore.getCorner().getHome();
                    Integer xT2 = matchStatisticsInfoDetailDTO.getT2() - wholeScore.getCorner().getAway();
                    period15Score.getCorner().setHome(period15Score.getCorner().getHome() + xT1);
                    period15Score.getCorner().setAway(period15Score.getCorner().getAway() + xT2);
                }
            }
            //保存比分数据记录
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }

    /**
     * 更新比分
     * @param matchScoresInfo
     * @param request
     */
    private void updateFootballScores(MatchScoresInfo matchScoresInfo,Request<MatchStatisticsInfoDTO> request) {
        //3.得到当前阶段比分
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeScore=allPeriodScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
        FootballScores period=allPeriodScores.get(request.getData().getPeriod()+0L);
        // 6 7 41 42
        if(request.getData().getPeriod()==6||request.getData().getPeriod()==7||request.getData().getPeriod()==41||request.getData().getPeriod()==42||request.getData().getPeriod()==50){
            if(period==null){
                period=new FootballScores(request.getData().getPeriod()+0l);
                allPeriodScores.put(request.getData().getPeriod()+0l,period);
            }
        }
        for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : request.getData().getMatchStatisticsInfoDetailList()) {
            //非加时点球计算
            if(matchStatisticsInfoDetailDTO.getCode().equals("match_score")&&(request.getData().getPeriod()==6||request.getData().getPeriod()==7||request.getData().getPeriod()==100||request.getData().getPeriod()==31)){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getGoal().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getGoal().getAway();
                wholeScore.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getGoal().setHome( period.getGoal().getHome()+xT1);
                    period.getGoal().setAway( period.getGoal().getAway()+xT2);
                }
            }
            //红黄牌角球 需要阶段性计算 UOF
            //当前总比分 减去 原总比分 即 当前阶段的变化分值，故当前阶段比分= 原阶段比分 + 变化分值
            if(matchStatisticsInfoDetailDTO.getCode().equals("yellow_card_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getYellowCard().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getYellowCard().getAway();
                wholeScore.getYellowCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getYellowCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getYellowCard().setHome( period.getYellowCard().getHome()+xT1);
                    period.getYellowCard().setAway( period.getYellowCard().getAway()+xT2);
                }
            }
            if(matchStatisticsInfoDetailDTO.getCode().equals("red_card_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getRedCard().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getRedCard().getAway();
                wholeScore.getRedCard().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getRedCard().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getRedCard().setHome( period.getRedCard().getHome()+xT1);
                    period.getRedCard().setAway( period.getRedCard().getAway()+xT2);
                }
            }
            if(matchStatisticsInfoDetailDTO.getCode().equals("corner_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getCorner().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getCorner().getAway();
                wholeScore.getCorner().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getCorner().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getCorner().setHome( period.getCorner().getHome()+xT1);
                    period.getCorner().setAway( period.getCorner().getAway()+xT2);
                }
            }
            //dangerous_attack_score shot_on_target_score  shot_off_target_score
            if(matchStatisticsInfoDetailDTO.getCode().equals("dangerous_attack_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getDangerousAttack().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getDangerousAttack().getAway();
                wholeScore.getDangerousAttack().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getDangerousAttack().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getDangerousAttack().setHome( period.getDangerousAttack().getHome()+xT1);
                    period.getDangerousAttack().setAway( period.getDangerousAttack().getAway()+xT2);
                }
            }
            if(matchStatisticsInfoDetailDTO.getCode().equals("shot_on_target_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getShotOn().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getShotOn().getAway();
                wholeScore.getShotOn().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getShotOn().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getShotOn().setHome( period.getShotOn().getHome()+xT1);
                    period.getShotOn().setAway( period.getShotOn().getAway()+xT2);
                }
            }
            if(matchStatisticsInfoDetailDTO.getCode().equals("shot_off_target_score")){
                Integer xT1= matchStatisticsInfoDetailDTO.getT1()-wholeScore.getShotOff().getHome();
                Integer xT2= matchStatisticsInfoDetailDTO.getT2()-wholeScore.getShotOff().getAway();
                wholeScore.getShotOff().setHome(matchStatisticsInfoDetailDTO.getT1());
                wholeScore.getShotOff().setAway(matchStatisticsInfoDetailDTO.getT2());
                if(period!=null){
                    period.getShotOff().setHome( period.getShotOff().getHome()+xT1);
                    period.getShotOff().setAway( period.getShotOff().getAway()+xT2);
                }
            }
            //加时  extra_time_score
            if(matchStatisticsInfoDetailDTO.getCode().equals("extra_time_score")){
                if(request.getData().getPeriod()==41){
                    if(period!=null){
                        period.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                        period.getGoal().setAway( matchStatisticsInfoDetailDTO.getT2());
                    }
                }
                if(request.getData().getPeriod()==42){
                    FootballScores period41=allPeriodScores.get(41l);
                    if(period!=null&&period41!=null){
                        period.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1()-period41.getGoal().getHome());
                        period.getGoal().setAway( matchStatisticsInfoDetailDTO.getT2()-period41.getGoal().getAway());
                    }
                }
            }
            //点球  penalty_shootout
            if(matchStatisticsInfoDetailDTO.getCode().equals("penalty_shootout")){
                if(period!=null){
                    period.getGoal().setHome(matchStatisticsInfoDetailDTO.getT1());
                    period.getGoal().setAway(matchStatisticsInfoDetailDTO.getT2());
                }
            }
            //保存比分数据记录
        }

        period.countFaCard();
        wholeScore.countFaCard();
        period.doShot();
        wholeScore.doShot();
        matchScoresInfo.setT1(wholeScore.getGoal().getHome());
        matchScoresInfo.setT2(wholeScore.getGoal().getAway());
        matchScoresInfo.setSecondsMatchStart(request.getData().getSecondsMatchStart().longValue());
//        matchScoresInfo.setPeriodT1(period.getGoal().getHome());
//        matchScoresInfo.setPeriodT2(period.getGoal().getAway());
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }
}
