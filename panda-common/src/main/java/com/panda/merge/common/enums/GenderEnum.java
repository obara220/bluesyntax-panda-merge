package com.panda.merge.common.enums;


import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 性别枚举
 * @author   tell
 * @since    2020年9月4日10:20:37
 * */
@Getter
public enum GenderEnum {
    MAN(1,"男"),
    WOMAN(2,"女"),
    OTHER(0,"其它")
    ;
    public Integer code;
    public String msg;

    GenderEnum(Integer code,String msg) {
        this.code = code;
        this.msg = msg;
    }
    /**
     * 获取性别列表
     * */
    public static List<Integer> getGenderList(){
        return Arrays.asList(MAN.getCode(), WOMAN.getCode(), OTHER.getCode());
    }

}
