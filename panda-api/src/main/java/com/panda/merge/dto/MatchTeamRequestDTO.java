package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MatchTeamRequestDTO implements Serializable {
    /**
     * 每页数据个数
     **/
    @ApiModelProperty(name = "每页数据量，默认50")
    private Integer size = 50;

    /**
     * 从第几页开始
     **/
    @ApiModelProperty(name = "从第几页开始，默认1")
    private Integer page = 1;

    /**
     * 体育种类id
     */
    @ApiModelProperty(name = "体育种类")
    private Long sportId;

    @ApiModelProperty(name = "标准球队的ids列表", notes = "标准球队的ids列表")
    private List<Long> ids;


    @ApiModelProperty("数据来源列表")
    private List<String> dataSourceCode;

    /**
     * 匹配规则列表
     **/
    @ApiModelProperty(name = "匹配规则", notes = "匹配规则。MA:已匹配；UMA:未匹配；其他任意值：全部")
    private List<String> matchList;

    @ApiModelProperty("地区id")
    private Long regionId;

    @ApiModelProperty(value = "球队ID状态", allowableValues = "range[0,1]", notes = "球队ID状态.0:无球队ID; 1:有球队ID; 其余值:全部")
    @Range(min = 0,max = 1,message = "球队状态查询参数错误。")
    private Integer sportTeamManagerIdStatus;

    @ApiModelProperty("球队名中文简体，冗余国际化信息表 zs")
    private String name;

    @ApiModelProperty("球队管理ID")
    private String teamManageId;

    @ApiModelProperty(name = "是否仅查询标准球队", notes = "1:仅查询标准球队")
    private Integer onlyStandard;

    @ApiModelProperty(name = "球队类型", notes = "1:男子团体;2:男子单打;3:女子单打;4:男子双打体;5,女子双打6:混合双打,7,其它,8:女子团体;")
    private List<Integer> sportTeamTypes = new ArrayList<>();


}
