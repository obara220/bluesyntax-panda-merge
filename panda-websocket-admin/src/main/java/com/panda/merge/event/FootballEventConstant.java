package com.panda.merge.event;

import java.util.HashMap;
import java.util.Map;

public class FootballEventConstant {

    public static Map<String,String> eventNameMap=new HashMap<String,String>();

//    static {
//        eventNameMap.put("goal","进球");
//        eventNameMap.put("substitution","换人");
//        eventNameMap.put("offside","越位");
//        eventNameMap.put("yellow_card","黄牌");
//        eventNameMap.put("red_card","红牌");
//        eventNameMap.put("corner","角球");
//    }

    static {
        eventNameMap.put("goal","{\"zs\":\"进球\",\"en\":\"Goal\"}");
        eventNameMap.put("substitution","{\"zs\":\"换人\",\"en\":\"Substitution\"}");
        eventNameMap.put("offside","{\"zs\":\"越位\",\"en\":\"Offside\"}");
        eventNameMap.put("yellow_card","{\"zs\":\"黄牌\",\"en\":\"Yellow card\"}");
        eventNameMap.put("red_card","{\"zs\":\"红牌\",\"en\":\"Red card\"}");
        eventNameMap.put("corner","{\"zs\":\"角球\",\"en\":\"Corner\"}");
    }
}
