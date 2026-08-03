package com.panda.merge.common.enums;


import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

/**
 * 允许推送的数据来源枚举类 对应数据表 data_source
 *
 * @author tell
 * @since 2020年9月4日10:20:37
 */
@Getter
public enum DataSourceCodeEnum {
    SR("SR", true, "sr:player:", "S01"),
    BC("BC", false, null, "B02"),
    BG("BG", false, null, "G01"),
    One88("188", false, null, "188"),
    QT("QT", false, null, "QT"),
    SBO("SBO", false, null, "SBO"),
    SBA("SBA", false, null, "SBA"),
    TS("TS", false, null, "V02"),
    MTS("MTS", false, null, "MTS"),
    CTS("CTS", false, null, "CTS"),
    GTS("GTS", false, null, "GTS"),
    OTS("OTS", false, null, "OTS"),
    BTS("BTS", false, null, "BTS"),
    F2TS("F2TS", false, null, "F2TS"),
    GR("GR", false, null, "G02"),
    TX("TX", false, null, "T01"),
    PA("PA", false, null, "PA"),
    RB("RB", false, null, "R01"),
    PD("PD", false, null, "PD"),
    PD2("PD2", false, null, "PD2"),
    V2("V2", false, null, "V01"),
    PI("PI", false, null, "PI"),
    AO("AO", false, null, "A01"),
    IM("IM", false, null, "IM"),
    LS("LS", false, null, "L01"),
    BT("BT", false, null, "B04"),
    OneX("1X", false, null, "1X"),
    BE("BE", false, null, "B03"),
    KO("KO", false, null, "K01"),
    BI("BI", false, null, "BI"),
    OD("OD", false, null, "O01"),
    RC("RC", false, null, "C01"),
    N01("N01", false, null, "N01"),
    N02("N02", false, null, "N02"),
    N03("N03", false, null, "N03"),
    L02("L02", false, null, "L02"),
    //S02数据源
    SK("SK", false, null, "S02"),
    FTS("FTS", false, null, "FTS"),
    FTS1("FTS1", false, null, "FTS1"),
    V03("V03", false, null, "V03"),
    V04("V04", false, null, "V04"),
    D01("D01", false, null, "D01"),
    F01("F01", false, null, "F01"),
    A99("A99", false, null, "A99"),

    ;
    public String code;
    /**
     * 该数据源的区域是否区分运动类型
     */
    public Boolean sportRegionFlag;
    /**
     * 2021年1月10日16:48:15 球员id前缀 （由于MatchEventInfoDTO中球员ID是Long类型，SR球员ID实际是String类型，如果该字段类型影响较大，所以这里需要配置球员前缀）
     */
    private String playerIdPrefix;

    private String maskedCode;

    DataSourceCodeEnum(String code, Boolean sportRegionFlag, String playerIdPrefix, String maskedCode) {
        this.code = code;
        this.sportRegionFlag = sportRegionFlag;
        this.playerIdPrefix = playerIdPrefix;
        this.maskedCode = maskedCode;
    }

    /**
     * 根据数据源code获取数据源枚举信息
     */
    public static DataSourceCodeEnum getDataSourceCodeEnumByCode(String code) {
        for (DataSourceCodeEnum dataSourceEnum : DataSourceCodeEnum.values()) {
            if (dataSourceEnum.getCode().equalsIgnoreCase(code)) {
                return dataSourceEnum;
            }
        }
        throw new RuntimeException(PandaErrorCodeEnum.DATASOURCE_NO_CHECK.getErrorMsg().replace("dataSourceCode", code));
    }

    /**
     * 根据数据源code判断当前数据源下区域是否区分运动类型，不区分返回0，区分返回原运动类型ID
     *
     * @param code    数据来源
     * @param sportId 标准运动类型ID
     */
    public static Long getRegionSportIdByCode(String code, Long sportId) {
        if (getDataSourceCodeEnumByCode(code).getSportRegionFlag()) {
            return sportId;
        }
        return 0L;
    }

    /**
     * 无需校验运动类型的数据源，接入侧那边已经转换为标准的运动类型，所以融合侧无需转换
     */
    public static List<String> getCodeList() {
        return Lists.newArrayList(PD.code, V2.code, RC.code, N01.code, N02.code, N03.code, V03.code, V04.code, F01.code, SR.code,D01.code);
    }

    /**
     * 有赛事分析相关的数据源编码
     */
    public static List<String> getAnalysisCodeList() {
        return Lists.newArrayList(TS.code, D01.code);
    }

    /**
     * 只有视频的数据源,并且无需设置视频源编码的
     */
    @Deprecated
    public static List<String> getOnlyVideoCodeList() {
        return Lists.newArrayList(BE.code, BI.code, OD.code);
    }

    /**
     * 只有电子赛事的数据源
     */
    public static List<String> getOnlyDzMatchCodeList() {
        return Lists.newArrayList(BE.code, OD.code);
    }

    /**
     * 包含电子赛事的数据源
     */
    public static List<String> getContainDzMatchCodeList() {
        return Lists.newArrayList(F01.code,SR.code);
    }


    /**
     * 事件无需校验赛事阶段和事件发生时间的数据源
     * 内部数据源,V02含集锦视频可以随时修改(V02也不可能是标准事件)
     */
    public static List<String> getPdCodeList() {
        return Lists.newArrayList(PD.code, PD2.code,TS.code);
    }

    /**
     * 含事件的数据源
     */
    public static List<String> getEventCodeList() {
        return Lists.newArrayList(OneX.code,
                BC.code,BE.code,BG.code,BT.code,
//                D01.code,
                F01.code,FTS.code,KO.code,LS.code,
                N01.code, N02.code, N03.code,PD.code, PD2.code,
                RB.code,SR.code,TS.code);
    }

    /**
     * 当事件源下发跨阶段修改比分或删除比分时，将在嘀嘀群组预警
     * @return
     */
    public static List<String> getCrossPeriodScoreChangedCode() {
        return Lists.newArrayList(BG.code, SR.code, KO.code, RB.code, BC.code);
    }

    /**
     * 支持PD自动切换到商业事件源
     * @return
     */
    public static List<String> getBusinessCode() {
        return Lists.newArrayList(BG.code, SR.code, KO.code, RB.code);
    }

    /**
     * 106940 【生产】【产品】S01,G01新增球员或更变球员信息时页面预警
     * 球员信息变更进行告警,涉及的数据源
     * @return
     */
    public static List<String> getPlayerModifyAlertCode() {
        return Lists.newArrayList(BG.code, SR.code);
    }
}
