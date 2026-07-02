package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchFreeze implements Serializable {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "体育种类id.对应standard_sport_type.id")
    private String settleNum;

    @ApiModelProperty(value = "体育种类id.对应standard_sport_type.id")
    private Long sportId;

    @ApiModelProperty(value = "赛事id")
    private Long matchId;

    @ApiModelProperty(value = "事件最新一次下发的linkId")
    private String key;

    @ApiModelProperty(value = "0未冻结1冻结")
    private Integer freezeSettleStatus;

    @ApiModelProperty(value = "操作对象编码")
    private String  eventCode;


    @ApiModelProperty(value = "事件比分id")
    private String eventId;

    @ApiModelProperty(value = "操作用户")
    private String operatorName;


}