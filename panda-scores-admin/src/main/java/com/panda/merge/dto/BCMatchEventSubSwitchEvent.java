package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BCMatchEventSubSwitchEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String linkId;

    @ApiModelProperty(value = "球种的Id")
    private Long sportId;

    @ApiModelProperty(value = "三方赛事ID")
    private String matchId;

    @ApiModelProperty(value = "订阅状态，1:为比赛开始后订阅")
    private Integer state;

}
