package com.panda.merge.dto.advertise.v2;

import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;

@Data
public class DeleteHotkeyV2Dto extends AbstructAdvertiseDto {
    private Long sportId;
    private Long thirdMatchId;
    private Long userId;
    private Integer flag;
}

