package com.panda.merge.utils;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.MatchSettleEventCompareDto;
import com.panda.merge.dto.MatchSettleEventFiveMinCompareDto;
import com.panda.merge.dto.settle.MatchSettleEventDto;
import com.panda.merge.dto.settle.MatchSettleEventExtryInfoDto;
import com.panda.merge.dto.settle.MatchSettleScoreDto;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchSettleEvent;
import com.panda.merge.model.MatchSettleScore;
import com.panda.merge.v2.entity.MatchSettleScoreEntity;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MatchEventInfoSettleUtils {
    public static Integer  doCountEventScore(  Integer order,MatchSettleEvent matchSettleEvent, MatchEventInfo data, List<MatchEventInfo> eventInfos) {
        boolean flag= true;
        for (MatchEventInfo eventInfo : eventInfos) {
            if(eventInfo.getId().equals(data.getId())){
                flag=false;
            }
        }
        if(flag){
            order++;
            eventInfos.add(data);
        }
        if(data.getEventCode().equals("goal")||data.getEventCode().equals("penalty_missed")){
            doCountGoal(matchSettleEvent,data,eventInfos);
        }else if(data.getEventCode().equals("corner")){
            doCountCorner(matchSettleEvent,data,eventInfos);
        }else if(data.getEventCode().equals("red_card")||data.getEventCode().equals("yellow_card")){
            doCountFacard(matchSettleEvent,data,eventInfos);
        }
        return order;
    }

    private static void doCountFacard(MatchSettleEvent matchSettleEvent, MatchEventInfo data, List<MatchEventInfo> eventInfos) {
        matchSettleEvent.setEventCode(data.getEventCode());
        Integer t1 =0,t2=0,firstT1=0,firstT2=0,secondT1=0,secondT2=0;
        for (MatchEventInfo eventInfo : eventInfos) {
            if(eventInfo.getEventCode().equals("red_card")){
                if(eventInfo.getHomeAway().equals("home")){
                    t1+=2;
                    secondT1++;
                }else {
                    t2+=2;
                    secondT2++;
                }
            }else {
                if(eventInfo.getHomeAway().equals("home")){
                    t1+=1;
                    firstT1++;
                }else {
                    t2+=1;
                    firstT2++;
                }
            }
        }
        matchSettleEvent.setT1(t1);matchSettleEvent.setT2(t2);
        matchSettleEvent.setFirstT1(firstT1);matchSettleEvent.setFirstT2(firstT2);
        matchSettleEvent.setSecondT1(secondT1);matchSettleEvent.setSecondT2(secondT2);
    }

    private static void doCountCorner(MatchSettleEvent matchSettleEvent, MatchEventInfo data, List<MatchEventInfo> eventInfos) {
        matchSettleEvent.setEventCode("corner");
        Integer t1=0,t2=0;
        for (MatchEventInfo eventInfo : eventInfos) {
            if(eventInfo.getHomeAway().equals("home")){
                t1++;
            }else {
                t2++;
            }
        }
        matchSettleEvent.setT1(t1);
        matchSettleEvent.setT2(t2);
    }

    private static void doCountGoal(MatchSettleEvent matchSettleEvent, MatchEventInfo data, List<MatchEventInfo> eventInfos) {
        matchSettleEvent.setEventCode("goal");
        if(data.getEventCode().equals("penalty_missed")){
            data.setExtraInfo("0");
        }else {
            data.setExtraInfo("1");
        }
        Integer t1=0,t2=0;
        for (MatchEventInfo eventInfo : eventInfos) {
            if(eventInfo.getHomeAway().equals("home")){
                t1++;
            }else {
                t2++;
            }
        }
        matchSettleEvent.setT2(t2);
        matchSettleEvent.setT1(t1);

    }

    public  static  boolean  equileMatchSettleEvent(MatchSettleEvent before,MatchSettleEvent after){
        //有 5分钟区间所以编辑的时候必须判断是否相同的 5分钟区间而不只是 单单判断比分
        MatchSettleEventFiveMinCompareDto eventBefore =new MatchSettleEventFiveMinCompareDto();
        MatchSettleEventFiveMinCompareDto eventAfter =new MatchSettleEventFiveMinCompareDto();
        BeanUtils.copyProperties(before,eventBefore);
        BeanUtils.copyProperties(after,eventAfter);
        JSONObject jsonBefore =(JSONObject) JSONObject.toJSON(eventBefore);
        JSONObject jsonAfter =(JSONObject) JSONObject.toJSON(eventAfter);
        return jsonBefore.toJSONString().equals(jsonAfter.toJSONString());
    }

    public  static  boolean  equileMatchSettleScores(MatchSettleScore before, MatchSettleScore after){
        MatchSettleEventCompareDto eventBefore =new MatchSettleEventCompareDto();
        MatchSettleEventCompareDto eventAfter =new MatchSettleEventCompareDto();
        BeanUtils.copyProperties(before,eventBefore);
        BeanUtils.copyProperties(after,eventAfter);
        JSONObject jsonBefore =(JSONObject) JSONObject.toJSON(eventBefore);
        JSONObject jsonAfter =(JSONObject) JSONObject.toJSON(eventAfter);
        return jsonBefore.toJSONString().equals(jsonAfter.toJSONString());
    }

    public  static  boolean  equileMatchSettleScoresV2(MatchSettleScore before, MatchSettleScore after){
        MatchSettleEventCompareDto eventBefore =new MatchSettleEventCompareDto();
        MatchSettleEventCompareDto eventAfter =new MatchSettleEventCompareDto();
        BeanUtils.copyProperties(before,eventBefore);
        BeanUtils.copyProperties(after,eventAfter);
        JSONObject jsonBefore =(JSONObject) JSONObject.toJSON(eventBefore);
        JSONObject jsonAfter =(JSONObject) JSONObject.toJSON(eventAfter);
        return jsonBefore.toJSONString().equals(jsonAfter.toJSONString());
    }

    /**
     * 计算比分唯一性
     * */
    public static void checkInfoKey(MatchSettleScoreDto matchSettleScoreDto) {
        //比分= settleNum
        matchSettleScoreDto.setKey(matchSettleScoreDto.getSettleNum().toString());
    }
    /**
     * 计算事件唯一性
     * */
    public static void checkInfoKey(MatchSettleEventDto matchSettleEventDto) {
        //事件= settleNum + eventOrder
        matchSettleEventDto.setKey(matchSettleEventDto.getPeriodId()+":"+matchSettleEventDto.getSettleNum()+":"+matchSettleEventDto.getEventOrder()+":"+matchSettleEventDto.getEventType());
    }

//    public static void main(String[] XX){
//        MatchSettleEvent before= new MatchSettleEvent();
//
//        MatchSettleEvent after =new MatchSettleEvent();
//        before.setT1(1);
//        after.setT1(2);
//        System.out.println(equileMatchSettleEvent(before,after));
//    }
}
