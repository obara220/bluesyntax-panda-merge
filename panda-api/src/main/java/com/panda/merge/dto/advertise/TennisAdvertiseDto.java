package com.panda.merge.dto.advertise;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TennisAdvertiseDto extends AbstructMatchSettleDto {
    //三方赛事id
    private Long thirdMatchId;
    @ApiModelProperty(value = "局制:1长盘制,2抢七制,3单人抢十,4双人抢十,5特")
    private Integer  matchLength;
    //盘比分
    private Long standardMatchId;

    //赛事(第几盘)
    private Integer roundType;

    @ApiModelProperty(value = "当前盘数")
    private Integer currentSet;

    @ApiModelProperty(value = "当前局数")
    private Integer currentRound;

    private Long period;

    private String homeAway;
}
