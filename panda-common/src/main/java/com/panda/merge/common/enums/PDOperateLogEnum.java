package com.panda.merge.common.enums;

/**
 * @author :  kenley
 * @project Name :  panda-scores-admin
 * @package Name :  com.panda.sports.manager.enums
 * @description :  操作类型枚举
 * @date: 2023-03-13 17:11:13
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum PDOperateLogEnum {


    /**
     * 足球赛事阶段中英文对照
     */
    SOCCER_PERIOD_0(10, "未开赛", "Not Started"),
    SOCCER_PERIOD_6(16, "上半场", "1st Half"),
    SOCCER_PERIOD_7(17, "下半场", "2nd Half"),
    SOCCER_PERIOD_31(131, "上半场结束/中场休息", "1st Half ended"),
    SOCCER_PERIOD_32(132, "等待加时开始", "Waiting for ET KO"),
    SOCCER_PERIOD_33(133, "加时赛休息", "ET Half Time"),
    SOCCER_PERIOD_34(134, "等待点球大战", "Waiting for PEN shootout"),
    SOCCER_PERIOD_41(141, "加时赛上半场", "1HT ET"),
    SOCCER_PERIOD_42(142, "加时赛下半场", "2HT ET"),
    SOCCER_PERIOD_50(150, "点球大战", "PEN Shootout"),
    SOCCER_PERIOD_80(180, "比赛中断", "Match Interrupted"),
    SOCCER_PERIOD_90(190, "比赛放弃", "Match Abandoned"),
    SOCCER_PERIOD_100(1100, "常规时间结束/全场结束", "Full Time"),
    SOCCER_PERIOD_110(1110, "加时赛结束", "ET ended"),
    SOCCER_PERIOD_120(1120, "点球大战结束", "PEN shootout ended"),
    SOCCER_PERIOD_999(1999, "比赛结束", "End"),
    SOCCER_PERIOD_998(1998, "未结束", "Not End"),

    /**
     * 篮球赛事阶段中英文对照
     */
    BASKETBALL_PERIOD_0(20, "未开赛", "Not Started"),
    BASKETBALL_PERIOD_1(21, "上半场", "1st Half"),
    BASKETBALL_PERIOD_2(22, "下半场", "2nd Half"),
    BASKETBALL_PERIOD_13(213, "第一节", "Q1"),
    BASKETBALL_PERIOD_21(221, "进行中", "In-Play"),
    BASKETBALL_PERIOD_31(231, "上半场结束/中场休息", "1st Half ended"),
    BASKETBALL_PERIOD_301(2301, "第一节结束", "Q1 ended"),
    BASKETBALL_PERIOD_14(214, "第二节", "Q2"),
    BASKETBALL_PERIOD_302(2302, "第二节结束", "Q2 ended"),
    BASKETBALL_PERIOD_15(215, "第三节", "Q3"),
    BASKETBALL_PERIOD_303(2303, "第三节结束", "Q3 ended"),
    BASKETBALL_PERIOD_16(216, "第四节", "Q4"),
    BASKETBALL_PERIOD_100(2100, "第四节结束", "Q4 ended"),
    BASKETBALL_PERIOD_32(232, "等待加时", "Waiting for ET"),
    BASKETBALL_PERIOD_40(240, "加时赛", "Extra Time"),
    BASKETBALL_PERIOD_110(2110, "加时赛结束", "ET ended"),
    BASKETBALL_PERIOD_61(261, "比赛推迟", "Match Delayed"),
    BASKETBALL_PERIOD_80(280, "比赛中断", "Match Interrupted"),
    BASKETBALL_PERIOD_90(290, "比赛放弃", "Match Abandoned"),
    BASKETBALL_PERIOD_999(2999, "比赛结束", "End"),

    /**
     * 冰球赛事阶段中英文对照
     */
    ICEHOCKEY_PERIOD_0(40, "未开始", "Not Started"),
    ICEHOCKEY_PERIOD_1(41, "第1节", "P1"),
    ICEHOCKEY_PERIOD_301(4301, "第1节结束", "P1 ended"),
    ICEHOCKEY_PERIOD_2(42, "第2节", "P2"),
    ICEHOCKEY_PERIOD_302(4302, "第2节结束", "P2 ended"),
    ICEHOCKEY_PERIOD_3(43, "第3节", "P3"),
    ICEHOCKEY_PERIOD_303(4303, "第3节结束", "P3 ended"),
    ICEHOCKEY_PERIOD_32(432, "等待加时", "Waiting for ET"),
    ICEHOCKEY_PERIOD_34(434, "等待点球", "Waiting for PEN shootout"),
    ICEHOCKEY_PERIOD_40(440, "加时赛", "Extra Time"),
    ICEHOCKEY_PERIOD_50(450, "点球大战", "PEN shootout"),
    ICEHOCKEY_PERIOD_80(480, "比赛中断", "Match Interrupted"),
    ICEHOCKEY_PERIOD_90(490, "比赛放弃", "Match Abandoned"),
    ICEHOCKEY_PERIOD_100(4100, "常规时间结束", "Full Time"),
    ICEHOCKEY_PERIOD_110(4110, "加时赛结束", "ET ended"),
    ICEHOCKEY_PERIOD_120(4120, "点球大战结束", "PEN shootout ended"),
    ICEHOCKEY_PERIOD_999(4999, "比赛结束", "End"),

    TENNIS(5, "网球", "tennis"),
    BADMINTON(10010, "羽毛球", "badminton"),
    TABLE_TENNIS(8, "乒乓球", "table tennis"),
    VOLLEYBALL(9, "排球", "volleyball"),
    FOOTBALL(1, "足球", "football"),
    BASKETBALL(2, "篮球", "basketball"),
    EDIT_SCORE_RESULT(10011,"编辑比分","edit score result"),
    ;

    private Integer code;

    private String enName;

    private String cnName;

    PDOperateLogEnum(Integer code, String cnName, String enName) {
        this.code = code;
        this.enName = enName;
        this.cnName = cnName;
    }

    public Integer getCode() {
        return code;
    }

    public String getEnName() {
        return enName;
    }

    public String getCnName() {
        return cnName;
    }

    public static PDOperateLogEnum getEnum(String code) {
        for (PDOperateLogEnum pdOperateLogEnum : PDOperateLogEnum.values()) {
            if (pdOperateLogEnum.getCode().toString().equals(code)) {
                return pdOperateLogEnum;
            }
        }
        return null;
    }

    public static String getEnNameByCode(String code) {
        for (PDOperateLogEnum pdOperateLogEnum : PDOperateLogEnum.values()) {
            if (pdOperateLogEnum.getCode().toString().equals(code)) {
                return pdOperateLogEnum.getEnName();
            }
        }
        return null;
    }

    public static String getCnNameByCode(String code) {
        for (PDOperateLogEnum pdOperateLogEnum : PDOperateLogEnum.values()) {
            if (pdOperateLogEnum.getCode().toString().equals(code)) {
                return pdOperateLogEnum.getCnName();
            }
        }
        return null;
    }

    /**
     * 用于报球板日志查询条件,转义判断中、英文名称，获取对应的code,
     * 用来查询数据库里面的上半场、下半场对应的code
     *
     * @param lang
     * @param name
     * @return
     */
    public static String getCodeByValue(String lang, String name) {

        for (PDOperateLogEnum pdOperateLogEnum : PDOperateLogEnum.values()) {
            switch (lang) {
                case "cn":
                    if (pdOperateLogEnum.getCnName().toString().equals(name)) {
                        return pdOperateLogEnum.getCode().toString().substring(1);
                    }
                    break;
                case "en":
                    if (pdOperateLogEnum.getEnName().toString().equals(name)) {
                        return pdOperateLogEnum.getCode().toString().substring(1);
                    }
                    break;
            }
        }
        return name;
    }

}
