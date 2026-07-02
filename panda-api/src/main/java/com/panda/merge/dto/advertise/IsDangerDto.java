package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class IsDangerDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    private Integer isDanger;  //1 危险 0安全
    private Long timeFromStartSecond;
}
