package com.panda.merge.dto.advertise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author warren
 * @since 2023/12/06 18:35:48
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InjuryTimeEventDto extends AbstructAdvertiseDto {
    // 三方赛事ID
    private Long thirdMatchId;

    // 伤停补时时间：单位S
    private Long timeOut;

    // 页面时间
    private Long timeFromStartSecond;
}
