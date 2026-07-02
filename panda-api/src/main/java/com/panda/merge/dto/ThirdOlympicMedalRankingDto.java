package com.panda.merge.dto;

import lombok.Data;

/**
 * 奥运奖牌排行榜单
 * @author  tell
 * @since   2021年7月1日15:03:12
 * */
@Data
public class ThirdOlympicMedalRankingDto {

    /** id*/
    private String id;

    /**奥运会年份*/
    private String year;

    /**榜单显示序号*/
    private Integer rankingShowNum;

    /**榜单排序号*/
    private Integer rankingNum;

    /**榜单名称*/
    private String rankingName;

    /**代表国家logo*/
    private String countryLogo;

    /**
     * 代表国家名称列表
     * key  : 多语言类型
     * value: 名称
     * */
    private String countryName;

    /**代表国家英文名称 */
    private String countryEnName;

    /**金牌数量*/
    private Integer goldNum;
    /**银牌数量*/
    private Integer silverNum;
    /**铜牌数量*/
    private Integer bronzeNum;

    /** 是否失效(0:否,1:是)*/
    private Integer invalid = 0;

}
