package com.panda.merge.common.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author :  idol
 * @description :  结算编码枚举
 * @date: 2022-2-20 18:10:53
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum MatchPeriodEnum {

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
    GOAL_1(101, "Kick-off", "谁先开球", null, null, 1),
    GOAL_2(102, "00:00 - 14:59", "进球 00:00 - 14:59", null, "1034,1035,1036", 5), //"1034,1035,1036"
    GOAL_3(103, "15:00 - 29:59", "进球 15:00 - 29:59", null, "1037,1038,1039",9), //"1037,1038,1039"),
    GOAL_4(104, "30:00 - 1HT", "进球 30:00 - 1HT", null, "1040,1041,1042,1043",14), //"1040,1041,1042,1043"),
    GOAL_5(105, "1HT", "进球上半场", null, "102,103,104",15),
    GOAL_6(106, "1HT - 59:59", "进球 1HT - 59:59", null, "1044,1045,1046",19), //"1044,1045,1046"),
    GOAL_7(107, "60:00 - 74:59", "进球 60:00 - 74:59", null, "1047,1048,1049",23), //"1047,1048,1049"),
    GOAL_8(108, "75:00 - FT", "进球 75:00 - FT", null, "1050,1051,1052,1053",28), //"1050,1051,1052,1053"),
    GOAL_9(109, "2HT", "进球下半场", null, "106,107,108",29),
    GOAL_10(1010, "FT", "进球全场(常规赛)", null, "105,109",30),
    GOAL_11(1011, "ET 00:00 - 04:59", "进球加时赛 00:00 - 04:59", null, null,31),
    GOAL_12(1012, "ET 05:00 - 09:59", "进球加时赛 05:00 - 09:59", null, null,32),
    GOAL_13(1013, "ET 10:00 - 1ET", "进球加时赛 10:00 - 1ET", null, null,33),
    GOAL_14(1014, "1ET", "进球加时赛上半场", null, null, 34),//"1011,1012,1013"),
    GOAL_15(1015, "ET 1ET - 19:59", "进球加时赛 1ET - 19:59", null, null,35),
    GOAL_16(1016, "20:00 - 24:59", "进球加时赛 20:00 - 24:59", null, null,36),
    GOAL_17(1017, "25:00 - ET", "进球加时赛 25:00 - ET", null, null,37),
    GOAL_18(1018, "2ET", "进球加时赛下半场", null, null,38),//"1015,1016,1017"),
    GOAL_19(1019, "ET", "进球加时赛全场", null, "1014,1018",39),
    GOAL_20(1020, "Winner / To Qualify", "获胜 / 晋级", null, null,9999),
    GOAL_21(1021, "Winning Method", "获胜方式", null, null,9999),

    GOAL_NG_1(1022, "1HT", "上半场进球次序", null, null,9999),
    GOAL_NG_PHASE_1(10225, "1HT", "上半场进球时段", null, null,9999),
    GOAL_NG_2(1023, "2HT", "下半场进球次序", null, null,9999),
    GOAL_NG_PHASE_2(10235, "2HT", "下半场进球时段", null, null,9999),
    GOAL_NG_3(1024, "Player & Goal Method", "进球球员 & 进球方式(常规赛)", null, null,9999),
    GOAL_NG_4(1025, "1ET", "加时赛上半场", null, null,9999),
    GOAL_NG_5(1026, "2ET", "加时赛下半场", null, null,9999),
    GOAL_NG_6(1027, "Player & Goal Method", "进球球员 & 进球方式(加时赛)", null, null,9999),

    /**
     * 点球大战
     */
    GOAL_PENALTY_1(1028, "Total PEN", "点球大战总比分", null, null,9999),
    GOAL_PENALTY_2(1029, "Total 1-5", "点球大战前五轮比分", null, null,9999),
    GOAL_PENALTY_3(1030, "Shoot-out", "点球大战", null, null,9999),
    GOAL_PENALTY_33(-1030, "Shoot-out First", "点球大战谁先射门", null, null,9999),
    GOAL_PENALTY_4(1031, "Penalty Shoot-out", "是否点球大战", null, null,9999),
    GOAL_PENALTY_5(1032, "No Extra Time & Penalty Shoot-out (Return)", "没有进行加时赛 & 点球大战 (走水)", null, null,9999),
    GOAL_PENALTY_6(1033, "No Penalty Shoot-out (Return)", "没有进行点球大战 (走水)", null, null,9999),


    /**
     * 五分钟比分
     */
    GOAL_5minute_1034(1034, "0:00 - 4:59", "0:00 - 4:59", null, null,2),
    GOAL_5minute_1035(1035, "5:00 - 9:59", "5:00 - 9:59", null, null,3),
    GOAL_5minute_1036(1036, "10:00 - 14:59", "10:00 - 14:59", null, null,4),
    GOAL_5minute_1037(1037, "15:00 - 19:59", "15:00 - 19:59", null, null,6),
    GOAL_5minute_1038(1038, "20:00 - 24:59", "20:00 - 24:59", null, null,7),
    GOAL_5minute_1039(1039, "25:00 - 29:59", "25:00 - 29:59", null, null,8),
    GOAL_5minute_1040(1040, "30:00 - 34:59", "30:00 - 34:59", null, null,10),
    GOAL_5minute_1041(1041, "35:00 - 39:59", "35:00 - 39:59", null, null,11),
    GOAL_5minute_1042(1042, "40:00 - 45:00 (excluded injury time)", "40:00 - 45:00 (不含补时)", null, null,12),
    GOAL_5minute_1043(1043, "1H Last-minute Goal (Injury Time)", "上半场 绝杀球", null, null,13),

    GOAL_5minute_1044(1044, "2H - 49:59", "下半场 - 49:59", null, null,16),
    GOAL_5minute_1045(1045, "50:00 - 54:59", "50:00 - 54:59", null, null,17),
    GOAL_5minute_1046(1046, "55:00 - 59:59", "55:00 - 59:59", null, null,18),
    GOAL_5minute_1047(1047, "60:00 - 64:59", "60:00 - 64:59", null, null,20),
    GOAL_5minute_1048(1048, "65:00 - 69:59", "65:00 - 69:59", null, null,21),
    GOAL_5minute_1049(1049, "70:00 - 74:59", "70:00 - 74:59", null, null,22),
    GOAL_5minute_1050(1050, "75:00 - 79:59", "75:00 - 79:59", null, null,24),
    GOAL_5minute_1051(1051, "80:00 - 84:59", "80:00 - 84:59", null, null,25),
    GOAL_5minute_1052(1052, "85:00 - 90:00 (excluded injury time)", "85:00 - 90:00 (不含补时)", null, null,26),
    GOAL_5minute_1053(1053, "2H Last-minute Goal (Injury Time)", "下半场 绝杀球", null, null,27),
    GOAL_5minute_1054(1054, "Shoot-out (Return)", "点球大战 (走水)", null, null,9999),
    GOAL_5minute_0(0, "No Goal", "没有进球", null, null,9999),


    /**
     * * 角球
     */
    Corner_15m_1(2011, "CR 00:00 - 14:59", "角球 00:00 - 14:59", null, null, 100),
    Corner_15m_2(2012, "CR 15:00 - 29:59", "角球 15:00 - 29:59", null, null,101),
    Corner_15m_3(2013, "CR 30:00 - HT", "角球 30:00 - HT", null, null,102),

    Corner_1(201, "1HT CR", "角球上半场", null, null,103),//"2011,2012,2013"),

    Corner_15m_4(2014, "CR HT - 59:59", "角球 HT - 59:59", null, null,104),
    Corner_15m_5(2015, "CR 60:00 - 74:59", "角球 60:00 - 74:59", null, null,105),
    Corner_15m_6(2016, "CR 75:00 - FT", "角球 75:00 - FT", null, null,106),

    Corner_2(202, "2HT CR", "角球下半场", null, null,107),//"2014,2015,2016"),
    Corner_3(203, "FT CR", "角球全场(常规赛)", null, "201,202",108),


    Corner_15m_7(2017, "EC 00:00 - 04:59", "加时赛角球 00:00 - 04:59", null, null,109),
    Corner_15m_8(2018, "EC 05:00 - 09:45", "加时赛角球 05:00 - 09:45", null, null,110),
    Corner_15m_9(2019, "EC 10:00 - 1ET", "加时赛角球 10:00 - 1ET", null, null,111),

    Corner_6(206, "1ET CR", "角球加时上半场", null, null, 112),//"2017,2018,2019"),

    Corner_15m_10(2020, "EC 1ET - 19:59", "加时赛角球 1ET - 19:59", null, null,113),
    Corner_15m_11(2021, "EC 20:00 - 24:59", "加时赛角球 20:00 - 24:59", null, null,114),
    Corner_15m_12(2022, "EC 25:00 - ET", "加时赛角球 25:00 - ET", null, null,115),

    Corner_7(207, "2ET CR", "角球加时下半场", null, null,116),//"2020,2021,2022"),
    Corner_8(208, "ET CR", "角球加时全场", null, "206,207",117),


    Corner_9(209, "1EC", "角球加时上半场角球次序", null, null,9999),
    Corner_10(2010, "2EC", "角球加时下半场角球次序", null, null,9999),
    Corner_4(204, "1 CR", "角球上半场角球次序", null, null,9999),
    Corner_PHASE_4(2045, "1 CR", "角球上半场角球时段", null, null,9999),
    Corner_5(205, "2 CR", "角球下半场角球次序", null, null,9999),
    Corner_PAHSE_5(2055, "2 CR", "角球下半场角球时段", null, null,9999),


    /**
     * 罚牌
     */
    BOOKINGS_1(301, "BK 00:00 - 14:59", "罚牌 00:00 - 14:59", null, null, 200),
    BOOKINGS_2(302, "BK 15:00 - 29:59", "罚牌 15:00 - 29:59", null, null,201),
    BOOKINGS_3(303, "BK 30:00 - 1HT", "罚牌 30:00 - 1HT", null, null,202),

    BOOKINGS_4(304, "BK 1HT", "罚牌上半场", null, "301,302,303",203),

    BOOKINGS_5(305, "BK 1HT - 59:59", "罚牌 1HT - 59:59", null, null,205),
    BOOKINGS_6(306, "BK 60:00 - 74:59", "罚牌 60:00 - 74:59", null, null,206),
    BOOKINGS_7(307, "BK 75:00 - FT", "罚牌 75:00 - FT", null, null,207),

    BOOKINGS_8(308, "BK 2HT", "罚牌下半场", null, "305,306,307",207),
    BOOKINGS_9(309, "BK FT", "罚牌全场(常规赛)", null, "304,308",208),

    BOOKINGS_10(3010, "EB 00:00 - 04:59", "罚牌加时赛 00:00 - 04:59", null, null, 209),
    BOOKINGS_11(3011, "EB 05:00 - 09:45", "罚牌加时赛 05:00 - 09:45", null, null,210),
    BOOKINGS_12(3012, "EB 10:00 - 1ET", "罚牌加时赛 10:00 - 1ET", null, null,211),

    BOOKINGS_13(3013, "1ET BK", "罚牌加时赛上半场", null, null, 212),//"3010,3011,3012"),

    BOOKINGS_14(3014, "EB 1ET - 19:59", "罚牌加时赛 1ET - 19:59", null, null,213),
    BOOKINGS_15(3015, "EB 20:00 - 24:59", "罚牌加时赛 20:00 - 24:59", null, null,214),
    BOOKINGS_16(3016, "EB 25:00 - ET", "罚牌加时赛 25:00 - ET", null, null,215),
    BOOKINGS_17(3017, "2ET BK", "罚牌加时赛下半场", null, null,216),//"3014,3015,3016"),

    BOOKINGS_18(3018, "ET BK", "罚牌加时赛全场", null, "3013,3017",217),
    BOOKINGS_19(3041, "1st Half Bookings -red card", "上半场红牌", null, null, 204),



    BK_NEXT_1(3019, "1HT", "罚牌上半场\n罚牌次序", null, null,9999),
    BK_NEXT_PHASE_1(30195, "1HT", "罚牌上半场\n罚牌时段", null, null,9999),
    BK_NEXT_2(3020, "2HT", "罚牌下半场\n罚牌次序", null, null,9999),
    BK_NEXT_PHASE_2(30205, "2HT", "罚牌下半场\n罚牌时段", null, null,9999),
    BK_NEXT_3(3021, "Player", "罚牌常规赛\n受罚球员", null, null,9999),
    BK_NEXT_4(3022, "1ET", "罚牌加时赛上半场\n罚牌次序", null, null,9999),
    BK_NEXT_5(3023, "2ET", "罚牌加时赛下半场\n罚牌次序", null, null,9999),
    BK_NEXT_6(3024, "Player", "罚牌加时赛\n受罚球员", null, null,9999),


    type_9(10009, "Pre-Match", "赛前盘", null, null,9999),
    type_10(10010, "In-Play", "滚球盘", null, null,9999),
    type_11(10011, "No Offer", "未开售", null, null,9999),


    ;

    private Integer code;

    private String value;

    private String name;

    private Integer period;

    private String settleNums;

    private Integer settleOrder;

    MatchPeriodEnum() {
    }

    public static Map<String, String> allNextPhases = new HashMap<String, String>() {{
        put("102", "103");
        put("103", "104");
        put("2011", "2012");
        put("2012", "2013");
        put("301", "302");
        put("302", "303");
        put("106", "107");
        put("107", "108");
        put("2014", "2015");
        put("2015", "2016");
        put("305", "306");
        put("306", "307");
        put("1034", "1035");
        put("1035", "1036");
//        put("1036", "1037");
        put("1037", "1038");
        put("1038", "1039");
//        put("1039", "1040");
        put("1040", "1041");
        put("1041", "1042");
        put("1042", "1043");
//        put("1043", "1044");
        put("1044", "1045");
        put("1045", "1046");
        put("1047", "1048");
        put("1048", "1049");
        put("1050", "1051");
        put("1051", "1052");
        put("1052", "1053");
    }};

    MatchPeriodEnum(Integer code, String value, String name, Integer period, String settleNums, Integer settleOrder) {
        this.code = code;
        this.value = value;
        this.name = name;
        this.period = period;
        this.settleNums = settleNums;
        this.settleOrder = settleOrder;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Integer getPeriod() {
        return period;
    }

    public String getSettleNums() {
        return settleNums;
    }
    public Integer getSettleOrder() {
        return settleOrder;
    }

    public static MatchPeriodEnum getEnum(String code) {
        for (MatchPeriodEnum matchPeriodEnum : MatchPeriodEnum.values()) {
            if (matchPeriodEnum.getCode().toString().equals(code)) {
                return matchPeriodEnum;
            }
        }
        return null;
    }

    /**
     * 根据当前的结算编码，获取到当前局的结算编码
     *
     * @param settleNum
     * @return
     */
    public static List<String> getFootBallPeriodSettleNumList(String settleNum) {

        if (StringUtils.isAnyEmpty(settleNum)) {
            return null;
        }
        MatchPeriodEnum matchPeriodEnum = MatchPeriodEnum.getEnum(settleNum);
        if (matchPeriodEnum == null) {
            return null;
        }
        if (StringUtils.isAnyEmpty(matchPeriodEnum.getSettleNums())) {
            return null;
        }
        return Arrays.asList(matchPeriodEnum.getSettleNums().split(","));
    }


    public static void main(String [] xx){
        MatchPeriodEnum matchPeriodEnum = MatchPeriodEnum.getEnum("102");
        if (matchPeriodEnum.getSettleNums() == null) {
            return ;
        }
        if (matchPeriodEnum.getPeriod() == null) {
            System.out.println( Arrays.asList(matchPeriodEnum.getSettleNums().split(",")));
        }
    }
}
