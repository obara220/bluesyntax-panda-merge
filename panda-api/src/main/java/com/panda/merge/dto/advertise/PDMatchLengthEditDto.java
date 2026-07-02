package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class PDMatchLengthEditDto  extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    private Long standardMatchId;

    private Long matchLength;
    //当前盘
    private Integer currentSet;
}
