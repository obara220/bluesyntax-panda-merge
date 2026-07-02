package com.panda.merge.common.enums;

/**
 * @author :  idol
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.enums
 * @description :  TODO
 * @date: 2022-2-8 15:13:13
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum GoalTypeEnum {
/**
 * 取值:0 1 2 3 -100 -200.其中:
 * 0 = 未知;-1 = 没有;1 = 罚球;2 = 乌龙球;3 = 头球;-100 = 射门;-200 = 任意球;
 */
    /**未说明 */
    UNKNOWN(0, "未知", "Unknown"),
    /**未确认*/
    NONE(-1, "没有", "None"),
    /**点球*/
    Penalty(1, "罚球", "Penalty"),
    /**乌龙球*/
    OWN_GOAL(2, "乌龙球", "Own Goal"),
    /**头球*/
    HEADER(3, "头球", "Header"),
    /**射门*/
    SHOT(-100, "射门", "Shot"),
    /**任意球*/
    FREE_KICK(-200, "任意球", "Free Kick"),
    /**走水*/
    GOWATER(900, "走水","Return"),

    //大于1000不会在进球类型中显示,仅用于操作日志使用
    /**没有进球*/
    NO_GOAL(1000, "没有进球", "no goal"),
    /**未选择*/
    UNSELECTED(1001, "未选择", "unselected"),


    ;

    private Integer code;

    private String zhValue;

    private String enValue;

    public static GoalTypeEnum getTypeByCode(String code) {
        for (GoalTypeEnum item : GoalTypeEnum.values()) {
            if (item.getEnValue().toString().equals(code)) {
                return item;
            }
        }
        return null;
    }


    public static GoalTypeEnum getType(String code) {
        for (GoalTypeEnum goalTypeEnum:GoalTypeEnum.values()) {
            if (goalTypeEnum.getCode().toString().equals(code)) {
                return  goalTypeEnum;
            }
        }
        return null;
    }

    GoalTypeEnum() {
    }

    GoalTypeEnum(Integer code, String zhValue, String enValue) {
        this.code = code;
        this.zhValue = zhValue;
        this.enValue = enValue;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getZhValue() {
        return zhValue;
    }

    public void setZhValue(String zhValue) {
        this.zhValue = zhValue;
    }

    public String getEnValue() {
        return enValue;
    }

    public void setEnValue(String enValue) {
        this.enValue = enValue;
    }


}
