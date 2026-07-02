package com.panda.merge.dto.scores;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 比分中心设置修改参数
 */
@Data
public class ScoresCenterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long startTime;

    private Long endTime;

    @ApiModelProperty(name = "比赛开始时间上限参数", notes = "时间戳精确到毫秒，UTC时间", required = true)
    private String startTimeFrom;

    private List<Long> sportIds;

    /**
     * 显示0/隐藏1
     */
    private Integer showStatus;


    private String userId;
    private String userName;
    private String ipAddress;

}
