package com.panda.merge.event;

import com.panda.merge.model.MatchEventCommon;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseballEventConstant {
    public static Map<String,String> eventNameMap=new HashMap<String,String>();

    //    static {
//        eventNameMap.put("run_scored","得分");
//        eventNameMap.put("runner_advances_to_base_x","跑垒员跑到x垒");
//        eventNameMap.put("runner_out","跑垒出局");
//        eventNameMap.put("strike","好球");
//        eventNameMap.put("ball","坏球");
//        eventNameMap.put("foul_ball","界外球");
//        eventNameMap.put("batter_out","击球员出局");
//        eventNameMap.put("batter_advances_to_base_x","击球员跑向x垒");
//        eventNameMap.put("balk","投手犯规");
//    }
    static {
        eventNameMap.put("run_scored","{\"zs\":\"得分\",\"en\":\"Runs\"}");
        eventNameMap.put("runner_advances_to_base_x","{\"zs\":\"跑垒员跑到x垒\",\"en\":\"Runner advances to base x\"}");
        eventNameMap.put("runner_out","{\"zs\":\"跑垒出局\",\"en\":\"Runner Out\"}");
        eventNameMap.put("strike","{\"zs\":\"好球\",\"en\":\"Strike\"}");
        eventNameMap.put("ball","{\"zs\":\"坏球\",\"en\":\"Ball\"}");
        eventNameMap.put("foul_ball","{\"zs\":\"界外球\",\"en\":\"Foul ball\"}");
        eventNameMap.put("batter_out","{\"zs\":\"击球员出局\",\"en\":\"Batter Out\"}");
        eventNameMap.put("batter_advances_to_base_x","{\"zs\":\"击球员跑向x垒\",\"en\":\"Batter advances to base x\"}");
        eventNameMap.put("balk","{\"zs\":\"投手犯规\",\"en\":\"Balk\"}");
    }
    public static String getEventName(MatchEventCommon eventCommon){
        String firstName =eventNameMap.get(eventCommon.getEventCode());
        if(eventCommon.getEventCode().equals("run_scored")){
            return  firstName +" "+ run_scored(eventCommon.getExtraInfo());
        }
        if(eventCommon.getEventCode().equals("runner_advances_to_base_x")){
            if(eventCommon.getAddition10()==null){
                eventCommon.setAddition10("");
            }
            return  firstName.replace("x",eventCommon.getAddition10());
        }
        if(eventCommon.getEventCode().equals("batter_advances_to_base_x")){
            if("4".equals(eventCommon.getExtraInfo())||"5".equals(eventCommon.getExtraInfo())||"6".equals(eventCommon.getExtraInfo())){
                eventCommon.setEventCode("hit");
                return "{\"zs\":\"安打\",\"en\":\"hit\"} ";
            }
            if(eventCommon.getAddition10()==null){
                eventCommon.setAddition10("");
            }
            return  firstName.replace("x",eventCommon.getAddition10());
        }
        if(eventCommon.getEventCode().equals("strike")){
            return  firstName +" "+ strike(eventCommon.getExtraInfo());
        }
        if(eventCommon.getEventCode().equals("ball")){
            return  firstName;
//            return  firstName +" "+ ball(eventCommon.getExtraInfo());
        }
        return firstName;
    }
    private static String run_scored(String extrainfo){
//        if("1".equals(extrainfo)){
//            return "自责分";
//        }
//        if("2".equals(extrainfo)){
//            return "逃离";
//        }
//        if("3".equals(extrainfo)){
//            return "盗垒";
//        }
//        if("4".equals(extrainfo)){
//            return "本垒打";
//        }
//        if("5".equals(extrainfo)){
//            return "";
//        }
        return "";
    }

    private static String strike(String extrainfo){
//        if("4".equals(extrainfo)){
//            return "界外球";
//        }
        return "";
    }

    private static String ball(String extrainfo){
        return "坏球";
    }


}
