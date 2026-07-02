package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class MatchTennisEditSetScoreDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    //当前第几盘
    private Integer currentSet;
    //主队盘局比分
    private Integer t1;
    //客队盘局比分
    private Integer t2;
}
