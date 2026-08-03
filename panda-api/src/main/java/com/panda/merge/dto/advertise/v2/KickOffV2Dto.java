package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;

@Data
public class KickOffV2Dto extends AbstructAdvertiseDto {
    private Long sportId;
    private Long thirdMatchId;
    private String whoKickOff;
    private String dataSourceCode;
    private Long periodId;
    private Long secondFromStart;
}
