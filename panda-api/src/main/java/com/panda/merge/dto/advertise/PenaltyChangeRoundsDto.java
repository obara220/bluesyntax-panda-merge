package com.panda.merge.dto.advertise;

import lombok.Data;



@Data
public class PenaltyChangeRoundsDto extends AbstructAdvertiseDto  {
    //三方赛事ID
    private Long thirdMatchId;

    //更换轮次到
    private Integer targetRound;

}
