package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class PDBasketBallNextPeriodDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;  // PD三方赛事id
    private Long periodId; //篮球也是用这个做倒计时的
}
