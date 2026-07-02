package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 三方联赛信息
 * @author    tell
 * @since     2020年10月17日15:56:20
 */
@Data
public class ThirdSportTournamentBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 三方联赛ID*/
    private Long id;

    /** 运动种类ID*/
    private Long sportId;

    /** 运动区域ID*/
    private Long regionId;

    /** 标准联赛ID*/
    private Long referenceId;


    private String thirdTournamentSourceId;

    /** 三方数据源当前赛季ID*/
    private String thirdSeasonSourceId;

    /** 联赛logo*/
    private String logoUrl;

    /** 联赛logo缩略图*/
    private String logoUrlThumb;

    /** 数据来源编码*/
    private String dataSourceCode;

    /** 备注*/
    private String remark;

    /** 联赛名称多语言*/
    private Long nameCode;

    /**
     * 联赛名称多语言列表
     */
    private List<I18nItemBO> il8nNameList;

}