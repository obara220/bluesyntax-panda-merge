package com.panda.merge.dto.advertise;

import lombok.Data;


@Data
public class ChangeMatchPeriodDto extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    private Long periodId;

    /**
     * 篮球4*12中场休息倒计时
     */
    private Long restTime;
}
