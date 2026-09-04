package com.panda.merge.common.enums;


import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

/**
 * 数据商编码脱敏
 *
 * @author titan
 * @since 2024-1-30 15:17
 */

public enum DataSourceEncrypEnum {


    AO("AO", "A01"),
    BC("BC", "B02"),
    BE("BE", "B03"),
    BT("BT", "B04"),
    RC("RC", "C01"),
    DJ("DJ", "DJ"),
    BG("BG", "G01"),
    GR("GR", "G02"),
    HB("HB", "H01"),
    IP("IP", "I01"),
    KO("KO", "K01"),
    LS("LS", "L01"),
    MY("MY", "M02"),
    OD("OD", "O01"),
    PD("PD", "PD"),
    PD2("PD2", "PD2"),
    RB("RB", "R01"),
    SR("SR", "S01"),
    TX("TX", "T01"),
    UF("UF", "U01"),
    V2("V2", "V01"),
    TS("TS", "V02"),
    V2G("V2G", "V11"),
    FTS("FTS", "FTS"),
    N01("N01", "N01"),
    N02("N02", "N02"),
    F01("F01", "F01");

    private String code;

    private String val;

    DataSourceEncrypEnum(String code, String val) {
        this.code = code;
        this.val = val;
    }

    public static String getDataSourceVal(String code){
        for (DataSourceEncrypEnum item:DataSourceEncrypEnum.values()){
            if (item.code.equals(code)){
                return item.val;
            }
        }
        return code;
    }

}
