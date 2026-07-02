package com.panda.merge.dto.advertise;

import lombok.Data;



@Data
public class PenaltyScoresEditDto extends AbstructAdvertiseDto  {
    //三方赛事ID
    private Long thirdMatchId;
    //标准赛事Id
    private Long standardMatchId;
    //1~99 轮数
    private Integer targetRound;
    //主队比分
    private Integer home;
    //客队比分
    private Integer away;
    /**
     * 主客队
     */
    private String homeAway;
}
