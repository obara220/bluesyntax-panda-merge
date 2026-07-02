package com.panda.merge.constant;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Getter
public enum FootballPeriodValidateEnum {

    // "1HT"           | 105  | "00:00 - 14:59","15:00 - 29:59","30:00 - 1HT"                   | "102,103,104"
    // "2HT"           | 109  | "1HT - 59:59","60:00 - 74:59","75:00 - FT"                      | "106,107,108"
    // "FT"            | 1010 | "1HT","2HT"                                                     | "105,109"
    // "1ET"           | 1014 | "ET 00:00 - 04:59","ET 05:00 - 09:59","ET 10:00 - 1ET"          | "1011,1012,1013"
    // "2ET"           | 1018 | "ET 1ET - 19:59","ET 20:00 - 24:59","25:00 - ET"                | "1015,1016,1017"
    // "ET"            | 1019 | "1ET","2ET"                                                     | "1014,1018"
    // "00:00 - 14:59" | 102  | "0:00 - 4:59","5:00 - 9:59","10:00 - 14:59"                     | "1034,1035,1036"
    // "15:00 - 29:59" | 103  | "15:00 - 19:59","20:00 - 24:59","25:00 - 29:59"                 | "1037,1038,1039"
    // "30:00 - 1HT"   | 104  | "30:00 - 34:59","35:00 - 39:59","40:00 - 45:00","上半场 绝杀球"   | "1040,1041,1042,1043"
    // "1HT - 59:59"   | 106  | "下半场 - 49:59","50:00 - 54:59",55:00 - 59:59                   | "1044,1045,1046"
    // "60:00 - 74:59" | 107  | "60:00 - 64:59","65:00 - 69:59","70:00 - 74:59"                 | "1047,1048,1049"
    // "75:00 - FT"    | 108  | "75:00 - 79:59","80:00 - 84:59""85:00 - 90:00","下半场 绝杀球"    | "1050,1051,1052,1053"


    /**
     * 进球
     */
    GOAL_1(101, "Kick-off", "谁先开球", null, null),
    GOAL_2(102, "00:00 - 14:59", "进球 00:00 - 14:59", "105", "1034,1035,1036"), //"1034,1035,1036"
    GOAL_3(103, "15:00 - 29:59", "进球 15:00 - 29:59", "105", "1037,1038,1039"), //"1037,1038,1039"),
    GOAL_4(104, "30:00 - 1HT", "进球 30:00 - 1HT", "105", "1040,1041,1042,1043"), //"1040,1041,1042,1043"),
    GOAL_5(105, "1HT", "进球上半场", "1010", "102,103,104"),
    GOAL_6(106, "1HT - 59:59", "进球 1HT - 59:59", "109", "1044,1045,1046"), //"1044,1045,1046"),
    GOAL_7(107, "60:00 - 74:59", "进球 60:00 - 74:59", "109", "1047,1048,1049"), //"1047,1048,1049"),
    GOAL_8(108, "75:00 - FT", "进球 75:00 - FT", "109", "1050,1051,1052,1053"), //"1050,1051,1052,1053"),
    GOAL_9(109, "2HT", "进球下半场", "1010", "106,107,108"),
    GOAL_10(1010, "FT", "进球全场(常规赛)", null, "105,109"),
    GOAL_11(1011, "ET 00:00 - 04:59", "进球加时赛 00:00 - 04:59", null, null),
    GOAL_12(1012, "ET 05:00 - 09:59", "进球加时赛 05:00 - 09:59", null, null),
    GOAL_13(1013, "ET 10:00 - 1ET", "进球加时赛 10:00 - 1ET", null, null),
    GOAL_14(1014, "1ET", "进球加时赛上半场", "1019", null),//"1011,1012,1013"),
    GOAL_15(1015, "ET 1ET - 19:59", "进球加时赛 1ET - 19:59", null, null),
    GOAL_16(1016, "20:00 - 24:59", "进球加时赛 20:00 - 24:59", null, null),
    GOAL_17(1017, "25:00 - ET", "进球加时赛 25:00 - ET", null, null),
    GOAL_18(1018, "2ET", "进球加时赛下半场", "1019", null),//"1015,1016,1017"),
    GOAL_19(1019, "ET", "进球加时赛全场", null, "1014,1018"),
    GOAL_20(1020, "Winner / To Qualify", "获胜 / 晋级", null, null),
    GOAL_21(1021, "Winning Method", "获胜方式", null, null),

    GOAL_NG_1(1022, "1HT", "上半场进球次序", null, null),
    GOAL_NG_2(1023, "2HT", "下半场进球次序", null, null),
    GOAL_NG_3(1024, "Player & Goal Method", "进球球员 & 进球方式(常规赛)", null, null),
    GOAL_NG_4(1025, "1ET", "加时赛上半场", null, null),
    GOAL_NG_5(1026, "2ET", "加时赛下半场", null, null),
    GOAL_NG_6(1027, "Player & Goal Method", "进球球员 & 进球方式(加时赛)", null, null),

    /**
     * 点球大战
     */
    GOAL_PENALTY_1(1028, "Total PEN", "点球大战总比分", null, null),
    GOAL_PENALTY_2(1029, "Total 1-5", "点球大战前五轮比分", null, null),
    GOAL_PENALTY_3(1030, "Shoot-out", "点球大战", null, null),
    GOAL_PENALTY_33(-1030, "Shoot-out First", "点球大战谁先射门", null, null),
    GOAL_PENALTY_4(1031, "Penalty Shoot-out", "是否点球大战", null, null),
    GOAL_PENALTY_5(1032, "No Extra Time & Penalty Shoot-out (Return)", "没有进行加时赛 & 点球大战 (走水)", null, null),
    GOAL_PENALTY_6(1033, "No Penalty Shoot-out (Return)", "没有进行点球大战 (走水)", null, null),


    /**
     * 五分钟比分
     */
    GOAL_5minute_1034(1034, "0:00 - 4:59", "0:00 - 4:59", "102", null),
    GOAL_5minute_1035(1035, "5:00 - 9:59", "5:00 - 9:59", "102", null),
    GOAL_5minute_1036(1036, "10:00 - 14:59", "10:00 - 14:59", "102", null),
    GOAL_5minute_1037(1037, "15:00 - 19:59", "15:00 - 19:59", "103", null),
    GOAL_5minute_1038(1038, "20:00 - 24:59", "20:00 - 24:59", "103", null),
    GOAL_5minute_1039(1039, "25:00 - 29:59", "25:00 - 29:59", "103", null),
    GOAL_5minute_1040(1040, "30:00 - 34:59", "30:00 - 34:59", "104", null),
    GOAL_5minute_1041(1041, "35:00 - 39:59", "35:00 - 39:59", "104", null),
    GOAL_5minute_1042(1042, "40:00 - 45:00 (excluded injury time)", "40:00 - 45:00 (不含补时)", "104", null),
    GOAL_5minute_1043(1043, "1H Last-minute Goal (Injury Time)", "上半场 绝杀球", "104", null),

    GOAL_5minute_1044(1044, "2H - 49:59", "下半场 - 49:59", "106", null),
    GOAL_5minute_1045(1045, "50:00 - 54:59", "50:00 - 54:59", "106", null),
    GOAL_5minute_1046(1046, "55:00 - 59:59", "55:00 - 59:59", "106", null),
    GOAL_5minute_1047(1047, "60:00 - 64:59", "60:00 - 64:59", "107", null),
    GOAL_5minute_1048(1048, "65:00 - 69:59", "65:00 - 69:59", "107", null),
    GOAL_5minute_1049(1049, "70:00 - 74:59", "70:00 - 74:59", "107", null),
    GOAL_5minute_1050(1050, "75:00 - 79:59", "75:00 - 79:59", "108", null),
    GOAL_5minute_1051(1051, "80:00 - 84:59", "80:00 - 84:59", "108", null),
    GOAL_5minute_1052(1052, "85:00 - 90:00 (excluded injury time)", "85:00 - 90:00 (不含补时)", "108", null),
    GOAL_5minute_1053(1053, "2H Last-minute Goal (Injury Time)", "下半场 绝杀球", "108", null),
    GOAL_5minute_1054(1054, "Shoot-out (Return)", "点球大战 (走水)", null, null),
    GOAL_5minute_0(0, "No Goal", "没有进球", null, null),


    /**
     * * 角球
     */
    Corner_15m_1(2011, "CR 00:00 - 14:59", "角球 00:00 - 14:59", null, null),
    Corner_15m_2(2012, "CR 15:00 - 29:59", "角球 15:00 - 29:59", null, null),
    Corner_15m_3(2013, "CR 30:00 - HT", "角球 30:00 - HT", null, null),

    Corner_1(201, "1HT CR", "角球上半场", null, null),//"2011,2012,2013"),

    Corner_15m_4(2014, "CR HT - 59:59", "角球 HT - 59:59", null, null),
    Corner_15m_5(2015, "CR 60:00 - 74:59", "角球 60:00 - 74:59", null, null),
    Corner_15m_6(2016, "CR 75:00 - FT", "角球 75:00 - FT", null, null),

    Corner_2(202, "2HT CR", "角球下半场", null, null),//"2014,2015,2016"),
    Corner_3(203, "FT CR", "角球全场(常规赛)", null, "201,202"),


    Corner_15m_7(2017, "EC 00:00 - 04:59", "加时赛角球 00:00 - 04:59", null, null),
    Corner_15m_8(2018, "EC 05:00 - 09:45", "加时赛角球 05:00 - 09:45", null, null),
    Corner_15m_9(2019, "EC 10:00 - 1ET", "加时赛角球 10:00 - 1ET", null, null),

    Corner_6(206, "1ET CR", "角球加时上半场", null, null),//"2017,2018,2019"),

    Corner_15m_10(2020, "EC 1ET - 19:59", "加时赛角球 1ET - 19:59", null, null),
    Corner_15m_11(2021, "EC 20:00 - 24:59", "加时赛角球 20:00 - 24:59", null, null),
    Corner_15m_12(2022, "EC 25:00 - ET", "加时赛角球 25:00 - ET", null, null),

    Corner_7(207, "2ET CR", "角球加时下半场", null, null),//"2020,2021,2022"),
    Corner_8(208, "ET CR", "角球加时全场", null, "206,207"),


    Corner_9(209, "1EC", "角球加时上半场角球次序", null, null),
    Corner_10(2010, "2EC", "角球加时下半场角球次序", null, null),
    Corner_4(204, "1 CR", "角球上半场角球次序", null, null),
    Corner_5(205, "2 CR", "角球下半场角球次序", null, null),


    /**
     * 罚牌
     */
    BOOKINGS_1(301, "BK 00:00 - 14:59", "罚牌 00:00 - 14:59", null, null),
    BOOKINGS_2(302, "BK 15:00 - 29:59", "罚牌 15:00 - 29:59", null, null),
    BOOKINGS_3(303, "BK 30:00 - 1HT", "罚牌 30:00 - 1HT", null, null),

    BOOKINGS_4(304, "BK 1HT", "罚牌上半场", null, "301,302,303"),

    BOOKINGS_5(305, "BK 1HT - 59:59", "罚牌 1HT - 59:59", null, null),
    BOOKINGS_6(306, "BK 60:00 - 74:59", "罚牌 60:00 - 74:59", null, null),
    BOOKINGS_7(307, "BK 75:00 - FT", "罚牌 75:00 - FT", null, null),

    BOOKINGS_8(308, "BK 2HT", "罚牌下半场", null, "305,306,307"),
    BOOKINGS_9(309, "BK FT", "罚牌全场(常规赛)", null, "304,308"),

    BOOKINGS_10(3010, "EB 00:00 - 04:59", "罚牌加时赛 00:00 - 04:59", null, null),
    BOOKINGS_11(3011, "EB 05:00 - 09:45", "罚牌加时赛 05:00 - 09:45", null, null),
    BOOKINGS_12(3012, "EB 10:00 - 1ET", "罚牌加时赛 10:00 - 1ET", null, null),

    BOOKINGS_13(3013, "1ET BK", "罚牌加时赛上半场", null, null),//"3010,3011,3012"),

    BOOKINGS_14(3014, "EB 1ET - 19:59", "罚牌加时赛 1ET - 19:59", null, null),
    BOOKINGS_15(3015, "EB 20:00 - 24:59", "罚牌加时赛 20:00 - 24:59", null, null),
    BOOKINGS_16(3016, "EB 25:00 - ET", "罚牌加时赛 25:00 - ET", null, null),
    BOOKINGS_17(3017, "2ET BK", "罚牌加时赛下半场", null, null),//"3014,3015,3016"),

    BOOKINGS_18(3018, "ET BK", "罚牌加时赛全场", null, "3013,3017"),
    BOOKINGS_19(3041, "1st Half Bookings -red card", "上半场红牌", null, null),



    BK_NEXT_1(3019, "1HT", "罚牌上半场\n罚牌次序", null, null),
    BK_NEXT_2(3020, "2HT", "罚牌下半场\n罚牌次序", null, null),
    BK_NEXT_3(3021, "Player", "罚牌常规赛\n受罚球员", null, null),
    BK_NEXT_4(3022, "1ET", "罚牌加时赛上半场\n罚牌次序", null, null),
    BK_NEXT_5(3023, "2ET", "罚牌加时赛下半场\n罚牌次序", null, null),
    BK_NEXT_6(3024, "Player", "罚牌加时赛\n受罚球员", null, null),


    type_9(10009, "Pre-Match", "赛前盘", null, null),
    type_10(10010, "In-Play", "滚球盘", null, null),
    type_11(10011, "No Offer", "未开售", null, null),
    ;

    private final Integer code;
    private final String value;
    private final String name;
    private final String parentSettleNums;
    private final String childSettleNums;

    FootballPeriodValidateEnum(Integer code, String value, String name, String parentSettleNums, String childSettleNums) {
        this.code = code;
        this.value = value;
        this.name = name;
        this.parentSettleNums = parentSettleNums;
        this.childSettleNums = childSettleNums;
    }

    public static FootballPeriodValidateEnum getEnum(String code) {
        if (code == null) {
            return null;
        }
        for (FootballPeriodValidateEnum matchPeriodEnum : FootballPeriodValidateEnum.values()) {
            if (matchPeriodEnum.getCode().toString().equals(code)) {
                return matchPeriodEnum;
            }
        }
        return null;
    }

    public static List<String> getChildSettleNumList(String settleNum) {
        List<String> result = new ArrayList<>();
        FootballPeriodValidateEnum matchPeriodEnum = FootballPeriodValidateEnum.getEnum(settleNum);
        if (matchPeriodEnum == null || matchPeriodEnum.getChildSettleNums() == null) {
            return result;
        }
        result.addAll(Arrays.asList(matchPeriodEnum.getChildSettleNums().split(",")));
        return result;
    }

    public static String getParentSettleNumList(String settleNum) {
        FootballPeriodValidateEnum matchPeriodEnum = FootballPeriodValidateEnum.getEnum(settleNum);
        if (matchPeriodEnum == null || matchPeriodEnum.getParentSettleNums() == null) {
            return null;
        }
        return matchPeriodEnum.getParentSettleNums();
    }

    public static Boolean isAlreadySettleLinkId(String linkId, Long standardMatchId){
        for (FootballPeriodValidateEnum matchPeriodEnum : FootballPeriodValidateEnum.values()) {
            if (linkId.equals(standardMatchId+"_"+matchPeriodEnum.getCode())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
