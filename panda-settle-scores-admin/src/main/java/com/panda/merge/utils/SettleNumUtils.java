package com.panda.merge.utils;

import com.panda.merge.constant.CommonConstant;
import com.panda.merge.model.MatchSettleScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettleNumUtils {

    //进球阶段比分 结算编码汇总
    public static List<String> PERIOD_SCORES_SETTLE_NUM_GOAL_LIST=new ArrayList<>();
    static{
//        PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("102");PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("103");
//        PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("104");
        PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("105");
//        PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("106");
//        PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("107");PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("108");
        PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("109");
        PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1010");PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1014");PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1018");
        PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1019");
    }
    //角球阶段比分 结算编码汇总
    public static List<String> PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST=new ArrayList<>();
    static{
        PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("201");PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("202");
        PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("203");PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("204");
        PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("205");
        PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("206");PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("207");
        PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("208");PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("209");
        PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.add("2010");


    }
    //角球阶段比分 结算编码汇总
    public static List<String> PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_LIST=new ArrayList<>();
    static{
        PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_LIST.add("2011");PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_LIST.add("2012");
        PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_LIST.add("2013");PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_LIST.add("2014");
        PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_LIST.add("2015");PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_LIST.add("2016");
    }
    //罚牌阶段比分 结算编码汇总
    public static List<String> PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST=new ArrayList<>();
    static{
//        PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("301");PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("302");
//        PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("303");
        PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("304");
//        PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("305");PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("306");
//        PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("307");
        PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("308");
        PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("309");PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("3013");
        PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("3017");PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.add("3018");
    }

    //5分钟进球数
    public static HashMap<String,String> fiveMinuteMap=new HashMap<>();
    static{
        fiveMinuteMap.put("1034","5"); fiveMinuteMap.put("1035","10");fiveMinuteMap.put("1036","15");
        fiveMinuteMap.put("1037","20");fiveMinuteMap.put("1038","25");fiveMinuteMap.put("1039","30");
        fiveMinuteMap.put("1040","35");fiveMinuteMap.put("1041","40");fiveMinuteMap.put("1042","45");
        fiveMinuteMap.put("1043","49");fiveMinuteMap.put("1044","50");fiveMinuteMap.put("1045","55");
        fiveMinuteMap.put("1046","60");fiveMinuteMap.put("1047","65");fiveMinuteMap.put("1048","70");
        fiveMinuteMap.put("1049","75");fiveMinuteMap.put("1050","80");fiveMinuteMap.put("1051","85");
        fiveMinuteMap.put("1052","90");fiveMinuteMap.put("1053","99");
    }

    public static HashMap<String,Integer> GOAL_5_Min_Map=new HashMap<>();
    static{
        GOAL_5_Min_Map.put("1034",5); GOAL_5_Min_Map.put("1035",10);GOAL_5_Min_Map.put("1036",15);
        GOAL_5_Min_Map.put("1037",20);GOAL_5_Min_Map.put("1038",25);GOAL_5_Min_Map.put("1039",30);
        GOAL_5_Min_Map.put("1040",35);GOAL_5_Min_Map.put("1041",40);GOAL_5_Min_Map.put("1042",45);
        GOAL_5_Min_Map.put("1043",49);GOAL_5_Min_Map.put("1044",50);GOAL_5_Min_Map.put("1045",55);
        GOAL_5_Min_Map.put("1046",60);GOAL_5_Min_Map.put("1047",65);GOAL_5_Min_Map.put("1048",70);
        GOAL_5_Min_Map.put("1049",75);GOAL_5_Min_Map.put("1050",80);GOAL_5_Min_Map.put("1051",85);
        GOAL_5_Min_Map.put("1052",90);GOAL_5_Min_Map.put("1053",99);
    }

    public static HashMap<String,Integer> GOAL_15_Min_Map=new HashMap<>();
    static{
        GOAL_15_Min_Map.put("102",15); GOAL_15_Min_Map.put("106",60);
        GOAL_15_Min_Map.put("103",30);GOAL_15_Min_Map.put("107",75);
        GOAL_15_Min_Map.put("104",45);GOAL_15_Min_Map.put("108",90);
    }
    public static HashMap<String,Integer> BASKETBALL_GOAL_6_Min_Map=new HashMap<>();
    static{
        BASKETBALL_GOAL_6_Min_Map.put("bk_q1041",6); BASKETBALL_GOAL_6_Min_Map.put("bk_q2041",18);
        BASKETBALL_GOAL_6_Min_Map.put("bk_q1042",0);BASKETBALL_GOAL_6_Min_Map.put("bk_q2042",12);
        BASKETBALL_GOAL_6_Min_Map.put("bk_q3041",30);BASKETBALL_GOAL_6_Min_Map.put("bk_q4041",42);
        BASKETBALL_GOAL_6_Min_Map.put("bk_q3042",24);BASKETBALL_GOAL_6_Min_Map.put("bk_q4042",36);
    }
    public static HashMap<String,Integer> CORNER_15_Min_Map=new HashMap<>();
    static{
        CORNER_15_Min_Map.put("2011",15); CORNER_15_Min_Map.put("2014",60);
        CORNER_15_Min_Map.put("2012",30);CORNER_15_Min_Map.put("2015",75);
        CORNER_15_Min_Map.put("2013",45);CORNER_15_Min_Map.put("2016",90);
    }
    public static HashMap<String,Integer> BOOKING_15_Min_Map=new HashMap<>();
    static{
        BOOKING_15_Min_Map.put("301",15); BOOKING_15_Min_Map.put("305",60);
        BOOKING_15_Min_Map.put("302",30);BOOKING_15_Min_Map.put("306",75);
        BOOKING_15_Min_Map.put("303",45);BOOKING_15_Min_Map.put("307",90);
    }

    public static String  getEventSettleNum(String eventCode,Long periodId){
        if(eventCode.equals("fa_card")||eventCode.equals("yellow_card")||eventCode.equals("red_card")){
            return getFaCardEventSettleNum(periodId);
        }
        if(eventCode.equals("no goal")||eventCode.equals("goal")){
            return getGoalEventSettleNum(periodId);
        }
        if(eventCode.equals("corner")){
            return getCornerEventSettleNum(periodId);
        }
        return "";
    }

    private static String getCornerEventSettleNum(Long periodId) {
        if(periodId.equals(6l)){
            return "204";
        }
        if(periodId.equals(7l)){
            return "205";
        }
        if(periodId.equals(41l)){
            return "209";
        }
        if(periodId.equals(42l)){
            return "2010";
        }
        return "";
    }

    private static String getGoalEventSettleNum(Long periodId) {
        if(periodId.equals(6l)){
            return "1022";
        }
        if(periodId.equals(7l)){
            return "1023";
        }
        if(periodId.equals(41l)){
            return "1025";
        }
        if(periodId.equals(42l)){
            return "1026";
        }
        return "";
    }

    private static String getFaCardEventSettleNum(Long periodId) {
        if(periodId.equals(6l)){
            return "3019";
        }
        if(periodId.equals(7l)){
            return "3020";
        }
        if(periodId.equals(41l)){
            return "3022";
        }
        if(periodId.equals(42l)){
            return "3023";
        }
        return "";
    }



    public static String  getTypeEventSettleNum(String eventCode,Long periodId, Integer eventType){
        if(eventCode.equals("fa_card")||eventCode.equals("yellow_card")||eventCode.equals("red_card")){
            return getTypeFaCardEventSettleNum(periodId, eventType);
        }
        if(eventCode.equals("no goal")||eventCode.equals("goal")){
            return getTypeGoalEventSettleNum(periodId, eventType);
        }
        if(eventCode.equals("corner")){
            return getTypeCornerEventSettleNum(periodId, eventType);
        }
        return "";
    }

    private static String getTypeCornerEventSettleNum(Long periodId, Integer eventType) {
        if (eventType == 3) {
            if (periodId.equals(6l)) {
                return "2045";
            } else if (periodId.equals(7l)) {
                return "2055";
            } else if (periodId.equals(41l)) {
                return "2095";
            } else if (periodId.equals(42l)) {
                return "20105";
            }
        } else if (eventType ==1) {
            if (periodId.equals(6l)) {
                return "204";
            } else if (periodId.equals(7l)) {
                return "205";
            } else if (periodId.equals(41l)) {
                return "209";
            } else if (periodId.equals(42l)) {
                return "2010";
            }
        }
        return "";
    }

    private static String getTypeGoalEventSettleNum(Long periodId, Integer eventType) {
        if(periodId.equals(6l)||periodId.equals(7l)){
            if (eventType == 3) {
                if (periodId.equals(6l)) {
                    return "10225";
                } else if (periodId.equals(7l)) {
                    return "10235";
                }
            } else if (eventType == 2) {
                return "1024";
            } else if(eventType ==1) {
                if (periodId.equals(6l)) {
                    return "1022";
                } else if (periodId.equals(7l)) {
                    return "1023";
                }
            }
        }

        if(periodId.equals(41l)||periodId.equals(42l)){
            if (eventType == 3) {
                if (periodId.equals(41l)) {
                    return "10255";
                } else if (periodId.equals(42l)) {
                    return "10265";
                }
            }else if (eventType == 2) {
                return "1027";
            } else if(eventType ==1) {
                if (periodId.equals(41l)) {
                    return "1025";
                } else if (periodId.equals(42l)) {
                    return "1026";
                }
            }
        }
        return "";
    }

    private static String getTypeFaCardEventSettleNum(Long periodId, Integer eventType) {
        if(periodId.equals(6l)||periodId.equals(7l)){
            if (eventType == 3) {
                if (periodId.equals(6l)) {
                    return "30195";
                } else if (periodId.equals(7l)) {
                    return "30205";
                }
            }else if (eventType == 2) {
                return "3021";
            } else if(eventType ==1) {
                if (periodId.equals(6l)) {
                    return "3019";
                } else if (periodId.equals(7l)) {
                    return "3020";
                }
            }
        }

        if(periodId.equals(41l)||periodId.equals(42l)){
            if (eventType == 3) {
                if (periodId.equals(41l)) {
                    return "30225";
                } else if (periodId.equals(42l)) {
                    return "30235";
                }
            }else if (eventType == 2) {
                return "3024";
            } else if(eventType ==1) {
                if (periodId.equals(41l)) {
                    return "3022";
                } else if (periodId.equals(42l)) {
                    return "3023";
                }
            }
        }
        return "";
    }

    public static List<String> getPieriodScoresBeforeSettleNum(String settleNum) {
        //进球类
        Integer index =PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.indexOf(settleNum);
        List<String> settleNumBefore=new ArrayList<>();
        for(int i=0;i<index;i++){
            settleNumBefore.add(PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.get(i));
        }
        //角球类
        if(settleNumBefore.size()==0){
             index =PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.indexOf(settleNum);
            for(int i=0;i<index;i++){
                settleNumBefore.add(PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_LIST.get(i));
            }
        }
//        if(settleNumBefore.size()==0){
//             index =PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_LIST.indexOf(settleNum);
//            for(int i=0;i<index;i++){
//                settleNumBefore.add(PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_LIST.get(i));
//            }
//        }
        //罚牌类
        if(settleNumBefore.size()==0){
            index =PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.indexOf(settleNum);
            for(int i=0;i<index;i++){
                settleNumBefore.add(PERIOD_SCORES_SETTLE_NUM_FA_CARD_LIST.get(i));
            }
        }
        return settleNumBefore;
    }

    public static List<String> getPieriodScoresBeforeSettleNewNum(String settleNum) {
        //进球类
        Integer index =PERIOD_SCORES_SETTLE_NUM_GOAL_NEW_LIST.indexOf(settleNum);
        List<String> settleNumBefore=new ArrayList<>();
        for(int i=0;i<index;i++){
            settleNumBefore.add(PERIOD_SCORES_SETTLE_NUM_GOAL_NEW_LIST.get(i));
        }
        //角球类
        if(settleNumBefore.size()==0){
            index =PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_NEW_LIST.indexOf(settleNum);
            for(int i=0;i<index;i++){
                settleNumBefore.add(PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_NEW_LIST.get(i));
            }
        }
        if(settleNumBefore.size()==0){
            index =PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_NEW_LIST.indexOf(settleNum);
            for(int i=0;i<index;i++){
                settleNumBefore.add(PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_NEW_LIST.get(i));
            }
        }
        //罚牌类
        if(settleNumBefore.size()==0){
            index =PERIOD_SCORES_SETTLE_NUM_FA_CARD_NEW_LIST.indexOf(settleNum);
            for(int i=0;i<index;i++){
                settleNumBefore.add(PERIOD_SCORES_SETTLE_NUM_FA_CARD_NEW_LIST.get(i));
            }
        }
        return settleNumBefore;
    }

    public static List<String> getFiveMinPieriodScoresBeforeSettleNum(String settleNum) {
        //进球类
        Integer index =FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.indexOf(settleNum);
        List<String> settleNumBefore=new ArrayList<>();
        for(int i=0;i<index;i++){
            settleNumBefore.add(FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.get(i));
        }
        return settleNumBefore;
    }

    //进球阶段比分 结算编码汇总
    public static List<String> PERIOD_SCORES_SETTLE_NUM_GOAL_NEW_LIST=new ArrayList<>();
    static{
        PERIOD_SCORES_SETTLE_NUM_GOAL_NEW_LIST.add("105");PERIOD_SCORES_SETTLE_NUM_GOAL_NEW_LIST.add("109");
        PERIOD_SCORES_SETTLE_NUM_GOAL_NEW_LIST.add("1010");
    }
    //角球阶段比分 结算编码汇总
    public static List<String> PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_NEW_LIST=new ArrayList<>();
    static{

        PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_NEW_LIST.add("201");
        PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_NEW_LIST.add("202");
        PERIOD_SCORES_SETTLE_NUM_CORNER_ONE_NEW_LIST.add("203");
    }
    //角球阶段比分 结算编码汇总
    public static List<String> PERIOD_SCORES_SETTLE_NUM_CORNER_TWO_NEW_LIST=new ArrayList<>();
    //罚牌阶段比分 结算编码汇总
    public static List<String> PERIOD_SCORES_SETTLE_NUM_FA_CARD_NEW_LIST=new ArrayList<>();
    static{
        PERIOD_SCORES_SETTLE_NUM_FA_CARD_NEW_LIST.add("304");PERIOD_SCORES_SETTLE_NUM_FA_CARD_NEW_LIST.add("308");
        PERIOD_SCORES_SETTLE_NUM_FA_CARD_NEW_LIST.add("309");
    }

    //进球阶段比分 结算编码汇总
    public static List<String> FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST=new ArrayList<>();
    static{
        FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1034");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1035");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1036");
        FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1037");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1038");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1039");
        FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1040");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1041");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1042");
        FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1043");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1044");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1045");
        FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1046");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1047");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1048");
        FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1049");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1050");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1051");
        FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1052");FIVE_MIN_PERIOD_SCORES_SETTLE_NUM_GOAL_LIST.add("1053");
    }

    public static Map<String,Long> SETTLE_NUM_EVENT_PERIOD_MAP=new HashMap<>();
    static {
        SETTLE_NUM_EVENT_PERIOD_MAP.put("105",6L); SETTLE_NUM_EVENT_PERIOD_MAP.put("109",7L);
        SETTLE_NUM_EVENT_PERIOD_MAP.put("1014",41L); SETTLE_NUM_EVENT_PERIOD_MAP.put("1018",42L);
        SETTLE_NUM_EVENT_PERIOD_MAP.put("201",6L); SETTLE_NUM_EVENT_PERIOD_MAP.put("202",7L);
        SETTLE_NUM_EVENT_PERIOD_MAP.put("206",41L); SETTLE_NUM_EVENT_PERIOD_MAP.put("207",42L);
        SETTLE_NUM_EVENT_PERIOD_MAP.put("304",6L); SETTLE_NUM_EVENT_PERIOD_MAP.put("308",7L);
        SETTLE_NUM_EVENT_PERIOD_MAP.put("3013",41L); SETTLE_NUM_EVENT_PERIOD_MAP.put("3017",42L);
    }
    public static Long countEventPeriodBySettleScore(String settleNum){
        return SETTLE_NUM_EVENT_PERIOD_MAP.get(settleNum);
    }


    public static String getEventSettleNumByPeriodAndEventCode(String eventCode, Long period) {
        if(eventCode.equals("goal")){
            if(period.equals(6L)){
                return "1022";
            }else if(period.equals(7L)){
                return "1023";
            }else if(period.equals(41L)){
                return "1025";
            }else if(period.equals(42l)){
                return "1026";
            }
        }else if(eventCode.equals("corner")){
            if(period.equals(6L)){
                return "204";
            }else if(period.equals(7L)){
                return "205";
            }else if(period.equals(41L)){
                return "206";
            }else if(period.equals(42l)){
                return "207";
            }
        }else if(eventCode.equals("facard")){
            if(period.equals(6L)){
                return "3019";
            }else if(period.equals(7L)){
                return "3020";
            }else if(period.equals(41L)){
                return "3022";
            }else if(period.equals(42l)){
                return "3023";
            }
        }else {
            return null;
        }
        return null;
    }

    public static String getNoneEventHomeAway(MatchSettleScore matchSettleScore) {
        if(matchSettleScore.getEventCode().equals("goal")){
            return "no goal";
        }else if(matchSettleScore.getEventCode().equals("corner")){
            return "none";
        }else if(matchSettleScore.getEventCode().equals("fa_card")){
            return "none";
        }else {
            return null;
        }
    }
    public static List<String> BASCKET_SCORES_SETTLE_NUM_LIST_4S= new ArrayList<>();
    public static List<String> BASCKET_SCORES_SETTLE_NUM_LIST_2S= new ArrayList<>();
    public static List<String> BASCKET_SCORES_SETTLE_NUM_LIST_3X= new ArrayList<>();
    static {
        //4节制
       BASCKET_SCORES_SETTLE_NUM_LIST_4S.add("bk_q104");
       BASCKET_SCORES_SETTLE_NUM_LIST_4S.add("bk_q204");
        BASCKET_SCORES_SETTLE_NUM_LIST_4S.add("bk_1ht");
        BASCKET_SCORES_SETTLE_NUM_LIST_4S.add("bk_q304");
        BASCKET_SCORES_SETTLE_NUM_LIST_4S.add("bk_q404");
        BASCKET_SCORES_SETTLE_NUM_LIST_4S.add("bk_2ht");
        BASCKET_SCORES_SETTLE_NUM_LIST_4S.add("bk_ft_rg");
        BASCKET_SCORES_SETTLE_NUM_LIST_4S.add("bk_et");
        BASCKET_SCORES_SETTLE_NUM_LIST_4S.add("bk_ft_et");
        //2节制
        BASCKET_SCORES_SETTLE_NUM_LIST_2S.add("bk_1ht");
        BASCKET_SCORES_SETTLE_NUM_LIST_2S.add("bk_2ht");
        BASCKET_SCORES_SETTLE_NUM_LIST_2S.add("bk_ft_rg");
        BASCKET_SCORES_SETTLE_NUM_LIST_2S.add("bk_et");
        BASCKET_SCORES_SETTLE_NUM_LIST_2S.add("bk_ft_et");
        //3X
        BASCKET_SCORES_SETTLE_NUM_LIST_3X.add("bk_ft_rg");
        BASCKET_SCORES_SETTLE_NUM_LIST_3X.add("bk_et");
        BASCKET_SCORES_SETTLE_NUM_LIST_3X.add("bk_ft_et");
    }
    public static List<String> countBasketballScoreSettleNumBefore(String settleNum,Integer matchLenth) {
        List<String> bascketScoreFullSettleNumList =null;
        if(matchLenth==73){
            bascketScoreFullSettleNumList=BASCKET_SCORES_SETTLE_NUM_LIST_3X;
        }else if(matchLenth==17){
            bascketScoreFullSettleNumList=BASCKET_SCORES_SETTLE_NUM_LIST_2S;
        }else {
            bascketScoreFullSettleNumList=BASCKET_SCORES_SETTLE_NUM_LIST_4S;
        }

        List<String> list =new ArrayList<>();
        Integer index =bascketScoreFullSettleNumList.indexOf(settleNum);
        if(index<0){
            return list;
        }
        for (int i=0;i<index;i++) {
            list.add(bascketScoreFullSettleNumList.get(i));
        }
        return list;
    }
    //灰色区间类型
    public static String getGrayType(String eventCode,String settleNum) {
        //5分钟进球 15分钟进球   15分钟角球
        if(eventCode.equals("goal")){
            Object o =  fiveMinuteMap.get(settleNum);
            if(o!=null){
                return "min5Goal";
            }else {
                return "min15Goal";
            }
        }else if(eventCode.equals("corner")) {
            return "min15Corner";
        }else if(eventCode.equals(CommonConstant.BASKETBALL_SCORE_EVENT_CODE)) {
            return CommonConstant.BASKETBALL_GRAY_GAOL_6MIN;
        }else {
            return "booking15Min";
        }
    }
    //灰色区间分钟数
    public static Integer getGrayMin(String settleNum) {
        //5 10  15 20  ...
        if(GOAL_5_Min_Map.get(settleNum)!=null){
            return GOAL_5_Min_Map.get(settleNum);
        }
        //15分钟进球
        if(GOAL_15_Min_Map.get(settleNum)!=null){
            return GOAL_15_Min_Map.get(settleNum);
        }
        //15分钟角球
        if(CORNER_15_Min_Map.get(settleNum)!=null){
            return CORNER_15_Min_Map.get(settleNum);
        }
        if(BOOKING_15_Min_Map.get(settleNum)!=null){
            return BOOKING_15_Min_Map.get(settleNum);
        }
        if(BASKETBALL_GOAL_6_Min_Map.get(settleNum)!=null){
            return BASKETBALL_GOAL_6_Min_Map.get(settleNum);
        }
        return 0;
    }
}
