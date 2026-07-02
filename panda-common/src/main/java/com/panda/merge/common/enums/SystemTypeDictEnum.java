package com.panda.merge.common.enums;


import lombok.Getter;

/**
 * 字典类型枚举 对应数据表 system_item_dict
 * @author   tell
 * @since    2020年9月4日10:20:37
 * */
@Getter
public enum SystemTypeDictEnum {
    /** 球队类型*/
    SPORT_TEAM_TYPE(9L, "球队类型"),
    /** 场地类型*/
    POSITION_TYPE(10L, "场地类型"),
    /** 比赛阶段类型*/
    MATCH_PERIOD(8L,"比赛阶段类型")
    ;
    /** id*/
    public Long code;
    /** value*/
    public String msg;

    SystemTypeDictEnum(Long code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
