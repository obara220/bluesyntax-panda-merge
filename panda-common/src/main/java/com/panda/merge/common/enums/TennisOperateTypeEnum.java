package com.panda.merge.common.enums;

/**
 * @author :  idol
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.enums
 * @description :  网球操作类型枚举
 * @date: 2023-3-22 15:24:17
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum TennisOperateTypeEnum {

    //报球版日志


    SCORES_PD_10049(10049, "比赛开始","Match Begin" ),
    SCORES_PD_10050(10050, "选择赛制","Choose Match" ),
    SCORES_PD_10051(10051, "第X盘,第X局","SET X,GAMES X" ),
    SCORES_PD_10052(10052, "比赛结束","Match Ended" ),
    SCORES_PD_10053(10053, "比赛中断","Match Postponed" ),
    SCORES_PD_10054(10054, "比赛推迟","Match Interrupted" ),
    SCORES_PD_10055(10055, "比赛取消","Match Canceled" ),
    SCORES_PD_10056(10056, "比赛恢复","Match Recover" ),
    SCORES_PD_10057(10057, "设置赛制","Match Round Type" ),
    SCORES_PD_10058(10058, "调整局制","Match Length" ),
    SCORES_PD_10059(10059, "录入局内比分","Add Scores" ),
    SCORES_PD_10060(10060, "第X盘,第X局开始","SET X,GAMES X Begin" ),
    SCORES_PD_10061(10061, "第X盘,第X局结束","SET X,GAMES X End" ),
    SCORES_PD_10062(10062, "设置盘最大局数","Set Max Round" ),
    SCORES_PD_10063(10063, "重新计算盘比分","ReCount Set Score" ),



    ;

    private Integer code;

    private String value;

    private String name;

    TennisOperateTypeEnum(Integer code, String name, String value ) {
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


    public static TennisOperateTypeEnum getEnum(String code) {
        for (TennisOperateTypeEnum operateLogTypeEnum : TennisOperateTypeEnum.values()) {
            if (operateLogTypeEnum.getCode().toString().equals(code)) {
                return operateLogTypeEnum;
            }
        }
        return null;
    }

    public static String getEnumByZs(String code) {
        for (TennisOperateTypeEnum operateLogTypeEnum : TennisOperateTypeEnum.values()) {
            if (operateLogTypeEnum.getCode().toString().equals(code)) {
                return operateLogTypeEnum.getName();
            }
        }
        return code;
    }

    public static String getEnumByEn(String code) {
        for (TennisOperateTypeEnum operateLogTypeEnum : TennisOperateTypeEnum.values()) {
            if (operateLogTypeEnum.getCode().toString().equals(code)) {
                return operateLogTypeEnum.getValue();
            }
        }
        return code;
    }
}
