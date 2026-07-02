package com.panda.merge.bo.thirdmatch;

import com.panda.merge.bo.I18nItemBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 赛事球队技术统计
 * @author     tell
 * @since      2025年6月10日9:42:31
 */
@Data
public class ThirdMatchTeamSkillStatisticsBO implements Serializable {

    /** 数据源ID:源赛事ID:源球队ID*/
    private String id;

    /** 三方赛事id */
    private Long thirdMatchId;

    /**标准赛事id */
    private Long standardMatchId;

    /** 标准球队ID*/
    private Long standardTeamId;

    /** 球队多语言*/
    private List<I18nItemBO> teamNameIl8nList;

    /** 球队logo*/
    private String teamLogo;
    /** 球队 logo缩略图的url地址*/
    private String teamLogoUrlThumb;


    @ApiModelProperty(value = "源赛事ID")
    private String matchId;

    @ApiModelProperty(value = "数据来源")
    private String dataSourceCode;

    @ApiModelProperty(value = "运动类型")
    private Long sportId;

    @ApiModelProperty(value = "源球队ID")
    private String teamId;

    @ApiModelProperty(value = "主客队（1:主,2:客）")
    private String homeAway;

    @ApiModelProperty(value = "篮板总数，包括进攻篮板和防守篮板")
    private Integer rebound;

    @ApiModelProperty(value = "进攻篮板数量")
    private Integer offensiveRebound;

    @ApiModelProperty(value = "防守篮板数量")
    private Integer defensiveRebound;

    @ApiModelProperty(value = "助攻数量")
    private Integer assist;

    @ApiModelProperty(value = "盖帽数量")
    private Integer block;

    @ApiModelProperty(value = "抢断数量")
    private Integer steal;

    @ApiModelProperty(value = "失误数量")
    private Integer turnover;

    @ApiModelProperty(value = "得分")
    private Integer score;

    @ApiModelProperty(value = "犯规数量")
    private Integer fouls;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    private static final long serialVersionUID = 1L;

}