package com.panda.merge.dto.advertise;

import lombok.Data;

/**
 * @author Fymen
 */
@Data
public class PDTennisRoundStatusDto extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    private Long standardMatchId;

    /**
     * 0开始 1结束
     */
    private Integer roundStatus;

    /**
     * 当前第几盘
     */
    private Integer currentSet;

    /**
     * 当前第几局
     */
    private Integer currentRound;

    private Integer t1 ;

    private Integer t2;
}
