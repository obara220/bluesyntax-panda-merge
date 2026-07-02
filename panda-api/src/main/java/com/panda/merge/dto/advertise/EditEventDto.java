package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class EditEventDto  extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    private Long editEventId;
    private Long timeFromStartSecond;
    private Integer home;
    private Integer away;
}


