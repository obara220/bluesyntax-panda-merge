package com.panda.merge.dto.advertise;

import lombok.Data;

/**
 * @author warren
 * @since 2024/11/12 13:01:18
 */
@Data
public class NoRetakePenDto extends AbstructAdvertiseDto {
    /**
     * 赛事消息id
     */
    private Long deleteEventId;
    /**
     * 三方赛事Id
     */
    private Long thirdMatchId;

    /**
     * 赛事进行时间
     */
    private Long timeFromStartSecond;

    /**
     * 主客队
     */
    private String homeAway;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 没有重踢标记 页面传 99
     */
    private String retakeStatus;
}
