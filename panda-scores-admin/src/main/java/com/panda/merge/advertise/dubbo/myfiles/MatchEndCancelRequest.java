package com.panda.merge.advertise.dubbo.myfiles;

import lombok.Data;

import java.io.Serializable;

/**
 * @author warren
 * @since 2024/01/24 10:57:15
 */
@Data
public class MatchEndCancelRequest implements Serializable {
    private Long thirdMatchId;// 当前报球版的三方赛事id
    private Long matchPeriod; // 比赛阶段
    private Long secondFromStart;// 比赛时间
    /**
     * 链路ID
     */
    private String requestId;
}
