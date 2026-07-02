package com.panda.merge.event;

import java.util.HashMap;
import java.util.Map;

public class IceHockeyEventConstant {
    static final Map<String, String> snkColor = new HashMap<>();
    //    static {
//        snkColor.put("0", "进球-未知情况");
//        snkColor.put("-1", "进球-一般进球");
//        snkColor.put("1", "进球-罚球");
//        snkColor.put("7", "进球-力量均匀");
//        snkColor.put("8", "进球-力量发挥");
//        snkColor.put("9", "进球-以多打少");
//        snkColor.put("11", "进球-空网");
//    }
    static {
        snkColor.put("0", "{\"zs\":\"进球\",\"en\":\"Goal-Unknown\"}");
        snkColor.put("-1", "{\"zs\":\"进球-一般进球\",\"en\":\"Goal-Not specified\"}");
        snkColor.put("1", "{\"zs\":\"进球-罚球\",\"en\":\"Goal-Penalty\"}");
        snkColor.put("7", "{\"zs\":\"进球-力量均匀\",\"en\":\"Goal-Even strength\"}");
        snkColor.put("8", "{\"zs\":\"进球-力量发挥\",\"en\":\"Goal-Power play\"}");
        snkColor.put("9", "{\"zs\":\"进球-以多打少\",\"en\":\"Goal-Short handed\"}");
        snkColor.put("11", "{\"zs\":\"进球-空网\",\"en\":\"Goal-Empty net\"}");
    }
    public static String  getEventName(String extrinfo){
        return snkColor.get(extrinfo);
    }
}
