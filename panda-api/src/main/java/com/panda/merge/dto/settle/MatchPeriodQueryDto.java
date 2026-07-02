package com.panda.merge.dto.settle;

import lombok.Data;

@Data
public class MatchPeriodQueryDto extends AbstructMatchSettleDto {

    /**
     * 赛事阶段
     * */
    private Long categorySetId ;
    /**
     * 赛事ID
     * */
    private Long standardMatchId;


}
