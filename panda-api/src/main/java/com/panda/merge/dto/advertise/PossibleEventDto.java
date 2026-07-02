package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class PossibleEventDto extends AbstructAdvertiseDto  {
    private Long thirdMatchId;
    private String possibleEventCode;
    private String homeAway;
    private Long timeFromStartSecond;
}
