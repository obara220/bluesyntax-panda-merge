package com.panda.merge.dto.advertise;


import lombok.Data;

/**
 * 点球即将开踢
 * @author Kepa
 */
@Data
public class PenaltyAboutToBeTakenDto extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    private String homeAway;

    private String eventCode;

    private Long timeFromStartSecond;
}
