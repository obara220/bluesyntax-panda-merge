package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class PDBasketBallParseContinueDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;  // PD三方赛事id
    private Integer matchGoStatus; //0 暂停/中断  1继续/重开
}
