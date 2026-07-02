package com.panda.merge.dto.advertise;

import lombok.Data;

/**
 * @author Kepa
 */
@Data
public class TakePenaltyDTO extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    private String homeAway;

    private String eventCode;

    private Integer penaltyRound;
}
