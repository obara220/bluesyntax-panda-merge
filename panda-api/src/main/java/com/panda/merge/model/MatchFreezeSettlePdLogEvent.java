package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchFreezeSettlePdLogEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "球种的Id")
    private Long sportId;
    @ApiModelProperty(value = "三方赛事ID")
    private String matchId;
    @ApiModelProperty(value = "操作类型：Y:冻结,N:取消冻结")
    private String category;
    @ApiModelProperty(value = "显示操作人")
    private String operatorName;
    @ApiModelProperty(value = "操作人Ip")
    private String ip;
    @ApiModelProperty(value = "操作人ID")
    private String operatorId;
    @ApiModelProperty(value = "冻结修改前")
    private String OperateForw;
}
