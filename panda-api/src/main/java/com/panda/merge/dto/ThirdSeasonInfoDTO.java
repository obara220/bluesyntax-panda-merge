package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author Kepa
 * @Date 2021/2/5 20:40
 * @Version 1.0
 */
@Data
public class ThirdSeasonInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 赛季id **/
    private String thirdSeasonId;

    /** 赛种 **/
    private Long sportId;

    /** 三方联赛ID **/
    private String thirdTournamentId;

    /** 赛季名称中文名 **/
    private String thirdSeasonName;

    /** 数据来源编码code（取值： SR BC分别代表：SportRadar、FeedConstruc。详情见data_source）*/
    @NotNull(message = "三方数据源联赛数据来源不能为null!")
    private String dataSourceCode;

    /** 赛季多语言 **/
    private List<I18nItemDTO> seasonNameList;

    /** 赛季开始时间 **/
    private Long startDate;

    /** 赛季结束时间 **/
    private Long endDate;

    /** 赛季id **/
    private String year;

}
