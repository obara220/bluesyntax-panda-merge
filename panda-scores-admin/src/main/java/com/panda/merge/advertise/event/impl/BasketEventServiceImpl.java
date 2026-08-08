package com.panda.merge.advertise.event.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.advertise.common.Constant;
import com.panda.merge.advertise.dto.MatchScoreAndTimeVo;
import com.panda.merge.advertise.dto.MatchScoreCommonVo;
import com.panda.merge.advertise.event.BasketEventService;
import com.panda.merge.advertise.mq.EventProducer;
import com.panda.merge.advertise.service.BasketBallScoreService;
import com.panda.merge.advertise.utils.MatchEventUtils;
import com.panda.merge.advertise.utils.MatchPeriodUtils;
import com.panda.merge.common.enums.PDScoreChangeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.advertise.ChangeMatchScoreDto;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Service
@Slf4j
public class BasketEventServiceImpl implements BasketEventService {
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private BasketBallScoreService basketBallScoreService;

    /**
     * 篮球比分变更事件
     * */
    @Override
    public int addScoreChangeEvent(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, ChangeMatchScoreDto changeMatchScoreDto) {
        Long startTimeSecond = changeMatchScoreDto.getMatchTime();
        Long period = changeMatchScoreDto.getPeriod();
        String linkedId = changeMatchScoreDto.getLinkedId();
        String remark = changeMatchScoreDto.getOperatorName();
        //1.事件转化专用方法
        MatchEventInfoDTO eventInfoDTO= MatchEventUtils.createMatchScoreEvent("score_change",data.getThirdMatchInfo(),matchScoreCommonVo,startTimeSecond,period,linkedId, remark);
        //2.经过比分中心计算统计
        int status = basketBallScoreService.updateScore(data,matchScoreCommonVo,changeMatchScoreDto);
        if (status == PDScoreChangeEnum.NUMBER_LESS_ZERO.getCode()) {
            return PDScoreChangeEnum.NUMBER_LESS_ZERO.getCode();
        }
        //如果当前比分变更才会下发
//        if(data.getMatchTimeInfo().getPeriod().equals(eventInfoDTO.getMatchPeriodId())){
//        }
        eventProducer.updateMatchScoresEventInfo(eventInfoDTO);
        Integer addT1 = matchScoreCommonVo.getAddT1();
        Integer addT2 = matchScoreCommonVo.getAddT2();
        if (null != addT1 && addT1 != 0) {
            eventInfoDTO.setExtrainfo(addT1 + "");
        }
        if (null != addT2 && addT2 != 0) {
            eventInfoDTO.setExtrainfo(addT2 + "");
        }
        if (null != addT1 && addT1 != 0 && null != addT2 && addT2 != 0) {
            eventInfoDTO.setExtrainfo(addT1 + "&" + addT2);
        }
        //4.下发MQ给实时服务
        log.info("::{}::PD篮球报球板比分-下发实时服务-计算时优先取缓存阶段比分,period={},入参阶段={}", changeMatchScoreDto.getThirdMatchId(), data.getMatchScoresInfo().getPeriod(),changeMatchScoreDto.getPeriod());
        if (period.equals(data.getMatchScoresInfo().getPeriod())) {
            eventProducer.sendPDBasketballEditEventInfo(eventInfoDTO);
        }
        return PDScoreChangeEnum.OPERATE_NORMAL.getCode();
    }
    /**
     * 篮球比分修正事件下发
     * */
    @Override
    public void addScoreCorrectEvent(MatchScoreAndTimeVo data, MatchScoreCommonVo matchScoreCommonVo, ChangeMatchScoreDto changeMatchScoreDto, String remark) {

//        MatchEventInfoDTO eventInfoDTO= MatchEventUtils.createMatchScoreEvent("score_change",data.getThirdMatchInfo(),matchScoreCommonVo,startTimeSecond,period,linkedId,remark);
        basketBallScoreService.updateScore(data,matchScoreCommonVo,changeMatchScoreDto);
        //1.求出阶段差 下发list事件

//        List<MatchEventInfoDTO> list= createListEvent(data.getMatchScoresInfo());
//        for (MatchEventInfoDTO matchEventInfoDTO : list) {
//            eventProducer.sendPDEventInfo(matchEventInfoDTO);
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e) {
//                
//            }
//        }
        //2.阶段比分变更拼接
//        eventInfoDTO.setEventCode("time_start");
        //3.依次推送事件
//        eventProducer.sendPDEventInfo(eventInfoDTO);
//        List<MatchEventInfoDTO> list=createListEvent( data);
//        for (MatchEventInfoDTO matchEventInfoDTO : list) {
//            eventProducer.sendPDEventInfo(matchEventInfoDTO);
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                
//            }
//        }
    }

    private List<MatchEventInfoDTO> createListEvent(MatchScoreAndTimeVo data) {
        MatchScoresInfo matchScoresInfo= data.getMatchScoresInfo();
        ThirdMatchInfo thirdMatchInfo= data.getThirdMatchInfo();
        MatchTimeInfo matchTimeInfo =data.getMatchTimeInfo();

        LinkedList<MatchEventInfoDTO> list=new LinkedList<>();
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        BasketballScores wholeScore=allPeriodScores.get(WHOLE_MATCH);
        allPeriodScores.remove(WHOLE_MATCH);
        List<Map.Entry<Long, BasketballScores>> scores=new ArrayList<>();
        for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
            scores.add(entry);
        }

        Collections.sort(scores, new Comparator<Map.Entry<Long, BasketballScores>>() {
            @Override
            public int compare(Map.Entry<Long, BasketballScores> o1, Map.Entry<Long, BasketballScores> o2) {

                return o1.getKey()>o2.getKey()?1:-1;
            }
        });
        CommonItem whole_Score =new CommonItem();
        CommonItem period_Score =new CommonItem();
        for (Map.Entry<Long, BasketballScores> score : scores) {
            //0.1 计算倒计时
            Integer minuts= Constant.BasketBallConstant.matchLenthTimeMap.get(matchScoresInfo.getMatchLength());
            if(score.getKey().equals(40l)){
                minuts=5;
            }
            Long seconds=minuts*60l;
            //1.创建前置事件
            Long beforPeriod = MatchPeriodUtils.BascketBallPeriod.getBeforePeriod(score.getKey(),matchScoresInfo.getMatchLength());
            MatchEventInfoDTO beforeMatchStatusEvent= CreateSimpleEvent(matchScoresInfo,thirdMatchInfo);
            beforeMatchStatusEvent.setMatchPeriodId(beforPeriod);
            beforeMatchStatusEvent.setSecondsFromStart(seconds);
            beforeMatchStatusEvent.setEventCode("match_status");
            beforeMatchStatusEvent.setT1(whole_Score.getHome());
            beforeMatchStatusEvent.setT2(whole_Score.getAway());
            beforeMatchStatusEvent.setFirstT1(period_Score.getHome());
            beforeMatchStatusEvent.setFirstT2(period_Score.getAway());
            list.add(beforeMatchStatusEvent);
            //2.创建当前事件
            //0.2 如果是当前阶段 则跳出循环单独下发
            whole_Score.setHome(whole_Score.getHome()+score.getValue().getMatchScore().getHome());
            whole_Score.setAway(whole_Score.getAway()+score.getValue().getMatchScore().getAway());
            period_Score.setHome(score.getValue().getMatchScore().getHome());
            period_Score.setAway(score.getValue().getMatchScore().getAway());
            MatchEventInfoDTO matchScoreEvent= CreateSimpleEvent(matchScoresInfo,thirdMatchInfo);
            beforeMatchStatusEvent.setMatchPeriodId(score.getKey());
            beforeMatchStatusEvent.setSecondsFromStart(seconds);
            beforeMatchStatusEvent.setEventCode("score_change");
            beforeMatchStatusEvent.setT1(whole_Score.getHome());
            beforeMatchStatusEvent.setT2(whole_Score.getAway());
            beforeMatchStatusEvent.setFirstT1(period_Score.getHome());
            beforeMatchStatusEvent.setFirstT2(period_Score.getAway());
            list.add(matchScoreEvent);
        }
        //单独下发当前阶段
        if(!SportPeriodConstant.BasketballPeriod.contans( matchScoresInfo.getPeriod(),matchScoresInfo.getMatchLength())){
            MatchEventInfoDTO matchStatusEvent= CreateSimpleEvent(matchScoresInfo,thirdMatchInfo);
            matchStatusEvent.setMatchPeriodId(matchScoresInfo.getPeriod());
            //时间计算
            Long startTimeSecond =matchTimeInfo.getSecondFromStart();

            matchStatusEvent.setSecondsFromStart(startTimeSecond);
            matchStatusEvent.setEventCode("match_status");
            matchStatusEvent.setT1(whole_Score.getHome());
            matchStatusEvent.setT2(whole_Score.getAway());
            matchStatusEvent.setFirstT1(period_Score.getHome());
            matchStatusEvent.setFirstT2(period_Score.getAway());
            list.add(matchStatusEvent);
        }else {
            Long startTimeSecond =matchTimeInfo.getSecondFromStart();
            if(SportPeriodConstant.BasketballPeriod.contans( matchScoresInfo.getPeriod(),0)){
                startTimeSecond =  matchTimeInfo.getSecondFromStart()-(System.currentTimeMillis()- matchTimeInfo.getEventTime())/1000;
                if(matchTimeInfo.getTimeGo()==0){
                    startTimeSecond= matchTimeInfo.getSecondFromStart();
                }
            }
            MatchEventInfoDTO matchTimeEvent= CreateSimpleEvent(matchScoresInfo,thirdMatchInfo);
            matchTimeEvent.setMatchPeriodId(matchScoresInfo.getPeriod());
            matchTimeEvent.setSecondsFromStart(startTimeSecond);
            matchTimeEvent.setEventCode("time_start");
            matchTimeEvent.setT1(whole_Score.getHome());
            matchTimeEvent.setT2(whole_Score.getAway());
            matchTimeEvent.setFirstT1(period_Score.getHome());
            matchTimeEvent.setFirstT2(period_Score.getAway());
            matchTimeEvent.setExtrainfo(matchTimeInfo.getTimeGo()+"");
            list.add(matchTimeEvent);
        }

        return list;
    }

    private MatchEventInfoDTO CreateSimpleEvent(MatchScoresInfo matchScoresInfo, ThirdMatchInfo thirdMatchInfo) {
        MatchEventInfoDTO matchEventInfoDTO =new MatchEventInfoDTO();
        matchEventInfoDTO.setMatchLength(matchScoresInfo.getMatchLength());
        matchEventInfoDTO.setSourceType("1");
        matchEventInfoDTO.setDataSourceCode("PD");
        matchEventInfoDTO.setCanceled(0);
        matchEventInfoDTO.setEventTime(System.currentTimeMillis());
        matchEventInfoDTO.setSportId(matchScoresInfo.getSportId());
        matchEventInfoDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
        matchEventInfoDTO.setCopyLinkId("PD_"+UUID.randomUUID().toString());
        matchEventInfoDTO.setThirdEventId("PD_"+UUID.randomUUID().toString());
        return matchEventInfoDTO;
    }


//    public static void main(String [] arr){
//        Map<Long, BasketballScores> allPeriodScores= new HashMap<>();
//        allPeriodScores.put(13l,new BasketballScores(1l));
//        BasketballScores basketballScores =new BasketballScores(1l);
//        basketballScores.getMatchScore().setHome(10);
//        allPeriodScores.put(14l,basketballScores);
//        BasketballScores basketballScores2 =new BasketballScores(1l);
//        basketballScores2.getMatchScore().setAway(10);
//        allPeriodScores.put(15l,basketballScores2);
//        List<Map.Entry<Long, BasketballScores>> scores=new ArrayList<>();
//        for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
//            scores.add(entry);
//        }
//
//        Collections.sort(scores, new Comparator<Map.Entry<Long, BasketballScores>>() {
//            @Override
//            public int compare(Map.Entry<Long, BasketballScores> o1, Map.Entry<Long, BasketballScores> o2) {
//
//                return o1.getKey()>o2.getKey()?1:-1;
//            }
//        });
//
//        for (Map.Entry<Long, BasketballScores> score : scores) {
//            System.out.println(JSONObject.toJSONString(score));
//        }
//
//    }
}
