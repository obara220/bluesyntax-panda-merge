package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 重播状态
 */
@Getter
public enum ReplayStatusEnum {

    STOP(0,"停止重播"),
    RUN(1,"正在重播");


    private Integer code;
    private String msg;

    ReplayStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ReplayStatusEnum get(String code){
        for(ReplayStatusEnum dataSourceEnum : ReplayStatusEnum.values()){
            if(dataSourceEnum.getCode().equals(code)){
                return dataSourceEnum;
            }
        }
        return null;
    }

}
