package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RerackV2Dto extends AbstructAdvertiseDto {
    private Long sportId;
    private Long thirdMatchId;
    private Long periodId;
}
