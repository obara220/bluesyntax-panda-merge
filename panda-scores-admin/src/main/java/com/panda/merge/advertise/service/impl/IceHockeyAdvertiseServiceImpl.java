package com.panda.merge.advertise.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.advertise.dto.*;
import com.panda.merge.advertise.event.CommonEventService;
import com.panda.merge.advertise.event.IceHockeyEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.IceHockeyAdvertiseService;
import com.panda.merge.advertise.service.IceHockeyScoreService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.dto.*;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.dto.advertise.EditFaScoreDto;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.utils.ScoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Slf4j
@Service
public class IceHockeyAdvertiseServiceImpl implements IceHockeyAdvertiseService {

    @Autowired
    private IceHockeyScoreService iceHockeyScoreService;

    @Autowired
    private IceHockeyEventService iceHockeyEventService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    ScoresProducer scoresProducer;

    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;
    @Autowired
    MatchTimeInfoMapper matchTimeInfoMapper;

    @Autowired
    private ScoreUtils scoreUtils;

    @Autowired
    CommonEventService commonEventService;

    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;

    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Override
    public Response changeMatchPeriod(MatchScoreAndTimeVo data, Long periodId, String linkId,String userName) {
        log.info("::{}::冰球变更比分阶段IceHockeyAdvertiseServiceImpl入参; MatchScoreAndTimeVo:{}, periodId:{}", linkId, JSON.toJSONString(data), periodId);
        Long currentTime = System.currentTimeMillis();
        // 查询阶段比分
        MatchScoreCommonVo matchScoreCommonVo = iceHockeyScoreService.searchCommonMatchScore(data.getMatchScoresInfo(), periodId);
//        if(periodId.equals(32l)){
//            commonEventService.changeMatchPeriodEvent(data,100l,0L,0L, currentTime, matchScoreCommonVo,linkId + "_PD");
//            try {
//                Thread.sleep(700);
//            } catch (InterruptedException e) {
//                
//            }
//        }
        Long remainTime =0l;
        if(periodId.equals(1l)||periodId.equals(2l)||periodId.equals(3l)||periodId.equals(0l)||periodId.equals(301l)||periodId.equals(302l)){
            remainTime=60*20l;
        }else if(periodId.equals(40L)||periodId.equals(100L)){
            remainTime=60*20L;
        }
//        if(periodId.equals(100l)){
//            periodId=32L;
//        }
        // 下发阶段事件
        commonEventService.changeMatchPeriodEvent(data,periodId,0L,remainTime, currentTime, matchScoreCommonVo, linkId,userName);
        if(periodId.equals(100l)){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                
            }
            commonEventService.changeMatchPeriodEvent(data,32L,0L,remainTime, currentTime, matchScoreCommonVo, linkId,userName);
        }
        //任何直接切换阶段 时间都不能开始
        data.getMatchTimeInfo().setTimeGo(0);
        matchTimeInfoMapper.updateByPrimaryKey(  data.getMatchTimeInfo());
        return Response.success();
    }

    @Override
    public Response changeMatchScore(String linkId, MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {
//        Long periodNow = data.getMatchTimeInfo().getPeriod();
//        if(periodNow.equals(changeMatchScoreDto.getPeriod())){
            // 当前阶段的比分变更
            return changeNowPeriodMatchScore(linkId, data,changeMatchScoreDto);
//        }else {
//            // 历史阶段的比分变更
//            return changePastPeriodMatchScore(linkId, data,changeMatchScoreDto);
//        }
    }

    /**
     * 当前阶段的比分变更
     * @param data
     * @param changeMatchScoreDto
     * @return
     */
    private Response changeNowPeriodMatchScore(String linkId, MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {
        // 1.是否比分修正事件
        boolean isDeleteEvent = iceHockeyScoreService.checkScoreChangeDelete(linkId, data.getMatchScoresInfo(),changeMatchScoreDto);
        // 2.计算下发的事件时间 当前倒计时
        Long startTimeSecond = changeMatchScoreDto.getMatchTime();
        // 3.计算总比分、阶段比分
        MatchScoreCommonVo matchScoreCommonVo = iceHockeyScoreService.countScore(linkId, data, changeMatchScoreDto);
        if(isDeleteEvent){
            // 4.比分修正事件
            iceHockeyEventService.addScoreChangeEvent(data,matchScoreCommonVo,startTimeSecond,changeMatchScoreDto.getPeriod(),changeMatchScoreDto.getLinkedId(),changeMatchScoreDto.getOperatorName());
            // 5.修正事件要冻结结算
//            eventProducer.sendFreezeSettle(changeMatchScoreDto,data.getThirdMatchInfo().getReferenceId(),data.getThirdMatchInfo().getSportId());
        } else {
            // 6.不是比分修正事件
//            eventProducer.sendFreezeSettle(changeMatchScoreDto,data.getThirdMatchInfo().getReferenceId(),data.getThirdMatchInfo().getSportId());
            iceHockeyEventService.addScoreChangeEvent(data,matchScoreCommonVo,startTimeSecond,changeMatchScoreDto.getPeriod(),changeMatchScoreDto.getLinkedId(),changeMatchScoreDto.getOperatorName());
        }
        // 7.下发比分变更事件  或者比分修正事件
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),changeMatchScoreDto.getLinkedId());
        // 冰球保存时下发比分
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createMatchTimeEvent(data, data.getMatchTimeInfo().getSecondFromStart(),
                data.getMatchTimeInfo().getSecondFromStart(), System.currentTimeMillis(), 1, changeMatchScoreDto.getPeriod(), linkId + "_PD");
        matchEventInfoDTO.setEventCode("goal");
        Map<Long, IceHockeyScores> scoresMap = JSON.parseObject(data.getMatchScoresInfo().getScoresJson(), new TypeReference<Map<Long, IceHockeyScores>>() {
        });
        IceHockeyScores periodScores = scoresMap.get(changeMatchScoreDto.getPeriod());
        matchEventInfoDTO.setT1(data.getMatchScoresInfo().getT1());
        matchEventInfoDTO.setT2(data.getMatchScoresInfo().getT2());
        matchEventInfoDTO.setFirstT1(data.getMatchScoresInfo().getPeriodT1());
        matchEventInfoDTO.setFirstT2(data.getMatchScoresInfo().getPeriodT2());
        matchEventInfoDTO.setSecondT1(periodScores.getMatchScore().getHome());
        matchEventInfoDTO.setSecondT2(periodScores.getMatchScore().getAway());
        matchEventInfoDTO.setCopyLinkId(linkId + "_PD");
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        return Response.success();
    }

    /**
     * 历史阶段的比分的变更
     * @param data
     * @param changeMatchScoreDto
     * @return
     */
    private Response changePastPeriodMatchScore(String linkId, MatchScoreAndTimeVo data, ChangeMatchScoreDto changeMatchScoreDto) {
        // 1.事件时间(当前倒计时)
        Long startTimeSecond = changeMatchScoreDto.getMatchTime();
        // 2.计算总比分、阶段比分
        MatchScoreCommonVo matchScoreCommonVo = iceHockeyScoreService.countScore(linkId, data, changeMatchScoreDto);
        iceHockeyEventService.addScoreCorrectEvent(data,matchScoreCommonVo,startTimeSecond,changeMatchScoreDto.getPeriod(),changeMatchScoreDto.getLinkedId(),changeMatchScoreDto.getOperatorName());
        // 3.修正事件要冻结结算
        eventProducer.sendFreezeSettle(changeMatchScoreDto,data.getThirdMatchInfo().getReferenceId(),data.getThirdMatchInfo().getSportId());
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),changeMatchScoreDto.getLinkedId());
        return Response.success();
    }

    @Override
    public Response buildIceHockeyAdvertiseVo(MatchScoreAndTimeVo data) {
        ThirdMatchInfo thirdMatchInfo =data.getThirdMatchInfo();
        MatchTimeInfo matchTimeInfo =data.getMatchTimeInfo();
        MatchScoresInfo matchScoresInfo =data.getMatchScoresInfo();
        PDMatchAdvertiseVo pdMatchAdvertiseVo=new PDMatchAdvertiseVo();
        pdMatchAdvertiseVo.setThirdMatchId(String.valueOf(thirdMatchInfo.getId()));
        pdMatchAdvertiseVo.setStandardMatchId(thirdMatchInfo.getReferenceId());
        pdMatchAdvertiseVo.setEventTime(matchTimeInfo.getEventTime());
        pdMatchAdvertiseVo.setMatchBeginTime(thirdMatchInfo.getBeginTime());
        pdMatchAdvertiseVo.setIsGo(matchTimeInfo.getTimeGo()+"");
        pdMatchAdvertiseVo.setSysTime(System.currentTimeMillis());
        //非开赛阶段 0
//        if( matchTimeInfo.getPeriod() == 0L ){
//            pdMatchAdvertiseVo.setIsGo("0");
//        }
        pdMatchAdvertiseVo.setPeriod(matchTimeInfo.getPeriod());
        pdMatchAdvertiseVo.setMatchLength(matchTimeInfo.getMatchLength());
        //时间计算
        //1.计算时间 当前剩余秒 = 上次倒计时- (现在系统时间-上次事件时间)/1000
        Long startTimeSecond= matchTimeInfo.getRemainingTime()-(System.currentTimeMillis()-matchTimeInfo.getEventTime())/1000;
        //2.
        if(startTimeSecond<0l){
            startTimeSecond=0l;
        }
        pdMatchAdvertiseVo.setMatchTime(startTimeSecond);

        //比分计算
        IceHockeyScoreVo iceHockeyScoreVo = this.transforScore(matchScoresInfo);
        pdMatchAdvertiseVo.setIceHockeyScore(iceHockeyScoreVo);
        List<Long> periodLists = Arrays.asList( 1L, 2L, 3L, 40L,50L);
        if(!periodLists.contains(pdMatchAdvertiseVo.getPeriod())){
            pdMatchAdvertiseVo.setIsGo("0");
        }else {
//            if(pdMatchAdvertiseVo.getIsGo().equals("0")){
//                pdMatchAdvertiseVo.setMatchTime(matchTimeInfo.getRemainingTime());
//            }
        }
        if(pdMatchAdvertiseVo.getIsGo().equals("0")){
            pdMatchAdvertiseVo.setMatchTime(matchTimeInfo.getRemainingTime());
        }
        if(pdMatchAdvertiseVo.getPeriod()<=0){
            pdMatchAdvertiseVo.setMatchTime(60*20l);
        }
        return Response.success(pdMatchAdvertiseVo);
    }

    @Override
    public Response matchStart(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId) {
        Long currentTime = System.currentTimeMillis();
        Long currentPeriod = matchScoreAndTimeVo.getMatchTimeInfo().getPeriod();
//        Integer currentMatchLength = matchScoreAndTimeVo.getMatchTimeInfo().getMatchLength();
        // 1.计算阶段
        List<Long> periodLists = Arrays.asList(0L, 1L,301L, 2L,302L, 3L,100L,32L, 40L, 110L,50L,120L, 999L);
        if (!periodLists.contains(currentPeriod)) {
            return Response.failed("当前阶段不允许调整赛事状态!");
        }

        Long nextPeriod = periodLists.get(periodLists.indexOf(currentPeriod) + 1) ;
        Long startTimeSecond = 0L;
        // 3.加时赛倒计时
            Long remainTime =0l;
            if(nextPeriod.equals(1l)||nextPeriod.equals(2l)||nextPeriod.equals(3l)){
                remainTime=60*20l;
            }else if(nextPeriod.equals(40L)){
                remainTime=60*20L;
            }
        startTimeSecond=remainTime;
        // 4.查询阶段比分
        MatchScoreCommonVo matchScoreCommonVo= iceHockeyScoreService.searchCommonMatchScore(matchScoreAndTimeVo.getMatchScoresInfo(), nextPeriod);
        // 5.下发阶段事件
        commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo, nextPeriod, startTimeSecond, startTimeSecond, currentTime, matchScoreCommonVo, linkId,"");
        String oddsDataSourceCode ="PD";
        StandardSportMarketSellExample example= new StandardSportMarketSellExample();
        example.createCriteria().andMatchInfoIdEqualTo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
        List<StandardSportMarketSell> list= standardSportMarketSellMapper.selectByExample(example);

        if(list.size()!=0){
            StandardSportMarketSell standardSportMarketSell=list.get(0);
            oddsDataSourceCode=standardSportMarketSell.getMatchStatusSourceCode();
            ThirdMatchInfoExample example2= new ThirdMatchInfoExample();
            example2.createCriteria().andReferenceIdEqualTo(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId()).andDataSourceCodeEqualTo(oddsDataSourceCode);
            List<ThirdMatchInfo> thirdMatchInfo= thirdMatchInfoMapper.selectByExample(example2);
            if(thirdMatchInfo.size()!=0){
                eventProducer.sendMatchStartStatus(thirdMatchInfo.get(0), linkId);
            }
        }
        try {
            Thread.sleep(200);
            MatchEventInfoDTO matchEventInfoDTO= MatchEventUtils.createMatchTimeEvent(matchScoreAndTimeVo, startTimeSecond, startTimeSecond, currentTime,1,nextPeriod,linkId + "_PD");
            // 6.发送MQ且记录事件
            eventProducer.sendPDEventInfo(matchEventInfoDTO);
        } catch (InterruptedException e) {
            
        }
        matchScoreAndTimeVo.getMatchTimeInfo().setTimeGo(1);
        matchTimeInfoMapper.updateByPrimaryKey(  matchScoreAndTimeVo.getMatchTimeInfo());
        return Response.success();
    }

    @Override
    public Response matchPause(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId) {
        //1.计算时间 当前剩余秒 = 上次倒计时- (现在系统时间-上次事件时间)/1000
        Long startTimeSecond =  matchScoreAndTimeVo.getMatchTimeInfo().getSecondFromStart()-(System.currentTimeMillis()- matchScoreAndTimeVo.getMatchTimeInfo().getEventTime())/1000;
        //2.下发时间暂停
        commonEventService.updateMatchTimeEvent(matchScoreAndTimeVo, matchScoreAndTimeVo.getMatchTimeInfo().getPeriod()
                , startTimeSecond, startTimeSecond, System.currentTimeMillis(),0, linkId);

        // 下发暂停
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createMatchTimeEvent(matchScoreAndTimeVo, startTimeSecond, startTimeSecond, System.currentTimeMillis(),0, matchScoreAndTimeVo.getMatchTimeInfo().getPeriod(),linkId + "_PD");
        matchEventInfoDTO.setEventCode("timeout");
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        return Response.success();
    }

    @Override
    public Response matchContinue(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId) {
        commonEventService.updateMatchTimeEvent(matchScoreAndTimeVo, matchScoreAndTimeVo.getMatchTimeInfo().getPeriod()
                ,matchScoreAndTimeVo.getMatchTimeInfo().getSecondFromStart(), matchScoreAndTimeVo.getMatchTimeInfo().getSecondFromStart(), System.currentTimeMillis(),1, linkId);

        // 下发暂停结束
        MatchEventInfoDTO matchEventInfoDTO = MatchEventUtils.createMatchTimeEvent(matchScoreAndTimeVo, matchScoreAndTimeVo.getMatchTimeInfo().getSecondFromStart(),
                matchScoreAndTimeVo.getMatchTimeInfo().getSecondFromStart(), System.currentTimeMillis(),1, matchScoreAndTimeVo.getMatchTimeInfo().getPeriod(),linkId + "_PD");
        matchEventInfoDTO.setEventCode("timeout_over");
        eventProducer.sendPDEventInfo(matchEventInfoDTO);
        return Response.success();
    }

    @Override
    public Response matchEnd(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId) {
        //1.查询阶段
        Long nextPeriod= MatchPeriodUtils.BascketBallPeriod.getNextPeriod(matchScoreAndTimeVo.getMatchTimeInfo().getPeriod(), matchScoreAndTimeVo.getMatchTimeInfo().getMatchLength());
        //3.查询阶段比分
        MatchScoreCommonVo matchScoreCommonVo= iceHockeyScoreService.searchCommonMatchScore(matchScoreAndTimeVo.getMatchScoresInfo(), matchScoreAndTimeVo.getMatchScoresInfo().getPeriod());
        //4.下发阶段事件
        commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo,nextPeriod,0L,0L, System.currentTimeMillis(), matchScoreCommonVo, linkId,"");
        return Response.success();
    }


    private IceHockeyScoreVo transforScore(MatchScoresInfo matchScoresInfo) {
        IceHockeyScoreVo iceHockeyScoreVo = new IceHockeyScoreVo();
        log.info("{},报球板冰球比分1:{}",matchScoresInfo.getThirdMatchId(),matchScoresInfo.getScoresJson());
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson()) ) {
            Map<Long, IceHockeyScores> periodIceHockeyScores = new HashMap<>();
            IceHockeyScores iceHockeyScores = new IceHockeyScores(0l);
            periodIceHockeyScores.put(WHOLE_MATCH, iceHockeyScores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodIceHockeyScores));
            matchScoresInfo.setT1(iceHockeyScores.getMatchScore().getHome());
            matchScoresInfo.setT2(iceHockeyScores.getMatchScore().getAway());
            matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
        }
        log.info("{},报球板冰球比分2:{}",matchScoresInfo.getThirdMatchId(),matchScoresInfo.getScoresJson());
        Map<Long, IceHockeyScores> allPeriodScores = scoreUtils.periodJson( matchScoresInfo.getScoresJson(), IceHockeyScores.class);
        IceHockeyScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        log.info("{},报球板冰球比分3,-1比分:{}",matchScoresInfo.getThirdMatchId(),wholeSores);
        //whole
        if(wholeSores!=null){
            iceHockeyScoreVo.setWhole(wholeSores.getMatchScore());
        }

        IceHockeyScores Q1 = allPeriodScores.get(1L);
        IceHockeyScores Q2 = allPeriodScores.get(2L);
        IceHockeyScores Q3 = allPeriodScores.get(3L);
        IceHockeyScores ET = allPeriodScores.get(40L);
        IceHockeyScores PEN = allPeriodScores.get(50L);
        if(Q1!=null){
            iceHockeyScoreVo.setQ1(Q1.getMatchScore());
        }
        if(Q2!=null) {
            iceHockeyScoreVo.setQ2(Q2.getMatchScore());
        }
        if(Q3!=null) {
            iceHockeyScoreVo.setQ3(Q3.getMatchScore());
        }
        if(ET!=null) {
            iceHockeyScoreVo.setET(ET.getMatchScore());
        }
        if(PEN!=null) {
            iceHockeyScoreVo.setPEN(PEN.getMatchScore());
        }
        iceHockeyScoreVo.setBigFa(wholeSores.getSuspensionBig());
        iceHockeyScoreVo.setSmallFa(wholeSores.getSuspensionSmall());
        log.info("{},报球板冰球比分4,冰球比分:{}",matchScoresInfo.getThirdMatchId(),iceHockeyScoreVo);
        return iceHockeyScoreVo;
    }

    @Override
    public Response match999End(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId) {
        Long currentTime = System.currentTimeMillis();
        MatchScoreCommonVo matchScoreCommonVo = iceHockeyScoreService.searchCommonMatchScore(matchScoreAndTimeVo.getMatchScoresInfo(),999L);
        //如果有点球大战，则结束的时候给总比分加1X
//        checkPenAndScore(matchScoreAndTimeVo,linkId);
        //4.下发阶段事件
        if( 40L == matchScoreAndTimeVo.getMatchScoresInfo().getPeriod()){
            commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo, 110l, 0L, 0L, currentTime, matchScoreCommonVo, linkId + "_PD" ,"");
        }else if(50L==matchScoreAndTimeVo.getMatchScoresInfo().getPeriod()){
            commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo, 120l, 0L, 0L, currentTime, matchScoreCommonVo, linkId + "_PD" ,"");
        }else if(3l==matchScoreAndTimeVo.getMatchScoresInfo().getPeriod()){
            commonEventService.changeMatchPeriodEvent(matchScoreAndTimeVo, 100l, 0L, 0L, currentTime, matchScoreCommonVo, linkId + "_PD" ,"");
        }
        StandardMatchInfo standardMatchInfo =standardMatchInfoMapper.selectByPrimaryKey(matchScoreAndTimeVo.getThirdMatchInfo().getReferenceId());
        if(standardMatchInfo!=null){
            standardMatchInfo.setMatchStatus(3);
            standardMatchInfo.setModifyTime(System.currentTimeMillis());
            matchScoreAndTimeVo.getThirdMatchInfo().setMatchStatus(3);
            matchScoreAndTimeVo.getThirdMatchInfo().setModifyTime(System.currentTimeMillis());
            thirdMatchInfoMapper.updateByPrimaryKey( matchScoreAndTimeVo.getThirdMatchInfo());
            standardMatchInfoMapper.updateByPrimaryKey(standardMatchInfo);
        }
        //2.下发状态变更
        eventProducer.sendMatchStatusTopic(linkId, matchScoreAndTimeVo.getThirdMatchInfo(), 3);
        commonEventService.changeMatchPeriodEvent( matchScoreAndTimeVo, 999l, 0L, 0L, currentTime, matchScoreCommonVo, linkId,"");
        return Response.success();
    }

    private void checkPenAndScore(MatchScoreAndTimeVo matchScoreAndTimeVo, String linkId) {
        Map<Long, IceHockeyScores> allPeriodScores = scoreUtils.periodJson( matchScoreAndTimeVo.getMatchScoresInfo().getScoresJson(), IceHockeyScores.class);
        IceHockeyScores penScore= allPeriodScores.get(50L);
        if(penScore!=null){
            IceHockeyScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
            if(penScore.getMatchScore().getHome()>penScore.getMatchScore().getAway()){
                wholeSores.getMatchScore().setHome(wholeSores.getMatchScore().getHome()+1);
            }else {
                wholeSores.getMatchScore().setAway(wholeSores.getMatchScore().getAway()+1);
            }
        }
        matchScoreAndTimeVo.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfoMapper.updateByPrimaryKey(  matchScoreAndTimeVo.getMatchScoresInfo());
        scoresProducer.sendToMQ(matchScoreAndTimeVo.getThirdMatchInfo(),matchScoreAndTimeVo.getMatchScoresInfo(),linkId);
    }

    @Override
    public Response editFaScore(MatchScoreAndTimeVo data, EditFaScoreDto editFaScoreDto) {
        if(StringUtils.isEmpty(data.getMatchScoresInfo().getScoresJson())){
            return Response.failed("比分为空");
        }
        Map<Long, IceHockeyScores> allPeriodScores = scoreUtils.periodJson( data.getMatchScoresInfo().getScoresJson(), IceHockeyScores.class);
        IceHockeyScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(wholeSores==null){
            return Response.failed("比分为空");
        }
        wholeSores.getSuspensionBig().setHome(editFaScoreDto.getBigFaT1());
        wholeSores.getSuspensionBig().setAway(editFaScoreDto.getBigFaT2());
        wholeSores.getSuspensionSmall().setHome(editFaScoreDto.getSmallFaT1());
        wholeSores.getSuspensionSmall().setAway(editFaScoreDto.getSmallFaT2());
        data.getMatchScoresInfo().setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfoMapper.updateByPrimaryKey(  data.getMatchScoresInfo());
        //下发比分
        scoresProducer.sendToMQ(data.getThirdMatchInfo(),data.getMatchScoresInfo(),editFaScoreDto.getLinkedId());
        return Response.success();
    }
}
