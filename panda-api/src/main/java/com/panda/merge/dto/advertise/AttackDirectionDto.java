package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class AttackDirectionDto extends AbstructAdvertiseDto  {
    private Long thirdMatchId;
    private Boolean changeDirection = Boolean.FALSE;
}
