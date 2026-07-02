package com.panda.merge.dto.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchSettleRollBackDto implements Serializable {

    /**
     * 回滚状态0未回滚，1回滚中
     */
    private Integer rollBackStatus;

    /**
     * 赛事id
     */
    private Long standardMatchId;

    /**
     * 比分事件id
     */
    private Long settleScoreEventId;

    /**
     * 1比分，2事件
     */
    private Integer dataType;

    /**
     * 回滚订单数
     */
    private Long rollBackOrderCount;

    /**
     * 事件类型
     */
    private String eventCode;
    /**
     * 1是点球大战
     */
    private Integer isDianQiu;
}
