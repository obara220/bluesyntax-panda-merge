package com.panda.merge.common.enums;

public enum MatchLeagueLevelEnum {


    League_Level_0(0, "0级别联赛", "0stLeague"),
    League_Level_1(1, "一级联赛", "1stLeague"),
    League_Level_2(2, "二级联赛", "2stLeague"),
    League_Level_3(3, "三级联赛", "3stLeague"),
    League_Level_4(4, "四级联赛", "4stLeague"),
    League_Level_5(5, "五级联赛", "5stLeague"),
    League_Level_6(6, "六级联赛", "6stLeague"),
    League_Level_7(7, "七级联赛", "7stLeague"),
    League_Level_8(8, "八级联赛", "8stLeague"),
    League_Level_9(9, "九级联赛", "9stLeague"),
    League_Level_10(10, "十级联赛", "10stLeague"),
    League_Level_11(11, "十一级联赛", "11stLeague"),
    League_Level_12(12, "十二级联赛", "12stLeague"),
    League_Level_13(13, "十三级联赛", "13stLeague"),
    League_Level_14(14, "十四级联赛", "14stLeague"),
    League_Level_15(15, "十五级联赛", "15stLeague"),
    League_Level_16(16, "十六级联赛", "16stLeague"),
    League_Level_17(17, "十七级联赛", "17stLeague"),
    League_Level_18(18, "十八级联赛", "18stLeague"),
    League_Level_19(19, "十九级联赛", "19stLeague"),
    League_Level_20(20, "二十级联赛", "20stLeague"),


    ;

    private Integer code;

    private String value;

    private String name;

    MatchLeagueLevelEnum(Integer code, String name, String value) {
        this.code = code;
        this.value = value;
        this.name = name;
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


    public static String getEnumByZs(String code) {
        for (MatchLeagueLevelEnum matchLeagueLevelEnum : MatchLeagueLevelEnum.values()) {
            if (matchLeagueLevelEnum.getCode().toString().equals(code)) {
                return matchLeagueLevelEnum.getName();
            }
        }
        return code;
    }

    public static String getEnumByEn(String code) {
        for (MatchLeagueLevelEnum matchLeagueLevelEnum : MatchLeagueLevelEnum.values()) {
            if (matchLeagueLevelEnum.getCode().toString().equals(code)) {
                return matchLeagueLevelEnum.getValue();
            }
        }
        return code;
    }

}
