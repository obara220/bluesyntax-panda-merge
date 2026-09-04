package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 球队相关信息参数类 </br>
 * @author :        tell
 * @Date:           2020年9月2日19:42:31
 */
@Data
public class ThirdMatchTeamDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 第三方提供的id。第三方球队id
     */
    @NotNull(message = "三方数据源球队ID不能为null!")
    private String thirdTeamId;

    /**
     *  bet_radar_id球队关联id
     */
    private Integer betRadarId;

    /**
     * 队伍名称。指的是中文名称。仅用用于数据库操作人员使用。
     */
    @NotNull(message = "三方数据源球队中文名称不能为null!")
    private String name;

    /**
     * 球队类型.1:团体;2:男单;3:女单;4:男双;5:女双;6:混双;7:未知
     * 字典数据，对应 parent_type_id = 9
     */
    private Integer type;

    /**
     * 主教练.如果第三不提供,则删除该字段
     */
    private String coach;

    /**
     * 主场。主场信息，比如：所在地 和 名称
     */
    private String statium;

    /**
     * 球队名称国际化信息
     */
    @Valid
    @NotNull(message = "三方数据源球队名称国际化不能为null!")
    private List<I18nItemDTO> teamNameList;

    /**
     * 赛事球队关系信息
     */
    @Valid
    @NotNull(message = "三方数据源球队赛事球队关系不能为null!")
    private ThirdMatchTeamRelationDTO matchTeamRelation;

    /**
     * 国籍.国籍所属国家id.对应third_sport_region.id
     */
    private String countryId;

    /**
     * 国藉
     */
    private String countryName;

    /**
     * 球队 logo缩略图的url地址。
     */
    private String logoUrlThumb;

    /**
     * 球员源ID(目前已经废弃)
     */
    @Deprecated
    private List<String> playerIds;

    /**
     * 球队 logo。图标的url地址
     */
    private String logoUrl;

    /**
     * 备注。
     */
    private String remark;
}
