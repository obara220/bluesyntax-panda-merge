package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class Goal5MinDataDto extends AbstructAdvertiseDto  {
    private Integer homeScore;
    private Integer awayScore;
    private Long period5Min;
}
