package com.panda.merge.dto.advertise;

import lombok.Data;


@Data
public class PDBasketBallPauseDto extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    private String  homeAway;

    /**
     * 比赛当前秒数
     * */
    private Long  matchTimeSecond;
    /**
     * 当前阶段id
     * */
    private Long periodId;
    /**
     * 1 暂停 2继续
     * */
    private Integer type;

    /**
     * 暂停/继续 点击次数
     */
    private long times;
}
