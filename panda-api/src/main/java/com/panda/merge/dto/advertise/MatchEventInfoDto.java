package com.panda.merge.dto.advertise;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchEventInfoDto implements Serializable {

    private static final long serialVersionUID = -5222992597806875284L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "体育种类id.对应standard_sport_type.id")
    private Long sportId;

    @ApiModelProperty(value = "是否被取消.1被取消;0:没有被取消")
    private Integer canceled;

    @ApiModelProperty(value = "事件编码.对应match_event_type.event_code")
    private String eventCode;

    @ApiModelProperty(value = "事件发生时间.UTC时间")
    private Long eventTime;

    @ApiModelProperty(value = "扩展信息")
    private String extraInfo;

    @ApiModelProperty(value = "主客场.主场队:home;客场队:away")
    private String homeAway;

    @ApiModelProperty(value = "比赛阶段id.system_item_dict.value")
    private String matchPeriodName;

    @ApiModelProperty(value = "主队数量")
    private Integer t1;

    @ApiModelProperty(value = "客队数量")
    private Integer t2;

    @ApiModelProperty(value = "距离比赛开始多少秒")
    private String secondsFromStart;

    @ApiModelProperty(value = "创建时间.UTC时间")
    private Long createTime;

}