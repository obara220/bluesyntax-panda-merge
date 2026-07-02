package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 赛事首发阵容基础信息
 * @author     tell
 * @since      2020年9月2日19:42:31
 */
@Data
public class ThirdMatchLineupSimpleDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    @NotNull(message = "数据源赛事ID不能为null!")
    private String thirdMatchSourceId;

    @NotNull(message = "数据源赛事运动类型不能为null!")
    private Long sportId;

    @NotNull(message = "数据来源不能为null!")
    private String dataSourceCode;

    @NotNull(message = "主队阵型不能为null!")
    private String homeFormation;

    @NotNull(message = "客队阵型不能为null!")
    private String awayFormation;

    @Valid
    @NotNull(message = "数据源赛事首发阵容列表不能为null!")
    private List<ThirdMatchLineupDTO> lineupList;

}
