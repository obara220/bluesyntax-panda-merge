package com.panda.merge.common.enums;


import lombok.Getter;

/**
 * 进球时间
 * @author   idol
 * @since    2022年2月16日13:42:55
 * */
@Getter
public enum GoalTimeEnum {
    Goal_1(1,  "sr:player:","谁先开球"),
    Goal_2(2,  "00:00 - 14:59","上半场前15分钟"),
    Goal_3(3,  "15:00 - 29.59","上半场15分钟-30分钟"),
    Goal_4(4,  "30:00 - 1HT","30分钟-上半场结束"),
    Goal_5(5,  "1HT","上半场"),
    Goal_6(6,  "1HT - 59:59","下半场前15分钟"),
    Goal_7(7,  "60:00 - 74:59","60分钟-75分钟"),
    Goal_8(8,  "75:00 - FT","75分钟到结束"),
    Goal_9(9,  "2HT","下半场"),
    Goal_10(10,  "FT","全场"),
    Goal_11(11,  "ET 00:00 - 04:59","加时赛"),
    Goal_12(12,  "",""),
    Goal_13(13,  "",""),
    Goal_14(14,  "",""),
    Goal_15(15,  "",""),
    Goal_16(16,  "",""),
    Goal_17(17,  "",""),
    Goal_18(18,  "",""),
    Goal_19(19,  "",""),
    Goal_20(20,  "",""),
    Goal_21(21,  "",""),
   ;

    public Integer code;

    private String value;
    //中文名稱
    private String name;

    GoalTimeEnum(Integer code, String value, String name) {
        this.code = code;
        this.value = value;
        this.name = name;
    }

    public static GoalTimeEnum getDataByCode(Integer code){
        for(GoalTimeEnum dataSourceEnum : GoalTimeEnum.values()){
            if(dataSourceEnum.getCode() == (code)){
                return dataSourceEnum;
            }
        }
       return null;
    }

}
