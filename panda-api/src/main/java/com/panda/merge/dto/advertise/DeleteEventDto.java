package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class DeleteEventDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    private Long deleteEventId;
    private Long timeFromStartSecond;
    private Long possibleEventId;
}

