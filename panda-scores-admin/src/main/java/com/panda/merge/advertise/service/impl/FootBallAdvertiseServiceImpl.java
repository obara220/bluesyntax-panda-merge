package com.panda.merge.advertise.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.common.Constant;
import com.panda.merge.advertise.dto.*;
import com.panda.merge.advertise.dubbo.MatchFootballBallAdvertiseApiImpl;
import com.panda.merge.advertise.event.BasketEventService;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.event.FootBallEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.BasketBallAdvertiseService;
import com.panda.merge.advertise.service.BasketBallScoreService;
import com.panda.merge.advertise.service.FootBallAdvertiseService;
import com.panda.merge.advertise.service.FootBallScoreService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.api.FootballDashboardAdvertiseApi;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.ChangeMatchStatusDto;
import com.panda.merge.dto.advertise.KickOffDto;
import com.panda.merge.dto.advertise.PossibleEventDto;
import com.panda.merge.mapper.MatchScoresEventInfoMapper;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.mq.producer.ThirdMatchInfoProducer;
import com.panda.merge.repository.PdMatchInfoRepository;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.panda.merge.advertise.common.Constant.MATCH_ADVERTIS_EVENT_STATUS;
import static com.panda.merge.common.enums.Constant.MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE;


@Service
@Slf4j
public class FootBallAdvertiseServiceImpl implements FootBallAdvertiseService {

    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    FootBallScoreService footBallScoreService;
    @Autowired
    IScoresService scoresService;
    @Autowired
    FootBallEventService footBallEventService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    ScoresProducer scoresProducer;
//    @Autowired
//    StandardMatchInfoMapper standardMatchInfoMapper;
//    @Autowired
//    ThirdMatchInfoMapper thirdMatchInfoMapper;
//    @Autowired
//    StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    CommonEventService commonEventService;
    @Autowired
    RedisService redisService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    FootballDashboardAdvertiseApi footballDashboardAdvertiseApi;
//    @Autowired
//    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    MatchFootballBallAdvertiseApiImpl matchFootballBallAdvertiseApi;
    @Autowired
    MatchScoresEventInfoMapper matchScoresEventInfoMapper;
    @Autowired
    PdMatchInfoRepository pdMatchInfoRepository;
    @Autowired
    ThirdMatchInfoProducer thirdMatchInfoProducer;
    @Override
    public Response matchStart(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkedId, KickOffDto kickOff, ChangeMatchStatusDto changeMatchStatus) {
        //1.计算阶段
        Long nextPeriod= 6l;
        //2 计算时长
        Long startTimeSecond= 0l;
        //修改赛事阶段以及时间
        //3.查询阶段比分
        MatchScoreCommonVo matchScoreCommonVo= footBallScoreService.searchCommonMatchScore(matchScoreAndTimeVo.getMatchScoresInfo(),nextPeriod);
        //4.下发阶段事件
        commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo,nextPeriod,startTimeSecond,startTimeSecond,System.currentTimeMillis(),matchScoreCommonVo,linkedId,"");
        String oddsDataSourceCode = matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode();
//        StandardSportMarketSellExample example= new StandardSportMarketSellExample();
//        example.createCriteria().andMatchInfoIdEqualTo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
//        List<StandardSportMarketSell> list= standardSportMarketSellMapper.selectByExample(example);
//        if(list.size()!=0){
//            StandardSportMarketSell standardSportMarketSell=list.get(0);
//            oddsDataSourceCode=standardSportMarketSell.getMatchStatusSourceCode();
//            ThirdMatchInfoExample example2= new ThirdMatchInfoExample();
//            example2.createCriteria().andReferenceIdEqualTo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId()).andDataSourceCodeEqualTo(oddsDataSourceCode);
//            List<ThirdMatchInfo> thirdMatchInfo= thirdMatchInfoMapper.selectByExample(example2);
//            if(thirdMatchInfo.size()!=0){
//                eventProducer.sendMatchStartStatus(thirdMatchInfo.get(0),linkedId);
//            }
//        }
        StandardSportMarketSell marketSell = pdMatchInfoRepository.getStandardSportMarketSell(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId(), null);
        log.info("::{}::PA_createMatchAdvertise开赛 marketSell::kickOff:{}",kickOff.getLinkedId(),marketSell);
        if (!ObjectUtils.isEmpty(marketSell)) {
            log.info("::{}::PA_createMatchAdvertise开赛 oddsDataSourceCode::kickOff:{}",kickOff.getLinkedId(),oddsDataSourceCode);
            ThirdMatchInfo thirdMatchInfo = pdMatchInfoRepository.getThirdMatchInfo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId(), oddsDataSourceCode, null);
            log.info("{}，PD报球板开赛下发状态1：{}",linkedId,thirdMatchInfo.getDataSourceCode());
            if (!ObjectUtils.isEmpty(thirdMatchInfo)) {
                eventProducer.sendMatchStartStatus(thirdMatchInfo, linkedId);
            }
        }

//        StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
        StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId(), null);
        if(standardMatchInfo!=null){
//            StandardSportMarketSellExample marketExample = new StandardSportMarketSellExample();
//            marketExample.createCriteria().andMatchInfoIdEqualTo(standardMatchInfo.getId());
//            List<StandardSportMarketSell> marketSells = standardSportMarketSellMapper.selectByExample(marketExample);
            StandardSportMarketSell marketSell1 = pdMatchInfoRepository.getStandardSportMarketSell(standardMatchInfo.getId(), null);
            if (!ObjectUtils.isEmpty(marketSell1)) {
                String dataSourceCode = "";
                if (null != kickOff && !ObjectUtils.isEmpty(kickOff.getDataSourceCode())) {
                    dataSourceCode = kickOff.getDataSourceCode();
                }
                if (null != changeMatchStatus && !ObjectUtils.isEmpty(changeMatchStatus.getDataSourceCode())) {
                    dataSourceCode = changeMatchStatus.getDataSourceCode();
                }
                if (dataSourceCode.equals(marketSell1.getBusinessEvent())) {
                    StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
                    newStandardMatchInfo.setId(standardMatchInfo.getId());
                    newStandardMatchInfo.setMatchStatus(1);
                    newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
//                    standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
                    pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
                }
            }
            matchScoreAndTimeVo.getThirdMatchInfo().setMatchStatus(1);
            matchScoreAndTimeVo.getThirdMatchInfo().setModifyTime(System.currentTimeMillis());
//            thirdMatchInfoMapper.updateByPrimaryKey( matchScoreAndTimeVo.getThirdMatchInfo());
            log.info("{}，PD报球板开赛下发状态2：{}",linkedId,matchScoreAndTimeVo.getThirdMatchInfo().getDataSourceCode());
            eventProducer.sendMatchStatusTopic(linkedId, matchScoreAndTimeVo.getThirdMatchInfo(), matchScoreAndTimeVo.getThirdMatchInfo().getMatchStatus());
        }
        try {
            Thread.sleep(200);
            MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createMatchTimeEvent(matchScoreAndTimeVo,startTimeSecond,startTimeSecond,System.currentTimeMillis(),1,nextPeriod,linkedId+"_PD");
            //3.发送MQ且记录事件
            matchEventInfoDTO.setAddition9("false");
            eventProducer.sendPDEventInfo(matchEventInfoDTO);
        } catch (InterruptedException e) {
            log.error("matchStart:",e);
        }
        return Response.success();
    }


    @Override
    public Response match999End(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkedId, ChangeMatchStatusDto changeMatchStatus) {
        MatchScoreCommonVo matchScoreCommonVo= footBallScoreService.searchCommonMatchScore(matchScoreAndTimeVo.getMatchScoresInfo(),999L);
        //4.下发阶段事件
        if(footBallScoreService.hasExtryPeriod(matchScoreAndTimeVo.getMatchScoresInfo())){
            commonEventService.changeFootBallMatchPeriodEvent(matchScoreAndTimeVo,110l,7200L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId+"_PD",changeMatchStatus.getOperatorName());
        }else if(footBallScoreService.hasPenaltyAwarded(matchScoreAndTimeVo.getMatchScoresInfo())) {
            commonEventService.changeFootBallMatchPeriodEvent(matchScoreAndTimeVo,120l,7200L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId+"_PD",changeMatchStatus.getOperatorName());
        }else{
            commonEventService.changeFootBallMatchPeriodEvent(matchScoreAndTimeVo,100l,5400L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId+"_PD",changeMatchStatus.getOperatorName());
        }
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            log.error("match999End:",e);
        }
        commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo,999l,5400L,0L,System.currentTimeMillis(),matchScoreCommonVo,linkedId,changeMatchStatus.getOperatorName());
        //1.修改三方赛事和标准赛事状态
//        StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
        StandardMatchInfo standardMatchInfo = pdMatchInfoRepository.getStandardMatchInfo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId(), null);
        if(standardMatchInfo!=null){
//            StandardSportMarketSellExample example = new StandardSportMarketSellExample();
//            example.createCriteria().andMatchInfoIdEqualTo(standardMatchInfo.getId());
//            List<StandardSportMarketSell> marketSells = standardSportMarketSellMapper.selectByExample(example);
            StandardSportMarketSell marketSell = pdMatchInfoRepository.getStandardSportMarketSell(standardMatchInfo.getId(), null);
            if (!ObjectUtils.isEmpty(marketSell)) {
                if (changeMatchStatus.getDataSourceCode().equals(marketSell.getBusinessEvent())) {
                    StandardMatchInfo newStandardMatchInfo = new StandardMatchInfo();
                    newStandardMatchInfo.setId(standardMatchInfo.getId());
                    newStandardMatchInfo.setMatchStatus(3);
                    newStandardMatchInfo.setModifyTime(System.currentTimeMillis());
//                    standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
                    pdMatchInfoRepository.setRedisAndStandardMatchInfo(newStandardMatchInfo, null);
                }
            }
            matchScoreAndTimeVo.getThirdMatchInfo().setMatchStatus(3);
            matchScoreAndTimeVo.getThirdMatchInfo().setModifyTime(System.currentTimeMillis());
//            thirdMatchInfoMapper.updateByPrimaryKey( matchScoreAndTimeVo.getThirdMatchInfo());
//            eventProducer.sendMatchStatusTopic(linkedId, matchScoreAndTimeVo.getThirdMatchInfo(), matchScoreAndTimeVo.getThirdMatchInfo().getMatchStatus());
            thirdMatchInfoProducer.updateMatchTimesInfoByMq(matchScoreAndTimeVo.getThirdMatchInfo());
        }
        //2.下发状态变更
        eventProducer.sendMatchStatusTopic(linkedId, matchScoreAndTimeVo.getThirdMatchInfo(), 3);
        return Response.success();
    }



    @Override
    public Response changeMatchPeriod(MatchScoreAndTimeVo data, Long periodId, String linkedId,String userName) {
        //3.查询阶段比分
        MatchScoreCommonVo matchScoreCommonVo= footBallScoreService.searchCommonMatchScore(data.getMatchScoresInfo(),periodId);
        data.setMatchScoresInfo(matchScoreCommonVo.getMatchScoresInfo());
        //足球是顺序下发无需下发多重阶段
        //时间计算
        Long startTimeSecond =0L;
        if(SportPeriodConstant.FootballPeriod.contans(data.getMatchTimeInfo().getPeriod())){
            startTimeSecond =  data.getMatchTimeInfo().getSecondFromStart()+(System.currentTimeMillis()- data.getMatchTimeInfo().getEventTime())/1000;
        }else {
            startTimeSecond= data.getMatchTimeInfo().getSecondFromStart();
        }

        //根据当前阶段判断时间
        if(periodId.equals(7L)||periodId.equals(41L)||periodId.equals(42L)){
            startTimeSecond= MatchPeriodUtils.getFootBallPeriodTime(data.getStandardMatchInfo().getMatchLength(),periodId);
        }
        if (periodId.equals(6L)) {
            startTimeSecond = 0L;
        }
        if (periodId.equals(31L)) {
            MatchTimeInfo matchTimeInfo = data.getMatchTimeInfo();
            long curTime = System.currentTimeMillis();
            matchTimeInfo.setHalfTime(curTime);
            matchTimeInfo.setModifyTime(curTime);
//            matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
            data.setMatchTimeInfo(matchTimeInfo);
        }
        //3.推送WS 事件接口
        commonEventService.setDangerOrSafe(false,data.getThirdMatchInfo().getId());
        //2.比分回滚阶段修剪
//        if(periodId.equals(6L)||periodId.equals(7L)||periodId.equals(41L)||periodId.equals(42L)){
//            matchFootballBallAdvertiseApi.cancelEndScoreUpdate(thirdMatchInfo,Math.toIntExact(startTimeSecond),periodId,data.getMatchScoresInfo());
//        }
        //4.下发阶段事件
        commonEventService.changeMatchPeriodEvent(data,periodId,startTimeSecond,startTimeSecond,System.currentTimeMillis(),matchScoreCommonVo,linkedId,userName);
        return Response.success();
    }

    @Override
    public Response<FootBallAdvertiseVo> buildFootBallAdvertiseVo(MatchScoreAndTimeVo data) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ThirdMatchInfo thirdMatchInfo =data.getThirdMatchInfo();
        MatchTimeInfo timeInfo =data.getMatchTimeInfo();
        Long infoPeriod = timeInfo.getPeriod();
        Integer infoTimeGo = timeInfo.getTimeGo();
        MatchTimeInfo matchTimeInfo = pdMatchInfoRepository.getMatchTimeInfo(data.getMatchTimeInfo().getThirdMatchId(), SourceTypeEnum.LIVE_DATA.getCode(), null);
        if ( !Objects.isNull(matchTimeInfo) ) {
            if ( null == matchTimeInfo.getTimeGo() || !Objects.equals(infoTimeGo, matchTimeInfo.getTimeGo())) {
                matchTimeInfo.setTimeGo(infoTimeGo);
            }
            if ( !Objects.equals(infoPeriod, matchTimeInfo.getPeriod()) ) {
                matchTimeInfo.setPeriod(infoPeriod);
            }
        } else {
            matchTimeInfo = timeInfo;
        }
//        MatchScoresInfo matchScoresInfo =data.getMatchScoresInfo();
        MatchScoresInfo matchScoresInfo =pdMatchInfoRepository.getMatchScoresInfoByPrimaryKey(data.getMatchScoresInfo().getId(), null);
        FootBallAdvertiseVo pdMatchAdvertiseVo=new FootBallAdvertiseVo();
        pdMatchAdvertiseVo.setThirdMatchId(thirdMatchInfo.getId().toString());
        pdMatchAdvertiseVo.setStandardMatchId(thirdMatchInfo.getReferenceId());
        pdMatchAdvertiseVo.setEventTime(matchTimeInfo.getEventTime());
        pdMatchAdvertiseVo.setMatchBeginTime(thirdMatchInfo.getBeginTime());
        pdMatchAdvertiseVo.setIsGo(matchTimeInfo.getTimeGo());
        pdMatchAdvertiseVo.setHasPeriod(countHasPeriod(data));
        pdMatchAdvertiseVo.setDataSourceCode(data.getThirdMatchInfo().getDataSourceCode());
        //非开赛阶段 0
        if(!SportPeriodConstant.FootballPeriod.contans(matchTimeInfo.getPeriod())){
            pdMatchAdvertiseVo.setIsGo(0);
        }
        pdMatchAdvertiseVo.setPeriod(matchTimeInfo.getPeriod());
        //时间计算
        //1.根据不同的赛制得到不同的倒计时
        Long startTimeSecond = 0L;
        if(SportPeriodConstant.FootballPeriod.contans( matchTimeInfo.getPeriod())){
            startTimeSecond =  matchTimeInfo.getSecondFromStart()+(System.currentTimeMillis()- matchTimeInfo.getEventTime())/1000;
        }else {
            startTimeSecond= matchTimeInfo.getSecondFromStart();
        }
        //2.
        if(startTimeSecond < 0L){
            startTimeSecond = 0L;
        }
        String suspensionKey = matchTimeInfo.getThirdMatchId() + "suspension";
        if (null != redisService.get(suspensionKey) && matchTimeInfo.getTimeGo() == 0) {
            startTimeSecond = matchTimeInfo.getSecondFromStart();
        }
        String interruptionKey = matchTimeInfo.getThirdMatchId() + "interruption";
        Object interruptionKeyObj = redisService.get(interruptionKey);
        if (null != interruptionKeyObj) {
            startTimeSecond = matchTimeInfo.getSecondFromStart();
        }
        log.info("足球报球板事件时间，thirdMatchId={},matchTime-startTimeSecond={},secondFromStart={},eventTime={}",
                matchTimeInfo.getThirdMatchId(),startTimeSecond,matchTimeInfo.getSecondFromStart(),matchTimeInfo.getEventTime());
        if (data.getStandardMatchInfo() != null) {
            pdMatchAdvertiseVo.setMatchManageId(data.getStandardMatchInfo().getMatchManageId());
        }
        pdMatchAdvertiseVo.setMatchTime(startTimeSecond);
        //比分计算
        FootBallScoreVo footBallScoreVo = footBallScoreService.transforScore(matchScoresInfo);
        pdMatchAdvertiseVo.setFootBallScore(footBallScoreVo);
        pdMatchAdvertiseVo = buildCacheMatchStatus(pdMatchAdvertiseVo);
        //点球大战比分
        buildPenaltyScore(matchScoresInfo,pdMatchAdvertiseVo);
        //15分钟进球比分
        build15GoalScore(matchScoresInfo,pdMatchAdvertiseVo);
        //15分钟角球比分
        build15CornerScore(matchScoresInfo,pdMatchAdvertiseVo);
        //5分钟进球比分
        build5GoalScore(matchScoresInfo,pdMatchAdvertiseVo);

        //15分钟黄牌
        build15YellowCrd(matchScoresInfo, pdMatchAdvertiseVo );

        //15分钟红牌
        build15RedCrd(matchScoresInfo, pdMatchAdvertiseVo );

        // 15分钟罚牌
        build15FaCrd(matchScoresInfo, pdMatchAdvertiseVo );

        // 15分钟统计：按进球种类划分
        footballPeriodScores(pdMatchAdvertiseVo);

        // PA报球板-赛事统计
        footballMatchCount(matchScoresInfo, pdMatchAdvertiseVo);

        MatchTimeInfo timeStatusDto = footballDashboardAdvertiseApi.getMatchTimeInfoUpdated(matchTimeInfo.getThirdMatchId());
        if (timeStatusDto == null) {
            timeStatusDto = matchTimeInfo;
        }
        timeStatusDto.setSecondFromStart(startTimeSecond);
        pdMatchAdvertiseVo.setInjuryAndtimeStatus(timeStatusDto);

        Integer timeGo = timeStatusDto.getTimeGo();
        if (timeGo == 0) {
            pdMatchAdvertiseVo.setControlType(2);
        }
        if (timeGo == 1) {
            pdMatchAdvertiseVo.setControlType(3);
        }

        // 点球进球后重踢
        MatchScoresEventInfoExample example = new MatchScoresEventInfoExample();
        example.createCriteria().andEventCodeIn(Arrays.asList("penalty_goal", "goal", "kick_off", "penalty"))
                .andThirdMatchSourceIdEqualTo(thirdMatchInfo.getThirdMatchSourceId());
        example.setOrderByClause("id desc limit 1");
        List<MatchScoresEventInfo> matchScoresEventInfoList = matchScoresEventInfoMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(matchScoresEventInfoList)) {
            MatchScoresEventInfo matchScoresEventInfo = matchScoresEventInfoList.get(0);
            String homeAway = matchScoresEventInfo.getHomeAway();
            if ("goal".equals(matchScoresEventInfo.getEventCode()) || "penalty_goal".equals(matchScoresEventInfo.getEventCode())) {
                if ("home".equals(homeAway)) {
                    pdMatchAdvertiseVo.setRetakePen("away");
                }
                if ("away".equals(homeAway)) {
                    pdMatchAdvertiseVo.setRetakePen("home");
                }
            }
            boolean kickoffFlag = "kick_off".equals(matchScoresEventInfo.getEventCode())
                    || matchScoresEventInfo.getCanceled() != null && matchScoresEventInfo.getCanceled() == 1
                    || pdMatchAdvertiseVo.getPeriod() == 50;
            if (kickoffFlag) {
                pdMatchAdvertiseVo.setRetakePen(null);
            }
        }

        //查询结算状态
        buildSettleStatus(pdMatchAdvertiseVo);
        pdMatchAdvertiseVo.setLiveEventSource(data.getStandardMatchInfo().getLiveEventSource());
        stopWatch.stop();
        log.info("BasketBallAdvertiseServiceImpl-buildFootBallAdvertiseVo-耗时={}, thirdMatchId={}",stopWatch.getTotalTimeMillis(),matchTimeInfo.getThirdMatchId());
        return Response.success(pdMatchAdvertiseVo);
    }

    /**
     * PA报球板阶段比分
     *
     * @param pdMatchAdvertiseVo 报球板大对象
     */
    private void footballPeriodScores(FootBallAdvertiseVo pdMatchAdvertiseVo) {
        FootballPeriodTimeVo footballPeriodTime = new FootballPeriodTimeVo();
        FootBallGoalScore15Vo footBallGoal15Score = pdMatchAdvertiseVo.getFootBallGoal15Score();
        FootBallCornerScore15Vo footBallCorner15Score = pdMatchAdvertiseVo.getFootBallCorner15Score();
        FootBallYellowCard15Vo footBallYellowCard15Vo = pdMatchAdvertiseVo.getFootBallYellowCard15Vo();
        FootBallRedCard15Vo footBallRedCard15Vo = pdMatchAdvertiseVo.getFootBallRedCard15Vo();
        // 00:00-14:59 进球、角球、黄牌、红牌
        footballPeriodTime.setPeriod15(new FootBallPeriod15Vo(
                footBallGoal15Score.getGoal15(), footBallCorner15Score.getCorner15(),
                footBallYellowCard15Vo.getYellowCard15(), footBallRedCard15Vo.getRedCard15()));
        // 15:00-29:59 进球、角球、黄牌、红牌
        footballPeriodTime.setPeriod30(new FootBallPeriod15Vo(
                footBallGoal15Score.getGoal30(), footBallCorner15Score.getCorner30(),
                footBallYellowCard15Vo.getYellowCard30(), footBallRedCard15Vo.getRedCard30()));
        // 30:00-44:59 进球、角球、黄牌、红牌
        footballPeriodTime.setPeriod45(new FootBallPeriod15Vo(
                footBallGoal15Score.getGoal45(), footBallCorner15Score.getCorner45(),
                footBallYellowCard15Vo.getYellowCard45(), footBallRedCard15Vo.getRedCard45()));
        // 45:00-59:59 进球、角球、黄牌、红牌
        footballPeriodTime.setPeriod60(new FootBallPeriod15Vo(
                footBallGoal15Score.getGoal60(), footBallCorner15Score.getCorner60(),
                footBallYellowCard15Vo.getYellowCard60(), footBallRedCard15Vo.getRedCard60()));
        // 60:00-74:59 进球、角球、黄牌、红牌
        footballPeriodTime.setPeriod75(new FootBallPeriod15Vo(
                footBallGoal15Score.getGoal75(), footBallCorner15Score.getCorner75(),
                footBallYellowCard15Vo.getYellowCard75(), footBallRedCard15Vo.getRedCard75()));
        // 75:00- 进球、角球、黄牌、红牌
        footballPeriodTime.setPeriod90(new FootBallPeriod15Vo(
                footBallGoal15Score.getGoal90(), footBallCorner15Score.getCorner90(),
                footBallYellowCard15Vo.getYellowCard90(), footBallRedCard15Vo.getRedCard90()));
        pdMatchAdvertiseVo.setFootballPeriodTimeVo(footballPeriodTime);
    }

    /**
     * PA报球板-赛事统计
     *
     * @param matchScoresInfo    赛事比分
     * @param pdMatchAdvertiseVo 报球板大对象
     */
    private void footballMatchCount(MatchScoresInfo matchScoresInfo, FootBallAdvertiseVo pdMatchAdvertiseVo) {
        String scoresJson = matchScoresInfo.getScoresJson();
        JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballMatchStageVo goal = new FootballMatchStageVo();
        goal.setTechName("进球");
        FootballMatchStageVo corner = new FootballMatchStageVo();
        corner.setTechName("角球");
        FootballMatchStageVo goalKick = new FootballMatchStageVo();
        goalKick.setTechName("球门球");
        FootballMatchStageVo throwIn = new FootballMatchStageVo();
        throwIn.setTechName("界外球");
        FootballMatchStageVo ballPossessionPercentage = new FootballMatchStageVo();
        ballPossessionPercentage.setTechName("控球率");
        FootballMatchStageVo possessionCount = new FootballMatchStageVo();
        possessionCount.setTechName("持球数");
        FootballMatchStageVo attack = new FootballMatchStageVo();
        attack.setTechName("进攻");
        FootballMatchStageVo dangerousAttack = new FootballMatchStageVo();
        dangerousAttack.setTechName("危险进攻");
        FootballMatchStageVo yellowCard = new FootballMatchStageVo();
        yellowCard.setTechName("黄牌");
        FootballMatchStageVo redCard = new FootballMatchStageVo();
        redCard.setTechName("红牌");
        FootballMatchStageVo shotOnTarget = new FootballMatchStageVo();
        shotOnTarget.setTechName("射正");
        FootballMatchStageVo shotOffTarget = new FootballMatchStageVo();
        shotOffTarget.setTechName("射偏");
        FootballMatchStageVo freeKick = new FootballMatchStageVo();
        freeKick.setTechName("任意球");
        FootballMatchStageVo offside = new FootballMatchStageVo();
        offside.setTechName("越位");
        for (Map.Entry<Long, FootballScores> vo : allPeriodScores.entrySet()) {
            FootballScores value = vo.getValue();
            // 上半场
            if (vo.getKey() == 6L) {
                goal.setFirstHalf(value.getGoal());
                corner.setFirstHalf(value.getCorner());
                goalKick.setFirstHalf(value.getGoalKick());
                throwIn.setFirstHalf(value.getThrowIn());
                ballPossessionPercentage.setFirstHalf(value.getBallPossessionPercentage());
                possessionCount.setFirstHalf(value.getPossessionCount());
                attack.setFirstHalf(value.getAttack());
                dangerousAttack.setFirstHalf(value.getDangerousAttack());
                yellowCard.setFirstHalf(value.getYellowCard());
                redCard.setFirstHalf(value.getRedCard());
                shotOnTarget.setFirstHalf(value.getShotOn());
                shotOffTarget.setFirstHalf(value.getShotOff());
                freeKick.setFirstHalf(value.getFreeKickScore());
                offside.setFirstHalf(value.getOffside());
            }
            // 下半场
            if (vo.getKey() == 7L) {
                goal.setSecondHalf(value.getGoal());
                corner.setSecondHalf(value.getCorner());
                goalKick.setSecondHalf(value.getGoalKick());
                throwIn.setSecondHalf(value.getThrowIn());
                ballPossessionPercentage.setSecondHalf(value.getBallPossessionPercentage());
                possessionCount.setSecondHalf(value.getPossessionCount());
                attack.setSecondHalf(value.getAttack());
                dangerousAttack.setSecondHalf(value.getDangerousAttack());
                yellowCard.setSecondHalf(value.getYellowCard());
                redCard.setSecondHalf(value.getRedCard());
                shotOnTarget.setSecondHalf(value.getShotOn());
                shotOffTarget.setSecondHalf(value.getShotOff());
                freeKick.setSecondHalf(value.getFreeKickScore());
                offside.setSecondHalf(value.getOffside());
            }
            // 加时上半场
            if (vo.getKey() == 41L) {
                goal.setFirstHalfOvertime(value.getGoal());
                corner.setFirstHalfOvertime(value.getCorner());
                goalKick.setFirstHalfOvertime(value.getGoalKick());
                throwIn.setFirstHalfOvertime(value.getThrowIn());
                ballPossessionPercentage.setFirstHalfOvertime(value.getBallPossessionPercentage());
                possessionCount.setFirstHalfOvertime(value.getPossessionCount());
                attack.setFirstHalfOvertime(value.getAttack());
                dangerousAttack.setFirstHalfOvertime(value.getDangerousAttack());
                yellowCard.setFirstHalfOvertime(value.getYellowCard());
                redCard.setFirstHalfOvertime(value.getRedCard());
                shotOnTarget.setFirstHalfOvertime(value.getShotOn());
                shotOffTarget.setFirstHalfOvertime(value.getShotOff());
                freeKick.setFirstHalfOvertime(value.getFreeKickScore());
                offside.setFirstHalfOvertime(value.getOffside());
            }
            // 加时下半场
            if (vo.getKey() == 42L) {
                goal.setSecondHalfOvertime(value.getGoal());
                corner.setSecondHalfOvertime(value.getCorner());
                goalKick.setSecondHalfOvertime(value.getGoalKick());
                throwIn.setSecondHalfOvertime(value.getThrowIn());
                ballPossessionPercentage.setSecondHalfOvertime(value.getBallPossessionPercentage());
                possessionCount.setSecondHalfOvertime(value.getPossessionCount());
                attack.setSecondHalfOvertime(value.getAttack());
                dangerousAttack.setSecondHalfOvertime(value.getDangerousAttack());
                yellowCard.setSecondHalfOvertime(value.getYellowCard());
                redCard.setSecondHalfOvertime(value.getRedCard());
                shotOnTarget.setSecondHalfOvertime(value.getShotOn());
                shotOffTarget.setSecondHalfOvertime(value.getShotOff());
                freeKick.setSecondHalfOvertime(value.getFreeKickScore());
                offside.setSecondHalfOvertime(value.getOffside());
            }
            // 全场
            if (vo.getKey() == -1L) {
                goal.setFullHalf(value.getGoal());
                corner.setFullHalf(value.getCorner());
                goalKick.setFullHalf(value.getGoalKick());
                throwIn.setFullHalf(value.getThrowIn());
                ballPossessionPercentage.setFullHalf(value.getBallPossessionPercentage());
                possessionCount.setFullHalf(value.getPossessionCount());
                attack.setFullHalf(value.getAttack());
                dangerousAttack.setFullHalf(value.getDangerousAttack());
                yellowCard.setFullHalf(value.getYellowCard());
                redCard.setFullHalf(value.getRedCard());
                shotOnTarget.setFullHalf(value.getShotOn());
                shotOffTarget.setFullHalf(value.getShotOff());
                freeKick.setFullHalf(value.getFreeKickScore());
                offside.setFullHalf(value.getOffside());
            }
        }
        FootballScoreboardVo footballScoreboardVo = new FootballScoreboardVo(
                goal, corner, goalKick, throwIn, ballPossessionPercentage, possessionCount, attack, dangerousAttack, yellowCard,
                redCard, shotOnTarget, shotOffTarget, freeKick, offside);
        pdMatchAdvertiseVo.setFootballScoreboardVo(footballScoreboardVo);
    }

    private void buildSettleStatus( FootBallAdvertiseVo pdMatchAdvertiseVo) {
        Object switchPdKey = redisService.get(MATCH_FOOTBALL_SWITCH_PD_DATA_SOURCE +":"+ pdMatchAdvertiseVo.getThirdMatchId());
        if (switchPdKey != null) {
            Integer status=0;
            //这个字段之前是三方赛事id 可能会超长，如果有则为0 不参与结算
            if(switchPdKey.toString().length()>7){
                 status=0;
            }else {
                 status =  Integer.parseInt(switchPdKey.toString());
            }
            pdMatchAdvertiseVo.setSettleStatus(status);
        }else {
            pdMatchAdvertiseVo.setSettleStatus(1);
        }

    }

    @Override
    public void checkAndcreateMinuteScore(MatchEventInfoDTO eventInfoDTO) {
        //redis加锁 key = 三方原始ID+dataSourceCode
        String key = eventInfoDTO.getThirdMatchSourceId() + eventInfoDTO.getDataSourceCode();
        try {
            if (redisService.tryLock(key, key, 10, 10)) {
                //先查询到 三方PD 比分
                MatchScoresInfoExample example = new MatchScoresInfoExample();
                example.createCriteria()
                        .andThirdMatchSourceIdEqualTo(eventInfoDTO.getThirdMatchSourceId())
                        .andDataSourceCodeEqualTo(eventInfoDTO.getDataSourceCode());
                List<MatchScoresInfo> matchScoresInfos = matchScoresInfoMapper.selectByExample(example);
                if (matchScoresInfos.size() <= 0) {
                    return;
                }
                //3.计算15分钟阶段 编码
                Long period15 =SportPeriodConstant.FootballPeriod.get15MinPeriod(eventInfoDTO.getMatchPeriodId(),eventInfoDTO.getSecondsFromStart());
                if(period15==null){
                    return;
                }
                //查询比分信息并解析
                MatchScoresInfo matchScoresInfo = matchScoresInfos.get(0);
                String scoresJson = matchScoresInfo.getScoresJson();
                JSONObject periodFootballScores = JSONObject.parseObject(scoresJson);
                Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
                FootballScores periodScores= allPeriodScores.get(period15);
                //检查是否有15分钟 比分
                if (periodScores == null) {
                    //如果没有则生成
                    matchScoresInfo.setEventTime(System.currentTimeMillis());
                    periodScores = FootballScores.createMinFootballScores();
                    allPeriodScores.put(period15, periodScores);
                    matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                    matchScoresInfo.setModifyTime(System.currentTimeMillis());
                    matchScoresInfo.setEventTime(eventInfoDTO.getEventTime());
                    //保存到三方赛事比分表
//                    matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
                    pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
                }
                //检查是否有5分钟比分
                Long period5 =SportPeriodConstant.FootballPeriod.get5MinPeriod(eventInfoDTO.getMatchPeriodId(),eventInfoDTO.getSecondsFromStart());
                if(period5==null){
                    return;
                }
                FootballScores periodScores5= allPeriodScores.get(period15);
                if (periodScores5 == null) {
                    //如果没有则生成
                    periodScores = FootballScores.createMinFootballScores();
                    matchScoresInfo.setEventTime(System.currentTimeMillis());
                    allPeriodScores.put(period5, periodScores);
                    matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                    matchScoresInfo.setModifyTime(System.currentTimeMillis());
                    matchScoresInfo.setEventTime(eventInfoDTO.getEventTime());
                    //保存到三方赛事比分表
//                    matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
                    pdMatchInfoRepository.setRedisAndMatchScoresInfo(matchScoresInfo, null);
                }

            }
        } catch (Exception e) {
            log.error("处理数据发生异常", e);
            return;
        } finally {
            //final 解锁
            redisService.unLock(key, key);
        }

    }

    @Override
    public Response matchPause(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkedId, ChangeMatchStatusDto changeMatchStatus) {
        //1. 暂停时间
        Long startTimeSecond =  changeMatchStatus.getTimeFromStartSecond();
        //2.下发时间暂停
        commonEventService.updateFootballMatchTimeEvent(matchScoreAndTimeVo,matchScoreAndTimeVo.getMatchTimeInfo().getPeriod()
                ,startTimeSecond,startTimeSecond,System.currentTimeMillis(),0,linkedId, changeMatchStatus.getControlType());
        return Response.success();
    }

    @Override
    public Response matchContinue(MatchScoreAndTimeVo data, String linkedId, Integer controlType) {
        //1.下发时间继续
        commonEventService.updateFootballMatchTimeEvent(data, data.getMatchTimeInfo().getPeriod(),
                data.getMatchTimeInfo().getSecondFromStart(), data.getMatchTimeInfo().getSecondFromStart(),
                System.currentTimeMillis(), 1, linkedId, controlType);
        return Response.success();
    }

    @Override
    public Response matchEnd(MatchScoreAndTimeVo data, String linkedId) {
        //1.下发时间继续
        commonEventService.updateMatchTimeEvent(data, 6L,
                0L, 0L,
                System.currentTimeMillis(), 0, linkedId);
        return Response.success();
    }

    private void build15GoalScore(MatchScoresInfo matchScoresInfo, FootBallAdvertiseVo pdMatchAdvertiseVo) {
        try{
            FootBallGoalScore15Vo footBallGoalScore15Vo = new FootBallGoalScore15Vo(matchScoresInfo);
            pdMatchAdvertiseVo.setFootBallGoal15Score(footBallGoalScore15Vo);
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }

    private void build15YellowCrd( MatchScoresInfo matchScoresInfo, FootBallAdvertiseVo pdMatchAdvertiseVo )
    {
        try
        {
            FootBallYellowCard15Vo footBallYellowCard15Vo = new FootBallYellowCard15Vo(matchScoresInfo);
            pdMatchAdvertiseVo.setFootBallYellowCard15Vo(footBallYellowCard15Vo);
        } catch ( Exception e )
        {
            log.error(":处理数据发生异常,error：", e);
        }
    }

    private void build15RedCrd( MatchScoresInfo matchScoresInfo, FootBallAdvertiseVo pdMatchAdvertiseVo )
    {
        try
        {
            FootBallRedCard15Vo footBallRedCard15Vo = new FootBallRedCard15Vo(matchScoresInfo);
            pdMatchAdvertiseVo.setFootBallRedCard15Vo(footBallRedCard15Vo);
        } catch ( Exception e )
        {
            log.error(":处理数据发生异常,error：", e);
        }
    }


    private void build15FaCrd( MatchScoresInfo matchScoresInfo, FootBallAdvertiseVo pdMatchAdvertiseVo )
    {
        try
        {
            FootBallFaCard15Vo footBallFaCard15Vo = new FootBallFaCard15Vo(matchScoresInfo);
            pdMatchAdvertiseVo.setFootBallFaCard15Vo(footBallFaCard15Vo);
        } catch ( Exception e )
        {
            log.error(":处理数据发生异常,error：", e);
        }
    }

    private void build15CornerScore(MatchScoresInfo matchScoresInfo,FootBallAdvertiseVo pdMatchAdvertiseVo)
    {
        try{
            FootBallCornerScore15Vo footBallCornerScore15Vo = new FootBallCornerScore15Vo(matchScoresInfo);
            pdMatchAdvertiseVo.setFootBallCorner15Score(footBallCornerScore15Vo);
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }

    private void build5GoalScore(MatchScoresInfo matchScoresInfo,FootBallAdvertiseVo pdMatchAdvertiseVo) {
        try{
            FootBallGoalScore5Vo footBallGoalScore5Vo =new FootBallGoalScore5Vo(matchScoresInfo);
            pdMatchAdvertiseVo.setFootBallGoal5Score(footBallGoalScore5Vo);
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }

    private void buildPenaltyScore(MatchScoresInfo matchScoresInfo,FootBallAdvertiseVo pdMatchAdvertiseVo) {
        try{
            FootBallPenaltyScoreVo footBallPenaltyScoreVo =new FootBallPenaltyScoreVo(matchScoresInfo);
            pdMatchAdvertiseVo.setFootBallPenaltyScore(footBallPenaltyScoreVo);
        }catch (Exception e){
            log.error(":处理数据发生异常:", e);
        }
    }

    private Integer countHasPeriod(MatchScoreAndTimeVo data) {
        Long period = data.getMatchTimeInfo().getPeriod();
        if (period == null) {
            period = 0L;
        }
        if(period.equals(0l)||
                period.equals(6l)||
                period.equals(7l)||
                period.equals(31l)||
                period.equals(100l)){
            return 1;
        }else if(period.equals(32L)||
                period.equals(41L)||
                period.equals(33L)||
                period.equals(42L)||
                period.equals(110L)){
            return 2;
        }else if(period.equals(34l)||
                period.equals(50l)||
                period.equals(120l)){
            return 3;
        }else{
            JSONObject periodFootballScores = JSONObject.parseObject(data.getMatchScoresInfo().getScoresJson());
            Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
            if(allPeriodScores.get(50l)!=null){
                return 3;
            }
            if(allPeriodScores.get(41l)!=null){
                return 2;
            }
            return 1;
        }
    }


    /**
     * 从缓存中取赛事事件状态
     * */
    private FootBallAdvertiseVo buildCacheMatchStatus(FootBallAdvertiseVo pdMatchAdvertiseVo) {
        String key =MATCH_ADVERTIS_EVENT_STATUS +pdMatchAdvertiseVo.getThirdMatchId();
        Object cacheEventStatus=redisService.get(key);
        FootballMatchEventStatusVo footballMatchEventStatusVo= FootballMatchEventStatusVo.init();
        FootBallEventStatusVo footBallEventStatus=new FootBallEventStatusVo();
        if(cacheEventStatus!=null){
            try{
                footballMatchEventStatusVo =JSONObject.toJavaObject(JSONObject.parseObject(cacheEventStatus.toString()) ,FootballMatchEventStatusVo.class);
            }catch (Exception e ){
                log.error("buildCacheMatchStatus error::",e);

            }
        }
        pdMatchAdvertiseVo.setCurrentEventCode(footballMatchEventStatusVo.getCurrentEventCode());
        pdMatchAdvertiseVo.setDanger(footballMatchEventStatusVo.getIsDanger());
        footBallEventStatus.setHasHomeCorner(footballMatchEventStatusVo.isHasHomeCorner());
        footBallEventStatus.setHasHomeGoal(footballMatchEventStatusVo.isHasHomeGoal());
        footBallEventStatus.setHasHomeRedCard(footballMatchEventStatusVo.isHasHomeRedCard());
        footBallEventStatus.setHasHomeYellowCard(footballMatchEventStatusVo.isHasHomeYellowCard());
        footBallEventStatus.setHasHomePenalty(footballMatchEventStatusVo.isHasHomePenalty());
        footBallEventStatus.setHasHomeConfirmPenalty(footballMatchEventStatusVo.isHasHomeConfirmPenalty());
        footBallEventStatus.setHasHomeThrowIn(footballMatchEventStatusVo.isHasHomeThrowIn());
        footBallEventStatus.setHasHomeAttack(footballMatchEventStatusVo.isHasHomeAttack());
        footBallEventStatus.setHasHomeGoalKick(footballMatchEventStatusVo.isHasHomeGoalKick());
        footBallEventStatus.setHasHomeOffside(footballMatchEventStatusVo.isHasHomeOffside());
        footBallEventStatus.setHasHomeShotOnTarget(footballMatchEventStatusVo.isHasHomeShotOnTarget());
        footBallEventStatus.setHasHomeShotOffTarget(footballMatchEventStatusVo.isHasHomeShotOffTarget());
        footBallEventStatus.setHasHomeYellowRedCard(footballMatchEventStatusVo.isHasHomeYellowRedCard());

        footBallEventStatus.setHasVAREvent(footballMatchEventStatusVo.isHasVAREvent());
        footBallEventStatus.setHasVARGoal(footballMatchEventStatusVo.isHasVARGoal());
        footBallEventStatus.setHasVARConfirmGoal(footballMatchEventStatusVo.isHasVARConfirmGoal());
        footBallEventStatus.setHasVARPenalty(footballMatchEventStatusVo.isHasVARPenalty());
        footBallEventStatus.setHasVARConfirmPenalty(footballMatchEventStatusVo.isHasVARConfirmPenalty());
        footBallEventStatus.setHasVARRedCard(footballMatchEventStatusVo.isHasVARRedCard());
        footBallEventStatus.setHasVARConfirmRedCard(footballMatchEventStatusVo.isHasVARConfirmRedCard());

        footBallEventStatus.setHasAwayCorner(footballMatchEventStatusVo.isHasAwayCorner());
        footBallEventStatus.setHasAwayGoal(footballMatchEventStatusVo.isHasAwayGoal());
        footBallEventStatus.setHasAwayRedCard(footballMatchEventStatusVo.isHasAwayRedCard());
        footBallEventStatus.setHasAwayYellowCard(footballMatchEventStatusVo.isHasAwayYellowCard());
        footBallEventStatus.setHasAwayPenalty(footballMatchEventStatusVo.isHasAwayPenalty());
        footBallEventStatus.setHasAwayConfirmPenalty(footballMatchEventStatusVo.isHasAwayConfirmPenalty());
        footBallEventStatus.setHasAwayThrowIn(footballMatchEventStatusVo.isHasAwayThrowIn());
        footBallEventStatus.setHasAwayAttack(footballMatchEventStatusVo.isHasAwayAttack());
        footBallEventStatus.setHasAwayGoalKick(footballMatchEventStatusVo.isHasAwayGoalKick());
        footBallEventStatus.setHasAwayOffside(footballMatchEventStatusVo.isHasAwayOffside());
        footBallEventStatus.setHasAwayShotOnTarget(footballMatchEventStatusVo.isHasAwayShotOnTarget());
        footBallEventStatus.setHasAwayShotOffTarget(footballMatchEventStatusVo.isHasAwayShotOffTarget());
        footBallEventStatus.setHasAwayYellowRedCard(footballMatchEventStatusVo.isHasAwayYellowRedCard());
        footBallEventStatus.setHasHomeFreeKick(footballMatchEventStatusVo.isHasHomeFreeKick());
        footBallEventStatus.setHasAwayFreeKick(footballMatchEventStatusVo.isHasAwayFreeKick());

        pdMatchAdvertiseVo.setFootBallEventStatus(footBallEventStatus);
        return pdMatchAdvertiseVo;
    }

    //5分钟 阶段计算
    public static Long get5MinPeriod(Long period,Long secondStart){
        //开场-4:59
        if(period==6&&secondStart<60*5){
            return 6005L;
        }
        //5:00 - 9:59
        if(period==6&&secondStart>=60*5&&secondStart<60*10){
            return 6010L;
        }
        //10:00 - 14:59
        if(period==6&&secondStart>=60*10&&secondStart<60*15){
            return 6015L;
        }
        //15:00 - 19:59
        if(period==6&&secondStart>=60*15&&secondStart<60*20){
            return 6020L;
        }
        //20:00 - 24:59
        if(period==6&&secondStart>=60*20&&secondStart<60*25){
            return 6025L;
        }
        //25:00 - 29:59
        if(period==6&&secondStart>=60*25&&secondStart<60*30){
            return 6030L;
        }
        //30:00 - 34:59
        if(period==6&&secondStart>=60*30&&secondStart<60*35){
            return 6035L;
        }
        //35:00 - 39:59
        if(period==6&&secondStart>=60*35&&secondStart<60*40){
            return 6040L;
        }
        //40:00 - 45:00
        if(period==6&&secondStart>=60*40&&secondStart<60*45){
            return 6045L;
        }
        //1H Last-minute Goal
        if(period==6&&secondStart>60*45){
            return 6050L;
        }

        //下半场- 49:59
        if(period==7&&secondStart<60*50){
            return 7050L;
        }
        //50:00 - 54:59
        if(period==7&&secondStart>=60*50&&secondStart<60*55){
            return 7055L;
        }
        //55:00 - 59:59
        if(period==7&&secondStart>=60*55&&secondStart<60*60){
            return 7060L;
        }
        //60:00 - 64:59
        if(period==7&&secondStart>=60*60&&secondStart<60*65){
            return 7065L;
        }
        //65:00 - 69:59
        if(period==7&&secondStart>=60*65&&secondStart<60*70){
            return 7070L;
        }
        //70:00 - 74:59
        if(period==7&&secondStart>=60*70&&secondStart<60*75){
            return 7075L;
        }
        //75:00 - 79:59
        if(period==7&&secondStart>=60*75&&secondStart<60*80){
            return 7080L;
        }
        //80:00 - 84:59
        if(period==7&&secondStart>=60*80&&secondStart<60*85){
            return 7085L;
        }
        //85:00 - 90:00
        if(period==7&&secondStart>=60*85&&secondStart<60*90){
            return 7090L;
        }
        //2H Last-minute Goal
        if(period==7&&secondStart>60*90){
            return 7095L;
        }

        return null;
    }



}
