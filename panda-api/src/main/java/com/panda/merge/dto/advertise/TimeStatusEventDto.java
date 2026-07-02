package com.panda.merge.dto.advertise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author warren
 * @since 2023/12/06 18:45:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeStatusEventDto extends AbstructAdvertiseDto {
    // 三方赛事ID
    private Long thirdMatchId;

    // 是否时间暂停：默认 1 不暂停 0 暂停
    private Integer timeGo;

    // 页面时间
    private Long timeFromStartSecond;
    /**
     * 类型：0为滚球操盘下发
     * 为空是PD报球板下发
     */
    private String type;

    /**
     * 下发恢复的阶段
     */
    private Long period;
}
