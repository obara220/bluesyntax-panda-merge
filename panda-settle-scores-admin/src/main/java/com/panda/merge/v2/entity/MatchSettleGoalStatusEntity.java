package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_goal_status")
public class MatchSettleGoalStatusEntity implements Serializable {
    private static final long serialVersionUID = -6492981419763695648L;

    private Long id;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "赛种")
    private Long thirdMatchId;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "标准赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "0有未确认进球1上个进球已经确认")
    private Integer goalStatus;

    @ApiModelProperty(value = "0有未确认角球1上个角球已经确认")
    private Integer cornerStatus;

    private Long modifyTime;

    private Long createTime;

}