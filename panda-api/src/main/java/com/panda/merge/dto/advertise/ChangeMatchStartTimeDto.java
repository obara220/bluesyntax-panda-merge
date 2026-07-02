package com.panda.merge.dto.advertise;

import lombok.Data;



@Data
public class ChangeMatchStartTimeDto extends AbstructAdvertiseDto{
    private Long thirdMatchId;
    private Long startTime;
    private Integer sportId;
}
