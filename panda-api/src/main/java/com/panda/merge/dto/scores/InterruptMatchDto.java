package com.panda.merge.dto.scores;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 赛事中断下发
 */
@Data
public class InterruptMatchDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String linkId;
    private Long matchId;
    private Long thirdMatchId;
    private Long matchTimes;
    private Long type;
    private String userId;
    private String userName;
    private String ipAddress;

}
