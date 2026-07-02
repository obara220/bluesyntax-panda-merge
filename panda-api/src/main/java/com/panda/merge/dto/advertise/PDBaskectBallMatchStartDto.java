package com.panda.merge.dto.advertise;

import lombok.Data;


@Data
public class PDBaskectBallMatchStartDto extends AbstructAdvertiseDto {

    private Long thirdMatchId;
    /**
     * 赛事控制类型:
     * 1 跳球开始 0  其他开始
     * */
    private Integer isJump;
    /**
     * 跳球开始 主客队
     * home主队
     * away 客队
     * */
    private String  jumpWonHomeAway;
}
