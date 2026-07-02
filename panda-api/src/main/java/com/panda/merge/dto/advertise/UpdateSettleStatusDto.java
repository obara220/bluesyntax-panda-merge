package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class UpdateSettleStatusDto extends AbstructAdvertiseDto {
    /**
     * 0: 不参与结算
     * 1: 参与结算
     * */
    private Integer settleStatus;
    /**
     * 三方赛事id
     * */
    private Long thirdMatchId;
}
