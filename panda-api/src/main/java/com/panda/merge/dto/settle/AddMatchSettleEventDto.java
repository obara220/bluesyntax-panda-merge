package com.panda.merge.dto.settle;

import lombok.Data;

@Data
public class AddMatchSettleEventDto extends AbstructMatchSettleDto {

    /**
     * 赛事ID
     * */
    private Long standardMatchId;

    private String eventCode;
    /**
     * 阶段ID
     * */
    private Long periodId;

    private String settleNum;

    private String fiveMinSection;

}
