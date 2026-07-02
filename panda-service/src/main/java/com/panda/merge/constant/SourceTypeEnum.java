package com.panda.merge.constant;

/**
 * @author :  kb
 * @package Name :  com.panda.merge.constant;
 * @description :
 * @date: 2019-10-31 15:49
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum SourceTypeEnum {

    UOF(0, "UOF"),
    LIVE_DATA(1, "Live Data");

    private Integer code;

    private String val;

    SourceTypeEnum(Integer code, String val) {
        this.code = code;
        this.val = val;
    }

    public Integer getCode() {
        return code;
    }

    public String getVal() {
        return val;
    }
}
