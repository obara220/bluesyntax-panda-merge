package com.panda.merge.dto.advertise;

import lombok.Data;


@Data
public class PDBasketBallEditEventDto extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    private Long  deleteEventId;
    /**
     * 比赛当前秒数
     * */
    private Long  matchTimeSecond;
    /**
     * 当前阶段id
     * */
    private Long periodId;

    private Integer t1;

    private Integer t2;

    /**
     * 当前传入的编辑比分
     */
    private Integer score;

    /**
     * 罚球次数
     * */
    private Integer freeThrowNumber;

    /**
     * 1 未命中  2投篮命中
     */
    private Integer ballEventType;
}
