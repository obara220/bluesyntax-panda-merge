package com.panda.merge.dto;

import com.panda.merge.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 赛事首发阵容信息
 * @author     tell
 * @since      2020年9月2日19:42:31
 */
@Data
public class ThirdMatchSidelinedDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @NotNull(message = "数据源赛事ID不能为null!")
    private String thirdMatchSourceId;

    @NotNull(message = "数据源赛事运动类型不能为null!")
    private Long sportId;

    @NotNull(message = "数据来源不能为null!")
    private String dataSourceCode;

    @NotNull(message = "数据源球队ID不能为null!")
    private String thirdTeamSourceId;

    @NotNull(message = "数据源球员ID不能为null!")
    private String thirdPlayerSourceId;
    /** 数据源球员中文名称*/
    private String thirdPlayerName;
    /** 数据源球员英文名称*/
    private String thirdPlayerEnName;
    /** 数据源球员头像*/
    private String thirdPlayerPicUrl;

    /** 主客队标识(1主队,2客队)*/
    @NotNull(message = "主客队标识不能null!")
    @EnumValue(message = "主客队标识值非法，值应为{1,2}其中之一,请检查",intValues ={1,2})
    private Integer homeAway;

    /** 球员位置名称（中场，前锋，后卫，门将）*/
    private String position;
    /** 球衣号码*/
    private Integer shirtNumber;

    /** 缺阵原因*/
    private String reason;
    /** 原因描述id*/
    private String descriptionId;
    /** 原因描述*/
    private String description;

    /** 是否失效(0:否,1:是)*/
    private Integer invalid;

}
