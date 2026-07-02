package com.panda.merge.event;

import com.panda.merge.model.MatchEventCommon;
import com.panda.merge.model.MatchEventCommonExample;

import java.util.HashMap;
import java.util.Map;

public class BasketballEventConstant {
    public static Map<String,String> eventNameMap=new HashMap<String,String>();

    //    static {
//        eventNameMap.put("score_change","进球");
//    }
    static {
        eventNameMap.put("score_change","{\"zs\":\"进球\",\"en\":\"Score\"}");
    }
}
