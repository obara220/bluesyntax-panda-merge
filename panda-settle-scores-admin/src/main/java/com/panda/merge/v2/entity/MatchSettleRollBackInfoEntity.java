package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_roll_back_info")
public class MatchSettleRollBackInfoEntity implements Serializable {
    private static final long serialVersionUID = -4519497522564063906L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "比分，事件id")
    private Long settleScoreEventId;

    @ApiModelProperty(value = "数据类型，1比分，2事件")
    private Integer dataType;

    @ApiModelProperty(value = "回滚状态")
    private Integer rollBackStatus;

    @ApiModelProperty(value = "回滚时间")
    private Long rollBackTime;

    @ApiModelProperty(value = "赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "回滚订单数")
    private Long rollBackOrderCount;

    @ApiModelProperty(value = "订单总数")
    private Long orderCount;

    @ApiModelProperty(value = "回调时间")
    private Long rollBackSuccessTime;

    private Long modifyTime;

    private Long createTime;

    @ApiModelProperty(value = "比分事件编码")
    private String eventCode;

    @ApiModelProperty(value = "1是点球大战")
    private Integer isDianQiu;

}