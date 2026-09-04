package com.panda.merge.dto;

import com.panda.merge.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 联赛相关信息参数类 </br>
 * @author :        tell
 * @Date:           2020年9月2日19:42:31
 */
@Data
public class ThirdSportTournamentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "三方数据源联赛ID不能为null!")
    @Size(message = "三方数据源联赛ID长度不能超过50个字符!!",max=50)
    private String thirdTournamentSourceId;

//    @NotNull(message = "三方数据源联赛父联赛ID不能为null!")
    @Size(message = "三方数据源联赛父联赛ID长度不能超过50个字符!!",max=50)
    private String fatherTournamentId;

    /** 是否子联赛（0：否，1：是）*/
    private String simpleFlage;

    /** 数据源联赛名称，中文优先*/
    @NotNull(message = "三方数据源联赛中文名称不能为null!")
    private String name;

    /** 数据源运动种类ID*/
    @NotNull(message = "三方数据源联赛运动类型不能为null!")
    private Long sportId;

    /** 数据来源编码code（取值： SR BC分别代表：SportRadar、FeedConstruc。详情见data_source）*/
    @NotNull(message = "三方数据源联赛数据来源不能为null!")
    private String dataSourceCode;

    /** 联赛名称多语言列表*/
    @Valid
    @NotNull(message = "三方数据源联赛名称国际化不能为null!")
    private List<I18nItemDTO> tournamentNameList;

    /** 当前赛季id **/
    private String thirdSeasonSourceId;

    /** 赛季多语言*/
    @Valid
    private List<I18nItemDTO> seasonNameList;

//    @NotNull(message = "联赛轮次类型不能为null")
//    @EnumValue(message = "联赛轮次类型预定值非法，值应为{Group,Cup,Qualification}其中之一,请检查",strValues ={"Group","Cup","Qualification"})
    private String currentRoundType;

    /** 联赛轮次数 （当联赛轮次类型是 Group 时存在值）*/
    private Integer currentRoundNumber;

    /** 联赛轮名称（当联赛轮次类型是 Cup 时存在值）*/
    private String tournamentRoundName;

    /** 联赛 logo。图标的url地址*/
    private String logoUrl;

    /** 联赛 logo缩略图的url地址。*/
    private String logoUrlThumb;

    /** 数据源区域id,无区域填 0*/
    @NotNull(message = "三方数据源联赛区域ID称不能为null!")
    private String sportRegionId;

    /** 数据源区域名称,无区域填 世界*/
//    @NotNull(message = "三方数据源联赛区域名称称不能为null!")
    private String sportRegionName;

    /** 备注*/
    private String remark;

    /** 创建时间。*/
    private Long createTime;


    /**
     * 赛事类型（默认1）{
     *     1：普通赛事
     *     2：电竞赛事
     *     3：篮球3x3(如果运动类型为篮球）
     *     4：MMA(如果运动类型为拳击）
     * }
     */
    private Integer matchType;
}
