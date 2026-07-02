package com.panda.merge.dto;

import com.panda.merge.model.MatchEventInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 原始事件信息详情累
 * @author   tell
 * @since    2020年9月21日12:40:41
 * */
public class MatchEventInfoDetail extends MatchEventInfo {

    @ApiModelProperty(value = "点球大战回合")
    @Getter
    @Setter
    @Deprecated
    private Integer penaltyShootoutRound;

    @ApiModelProperty(value = "是否错误完赛事件（普通足球阶段为999才会使用该字段，0:否，1:是）")
    @Getter
    @Setter
    private Integer isErrorEndEvent = 0;

    @ApiModelProperty(value = "按照数据源编码分表的表名")
    @Getter
    @Setter
    private String tableName;

    @ApiModelProperty(value = "查询数据条数")
    @Getter
    @Setter
    private Integer size;

    @ApiModelProperty(value = "事件集合")
    @Getter
    @Setter
    private List<MatchEventInfo> data;

    @ApiModelProperty(value = "时间戳")
    @Getter
    @Setter
    private Long dayDateTime;

}