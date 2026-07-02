package com.panda.merge.dto.advertise;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

//比分设置dto

@Data
public class TennisQueryDto extends AbstructAdvertiseDto {
    //三方赛事id
    private Long thirdMatchId;
    //赛事(第几盘)
    private Long  matchLength;
    //盘比分

    //赛事(第几局)
    private Long  matchPart;

    //主队比分(网球局内比分 标识为  1=15 2=30 3=45 4=ad 5=win)
    private Integer  T1;
    //客队比分(网球局内比分 标识为  1=15 2=30 3=45 4=ad 5=win)
    private Integer  T2;

    //事件信息
    private String dataSourceCode;
    private String eventCode;
    private Long eventTime;
    private Long matchPeriodId;
    private Long periodId;

    @ApiModelProperty(value = "局制:1长盘制,2抢七制,3单人抢十,4双人抢十,5特")
    private Integer roundType;

    @ApiModelProperty(value = "当前盘数")
    private Integer currentSet;

    @ApiModelProperty(value = "当前局数")
    private Integer currentRound;
}
