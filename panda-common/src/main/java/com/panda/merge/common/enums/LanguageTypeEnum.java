package com.panda.merge.common.enums;

/**
 * @author : nonhung
 * @project Name : data-nonrealtime
 * @package Name : com.panda.sport.data.nonrealtime.enums
 * @description : TODO
 * @date: 2020-09-10 19:44
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
public enum LanguageTypeEnum {
    zs(1, "中文简体"),
    zh(2, "中文繁体"),
    en(3, "英文"),
    jc(4, "简体"),
    es(5, "西班牙语"),
    it_IT(6, "意大利语"),
    de_DE(7, "德语"),
    fr_FR(8, "法语"),
    pt(9, "葡萄牙语"),
    ru(10, "俄语"),
    ja(11, "日语"),
    ko(12, "韩语"),
    th(13, "泰语"),
    vi(14, "越南语"),
    ms(15, "马来语"),
    ad(16, "印尼语"),
    mya(23, "缅甸语"),
    hk(24, "中文繁译"),
    ;
    private Integer code;

    private String value;

    LanguageTypeEnum(Integer code, String val) {
        this.code = code;
        this.value = val;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
