package com.panda.merge.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mirro
 * @Project Name :  panda_data_nonrealtime
 * @Package Name :  com.panda.sport.data.nonrealtime.api.query.bo
 * @Description:
 * @date 2019/10/24 21:07
 * @ModificationHistory Who    When    What
 */
@Data
public class ThirdSportTeamBO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 球队表id
     */
    private Long id;

    /**
     * 体育种类id。体育种类id
     */
    private Long sportId;


    /**
     * 球队区域ID。  standard_sport_region.id
     */
    private Long regionId;

    /**
     * 数据来源编码。取值： SR BC分别代表：SportRadar、FeedConstruc。详情见data_source
     */
    private String dataSourceCode;

    /**
     * 球队 logo。图标的url地址
     */
    private String logoUrl;

    /**
     * 球队 logo缩略图的url地址
     */
    private String logoUrlThumb;

    /**
     * 球队名称编码。国际化信息
     */
    private List<I18nItemBO> il8nNameList;

    /**
     * 对用户可见。1：可见； 0：不可见
     */
    private Integer visible;

    /**
     * 主教练。主教练名称
     */
    private String coach;

    /**
     * 主场。比如：所在地 和 名称
     */
    private String statium;

    /**
     * 球队介绍。默认是空
     */
    private String introduction;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 更新时间。
     */
    private Long modifyTime;

    /**
     * 赛事球队关系信息
     */
    private ThirdMatchTeamRelationBO matchTeamRelation;
}
