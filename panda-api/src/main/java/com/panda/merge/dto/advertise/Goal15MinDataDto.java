package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class Goal15MinDataDto extends AbstructAdvertiseDto  {
    private Integer homeScore;
    private Integer awayScore;
    private Long period15Min;
}
