package com.panda.merge.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件编码工具类
 * */
public class EndEventUtils {
    static List<String> FA_CARD_CODES=new ArrayList<>();
    static List<String> GOAL_CODES=new ArrayList<>();
    static List<String> CORNER_CODES=new ArrayList<>();
    static {
        FA_CARD_CODES.add("fa_card");
        FA_CARD_CODES.add("yellow_card");
        FA_CARD_CODES.add("red_card");

        GOAL_CODES.add("goal");
        CORNER_CODES.add("corner");
    }

    /**
     * 根据事件编码类型获取事件编码相关类型的List
     * */
    public static List<String> eventCodesFootballByEventCode(String eventCode){
        if(eventCode.equals("yellow_card")||eventCode.equals("red_card")||eventCode.equals("fa_card")){
            return FA_CARD_CODES;
        }else if(eventCode.equals("corner")){
            return CORNER_CODES;
        }else if(eventCode.equals("goal")){
            return GOAL_CODES;
        }else {
            return new ArrayList<>();
        }
    }
    static List<Long> HT_EVENT_PERIOD=new ArrayList<>();
    static List<Long> FT_EVENT_PERIOD=new ArrayList<>();
    static {
        HT_EVENT_PERIOD.add(6L);
        FT_EVENT_PERIOD.add(6L);
        FT_EVENT_PERIOD.add(7L);
    }
    /**
     * 根据事件阶段获取需要的事件阶段list
     * */
    public static List<Long> periodsFootballByScorePeriod(Long periodId) {
        //假如上半场休息阶段 直接返回 上半场 查询事件
        if(periodId.equals(31L)){
            return HT_EVENT_PERIOD;
        }else if(periodId.equals(100L)){
            return FT_EVENT_PERIOD;
        }else {
            return null;
        }
        //假如是下半场休息阶段 则返回 上下半场 查询事件
    }
    public static List<String> HOME_AWAY=new ArrayList<>();
    static {
        HOME_AWAY.add("home");
        HOME_AWAY.add("away");
    }
    static List<Long> HT_END_PERIOD=new ArrayList<>();
    static List<Long> FT_END_PERIOD=new ArrayList<>();
    static {
        HT_END_PERIOD.add(31L);
        HT_END_PERIOD.add(100L);
        FT_END_PERIOD.add(100L);
    }
    public static List<Long> periodsFootballByEventPeriod(Long periodId) {
        if(periodId.equals(6L)){
            return HT_END_PERIOD;
        }else if(periodId.equals(7L)){
            return FT_END_PERIOD;
        }else {
            return null;
        }
    }

    private static List<Long> BASKET_IN_GAME_PERIOD=new ArrayList<>();
    static {
        BASKET_IN_GAME_PERIOD.add(1L);
        BASKET_IN_GAME_PERIOD.add(2L);
        BASKET_IN_GAME_PERIOD.add(13L);
        BASKET_IN_GAME_PERIOD.add(14L);
        BASKET_IN_GAME_PERIOD.add(15L);
        BASKET_IN_GAME_PERIOD.add(16L);
        BASKET_IN_GAME_PERIOD.add(40L);
        BASKET_IN_GAME_PERIOD.add(21L);
    }
    public static boolean basketInGameByPeriod(Long periodId){
        if(periodId==null){
            return false;
        }
        if(BASKET_IN_GAME_PERIOD.contains(periodId)){
            return true;
        }else {
            return false;
        }
    }
    /**
     * 根据阶段获取每节即时结算编码
     * */
    public static String getBasketballInSettleCodeByPeriod(Long period) {
        String code=null;
        switch (period.intValue()){
            case 13:
                code="bk_in_q01";
                break;
            case 14:
                code="bk_in_q02";
                break;
            case 15:
                code="bk_in_q03";
                break;
            case 16:
                code="bk_in_q04";
                break;
            case 1:
                code="bk_in_1ht";
                break;
            case 2:
//                code="bk_in_2ht";
                code="bk_in_2htet";
                break;
            case 21:
                code="";
                break;
            case 40:
                code=null;
                break;
        }
        return code;
    }
}
