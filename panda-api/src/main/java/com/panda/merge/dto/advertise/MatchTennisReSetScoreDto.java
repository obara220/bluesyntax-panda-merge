package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class MatchTennisReSetScoreDto extends AbstructAdvertiseDto {
    private Long thirdMatchId;
    /**
     * 选中重新计算比分的当前盘
     * */
    private Integer currentSet;
}
