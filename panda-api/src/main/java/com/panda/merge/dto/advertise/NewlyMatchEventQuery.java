package com.panda.merge.dto.advertise;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *  idol
 * @project Name :  panda_data_service
 * @package Name :  com.panda.sports.manager.realtime.query
 * @description :  TODO
 * --------  ---------  --------------------------
 */
@Data
public class NewlyMatchEventQuery implements Serializable {

    @ApiModelProperty(name = "创建时间", notes = "创建时间筛选是否有新的事件")
    private Long createTime;

    @ApiModelProperty(name = "标准赛事id", notes = "标准赛事id")
    private Long matchId;

}
