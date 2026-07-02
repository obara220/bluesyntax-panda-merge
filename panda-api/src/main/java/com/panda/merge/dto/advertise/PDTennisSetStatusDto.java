package com.panda.merge.dto.advertise;

import lombok.Data;

@Data
public class PDTennisSetStatusDto extends AbstructAdvertiseDto {

    private Long thirdMatchId;

    private Long standardMatchId;
    // 0开始 1结束
    private Integer setStatus;
    //当前第几盘
    private Integer currentSet;
}
