package com.panda.merge.event;

import java.util.HashMap;
import java.util.Map;

public class TennisEventConstant {

    public static Map<String,String> eventNameMap=new HashMap<String,String>();

    static {
        eventNameMap.put("tennis_score_change","得分");
    }
    //    public static String ACE="ACE球";  //1
//    public static String SAVE="保发";  //0 - home away
//    public static String BREAK="破发"; //0 -  away away
//    public static String FAULT ="双发失误"; //2
    public static String ACE="{\"zs\":\"ACE球\",\"en\":\"ACE\"}";  //1
    public static String SAVE="{\"zs\":\"保发\",\"en\":\"Guarantee\"}";  //0 - home away
    public static String BREAK="{\"zs\":\"破发\",\"en\":\"Break\"}"; //0 -  away away
    public static String FAULT ="{\"zs\":\"双发失误\",\"en\":\"Double fault\"}"; //2
}
