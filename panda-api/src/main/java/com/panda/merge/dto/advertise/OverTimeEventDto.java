package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class OverTimeEventDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    private Integer minute;
    private Long timeFromStartSecond;
}
