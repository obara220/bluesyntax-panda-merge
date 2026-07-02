package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class EditFaScoreDto extends AbstructAdvertiseDto {
    private Integer type;
    private Long thirdMatchId;
    private Integer bigFaT1;
    private Integer bigFaT2;
    private Integer smallFaT1;
    private Integer smallFaT2;
}
