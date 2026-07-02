package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class SettleCenterDto extends AbstructAdvertiseDto {
    private Long matchId;
    private Long eventTime;
    private Long matchPeriodId;
    private Long timeFromStartSecond;

}
