package com.panda.merge.dto.advertise;

import lombok.Data;


@Data
public class PDBasketBallDeleteEventDto extends DeleteEventDto {

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

}
