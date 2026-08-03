package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;


@Data
public class ChangeMatchPeriodV2Dto extends AbstructAdvertiseDto {
    private Long sportId;
    private Long thirdMatchId;
    private Long periodId;

    /**
     * 篮球4*12中场休息倒计时
     */
    private Long restTime;
}
