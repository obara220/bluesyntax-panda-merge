package com.panda.merge.dto.advertise;

import lombok.Data;


@Data
public class PDBasketBallSearchEventDto extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    /**
     * 当前阶段id  全部=-1
     * 4节制给  13 14 15 16
     * 2节制给  1 2
     * */
    private Long periodId;


}
