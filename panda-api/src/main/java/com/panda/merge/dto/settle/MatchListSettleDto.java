package com.panda.merge.dto.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 结算推送赛事列表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchListSettleDto implements Serializable {
    /**
     * 标准赛事ID
     * */
    private Long standardMatchId;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 结算核对记录id
     */
    private Long settleCheckId;

    /**
     * 比分事件id
     */
    private Long settleScoreEventId;

    /**
     * 标记调用类型，仅做业务调用标识
     */
    private Integer type;
}
