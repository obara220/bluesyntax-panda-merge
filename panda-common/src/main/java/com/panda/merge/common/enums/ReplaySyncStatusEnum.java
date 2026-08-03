package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 重播同步状态
 */
@Getter
public enum ReplaySyncStatusEnum {

    NOT_SYNC(0,"未拉取"),
    SYNCING(1,"拉取中"),
    SYNCED(2,"已拉取");

    private Integer code;
    private String msg;

    ReplaySyncStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ReplaySyncStatusEnum get(String code){
        for(ReplaySyncStatusEnum dataSourceEnum : ReplaySyncStatusEnum.values()){
            if(dataSourceEnum.getCode().equals(code)){
                return dataSourceEnum;
            }
        }
        return null;
    }

}
