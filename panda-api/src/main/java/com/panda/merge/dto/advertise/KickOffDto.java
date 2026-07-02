package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class KickOffDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    private String whoKickOff;
    private String dataSourceCode;
}
