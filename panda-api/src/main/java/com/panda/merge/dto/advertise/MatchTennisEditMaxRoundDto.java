package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class MatchTennisEditMaxRoundDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    //选中的盘
    private Integer currentSet;
    //最大局数
    private Integer maxRound;
}
