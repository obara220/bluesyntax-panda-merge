package com.panda.merge.event;

import java.util.HashMap;
import java.util.Map;

public class SnookerEventConstant {
    static final Map<String, String> snkColor = new HashMap<>();
    //    static {
//        snkColor.put("1", "红球");
//        snkColor.put("2", "黄球");
//        snkColor.put("3", "棕球");
//        snkColor.put("4", "绿球");
//        snkColor.put("5", "蓝球");
//        snkColor.put("6", "粉球");
//        snkColor.put("7", "黑球");
//    }
    static {
        snkColor.put("1", "{\"zs\":\"红球\",\"en\":\"Red ball\"}");
        snkColor.put("2", "{\"zs\":\"黄球\",\"en\":\"Yellow ball\"}");
        snkColor.put("3", "{\"zs\":\"棕球\",\"en\":\"Brown ball\"}");
        snkColor.put("4", "{\"zs\":\"绿球\",\"en\":\"Green ball\"}");
        snkColor.put("5", "{\"zs\":\"蓝球\",\"en\":\"Blue ball\"}");
        snkColor.put("6", "{\"zs\":\"粉球\",\"en\":\"Pink ball\"}");
        snkColor.put("7", "{\"zs\":\"黑球\",\"en\":\"Black ball\"}");
    }
    public static String  getEventName(String extrinfo){
        return snkColor.get(extrinfo);
    }
}
