package com.panda.merge.advertise.dubbo.myfiles;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author warren
 * @since 2024/01/06 13:29:43
 */
public enum DataSourceCodeEnum {
    SR("SR", "1"),
    BC("BC", "2"),
    BG("BG", "3"),
    TX("TX", "4"),
    PI("PI", "12"),
    AO("AO", "13"),
    One88("188", "20"),
    QT("QT", "21"),
    SBO("SBO", "22"),
    SBA("SBA", "23"),
    TS("TS", "24"),
    PA("PA", "99"),
    RB("RB", "10"),
    V2("V2", "99"),
    PD("PD", "88"),
    PD2("PD2", "89"),
    LS("LS","14"),
    BT("BT","19"),
    OneX("1X","20"),
    BE("BE","21"),
    KO("KO","22"),
    RC("RC","23"),
    OD("OD","24"),
    N01("N01","28"),
    N02("N02","29"),
    SK("SK","30");

    public String code;
    public String id;

    DataSourceCodeEnum(String code, String id) {
        this.code = code;
        this.id = id;
    }

    /**
     * 通过code获取对应的id信息
     *
     * @param code
     * @return java.lang.String
     * @description 通过code获取对应的id信息
     **/
    public static String getIdByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        DataSourceCodeEnum[] values = DataSourceCodeEnum.values();
        for (DataSourceCodeEnum e : values) {
            if (e.code.equals(code)) {
                return e.id;
            }
        }
        return "";
    }

    public String getCode() {
        return this.code;
    }


    /**
     * @description: 获取商业数据源code
     * @param:
     * @return:
     * @author nonhung
     * @date: 2021/8/12 21:21
     */
    public static List<String> getCommersCode(){
        List<String> list = new ArrayList<String>();
        list.add(DataSourceCodeEnum.SR.getCode());
        list.add(DataSourceCodeEnum.BG.getCode());
        list.add(DataSourceCodeEnum.RB.getCode());
        list.add(DataSourceCodeEnum.TX.getCode());
        list.add(DataSourceCodeEnum.BC.getCode());
        list.add(DataSourceCodeEnum.PD.getCode());
        list.add(DataSourceCodeEnum.PI.getCode());
        list.add(DataSourceCodeEnum.AO.getCode());
        list.add(DataSourceCodeEnum.LS.getCode());
        list.add(DataSourceCodeEnum.BT.getCode());
        list.add(DataSourceCodeEnum.OneX.getCode());
        list.add(DataSourceCodeEnum.BE.getCode());
        list.add(DataSourceCodeEnum.KO.getCode());
        list.add(DataSourceCodeEnum.OD.getCode());
        list.add(DataSourceCodeEnum.N01.getCode());
        list.add(DataSourceCodeEnum.N02.getCode());
        return list;
    }
}
