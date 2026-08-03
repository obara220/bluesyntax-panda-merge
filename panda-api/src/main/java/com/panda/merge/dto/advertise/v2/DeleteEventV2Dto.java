package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;

@Data
public class DeleteEventV2Dto extends AbstructAdvertiseDto {
    private Long sportId;
    private Long thirdMatchId;
    private Long deleteEventId;

    //TODO 不确定
    private Long timeFromStartSecond;
    private Long possibleEventId;
}

