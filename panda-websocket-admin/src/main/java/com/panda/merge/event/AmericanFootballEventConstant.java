package com.panda.merge.event;

import com.panda.merge.model.MatchEventCommon;

import java.util.HashMap;
import java.util.Map;

public class AmericanFootballEventConstant {
    public static Map<String,String> eventNameMap=new HashMap<String,String>();

    //    static {
//        eventNameMap.put("touchdown","达阵");
//        eventNameMap.put("point2_conversion","两分转换");
//        eventNameMap.put("penalty","判罚");
//        eventNameMap.put("extra_point","附加分");
//        eventNameMap.put("safety","安全分");
//        eventNameMap.put("field_goal","射门");
//        eventNameMap.put("challenge","教练对抗");
//    }
    static {
        eventNameMap.put("touchdown","{\"zs\":\"达阵\",\"en\":\"Touchdown\"}");
        eventNameMap.put("point2_conversion","{\"zs\":\"两分转换\",\"en\":\"2 point conversion\"}");
        eventNameMap.put("penalty","{\"zs\":\"判罚\",\"en\":\"Penalty\"}");
        eventNameMap.put("extra_point","{\"zs\":\"附加分\",\"en\":\"Extra point\"}");
        eventNameMap.put("safety","{\"zs\":\"安全分\",\"en\":\"Safety\"}");
        eventNameMap.put("field_goal","{\"zs\":\"射门\",\"en\":\"Field goal\"}");
        eventNameMap.put("challenge","{\"zs\":\"教练对抗\",\"en\":\"Challenge\"}");
    }
    public static String getEventName(MatchEventCommon eventCommon){
        return eventNameMap.get(eventCommon.getEventCode());
    }
}
