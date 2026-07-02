package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 三方赛事球员伤停信息
 * @author  tell
 * @since   2021年4月17日14:09:54
 */
@Data
public class ThirdMatchSidelinedBO implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 数据来源ID+赛事源ID+球队源ID+球员源ID*/
    private String id;

    /** 三方赛事id */
    private Long thirdMatchId;
    /**标准赛事id */
    private Long standardMatchId;

    /** 数据源赛事id */
    private String thirdMatchSourceId;

    /** 运动类型*/
    private Long sportId;

    /** 数据来源*/
    private String dataSourceCode;

    /** 数据源球队id*/
    private String thirdTeamSourceId;

    /** 数据源球员id*/
    private String thirdPlayerSourceId;

    /** 球员名称*/
    private String thirdPlayerName;

    /** 球员英文名称*/
    private String thirdPlayerEnName;

    /** 球员头像*/
    private String thirdPlayerPicUrl;

    /** 球员位置中文名称*/
    private String positionName;

    /** 球员位置英文名称*/
    private String positionEnName;

    /** 球衣号码*/
    private Integer shirtNumber;

    /** 主客队标识(1主队,2客队)*/
    private Integer homeAway;

    /** 缺阵原因*/
    private String reason;

    /** 原因描述id*/
    private String descriptionId;

    /** 原因描述*/
    private String description;

    private Long createTime;

    private Long modifyTime;


    /** 是否失效(0:否,1:是)*/
    private Integer invalid;

}
