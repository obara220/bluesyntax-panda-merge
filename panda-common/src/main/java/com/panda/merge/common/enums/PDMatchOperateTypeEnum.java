package com.panda.merge.common.enums;

/**
 * @author :  idol
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.enums
 * @description :  报球版足、蓝、冰操作类型枚举
 * @date: 2023-3-22 15:24:17
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum PDMatchOperateTypeEnum {

    //报球版足、蓝、冰操作类型
    PD_Match_100103(100103, "修改开赛时间", "Modify Match Time"),
    PD_Match_100104(100104, "修改开赛日期", "Modify Match Date"),
    PD_Match_10088(10088, "修改赛事时间", "Modify In-Play Time"),
    PD_Match_100121(100121, "修改比分", "Modify Score"),
    PD_Match_10064(10064, "删除事件", "Delete Event"),
    PD_Match_10065(10065, "点击", "Click"),
    PD_Match_100106(100106, "开球", "Kick-Off"),
    PD_Match_100119(100119, "比赛结束", "Match Ended"),
    PD_Match_100120(100120, "开关封锁", "Offer Status"),
    PD_Match_10090(10090, "结算冻结", "Stop Settlement"),
    PD_Match_10093(10093, "取消冻结", "Resume Settlement"),
    PD_Match_10097(10097, "赛事阶段", "Match Period"),
    PD_Match_100112(100112, "开始赛事时间", "Match Time Starts"),
    PD_Match_100071(100071, "报球板参与结算开关", "PD Settlement Switch"),

    PD_Match_100113(100113, "暂停赛事时间", "Match Time Stops"),
    PD_Match_10094(10094, "得分", "Point"),
    PD_Match_10089(10089, "倒计时 +/-", "Countdown +/-"),
    PD_Match_100134(100134, "赛制时间", "Format Period"),
    PD_Match_100073(100073, "比分编辑", "Edit Score"),
    PD_Match_100074(100074, "取消结束", "Cancel Match End"),

    ;

    private Integer code;

    private String value;

    private String name;

    PDMatchOperateTypeEnum(Integer code, String name, String value) {
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


    public static PDMatchOperateTypeEnum getEnum(String code) {
        for (PDMatchOperateTypeEnum operateLogTypeEnum : PDMatchOperateTypeEnum.values()) {
            if (operateLogTypeEnum.getCode().toString().equals(code)) {
                return operateLogTypeEnum;
            }
        }
        return null;
    }

    /**
     * 用于报球板日志查询条件,转义判断中、英文名称，获取对应的code,
     * 来查询数据库里面的操作类型对应的code
     *
     * @param lang
     * @param name
     * @return
     */
    public static String getCodeByValue(String lang, String name) {

        String result = name;
        for (PDMatchOperateTypeEnum pdMatchOperateTypeEnum : PDMatchOperateTypeEnum.values()) {
            switch (lang) {
                case "cn":
                    if (pdMatchOperateTypeEnum.getName().toString().equals(name)) {
                        return pdMatchOperateTypeEnum.getCode().toString().substring(1);
                    }
                    break;
                case "en":
                    if (pdMatchOperateTypeEnum.getValue().toString().equals(name)) {
                        return pdMatchOperateTypeEnum.getCode().toString().substring(1);
                    }
                    break;
            }
        }
        return result;
    }

}
