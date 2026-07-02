package com.panda.merge.dto.settle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
public class MatchSettleRealTimeOnOffDto implements Serializable {
    private static final long serialVersionUID = 2695183936820851492L;

    @ApiModelProperty("即时开关")
    private Boolean realTimeOnOff;

}
