package com.panda.merge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 公共事件-系统时间
 *
 * @author warren
 * @since 2024/10/11 12:30:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicEvent implements Serializable {
    /**
     * 三方赛事ID
     */
    private Long thirdMatchId;

    /**
     * 主队控球时间 单位：毫秒
     */
    private Long homePossessionTime;

    /**
     * 客队控球时间 单位：毫秒
     */
    private Long awayPossessionTime;


    /**
     * 公共事件1
     */
    private Long publicEventOne;

    /**
     * 公共事件2
     */
    private Long publicEventTwo;

    /**
     * 系统时间
     */
    private Long currentTime;

    /**
     * 上一个事件类别
     * kick_off: 开始比赛 kickOff
     * homeAwayEvent: 主客队事件 home|away
     * publicEventOne: 公共事件1 publicEventOne
     * publicEventTwo: 公共事件2 publicEventTwo
     * 标准数据源非事件计时阶段时，置为: outOfEvent
     */
    private String previousEvent;

    /**
     * 超出5分钟未有事件下发，将以公共时间2计算，不计为主/客队数据
     * 计数器：默认=0，超过5分钟切换后=1，5分钟后非公共事件2有操作重置为 0
     */
    private Integer eventStatus;
}
