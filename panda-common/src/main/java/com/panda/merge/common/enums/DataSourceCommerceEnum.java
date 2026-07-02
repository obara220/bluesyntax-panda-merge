package com.panda.merge.common.enums;

/**
 * 数据源商业来源枚举类
 * @author :  tell
 * @since   2020年9月12日10:31:12
 */
public enum DataSourceCommerceEnum {

    /**
     * 商业来源
     */
    COMMERCE(1, "商业数据源"),

    /**
     * 非商业
     */
    NON_COMMERCE(0, "非商业数据源");

    private Integer code;

    private String val;

    DataSourceCommerceEnum(Integer code, String val) {
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
