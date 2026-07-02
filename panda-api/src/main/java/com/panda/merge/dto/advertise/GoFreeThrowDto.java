package com.panda.merge.dto.advertise;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import lombok.Data;

@Data
public class GoFreeThrowDto extends AbstructMatchSettleDto {
    /**
     * PD赛事id
     * */
    private Long thirdMatchId;
    /**
     * 第几个罚球 1 2 3
     * */
    private long eventOrder;
    /**
     * -1 未投  0 未进  1进了
     * */
    private Integer freeThrowResult ;

    /**
     * 篮球也是用这个做倒计时的 比赛进行时间
     */
    private Long timeFromStartSecond;
}
